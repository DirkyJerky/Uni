import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymTable {
	List<HashMap<String, Sym>> syms;
	
	public SymTable() {
		this.syms = new ArrayList<>();
	}
	
	public void addDecl(String name, Sym sym) throws DuplicateSymException, EmptySymTableException {
		if (this.syms.isEmpty()) {
			throw new EmptySymTableException();
		}
		
		if (name == null || sym == null) {
			throw new IllegalArgumentException("Null argument");
		}
		
		HashMap<String, Sym> map = this.syms.get(0);
		
		if (map.containsKey(name)) {
			throw new DuplicateSymException();
		} else {
			map.put(name, sym);
		}
	}
	
	public void addScope() {
		this.syms.add(0, new HashMap<>());
	}
	
	public Sym lookupLocal(String name) throws EmptySymTableException {
		if (this.syms.isEmpty()) {
			throw new EmptySymTableException();
		} else {
			return this.syms.get(0).get(name);
		}
	}
	
	public Sym lookupGlobal(String name) throws EmptySymTableException {
		if (this.syms.isEmpty()) {
			throw new EmptySymTableException();
		} else {
			for (HashMap<String, Sym> map : this.syms) {
				if (map.containsKey(name)) {
					return map.get(name);
				}
			}
			return null;
		}
	}
	
	public void removeScope() throws EmptySymTableException {
		if (this.syms.isEmpty()) {
			throw new EmptySymTableException();
		} else {
			this.syms.remove(0);
		}
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("\nSym Table\n");
		for (HashMap<String, Sym> map : this.syms) {
			builder.append(map);
			builder.append('\n');
		}
		builder.append('\n');
		
		return builder.toString();
	}
}
