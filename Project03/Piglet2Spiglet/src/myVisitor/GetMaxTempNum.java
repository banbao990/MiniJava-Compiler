package myVisitor;

import global.Global;
import syntaxtree.Temp;
import visitor.DepthFirstVisitor;

// no arg, no return value
public class GetMaxTempNum extends DepthFirstVisitor {

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    @Override
    public void visit(Temp n) {
        n.f0.accept(this);
        n.f1.accept(this);
        int tempNum = Integer.parseInt(n.f1.f0.tokenImage);
        if (tempNum > Global.tempNum)
            Global.tempNum = tempNum;
    }
}
