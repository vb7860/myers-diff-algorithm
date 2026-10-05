import java.util.List;
import java.util.Map;

// JOB 2: Give every distinct line a number (like a roll number).
// The same line text always gets the same number, in both files,
// so Myers can compare two ints instead of two strings.
public class LineNumberer {

    public static int[] toIds(List<String> lines, Map<String, Integer> book) {
        int[] ids = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Integer id = book.get(line);        // seen this line before?
            if (id == null) {                   // no: give it the next new number
                id = book.size();
                book.put(line, id);
            }
            ids[i] = id;
        }
        return ids;
    }
}