package soft3d.util;

import java.awt.Rectangle;
import java.io.BufferedReader;

import loader.xfile.XMesh;
import soft3d.Material;
import soft3d.Matrix;
import soft3d.Texture;
import soft3d.Vec3;
import soft3d.Framebuffer;
import soft3d.v1_0.TinyGL;
import static soft3d.Framebuffer.computeNormals;
import static soft3d.Framebuffer.VIEWMATRIX;;
/**
 * 网格
 * @author Administrator
 *
 */
public class Mesh {
	public Rectangle screenSize=new Rectangle(10,10);
	boolean converted=false;
	public boolean textureAnimated=false;
	public CustomVertex[] rawVertices;
	public int[] indices;
	
	public int nVertices;
	public float[] vertices;
	public float[] texCoords;
	public float[] normals;
	public float[] verticesBuf;
	public float[] screenBuf;
	public float[] normalsBuf;
	
	public Texture[] textures;
	public Material[] materials;
	public int[] materialIndices;
	// Allocate vertex and index buffer for rectangular patch.
	// Optimize vertex and index buffer for perfect cache coherency.
	int s_tessellationLevel = 9;
	void initPatchGeometry(int density)
	{
		
		// @@ Use multiple strips when density >= cache size.
		int s_patchIndexCount=0;
		boolean USE_STRIPS=false;
		boolean s_preWarmCache=true;
	if( USE_STRIPS){
		s_patchIndexCount = density * (2 * density + 3);
		if (s_preWarmCache)
		{
			//s_patchIndexCount += 2 * density + 2;
			s_patchIndexCount += 1 * density + 3;
		}
	}
	else{
		s_patchIndexCount = density * density * 6;

		int strips = 0;
		if (s_preWarmCache)
		{
			s_patchIndexCount += 3 * density;

			/*while (density + 1 > (strips + 1) * s_cacheSize)
			{
				strips++;
			}*/
		}
	}
		int s_patchVertexCount = (density + 1) * (density + 1);

	    float [] vertexPtr = new float[2 * s_patchVertexCount];
		int[] indexPtr = new int[s_patchIndexCount];

	    int idx = 0;
	    for(int i = 0; i <= density; i++)
	    {
	        float v = (float)(i) / density;
	            
	        for(int e = 0; e <= density; e++)
	        {
	            float u = (float)(e) / density;

	            vertexPtr[idx++] = u;
	            vertexPtr[idx++] = v;
	        }
	    }
	    assert(idx == 2 * s_patchVertexCount);

	    idx = 0;

	if( USE_STRIPS){
		if (s_preWarmCache)
		{
			// Create degenerate triangles to prewarm cache.
//			indexPtr[idx++] = i;
//			indexPtr[idx++] = i + 1;
			indexPtr[idx++] = 0;
			indexPtr[idx++] = 0 + 1;
			
			for(int i = 0; i < density; i++)
			{
				indexPtr[idx++] = i + 1;
			//	indexPtr[idx++] = i + 1;
			}

			indexPtr[idx++] = 0xFFFF; // Restart strip.
		}

		// Real triangles:
	    for(int i = 0; i < density; i++)
	    {
	        indexPtr[idx++] = (density+1) * (i+1);
	        indexPtr[idx++] = (density+1) * (i+0);

	        for(int e = 0; e < density; e++)
	        {
	            indexPtr[idx++] = (density+1) * (i+1) + (e+1);
	            indexPtr[idx++] = (density+1) * (i+0) + (e+1);
	        }

	        indexPtr[idx++] = 0xFFFF; // Restart strip.
	    }
	    assert(idx == s_patchIndexCount);
	}
	else{
		if (s_preWarmCache)
		{
			// Create degenerate triangles to prewarm cache.
			for(int i = 0; i < density; i++)
			{
				indexPtr[idx++] = i;
				indexPtr[idx++] = i + 1;
				indexPtr[idx++] = i + 1;
			}
		}

		// Real triangles:
	    for(int i = 0; i < density; i++)
	    {
	        for(int e = 0; e < density; e++)
	        {
	            indexPtr[idx++] = (density+1) * (i+0) + (e+0);
	            indexPtr[idx++] = (density+1) * (i+0) + (e+1);
	            indexPtr[idx++] = (density+1) * (i+1) + (e+0);

	            indexPtr[idx++] = (density+1) * (i+1) + (e+0);
	            indexPtr[idx++] = (density+1) * (i+0) + (e+1);
	            indexPtr[idx++] = (density+1) * (i+1) + (e+1);
	        }
	    }
	    assert(idx == s_patchIndexCount);
	}//#endif

		// @@ Reorder vertices according to indices to maximize cache coherency.

//	    glGenBuffersARB(1, &s_vbHandle);
//	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, s_vbHandle);
//	    glBufferDataARB(GL_ARRAY_BUFFER_ARB, s_patchVertexCount * sizeof(float) * 2, vertexPtr, GL_STATIC_DRAW_ARB);
//
//	    glGenBuffersARB(1, &s_ibHandle);
//	    glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER, s_ibHandle);
//	    glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER, s_patchIndexCount * sizeof(short), indexPtr, GL_STATIC_DRAW_ARB);

//	    delete [] vertexPtr;
//	    delete [] indexPtr;
	}
	public static Mesh create(XMesh mesh){
		float[]vertices=mesh.vertices;
		int[] indices=mesh.indices;
		Mesh meshData=new Mesh();
		
		float[] normals=mesh.normals;
		meshData.indices=indices;
		if(normals==null){
			mesh.normals=new float[vertices.length];
			normals=mesh.normals;
			computeNormals(vertices, indices,normals);
		}
		int toIndex=vertices.length/3;
		meshData.rawVertices=new CustomVertex[toIndex];
		for (int i = 0; i < toIndex; i++) {
			float[] pos={ vertices[3*i] ,vertices[3*i+1],vertices[3*i+2]};
			float[] nom={ normals[3*i] ,normals[3*i+1],normals[3*i+2]};
			CustomVertex vertex=new CustomVertex();
			vertex.pos=pos;
			vertex.normal=nom;
			vertex.tex=new float[]{0,0};
			meshData.rawVertices[i]=vertex;
		}
		return meshData;
	}
	public static Mesh create(float[] vertices,int[] indices) {
		Mesh meshData=new Mesh();
		meshData.normals=new float[vertices.length];
		float[] normals=meshData.normals;
		meshData.indices=indices;
		computeNormals(vertices, indices, normals);
		int toIndex=vertices.length/3;
		meshData.rawVertices=new CustomVertex[toIndex];
		for (int i = 0; i < toIndex; i++) {
			float[] pos={ vertices[i] ,vertices[i+1],vertices[i+2]};
			float[] nom={ normals[i] ,normals[i+1],normals[i+2]};
			CustomVertex vertex=new CustomVertex();
			vertex.pos=pos;
			vertex.normal=nom;
			vertex.tex=new float[]{0,0};
			meshData.rawVertices[i]=vertex;
		}
		return meshData;
	}
public static CustomVertex newVertex(double x,double y,double z) {
	CustomVertex v=new CustomVertex();
	v.pos=new float[]{(float) x,(float) y,(float) z};
	v.tex=new float[]{0,0};
	v.normal=new float[]{0,0,0};
	return v;
		
	}
	float[] vector01={0,0,0},vector12={0,0,0};
	public void creatSimpleMesh1(){
		rawVertices=new CustomVertex[]{
				newVertex(0.0613, 44.1625, -0.1873),	
				newVertex(0.0613, 31.2276, -31.4149),
				newVertex(-22.0199, 31.2276, -22.2685),	
				newVertex(-31.1662, 31.2276 ,-0.1873),
				newVertex(-22.0199, 31.2276, 21.8939),	
				newVertex(0.0613, 31.2276, 31.0403),
				newVertex( 22.1426, 31.2276, 21.8939),	
				newVertex(31.2889, 31.2276, -0.1873),
				newVertex(22.1426, 31.2276, -22.2685),	
				newVertex(0.0613, -0.0000, -44.3498),
				newVertex(-31.1662, -0.0000, -31.4149),	
				newVertex(-44.1011, -0.0000, -0.1873),
				newVertex(-31.1662, -0.0000, 31.0403),	
				newVertex(0.0613, -0.0000, 43.9752),
				newVertex(31.2889, -0.0000, 31.0403),	
				newVertex(44.2238, -0.0000, -0.1873),
				newVertex(31.2889, -0.0000, -31.4149),	
				newVertex(0.0613, -31.2276, -31.4149),
				newVertex(-22.0199, -31.2276, -22.2685),	
				newVertex(-31.1662, -31.2276, -0.1873),
				newVertex(-22.0199, -31.2276, 21.8939),	
				newVertex(0.0613, -31.2276, 31.0403),
				newVertex(22.1426, -31.2276, 21.8939),	
				newVertex(31.2889, -31.2276, -0.1873),
				newVertex(22.1426, -31.2276, -22.2685),	
				newVertex(0.0613, -44.1625, -0.1873)
		};
		indices=new int[]{
				1,2,3,
				1,3,4,
				1,4,5,
				1,5,6,
				1,6,7,
				1,7,8,
				1,8,9,
				1,9,2,
				2,10,11,
				2,11,3,
				3,11,12,
				3,12,4,
				4,12,13,
				4,13,5,
				5,13,14,
				5,14,6,
				6,14,15,
				6,15,7,
				7,15,16,
				7,16,8,
				8,16,17,
				8,17,9,
				9,17,10,
				9,10,2,
				10,18,19,
				10,19,11,
				11,19,20,
				11,20,12,
				12,20,21,
				12,21,13,
				13,21,22,
				13,22,14,
				14,22,23,
				14,23,15,
				15,23,24,
				15,24,16,
				16,24,25,
				16,25,17,
				17,25,18,
				17,18,10,
				26,19,18,
				26,20,19,
				26,21,20,
				26,22,21,
				26,23,22,
				26,24,23,
				26,25,24,
				26,18,25
		};
		for (int i = 0; i < indices.length; i++) {
			indices[i]--;
		}
	}
	public void creatSimpleMesh2() {
		rawVertices=new CustomVertex[]{
				newVertex(0.0026 ,0.0000 ,-52.0204),
				newVertex( 42.1128, 10.4496, -13.8335),
				newVertex(  49.3333, 0.0000, -16.1795),
				newVertex( 0.0026 ,10.4496 ,-44.4284),
				newVertex(30.4298, 6.4582, -10.0374),
				newVertex( 0.0026 ,6.4582 ,-32.1441),
				newVertex( 30.4298, -6.4582, -10.0374),
				newVertex( 0.0026 ,-6.4582, -32.1441),
				newVertex( 42.1128 ,-10.4496 ,-13.8335),
				newVertex(0.0026, -10.4496, -44.4284),
				newVertex( 26.0281, 10.4496, 35.6701),
				newVertex( 30.4906 ,0.0000, 41.8122),
				newVertex(18.8076 ,6.4582, 25.7320),
				newVertex( 18.8076, -6.4582, 25.7320),
				newVertex(26.0281, -10.4496, 35.6701),
				newVertex(  -26.0230, 10.4496, 35.6701),
				newVertex(  -30.4855, 0.0000 ,41.8122),
				newVertex(-18.8025, 6.4582, 25.7320),
				newVertex( -18.8025, -6.4582,25.7320),
				newVertex(  -26.0230, -10.4496, 35.6701),
				newVertex(  -42.1077, 10.4496, -13.8335),
				newVertex(  -49.3282 ,0.0000, -16.1795),
				newVertex(  -30.4247, 6.4582, -10.0374),
				newVertex(  -30.4247, -6.4582, -10.0374),
				newVertex (-42.1077, -10.4496, -13.8335)
				
				
		};
		indices=new int[]{
				 1,2,3 
				,1,4,2 
				, 4,5,2 
				, 4,6,5 
				, 6,7,5 
				, 6,8,7 
				, 8,9,7 
				, 8,10,9 
				, 10,3,9 
				, 10,1,3 
				, 3,11,12
				, 3,2,11 
				, 2,13,11 
				, 2,5,13 
				, 5,14,13 
				, 5,7,14 
				, 7,15,14 
				, 7,9,15 
				,9,12,15 
				, 9,3,12 
				, 12,16,17 
				, 12,11,16 
				, 11,18,16 
				, 11,13,18 
				, 13,19,18 
				, 13,14,19 
				, 14,20,19 
				, 14,15,20 
				, 15,17,20 
				, 15,12,17 
				, 17,21,22 
				, 17,16,21 
				, 16,23,21 
				, 16,18,23 
				, 18,24,23 
				, 18,19,24 
				, 19,25,24 
				, 19,20,25 
				, 20,22,25 
				, 20,17,22 
				, 22,4,1 
				, 22,21,4 
				, 21,6,4 
				, 21,23,6 
				, 23,8,6 
				, 23,24,8 
				, 24,10,8 
				, 24,25,10 
				, 25,1,10 
				, 25,22,1 
		};
		for (int i = 0; i < indices.length; i++) {
			indices[i]--;
		}
	}
	
