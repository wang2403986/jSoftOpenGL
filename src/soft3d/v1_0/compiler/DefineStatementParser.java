package soft3d.v1_0.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.LinkedList;

public class DefineStatementParser {

	public static void createShader(String vertexShader, String fragShader) throws IOException{
		
		mainFunction.setLength(0);
		functions.setLength(0);
		createShader(vertexShader);
		StringBuilder vertexShaderMain = new StringBuilder(mainFunction);
		mainFunction.setLength(0);
		createShader(fragShader);
		StringBuilder fragShaderMain = new StringBuilder(mainFunction);
		StringBuilder functionDefs = new StringBuilder(DefineStatementParser.functions);
		createUniformsMap(null);
		ShaderCompiler.vertexShaderMain=vertexShaderMain;
		ShaderCompiler.fragShaderMain=fragShaderMain;
		ShaderCompiler.functionDefs=functionDefs;
	}
	public static void createShader(String shaderFile) throws IOException{
		FileReader fileReader= new FileReader(shaderFile);
		BufferedReader br = new BufferedReader(fileReader);
		StringBuilder code = new StringBuilder(512);
		String line=br.readLine();
		if(line!=null) {
			code.append(line);
		}
		while ((line=br.readLine())!=null) {
			code.append("\n"+line );
		}
		br.close();
		CharBuffer buffer=CharBuffer.wrap(code.toString().toCharArray());
		Tokenizer tokenizer=new Tokenizer();
		LinkedList<Identifier> statement = new LinkedList<Identifier>();
		while (buffer.hasRemaining()) {
			Identifier i=tokenizer.next(buffer);
			if (i!=null) {
				statement.add(i);
			}
		}
		parse(statement, null);
		System.err.println(mainFunction);
	}
	static StatementParser statementParser = new StatementParser();
	public static void parse (LinkedList<Identifier> statement, ShaderCompiler shaderCompiler) {
		while (!statement.isEmpty()){
			Context context=new Context();
			statementParser.out.setLength(0);
			LinkedList<Identifier> prevs = defineStatement(statement, context); // attribute vec3 aPosition;
																			    // vec3 functionName(vec3 arg0, vec3 arg1) {;
			boolean noOutput=(context.isAttribute||context.isUniform||context.isVarying);
			if (statementParser.out.length()==0 && !noOutput){
				if (context.varType!=null&&context.varName!=null && !"main".equals(context.varName) ){
					statementParser.out.append(""+context.varType+" "+ context.varName +";");
				}
			}
			if (!statement.isEmpty()){
				Identifier current_id = statement.getFirst();
				if (current_id.token==',' && prevs!=null) {
					if(!noOutput) statementParser.out.append(";");
					statement.removeFirst();
					for (Identifier e: prevs)
						statement.addFirst(e);
				}
			}
			if(!noOutput&&statementParser.out.length()>0){
//				System.err.println("static "+statementParser.out);
				functions.append("static "+statementParser.out +"\n");
			}
			bindShaderVar(context, shaderCompiler);
		}
	}
	public static void createUniformsMap(ShaderCompiler shaderCompiler){
		int uniformId=0;
		ShaderCompiler.uniformsMap=new HashMap<>();
		for (String uniformName: ShaderCompiler.uniforms.keySet()){
			if(ShaderCompiler.uniformsMap.get(uniformName)==null)
				ShaderCompiler.uniformsMap.put(uniformName, uniformId++);
		}
		createAttribLocationsMap(shaderCompiler);
	}
	public static void createAttribLocationsMap(ShaderCompiler shaderCompiler){
		int uniformId=0;
		ShaderCompiler.attribLocationsMap=new HashMap<>();
		for (String uniformName: ShaderCompiler.attributes.keySet()){
			if(ShaderCompiler.attribLocationsMap.get(uniformName)==null)
				ShaderCompiler.attribLocationsMap.put(uniformName, uniformId++);
		}
	}
	public static StringBuilder mainFunction=new StringBuilder();
	public static StringBuilder functions=new StringBuilder();
	public static LinkedList<Identifier> defineStatement(LinkedList<Identifier> statement,Context context){
		Identifier i = null;
		LinkedList<Identifier> prevs=new LinkedList<>();
		if(context==null){
			context = new Context();
		}
		boolean getType=true;
		while (!statement.isEmpty()) {
			i = statement.removeFirst();
			prevs.add(i);
			if (i.name.equals("varying")){
				context.isVarying=true;
			} else if (i.name.equals("dFdx")||i.name.equals("dFdy") ){
				context.dFdx=i.name;
			} else if (i.name.equals("mipmap")&& i.token=='('){
				context.mipmap=statement.removeFirst().name;
				statement.removeFirst();
			} else if (i.name.equals("attribute")){
				context.isAttribute=true;
			} else if (i.name.equals("uniform")){
				context.isUniform=true;
			} else if (i.name.equals("output")){
				context.isOutput=true;
			} else if (i.name.equals("input")){
				context.isInput=true;
			} else if (i.name.equals("const")){
				context.isConst=true;
			} else if (i.name.equals("static")){
				context.isStatic=true;
			} else if (i.token == '=') {
				statement.addFirst(prevs.removeLast());
				statement.addFirst(i);
				statementParser.out.append(context.varType +" ");
				statementParser.statement(statement);
				
			} else if (i.token==',' || i.token==')' || i.token=='}') {// reach end
				prevs.removeLast();
				statement.addFirst(i);
//				statementParser.println(";");
				break;
			} else if (i.token==';'){
				break;
			} else if (i.token=='[') { // uniform type uMatrix[4];
				int arraySize = Integer.parseInt(statement.removeFirst().name);
				context.arraySize=arraySize;
				if(statement.removeFirst().token!=']'){
					System.err.println("not match ']' !");
				}
				break;
			} else if (i.token==(char)Tokenizer.Id) {
				if(getType) {
					context.varType=i.name;
					getType = false;
				}
				else context.varName=i.name;
				
			} else if (i.token==Tokenizer.Call) { // void functionName(int arg0, vec3 arg1) {}
				boolean isMainFunction=false;
				StringBuilder tmp = new StringBuilder();
				context.varName=i.name;
				if("main".equals(context.varName)){
					System.out.println("main function found!!");
					isMainFunction=true;
				}
				tmp.append("" +context.varType+" "+context.varName +"(");
				while (!statement.isEmpty()&&statement.getFirst().token != ')') {
					Context context2=new Context();
					defineStatement(statement , context2);
					tmp.append( context2.varType+" "+context2.varName );
					if (!statement.isEmpty()&&statement.getFirst().token == ','){
						tmp.append(",");
						statement.removeFirst();
					}
				}
				if (!statement.isEmpty()&&statement.getFirst().token == ')'){
					statement.removeFirst();
					statementParser.out.setLength(0);
					statementParser.statement(statement);
					if (isMainFunction) {
						mainFunction.setLength(0);
						mainFunction.append(statementParser.out);
						statementParser.out.setLength(0);
					} else {
						tmp.append(") "  + statementParser.out  );
						statementParser.out.setLength(0);
						statementParser.out.append(tmp);
					}
				}
				break;
			}
		}
		return prevs;
	}
	public static String bindShaderVar( Context context, ShaderCompiler shaderCompiler){
		if(context==null)
			return null;
		if (context.isAttribute){
			ShaderCompiler.attributes.put(context.varName, context.varType);
			
		} else if (context.isUniform) {
			if (context.arraySize>0) {
				context.varType = context.varType+"[]"; // to support array type
			}
			ShaderCompiler.uniforms.put(context.varName, context.varType);
			
		} else if(context.isVarying){
			if (context.mipmap!=null) {
				Identifier id = new Identifier();id.name=context.varName;
				id.variableType=context.varType;
				id.objectValue=context.mipmap;
				MipmapCodeGenerater.mipmap_levels.put(context.varName, id);
			}
			if (context.dFdx!=null) {
				Identifier id = new Identifier();id.name=context.varName;
				id.variableType=context.varType;
				id.objectValue=context.dFdx;
				MipmapCodeGenerater.dFdxdy.put(context.varName, id);
			}
			ShaderCompiler.varyings.put(context.varName, context.varType);
		}
		return null;
	}
	public static class Context {
		public Context parent;
		public String varType;
		public String varName;
		public String mipmap, dFdx;
		public StringBuilder output;
		public int arraySize=0;
		public boolean isAttribute,isUniform,isVarying, isConst,isStatic,isOutput,isInput;
	}
}