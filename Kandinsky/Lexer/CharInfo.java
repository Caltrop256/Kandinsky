package Kandinsky.Lexer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CharInfo {
    final static Path root = Paths.get("").toAbsolutePath();

    public int ln;
    public int col;
    public final String file;

    public CharInfo(int ln, int col, String file) {
        this.ln = ln;
        this.col = col;
        this.file = root.relativize(Paths.get(file).toAbsolutePath()).toString();
    }

    public CharInfo(int ln, int col) {
        this.ln = ln;
        this.col = col;
        this.file = "@Anonymous";
    }

    public CharInfo copy() {
        return new CharInfo(this.ln, this.col, this.file);
    }

    public String toString() {
        return "(" + this.file + ":" + this.ln + ":" + this.col + ")";
    }
}