	public void creatSimpleMesh() {
		rawVertices=new CustomVertex[]{
				newVertex(0.000000f,0.000000f,1.000000f),
				newVertex(0.866025f, 0.000000f, 0.500001f),
				newVertex(0.000001f, 0.866025f, 0.500001f),
				newVertex(-0.866025f, 0.000002f, 0.500001f),
				newVertex(-0.000003f, -0.866025f, 0.500001f),
				newVertex(0.866026f, 0.000000f, -0.499998f),
				newVertex(0.000001f, 0.866026f, -0.499998f),
				newVertex(-0.866026f, 0.000002f, -0.499998f),
				newVertex(-0.000003f, -0.866026f, -0.499998f),
				newVertex(0.000000f, 0.000000f, -1.000000f)
				
				
		};
		indices=new int[]{
				1, 2, 3,
				1, 3, 4,
				1, 4, 5,
				1, 5, 2,
				2, 6, 3,
				3, 6, 7,
				3, 7, 4,
				4, 7, 8,
				4, 8, 5,
				5, 8, 9,
				5, 9, 2,
				2, 9, 6,
				10, 7,6,
				10, 8, 7,
				10, 9, 8,
				10, 6,9
		};
		for (int i = 0; i < indices.length; i++) {
			indices[i]--;
		}
	}
	void _glmFirstPass (Mesh model, BufferedReader file)
	{
		
	}

