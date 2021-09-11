import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a C-- program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// %%%ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode {
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
    
    public String unparseString() {
    	StringWriter stringWriter = new StringWriter();
    	PrintWriter writer = new PrintWriter(stringWriter);
    	this.unparse(writer, 0);
    	return stringWriter.toString();
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    /**
     * nameAnalysis
     * Creates an empty symbol table for the outermost scope, then processes
     * all of the globals, struct defintions, and functions in the program.
     */
    public void nameAnalysis() {
        SymTable symTab = new SymTable();
        myDeclList.nameAnalysis(symTab);
        
        TSym mainSym = null;
        try {
        	mainSym = symTab.lookupGlobal("main");
        } catch (EmptySymTableException e) {}
        
        if (mainSym == null || (!(mainSym instanceof FnSym))) {
        	ErrMsg.fatal(0, 0, "No main function");
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck() {
        myDeclList.typeCheck();
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;

	public void codeGen() {
		myDeclList.codeGen();
	}
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void codeGen() {
        for (DeclNode node : myDecls) {
            node.codeGen();
        }
	}

	/**
     * nameAnalysis
     * Given a symbol table symTab, process all of the decls in the list.
     */
    public void nameAnalysis(SymTable symTab) {	
        nameAnalysis(symTab, symTab);
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab and a global symbol table globalTab
     * (for processing struct names in variable decls), process all of the
     * decls in the list.
     */
    public void nameAnalysis(SymTable symTab, SymTable globalTab) {
        for (DeclNode node : myDecls) {
            if (node instanceof VarDeclNode) {
                (	(VarDeclNode)node).nameAnalysis(symTab, globalTab);
            } else {
                node.nameAnalysis(symTab);
            }
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck() {
        for (DeclNode node : myDecls) {
            node.typeCheck();
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * for each formal decl in the list
     *     process the formal decl
     *     if there was no error, add type of formal decl to list
     */
    public List<Type> nameAnalysis(SymTable symTab) {
        List<Type> typeList = new LinkedList<Type>();
        for (FormalDeclNode node : myFormals) {
            TSym sym = node.nameAnalysis(symTab);
            if (sym != null) {
                typeList.add(sym.getType());
            }
        }
        return typeList;
    }

    /**
     * Return the number of formals in this list.
     */
    public int length() {
        return myFormals.size();
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the declaration list
     * - process the statement list
     */
    public void nameAnalysis(SymTable symTab) {
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myStmtList.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    
	public void codeGen() {
		myDeclList.codeGen(); // Shouldn't output anything
		myStmtList.codeGen();
	}
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void codeGen() {
        for (StmtNode node : myStmts) {
            node.codeGen();
        }
	}

	/**
     * nameAnalysis
     * Given a symbol table symTab, process each statement in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for (StmtNode node : myStmts) {
            node.nameAnalysis(symTab);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        for(StmtNode node : myStmts) {
            node.typeCheck(retType);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public int size() {
        return myExps.size();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each exp in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for (ExpNode node : myExps) {
            node.nameAnalysis(symTab);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(List<Type> typeList) {
        int k = 0;
        try {
            for (ExpNode node : myExps) {
                Type actualType = node.typeCheck();     // actual type of arg

                if (!actualType.isErrorType()) {        // if this is not an error
                    Type formalType = typeList.get(k);  // get the formal type
                    if (!formalType.equals(actualType)) {
                        ErrMsg.fatal(node.lineNum(), node.charNum(),
                                     "Type of actual does not match type of formal");
                    }
                }
                k++;
            }
        } catch (NoSuchElementException e) {
            System.err.println("unexpected NoSuchElementException in ExpListNode.typeCheck");
            System.exit(-1);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;

	public void codeGen() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		for (int i = myExps.size() - 1; i >= 0; i -= 1) {
			Codegen.generateWithComment("", "begin push #" + i);
			myExps.get(i).codeGenRhs();
			Codegen.generateWithComment("", "end push #" + i);
		}
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    /**
     * Note: a formal decl needs to return a sym
     */
    abstract public TSym nameAnalysis(SymTable symTab);

    public abstract void codeGen();

	// default version of typeCheck for non-function decls
    public void typeCheck() { }
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    /**
     * nameAnalysis (overloaded)
     * Given a symbol table symTab, do:
     * if this name is declared void, then error
     * else if the declaration is of a struct type,
     *     lookup type name (globally)
     *     if type name doesn't exist, then error
     * if no errors so far,
     *     if name has already been declared in this scope, then error
     *     else add name to local symbol table
     *
     * symTab is local symbol table (say, for struct field decls)
     * globalTab is global symbol table (for struct type names)
     * symTab and globalTab can be the same
     */
    public TSym nameAnalysis(SymTable symTab) {
        return nameAnalysis(symTab, symTab);
    }

    public TSym nameAnalysis(SymTable symTab, SymTable globalTab) {
        boolean badDecl = false;
        String name = myId.name();
        TSym sym = null;
        IdNode structId = null;

        if (myType instanceof VoidNode) {  // check for void type
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Non-function declared void");
            badDecl = true;
        }

        else if (myType instanceof StructNode) {
            structId = ((StructNode)myType).idNode();

            try {
                sym = globalTab.lookupGlobal(structId.name());
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in VarDeclNode.nameAnalysis");
            }

            // if the name for the struct type is not found,
            // or is not a struct type
            if (sym == null || !(sym instanceof StructDefSym)) {
                ErrMsg.fatal(structId.lineNum(), structId.charNum(),
                             "Invalid name of struct type");
                badDecl = true;
            }
            else {
                structId.link(sym);
            }
        }

        TSym symCheckMul = null;

        try {
            symCheckMul = symTab.lookupLocal(name);
        } catch (EmptySymTableException ex) {
                            System.err.println("Unexpected EmptySymTableException " +
                                   " in VarDeclNode.nameAnalysis");
        }

        if (symCheckMul != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Multiply declared identifier");
            badDecl = true;
        }

        if (!badDecl) {  // insert into symbol table
            try {
                if (myType instanceof StructNode) {
                    sym = new StructSym(structId);
                    sym.setVarType(VarSymType.STRUCT);
                }
                else {
                    sym = new TSym(myType.type());
                    sym.setVarType(VarSymType.NONFORMAL);
                }
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (IllegalArgumentException ex) {
                System.err.println("Unexpected IllegalArgumentException " +
                                   " in VarDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }

        return sym;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
        VarSymType varType = this.myId.sym().getVarType();
        if (varType != VarSymType.NOT_VAR) {
            p.print('[');
            if (varType == VarSymType.LOCAL || varType == VarSymType.FORMAL) {
            	p.print("o=");
            	p.print(this.myId.sym().getFpOffset());
            } else {
            	p.print(this.myId.sym().getVarType());
            }
            p.print(']');
        }
        p.println(";");
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;

	@Override
	public void codeGen() {
		VarSymType varType = this.myId.sym().getVarType();
		switch (varType) {
			case GLOBAL: {
				Codegen.generate(".data");
				Codegen.generate(".align 4");
				Codegen.generateLabeled("_" + myId.name(), ".space 4", this.unparseString());
				break;
			}
			case LOCAL: {
				// Fn definitions will take care of making space
				break;
			}
			case STRUCT: {
				ErrMsg.warn(myId.lineNum(), myId.charNum(), "Struct variables unsupported");
				break;
			}
		}
	}
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public static String fnExitLabel = null;
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name has already been declared in this scope, then error
     * else add name to local symbol table
     * in any case, do the following:
     *     enter new scope
     *     process the formals
     *     if this function is not multiply declared,
     *         update symbol table entry with types of formals
     *     process the body of the function
     *     exit scope
     */
    public TSym nameAnalysis(SymTable symTab) {
        String name = myId.name();
        FnSym sym = null;
        TSym symCheckMul = null;

        try {
            symCheckMul = symTab.lookupLocal(name);
        } catch (EmptySymTableException ex) {
                            System.err.println("Unexpected EmptySymTableException " +
                                   " in FnDeclNode.nameAnalysis");
        }

        if (symCheckMul != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Multiply declared identifier");
        }

        else { // add function name to local symbol table
            try {
                sym = new FnSym(myType.type(), myFormalsList.length());
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in FnDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in FnDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (IllegalArgumentException ex) {
                System.err.println("Unexpected IllegalArgumentException " +
                                   " in FnDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }

        symTab.addScope();  // add a new scope for locals and params
        symTab.resetOffsets();

        // process the formals
        List<Type> typeList = myFormalsList.nameAnalysis(symTab);
        if (sym != null) {
            sym.addFormals(typeList);
        }

        myBody.nameAnalysis(symTab); // process the function body
        
        sym.setFinalSpOffset(symTab.getNextFpOffset());

        try {
            symTab.removeScope();  // exit scope
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in FnDeclNode.nameAnalysis");
            System.exit(-1);
        }

        return null;
    }

    /**
     * typeCheck
     */
    public void typeCheck() {
        myBody.typeCheck(myType.type());
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.print(")[sp=fp");
        p.print(((FnSym)this.myId.sym()).getFinalSpOffset());
        p.println("]{");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
    
	@Override
	public void codeGen() {
		Codegen.generateWithComment("", "Begin Fn");
		
		Codegen.generateWithComment("", "  Begin Preamble");
		Codegen.generate(".text");
		Codegen.genLabel(myId.name());
		Codegen.generateWithComment("", "  End Preamble");
		
		Codegen.generateWithComment("", "  Begin Prologue");
		Codegen.genPush(Codegen.RA);
		Codegen.genPush(Codegen.FP);
		Codegen.generateWithComment("subu", "locals space", Codegen.SP, Codegen.SP, String.valueOf(Math.abs(((FnSym)myId.sym()).getFinalSpOffset()) - 8));
		Codegen.generateWithComment("addu", "update fp", Codegen.FP, Codegen.SP, String.valueOf(Math.abs(((FnSym)myId.sym()).getFinalSpOffset())));
		Codegen.generateWithComment("", "  End Prologue");
		
		Codegen.generateWithComment("", "  Begin Body");
		fnExitLabel = myId.name() + "_exit";
		myBody.codeGen();
		fnExitLabel = null;
		Codegen.generateWithComment("", "  End Body");
		
		Codegen.generateWithComment("", "  Begin Epilogue");
		Codegen.genLabel(myId.name() + "_exit");
		Codegen.generateIndexed("lw", Codegen.RA, Codegen.FP, 0);
		Codegen.generate("move", Codegen.T0, Codegen.FP);
		Codegen.generateIndexed("lw", Codegen.FP, Codegen.FP, -4);
		Codegen.generate("move", Codegen.SP, Codegen.T0);
		if (myId.name().equalsIgnoreCase("main")) {
			Codegen.generate("li", Codegen.V0, 10);
			Codegen.generate("syscall");
		} else {
			Codegen.generate("jr", Codegen.RA);
		}
		Codegen.generateWithComment("", "  End Epilogue");
		
		Codegen.generateWithComment("", "End Fn");
	}
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this formal is declared void, then error
     * else if this formal is already in the local symble table,
     *     then issue multiply declared error message and return null
     * else add a new entry to the symbol table and return that TSym
     */
    public TSym nameAnalysis(SymTable symTab) {
        String name = myId.name();
        boolean badDecl = false;
        TSym sym = null;

        if (myType instanceof VoidNode) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Non-function declared void");
            badDecl = true;
        }

        TSym symCheckMul = null;

        try {
            symCheckMul = symTab.lookupLocal(name);
        } catch (EmptySymTableException ex) {
                            System.err.println("Unexpected EmptySymTableException " +
                                   " in FormalDeclNode.nameAnalysis");
        }

        if (symCheckMul != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Multiply declared identifier");
            badDecl = true;
        }

        if (!badDecl) {  // insert into symbol table
            try {
                sym = new TSym(myType.type());
                sym.setVarType(VarSymType.FORMAL);
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in FormalDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in FormalDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (IllegalArgumentException ex) {
                System.err.println("Unexpected IllegalArgumentException " +
                                   " in FormalDeclNode.nameAnalysis");
                System.exit(-1);
            }
	      }

        return sym;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.name());
        VarSymType varType = this.myId.sym().getVarType();
        if (varType != VarSymType.NOT_VAR) {
            p.print('[');
            if (varType == VarSymType.LOCAL || varType == VarSymType.FORMAL) {
            	p.print("o=");
            	p.print(this.myId.sym().getFpOffset());
            } else {
            	p.print(this.myId.sym().getVarType());
            }
            p.print(']');
        }
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
    
	@Override
	public void codeGen() {
		// Noop, nameAnalysis took care of everything
	}
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name is already in the symbol table,
     *     then multiply declared error (don't add to symbol table)
     * create a new symbol table for this struct definition
     * process the decl list
     * if no errors
     *     add a new entry to symbol table for this struct
     */
    public TSym nameAnalysis(SymTable symTab) {
        String name = myId.name();
        boolean badDecl = false;

        TSym symCheckMul = null;

        try {
            symCheckMul = symTab.lookupLocal(name);
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                           " in StructDeclNode.nameAnalysis");
        }

        if (symCheckMul != null) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Multiply declared identifier");
            badDecl = true;
        }


        if (!badDecl) {
            try {   // add entry to symbol table
                SymTable structSymTab = new SymTable();
                structSymTab.enterStructDefn();
                myDeclList.nameAnalysis(structSymTab, symTab);
                structSymTab.exitStructDefn();
                StructDefSym sym = new StructDefSym(structSymTab);
                symTab.addDecl(name, sym);
                myId.link(sym);
            } catch (DuplicateSymException ex) {
                System.err.println("Unexpected DuplicateSymException " +
                                   " in StructDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                   " in StructDeclNode.nameAnalysis");
                System.exit(-1);
            } catch (IllegalArgumentException ex) {
                System.err.println("Unexpected IllegalArgumentException " +
                                   " in StructDeclNode.nameAnalysis");
                System.exit(-1);
            }
        }

        return null;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        p.print(myId.name());
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
    private DeclListNode myDeclList;
    
	@Override
	public void codeGen() {
		ErrMsg.warn(myId.lineNum(), myId.charNum(), "Struct defns unsupported");
	}
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    /* all subclasses must provide a type method */
    abstract public Type type();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    /**
     * type
     */
    public Type type() {
        return new IntType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    /**
     * type
     */
    public Type type() {
        return new BoolType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    /**
     * type
     */
    public Type type() {
        return new VoidType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public IdNode idNode() {
        return myId;
    }

    /**
     * type
     */
    public Type type() {
        return new StructType(myId);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        p.print(myId.name());
    }

    // 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    abstract public void nameAnalysis(SymTable symTab);
    public abstract void codeGen();
	abstract public void typeCheck(Type retType);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myAssign.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myAssign.typeCheck();
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    // 1 kid
    private AssignNode myAssign;

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", this.unparseString());
		myAssign.codeGenRhs();
        Codegen.generate("addu", Codegen.SP, Codegen.SP, 4);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    // 1 kid
    private ExpNode myExp;

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", this.unparseString());
		myExp.codeGenLhs();
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0); // Unincremeted value
		Codegen.genPop(Codegen.T1); // Destination
		Codegen.generate("addi", Codegen.T0, Codegen.T0, 1);
		Codegen.generateIndexed("sw", Codegen.T0, Codegen.T1, 0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // 1 kid
    private ExpNode myExp;

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", this.unparseString());
		myExp.codeGenLhs();
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0); // Unincremeted value
		Codegen.genPop(Codegen.T1); // Destination
		Codegen.generate("addi", Codegen.T0, Codegen.T0, -1);
		Codegen.generateIndexed("sw", Codegen.T0, Codegen.T1, 0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class ReadStmtNode extends StmtNode {
	Type type;
    public ReadStmtNode(ExpNode e) {
        myExp = e;
        type = null;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        type = myExp.typeCheck();

        if (type.isFnType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a function");
        }

        if (type.isStructDefType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a struct name");
        }

        if (type.isStructType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to read a struct variable");
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;

	@Override
	public void codeGen() {
		if (type == null || (!type.isIntType() && !type.isBoolType())) {
			ErrMsg.fatal(myExp.lineNum(), myExp.charNum(), "Unsupported Read type (" + type + ")");
		}
		Codegen.generateWithComment("", this.unparseString());
		Codegen.generate("li", Codegen.V0, 5);
		Codegen.generate("syscall");
		myExp.codeGenLhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generateIndexed("sw", Codegen.V0, Codegen.T0, 0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class WriteStmtNode extends StmtNode {
	Type type;
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
        type = null;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        type = myExp.typeCheck();

        if (type.isFnType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a function");
        }

        if (type.isStructDefType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a struct name");
        }

        if (type.isStructType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write a struct variable");
        }

        if (type.isVoidType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Attempt to write void");
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp;

	@Override
	public void codeGen() {
		if (type == null || (!type.isIntType() && !type.isBoolType() && !type.isStringType())) {
			ErrMsg.fatal(myExp.lineNum(), myExp.charNum(), "Unsupported Write type (" + type + ")");
		}
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.A0);
		if (type.isIntType() || type.isBoolType()) {
			Codegen.generate("li", Codegen.V0, 1);
		} else if (type.isStringType()) {
			Codegen.generate("li", Codegen.V0, 4);
		}
		Codegen.generate("syscall");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfStmtNode.nameAnalysis");
            System.exit(-1);
        }
    }

     /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as an if condition");
        }

        myStmtList.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", "if (" + myExp.unparseString() + ") {");
		myExp.codeGenRhs();
		String exitLabel = Codegen.nextLabel() + "_exit";
		Codegen.genPop(Codegen.T0);
		Codegen.generate("beq", Codegen.T0, "$zero", exitLabel);
		myDeclList.codeGen();
		myStmtList.codeGen();
		Codegen.genLabel(exitLabel);
		Codegen.generateWithComment("", "} end if");
	}
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts of then
     * - exit the scope
     * - enter a new scope
     * - process the decls and stmts of else
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myThenDeclList.nameAnalysis(symTab);
        myThenStmtList.nameAnalysis(symTab);
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfElseStmtNode.nameAnalysis");
            System.exit(-1);
        }
        symTab.addScope();
        myElseDeclList.nameAnalysis(symTab);
        myElseStmtList.nameAnalysis(symTab);
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IfElseStmtNode.nameAnalysis");
            System.exit(-1);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as an if condition");
        }

        myThenStmtList.typeCheck(retType);
        myElseStmtList.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
        addIndentation(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;

	@Override
	public void codeGen() {
		String elseLabel = Codegen.nextLabel() + "_else";
		String exitLabel = Codegen.nextLabel() + "_exit";
		Codegen.generateWithComment("", "if (" + myExp.unparseString() + ") {");
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("beq", Codegen.T0, "$zero", elseLabel);
		myThenDeclList.codeGen();
		myThenStmtList.codeGen();
		Codegen.generate("j", exitLabel);
		Codegen.generateWithComment("", "} else {");
		Codegen.genLabel(elseLabel);
		myElseDeclList.codeGen();
		myElseStmtList.codeGen();
		Codegen.genLabel(exitLabel);
		Codegen.generateWithComment("", "} end ifelse");
	}
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in WhileStmtNode.nameAnalysis");
            System.exit(-1);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-bool expression used as a while condition");
        }

        myStmtList.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

	@Override
	public void codeGen() {
		String startLabel = Codegen.nextLabel() + "_start";
		String exitLabel = Codegen.nextLabel() + "_exit";
		Codegen.generateWithComment("", "while (" + myExp.unparseString() + ") {");
		Codegen.genLabel(startLabel);
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("beq", Codegen.T0, "$zero", exitLabel);
		myDeclList.codeGen();
		myStmtList.codeGen();
		Codegen.generate("j", startLabel);
		Codegen.genLabel(exitLabel);
		Codegen.generateWithComment("", "} end while");
	}
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
        try {
            symTab.removeScope();
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in RepeatStmtNode.nameAnalysis");
            System.exit(-1);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        Type type = myExp.typeCheck();

        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                         "Non-integer expression used as a repeat clause");
        }

        myStmtList.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

	@Override
	public void codeGen() {
		ErrMsg.fatal(myExp.lineNum(), myExp.charNum(), "Repeat statement unsupported");
	}
}


class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myCall.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        myCall.typeCheck();
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // 1 kid
    private CallExpNode myCall;

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myCall.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child,
     * if it has one
     */
    public void nameAnalysis(SymTable symTab) {
        if (myExp != null) {
            myExp.nameAnalysis(symTab);
        }
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type retType) {
        if (myExp != null) {  // return value given
            Type type = myExp.typeCheck();

            if (retType.isVoidType()) {
                ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                             "Return with a value in a void function");
            }

            else if (!retType.isErrorType() && !type.isErrorType() && !retType.equals(type)){
                ErrMsg.fatal(myExp.lineNum(), myExp.charNum(),
                             "Bad return value");
            }
        }

        else {  // no return value given -- ok if this is a void function
            if (!retType.isVoidType()) {
                ErrMsg.fatal(0, 0, "Missing return value");
            }
        }

    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp; // possibly null

	@Override
	public void codeGen() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		if (myExp != null) {
			if (FnDeclNode.fnExitLabel == null) {
				ErrMsg.fatal(myExp.lineNum(), myExp.charNum(), "Return node without fn return label");
			}
			myExp.codeGenRhs();
			Codegen.genPop(Codegen.V0);
			Codegen.generate("la", Codegen.T0, FnDeclNode.fnExitLabel);
			Codegen.generate("jr", Codegen.T0);
		} else {
			if (FnDeclNode.fnExitLabel == null) {
				ErrMsg.fatal(0, 0, "Return node without fn return label");
			}
			Codegen.generate("la", Codegen.T0, FnDeclNode.fnExitLabel);
			Codegen.generate("jr", Codegen.T0);
		}
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    /**
     * Default version for nodes with no names
     */
    public void nameAnalysis(SymTable symTab) { }

    public abstract void codeGenRhs();

	public abstract void codeGenLhs();

	abstract public Type typeCheck();
    abstract public int lineNum();
    abstract public int charNum();
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }

    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new IntType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;

	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		Codegen.generate("li", Codegen.T0, myIntVal);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "IntLitNode as LHS");
	}
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }

    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new StringType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		Codegen.generate(".data");
		String strLabel = Codegen.nextLabel();
		Codegen.generateLabeled(strLabel, ".asciiz " + myStrVal, "");
		Codegen.generate(".text");
		Codegen.generate("la", Codegen.T0, strLabel);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "StringLitNode as LHS");
	}
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }

    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new BoolType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		Codegen.generate("li", Codegen.T0, Codegen.TRUE);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "TrueNode as LHS");
	}
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    /**
     * Return the line number for this literal.
     */
    public int lineNum() {
        return myLineNum;
    }

    /**
     * Return the char number for this literal.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return new BoolType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		Codegen.generate("li", Codegen.T0, Codegen.FALSE);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "FalseNode as LHS");
	}
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    /**
     * Link the given symbol to this ID.
     */
    public void link(TSym sym) {
        mySym = sym;
    }

    /**
     * Return the name of this ID.
     */
    public String name() {
        return myStrVal;
    }

    /**
     * Return the symbol associated with this ID.
     */
    public TSym sym() {
        return mySym;
    }

    /**
     * Return the line number for this ID.
     */
    public int lineNum() {
        return myLineNum;
    }

    /**
     * Return the char number for this ID.
     */
    public int charNum() {
        return myCharNum;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - check for use of undeclared name
     * - if ok, link to symbol table entry
     */
    public void nameAnalysis(SymTable symTab) {
        TSym sym = null;

        try {
          sym = symTab.lookupGlobal(myStrVal);
        } catch (EmptySymTableException ex) {
            System.err.println("Unexpected EmptySymTableException " +
                               " in IdNode.nameAnalysis");
            System.exit(-1);
        }

        if (sym == null) {
            ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
        } else {
            link(sym);
        }
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        if (mySym != null) {
            return mySym.getType();
        }
        else {
            System.err.println("ID with null sym field in IdNode.typeCheck");
            System.exit(-1);
        }
        return null;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if (mySym != null) {
            p.print("(" + mySym + ")");
        }
        VarSymType varType = this.sym().getVarType();
        if (varType != VarSymType.NOT_VAR) {
            p.print('[');
            if (varType == VarSymType.LOCAL || varType == VarSymType.FORMAL) {
            	p.print("o=");
            	p.print(this.sym().getFpOffset());
            } else {
            	p.print(this.sym().getVarType());
            }
            p.print(']');
        }
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private TSym mySym;
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString() + " (rhs)");
		if (mySym.getVarType() == VarSymType.LOCAL || mySym.getVarType() == VarSymType.FORMAL) {
			Codegen.generateIndexed("lw", Codegen.T0, Codegen.FP, mySym.getFpOffset());
			Codegen.genPush(Codegen.T0);
		} else if (mySym.getVarType() == VarSymType.GLOBAL) {
			Codegen.generate("lw", Codegen.T0, "_" + myStrVal);
			Codegen.genPush(Codegen.T0);
		} else {
			ErrMsg.fatal(myLineNum, myCharNum, "Unsuppored var type in IDNode RHS: " + mySym.getVarType());
		}
		Codegen.generateWithComment("", "end " + this.unparseString() + " (rhs)");
	}

	@Override
	public void codeGenLhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString() + " (lhs)");
		if (mySym.getVarType() == VarSymType.LOCAL || mySym.getVarType() == VarSymType.FORMAL) {
			Codegen.generateIndexed("la", Codegen.T0, Codegen.FP, mySym.getFpOffset());
			Codegen.genPush(Codegen.T0);
		} else if (mySym.getVarType() == VarSymType.GLOBAL) {
			Codegen.generate("la", Codegen.T0, "_" + myStrVal);
			Codegen.genPush(Codegen.T0);
		} else {
			ErrMsg.fatal(myLineNum, myCharNum, "Unsuppored var type in IDNode LHS: " + mySym.getVarType());
		}
		Codegen.generateWithComment("", "end " + this.unparseString() + " (lhs)");
	}
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        myId = id;
        mySym = null;
    }

    /**
     * Return the symbol associated with this dot-access node.
     */
    public TSym sym() {
        return mySym;
    }

    /**
     * Return the line number for this dot-access node.
     * The line number is the one corresponding to the RHS of the dot-access.
     */
    public int lineNum() {
        return myId.lineNum();
    }

    /**
     * Return the char number for this dot-access node.
     * The char number is the one corresponding to the RHS of the dot-access.
     */
    public int charNum() {
        return myId.charNum();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the LHS of the dot-access
     * - process the RHS of the dot-access
     * - if the RHS is of a struct type, set the sym for this node so that
     *   a dot-access "higher up" in the AST can get access to the symbol
     *   table for the appropriate struct definition
     */
    public void nameAnalysis(SymTable symTab) {
        badAccess = false;
        SymTable structSymTab = null; // to lookup RHS of dot-access
        TSym sym = null;

        myLoc.nameAnalysis(symTab);  // do name analysis on LHS

        // if myLoc is really an ID, then sym will be a link to the ID's symbol
        if (myLoc instanceof IdNode) {
            IdNode id = (IdNode)myLoc;
            sym = id.sym();

            // check ID has been declared to be of a struct type

            if (sym == null) { // ID was undeclared
                badAccess = true;
            }
            else if (sym instanceof StructSym) {
                // get symbol table for struct type
                TSym tempSym = ((StructSym)sym).getStructType().sym();
                structSymTab = ((StructDefSym)tempSym).getSymTable();
            }
            else {  // LHS is not a struct type
                ErrMsg.fatal(id.lineNum(), id.charNum(),
                             "Dot-access of non-struct type");
                badAccess = true;
            }
        }

        // if myLoc is really a dot-access (i.e., myLoc was of the form
        // LHSloc.RHSid), then sym will either be
        // null - indicating RHSid is not of a struct type, or
        // a link to the TSym for the struct type RHSid was declared to be
        else if (myLoc instanceof DotAccessExpNode) {
            DotAccessExpNode loc = (DotAccessExpNode)myLoc;

            if (loc.badAccess) {  // if errors in processing myLoc
                badAccess = true; // don't continue proccessing this dot-access
            }
            else { //  no errors in processing myLoc
                sym = loc.sym();

                if (sym == null) {  // no struct in which to look up RHS
                    ErrMsg.fatal(loc.lineNum(), loc.charNum(),
                                 "Dot-access of non-struct type");
                    badAccess = true;
                }
                else {  // get the struct's symbol table in which to lookup RHS
                    if (sym instanceof StructDefSym) {
                        structSymTab = ((StructDefSym)sym).getSymTable();
                    }
                    else {
                        System.err.println("Unexpected Sym type in DotAccessExpNode");
                        System.exit(-1);
                    }
                }
            }

        }

        else { // don't know what kind of thing myLoc is
            System.err.println("Unexpected node type in LHS of dot-access");
            System.exit(-1);
        }

        // do name analysis on RHS of dot-access in the struct's symbol table
        if (!badAccess) {

            try {
                sym = structSymTab.lookupGlobal(myId.name()); // lookup
            } catch (EmptySymTableException ex) {
                System.err.println("Unexpected EmptySymTableException " +
                                 " in DotAccessExpNode.nameAnalysis");
            }

            if (sym == null) { // not found - RHS is not a valid field name
                ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                             "Invalid struct field name");
                badAccess = true;
            }

            else {
                myId.link(sym);  // link the symbol
                // if RHS is itself as struct type, link the symbol for its struct
                // type to this dot-access node (to allow chained dot-access)
                if (sym instanceof StructSym) {
                    mySym = ((StructSym)sym).getStructType().sym();
                }
            }
        }
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        return myId.typeCheck();
    }

    public void unparse(PrintWriter p, int indent) {
        myLoc.unparse(p, 0);
        p.print(".");
        myId.unparse(p, 0);
    }

    // 2 kids
    private ExpNode myLoc;
    private IdNode myId;
    private TSym mySym;          // link to TSym for struct type
    private boolean badAccess;  // to prevent multiple, cascading errors
    
	@Override
	public void codeGenRhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "Struct DotAccess is unsupported");
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "Struct DotAccess is unsupported");
	}
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    /**
     * Return the line number for this assignment node.
     * The line number is the one corresponding to the left operand.
     */
    public int lineNum() {
        return myLhs.lineNum();
    }

    /**
     * Return the char number for this assignment node.
     * The char number is the one corresponding to the left operand.
     */
    public int charNum() {
        return myLhs.charNum();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myLhs.nameAnalysis(symTab);
        myExp.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type typeLhs = myLhs.typeCheck();
        Type typeExp = myExp.typeCheck();
        Type retType = typeLhs;

        if (typeLhs.isFnType() && typeExp.isFnType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Function assignment");
            retType = new ErrorType();
        }

        if (typeLhs.isStructDefType() && typeExp.isStructDefType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Struct name assignment");
            retType = new ErrorType();
        }

        if (typeLhs.isStructType() && typeExp.isStructType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Struct variable assignment");
            retType = new ErrorType();
        }

        if (!typeLhs.equals(typeExp) && !typeLhs.isErrorType() && !typeExp.isErrorType()) {
            ErrMsg.fatal(lineNum(), charNum(), "Type mismatch");
            retType = new ErrorType();
        }

        if (typeLhs.isErrorType() || typeExp.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp.codeGenRhs();
		myLhs.codeGenLhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genPop(Codegen.T1);
		Codegen.generateIndexed("sw", Codegen.T1, Codegen.T0, 0);
		Codegen.genPush(Codegen.T1);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "AssignNode as LHS");
	}
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    /**
     * Return the line number for this call node.
     * The line number is the one corresponding to the function name.
     */
    public int lineNum() {
        return myId.lineNum();
    }

    /**
     * Return the char number for this call node.
     * The char number is the one corresponding to the function name.
     */
    public int charNum() {
        return myId.charNum();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myId.nameAnalysis(symTab);
        myExpList.nameAnalysis(symTab);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        if (!myId.typeCheck().isFnType()) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Attempt to call a non-function");
            return new ErrorType();
        }

        FnSym fnSym = (FnSym)(myId.sym());

        if (fnSym == null) {
            System.err.println("null sym for Id in CallExpNode.typeCheck");
            System.exit(-1);
        }

        if (myExpList.size() != fnSym.getNumParams()) {
            ErrMsg.fatal(myId.lineNum(), myId.charNum(),
                         "Function call with wrong number of args");
            return fnSym.getReturnType();
        }

        myExpList.typeCheck(fnSym.getParamTypes());
        return fnSym.getReturnType();
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
    
	@Override
	public void codeGenRhs() {
        FnSym fnSym = (FnSym)(myId.sym());
        
		Codegen.generateWithComment("", "begin " + this.unparseString());
		if (myExpList == null) {
			Codegen.generate("jal", myId.name());
			if (fnSym.getReturnType().isVoidType()) {
				Codegen.generate("li", Codegen.V0, 0);
			}
			Codegen.genPush(Codegen.V0);
		} else {
			myExpList.codeGen();
			Codegen.generate("jal", myId.name());
			Codegen.generateWithComment("addu", "tear down params", Codegen.SP, Codegen.SP, String.valueOf(myExpList.size() * 4));
			if (fnSym.getReturnType().isVoidType()) {
				Codegen.generate("li", Codegen.V0, 0);
			}
			Codegen.genPush(Codegen.V0);
		}
		Codegen.generateWithComment("", "end " + this.unparseString());
	}

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "CallExpNode as LHS");
	}
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * Return the line number for this unary expression node.
     * The line number is the one corresponding to the  operand.
     */
    public int lineNum() {
        return myExp.lineNum();
    }

    /**
     * Return the char number for this unary expression node.
     * The char number is the one corresponding to the  operand.
     */
    public int charNum() {
        return myExp.charNum();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    // one child
    protected ExpNode myExp;

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "UnaryExpNode as LHS");
	}
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    /**
     * Return the line number for this binary expression node.
     * The line number is the one corresponding to the left operand.
     */
    public int lineNum() {
        return myExp1.lineNum();
    }

    /**
     * Return the char number for this binary expression node.
     * The char number is the one corresponding to the left operand.
     */
    public int charNum() {
        return myExp1.charNum();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myExp1.nameAnalysis(symTab);
        myExp2.nameAnalysis(symTab);
    }

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;

	@Override
	public void codeGenLhs() {
		ErrMsg.fatal(this.lineNum(), this.charNum(), "BinaryExpNode as LHS");
	}
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type = myExp.typeCheck();
        Type retType = new IntType();

        if (!type.isErrorType() && !type.isIntType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }

        if (type.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", Codegen.T1, 0);
		Codegen.generate("sub", Codegen.T0, Codegen.T1, Codegen.T0);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type = myExp.typeCheck();
        Type retType = new BoolType();

        if (!type.isErrorType() && !type.isBoolType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }

        if (type.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", Codegen.T1, 1);
		Codegen.generate("subu", Codegen.T0, Codegen.T1, Codegen.T0);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

abstract class ArithmeticExpNode extends BinaryExpNode {
    public ArithmeticExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new IntType();

        if (!type1.isErrorType() && !type1.isIntType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }

        if (!type2.isErrorType() && !type2.isIntType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Arithmetic operator applied to non-numeric operand");
            retType = new ErrorType();
        }

        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }
}

abstract class LogicalExpNode extends BinaryExpNode {
    public LogicalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();

        if (!type1.isErrorType() && !type1.isBoolType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }

        if (!type2.isErrorType() && !type2.isBoolType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Logical operator applied to non-bool operand");
            retType = new ErrorType();
        }

        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }
}

abstract class EqualityExpNode extends BinaryExpNode {
	Type cmpTypes;
    public EqualityExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
        cmpTypes = null;
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();

        if (type1.isVoidType() && type2.isVoidType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to void functions");
            retType = new ErrorType();
        }

        if (type1.isFnType() && type2.isFnType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to functions");
            retType = new ErrorType();
        }

        if (type1.isStructDefType() && type2.isStructDefType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to struct names");
            retType = new ErrorType();
        }

        if (type1.isStructType() && type2.isStructType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Equality operator applied to struct variables");
            retType = new ErrorType();
        }

        if (!type1.equals(type2) && !type1.isErrorType() && !type2.isErrorType()) {
            ErrMsg.fatal(lineNum(), charNum(),
                         "Type mismatch");
            retType = new ErrorType();
        }

        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }

        if (retType.isBoolType()) {
        	cmpTypes = type1;
        }
        
        return retType;
    }
}

abstract class RelationalExpNode extends BinaryExpNode {
    public RelationalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    /**
     * typeCheck
     */
    public Type typeCheck() {
        Type type1 = myExp1.typeCheck();
        Type type2 = myExp2.typeCheck();
        Type retType = new BoolType();

        if (!type1.isErrorType() && !type1.isIntType()) {
            ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(),
                         "Relational operator applied to non-numeric operand");
            retType = new ErrorType();
        }

        if (!type2.isErrorType() && !type2.isIntType()) {
            ErrMsg.fatal(myExp2.lineNum(), myExp2.charNum(),
                         "Relational operator applied to non-numeric operand");
            retType = new ErrorType();
        }

        if (type1.isErrorType() || type2.isErrorType()) {
            retType = new ErrorType();
        }

        return retType;
    }
}

class PlusNode extends ArithmeticExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genPop(Codegen.T1);
		Codegen.generate("add", Codegen.T0, Codegen.T0, Codegen.T1);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class MinusNode extends ArithmeticExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T1);
		Codegen.genPop(Codegen.T0);
		Codegen.generate("sub", Codegen.T0, Codegen.T0, Codegen.T1);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class TimesNode extends ArithmeticExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }


    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genPop(Codegen.T1);
		Codegen.generate("mul", Codegen.T0, Codegen.T0, Codegen.T1);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class DivideNode extends ArithmeticExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genPop(Codegen.T1);
		Codegen.generate("div", Codegen.T1, Codegen.T0);
		Codegen.generate("mflo", Codegen.T0);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class AndNode extends LogicalExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		String scLabel = Codegen.nextLabel();
		myExp1.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("beq", Codegen.T0, "$zero", scLabel);
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genLabel(scLabel);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class OrNode extends LogicalExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		String scLabel = Codegen.nextLabel();
		myExp1.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.generate("bne", Codegen.T0, "$zero", scLabel);
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genLabel(scLabel);
		Codegen.genPush(Codegen.T0);
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class EqualsNode extends EqualityExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		if (cmpTypes.isBoolType() || cmpTypes.isIntType()) {
			myExp1.codeGenRhs();
			myExp2.codeGenRhs();
			Codegen.genPop(Codegen.T0);
			Codegen.genPop(Codegen.T1);
			Codegen.generate("li", "$t2", 0);
			String skipLabel = Codegen.nextLabel();
			Codegen.generate("bne", Codegen.T0, Codegen.T1, skipLabel);
			Codegen.generate("addu", "$t2", 1);
			Codegen.genLabel(skipLabel);
			Codegen.genPush("$t2");
		} else if (cmpTypes.isStringType()) {
			String loopStart = Codegen.nextLabel();
			String exitLoop = Codegen.nextLabel();
			myExp1.codeGenRhs();
			myExp2.codeGenRhs();
			Codegen.genPop(Codegen.T0);
			Codegen.genPop(Codegen.T1);
			Codegen.genLabel(loopStart);
			Codegen.generateIndexed("lb", "$t2", Codegen.T0, 0);
			Codegen.generateIndexed("lb", "$t3", Codegen.T1, 0);
			Codegen.generate("bne", "$t2", "$t3", exitLoop);
			Codegen.generate("beq", "$t2", "$zero", exitLoop);
			Codegen.generate("addi", Codegen.T0, Codegen.T0, 1);
			Codegen.generate("addi", Codegen.T1, Codegen.T1, 1);
			Codegen.generate("j", loopStart);
			Codegen.genLabel(exitLoop);
			String falseEnding = Codegen.nextLabel();
			Codegen.generate("li", "$t4", 0);
			Codegen.generate("bne", "$t2", "$zero", falseEnding);
			Codegen.generate("addi", "$t4", "$t4", 1);
			Codegen.genLabel(falseEnding);
			Codegen.genPush("$t4");
			
		} else {
			ErrMsg.fatal(myExp1.lineNum(), myExp1.charNum(), "Unsupported type in EqualsNode");
		}
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class NotEqualsNode extends EqualityExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T0);
		Codegen.genPop(Codegen.T1);
		Codegen.generate("li", "$t2", 0);
		String skipLabel = Codegen.nextLabel();
		Codegen.generate("beq", Codegen.T0, Codegen.T1, skipLabel);
		Codegen.generate("addu", "$t2", 1);
		Codegen.genLabel(skipLabel);
		Codegen.genPush("$t2");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class LessNode extends RelationalExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T1);
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", "$t2", 0);
		String skipLabel = Codegen.nextLabel();
		Codegen.generate("bge", Codegen.T0, Codegen.T1, skipLabel);
		Codegen.generate("addu", "$t2", 1);
		Codegen.genLabel(skipLabel);
		Codegen.genPush("$t2");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class GreaterNode extends RelationalExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T1);
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", "$t2", 0);
		String skipLabel = Codegen.nextLabel();
		Codegen.generate("ble", Codegen.T0, Codegen.T1, skipLabel);
		Codegen.generate("addu", "$t2", 1);
		Codegen.genLabel(skipLabel);
		Codegen.genPush("$t2");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class LessEqNode extends RelationalExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T1);
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", "$t2", 0);
		String skipLabel = Codegen.nextLabel();
		Codegen.generate("bgt", Codegen.T0, Codegen.T1, skipLabel);
		Codegen.generate("addu", "$t2", 1);
		Codegen.genLabel(skipLabel);
		Codegen.genPush("$t2");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}

class GreaterEqNode extends RelationalExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
    
	@Override
	public void codeGenRhs() {
		Codegen.generateWithComment("", "begin " + this.unparseString());
		myExp1.codeGenRhs();
		myExp2.codeGenRhs();
		Codegen.genPop(Codegen.T1);
		Codegen.genPop(Codegen.T0);
		Codegen.generate("li", "$t2", 0);
		String skipLabel = Codegen.nextLabel();
		Codegen.generate("blt", Codegen.T0, Codegen.T1, skipLabel);
		Codegen.generate("addu", "$t2", 1);
		Codegen.genLabel(skipLabel);
		Codegen.genPush("$t2");
		Codegen.generateWithComment("", "end " + this.unparseString());
	}
}
