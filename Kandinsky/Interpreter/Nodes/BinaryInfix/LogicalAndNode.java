package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Parser.OperatorInfo;

public class LogicalAndNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("&&", 3);
    public LogicalAndNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(res -> {
            if(!res.isTruthy()) return frame.cont(cont, res);
            else return this.right.eval(frame, scope, ContUtils.wrap(res2 -> {
                return frame.cont(cont, res2);
            }));
        }));
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
