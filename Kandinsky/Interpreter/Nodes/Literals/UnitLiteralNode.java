package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.UnitValue;
import Kandinsky.Lexer.CharInfo;

public class UnitLiteralNode extends Node {
    public UnitLiteralNode(CharInfo info) {
        super(NodeType.UNITLITERAL, info);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new UnitValue());
    }

    public String lispify(int depth) {
        return "{}";
    }
}
