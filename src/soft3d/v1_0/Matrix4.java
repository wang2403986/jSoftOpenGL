package soft3d.v1_0;

import static soft3d.v1_0.GLM.*;

import soft3d.v1_0.types.*;
/**
 * 行序(row-major)存储
 */
public final class Matrix4 {
	public static final float EPSILON = 0.000001f;
	public static float[][] copy(float[][] out,mat4 src) {
		out[0][0]=src.m00; out[0][1]=src.m10; out[0][2]=src.m20; out[0][3]=src.m30;
		out[1][0]=src.m01; out[1][1]=src.m11; out[1][2]=src.m21; out[1][3]=src.m31;
		out[2][0]=src.m02; out[2][1]=src.m12; out[2][2]=src.m22; out[2][3]=src.m32;
		out[3][0]=src.m03; out[3][1]=src.m13; out[3][2]=src.m23; out[3][3]=src.m33;
		return out;
	}
//	public static float[][] copy(float[][] out,mat4 src){
//		out[0][0]=src.m00; out[0][1]=src.m01; out[0][2]=src.m02; out[0][3]=src.m03;
//		out[1][0]=src.m10; out[1][1]=src.m11; out[1][2]=src.m12; out[1][3]=src.m13;
//		out[2][0]=src.m20; out[2][1]=src.m21; out[2][2]=src.m22; out[2][3]=src.m23;
//		out[3][0]=src.m30; out[3][1]=src.m31; out[3][2]=src.m32; out[3][3]=src.m33;
//		return out;
//	}
	public static mat4 identity (mat4 _this) {

		set(_this,

			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1

		);

		return _this;	//返回4x4的单位矩阵

	}
	public static mat4 copy(mat4 dst,mat4  src) {
		dst.m00=src.m00; dst.m01=src.m01; dst.m02=src.m02; dst.m03=src.m03;
		dst.m10=src.m10; dst.m11=src.m11; dst.m12=src.m12; dst.m13=src.m13;
		dst.m20=src.m20; dst.m21=src.m21; dst.m22=src.m22; dst.m23=src.m23;
		dst.m30=src.m30; dst.m31=src.m31; dst.m32=src.m32; dst.m33=src.m33;
		return dst;
	}

