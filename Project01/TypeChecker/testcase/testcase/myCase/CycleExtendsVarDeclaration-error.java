// extends chain has cycle
// variable not defined

class AD {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class C extends D {
    int a; // OK
    public int tt() {
        return this.t(); // OK
    }
}

class D extends C { // TE
    public int t() {
        return a;
    }
}

class A extends B {
    public int foo() {
        return a; // TE, not defined
    }
}

class B extends A {
    public int t() {
        return this.foo(); // OK
    }
}