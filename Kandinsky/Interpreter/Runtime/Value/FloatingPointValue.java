package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class FloatingPointValue extends Value {
    static public double constrainTo(Typedef type, double value) {
        switch(type) {
            default : throw new IllegalArgumentException("Can not constrain non-floating-point type " + type.toString());
            case f64 : return value;
            case f32 : return (float)value;
        }
    }

    public final double value;

    public FloatingPointValue(double value) {
        super(Typedef.f64);
        this.value = value;
    }

    public FloatingPointValue(Typedef type, double value) {
        super(type);
        this.value = constrainTo(type, value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public FloatingPointValue copy() {
        return new FloatingPointValue(this.value);
    }

    public boolean equals(Value value) {
        if(!value.isFloat()) return false;
        if(Double.isNaN(value.getFloat())) return Double.isNaN(this.value);
        return value.getFloat() == this.value;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(Typedef.isInteger(type)) return new IntegerValue(type, (long)this.value);
        if(Typedef.isFloat(type)) return new FloatingPointValue(Typedef.f32, value);
        if(type == Typedef.string) return new StringValue(this.toString());
        throw new RuntimeException(requester, "Can not cast Float to " + type);
    }
}
