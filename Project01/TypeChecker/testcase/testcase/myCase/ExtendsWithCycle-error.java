// extends with cycle
class A {
    public static void main(String [] arg) {
        System.out.println(0);
    }
}

class B extends C {}
class C extends D {}
class D extends B {} // TE

class E extends E {} // TE
