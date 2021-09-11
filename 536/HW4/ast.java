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
//     Subclass            Children
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
// whether they are leaves, internal nodes with linked lists of children, or
// internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode {
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);
    
    abstract public void nameAnalysis(SymTable symTable);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
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

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    private DeclListNode myDeclList;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myDeclList.nameAnalysis(symTable);
	}
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
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

    private List<DeclNode> myDecls;

	@Override
	public void nameAnalysis(SymTable symTable) {
		for (DeclNode decl : myDecls) {
			decl.nameAnalysis(symTable);
		}
	}
	
	public List<DeclNode> getDeclNodes() {
		return myDecls;
	}
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
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

    private List<FormalDeclNode> myFormals;

	@Override
	public void nameAnalysis(SymTable symTable) {
		for (FormalDeclNode node : myFormals) {
			node.nameAnalysis(symTable);
		}
	}

	public List<String> getTypeList() {
		List<String> typeList = new ArrayList<>(myFormals.size());
		
		for (FormalDeclNode node : myFormals) {
			typeList.add(node.getType());
		}
		return typeList;
	}
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myDeclList.nameAnalysis(symTable);
		myStmtList.nameAnalysis(symTable);
	}
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    private List<StmtNode> myStmts;

	@Override
	public void nameAnalysis(SymTable symTable) {
		for (StmtNode node : myStmts) {
			node.nameAnalysis(symTable);
		}
	}
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
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

    private List<ExpNode> myExps;

	@Override
	public void nameAnalysis(SymTable symTable) {
		for (ExpNode node : myExps) {
			node.nameAnalysis(symTable);
		}
	}
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    private TypeNode myType;
	private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type
  
    public static int NOT_STRUCT = -1;
    
    public TypeNode getMyType() {
		return myType;
	}

	public IdNode getMyId() {
		return myId;
	}

	@Override
	public void nameAnalysis(SymTable symTable) {
		myType.nameAnalysis(symTable);
		
		if (myType instanceof VoidNode) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
		}
		
		try {
			String name = this.myId.getID();
			
			TSym.VariableSym varSym;
			if (this.myType instanceof StructNode) {
				varSym = new TSym.StructVariableSym(((StructNode) this.myType).getIdNode().getID(), name);
			} else {
				varSym = new TSym.VariableSym(this.myType.getType(), name);
			}
			symTable.addDecl(name, varSym);
//			myId.setSymbol(varSym);
		} catch (DuplicateSymException e) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
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

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myType.nameAnalysis(symTable);
		
		try {
			if (null == symTable.lookupLocal(myId.getID())) {
				String name = myId.getID();
				List<String> paramTypes = myFormalsList.getTypeList();
				String returnType = myType.getType();
				TSym fnSym = new TSym.FunctionDefnSym(name, paramTypes, returnType );
				
				symTable.addDecl(name, fnSym);
//				myId.setSymbol(fnSym);
			} else {
				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
			}
		} catch (DuplicateSymException e) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			symTable.addScope();
			
			myFormalsList.nameAnalysis(symTable);
			myBody.nameAnalysis(symTable);
			
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
		
	}
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

	public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    private TypeNode myType;
    private IdNode myId;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myType.nameAnalysis(symTable);

		if (myType instanceof VoidNode) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
		}
		
		TSym sym = new TSym.VariableSym(myType.getType(), myId.getID());
		try {
			symTable.addDecl(myId.getID(), sym);
//			myId.setSymbol(sym);
		} catch (DuplicateSymException e) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}

    public String getType() {
		return myType.getType();
	}
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndentation(p, indent);
        p.println("};\n");

    }

    private IdNode myId;
    private DeclListNode myDeclList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		SymTable fields = new SymTable();
		
		TSym sym = new TSym.StructDefnSym(myId.getID(), fields);
		try {
			symTable.addDecl(myId.getID(), sym);
//			myId.setSymbol(sym);
		} catch (DuplicateSymException e) {
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
			return;
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		for (DeclNode node : myDeclList.getDeclNodes()) {
			if (!(node instanceof VarDeclNode)) {
				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Error:  Struct defn members not VarDeclNodes");
			} else {
				((VarDeclNode) node).nameAnalysis(fields);
			}
		}
	}
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
	public abstract String getType();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }

	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}

	@Override
	public String getType() {
		return "int";
	}
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }

	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}

	@Override
	public String getType() {
		return "bool";
	}
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}

	@Override
	public String getType() {
		return "void";
	}
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    private IdNode myId;

	@Override
	public String getType() {
		return "struct";
	}

	public IdNode getIdNode() {
		return myId;
	}

	@Override
	public void nameAnalysis(SymTable symTable) {
		try {
			TSym sym = symTable.lookupGlobal(myId.getID());
			if (sym == null) {
				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Undeclared identifier");
				return;
			}
			
			if (! (sym instanceof TSym.StructDefnSym)) {
				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid name of struct type");
			} else {
//				myId.setSymbol(sym);
			}
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
		
	}
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    private AssignNode myAssign;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myAssign.nameAnalysis(symTable);
	}
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    private ExpNode myExp;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
	}
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    private ExpNode myExp;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
	}
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
	}
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    private ExpNode myExp;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
	}
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
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

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
		
		symTable.addScope();
		
		myDeclList.nameAnalysis(symTable);
		myStmtList.nameAnalysis(symTable);
		
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
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

    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
		
		symTable.addScope();
		
		myThenDeclList.nameAnalysis(symTable);
		myThenStmtList.nameAnalysis(symTable);
		
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
		
		symTable.addScope();
		
		myElseDeclList.nameAnalysis(symTable);
		myElseStmtList.nameAnalysis(symTable);
		
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
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

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
		
		symTable.addScope();
		
		myDeclList.nameAnalysis(symTable);
		myStmtList.nameAnalysis(symTable);
		
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
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

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
		
		symTable.addScope();
		
		myDeclList.nameAnalysis(symTable);
		myStmtList.nameAnalysis(symTable);
		
		try {
			symTable.removeScope();
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    private CallExpNode myCall;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myCall.nameAnalysis(symTable);
	}
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
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

    private ExpNode myExp; // possibly null

	@Override
	public void nameAnalysis(SymTable symTable) {
		if (myExp != null) {
			myExp.nameAnalysis(symTable);
		}
	}
    
    
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop 
	}
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		// Noop
	}
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if (this.symbol != null) {
        	p.print('(');
        	p.print(this.symbol.getType());
        	p.print(')');
        }
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    
    private TSym symbol;

	public TSym getSymbol() {
		return symbol;
	}

	public void setSymbol(TSym symbol) {
		this.symbol = symbol;
	}

	public int getLineNum() {
		return myLineNum;
	}

	public int getCharNum() {
		return myCharNum;
	}
	
	public String getID() {
		return this.myStrVal;
	}

	// This should only be called when used as an ExpNode, i.e. as variable access.
	@Override
	public void nameAnalysis(SymTable symTable) {
		try {
			TSym sym = symTable.lookupGlobal(this.myStrVal);
			
			if (sym == null) {
				ErrMsg.fatal(getLineNum(), getCharNum(), "Undeclared identifier");
			} else {
				this.setSymbol(sym);
			}
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }

    private ExpNode myLoc;
    private IdNode myId;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myLoc.nameAnalysis(symTable);
		
		if (!(myLoc instanceof IdNode)) {
			// Not the correct location, but the correct location is unobtainable with the current API.
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Dot-access of non-struct type"); // Not of IdNode type either, shouldnt happen.
			return;
		}
		
		String lhs = ((IdNode) myLoc).getID();
		
		try {
			TSym sym = symTable.lookupGlobal(lhs);
			
			// If sym doesn't exist, the nameAnalysis call on myLoc above will error for us
			if (sym == null) {
				return;
			}
			
			if (!(sym instanceof TSym.StructVariableSym)) {
				ErrMsg.fatal(((IdNode)myLoc).getLineNum(), ((IdNode)myLoc).getCharNum(), "Dot-access of non-struct type");
				return;
			}
			
			TSym structDefn = symTable.lookupGlobal(((TSym.StructVariableSym) sym).getStructID());

			if (!(structDefn instanceof TSym.StructDefnSym)) {
				ErrMsg.fatal(((IdNode)myLoc).getLineNum(), ((IdNode)myLoc).getCharNum(), "Dot-access of non-struct type");
				return;
			}
			
			SymTable fieldMap = ((TSym.StructDefnSym) structDefn).getFieldMap();
			
			TSym fieldSym = fieldMap.lookupGlobal(myId.getID());
			
			if (fieldSym == null) {
				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid struct field name");
				return;
			}
			
			myId.setSymbol(fieldSym);
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    private ExpNode myLhs;
    private ExpNode myExp;
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		myLhs.nameAnalysis(symTable);
		myExp.nameAnalysis(symTable);
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

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
    
	@Override
	public void nameAnalysis(SymTable symTable) {
		try {
			TSym sym = symTable.lookupGlobal(this.myId.getID());
			
			if (!(sym instanceof TSym.FunctionDefnSym)) {
//				ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Call attempt on non function type");
			} else {
				myId.setSymbol(sym);
			}
			
			if (myExpList != null) {
				myExpList.nameAnalysis(symTable);
			}
		} catch (EmptySymTableException e) {
			e.printStackTrace();
		}
	}
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    protected ExpNode myExp;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp.nameAnalysis(symTable);
	}
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    protected ExpNode myExp1;
    protected ExpNode myExp2;

	@Override
	public void nameAnalysis(SymTable symTable) {
		myExp1.nameAnalysis(symTable);
		myExp2.nameAnalysis(symTable);
	}
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
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
}

class MinusNode extends BinaryExpNode {
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
}

class TimesNode extends BinaryExpNode {
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
}

class DivideNode extends BinaryExpNode {
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
}

class AndNode extends BinaryExpNode {
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
}

class OrNode extends BinaryExpNode {
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
}

class EqualsNode extends BinaryExpNode {
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
}

class NotEqualsNode extends BinaryExpNode {
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
}

class LessNode extends BinaryExpNode {
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
}

class GreaterNode extends BinaryExpNode {
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
}

class LessEqNode extends BinaryExpNode {
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
}

class GreaterEqNode extends BinaryExpNode {
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
}
