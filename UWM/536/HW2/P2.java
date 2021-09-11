import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the C-- scanner.
 * This version is set up to test all tokens, but you are required to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens)
 */
public class P2 {
    public static void main(String[] args) throws IOException {
                                           // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        testTokenLocations();
        testTokenErrors();
        CharNum.num = 1;
    
        // ADD CALLS TO OTHER TEST METHODS HERE
    }

    /**
     * testTokenLocations
     *
     * Open and read from file locatedTokens.in
     * For each token read, write the corresponding location to locatedTokens.out
     * In the form "{token}@{line}:{char}", where line and char are the source locations of the token
     * locatedTokens.out can be compared then with locatedTokens.corr
	 *
	 * locatedTokens.in also tests comments functionality (comments dont show up in result)
	 *
	 * In summary, this will test
	 *  - Correct charnum incrementing from tokens
	 *  - Correct linenum incrementing from tokens
	 *  - Comments
	 *  - Whitespace impact on charnum/linenum
     */
    private static void testTokenLocations() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("locatedTokens.in");
            outFile = new PrintWriter(new FileWriter("locatedTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File locatedTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("locatedTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.print("bool"); 
                break;
            case sym.INT:
                outFile.print("int");
                break;
            case sym.VOID:
                outFile.print("void");
                break;
            case sym.TRUE:
                outFile.print("true"); 
                break;
            case sym.FALSE:
                outFile.print("false"); 
                break;
            case sym.STRUCT:
                outFile.print("struct"); 
                break;
            case sym.CIN:
                outFile.print("cin"); 
                break;
            case sym.COUT:
                outFile.print("cout");
                break;				
            case sym.IF:
                outFile.print("if");
                break;
            case sym.ELSE:
                outFile.print("else");
                break;
            case sym.WHILE:
                outFile.print("while");
                break;
            case sym.RETURN:
                outFile.print("return");
                break;
            case sym.ID:
                outFile.print(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.print(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.print(((StrLitTokenVal)token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.print("{");
                break;
            case sym.RCURLY:
                outFile.print("}");
                break;
            case sym.LPAREN:
                outFile.print("(");
                break;
            case sym.RPAREN:
                outFile.print(")");
                break;
            case sym.SEMICOLON:
                outFile.print(";");
                break;
            case sym.COMMA:
                outFile.print(",");
                break;
            case sym.DOT:
                outFile.print(".");
                break;
            case sym.WRITE:
                outFile.print("<<");
                break;
            case sym.READ:
                outFile.print(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.print("++");
                break;
            case sym.MINUSMINUS:
                outFile.print("--");
                break;	
            case sym.PLUS:
                outFile.print("+");
                break;
            case sym.MINUS:
                outFile.print("-");
                break;
            case sym.TIMES:
                outFile.print("*");
                break;
            case sym.DIVIDE:
                outFile.print("/");
                break;
            case sym.NOT:
                outFile.print("!");
                break;
            case sym.AND:
                outFile.print("&&");
                break;
            case sym.OR:
                outFile.print("||");
                break;
            case sym.EQUALS:
                outFile.print("==");
                break;
            case sym.NOTEQUALS:
                outFile.print("!=");
                break;
            case sym.LESS:
                outFile.print("<");
                break;
            case sym.GREATER:
                outFile.print(">");
                break;
            case sym.LESSEQ:
                outFile.print("<=");
                break;
            case sym.GREATEREQ:
                outFile.print(">=");
                break;
			case sym.ASSIGN:
                outFile.print("=");
                break;
			default:
				outFile.print("UNKNOWN TOKEN");
            } // end switch
            
            outFile.print('@');
            outFile.print(((TokenVal) token.value).linenum);
            outFile.print(':');
            outFile.print(((TokenVal) token.value).charnum);
            outFile.println();

            token = scanner.next_token();
        } // end while
        outFile.close();
    }

    /**
     * testTokenErrors
     *
     * Open and read from file errorTokens.in
     * No parsable tokens exist in the file, so the parser should return no tokens.
     * The error messages will be piped to errors.out in the makefile, which can be compared with errors.corr
     */
    private static void testTokenErrors() throws IOException {
        // open input and output files
        FileReader inFile = null;
        try {
            inFile = new FileReader("errorTokens.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        }
        
        System.err.println("BEGIN PARSING ERRORS");

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        // First token should be EOF
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                System.err.println("bool"); 
                break;
            case sym.INT:
                System.err.println("int");
                break;
            case sym.VOID:
                System.err.println("void");
                break;
            case sym.TRUE:
                System.err.println("true"); 
                break;
            case sym.FALSE:
                System.err.println("false"); 
                break;
            case sym.STRUCT:
                System.err.println("struct"); 
                break;
            case sym.CIN:
                System.err.println("cin"); 
                break;
            case sym.COUT:
                System.err.println("cout");
                break;				
            case sym.IF:
                System.err.println("if");
                break;
            case sym.ELSE:
                System.err.println("else");
                break;
            case sym.WHILE:
                System.err.println("while");
                break;
            case sym.RETURN:
                System.err.println("return");
                break;
            case sym.ID:
                System.err.println(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                System.err.println(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                System.err.println(((StrLitTokenVal)token.value).strVal);
                break;    
            case sym.LCURLY:
                System.err.println("{");
                break;
            case sym.RCURLY:
                System.err.println("}");
                break;
            case sym.LPAREN:
                System.err.println("(");
                break;
            case sym.RPAREN:
                System.err.println(")");
                break;
            case sym.SEMICOLON:
                System.err.println(";");
                break;
            case sym.COMMA:
                System.err.println(",");
                break;
            case sym.DOT:
                System.err.println(".");
                break;
            case sym.WRITE:
                System.err.println("<<");
                break;
            case sym.READ:
                System.err.println(">>");
                break;				
            case sym.PLUSPLUS:
                System.err.println("++");
                break;
            case sym.MINUSMINUS:
                System.err.println("--");
                break;	
            case sym.PLUS:
                System.err.println("+");
                break;
            case sym.MINUS:
                System.err.println("-");
                break;
            case sym.TIMES:
                System.err.println("*");
                break;
            case sym.DIVIDE:
                System.err.println("/");
                break;
            case sym.NOT:
                System.err.println("!");
                break;
            case sym.AND:
                System.err.println("&&");
                break;
            case sym.OR:
                System.err.println("||");
                break;
            case sym.EQUALS:
                System.err.println("==");
                break;
            case sym.NOTEQUALS:
                System.err.println("!=");
                break;
            case sym.LESS:
                System.err.println("<");
                break;
            case sym.GREATER:
                System.err.println(">");
                break;
            case sym.LESSEQ:
                System.err.println("<=");
                break;
            case sym.GREATEREQ:
                System.err.println(">=");
                break;
			case sym.ASSIGN:
                System.err.println("=");
                break;
			default:
				System.err.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        
        System.err.println("END PARSING ERRORS");
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.in
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool"); 
                break;
            case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true"); 
                break;
            case sym.FALSE:
                outFile.println("false"); 
                break;
            case sym.STRUCT:
                outFile.println("struct"); 
                break;
            case sym.CIN:
                outFile.println("cin"); 
                break;
            case sym.COUT:
                outFile.println("cout");
                break;				
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;	
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
			case sym.ASSIGN:
                outFile.println("=");
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }
}
