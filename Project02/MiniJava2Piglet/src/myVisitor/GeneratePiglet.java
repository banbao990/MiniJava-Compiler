package myVisitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import global.Global;
import symbol.MyClass;
import symbol.MyClassList;
import symbol.MyIdentifier;
import symbol.MyMethod;
import symbol.MyType;
import symbol.MyVar;
import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeList;
import syntaxtree.NodeListOptional;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.NodeToken;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;

public class GeneratePiglet extends GJDepthFirst<MyType, MyType> {
    /*
     * BUG-FIX-AFTER
     * 1. 多态的问题,参数传递时的隐式赋值
     *    (需要在MessageSend调用之前修改para的nowtype)
     * 2. 参数大于19传递过程中 复制时少了一个 accept(哭)
     * 3. 变量初始化(??) -> 不需要检查
     * 4. 数组越界(a[a.length] => ERROR)
     */
    // 记录局部变量和 TEMP 的映射关系,注意在每次进入method的时候会被更新为 "空"
    // 因为不同的函数之间互不干扰,而且由于顺序读取,除了一个函数之后不会再使用之前的 Temp
    // 这里注意主类 main 函数(也要加上)
    // 只记录局部变量(非成员变量),this(特殊处理->加入,因为成员变量没有存这个,当然也可以另外判断)
    // 不需要全保存,不放入Global
    // TODO, 可以在每次进入的时候将 TEMP 计数设置为 0,进而优化
    HashMap<String, String> name2Temp = new HashMap<>();

    // 在函数调用的过程中用到
    // 记录参数的个数
    int paraCount = 0;
    // 记录当前调用的函数
    MyMethod paraMethod;
    // 记录参数过多时的基地址
    String paraBaseAddr;

