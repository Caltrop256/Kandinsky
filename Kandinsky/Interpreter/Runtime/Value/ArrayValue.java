package Kandinsky.Interpreter.Runtime.Value;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Lexer.Tokens.Typedef;

public class ArrayValue extends Value {
    public final Value[] values;

    public ArrayValue(Value[] values) {
        super(Typedef.array);
        this.values = values;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < this.values.length; ++i) {
            sb.append(this.values[i].toString());
            if(i != this.values.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public ArrayValue copy() {
        final ArrayValue copy = new ArrayValue(new Value[this.values.length]);
        int i = copy.values.length;
        while(i --> 0) copy.values[i] = this.values[i];
        return copy;
    }

    public boolean equals(Value value) {
        if(value.type != Typedef.array) return false;
        final ArrayValue arr = (ArrayValue)value;
        if(arr.values.length != this.values.length) return false;
        for(int i = 0; i < this.values.length; ++i) {
            if(!arr.values[i].equals(this.values[i])) return false;
        }
        return true;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.string) {
            boolean isString = true;
            final IntegerValue[] chars = new IntegerValue[this.values.length];
            for(int i = 0; i < this.values.length; ++i) {
                if(!this.values[i].isInteger()) {
                    isString = false;
                    break;
                } else chars[i] = (IntegerValue)this.values[i];
            }
            if(isString) return new StringValue(chars);
            return new StringValue(this.toString());
        }
        throw new RuntimeException(requester, "Can not cast array to " + type);
    }
}
