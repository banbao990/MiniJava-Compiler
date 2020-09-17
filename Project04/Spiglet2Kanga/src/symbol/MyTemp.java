package symbol;

public class MyTemp extends MyType {
    /** TEMP 名称 */
    public final String name;
    /** 名称对应的数字 */
    public final int nameDigit;
    /** 活性区间起点 */
    public int start;
    /** 活性区间终点 */
    public int end;

    /** 默认构造函数 */
    public MyTemp(String name) {
        this.name = name;
        this.nameDigit = Integer.parseInt(name);
    }
}
