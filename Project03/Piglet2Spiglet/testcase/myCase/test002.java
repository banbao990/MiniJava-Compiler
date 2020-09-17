class test002{
    public static void main(String[] a){
        int i;
        Animal p;
        i = 3;
        if(i < 4)
            p = new Cat();
        else
            p = new Animal();
        System.out.println(p.f());
    }
}

class Animal{
    public int f(){
        return 1;
    }
}

class Cat extends Animal{
    public int f(){
        return 0;
    }
}
/*
0
*/