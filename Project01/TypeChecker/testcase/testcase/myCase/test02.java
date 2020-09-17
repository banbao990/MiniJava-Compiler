class AD {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class C {
    public D tt() {
        return new D();
    }
}

class D extends C {
    public int t() {
        C a;
        a = (new C().tt()).tt();
        return 0;
    }
}
