package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.Token;

public class CharMatcher extends Matcher {
    public static char readHexCode(CharStream stream) throws LexingException, NumberFormatException, IOException {
        if(stream.eof()) throw new LexingException(stream.getPosition(), "Unexpected end of file!");
        try {
            return (char)Integer.parseInt("" + stream.next() + stream.next(), 16);
        } catch(NumberFormatException e) {
            throw new LexingException(stream.getPosition(), "Invalid Hex escape code!");
        }
    }

    public static char parseChar(CharStream stream) throws IOException, LexingException {
        if(stream.eof()) {
            stream.next();
            throw new LexingException(stream.getPosition(), "Unexpected end of file!");
        }
        final char start = stream.next();
        if(start == '\\') switch(stream.next()) {
            case 'a' : return (char)0x07;
            case 'b' : return (char)0x08;
            case 'e' : return (char)0x1B;
            case 'f' : return (char)0x0C;
            case 'n' : return (char)0x0A;
            case 'r' : return (char)0x0D;
            case 't' : return (char)0x09;
            case 'v' : return (char)0x0B;
            case '\\': return (char)0x5C;
            case '\'': return (char)0x27;
            case '"' : return (char)0x22;
            case '?' : return (char)0x3F;
            case 'x' : return readHexCode(stream);
            case '\n':
                throw new LexingException(stream.getPosition(), "Unexpected newline!");
            case (char)-1 :
                throw new LexingException(stream.getPosition(), "Unexpected end of file!");
            default :
                throw new LexingException(stream.getPosition(), "Invalid escape sequence!");
        } else if(start == '\n') throw new LexingException(stream.getPosition(), "Unexpected newline!");
        return start;
    }

    public boolean checkTarget(char peek) {
        return peek == '\'';
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        stream.next();
        final CharInfo pos = stream.getPosition();
        final char value = parseChar(stream);
        if(stream.eof()) {
            stream.next();
            throw new LexingException(stream.getPosition(), "Unexpected end of file!");
        }
        if(stream.next() != '\'') throw new LexingException(stream.getPosition(), "Characters may only have one character(?) (im not adding multichars)");
        return new IntegerToken(value, pos);
    }
}
