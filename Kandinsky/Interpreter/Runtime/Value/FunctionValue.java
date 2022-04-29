package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.Function.FunctionLiteralNode;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Variable.Variable;
import Kandinsky.Lexer.Tokens.Typedef;

public class FunctionValue extends Value {
    public static Thunk call(Value func, Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        if(func.type != Typedef.fn) throw new RuntimeException(caller, "Invalid Function Call!");
        return (func instanceof ExternFunctionValue) 
            ? ((ExternFunctionValue)func).call(args, caller, frame, cont) 
            : ((FunctionValue)func).call(args, caller, frame, cont);
    }

    public static Typedef[] getParameterTypes(Value func) {
        if(func.type != Typedef.fn) throw new IllegalArgumentException("Cant get parameter list from non-function value!");
        if(func instanceof ExternFunctionValue) return ((ExternFunctionValue)func).parameterTypes;
        else {
            final FunctionValue function = (FunctionValue)func;
            final Typedef[] types = new Typedef[function.value.parameters.length];
            for(int i = 0; i < types.length; ++i) {
                types[i] = function.value.parameters[i].type;
            }
            return types;
        }
    }

    public static Typedef getReturnType(Value func) {
        return (func instanceof ExternFunctionValue)
            ? ((ExternFunctionValue)func).returnType
            : ((FunctionValue)func).value.returnType;
    }

    public final FunctionLiteralNode value;
    public final Scope parentScope;

    public FunctionValue(FunctionLiteralNode value, Scope parentScope) {
        super(Typedef.fn);
        this.value = value;
        this.parentScope = parentScope;
    }

    public Thunk call(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        if(args.length < this.value.parameters.length) 
            throw new RuntimeException(caller, "Parameter count doesn't match. Function demands " + this.value.parameters.length + " but got " + args.length + "!");
        
        final Scope bodyScope = new Scope(this.parentScope);
        for(int i = 0; i < this.value.parameters.length; ++i) {
            args[i] = Value.weakCoerce(args[i], this.value.parameters[i].type, caller);
            bodyScope.initializeVariable(
                this.value.parameters[i].name, 
                new Variable(
                    this.value.parameters[i].type, 
                    this.value.parameters[i].constant, 
                    args[i]
                ), 
                caller
            );
        };
        bodyScope.initializeVariable("Vargs", new Variable(Typedef.array, true, new ArrayValue(args)), caller);
        
        frame.callstack.add(caller.info);
        return this.value.body.eval(frame, bodyScope, ContUtils.wrap(value -> {
            frame.callstack.pop();
            return frame.cont(cont, Value.weakCoerce(value, this.value.returnType, caller));
        }));
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\u03bb(");
        for(int i = 0; i < this.value.parameters.length; ++i) {
            if(i != 0) sb.append(' ');
            sb.append(this.value.parameters[i].type.toString());
        }
        sb.append("):");
        sb.append(this.value.returnType.toString());
        return sb.toString();
    }

    public FunctionValue copy() {
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
