package Kandinsky.Interpreter.Nodes.Function;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class FunctionCallNode extends Node {
    public final Node left;
    public final Node[] arguments;

    public FunctionCallNode(CharInfo info, Node left, Node[] arguments) {
        super(NodeType.FUNCTIONCALL, info);
        this.left = left;
        this.arguments = arguments;
        this.left.parent = this;
        for(final Node argument : this.arguments) {
            argument.parent = this;
        }
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(v -> {
            if(v.type != Typedef.fn) throw new TypeError(this, v.type, "Invalid function call!");
            return this.argLoop(0, new Value[this.arguments.length], v, frame, scope, cont);
        }));
    }

    private Thunk argLoop(int i, Value[] values, Value func, AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(i == this.arguments.length) {
            return FunctionValue.call(func, values, this, frame, ContUtils.wrap(res -> {
                return frame.cont(cont, res);
            }));
        } else {
            return this.arguments[i].eval(frame, scope, ContUtils.wrap(arg -> {
                values[i] = arg;
                return this.argLoop(i + 1, values, func, frame, scope, cont);
            }));
        }
    }

    public String lispify(int depth) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(CALL " + this.left.lispify(depth) + "");
        if(this.arguments.length != 0) {
            sb.append(' ');
            for(final Node argument : this.arguments) {
                sb.append(argument.lispify(depth) + " ");
            }
            sb.setCharAt(sb.length() - 1, ')');
        } else sb.append(')');
        return sb.toString();
    }
}
