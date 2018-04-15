package soft3d;
import static soft3d.Vec3.normalize1;
import static soft3d.Vec3.*;

import java.util.ArrayList;

import soft3d.util.Mesh;
/**
 * PN triangles 方法，把一个三角形细分为一个曲面 Bézier Surface
 * @author Administrator
 *
 */
public class PNTriangles {
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
	public void glBegin() {
		begin=0;
	}
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
	void  PNPatch(int faceNum){

		double[] bpointA={0,0,0};
		double[] npointA={0,0,0};

		boolean toggleU = false;

		// Original points
		float[] b300 =data.rawVertices[ data.indices[faceNum*3]].pos;
		float[] b030 = data.rawVertices[ data.indices[faceNum*3+1]].pos;
		float[] b003 = data.rawVertices[ data.indices[faceNum*3+2]].pos;

		// Original normals
		float[] n200 = data.rawVertices[ data.indices[faceNum*3]].normal;
		float[] n020 = data.rawVertices[ data.indices[faceNum*3+1]].normal;
		float[] n002 = data.rawVertices[ data.indices[faceNum*3+2]].normal;
//??????????????????????????????????????????????????
//		normalize1(n200);
//		normalize1(n020);
//		normalize1(n002);

		float[] bA = sub(b030, b300);//(b030 - b300);
		float[] nA = add(n200,n020);//(n200 + n020);
		float[] bB = sub(b003, b030);//(b003 - b030);
		float[] nB =add(n020,n002);// (n020 + n002);
		float[] bC = sub(b003, b300);//(b003 - b300);
		float[] nC = add(n002,n200);//(n002 + n200);

		double bAS = getSquaredLength(bA);
		double bBS = getSquaredLength(bB);
		double bCS = getSquaredLength(bC);

		if(bAS == 0)
			bAS = 1;
		if(bBS == 0)
			bBS = 1;
		if(bCS == 0)
			bCS = 1;

		float[] n110 =sub(nA ,mul(dot(bA , nA) / bAS * 2 , bA ) );// n110 = nA - (((bA * nA) / bAS) * 2) * bA;
		float[] n011 =sub( nB , mul(dot(bB , nB) / bBS * 2 , bB) );//n011 = nB - (((bB * nB) / bBS) * 2) * bB;
		float[] n101 =sub( nC ,  mul(dot(bC , nC) / bCS* 2 , bC) );//n101 = nC - (((bC * nC) / bCS) * 2) * bC;
		//?????????????????????????????
//		normalize1(n110);normalize1(n011);normalize1(n101);

		// Six secondary control points
		float[] b210 = div( add(mul(b300 , 2) , b030 ,neg(mul( dot(n200 , sub(b030 , b300)) , n200 ) )),3);//((b300 * 2) + b030 - (n200 * (b030 - b300)) * n200) / 3;
		float[] b120 = div(add(mul(b030 , 2) , b300 ,neg( mul(dot(n020 , sub(b300 , b030)) , n020)) ), 3 );//((b030 * 2) + b300 - (n020 * (b300 - b030)) * n020) / 3;

		float[] b021 = div(add(mul(b030 , 2) , b003 ,neg (mul(dot(n020 ,sub (b003 , b030)) , n020)) ), 3);
		float[] b012 = div(add(mul(b003 , 2) , b030 ,neg( mul(dot(n002 , sub(b030 , b003)) , n002)) ), 3);//((b003 * 2) + b030 - (n002 * (b030 - b003)) * n002) / 3;

		float[] b102 = div(add(mul(b003 , 2) , b300 ,neg( mul(dot(n002 , sub(b300 , b003)) , n002))) , 3);//((b003 * 2) + b300 - (n002 * (b300 - b003)) * n002) / 3;
		float[] b201 = div(add(mul(b300 , 2) , b003 ,neg( mul(dot(n200 , sub(b003 , b300)) , n200))), 3);//b201 = ((b300 * 2) + b003 - (n200 * (b003 - b300)) * n200) / 3;

		// Center control point
		float[] b111 = div(add(b210 , b120 , b102 , b201 , b021 , b012) , 4);//((b210 + b120 + b102 + b201 + b021 + b012) / 4) - ((b300 + b030 + b003) / 6);
		b111 =sub( b111, div(add(b300 , b030 , b003) , 6));
		double u = 0;
		double v = 0;
		double w = 1.0-u-v;
		double maxV = 1.0;
		double uTemp;

		double divisions = 1.0 / (double) numberOfDivisions;
		double precision = divisions / 10;

		float[] bpoint;
		float[] npoint;

		if(faceNum > -1){
			while (u < 1){
				begin=0;//glBegin(GL_TRIANGLE_STRIP);
				v = 0;

				while (v < maxV - precision){
					w = 1.0-u-v;
//					bpoint = b300*(u*u*u)+b030*(v*v*v)+b003*(w*w*w)+b210*(3*u*u*v)+ 
//							 b201*(3*u*u*w)+b120*(3*u*v*v)+b021*(3*v*v*w)+b012*(3*v*w*w)+ 
//							 b102*(3*u*w*w)+b111*(6*u*v*w);
//					npoint = n200 * (u*u) + n020 * (v*v) + n002 * (w*w) + 
//					 n110 * (u*v) + n101 * (u*w) + n011 * (v*w);
					bpoint =add( mul(b300,(u*u*u)), mul(b030,(v*v*v)) , mul(b003,(w*w*w)) , mul(b210,(3*u*u*v)), 
							 mul(b201,(3*u*u*w)) , mul(b120,(3*u*v*v)) , mul(b021,(3*v*v*w)) , mul(b012,(3*v*w*w)),
							 mul(b102,(3*u*w*w)) , mul(b111,(6*u*v*w)));
					npoint =add( mul(n200 , (u*u)) , mul(n020 , (v*v)) , mul(n002 , (w*w)) ,
							mul(n110 , (u*v)) , mul(n101 , (u*w)) , mul(n011 , (v*w)) );

					normalize1(npoint);

//					bpoint.toArray(bpointA);
//					npoint.toArray(npointA);
//					glNormal3dv(npointA);
//					glVertex3dv(bpointA);
					addVertex(bpoint, npoint);

					uTemp = u;
					u = u + divisions;

					w = 1.0-u-v;
//					bpoint = b300*(u*u*u)+b030*(v*v*v)+b003*(w*w*w)+b210*(3*u*u*v)+ 
//							 b201*(3*u*u*w)+b120*(3*u*v*v)+b021*(3*v*v*w)+b012*(3*v*w*w)+ 
//							 b102*(3*u*w*w)+b111*(6*u*v*w);
//
//					npoint = n200 * (u*u) + n020 * (v*v) + n002 * (w*w) + 
//							 n110 * (u*v) + n101 * (u*w) + n011 * (v*w);
					bpoint =add( mul(b300,(u*u*u)), mul(b030,(v*v*v)) , mul(b003,(w*w*w)) , mul(b210,(3*u*u*v)), 
							 mul(b201,(3*u*u*w)) , mul(b120,(3*u*v*v)) , mul(b021,(3*v*v*w)) , mul(b012,(3*v*w*w)),
							 mul(b102,(3*u*w*w)) , mul(b111,(6*u*v*w)));
					npoint =add( mul(n200 , (u*u)) , mul(n020 , (v*v)) , mul(n002 , (w*w)) ,
							mul(n110 , (u*v)) , mul(n101 , (u*w)) , mul(n011 , (v*w)) );

					normalize1(npoint);

//					bpoint.toArray(bpointA);
//					npoint.toArray(npointA);
//					glNormal3dv(npointA);
//					glVertex3dv(bpointA);
					addVertex(bpoint, npoint);

					u = uTemp;
					v = v + divisions;	
				}
				w = 1.0-u-v;
//				bpoint = b300*(u*u*u)+b030*(v*v*v)+b003*(w*w*w)+b210*(3*u*u*v)+ 
//						 b201*(3*u*u*w)+b120*(3*u*v*v)+b021*(3*v*v*w)+b012*(3*v*w*w)+ 
//						 b102*(3*u*w*w)+b111*(6*u*v*w);
//
//				npoint = n200 * (u*u) + n020 * (v*v) + n002 * (w*w) + 
//						 n110 * (u*v) + n101 * (u*w) + n011 * (v*w);
				bpoint =add( mul(b300,(u*u*u)), mul(b030,(v*v*v)) , mul(b003,(w*w*w)) , mul(b210,(3*u*u*v)), 
						 mul(b201,(3*u*u*w)) , mul(b120,(3*u*v*v)) , mul(b021,(3*v*v*w)) , mul(b012,(3*v*w*w)),
						 mul(b102,(3*u*w*w)) , mul(b111,(6*u*v*w)));
				npoint =add( mul(n200 , (u*u)) , mul(n020 , (v*v)) , mul(n002 , (w*w)) ,
						mul(n110 , (u*v)) , mul(n101 , (u*w)) , mul(n011 , (v*w)) );
				normalize1(npoint);

//				bpoint.toArray(bpointA);
//				npoint.toArray(npointA);
//				glNormal3dv(npointA);
//				glVertex3dv(bpointA);
				addVertex(bpoint, npoint);
				
				maxV = maxV - divisions;
				u = u + divisions;
				//glEnd();
			}

			
		}
	}
}
