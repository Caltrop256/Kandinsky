package Kandinsky.Interpreter.Runtime.Externals;

import java.util.HashMap;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.ExternFunctionValue;
import Kandinsky.Interpreter.Runtime.Value.FunctionValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class Frame {
    public final static ExternFunctionValue sleep = new ExternFunctionValue(new Typedef[]{Typedef.u32}, Typedef.u1) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            try {
                Thread.sleep(args[0].getInteger());
                return frame.cont(cont, new IntegerValue(true));
            } catch (InterruptedException e) {
                e.printStackTrace();
                return frame.cont(cont, new IntegerValue(false));
            }
        }
    };

    public final static ExternFunctionValue callcc = new ExternFunctionValue(new Typedef[]{Typedef.fn}, Typedef.any) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            return FunctionValue.call(args[0], new Value[]{
                new ExternFunctionValue(new Typedef[]{Typedef.any}, Typedef.any) {
                    protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation __cont) throws RuntimeException {
                        return frame.cont(cont, args[0]);
                    }
                }
            }, caller, frame, cont);
        }
    };

    public final static ExternFunctionValue file = new ExternFunctionValue(new Typedef[]{}, Typedef.struct) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final HashMap<String, Value> struct = new HashMap<String, Value>();
            struct.put("filename", new StringValue(caller.info.file));
            struct.put("line", new IntegerValue(caller.info.ln));
            struct.put("column", new IntegerValue(caller.info.col));
            struct.put("format", new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
                protected Thunk run(Value[] args, Node _caller, AbstractState frame, Continuation cont) throws RuntimeException {
                    return frame.cont(cont, new StringValue(caller.info.toString()));
                }
            });
            return frame.cont(cont, new StructValue(struct, null));
        };
    };

    public final static ExternFunctionValue callstack = new ExternFunctionValue(new Typedef[]{}, Typedef.array) {
        protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
            final StructValue[] structs = new StructValue[frame.callstack.size()];
            for(int i = 0; i < frame.callstack.size(); ++i) {
                final CharInfo info = frame.callstack.get(i);
                final HashMap<String, Value> struct = new HashMap<String, Value>();
                struct.put("filename", new StringValue(info.file));
                struct.put("line", new IntegerValue(info.ln));
                struct.put("column", new IntegerValue(info.col));
                struct.put("format", new ExternFunctionValue(new Typedef[]{}, Typedef.string) {
                    protected Thunk run(Value[] args, Node caller, AbstractState frame, Continuation cont) throws RuntimeException {
                        return frame.cont(cont, new StringValue(info.toString()));
                    }
                });
                structs[i] = new StructValue(struct, null);
            }
            return frame.cont(cont, new ArrayValue(structs));
        };
    };
}
