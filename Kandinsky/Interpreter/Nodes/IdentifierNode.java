package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.Tokens.IdentifierToken;

public class IdentifierNode extends Node {
    public final String name;

    public IdentifierNode(IdentifierToken t) {
        super(NodeType.IDENTIFIER, t.info);
        this.name = t.value;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, scope.getValue(this.name, this).copy());
    }

    public String lispify(int depth) {
        return this.name;
    }
}
