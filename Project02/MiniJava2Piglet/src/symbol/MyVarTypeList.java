package symbol;

import java.util.ArrayList;

public class MyVarTypeList extends MyType {
    private ArrayList<String> varTypeList = new ArrayList<>();

    // 默认构造函数
    public MyVarTypeList() {
    }

    public MyVarTypeList(String name, int row, int col) {
        super(name, row, col);
    }

    // getter, varTypeList
    public ArrayList<String> getVarTypeList() {
        return this.varTypeList;
    }

    // setter, varTypeList
    public void insertVarType(String varType) {
        this.varTypeList.add(varType);
    }

    public void insertVarTypeList(MyVarTypeList varTypeList2) {
        this.varTypeList.addAll(varTypeList2.getVarTypeList());
    }
}
