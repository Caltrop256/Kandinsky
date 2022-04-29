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
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Parser.OperatorInfo;

public class MultiplicationNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("*", 20);

    public MultiplicationNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return ContUtils.overload(left, right, frame, scope, cont, new TypeSplitter(info.symbol, this) {

            public Value run(IntegerValue left, IntegerValue right) {
                return new IntegerValue(left.getInteger() * right.getInteger());
            }

            public Value run(FloatingPointValue left, FloatingPointValue right) {
                return new FloatingPointValue(left.getFloat() * right.getFloat());
            }

            public Value run(ArrayValue left, IntegerValue right) {
                final Value[] items = new Value[(int)(left.values.length * right.getInteger())];
                for(int i = 0; i < items.length; ++i) items[i] = left.values[i % left.values.length];
                return new ArrayValue(items);
            }

            public Value run(StringValue left, IntegerValue right) {
                final IntegerValue[] items = new IntegerValue[(int)(left.values.length * right.getInteger())];
                for(int i = 0; i < items.length; ++i) items[i] = left.values[i % left.values.length];
                return new StringValue(items);
            }
        });
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
