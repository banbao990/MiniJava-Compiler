package symbol;

public class MyString extends MyType {
    /** 返回的语句 */
    public String stmt;

    public MyString(String stmt) {
        this.stmt = stmt;
    }

    /** 返回该语句*/
    @Override
    public String toString() {
        return this.stmt;
    }
}
