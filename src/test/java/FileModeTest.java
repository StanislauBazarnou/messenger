import org.example.MessengerApp;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileModeTest {

    private String inputFile = "src/main/resources/input.txt";
    private MessengerApp app = new MessengerApp();

    @Test
    void fileMode_SingleMessage_CorrectOutput() {
        // given
        String outputFile = "src/main/resources/output.txt";

        // when
        app.fileMode(inputFile, outputFile);

        // then
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String actualOutput = sb.toString().trim();
        String expectedOutput = "Hello, Stas!";
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void fileMode_SingleMessage_CorrectOutput_ToTempDir(@TempDir Path tempDir) throws IOException {
        File tempFile = Files.createFile(tempDir.resolve("tempFile.txt")).toFile();

        // when
        app.fileMode(inputFile, tempFile.getPath());

        // then
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String actualOutput = sb.toString().trim();
        String expectedOutput = "Hello, Stas!";
        assertEquals(expectedOutput, actualOutput);
    }

    // NEGATIVE TESTS

    @Test
    @Tag("Negative")
    void fileMode_NonexistentInputFile_ThrowsException() {
        MessengerApp app = new MessengerApp();
        String inputFile = "src/main/resources/nonexistent.txt";
        String outputFile = "src/main/resources/output.txt";

        assertThrows(RuntimeException.class, () -> {
            app.fileMode(inputFile, outputFile);
        });
    }

    @Test
    @Tag("Negative")
    void fileMode_NonexistentOutputFile_CreatesNewFile() {
        // given
        MessengerApp app = new MessengerApp();
        String inputFile = "src/main/resources/input.txt";
        String outputFile = "src/main/resources/nonexistent.txt";

        // when
        app.fileMode(inputFile, outputFile);

        // then
        // Check that outputFile now exists
        assertTrue(new File(outputFile).exists());

        // Clean up after test
        new File(outputFile).delete();
    }
}
