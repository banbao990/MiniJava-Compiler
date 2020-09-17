package global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import symbol.MyProcedure;
import symbol.MyStmt;
import symbol.MyTemp;

public class Global {
    // 一些常量
    public static final String varMain = " MAIN ";
    public static final String varEnd = "\nEND\n";
    public static final String varNoop = " NOOP\n";
    public static final String varError = " ERROR\n";
    public static final String varCjump = " CJUMP ";
    public static final String varJump = " JUMP ";
    public static final String varHstore = " HSTORE ";
    public static final String varHload = " HLOAD ";
    public static final String varMove = " MOVE ";
    public static final String varPrint = " PRINT ";
    public static final String varALoad = " ALOAD ";
    public static final String varAStore = " ASTORE ";
    public static final String varPassArg = " PASSARG ";
    public static final String varCall = " CALL ";
    public static final String varHallocate = " HALLOCATE ";
    public static final String varLt = " LT ";
    public static final String varPlus = " PLUS ";
    public static final String varMinus = " MINUS ";
    public static final String varTimes = " TIMES ";
    public static final String varTemp = " TEMP ";
    public static final String varSpilled = " SPILLEDARG ";
    public static final String[] aReg = { " a0 ", " a1 ", " a2 ", " a3 " };
    public static final String[] vReg = { " v0 ", " v1 " };
    public static final String v0 = " v0 ";
    /** t0 - t8 */
    public static final String[] tReg = { " t0 ", " t1 ", " t2 ", " t3 ",
            " t4 ", " t5 ", " t6 ", " t7 ", " t8 " };
    // "t9" 作为临时寄存器
    /** s0 - s7 */
    public static final String[] sReg = { " s0 ", " s1 ", " s2 ", " s3 ",
            " s4 ", " s5 ", " s6 ", " s7 " };
    /** 所有的可以分配的寄存器个数 */
    public static final int totalReg = 17;

    // 关于 Label
    /** 当前可以分配的最小Label */
    public static int MaxLabel = 0;

    public static String getLabel() {
        return " L" + (MaxLabel++) + ' ';
    }

    // 关于 Procedure
    /** 保存所有 Procedure 的数组 */
    public static ArrayList<MyProcedure> proc = new ArrayList<>();
    /** 所有的函数名称的 Set */
    public static HashSet<String> procSet = new HashSet<>();

    // 关于代码输出
    /** 输出的 Kanga 代码 */
    public static String outputString = "";

    /** 将输出规范化 */
    public static void normOfOutputString() {
        // TODO
        // 去除单行的 NOOP
        // 一次去不干净
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        Global.outputString = Global.outputString.replaceAll("  ", " ");
        String[] seg = Global.outputString.split("\n");
        String _ret = "";
        String blank = "    ";
        for (String x : seg) {
            String temp = x.trim();
            // 检查是否为 MOVE a a
            String[] infos = temp.split(" ");
            if (infos.length == 3) {
                if ("MOVE".equals(infos[0]) && infos[1].equals(infos[2]))
                    continue;
            }
            if (temp.equals("") || temp.equals("NOOP"))
                continue;
            if (temp.indexOf('[') == -1) {
                if (!temp.startsWith("END") && !temp.startsWith("L")) {
                    temp = blank + temp;
                }
            } else {
                temp = '\n' + temp;
            }
            _ret += temp + "\n";
        }
        Global.outputString = _ret;
    }

    /** 在构造好了用于规则推导的初始约束变量之后要做的事 */
    public static void deal() {
        // TODO
        Global.livenessAnalysis();
        Global.constructLivenessInterval();
        Global.regDistribution();
        Global.calcStackSize();
        Global.calcProcLabel();
        Global.calcOffsetOfSpilled();
    }

    /** 重新计算溢出寄存器的 offset */
    public static void calcOffsetOfSpilled() {
        for (MyProcedure p : Global.proc) {
            for (String name : p.temp2spilled.keySet()) {
                int nameInt = Integer.parseInt(name);
                int offset = p.temp2spilled.get(name);
                // 是参数的话直接使用原始的偏移
                if (nameInt < 20) {
                    // nameInt >= 4
                    offset = nameInt - 4;
                }
                // 不是参数的话需要修改
                else {
                    // paras > 4
                    int paras = p.paras <= 4 ? 4 : p.paras;
                    offset = (paras - 4) + offset - 1 + Global.totalReg;
                    // 需要加上保存的参数值
                    offset += p.paras > 4 ? 4 : p.paras;
                    // 加上预留的传参栈
                    offset += (p.paras > 4 ? p.paras - 4 : 0);
                }
                p.temp2spilled.put(name, offset);
            }
        }
    }

    /** 生成一个关于所有的函数名称的 HashSet */
    public static void calcProcLabel() {
        for (MyProcedure p : Global.proc)
            Global.procSet.add(p.name);
    }

    /** 计算每一个函数的栈大小 */
    public static void calcStackSize() {
        for (MyProcedure p : Global.proc) {
            // procA 需要的栈单元个数 : 参数(如果需要>4) + 溢出(spilled)单元 + 需要保存的寄存器
            int paras = p.paras;
            // 需要保存参数
            // 因为后面没有为参数再重新分配寄存器,可能修改了参数的值导致全局寄存器值变化
            // paras = paras > 4 ? paras - 4 : 0;
            p.stackSize = paras + p.offsetForSpilled + Global.totalReg;
        }
    }

