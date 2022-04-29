package Kandinsky.Interpreter.Nodes.BinaryInfix;

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
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class MapNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("->", 10);

    public MapNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(left -> {
            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                if(right.type != Typedef.fn) throw new TypeError(this, right.type, "Must be a function!");
                if(FunctionValue.getParameterTypes(right).length > 3) throw new RuntimeException(this, "Mapping function demands more than 3 arguments!");

                if(left.type == Typedef.array) {
                    final ArrayValue eleft = (ArrayValue)left;
                    return arrayLoop(0, new Value[eleft.values.length], eleft, right, frame, scope, cont);
                }
                if(left.type == Typedef.string) {
                    final StringValue eleft = (StringValue)left;
                    return stringLoop(0, new IntegerValue[eleft.values.length], eleft, right, frame, scope, cont);
                }

                throw new TypeError(this, left.type, "Can not map value!");
            }));
        }));
    }

    public Thunk arrayLoop(int i, Value[] values, ArrayValue arr, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i >= values.length) {
            return frame.cont(cont, new ArrayValue(values));
        } else {
            return FunctionValue.call(func, new Value[]{arr.values[i], new IntegerValue(i), arr}, this, frame, ContUtils.wrap(res -> {
                values[i] = res;
                return arrayLoop(i + 1, values, arr, func, frame, scope, cont);
            }));
        }
    }

    public Thunk stringLoop(int i, IntegerValue[] values, StringValue arr, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i >= values.length) {
            return frame.cont(cont, new StringValue(values));
        } else {
            return FunctionValue.call(func, new Value[]{arr.values[i], new IntegerValue(i), arr}, this, frame, ContUtils.wrap(res -> {
                values[i] = (IntegerValue)Value.weakCoerce(res, Typedef.u32, this);
                return stringLoop(i + 1, values, arr, func, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
