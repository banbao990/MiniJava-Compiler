package global;

public class Global {
    // 关于代码输出
    /** 输出的 mips 代码 */
    public static String outputString = "";

    // 一些辅助定义
    static final String blank = "        ";
    static final int blankLength = blank.length();

    /** 将输出规范化 */
    public static void normOfOutputString() {
        // TODO
        // 去除单行的 NOOP
        // 一次去不干净
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        String[] seg = Global.outputString.split("\n");
        String _ret = "";
        for (String x : seg) {
            String temp = x.trim();
            // 空行
            if (temp.equals(""))
                continue;
            if (temp.equals(".text") || temp.equals(".data")) {
                _ret += '\n';
            }
            // 检查是否为函数声明
            // TODO 可能是字符串中含有":"
            int indexOfColon = temp.indexOf(':');
            if (indexOfColon != -1) {
                // 是否为 Label
                boolean isLabel = temp.charAt(temp.length() - 1) != ':';
                if (isLabel) {
                    if (indexOfColon >= Global.blankLength)
                        _ret += temp;
                    else
                        _ret += temp.substring(0, indexOfColon + 1)
                                + Global.blank.substring(indexOfColon + 1)
                                + temp.substring(indexOfColon + 1).trim();
                } else {
                    _ret += temp;
                }
            } else {
                _ret += Global.blank + temp;
            }
            _ret += '\n';
        }
        Global.outputString = _ret;
    }
}
