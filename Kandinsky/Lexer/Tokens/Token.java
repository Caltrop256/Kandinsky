package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public abstract class Token {
    public final TokenType type;
    public final String value;
    public final CharInfo info;
    Token(TokenType type, String value, CharInfo info) {
        this.type = type;
        this.value = value;
        this.info = info;
    }

    public String toString() {
        return this.type.toString().toLowerCase() + (this.value.length() != 0 ? " '" + this.value + "'" : "");
    }
}
