import java.nio.charset.StandardCharsets;

// PART B: for one changed line pair, find which characters changed.
// Runs Myers again, on the Unicode code points of the two lines,
// and returns the text after "? ", like "12-13 | 11-12".
public class Highlighter {

    public static String ranges(String oldLine, String newLine) {
        int[] x = codePoints(oldLine);
        int[] y = codePoints(newLine);
        int[] ops = Myers.diff(x, y);

        StringBuilder oldR = new StringBuilder();
        StringBuilder newR = new StringBuilder();
        int i = 0, j = 0;                 // position in old line, position in new line
        int oldStart = -1, newStart = -1; // start of the open range, -1 = no open range

        for (int op : ops) {
            if (op == Myers.KEEP) {
                // A kept character ends any open range
                if (oldStart >= 0) { addRange(oldR, oldStart, i); oldStart = -1; }
                if (newStart >= 0) { addRange(newR, newStart, j); newStart = -1; }
                i++;
                j++;
            } else if (op == Myers.DELETE) {
                if (oldStart < 0) oldStart = i;
                i++;
            } else {
                if (newStart < 0) newStart = j;
                j++;
            }
        }
        if (oldStart >= 0) addRange(oldR, oldStart, i);
        if (newStart >= 0) addRange(newR, newStart, j);

        String o = oldR.length() == 0 ? "." : oldR.toString();
        String n = newR.length() == 0 ? "." : newR.toString();
        return o + " | " + n;
    }

    // Lines are stored as ISO-8859-1 (raw bytes). Turn the bytes back into UTF-8 text,
    // then into code points, so an emoji counts as one character.
    private static int[] codePoints(String isoLine) {
        byte[] bytes = isoLine.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8).codePoints().toArray();
    }

    private static void addRange(StringBuilder sb, int start, int end) {
        if (sb.length() > 0) sb.append(',');
        sb.append(start).append('-').append(end);
    }
}