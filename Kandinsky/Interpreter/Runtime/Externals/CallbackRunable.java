package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Interpreter.Continuation.Continuation;

public abstract class CallbackRunable implements Runnable {
    final protected Continuation cont;

    public CallbackRunable(Continuation cont) {
        this.cont = cont;
    }
}
