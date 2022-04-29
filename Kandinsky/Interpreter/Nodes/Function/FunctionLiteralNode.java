package Kandinsky.Interpreter.Nodes.Function;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class FunctionLiteralNode extends Node {
    public final Typedef returnType;
    public final FunctionParameterNode[] parameters;
    public final Node body;

    public FunctionLiteralNode(CharInfo info, Typedef returnType, FunctionParameterNode[] parameters, Node body) {
        super(NodeType.FUNCTIONLITERAL, info);
        this.returnType = returnType;
        this.parameters = parameters;
        this.body = body;
        this.body.parent = this;
        for(final FunctionParameterNode parameter : this.parameters) {
            parameter.parent = this;
        }
    }
    
    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new FunctionValue(this, scope));
    }

    public String lispify(int depth) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(\u03bb(");
        for(final FunctionParameterNode parameter : this.parameters) {
            sb.append(parameter.lispify(depth) + " ");
        }
        sb.setCharAt(sb.length() - 1, ')');
        sb.append(":" + this.returnType.toString());
        sb.append("\n" + Node.depth(depth + 1) + this.body.lispify(depth + 1));
        sb.append("\n" + Node.depth(depth) + ")");
        return sb.toString();
    }
}
