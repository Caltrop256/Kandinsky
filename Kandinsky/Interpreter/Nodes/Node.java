package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;

public abstract class Node {
    public static String depth(int i) {
        final StringBuilder sb = new StringBuilder();
        while(i --> 0) sb.append("  ");
        return sb.toString();
    }

    public final NodeType type;
    public final CharInfo info;
    public Node parent = null;

    public Node(NodeType type, CharInfo info) {
        this.type = type;
        this.info = info;
    }

    public abstract Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException;

    public String treeTrace() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Node node = this;
        do {
            sb.append("\tat " + node.info.toString() + " " + node.type.toString() + "\n");
            i += 1;
        } while((node = node.parent) != null && i < 12);
        return sb.toString();
    }
    public abstract String lispify(int depth);
}
