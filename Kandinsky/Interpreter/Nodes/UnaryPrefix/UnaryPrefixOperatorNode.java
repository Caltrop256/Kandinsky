package Kandinsky.Interpreter.Nodes.UnaryPrefix;

import Kandinsky.Exceptions.ParsingException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Lexer.CharInfo;

public abstract class UnaryPrefixOperatorNode extends Node {
    public static UnaryPrefixOperatorNode create(String symbol, CharInfo pos, Node right) throws ParsingException {
        switch(symbol) {
            case "-" : return new NegationOperator(pos, right);
            case "+" : return new DoNothingButThrowErrorOnNonNumericValuesOperator(pos, right);
            case "#" : return new IndexablePrefixOperator(pos, right);
            case "!" : return new LogicalNotNode(pos, right);
            case "*" : return new TypeInspectOperator(pos, right);
            case "~" : return new BitwiseNegationNode(pos, right);
            case "/" : return new UnaryDivisionNode(pos, right);

            default : throw new ParsingException(pos, "Invalid Prefix Operator: " + symbol);
        }
    }

    final public Node right;
    UnaryPrefixOperatorNode(CharInfo pos, Node right) {
        super(NodeType.UNARYPREFIXOPERATION, pos);
        this.right = right;
        this.right.parent = this;
    }

    public String lispify(String symbol, int depth) {
        return "(" + symbol + " " + this.right.lispify(depth) + ")";
    }
}
