package soft3d.v1_0.compiler;

import static soft3d.v1_0.compiler.VariableType.typedef_str;
import static soft3d.v1_0.compiler.VariableType.vec_size;

import java.io.PrintWriter;
import java.util.HashMap;

public final class MipmapCodeGenerater {
  
  public static HashMap<String, Identifier> mipmap_levels=new HashMap<String, Identifier>();
  public static HashMap<String, Identifier> dFdxdy=new HashMap<String, Identifier>();
  public static void clear() {
    mipmap_levels.clear();
    dFdxdy.clear();
  }
  public static HashMap<String, String> perspectiveCorrectInterpolations() {
    HashMap<String, String> Interpolations = new HashMap<>();
    for (String key : mipmap_levels.keySet()) {
      Interpolations.put(key+"_mipmap", "float");
    }
    for (String key : dFdxdy.keySet()) {
    	String varType=dFdxdy.get(key).variableType;
        Interpolations.put(key+"_dFdx", varType);
        Interpolations.put(key+"_dFdy", varType);
      }
    return Interpolations;
  }
  public static void generaterCode(PrintWriter out) {
    
    for(String key : mipmap_levels.keySet()){
      String varType ="float";
      String s ="";
      int vecSize = vec_size("float");
      //typedef mipmap_levels
      String mipmap_level=""+key +"_mipmap";
      typedef_str("ddx_"+mipmap_level, "float",out);
      typedef_str("ddy_"+mipmap_level, "float",out);
      typedef_str("v0_"+mipmap_level,  "float",out);
      typedef_str("v1_"+mipmap_level, "float",out);
      typedef_str("v2_"+mipmap_level,"float",out);
      s+="\n";
      String code1="v0_"+key+"_mipmap=MipmapEXT.mipmapLevel("+mipmap_levels.get(key).objectValue+","
      +"v0_"+key+","
          + ("ddx_"+key)  +(",ddy_"+key)  + 
          ",v0_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);\n";
      s+=code1;
      code1="v1_"+key+"_mipmap=MipmapEXT.mipmapLevel("+mipmap_levels.get(key).objectValue+","
          +"v1_"+key+","
              + ("ddx_"+key)  +(",ddy_"+key)  + 
              ",v1_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);\n";
          s+=code1;
      code1="v2_"+key+"_mipmap=MipmapEXT.mipmapLevel("+mipmap_levels.get(key).objectValue+","
          +"v2_"+key+","
              + ("ddx_"+key)  +(",ddy_"+key)  + 
              ",v2_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);\n";
          s+=code1;

          String var = key + "_mipmap";
          for (int i = 0; i < vecSize; i++) {
            if(vecSize>1)  var =key+ ShaderCompiler.property_str(i);
            s+="fDelta0 = v1_"+var+" - v0_"+var+";";
            s+="fDelta1 = v2_"+var+" - v0_"+var+";";
            s+="fDdx = (fDelta0 * fDeltaY1- fDelta1 * fDeltaY0) * fCommonGradient;";
            s+="fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;";
            s+="ddx_"+var+" =fDdx;";
            s+="ddy_"+var+" =fDdy;";
          }
          s+="\n";
      out.println(s);
    }
 
    
    for(String key : dFdxdy.keySet()){
        String varType =dFdxdy.get(key).variableType;
        String s ="";
        int vecSize = vec_size(varType);
        //typedef mipmap_levels
        String mipmap_level=""+key +"_dFdx";
        typedef_str("ddx_"+mipmap_level, varType,out);
        typedef_str("ddy_"+mipmap_level, varType,out);
        typedef_str("v0_" +mipmap_level, varType,out);
        typedef_str("v1_" +mipmap_level, varType,out);
        typedef_str("v2_" +mipmap_level, varType,out);
        s+="\n"; String ddw = "ddx_gl_FragDepth";
        String code1="v0_"+key+"_dFdx=DFdxEXT.dFdx(" +"v0_"+key+","
            + ("ddx_"+key)  +(",ddy_"+key)  + 
            ",v0_gl_FragDepth,"+ddw+");\n";
        s+=code1;
        code1="v1_"+key+"_dFdx=DFdxEXT.dFdx(" +"v1_"+key+","
                + ("ddx_"+key)  +(",ddy_"+key)  + 
                ",v1_gl_FragDepth,"+ddw+");\n";
            s+=code1;
        code1="v2_"+key+"_dFdx=DFdxEXT.dFdx(" +"v2_"+key+","
                + ("ddx_"+key)  +(",ddy_"+key)  + 
                ",v2_gl_FragDepth,"+ddw+");\n";
            s+=code1;

            String var = ""+key +"_dFdx";
            for (int i = 0; i < vecSize; i++) {
              if(vecSize>1)  var =key+ ShaderCompiler.property_str(i);
              s+="fDelta0 = v1_"+var+" - v0_"+var+";";
              s+="fDelta1 = v2_"+var+" - v0_"+var+";";
              s+="fDdx = (fDelta0 * fDeltaY1- fDelta1 * fDeltaY0) * fCommonGradient;";
              s+="fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;";
              s+="ddx_"+var+" =fDdx;";
              s+="ddy_"+var+" =fDdy;";
            }
            s+="\n";
        out.println(s);
      }
    for(String key : dFdxdy.keySet()){
        String varType =dFdxdy.get(key).variableType;
        String s ="";
        int vecSize = vec_size(varType);
        //typedef mipmap_levels
        String mipmap_level=""+key +"_dFdy";
        typedef_str("ddx_"+mipmap_level, varType,out);
        typedef_str("ddy_"+mipmap_level, varType,out);
        typedef_str("v0_" +mipmap_level, varType,out);
        typedef_str("v1_" +mipmap_level, varType,out);
        typedef_str("v2_" +mipmap_level, varType,out);
        s+="\n"; String ddw = "ddy_gl_FragDepth";
        String code1="v0_"+key+"_dFdy=DFdxEXT.dFdx(" +"v0_"+key+","
            + ("ddx_"+key)  +(",ddy_"+key)  + 
            ",v0_gl_FragDepth,"+ddw+");\n";
        s+=code1;
        code1="v1_"+key+"_dFdy=DFdxEXT.dFdx(" +"v1_"+key+","
                + ("ddx_"+key)  +(",ddy_"+key)  + 
                ",v1_gl_FragDepth,"+ddw+");\n";
            s+=code1;
        code1="v2_"+key+"_dFdy=DFdxEXT.dFdx(" +"v2_"+key+","
                + ("ddx_"+key)  +(",ddy_"+key)  + 
                ",v2_gl_FragDepth,"+ddw+");\n";
            s+=code1;

            String var = ""+key +"_dFdy";
            for (int i = 0; i < vecSize; i++) {
              if(vecSize>1)  var =key+ ShaderCompiler.property_str(i);
              s+="fDelta0 = v1_"+var+" - v0_"+var+";";
              s+="fDelta1 = v2_"+var+" - v0_"+var+";";
              s+="fDdx = (fDelta0 * fDeltaY1- fDelta1 * fDeltaY0) * fCommonGradient;";
              s+="fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;";
              s+="ddx_"+var+" =fDdx;";
              s+="ddy_"+var+" =fDdy;";
            }
            s+="\n";
        out.println(s);
      }
}
}
