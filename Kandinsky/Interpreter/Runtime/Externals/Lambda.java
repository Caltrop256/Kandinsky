package Kandinsky.Interpreter.Runtime.Externals;

import java.io.IOException;
import java.io.StringReader;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.MalformedNode;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Interpreter.Runtime.Variable.Variable;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Lexer;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.Parser;

public class Lambda {
    public final static ExternFunctionValue parse = new ExternFunctionValue(new Typedef[]{}, Typedef.fn) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            if(args.length == 0) throw new RuntimeException(caller, "Lambda construction needs at least 1 argument");
            final String[] varnames = new String[args.length - 1];
            final Typedef[] requiredArgs = new Typedef[args.length - 1];
            for(int i = 0; i < args.length; ++i) {
                if(args[i].type != Typedef.string) throw new RuntimeException(caller, "All Lambda construction arguments must be strings!");
                if(i != args.length - 1) {
                    requiredArgs[i] = Typedef.any;
                    varnames[i] = args[i].getString();
                }
            }

            Node root = null;
            try {
                final Parser parser = new Parser(new Lexer(new CharStream(new StringReader(args[args.length - 1].getString()))));
                root = parser.root;
            } catch (IOException | LexingException e) {
                root = new MalformedNode(new CharInfo(1, 1), e, 0);
            }

            final Node ast = root;

            return frame.cont(cont, new ExternFunctionValue(requiredArgs, Typedef.any) {
                protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                    final Scope localScope = new Scope();
                    for(int i = 0; i < varnames.length; ++i) {
                        localScope.initializeVariable(varnames[i], new Variable(Typedef.any, true, args[i]), caller);
                    }
                    return ast.eval(frame, localScope, ContUtils.wrap(res -> {
                        return frame.cont(cont, res);
                    }));
                }
            });
        }
    };
}
