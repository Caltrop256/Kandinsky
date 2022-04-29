package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.PunctuationToken;
import Kandinsky.Lexer.Tokens.Token;

public class PunctuationMatcher extends Matcher {
    public boolean checkTarget(char peek) {
        return ".,:;?(){}[]".indexOf(peek) != -1;
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        return new PunctuationToken(stream.next(), stream.getPosition());
    }
    
}
