class test001{
    public static void main(String[] a){
        System.out.println(new A().f(101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,new A().g(1,2,3,4,5,6),new A().g(7,8,9,10,11,12),new A().g(13,14,15,16,17,18)));
    }
}

class A{
    public int f(int a1,int a2,int a3,int a4,int a5,int a6,int a7,int a8,int a9,int a10,int a11,int a12,int a13,int a14,int a15,int a16,int a17,int a18,int a19,int a20){
        return a20;
    }
    
    public int g(int a1,int a2,int a3,int a4,int a5,int a6){
        return a1;
    }
}
/*
13
*/