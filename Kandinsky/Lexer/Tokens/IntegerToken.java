package Kandinsky.Lexer.Tokens;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;

public class IntegerToken extends Token {
    public final long literalValue;

    public IntegerToken(String value, byte radix, CharInfo info) throws LexingException {
        super(TokenType.INTEGER, value, info);
        try {
            if(radix != 10) value = value.substring(2);
            this.literalValue = Long.parseLong(value, radix);
            if(this.literalValue > 4294967295L) throw new LexingException(info, "Number literals may not exceed 4294967295!");
        } catch(NumberFormatException e) {
            throw new LexingException(info, "Number literals may not exceed 4294967295!");
        }
    }

    public IntegerToken(char value, CharInfo info) {
        super(TokenType.INTEGER, String.valueOf(value), info);
        this.literalValue = (long)value;
    }    
}
