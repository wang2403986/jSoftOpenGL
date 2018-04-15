package soft3d.v1_0;

import soft3d.v1_0.types.*;

public class Vector3 {

	
	public static vec3 setLength(vec3 out, float l){

		float oldLength = GLM.length(out);

		if ( oldLength != 0 && l != oldLength  ) {		//�����ж�,���ԭ�������³��Ȳ����,����ԭ���Ȳ�Ϊ0.

			copy(out, GLM.mul(out, l / oldLength ));//����.multiplyScalar()����,�����³�����ԭ���ȵı�.
		}

		return out;		//���ذ��ղ���l(����)�����µ���ά����(x,y,z)ֵ.

	}
	public static vec3 copy (vec3 out, vec3 in){
		out.x=in.x;
		out.y=in.y;
		out.z=in.z;
		return out;
	}
	public static vec4 copy (vec4 out, vec4 in){
		out.x=in.x;
		out.y=in.y;
		out.z=in.z;
		out.w=in.w;
		return out;
	}
	public static vec2 copy (vec2 out, vec2 in){
		out.x=in.x;
		out.y=in.y;
		return out;
	}
	//1˳ʱ  
	  public static final float ccw(vec4 a, vec4 b, vec4 c) {  
	      float ax = a.x, ay = a.y;  
	      float bx = b.x, by = b.y;  
	      float cx = c.x, cy = c.y;  
	        
	      float v = (bx - ax) * (cy - ay) - (cx - ax) * (by - ay);  
	      return v;  
	  }
}
