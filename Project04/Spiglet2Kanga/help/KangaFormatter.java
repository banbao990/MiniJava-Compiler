
import kanga.syntaxtree.*;
import kanga.visitor.*;

public class KangaFormatter extends TreeFormatter {

    protected void processList(NodeListInterface n) {
	processList(n, force());
    }

    public void visit(NodeToken n) {
	super.visit(n);
	    // add a space after each token
	add(space());
    }

    public void visit(Goal n) {
	n.nodeToken.accept(this);
	n.nodeToken1.accept(this);
	n.integerLiteral.accept(this);
	n.nodeToken2.accept(this);
	n.nodeToken3.accept(this);
	n.integerLiteral1.accept(this);
	n.nodeToken4.accept(this);
	n.nodeToken5.accept(this);
	n.integerLiteral2.accept(this);
	n.nodeToken6.accept(this);
	add(indent());
	add(force());
	n.stmtList.accept(this);
	add(outdent());
	add(force());
	n.nodeToken7.accept(this);
	n.nodeListOptional.accept(this);
	add(force());
	n.nodeToken8.accept(this);
    }

    public void visit(Procedure n) {
	add(force());
	add(force());
	n.label.accept(this);
	n.nodeToken.accept(this);
	n.integerLiteral.accept(this);
	n.nodeToken1.accept(this);
	n.nodeToken2.accept(this);
	n.integerLiteral1.accept(this);
	n.nodeToken3.accept(this);
	n.nodeToken4.accept(this);
	n.integerLiteral2.accept(this);
	n.nodeToken5.accept(this);
	add(indent());
	add(force());
	n.stmtList.accept(this);
	add(outdent());
	add(force());
	n.nodeToken6.accept(this);
    }

}