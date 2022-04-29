package Kandinsky.Interpreter.Nodes.BinaryInfix;

import java.util.HashMap;

import Kandinsky.Exceptions.ParsingException;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.NodeType;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Parser.OperatorInfo;

public abstract class BinaryInfixOperatorNode extends Node {
    static public final int precedence = -1;
    static private HashMap<String, OperatorInfo> constructMap() {
        final HashMap<String, OperatorInfo> operators = new HashMap<String, OperatorInfo>();
        operators.put("°", SequenceNode.info);

        operators.put("=", AssignNode.info);
        operators.put("&=", new OperatorInfo("&=", 1));
        operators.put("|=", new OperatorInfo("|=", 1));
        operators.put("+=", new OperatorInfo("+=", 1));
        operators.put("-=", new OperatorInfo("-=", 1));
        operators.put("*=", new OperatorInfo("*=", 1));
        operators.put("/=", new OperatorInfo("/=", 1));

        operators.put("||", LogicalNotNode.info);
        operators.put("&&", LogicalAndNode.info);

        operators.put("==", EqualityNode.info);
        operators.put("!=", InequalityNode.info);
        operators.put("<", LessThanNode.info);
        operators.put(">", GreaterThanNode.info);
        operators.put(">=", GreaterOrEqualNode.info);
        operators.put("<=", LessOrEqualNode.info);

        operators.put("&", BitwiseAndNode.info);
        operators.put("|", BitwiseNotNode.info);

        operators.put("<<", BitShiftLeft.info);
        operators.put(">>", BitShiftRight.info);

        operators.put("->", MapNode.info);
        operators.put("-/>", FilterNode.info);
        operators.put("->>", FoldNode.info);
        operators.put("-<>", FoldRightNode.info);
        operators.put("+", AdditionNode.info);
        operators.put("-", SubtractionNode.info);

        operators.put("*", MultiplicationNode.info);
        operators.put("/", DivisionNode.info);
        operators.put("%", ModNode.info);
        operators.put("~", BindNode.info);

        operators.put("**", PowNode.info);

        operators.put("#", IndexNode.info);
        return operators;
    }
    static public final HashMap<String, OperatorInfo> operators = constructMap();

    public static BinaryInfixOperatorNode create(String symbol, CharInfo pos, Node left, Node right) throws ParsingException {
        switch(symbol) {
            case "°" : return new SequenceNode(pos, left, right);
            
            case "=" : return new AssignNode(pos, left, right);
            case "&=" : return new AssignNode(pos, left, new BitwiseAndNode(pos, left, right));
            case "|=" : return new AssignNode(pos, left, new BitwiseNotNode(pos, left, right)); 
            case "+=" : return new AssignNode(pos, left, new AdditionNode(pos, left, right));
            case "-=" : return new AssignNode(pos, left, new SubtractionNode(pos, left, right));
            case "*=" : return new AssignNode(pos, left, new MultiplicationNode(pos, left, right));
            case "/=" : return new AssignNode(pos, left, new DivisionNode(pos, left, right));
            case "%=" : return new AssignNode(pos, left, new ModNode(pos, left, right));

            case "||" : return new LogicalNotNode(pos, left, right);
            case "&&" : return new LogicalAndNode(pos, left, right);

            case "==" : return new EqualityNode(pos, left, right);
            case "!=" : return new InequalityNode(pos, left, right);
            case "<" : return new LessThanNode(pos, left, right);
            case ">" : return new GreaterThanNode(pos, left, right);
            case ">=" : return new GreaterOrEqualNode(pos, left, right);
            case "<=" : return new LessOrEqualNode(pos, left, right);

            case "&" : return new BitwiseAndNode(pos, left, right);
            case "|" : return new BitwiseNotNode(pos, left, right);

            case "<<" : return new BitShiftLeft(pos, left, right);
            case ">>" : return new BitShiftRight(pos, left, right);

            case "->" : return new MapNode(pos, left, right);
            case "-/>" : return new FilterNode(pos, left, right);
            case "->>" : return new FoldNode(pos, left, right);
            case "-<>" : return new FoldRightNode(pos, left, right);
            case "+" : return new AdditionNode(pos, left, right);
            case "-" : return new SubtractionNode(pos, left, right);

            case "*" : return new MultiplicationNode(pos, left, right);
            case "/" : return new DivisionNode(pos, left, right);
            case "%" : return new ModNode(pos, left, right);
            case "~" : return new BindNode(pos, left, right);

            case "**" : return new PowNode(pos, left, right);

            case "#" : return new IndexNode(pos, left, right);

            default : throw new ParsingException(pos, "Unknown Operator '"+ symbol +"'!");
        }
    }
    
    public final Node left;
    public final Node right;

    public BinaryInfixOperatorNode(CharInfo pos, Node left, Node right) {
        super(NodeType.BINARYINFIXOPERATION, pos);
        this.left = left;
        this.right = right;
        this.left.parent = this;
        this.right.parent = this;
    }

    public String lispify(String symbol, int depth) {
        return "(" + symbol + " " + this.left.lispify(depth) + " " + this.right.lispify(depth) + ")";
    }
}
