import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// JOB 4: print the edit script.
// - every output line ends with '\n' only (never println, which prints \r\n on Windows)
// - lines are written back as their exact original bytes
// - delete-first rule: in each change block, all '-' lines before any '+' line
// - highlight mode: after the i-th paired '+' line, print "? old | new"
public class Printer {

    public static void print(int[] ops, List<String> a, List<String> b, boolean highlight)
            throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(FileDescriptor.out), 1 << 16);

        int i = 0, j = 0;   // next line of A, next line of B
        int p = 0;          // position in ops
        while (p < ops.length) {
            if (ops[p] == Myers.KEEP) {
                writeLine(out, ' ', a.get(i));
                i++;
                j++;
                p++;
                continue;
            }
            // Collect one change block: everything up to the next KEEP
            List<String> dels = new ArrayList<>();
            List<String> inss = new ArrayList<>();
            while (p < ops.length && ops[p] != Myers.KEEP) {
                if (ops[p] == Myers.DELETE) dels.add(a.get(i++));
                else inss.add(b.get(j++));
                p++;
            }
            for (String line : dels) writeLine(out, '-', line);
            int pairs = Math.min(dels.size(), inss.size());
            for (int t = 0; t < inss.size(); t++) {
                writeLine(out, '+', inss.get(t));
                if (highlight && t < pairs) {
                    writeLine(out, '?', " " + Highlighter.ranges(dels.get(t), inss.get(t)));
                }
            }
        }
        out.flush();
    }

    private static void writeLine(OutputStream out, char prefix, String line) throws IOException {
        out.write(prefix);
        out.write(line.getBytes(StandardCharsets.ISO_8859_1));  // exact original bytes
        out.write('\n');
    }
}