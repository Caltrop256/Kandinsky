package Kandinsky.Interpreter.Runtime.Externals;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.UnitValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Panic extends ExternFunctionValue {
    public Panic() {
        super(new Typedef[]{}, Typedef.unit);
    }
    public Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
        throw new RuntimeException(caller, "Panic!: " + (args.length > 0 ? args[0] : new UnitValue()).toString());
    }
}
