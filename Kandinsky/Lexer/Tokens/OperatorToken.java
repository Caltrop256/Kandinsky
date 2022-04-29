package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class OperatorToken extends Token {
    public OperatorToken(String value, CharInfo info) {
        super(TokenType.OPERATOR, value, info);
    }
    
}
