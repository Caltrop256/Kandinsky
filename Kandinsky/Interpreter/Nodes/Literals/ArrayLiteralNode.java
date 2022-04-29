package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;

public class ArrayLiteralNode extends Node {
    public final Node[] items;

    public ArrayLiteralNode(CharInfo info, Node[] items) {
        super(NodeType.ARRAYLITERAL, info);
        this.items = items;
        for(final Node item : this.items) {
            item.parent = this;
        }
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return loop(0, new Value[this.items.length], frame, scope, cont);
    }

    private Thunk loop(int i, Value[] values, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i == values.length) {
            return frame.cont(cont, new ArrayValue(values));
        } else {
            return this.items[i].eval(frame, scope, ContUtils.wrap(value -> {
                values[i] = value;
                return loop(i + 1, values, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        final StringBuilder sb = new StringBuilder();
        sb.append("array(");
        for(int i = 0; i < this.items.length; ++i) {
            sb.append(this.items[i].lispify(depth));
            if(i != this.items.length - 1) sb.append(' ');
        }
        sb.append(')');
        return sb.toString();
    }
}
