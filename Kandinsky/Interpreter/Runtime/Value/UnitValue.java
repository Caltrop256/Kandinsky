package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class UnitValue extends Value {
    public UnitValue() {
        super(Typedef.unit);
    }

    public String toString() {
        return "<<UNIT>>";
    }

    public Value copy() {
        return new UnitValue();
    }

    public boolean equals(Value value) {
        return value.type == Typedef.unit;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        throw new RuntimeException(requester, "Can not cast 0-bit value to non-lambda value " + type + "!");
    }
}