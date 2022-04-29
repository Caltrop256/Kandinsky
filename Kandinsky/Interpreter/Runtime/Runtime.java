package Kandinsky.Interpreter.Runtime;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import Kandinsky.Color;
import Kandinsky.Exceptions.LexingException;
import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Runtime.Externals.Array;
import Kandinsky.Interpreter.Runtime.Externals.Console;
import Kandinsky.Interpreter.Runtime.Externals.Frame;
import Kandinsky.Interpreter.Runtime.Externals.Lambda;
import Kandinsky.Interpreter.Runtime.Externals.Panic;
import Kandinsky.Interpreter.Runtime.Externals.Println;
import Kandinsky.Interpreter.Runtime.Externals.Ycombinator;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Interpreter.Runtime.Variable.Variable;
import Kandinsky.Lexer.CharStream;
import Kandinsky.Lexer.Lexer;
import Kandinsky.Parser.Parser;

public class Runtime {
    public final Scope globalScope = new Scope();

    public Runtime(String[] inpArgs) throws IOException, LexingException {
        final String entry = inpArgs[0];
        final String[] args = new String[inpArgs.length - 1];
        for(int i = 1; i < inpArgs.length; ++i) args[i - 1] = inpArgs[i];
        setUpScope(args);
        final CharStream stream = new CharStream(entry);
        final Lexer lexer = new Lexer(stream);
        final Parser parser = new Parser(lexer);

        if(parser.malformedNodes.size() != 0) {
            System.out.println("\n" + Color.red + "Construction failed for " + parser.malformedNodes.size() + " nodes" + Color.reset);
            for(int i = 0; i < parser.malformedNodes.size(); ++i) {
                System.out.println("[" + i + "]" + parser.malformedNodes.get(i).exception.getMessage());
            }
        } else {
            final AbstractState frame = new AbstractState(parser.root);
            final Continuation lastCallback = value -> {
                System.out.println(Color.cyan + value + Color.reset);
                return null;
            };
            try {
                Thunk thunk = parser.root.eval(frame, globalScope, lastCallback);
                while(thunk != null) {
                    //System.out.println("tramp " + frame.depth);
                    thunk = thunk.run();
                }
            } catch(RuntimeException e) {
                System.out.println(Color.red + e.getMessage() + Color.reset);
                System.out.println("\tAST Trace:\n" + e.cause.treeTrace());
                System.out.println("\tStack Trace:\n" + frame.stackTrace());
            }
        }
    }

    private void setUpScope(String[] args) {
        try {
            this.loadModule(Kandinsky.Interpreter.Runtime.Externals.Math.class);
            this.loadModule(Frame.class);
            this.loadModule(Array.class);
            this.loadModule(Console.class);
            this.loadModule(Lambda.class);
            this.setValue("println", new Println());
            this.setValue("Y", new Ycombinator());
            this.setValue("panic", new Panic());

            final StringValue[] vArgs = new StringValue[args.length];
            for(int i = 0; i < args.length; ++i) vArgs[i] = new StringValue(args[i]);
            this.setValue("Vargs", new ArrayValue(vArgs));
        } catch(RuntimeException e) {e.printStackTrace();};
    }

    private void loadModule(Class<?> module) throws RuntimeException {
        final HashMap<String, Value> struct = new HashMap<String, Value>();
        for(final Field field : module.getDeclaredFields()) {
            try {
                struct.put(field.getName(), (Value)field.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.setValue(module.getSimpleName(), new StructValue(struct, null));
    }

    private void setValue(String name, Value value) throws RuntimeException {
        globalScope.initializeVariable(name, new Variable(value.type, true, value), null);
    }
}
