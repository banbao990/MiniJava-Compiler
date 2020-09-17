class TestForSeq {
    int a, b;
    TestForSeq() {
        this.a = 1;
        this.b = 1;
    }

    public int changeAValue() {
        this.a = 2;
        return this.a;
    }

    public void print(int aa, int bb) {
        System.out.println(aa);
        System.out.println(bb);
    }

    public int print2(int z) {
        System.out.println(z);
        return z;
    }

    public void noop(int aa, int bb) {
        return;
    }
}

public class Seq {

    public static void AP(Object a) {
        System.out.println(a);
    }

    public static void main(String[] args) {
        TestForSeq s = new TestForSeq();
        s.print(s.changeAValue(), s.a); // do as seq tells
        s.noop(s.print2(1), s.print2(2));
        System.out.println("hhhhh");
    }
}
/*
2
2
1
2
hhhhh
*/