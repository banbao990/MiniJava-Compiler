package symbol;

import java.util.HashMap;

public class MyIdentifier extends MyType {
    // 记录当前的实际类型,多态的时候会用到
    public String nowType = "";

    // 获取整个变量列表
    public HashMap<String, MyVar> getLocalVarList() {
        return this.localVarList;
    }

    /*-------------------------------------*/
    // 变量列表(MyClass/MyMethod)
    // 由于规定所有的变量声明必须在所有的变量使用前HashMap就够了)
    // 这么设置会导致 MyVar 的空间变大(MyMethod/MyClass)
    private HashMap<String, MyVar> localVarList = new HashMap<>();
    // 父结点(MyVar/MyMethod)
    private MyIdentifier parent;

    // 默认构造函数
    public MyIdentifier() {
    }

    // 构造函数
    public MyIdentifier(String name, int row, int col) {
        super(name, row, col);
        this.parent = null;
    }

    // 构造函数
    public MyIdentifier(String name, MyIdentifier parent, int row, int col) {
        super(name, row, col);
        this.parent = parent;
    }

    // 添加成员
    public String insertVar(MyVar n) {
        String name = n.getName();
        if (this.localVarList.containsKey(name)) {
            return ("Variable \"" + name + "\" is already exsited!");
        }
        // 参数列表中也不允许出现同名的变量
        if (this instanceof MyMethod
                && ((MyMethod) this).getParameterList().containsKey(name)) {
            return ("Variable \"" + name
                    + "\" is already exsited in the parameter list!");
        }
        this.localVarList.put(name, n);
        return null;
    }

    // getter parent
    public MyIdentifier getParent() {
        return this.parent;
    }

    // setter parent
    public void setParent(MyIdentifier parent) {
        this.parent = parent;
    }

    // 判断变量 varName 是否在局部声明
    public String checkLocalVarDeclaration(String varName) {
        // 局部声明(MyClass/MyMethod)
        if (this.localVarList.containsKey(varName)) {
            return null;
        }
        return ("The variable \"" + varName + "\" is not declared!");
    }

    public String getTypeByLocalVarName(String varName) {
        MyVar var = this.localVarList.get(varName);
        String localVarType = null;
        // 变量未声明(但是已经报过错了)
        if (var != null) {
            localVarType = var.getType();
        }
        return localVarType;
    }

    public boolean containsLocalVar(String varName) {
        return this.localVarList.containsKey(varName);
    }
}
