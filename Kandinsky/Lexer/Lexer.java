package Kandinsky.Lexer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Lexer.Matchers.CharMatcher;
import Kandinsky.Lexer.Matchers.IdentifierMatcher;
import Kandinsky.Lexer.Matchers.NumericMatcher;
import Kandinsky.Lexer.Matchers.OperatorMatcher;
import Kandinsky.Lexer.Matchers.PunctuationMatcher;
import Kandinsky.Lexer.Matchers.StringMatcher;
import Kandinsky.Lexer.Tokens.EOFToken;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.MalformedToken;
import Kandinsky.Lexer.Tokens.Token;
import Kandinsky.Lexer.Tokens.TokenType;

public class Lexer {
    private static final NumericMatcher numericMatcher = new NumericMatcher();
    private static final CharMatcher charMatcher = new CharMatcher();
    private static final IdentifierMatcher identifierMatcher = new IdentifierMatcher();
    private static final PunctuationMatcher punctuationMatcher = new PunctuationMatcher();
    private static final OperatorMatcher operatorMatcher = new OperatorMatcher();
    private static final StringMatcher stringMatcher = new StringMatcher();

    private final Stack<CharStream> streams = new Stack<CharStream>();
    private Token buffer;
    private CharInfo position;

    private int commentDepth = 0;

    private final HashSet<String> includeGuarded = new HashSet<String>();
    private boolean includeGuard = true;

    public Lexer(CharStream stream) throws IOException, LexingException {
        streams.push(stream);
        this.includeGuarded.add(stream.getPosition().file);
        if(!streams.peek().eof()) this.buffer = this.processNext();
        else buffer = new EOFToken(streams.peek().getPosition());
    }

    public Token peek() {
        return this.buffer;
    }

    public Token next() throws IOException, LexingException {
        final Token t = this.buffer;
        if(!this.eof()) {
            final Token next = this.processNext();
            this.buffer = next;
        }
        this.position = t.info;
        return t;
    }

    public boolean eof() {
        return this.peek().type == TokenType.EOF;
    }

    public CharInfo getPosition() {
        return this.position == null
            ? this.streams.empty()
                ? new CharInfo(1, 1)
                : this.streams.peek().getPosition()
            : this.position;
    }

    private Token skipIfNeeded(Token t) throws IOException, LexingException {
        if(this.commentDepth != 0) return this.processNext();
        else return t;
    }

    private Token processNext() throws IOException, LexingException {
        final CharStream stream = this.streams.peek();
        stream.skipWhitespace();
        if(stream.eof()) {
            stream.next();
            if(this.commentDepth != 0) throw new LexingException(stream.getPosition(), "Multi-line comment is never closed!");
            this.streams.pop();
            if(this.streams.empty()) return new EOFToken(stream.getPosition());
            else return this.processNext();
        }

        if(stream.peek() == '$') {
            this.handleLexerDirective(stream);
            return this.processNext();
        }

        try {
            if(numericMatcher.checkTarget(stream.peek())) return skipIfNeeded(numericMatcher.getToken(stream));
            if(charMatcher.checkTarget(stream.peek())) return skipIfNeeded(charMatcher.getToken(stream));
            if(identifierMatcher.checkTarget(stream.peek())) return skipIfNeeded(identifierMatcher.getToken(stream));
            if(punctuationMatcher.checkTarget(stream.peek())) return skipIfNeeded(punctuationMatcher.getToken(stream));
            if(stringMatcher.checkTarget(stream.peek())) return skipIfNeeded(stringMatcher.getToken(stream));
            if(operatorMatcher.checkTarget(stream.peek())) {
                final Token op = operatorMatcher.getToken(stream);
                switch(op.value) {
                    case "//" : 
                        stream.skipLine();
                        return this.processNext();
                    case "/*" :
                        this.commentDepth += 1;
                        return this.processNext();
                    case "*/" :
                        this.commentDepth -= 1;
                        if(this.commentDepth < 0) throw new LexingException(op.info, "Unbalanced multi-line terminations!");
                        return this.processNext();
                    default :
                        return skipIfNeeded(op);
    
                }
            }
        } catch(LexingException e) {
            return new MalformedToken(e);
        }

        final char unexpected = stream.next();
        throw new LexingException(stream.getPosition(), "Illegal character: '" + unexpected + "'!");
    }

    private void handleLexerDirective(CharStream stream) throws IOException, LexingException {
        stream.next();
        if(!identifierMatcher.checkTarget(stream.peek())) throw new LexingException(stream.getPosition(), "Lexer directives must be identifiers!");
        switch(identifierMatcher.getToken(stream).value) {
            case "include" :
                stream.skipWhitespace();
                if(!stringMatcher.checkTarget(stream.peek())) throw new LexingException(stream.getPosition(), "Include path must be a string!");
                final Token inputToken = stringMatcher.getToken(stream);
                try {
                    final CharStream includedStream = new CharStream(inputToken.value);
                    if(this.commentDepth != 0) break;
                    final String includedPath = includedStream.getPosition().file;
                    if(this.includeGuard && this.includeGuarded.contains(includedPath)) break;
                    this.includeGuarded.add(includedPath);
                    this.streams.push(includedStream);
                    break;
                } catch(IOException e) {
                    throw new LexingException(inputToken.info, "Unknown or invalid file!");
                }
            case "include-guard" :
                stream.skipWhitespace();
                final Token t;
                if(numericMatcher.checkTarget(stream.peek())) {
                    t = numericMatcher.getToken(stream);
                    if(t.type != TokenType.INTEGER) throw new LexingException(t.info, "include-guard must be an integer!");
                } else if(identifierMatcher.checkTarget(stream.peek())) {
                    t = identifierMatcher.getToken(stream);
                    if(t.type != TokenType.INTEGER) throw new LexingException(t.info, "include-guard must be an integer!");
                } else throw new LexingException(stream.getPosition(), "include-guard must be an integer!");
                this.includeGuard = ((IntegerToken)t).literalValue != 0;
                break;
            default :
                throw new LexingException(stream.getPosition(), "Unknown lexing directive!");
        }
    }
}
