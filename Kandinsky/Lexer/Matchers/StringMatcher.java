package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.StringToken;
import Kandinsky.Lexer.Tokens.Token;

public class StringMatcher extends Matcher {
    public boolean checkTarget(char peek) {
        return peek == '"';
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        stream.next();
        final StringBuilder sb = new StringBuilder();
        final CharInfo pos = stream.getPosition();

        while(!stream.eof() && stream.peek() != '"') {
            sb.append(CharMatcher.parseChar(stream));
        }

        if(stream.next() != '"') throw new LexingException(stream.getPosition(), "Unexpected end of file!");

        return new StringToken(sb.toString(), pos);
    }
    
}
