package Kandinsky.Interpreter.Runtime;

import java.util.HashMap;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Runtime.Value.FloatingPointValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Interpreter.Runtime.Variable.Variable;
import Kandinsky.Lexer.Tokens.Typedef;

public class Scope {
    private final HashMap<String, Variable> variables = new HashMap<String, Variable>();
    private final Scope parent;

    public Scope() {
        this.parent = null;
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }

    private Scope getScopeByVariableName(String name) {
        Scope scope = this;
        do {
            if(scope.variables.containsKey(name)) return scope;
        } while((scope = scope.parent) != null);
        return null;
    }

    public void initializeVariable(String name, Variable variable, Node requester) throws RuntimeException {
        if(this.variables.containsKey(name)) throw new RuntimeException(requester, "Variable " + name + " already initialized!");
        this.variables.put(name, variable);
    }

    public Value getValue(String name, Node requester) throws RuntimeException {
        final Scope scope = getScopeByVariableName(name);
        if(scope == null) throw new RuntimeException(requester, "Tried to access uninitialized variable " + name);
        return scope.variables.get(name).value;
    }

    public Value setValue(String name, Value value, Node requester) throws RuntimeException {
        final Scope scope = getScopeByVariableName(name);
        if(scope == null) throw new RuntimeException(requester, "Tried to access uninitialized variable " + name);
        final Variable var = scope.variables.get(name);
        if(var.constant) throw new RuntimeException(requester, "Cant redefine constant!");
        if(var.type != Typedef.any) {
            if(Typedef.isInteger(var.type)) {
                if(!Typedef.isInteger(value.type)) throw new RuntimeException(requester, "Can not constrain non-integer type " + value.type);
                value = new IntegerValue(var.type, ((IntegerValue)value).value);
            } else if(Typedef.isFloat(var.type)) {
                if(!Typedef.isFloat(value.type)) throw new RuntimeException(requester, "Can not constrain non-floating-point type " + value.type);
                value = new FloatingPointValue(var.type, ((FloatingPointValue)value).value);
            } else if(var.type != value.type) throw new RuntimeException(requester, "Can not assign a " + value.type + " value to " + var.type);
        }
        var.value = value;
        return value;
    }
}
