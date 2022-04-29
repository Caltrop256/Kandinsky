package Kandinsky.Lexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class CharStream {
    private final Reader stream;
    private final CharInfo info;
    private int buffer;
    private boolean encounteredNewline = false;

    public CharStream(String path) throws IOException {
        this.stream = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        this.info = new CharInfo(1, 0, path);
        this.buffer = this.stream.read();
    }

    public CharStream(Reader stream) throws IOException {
        this.stream = stream;
        this.info = new CharInfo(1, 0);
        this.buffer = this.stream.read();
    }

    public boolean eof() {
        return this.buffer == -1;
    }

    public char peek() {
        return (char)this.buffer;
    }

    public char next() throws IOException {
        final char n = this.peek();
        while((this.buffer = this.stream.read()) == '\r');
        if(this.encounteredNewline) {
            this.encounteredNewline = false;
            this.info.ln += 1;
            this.info.col = 1;
        } else this.info.col += 1;
        if(n == '\n') this.encounteredNewline = true;
        return n;
    }
    
    public CharInfo getPosition() {
        return this.info.copy();
    }

    public void skipLine() throws IOException {
        while(!this.eof() && this.next() != '\n');
    }

    public void skipWhitespace() throws IOException {
        while(!this.eof() && Character.isWhitespace(this.peek())) this.next();
    }
}
