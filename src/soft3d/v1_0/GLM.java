package soft3d.v1_0;

import static java.lang.Float.floatToRawIntBits;
import static java.lang.Float.intBitsToFloat;
import static java.lang.Math.abs;

import soft3d.Texture;
import soft3d.v1_0.types.*;

public final class GLM {
	
	public static mat4 mat4() {
		return new mat4();
	}
	public static vec2 vec2(float x,float y) {
		final vec2 v = new vec2();
		v.x=x;v.y=y;
		return v;
	}
	public static vec3 vec3(float x, float y, float z) {
		final vec3 v =new vec3();
		v.x = x;
		v.y = y;
		v.z = z;
		return v;
	}
	public static vec3 vec3(vec4 a) {
		final vec3 r =new vec3();
		r.x = a.x;
		r.y = a.y;
		r.z = a.z;
		return r;
	}
	public static vec4 vec4(float x, float y, float z, float w) {
		final vec4 v =new vec4();
		v.x = x;
		v.y = y;
		v.z = z;
		v.w = w;
		return v;
	}
	public static vec4 vec4(vec3 a, float w) {
		final vec4 v =new vec4();
		v.x = a.x;
		v.y = a.y;
		v.z = a.z;
		v.w = w;
		return v;
	}
	public static ivec4 ivec4(int x, int y, int z, int w) {
		final ivec4 v =new ivec4();
		v.x = x;
		v.y = y;
		v.z = z;
		v.w = w;
		return v;
	}
	public final static ivec4 texture2D(final Texture sampler,vec2 tex) {
		final ivec4 v =new ivec4();
		sampler.nearestFilter(v, tex.x,tex.y);
//		if (sampler ==null ) return v;
//		int iX=(int) (tex.x*sampler.widthSub1+0.5f);
//		int iY=(int) (tex.y*sampler.heightSub1+0.5f);
//		int index = iY*sampler.width+iX;
//		if(index >= sampler.intData.length || index<0) return v;
//		int rgba=sampler.intData[index];
//		v.x=rgba >>> 24;
//		v.y=(rgba >> 16) &0xff;
//		v.z=(rgba >> 8) &0xff;
//		v.w=rgba & 0xff;
		return v;
	}

	static final int FACE_POS_X = 0, FACE_NEG_X = 1, FACE_POS_Y = 2,
			FACE_NEG_Y = 3, FACE_POS_Z = 4, FACE_NEG_Z = 5;
	public static ivec4 textureCube(Texture[] texObj, vec3 texcoord) {
		
		float rx = texcoord.x;
		float ry = texcoord.y;
		float rz = texcoord.z;
		float arx = abs(rx), ary = abs(ry), arz = abs(rz);
		int face;
		float sc, tc, ma;
		if (arx >= ary && arx >= arz) {
			if (rx >= 0.0F) {
				face = FACE_POS_X;
				sc = -rz;
				tc = -ry;
				ma = arx;
			} else {
				face = FACE_NEG_X;
				sc = rz;
				tc = -ry;
				ma = arx;
			}
		} else if (ary >= arx && ary >= arz) {
			if (ry >= 0.0F) {
				face = FACE_POS_Y;
				sc = rx;
				tc = rz;
				ma = ary;
			} else {
				face = FACE_NEG_Y;
				sc = rx;
				tc = -rz;
				ma = ary;
			}
		} else {
			if (rz > 0.0F) {
				face = FACE_POS_Z;
				sc = rx;
				tc = -ry;
				ma = arz;
			} else {
				face = FACE_NEG_Z;
				sc = -rx;
				tc = -ry;
				ma = arz;
			}
		}
		final vec2 tex=new vec2();
		float ima = 1.0F / ma;
		tex.x = (sc * ima + 1.0F) * 0.5F;
		tex.y = (tc * ima + 1.0F) * 0.5F;
		return texture2D(texObj[face], tex);
	}
	public static final ivec4 clamp(final ivec4 a) {
		final ivec4 v = new ivec4();
		if (a.w < 0)
			v.w = 0;
		else if (a.w > 255)
			v.w = 255;
		else
			v.w = a.w;
		
		if (a.z < 0)
			v.z = 0;
		else if (a.z > 255)
			v.z = 255;
		else
			v.z = a.z;
		
		if (a.x < 0)
			v.x = 0;
		else if (a.x > 255)
			v.x = 255;
		else
			v.x = a.x;
		
		if (a.y < 0)
			v.y = 0;
		else if (a.y > 255)
			v.y = 255;
		else
			v.y = a.y;
		return v;
	}
	public static int add(int x,int y) {
		return x+y;
	}
	public static int sub(int x,int y) {
		return x-y;
	}
	public static int div(int x,int y) {
		return x/y;
	}
	public static float add(float x,float y) {
		return x+y;
	}
	public static float sub(float x,float y) {
		return x-y;
	}
	public static float mul(float x,float y) {
		return x*y;
	}
	public static float div(float x,float y) {
		return x/y;
	}

