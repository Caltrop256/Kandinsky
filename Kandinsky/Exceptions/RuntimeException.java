package Kandinsky.Exceptions;

import Kandinsky.Interpreter.Nodes.Node;

public class RuntimeException extends Exception {
    final public Node cause;
    public RuntimeException(Node cause, String message) {
        super(cause.info.toString() + " " + message);
        this.cause = cause;
    }
}
