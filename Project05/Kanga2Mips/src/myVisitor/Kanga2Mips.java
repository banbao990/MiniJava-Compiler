package myVisitor;

import global.Global;
import syntaxtree.ALoadStmt;
import syntaxtree.AStoreStmt;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.CallStmt;
import syntaxtree.ErrorStmt;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.PassArgStmt;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.Reg;
import syntaxtree.SimpleExp;

public class Kanga2Mips extends visitor.GJNoArguDepthFirst<String> {
    // TODO 需要在 syscall 之前保存好 v0 和 a 类寄存器
    // 主要的一些操作都是仿造 BubbleSort 里的代码实现的(BS)

    // 自定义变量
    /** 当前函数名 */
    private String procedureName;
    /** 当前函数栈帧大小 */
    private int stackSize;
    /** 寄存器 */
    private final String[] reg = { "a0", "a1", "a2", "a3", "t0", "t1", "t2",
            "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5",
            "s6", "s7", "t8", "t9", "v0", "v1" };
    /** 运算操作符 */
    private final String[] op = { "slt", "add", "sub", "mul" };
    /** MOVE 语句中 Reg */
    private String moveReg = null;
    /** 保存寄存器 a0, v0 */
    private String save_a0 = "sw $a0, 4($sp)\n";
    private String save_v0 = "sw $v0, ($sp)\n";
    /** 恢复寄存器 a0, v0 */
    private String load_a0 = "lw $a0, 4($sp)\n";
    private String load_v0 = "lw $v0, ($sp)\n";
    /** 保存寄存器 */
    private final int saveRegNum = 2;
    // 自定义函数

    /** 判断是否需要保存 a0v0 */
    private void save_a0v0(String r1) {
        if (!"a0".equals(r1))
            Global.outputString += this.save_a0;
        if (!"v0".equals(r1))
            Global.outputString += this.save_v0;
    }

    /** 判断是否需要恢复 a0v0 */
    private void load_a0v0(String r1) {
        if (!"a0".equals(r1))
            Global.outputString += this.load_a0;
        if (!"v0".equals(r1))
            Global.outputString += this.load_v0;
    }

    /** 计算当前函数栈帧大小,
      * p1,p2,p3 即为 kanga 中的 3 个参数
     */
    private void calcStackSize(int p1, int p2, int p3) {
        // TODO
        // 当前栈帧包括如下内容
        // 返回地址
        // 上一个栈帧的 fp
        // 需要保存的寄存器(a0,v0)
        this.stackSize = (p2 + 2 + this.saveRegNum) * 4;
    }

    /** 添加一些整体的函数(syscall) */
    private void addHelperFunc() {
        Global.outputString += ".text\n" + ".globl _halloc\n" + "_halloc:\n"
                + "li $v0, 9\n" + "syscall\n" + "j $ra\n" + ".text\n"
                + ".globl _print\n" + "_print:\n" + "li $v0, 1\n" + "syscall\n"
                + "la $a0, newl\n" + "li $v0, 4\n" + "syscall\n" + "j $ra\n"
                + ".data\n" + ".align 0\n" + "newl:.asciiz \"\\n\"\n"
                + ".data\n" + ".align 0\n"
                + "str_er:.asciiz \"ERROR : abnormal termination\\n\"\n";
    }

    /** 添加函数头 */
    private void addProcedureHead() {
        Global.outputString += ".text\n" + ".globl " + this.procedureName + '\n'
                + this.procedureName + ":\n" + "sw $fp, -8($sp)\n" // 保存上一帧的 fp
                + "sw $ra, -4($sp)\n" // 保存返回地址
                + "move $fp, $sp\n" // 修改 fp 为当前栈底 (sp)
                + "subu $sp, $sp, " + this.stackSize + '\n'; // 开栈
    }

    /** 函数扫尾工作 */
    private void addProcedureTail() {
        Global.outputString += "addu $sp, $sp, " + this.stackSize + '\n' // 清栈
                + "lw $ra, -4($sp)\n" // 获取返回地址
                + "lw $fp, -8($sp)\n" // 恢复上一帧的 fp
                + "j $ra\n";
    }

    /* visit-accept */

    /* NodeList */
    /* NodeListOptional */
    /* NodeOptional */
    /* NodeSequence */
    /* NodeToken */

