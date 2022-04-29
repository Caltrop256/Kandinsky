package Kandinsky.Interpreter.Continuation;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.BinaryInfix.BinaryInfixOperatorNode;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.Tokens.Typedef;

public abstract class TypeSplitter {
    public final BinaryInfixOperatorNode caller;
    public final String symbol;
    
    public TypeSplitter(String symbol, BinaryInfixOperatorNode caller) {
        this.symbol = symbol;
        this.caller = caller;
    }

    protected Value run(Value left, Value right) throws RuntimeException {
        if(left.isInteger()) {
            if(right.isInteger()) return run((IntegerValue)left, (IntegerValue)right);
        } 
        else if(left.isFloat()) {
            if(right.isFloat()) return run((FloatingPointValue)left, (FloatingPointValue)right);
        }
        else if(left.type == Typedef.array) {
            if(right.isInteger()) return run((ArrayValue)left, (IntegerValue)right);
            return run((ArrayValue)left, right);
        }
        else if(left.type == Typedef.string) {
            if(right.isInteger()) return run((StringValue)left, (IntegerValue)right);
            return run((StringValue)left, right);
        }
        else if(left.type == Typedef.fn) {
            if(right.type == Typedef.fn) return efn(left, right);
            if(right.isInteger()) return efn(left, (IntegerValue)right);

            return efng(left, right);
        }
        else if(left.type == Typedef.struct) {
            if(right.type == Typedef.string) return run((StructValue)left, (StringValue)right);
        }
        return invalid(left, right);
    }

    public Value run(IntegerValue left, IntegerValue right) throws RuntimeException {return invalid(left, right);}
    public Value run(FloatingPointValue left, FloatingPointValue right) throws RuntimeException {return invalid(left, right);}
    public Value run(ArrayValue left, IntegerValue right) throws RuntimeException {return invalid(left, right);}
    public Value run(ArrayValue left, Value right) throws RuntimeException {return invalid(left, right);}
    public Value run(StringValue left, IntegerValue right) throws RuntimeException {return invalid(left, right);}
    public Value run(StringValue left, Value right) throws RuntimeException {return invalid(left, right);}
    public Value efn(Value left, Value right) throws RuntimeException {return invalid(left, right);}
    public Value efng(Value left, Value right) throws RuntimeException {return invalid(left, right);}
    public Value efn(Value left, IntegerValue right) throws RuntimeException {return invalid(left, right);}
    public Value run(StructValue left, StringValue right) throws RuntimeException {return invalid(left, right);}

    public Value invalid(Value left, Value right) throws RuntimeException {
        throw new RuntimeException(this.caller, "No overload in " + this.symbol + " for (" + left.type + "/" + right.type + ")!");
    }
}
