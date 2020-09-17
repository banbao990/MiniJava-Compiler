package symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProcedure extends MyType {
    /** 函数名称 */
    public String name;
    /** 调用其他过程的最大参数个数 */
    public int callParas;
    /** 参数个数 */
    public int paras;
    /** 栈单元个数的调用参数 */
    public int stackSize;

    /** 构造函数, 名称+参数个数*/
    public MyProcedure(String name, int nowParas) {
        this.name = name;
        this.callParas = 0;
        this.paras = nowParas;
        this.labelTrans = new HashMap<>();
        this.stmt = new ArrayList<>();
        this.name2temp = new HashMap<>();
        this.temps = new ArrayList<>();
        this.temp2reg = new HashMap<>();
        this.temp2spilled = new HashMap<>();
        this.offsetForSpilled = 0;
    }

    // 关于 Label
    /** 维护一个从旧的Label到新的Label的映射(因为Kanga的Label是全局的) */
    public HashMap<String, String> labelTrans;

    // 关于 Stmt
    /** Stmt 序列 */
    public ArrayList<MyStmt> stmt;

    public void addStmt(MyStmt stmt) {
        this.stmt.add(stmt);
    }

    // 关于活性区间
    /** TEMP 到活性区间的映射 */
    public HashMap<String, MyTemp> name2temp;
    /** MyTemp 数组 */
    public ArrayList<MyTemp> temps;
    /** 寄存器分配 */
    public HashMap<String, String> temp2reg;
    /** 溢出的变量对应溢出的单位(第一个溢出变量的偏移设为1) */
    public HashMap<String, Integer> temp2spilled;
    /** 溢出变量的偏移 */
    public int offsetForSpilled;
}
