package soft3d;
import static java.lang.Math.*;
import static soft3d.Vec3.normalize;
public final class Matrix {
	
	private static final float[][] rotationX={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	private static final float[][] rotationY={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	private static final float[][] rotationZ={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	
	public static final void loadIdentity(float[][] mat) {
		mat[0][0]=1; mat[0][1]=0; mat[0][2]=0; mat[0][3]=0;
	    mat[1][0]=0; mat[1][1]=1; mat[1][2]=0; mat[1][3]=0;
	    mat[2][0]=0; mat[2][1]=0; mat[2][2]=1; mat[2][3]=0;
	    mat[3][0]=0; mat[3][1]=0; mat[3][2]=0; mat[3][3]=1;
	   
	}
	
	public static void matcopy(float[][] dst, float[][] src){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				dst[i][j]=src[i][j];
			}
		}
	}
	
	/**
	 * transform all points in array
	 * @param source source points
	 * @param mat Matrix
	 * @param dest result
	 */
	public static final void transform(float[] source,float[][] mat,float[] dest) {
		int len=source.length;
		int i=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0],_30=mat[3][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1],_31=mat[3][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2],_32=mat[3][2];
		while(i<len){
			float srcX=source[i],srcY=source[i+1],srcZ=source[i+2];
			
			float x=srcX*_00+srcY*_10+srcZ*_20+_30;
			float y=srcX*_01+srcY*_11+srcZ*_21+_31;
			float z=srcX*_02+srcY*_12+srcZ*_22+_32;
			dest[i]=x;
			dest[i+1]=y;
			dest[i+2]=z;
			i+=3;
		}
		
	}
	public static final void transform(float[] source,float[][] mat,float[] dest, float interpolation) {
		int len=source.length;
		int i=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0],_30=mat[3][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1],_31=mat[3][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2],_32=mat[3][2];
		while(i<len){
			float srcX=source[i],srcY=source[i+1],srcZ=source[i+2];
			
			float x=srcX*_00+srcY*_10+srcZ*_20+_30;
			float y=srcX*_01+srcY*_11+srcZ*_21+_31;
			float z=srcX*_02+srcY*_12+srcZ*_22+_32;
			dest[i]+=x * interpolation;
			dest[i+1]+=y * interpolation;
			dest[i+2]+=z * interpolation;
			i+=3;
		}
		
	}
	
	public static final void transform(float[] source,float[][] mat,float[] weights,int[]vertexIndices,float[] dest) {
		int weightLen=weights.length;
		int weightIndex=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0],_30=mat[3][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1],_31=mat[3][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2],_32=mat[3][2];
		while(weightIndex<weightLen){
			
			float w=weights[weightIndex];
			int vertexIndex= vertexIndices[weightIndex];
			
			vertexIndex=vertexIndex+vertexIndex+vertexIndex;
			float srcX=source[vertexIndex];
			float srcY=source[vertexIndex+1];
			float srcZ=source[vertexIndex+2];
			
			float x=srcX*_00+srcY*_10+srcZ*_20+_30;
			float y=srcX*_01+srcY*_11+srcZ*_21+_31;
			float z=srcX*_02+srcY*_12+srcZ*_22+_32;
			
			dest[vertexIndex]+=x*w;
			dest[vertexIndex+1]+=y*w;
			dest[vertexIndex+2]+=z*w;
			weightIndex++;
		}
		
	}
	public static final void transform(float[] source,float[][] mat,float[] weights,int[]vertexIndices,float[] dest, float interpolation) {
		int weightLen=weights.length;
		int weightIndex=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0],_30=mat[3][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1],_31=mat[3][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2],_32=mat[3][2];
		while(weightIndex<weightLen){
			
			float w=weights[weightIndex];
			int vertexIndex= vertexIndices[weightIndex];
			
			vertexIndex=vertexIndex+vertexIndex+vertexIndex;
			float srcX=source[vertexIndex];
			float srcY=source[vertexIndex+1];
			float srcZ=source[vertexIndex+2];
			
			float x=srcX*_00+srcY*_10+srcZ*_20+_30;
			float y=srcX*_01+srcY*_11+srcZ*_21+_31;
			float z=srcX*_02+srcY*_12+srcZ*_22+_32;
			
			w = w * interpolation;
			dest[vertexIndex]+=x*w;
			dest[vertexIndex+1]+=y*w;
			dest[vertexIndex+2]+=z*w;
			weightIndex++;
		}
		
	}
	public static final void mul(float[][] mat1, float[][] mat2,float[][] dest)
	{
		float m00=mat1[0][0]*mat2[0][0]+mat1[0][1]*mat2[1][0]+mat1[0][2]*mat2[2][0]+mat1[0][3]*mat2[3][0];
		  float m01=mat1[0][0]*mat2[0][1]+mat1[0][1]*mat2[1][1]+mat1[0][2]*mat2[2][1]+mat1[0][3]*mat2[3][1];
		  float m02=mat1[0][0]*mat2[0][2]+mat1[0][1]*mat2[1][2]+mat1[0][2]*mat2[2][2]+mat1[0][3]*mat2[3][2];
		  float m03=mat1[0][0]*mat2[0][3]+mat1[0][1]*mat2[1][3]+mat1[0][2]*mat2[2][3]+mat1[0][3]*mat2[3][3];
		  float m10=mat1[1][0]*mat2[0][0]+mat1[1][1]*mat2[1][0]+mat1[1][2]*mat2[2][0]+mat1[1][3]*mat2[3][0];
		  float m11=mat1[1][0]*mat2[0][1]+mat1[1][1]*mat2[1][1]+mat1[1][2]*mat2[2][1]+mat1[1][3]*mat2[3][1];
		  float m12=mat1[1][0]*mat2[0][2]+mat1[1][1]*mat2[1][2]+mat1[1][2]*mat2[2][2]+mat1[1][3]*mat2[3][2];
		  float m13=mat1[1][0]*mat2[0][3]+mat1[1][1]*mat2[1][3]+mat1[1][2]*mat2[2][3]+mat1[1][3]*mat2[3][3];
		  float m20=mat1[2][0]*mat2[0][0]+mat1[2][1]*mat2[1][0]+mat1[2][2]*mat2[2][0]+mat1[2][3]*mat2[3][0];
		  float m21=mat1[2][0]*mat2[0][1]+mat1[2][1]*mat2[1][1]+mat1[2][2]*mat2[2][1]+mat1[2][3]*mat2[3][1];
		  float m22=mat1[2][0]*mat2[0][2]+mat1[2][1]*mat2[1][2]+mat1[2][2]*mat2[2][2]+mat1[2][3]*mat2[3][2];
		  float m23=mat1[2][0]*mat2[0][3]+mat1[2][1]*mat2[1][3]+mat1[2][2]*mat2[2][3]+mat1[2][3]*mat2[3][3];
		  float m30=mat1[3][0]*mat2[0][0]+mat1[3][1]*mat2[1][0]+mat1[3][2]*mat2[2][0]+mat1[3][3]*mat2[3][0];
		  float m31=mat1[3][0]*mat2[0][1]+mat1[3][1]*mat2[1][1]+mat1[3][2]*mat2[2][1]+mat1[3][3]*mat2[3][1];
		  float m32=mat1[3][0]*mat2[0][2]+mat1[3][1]*mat2[1][2]+mat1[3][2]*mat2[2][2]+mat1[3][3]*mat2[3][2];
		  float m33=mat1[3][0]*mat2[0][3]+mat1[3][1]*mat2[1][3]+mat1[3][2]*mat2[2][3]+mat1[3][3]*mat2[3][3];
		  dest[0][0] =m00;
		  dest[0][1] =m01;
		  dest[0][2] =m02;
		  dest[0][3] =m03;
		  dest[1][0] =m10;
		  dest[1][1] =m11;
		  dest[1][2] =m12;
		  dest[1][3] =m13;
		  dest[2][0] =m20;
		  dest[2][1] =m21;
		  dest[2][2] =m22;
		  dest[2][3] =m23;
		  dest[3][0] =m30;
		  dest[3][1] =m31;
		  dest[3][2] =m32;
		  dest[3][3] =m33;
	}
	
	public static final void translate(float tx, float ty, float tz,float[][] tmat) {
		tmat[0][0] = 1;	tmat[0][1] = 0;	tmat[0][2] = 0;	tmat[0][3] = 0;
		tmat[1][0] = 0;	tmat[1][1] = 1;	tmat[1][2] = 0;	tmat[1][3] = 0;
		tmat[2][0] = 0;	tmat[2][1] = 0;	tmat[2][2] = 1;	tmat[2][3] = 0;
		tmat[3][0] = tx;tmat[3][1] = ty;tmat[3][2] = tz;tmat[3][3] = 1;
	}
	
	public static final void scale(float sx, float sy, float sz,float[][] smat) {
		smat[0][0]=sx;smat[0][1]=0;smat[0][2]=0;smat[0][3]=0;
		smat[1][0]=0;smat[1][1]=sy;smat[1][2]=0;smat[1][3]=0;
		smat[2][0]=0;smat[2][1]=0;smat[2][2]=sz;smat[2][3]=0;
		smat[3][0]=0;smat[3][1]=0;smat[3][2]=0;smat[3][3]=1;
	}
	
	public static final void rotate(float ax,float ay,float az,float[][] mat) {
		float cosx=(float) cos(ax),cosy=(float) cos(ay),cosz=(float) cos(az);
		float sinx=(float) sin(ax),siny=(float) sin(ay),sinz=(float) sin(az);
		float[][] rX=rotationX,rY=rotationY,rZ=rotationZ;
		//rotationX
		rX[0][0]=1;rX[0][1]= 0;   rX[0][2]= 0;  rX[0][3]=0;
		rX[1][0]=0;rX[1][1]=cosx; rX[1][2]=sinx;rX[1][3]=0;
		rX[2][0]=0;rX[2][1]=-sinx;rX[2][2]=cosx;rX[2][3]=0;
		rX[3][0]=0;rX[3][1]= 0;   rX[3][2]= 0;  rX[3][3]=1;
		//rotationY
		rY[0][0]=cosy;rY[0][1]=0;rY[0][2]=-siny;rY[0][3]=0;
		rY[1][0]= 0;  rY[1][1]=1;rY[1][2]= 0;   rY[1][3]=0;
		rY[2][0]=siny;rY[2][1]=0;rY[2][2]=cosy; rY[2][3]=0;
		rY[3][0]=0;   rY[3][1]=0;rY[3][2]=0;    rY[3][3]=1;
		//rotationZ
		rZ[0][0]=cosz; rZ[0][1]=sinz;rZ[0][2]=0;rZ[0][3]=0;
		rZ[1][0]=-sinz;rZ[1][1]=cosz;rZ[1][2]=0;rZ[1][3]=0;
		rZ[2][0]= 0;   rZ[2][1]= 0;  rZ[2][2]=1;rZ[2][3]=0;
		rZ[3][0]= 0;   rZ[3][1]= 0;  rZ[3][2]=0;rZ[3][3]=1;
		//merge
		mul(rX, rY,rX);
		mul(rX,rZ,mat);
	}
	/**
	 * RotationAxis
	 * @param pv
	 * @param theta
	 * @param mat
	 */
	public static void  rotationAxis(  float[] pv, float  theta,float[][] mat) {  //求单位向量,之后使得len(*pv) = sin(theta);
		float[] n={pv[0],pv[1],pv[2]};//拷贝 
	
	    normalize(n);    //单位化
	    float  nLength = (float) abs(sin(theta));   //加绝对值是为了使其方向不变 
	    n[0] *= nLength;n[1] *= nLength;n[2] *= nLength;//使得n的长度为nLength 
	    float  a =  n[0];        //赋值
	    
	    float  b =  n[1];
	    float  c =  n[2];
	    float d =  (float) cos(theta);  //点积  1*1*cos(theta) 
	    float   t =  (1 - d) / (nLength * nLength);     //临时变量
	    mat[0][0]=a*t * a + d;mat[0][1]= b*t * a + c;mat[0][2]=c*t * a - b;mat[0][3]=0.0f;      //生成矩阵
	    mat[1][0]=a*t * b - c;mat[1][1]=b*t * b + d;mat[1][2]=c*t * b + a;mat[1][3]= 0.0f;
	    mat[2][0]=a*t * c + b;mat[2][1]= b*t * c - a;mat[2][2]= c*t * c + d;mat[2][3]= 0.0f;
	    mat[3][0]=0.0f;mat[3][1]=         0.0f;mat[3][2]=        0.0f;mat[3][3]=       1.0f;
	    
	}
	public static void transpose(float[][] mat,float[][] result) {
		float m00=mat[0][0];
		float m01=mat[0][1];
		float m02=mat[0][2];
		float m03=mat[0][3];
		float m10=mat[1][0];
		float m11=mat[1][1];
		float m12=mat[1][2];
		float m13=mat[1][3];
		float m20=mat[2][0];
		float m21=mat[2][1];
		float m22=mat[2][2];
		float m23=mat[2][3];
		float m30=mat[3][0];
		float m31=mat[3][1];
		float m32=mat[3][2];
		float m33=mat[3][3];
		
		result[0][0]= m00;
		result[0][1]= m10;
		result[0][2]= m20;
		result[0][3]= m30;
		result[1][0]= m01;
		result[1][1]= m11;
		result[1][2]= m21;
		result[1][3]= m31;
		result[2][0]= m02;
		result[2][1]= m12;
		result[2][2]= m22;
		result[2][3]= m32;
		result[3][0]= m03;
		result[3][1]= m13;
		result[3][2]= m23;
		result[3][3]= m33;
	}
}
