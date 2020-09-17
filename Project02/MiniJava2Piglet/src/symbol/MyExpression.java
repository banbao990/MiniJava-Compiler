package symbol;

public class MyExpression extends MyType {
    // 只用得到 name
    // 而且只有类用得到,数组用不到,因为minijava不允许定义类的数组
    public MyExpression(String name) {
        super(name);
    }

    /*-------------------------------------------------*/
    // 构造函数
    // name 表示变量的类型
    public MyExpression(String name, int row, int col) {
        super(name, row, col);
    }
}
