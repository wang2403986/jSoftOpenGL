package soft3d.v1_0.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import soft3d.util.JavaStringObject;
import soft3d.v1_0.TinyGL;
import soft3d.v1_0.VertexAttribPointer;
import static soft3d.v1_0.compiler.VariableType.*;

public class ShaderCompiler {
	
	public static int sizeOfVSOutput(){
		int ret  = 0;
		for(String name : varyings.keySet()){
			if (attributes.get(name)==null) {
				int vecSize = vec_size(varyings.get(name));
				ret += vecSize;
			}
		}
		return ret + 3;
	}
	public static void pushVertexShader(PrintWriter out,String index,String vertexShader){
		out.println(vertexShader);
	}
	static String property_str(int i){ return "."+property[i];}
	public static TinyGL createProgram(String vertexShaderFile,String fragShaderFile){
		try {
			createShader(vertexShaderFile, fragShaderFile);
			StringWriter writer = new StringWriter(); // 内存字符串输出流
			StringWriter bufferWriter = new StringWriter(); // 内存字符串输出流
			PrintWriter out = new PrintWriter(bufferWriter);
			FileReader fileReader= new FileReader("kernels/TinyGL.txt");
			BufferedReader br = new BufferedReader(fileReader);
			String line="";
			while ((line=br.readLine())!=null) {
				out.println(line);
				if (line.startsWith("//__init();")) {
					out.println("uniforms= new Object["+uniforms.size()+"];");
//					for (String name :uniforms.keySet()) {
//						String type= uniforms.get(name);  int index = uniformsMap.get(name);
//						if(vec_size(type)>1) out.println("uniforms["+index+"]=new "+type +"();");
//					}
				}else if (line.startsWith("//__sizeOfVSOutput();")) {
					for (String name :uniforms.keySet()) {
						String type= uniforms.get(name);
						int index = uniformsMap.get(name);
						out.println("final "+type+" "+name+" "+"="+"("+type+")uniforms["+index+"];");
					}
					int vsOutputSize = sizeOfVSOutput();
					out.println("sizeOfVSOutput = "+vsOutputSize +";");
					bufferWriter.flush();
					writer.write(bufferWriter.toString());
					bufferWriter.close();
					bufferWriter=new StringWriter();
					out = new PrintWriter(bufferWriter);
				}else if (line.startsWith("//__vertex_shader_begin();")) {
					HashMap<String, String> map=new HashMap<>(attributes);
					map.putAll(varyings);
					for(String name : map.keySet()){
						String varType =map.get(name);
						typedef_str(name, varType, out);
					}
//					VertexAttribPointer[] vertexAttribPointers = TinyGL.vertexAttribPointers;
					for (String attrib : attribLocationsMap.keySet()) {
						int i= attribLocationsMap.get(attrib);
//					}
//					for (int i = 0; i < vertexAttribPointers.length; i++) {
//						VertexAttribPointer pointer= vertexAttribPointers[i];
//						String attrib = pointer.bindAttribLocation;
						if (attrib!=null&&map.get(attrib)!=null) {
							String varType= map.get(attrib);
							String s ="";
							s+="attribPointer0 = vertexAttribPointers["+i+"];";
							s+="if(attribPointer0.data!=null){\n";
							s+="offset0 = _i0*attribPointer0.size+attribPointer0.offset;";
							int vecSize = vec_size(varType);
							for (int k = 0; k < vecSize; k++)
								s+= attrib+(vecSize>1?property_str(k):"")+"="+"attribPointer0.data[offset0+" + k +"];";
							out.println(s);
							out.println("}");
						}
					}
//					for(String name : map.keySet()){
//						String varType =map.get(name);
//						define_var(name, varType,out);//TODO
//					}
					pushVertexShader(out, "v0_",vertexShaderMain.toString());
				} else if (line.startsWith("//__vertex_shader_end();")) {
					HashMap<String, String> map = vs_output_varyings();
					for(String key : map.keySet()){
						String varType =map.get(key);
						String s ="";
						int vecSize = vec_size(varType);
						for (int i = 0; i < vecSize; i++) {
							s+="VSOutputBuffers[outputId++]="+key+ (vecSize>1?property_str(i):"") +";";
						}
						out.println(s);
					}
				} else if (line.startsWith("//__perspectiveCorrectInterpolations;")) {
					for(String name : varyings.keySet()){
						String varType =varyings.get(name);
						typedef_str("v0_"+name, varType,out);
						typedef_str("v1_"+name, varType,out);
						typedef_str("v2_"+name, varType,out);
					}
					HashMap<String, String> allVaryings=varyings;
//					VertexAttribPointer[] vertexAttribPointers = TinyGL.vertexAttribPointers;
					for (String attrib : attribLocationsMap.keySet()) {
						if(varyings.get(attrib)==null) continue;
						int i= attribLocationsMap.get(attrib);
//					}
//					for (int i = 0; i < vertexAttribPointers.length; i++) {
//						VertexAttribPointer pointer= vertexAttribPointers[i];
//						String attrib = pointer.bindAttribLocation;
						if (attrib!=null&&allVaryings.get(attrib)!=null) {
							String varType= allVaryings.get(attrib);
							String s ="";
							s+="attribPointer0 = vertexAttribPointers["+i+"];";
							s+="if(attribPointer0.data!=null){\n";
							s+="offset0 = _i0*attribPointer0.size+attribPointer0.offset;";
							s+="offset1 = _i1*attribPointer0.size+attribPointer0.offset;";
							s+="offset2 = _i2*attribPointer0.size+attribPointer0.offset;";
							out.println(s);
							for (int j = 0; j < 3; j++) {
								int vecSize = vec_size(varType);
								s ="";
								String var = "";
								for (int k = 0; k < vecSize; k++) {
									if(vecSize>1)  var = property_str(k);
									s+="v"+j+"_"+attrib+ var +"="+"attribPointer0.data[offset"+j+" + "+k+"];";
								}
								out.println(s);
							}
							out.println("}");
						}
					}
					HashMap<String, String> map = vs_output_varyings();
					for(String key : map.keySet()){
						String varType =map.get(key);
						String s ="";//TODO
						String var = key;
						int vecSize = vec_size(varType);
						int v0_Offset=3,v1_Offset=3,v2_Offset=3;
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1)  var =key+ property_str(i);
							s+="v0_"+var+" = VSOutputBuffers[v0_Offset+"+(v0_Offset++)+"];";
							s+="v1_"+var+" = VSOutputBuffers[v1_Offset+"+(v1_Offset++)+"];";
							s+="v2_"+var+" = VSOutputBuffers[v2_Offset+"+(v2_Offset++)+"];";
						}
						out.println(s);
					}
				} else if (line.startsWith("//__ddx_ddy();")) {
					for(String key : varyings.keySet()){
						String varType =varyings.get(key);
						typedef_str("ddx_"+key, varType,out);
						typedef_str("ddy_"+key, varType,out);
						String s ="";
						String var = key;
						int vecSize = vec_size(varType);
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1)  var =key+ property_str(i);
							s+="v0_"+var+"*=v0_gl_FragDepth;";
							s+="v1_"+var+"*=v1_gl_FragDepth;";
							s+="v2_"+var+"*=v2_gl_FragDepth;";
						}
						out.println(s); s="";
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1)  var =key+ property_str(i);
							s+="fDelta0 = v1_"+var+" - v0_"+var+";";
							s+="fDelta1 = v2_"+var+" - v0_"+var+";";
							s+="fDdx = (fDelta0 * fDeltaY1- fDelta1 * fDeltaY0) * fCommonGradient;";
							s+="fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;";
							s+="ddx_"+var+" =fDdx;";
							s+="ddy_"+var+" =fDdy;";
						}
						out.println(s);
					}
					MipmapCodeGenerater.generaterCode(out);
					varyings.putAll(MipmapCodeGenerater.perspectiveCorrectInterpolations());
				}else if (line.startsWith("//__x0();")) {
					String s="";
					for(String key : varyings.keySet()){
						String varType =varyings.get(key);
						typedef_str("x0_"+key, varType,out);
						int vecSize = vec_size(varType);
						String var = key;
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1) var =key+ property_str(i);
							s+="x0_"+var+" = v0_"+var+" + ddx_"+var+" * fOffsetX + ddy_"+var+" * fOffsetY;";
						}
					}
					out.println(s);
				}else if (line.startsWith("//__add_ddx();")) {
					String s="";
					for(String key : varyings.keySet()){
						String varType =varyings.get(key);
						int vecSize = vec_size(varType);
						String var = key;
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1)  var =key+ property_str(i);
							s+="x0_"+var+" += ddx_"+var +",";
						}
					}
					out.println(s);
				}else if (line.startsWith("//__PSInput();")) {
					String s="";
					for(String key : varyings.keySet()){
						String varType =varyings.get(key);
						typedef_str(key, varType, out);
						int vecSize = vec_size(varType);
						String var = key;
						for (int i = 0; i < vecSize; i++) {
							if(vecSize>1)  var =key+ property_str(i);
							s+= var+" = x0_"+var+"*invW;";//TODO
						}
					}
					out.println(s);
				}else if (line.startsWith("//__depth_test();")) {
					switch (TinyGL.GLDepthFunc) {
					case 1:
						out.println("gl_FragDepth =invW; depthTestPass = dstFragDepth < gl_FragDepth;");
						break;
					default:
						out.println("gl_FragDepth =invW; depthTestPass = dstFragDepth > gl_FragDepth;");
						break;
					}
				}else if (line.startsWith("//__fragment_shader();")) {
//					for (String varName : varyings.keySet()) {
						//define_var(varName, varyings.get(varName), out); // TODO
//					}
					out.write(fragShaderMain.toString());
				}else if(line.startsWith("//__functionDefs")){
					out.println();
					out.println(functionDefs.toString());
				}
			}
			br.close();
			out.flush();
			out.close();
