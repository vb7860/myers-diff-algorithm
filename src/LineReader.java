import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// JOB 1: Read a file as raw bytes and cut it into lines at every '\n'.
// Each line is stored as an ISO-8859-1 String: every byte becomes exactly one char,
// so nothing is lost, even for bytes that are not valid UTF-8. '\r' stays in the line.
public class LineReader {

    public static List<String> readLines(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(path));

        List<String> lines = new ArrayList<>();
        int start = 0;                                  // where the current line begins
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '\n') {                     // end of a line found
                lines.add(new String(bytes, start, i - start, StandardCharsets.ISO_8859_1));
                start = i + 1;
            }
        }
        // A last piece without '\n'. If the file ended with '\n', the last piece is
        // empty and we drop it (rule 2 of the assignment).
        if (start < bytes.length) {
            lines.add(new String(bytes, start, bytes.length - start, StandardCharsets.ISO_8859_1));
        }
        return lines;
    }
}