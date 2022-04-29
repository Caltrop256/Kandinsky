package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.Token;

public abstract class Matcher {
    public abstract boolean checkTarget(char peek);
    public abstract Token getToken(CharStream stream) throws LexingException, IOException;
}
