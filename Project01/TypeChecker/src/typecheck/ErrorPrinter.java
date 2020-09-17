package typecheck;

import static java.lang.System.out;

import java.util.LinkedList;
import java.util.Queue;

public class ErrorPrinter {
    private static int size = 0;
    public static Queue<String> errorMsg = new LinkedList<>();

    public static void print(String msg, int row, int col) {
        out.printf("Error : %s\n    row:%d, col:%d\n\n", msg, row, col);
        ++size;
    }

    public static int getSize() {
        return size;
    }
}
