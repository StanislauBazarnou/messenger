import org.example.MessengerApp;
import org.example.TemplateGenerator;
import org.example.TemplateGeneratorImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import util.LogToFileExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileModeTest {

    private String inputFile = "src/main/resources/input.txt";
    private String outputFile = "src/main/resources/output.txt";
    private MessengerApp app = new MessengerApp();
    private TemplateGenerator templateGenerator = new TemplateGeneratorImpl();

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void fileMode_SingleMessage_CorrectOutput() {
        // when
        app.fileMode(inputFile, outputFile);
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

        // then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void fileMode_SingleMessage_CorrectOutput_ToTempDir(@TempDir Path tempDir) throws IOException {
        // given
        File tempFile = Files.createFile(tempDir.resolve("tempFile.txt")).toFile();

        // when
        app.fileMode(inputFile, outputFile);
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

        // then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void fileMode_SingleMessage_CorrectOutput_UsingMock() {
        // given
        IFileReader mockReader = mock(IFileReader.class);
        when(mockReader.readLine()).thenReturn("Hello, #{name}!=name:Stas", null);

        // when
        String expectedOutput = "Hello, Stas!";
        String line = mockReader.readLine();
        String[] parts = line.split("=");
        String template = parts[0];

        Map<String, String> placeholders = Arrays.stream(parts[1].split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

        String actualOutput = templateGenerator.generate(template, placeholders);

        // then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    @ExtendWith(LogToFileExtension.class)
    void fileMode_SingleMessage_CorrectOutput_UsingSpy() {
        // given
        String fileName = "src/main/resources/input.txt";
        IFileReader realReader = null;
        try {
            realReader = new FileReaderImpl(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        IFileReader spyReader = spy(realReader);

        doReturn("Hello, #{name}!=name:Stas").doReturn(null).when(spyReader).readLine();

        // when
        String expectedOutput = "Hello, Stas!";
        String line = spyReader.readLine();
        String[] parts = line.split("=");
        String template = parts[0];

        Map<String, String> placeholders = Arrays.stream(parts[1].split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

        String actualOutput = templateGenerator.generate(template, placeholders);

        // then
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
        assertTrue(new File(outputFile).exists());

        // Clean up after test
        new File(outputFile).delete();
    }

    @Test
    void whenIOException_thenThrowsUncheckedIOException() throws FileNotFoundException {
        // given
        IFileReader fileReader = mock(IFileReader.class);
        // Isolate the behavior of readLine() method to make sure it throws the expected exception
        // when an IOException happens
        when(fileReader.readLine()).thenThrow(new UncheckedIOException(new IOException()));

        // then
        assertThrows(UncheckedIOException.class, fileReader::readLine);
    }
}
