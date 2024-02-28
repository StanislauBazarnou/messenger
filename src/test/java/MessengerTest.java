import org.example.MessengerApp;
import org.example.TemplateGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessengerTest {
    // unit under test (sut - system)
    private TemplateGenerator uut;

    // CONSOLE MODE TESTS

    @Test
    void generate_GivenSimpleString_ReturnsSameString() {
        // given
        String template = "Hello, World!";
        // pass an empty map since the string has no placeholders
        Map<String, String> placeholders = new HashMap<>();
        // when
        String result = uut.generate(template, placeholders);
        // then
        assertEquals(template, result);
    }

    @Test
    void generate_GivenSpecialSymbolsInPlaceholders_ReplacesCorrectly() {
        // given
        String template = "Hello, #{name}!";
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "José!@$%^&*()_+|~`={}:<>?,./;[]\\");
        // when
        String result = uut.generate(template, placeholders);
        // then
        String expected = "Hello, José!@$%^&*()_+|~`={}:<>?,./;[]\\!";
        assertEquals(expected, result);
    }

    @Test
    void generate_MultiplePlaceholders_ReplacesCorrectly() {
        // given
        String template = "Hello, #{name}! Today is #{day}.";
        // Create a map of placeholders and their replacements
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        placeholders.put("day", "Monday");
        // when
        String result = uut.generate(template, placeholders);
        // then
        String expected = "Hello, John! Today is Monday.";
        assertEquals(expected, result);
    }

    @Test
    void shouldGenerateEmail_whenAllKeysAreProvided() {
        // given
        String template = "#{subject}. Hello, #{recipient}! #{body}";

        Map<String, String> allKeysMap = Map.of("subject", "Advertisement",
                "recipient", "Anna",
                "body", "We are glad to present ...");
        // when
        String resultEmail = uut.generate(template, allKeysMap);
        // then
        assertEquals("Advertisement. Hello Anna, We are glad to present ...", resultEmail);
    }

    @Test
    void shouldGenerateEmail_whenRedundantKeysAreProvided() {
        // given
        String template = "#{subject}. Hello, #{recipient}! #{body}";

        Map<String, String> allKeysMap = Map.of("subject", "Advertisement",
                "recipient", "Anna",
                "body", "We are glad to present ...",
                "day", "Monday");
        // when
        String resultEmail = uut.generate(template, allKeysMap);
        // then
        Assertions.assertNotEquals("Advertisement, Hello Anna, We are glad to present ...", resultEmail);
    }

    @Test
    void shouldNotGenerateEmail_whenSomeKeysAreMissed() {
        // given
        String template = "#{subject}. Hello, #{recipient}! #{body}";

        Map<String, String> allKeysMap = Map.of("subject", "Advertisement",
                "recipient", "Anna");
        // when
        String resultEmail = uut.generate(template, allKeysMap);
        // then
        Assertions.assertNotEquals("Advertisement, Hello Anna, We are glad to present ...", resultEmail);
    }

    @Test
    void testTemplateGenerator() {
        // given
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        String template = "Hello, #{name}!";
        // when
        String result = uut.generate(template, placeholders);
        // then
        assertEquals("Hello, John!", result);
    }

    // FILE MODE TESTS

    @Test
    void fileMode_SingleMessage_CorrectOutput() {
        // given
        MessengerApp app = new MessengerApp();
        String inputFile = "src/test/resources/inputSingle.txt";
        String outputFile = "src/test/resources/outputSingle.txt";

        // when
        app.fileMode(inputFile, outputFile);

        // then
        // Read the contents of outputFile and compare to expected result
        String actualOutput = null; // TODO: read the contents of outputFile
        String expectedOutput = null; // TODO: the output you expect
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void fileMode_NonexistentInputFile_ThrowsException() {
        MessengerApp app = new MessengerApp();
        String inputFile = "src/test/resources/nonexistent.txt";
        String outputFile = "src/test/resources/output.txt";

        assertThrows(FileNotFoundException.class, () -> {
            app.fileMode(inputFile, outputFile);
        });
    }

    @Test
    void fileMode_NonexistentOutputFile_CreatesNewFile() {
        // given
        MessengerApp app = new MessengerApp();
        String inputFile = "src/test/resources/input.txt";
        String outputFile = "src/test/resources/nonexistent.txt";
        // Before the run, ensure that outputFile does not exist

        // when
        app.fileMode(inputFile, outputFile);

        // then
        // Check that outputFile now exists
        assertTrue(new File(outputFile).exists());

        // Clean up after test
        new File(outputFile).delete();
    }

}
