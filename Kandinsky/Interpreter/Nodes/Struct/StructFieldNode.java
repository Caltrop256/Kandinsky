package Kandinsky.Interpreter.Nodes.Struct;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.InitializerNode;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class StructFieldNode extends InitializerNode {
    public StructFieldNode(CharInfo pos, Typedef type, boolean constant, String name, Node value) {
        super(pos, type, constant, name, value);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.value.eval(frame, scope, ContUtils.wrap(value -> {
            return frame.cont(cont, value);
        }));
    }
}
