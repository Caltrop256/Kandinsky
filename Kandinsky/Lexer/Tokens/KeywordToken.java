package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class KeywordToken extends Token {
    public KeywordToken(String value, CharInfo info) {
        super(TokenType.KEYWORD, value, info);
    }
    
}
