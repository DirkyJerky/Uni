
public class P1 {

	public static void main(String[] args) {
		test(testScopes());
		test(testToString());
		test(testLookups());
		test(testDup());
		test(testSyms());
	}
	
	/**
	 * Tests will return null if they succeed, and a String describing the error if they don't.
	 * @param arg The potentially null string as a result from calling a test
	 */
	private static void test(String arg) {
		if (arg != null) {
			System.out.println(arg);
		}
	}

	
	
	/**
	 * Test that scopes are properly added, and accessory exceptions are thrown
	 */
	private static String testScopes() {
		SymTable table = new SymTable();
		try {
			table.removeScope();
		} catch (EmptySymTableException e) {
			return "testScopes: start table remove should not have errored";
		}
		try {
			table.removeScope();
			return "testScopes: empty table remove should have errored";
		} catch (EmptySymTableException e) {}
		
		table.addScope();
		table.addScope();
		try {
			table.removeScope();
			table.removeScope();
		} catch (EmptySymTableException e) {
			return "testScopes: remove after add should have succeeded";
		}
		
		try {
			table.removeScope();
			return "testScopes: empty table remove after adds+removes should have errored";
		} catch (EmptySymTableException e) {}
		
		return null;
	}
	
	/**
	 * Test that `toString()` (and `print()`) output is correct
	 */
	private static String testToString() {
		SymTable table = new SymTable();
		
		try {
			table.addScope();
			table.addDecl("a", new Sym("a"));
			table.addScope();
			table.addDecl("b", new Sym("b"));
		} catch (DuplicateSymException | EmptySymTableException e) {
			return "testToString: errored on addDecl to empty table";
		}
		
		String expected = "\n" + 
				"Sym Table\n" + 
				"{b=b}\n" + 
				"{a=a}\n" +
				"{}\n" +
				"\n";
		
		if (! expected.contentEquals(table.toString())) {
			return "testToString: unexpected toString, expected:" + expected + "but found: " + table.toString();
		}
		
		table.print();
		
		return null;
	}
	
	/**
	 * Test local and global lookups
	 */
	private static String testLookups() {
		SymTable table = new SymTable();
		Sym a = new Sym("a");
		Sym b = new Sym("b");
		Sym c = new Sym("c");
		Sym d = new Sym("d");
		
		try {
			table.addScope();
			table.addDecl("a", a);
			table.addDecl("b", b);
			table.addScope();
			table.addDecl("c", c);
			table.addDecl("d", d);
		} catch (DuplicateSymException e) {
			return "testLookups: errored on addDecl to empty table";
		} catch (EmptySymTableException e) {
			return "testLookups: errored on addDecl to empty table";
		}
		
		try {
			if (table.lookupLocal("c") == null) {
				return "testLookups: lookup local should have succeeded";
			}
			if (table.lookupLocal("d") == null) {
				return "testLookups: lookup local should have succeeded";
			}
			if (table.lookupLocal("a") != null) {
				return "testLookups: lookup local should have failed";
			}
			if (table.lookupLocal("b") != null) {
				return "testLookups: lookup local should have failed";
			}
			if (table.lookupLocal("e") != null) {
				return "testLookups: lookup local should have failed";
			}

			if (table.lookupGlobal("a") == null) {
				return "testLookups: lookup global should have succeeded";
			}
			if (table.lookupGlobal("b") == null) {
				return "testLookups: lookup global should have succeeded";
			}
			if (table.lookupGlobal("c") == null) {
				return "testLookups: lookup global should have succeeded";
			}
			if (table.lookupGlobal("d") == null) {
				return "testLookups: lookup global should have succeeded";
			}
			if (table.lookupGlobal("e") != null) {
				return "testLookups: lookup global should have failed";
			}
			
		} catch (EmptySymTableException e) {
			return "testLookups: empty table";
		}
		
		return null;
	}
	
	/**
	 * Test duplicate symbol exception
	 */
	private static String testDup() {
		SymTable table = new SymTable();
		
		try {
			table.addScope();
			table.addDecl("a", new Sym("a"));
			table.addDecl("b", new Sym("b"));
			table.addScope();
			table.addDecl("c", new Sym("c"));
			table.addDecl("a", new Sym("a2"));
		} catch (DuplicateSymException e) {
			return "testDup: errored on addDecl to empty table";
		} catch (EmptySymTableException e) {
			return "testDup: errored on addDecl to empty table";
		}
		
		try {
			table.addDecl("c", new Sym("c"));
			return "testDup: duplicate addDecl should have errored";
		} catch (DuplicateSymException e) {
			// expected
		} catch (EmptySymTableException e) {
			return "testDup: empty table";
		}
		
		return null;
	}
	
	/**
	 * Test `Sym` class functionality
	 */
	private static String testSyms() {
		Sym a = new Sym("a");
		Sym n = new Sym(null);
		
		if (a.getType() != "a") {
			return "testSyms: symbol type wrong";
		} 
		
		if (a.toString() != "a") {
			return "testSyms: symbol toString wrong";
		} 
		
		
		if (n.getType() != null) {
			return "testSyms: null symbol type wrong";
		} 
		
		if (n.toString() != null) {
			return "testSyms: null symbol toString wrong";
		}  
		
		return null;
	}
}
