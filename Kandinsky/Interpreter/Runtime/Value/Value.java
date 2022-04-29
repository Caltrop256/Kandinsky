package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public abstract class Value {
    public static Value weakCoerce(Value value, Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.any || value.type == type) return value;
        if(value.isInteger() && Typedef.isInteger(type))
            return new IntegerValue(type, ((IntegerValue)value).value);
        if(value.isFloat() && Typedef.isFloat(type))
            return new FloatingPointValue(type, ((FloatingPointValue)value).value);

        throw new RuntimeException(requester, "Attempted to coerce " + value.type + " to " + type);
    }

    public final Typedef type;

    public Value(Typedef type) {
        this.type = type;
    }

    public abstract String toString();
    public abstract Value copy();
    public abstract boolean equals(Value value);
    public abstract Value coerceTo(Typedef type, Node requester) throws RuntimeException;

    public boolean isInteger() {
        return Typedef.isInteger(this.type);
    }
    public boolean isFloat() {
        return Typedef.isFloat(this.type);
    }
    public boolean isTruthy() {
        if(this.isInteger()) return ((IntegerValue)this).value != 0;
        else if(this.isFloat()) return ((FloatingPointValue)this).value != 0.0f;
        else if(this.type == Typedef.array) return ((ArrayValue)this).values.length != 0;
        else if(this.type == Typedef.string) return ((StringValue)this).values.length != 0;
        else if(this.type == Typedef.struct) return ((StructValue)this).fieldValues.size() != 0;
        else if(this.type == Typedef.fn || this.type == Typedef.typedef) return true;

        return false;
    }

    public long getInteger() {
        return ((IntegerValue)this).value;
    }
    public double getFloat() {
        return ((FloatingPointValue)this).value;
    }
    public String getString() {
        final StringBuilder sb = new StringBuilder();
        for(final IntegerValue ch : ((StringValue)this).values) {
            sb.append((char)ch.value);
        }
        return sb.toString();
    }
}