    //
    // User-generated visitor methods below
    //
    /**
     * f0 -> "MAIN"
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> "["
     * f5 -> IntegerLiteral()
     * f6 -> "]"
     * f7 -> "["
     * f8 -> IntegerLiteral()
     * f9 -> "]"
     * f10 -> StmtList()
     * f11 -> "END"
     * f12 -> ( Procedure() )*
     * f13 -> <EOF>
     */
    @Override
    public String visit(Goal n) {
        this.addHelperFunc();
        String _ret = null;
        this.procedureName = "main";
        this.calcStackSize(Integer.parseInt(n.f2.f0.tokenImage),
                Integer.parseInt(n.f5.f0.tokenImage),
                Integer.parseInt(n.f8.f0.tokenImage));
        this.addProcedureHead();
        n.f10.accept(this);
        this.addProcedureTail();
        n.f12.accept(this);
        return _ret;
    }

    /* StmtList */

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> "["
     * f5 -> IntegerLiteral()
     * f6 -> "]"
     * f7 -> "["
     * f8 -> IntegerLiteral()
     * f9 -> "]"
     * f10 -> StmtList()
     * f11 -> "END"
     */
    @Override
    public String visit(Procedure n) {
        String _ret = null;
        this.procedureName = n.f0.f0.tokenImage;
        this.calcStackSize(Integer.parseInt(n.f2.f0.tokenImage),
                Integer.parseInt(n.f5.f0.tokenImage),
                Integer.parseInt(n.f8.f0.tokenImage));
        this.addProcedureHead();
        n.f10.accept(this);
        this.addProcedureTail();
        return _ret;
    }

    /* Stmt */

