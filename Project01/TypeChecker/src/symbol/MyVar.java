package symbol;

public class MyVar extends MyIdentifier {

    private String type;

    // 构造函数
    public MyVar(String name, String type, MyIdentifier parent, int row,
            int col) {
        super(name, row, col);
        setType(type);
    }

    // getter, type
    public String getType() {
        return this.type;
    }

    // setter, type
    public void setType(String type) {
        this.type = type;
    }
}
