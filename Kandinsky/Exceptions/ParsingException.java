package Kandinsky.Exceptions;

import Kandinsky.Lexer.CharInfo;

public class ParsingException extends Exception {
    public ParsingException(CharInfo pos, String message) {
        super(pos.toString() + " " + message);
    }
}
