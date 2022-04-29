package Kandinsky;

public class Color {
    private static boolean isWin = System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    public static String reset = isWin ? "" : "\u001B[0m";
    public static String cyan = isWin ? "" : "\u001B[36m";
    public static String red = isWin ? "" : "\u001B[31m";
    public static String yellow = isWin ? "" : "\u001B[33m";
}