class test12_10 {
    public static void main(String[] a){
	System.out.println(new Test().start());
    }
}

class Test{

    int[] i;

    public int start(){

	i = new int[10];

	i[10] = 80;

	return 40;
    }
}
/*
ArrayIndexOutOfBoundsException
*/