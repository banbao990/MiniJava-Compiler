package myVisitor;

import java.util.HashMap;

import global.Global;
import symbol.MyProcedure;
import symbol.MyStmt;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Exp;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.Node;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.Operator;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.DepthFirstVisitor;

public class GenerateLiveMap extends DepthFirstVisitor {
    // 全局变量
    /** 当前的 Procedure */
    MyProcedure nowProc;
    /** 当前的 Stmt */
    MyStmt nowStmt;
    /** 当前的 Stmt 是 Procedure 中第几个 Stmt */
    int nowStmtIndex;
    /** 当前的 Proc 一共有几个 Stmt */
    int nowProcTotalStmt;
    /** Label 和 Stmt 的 index 的映射 */
    HashMap<String, Integer> label2Index = new HashMap<>();

    // 自定义函数
    /** 检查 Label 是否已经存在一个映射 */
    void checkLabel(String tLabel) {
        if (this.nowProc.labelTrans.containsKey(tLabel))
            return;
        String tNewLabel = Global.getLabel();
        this.nowProc.labelTrans.put(tLabel, tNewLabel);
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
    public void visit(Goal n) {
        // 构造 Procedure 并加入 Global, 清空 label2Index
        this.nowProc = new MyProcedure("MAIN", 0);
        Global.proc.add(this.nowProc);
        this.label2Index.clear();
        // 因为第一个是 0
        this.nowStmtIndex = -1;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        // 添加一个退出结点
        this.nowProc.addStmt(new MyStmt());
        n.f3.accept(this);
        n.f4.accept(this);
        // DEBUG TODO
        // ArrayList<MyProcedure> p = Global.proc;
        // int a = 0;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    @Override
    public void visit(StmtList n) {
        // 处理 Label 的映射 + 处理 Label 的 Stmt 序号
        int index = -1;
        // 直接获取 StmtList 的 size
        // 需要加上一个 RETURN(而且RETURN在exit之前)
        // 注意 MAIN 是没有 RETURN 的
        if (!(this.nowProc.name.equals("MAIN")))
            this.nowProcTotalStmt = n.f0.nodes.size() + 1;
        for (Node t1 : n.f0.nodes) {
            ++index;
            if (!(t1 instanceof NodeSequence))
                continue;
            Node t2 = ((NodeSequence) t1).nodes.get(0);
            if (!(t2 instanceof NodeOptional))
                continue;
            Node t3 = ((NodeOptional) t2).node;
            if (!(t3 instanceof Label))
                continue;
            String oldLabel = ((Label) t3).f0.tokenImage;
            checkLabel(oldLabel);
            this.label2Index.put(oldLabel, index);
        }
        n.f0.accept(this);
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    @Override
    public void visit(Procedure n) {
        // 获取到 Label
        String nowLabel = n.f0.f0.tokenImage;
        int nowArgs = Integer.parseInt(n.f2.f0.tokenImage);
        // 构造 Procedure 并加入 Global, 清空 label2Index
        this.nowProc = new MyProcedure(nowLabel, nowArgs);
        Global.proc.add(nowProc);
        this.label2Index.clear();
        // 因为第一个是 0
        this.nowStmtIndex = -1;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        // 添加一个退出结点
        this.nowProc.addStmt(new MyStmt());
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
    public void visit(Stmt n) {
        // 构造 Stmt 并加入 Procedure
        // 构造 Stmt(id/vars)
        this.nowStmt = new MyStmt();
        this.nowProc.addStmt(nowStmt);
        // 调整当前 Stmt 的 index
        ++this.nowStmtIndex;
        n.f0.accept(this);
    }

    /**
     * f0 -> "NOOP"
     */
    @Override
    public void visit(NoOpStmt n) {
        // 后继为下一个语句
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        n.f0.accept(this);
    }

    /**
     * f0 -> "ERROR"
     */
    @Override
    public void visit(ErrorStmt n) {
        // 后继为 exit
        this.nowStmt.addNext(this.nowProcTotalStmt);
        n.f0.accept(this);
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    @Override
    public void visit(CJumpStmt n) {
        // 后继有两个:Label所在的行数+下一行
        String tlabel = n.f2.f0.tokenImage;
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        this.nowStmt.addNext(this.label2Index.get(tlabel));
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    @Override
    public void visit(JumpStmt n) {
        // 后继有两个:Label所在的行数+下一行
        String tlabel = n.f1.f0.tokenImage;
        // this.nowStmt.addNext(this.nowStmtIndex + 1);
        this.nowStmt.addNext(this.label2Index.get(tlabel));
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    @Override
    public void visit(HStoreStmt n) {
        // 后继为下一个语句
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Temp()
     * f3 -> IntegerLiteral()
     */
    @Override
    public void visit(HLoadStmt n) {
        String tempName = n.f1.f1.f0.tokenImage;
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        this.nowStmt.addId(tempName);
        n.f0.accept(this);
        // n.f1.accept(this); // 屏蔽
        n.f2.accept(this);
        n.f3.accept(this);
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    @Override
    public void visit(MoveStmt n) {
        // 后继为下一个语句
        String tempName = n.f1.f1.f0.tokenImage;
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        this.nowStmt.addId(tempName);
        n.f0.accept(this);
        // 屏蔽
        // n.f1.accept(this);
        // this.nowProc.name2Temp.put(tempName, new MyTemp(tempName));
        n.f2.accept(this);
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    @Override
    public void visit(PrintStmt n) {
        // 后继为下一个语句
        this.nowStmt.addNext(this.nowStmtIndex + 1);
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> Call()
     *         | HAllocate()
     *         | BinOp()
     *         | SimpleExp()
     */
    @Override
    public void visit(Exp n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> SimpleExp()
     * f4 -> "END"
     */
    @Override
    public void visit(StmtExp n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        // 注意这里也需要一个 Stmt
        this.nowStmt = new MyStmt();
        this.nowProc.addStmt(nowStmt);
        // 调整当前 Stmt 的 index
        ++this.nowStmtIndex;
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     * f2 -> "("
     * f3 -> ( Temp() )*
     * f4 -> ")"
     */
    @Override
    public void visit(Call n) {
        int tempMaxCallParas = n.f3.size();
        if (this.nowProc.callParas < tempMaxCallParas) {
            this.nowProc.callParas = tempMaxCallParas;
        }
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    @Override
    public void visit(HAllocate n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    @Override
    public void visit(BinOp n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> "LT"
     *         | "PLUS"
     *         | "MINUS"
     *         | "TIMES"
     */
    @Override
    public void visit(Operator n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> Temp()
     *         | IntegerLiteral()
     *         | Label()
     */
    @Override
    public void visit(SimpleExp n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    @Override
    public void visit(Temp n) {
        String v = n.f1.f0.tokenImage;
        // 前 4 个参数不加入
        if (Integer.parseInt(v) >= 4)
            this.nowStmt.addVariables(v);
        // this.nowProc.name2Temp.put(v, new MyTemp(v));
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /* IntegerLiteral */

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public void visit(Label n) {
        n.f0.accept(this);
    }
}
