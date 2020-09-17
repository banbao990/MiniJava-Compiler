package symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// 所有类的列表
public class MyClassList extends MyType {
    // 没有顺序要求
    private HashMap<String, MyClass> classList = new HashMap<>();

    // 构造函数
    public MyClassList() {
    }

    // contains
    public boolean containskeys(String className) {
        return this.classList.containsKey(className);
    }

    public ArrayList<String> getClassTypeList() {
        ArrayList<String> typeList = new ArrayList<>();
        Iterator<String> it = this.classList.keySet().iterator();
        while (it.hasNext()) {
            typeList.add(it.next().toString());
        }
        return typeList;
    }

    // insert
    public String insertClass(MyClass n) {
        String name = n.getName();
        if (this.classList.containsKey(n.getName())) {
            return ("Class " + name + " is already existed!");
        }
        this.classList.put(n.getName(), n);
        return null;
    }

    public MyClass getClassByName(String methodName) {
        MyClass returnClass = this.classList.get(methodName);
        return returnClass;
    }
}
