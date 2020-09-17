package myVisitor;

import symbol.MyClass;
import symbol.MyClassList;
import symbol.MyIdentifier;
import symbol.MyMethod;
import symbol.MyType;
import symbol.MyVar;
import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.FormalParameter;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MethodDeclaration;
import syntaxtree.Type;
import syntaxtree.VarDeclaration;
import typecheck.ErrorPrinter;
import visitor.GJDepthFirst;

/*
 * 设计思路:所有的事情能让儿子做就都让儿子来做
 * 例外:Identifier,Type
 * 语句块只会出现在函数内部
 * 语句块里不能有赋值语句(因此Block内只需要检验变量是否声明)
 * 不会出现多重继承
*/
public class BuildSymbolTableVisitor extends GJDepthFirst<MyType, MyType> {
    /* NodeList */
    /* NodeListOptional */
    /* NodeOptional */
    /* NodeSequence */
    /* NodeToken */
    /* Goal */
    /* MainClass */
    // 语法要求只能有main函数, 没有其他的字段
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
        String msg = null;
        n.f0.accept(this, arg);
        // identifier
        MyIdentifier id01 = (MyIdentifier) n.f1.accept(this, arg);
        // mainClass, 将 mainClass 插入到 Goal(MyClassList) 节点中
        MyClass mainClass = new MyClass(id01.getName(), id01.getRow(),
                id01.getCol());
        msg = ((MyClassList) arg).insertClass(mainClass);
        if (msg != null) {
            ErrorPrinter.print(msg, mainClass.getRow(), mainClass.getCol());
        }
        // main(method), 将 main 函数插到 mainClass 里面
        MyMethod method = new MyMethod("main", "void", mainClass,
                n.f6.beginLine, n.f6.beginColumn);
        msg = mainClass.insertMethod(method);
        if (msg != null) {
            ErrorPrinter.print(msg, method.getRow(), method.getCol());
        }

