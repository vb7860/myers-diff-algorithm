import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// MANAGER: checks the command, then calls the other files in order.
//   java Main lines A B       -> Part A
//   java Main highlight A B   -> Part B
public class Main {
    public static void main(String[] args) throws IOException {
        boolean known = args.length == 3 && (args[0].equals("lines") || args[0].equals("highlight"));
        if (!known) {
            System.err.println("usage: Main lines|highlight A_PATH B_PATH");
            System.exit(2);
        }
        String command = args[0];
        String aPath = args[1];
        String bPath = args[2];

        // Job 1: read both files. If one can't be read: nothing on stdout, message on stderr, exit 2.
        List<String> a;
        List<String> b;
        try {
            a = LineReader.readLines(aPath);
            b = LineReader.readLines(bPath);
        } catch (IOException e) {
            System.err.println("error: cannot read file: " + e.getMessage());
            System.exit(2);
            return;
        }

        // Job 2: give each line a number (one book shared by both files)
        Map<String, Integer> book = new HashMap<>();
        int[] aIds = LineNumberer.toIds(a, book);
        int[] bIds = LineNumberer.toIds(b, book);

        // Job 3: Myers finds the minimal edit script
        int[] ops = Myers.diff(aIds, bIds);

        // Job 4: print it (with character ranges when the command is "highlight")
        Printer.print(ops, a, b, command.equals("highlight"));
    }
}