//			writer.write(definedWriter.toString());//write common defines 
			writer.write(bufferWriter.toString());// write codes
			System.out.println(writer.toString());
			// 2.开始编译
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			if (javaCompiler==null) {
				System.err.println("请在Build Path加入tools.jar");
			}
			JavaFileObject fileObject = new JavaStringObject("TinyGLImpl", writer.toString());
			CompilationTask task = javaCompiler.getTask(null, null, null, Arrays.asList("-d","./bin"), null, Arrays.asList(fileObject));
			boolean success = task.call();
			if (!success) {
			    System.out.println("编译失败");
			}else{
			    System.out.println("编译成功");
			}
			URL[] urls = new URL[]{new URL("file:/" + "./bin/")};
			URLClassLoader classLoader = new URLClassLoader(urls);
			Class<?> classl = classLoader.loadClass("soft3d.v1_0.TinyGLImpl");
			classLoader.close();
			Object[] argsl = {};
			TinyGL object= (TinyGL) classl.getConstructors()[0].newInstance(argsl);
			object.attribLocationsMap=attribLocationsMap;
			object.uniformsMap=uniformsMap;
			return  object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static final String[] property = {"x","y","z","w"};
//	public static HashMap<String, Object> definedVariables  = new HashMap<>();
//	static StringWriter definedWriter = new StringWriter(); // 内存字符串输出流
//	public static void clear_writer(){
//		writer = new StringWriter();
//		out = new PrintWriter(writer);
//	}
//	public static void define_scalar(String varName,String varType,PrintWriter out){
////		if(definedVariables.get(varName)!=null)
////			return;
//		definedVariables.put(varName, varType);
//		if (out==null){
//			definedWriter.write(VariableType.scalar_str(varName, varType) + "\n");
//		}else{
//			out.write(VariableType.scalar_str(varName, varType) + "\n");
//		}
//	}
//	public static void define_var(String varName,String varType,PrintWriter out){
//		if (out==null){
//			definedWriter.write(class_str(varName, varType) + ";\n");
//		}else{
//			out.write(class_str(varName, varType) + ";\n");
//		}
//	}
	public static HashMap<String, String> vs_output_varyings(){
		HashMap<String, String> map= new HashMap<>();
		for(String name : varyings.keySet()){
			if (attributes.get(name)==null) {
				map.put(name, varyings.get(name));
			}
		}
		return map;
	}
	public static HashMap<String, String> varyings=new HashMap<String, String>();
	public static LinkedHashMap<String, String> attributes=new LinkedHashMap<String, String>();
	public static HashMap<String,String> uniforms=new HashMap<String, String>();
	public static HashMap<String, Integer> uniformsMap;
	public static HashMap<String, Integer>attribLocationsMap;
	public static StringBuilder vertexShaderMain,fragShaderMain,functionDefs;
	public static void clear() {
		varyings.clear();
		attributes.clear();
		uniforms.clear();
		MipmapCodeGenerater.clear();
	}
	public static void createShader(String vertexShader, String fragShader) throws Exception{
		clear();
		DefineStatementParser.createShader(vertexShader, fragShader);
	}
	
}
