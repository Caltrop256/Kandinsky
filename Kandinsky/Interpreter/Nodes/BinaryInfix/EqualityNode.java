package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Parser.OperatorInfo;

public class EqualityNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("==", 4);

    public EqualityNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return left.eval(frame, scope, ContUtils.wrap(left -> {
            return right.eval(frame, scope, ContUtils.wrap(right -> {
                return frame.cont(cont, new IntegerValue(left.equals(right)));
            }));
        }));
    }
    
    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
