package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class CastingNode extends Node {
    public final Node left;
    public final Typedef type;

    public CastingNode(CharInfo pos, Node left, Typedef type) {
        super(NodeType.CAST, pos);
        this.left = left;
        this.left.parent = this;
        this.type = type;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(value -> {
            if(this.type == Typedef.fn) return frame.cont(cont, new ExternFunctionValue(new Typedef[]{}, value.type) {
                protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                    return frame.cont(cont, value);
                }                 
            });
            if(value.type == this.type) return frame.cont(cont, value);
            return frame.cont(cont, value.coerceTo(this.type, this));
        }));
    }

    public String lispify(int depth) {
        return this.left.lispify(depth) + ":" + this.type.toString();
    }
}
