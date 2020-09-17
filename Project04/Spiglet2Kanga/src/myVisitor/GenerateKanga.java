package myVisitor;

import java.util.ArrayList;

import global.Global;
import helper.MyIsSpilled;
import helper.MyPair;
import symbol.MyNoOutput;
import symbol.MyPara;
import symbol.MyProcedure;
import symbol.MyString;
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
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.GJDepthFirst;

/**
 * 0. SD(shield) : 屏蔽:
 *    NU(no use) : 无用
 * 1. 注意每一个 Stmt 最多用到3个寄存器,因此至少准备两个临时寄存器(假定两个变量都已经溢出)
 *  | MOVE Reg Op Reg Reg
 * 2. 注意是允许 Call Func (实测)
 *  | Stmt 直接返回语句
 */
public class GenerateKanga extends GJDepthFirst<MyType, MyType> {
    // 全局变量
    /** 临时寄存器 */
    private final String[] regsForTemporaryUse = { " v0 ", " v1 ", " t9 " };
    /** 第 3 个临时寄存器 */
    private final String thirdTemporaryReg = " t9 ";
    /** 当前空余的最小的临时寄存器 */
    private int regsForTemporaryUseNum = 0;
    /** 当前的 Procedure */
    private MyProcedure nowProc;
    /** 当前是第几个 Procedure */
    private int nowProcIndex = -1;
    /** 用于函数参数的保存 */
    private ArrayList<MyIsSpilled> procParas = new ArrayList<>();
    /** 一个记号,用于记录当前的 TEMP 是否为参数 */
    private boolean shouldAddParas = false;
    /** 如果变量溢出,需要在该语句之后将变量存回栈上 */
    private ArrayList<MyPair> restoreSpilled = new ArrayList<>();

    // 自定义函数
    /** 更新当前的 Proc,并且重置 Stmt 的索引  */
    private void refreshProc() {
        this.nowProc = Global.proc.get(++this.nowProcIndex);
    }

    /** 获取一个临时寄存器 */
    private String getTemporaryReg() {
        return this.regsForTemporaryUse[++this.regsForTemporaryUseNum];
    }

