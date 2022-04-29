package Kandinsky.Parser;

import java.io.IOException;
import java.util.ArrayList;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Exceptions.ParsingException;
import Kandinsky.Interpreter.Nodes.BlockNode;
import Kandinsky.Interpreter.Nodes.BranchNode;
import Kandinsky.Interpreter.Nodes.CastingNode;
import Kandinsky.Interpreter.Nodes.IdentifierNode;
import Kandinsky.Interpreter.Nodes.InitializerNode;
import Kandinsky.Interpreter.Nodes.MalformedNode;
import Kandinsky.Interpreter.Nodes.Node;
import Kandinsky.Interpreter.Nodes.BinaryInfix.BinaryInfixOperatorNode;
import Kandinsky.Interpreter.Nodes.Function.FunctionCallNode;
import Kandinsky.Interpreter.Nodes.Function.FunctionLiteralNode;
import Kandinsky.Interpreter.Nodes.Function.FunctionParameterNode;
import Kandinsky.Interpreter.Nodes.Literals.ArrayLiteralNode;
import Kandinsky.Interpreter.Nodes.Literals.FloatLiteralNode;
import Kandinsky.Interpreter.Nodes.Literals.IntegerLiteralNode;
import Kandinsky.Interpreter.Nodes.Literals.StringLiteralNode;
import Kandinsky.Interpreter.Nodes.Literals.TypedefLiteralNode;
import Kandinsky.Interpreter.Nodes.Literals.UnitLiteralNode;
import Kandinsky.Interpreter.Nodes.Struct.StructFieldAccessNode;
import Kandinsky.Interpreter.Nodes.Struct.StructFieldNode;
import Kandinsky.Interpreter.Nodes.Struct.StructLiteralNode;
import Kandinsky.Interpreter.Nodes.UnaryPrefix.UnaryPrefixOperatorNode;
import Kandinsky.Interpreter.Nodes.UnarySuffix.FactorialNode;
import Kandinsky.Lexer.CharInfo;
import Kandinsky.Lexer.Lexer;
import Kandinsky.Lexer.Tokens.FloatToken;
import Kandinsky.Lexer.Tokens.IdentifierToken;
import Kandinsky.Lexer.Tokens.IntegerToken;
import Kandinsky.Lexer.Tokens.StringToken;
import Kandinsky.Lexer.Tokens.Token;
import Kandinsky.Lexer.Tokens.TokenType;
import Kandinsky.Lexer.Tokens.TypedefToken;
import Kandinsky.Lexer.Tokens.Typedef;

public class Parser {
    private final Lexer lexer;
    public final Node root;

    public final ArrayList<MalformedNode> malformedNodes = new ArrayList<MalformedNode>();

    public Parser(Lexer lexer) throws IOException, LexingException {
        this.lexer = lexer;
        final CharInfo start = lexer.getPosition();

        final ArrayList<Node> expressions = new ArrayList<Node>();
        while(!lexer.eof()) {
            expressions.add(eatExpression());
            if(!lexer.eof() || nextIs(TokenType.PUNCTUATION, ";")) {
                if(nextIs(TokenType.PUNCTUATION, ";")) lexer.next();
                else {
                    final MalformedNode malformed = new MalformedNode(
                        lexer.peek().info, 
                        new ParsingException(lexer.peek().info, "Unexpected " + lexer.peek().type.toString().toLowerCase() + ", did you forget semicolons?"),
                        this.malformedNodes.size()
                    );
                    this.malformedNodes.add(malformed);
                    expressions.add(malformed);
                };
            }
        }
        if(expressions.size() == 1) this.root = expressions.get(0);
        else if(expressions.size() == 0) this.root = new UnitLiteralNode(start);
        else this.root = new BlockNode(start, expressions.toArray(new Node[expressions.size()]));
    }

