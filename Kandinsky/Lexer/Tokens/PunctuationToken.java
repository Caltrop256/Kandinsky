package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class PunctuationToken extends Token {
    public PunctuationToken(char value, CharInfo info) {
        super(TokenType.PUNCTUATION, String.valueOf(value), info);
    }
}
