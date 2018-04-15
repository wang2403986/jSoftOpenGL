package soft3d.v1_0.compiler;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class VariableType {
	
	public static int vec_size(String e) {
		Integer i=vector_types.get(e);
		return i==null?0:i.intValue();
	}
	public static boolean array_type(String e) {
		return e.charAt(e.length()-1)==']';
	}
	public static Map<String, Integer> vector_types=new HashMap<>();

	static {
		String[] types = { "int", "float", "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4", "mat4" };
		int[] sizes =    {  1,      1,        2,      3,      4,      2,        3,       4,      16 };
		for (int i = 0; i < sizes.length; i++)
			vector_types.put(types[i], sizes[i]);
	}
	public static String typedef_str(String varName,String varType,PrintWriter out) {
		if(vec_size(varType)>1){
			out.println(  " " +varType+" " +varName +"=new " + varType+ "();"  );
		} else{
			out.println(  " " +varType+" " +varName +"=0;"  );
		}
		return "";
	}
//	public static boolean isVector(Identifier b) {
//		Integer i= vector_types.get(b.variableType);
//		return  i!= null&&i.intValue()>1;
//	}
//	public static String class_str(String varName,String varType){
//		String s= "";
//		int vec_size = vec_size(varType);
//		if (vec_size<=1)
//			return s;
//		s+=varType+" "+varName+"="+varType+"(";
//		s+=varName+"_0";
//		for (int i = 1; i < vec_size; i++) {
//			s+=","+varName+"_"+i;
//		}
//		s+=")";
//		return s;
//	}
//	public static String scalar_str(String varName,String varType){
//		String s= "";
//		int vec_size = vec_size(varType);
//		String type= "float";
//		if (varType.startsWith("vec")||varType.startsWith("mat")) {
//			
//		}else if (varType.startsWith("ivec")) {
//			type= "int";
//		}
//		for (int i = 0; i < vec_size; i++) {
//			s += type+" "+varName +"_"+i+"=0;";
//			if(vec_size==1)
//			s = varType + " "+varName +"=0;";
//		}
//		return s;
//	}
}
