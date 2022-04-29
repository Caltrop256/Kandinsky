package Kandinsky;

import java.io.IOException;

import Kandinsky.Exceptions.LexingException;
import Kandinsky.Interpreter.Runtime.Runtime;


public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("No entry file specified!");
        } else {
            try {
                new Runtime(args);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (LexingException e) {
                System.out.println(Color.red + "Error while parsing files!: " + e.getMessage() + Color.reset);
            }
        }
    }
}