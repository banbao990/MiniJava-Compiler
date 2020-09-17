package myVisitor;

import java.util.ArrayList;
import java.util.Enumeration;

import global.Global;
import symbol.MyCall;
import symbol.MyExp;
import symbol.MyStmtList;
import symbol.MyType;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Exp;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.Node;
import syntaxtree.NodeListOptional;
import syntaxtree.Operator;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.GJDepthFirst;

public class GenerateSpiglet extends GJDepthFirst<MyType, MyType> {

    // TODO
    /*
     * idea: 子结点将复合语句拆解然后返回给父结点自身是否为 TEMP
     *     1. IntegerLiteral/Label/TEMP 不输出(父类输出)
     *     2. Exp 不能返回 null,不输出
     *
     * TODO:
     *     1. visitor 好像可以使用不带参数的
     */

    // Auto class visitors--probably don't need to be overridden.
    /* NodeList */
    /* NodeOptional */
    /* NodeSequence */
    /* NodeSequence */


    /* NodeListOptional */
    @Override
    public MyType visit(NodeListOptional n, MyType arg) {
        // Goal/StmtList/Call
        MyType upDown = null;

        // 如果是 MyStmtList 传下来的就直接下传(为了 Label)
        if(arg != null && arg instanceof MyStmtList)
            upDown = arg;
        if ( n.present() ) {
            MyType _ret = null;
            if(arg != null && arg instanceof MyCall) {
                // 保存当前调用的记录
                ArrayList<String> tempParas = Global.paras;
                for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                    MyType exp = e.nextElement().accept(this, upDown);
                    if(!exp.isTemp) {
                        String t1 = Global.getTemp();
                        Global.outputString += Global.varMove + t1 + exp;
                        exp = new MyType(t1, true);
                    }
                    tempParas.add(exp.toString());
                }
                // 恢复记录
                Global.paras = tempParas;
            }
            // origin
            else {
                // int _count = 0;
                for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                    e.nextElement().accept(this, upDown);
                    // _count++;
                }
                return _ret;
            }
            return _ret;
        }
        else
            return null;
    }

    // User-generated visitor methods below

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    @Override
    public MyType visit(Goal n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// MAIN
        Global.outputString += Global.varMain;
        n.f1.accept(this, null);
        Global.outputString += Global.varEnd;
        n.f2.accept(this, null);// END
        n.f3.accept(this, null);
        n.f4.accept(this, null);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    @Override
    public MyType visit(StmtList n, MyType arg) {
        MyType _ret = null;
        // Label
        // TODO
        n.f0.accept(this ,new MyStmtList());
        // n.f0.accept(this ,null);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    @Override
    public MyType visit(Procedure n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null); // Label
        n.f1.accept(this, null); // [
        n.f2.accept(this, null); // IntegerLiteral
        n.f3.accept(this, null); // ]
        Global.outputString += n.f0.f0.tokenImage
                + " [ " + n.f2.f0.tokenImage + " ]\n";
        n.f4.accept(this, null);
        return _ret;
    }

    /* Stmt */
    // Stmt 的内容子类已经都输出了
    /**
     * f0 -> NoOpStmt()
     *         | ErrorStmt()
     *         | CJumpStmt()
     *         | JumpStmt()
     *         | HStoreStmt()
     *         | HLoadStmt()
     *         | MoveStmt()
     *         | PrintStmt()
     */

    /**
     * f0 -> "NOOP"
     */
    @Override
    public MyType visit(NoOpStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// NOOP
        Global.outputString += Global.varNoop;
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    @Override
    public MyType visit(ErrorStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// ERROR
        Global.outputString += Global.varError;
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Exp()
     * f2 -> Label()
     */
    @Override
    public MyType visit(CJumpStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// CJUMP
        MyType exp1 = n.f1.accept(this, null);

        if(!exp1.isTemp) {
            /*
             * CJUMP Exp Label
             *
             * 返回后 Exp 已经是一个 TEMP
             *
             * MOVE TEMP 21 Exp
             * CJUMP TEMP 21 Label
             */
            String t1 = Global.getTemp();
            Global.outputString += Global.varMove + t1 + exp1 + "\n";
            exp1 = new MyType(t1);
        }

        n.f2.accept(this, null);// Label

        // CJUMP TEMP 21 Label
        // 这里用 exp1 的原因是因为保证返回 exp1 的一致性
        Global.outputString += Global.varCjump + exp1 + n.f2.f0.tokenImage + "\n";
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    @Override
    public MyType visit(JumpStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);
        n.f1.accept(this, null);

        // JUMP Label
        Global.outputString += Global.varJump + n.f1.f0.tokenImage + "\n";
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Exp()
     * f2 -> IntegerLiteral()
     * f3 -> Exp()
     */
    @Override
    public MyType visit(HStoreStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// HSTORE
        MyType exp1 = n.f1.accept(this, null);// exp1
        if(!exp1.isTemp) {
            String t1 = Global.getTemp();
            Global.outputString += Global.varMove + exp1 + t1 + "\n";
            exp1 = new MyType(t1);
        }
        n.f2.accept(this, null);// IntegerLiteral
        MyType exp2 = n.f3.accept(this, null);// exp2
        if(!exp2.isTemp) {
            String t2 = Global.getTemp();
            Global.outputString += Global.varMove + exp2 + t2 + "\n";
            exp1 = new MyType(t2);
        }
        // HSTORE exp1 IntegerLiteral exp2
        Global.outputString += Global.varHstore + exp1
                + n.f2.f0.tokenImage + exp2 + "\n";
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Exp()
     * f3 -> IntegerLiteral()
     */
    @Override
    public MyType visit(HLoadStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null); // HLOAD
        String t1 = n.f1.accept(this, null).toString();
        MyType exp = n.f2.accept(this, null);
        if(!exp.isTemp) {
            String t2 = Global.getTemp();// TMEP 22
            Global.outputString += Global.varMove + t2 + exp + "\n";
            exp = new MyType(t2, true);
        }
        n.f3.accept(this, null);

        // HLOAD TEMP 21 Exp2 IntegerLiteral
        Global.outputString += Global.varHload
                + t1 + exp + n.f3.f0.tokenImage + "\n";
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    @Override
    public MyType visit(MoveStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// MOVE
        String t1 = n.f1.accept(this, null).toString();// TEMP 21
        MyType exp = n.f2.accept(this, null);// exp
        if(!exp.isTemp) {
            String t2 = Global.getTemp();// TEMP 22
            Global.outputString += Global.varMove + t2 + exp + "\n";
            exp = new MyType(t2, true);
        }

        //  MOVE TEMP 21 Exp
        Global.outputString += Global.varMove + t1 + exp + "\n";
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> Exp()
     */
    @Override
    public MyType visit(PrintStmt n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);// PRINT
        MyType exp = n.f1.accept(this, null);// exp
        if(!exp.isTemp) {
            String t1 = Global.getTemp();
            Global.outputString += Global.varMove + t1 + exp + "\n";
            exp = new MyType(t1, true);
        }
        // PRINT Exp
        Global.outputString += Global.varPrint + exp + "\n";
        return _ret;
    }

    /**
     * f0 -> StmtExp()
     *         | Call()
     *         | HAllocate()
     *         | BinOp()
     *         | Temp()
     *         | IntegerLiteral()
     *         | Label()
     */
    @Override
    public MyType visit(Exp n, MyType arg) {
        // 向下传 MyExp,用于 BEGIN...RETURN...END 的修正
        MyType exp = n.f0.accept(this, new MyExp());
        if(!exp.isTemp) {
            String t1 = Global.getTemp();// TMEP 21
            Global.outputString += Global.varMove + t1 + exp + "\n";
            exp = new MyType(t1, true);
        }
        return exp;
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> Exp()
     * f4 -> "END"
     */
    @Override
    public MyType visit(StmtExp n, MyType arg) {
        // TODO 将输出写到一起

        n.f0.accept(this, null);// BEGIN

        // TODO
        // if(arg == null || !(arg instanceof MyExp))
        if(arg == null)
            Global.outputString += Global.varBegin;

        n.f1.accept(this, null);// StmtList
        n.f2.accept(this, null);// RETURN
        MyType exp = n.f3.accept(this, null);// Exp
        n.f4.accept(this, null);// END

        if(!exp.isSimple) {
            String t1 = Global.getTemp();// TEMP 21
            Global.outputString += Global.varMove + t1 + exp;
            exp = new MyType(t1, true);
        }

        if(arg == null)
            Global.outputString += Global.varReturn + exp + Global.varEnd;

        return exp;
    }

    /**
     * f0 -> "CALL"
     * f1 -> Exp()
     * f2 -> "("
     * f3 -> ( Exp() )*
     * f4 -> ")"
     */
    @Override
    public MyType visit(Call n, MyType arg) {
        // TODO CALL 嵌套的问题 => 后序遍历输出即可
        // 需要记录当前的调用函数
        // 需要记录参数顺序
        /*
         * ex:
         * CALL Exp1 A (
         *     CALL A(
         *         TEMP 21 TEMP 22
         *     )
         *
         *     CALL A (
         *         TEMP 23, CALL A (
         *             TEMP 24 TEMP 25
         *         )
         *     )
         * )
         * MOVE TEMP 26 CALL ( TEMP 21 TEMP 22 )
         * MOVE TEMP 27 CALL ( TEMP 24 TEMP 25 )
         * MOVE TEMP 28 CALL ( TEMP 23 TEMP 27 )
         * MOVE TEMP 29 CALL ( TEMP 26 TEMP 28 )
         *
         * return TEMP 29
         */

        MyType _ret = null;
        n.f0.accept(this, null);// CALL
        MyType exp1 = n.f1.accept(this, null);// Exp1
        n.f2.accept(this, null);// (

        Global.paras = new ArrayList<>();
        n.f3.accept(this, new MyCall());// (Exp)*
        n.f4.accept(this, null);// )


        // do at last
        if(!exp1.isSimple) {
            String t1 = Global.getTemp();// TEMP 21
            Global.outputString += Global.varMove + t1 + exp1 + "\n";
            exp1 = new MyType(t1, true);
        }
        String t2 = Global.getTemp();
        Global.outputString += Global.varMove + t2
                + Global.varCall + exp1 + " ( ";
        for(String temp : Global.paras) {
            Global.outputString += temp;
        }
        Global.outputString += " )\n";
        _ret = new MyType(t2, true);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> Exp()
     */
    @Override
    public MyType visit(HAllocate n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);
        MyType exp = n.f1.accept(this, null);
        if(!exp.isSimple) {
            String t1 = Global.getTemp();
            Global.outputString += Global.varMove + t1 + exp + "\n";
            exp = new MyType(t1, true);
        }
        String t2 = Global.getTemp();
        Global.outputString += Global.varMove + t2
                + Global.varHallocate + exp + "\n";
        _ret = new MyType(t2, true);
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Exp()
     * f2 -> Exp()
     */
    @Override
    public MyType visit(BinOp n, MyType arg) {
        MyType _ret = null;
        MyType op = n.f0.accept(this, null);
        MyType exp1 = n.f1.accept(this, null);
        MyType exp2 = n.f2.accept(this, null);

        if(!exp1.isTemp) {
            String t1 = Global.getTemp();
            Global.outputString += Global.varMove + t1 + exp1 + "\n";
            exp1 = new MyType(t1, true);
        }
        if(!exp2.isSimple) {
            String t2 = Global.getTemp();
            Global.outputString += Global.varMove + t2 + exp2 + "\n";
            exp2 = new MyType(t2, true);
        }
        String t3 = Global.getTemp();
        // toString
        Global.outputString += Global.varMove + t3
                + op + exp1.toString() + exp2.toString() + "\n";
        _ret = new MyType(t3, true);
        return _ret;
    }

    /**
     * f0 -> "LT"
     *         | "PLUS"
     *         | "MINUS"
     *         | "TIMES"
     */
    @Override
    public MyType visit(Operator n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this ,null);
        String name;
        if(n.f0.which == 0) name = Global.varLt;
        else if(n.f0.which == 1) name = Global.varPlus;
        else if(n.f0.which == 2) name = Global.varMinus;
        else name = Global.varTimes;
        _ret = new MyType(name, false);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    @Override
    public MyType visit(Temp n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);
        n.f1.accept(this, null);
        _ret = new MyType(Global.varTemp + n.f1.f0.tokenImage + " ", true);
        return _ret;
    }


    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public MyType visit(IntegerLiteral n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);
        _ret = new MyType(n.f0.tokenImage, false, true);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public MyType visit(Label n, MyType arg) {
        MyType _ret = null;
        n.f0.accept(this, null);
        if(arg instanceof MyStmtList) {
            Global.outputString += " " + n.f0.tokenImage + " ";
        }
        _ret = new MyType(n.f0.tokenImage, false, true);
        return _ret;
    }
}