    /** 为每个变量构建活性区间,没有考虑变量复用 */
    public static void constructLivenessInterval() {
        // TODO 还有个问题,关于参数怎么处理
        for (MyProcedure p : Global.proc) {
            HashMap<String, MyTemp> liveInterval = p.name2temp;
            int size = p.stmt.size();
            liveInterval.clear();
            for (int i = 0; i < size; ++i) {
                MyStmt nowStmt = p.stmt.get(i);
                for (String tempName : nowStmt.Constraints) {
                    if (liveInterval.containsKey(tempName)) {
                        liveInterval.get(tempName).end = i;
                    } else {
                        MyTemp newTemp = new MyTemp(tempName);
                        newTemp.start = i;
                        // 这里不加会出现 end < start
                        newTemp.end = i;
                        liveInterval.put(tempName, newTemp);
                    }
                }
            }
            // 构造 p.temps(活性区间数组)
            for (String key : liveInterval.keySet()) {
                p.temps.add(liveInterval.get(key));
            }
            // 排序(线性扫描的规则:先出现的排在前面,先结束的排在前面)
            p.temps.sort((a, b) -> {
                if (a.start != b.start)
                    return a.start - b.start;
                else
                    return a.end - b.end;
            });
        }
    }

    /** 活性分析,规则推导 */
    public static void livenessAnalysis() {
        // TODO 需要对多余的参数进行寄存器分配
        // 对每一个 Procedure 进行规则推导
        for (MyProcedure p : Global.proc) {
            // 多余的参数进行寄存器分配
            // TODO 是否需要添加一条语句,或者说直接把声明语句和第一句加在一起是否会出错
            if (p.paras > 4) {
                MyStmt firstStmt = p.stmt.get(0);
                for (int i = 4; i < p.paras; ++i)
                    firstStmt.id.add("" + i);
            }
            int size = p.stmt.size();
            // 感觉 clear 比 new 快
            TreeSet<String> join = new TreeSet<String>();
            while (true) {
                boolean stable = true;
                for (int i = size - 1; i >= 0; --i) {
                    MyStmt nowStmt = p.stmt.get(i);
                    join.clear();
                    // join(v) = U Constraints(w:w是v的后继)
                    // 逐个加入 Constraint
                    for (int x : nowStmt.next) {
                        if (x >= p.stmt.size()) {
                            System.err.println("INDEX OUT!");
                        }
                        join.addAll(p.stmt.get(x).Constraints);
                    }
                    // newConstraint = join - id + vars
                    join.removeAll(nowStmt.id);
                    join.addAll(nowStmt.vars);
                    if (!join.equals(nowStmt.Constraints)) {
                        stable = false;
                        nowStmt.Constraints.clear();
                        nowStmt.Constraints.addAll(join);
                    }
                }
                if (stable)
                    break;
            }
        }
    }

    /**
     * 寄存器分配(已经排序好了)
     * 这里主要的思想是将所有的 s/t 寄存器用于分配
     * 也就是说,所有的寄存器都是 callee-saved
     * 好处是能够分配的寄存器其多了,坏处是可能不需要在栈上保存这么多寄存器
     */
    public static void regDistribution() {
        // 所有的待分配的寄存器
        HashSet<String> allFreeReg = new HashSet<>();
        for (String reg : Global.tReg)
            allFreeReg.add(reg);
        for (String reg : Global.sReg)
            allFreeReg.add(reg);
        // 接下来用于分配的寄存器(之所以写外面是为了避免 new)
        HashSet<String> freeReg = new HashSet<>();
        // p.temps 的一个拷贝,用于判断寄存器的回收,(之所以写外面是为了避免 new)
        ArrayList<MyTemp> cloneTemps = new ArrayList<>();
        for (MyProcedure p : Global.proc) {
            // 准备工作,注意 clear
            freeReg.clear();
            freeReg.addAll(allFreeReg);
            cloneTemps.clear();
            cloneTemps.addAll(p.temps);
            // 按照结束时间排序
            cloneTemps.sort((a, b) -> {
                return a.end - b.end;
            });
            int recycle = 0;
            int size = cloneTemps.size();
            // 开始分配寄存器
            for (MyTemp nowTemp : p.temps) {
                int nowStmtIndex = nowTemp.start;
                // 每前进一步需要检查此时是否有寄存器可以变为重新分配
                // step 0 : 回收寄存器
                while (recycle < size) {
                    // 回收
                    if (cloneTemps.get(recycle).end < nowStmtIndex) {
                        String tempName = cloneTemps.get(recycle).name;
                        String recycleReg = p.temp2reg.get(tempName);
                        // 如果本身就溢出了,不回收
                        if (recycleReg != null)
                            freeReg.add(recycleReg);
                        ++recycle;
                    } else
                        break;
                }
                // step 1 : 已经分配过了
                if (p.temp2reg.containsKey(nowTemp.name))
                    continue;
                // step 2 : 试图找到能够分配的寄存器分配
                // 满了就溢出
                // 这里为了方便实现,当寄存器不够的时候直接溢出,而不是溢出最晚的
                if (freeReg.isEmpty()) {
                    p.temp2spilled.put(nowTemp.name, ++p.offsetForSpilled);
                }
                // 未满就分配寄存器
                else {
                    // 这里也没有随机的意味,只需要取出一个即可
                    String reg = freeReg.iterator().next();
                    freeReg.remove(reg);
                    p.temp2reg.put(nowTemp.name, reg);
                }
            }
        }
    }
}
