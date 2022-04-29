package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Interpreter.Runtime.Variable.Variable;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class InitializerNode extends Node {
    public final Typedef type;
    public final boolean constant;
    public final String name;
    public final Node value;

    public InitializerNode(CharInfo pos, Typedef type, boolean constant, String name, Node value) {
        super(NodeType.INIT, pos);
        this.type = type;
        this.constant = constant;
        this.name = name;
        this.value = value;
        this.value.parent = this;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.value.eval(frame, scope, ContUtils.wrap(value -> {
            final Value val = Value.weakCoerce(value, this.type, this);
            final Variable var = new Variable(this.type, this.constant, val);
            scope.initializeVariable(this.name, var, this);
            return frame.cont(cont, val);
        }));
    }

    public String lispify(int depth) {
        return "(" + (this.constant ? "INIT" : "INITMUT") + " " + name + ":" + this.type + " " + this.value.lispify(depth) + ")";
    }
}
