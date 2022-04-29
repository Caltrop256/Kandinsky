package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class EOFToken extends Token {
    public EOFToken(CharInfo end) {
        super(TokenType.EOF, "", end);
    }
}
