package symbol;

public class MyType {

    // TEMP
    public String name;

    // whether TEMP
    public boolean isTemp = false;

    // whether SimpleExp
    public boolean isSimple = false;

    // constructor
    public MyType() {
    }

    // constructor
    public MyType(String name) {
        this.name = name;
    }

    // constructor
    public MyType(String name, boolean isTemp) {
        this.name = name;
        this.isTemp = isTemp;
        // isTemp => isSimple
        if (isTemp) {
            this.isSimple = true;
        }
    }

    // constructor
    public MyType(String name, boolean isTemp, boolean isSimple) {
        this.name = name;
        this.isTemp = isTemp;
        this.isSimple = isSimple;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
