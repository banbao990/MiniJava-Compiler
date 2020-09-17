package myVisitor;

import java.util.Enumeration;

import global.Global;
import symbol.MyClass;
import symbol.MyClassList;
import symbol.MyExpression;
import symbol.MyIdentifier;
import symbol.MyMethod;
import symbol.MyType;
import symbol.MyVarTypeList;
import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
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
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeListOptional;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import typecheck.ErrorPrinter;
import visitor.GJDepthFirst;

/*
 * 1. AndExpression 处理成 (booloean && boolean)
 * 2. 所有的 exp 返回值都有可能为 null(未定义变量或者类型错误,如boolean+int)
 * 3. 允许子类隐藏父类变量(类型可以不同)
 */
public class TypeCheckVisitor extends GJDepthFirst<MyType, MyType> {

    /* NodeList */
    /* NodeListOptional */
    @Override
    public MyType visit(NodeListOptional n, MyType arg) {
        if (n.present()) {
            MyVarTypeList _ret = new MyVarTypeList();
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                MyType type = e.nextElement().accept(this, arg);
                if (type instanceof MyExpression) {
                    // minijava 没有 null 指针, 因此若有一个变量类型出错, 此时参数个数不匹配
                    // null instanceof MyType = false;
                    _ret.insertVarType(((MyExpression) type).getName());
                    _count++;
                }
            }
            // 如果不是 ExpressionList
            // 声明时不是expression
            if (_count == 0) {
                _ret = null;
            }
            return _ret;
        } else {
            return null;
        }
    }

    /* NodeOptional */
    /* NodeSequence */
    /* NodeToken */
    /* Goal */
    /* MainClass */
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
        MyType _ret = null;
        n.f0.accept(this, arg);
        // get mainClass
        MyIdentifier id01 = (MyIdentifier) n.f1.accept(this, arg);
        MyClass mainClass = ((MyClassList) arg).getClassByName(id01.getName());
        MyMethod mainMethod = mainClass.getMethodByName("main");
        n.f2.accept(this, arg);
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
        n.f14.accept(this, mainMethod);
        n.f15.accept(this, mainMethod);
        n.f16.accept(this, mainMethod);
        n.f17.accept(this, mainMethod);
        return _ret;
    }

    /* TypeDeclaration */
    /* ClassDeclaration */
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
        // get commonClass
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        MyClass commonClass = ((MyClassList) arg).getClassByName(id.getName());
        n.f2.accept(this, commonClass);
        n.f3.accept(this, commonClass);
        n.f4.accept(this, commonClass);
        n.f5.accept(this, commonClass);
        return _ret;
    }

    /* ClassExtendsDeclaration */
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

    /* VarDeclaration */
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public MyType visit(VarDeclaration n, MyType arg) {
        MyType _ret = null;
        MyType type = n.f0.accept(this, arg);
        // 判断这种类型是否声明过
        checkTypeDeclared(type);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        return _ret;
    }

    /* MethodDeclaration */
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

        // type
        MyType type = n.f1.accept(this, arg);
        checkTypeDeclared(type);

        // identifier, method
        MyIdentifier id = (MyIdentifier) n.f2.accept(this, arg);
        MyMethod method = ((MyClass) arg).getMethodByName(id.getName());
        // 重载(overload->OK)
        // 覆盖(override->ERROR)
        // 非同名函数 -> OK
        String msg = null;
        if (method != null) {
            msg = method.checkOverrideExtendsClass();
        }
        if (msg != null) {
            ErrorPrinter.print(msg, type.getRow(), id.getCol());
        }
        n.f3.accept(this, method);
        n.f4.accept(this, method);
        n.f5.accept(this, method);
        n.f6.accept(this, method);
        n.f7.accept(this, method);
        n.f8.accept(this, method);
        n.f9.accept(this, method);

        // expression
        MyExpression exp = (MyExpression) n.f10.accept(this, method);
        checkExpEqual(exp, type.getName());
        n.f11.accept(this, method);
        n.f12.accept(this, method);
        return _rel;
    }

    /* FormalParameterList */
    /* FormalParameter */
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public MyType visit(FormalParameter n, MyType arg) {
        MyType _ret = null;
        MyType type = n.f0.accept(this, arg);
        checkTypeDeclared(type);
        n.f1.accept(this, arg);
        return _ret;
    }

    /* FormalParameterRest */
    /* Type */
    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    @Override
    public MyType visit(Type n, MyType arg) {
        MyType _ret = null;
        _ret = n.f0.accept(this, arg);
        return _ret;
    }

    /* ArrayType */
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
        _ret = new MyType("int[]", n.f0.beginLine, n.f0.beginColumn);
        return _ret;
    }

    /* BooleanType */
    /**
     * f0 -> "boolean"
     */
    @Override
    public MyType visit(BooleanType n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyType("boolean", n.f0.beginLine, n.f0.beginColumn);
        return _ret;
    }

    /* IntegetType */
    /**
     * f0 -> "int"
     */
    @Override
    public MyType visit(IntegerType n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyType("int", n.f0.beginLine, n.f0.beginColumn);
        return _ret;
    }

    /* Statement */
    /* Block */
    /* AssignmentStatement */
    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public MyType visit(AssignmentStatement n, MyType arg) {
        MyType _ret = null;
        MyIdentifier id = (MyIdentifier) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp = (MyExpression) n.f2.accept(this, arg);
        String varType = ((MyMethod) arg).getTypeByVarName(id.getName());
        // id 未定义, exp 未定义时不做处理(已经处理过错误->需要输出)
        if (varType == null) {
            ErrorPrinter.print(
                    "The variable \"" + id.getName() + "\" is not declared!",
                    id.getRow(), id.getCol());
        }
        if (varType != null && exp != null
                && !Global.checkTypeMatch(varType, exp.getName())) {
            ErrorPrinter.print("Can not convert from \"" + exp.getName()
                    + "\" to \"" + varType + "\"!", n.f1.beginLine,
                    n.f1.endColumn);
        }
        n.f3.accept(this, arg);
        return _ret;
    }

    /* ArrayAssignmentStatement */
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
        MyType _ret = null;
        MyIdentifier id = (MyIdentifier) n.f0.accept(this, arg);
        // minijava 只不允许 int[] 数组(因此如果是其它类型的数组调用必然类型不匹配)
        // 必然不是数组,不然 ParseError
        String varType = ((MyMethod) arg).getTypeByVarName(id.getName());
        // 判断变量是否定义(错误信息已经输出->需要输出)
        if (varType == null) {
            ErrorPrinter.print(
                    "The variable \"" + id.getName() + "\" is not declared!",
                    id.getRow(), id.getCol());
        }
        if (varType != null && !("int[]").equals(varType)) {
            ErrorPrinter.print(
                    "only \"int[]\" is allowed in minijava,"
                            + " The type of the variable \"" + id.getName()
                            + "\" is not \"int[]\"!",
                    n.f4.beginLine, n.f4.beginColumn);
        }
        n.f1.accept(this, arg);
        MyExpression exp01 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 != null && !("int").equals(exp01.getName())) {
            ErrorPrinter.print("The index of array must be Integer!",
                    exp01.getRow(), exp01.getCol());
        }
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f5.accept(this, arg);
        if (exp02 != null && !("int").equals(exp02.getName())) {
            ErrorPrinter.print("only \"int[]\" is allowed in minijava, "
                    + "can not convert from \"" + exp02.getName()
                    + "\" to int!", n.f4.beginLine, n.f4.beginColumn);
        }
        n.f6.accept(this, arg);
        return _ret;
    }

    /* IfStatement */
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
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp = (MyExpression) n.f2.accept(this, arg);
        // exp 可能为未声明变量
        if (exp != null && !("boolean").equals(exp.getName())) {
            ErrorPrinter.print(
                    "The expression of the \"if()\" must be boolean!",
                    exp.getRow(), exp.getCol());
        }
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);
        n.f5.accept(this, arg);
        n.f6.accept(this, arg);
        return _ret;
    }

    /* WhileStatement */
    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public MyType visit(WhileStatement n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp = (MyExpression) n.f2.accept(this, arg);
        if (exp != null && !("boolean").equals(exp.getName())) {
            ErrorPrinter.print(
                    "The expression of the \"while()\" must be boolean!",
                    exp.getRow(), exp.getCol());
        }
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);
        return _ret;
    }

    /* PrintStatement */
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
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp = (MyExpression) n.f2.accept(this, arg);
        // exp 可能未定义
        if (exp != null && !("int").equals(exp.getName())) {
            ErrorPrinter.print("The expression of the \"System.out.println()\""
                    + " must be int!", exp.getRow(), exp.getCol());
        }
        n.f3.accept(this, arg);
        n.f4.accept(this, arg);
        return _ret;
    }

    /* Expression */
    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    @Override
    public MyType visit(Expression n, MyType arg) {
        return n.f0.accept(this, arg);
    }

    /* AndExpression */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(AndExpression n, MyType arg) {
        String errorMsg = "The operand of '&&' must be boolean";
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 == null || exp02 == null
                || !("boolean").equals(exp01.getName())
                || !("boolean").equals(exp02.getName())) {
            ErrorPrinter.print(errorMsg, n.f1.beginLine, n.f1.beginColumn);
            return null;
        }
        return new MyExpression("boolean", n.f1.beginLine, n.f1.beginColumn);
    }

    /* CompareExpression */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(CompareExpression n, MyType arg) {
        String errorMsg = "The operand of '<' must be int";
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 == null || exp02 == null || !("int").equals(exp01.getName())
                || !("int").equals(exp02.getName())) {
            ErrorPrinter.print(errorMsg, n.f1.beginLine, n.f1.beginColumn);
            return null;
        }
        return new MyExpression("boolean", n.f1.beginLine, n.f1.beginColumn);
    }

    /* PlusExpression */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(PlusExpression n, MyType arg) {
        String errorMsg = "The operand of '+' must be int";
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 == null || exp02 == null || !("int").equals(exp01.getName())
                || !("int").equals(exp02.getName())) {
            ErrorPrinter.print(errorMsg, n.f1.beginLine, n.f1.beginColumn);
            return null;
        }
        return new MyExpression("int", n.f1.beginLine, n.f1.beginColumn);
    }

    /* MinusExpression */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(MinusExpression n, MyType arg) {
        String errorMsg = "The operand of '-' must be int";
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 == null || exp02 == null || !("int").equals(exp01.getName())
                || !("int").equals(exp02.getName())) {
            ErrorPrinter.print(errorMsg, n.f1.beginLine, n.f1.beginColumn);
            return null;
        }
        return new MyExpression("int", n.f1.beginLine, n.f1.beginColumn);
    }

    /* TimesExpression */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public MyType visit(TimesExpression n, MyType arg) {
        String errorMsg = "The operand of '*' must be int";
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp01 == null || exp02 == null || !("int").equals(exp01.getName())
                || !("int").equals(exp02.getName())) {
            ErrorPrinter.print(errorMsg, n.f1.beginLine, n.f1.beginColumn);
            return null;
        }
        return new MyExpression("int", n.f1.beginLine, n.f1.beginColumn);
    }

    /* ArrayLookup */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public MyType visit(ArrayLookup n, MyType arg) {
        boolean checkError = false;
        // 未定义或类型不是int[]
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        if (exp01 == null || !("int[]").equals(exp01.getName())) {
            ErrorPrinter.print("The type of array must be int[]!",
                    n.f1.beginLine, n.f1.beginColumn - 1);
            checkError = true;
        }
        n.f1.accept(this, arg);
        MyExpression exp02 = (MyExpression) n.f2.accept(this, arg);
        if (exp02 == null || !("int").equals(exp02.getName())) {
            ErrorPrinter.print("The index of array must be Integer!",
                    n.f3.beginLine, n.f3.beginColumn - 1);
            checkError = true;
        }
        n.f3.accept(this, arg);
        if (!checkError) {
            return new MyExpression("int", n.f1.beginLine, n.f1.beginColumn);
        } else {
            return null;
        }
    }

    /* ArrayLength */
    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public MyType visit(ArrayLength n, MyType arg) {
        MyExpression exp = (MyExpression) n.f0.accept(this, arg);
        // length 在 minijava 中只有 int[] 有(其他ParseError)
        if (exp != null && !("int[]").equals(exp.getName())) {
            ErrorPrinter.print(
                    "The type of \"" + exp.getName()
                            + "\" does not have the filed named \"length\"",
                    exp.getRow(), exp.getCol());
            // 表示 exp 没用了(类型错误)
            exp = null;
        }
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        if (exp == null) {
            return null;
        }
        return new MyExpression("int", n.f2.beginLine, n.f2.beginColumn);
    }

    /* MessageSend */
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
        MyType _ret = null;
        MyExpression exp = (MyExpression) n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        MyIdentifier id = (MyIdentifier) n.f2.accept(this, arg);
        n.f3.accept(this, arg);
        MyVarTypeList varTypeList = (MyVarTypeList) n.f4.accept(this, arg);
        n.f5.accept(this, arg);
        // 先检查变量是否声明(已经报过错)
        if (exp != null && exp.getName() != null) {
            // 再检查 exp01 是不是一个类
            if (!Global.allClass.containskeys(exp.getName())) {
                ErrorPrinter.print(
                        "The type of \"" + n.f2.f0.toString()
                                + "\" is not a declared class!",
                        exp.getRow(), exp.getCol());
            }
            // 检查 exp01 这个类有没有成员函数
            else {
                MyMethod method = Global.allClass.getClassByName(exp.getName())
                        .getMethodByName(id.getName());
                if (method == null) {
                    ErrorPrinter.print("The class \"" + exp.getName()
                            + "\" does not contains the function named \""
                            + id.getName() + "\"!", id.getRow(), id.getCol());
                }
                // 检查参数类型是否匹配
                else {
                    if (!method.matchParameters(varTypeList)) {
                        ErrorPrinter.print(
                                "The parameters given does not match the required!",
                                n.f3.beginLine, n.f3.beginColumn);
                    } else {
                        _ret = new MyExpression(method.getReturnType(),
                                id.getRow(), id.getCol());
                    }
                }
            }
        }
        return _ret;
    }

    /* ExpressionList */
    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    @Override
    public MyType visit(ExpressionList n, MyType arg) {
        MyExpression exp01 = (MyExpression) n.f0.accept(this, arg);
        MyVarTypeList varTypeList = (MyVarTypeList) n.f1.accept(this, arg);
        // 没有参数
        if (exp01 == null) {
            return null;
        }
        // 名字为 null,没人会引用
        MyVarTypeList _ret = new MyVarTypeList(null, exp01.getRow(),
                exp01.getCol());
        _ret.insertVarType(exp01.getName());
        // 参数不止一个
        if (varTypeList != null) {
            _ret.insertVarTypeList(varTypeList);
        }
        return _ret;
    }

    /* ExpressionRest */
    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public MyType visit(ExpressionRest n, MyType arg) {
        n.f0.accept(this, arg);
        return n.f1.accept(this, arg);
    }

    /* PrimaryExpression */
    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    @Override
    public MyType visit(PrimaryExpression n, MyType arg) {
        // 1.MyExpression(name即为type):表达式(else)
        // 2.MyIdentifier(需要重新自己确定type):标识符(Identifier)
        // 3.返回 null 表示变量没有声明
        MyType _ret = null;
        MyType exp = n.f0.accept(this, arg);
        if (exp instanceof MyExpression) {
            _ret = exp;
        }
        // MyIdentifier
        // 检查变量是否声明,否则导致空指针(报错信息无需输出,已输出->需要输出)
        else if (exp != null) {
            String varName = exp.getName();
            String type = ((MyMethod) arg).getTypeByVarName(varName);
            if (type != null) {
                _ret = new MyExpression(type, exp.getRow(), exp.getCol());
            } else {
                ErrorPrinter.print(
                        "The variable \"" + varName + "\" is not declared!",
                        exp.getRow(), exp.getCol());
            }
        }
        return _ret;
    }

    /* IntegerLiteral */
    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public MyType visit(IntegerLiteral n, MyType arg) {
        n.f0.accept(this, arg);
        MyExpression exp = new MyExpression("int", n.f0.beginLine,
                n.f0.beginColumn);
        return exp;
    }

    /* TrueLiteral */
    /**
     * f0 -> "true"
     */
    @Override
    public MyType visit(TrueLiteral n, MyType arg) {
        n.f0.accept(this, arg);
        MyExpression exp = new MyExpression("boolean", n.f0.beginLine,
                n.f0.beginColumn);
        return exp;
    }

    /* FalseLiteral */
    /**
     * f0 -> "false"
     */
    @Override
    public MyType visit(FalseLiteral n, MyType arg) {
        n.f0.accept(this, arg);
        MyExpression exp = new MyExpression("boolean", n.f0.beginLine,
                n.f0.beginColumn);
        return exp;
    }

    /* Identifier */
    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public MyType visit(Identifier n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, arg);
        _ret = new MyIdentifier(n.f0.toString(), n.f0.beginLine,
                n.f0.endColumn);
        return _ret;
    }

    /* ThisExpression */
    /**
     * f0 -> "this"
     */
    @Override
    public MyType visit(ThisExpression n, MyType arg) {
        // 返回MyExpression, name = className
        String className = ((MyClass) ((MyMethod) arg).getParent()).getName();
        n.f0.accept(this, arg);
        MyExpression exp = new MyExpression(className, n.f0.beginLine,
                n.f0.beginColumn);
        return exp;
    }

    /* ArrayAllocationExpression */
    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public MyType visit(ArrayAllocationExpression n, MyType arg) {
        MyExpression _ret = null;
        n.f0.accept(this, arg);
        n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        // 数组下标必须为Integer
        MyExpression arrayIndex = (MyExpression) n.f3.accept(this, arg);
        if (arrayIndex == null || !("int").equals(arrayIndex.getName())) {
            ErrorPrinter.print("The index of array must be Integer!",
                    n.f2.beginLine, n.f2.beginColumn + 1);
            _ret = null;
        } else {
            _ret = new MyExpression("int[]", n.f0.beginLine, n.f0.beginColumn);
        }
        n.f4.accept(this, arg);
        return _ret;
    }

    /* AllocationExpression */
    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public MyType visit(AllocationExpression n, MyType arg) {
        // 构造函数只能为无参构造函数
        MyType _ret = null;
        n.f0.accept(this, arg);
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        // 检查类是否声明
        if (!Global.allClass.containskeys(id.getName())) {
            ErrorPrinter.print(
                    "The Class \"" + id.getName() + "\" is not declared!",
                    id.getRow(), id.getCol());
        } else {
            _ret = new MyExpression(id.getName(), id.getRow(), id.getCol());
        }
        n.f2.accept(this, arg);
        n.f3.accept(this, arg);
        return _ret;
    }

    /* NotExpression */
    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    @Override
    public MyType visit(NotExpression n, MyType arg) {
        n.f0.accept(this, arg);
        MyType exp = n.f1.accept(this, arg);
        if (exp != null && !("boolean").equals(exp.getName())) {
            ErrorPrinter.print("The operand of '!' must be boolean",
                    exp.getRow(), exp.getCol());
        }
        return exp;
    }

    /* BracketExpression */
    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public MyType visit(BracketExpression n, MyType arg) {
        MyType exp = null;
        n.f0.accept(this, arg);
        exp = n.f1.accept(this, arg);
        n.f2.accept(this, arg);
        return exp;
    }

    /* 判断类型是否匹配 */
    // 检查实际返回值是否和声明的返回值一致
    public boolean checkExpEqual(MyExpression n, String leftType) {
        if (n == null) {
            return false;
        }
        String rightType = n.getName();
        if (Global.checkTypeMatch(leftType, rightType)) {
            return true;
        }
        ErrorPrinter.print("Return expression doesn't match the return type",
                n.getRow(), n.getCol());
        return false;
    }

    /* 判断类型是否已经声明过 */
    // 检查使用的标识符(声明类或者内置)是否声明过(可能在后面声明)
    public void checkTypeDeclared(MyType type) {
        String typeName = "";
        // 声明类
        if (type instanceof MyIdentifier) {
            typeName = ((MyIdentifier) type).getName();
            if (Global.allClass.containskeys(typeName)) {
                return;
            }
        }
        // 内置标识符
        else {
            typeName = type.getName();
            if (("int").equals(typeName) || ("int[]").equals(typeName)
                    || ("boolean").equals(typeName)) {
                return;
            }
        }
        ErrorPrinter.print("Undefined type:\"" + typeName + "\"", type.getRow(),
                type.getCol());
    }
}
