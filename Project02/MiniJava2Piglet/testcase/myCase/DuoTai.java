class DuoTai {
    public static void main(String[] arg) {
        A t;
        int a;
        t = new A();
        a = t.f();
        t = new B();
        a = t.f();
        t = new A();
        a = t.f();
    }
}

class A {
    int a;
    public int f() {
        a = 1;
        System.out.println(a);
        return 0;
    }
}

class B extends A {
    int a;
    public int f() {
        a = 2;
        System.out.println(a);
        return 0;
    }
}
/*
1
2
1
*/