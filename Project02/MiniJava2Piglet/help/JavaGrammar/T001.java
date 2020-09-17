class Father {
    public int age = 50;
}

class Son extends Father {
    public int age = 25;
}

public class T001 {

    public static void AP(Object a) {
        System.out.println(a);
    }

    public static void main(String[] args) {
        Son s = new Son();
        AP(s.age);
        Father f = (Father) s;
        AP(f.age);
    }
}
/*
25
50
*/