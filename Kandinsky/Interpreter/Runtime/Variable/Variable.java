package Kandinsky.Interpreter.Runtime.Variable;

import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Variable {
    public final Typedef type;
    public final boolean constant;
    public Value value;

    public Variable(Typedef type, boolean constant, Value init) {
        this.type = type;
        this.constant = constant;
        this.value = init;
    }
}
