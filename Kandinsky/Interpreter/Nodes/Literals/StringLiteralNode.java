package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Lexer.CharInfo;

public class StringLiteralNode extends Node {
    final String value;

    public StringLiteralNode(CharInfo info, String value) {
        super(NodeType.STRINGLITERAL, info);
        this.value = value;
    }
    
    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new StringValue(this.value));
    }

    public String lispify(int depth) {
        return "\"" + this.value + "\"";
    }
}
