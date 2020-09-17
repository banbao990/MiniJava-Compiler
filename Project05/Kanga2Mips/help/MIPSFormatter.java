
import mips.syntaxtree.*;
import mips.visitor.*;

public class MIPSFormatter extends TreeFormatter {

    protected void processList(NodeListInterface n) {
	processList(n, force());
    }

    public void visit(NodeToken n) {
	super.visit(n);
	    // add a space after each token
	add(space());
    }
    
    public void visit(StmtList n) {
    	n.stmt.accept(this);
    	add(force());
    	n.nodeOptional.accept(this);
    }
    
    public void visit(SegmentDir n) {
    	add(force());
    	super.visit(n);
    }
    
}