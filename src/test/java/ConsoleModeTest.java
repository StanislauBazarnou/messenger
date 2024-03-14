import annotations.Annotations;
import org.example.TemplateGenerator;
import org.example.TemplateGeneratorImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.LogToFileExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleModeTest {
    // unit under test (sut - system)
    private TemplateGenerator uut;

    @BeforeEach
    void setUp() {
        uut = new TemplateGeneratorImpl();
    }

    @Test
    @Tag("Simple unit tests")
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

    @ParameterizedTest
    @ValueSource(strings = {"José!@$%^&*()_+|~`={}:<>?,./;[]\\"})
    void generate_GivenSpecialSymbolsInPlaceholders_ReplacesCorrectly(String input) {
        // given
        String template = "Hello, #{name}!";
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", input);
        // when
        String result = uut.generate(template, placeholders);
        // then
        String expected = "Hello, " + input + "!";
        assertEquals(expected, result);
    }

    @TestFactory
    Collection<DynamicTest> generate_GivenSpecialSymbolsInPlaceholders_ReplacesCorrectly() {
        List<String> inputs = Arrays.asList("José!@$%^&*()_+|~`={}:<>?,./;[]\\", "abc123", "@$%&*()=|~{}:<>?,./;");
        List<DynamicTest> tests = new ArrayList<>();

        for (String input : inputs) {
            String testName = "Testing input: " + input;
            // Executable is a functional interface which has a single method void execute() throws
            // Throwable. It is often used in assertions that could throw an exception, where the
            // lambda expression is the action that is expected to throw an exception.  In the case of DynamicTest,
            // Executable exec = () -> {...} is defining what the test should do when it is executed
            Executable exec = () -> {
                String template = "Hello, #{name}!";
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("name", input);

                String result = uut.generate(template, placeholders);
                String expected = "Hello, " + input + "!";
                assertEquals(expected, result);
            };
            tests.add(DynamicTest.dynamicTest(testName, exec));
        }
        return tests;
    }

    @Test
    @Tag("Simple unit tests")
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
    @Tag("Windows")
    @Annotations.TestOnWindows
    void generate_MultiplePlaceholders_ReplacesCorrectly_Windows() {
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
    @Tag("Mac")
    @Annotations.TestOnMac
    void generate_MultiplePlaceholders_ReplacesCorrectly_Mac() {
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
    @Tag("Simple unit tests")
    void shouldGenerateEmail_whenAllKeysAreProvided() {
        // given
        String template = "#{subject}. Hello #{recipient}, #{body}";

        Map<String, String> allKeysMap = Map.of("subject", "Advertisement",
                "recipient", "Anna",
                "body", "We are glad to present ...");
        // when
        String resultEmail = uut.generate(template, allKeysMap);
        // then
        assertEquals("Advertisement. Hello Anna, We are glad to present ...", resultEmail);
    }

    @Test
    @Tag("Negative")
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
    @ExtendWith(LogToFileExtension.class)
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

    @Test
    void testWithMockedConsoleInput() {
        // given
        String input = "Hello, #{name}!\nStas";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // when
        //System.in is "mocked" console input
        Scanner myScanner = new Scanner(System.in);
        String template = myScanner.nextLine();
        String placeholder = myScanner.nextLine();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", placeholder);

        String result = uut.generate(template, placeholders);

        // then
        String expected = "Hello, Stas!";
        assertEquals(expected, result);

        // Reset System.in to standard console input after the test
        System.setIn(System.in);
    }

}
