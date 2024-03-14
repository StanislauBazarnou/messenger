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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please provide the template: ");
        String template = scanner.nextLine();
        System.out.print("Please provide placeholders in format: key=value ");
        String placeholdersStr = scanner.nextLine();

        Map<String, String> placeholders = Arrays.stream(placeholdersStr.split(","))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

        String result = templateGenerator.generate(template, placeholders);
        System.out.println(result);
    }

    public void fileMode(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                String template = parts[0];

                Map<String, String> placeholders = Arrays.stream(parts[1].split(","))
                        .map(s -> s.split(":"))
                        .collect(Collectors.toMap(a -> a[0], a -> a[1]));

                String result = templateGenerator.generate(template, placeholders);
                writer.println(result);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
