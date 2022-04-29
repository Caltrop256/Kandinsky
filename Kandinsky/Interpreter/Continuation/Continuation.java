package Kandinsky.Interpreter.Continuation;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Runtime.Value.Value;

@FunctionalInterface
public interface Continuation {
    public Thunk apply(Value res) throws RuntimeException;
}
