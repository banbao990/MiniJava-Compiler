// assign error
class A{
    public static void main(String [] arg){
        System.out.println(0);
    }
}

class C extends B{}
class D extends C{}
class E extends A{}
class F extends B{}

// A <- B <- C <- D
//  \    \
//   \    <- F
//    <- E

class B extends A{
    public int test01(){
        A a;
        A b;
        A c;
        A d;
        A e;
        A f;
        a = new A();
        b = new B();
        c = new C();
        d = new D();
        e = new E();
        f = new F();
        return 1;
    }
    
    public int test02(){
        B a;
        B b;
        B c;
        B d;
        B e;
        B f;
        a = new A(); // TE
        b = new B();
        c = new C();
        d = new D();
        e = new E(); // TE
        f = new F();
        return 1;
    }
    
    public int test03(){
        C a;
        C b;
        C c;
        C d;
        C e;
        C f;
        a = new A(); // TE
        b = new B(); // TE
        c = new C();
        d = new D();
        e = new E(); // TE
        f = new F(); // TE
        return 1;
    }
    
    public int test04(){
        D a;
        D b;
        D c;
        D d;
        D e;
        D f;
        a = new A(); // TE
        b = new B(); // TE
        c = new C(); // TE
        d = new D();
        e = new E(); // TE
        f = new F(); // TE
        return 1;
    }
    
    public int test05(){
        E a;
        E b;
        E c;
        E d;
        E e;
        E f;
        a = new A(); // TE
        b = new B(); // TE
        c = new C(); // TE
        d = new D(); // TE
        e = new E();
        f = new F(); // TE
        return 1;
    }
    
    public int test06(){
        F a;
        F b;
        F c;
        F d;
        F e;
        F f;
        a = new A(); // TE
        b = new B(); // TE
        c = new C(); // TE
        d = new D(); // TE
        e = new E(); // TE
        f = new F();
        return 1;
    }
}