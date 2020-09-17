class Over19Paras2 {
    public static void main(String[] s) {
        A a1;
        A a2;
        int[] a;
        a = new int[20];
        a1 = new B();
        a2 = new C();
        System.out.println(a1.run(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23, a));
        System.out.println(a2.run(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23, a));
    }
}

class A {
    int c;

    public int run(int a0, int a1, int a2, int a3, int a4, int a5, int a6,
            int a7, int a8, int a9, int a10, int a11, int a12, int a13, int a14,
            int a15, int a16, int a17, int a18, int a19, int a20, int a21,
            int a22, int a23, int[] a) {
        // a23=1;
        // a[10]=10;
        boolean x;
        x = this.f();
        c = 10;
        System.out.println(c);
        // System.out.println(a[10]);
        return a[10];
    }

    public boolean f() {
        c = 10;
        return true;
    }
}

class B extends A {
    int c;

    public boolean f() {
        c = 20;
        // System.out.println(c);
        return true;
    }
}

class C extends B {
    public int run(int a0, int a1, int a2, int a3, int a4, int a5, int a6,
            int a7, int a8, int a9, int a10, int a11, int a12, int a13, int a14,
            int a15, int a16, int a17, int a18, int a19, int a20, int a21,
            int a22, int a23, int[] a) {
        boolean x;
        x = this.f();
        System.out.println(c);
        return 1000000;
    }
}
/*
10
0
20
1000000
*/