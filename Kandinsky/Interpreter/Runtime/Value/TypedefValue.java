package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class TypedefValue extends Value {
    final public Typedef value;
    public TypedefValue(Typedef value) {
        super(Typedef.typedef);
        this.value = value;
    }

    public String toString() {
        return this.value.toString();
    }

    public TypedefValue copy() {
        return new TypedefValue(this.value);
    }

    public boolean equals(Value value) {
        return value.type == Typedef.typedef && ((TypedefValue)value).value == this.value;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.string) return new StringValue(this.value.toString());
        if(Typedef.isInteger(type)) return new IntegerValue(type, this.value.ordinal());
        if(Typedef.isFloat(type)) return new FloatingPointValue(type, (double)this.value.ordinal());

        throw new RuntimeException(requester, "Can not cast Typedef to " + type);
    }
}
