class AD {
    public static void main(String[] args) {
        System.out.println(new C().fun());
    }
}

class A {
    int a1;
    int a2;
    int a3;
    int age;
    int a4;
    public int setAge() {
        age = 10;
        return age;
    }
    public int getAge() {
        return age;
    }
}

class B extends A {
    int a5;
    int a6;
    int age;
    int a7;
    int a8;
    public int getAge() {
        return age;
    }
}

class C {
    public int fun() {
        A b;
        int tmp;
        b = new B();
        tmp = b.setAge();
        return b.getAge();
    }
}