    private Token eat(TokenType expectedType) throws IOException, LexingException, ParsingException {
        final Token received = lexer.next();
        if(received.type == TokenType.MALFORMED) throw new ParsingException(received.info, received.value);

        if(received.type != expectedType)
            throw new ParsingException(received.info, "Unexpected " + received.toString() + "! Expected " + expectedType.toString().toLowerCase() + "!");
        return received;
    }
    private Token eat(TokenType expectedType, String expectedValue) throws IOException, LexingException, ParsingException {
        final Token received = lexer.next();
        if(received.type == TokenType.MALFORMED) throw new ParsingException(received.info, received.value);

        if(received.type != expectedType || !received.value.equals(expectedValue))
            throw new ParsingException(received.info, "Unexpected " + received.toString() + "! Expected " + expectedType.toString().toLowerCase() + " '" + expectedValue + "'!");
        return received;
    }

    private boolean nextIs(TokenType expectedType) {
        return lexer.peek().type == expectedType;
    }
    private boolean nextIs(TokenType expectedType, String expectedValue) {
        return lexer.peek().type == expectedType && lexer.peek().value.equals(expectedValue);
    }

    private Node eatExpression() throws IOException, LexingException {
        return postProcessAtom(maybeEatBinary(eatAtom(), -1));
    }

    private Node maybeEatBinary(Node left, int precedenceLeft) throws IOException, LexingException {
        try {
            if(!nextIs(TokenType.OPERATOR)) return left;
            final OperatorInfo right = BinaryInfixOperatorNode.operators.get(lexer.peek().value);
            if(right == null) throw new ParsingException(lexer.peek().info, "Invalid operator: " + lexer.next().value);
            if(right.precedence <= precedenceLeft) return left;
            final CharInfo pos = eat(TokenType.OPERATOR, right.symbol).info;
            return maybeEatBinary(
                BinaryInfixOperatorNode.create(right.symbol, pos, left, maybeEatBinary(eatAtom(), right.precedence)), 
                precedenceLeft
            );
        } catch(Exception e) {
            final MalformedNode err = new MalformedNode(lexer.peek().info, e, this.malformedNodes.size());
            this.malformedNodes.add(err);
            return err;
        }
    }

    private Node postProcessAtom(Node atom) throws IOException, LexingException {
        try {
            if(nextIs(TokenType.KEYWORD, "as"))   return postProcessAtom(eatCast(atom));
            if(nextIs(TokenType.PUNCTUATION, "(")) return postProcessAtom(eatFunctionCall(atom));
            if(nextIs(TokenType.PUNCTUATION, ".")) return postProcessAtom(eatFieldAccess(atom));
            if(nextIs(TokenType.OPERATOR, "!"))    return postProcessAtom(new FactorialNode(lexer.next().info, atom));
        } catch(Exception e) {
            final MalformedNode err = new MalformedNode(lexer.peek().info, e, this.malformedNodes.size());
            this.malformedNodes.add(err);
            return err;
        }

        return atom;
    }

    private Node eatAtom() throws IOException, LexingException {
        final Node atom;
        try {
            if     (nextIs(TokenType.PUNCTUATION, "("))      atom = eatParenthesizedExpression();
            else if(nextIs(TokenType.OPERATOR))              atom = eatUnaryPrefixOperator();
            else if(nextIs(TokenType.PUNCTUATION, "{"))      atom = eatBlockNode();
            else if(nextIs(TokenType.KEYWORD,     "if"))     atom = eatBranch();
            else if(nextIs(TokenType.KEYWORD,     "let"))    atom = eatVariableInitialization();
            else if(nextIs(TokenType.KEYWORD,     "fn"))     atom = eatFunctionLiteral();
            else if(nextIs(TokenType.KEYWORD,     "\u03bb")) atom = eatFunctionLiteral();
            else if(nextIs(TokenType.KEYWORD,     "struct")) atom = eatStruct();
            else if(nextIs(TokenType.PUNCTUATION, "["))      atom = eatArrayLiteral();
            else if(nextIs(TokenType.STRING))                atom = eatStringLiteral();
            else if(nextIs(TokenType.IDENTIFIER))            atom = new IdentifierNode((IdentifierToken)lexer.next());
            else if(nextIs(TokenType.INTEGER))               atom = new IntegerLiteralNode((IntegerToken)lexer.next());
            else if(nextIs(TokenType.FLOAT))                 atom = new FloatLiteralNode((FloatToken)lexer.next());
            else if(nextIs(TokenType.TYPEDEF))               atom = new TypedefLiteralNode((TypedefToken)lexer.next());
            else {
                final Token unexpected = this.lexer.next();
                if(unexpected.type == TokenType.MALFORMED) throw new ParsingException(unexpected.info, unexpected.value);
                throw new ParsingException(unexpected.info, "Unexpected " + unexpected.toString() + "!");
            }
            return postProcessAtom(atom);
        } catch(ParsingException e) {
            final MalformedNode malformed = new MalformedNode(lexer.getPosition(), e, this.malformedNodes.size());
            this.malformedNodes.add(malformed);
            return malformed;
        }
    }

