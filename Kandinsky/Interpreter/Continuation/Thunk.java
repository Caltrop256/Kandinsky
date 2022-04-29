package Kandinsky.Interpreter.Continuation;

import Kandinsky.Exceptions.RuntimeException;

@FunctionalInterface
public interface Thunk {
    public Thunk run() throws RuntimeException;
}
