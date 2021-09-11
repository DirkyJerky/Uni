/**
 * Context class that keeps track of necessary state for type checking.
 * Holds:
 * - A symbol table of global struct and fn defnitions
 * - A tracker for when we are in a function body.
 */
public class TypeContext {
	private FnSym inFunctionDecl;
	
	public TypeContext() {
		this.setFnSym(null);
	}

	public FnSym getFnSym() {
		return inFunctionDecl;
	}

	public void setFnSym(FnSym fnSym) {
		this.inFunctionDecl = fnSym;
	}
}
