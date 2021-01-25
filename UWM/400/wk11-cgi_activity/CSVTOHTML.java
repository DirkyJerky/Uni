import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class CSVTOHTML {
	static String HEADER =    "<html>\n"
							+ "    <head>\n"
							+ "        <style>\n"
							+ "            table {\n"
							+ "                border-collapse: collapse;\n"
							+ "                margin: 40px 30px;\n"
							+ "                font-size: 1em;\n"
							+ "                font-family: sans-serif;\n"
							+ "                min-width: 400px;\n"
							+ "                box-shadow: 0 0 20px #00000040;\n"
							+ "            }\n"
							+ "            th, td {\n"
							+ "                padding: 12px 15px;\n"
							+ "            }\n"
							+ "            tr {\n"
							+ "                border-bottom: 1px solid #dddddd;\n"
							+ "            }\n"
							+ "            tr:nth-of-type(even) {\n"
							+ "                background-color: #f3f3d3;\n"
							+ "            }\n"
							+ "            tr:last-of-type {\n"
							+ "                border-bottom: none;\n"
							+ "            }\n"
							+ "        </style>\n"
							+ "    </head>\n"
							+ "    <body>\n"
							+ "        <p>\n";
	
	
	static String FOOTER =    "        </p>\n"
							+ "    </body>\n"
							+ "</html>\n";

    public static void main(String[] args) {
        try {
        	String gen = genTable();
        	
            System.out.print(HEADER);
            System.out.println(gen);
            System.out.print(FOOTER);
        } catch(Exception e) {
            System.out.println("<html><body><pre>");
            System.out.println("Ooops, something went wrong. There was an exception in the Java program:");
            e.printStackTrace(System.out);
            System.out.println("</pre></body></html>");
        }
    }

    static String FILE = "data.csv";
    
    private static String genTable() throws Exception {
		List<String[]> data = readAll(new File(FILE));
		Iterator<String[]> iter = data.iterator();
		
		StringBuilder out = new StringBuilder();
		
		out.append("<table><thead><tr>");
		
		for (String colTitle : iter.next()) {
			out.append("<th>");
			out.append(colTitle);
			out.append("</th>");
		}
		
		out.append("</tr></thead><tbody>");
		
		while (iter.hasNext()) {
			String[] rowData = iter.next();
			
			out.append("<tr>");
			
			for (String unitData : rowData) {
				out.append("<td>");
				out.append(unitData);
				out.append("</td>");
			}
			
			out.append("</tr>");
		}
		
		out.append("</tbody></table>");
		
		return out.toString();
	}

	public static List<String[]> readAll(File csvFile) throws Exception {
        List<String[]> output = new ArrayList<>();
        try (Scanner fin = new Scanner(csvFile)) {
            while(fin.hasNextLine()) {
                output.add(fin.nextLine().split(","));
            }    
        }
        return output;
    }    

}

