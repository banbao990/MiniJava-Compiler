// overload
// print(boolean)
// return type not match

class Factorial {
    public static void main(String[] a) {
        System.out.println(true); // TE
    }
}

class A {
    public boolean a(int a, int b) {
        return true;
    }
    public boolean b(int a, int b) {
        return true;
    }
}

class B extends A {
    // overload(ERROR)
    // TE
    public boolean a(int a, int b, int c) {
        return true;
    }

    // override(OK)
    public boolean b(int a, int b) {
        return true;
    }

    // overload(ERROR)
    // TE
    public int a(int a, int b) {
        return true; // TE
    }
}