import java.util.*;

enum VarSymType {
	NONFORMAL, // addDecl will turn this into either global or local
	GLOBAL,
	LOCAL,
	FORMAL,
	STRUCT, // Struct variable
	STRUCT_MEMBER, // Struct member variable
	NOT_VAR;
}

/**
 * The TSym class defines a symbol-table entry.
 * Each TSym contains a type (a Type).
 */
public class TSym {
    private Type type;
    private VarSymType varType;
    private int fpOffset;

    public TSym(Type type) {
        this.type = type;
        this.setVarType(VarSymType.NOT_VAR);
        this.setFpOffset(0);
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return type.toString();
    }

	public VarSymType getVarType() {
		return varType;
	}

	public void setVarType(VarSymType varType) {
		this.varType = varType;
	}

	public int getFpOffset() {
		return fpOffset;
	}

	public void setFpOffset(int fpOffset) {
		this.fpOffset = fpOffset;
	}
}

/**
 * The FnSym class is a subclass of the TSym class just for functions.
 * The returnType field holds the return type and there are fields to hold
 * information about the parameters.
 */
class FnSym extends TSym {
    // new fields
    private Type returnType;
    private int numParams;
    private List<Type> paramTypes;
    private int finalSpOffset;

    public FnSym(Type type, int numparams) {
        super(new FnType());
        returnType = type;
        numParams = numparams;
        setFinalSpOffset(0);
    }

    public void addFormals(List<Type> L) {
        paramTypes = L;
    }

    public Type getReturnType() {
        return returnType;
    }

    public int getNumParams() {
        return numParams;
    }

    public List<Type> getParamTypes() {
        return paramTypes;
    }

    public String toString() {
        // make list of formals
        String str = "";
        boolean notfirst = false;
        for (Type type : paramTypes) {
            if (notfirst)
                str += ",";
            else
                notfirst = true;
            str += type.toString();
        }

        str += "->" + returnType.toString();
        return str;
    }

	public int getFinalSpOffset() {
		return finalSpOffset;
	}

	public void setFinalSpOffset(int finalSpOffset) {
		this.finalSpOffset = finalSpOffset;
	}
}

/**
 * The StructSym class is a subclass of the TSym class just for variables
 * declared to be a struct type.
 * Each StructSym contains a symbol table to hold information about its
 * fields.
 */
class StructSym extends TSym {
    // new fields
    private IdNode structType;  // name of the struct type

    public StructSym(IdNode id) {
        super(new StructType(id));
        structType = id;
    }

    public IdNode getStructType() {
        return structType;
    }
}

/**
 * The StructDefSym class is a subclass of the TSym class just for the
 * definition of a struct type.
 * Each StructDefSym contains a symbol table to hold information about its
 * fields.
 */
class StructDefSym extends TSym {
    // new fields
    private SymTable symTab;

    public StructDefSym(SymTable table) {
        super(new StructDefType());
        symTab = table;
    }

    public SymTable getSymTable() {
        return symTab;
    }
}
