package helper;

/** 用于 CALL 语句中的参数 */
public class MyIsSpilled {
    public String name;
    /** 溢出的参数,包括前4个参数 */
    public boolean isSpilled;
    public int offset;

    public MyIsSpilled(String name, boolean isSpilled, int offset) {
        this.name = name;
        this.isSpilled = isSpilled;
        this.offset = offset;
    }
}
