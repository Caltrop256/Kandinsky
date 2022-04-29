package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Println extends ExternFunctionValue {
    public Println() {
        super(new Typedef[]{Typedef.any}, Typedef.any);
    }
    public Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        System.out.println(args[0].toString());
        return frame.cont(cont, args[0]);
    }
}
