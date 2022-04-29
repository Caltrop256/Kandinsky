package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.TypedefValue;
import Kandinsky.Lexer.Tokens.TypedefToken;
import Kandinsky.Lexer.Tokens.Typedef;

public class TypedefLiteralNode extends Node {
    public final Typedef value;

    public TypedefLiteralNode(TypedefToken t) {
        super(NodeType.TYPEDEFLITERAL, t.info);
        this.value = t.literalValue;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new TypedefValue(this.value));
    }

    public String lispify(int depth) {
        return this.value.toString();
    }
}
