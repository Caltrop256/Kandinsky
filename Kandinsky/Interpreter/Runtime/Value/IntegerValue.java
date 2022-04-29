package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class IntegerValue extends Value {
    static public long constrainTo(Typedef type, long value) {
        switch(type) {
            default : throw new IllegalArgumentException("Can not constrain non-integer type " + type.toString());
            case u1 : return value == 0 ? 0 : 1;
            case i8 : return (byte)value;
            case i16 : return (short)value;
            case i32 : return (int)value;
            case u8 : return value & 0xffL;
            case u16 : return value & 0xffffL;
            case u32 : return value & 0xffffffffL;
        }
    }
    static public Typedef getAppropriateType(long value) {
        if(value > 2147483647L) return Typedef.u32;
        else return Typedef.i32;
    }

    public final long value;

    public IntegerValue(long value) {
        super(IntegerValue.getAppropriateType(value));
        this.value = constrainTo(this.type, value);
    }
    public IntegerValue(boolean value) {
        super(Typedef.u1);
        this.value = value ? 1 : 0;
    }
    public IntegerValue(Typedef type, long value) {
        super(type);
        this.value = constrainTo(type, value);
    }

    public String toString() {
        if(this.type == Typedef.u1) return this.value != 0 ? "true" : "false";
        else return String.valueOf(this.value);
    }

    public IntegerValue copy() {
        return new IntegerValue(this.value);
    }

    public boolean equals(Value value) {
        return value.isInteger() && value.getInteger() == this.value;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.string) return new StringValue(this.toString());
        if(Typedef.isInteger(type)) return new IntegerValue(type, this.value);
        if(Typedef.isFloat(type)) return new FloatingPointValue(type, (double)this.value);
        throw new RuntimeException(requester, "Can not cast integer to " + type);
    }
}