    private Typedef eatDeclaringTypedef() throws IOException, LexingException, ParsingException {
        if(nextIs(TokenType.PUNCTUATION, ":") || nextIs(TokenType.KEYWORD, "as")) {
            lexer.next();
            if(nextIs(TokenType.TYPEDEF)) {
                return ((TypedefToken)eat(TokenType.TYPEDEF)).literalValue;
            } else if(nextIs(TokenType.KEYWORD, "fn")) {
                eat(TokenType.KEYWORD, "fn");
                return Typedef.fn;
            } else if(nextIs(TokenType.KEYWORD, "\u03bb")) {
                eat(TokenType.KEYWORD, "\u03bb");
                return Typedef.fn;
            } else if(nextIs(TokenType.KEYWORD, "struct")) {
                eat(TokenType.KEYWORD, "struct");
                return Typedef.struct;
            } else throw new ParsingException(lexer.next().info, "Expected Type Definition!");
        } else return Typedef.any;
    }

    private Node eatParenthesizedExpression() throws IOException, LexingException, ParsingException {
        eat(TokenType.PUNCTUATION, "(");
        Node atom = eatExpression();
        eat(TokenType.PUNCTUATION, ")");
        return atom;
    }

    private Node eatBlockNode() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.PUNCTUATION, "{").info;
        final ArrayList<Node> expressions = new ArrayList<Node>();

        while(!lexer.eof()) {
            if(nextIs(TokenType.PUNCTUATION, "}")) break;
            expressions.add(eatExpression());
            if(!nextIs(TokenType.PUNCTUATION, "}") || nextIs(TokenType.PUNCTUATION, ";")) eat(TokenType.PUNCTUATION, ";");
            if(nextIs(TokenType.PUNCTUATION, "}")) break;
        }
        eat(TokenType.PUNCTUATION, "}");

        if(expressions.size() == 0) return new UnitLiteralNode(pos);
        else return new BlockNode(pos, expressions.toArray(new Node[expressions.size()]));
    }

    private Node eatUnaryPrefixOperator() throws IOException, LexingException, ParsingException {
        final Token t = eat(TokenType.OPERATOR);
        return UnaryPrefixOperatorNode.create(t.value, t.info, eatAtom());
    }

    private Node eatBranch() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.KEYWORD, "if").info;
        final Node condition = eatExpression();
        eat(TokenType.PUNCTUATION, "?");
        final Node ifTrue = eatExpression();
        if(nextIs(TokenType.PUNCTUATION, ":")) {
            eat(TokenType.PUNCTUATION, ":");
            return new BranchNode(pos, condition, ifTrue, eatExpression());
        } else return new BranchNode(pos, condition, ifTrue, null);
    }

    private Node eatVariableInitialization() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.KEYWORD, "let").info;
        final boolean constant = !nextIs(TokenType.KEYWORD, "mut");
        if(!constant) eat(TokenType.KEYWORD, "mut");
        final String name = eat(TokenType.IDENTIFIER).value;
        final Typedef type = eatDeclaringTypedef();
        eat(TokenType.OPERATOR, "=");
        return new InitializerNode(pos, type, constant, name, eatExpression());
    }

    private Node eatFunctionLiteral() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.KEYWORD).info;

        final ArrayList<FunctionParameterNode> parameters = new ArrayList<FunctionParameterNode>();
        final Typedef returnType;

        if(nextIs(TokenType.PUNCTUATION, "(")) {
            eat(TokenType.PUNCTUATION, "(");
            while(!lexer.eof()) {
                if(nextIs(TokenType.PUNCTUATION, ")")) break;
    
                CharInfo parameterPos = null;
                final boolean constant = !nextIs(TokenType.KEYWORD, "mut");
                if(!constant) parameterPos = eat(TokenType.KEYWORD, "mut").info;
                final Token iden = eat(TokenType.IDENTIFIER);
                if(constant) parameterPos = iden.info;
                final String name = iden.value;
                final Typedef type = eatDeclaringTypedef();
                parameters.add(new FunctionParameterNode(parameterPos, name, type, constant));
    
                if(!nextIs(TokenType.PUNCTUATION, ")")) eat(TokenType.PUNCTUATION, ",");
            }
            eat(TokenType.PUNCTUATION, ")");
    
            returnType = eatDeclaringTypedef();
        } else if(nextIs(TokenType.IDENTIFIER)) {
            final Token iden = eat(TokenType.IDENTIFIER);
            parameters.add(new FunctionParameterNode(iden.info, iden.value, Typedef.any, true));
            returnType = Typedef.any;
        } else throw new ParsingException(lexer.next().info, "Expected argument list!");

        return new FunctionLiteralNode(
            pos, 
            returnType, 
            parameters.toArray(new FunctionParameterNode[parameters.size()]), 
            eatExpression()
        );
    }

    private StructLiteralNode eatStruct() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.KEYWORD, "struct").info;

        final ArrayList<StructFieldNode> fields = new ArrayList<StructFieldNode>();
        eat(TokenType.PUNCTUATION, "{");
        while(!lexer.eof()) {
            if(nextIs(TokenType.PUNCTUATION, "}")) break;

            CharInfo fieldPos = null;
            final boolean constant = !nextIs(TokenType.KEYWORD, "mut");
            if(!constant) fieldPos = eat(TokenType.KEYWORD, "mut").info;
            final Token iden = eat(TokenType.IDENTIFIER);
            if(constant) fieldPos = iden.info;
            final String name = iden.value;
            final Typedef type = eatDeclaringTypedef();
            eat(TokenType.OPERATOR, "=");
            fields.add(new StructFieldNode(fieldPos, type, constant, name, eatExpression()));

            if(!nextIs(TokenType.PUNCTUATION, "}") || nextIs(TokenType.PUNCTUATION, ";")) eat(TokenType.PUNCTUATION, ";");
        }
        eat(TokenType.PUNCTUATION, "}");

        return new StructLiteralNode(pos, fields.toArray(new StructFieldNode[fields.size()]));
    }

    private Node eatArrayLiteral() throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.PUNCTUATION, "[").info;

        final ArrayList<Node> items = new ArrayList<Node>();
        while(!lexer.eof()) {
            if(nextIs(TokenType.PUNCTUATION, "]")) break;
            items.add(eatExpression());
            if(!nextIs(TokenType.PUNCTUATION, "]")) eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.PUNCTUATION, "]");

        return new ArrayLiteralNode(pos, items.toArray(new Node[items.size()]));
    }

    private Node eatStringLiteral() throws IOException, LexingException, ParsingException {
        final StringToken string = (StringToken)eat(TokenType.STRING);
        return new StringLiteralNode(string.info, string.value);
    }

    private Node eatCast(Node atom) throws IOException, LexingException, ParsingException {
        final Typedef type = eatDeclaringTypedef();
        return new CastingNode(lexer.getPosition(), atom, type);
    }

    private Node eatFunctionCall(Node atom) throws IOException, LexingException, ParsingException {
        final CharInfo pos = eat(TokenType.PUNCTUATION, "(").info;

        final ArrayList<Node> arguments = new ArrayList<Node>();
        while(!lexer.eof()) {
            if(nextIs(TokenType.PUNCTUATION, ")")) break;
            arguments.add(eatExpression());
            if(!nextIs(TokenType.PUNCTUATION, ")")) eat(TokenType.PUNCTUATION, ",");
        }
        eat(TokenType.PUNCTUATION, ")");

        return new FunctionCallNode(pos, atom, arguments.toArray(new Node[arguments.size()]));
    }

    private StructFieldAccessNode eatFieldAccess(Node atom) throws IOException, LexingException, ParsingException {
        return new StructFieldAccessNode(
            eat(TokenType.PUNCTUATION, ".").info, 
            atom, 
            ((IdentifierToken)eat(TokenType.IDENTIFIER)).value
        );
    }
}
