package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Lexer.Tokens.FloatToken;

public class FloatLiteralNode extends Node {
    public final double value;

    public FloatLiteralNode(FloatToken t) {
        super(NodeType.FLOATLITERAL, t.info);
        this.value = t.literalValue;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new FloatingPointValue(this.value));
    }

    public String lispify(int depth) {
        return String.valueOf(this.value);
    }
}
