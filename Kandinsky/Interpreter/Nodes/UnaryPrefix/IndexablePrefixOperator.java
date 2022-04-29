package Kandinsky.Interpreter.Nodes.UnaryPrefix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class IndexablePrefixOperator extends UnaryPrefixOperatorNode {
    public IndexablePrefixOperator(CharInfo pos, Node right) {
        super(pos, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.right.eval(frame, scope, ContUtils.wrap(res -> {
            if(res.type == Typedef.array) return frame.cont(cont, new IntegerValue(((ArrayValue)res).values.length));
            if(res.type == Typedef.string) return frame.cont(cont, new IntegerValue(((StringValue)res).values.length));
            if(res.type == Typedef.fn) return frame.cont(cont, new IntegerValue(FunctionValue.getParameterTypes(res).length));
            if(res.isInteger()) {
                if(res.getInteger() < 0) {
                    final IntegerValue[] arr = new IntegerValue[(int)Math.abs(res.getInteger())];
                    for(int i = 0; i < arr.length; ++i) arr[i] = new IntegerValue(i + 1 + res.getInteger());
                    return frame.cont(cont, new ArrayValue(arr));
                } else {
                    final IntegerValue[] arr = new IntegerValue[(int)res.getInteger()];
                    for(int i = 0; i < arr.length; ++i) arr[i] = new IntegerValue(i);
                    return frame.cont(cont, new ArrayValue(arr));
                }
            }
            if(res.type == Typedef.struct) {
                final StructValue struct = (StructValue)res;
                final StringValue[] fields = new StringValue[struct.fieldValues.size()];
                int i = 0;
                for(final String field : struct.fieldValues.keySet()) fields[i++] = new StringValue(field);
                return frame.cont(cont, new ArrayValue(fields));
            }
            throw new TypeError(this, res.type, "No overload in # found for type!");
        }));
    }

    public String lispify(int depth) {
        return super.lispify("#", depth);
    }
    
}
