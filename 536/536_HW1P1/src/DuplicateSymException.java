@SuppressWarnings("serial")
public class DuplicateSymException extends Exception {
	public DuplicateSymException() {
//		this(null);
		super();
	}
	
	public DuplicateSymException(String ident) {
		super("SymTable already contains identifier" + (ident == null ? "" : " " + ident));
	}
}
