package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class BitShiftRight extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo(">>", 9);

    public BitShiftRight(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(left -> {
            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                if(left.isInteger() && right.isInteger()) return frame.cont(cont, new IntegerValue(left.getInteger() >> right.getInteger()));
                if(left.type == Typedef.array && right.type == Typedef.fn) {
                    return FunctionValue.call(right, ((ArrayValue)left).values, this, frame, ContUtils.wrap(res -> {
                        return frame.cont(cont, res);
                    }));
                }

                throw new RuntimeException(this, "No overload in >> found for (" + left.type + "/" + right.type + ")!");
            }));
        }));
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
