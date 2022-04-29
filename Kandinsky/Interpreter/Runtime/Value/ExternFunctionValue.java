package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public abstract class ExternFunctionValue extends Value {
    public final Typedef[] parameterTypes;
    public final Typedef returnType;

    public ExternFunctionValue(Typedef[] parameterTypes, Typedef returnType) {
        super(Typedef.fn);
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    protected abstract Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException;

    public Thunk call(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        if(args.length < this.parameterTypes.length)
            throw new RuntimeException(caller, "Parameter count doesn't match. Function demands " + this.parameterTypes.length + " but got " + args.length + "!");
        
        for(int i = 0; i < this.parameterTypes.length; ++i) {
            args[i] = Value.weakCoerce(args[i], this.parameterTypes[i], caller);
        }

        frame.callstack.add(caller.info);
        return this.run(args, caller, frame, ContUtils.wrap(result -> {
            frame.callstack.pop();
            return frame.cont(cont, Value.weakCoerce(result, this.returnType, caller));
        }));
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\u039b(");
        for(int i = 0; i < this.parameterTypes.length; ++i) {
            if(i != 0) sb.append(' ');
            sb.append(this.parameterTypes[i].toString());
        }
        sb.append("):");
        sb.append(this.returnType.toString());
        return sb.toString();
    }

    public ExternFunctionValue copy() {
        return this;
    }

    public boolean equals(Value value) {
        return value == this;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.string) return new StringValue(this.toString());
        throw new RuntimeException(requester, "Can not cast Lambda to " + type);
    }
}
