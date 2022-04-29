package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class IdentifierToken extends Token {
    public IdentifierToken(String value, CharInfo info) {
        super(TokenType.IDENTIFIER, value, info);
    }
    
}
