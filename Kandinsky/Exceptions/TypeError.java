package Kandinsky.Exceptions;

import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class TypeError extends RuntimeException {
    public TypeError(Node cause, Typedef offendingType, String message) {
        super(cause, "TypeError: Unexpected " + offendingType.toString() + "! " + message);
    }
}
