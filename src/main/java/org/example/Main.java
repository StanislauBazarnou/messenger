package org.example;

public class Main {

    public static void main(String[] args) {
        System.out.println("Template generator welcomes you!");
        MessengerApp app = new MessengerApp();
        // if no arguments, use console mode
        if (args.length == 0) {
            app.consoleMode();
        // if there are two arguments, treat them as input and output file paths and use file mode
        } else if (args.length == 2) {
            app.fileMode(args[0], args[1]);
        } else {
            System.err.println("Invalid arguments.");
        }
    }
}