    /** 将所有临时寄存器置空 */
    private void freeAllTemporaryReg() {
        this.regsForTemporaryUseNum = -1;
    }
    // 结点的遍历
    /* NodeList */
    /* NodeListOptional */
    /* NodeOptional */
    /* NodeSequence */
    /* NodeToken */

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    @Override
    public MyType visit(Goal n, MyType arg) {
        this.refreshProc();
        // MAIN
        Global.outputString += Global.varMain + "[" + this.nowProc.paras + "]"
                + "[" + this.nowProc.stackSize + "]" + "["
                + this.nowProc.callParas + "]\n";
        // n.f0.accept(this, arg); // NU
        n.f1.accept(this, arg);
        // END
        Global.outputString += Global.varEnd;
        // n.f2.accept(this, arg); // NU
        n.f3.accept(this, arg);
        // n.f4.accept(this, arg); // NU
        return null;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    @Override
    public MyType visit(StmtList n, MyType arg) {
        n.f0.accept(this, arg);
        return null;
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
        this.refreshProc();
        // MAIN
        Global.outputString += this.nowProc.name + " [" + this.nowProc.paras
                + "]" + "[" + this.nowProc.stackSize + "]" + "["
                + this.nowProc.callParas + "]\n";
        // n.f0.accept(this, arg); // SD
        // n.f1.accept(this, arg); // NU
        // n.f2.accept(this, arg); // NU
        // n.f3.accept(this, arg); // NU
        n.f4.accept(this, arg);
        Global.outputString += Global.varEnd;
        return null;
    }

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
    @Override
    public MyType visit(Stmt n, MyType arg) {
        // 如果当前语句本来应该有寄存器分配,但是却没有,而且也没有找到溢出痕迹,则修改为 NOOP
        // 这是为了处理有 Label 的情况
        // Label -> Label NOOP
        // 清空临时寄存器
        this.freeAllTemporaryReg();
        this.restoreSpilled.clear();
        MyType stmt = n.f0.accept(this, new MyNoOutput());
        // 返回值为 null 表示忽略
        if (stmt == null) {
            Global.outputString += Global.varNoop;
        } else {
            Global.outputString += stmt.toString() + "\n";
        }
        // spilled
        for (MyPair pair : this.restoreSpilled) {
            // ASTORE SPILLEDARG 0 s0
            Global.outputString += Global.varAStore + Global.varSpilled
                    + pair.offset + pair.reg + "\n";
        }
        return null;
    }

    /**
     * f0 -> "NOOP"
     */
    @Override
    public MyType visit(NoOpStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        return new MyString(Global.varNoop);
    }

    /**
     * f0 -> "ERROR"
     */
    @Override
    public MyType visit(ErrorStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        return new MyString(Global.varError);
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    @Override
    public MyType visit(CJumpStmt n, MyType arg) {
        // 讲道理这里应该是一定会分配的
        // n.f0.accept(this, arg); // NU
        // 多态
        MyString temp = (MyString) (n.f1.accept(this, arg));
        String label = n.f2.accept(this, arg).toString();
        if (temp == null)
            return null;
        else
            return new MyString(Global.varCjump + temp.toString() + label);
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    @Override
    public MyType visit(JumpStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        String label = n.f1.accept(this, arg).toString();
        return new MyString(Global.varJump + label);
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    @Override
    public MyType visit(HStoreStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        MyString temp1 = (MyString) (n.f1.accept(this, arg));
        String tempInt = n.f2.f0.tokenImage;
        // n.f2.accept(this, arg); // NU
        MyString temp2 = (MyString) (n.f3.accept(this, arg));
        if (temp1 == null || temp2 == null)
            return null;
        return new MyString(Global.varHstore + temp1.toString() + tempInt
                + temp2.toString());
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Temp()
     * f3 -> IntegerLiteral()
     */
    @Override
    public MyType visit(HLoadStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        MyString temp1 = (MyString) (n.f1.accept(this, arg));
        MyString temp2 = (MyString) (n.f2.accept(this, arg));
        // n.f3.accept(this, arg); // NU
        String tempInt = n.f3.f0.tokenImage;
        if (temp1 == null || temp2 == null)
            return null;

        return new MyString(Global.varHload + temp1.toString()
                + temp2.toString() + tempInt);
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    @Override
    public MyType visit(MoveStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        MyString temp = (MyString) (n.f1.accept(this, arg));
        MyString exp = (MyString) (n.f2.accept(this, arg));
        if (temp == null || exp == null)
            return null;
        return new MyString(
                Global.varMove + ' ' + temp.toString() + ' ' + exp.toString());
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    @Override
    public MyType visit(PrintStmt n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        MyString exp = (MyString) (n.f1.accept(this, arg));
        if (exp == null)
            return null;
        return new MyString(Global.varPrint + exp.toString());
    }

    /**
     * f0 -> Call()
     *         | HAllocate()
     *         | BinOp()
     *         | SimpleExp()
     */
    @Override
    public MyType visit(Exp n, MyType arg) {
        return n.f0.accept(this, arg);
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> SimpleExp()
     * f4 -> "END"
     */
    @Override
    public MyType visit(StmtExp n, MyType arg) {
        // TODO
        // 可以优化的地方,并非需要保存所有的寄存器
        // TODO 只需要留位置,但是不需要实际操作
        // step 1.保存所有的寄存器 callee-saved
        // ASTORE SPILLEDARG 0 s0
        int offset = -1;
        // 预留传参的空间
        offset += (this.nowProc.paras > 4 ? this.nowProc.paras - 4 : 0);
        offset += this.nowProc.paras > 4 ? 4 : this.nowProc.paras;
        // int sizeForCycle = 0;
        // for(String reg : Global.aReg) {
        // if(++sizeForCycle > this.nowProc.paras) break;
        // Global.outputString += Global.varAStore + Global.varSpilled
        // + (++offset) + reg + "\n";
        // }
        for (String reg : Global.sReg) {
            Global.outputString += Global.varAStore + Global.varSpilled
                    + (++offset) + reg + "\n";
        }
        for (String reg : Global.tReg) {
            Global.outputString += Global.varAStore + Global.varSpilled
                    + (++offset) + reg + "\n";
        }
        // step 2.加载所有的参数
        int paras = this.nowProc.paras;
        for (int i = 4; i < paras; ++i) {
            String oriTemp = i + "";
            // 已分配寄存器就加载到指定寄存器
            String reg = this.nowProc.temp2reg.get(oriTemp);
            // 若没有分配寄存器,可能是因为没有用到,也可能是因为寄存器溢出
            // 以上两种情况都不需要处理
            if (reg != null) {
                // ALOAD s0 SPILLEDARG 0
                Global.outputString += Global.varALoad + reg + Global.varSpilled
                        + (i - 4) + "\n";
            }
        }
        // n.f0.accept(this, arg); // NU
        n.f1.accept(this, arg);
        // n.f2.accept(this, arg); // NU
        MyString ret = (MyString) (n.f3.accept(this, arg));
        if (ret == null) {
            System.err.println("ERROR:RETURN NULL!");
        }
        Global.outputString += Global.varMove + Global.v0 + ret + "\n";
        // n.f4.accept(this, arg); // NU
        // step 3.恢复所有的寄存器
        // ALOAD s3 SPILLEDARG 3
        offset = -1;
        offset += (this.nowProc.paras > 4 ? this.nowProc.paras - 4 : 0);
        offset += this.nowProc.paras > 4 ? 4 : this.nowProc.paras;
        // sizeForCycle = 0;
        // for(String reg : Global.aReg) {
        // if(++sizeForCycle > this.nowProc.paras) break;
        // Global.outputString += Global.varALoad + reg
        // + Global.varSpilled + (++offset) + "\n";
        // }
        for (String reg : Global.sReg) {
            Global.outputString += Global.varALoad + reg + Global.varSpilled
                    + (++offset) + "\n";
        }
        for (String reg : Global.tReg) {
            Global.outputString += Global.varALoad + reg + Global.varSpilled
                    + (++offset) + "\n";
        }
        return null;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     * f2 -> "("
     * f3 -> ( Temp() )*
     * f4 -> ")"
     */
    @Override
    public MyType visit(Call n, MyType arg) {
        // 注意在这里可能一个 Stmt 的 TEMP 个数超过 3 个
        // 但是不要怕, 在进入参数列表之前最多使用 2 个临时寄存器,也就是卓有一个空余
        // 因此参数列表都是用第 3 个寄存器,这样就要求不输出 MOVE 指令
        // TODO 多了已经分配了寄存器的 MOVE 指令
        // 传参 : 前4个参数,剩余的参数
        // 需要输出的语句 : 传参, CALL语句
        // n.f0.accept(this, arg); // NU
        MyString func = (MyString) (n.f1.accept(this, arg));
        // n.f2.accept(this, arg); // NU
        this.shouldAddParas = true;
        this.procParas.clear();
        n.f3.accept(this, new MyPara());
        int size = this.procParas.size();
        // 需要保存好参数寄存器中的值(注意需要在 MOVE 之前)
        int offset = -1;
        offset += (this.nowProc.paras > 4 ? this.nowProc.paras - 4 : 0);
        int sizeForCycle = 0;
        for (String reg : Global.aReg) {
            if (++sizeForCycle > this.nowProc.paras)
                break;
            Global.outputString += Global.varAStore + Global.varSpilled
                    + (++offset) + reg + "\n";
        }
        // 需要传参 (这里的顺序很重要,否则可能会把参数先修改掉)
        // 例子
        /**
         * MOVE a0 s5
         * MOVE a1 t6
         * MOVE a2 s6
         * MOVE a3 t7
         * PASSARG 1 a3
         * PASSARG 2 a2
         * PASSARG 3 a1
         */
        // TODO 本参数的参数作为调用函数的参数传递时需要特殊处理
        // 这里的处理方式是因为已经将参数保存到栈上,因此参数传递时使用栈上读取的方式
        /**
         * 需要特殊处理
         * MOVE a0 t1
         * MOVE a1 a0
         */
        // PASSARG IntegerLiteral Reg
        // PASSARG 从 1 开始
        for (int i = 4; i < size; ++i) {
            MyIsSpilled nowPara = this.procParas.get(i);
            String reg;
            if (nowPara.isSpilled) {
                // 前 4 个参数
                if (nowPara.name.indexOf("a") != -1) {
                    String name = nowPara.name.trim();
                    nowPara.offset = (name.charAt(1) - '0')
                            + (size > 4 ? size - 4 : 0);
                }
                // 不需要恢复
                reg = this.thirdTemporaryReg;
                Global.outputString += Global.varALoad + reg + Global.varSpilled
                        + nowPara.offset + "\n";
                // 如果溢出,注意这里都是使用 thirdTemporaryReg
            } else
                reg = nowPara.name;
            Global.outputString += Global.varPassArg + (i - 3) + reg + "\n";
        }
        // 前 4 个参数
        for (int i = 0; i < 4 && i < size; ++i) {
            MyIsSpilled nowPara = this.procParas.get(i);
            String reg;
            if (nowPara.isSpilled) {
                // 前 4 个参数
                if (nowPara.name.indexOf("a") != -1) {
                    String name = nowPara.name.trim();
                    nowPara.offset = (name.charAt(1) - '0')
                            + (size > 4 ? size - 4 : 0);
                }
                // 不需要恢复
                reg = this.thirdTemporaryReg;
                Global.outputString += Global.varALoad + reg + Global.varSpilled
                        + nowPara.offset + "\n";
                // 如果溢出,注意这里都是使用 thirdTemporaryReg
            } else
                reg = nowPara.name;
            Global.outputString += Global.varMove + Global.aReg[i] + reg + "\n";
        }
        this.shouldAddParas = false;
        // n.f4.accept(this, arg); // NU
        // 输出 CALL 语句
        if (func == null) {
            System.err.println("ERROR:CALL NULL!");
        }
        Global.outputString += Global.varCall + func + "\n";
        // 恢复参数寄存器中的值
        offset = -1;
        offset += (this.nowProc.paras > 4 ? this.nowProc.paras - 4 : 0);
        sizeForCycle = 0;
        for (String reg : Global.aReg) {
            if (++sizeForCycle > this.nowProc.paras)
                break;
            Global.outputString += Global.varALoad + reg + Global.varSpilled
                    + (++offset) + "\n";
        }
        return new MyString(Global.v0);
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    @Override
    public MyType visit(HAllocate n, MyType arg) {
        // n.f0.accept(this, arg); // NU
        MyString exp = (MyString) (n.f1.accept(this, arg));
        if (exp == null)
            return null;
        return new MyString(Global.varHallocate + exp.toString());
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    @Override
    public MyType visit(BinOp n, MyType arg) {
        // 测试可行
        String op = n.f0.f0.choice.toString();
        // n.f0.accept(this, arg);
        MyString temp = (MyString) (n.f1.accept(this, arg));
        MyString exp = (MyString) (n.f2.accept(this, arg));
        if (temp == null || exp == null)
            return null;
        return new MyString(" " + op + temp.toString() + exp.toString());
    }

    /* Operator */

    /**
     * f0 -> Temp()
     *         | IntegerLiteral()
     *         | Label()
     */
    @Override
    public MyType visit(SimpleExp n, MyType arg) {
        return n.f0.accept(this, arg);
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    @Override
    public MyType visit(Temp n, MyType arg) {
        // TODO
        // 如果是参数传递,需要记录当前的 TEMP
        // 如果是溢出的寄存器接下来需要保存回去
        String temp = n.f1.f0.tokenImage;
        int tempInt = Integer.parseInt(temp);
        boolean isSpilled = false;
        int offset = 0;
        // 参数 < 4
        if (tempInt < 4) {
            // 注意这里的操作,将参数的 isSpilled 设为 true
            isSpilled = true;
            temp = Global.aReg[tempInt];
        }
        // 分配到寄存器
        else if (this.nowProc.temp2reg.containsKey(temp)) {
            temp = this.nowProc.temp2reg.get(temp);
        }
        // 如果没有分配到
        else if (!this.nowProc.temp2spilled.containsKey(temp)) {
            temp = null;
        }
        // 溢出
        else {
            offset = this.nowProc.temp2spilled.get(temp);
            isSpilled = true;
            // 如果不是参数
            if (!(arg instanceof MyPara)) {
                // 需要恢复
                temp = this.getTemporaryReg();
                // ALOAD s0 SPILLEDARG 0
                Global.outputString += Global.varALoad + temp
                        + Global.varSpilled + offset + "\n";
                this.restoreSpilled.add(new MyPair(temp, offset));
            }
        }
        if (this.shouldAddParas) {
            this.procParas.add(new MyIsSpilled(temp, isSpilled, offset));
        }
        // n.f0.accept(this, arg); // NU
        // n.f1.accept(this, arg); // NU
        if (temp == null)
            return null;
        return new MyString(temp);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public MyType visit(IntegerLiteral n, MyType arg) {
        // 可能是 SimpleExp 下来的
        String simpleInt = n.f0.tokenImage;
        // n.f0.accept(this, arg); // NU
        return new MyString(simpleInt);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public MyType visit(Label n, MyType arg) {
        String stmt = n.f0.tokenImage;
        // 区分是函数符号还是跳转 Label
        if (!Global.procSet.contains(stmt)) {
            stmt = this.nowProc.labelTrans.get(stmt);
        }
        // n.f0.accept(this, arg); // NU
        // Stmt 中的
        // MDZZ
        if (arg instanceof MyNoOutput) {
            return new MyString(stmt);
        }
        // 此时只有可能是 Stmt 前面的 Label
        else {
            Global.outputString += stmt + " ";
            return null;
        }
    }
}
