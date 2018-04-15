package loader.xfile;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import soft3d.Framebuffer;
import soft3d.Matrix;
import soft3d.Texture;
import soft3d.Vec3;

import static soft3d.Framebuffer.computeNormals;

public class XMesh {
	//public Extent extent;
	public Rectangle screenBounds = new Rectangle(10,10);
	public boolean visible = true;
	public int nVertices;
	public int[] indices;
	public float[] vertices;
	public float[] texCoords;
	public float[] normals;
	
	public float[] normalsBuf;
	public float[] verticesBuf;
	public float[] screenBuf;
	/**
	 * Sub Mesh: faceStart-faceEnd-materialIndex
	 */
	public List<int[]> subMeshIndices;
	public int[] subMeshVertexRange;
	
	public List<SkinWeights> skinWeightsList;
	public int[] materialIndices;
	public List<Material> materials;
	public List<Texture> textures;
	public List<String> materialNames;
	
	private final float[][] _mat0={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	private final float[][] buf1={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	public void updateNoskin(XFrame frame) {
		//绘制前的准备工作
		if ((normals == null||normals.length!=vertices.length)){
			normals = new float[vertices.length];
			computeNormals(vertices, indices, normals);
		}
		final float[][] mat0=_mat0;
		float interp = frame.interpolation;
//		interp = 0;
		float[][] transfrom= frame.frameTransformMatrix;
		Matrix.mul(transfrom, Framebuffer.VIEWMATRIX, mat0);
		Matrix.transform(vertices,mat0, verticesBuf, 1-interp);
//		Matrix.getNormalMatrix(mat0, buf1);
		transformNormals(normals,mat0, normalsBuf);
		
		if (screenBuf == null)
			screenBuf = new float[nVertices*2];
//		//物体在屏幕上的大小
//		SoftGraphics3D.getBounds(screenBuf,screenBounds);
		/* Need to be normalized?*/
		if (normalsBuf.length>3) {
			float x0=normalsBuf[0];
			float y0=normalsBuf[1];
			float z0=normalsBuf[2];
			float m=x0*x0+y0*y0+z0*z0;
			if (m>0.6f&&m<1.4f) {
				
			} else if(m!=0f){//Need to be normalized
				Vec3.normalizeAll(normalsBuf);
			}
		}
//		if (LIGHT_ON)computeNormals(verticesBuf, indices, normals);
	}
	
	public void update(XFrame frame) {
		if (verticesBuf==null) {
			verticesBuf=new  float[vertices.length];
		}
		final float[][] mat0=_mat0;
		Arrays.fill(verticesBuf, 0);
		if (normalsBuf==null) {
			normalsBuf=new float[vertices.length];
		} else Arrays.fill(normalsBuf, 0);
		if (skinWeightsList==null) {
			updateNoskin(frame);
			return;
		}
		Matrix.loadIdentity(mat0);
		Matrix.loadIdentity(buf1);
		boolean needCalcNormal=normals == null||normals.length!=vertices.length;
		for (SkinWeights skin :skinWeightsList) {
			XFrame node = skin.node;
			if (node==null||skin.vertexIndices==null) {
				continue;
			}
			float[][] boneTrans=skin.matrixOffset;
			float[][] transfrom= node.frameTransformMatrix;
			Matrix.mul(boneTrans, transfrom, mat0);
			Matrix.mul( mat0,Framebuffer.VIEWMATRIX, mat0);
			int[] indices=skin.vertexIndices;
			float[] weights=skin.weights;
//			node.interpolation=0;
			float interp = node.interpolation;
			Matrix.transform(vertices,mat0,weights,indices, verticesBuf, 1-interp);
			
			Matrix.mul(boneTrans, transfrom, mat0);
//			float[][] normMat = Matrix.getNormalMatrix(mat0, buf1);
			if(!needCalcNormal)
				transformNormals(normals,mat0,weights,indices, normalsBuf, 1-interp);
			
//			if(interp==0.0f) continue;
//			transfrom= node.nextKeyframeTransform;
//			Matrix.mul(boneTrans, transfrom, mat0);
//			Matrix.mul(mat0, Framebuffer.VIEWMATRIX, mat0);
//			Matrix.transform(vertices,mat0,weights,indices, verticesBuf, interp);
//			Matrix.mul(boneTrans, transfrom, mat0);
//			normMat = Matrix.getNormalMatrix(mat0, buf1);
//			if(!needCalcNormal)
//			Matrix.transform(normals,normMat,weights,indices, normalsBuf, interp)
			;
		}
		
		//绘制前的准备工作
		if ( needCalcNormal){
			if (normals!=null&&vertices.length!=normals.length)
				System.err.println("length of vertices no equals length of normals !");
			normals = new float[vertices.length];
			computeNormals(vertices, indices, normals);
		}
		if (screenBuf == null)
			screenBuf = new float[nVertices*2];
//		//物体在屏幕上的大小
//		SoftGraphics3D.getBounds(screenBuf,screenBounds);
	}
	
	public void reset() {
		for (int i = 0; i < vertices.length; i++) {
			verticesBuf[i]=vertices[i];
		}
	}
	static void transformNormals(float[] source,float[][] mat,float[] weights,int[]vertexIndices,float[] dest, float interpolation) {
		int weightLen=weights.length;
		int weightIndex=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2];
		while(weightIndex<weightLen){
			
			float w=weights[weightIndex];
			int vertexIndex= vertexIndices[weightIndex];
			
			vertexIndex=vertexIndex+vertexIndex+vertexIndex;
			float srcX=source[vertexIndex];
			float srcY=source[vertexIndex+1];
			float srcZ=source[vertexIndex+2];
			
			float x =srcX,y =srcY,z=srcZ;
		    float nx = _00 * x + _10 * y + _20 * z;
		    float ny = _01 * x + _11 * y + _21 * z;
		    float nz =_02 * x + _12 * y + _22 * z;

		    w = w * interpolation;
		    Vec3 norm= Vec3.vec3(nx,ny,nz);
		    Vec3.normalize(norm);
		    dest[vertexIndex]+=norm.x*w;
		    dest[vertexIndex+1]+=norm.y*w;
		    dest[vertexIndex+2]+=norm.z*w;
			weightIndex++;
		}
	}
	private void transformNormals(float[] source,float[][] mat,float[] dest) {
		int len=source.length;
		int i=0;
		float _00=mat[0][0],_10=mat[1][0],_20=mat[2][0];
		float _01=mat[0][1],_11=mat[1][1],_21=mat[2][1];
		float _02=mat[0][2],_12=mat[1][2],_22=mat[2][2];
		while(i<len){
			float srcX=source[i],srcY=source[i+1],srcZ=source[i+2];
			
			float x =srcX,y =srcY,z=srcZ;
		    float nx = _00 * x + _10 * y + _20 * z;
		    float ny = _01 * x + _11 * y + _21 * z;
		    float nz =_02 * x + _12 * y + _22 * z;
		    Vec3 norm= Vec3.vec3(nx,ny,nz);
		    Vec3.normalize(norm);
			dest[i]=norm.x ;
			dest[i+1]=norm.y ;
			dest[i+2]=norm.z ;
			i+=3;
		}
	}
}