	public static final ivec4 add(final ivec4 a,final ivec4 b) {
		final ivec4 r =new ivec4();
		r.x = a.x+ b.x;r.y = a.y+ b.y;
		r.z = a.z+ b.z;r.w = a.w+ b.w;
		return r;
	}
	public static vec3 mul(mat4 m,vec3 v){
		final vec3 _this = new vec3();
		float x = v.x, y = v.y, z = v.z;
		mat4 e = m;

		_this.x = e.m00 * x + e.m01 * y + e.m02 * z + e.m03 ;
		_this.y = e.m10 * x + e.m11 * y + e.m12 * z + e.m13 ;
		_this.z = e.m20 * x + e.m21 * y + e.m22 * z + e.m23 ;

		return _this;
	}
	public static vec4 mul(mat4 m, vec4 v) { 
		final vec4 _this = new vec4();
		float x = v.x, y = v.y, z = v.z,w = v.w;
		mat4 e = m;

		_this.x = e.m00 * x + e.m01 * y + e.m02 * z + e.m03 * w;
		_this.y = e.m10 * x + e.m11 * y + e.m12 * z + e.m13 * w;
		_this.z = e.m20 * x + e.m21 * y + e.m22 * z + e.m23 * w;
		_this.w = e.m30 * x + e.m31 * y + e.m32 * z + e.m33 * w;

		return _this;
    } 
	public static mat4 mul(float scalar,mat4 a) { 
		final mat4 result = new mat4(); 
        result.m00 = a.m00 * scalar; 
        result.m10 = a.m10 * scalar; 
        result.m20 = a.m20 * scalar; 
        result.m30 = a.m30 * scalar; 
        result.m01 = a.m01 * scalar; 
        result.m11 = a.m11 * scalar; 
        result.m21 = a.m21 * scalar; 
        result.m31 = a.m31 * scalar; 
        result.m02 = a.m02 * scalar; 
        result.m12 = a.m12 * scalar; 
        result.m22 = a.m22 * scalar; 
        result.m32 = a.m32 * scalar; 
        result.m03 = a.m03 * scalar; 
        result.m13 = a.m13 * scalar; 
        result.m23 = a.m23 * scalar; 
        result.m33 = a.m33 * scalar; 
        return result; 
    }
	/**
	 * The second parameters are first applied to the vertex
	 */
	public static mat4 mul(mat4 a,mat4 b) {
		final mat4 ae = a;
		final mat4 be = b;
		final mat4 te = new mat4();

		float a11 = ae.m00, a12 = ae.m01, a13 = ae.m02, a14 = ae.m03;
		float a21 = ae.m10, a22 = ae.m11, a23 = ae.m12, a24 = ae.m13;
		float a31 = ae.m20, a32 = ae.m21, a33 = ae.m22, a34 = ae.m23;
		float a41 = ae.m30, a42 = ae.m31, a43 = ae.m32, a44 = ae.m33;

		float b11 = be.m00, b12 = be.m01, b13 = be.m02, b14 = be.m03;
		float b21 = be.m10, b22 = be.m11, b23 = be.m12, b24 = be.m13;
		float b31 = be.m20, b32 = be.m21, b33 = be.m22, b34 = be.m23;
		float b41 = be.m30, b42 = be.m31, b43 = be.m32, b44 = be.m33;

		te.m00 = a11 * b11 + a12 * b21 + a13 * b31 + a14 * b41;
		te.m01 = a11 * b12 + a12 * b22 + a13 * b32 + a14 * b42;
		te.m02 = a11 * b13 + a12 * b23 + a13 * b33 + a14 * b43;
		te.m03 = a11 * b14 + a12 * b24 + a13 * b34 + a14 * b44;

		te.m10 = a21 * b11 + a22 * b21 + a23 * b31 + a24 * b41;
		te.m11 = a21 * b12 + a22 * b22 + a23 * b32 + a24 * b42;
		te.m12 = a21 * b13 + a22 * b23 + a23 * b33 + a24 * b43;
		te.m13 = a21 * b14 + a22 * b24 + a23 * b34 + a24 * b44;

		te.m20 = a31 * b11 + a32 * b21 + a33 * b31 + a34 * b41;
		te.m21 = a31 * b12 + a32 * b22 + a33 * b32 + a34 * b42;
		te.m22 = a31 * b13 + a32 * b23 + a33 * b33 + a34 * b43;
		te.m23 = a31 * b14 + a32 * b24 + a33 * b34 + a34 * b44;

		te.m30 = a41 * b11 + a42 * b21 + a43 * b31 + a44 * b41;
		te.m31 = a41 * b12 + a42 * b22 + a43 * b32 + a44 * b42;
		te.m32 = a41 * b13 + a42 * b23 + a43 * b33 + a44 * b43;
		te.m33 = a41 * b14 + a42 * b24 + a43 * b34 + a44 * b44;

		return te;

    }
	public static mat4 transpose(mat4 a) {
        final mat4 result = new mat4(); 
 
        result.m00 = a.m00; 
        result.m10 = a.m01; 
        result.m20 = a.m02; 
        result.m30 = a.m03; 
 
        result.m01 = a.m10; 
        result.m11 = a.m11; 
        result.m21 = a.m12; 
        result.m31 = a.m13; 
 
        result.m02 = a.m20; 
        result.m12 = a.m21; 
        result.m22 = a.m22; 
        result.m32 = a.m23; 
 
        result.m03 = a.m30; 
        result.m13 = a.m31; 
        result.m23 = a.m32; 
        result.m33 = a.m33; 
 
        return result; 
    }
	public mat4 add(mat4 a,mat4 other) {
        final mat4 result = new mat4(); 
 
        result.m00 = a.m00 + other.m00; 
        result.m10 = a.m10 + other.m10; 
        result.m20 = a.m20 + other.m20; 
        result.m30 = a.m30 + other.m30; 
 
        result.m01 = a.m01 + other.m01; 
        result.m11 = a.m11 + other.m11; 
        result.m21 = a.m21 + other.m21; 
        result.m31 = a.m31 + other.m31; 
 
        result.m02 = a.m02 + other.m02; 
        result.m12 = a.m12 + other.m12; 
        result.m22 = a.m22 + other.m22; 
        result.m32 = a.m32 + other.m32; 
 
        result.m03 = a.m03 + other.m03; 
        result.m13 = a.m13 + other.m13; 
        result.m23 = a.m23 + other.m23; 
        result.m33 = a.m33 + other.m33; 
 
        return result; 
    } 
	public static vec4 lerp(vec4 a,vec4 other, float alpha) {
        return add(mul((1f - alpha),a) , mul(alpha, other));
    }
	public static vec3 lerp(vec3 a,vec3 other, float alpha) {
        return add(mul((1f - alpha),a) , mul(alpha, other));
    }
	public static float lengthSquared(vec3 a) {
        return a.x * a.x + a.y * a.y + a.z * a.z; 
    } 
	public static float length(vec3 vec) {
		return (float) Math.sqrt(lengthSquared(vec));
	}
	public static vec4 mul(float k,vec4 v0) {
		final vec4 v =new vec4();
		float x=v0.x, y=v0.y, z=v0.z, w=v0.w;;
		v.x=x*k;
		v.y=y*k;
		v.z=z*k;
		v.w=w*k;
		return v;
	}
	public static vec3 mul(vec3 v0,vec3 v1) {
		final vec3 v =new vec3();
		float x=v0.x, y=v0.y, z=v0.z;
		v.x=x*v1.x;
		v.y=y*v1.y;
		v.z=z*v1.z;
		return v;
	}
	public static vec3 mul(float k,vec3 v0) {
		final vec3 v =new vec3();
		float x=v0.x, y=v0.y, z=v0.z;
		v.x=x*k;
		v.y=y*k;
		v.z=z*k;
		return v;
	}
	public static vec3 mul(vec3 v0, float k) {
		final vec3 v =new vec3();
		float x=v0.x, y=v0.y, z=v0.z;
		v.x=x*k;
		v.y=y*k;
		v.z=z*k;
		return v;
	}
	public static vec3 sub(vec3 a, vec3 b) {
		final vec3 v =new vec3();
		v.x = a.x-b.x;
		v.y =  a.y-b.y;
		v.z =  a.z-b.z;
		return v;
	}
	
