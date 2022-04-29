package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Ycombinator extends ExternFunctionValue {
    public Ycombinator() {
        super(new Typedef[]{Typedef.fn}, Typedef.fn);
    }

    protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        final Value func = args[0];
        final Typedef[] funcArgs = FunctionValue.getParameterTypes(func);
        final Typedef[] selfArgs = new Typedef[java.lang.Math.max(funcArgs.length - 1, 0)];
        for(int i = 1; i < funcArgs.length; ++i) {
            selfArgs[i - 1] = funcArgs[i];
        }

        final ExternFunctionValue self = new ExternFunctionValue(selfArgs, FunctionValue.getReturnType(func)) {
            protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                final Value[] passedArgs = new Value[args.length + 1];
                passedArgs[0] = this;
                for(int i = 0; i < args.length; ++i) {
                    passedArgs[i + 1] = args[i];
                }
                return FunctionValue.call(func, passedArgs, caller, frame, ContUtils.wrap(res -> {
                    return frame.cont(cont, res);
                }));
            };
        };

        return frame.cont(cont, self);
    }
}
