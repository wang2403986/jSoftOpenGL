package soft3d.util;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
/**
 * Quaternion : (w, x, y, z)
 * @author Administrator
 *
 */
public class Quaternion {

	public static  void quatNormalize(float[] quaternion)
	 {
		double magnitude;
		double x = quaternion[1];
		double y = quaternion[2];
		double z = quaternion[3];
		double w = quaternion[0];
		magnitude = (float) Math.sqrt(x * x + y * y + z * z + w * w);
		float r_magnitude = (float) (1d / magnitude);
		quaternion[1] *= r_magnitude;
		quaternion[2] *= r_magnitude;
		quaternion[3] *= r_magnitude;
		quaternion[0] *= r_magnitude;
	 }
	/** 由欧拉角创建四元数*/
	public static void angle2Quat(float[] angle, float[] quat) {
		float cx = (float) cos(angle[0] / 2);
		float sx = (float) sin(angle[0] / 2);
		float cy = (float) cos(angle[1] / 2);
		float sy = (float) sin(angle[1] / 2);
		float cz = (float) cos(angle[2] / 2);
		float sz = (float) sin(angle[2] / 2);

		float w = cx * cy * cz + sx * sy * sz;
		float x = sx * cy * cz - cx * sy * sz;
		float y = cx * sy * cz + sx * cy * sz;
		float z = cx * cy * sz - sx * sy * cz;
		quat[0] = w;
		quat[1] = x;
		quat[2] = y;
		quat[3] = z;
	}
	/**
	 * Quaternion::ToRotationMatrix
	 * @see OGRE的四元数转矩阵的代码
	 * @param quat
	 * @param mat
	 */
	public static void quat2Matrix(float[] quat, float[][] mat) {
		float x= quat[1],y= quat[2],z= quat[3],w= quat[0];
		float fTx  = x+x;
		float fTy  = y+y;
		float fTz  = z+z;
		float fTwx = fTx*w;
		float fTwy = fTy*w;
		float fTwz = fTz*w;
        float fTxx = fTx*x;
        float fTxy = fTy*x;
        float fTxz = fTz*x;
        float fTyy = fTy*y;
        float fTyz = fTz*y;
        float fTzz = fTz*z;
        float[][] kRot = mat;
        kRot[0][0] = 1.0f-(fTyy+fTzz);
        kRot[0][1] = fTxy-fTwz;                  //改为fTxy+fTwz
        kRot[0][2] = fTxz+fTwy;                 //改为fTxz-fTwy
        kRot[1][0] = fTxy+fTwz;                 //改为fTxy-fTwz
        kRot[1][1] = 1.0f-(fTxx+fTzz);
        kRot[1][2] = fTyz-fTwx;                  //改为fTyz+fTwx
        kRot[2][0] = fTxz-fTwy;                  //改为fTxz+fTwy
        kRot[2][1] = fTyz+fTwx;                 //改为fTyz-fTwx
        kRot[2][2] = 1.0f-(fTxx+fTyy);
	}
}
