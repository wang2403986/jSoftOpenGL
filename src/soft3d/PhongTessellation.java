package soft3d;
import static soft3d.Vec3.normalize1;
import static soft3d.util.Float3.*;

import java.util.ArrayList;

import soft3d.util.Mesh;
/**
 * Phong Tessellation 方法，把一个三角形细分为一个曲面
 * @author Administrator
 *
 */
public class PhongTessellation {
	public int[] indices;
	public float[] texCoords;
	public float[] normals;
	public float[] vertices;
	public float[] verticesBuf;
	public float[] screenBuf;
	public float[] normalsBuf;
//	public void draw(SoftGraphics3D graphics3d) {
//		graphics3d.drawTriangleList(verticesBuf, indices, texCoords, normalsBuf, screenBuf, null);
//	}
	public void convertToMesh(){
		vertices=new float[verticesList.size()*3];
		int ipos=0;
		for (int i = 0; i < verticesList.size(); i++) {
			float[] pos1= verticesList.get(i);
			vertices[ipos]=pos1[0];
			vertices[ipos+1]=pos1[1];
			vertices[ipos+2]=pos1[2];
			ipos+=3;
		}
		indices=new int[indicesList.size()*3];
		ipos=0;
		for (int i = 0; i < indicesList.size(); i++) {
			int[] pos1= indicesList.get(i);
			indices[ipos]=pos1[0];
			indices[ipos+1]=pos1[1];
			indices[ipos+2]=pos1[2];
			ipos+=3;
		}
		normals=new float[normalsList.size()*3];
		ipos=0;
		for (int i = 0; i < normalsList.size(); i++) {
			float[] pos1= normalsList.get(i);
			normals[ipos]=pos1[0];
			normals[ipos+1]=pos1[1];
			normals[ipos+2]=pos1[2];
			ipos+=3;
		}
	}
	public void update(){
//		int vSize= vertices.length;
//		if (verticesBuf==null||verticesBuf.length!=vertices.length) {
//			verticesBuf=new float[vSize];
//			normalsBuf=new float[vSize];
//			screenBuf=new float[vSize/3*2];
//		}
//		
//		final float[][] mat0=SoftGraphics3D.transform;
////		Matrix.loadIdentity(mat0);
////		Matrix.matrixMult(mat0, SoftGraphics3D.transform, mat0);
//		Matrix.transform(vertices,mat0, verticesBuf);
//		Matrix.transform(normals,mat0, normalsBuf);
//		//Vector3.normalizeAll(normalsBuf);
//		SoftGraphics3D.project(verticesBuf, screenBuf);
//		//SoftGraphics3D.getBounds(screenBuf, rect);
	}
	ArrayList<float[]> verticesList=new ArrayList<float[]>();
	ArrayList<float[]> normalsList=new ArrayList<float[]>();
	ArrayList<int[]> indicesList=new ArrayList<int[]>();
	boolean isOdd=false;
	int begin=0;
	public int addVertex(float[] pos,float[] normal) {
		
		int pIndex= verticesList.size();
		verticesList.add(pos);
		normalsList.add(normal);
		int size= indicesList.size();
		if (begin==2) {
			int[] newTri={pIndex-2,pIndex-1,pIndex};
			indicesList.add(newTri);
			isOdd=true;
		}
		else if (begin>2) {
			if (isOdd) {
				//int[] pre=indicesList.get(size-1);
				int[] newTri={pIndex-1,pIndex-2,pIndex};
				indicesList.add(newTri);
			}
			else{
				//int[] pre=indicesList.get(size-1);
				int[] newTri={pIndex-2,pIndex-1,pIndex};
				indicesList.add(newTri);
			}
			isOdd=!isOdd;
		}
		begin++;
		return 0;
	}

	int numberOfDivisions = 10;
	
