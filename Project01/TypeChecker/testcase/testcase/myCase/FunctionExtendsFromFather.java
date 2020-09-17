class AD {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class C{
    int a;
}

class D extends C{
    public int t(){
        return a;
    }
}

class A {
    int age;
    public boolean setAge(){
        age = 10;
        return true;
    }
    public int getAge(){
        return age;
    }
    public int foo() {
        return 0;
    }
}

class B extends A{
    int age;
    public int foo() {
        boolean flag;
        flag = this.setAge();
        return this.getAge();
    }
}
