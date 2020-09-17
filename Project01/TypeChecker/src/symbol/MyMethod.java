package symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import global.Global;

// 方法类
public class MyMethod extends MyIdentifier {
    // 包括返回值类型, 变量列表, 参数列表
    private String returnType;
    // 参数列表
    private HashMap<String, MyVar> parameterList = new HashMap<>();
    // 参数类型列表
    private ArrayList<String> parameterTypeList = new ArrayList<>();
    // 语句块里不能有声明语句

    // 构造函数
    public MyMethod(String name, String returnType, MyIdentifier parent,
            int row, int col) {
        super(name, row, col);
        this.setParent(parent);
        this.returnType = returnType;
    }

    // getter, parameterList
    public HashMap<String, MyVar> getParameterList() {
        return this.parameterList;
    }

    // getter, returnType
    public String getReturnType() {
        return this.returnType;
    }

    // setter returnType
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String insertParameters(MyVar n) {
        String name = n.getName();
        // 检查是否有同名参数
        if (this.parameterList.containsKey(name)) {
            return ("Variable \"" + name + "\" is already exsited");
        }
        // 加入参数列表
        this.parameterList.put(name, n);
        this.parameterTypeList.add(n.getType());
        return null;
    }

    // 需要检查null
    public String getTypeByVarName(String varName) {
        String varType = null;
        // 局部定义
        if (super.containsLocalVar(varName)) {
            varType = super.getTypeByLocalVarName(varName);
        }
        // 参数列表定义
        else if (this.parameterList.containsKey(varName)) {
            varType = this.parameterList.get(varName).getType();
        }
        // 类定义
        else {
            varType = ((MyClass) (this.getParent())).getTypeByVarName(varName);
        }
        return varType;
    }

    public boolean matchParameters(MyVarTypeList varTypeList) {
        // 判断是否没有参数
        if (varTypeList == null) {
            return (this.parameterTypeList.size() == 0);
        }
        ArrayList<String> matcher = varTypeList.getVarTypeList();
        // 首先判断参数个数相同
        if (matcher.size() != this.parameterList.size()) {
            return false;
        }
        // 逐个匹配参数类型
        int size = this.parameterList.size();
        for (int i = 0; i < size; ++i) {
            String p = this.parameterTypeList.get(i);
            String m = matcher.get(i);
            // 判断类型(相同,null,子类)
            // minijava 没有 null
            if (!Global.checkTypeMatch(p, m)) {
                return false;
            }
        }
        return true;
    }

    public String checkOverrideExtendsClass() {
        MyClass extendsClass = ((MyClass) this.getParent()).getExtendsClass();
        // 防止 A->B->A 的循环继承
        HashSet<String> s = new HashSet<>();
        // 一些变量用于记录
        String returnErrorMsg = "Overload is not allowed in minijava, \""
                + this.getName()
                + "\" is declared diffirently in the extendsClass \"";
        // 这里也解决了继承类未定义的问题
        while (extendsClass != null) {
            String className = extendsClass.getName();
            // 找完一遍了,没有发现 overload
            if (s.contains(className)) {
                return null;
            }
            s.add(className);
            MyMethod method = extendsClass.getMethodByName(this.getName());
            // 发现同名函数,判断是否为 overload
            if (method != null) {
                // 检查返回值
                if (!this.returnType.equals(method.returnType)) {
                    return (returnErrorMsg + className + "\"");
                }
                // 检查参数声明
                ArrayList<String> varList = method.parameterTypeList;
                if (!varList.equals(this.parameterTypeList)) {
                    return (returnErrorMsg + className + "\"");
                }
            }
            extendsClass = extendsClass.getExtendsClass();
        }
        return null;
    }
}
