package Kandinsky.Interpreter.Nodes.Struct;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;

public class StructFieldAccessNode extends Node {
    public final Node left;
    public final String field;

    public StructFieldAccessNode(CharInfo info, Node left, String field) {
        super(NodeType.STRUCTFIELDACCESS, info);
        this.left = left;
        this.left.parent = this;
        this.field = field;
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        return this.left.eval(frame, scope, ContUtils.wrap(eleft -> {
            if(eleft.type != Typedef.struct) throw new TypeError(this, eleft.type, "Tried to access field of non-struct!");
            final StructValue struct = (StructValue)eleft;
            if(!struct.fieldValues.containsKey(this.field)) throw new RuntimeException(this, "The accessed field '" + this.field + "' does not exist in " + struct.toString() + "!");
            return frame.cont(cont, struct.fieldValues.get(this.field));
        }));
    }

    public String lispify(int depth) {
        return this.left.lispify(depth) + "." + this.field;
    }
}
