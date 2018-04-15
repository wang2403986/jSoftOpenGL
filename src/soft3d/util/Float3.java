package soft3d.util;

public class Float3 {

	public static float[] float3(float x,float y,float z) {
		float[] r={x,y,z};
		return r;
	}
	public static float[] add(float[] v0,float[] v1) {
		float[] r={v0[0]+v1[0],v0[1]+v1[1],v0[2]+v1[2]};
		return r;
		
	}
	public static float[] add(float[]...vs ) {
		float[] r={0,0,0};
		int len=vs.length;
		for (int i = 0; i < len; i++) {
			r[0]+=vs[i][0];
			r[1]+=vs[i][1];
			r[2]+=vs[i][2];
		}
		return r;
		
	}
	public static float[] sub(float[]v0, float[]v1) {
		float[] r={v0[0]-v1[0],v0[1]-v1[1],v0[2]-v1[2]};
		
		return r;
		
	}
	public static float dot(float[]v0, float[]v1) {
		float x0=v0[0],y0=v0[1],z0=v0[2];
		float x1=v1[0],y1=v1[1],z1=v1[2];
		return x0*x1+y0*y1+z0*z1;
	}
	
	public static float[] mul(float k,float[] v0) {
		float[] r={k*v0[0],k*v0[1],k*v0[2]};
		
		return r;
		
	}
}