    // 需要重载的函数
    @Override
    public MyType visit(NodeList n, MyType arg) {
        MyType _ret = null;
        // int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this, arg);
            // _count++;
        }
        return _ret;
    }

    @Override
    public MyType visit(NodeListOptional n, MyType arg) {
        if (n.present()) {
            MyType _ret = null;
            // int _count=0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                e.nextElement().accept(this, arg);
                // _count++;
            }
            return _ret;
        } else
            return null;
    }

    @Override
    public MyType visit(NodeOptional n, MyType arg) {
        if (n.present())
            return n.node.accept(this, arg);
        else
            return null;
    }

    @Override
    public MyType visit(NodeSequence n, MyType arg) {
        MyType _ret = null;
        // int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this, arg);
            // _count++;
        }
        return _ret;
    }

    @Override
    public MyType visit(NodeToken n, MyType arg) {
        return null;
    }

    //
    // User-generated visitor methods below
    //

    // 周游
    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    @Override
    public MyType visit(Goal n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public MyType visit(MainClass n, MyType arg) {
        Global.outputString += Global.varMain;// MAIN
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        // get mainClass
        MyIdentifier id01 = (MyIdentifier) n.f1.accept(this, arg);
        MyClass mainClass = ((MyClassList) arg).getClassByName(id01.getName());
        MyMethod mainMethod = mainClass.getMethodByName("main");
        n.f2.accept(this, mainMethod);
        n.f3.accept(this, mainMethod);
        n.f4.accept(this, mainMethod);
        n.f5.accept(this, mainMethod);
        n.f6.accept(this, mainMethod);
        n.f7.accept(this, mainMethod);
        n.f8.accept(this, mainMethod);
        n.f9.accept(this, mainMethod);
        n.f10.accept(this, mainMethod);
        n.f11.accept(this, mainMethod);
        n.f12.accept(this, mainMethod);
        n.f13.accept(this, mainMethod);
        // 由于 main 没有 visitor,手动构造
        this.name2Temp = new HashMap<>();
        this.name2Temp.put("this", " TEMP 0 ");
        // 说实话,这里只有 String[] arg,没必要这么麻烦,但是写了就算了,肯定也不会超过20
        ArrayList<String> paraList = mainMethod.parameterNameList;
        int i = 1;
        for (String varName : paraList) {
            this.name2Temp.put(varName, Global.varTemp + i + " ");
            ++i;
        }
        n.f14.accept(this, mainMethod);
        n.f15.accept(this, mainMethod);
        n.f16.accept(this, mainMethod);
        n.f17.accept(this, mainMethod);
        Global.outputString += Global.varEnd;// END
        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     *        | ClassExtendsDeclaration()
     */
    @Override
    public MyType visit(TypeDeclaration n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public MyType visit(ClassDeclaration n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        // 获取类名
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        MyClass commonClass = ((MyClassList) arg).getClassByName(id.getName());
        n.f2.accept(this, commonClass);
        n.f3.accept(this, commonClass);
        n.f4.accept(this, commonClass);
        n.f5.accept(this, commonClass);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public MyType visit(ClassExtendsDeclaration n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        // get extendClass
        // extendClass 表示有继承别人的类(不是指继承的类)
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        MyClass extendsClass = ((MyClassList) arg).getClassByName(id.getName());
        n.f2.accept(this, extendsClass);
        n.f3.accept(this, extendsClass);
        n.f4.accept(this, extendsClass);
        n.f5.accept(this, extendsClass);
        n.f6.accept(this, extendsClass);
        n.f7.accept(this, extendsClass);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public MyType visit(VarDeclaration n, MyType arg) {
        MyType _ret = null;
        // 给所有的变量设置一个当前类型的属性
        String typeName = n.f0.accept(this, arg).getName();
        String nowVarName = n.f1.accept(this, arg).getName();
        MyVar nowVar;
        if (arg instanceof MyClass) {
            nowVar = ((MyClass) arg).getLocalVarList().get(nowVarName);
        }
        // else if(arg instanceof MyMethod) {
        else {
            nowVar = ((MyMethod) arg).getLocalVarList().get(nowVarName);
        }
        nowVar.nowType = typeName;
        // 声明时将局部变量和 TEMP 对应上
        // 当然成员变量不需要加进去
        // 只加入局部变量
        if (arg instanceof MyMethod) {
            this.name2Temp.put(nowVarName, Global.getTemp());
        }
        n.f2.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public MyType visit(MethodDeclaration n, MyType arg) {
        MyType _rel = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyIdentifier id = (MyIdentifier) n.f2.accept(this, arg);
        MyMethod method = ((MyClass) arg).getMethodByName(id.getName());
        // 将参数加入到 name2temp 中
        this.name2Temp = new HashMap<>();
        this.name2Temp.put("this", " TEMP 0 ");
        int i = 1;
        int size = method.parameterNameList.size();
        // 因为还要传入一个 this 参数(自身的符号表)
        if (size <= 19) {
            for (String x : method.parameterNameList) {
                this.name2Temp.put(x, Global.varTemp + i + " ");
                ++i;
            }
        } else {
            // 这里保存 19个,最后一个存为数组引用
            // 具体的操作在 ExpressionList, ExpressionRest 中实现
            size = 19;
            i = 1;
            for (String x : method.parameterNameList) {
                this.name2Temp.put(x, Global.varTemp + i + " ");
                ++i;
                if (i == 19)
                    break;
            }
        }
        // 声明函数 ClassName_MethodName
        Global.outputString += arg.getName() + "_" + id.getName() + " [ "
                + (size + 1) + " ]\n" + Global.varBegin; // BEGIN
        n.f3.accept(this, method);
        n.f4.accept(this, method);
        n.f5.accept(this, method);
        n.f6.accept(this, method);
        n.f7.accept(this, method);
        n.f8.accept(this, method);
        n.f9.accept(this, method);
        Global.outputString += Global.varReturn;// RETURN
        n.f10.accept(this, method);
        n.f11.accept(this, method);
        n.f12.accept(this, method);
        Global.outputString += "\n" + Global.varEnd; // END
        return _rel;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    @Override
    public MyType visit(FormalParameterList n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public MyType visit(FormalParameter n, MyType arg) {
        MyType _ret = null;
        // 设置每个变量的类型
        String typeName = n.f0.accept(this, arg).getName();
        String nowVarName = n.f1.accept(this, arg).getName();
        MyVar nowVar = ((MyMethod) arg).getParameterList().get(nowVarName);
        nowVar.nowType = typeName;
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public MyType visit(FormalParameterRest n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
     *        | BooleanType()
     *        | IntegerType()
     *        | Identifier()
     */
    @Override
    public MyType visit(Type n, MyType arg) {
        MyType _ret = null;
        _ret = n.f0.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public MyType visit(ArrayType n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        _ret = new MyType("int[]");
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    @Override
    public MyType visit(BooleanType n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    @Override
    public MyType visit(IntegerType n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> Block()
     *        | AssignmentStatement()
     *        | ArrayAssignmentStatement()
     *        | IfStatement()
     *        | WhileStatement()
     *        | PrintStatement()
     */
    @Override
    public MyType visit(Statement n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public MyType visit(Block n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public MyType visit(AssignmentStatement n, MyType arg) {
        MyType _ret = null;
        // 关于 "="
        // 1. 类 = 表示引用复制
        // 2. 基础类型的 = 表示值复制

        // 获取变量名字
        // 两种情况,局部变量 -> MOVE TEMP * Exp
        // 局部变量包括(参数+局部定义)
        // 成员变量 -> HSTORE Exp2 offset Exp1
        // MOVE TEMP *
        String leftVarName = n.f0.accept(this, arg).getName();
        String tempNameForLeftVar = this.name2Temp.get(leftVarName);
        int offset = ((MyMethod) arg).parameterNameList.indexOf(leftVarName);
        // 有 TEMP 记录, 局部变量(非参数/参数号<19)
        if (tempNameForLeftVar != null) {
            Global.outputString += Global.varMove + tempNameForLeftVar;
        }
        // 局部变量(而且参数号 >= 19)
        // 参数号 = 1 -> offset = 0
        else if (offset != -1) {
            // 存到 TEMP 19 指向的地址中(+offset)
            // 参数号 = 19 -> offset = 18 -> tempOffset = 0
            int tempOffset = (offset - 18) * 4;
            Global.outputString += Global.varHstore + " TEMP 19 " + tempOffset;
        }
        // 成员变量
        // HSTORE TEMP 0 offset Exp
        else {
            MyClass nowClass = (MyClass) ((MyMethod) arg).getParent();
            int offsetOfVar = (nowClass.offsetOfVTable.get(leftVarName) + 1)
                    * 4;
            Global.outputString += Global.varHstore + " TEMP 0 " + offsetOfVar;
        }
        n.f1.accept(this, arg);

        // 需要将变量 f1 的当前属性修改为 f2 返回的属性
        // 这里要求所有的Expression都不能返回null
        String rightType = n.f2.accept(this, arg).getName();

        Global.outputString += "\n";
        n.f3.accept(this, arg);
        MyVar nowVar;
        if (arg instanceof MyClass) {
            nowVar = ((MyClass) arg).getVarByName(leftVarName);
        }
        // else if(arg instanceof MyMethod) {
        else {
            nowVar = ((MyMethod) arg).getVarByName(leftVarName);
        }
        nowVar.nowType = rightType;
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public MyType visit(ArrayAssignmentStatement n, MyType arg) {
        // 基本上每一个地方都需要考虑当前变量是否为参数列表的19个以上
        // 数组的存储(仿造 BubbleSort -> length + ele[0,...,length-1])
        MyType _ret = null;

        // 以下即为数组生成一个TEMP(若已经有了就不需要了)
        // a[1] = 1; a[2] = 2;
        // 此时第二个就不需要了
        String arrayBase = n.f0.accept(this, arg).getName();
        String tempForArrayBase = this.name2Temp.get(arrayBase);
        int offset = ((MyMethod) arg).parameterNameList.indexOf(arrayBase);
        // 有 TEMP 记录, 局部变量(非参数/参数号<19)
        if (tempForArrayBase != null) {
        }

        // HLOAD TEMP * Exp Offset
        // 局部变量(而且参数号 >= 19)
        // 参数号 = 1 -> offset = 0
        else if (offset != -1) {
            // 获取 TEMP
            tempForArrayBase = Global.getTemp();
            // 参数号 = 19 -> offset = 18 -> tempOffset = 0
            int tempOffset = (offset - 18) * 4;
            Global.outputString += "\n" + Global.varHload + tempForArrayBase
                    + " TEMP 19 " + tempOffset + "\n";
        }
        // 成员变量
        else {
            tempForArrayBase = Global.getTemp();
            MyClass nowClass = (MyClass) ((MyMethod) arg).getParent();
            // +1 是因为 offsetOfVTable 中没有记录 DTable,而实际上的 VTable 的第一位是 DTable
            int offsetOfVar = (nowClass.offsetOfVTable.get(arrayBase) + 1) * 4;
            Global.outputString += "\n" + Global.varHload + tempForArrayBase
                    + " TEMP 0 " + offsetOfVar + "\n";
        }
        // 这里不需要记录生成的 TEMP, 因为直接存到了内存区域, 下一次重新读取
        // 为数组生成了 TEMP
        // MOVE TEMP * Exp
        n.f1.accept(this, arg);
        String indexOfArray = Global.getTemp();
        Global.outputString += Global.varMove + indexOfArray;
        n.f2.accept(this, arg);
        n.f3.accept(this, arg);

        // 检查数组越界,仿造 BubbleSort 代码中的样子,数字变成 indexOfArray
        // 计算偏移量是否超过了数组头记录的大小
        String noopLabel = Global.getLabel();
        String tempForLength = Global.getTemp();
        /**
          * TEMP 22 : length
          * TEMP 23 : index
          *
          * HLOAD TEMP 21 TEMP 22 0
          * CJUMP MINUS 1 LT TEMP 23 TEMP 21 L0
          * ERROR
          * L0 NOOP
         */
        // 加载长度
        Global.outputString += "\n" + Global.varHload + tempForLength
                + tempForArrayBase + " 0\n";
        // 进行判断
        // TODO
        // if(indexOfArray<tempForLength) NOOP
        // <=> if(tempForLength<=indexOfArray) ERROR
        // 注意 CJUMP 为 0 跳转
        Global.outputString += Global.varCjump + Global.varMinus + " 1 "
                + Global.varLt + indexOfArray + tempForLength + noopLabel
                + "\n";
        Global.outputString += Global.varError;
        Global.outputString += noopLabel + Global.varNoop;

        n.f4.accept(this, arg);
        // TODO 合并现在看来不太行,因为 MOVE 好像不返回
        // 赋值
        String tempValue = Global.getTemp();
        Global.outputString += Global.varMove + tempValue;
        n.f5.accept(this, arg);

        // 保存到内存
        // HSTORE Exp2 Offset Exp1
        Global.outputString += "\n" + Global.varHstore + Global.varPlus
                + tempForArrayBase + Global.varTimes + " 4 " + Global.varPlus
                + indexOfArray + " 1 " + " 0 " + tempValue + "\n";
        n.f6.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public MyType visit(IfStatement n, MyType arg) {
        // 加个Label 加个跳转 => OK
        /*
         * if exp:
         *     s1;
         * else:
         *     s2;
         *
         *    CJUMP exp L1
         *    s1
         *    JUMP L2
         * L1 NOOP
         *    s2
         * L2 NOOP
         */
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);

        String l1 = Global.getLabel();
        String l2 = Global.getLabel();
        Global.outputString += Global.varCjump;
        n.f2.accept(this, arg);// exp
        Global.outputString += l1 + "\n";
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);// s1
        Global.outputString += Global.varJump + l2 + "\n";
        Global.outputString += l1 + Global.varNoop + "\n";
        n.f5.accept(this, arg);
        n.f6.accept(this, arg);// s2
        Global.outputString += "\n" + l2 + Global.varNoop + "\n";
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public MyType visit(WhileStatement n, MyType arg) {
        /*
         * while exp:
         *     statement
         *
         * L1 NOOP
         *    CJUMP exp L2
         *    statement
         *    JUMP L1
         * L2 NOOp
         */
        MyType _ret = null;
        String l1 = Global.getLabel();
        String l2 = Global.getLabel();
        Global.outputString += l1 + Global.varNoop + "\n" + Global.varCjump;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp
        Global.outputString += l2 + "\n";
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);// statement
        Global.outputString += Global.varJump + l1 + "\n" + l2 + Global.varNoop
                + "\n";
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public MyType visit(PrintStatement n, MyType arg) {
        MyType _ret = null;
        Global.outputString += Global.varPrint;// PRINT
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp
        Global.outputString += "\n";
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> AndExpression()
     *        | CompareExpression()
     *        | PlusExpression()
     *        | MinusExpression()
     *        | TimesExpression()
     *        | ArrayLookup()
     *        | ArrayLength()
     *        | MessageSend()
     *        | PrimaryExpression()
     */
    @Override
    public MyType visit(Expression n, MyType arg) {
        MyType _ret = null;
        _ret = n.f0.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(AndExpression n, MyType arg) {
        // 短路操作
        // 若第一个为 false, 判断结束
        /*
         * Exp1 && Exp2
         *
         * BEGIN
         *    MOVE TEMP 21 0
         *    CJUMP Exp1 L1
         *    MOVE TEMP 21 Exp2
         * L1 NOOP
         * RETURN
         *    TEMP 21
         * END
        */
        MyType _ret = null;
        String t1 = Global.getTemp();
        String l1 = Global.getLabel();
        Global.outputString += Global.varBegin + Global.varMove + t1 + " 0\n"
                + Global.varCjump;
        n.f0.accept(this, arg);// exp1
        Global.outputString += l1 + "\n" + Global.varMove + t1;
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        Global.outputString += "\n" + l1 + Global.varNoop + "\n"
                + Global.varReturn + t1 + "\n" + Global.varEnd;
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(CompareExpression n, MyType arg) {
        /*
         * exp1 < exp2
         *
         * LT exp1 exp2
        */
        MyType _ret = null;
        Global.outputString += Global.varLt;
        n.f0.accept(this, arg);// exp1
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(PlusExpression n, MyType arg) {
        /*
         * exp1 + exp2
         *
         * PLUS exp1 exp2
         */
        MyType _ret = null;
        Global.outputString += Global.varPlus;
        n.f0.accept(this, arg);// exp1
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(MinusExpression n, MyType arg) {
        /*
         * exp1 - exp2
         *
         * MINUS exp1 exp2
         */
        MyType _ret = null;
        Global.outputString += Global.varMinus;
        n.f0.accept(this, arg);// exp1
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(TimesExpression n, MyType arg) {
        /*
         * exp1 * exp2
         *
         * MINUS exp1 exp2
         */
        MyType _ret = null;
        Global.outputString += Global.varTimes;
        n.f0.accept(this, arg);// exp1
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public MyType visit(ArrayLookup n, MyType arg) {
        // 需要检查数组是否越界
        // 返回值一定为 int, minijava的语法规定
        /*
         * exp1 [ exp2 ]
         *
         * BEGIN
         *     MOVE TEMP 21 exp1
         *     MOVE TEMP 22 exp2        // 索引
         *     HLOAD TEMP 23 TEMP 21 0  // 长度
         *     CJUMP MINUS 1 LT TEMP 22 TEMP 23 L1
         *     ERROR
         * L1  NOOP
         *     HLOAD TEMP 24 PLUS TEMP 21 TIMES 4 PLUS 1 TEMP 22 0
         * RETURN
         *     TEMP 24
         * END
         */
        MyType _ret = null;
        // TEMP, LABEL
        String t1 = Global.getTemp();
        String t2 = Global.getTemp();
        String t3 = Global.getTemp();
        String t4 = Global.getTemp();
        String l1 = Global.getLabel();
        Global.outputString += Global.varBegin + Global.varMove + t1;
        n.f0.accept(this, arg);// exp1
        Global.outputString += "\n" + Global.varMove + t2;
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);// exp2
        Global.outputString += "\n" + Global.varHload + t3 + t1 + " 0\n"
                + Global.varCjump + Global.varMinus + " 1 " + Global.varLt + t2
                + t3 + l1 + "\n" + Global.varError + l1 + Global.varNoop
                + Global.varHload + t4 + Global.varPlus + t1 + Global.varTimes
                + " 4 " + Global.varPlus + " 1 " + t2 + "0\n" + Global.varReturn
                + t4 + "\n" + Global.varEnd;
        n.f3.accept(this, arg);
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public MyType visit(ArrayLength n, MyType arg) {
        /*
         * exp.length
         *
         * BEGIN
         *     MOVE TEMP 21 exp
         *     HLOAD TEMP 22 TEMP 21 0
         * RETURN
         *     TEMP 22
         * END
         */
        String t1 = Global.getTemp();
        String t2 = Global.getTemp();
        MyType _ret = null;
        Global.outputString += Global.varBegin + Global.varMove + t1;
        n.f0.accept(this, arg);// exp
        Global.outputString += "\n" + Global.varHload + t2 + t1 + " 0\n"
                + Global.varReturn + t2 + "\n" + Global.varEnd;
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public MyType visit(MessageSend n, MyType arg) {
        /*
         * exp1.exp2( (exp)* )
         *
         * CALL Func "(" VTable (exp)* ")"
         *
         * TODO 对于参数而言存在问题,在声明时不知道具体的类型
         *
         * ERROR:Func 直接找到名称即可
         * |不过这里存在问题只是因为
         * |修改:
         * |    现在的想法是因为子类调用不到父类同名方法
         * |    因此直接将父类同名方法覆盖(相同的一个Offset)
         * |    如果想调用父类同名方法必须是强制类型转换
         */
        String VTable = Global.getTemp();
        MyType _ret = null;
        Global.outputString += Global.varCall + Global.varBegin + Global.varMove
                + VTable;
        // 获取类名
        // varName 只可能是
        // this/某个对象/new
        MyType forClassName = n.f0.accept(this, arg);// exp1
        String varName;
        String className;
        if (forClassName instanceof MyIdentifier) {
            varName = forClassName.getName();
            MyVar var = ((MyMethod) arg).getLocalVarList().get(varName);
            if (var == null) {
                var = ((MyMethod) arg).getParameterList().get(varName);
            }
            className = var.nowType;
        } else {
            className = forClassName.getName();
        }
        // 获取方法名
        n.f1.accept(this, arg);
        String methodName = n.f2.accept(this, arg).getName();// exp2
        // 可能是继承父类的方法
        while (!Global.allMethodsName.contains(className + "_" + methodName)) {
            className = Global.allClass.getClassByName(className)
                    .getExtendsClassName();
        }
        // FUNC
        // 父类中的同名函数与子类的同名函数是相同的 Offset
        // 因此计算父类的Offset也可以满足子类的要求
        /* Global.outputString += "\n"
                + Global.varReturn
                + className + "_" + methodName
                + Global.varEnd;
        */
        int offsetForFunction = Global.allClass
                .getClassByName(className).offsetOfDTable
                        .get(className + "_" + methodName);
        offsetForFunction *= 4;
        /*
         * |加载函数
         * BEGIN
         *     HLOAD DTABLE VTABLE 0
         *     HLOAD TEMP 21 VTABLE offsetForFunction
         * RETURN
         *     TEMP 21
         * END
         */
        String tt1 = Global.getTemp();
        String DTable = Global.getTemp();
        Global.outputString += "\n" + Global.varReturn + Global.varBegin
                + Global.varHload + DTable + VTable + " 0\n" + Global.varHload
                + tt1 + DTable + " " + offsetForFunction + "\n"
                + Global.varReturn + tt1 + "\n" + Global.varEnd + Global.varEnd;
        n.f3.accept(this, arg);
        Global.outputString += "(" + VTable;
        MyMethod nowMethod = Global.allClass.getClassByName(className)
                .getMethodByName(methodName);
        this.paraMethod = nowMethod;// 记录下当前调用的方法
        n.f4.accept(this, arg);// (exp)*
        Global.outputString += ")\n";
        n.f5.accept(this, arg);
        _ret = new MyType(nowMethod.getReturnType());
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    @Override
    public MyType visit(ExpressionList n, MyType arg) {
        MyType _ret = null;
        this.paraCount = 1; // 设为1,因为之前还有一个DTable
        // 函数调用可能存在嵌套
        int tempParaCount = this.paraCount;
        MyMethod tempMethod = this.paraMethod;
        n.f0.accept(this, arg);
        this.paraMethod = tempMethod;
        this.paraCount = tempParaCount;
        ++this.paraCount;
        n.f1.accept(this, arg);
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public MyType visit(ExpressionRest n, MyType arg) {
        // 处理函数参数过多的问题
        // size > 19
        MyType _ret = null;
        MyMethod tempMethod = this.paraMethod;// 保存
        int tempParaCount = this.paraCount;
        int size = this.paraMethod.parameterNameList.size();
        // size <= 19
        if (size <= 19) {
            // 无事发生,正常操作
            n.f0.accept(this, arg);
            n.f1.accept(this, arg);// exp
            this.paraCount = tempParaCount;
            ++this.paraCount;
        }
        // size > 19
        else if (tempParaCount < 19) {
            // 无事发生,还能够装下
            n.f0.accept(this, arg);
            // BUG-FIX2, 漏了
            n.f1.accept(this, arg);// exp
            this.paraCount = tempParaCount;
            ++this.paraCount;
        }
        // 开辟数组(数组不需要带头)
        else if (tempParaCount == 19) {
            // 这个时候需要为最后一个构造参数数组
            // 为了方便,我们记录一下现在构造的参数数组的基地址
            this.paraBaseAddr = Global.getTemp();
            int leftParas = size - 18;
            // 开辟新空间
            /*
             * BEGIN
             *     MOVE paraBaseAddr HALLOCATE TIMES LeftParas 4
             *     HSTORE paraBaseAddr 0 Exp1
             *     ......
             *     HSTORE paraBaseAddr Offset Expx
             *     ......
             * RETURN
             *     paraBaseAddr
             * END
            */
            Global.outputString += Global.varBegin + Global.varMove
                    + this.paraBaseAddr + Global.varHallocate + Global.varTimes
                    + leftParas + " 4\n" + Global.varHstore + this.paraBaseAddr
                    + " 0 ";
            n.f0.accept(this, arg);
            n.f1.accept(this, arg);// exp
            Global.outputString += "\n";
            this.paraCount = tempParaCount;
            ++this.paraCount;
        }
        // 数组填充
        else if (tempParaCount > 19 && tempParaCount < size) {
            int offset = 4 * (tempParaCount - 19);
            Global.outputString += Global.varHstore + this.paraBaseAddr
                    + offset;
            n.f0.accept(this, arg);
            n.f1.accept(this, arg);// exp
            Global.outputString += "\n";
            this.paraCount = tempParaCount;
            ++this.paraCount;
        }
        // 返回数组
        else {
            int offset = 4 * (tempParaCount - 19);
            Global.outputString += Global.varHstore + this.paraBaseAddr
                    + offset;
            n.f0.accept(this, arg);
            n.f1.accept(this, arg);// exp
            Global.outputString += "\n" + Global.varReturn + this.paraBaseAddr
                    + "\n" + Global.varEnd;
            // 最后一个了
            // ++this.paraCount;
        }
        this.paraMethod = tempMethod;// 恢复
        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     *        | TrueLiteral()
     *        | FalseLiteral()
     *        | Identifier()
     *        | ThisExpression()
     *        | ArrayAllocationExpression()
     *        | AllocationExpression()
     *        | NotExpression()
     *        | BracketExpression()
     */
    @Override
    public MyType visit(PrimaryExpression n, MyType arg) {
        // 此时需要对 Identifier 进行处理
        // 因为声明(不处理)和后续表达(处理)必须分开
        MyType _ret = null;
        _ret = n.f0.accept(this, arg);
        // 是一个 Identifier
        // 取出它的属性表
        if (_ret instanceof MyIdentifier) {
            String varName = _ret.getName();
            String varTemp = this.name2Temp.get(varName);
            int offset = ((MyMethod) arg).parameterNameList.indexOf(varName);
            // 有 TEMP 记录, 局部变量(非参数/参数号<19)
            if (varTemp != null) {
                Global.outputString += varTemp;
            }
            // 参数(参数号>19)
            else if (offset != -1) {
                String tempFor19 = Global.getTemp();
                int tempOffset = (offset - 18) * 4;
                Global.outputString += Global.varBegin + Global.varHload
                        + tempFor19 + " TEMP 19 " + tempOffset + "\n"
                        + Global.varReturn + tempFor19 + "\n" + Global.varEnd;
            }
            // 成员变量
            else {
                String tempForMem = Global.getTemp();
                MyClass nowClass = (MyClass) ((MyMethod) arg).getParent();
                int offsetOfVar = (nowClass.offsetOfVTable.get(varName) + 1)
                        * 4;
                Global.outputString += Global.varBegin + Global.varHload
                        + tempForMem + " TEMP 0 " + offsetOfVar + "\n"
                        + Global.varReturn + tempForMem + "\n" + Global.varEnd;
            }
        }
        // 返回只需要修改一下,不然可能重复做
        if (_ret instanceof MyIdentifier) {
            String typeName = ((MyMethod) arg)
                    .getVarByName(_ret.getName()).nowType;
            _ret = new MyType(typeName);
        }
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public MyType visit(IntegerLiteral n, MyType arg) {
        MyType _ret = null;
        Global.outputString += " " + n.f0.tokenImage + " ";
        n.f0.accept(this, arg);
        _ret = new MyType("int");
        return _ret;
    }

    /**
     * f0 -> "true"
     */
    @Override
    public MyType visit(TrueLiteral n, MyType arg) {
        MyType _ret = null;
        Global.outputString += " 1 ";
        n.f0.accept(this, arg);
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
     * f0 -> "false"
     */
    @Override
    public MyType visit(FalseLiteral n, MyType arg) {
        MyType _ret = null;
        Global.outputString += " 0 ";
        n.f0.accept(this, arg);
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
      * f0 -> <IDENTIFIER>
      */
    @Override
    public MyType visit(Identifier n, MyType arg) {
        // 此处不输出
        // 定义时不需要输出
        // 其余已经在PrimaryExpression中输出
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyIdentifier(n.f0.toString(), n.f0.beginLine,
                n.f0.endColumn);
        return _ret;
    }

    /**
     * f0 -> "this"
     */
    @Override
    public MyType visit(ThisExpression n, MyType arg) {
        MyType _ret = null;
        Global.outputString += " TEMP 0 ";
        n.f0.accept(this, arg);
        String typeName = ((MyMethod) arg).getParent().getName();
        _ret = new MyType(typeName);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public MyType visit(ArrayAllocationExpression n, MyType arg) {
        // 开辟一块空间,返回引用
        /* new int [Exp]
         *
         * BEGIN
         *     MOVE TEMP 21 Exp                                 // 长度
         *     MOVE TEMP 22 HALLOCATE TIMES 4 PLUS 1 TEMP 21    // 基址
         *     HSTORE TEMP 22 0 TEMP 21
         *     // 初始化为0,仿 Bubblesort
         * L1  CJUMP LT 0 TEMP 21 L2
         *     HSTORE PLUS TEMP 22 TIMES 4 TEMP 21 0 0
         *     MOVE TEMP 21 MINUS TEMP 21 1
         *     JUMP L1
         * L2  NOOP
         * RETURN
         *     TEMP 22
         * END
         */
        String t1 = Global.getTemp();
        String t2 = Global.getTemp();
        String l1 = Global.getLabel();
        String l2 = Global.getLabel();
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        Global.outputString += Global.varBegin + Global.varMove + t1;
        n.f3.accept(this, arg);// exp
        Global.outputString += "\n" + Global.varMove + t2 + Global.varHallocate
                + Global.varTimes + " 4 " + Global.varPlus + " 1 " + t1 + "\n"
                + Global.varHstore + t2 + " 0 " + t1 + "\n" + l1
                + Global.varCjump + Global.varLt + " 0 " + t1 + l2 + "\n"
                + Global.varHstore + Global.varPlus + t2 + Global.varTimes
                + " 4 " + t1 + " 0 0\n" + Global.varMove + t1 + Global.varMinus
                + t1 + " 1\n" + Global.varJump + l1 + "\n" + l2 + Global.varNoop
                + "\n" + Global.varReturn + t2 + "\n" + Global.varEnd;
        n.f4.accept(this, arg);
        _ret = new MyType("int[]");
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public MyType visit(AllocationExpression n, MyType arg) {
        // 构造 VTable
        // 直接复制类中就行了
        MyType _ret = null;
        n.f0.accept(this, arg);
        String typeName = n.f1.accept(this, arg).getName();
        // code start
        String DT = Global.getTemp();
        String VT = Global.getTemp();
        Global.outputString += Global.varBegin;
        MyClass nowClass = Global.allClass.getClassByName(typeName);
        // 方法表
        // 这个地方为了处理方便,没有共享方法表
        // TODO 共享方法表
        int size = nowClass.DTable.size() * 4;
        int offset = 0;
        Global.outputString += Global.varMove + DT + Global.varHallocate + size
                + "\n";
        for (String a : nowClass.DTable) {
            Global.outputString += Global.varHstore + DT + offset + " " + a
                    + "\n";
            offset += 4;
        }

        // 变量表
        size = nowClass.VTable.size() * 4 + 4;
        Global.outputString += Global.varMove + VT + Global.varHallocate + size
                + "\n" + Global.varHstore + VT + " 0 " + DT + "\n";
        offset = 4;
        // 清0 : iterTime 次
        int iterTime = nowClass.VTable.size();
        while (iterTime-- > 0) {
            Global.outputString += Global.varHstore + VT + offset + " 0\n";
            offset += 4;
        }
        // 返回
        Global.outputString += Global.varReturn + VT + "\n" + Global.varEnd;
        // code end
        n.f2.accept(this, arg);
        n.f3.accept(this, arg);
        _ret = new MyType(typeName);
        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    @Override
    public MyType visit(NotExpression n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        Global.outputString += Global.varMinus + " 1 ";
        n.f1.accept(this, arg);
        _ret = new MyType("boolean");
        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public MyType visit(BracketExpression n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        return _ret;
    }
}
