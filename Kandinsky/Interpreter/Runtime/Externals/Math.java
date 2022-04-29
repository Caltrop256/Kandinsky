package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Math {
    public final static ExternFunctionValue random = new ExternFunctionValue(new Typedef[]{}, Typedef.f64) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            return frame.cont(cont, new FloatingPointValue(Typedef.f64, java.lang.Math.random()));
        }
    };

    public final static FloatingPointValue NaN = new FloatingPointValue(0.0 / 0.0);
    public final static FloatingPointValue Infinity = new FloatingPointValue(1.0 / 0.0);
    public final static FloatingPointValue NegativeInfinity = new FloatingPointValue(1.0 / -0.0);
}
