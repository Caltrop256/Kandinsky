package Kandinsky.Lexer.Matchers;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Tokens.OperatorToken;
import Kandinsky.Lexer.Tokens.Token;

public class OperatorMatcher extends Matcher {
    private static final String valid = "+-*/%=&|<>!~^#°";
    public boolean checkTarget(char peek) {
        return valid.indexOf(peek) != -1;
    }

    public Token getToken(CharStream stream) throws LexingException, IOException {
        final StringBuilder sb = new StringBuilder();
        final char first = stream.next();
        sb.append(first);
        final CharInfo pos = stream.getPosition();

        switch(first) {
            case '=' :
            case '+' :
            case '%' :
            case '!' :
                if(stream.peek() == '=') sb.append(stream.next());
                break;
            case '<' :
                switch(stream.peek()) {
                    case '=' :
                    case '<' :
                        sb.append(stream.next());
                        break;
                }
            case '/' :
                switch(stream.peek()) {
                    case '/' :
                    case '*' :
                    case '=' :
                        sb.append(stream.next());
                        break;
                }
            case '>' :
                switch(stream.peek()) {
                    case '=' :
                    case '>' :
                        sb.append(stream.next());
                        break;
                }
            case '&' :
                if(stream.peek() == '&') sb.append(stream.next());
                break;
            case '|' :
                if(stream.peek() == '|') sb.append(stream.next());
                break;
            case '*' :
                switch(stream.peek()) {
                    case '=' :
                    case '*' :
                    case '/' :
                        sb.append(stream.next());
                        break;
                }
            case '-' :
                switch(stream.peek()) {
                    case '=' :
                        sb.append(stream.next());
                        break;
                    case '<' :
                    case '/' :
                        sb.append(stream.next());
                        if(stream.peek() == '>') sb.append(stream.next());
                        else throw new LexingException(pos, "Invalid operator!");
                        break;
                    case '>' :
                    sb.append(stream.next());
                    if(stream.peek() == '>') sb.append(stream.next());
                    break;
                }
        }

        return new OperatorToken(sb.toString(), pos);
    }
    
}
