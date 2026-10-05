import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// JOB 3: Myers' O(ND) algorithm. Works on any int sequence:
// line ids for Part A, Unicode code points for Part B.
// Returns the edit script: one entry per step, KEEP / DELETE / INSERT,
// with the fewest possible DELETE + INSERT.
public class Myers {

    public static final int KEEP = 0;
    public static final int DELETE = 1;
    public static final int INSERT = 2;

    public static int[] diff(int[] a, int[] b) {
        int n = a.length, m = b.length;

        // Shortcut: equal lines at the start and end are always KEEP.
        // Real commits change a small part of a big file, so this saves a lot of work.
        int pre = 0;
        while (pre < n && pre < m && a[pre] == b[pre]) pre++;
        int suf = 0;
        while (suf < n - pre && suf < m - pre && a[n - 1 - suf] == b[m - 1 - suf]) suf++;

        int[] middle = search(a, pre, n - suf, b, pre, m - suf);

        int[] ops = new int[pre + middle.length + suf];   // new int[] is all 0 = KEEP
        System.arraycopy(middle, 0, ops, pre, middle.length);
        return ops;
    }

    // Myers on a[aLo..aHi) and b[bLo..bHi).
    private static int[] search(int[] a, int aLo, int aHi, int[] b, int bLo, int bHi) {
        int n = aHi - aLo, m = bHi - bLo;
        int max = n + m;
        int off = max + 1;                      // diagonal k is stored at v[k + off]
        int[] v = new int[2 * max + 3];         // v[k + off] = furthest x reached on diagonal k
        List<int[]> trace = new ArrayList<>();  // copy of v[-d..d] after each round d
        int foundD = 0;

        // ---- Forward search: round d = paths that use exactly d edits ----
        search:
        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                int x;
                if (k == -d || (k != d && v[off + k - 1] < v[off + k + 1])) {
                    x = v[off + k + 1];         // come from diagonal k+1: move down = INSERT
                } else {
                    x = v[off + k - 1] + 1;     // come from diagonal k-1: move right = DELETE
                }
                int y = x - k;
                // Follow the snake: free diagonal moves while the elements are equal
                while (x < n && y < m && a[aLo + x] == b[bLo + y]) {
                    x++;
                    y++;
                }
                v[off + k] = x;
                if (x >= n && y >= m) {         // reached the end (n, m) with d edits
                    trace.add(Arrays.copyOfRange(v, off - d, off + d + 1));
                    foundD = d;
                    break search;
                }
            }
            // Save only the part of v used in this round (2d+1 values), not the whole array
            trace.add(Arrays.copyOfRange(v, off - d, off + d + 1));
        }

        // ---- Backtrack: walk from (n, m) back to (0, 0) ----
        int[] ops = new int[max];               // filled from the back to the front
        int pos = max;
        int x = n, y = m;
        for (int d = foundD; d > 0; d--) {
            int[] prev = trace.get(d - 1);      // v after round d-1; diagonal k is at prev[k + d - 1]
            int k = x - y;
            int prevK;
            if (k == -d || (k != d && prev[k - 1 + d - 1] < prev[k + 1 + d - 1])) {
                prevK = k + 1;                  // we came down
            } else {
                prevK = k - 1;                  // we came right
            }
            int prevX = prev[prevK + d - 1];
            int prevY = prevX - prevK;

            while (x > prevX && y > prevY) {    // undo the snake: these were KEEPs
                ops[--pos] = KEEP;
                x--;
                y--;
            }
            ops[--pos] = (prevK == k + 1) ? INSERT : DELETE;  // the one edit of this round
            x = prevX;
            y = prevY;
        }
        while (x > 0 && y > 0) {                // the snake of round 0
            ops[--pos] = KEEP;
            x--;
            y--;
        }
        return Arrays.copyOfRange(ops, pos, max);
    }
}