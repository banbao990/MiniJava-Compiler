class Main {
	public static void main(String[] a){
		System.out.println(new A().run());
	}
}

class A {
	public int run() {
		int[] a;
		a = new int[20];
		System.out.println(a[10]);
		return a[40];
	}
}
/*
12
15
27
*/