	/* Second pass at a Wavefront OBJ file that gets all the data.
	 *
	 * model - properly initialized GLMmodel structure
	 * file  - (fopen'd) file descriptor 
	 */
	void _glmSecondPass (Mesh model, BufferedReader file)
	{
	 
	}
	public void getFaceNormal(int[] vindices, float[] faceNormal) {
		CustomVertex v0=rawVertices[vindices[0]];
		CustomVertex v1=rawVertices[vindices[1]];
		CustomVertex v2=rawVertices[vindices[2]];
		vector01[0]=v1.pos[0]-v0.pos[0];
		vector01[1]=v1.pos[1]-v0.pos[1];
		vector01[2]=v1.pos[2]-v0.pos[2];
		vector12[0]=v2.pos[0]-v1.pos[0];
		vector12[1]=v2.pos[1]-v1.pos[1];
		vector12[2]=v2.pos[2]-v1.pos[2];
		Vec3.cross(vector01, vector12, faceNormal);
		Vec3.normalize(faceNormal);
	}
	public void updateTexture(long time) {
		time = time % 2000;
		int index = Math.round(time / 2000f * 15);
//		SoftGraphics3D.texture = textures[index];
		System.out.println(index);
	}
	public void update(){
		if (!converted) {
			passConvert();
			
		}
		final float[][] mat0= VIEWMATRIX;
//		Matrix.loadIdentity(mat0);
		Matrix.transform(vertices,mat0, verticesBuf);
		if (normals!=null&&normalsBuf!=null) {
			Matrix.transform(normals,mat0, normalsBuf);
			Vec3.normalizeAll(normalsBuf);
		}
//		
//		SoftGraphics3D.project(verticesBuf, screenBuf);
		//获取在屏幕上的尺寸
//		SoftGraphics3D.getBounds(screenBuf, screenSize);
	}
	/**
	 * Convert Pass
	 */
	public void passConvert(){
		nVertices=rawVertices.length;
		vertices=new float[3*nVertices];
		verticesBuf=new float[3*nVertices];
		texCoords=new float[2*nVertices];
		screenBuf=new float[2*nVertices];
		normals=new float[3*nVertices];
		normalsBuf=new float[3*nVertices];
		int i2d=0,i3d=0;
		for (CustomVertex element : rawVertices) {
			
			for (int i = 0; i < element.pos.length; i++) {
				vertices[i3d+i]=element.pos[i];
				normals[i3d+i]=element.normal[i];
			}
			for (int i = 0; i < element.tex.length; i++) {
				texCoords[i2d+i]=element.tex[i];
			}
			i2d+=2;
			i3d+=3;
		}
		rawVertices=null;
		converted=true;
	}
	final float[] _vector0={0,0,0};
	final float[][] _mat0={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
//	public void draw(SoftGraphics3D graphics3d) {
//		graphics3d.drawTriangleList(verticesBuf, indices, texCoords, normalsBuf, screenBuf, null);
//	}
}
