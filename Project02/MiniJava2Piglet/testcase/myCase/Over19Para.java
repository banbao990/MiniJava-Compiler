class Over19Para {
    public static void main(String[] a) {
        System.out.println(new Over19paraEx().work());
    }
}

class Over19paraEx {
    public int overTest(int a0,int a1,int a2,int a3,int a4,int a5,int a6,int a7,int a8,int a9,int a10,int a11,int a12,int a13,int a14,int a15,int a16,int a17,int a18,int a19) {

        int x;
        int y;
        int z;

        x = a19;
        y = x;
        z = y;

        System.out.println(z);
        return x;
    }

    public int work() {
        int[] a;
        int i;
        i = 0;
        a = new int[100];
        while(i < 20) {
            a[i] = i;
            i = i + 1;
        }
        i = this.overTest(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19]);
        return 0;
    }
}
/*
19
0
*/