package global;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import symbol.MyClass;
import symbol.MyClassList;
import typecheck.ErrorPrinter;

public class Global {
    // 一些常量
    public static final String varBegin = "\nBEGIN\n";
    public static final String varEnd = "\nEND\n";
    public static final String varReturn = "\nRETURN\n";
    public static final String varCall = " CALL ";
    public static final String varNoop = " NOOP\n";
    public static final String varError = " ERROR\n";
    public static final String varCjump = " CJUMP ";
    public static final String varJump = " JUMP ";
    public static final String varMain = " MAIN\n";
    public static final String varHstore = " HSTORE ";
    public static final String varHload = " HLOAD ";
    public static final String varMove = " MOVE ";
    public static final String varPrint = " PRINT ";
    public static final String varHallocate = " HALLOCATE ";
    public static final String varLt = " LT ";
    public static final String varPlus = " PLUS ";
    public static final String varMinus = " MINUS ";
    public static final String varTimes = " TIMES ";
    public static final String varTemp = " TEMP ";

    public static final int AddrLength = 4;

    // 建立所有函数的符号表
    public static void buildMethodTable() {
        ArrayList<String> classNames = Global.allClass.getClassTypeList();
        for (String c : classNames) {
            MyClass mc = Global.allClass.getClassByName(c);
            Set<String> methodList = mc.getAllMethodsName();
            for (String m : methodList) {
                Global.allMethodsName.add(c + "_" + m);
            }
        }
    }

    // BUG_FIX1:子类同名函数直接覆盖父类函数
    // 为所有的类建立 VTable 和 DTable
    public static void buildVDTable() {
        // 类型检查保证没有循环继承
        ArrayList<String> classNames = Global.allClass.getClassTypeList();
        // 用一个 set 来记录已经建立过 Table 的类
        HashSet<String> record = new HashSet<>();
        for (String cn : classNames) {
            // 为了保证父类在最前面,需要依次向上找继承的类
            if (record.contains(cn))
                continue;
            MyClass c = Global.allClass.getClassByName(cn);
            // 用来反向保存继承类
            Stack<MyClass> stack = new Stack<>();
            stack.push(c);
            while (true) {
                c = c.getExtendsClass();
                if (c == null || record.contains(c.getName()))
                    break;
                stack.push(c);
            }
            while (!stack.empty()) {
                // 建立 VTable/DTable
                c = stack.pop();
                MyClass ec = c.getExtendsClass();
                String ecName = null;
                if (ec != null) {
                    // 继承类的名称
                    ecName = ec.getName();
                    // 父类添加到子类中
                    // 属性名就是用本来的名字,函数名需要用 ClassName_MethodName
                    // 在 minijava中不存在强制类型转换,因此子类如果隐藏了父类的属性,不可能再使用父类的属性
                    // 因此使用 map 不会有问题
                    c.DTable.addAll(ec.DTable);
                    for (String x : ec.offsetOfDTable.keySet())
                        c.offsetOfDTable.put(x, ec.offsetOfDTable.get(x));
                    c.VTable.addAll(ec.VTable);
                    for (String x : ec.offsetOfVTable.keySet())
                        c.offsetOfVTable.put(x, ec.offsetOfVTable.get(x));
                }
                // VTable
                Set<String> varList = c.getLocalVarList().keySet();
                for (String x : varList) {
                    c.offsetOfVTable.put(x, c.VTable.size());
                    c.VTable.add(x);
                }

                // DTable
                Set<String> methodList = c.getAllMethodsName();
                // 没有父类
                if (ec == null) {
                    for (String x : methodList) {
                        String newMethod = c.getName() + "_" + x;
                        c.offsetOfDTable.put(newMethod, c.DTable.size());
                        c.DTable.add(newMethod);
                    }
                }
                // 有父类
                else {
                    // 覆盖父类同名函数
                    for (String x : methodList) {
                        String check = ecName + "_" + x;
                        String newMethod = c.getName() + "_" + x;
                        int index = c.DTable.indexOf(check);
                        // 如果存在父类同名函数则覆盖之
                        if (index != -1) {
                            c.DTable.set(index, newMethod);
                            c.offsetOfDTable.put(newMethod, index);
                            continue;
                        }
                        c.offsetOfDTable.put(newMethod, c.DTable.size());
                        c.DTable.add(newMethod);
                    }
                }
                record.add(c.getName());
            }
        }
    }

