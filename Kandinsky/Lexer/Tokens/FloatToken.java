package Kandinsky.Lexer.Tokens;

import Kandinsky.Lexer.CharInfo;

public class FloatToken extends Token {
    final public double literalValue;

    public FloatToken(String value, CharInfo info) {
        super(TokenType.FLOAT, value, info);
        final char last = value.charAt(value.length() - 1);
        if(last == 'f' || last == 'F' || last == 'd' || last == 'D') {
            value = value.substring(0, value.length() - 1);
            if(last == 'f' || last == 'F') this.literalValue = Float.parseFloat(value);
            else this.literalValue = Double.parseDouble(value);
        } else {
            this.literalValue = Double.parseDouble(value);
        }
    }
}
