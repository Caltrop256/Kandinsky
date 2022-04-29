package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.FloatToken;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.Token;

public class NumericMatcher extends Matcher {
    public boolean charDec(char c) {
        return '0' <= c && c <= '9';
    }
    public boolean charFloatSuffix(char c) {
        return c == 'f' || c == 'F' || c == 'd' || c == 'D';
    }
    public boolean charHex(char c) {
        return charDec(c) || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }
    public boolean charBin(char c) {
        return c == '0' || c == '1';
    }
    public boolean charOct(char c) {
        return '0' <= c && c <= '7';
    }

    public boolean checkTarget(char peek) {
        return charDec(peek);
    }

    public Token matchFloat(CharInfo pos, CharStream stream, StringBuilder sb) throws IOException, LexingException {
        if(!charDec(stream.peek())) throw new LexingException(pos, "Floating point literals must have a valid number after the dot!");
        sb.append(stream.next());

        while(!stream.eof()) {
            if(charDec(stream.peek())) sb.append(stream.next());
            else if(stream.peek() == '_') stream.next();
            else break;
        }

        if(charFloatSuffix(stream.peek())) {
            sb.append(stream.next());
            final String value = sb.toString();
            return new FloatToken(value, pos);
        } return new FloatToken(sb.toString(), pos);
    }

    public Token matchDec(CharInfo pos, CharStream stream, StringBuilder sb) throws IOException, LexingException {
        while(!stream.eof()) {
            if(charDec(stream.peek())) sb.append(stream.next());
            else if(stream.peek() == '_') stream.next();
            else if(stream.peek() == '.') {
                sb.append(stream.next());
                return matchFloat(pos, stream, sb);
            } else break;
        }

        if(charFloatSuffix(stream.peek())) {
            sb.append(stream.next());
            final String value = sb.toString();
            return new FloatToken(value, pos);
        } else return new IntegerToken(sb.toString(), (byte)10, pos);
    }

    public Token matchHex(CharInfo pos, CharStream stream, StringBuilder sb) throws IOException, LexingException {
        if(!charHex(stream.peek())) throw new LexingException(pos, "Hexadecimal Integer literal must contain at least 1 digit!");
        sb.append(stream.next());

        while(!stream.eof()) {
            if(charHex(stream.peek())) sb.append(stream.next());
            else if(stream.peek() == '_') stream.next();
            else break;
        }

        return new IntegerToken(sb.toString(), (byte)16, pos);
    }

    public Token matchBin(CharInfo pos, CharStream stream, StringBuilder sb) throws IOException, LexingException {
        if(!charBin(stream.peek())) throw new LexingException(pos, "Binary Integer literal must contain at least 1 digit!");
        sb.append(stream.next());

        while(!stream.eof()) {
            if(charBin(stream.peek())) sb.append(stream.next());
            else if(stream.peek() == '_') stream.next();
            else break;
        }

        return new IntegerToken(sb.toString(), (byte)2, pos);
    }

    public Token matchOct(CharInfo pos, CharStream stream, StringBuilder sb) throws IOException, LexingException {
        if(!charOct(stream.peek())) throw new LexingException(pos, "Octal Integer literal must contain at least 1 digit!");
        sb.append(stream.next());

        while(!stream.eof()) {
            if(charOct(stream.peek())) sb.append(stream.next());
            else if(stream.peek() == '_') stream.next();
            else break;
        }

        return new IntegerToken(sb.toString(), (byte)8, pos);
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        final StringBuilder sb = new StringBuilder();
        final char start = stream.next();
        sb.append(start);
        final CharInfo pos = stream.getPosition();

        if(start == '0') {
            switch(stream.peek()) {
                default : return matchDec(pos, stream, sb);
                case '.' :
                    sb.append(stream.next());
                    return matchFloat(pos, stream, sb);
                case 'x' :
                    sb.append(stream.next());
                    return matchHex(pos, stream, sb);
                case 'b' :
                    sb.append(stream.next());
                    return matchBin(pos, stream, sb);
                case 'o' :
                    sb.append(stream.next());
                    return matchOct(pos, stream, sb);
            }
        } else return matchDec(pos, stream, sb);
    }
    
}
