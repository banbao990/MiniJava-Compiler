class test12_11 {
    public static void main(String[] a){
	System.out.println(new Test().start());
    }
}

class Test{

    int[] i;

    public int start(){

	i = new int[10];

	i[11] = 80;

	return 40;
    }
}
/*
ArrayIndexOutOfBoundsException
*/