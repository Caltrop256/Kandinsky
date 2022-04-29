package Kandinsky.Interpreter.Nodes.Literals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.Typedef;

public class IntegerLiteralNode extends Node {
    public final long value;
    public final Typedef appropriateType;

    public IntegerLiteralNode(CharInfo pos, long value) {
        super(NodeType.INTEGERLITERAL, pos);
        this.value = value;
        this.appropriateType = IntegerValue.getAppropriateType(value);
    }

    public IntegerLiteralNode(IntegerToken t) {
        super(NodeType.INTEGERLITERAL, t.info);
        this.value = t.literalValue;
        this.appropriateType = IntegerValue.getAppropriateType(this.value);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return frame.cont(cont, new IntegerValue(this.appropriateType, this.value));
    }

    public String lispify(int depth) {
        return String.valueOf(this.value);
    }
}
