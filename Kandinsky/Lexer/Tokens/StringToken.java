package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class StringToken extends Token {
    public StringToken(String value, CharInfo info) {
        super(TokenType.STRING, value, info);
    }
}
