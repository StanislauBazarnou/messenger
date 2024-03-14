package util;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogToFileExtension implements BeforeEachCallback, AfterEachCallback {

    private long startTime;

    @Override
    public void beforeEach(ExtensionContext context) {
        startTime = System.currentTimeMillis();
        // log that the test started
        writeToLog("Test started: " + context.getRequiredTestMethod().getName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        long duration = System.currentTimeMillis() - startTime;
        // log that the test ended, plus the time it took
        writeToLog("Test ended: " + context.getRequiredTestMethod().getName() + ", took " + duration + " ms");
    }

    private void writeToLog(String message) {
        // write message to a file
        try (FileWriter fw = new FileWriter("test_logs.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to log file", e);
        }
    }
}
