import java.util.List;

public class TSym {
    private String type;
    
    public TSym(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type;
    }
    
    public static class VariableSym extends TSym {
		private String name;

		public VariableSym(String type, String name) {
			super(type);
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
    }
    
    public static class StructVariableSym extends VariableSym {
		private String structID;

		public StructVariableSym(String structID, String name) {
			super(structID, name);
			this.structID = structID;
		}

		public String getStructID() {
			return structID;
		}
    	
    }
    
    public static class FunctionDefnSym extends TSym {
		private String name;
		private List<String> paramTypes;
		private String returnType;

		public FunctionDefnSym(String name, List<String> paramTypes, String returnType) {
			super(buildTypeSig(paramTypes, returnType));
			this.name = name;
			this.paramTypes = paramTypes;
			this.returnType = returnType;
		}
		
		private static String buildTypeSig(List<String> params, String returnTy) {
			StringBuilder builder = new StringBuilder();
			
			boolean isFirst = true; 
			
			for (String type : params) {
				if (isFirst) {
					builder.append(type);
					isFirst = false;
				} else {
					builder.append(',');
					builder.append(type);
				}
			}
			
			builder.append("->");
			builder.append(returnTy);
			
			return builder.toString();
		}

		public String getName() {
			return name;
		}

		public List<String> getParamTypes() {
			return paramTypes;
		}

		public String getReturnType() {
			return returnType;
		}
    }
    
    public static class StructDefnSym extends TSym {
		private String name;
		private SymTable fields; // field_name -> type
		
		public StructDefnSym(String name, SymTable fields) {
			super(name);
			this.name = name;
			this.fields = fields;
		}
		
		public String getName() {
			return name;
		}

		public SymTable getFieldMap() {
			return fields;
		}
    }
}