	public static void set(mat4 dst,float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13,
			float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		dst.m00=m00; dst.m01=m01; dst.m02=m02; dst.m03=m03;
		dst.m10=m10; dst.m11=m11; dst.m12=m12; dst.m13=m13;
		dst.m20=m20; dst.m21=m21; dst.m22=m22; dst.m23=m23;
		dst.m30=m30; dst.m31=m31; dst.m32=m32; dst.m33=m33;
	}
	public static mat4 translate  (mat4 _this,float x,float y,float z ) {
		set(_this,
			1, 0, 0, x,
			0, 1, 0, y,
			0, 0, 1, z,
			0, 0, 0, 1
		);

		return _this;	//返回Matrix4(4x4矩阵),平移矩阵

	}
	public static mat4 scale (mat4 _this,float x,float y,float z) {

		set(_this,

			x, 0, 0, 0,
			0, y, 0, 0,
			0, 0, z, 0,
			0, 0, 0, 1

		);

		return _this;	//返回Matrix4(4x4矩阵),缩放矩阵.

	}
	public static mat4 rotateY  (mat4 _this,float theta ) {

		float c = (float) Math.cos( theta ), s = (float) Math.sin( theta );

		set(_this,

			 c, 0, s, 0,
			 0, 1, 0, 0,
			- s, 0, c, 0,
			 0, 0, 0, 1

		);

		return _this;	//返回Matrix4(4x4矩阵),旋转矩阵.

	}
	public static mat4 rotateAxis (mat4  _this,vec3 axis,float angle ) {

		// Based on http://www.gamedev.net/reference/articles/article1199.asp

		float c = (float) Math.cos( angle );
		float s = (float) Math.sin( angle );
		float t = 1 - c;
		float x = axis.x, y = axis.y, z = axis.z;
		float tx = t * x, ty = t * y;

		set(_this,

			tx * x + c, tx * y - s * z, tx * z + s * y, 0,
			tx * y + s * z, ty * y + c, ty * z - s * x, 0,
			tx * z - s * y, ty * z + s * x, t * z * z + c, 0,
			0, 0, 0, 1

		);

		 return _this;	//返回Matrix4(4x4矩阵),旋转矩阵.

	}
	/*
	///Frustum方法根据left, right, bottom, top, near, far生成透视投影矩阵,Frustum平截头体
	*/
	///<summary>makeFrustum</summary>
	///<param name ="left" type="Number">指明相对于垂直平面的左侧坐标位置</param>
	///<param name ="right" type="Number">指明相对于垂直平面的右侧坐标位置</param>
	///<param name ="bottom" type="Number">指明相对于垂直平面的底部坐标位置</param>
	///<param name ="top" type="Number">指明相对于垂直平面的顶部坐标位置</param>
	///<param name ="near" type="Number">指明相对于深度剪切面的近的距离，必须为正数</param>
	///<param name ="far" type="Number">指明相对于深度剪切面的远的距离，必须为正数</param>
	///<returns type="Matrix4">返回Matrix4(4x4矩阵),透视投影矩阵.</returns>
	public static mat4 frustum (mat4 m,float left,float right,float bottom,float top,float near,float far ) {

		mat4 te = m;
		float x = 2 * near / ( right - left );
		float y = 2 * near / ( top - bottom );

		float a = ( right + left ) / ( right - left );
		float b = ( top + bottom ) / ( top - bottom );
		float c = - ( far + near ) / ( far - near );
		float d = - 2 * far * near / ( far - near );

		te.m00 = x;	te.m01 = 0;	te.m02 = a;	te.m03 = 0;
		te.m10 = 0;	te.m11 = y;	te.m12 = b;	te.m13 = 0;
		te.m20 = 0;	te.m21 = 0;	te.m22 = c;	te.m23 = d;
		te.m30 = 0;	te.m31 = 0;	te.m32 = - 1;	te.m33 = 0;

		return m;

	}
	/*
	///Perspective方法根据 fov, aspect, near, far 生成透视投影矩阵,对makeFrustu()方法的封装,适配人们习惯的表达方式.
	*/
	///<summary>makePerspective</summary>
	///<param name ="fov" type="Number">指明相机的可视角度</param>
	///<param name ="aspect" type="Number">指明相机可视范围的长宽比</param>
	///<param name ="near" type="Number">指明相对于深度剪切面的近的距离，必须为正数</param>
	///<param name ="far" type="Number">指明相对于深度剪切面的远的距离，必须为正数</param>
	///<returns type="Matrix4">返回Matrix4(4x4矩阵),透视投影矩阵.</returns>
	public static mat4 perspective(mat4 out, float fov, float aspect, float near, float far) {
		double fovy = Math.toRadians(fov);
		float f = 1.f / (float)Math.tan(fovy / 2);
		float nf = 1.f / (near - far);
		out.m00 = f / aspect;
		  out.m10 = 0;
		  out.m20 = 0;
		  out.m30 = 0;
		  out.m01 = 0;
		  out.m11 = f;
		  out.m21 = 0;
		  out.m31 = 0;
		  out.m02 = 0;
		  out.m12 = 0;
		  out.m22 = (far + near) * nf;
		  out.m32 = -1;
		  out.m03 = 0;
		  out.m13 = 0;
		  out.m23 = 2 * far * near * nf;
		  out.m33 = 0;
		  return out;
    }
	/*
	///getInverse方法用来获得Matrix4(4x4矩阵)的逆矩阵.
	/// NOTE:逆矩阵与当前矩阵相乘得到单位矩阵.
	*/
	public static float[] getInverse (float[] _this,  float[] m ) {

		// based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
		float[] te = _this;
		float[] me = m;

		float n11 = me[ 0 ], n12 = me[ 4 ], n13 = me[ 8 ], n14 = me[ 12 ];
		float n21 = me[ 1 ], n22 = me[ 5 ], n23 = me[ 9 ], n24 = me[ 13 ];
		float n31 = me[ 2 ], n32 = me[ 6 ], n33 = me[ 10 ], n34 = me[ 14 ];
		float n41 = me[ 3 ], n42 = me[ 7 ], n43 = me[ 11 ], n44 = me[ 15 ];

		te[ 0 ] = n23 * n34 * n42 - n24 * n33 * n42 + n24 * n32 * n43 - n22 * n34 * n43 - n23 * n32 * n44 + n22 * n33 * n44;
		te[ 4 ] = n14 * n33 * n42 - n13 * n34 * n42 - n14 * n32 * n43 + n12 * n34 * n43 + n13 * n32 * n44 - n12 * n33 * n44;
		te[ 8 ] = n13 * n24 * n42 - n14 * n23 * n42 + n14 * n22 * n43 - n12 * n24 * n43 - n13 * n22 * n44 + n12 * n23 * n44;
		te[ 12 ] = n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33 + n13 * n22 * n34 - n12 * n23 * n34;
		te[ 1 ] = n24 * n33 * n41 - n23 * n34 * n41 - n24 * n31 * n43 + n21 * n34 * n43 + n23 * n31 * n44 - n21 * n33 * n44;
		te[ 5 ] = n13 * n34 * n41 - n14 * n33 * n41 + n14 * n31 * n43 - n11 * n34 * n43 - n13 * n31 * n44 + n11 * n33 * n44;
		te[ 9 ] = n14 * n23 * n41 - n13 * n24 * n41 - n14 * n21 * n43 + n11 * n24 * n43 + n13 * n21 * n44 - n11 * n23 * n44;
		te[ 13 ] = n13 * n24 * n31 - n14 * n23 * n31 + n14 * n21 * n33 - n11 * n24 * n33 - n13 * n21 * n34 + n11 * n23 * n34;
		te[ 2 ] = n22 * n34 * n41 - n24 * n32 * n41 + n24 * n31 * n42 - n21 * n34 * n42 - n22 * n31 * n44 + n21 * n32 * n44;
		te[ 6 ] = n14 * n32 * n41 - n12 * n34 * n41 - n14 * n31 * n42 + n11 * n34 * n42 + n12 * n31 * n44 - n11 * n32 * n44;
		te[ 10 ] = n12 * n24 * n41 - n14 * n22 * n41 + n14 * n21 * n42 - n11 * n24 * n42 - n12 * n21 * n44 + n11 * n22 * n44;
		te[ 14 ] = n14 * n22 * n31 - n12 * n24 * n31 - n14 * n21 * n32 + n11 * n24 * n32 + n12 * n21 * n34 - n11 * n22 * n34;
		te[ 3 ] = n23 * n32 * n41 - n22 * n33 * n41 - n23 * n31 * n42 + n21 * n33 * n42 + n22 * n31 * n43 - n21 * n32 * n43;
		te[ 7 ] = n12 * n33 * n41 - n13 * n32 * n41 + n13 * n31 * n42 - n11 * n33 * n42 - n12 * n31 * n43 + n11 * n32 * n43;
		te[ 11 ] = n13 * n22 * n41 - n12 * n23 * n41 - n13 * n21 * n42 + n11 * n23 * n42 + n12 * n21 * n43 - n11 * n22 * n43;
		te[ 15 ] = n12 * n23 * n31 - n13 * n22 * n31 + n13 * n21 * n32 - n11 * n23 * n32 - n12 * n21 * n33 + n11 * n22 * n33;

		float det = n11 * te[ 0 ] + n21 * te[ 4 ] + n31 * te[ 8 ] + n41 * te[ 12 ];	//获得参数matrix行列式的值

		if ( det == 0 ) {		// 没有逆矩阵


			//var msg = "Matrix4.getInverse(): can't invert matrix, determinant is 0";	//提示用户该矩阵没有逆矩阵


			//TODO this.identity();	//获得一个单位矩阵

			return _this;	//返回单位矩阵
		}

		//this.multiplyScalar( 1 / det );	//除以行列式得到逆矩阵
		det =1 / det;
		for (int i=0; i<16;i++) {
			_this[i]*=det;
		}

		return _this;	//返回Matrix4(4x4矩阵)的逆矩阵.

	}
	/*
	///lookAt(eye,center,up)将对象设定为一个视图矩阵，参数都是Vector3对象，该矩阵只会用到eye和center的相对位置。
	///该视图矩阵表示，摄像机在eye位置看向center位置，且向上的向量（这一点稍后解释）为up时的视图矩阵。
	///视图矩阵又可以看做摄像机的模型矩阵，所以该函数产生的矩阵又可以表示以下变换：将物体从原点平移至位置center-eye，
	///再将其旋转至向上的向量为up。向上的向量up用来固定相机，可以想象当相机固定在一点，镜头朝向固定方向的时候，
	///还是可以在一个维度里自由旋转的，up向量固定相机的这个维度。
	///这里的解释摘抄自:http://www.cnblogs.com/yiyezhai/archive/2012/11/29/2791319.html
	*/
	///<summary>lookAt</summary>
	///<param name ="eye" type="Vector3">表示相机位置的Vector3三维向量</param>
	///<param name ="target" type="Vector3">表示目标的Vector3三维向量</param>
	///<param name ="up" type="Vector3">表示向上的Vector3三维向量</param>
	///<returns type="Matrix4(4x4矩阵)">返回新的Matrix4(4x4矩阵)</returns>
	public final static mat4 lookAt (mat4 out,  vec3 eye,vec3 center,vec3 up) {
		float x0 = 0, x1 = 0, x2 = 0, y0 = 0, y1 = 0, y2 = 0, z0 = 0, z1 = 0, z2 = 0, len = 0;
			  float eyex = eye.x;
			  float eyey = eye.y;
			  float eyez = eye.z;
			  float upx = up.x;
			  float upy = up.y;
			  float upz = up.z;
			  float centerx = center.x;
			  float centery = center.y;
			  float centerz = center.z;

			  if (Math.abs(eyex - centerx) < EPSILON && Math.abs(eyey - centery) < EPSILON && Math.abs(eyez - centerz) < EPSILON) {
			    return identity(out);
			  }

			  z0 = eyex - centerx;
			  z1 = eyey - centery;
			  z2 = eyez - centerz;

			  len =  1f / (float)Math.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
			  z0 *= len;
			  z1 *= len;
			  z2 *= len;

			  x0 = upy * z2 - upz * z1;
			  x1 = upz * z0 - upx * z2;
			  x2 = upx * z1 - upy * z0;
			  len = (float) Math.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
			  if (len==0f) {
			    x0 = 0;
			    x1 = 0;
			    x2 = 0;
			  } else {
			    len = 1 / len;
			    x0 *= len;
			    x1 *= len;
			    x2 *= len;
			  }

			  y0 = z1 * x2 - z2 * x1;
			  y1 = z2 * x0 - z0 * x2;
			  y2 = z0 * x1 - z1 * x0;

			  len = (float) Math.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
			  if (len==0f) {
			    y0 = 0;
			    y1 = 0;
			    y2 = 0;
			  } else {
			    len = 1 / len;
			    y0 *= len;
			    y1 *= len;
			    y2 *= len;
			  }

			  out.m00 = x0;
			  out.m10 = y0;
			  out.m20 = z0;
			  out.m30 = 0;
			  out.m01 = x1;
			  out.m11 = y1;
			  out.m21 = z1;
			  out.m31 = 0;
			  out.m02 = x2;
			  out.m12 = y2;
			  out.m22 = z2;
			  out.m32 = 0;
			  out.m03 = -(x0 * eyex + x1 * eyey + x2 * eyez);
			  out.m13 = -(y0 * eyex + y1 * eyey + y2 * eyez);
			  out.m23 = -(z0 * eyex + z1 * eyey + z2 * eyez);
			  out.m33 = 1;

			  return out;

		}
	
	
}