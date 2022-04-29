package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Array {
    public final static ExternFunctionValue shift = new ExternFunctionValue(new Typedef[]{Typedef.array}, Typedef.array) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final ArrayValue inp = (ArrayValue)args[0];
            final Value[] values = new Value[inp.values.length - 1];
            for(int i = 1; i < inp.values.length; ++i) {
                values[i - 1] = inp.values[i];
            }
            return frame.cont(cont, new ArrayValue(values));
        }
    };

    public final static ExternFunctionValue unshift = new ExternFunctionValue(new Typedef[]{Typedef.array, Typedef.any}, Typedef.array) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final ArrayValue inp = (ArrayValue)args[0];
            final Value[] values = new Value[inp.values.length + 1];
            for(int i = 0; i < inp.values.length; ++i) {
                values[i + 1] = inp.values[i];
            }
            values[0] = args[1];
            return frame.cont(cont, new ArrayValue(values));
        }
    };

    public final static ExternFunctionValue pop = new ExternFunctionValue(new Typedef[]{Typedef.array}, Typedef.array) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final ArrayValue inp = (ArrayValue)args[0];
            final Value[] values = new Value[inp.values.length - 1];
            for(int i = 0; i < inp.values.length - 1; ++i) {
                values[i] = inp.values[i];
            }
            return frame.cont(cont, new ArrayValue(values));
        }
    };
}