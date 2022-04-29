package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class FoldRightNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("-<>", 10);

    public FoldRightNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(left -> {

            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                if(right.type != Typedef.fn) throw new TypeError(this, right.type, "Must be a function!");
                if(FunctionValue.getParameterTypes(right).length > 4) throw new RuntimeException(this, "Folding function demands more than 4 arguments!");

                if(left.type == Typedef.array) {
                    final ArrayValue eleft = (ArrayValue)left;
                    return arrayLoop(eleft.values.length - 1, new IntegerValue(0), eleft, right, frame, scope, cont);
                }
                if(left.type == Typedef.string) {
                    return arrayLoop(((StringValue)left).values.length - 1, new IntegerValue(0), (ArrayValue)left.coerceTo(Typedef.array, this), right, frame, scope, cont);
                }

                throw new TypeError(this, left.type, "Can not fold value!");
            }));
        }));
    }

    public Thunk arrayLoop(int i, Value acum, ArrayValue arr, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i < 0) {
            return frame.cont(cont, acum);
        } else {
            return FunctionValue.call(func, new Value[]{acum, arr.values[i], new IntegerValue(i), arr}, this, frame, ContUtils.wrap(res -> {
                return arrayLoop(i - 1, res, arr, func, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
