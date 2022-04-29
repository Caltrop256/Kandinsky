package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;

public class BlockNode extends Node {
    final Node[] expressions;
    
    public BlockNode(CharInfo info, Node[] expressions) {
        super(NodeType.BLOCK, info);
        this.expressions = expressions;
        for(int i = 0; i < this.expressions.length; ++i) {
            this.expressions[i].parent = this;
        }
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.loop(0, frame, new Scope(scope), cont);
    }

    private Thunk loop(int i, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.expressions[i].eval(frame, scope, ContUtils.wrap(value -> {
            if(i == this.expressions.length - 1) return frame.cont(cont, value);
            else return loop(i + 1, frame, scope, cont);
        }));
    }

    public String lispify(int depth) {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        for(final Node expression : this.expressions) {
            sb.append("\n" + Node.depth(depth + 1) + expression.lispify(depth + 1));
        }
        sb.append("\n" + Node.depth(depth) + ")");
        return sb.toString();
    }
}
