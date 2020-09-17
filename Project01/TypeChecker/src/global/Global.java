package global;

import java.util.ArrayList;
import java.util.HashSet;

import symbol.MyClass;
import symbol.MyClassList;
import typecheck.ErrorPrinter;

public class Global {
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
        if(rightType == null) {
            return true;
        }
        // 相同类型
        if(leftType.equals(rightType)) {
            return true;
        }
        // 检查 rightType 是 leftType 的子类
        // 注意 A->B->A, test(A,C)
        HashSet<String> s = new HashSet<String>();
        while(!rightType.equals(leftType)){
            // 存在上述情况
            if(s.contains(rightType)) {
                return false;
            }
            // 可能 Class rightType 的父类未声明(之前肯定已经检查过了,这里不报错)
            MyClass extendClass = Global.allClass.getClassByName(rightType);
            if(extendClass == null) {
                return false;
            }
            s.add(rightType);
            rightType = extendClass.getExtendsClassName();
            if(rightType == null) {
                return false;
            }
        }
        return true;
    }

    // 检查继承的类是否都声明了
    private static void extendsClassDeclarationCheck() {
        for(String nowClassName : typeList) {
            MyClass nowClass = allClass.getClassByName(nowClassName);
            String extendsClassName = nowClass.getExtendsClassName();
            // 可能没有继承于别的类
            if(extendsClassName != null && !typeList.contains(extendsClassName)) {
                ErrorPrinter.print("Class \"" + extendsClassName + "\" is not declared!",
                        nowClass.getRow(), nowClass.getCol());
            }
        }
    }

    private static void cycleExtendsCheck() {
        // 循环继承的检查(DAG),考虑到类的数量很少,直接使用 checkTypeMatch 进行检查
        // 时间复杂度:O(n^3)
        int size = typeList.size();
        for(int i = 0; i < size; ++i) {
            String class01 = typeList.get(i);
            for(int j = i + 1; j < size; ++j) {
                String class02 = typeList.get(j);
                if(checkTypeMatch(class01, class02)
                        && checkTypeMatch(class02, class01)) {
                    MyClass class03 = allClass.getClassByName(class01);
                    ErrorPrinter.print("\"" + class01 + "\" extends \"" + class02
                            + ", \"" + class02 + "\" extends " + class01 + "\""
                            , class03.getRow(), class03.getCol());
                }
            }
        }
    }
}
