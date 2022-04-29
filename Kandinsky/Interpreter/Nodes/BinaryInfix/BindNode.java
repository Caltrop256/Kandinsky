package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class BindNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("~", 20);

    public BindNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }
    
    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(left -> {
            if(left.type != Typedef.fn) throw new RuntimeException(this, "Can only bind lambdas!");
            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                final Typedef[] leftArgs = FunctionValue.getParameterTypes(left);
                final Value boundedValue = leftArgs.length != 0 ? Value.weakCoerce(right, leftArgs[0], this) : right;
                final Typedef[] args = new Typedef[leftArgs.length - 1];
                for(int i = 1; i < leftArgs.length; ++i) {
                    args[i - 1] = leftArgs[i];
                }

                final Value func = new ExternFunctionValue(args, FunctionValue.getReturnType(left)) {
                    protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                        final Value[] appliedArgs = new Value[args.length + 1];
                        appliedArgs[0] = boundedValue;
                        for(int i = 1; i < appliedArgs.length; ++i) {
                            appliedArgs[i] = args[i - 1];
                        }
                        return FunctionValue.call(left, appliedArgs, caller, frame, ContUtils.wrap(res -> {
                            return frame.cont(cont, res);
                        }));
                    }
                };

                return frame.cont(cont, func);
            }));
        }));
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
