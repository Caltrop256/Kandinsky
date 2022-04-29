package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class StringValue extends Value {
    public final IntegerValue[] values;

    public StringValue(String string) {
        super(Typedef.string);
        this.values = new IntegerValue[string.length()];
        for(int i = 0; i < string.length(); ++i) {
            this.values[i] = new IntegerValue(string.charAt(i));
        }
    }

    public StringValue(IntegerValue[] values) {
        super(Typedef.string);
        this.values = values;
    }

    public String toString() {
        return "\"" + this.getString() + "\"";
    }

    public StringValue copy() {
        return new StringValue(this.getString());
    }

    public boolean equals(Value value) {
        if(value.type != Typedef.string) return false;
        final StringValue string = (StringValue)value;
        if(string.values.length != this.values.length) return false;
        for(int i = 0; i < this.values.length; ++i) {
            if(!string.values[i].equals(this.values[i])) return false;
        }
        return true;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.array) return new ArrayValue(this.values);
        if(Typedef.isInteger(type)) try {
            return new IntegerValue(type, Long.parseLong(this.getString(), 10));
        } catch(Exception e) {
            throw new RuntimeException(requester, "Attempted to cast non-numeric string to " + type);
        }
        if(Typedef.isFloat(type)) try {
            return new FloatingPointValue(type, Double.parseDouble(this.getString()));
        } catch(Exception e) {
            throw new RuntimeException(requester, "Attempted to cast non-numeric string to " + type);
        }
        throw new RuntimeException(requester, "Can not cast string to " + type);
    }
}
