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
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class AdditionNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("+", 10);

    public AdditionNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return ContUtils.overload(left, right, frame, scope, cont, new TypeSplitter(info.symbol, this) {

            public Value run(IntegerValue left, IntegerValue right) {
                return new IntegerValue(left.getInteger() + right.getInteger());
            }

            public Value run(FloatingPointValue left, FloatingPointValue right) {
                return new FloatingPointValue(left.getFloat() + right.getFloat());
            }

            public Value run(ArrayValue left, Value right) {
                final Value[] items = new Value[left.values.length + 1];
                for(int i = 0; i < left.values.length; ++i) items[i] = left.values[i];
                items[items.length - 1] = right;
                return new ArrayValue(items);
            }

            public Value run(StringValue left, IntegerValue right) {return this.run(left, (Value)right);};

            public Value run(StringValue left, Value right) {
                final StringValue eright;

                if(right.type == Typedef.string) eright = (StringValue)right;
                else if(right.isInteger()) eright = new StringValue(new IntegerValue[]{(IntegerValue)right});
                else eright = new StringValue(right.toString());
        
                final IntegerValue[] items = new IntegerValue[left.values.length + eright.values.length];
                int i = 0;
                for(; i < left.values.length; ++i) items[i] = left.values[i];
                for(int j = 0; j < eright.values.length; ++j, ++i) items[i] = eright.values[j];
                return new StringValue(items);
            }

            public Value efn(Value left, Value right) {
                return new ExternFunctionValue(FunctionValue.getParameterTypes(right), Typedef.any) {
                    protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                        return FunctionValue.call(right, args, caller, frame, ContUtils.wrap(res -> {
                            return FunctionValue.call(left, new Value[]{res}, caller, frame, ContUtils.wrap(res2 -> {
                                return frame.cont(cont, res2);
                            }));
                        }));
                    }
                };
            }
        });
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
