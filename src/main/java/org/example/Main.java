package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MessengerApp app = new MessengerApp();
        if (args.length == 0) {
            app.consoleMode();  // if no arguments, use console mode
        } else if (args.length == 2) {
            // You pass parameters to the main method via command line arguments when you run your Java program
            // if there are two arguments, treat them as input and output file paths and use file mode
            app.fileMode(args[0], args[1]);
        } else {
            System.err.println("Invalid arguments.");
        }
    }
}
