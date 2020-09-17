class test12_length_minus_1 {
    public static void main(String[] a){
	System.out.println(new Test().start());
    }
}

class Test{

    int[] i;

    public int start(){

	i = new int[10];

	i[(i.length)-1] = 80;

	return 40;
    }
}
/*
40
*/