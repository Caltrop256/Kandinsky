package Kandinsky.Lexer.Tokens;

import Kandinsky.Exceptions.LexingException;

public class MalformedToken extends Token {
    public MalformedToken(LexingException exception) {
        super(TokenType.MALFORMED, exception.rawMessage, exception.pos);
    }
    
}
