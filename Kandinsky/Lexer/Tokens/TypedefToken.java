package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class TypedefToken extends Token {
    public final Typedef literalValue;

    public TypedefToken(String value, CharInfo info) {
        super(TokenType.TYPEDEF, value, info);
        this.literalValue = Typedef.valueOf(value);
    }
}
