package symbol;

import java.util.HashSet;
import java.util.TreeSet;

public class MyStmt extends MyType {
    /**
     * 约束(可以通过如下方式一致解决)
     * Constraints(v) = join(v) - id + vars
     */
    public TreeSet<String> Constraints;
    /** id(Set处理起来比较方便) */
    public HashSet<String> id;
    /** 变量列表(right)  */
    public HashSet<String> vars;
    /** 后继列表 */
    public HashSet<Integer> next;

    /** 构造函数(witch = 0) */
    public MyStmt() {
        this.Constraints = new TreeSet<>();
        this.id = new HashSet<>();
        this.vars = new HashSet<>();
        this.next = new HashSet<>();
    }

    /** 添加变量 */
    public void addVariables(String v) {
        this.vars.add(v);
    }

    /** 添加后继 */
    public void addNext(int n) {
        this.next.add(n);
    }

    /** 添加 id */
    public void addId(String id) {
        this.id.add(id);
    }
}
