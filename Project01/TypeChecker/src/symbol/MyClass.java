package symbol;

import java.util.HashMap;
import java.util.HashSet;

import global.Global;

public class MyClass extends MyIdentifier {
    // 包括:函数列表(不允许重载), 继承类
    private HashMap<String, MyMethod> methodList = new HashMap<>();
    private String extendsClassName;

    // 构造函数
    public MyClass(String className, int row, int col) {
        super(className, row, col);
    }

    // getter methodList
    public MyMethod getMethodByName(String methodName) {
        // 首先判断是否在本类中定义
        MyMethod method = this.methodList.get(methodName);
        if (method != null) {
            return method;
        }
        String extendsClassName = this.getExtendsClassName();
        // 循环继承问题
        HashSet<String> s = new HashSet<>();
        s.add(super.getName());
        // 检查继承类中定义
        while (extendsClassName != null) {
            // 检测到循环继承
            if (s.contains(extendsClassName)) {
                break;
            }
            s.add(extendsClassName);
            MyClass extendsClass = Global.allClass
                    .getClassByName(extendsClassName);
            // 继承类未定义
            if (extendsClass == null) {
                return null;
            }
            method = extendsClass.methodList.get(methodName);
            if (method != null) {
                return method;
            }
            extendsClassName = extendsClass.getExtendsClassName();
        }
        return null;
    }

    // setter methodList
    // 插入函数到methodList
    // 语义检查
    // 在第一次dfs是调用,不判断
    public String insertMethod(MyMethod method) {
        // 判断函数是否已经定义过了
        String name = method.getName();
        if (this.methodList.containsKey(name)) {
            return ("function \"" + name + "\" exsited in the class \""
                    + this.getName()
                    + "\", overloading is not allowed in minijava!");
        }
        // 判断父类里面是否存在同名方法
        // 存在声明完全一致的同名方法 -> OK(允许覆盖override)
        // 存在声明不完全一致的同名方法 -> ERROR(不允许重载overload)
        // 插入函数
        this.methodList.put(name, method);
        return null;
    }

    // getter, extendsClass
    public String getExtendsClassName() {
        return this.extendsClassName;
    }

    public MyClass getExtendsClass() {
        String extendsClass = this.extendsClassName;
        if (extendsClass == null) {
            return null;
        }
        return Global.allClass.getClassByName(extendsClass);
    }

    // setter, extendsClass
    public void setExtendsClass(String extendsClassName) {
        this.extendsClassName = extendsClassName;
    }

    public String getTypeByVarName(String varName) {
        String returnType = super.getTypeByLocalVarName(varName);
        if (returnType != null) {
            return returnType;
        }
        // 局部变量中未声明,检查继承类中的声明
        // 注意类的循环继承
        String extendsClassName = this.getExtendsClassName();
        // 循环继承问题
        HashSet<String> s = new HashSet<>();
        s.add(super.getName());
        // 检查继承类中定义
        while (extendsClassName != null) {
            // 检测到循环继承
            if (s.contains(extendsClassName)) {
                break;
            }
            s.add(extendsClassName);
            MyClass extendsClass = Global.allClass
                    .getClassByName(extendsClassName);
            // 继承类未定义
            if (extendsClass == null) {
                return null;
            }
            returnType = extendsClass.getTypeByLocalVarName(varName);
            if (returnType != null) {
                return returnType;
            }
            extendsClassName = extendsClass.getExtendsClassName();
        }
        return null;
    }

    // 是否有变量定义
    // 需要解决循环继承的问题
    public boolean containsVar(String varName) {
        // 类中定义
        if (super.containsLocalVar(varName)) {
            return true;
        }
        String extendsClassName = this.getExtendsClassName();
        // 循环继承问题
        HashSet<String> s = new HashSet<>();
        s.add(super.getName());
        // 检查继承类中定义
        while (extendsClassName != null) {
            // 检测到循环继承
            if (s.contains(extendsClassName)) {
                break;
            }
            s.add(extendsClassName);
            MyClass extendsClass = Global.allClass
                    .getClassByName(extendsClassName);
            // 继承类未定义
            if (extendsClass == null) {
                return false;
            }
            if (extendsClass.containsLocalVar(varName)) {
                return true;
            }
            extendsClassName = extendsClass.getExtendsClassName();
        }
        return false;
    }
}
