package loader.xfile;

import soft3d.Matrix;
import soft3d.util.Quaternion;

public class Animation {
	public XFrame node;
	public String nodeName;
	public String name;
	
	public AnimationKey matrixKey;
	public AnimationKey rotateKey;
	public AnimationKey scaleKey;
	public AnimationKey translateKey;
	
	public void getKeyframesTime(AnimationKey key, long time){
		int len=key.times.length;
		int start=key.times[0];
		int end=key.times[len-1];
		
		time=time%(end-start) + start;
		float rat=(time-start)/(float)(end-start);
		
		float fIndex= (len-1)*rat;
		time=(int)(fIndex);
		time=(int)Math.round(fIndex);
		
		if(time<0)time=0;
		if(time>=len)time=len-1;
		key.currentFrame=(int) time;
		key.nextFrame=(int) (time+1>len-1?len-1:time+1);
		node.interpolation = (fIndex - time)/1.0f;
	}
	public void updateTransform(long lTime) {
//		lTime = 3280;
		int start = 0;
		float[][] matrix=node.frameTransformMatrix;
		if (matrixKey != null) {
			AnimationKey key = matrixKey;
			getKeyframesTime(key, lTime);
			float[] floatKeys=key.floatKeys;
			start=16*key.currentFrame;
			for (int r = 0; r < 4; r++) {
				for (int c = 0; c < 4; c++) {
					matrix[r][c]=floatKeys[start];
					start++;
				}
			}
//			matrix = node.nextKeyframeTransform;
//			start=16*key.nextFrame;
//			for (int r = 0; r < 4; r++) {
//				for (int c = 0; c < 4; c++) {
//					matrix[r][c]=floatKeys[start];
//					start++;
//				}
//			}
			return;
		}
		final float[][] mat ={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		Matrix.loadIdentity(matrix);
		if (scaleKey !=null) {
			AnimationKey key = scaleKey;
			getKeyframesTime(key, lTime);
			float[] floatKeys=key.floatKeys;
			start=3*key.currentFrame;
			float sx0 = floatKeys[start++];
			float sy0 = floatKeys[start++];
			float sz0 = floatKeys[start++];
			
			start=3*key.nextFrame;
			float sx1 = floatKeys[start++];
			float sy1 = floatKeys[start++];
			float sz1 = floatKeys[start++];
			
			float lerp = node.interpolation;
			float sx = sx0 + (sx1-sx0)*lerp;
			float sy = sy0 + (sy1-sy0)*lerp;
			float sz = sz0 + (sz1-sz0)*lerp;
			Matrix.scale(sx, sy, sz, matrix);
		}
		if (rotateKey != null) {
			AnimationKey key = rotateKey;
			getKeyframesTime(key, lTime);
			float[] floatKeys=key.floatKeys;
			start=4*key.currentFrame;
			float qw0 = floatKeys[start++];
			float qx0 = floatKeys[start++];
			float qy0 = floatKeys[start++];
			float qz0 = floatKeys[start++];
			
			start=4*key.nextFrame;
			float qw1 = floatKeys[start++];
			float qx1 = floatKeys[start++];
			float qy1 = floatKeys[start++];
			float qz1 = floatKeys[start++];
			
			float lerp = node.interpolation;
			float qw = qw0 + (qw1-qw0)*lerp;
			float qx = qx0 + (qx1-qx0)*lerp;
			float qy = qy0 + (qy1-qy0)*lerp;
			float qz = qz0 + (qz1-qz0)*lerp;
			final float[] quat= {qw,qx,qy,qz};
			Quaternion.quatNormalize(quat);
			Quaternion.quat2Matrix(quat, mat);
			Matrix.mul(matrix,mat, matrix);
		}
		if (translateKey != null) {
			AnimationKey key = translateKey;
			getKeyframesTime(key, lTime);
			float[] floatKeys=key.floatKeys;
			start=3*key.currentFrame;
			float x0 = floatKeys[start++];
			float y0 = floatKeys[start++];
			float z0 = floatKeys[start++];
			
			start=3*key.nextFrame;
			float x1 = floatKeys[start++];
			float y1 = floatKeys[start++];
			float z1 = floatKeys[start++];
			
			float lerp = node.interpolation;
			float x = x0 + (x1-x0)*lerp;
			float y = y0 + (y1-y0)*lerp;
			float z = z0 + (z1-z0)*lerp;
			Matrix.translate(x, y, z, mat);
			Matrix.mul(matrix,mat, matrix);
		}
		
		node.interpolation = 0;
	}
}
