class Factorial_2{
    public static void main(String[] a){
        System.out.println((new Fac().ComputeFac(4)) + (new Fac().ComputeFac(4)));
    }
}

class Fac {
    public int ComputeFac(int num){
        int num_aux ;
        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux ;
    }
}
/*
48
*/