    // 所有的函数
    public static ArrayList<String> allMethodsName = new ArrayList<>();

    // 变量和 TEMP 的映射关系
    // public static HashMap<String, String> name2Temp = new HashMap<>();

    // 最后面输出的 piglet
    public static String outputString = "";
    // 判断代码是否 ERROR 掉
    public static boolean isError = false;

    // TEMP 计数
    public static int tempCount = 20;

    // 获取一个 TEMP
    public static String getTemp() {
        return Global.varTemp + " " + (tempCount++) + " ";
    }

    // Label 计数
    public static int LabelCount = 0;

    // 获取一个 Label
    public static String getLabel() {
        return "L" + (Global.LabelCount++);
    }

    // TODO
    // 优化一下输出代码的结构
    public static void normOfOutputString() {
        Global.outputString = Global.outputString.replaceAll("\n\n", "\n");
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        String[] seg = Global.outputString.split("\n");
        String _ret = "";
        int tab = 0;
        String blank = "    ";
        for (String x : seg) {
            String temp = x.trim();
            if (x.equals(""))
                continue;
            if (temp.startsWith("BEGIN") || temp.startsWith("MAIN")) {
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
                ++tab;
            } else if (temp.startsWith("RETURN") || temp.startsWith("L")) {
                for (int i = 0; i < tab - 1; ++i)
                    temp = blank + temp;
            } else if (temp.startsWith("END")) {
                --tab;
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
            } else {
                for (int i = 0; i < tab; ++i)
                    temp = blank + temp;
            }
            _ret += temp + "\n";
        }
        Global.outputString = _ret;
    }

    // TODO
    // 将生成的piglet输出到文件和标准输出
    public static void outputToFileAndCMD(String fileName) {

    }

    /*-----------------------------------------*/
    public static MyClassList allClass;
    // check 调用时生成,类列表
    private static ArrayList<String> typeList;

    // check 总函数
    public static void check() {
        typeList = allClass.getClassTypeList();
        extendsClassDeclarationCheck();
        cycleExtendsCheck();
    }

    // 检查类型匹配, 能否实现 leftType = rightType
    public static boolean checkTypeMatch(String leftType, String rightType) {
        // null
        // minijava 没有 null
        if (rightType == null) {
            return true;
        }
        // 相同类型
        if (leftType.equals(rightType)) {
            return true;
        }
        // 检查 rightType 是 leftType 的子类
        // 注意 A->B->A,test(A,C)
        HashSet<String> s = new HashSet<String>();
        while (!rightType.equals(leftType)) {
            // 存在上述情况
            if (s.contains(rightType)) {
                return false;
            }
            // 可能 Class rightType 的父类未声明(之前肯定已经检查过了,这里不报错)
            MyClass extendClass = Global.allClass.getClassByName(rightType);
            if (extendClass == null) {
                return false;
            }
            s.add(rightType);
            rightType = extendClass.getExtendsClassName();
            if (rightType == null) {
                return false;
            }
        }
        return true;
    }

    // 检查继承的类是否都声明了
    private static void extendsClassDeclarationCheck() {
        for (String nowClassName : typeList) {
            MyClass nowClass = allClass.getClassByName(nowClassName);
            String extendsClassName = nowClass.getExtendsClassName();
            // 可能没有继承于别的类
            if (extendsClassName != null
                    && !typeList.contains(extendsClassName)) {
                ErrorPrinter.print(
                        "Class \"" + extendsClassName + "\" is not declared!",
                        nowClass.getRow(), nowClass.getCol());
            }
        }
    }

    private static void cycleExtendsCheck() {
        // 循环继承的检查(DAG),考虑到类的数量很少,直接使用 checkTypeMatch 进行检查
        // 时间复杂度:O(n^3)
        int size = typeList.size();
        for (int i = 0; i < size; ++i) {
            String class01 = typeList.get(i);
            for (int j = i + 1; j < size; ++j) {
                String class02 = typeList.get(j);
                if (checkTypeMatch(class01, class02)
                        && checkTypeMatch(class02, class01)) {
                    MyClass class03 = allClass.getClassByName(class01);
                    ErrorPrinter.print(
                            "\"" + class01 + "\" extends \"" + class02 + ", \""
                                    + class02 + "\" extends " + class01 + "\"",
                            class03.getRow(), class03.getCol());
                }
            }
        }
    }
}
