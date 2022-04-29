package Kandinsky.Interpreter.Nodes.Function;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class FunctionParameterNode extends Node {
    public final String name;
    public final Typedef type;
    public final boolean constant;

    public FunctionParameterNode(CharInfo info, String name, Typedef type, boolean constant) {
        super(NodeType.FUNCTIONPARAMETER, info);
        this.name = name;
        this.type = type;
        this.constant = constant;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        throw new RuntimeException(this, "Can not eval FunctionParameterNode! (This should never happen)");
    }

    public String lispify(int depth) {
        return (constant ? "" : "mut ") + this.name + ":" + this.type.toString();
    }
}
