package org.example;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MessengerApp {

    private TemplateGenerator templateGenerator;

    public MessengerApp() {
        this.templateGenerator = new TemplateGeneratorImpl();
    }

    public void consoleMode() {
        // read from console, generate messages, and print to console
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please provide the template: ");
        String template = scanner.nextLine();
        System.out.print("Please provide placeholders in format: key=value, separate by comma: ");
        String placeholdersStr = scanner.nextLine();

        // Parse placeholders to a map
        Map<String, String> placeholders = Arrays.stream(placeholdersStr.split(","))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

        // Generate & Print the result
        String result = templateGenerator.generate(template, placeholders);
        System.out.println(result);
    }

    public void fileMode(String inputFilePath, String outputFilePath) {
        // read from file, generate messages, and write to file
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line to get template and placeholders
                // For example, let's assume that each line is in format: template=placeholder1:value1,placeholder2=value2
                String[] parts = line.split("=");
                String template = parts[0];

                Map<String, String> placeholders = Arrays.stream(parts[1].split(","))
                        .map(s -> s.split(":"))
                        .collect(Collectors.toMap(a -> a[0], a -> a[1]));

                // Generate and write the result to output file
                String result = templateGenerator.generate(template, placeholders);
                writer.println(result);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
