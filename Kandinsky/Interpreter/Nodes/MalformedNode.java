package Kandinsky.Interpreter.Nodes;

import Kandinsky.Color;
import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;

public class MalformedNode extends Node {
    public final Exception exception;
    public final int errorPos;

    public MalformedNode(CharInfo info, Exception exception, int n) {
        super(NodeType.MALFORMED, info);
        this.exception = exception;
        this.errorPos = n;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        throw new RuntimeException(this, "Can not evaluate malformed node. Node construction failed: " + this.exception.getMessage());
    }

    @Override
    public String lispify(int depth) {
        return Color.red + "{{MALFORMED NODE[" + this.errorPos + "]}}" + Color.reset;
    }
    
}
