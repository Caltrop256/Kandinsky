package Kandinsky.Interpreter.Nodes.Struct;

import java.util.HashMap;
import java.util.HashSet;

import Kandinsky.Exceptions.ParsingException;
import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;

public class StructLiteralNode extends Node {
    final public StructFieldNode[] fields;

    public StructLiteralNode(CharInfo info, StructFieldNode[] fields) throws ParsingException {
        super(NodeType.STRUCTLITERAL, info);
        this.fields = fields;
        final HashSet<String> seen = new HashSet<String>();
        for(final StructFieldNode field : this.fields) {
            if(seen.contains(field.name)) throw new ParsingException(this.info, "Can not declare field " + field.name + " twice!");
            seen.add(field.name);
            field.parent = this;
        }
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return loop(0, new HashMap<String, Value>(), frame, scope, cont);
    }

    private Thunk loop(int i, HashMap<String, Value> values, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i == this.fields.length) {
            return frame.cont(cont, new StructValue(values, this));
        } else {
            return this.fields[i].eval(frame, scope, ContUtils.wrap(value -> {
                values.put(this.fields[i].name, Value.weakCoerce(value, this.fields[i].type, this));
                return loop(i + 1, values, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(struct (");
        for(int i = 0; i < this.fields.length; ++i) {
            sb.append("\n" + Node.depth(depth + 1) + this.fields[i].lispify(depth + 1));
        }
        sb.append("\n" + Node.depth(depth) + "))");
        return sb.toString();
    }
}
