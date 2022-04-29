package Kandinsky.Lexer.Matchers;

import java.io.IOException;
import java.util.Arrays;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.IdentifierToken;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.KeywordToken;
import Kandinsky.Lexer.Tokens.Token;
import Kandinsky.Lexer.Tokens.TypedefToken;
import Kandinsky.Lexer.Tokens.Typedef;

public class IdentifierMatcher extends Matcher {
    private static String[] validTypedefValues = Arrays.stream(Typedef.values()).map(Enum::name).toArray(String[]::new);

    public boolean checkTarget(char peek) {
        return ('a' <= peek && peek <= 'z') || ('A' <= peek && peek <= 'Z') || "\u03bb_".indexOf(peek) != -1;
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(stream.next());
        final CharInfo pos = stream.getPosition();
        
        if(sb.charAt(0) != '\u03bb') {
            while(
                !stream.eof() && (
                    ('a' <= stream.peek() && stream.peek() <= 'z') ||
                    ('A' <= stream.peek() && stream.peek() <= 'Z') ||
                    ('0' <= stream.peek() && stream.peek() <= '9') ||
                    "_-?".indexOf(stream.peek()) != -1
                )
            ) sb.append(stream.next());
        }

        final String value = sb.toString();
        final String[] keywords = {"let", "mut", "if", "fn", "\u03bb", "struct", "as"};

        if(value.equals("true")) return new IntegerToken("1", (byte)10, pos);
        if(value.equals("false")) return new IntegerToken("0", (byte)10, pos);
        if(Arrays.stream(keywords).anyMatch(value::equals)) return new KeywordToken(value, pos);
        if(Arrays.stream(validTypedefValues).anyMatch(value::equals)) return new TypedefToken(value, pos);
        return new IdentifierToken(value, pos);
    }
    
}
