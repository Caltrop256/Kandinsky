package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Continuation.TypeSplitter;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.TypedefValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class IndexNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("#", 100);

    public IndexNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return ContUtils.overload(left, right, frame, scope, cont, new TypeSplitter(info.symbol, this) {

            public Value run(ArrayValue left, IntegerValue right) throws RuntimeException {
                int index = (int)right.getInteger();
                if(index < 0) index = left.values.length + index;
                if(index < 0) throw new RuntimeException(this.caller, "Tried to access negative index (wrapped around twice)!");
                if(index >= left.values.length) throw new RuntimeException(this.caller, "Tried to access out of bounds index! (" + index + " >= " + left.values.length + ")");
                return left.values[index];
            }

            public Value run(StringValue left, IntegerValue right) throws RuntimeException {
                int index = (int)right.getInteger();
                if(index < 0) index = left.values.length + index;
                if(index < 0) throw new RuntimeException(this.caller, "Tried to access negative index (wrapped around twice)!");
                if(index >= left.values.length) throw new RuntimeException(this.caller, "Tried to access out of bounds index! (" + index + " >= " + left.values.length + ")");
                return left.values[index];
            }

            public Value run(StructValue left, StringValue right) throws RuntimeException {
                final String field = right.getString();
                if(!left.fieldValues.containsKey(field)) throw new RuntimeException(this.caller, "Tried to access invalid field: '" + field.toString() + "' in " + left.toString() + "!");
                return left.fieldValues.get(field).copy();
            }

            public Value efn(Value left, IntegerValue right) throws RuntimeException {
                final Typedef[] args = FunctionValue.getParameterTypes(left);
                final int index = (int)right.getInteger();
                if(index < 0) throw new RuntimeException(this.caller, "Tried to access negativeth parameter type!");
                else if(index >= args.length) throw new RuntimeException(this.caller, "Tried to access out of bounds parameter type!");
                return new TypedefValue(FunctionValue.getParameterTypes(left)[index]);
            };
        });
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
