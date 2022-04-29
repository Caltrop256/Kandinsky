package Kandinsky.Interpreter.Continuation;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;

public class ContUtils {
    public static Continuation wrap(Continuation continuation) {
        return value -> {
            try {
                return continuation.apply(value);
            } catch(RuntimeException e) {
                throw e;
            }
        };
    };

    public static Thunk overload(Node left, Node right, AbstractState frame, Scope scope, Continuation cont, TypeSplitter splitter) throws RuntimeException {
        return left.eval(frame, scope, wrap(resLeft -> {
            return right.eval(frame, scope, wrap(resRight -> {
                return frame.cont(cont, splitter.run(resLeft, resRight));
            }));
        }));
    }
}