    /**
     * f0 -> "NOOP"
     */
    @Override
    public String visit(NoOpStmt n) {
        String _ret = null;
        Global.outputString += "nop\n";
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    @Override
    public String visit(ErrorStmt n) {
        String _ret = null;
        // 不需要保存 v0,a0 反正也回不去了
        Global.outputString += "li $v0, 4\n" + "la $a0, str_er\n" + "syscall\n"
                + "li $v0, 10\n" // exit(BS)
                + "syscall\n";
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Reg()
     * f2 -> Label()
     */
    @Override
    public String visit(CJumpStmt n) {
        String _ret = null;
        Global.outputString += "beqz $" + this.reg[n.f1.f0.which] + ", "
                + n.f2.f0.tokenImage + '\n';
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    @Override
    public String visit(JumpStmt n) {
        String _ret = null;
        Global.outputString += "j " + n.f1.f0.tokenImage + '\n';
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Reg()
     * f2 -> IntegerLiteral()
     * f3 -> Reg()
     */
    @Override
    public String visit(HStoreStmt n) {
        String _ret = null;
        String r1 = this.reg[n.f1.f0.which];
        String offset = n.f2.f0.tokenImage;
        String r2 = this.reg[n.f3.f0.which];
        Global.outputString += "sw $" + r2 + ", " + offset + "($" + r1 + ")\n";
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Reg()
     * f2 -> Reg()
     * f3 -> IntegerLiteral()
     */
    @Override
    public String visit(HLoadStmt n) {
        String _ret = null;
        String r1 = this.reg[n.f1.f0.which];
        String r2 = this.reg[n.f2.f0.which];
        String offset = n.f3.f0.tokenImage;
        Global.outputString += "lw $" + r1 + ", " + offset + "($" + r2 + ")\n";
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Reg()
     * f2 -> Exp()
     */
    @Override
    public String visit(MoveStmt n) {
        String _ret = null;
        String r1 = this.reg[n.f1.f0.which];
        int which = n.f2.f0.which;
        if (which == 0) {
            /* 0 MOVE r1 HALLOCATE SimpleExp */
            this.save_a0v0(r1);
            n.f2.accept(this);
            Global.outputString += "move $" + r1 + ", $v0\n";
            this.load_a0v0(r1);
        } else {
            /* 1 MOVE r1 Operator Reg SimpleExp */
            /* 2 MOVE r1 SimpleExp */
            this.moveReg = r1;
            n.f2.accept(this);
            this.moveReg = null;
        }
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    @Override
    public String visit(PrintStmt n) {
        /*
        * PRINT t3
        *
        * move $a0, $t3
        * jal _print
        */
        String simpleExp = n.f1.accept(this);
        this.save_a0v0(null);
        Global.outputString += "move $a0, " + simpleExp + '\n' + "jal _print\n";
        this.load_a0v0(null);
        return null;
    }

    /**
     * f0 -> "ALOAD"
     * f1 -> Reg()
     * f2 -> SpilledArg()
     */
    @Override
    public String visit(ALoadStmt n) {
        String r1 = this.reg[n.f1.f0.which];
        int offset = Integer.parseInt(n.f2.f1.f0.tokenImage);
        // SPILLEDARG 起始0
        // +3 : 用于保存返回地址和上一栈帧的 fp
        Global.outputString += "lw $" + r1 + ", -" + ((offset + 3) * 4)
                + "($fp)\n";
        return null;
    }

    /**
     * f0 -> "ASTORE"
     * f1 -> SpilledArg()
     * f2 -> Reg()
     */
    @Override
    public String visit(AStoreStmt n) {
        String r1 = this.reg[n.f2.f0.which];
        int offset = Integer.parseInt(n.f1.f1.f0.tokenImage);
        Global.outputString += "sw $" + r1 + ", -" + ((offset + 3) * 4)
                + "($fp)\n";
        return null;
    }

    /**
     * f0 -> "PASSARG"
     * f1 -> IntegerLiteral()
     * f2 -> Reg()
     */
    @Override
    public String visit(PassArgStmt n) {
        // 直接将参数保存到下一个栈帧
        // PASSARG 起始1
        int offset = Integer.parseInt(n.f1.f0.tokenImage);
        String r1 = this.reg[n.f2.f0.which];
        Global.outputString += "sw $" + r1 + ", -" + ((offset + 2) * 4)
                + "($sp)\n";
        return null;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     */
    @Override
    public String visit(CallStmt n) {
        String simpleExp = n.f1.accept(this);
        // 判断一下是寄存器还是Label
        if (simpleExp.charAt(0) == '$')
            Global.outputString += "jalr " + simpleExp + '\n';
        else
            Global.outputString += "jal " + simpleExp + '\n';
        return null;
    }

    /* Exp */

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    @Override
    public String visit(HAllocate n) {
        String _ret = null;
        /*
        * li $a0, 16
        * jal _halloc
        */
        this.moveReg = "a0";
        n.f1.accept(this);
        Global.outputString += "jal _halloc\n";
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Reg()
     * f2 -> SimpleExp()
     */
    @Override
    public String visit(BinOp n) {
        /*
        * MOVE t2 MINUS t0 t1
        * sub $t2, $t0, $t1
        *
        * MOVE t1 t0 1
        * sub $t1, $t0, 1
        */
        String _ret = null;
        String op = this.op[n.f0.f0.which];
        String r1 = this.moveReg;
        this.moveReg = null; // 用完即删
        String r2 = this.reg[n.f1.f0.which];
        String simpleExp = n.f2.accept(this); // 返回若为 Reg,已经自带 '$'
        Global.outputString += op + " $" + r1 + ", $" + r2 + ", " + simpleExp
                + '\n';
        return _ret;
    }

    /* Operator */
    /* SpilledArg */

    /**
     * f0 -> Reg()
     *       | IntegerLiteral()
     *       | Label()
     */
    @Override
    public String visit(SimpleExp n) {
        int which = n.f0.which;
        String t;
        if (which == 0)
            t = '$' + this.reg[((Reg) (n.f0.choice)).f0.which];
        else if (which == 1)
            t = ((IntegerLiteral) (n.f0.choice)).f0.tokenImage;
        else
            t = ((Label) (n.f0.choice)).f0.tokenImage;
        if (this.moveReg != null) {
            /*
             * 2
             * MOVE t2 BBS_Init
             * la $t2, BBS_Init
             *
             * 1
             * MOVE t2 4
             * li $t2, 4
             *
             * 0
             * MOVE t0 t1
             * move $t0, $t1
            */
            if (which == 0)
                Global.outputString += "move $" + this.moveReg + ", " + t
                        + '\n';
            else if (which == 1)
                Global.outputString += "li $" + this.moveReg + ", " + t + '\n';
            else
                Global.outputString += "la $" + this.moveReg + ", " + t + '\n';
            this.moveReg = null; // 用完即删
        }
        return t;
    }

    /* Reg */
    /* IntegerLiteral */

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public String visit(Label n) {
        // 注意进入这里的只会有开头的标识 Label
        // 屏蔽 Procedure/CJUMP/JUMP/SimpleExp
        String _ret = null;
        Global.outputString += n.f0.tokenImage + ":";
        return _ret;
    }

}
