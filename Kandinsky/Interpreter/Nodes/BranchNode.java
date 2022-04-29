package Kandinsky.Interpreter.Nodes;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Lexer.CharInfo;

public class BranchNode extends Node {
    public final Node condition;
    public final Node ifTrue;
    public final Node ifFalse;

    public BranchNode(CharInfo info, Node condition, Node ifTrue, Node ifFalse) {
        super(NodeType.BRANCH, info);
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
        this.condition.parent = this;
        this.ifTrue.parent = this;
        if(this.ifFalse != null) this.ifFalse.parent = this;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.condition.eval(frame, scope, ContUtils.wrap(cond -> {
            if(cond.isTruthy()) return this.ifTrue.eval(frame, scope, ContUtils.wrap(trueVal -> {
                return frame.cont(cont, trueVal);
            })); else if(this.ifFalse != null) return this.ifFalse.eval(frame, scope, ContUtils.wrap(falseVal -> {
                return frame.cont(cont, falseVal);
            })); else return frame.cont(cont, new IntegerValue(false));
        }));
    }

    public Thunk eval(Scope scope, Continuation cont) throws RuntimeException {
        throw new RuntimeException(this, "Unimplemented Node: " + this.type);
    }

    public String lispify(int depth) {
        return "(if " + this.condition.lispify(depth)
            + "\n" + Node.depth(depth + 1) + this.ifTrue.lispify(depth + 1)
            + (this.ifFalse != null ? ("\n" + Node.depth(depth + 1) + this.ifFalse.lispify(depth + 1)) : "")
            + "\n" + Node.depth(depth) + ")";

    }
}
