package Kandinsky.Interpreter.Nodes.UnaryPrefix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;

public class UnaryDivisionNode extends UnaryPrefixOperatorNode {
    public UnaryDivisionNode(CharInfo pos, Node right) {
        super(pos, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.right.eval(frame, scope, ContUtils.wrap(res -> {
            if(res.isInteger()) {
                final long n = res.getInteger();
                if(n == 0l) throw new RuntimeException(this, "Division by 0");
                else if(n == 1l) return frame.cont(cont, new IntegerValue(1));
                else return frame.cont(cont, new IntegerValue(0));
            }
            if(res.isFloat()) return frame.cont(cont, new FloatingPointValue(1 / res.getFloat()));
            throw new TypeError(this, res.type, "You may only apply the Negation Operator on numeric types!");
        }));
    }

    public String lispify(int depth) {
        return super.lispify("/", depth);
    }
    
}
