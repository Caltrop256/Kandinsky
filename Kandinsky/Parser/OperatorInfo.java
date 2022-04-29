package Kandinsky.Parser;

public class OperatorInfo {
    public final String symbol;
    public final int precedence;
    public final boolean rightAssociative;
    public OperatorInfo(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.rightAssociative = false;
    }

    public OperatorInfo(String symbol, int precedence, boolean rightAssociative) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
    }
}