        mainClass.insertMethod(method);
        n.f2.accept(this, arg);
        n.f3.accept(this, method);
        n.f4.accept(this, method);
        n.f5.accept(this, method);
        n.f6.accept(this, method);
        n.f7.accept(this, method);
        n.f8.accept(this, method);// "String"
        n.f9.accept(this, method);
        n.f10.accept(this, method);
        // identifier
        MyIdentifier id02 = (MyIdentifier) n.f11.accept(this, method);
        // 加入函数参数列表
        // 这里由于语法规定了只能有一个参数, 不用检查有参数同名(返回值)
        method.insertParameters(new MyVar(id02.getName(), "String", method,
                id02.getRow(), id02.getCol()));
        n.f12.accept(this, method);
        n.f13.accept(this, method);
        n.f14.accept(this, method);
        n.f15.accept(this, method);
        n.f16.accept(this, method);
        n.f17.accept(this, arg);
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
        String msg = null;
        n.f0.accept(this, arg);
        // identifier
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        // class
        MyClass commonClass = new MyClass(id.getName(), id.getRow(),
                id.getCol());
        // 将 class 插入到 ClassList 当中
        msg = ((MyClassList) arg).insertClass(commonClass);
        if (msg != null) {
            ErrorPrinter.print(msg, id.getRow(), id.getCol());
        }
        n.f2.accept(this, commonClass);
        n.f3.accept(this, commonClass);
        n.f4.accept(this, commonClass);
        n.f5.accept(this, commonClass);
        return _ret;
    }

    /* ClassExtendsDeclaration */
    // 语法要求所有所有变量声明须在函数声明之前(f5,f6)
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
        String msg = null;
        n.f0.accept(this, arg);
        // identifier
        MyIdentifier id01 = (MyIdentifier) n.f1.accept(this, arg);
        // class, 将 class 插入到 ClassList 中
        MyClass extendsClass = new MyClass(id01.getName(), id01.getRow(),
                id01.getCol());
        msg = ((MyClassList) arg).insertClass(extendsClass);
        if (msg != null) {
            ErrorPrinter.print(msg, id01.getRow(), id01.getCol());
        }
        n.f2.accept(this, extendsClass);
        // extendsClass
        MyIdentifier id02 = (MyIdentifier) n.f3.accept(this, extendsClass);
        // 设置extendsClass
        extendsClass.setExtendsClass(id02.getName());
        // 自己不能继承自己
        if (id01.getName().equals(id02.getName())) {
            ErrorPrinter.print(
                    "Class \"" + id01.getName() + "\" extends itself!",
                    id01.getRow(), id01.getCol());
        }
        n.f4.accept(this, extendsClass);
        n.f5.accept(this, extendsClass);
        n.f6.accept(this, extendsClass);
        n.f7.accept(this, extendsClass);
        return _ret;
    }

    /* VarDeclaration */
    // 语法要求只是声明, 没有赋值
    // 语法规定父结点可能是 MyClass/MyMethod
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public MyType visit(VarDeclaration n, MyType arg) {
        MyType _ret = null;
        String msg = null;
        // type
        MyType type = n.f0.accept(this, arg);
        // idtentifier
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        // var, 将变量插入类或函数
        MyVar var = new MyVar(id.getName(), type.getName(), (MyIdentifier) arg,
                id.getRow(), id.getCol());
        msg = ((MyIdentifier) arg).insertVar(var);
        if (msg != null) {
            ErrorPrinter.print(msg, id.getRow(), id.getCol());
        }
        n.f2.accept(this, arg);
        return _ret;
    }

    /* MethodDeclaration */
    // arg 表示当前访问元素在语法树中的父结点
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
        MyType _ret = null;
        n.f0.accept(this, arg);// "public"
        MyType type = n.f1.accept(this, arg);// Type
        MyIdentifier id = (MyIdentifier) n.f2.accept(this, arg);// Identifier
        MyMethod method = new MyMethod(id.getName(), type.getName(),
                (MyIdentifier) arg, id.getRow(), id.getCol());

        // 将函数插入到类所在的列表里
        // 这里可以进行语义检查(to do)
        String msg = ((MyClass) arg).insertMethod(method);
        if (msg != null) {
            ErrorPrinter.print(msg, method.getRow(), method.getCol());
        }
        n.f3.accept(this, method);// "("
        n.f4.accept(this, method);// ( FormalParameterList )?
        n.f5.accept(this, method);// ")"
        n.f6.accept(this, method);// "{"
        n.f7.accept(this, method);// ( VarDeclaration )*
        n.f8.accept(this, method);// ( Statement )*
        n.f9.accept(this, method);// "return"
        n.f10.accept(this, method);// Expression
        n.f11.accept(this, method);// ";"
        n.f12.accept(this, method);// "}"
        return _ret;
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
        String msg = null;
        // var,将 var 插入函数的参数列表
        MyType type = n.f0.accept(this, arg);
        MyIdentifier id = (MyIdentifier) n.f1.accept(this, arg);
        MyVar var = new MyVar(id.getName(), type.getName(), (MyMethod) arg,
                id.getRow(), id.getCol());
        msg = ((MyMethod) arg).insertParameters(var);
        if (msg != null) {
            ErrorPrinter.print(msg, var.getRow(), var.getCol());
        }
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
    /* AssignmentStatement */ // 无法检查是否声明
    /* ArrayAssignmentStatement */
    /* IfStatement */
    /* WhileStatement */
    /* PrintStatement */
    /* Expression */
    /* AndExpression */
    /* CompareExpression */
    /* PlusExpression */
    /* MinusExpression */
    /* TimesExpression */
    /* ArrayLookup */
    /* ArrayLength */
    /* MessageSend */
    /* ExpressionList */
    /* ExpressionRest */
    /* PrimaryExpression */ // 无法检查变量是否已经声明过
    /* IntegerLiteral */
    /* TrueLiteral */
    /* FalseLiteral */
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
        // ((MyIdentifier)_ret).setParent((MyIdentifier)arg);
        return _ret;
    }

    /* ThisExpression */
    /* ArrayAllocationExpression */
    /* AllocationExpression */
    /* NotExpression */
    /* BracketExpression */
}
