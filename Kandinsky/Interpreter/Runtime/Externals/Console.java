package Kandinsky.Interpreter.Runtime.Externals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import Kandinsky.Color;
import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public class Console {
    public final static ExternFunctionValue log = new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final StringBuilder sb = new StringBuilder();
            for(int i = 0; i < args.length; ++i) {
                if(args[i].type == Typedef.string) sb.append(args[i].getString());
                else sb.append(args[i].toString());
                if(i != args.length - 1) sb.append(' ');
            }
            final String value = sb.toString();
            System.out.println(value);
            return frame.cont(cont, new StringValue(value));
        }
    };
    public final static ExternFunctionValue timestamp = new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final StringBuilder sb = new StringBuilder();
            sb.append(Color.yellow + "[" + new SimpleDateFormat("yyyy MMM dd HH:mm:ss").format(new Date()) + "]" + Color.reset + " ");
            for(int i = 0; i < args.length; ++i) {
                if(args[i].type == Typedef.string) sb.append(args[i].getString());
                else sb.append(args[i].toString());
                if(i != args.length - 1) sb.append(' ');
            }
            final String value = sb.toString();
            System.out.println(value);
            return frame.cont(cont, new StringValue(value));
        }
    };

    public final static ExternFunctionValue trace = new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final StringBuilder sb = new StringBuilder();
            for(int i = 0; i < args.length; ++i) {
                if(args[i].type == Typedef.string) sb.append(args[i].getString());
                else sb.append(args[i].toString());
                if(i != args.length - 1) sb.append(' ');
            }
            sb.append("\n" + frame.stackTrace());
            final String value = sb.toString();
            System.out.println(value);
            return frame.cont(cont, new StringValue(value));
        }
    };


    public final static ExternFunctionValue clear = new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            System.out.print("\033[H\033[2J");  
            System.out.flush();
            try {
                if(System.getProperty("os.name").contains("Windows")) Runtime.getRuntime().exec("cls");
                else Runtime.getRuntime().exec("clear"); 
            } catch (final Exception e) {};
            return frame.cont(cont, new StringValue(""));
        }
    };

    public final static ExternFunctionValue readI32 = new ExternFunctionValue(new Typedef[]{}, Typedef.i32) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final Scanner s = new Scanner(System.in);
            while(!s.hasNextInt()) s.next();
            final int in = s.nextInt();
            s.close();
            return frame.cont(cont, new IntegerValue(in));
        };
    };
}