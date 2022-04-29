package Kandinsky.Exceptions;

import Kandinsky.Lexer.CharInfo;

public class LexingException extends Exception {
    public final CharInfo pos;
    public final String rawMessage;
    public LexingException(CharInfo pos, String message) {
        super(pos.toString() + " " + message);
        this.pos = pos;
        this.rawMessage = message;
    }
}
