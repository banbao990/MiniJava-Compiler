class test12_9 {
    public static void main(String[] a){
	System.out.println(new Test().start());
    }
}

class Test{

    int[] i;

    public int start(){

	i = new int[10];

	i[9] = 80;

	return 40;
    }
}
/*
40
*/