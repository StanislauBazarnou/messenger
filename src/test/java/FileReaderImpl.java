import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FileReaderImpl implements IFileReader {
    private final BufferedReader reader;

    public FileReaderImpl(String fileName) throws FileNotFoundException {
        this.reader = new BufferedReader(new java.io.FileReader(fileName));
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}