package Kandinsky.Interpreter.Runtime.Value;

import java.util.HashMap;
import java.util.Map;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.Struct.StructLiteralNode;
import Kandinsky.Lexer.Tokens.Typedef;

public class StructValue extends Value {
    public final HashMap<String, Value> fieldValues;
    public final StructLiteralNode definition;

    public StructValue(HashMap<String, Value> fieldValues, StructLiteralNode definition) {
        super(Typedef.struct);
        this.fieldValues = fieldValues;
        this.definition = definition;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        if(this.fieldValues.size() != 0) {
            int i = 0;
            for(final Map.Entry<String, Value> entry : this.fieldValues.entrySet()) {
                sb.append(entry.getKey() + ": " + entry.getValue().toString());
                if(i++ < this.fieldValues.size() - 1) sb.append(", ");
            }
            sb.append('}');
        } else sb.append('}');
        return sb.toString();
    }

    public StructValue copy() {
        final HashMap<String, Value> copiedValues = new HashMap<String, Value>();
        for(final Map.Entry<String, Value> entry : this.fieldValues.entrySet()) {
            copiedValues.put(entry.getKey(), entry.getValue().copy());
        }
        return new StructValue(copiedValues, this.definition);
    }

    public boolean equals(Value value) {
        if(value.type != Typedef.struct) return false;
        final StructValue struct = (StructValue)value;
        if(struct.fieldValues.size() != this.fieldValues.size()) return false;
        for(final Map.Entry<String, Value> entry : this.fieldValues.entrySet()) {
            if(!struct.fieldValues.containsKey(entry.getKey())) return false;
            if(!struct.fieldValues.get(entry.getKey()).equals(entry.getValue())) return false;
        }
        return true;
    }

    public Value coerceTo(Typedef type, Node requester) throws RuntimeException {
        if(type == Typedef.u1) return new IntegerValue(this.isTruthy());
        if(type == Typedef.string) return new StringValue(this.toString());
        throw new RuntimeException(requester, "Can not cast struct to " + type);
    }
}
