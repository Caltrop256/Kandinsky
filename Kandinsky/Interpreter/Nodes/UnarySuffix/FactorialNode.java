package Kandinsky.Interpreter.Nodes.UnarySuffix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;

public class FactorialNode extends Node {
    final public Node left;
    public FactorialNode(CharInfo pos, Node left) {
        super(NodeType.UNARYSUFFIXOPERATION, pos);
        this.left = left;
        this.left.parent = this;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(res -> {
            if(res.isInteger()) {
                long n = res.getInteger();
                if(n < 0) throw new RuntimeException(this, "Can not compute factorial of negative integer!");
                if(n == 0) return frame.cont(cont, new IntegerValue(1));
                long x = 1;
                do {x *= n;} while(n --> 1);
                return frame.cont(cont, new IntegerValue(x));
            }
            if(res.isFloat()) {
                double n = res.getFloat();
                if(n < 0) throw new RuntimeException(this, "Can not compute factorial of negative float!");
                if(n == 0) return frame.cont(cont, new FloatingPointValue(1));
                n = Math.floor(n);
                double x = 1;
                do {x *= n;} while(n --> 1);
                return frame.cont(cont, new FloatingPointValue(x));
            }

            throw new TypeError(this, res.type, "Cannot complete factorial operation for type!");
        }));
    }

    public String lispify(int depth) {
        return "(" + this.left.lispify(depth) + " !)";
    }
}
