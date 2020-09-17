package symbol;

public class MyType {

    public MyType(String name) {
        this.name = name;
    }

    /*------------------------------------------*/
    private String name;
    private int col;
    private int row;

    // 默认构造函数(MyClassList/MyVarTypeList)
    public MyType() {
    }

    // 构造函数
    public MyType(String name, int row, int col) {
        this.name = name;
        this.row = row;
        this.col = col;
    }

    // getter col
    public int getCol() {
        return col;
    }

    // setter col
    public void setCol(int col) {
        this.col = col;
    }

    // getter row
    public int getRow() {
        return row;
    }

    // setter row
    public void setRow(int row) {
        this.row = row;
    }

    // getter name
    public String getName() {
        return this.name;
    }

    // setter name
    public void setName(String name) {
        this.name = name;
    }
}
