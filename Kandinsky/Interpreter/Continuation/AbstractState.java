package Kandinsky.Interpreter.Continuation;

import java.util.Stack;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;

public class AbstractState {
    public final int maxDepth = 100;
    public int depth = 0;

    public int maxCallStackPrintDepth = 25;
    public final Stack<CharInfo> callstack;
    public final Stack<Value> pstack = new Stack<Value>();

    public AbstractState(Node root) {
        this.callstack = new Stack<CharInfo>();
        this.callstack.add(root.info);
    }

    public String stackTrace() {
        final Stack<CharInfo> callstack = new Stack<CharInfo>();
        callstack.addAll(this.callstack);
        final StringBuilder sb = new StringBuilder();
        for(int i = 0, len = Math.min(this.maxCallStackPrintDepth, callstack.size()); i < len; ++i) {
            sb.append("\tat " + callstack.pop().toString());
            if(i != len - 1) sb.append("\n");
        }
        if(!callstack.empty()) sb.append("\n\t(" + callstack.size() + " more frames)");
        return sb.toString();
    }

    public Thunk cont(Continuation cont, Value result) throws RuntimeException {
        this.depth += 1;
        if(this.depth >= this.maxDepth) {
            this.depth = 0;
            return () -> cont.apply(result);
        } else return cont.apply(result);
    }
}
