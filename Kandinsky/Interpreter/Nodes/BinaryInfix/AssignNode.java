package Kandinsky.Interpreter.Nodes.BinaryInfix;

import Kandinsky.Exceptions.RuntimeException;
import Kandinsky.Exceptions.TypeError;
import Kandinsky.Interpreter.Continuation.AbstractState;
import Kandinsky.Interpreter.Continuation.ContUtils;
import Kandinsky.Interpreter.Continuation.Continuation;
import Kandinsky.Interpreter.Continuation.Thunk;
import Kandinsky.Interpreter.Nodes.IdentifierNode;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Interpreter.Nodes.Struct.StructFieldAccessNode;
import Kandinsky.Interpreter.Runtime.Scope;
import Kandinsky.Interpreter.Runtime.Value.ArrayValue;
import Kandinsky.Interpreter.Runtime.Value.IntegerValue;
import Kandinsky.Interpreter.Runtime.Value.StringValue;
import Kandinsky.Interpreter.Runtime.Value.StructValue;
import Kandinsky.Interpreter.Runtime.Value.Value;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Tokens.Typedef;
import Kandinsky.Parser.OperatorInfo;

public class AssignNode extends BinaryInfixOperatorNode {
    static public final OperatorInfo info = new OperatorInfo("=", 1);
    public AssignNode(CharInfo pos, Node left, Node right) {
        super(pos, left, right);
    }

    public Thunk eval(AbstractState frame, Scope scope, Continuation cont) throws RuntimeException {
        if(left.type == NodeType.IDENTIFIER) {
            final String name = ((IdentifierNode)left).name;
            return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                return frame.cont(cont, scope.setValue(name, right, this));
            }));
        } else if(left instanceof IndexNode) {
            final IndexNode indNode = (IndexNode)left;
            if(indNode.left.type != NodeType.IDENTIFIER) throw new RuntimeException(this, "Accessed array must be a identifier!");
            final IdentifierNode arrayIdentifier = (IdentifierNode)indNode.left;
            return arrayIdentifier.eval(frame, scope, ContUtils.wrap(res -> {
                if(res.type == Typedef.array) {
                    final ArrayValue array = (ArrayValue)res;
                    return indNode.right.eval(frame, scope, ContUtils.wrap(index -> {
                        if(!index.isInteger()) throw new RuntimeException(this, "Tried to access non-numeric index!");
                        final long ind = index.getInteger();
                        if(ind > array.values.length) throw new RuntimeException(indNode, "Tried to access out of bounds index! (" + ind + " >= " + array.values.length + ")");
                        if(ind < 0) throw new RuntimeException(indNode, "Tried to access negative index!");
                        final ArrayValue copied = array.copy();
                        return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                            copied.values[(int)ind] = right;
                            scope.setValue(arrayIdentifier.name, copied, this);
                            return frame.cont(cont, copied);
                        }));
                    }));
                } else if(res.type == Typedef.string) {
                    final StringValue string = (StringValue)res;
                    return indNode.right.eval(frame, scope, ContUtils.wrap(index -> {
                        if(!index.isInteger()) throw new RuntimeException(this, "Tried to access non-numeric index!");
                        final long ind = index.getInteger();
                        if(ind > string.values.length) throw new RuntimeException(indNode, "Tried to access out of bounds index! (" + ind + " >= " + string.values.length + ")");
                        if(ind < 0) throw new RuntimeException(indNode, "Tried to access negative index!");
                        final StringValue copied = string.copy();
                        return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                            if(!right.isInteger()) throw new TypeError(this, right.type, "Can only assign numeric or char types to string index!");
                            copied.values[(int)ind] = (IntegerValue)right;
                            scope.setValue(arrayIdentifier.name, copied, this);
                            return frame.cont(cont, copied);
                        }));
                    }));
                } else throw new RuntimeException(this, "Tried to assign index of non-array/string type!");
            }));
        } else if(left.type == NodeType.STRUCTFIELDACCESS) {
            final StructFieldAccessNode accessNode = (StructFieldAccessNode)left;
            if(accessNode.left.type != NodeType.IDENTIFIER) throw new RuntimeException(this, "Tried to modify field of non-identifier!");
            final IdentifierNode structIdentifier = (IdentifierNode)accessNode.left;
            return structIdentifier.eval(frame, scope, ContUtils.wrap(res -> {
                if(res.type != Typedef.struct) throw new TypeError(this, res.type, "Field to modify must be that of a struct!");
                final StructValue struct = (StructValue)res;
                if(!struct.fieldValues.containsKey(accessNode.field)) throw new RuntimeException(this, "No field '" + accessNode.field + "' in " + struct.toString() + "!");
                Typedef type = null;
                for(int i = 0; i < struct.definition.fields.length; ++i) {
                    if(struct.definition.fields[i].name.equals(accessNode.field)) {
                        if(struct.definition.fields[i].constant) throw new RuntimeException(this, "Can't redefine constant field " + accessNode.field + "!");
                        type = struct.definition.fields[i].type;
                        break;
                    }
                }
                final Typedef ttype = type;
                if(ttype == null) throw new RuntimeException(this, "No corresponding field found in definition (this should never happen)!");
                final StructValue copy = struct.copy();
                return this.right.eval(frame, scope, ContUtils.wrap(right -> {
                    copy.fieldValues.put(accessNode.field, Value.weakCoerce(right, ttype, this));
                    scope.setValue(structIdentifier.name, copy, this);
                    return frame.cont(cont, copy);
                }));
            }));
        }

        throw new RuntimeException(this, "Invalid assignment to non-identifier!");
    }

    public String lispify(int depth) {
        return super.lispify(info.symbol, depth);
    }
}