	public static vec3 add(vec3 a, vec3 b) {
		final vec3 v =new vec3();
		v.x = a.x+b.x;
		v.y =  a.y+b.y;
		v.z =  a.z+b.z;
		return v;
	}
	public static vec4 add(vec4 a, vec4 b) {
		final vec4 v =new vec4();
		v.x = a.x+b.x;
		v.y =  a.y+b.y;
		v.z =  a.z+b.z;
		v.w =  a.w+b.w;
		return v;
	}
	public static vec4 sub(vec4 a, vec4 b) {
		final vec4 v =new vec4();
		v.x = a.x-b.x;
		v.y =  a.y-b.y;
		v.z =  a.z-b.z;
		v.w =  a.w-b.w;
		return v;
	}
	public static vec3 normalize(vec3 a) {
		final vec3 v =new vec3();
		float x = a.x, y = a.y, z = a.z;
		float r_m = x * x + y * y + z * z;
		float xhalf = 0.5f * r_m;
		int i = floatToRawIntBits(r_m); // get bits for floating VALUE
		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
		r_m = intBitsToFloat(i); // convert bits BACK to float
		r_m = r_m * (1.5f - xhalf * r_m * r_m); // Newton step, repeating increases accuracy
		v.x = x*r_m;
		v.y = y*r_m;
		v.z = z*r_m;
		return v;
	}
	public static vec3 cross(vec3 a, vec3 b) {
		final vec3 _this = new vec3();
		float ax = a.x, ay = a.y, az = a.z;
		float bx = b.x, by = b.y, bz = b.z;

		_this.x = ay * bz - az * by;
		_this.y = az * bx - ax * bz;
		_this.z = ax * by - ay * bx;

		return _this;
	}
	public static float dot(vec3 _this, vec3 v) {
		return _this.x * v.x + _this.y * v.y + _this.z * v.z;
	}
	public static vec3 reflect (vec3 I, vec3 N){
		final vec3 R =new vec3();
		float dot=dot(I,N);
		if (dot<0)
			dot=0;
		R.x=I.x-2*dot*N.x;
		R.y=I.y-2*dot*N.y;
		R.z=I.z-2*dot*N.z;
		return R;
	}
	public static vec2 add(vec2 a,vec2 b){
		final vec2 r = new vec2();
		r.x=a.x+b.x;
		r.y=a.y+b.y;
		return r;
	}
	public static vec2 mul(vec2 a,float b){
		final vec2 r = new vec2();
		r.x=a.x*b;
		r.y=a.y*b;
		return r;
	}
	public static vec2 mul(vec2 a,vec2 b){
		final vec2 r = new vec2();
		r.x=a.x*b.x;
		r.y=a.y*b.y;
		return r;
	}
	public static vec2 sub(vec2 a,vec2 b){
		final vec2 r = new vec2();
		r.x=a.x-b.x;
		r.y=a.y-b.y;
		return r;
	}
}