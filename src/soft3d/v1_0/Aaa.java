package soft3d.v1_0;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Aaa {
	public static void main(String[] s){
		float f=Float.MIN_NORMAL;
	    String aaa = "out[0] = f / aspect;\r\n" + 
	    		"		  out[1] = 0;\r\n" + 
	    		"		  out[2] = 0;\r\n" + 
	    		"		  out[3] = 0;\r\n" + 
	    		"		  out[4] = 0;\r\n" + 
	    		"		  out[5] = f;\r\n" + 
	    		"		  out[6] = 0;\r\n" + 
	    		"		  out[7] = 0;\r\n" + 
	    		"		  out[8] = 0;\r\n" + 
	    		"		  out[9] = 0;\r\n" + 
	    		"		  out[10] = (far + near) * nf;\r\n" + 
	    		"		  out[11] = -1;\r\n" + 
	    		"		  out[12] = 0;\r\n" + 
	    		"		  out[13] = 0;\r\n" + 
	    		"		  out[14] = 2 * far * near * nf;\r\n" + 
	    		"		  out[15] = 0;";
	    Pattern p=Pattern.compile("\\[ ?\\d(\\d?) ?\\]"); 
	    Matcher m=p.matcher(aaa); 
	    String ret = "";
	    int lastpos=0;
	    while(m.find()) { 
//	      System.out.println(m.group()); 
//	      System.out.print("start:"+m.start()); 
//	      System.out.println(" end:"+m.end()); 
	      String match=m.group();
	     int iii= Integer.parseInt( match.substring(1, match.length()-1).trim()  );
	     int row =  iii/4;int column =  iii%4;
	     match=".m"+column+row;
	     
	     ret+= aaa.substring(lastpos, m.start()).concat(match);
	     lastpos=m.end(); 
	 } 
	    System.out.print( ret  +   aaa.substring(lastpos));
	  }
}
