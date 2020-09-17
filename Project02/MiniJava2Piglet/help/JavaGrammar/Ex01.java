
class A {
    int a = 1;
    void f() {
        System.out.println(a);
    }
}

class B extends A {
    int a = 2;
    void f() {
        System.out.println(a);
    }
}

public class Ex01 {
    public static void main(String[] args) {
        A a = new B();
        System.out.println(a.a);
        a.f();
    }
}
/*
1
2
*/
