
class A {
    int a = 1;
    void changeA(int a) {
        this.a = a;
    }
}

public class Ex02 {
    public static void main(String[] args) {
        A t1 = new A();
        A t2 = t1;
        System.out.println("t1:" + t1.a + ",t2:" + t2.a);
        t1.changeA(99);
        System.out.println("t1:" + t1.a + ",t2:" + t2.a);
        int a = 1;
        int b = a;
        System.out.println("a:" + a + ",b:" + b);
        a = 2;
        System.out.println("a:" + a + ",b:" + b);
    }
}
/*
t1:1,t2:1
t1:99,t2:99
a:1,b:1
a:2,b:1
*/
