package soft3d;

import static java.lang.Float.intBitsToFloat;
import static java.lang.Float.floatToRawIntBits;
//import static java.lang.Float.floatToIntBits;
/**
 * Vector3
 * @author Administrator
 *
 */
public final class Vec3 {
	public float x,y,z;
	public final void set(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public static final Vec3 vec3(float x,float y,float z) {
		Vec3 vec3=new Vec3();
		vec3.x=x;
		vec3.y=y;
		vec3.z=z;
		return vec3;
	}
	public static final void add(Vec3 v0,Vec3 v1,Vec3 out) {
		out.x=v0.x+v1.x;
		out.y=v0.y+v1.y;
		out.z=v0.z+v1.z;
		
	}
	public static final void sub(Vec3 v0,Vec3 v1,Vec3 out) {
		out.x=v0.x-v1.x;
		out.y=v0.y-v1.y;
		out.z=v0.z-v1.z;
		
	}
	public static final float dot(Vec3 vector0, Vec3 vector1) {
		float x0 = vector0.x, y0 = vector0.y, z0 = vector0.z;
		float x1 = vector1.x, y1 = vector1.y, z1 = vector1.z;
		return x0 * x1 + y0 * y1 + z0 * z1;
	}
	public static final void cross(Vec3 vector1,Vec3 vector2,
			Vec3 result) {
		float x0 = vector1.x, y0 = vector1.y, z0 = vector1.z;
		float x1 = vector2.x, y1 = vector2.y, z1 = vector2.z;
		result.x = y0 * z1 - z0 * y1;
		result.y = z0 * x1 - x0 * z1;
		result.z = x0 * y1 - y0 * x1;
	}
	public static final void normalize(Vec3 v) {
		float x = v.x, y = v.y, z = v.z;
		float r_m = x * x + y * y + z * z;
		float xhalf = 0.5f * r_m;
		int i = floatToRawIntBits(r_m); // get bits for floating VALUE
		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
		r_m = intBitsToFloat(i); // convert bits BACK to float
		r_m = r_m * (1.5f - xhalf * r_m * r_m); // Newton step, repeating increases accuracy
		v.x = x*r_m;
		v.y = y*r_m;
		v.z = z*r_m;
	}
	/**
	 * 更为精确的归一化方法 More accurate method
	 * @param norm
	 */
	public static final void normalize1(Vec3 norm) {
		float x= norm.x,y=norm.y,z=norm.z;
		float r_m=(float) (1d/Math.sqrt(x*x+y*y+z*z));
        norm.x=x*r_m;
	    norm.y=y*r_m;
	    norm.z=z*r_m;
	}
	/**
	 * 归一化(Fast)
	 * @param vector
	 * @return
	 */
	public static final void normalize(float[] vector) {
		float x = vector[0], y = vector[1], z = vector[2];
		float r_m = x * x + y * y + z * z;
		float xhalf = 0.5f * r_m;
		int i = floatToRawIntBits(r_m); // get bits for floating VALUE
		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
		r_m = intBitsToFloat(i); // convert bits BACK to float
		r_m = r_m * (1.5f - xhalf * r_m * r_m); // Newton step, repeating increases accuracy
		vector[0] = x*r_m;
		vector[1] = y*r_m;
		vector[2] = z*r_m;
	}
	/**
	 * 更为精确的归一化方法 More accurate method
	 * @param norm
	 */
	public static final void normalize1(float[] norm) {
		float x= norm[0],y=norm[1],z=norm[2];
		double base=Math.sqrt(x*x+y*y+z*z);
		float r_m=(float) (1d/base);
        norm[0]=x*r_m;
	    norm[1]=y*r_m;
	    norm[2]=z*r_m;
//	    norm[0]/=base;
//	    norm[1]/=base;
//	    norm[2]/=base;
	}
	/**
	 * 归一化数组中所有向量(Fast)
	 * @param vector
	 */
	public static final void normalizeAll(float[] vector) {
		int len = vector.length;
		int index = 0;
		while (index < len) {
			float x = vector[index], y = vector[index + 1], z = vector[index + 2];
			float r_m = x * x + y * y + z * z;
			float xhalf = 0.5f * r_m;
			int i = floatToRawIntBits(r_m); // get bits for floating VALUE
			i = 0x5f375a86 - (i >> 1); // gives initial guess y0
			r_m = intBitsToFloat(i); // convert bits BACK to float
			r_m = r_m * (1.5f - xhalf * r_m * r_m); // Newton step, repeating increases accuracy
			vector[index] = x*r_m;
			vector[index + 1] = y*r_m;
			vector[index + 2] = z*r_m;
			index += 3;
		}
	}

	/**
	 * cross Product
	 * @param vector1
	 * @param vector2
	 * @param result
	 */
	public static final void cross(float[] vector1, float[] vector2,
			float[] result) {
		float x0 = vector1[0], y0 = vector1[1], z0 = vector1[2];
		float x1 = vector2[0], y1 = vector2[1], z1 = vector2[2];
		result[0] = y0 * z1 - z0 * y1;
		result[1] = z0 * x1 - x0 * z1;
		result[2] = x0 * y1 - y0 * x1;
	}
	public static final float[] cross(float[] vector1, float[] vector2) {
		float x0 = vector1[0], y0 = vector1[1], z0 = vector1[2];
		float x1 = vector2[0], y1 = vector2[1], z1 = vector2[2];
		float[] result={0,0,0};
		result[0] = y0 * z1 - z0 * y1;
		result[1] = z0 * x1 - x0 * z1;
		result[2] = x0 * y1 - y0 * x1;
		return result;
	}
	
	/**
	 * v0 + v1
	 * @param v0
	 * @param v1
	 * @return v0 + v1
	 */
	public static float[] add(float[] v0,float[] v1) {
		float[] r= {v0[0]+v1[0],v0[1]+v1[1],v0[2]+v1[2]};
		return r;
	}
	/**
	 * 连加 Repeated addition
	 * @param vs
	 * @return vs[0]+vs[1]+vs[2]+...
	 */
	public static float[] add(float[]... vs) {
		
		float[] r={0,0,0};
		int length=vs.length;
		for (int i = 0; i < length; i++) {
			r[0]+=vs[i][0];
			r[1]+=vs[i][1];
			r[2]+=vs[i][2];
		}
		
		return r;
	}
	/**
	 * v0 / k
	 * @param v0
	 * @param k
	 * @return v0 / k
	 */
	public static float[] div(float[] v0,double k) {
		float[] r= {(float) (v0[0]/k),(float) (v0[1]/k),(float) (v0[2]/k)};
		return r;
	}
	/**
	 * v0 * v1
	 * @param v0
	 * @param v1
	 * @return	v0 * v1
	 */
	public static float dot(float[] v0,float[] v1) {
		float x0 = v0[0], y0 = v0[1], z0 = v0[2];
		float x1 = v1[0], y1 = v1[1], z1 = v1[2];
		return x0 * x1 + y0 * y1 + z0 * z1;
	}
	
	/**
	 * v0 * k
	 * @param v0
	 * @param k
	 * @return v0 * k
	 */
	public static float[] mul(float[] v0,double k) {
		float x=v0[0],y=v0[1],z=v0[2];
		float[] r={(float) (x*k),(float) (y*k),(float) (z*k)};
		return r;
	}
	/**
	 * v0 * k
	 * @param v0
	 * @param k
	 * @return v0 * k
	 */
	public static float[] mul(double k,float[] v0) {
		float x=v0[0],y=v0[1],z=v0[2];
		float[] r={(float) (x*k),(float) (y*k),(float) (z*k)};
		return r;
	}
	/**
	 * -v0
	 * @param v0
	 * @return -v0
	 */
	public static float[] neg(float[] v0) {
		float x=v0[0],y=v0[1],z=v0[2];
		float[] r={-x,-y,-z};
		return r;
	}
	/**
	 * v0 - v1
	 * @param v0
	 * @param v1
	 * @return	v0 - v1
	 */
	public static float[] sub(float[] v0,float[] v1) {
		float[] r={v0[0]-v1[0],v0[1]-v1[1],v0[2]-v1[2]};
		return r;
	}
	/**
	 * get Length of vector
	 * @param v0
	 * @return sqrt(x * x + y * y + z * z)
	 */
	public static double getLength(float[] v0)
	{
		float x=v0[0], y=v0[1], z=v0[2];
		return Math.sqrt(x * x + y * y + z * z);
	}
	/**
	 * get Squared Length of vector
	 * @param v0
	 * @return x * x + y * y + z * z
	 */
	public static double getSquaredLength(float[] v0)
	{
		float x=v0[0], y=v0[1], z=v0[2];
		return (x * x + y * y + z * z);
	}
}
