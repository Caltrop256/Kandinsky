package Kandinsky.Interpreter.Nodes.BinaryInfix;

import java.util.ArrayList;

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

public class FilterNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("-/>", 10);

    public FilterNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(left -> {

            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                if(right.type != Typedef.fn) throw new TypeError(this, right.type, "Must be a function!");
                if(FunctionValue.getParameterTypes(right).length > 3) throw new RuntimeException(this, "Filter function demands more than 3 arguments!");

                if(left.type == Typedef.array) {
                    final ArrayValue eleft = (ArrayValue)left;
                    return arrayLoop(0, new ArrayList<Value>(), eleft, right, frame, scope, cont);
                }
                if(left.type == Typedef.string) {
                    final StringValue eleft = (StringValue)left;
                    return stringLoop(0, new ArrayList<IntegerValue>(), eleft, right, frame, scope, cont);
                }

                throw new TypeError(this, left.type, "Can not filter value!");
            }));
        }));
    }

    private Thunk arrayLoop(int i, ArrayList<Value> values, ArrayValue arr, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i == arr.values.length) {
            return frame.cont(cont, new ArrayValue(values.toArray(new Value[values.size()])));
        } else {
            return FunctionValue.call(func, new Value[]{arr.values[i], new IntegerValue(i), arr}, this, frame, ContUtils.wrap(res -> {
                if(res.isTruthy()) values.add(arr.values[i]);
                return this.arrayLoop(i + 1, values, arr, func, frame, scope, cont);
            }));
        }
    }

    private Thunk stringLoop(int i, ArrayList<IntegerValue> values, StringValue arr, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i == arr.values.length) {
            return frame.cont(cont, new StringValue(values.toArray(new IntegerValue[values.size()])));
        } else {
            return FunctionValue.call(func, new Value[]{arr.values[i], new IntegerValue(i), arr}, this, frame, ContUtils.wrap(res -> {
                if(res.isTruthy()) values.add(arr.values[i]);
                return this.stringLoop(i + 1, values, arr, func, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