	public Mesh data;
	public void  tessellate(Mesh data){
		this.data=data;
		int count=data.indices.length/3;
		for (int i=0; i < count; i++){
			PNPatch(i);
		}
	}
	class PhongPatch
	{
		float termIJ;
		float termJK;
		float termIK;
	};
	float uTessAlpha=0.6f;
	public int gl_InvocationID=0;
	
	float[][] iPositions={null,null,null};
	float[][] iNormals={null,null,null};
	
	PhongPatch[] oPhongPatch={new PhongPatch(),new PhongPatch(),new PhongPatch()};

	float[] Pi ; //gl_in[0].gl_Position.xyz
	float[] Pj;  //gl_in[1].gl_Position.xyz
	float[] Pk ; //gl_in[2].gl_Position.xyz
	float PIi(int i, float[] q)
	{
		float[] q_minus_p = sub(q , iPositions[i]);//= q - gl_in[i].gl_Position.xyz;
		return q[gl_InvocationID] - dot(q_minus_p, iNormals[i])
		                          * iNormals[i][gl_InvocationID];
	}
	void  PNPatch(int faceNum){
		// Original points
		Pi =data.rawVertices[ data.indices[faceNum*3]].pos;
		Pj = data.rawVertices[ data.indices[faceNum*3+1]].pos;
		Pk = data.rawVertices[ data.indices[faceNum*3+2]].pos;
		iPositions[0]=Pi;iPositions[1]=Pj;iPositions[2]=Pk;
		// Original normals
		iNormals[0] = data.rawVertices[ data.indices[faceNum*3]].normal;
		iNormals[1] = data.rawVertices[ data.indices[faceNum*3+1]].normal;
		iNormals[2] = data.rawVertices[ data.indices[faceNum*3+2]].normal;
		for(gl_InvocationID=0;gl_InvocationID<3;gl_InvocationID++){
			oPhongPatch[gl_InvocationID].termIJ = PIi(0,Pj) + PIi(1,Pi);
			oPhongPatch[gl_InvocationID].termJK = PIi(1,Pk) + PIi(2,Pj);
			oPhongPatch[gl_InvocationID].termIK = PIi(2,Pi) + PIi(0,Pk);
		}
		
		float u = 0;
		float v = 0;
		float w = 1.0f-u-v;
		float maxV = 1.0f;
		float uTemp;

		float divisions = 1.0f / (float) numberOfDivisions;
		float precision = divisions / 10;

		if(faceNum > -1){
			while (u < 1){
				begin=0;//glBegin(GL_TRIANGLE_STRIP);
				v = 0;

				while (v < maxV - precision){
					w = 1.0f-u-v;
					/******************DS********************/
					// precompute squared tesscoords
//					float[] tc2 = mul(tc1,tc1);

					// compute texcoord and normal
//					oTexCoord =add( mul( gl_TessCoord[0],iTexCoord_s[0])
//					          , mul(gl_TessCoord[1],iTexCoord_s[1])
//					          , mul(gl_TessCoord[2],iTexCoord_s[2]) );
					float[]oNormal   =add( mul(u,iNormals[0])
					          , mul(v,iNormals[1])
					          , mul(w,iNormals[2]) );

					// interpolated position
					float[] barPos =add( mul( u,Pi)
					            , mul(v,Pj)
					            , mul(w,Pk) );

					// build terms
					PhongPatch[] iPhongPatch=oPhongPatch;
					float[] termIJ = float3(iPhongPatch[0].termIJ,
					                   iPhongPatch[1].termIJ,
					                   iPhongPatch[2].termIJ);
					float[] termJK = float3(iPhongPatch[0].termJK,
					                   iPhongPatch[1].termJK,
					                   iPhongPatch[2].termJK);
					float[] termIK = float3(iPhongPatch[0].termIK,
					                   iPhongPatch[1].termIK,
					                   iPhongPatch[2].termIK);

					// phong tesselated pos
					float[] phongPos   =add( mul( u*u,Pi)
					                , mul(v*v,Pj)
					                , mul(w*w,Pk)
					                , mul(u*v,termIJ)
					                , mul(v*w,termJK)
					                , mul(w*u,termIK)  );

					// final position
					float[] finalPos =add(  mul((1.0f-uTessAlpha),barPos) , mul(uTessAlpha,phongPos)  );
					normalize1(oNormal);
					addVertex(finalPos, oNormal);

					uTemp = u;
					u = u + divisions;

					w = 1.0f-u-v;
					/******************DS********************/
					// precompute squared tesscoords
//					float[] tc2 = mul(tc1,tc1);

					// compute texcoord and normal
//					oTexCoord =add( mul( gl_TessCoord[0],iTexCoord_s[0])
//					          , mul(gl_TessCoord[1],iTexCoord_s[1])
//					          , mul(gl_TessCoord[2],iTexCoord_s[2]) );
					oNormal   =add( mul(u,iNormals[0])
					          , mul(v,iNormals[1])
					          , mul(w,iNormals[2]) );

					// interpolated position
					barPos =add( mul( u,Pi)
					            , mul(v,Pj)
					            , mul(w,Pk) );

					// build terms
					iPhongPatch=oPhongPatch;
					termIJ = float3(iPhongPatch[0].termIJ,
					                   iPhongPatch[1].termIJ,
					                   iPhongPatch[2].termIJ);
					termJK = float3(iPhongPatch[0].termJK,
					                   iPhongPatch[1].termJK,
					                   iPhongPatch[2].termJK);
					termIK = float3(iPhongPatch[0].termIK,
					                   iPhongPatch[1].termIK,
					                   iPhongPatch[2].termIK);

					// phong tesselated pos
					phongPos   =add( mul( u*u,Pi)
					                , mul(v*v,Pj)
					                , mul(w*w,Pk)
					                , mul(u*v,termIJ)
					                , mul(v*w,termJK)
					                , mul(w*u,termIK)  );

					// final position
					finalPos =add(  mul((1.0f-uTessAlpha),barPos) , mul(uTessAlpha,phongPos)  );
					normalize1(oNormal);
					addVertex(finalPos, oNormal);

					u = uTemp;
					v = v + divisions;	
				}
				w = 1.0f-u-v;
				/******************DS********************/
				// precompute squared tesscoords
//				float[] tc2 = mul(tc1,tc1);

				// compute texcoord and normal
//				oTexCoord =add( mul( gl_TessCoord[0],iTexCoord_s[0])
//				          , mul(gl_TessCoord[1],iTexCoord_s[1])
//				          , mul(gl_TessCoord[2],iTexCoord_s[2]) );
				float[]oNormal   =add( mul(u,iNormals[0])
				          , mul(v,iNormals[1])
				          , mul(w,iNormals[2]) );

				// interpolated position
				float[] barPos =add( mul( u,Pi)
				            , mul(v,Pj)
				            , mul(w,Pk) );

				// build terms
				PhongPatch[] iPhongPatch=oPhongPatch;
				float[] termIJ = float3(iPhongPatch[0].termIJ,
				                   iPhongPatch[1].termIJ,
				                   iPhongPatch[2].termIJ);
				float[] termJK = float3(iPhongPatch[0].termJK,
				                   iPhongPatch[1].termJK,
				                   iPhongPatch[2].termJK);
				float[] termIK = float3(iPhongPatch[0].termIK,
				                   iPhongPatch[1].termIK,
				                   iPhongPatch[2].termIK);

				// phong tesselated pos
				float[] phongPos   =add( mul( u*u,Pi)
				                , mul(v*v,Pj)
				                , mul(w*w,Pk)
				                , mul(u*v,termIJ)
				                , mul(v*w,termJK)
				                , mul(w*u,termIK)  );

				// final position
				float[] finalPos =add(  mul((1.0f-uTessAlpha),barPos) , mul(uTessAlpha,phongPos)  );
				normalize1(oNormal);
				addVertex(finalPos, oNormal);
				
				maxV = maxV - divisions;
				u = u + divisions;
				//glEnd();
			}

			
		}
	}
}
