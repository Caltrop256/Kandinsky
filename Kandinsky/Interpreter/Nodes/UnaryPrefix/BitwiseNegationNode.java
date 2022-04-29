package Kandinsky.Interpreter.Nodes.UnaryPrefix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;

public class BitwiseNegationNode extends UnaryPrefixOperatorNode {
    public BitwiseNegationNode(CharInfo pos, Node right) {
        super(pos, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.right.eval(frame, scope, ContUtils.wrap(res -> {
            if(!res.isInteger()) throw new TypeError(this, res.type, "Can only bitwise negate on integer types!");
            return frame.cont(cont, new IntegerValue(~res.getInteger()));
        }));
    }

    public String lispify(int depth) {
        return super.lispify("~", depth);
    }
}
