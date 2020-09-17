package global;

import java.util.ArrayList;

public class Global {
    // 一些常量
    public static final String varBegin = "\nBEGIN\n";
    public static final String varEnd = "\nEND\n";
    public static final String varReturn = "\nRETURN\n";
    public static final String varCall = " CALL ";
    public static final String varNoop = " NOOP\n";
    public static final String varError = " ERROR\n";
    public static final String varCjump = " CJUMP ";
    public static final String varJump = " JUMP ";
    public static final String varMain = " MAIN\n";
    public static final String varHstore = " HSTORE ";
    public static final String varHload = " HLOAD ";
    public static final String varMove = " MOVE ";
    public static final String varPrint = " PRINT ";
    public static final String varHallocate = " HALLOCATE ";
    public static final String varLt = " LT ";
    public static final String varPlus = " PLUS ";
    public static final String varMinus = " MINUS ";
    public static final String varTimes = " TIMES ";
    public static final String varTemp = " TEMP ";

    public static ArrayList<String> paras = new ArrayList<>();

    // 输出
    public static String outputString = "";
    // 记录新增的 TEMP
    // public static HashMap<String,String> name2Temp = new HashMap<>();
    // 当前已使用的最大 TEMP
    public static int tempNum = 0;

    // 获取一个新的 TEMP
    public static String getTemp() {
        return Global.varTemp + (++Global.tempNum) + " ";
    }

    // TODO 优化一下关于 Label 的缩进问题
    // 优化一下输出代码的结构
    public static void normOfOutputString() {
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        String[] seg = Global.outputString.split("\n");
        String _ret = "";
        int tab = 0;
        String blank = "    ";
        for (String x : seg) {
            String temp = x.trim();
            if (x.equals(""))
                continue;
            if (temp.startsWith("BEGIN") || temp.startsWith("MAIN")) {
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
                ++tab;
            } else if (temp.startsWith("RETURN") || temp.startsWith("L")) {
                for (int i = 0; i < tab - 1; ++i)
                    temp = blank + temp;
            } else if (temp.startsWith("END")) {
                --tab;
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
            } else {
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
            }
            _ret += temp + "\n";
        }
        Global.outputString = _ret;
    }
}
