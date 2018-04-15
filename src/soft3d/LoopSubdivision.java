package soft3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import loader.xfile.XMesh;
import soft3d.util.Mesh;
/**
 * Loop曲面细分
 */
public class LoopSubdivision {

	public int[] indices;
	public float[] texCoords;
	public float[] normals;
	public float[] fVertices;
	public float[] verticesBuf;
	public float[] screenBuf;
	public float[] normalsBuf;
//	public void draw(SoftGraphics3D graphics3d) {
//		graphics3d.drawTriangleList(verticesBuf, indices, texCoords, normalsBuf, screenBuf, null);
//	}
	public void loadModel(XMesh mesh) {
		Mesh meshData= Mesh.create(mesh.vertices, mesh.indices);
		loadModel(meshData);
	}
	public void convertToMesh(){
		int triSize= triLib.size();
		indices=new int[triSize*3];
		int vSize= vLib.size();
		int indicesFrom=0;
		for (Triangle tri : triLib) {
			HalfEdge[] loop =tri.loop;
			
			indices[indicesFrom]= loop[0].tail;
			indices[indicesFrom+1]= loop[1].tail;
			indices[indicesFrom+2]= loop[2].tail;
			indicesFrom+=3;
			//tri.getNorm(vLib);
		}
		fVertices=new float[vSize*3];
		texCoords=new float[vSize*2];
		int from=0;
		int from2=0;
		for (Vertex ver : vLib) {
			texCoords[from2]=ver.uv[0];
			texCoords[from2+1]=ver.uv[1];
			fVertices[from]= ver.pos[0];
			fVertices[from+1]= ver.pos[1];
			fVertices[from+2]= ver.pos[2];
			from2+=2;
			from+=3;
		}
		verticesBuf=new float[vSize*3];
		//normalsBuf=new float[vSize*3];
		screenBuf=new float[vSize*2];
	}
//	public void update(){
//		final float[][] mat0=SoftGraphics3D.transform;
////		Matrix.loadIdentity(mat0);
////		Matrix.matrixMult(mat0, SoftGraphics3D.transform, mat0);
//		Matrix.transform(fVertices,mat0, verticesBuf);
//		//Matrix.transform(normals,mat0, normalsBuf);
//		//Vector3.normalizeAll(normalsBuf);
//		SoftGraphics3D.project(verticesBuf, screenBuf);
//		//SoftGraphics3D.getBounds(screenBuf, rect);
//	}
	class HalfEdge
	{
	    int tail;
		int newVerter;
		int triIndex;
	    HalfEdge twin;

	    HalfEdge()
		{
		  twin=null;
		  tail=newVerter=triIndex=-1;
		}
	    
	};
	class Vertex
    {
      float[] pos={0,0,0};
      float[] uv={0,0};
      HalfEdge hEdge;
      int adjCount;

         Vertex()
    	{
    	  hEdge=null;
    	  adjCount=0;
    	}
    };
	class Triangle
	{

	   HalfEdge[] loop;
	   float[] norm={0,0,0};
	public int[] vindices={0,0,0};
	public int findex;

	   Triangle()
	   {
	     loop=null;
	   }

	   void allocateLoop(int size)
	   {
	       loop=new HalfEdge[size];
	       for (int i = 0; i < loop.length; i++) {
	    	   loop[i]=new HalfEdge();
		}
	   }

	   void getNorm(List<Vertex> vLib)
	   { 
		   float[] v1={0,0,0},v2={0,0,0};
		   float base;

		   v1[0]=vLib.get(loop[1].tail).pos[0]-vLib.get(loop[0].tail).pos[0];
		   v1[1]=vLib.get(loop[1].tail).pos[1]-vLib.get(loop[0].tail).pos[1];
		   v1[2]=vLib.get(loop[1].tail).pos[2]-vLib.get(loop[0].tail).pos[2];
		   v2[0]=vLib.get(loop[2].tail).pos[0]-vLib.get(loop[0].tail).pos[0];
		   v2[1]=vLib.get(loop[2].tail).pos[1]-vLib.get(loop[0].tail).pos[1];
		   v2[2]=vLib.get(loop[2].tail).pos[2]-vLib.get(loop[0].tail).pos[2];

	        //vector_cross(norm,v1,v2);
		   Vec3.cross(v1, v2, norm);

		    base=(float)Math.sqrt(norm[0]*norm[0]+norm[1]*norm[1]+norm[2]*norm[2]);
	        norm[0]=norm[0]/base;
		    norm[1]=norm[1]/base;
		    norm[2]=norm[2]/base;
	   }
	};
	
	List<Triangle> triLib;
	List<Vertex> vLib;
	int edgeCount;
	float scale;
	float cx, cy, cz;

	///////////////// for test ////////////
        float testStop;

	///////////////// for test ////////////
        
        void computeNewEdgeVertex(float[] res,float[] p1,float[] p2,float[] p3,float[] p4)
    	{
    	   int len=res.length;
    	   for(int i=0;i<len;i++)
    	   {
    		 res[i]=0.375f*(p1[i]+p2[i])+0.125f*(p3[i]+p4[i]);
    	   }
    	}
        void computeNewEdgeVertex(float[] res,float[] p1,float[] p2)
    	{
        	int len=res.length;
    	   for(int i=0;i<len;i++)
    	   {
    		 res[i]=0.5f*(p1[i]+p2[i]);
    	   }
    	}
        void computeNewEdgeVertex(float[] res,float[] p1,float[] p2,float[] p3)
    	{
        	int len=res.length;
    	   for(int i=0;i<len;i++)
    	   {
    		 res[i]=0.4f*(p1[i]+p2[i])+0.2f*p3[i];
    	   }
    	}
        void computeBorderVertex(float[] res,float[] p1,float[] p2)
    	{
        	int len=res.length;
    	   for(int i=0;i<len;i++)
    	   {
    		 res[i]=0.75f*res[i]+0.125f*(p1[i]+p2[i]);
    	   }
    	}
        
        
        void loopSubdivision()
    	{
    	  if(triLib.size()==0)
    		 return;
    	  int i,j,vIndex,preVertexCount,preTriCount,twinPreV;
    	  int computeCount;
    	  //Map<Integer ,HalfEdge>::iterator iter;
    	   Triangle tri,twinTri;
    	   HalfEdge[] loop;
    	   float[] p1={0,0,0},p2={0,0,0},p3={0,0,0},p4={0,0,0};
    	   float[] t1={0,0},t2={0,0},t3={0,0,0},t4={0,0};
//    	   float[] p0={0,0,0,0};
    	   float vertexFactor;
     
    	   //test();

    	   /////////  计算边顶点 //////////////////////////
    	   preVertexCount=vIndex=vLib.size();
    	   for (int k = 0; k < edgeCount; k++) {
    		   vLib.add(new Vertex());
		}
    	   
          //vLib.resize(vLib.size()+edgeCount);
          for(i=0;i<triLib.size();i++)
    	  {
    		  loop=triLib.get(i).loop;

    		  //////  test  ///////////////
    			 if(vIndex>=vLib.size())
    			 {
    			     testStop=1.0f;break;///????
    			 }
    		   //////  test  ///////////////

    	     for(j=0;j<3;j++)
    		 {
    		   if(loop[j].newVerter>=0)
    			   continue;
    		   p1=vLib.get(loop[j].tail).pos;
    		   p2=vLib.get(loop[(j+1)%3].tail).pos;
    		   p3=vLib.get(loop[(j+2)%3].tail).pos;
    		   p4=null;
    		   t1=vLib.get(loop[j].tail).uv;
    		   t2=vLib.get(loop[(j+1)%3].tail).uv;
    		   t3=vLib.get(loop[(j+2)%3].tail).uv;
    		   t4=null;
    		   if(loop[j].twin!=null)
    		   {
    		     twinTri= triLib.get(loop[j].twin.triIndex);
    		     twinPreV=twinTri.loop[(index(loop[j].twin,twinTri.loop)+2)%3].tail;
    		     p4=vLib.get(twinPreV).pos;
    		     t4=vLib.get(twinPreV).uv;
    			 computeNewEdgeVertex(vLib.get(vIndex).pos,p1,p2,p3,p4);
    			 computeNewEdgeVertex(vLib.get(vIndex).uv,t1,t2,t3,t4);
    			 loop[j].newVerter=loop[j].twin.newVerter=vIndex;
    			 vLib.get(vIndex).adjCount=6;
    			 //vLib[vIndex].adjHalfEdge[]=NULL;
    		   }else
    		   {
    		     computeNewEdgeVertex(vLib.get(vIndex).pos,p1,p2);
    		     computeNewEdgeVertex(vLib.get(vIndex).uv,t1,t2);
    			 //computeNewEdgeVertex(vLib[vIndex].pos,p1,p2,p3);
    			 loop[j].newVerter=vIndex;
    			 vLib.get(vIndex).adjCount=4;
    		   }
    		   
    		   vLib.get(vIndex).hEdge=loop[j];
               vIndex++;
    		 }
    	  }

    	 /////////  计算以前顶点的新位置 //////////////////////////
          HalfEdge adjHEdge,firstAdj,nHEdge,pHEdge;
    	  int fAdjVertex,adjV;
          /*float (*oldPos)[3]=new float[preVertexCount][3];
    	    for(i=0;i<preVertexCount;i++)
    	  {
    		  vector_set(oldPos[i],vLib[i].pos);
    	  }*/
    	  float []oldPos=new float[preVertexCount*3];
    	  for(i=0;i<preVertexCount;i++)
    	  {
    		  vector_set( oldPos,i*3,vLib.get(i).pos);
    	  }
        
    	  for(i=0;i<preVertexCount;i++)
    	  {
    	     
    		 adjHEdge=firstAdj=vLib.get(i).hEdge;
    		 if(vLib.get(i).adjCount<=0)
    		 {
    			 continue;
    		 }else
    		 if(vLib.get(i).adjCount==2)
    		 {
    		   p1[0]= oldPos[firstAdj.tail*3];p1[1]= oldPos[firstAdj.tail*3+1];p1[2]= oldPos[firstAdj.tail*3+2];
    		   twinTri= triLib.get(firstAdj.triIndex);
    		   int i1=index(firstAdj,twinTri.loop);
    		   p2[0]= oldPos[twinTri.loop[(i1+2)%3].tail*3];
    		   p2[1]= oldPos[twinTri.loop[(i1+2)%3].tail*3+1];
    		   p2[2]= oldPos[twinTri.loop[(i1+2)%3].tail*3+2];
               computeBorderVertex(vLib.get(i).pos,p1,p2);
    		 }else
    		 {
    			 computeCount=0;
    			if(vLib.get(i).adjCount==3)
    			{
    			  vector_scale(vLib.get(i).pos,7.0f/16.0f);
    			  vertexFactor=3.0f/16.0f;
    			}else
    			{
    			  /*vertexFactor=0.375+0.25*cos(2*pai/vLib[i].adjCount);
                  vertexFactor*=vertexFactor;
    		      vertexFactor=0.725-vertexFactor;*/
                  vertexFactor=0.375f;
                  vector_scale(vLib.get(i).pos,1.0f-vertexFactor);
    			  vertexFactor/=(float)vLib.get(i).adjCount;
    			}

    			 fAdjVertex=adjV=adjHEdge.tail;

    			 do{
                      vLib.get(i).pos[0]+=vertexFactor*oldPos[adjV*3];
    			      vLib.get(i).pos[1]+=vertexFactor*oldPos[adjV*3+1];
    			      vLib.get(i).pos[2]+=vertexFactor*oldPos[adjV*3+2];
    				  computeCount++;
    				  if(adjHEdge==null)
    					  break;
    				  pHEdge= triLib.get(adjHEdge.triIndex).loop[(index(adjHEdge,triLib.get(adjHEdge.triIndex).loop)+2)%3];
    				  adjV=pHEdge.tail;
                      nHEdge=  triLib.get(adjHEdge.triIndex).loop[(index(adjHEdge,triLib.get(adjHEdge.triIndex).loop)+1)%3];
    		          adjHEdge=nHEdge.twin;
    				  
    		       }while(adjV!=fAdjVertex);

    		      if(adjHEdge==null)
    		      {
    		         adjHEdge=firstAdj=vLib.get(i).hEdge.twin;
    		         while(adjHEdge!=null)
    		         {
    			        twinTri= triLib.get(adjHEdge.triIndex);
    			        adjHEdge= twinTri.loop[(index(adjHEdge,twinTri.loop)+2)%3];
                        vLib.get(i).pos[0]+=vertexFactor*oldPos[adjHEdge.tail*3];
    			        vLib.get(i).pos[1]+=vertexFactor*oldPos[adjHEdge.tail*3+1];
    			        vLib.get(i).pos[2]+=vertexFactor*oldPos[adjHEdge.tail*3+2];
    			        adjHEdge=adjHEdge.twin;
    					 computeCount++;
    		         }
    		      }
    			   if(computeCount!= vLib.get(i).adjCount)
    			   { 
    			     testStop=1.0f;
    			   }
    		 }
    	  }

    	  oldPos=null;

    	  for(i=0;i<preVertexCount;i++)
    	  {
              vLib.get(i).hEdge=null;
    	  }

    	  ////////////   生成新的三角形   ////////////////////////
    	 //  vector<Triangle> triTemp(triLib);
           preTriCount=triLib.size();
    	   //triLib.resize(preTriCount*4);
           int iCount1= preTriCount*4-preTriCount;
           for (int l = 0; l < iCount1; l++) {
        	   triLib.add(new Triangle());
		}
    	   int newTriIndex;
    	   HalfEdge[]  newLoop;
    	  for(i=0;i<preTriCount;i++)
    	  {
    		  loop=triLib.get(i).loop;
    	     for(j=0;j<3;j++)
    		 {
    			newTriIndex=preTriCount+i*3+j;
    			triLib.get(newTriIndex).allocateLoop(3);
                newLoop=triLib.get(newTriIndex).loop;
                newLoop[0].tail=loop[j].tail;
    			newLoop[0].triIndex=newTriIndex;
    			newLoop[1].tail=loop[j].newVerter;
    			newLoop[1].triIndex=newTriIndex;
    			newLoop[2].tail=loop[(j+2)%3].newVerter;
    			newLoop[2].triIndex=newTriIndex;
    			triLib.get(newTriIndex).getNorm(vLib);
                if(vLib.get(loop[j].tail).hEdge==null)
    			 vLib.get(loop[j].tail).hEdge= newLoop[2];
    		 }
    	  }

    	   ////////////   计算新三角形的两个外twin     ////////////////////////
           HalfEdge[] twinNext, twinLoop;
    	   Triangle jTwinTri;
    	   int twinIndex,k;
           for(i=0;i<preTriCount;i++)
    	  {
    		 loop=triLib.get(i).loop;
    	     for(j=0;j<3;j++)
    		 {
    			newTriIndex=preTriCount+i*3+j;
    			if(triLib.get(newTriIndex).loop[0].twin==null&&loop[j].twin!=null)
    			{
    			  twinLoop=triLib.get(loop[j].twin.triIndex).loop;
    			  jTwinTri= triLib.get(preTriCount+loop[j].twin.triIndex*3+(index(loop[j].twin,twinLoop)+1)%3);
    			  triLib.get(newTriIndex).loop[0].twin= jTwinTri.loop[2];
    			  jTwinTri.loop[2].twin= triLib.get(newTriIndex).loop[0];

    			}

    			if(triLib.get(newTriIndex).loop[2].twin==null&&loop[(j+2)%3].twin!=null)
    			{
    			  twinLoop=triLib.get(loop[(j+2)%3].twin.triIndex).loop;
    			  jTwinTri= triLib.get(preTriCount+loop[(j+2)%3].twin.triIndex*3+(index(loop[(j+2)%3].twin,twinLoop))%3);
    		      triLib.get(newTriIndex).loop[2].twin= jTwinTri.loop[0];
    			  jTwinTri.loop[0].twin= triLib.get(newTriIndex).loop[2];
    			}
    		 }
    	  }

    	   ////////////   计算新三角形的一个内twin     ////////////////////////

    	  for(i=0;i<preTriCount;i++)
    	  {
    		 loop=triLib.get(i).loop;
    	     for(j=0;j<3;j++)
    		 {
    			newTriIndex=preTriCount+i*3+j;
    			loop[j].tail=loop[(j+2)%3].newVerter;
                //vLib[loop[j].newVerter].hEdge
    			loop[j].twin= triLib.get(newTriIndex).loop[1];
    			triLib.get(newTriIndex).loop[1].twin= loop[j];
    		 }
    		 triLib.get(i).getNorm(vLib);
             
    		 loop[0].newVerter=loop[1].newVerter=loop[2].newVerter=-1;
    		 //loop[0].twin=loop[1].twin=loop[2].twin=NULL;
             
    	  }
    	  edgeCount=edgeCount*2+preTriCount*3;
     
    	}
        void vector_set(float [] res,int i,float [] a)
        {
          res[i] = a[0];
          res[i+1] = a[1];
          res[i+2] = a[2];
        }
        void vector_scale(float[] res, float d)
        {
          res[0] *= d;
          res[1] *= d;
          res[2] *= d;
        }
        int index(HalfEdge item,HalfEdge[] array){
        	int to= array.length;
        	for (int i = 0; i < to; i++) {
				if (item==array[i]) {
					return i;
				}
			}
        	return -1;
        }
        
        
        void loadModel(Mesh mesh)
        {
  	       int i,j;
  		 float maxx, minx, maxy, miny, maxz, minz;
  		 float w,h,d;
  	       
  		   //GLMmodel::GLMgroup *g;
  		   Vertex v=new Vertex();
  		   Triangle tri;
  		   int[] vindex={0,0,0};
  		 Iterator<Entry<Integer, HalfEdge>> iter;
  		    //GLMmodel * glmModel=new GLMmodel(fn);
  		 int numvertices=mesh.rawVertices.length;
  		LinkedHashMap<Integer ,HalfEdge >[]adjHalfEdge=new LinkedHashMap[numvertices] ;
  		 for (int k = 0; k < numvertices; k++) {
  			adjHalfEdge[k]=(new LinkedHashMap<Integer ,HalfEdge>());
		}
  			edgeCount=0;
//  			for(i=0;i<triLib.size();i++)
//  			{
//  			  triLib.get(i).loop=null;
//  			}
  			vLib=new ArrayList<LoopSubdivision.Vertex>(numvertices);
  			int numtriangles=mesh.indices.length/3;
  			triLib=new ArrayList<LoopSubdivision.Triangle>(numtriangles);
  			for (int k = 0; k < numvertices; k++) {
  				vLib.add(new Vertex());
			}
  			
   		   for (int k = 0; k < numtriangles; k++) {
   			 triLib.add(new Triangle());
   		   }
  		   //vLib.resize(numvertices);
             
//  		   maxx = minx = glmModel->vertices[3];
//  	       maxy = miny = glmModel->vertices[4];
//  	       maxz = minz = glmModel->vertices[5];

  			float[] vertices=null;
  		   for(j=0;j<numvertices;j++)
  		   {
  			 vertices=mesh.rawVertices[j].pos;//j*3+3
  			 float[] uv=mesh.rawVertices[j].tex;
  			vector_set(vLib.get(j).uv,  uv,0);
  			 vector_set(vLib.get(j).pos,  vertices,0);
               vector_set(v.pos, vertices,0);
//  			 if (maxx < v.pos[0])
//  				 maxx =  v.pos[0];
//  			 else if (minx > v.pos[0]) 
//  				 minx =  v.pos[0];
//          
//  		     if (maxy < v.pos[1])
//  				 maxy =  v.pos[1];
//  			 else if (miny > v.pos[1]) 
//  				 miny =  v.pos[1];
//
//  		     if (maxz < v.pos[2])
//  				 maxz =  v.pos[2];
//  			 else if(minz > v.pos[2]) 
//  				 minz =  v.pos[2];
  		   }
            
//  		   cx = (maxx + minx) / 2.0;
//  	       cy = (maxy + miny) / 2.0;
//  	       cz = (maxz + minz) / 2.0;
//  		   w=maxx - minx;
//  		   h=maxy - miny;
//  		   d=maxz - minz;
//  		   scale = 2.5/ sqrt(w*w+h*h+d*d);

//  		   triLib.resize(numtriangles);
  		  
  		 float[] facetnorms=null;
  		Triangle[] triangles=null;
  		   for(j=0;j<numtriangles;j++)
  	       {
  			  tri= triLib.get(j);
  			  tri.allocateLoop(3);
  			int triangles_j_vindices=mesh.indices[j*3];
//  			  vector_set(tri.norm,  mesh.normals,j*3);
  			int[] vindices={mesh.indices[j*3],mesh.indices[j*3+1],mesh.indices[j*3+2]};
  			mesh.getFaceNormal(vindices, tri.norm);
//  			tri.vindices=vindices;
  			vindex=new int[]{vindices[0],vindices[1],vindices[2]};
  			  //vector_set(vindex, mesh.indices,triangles_j_vindices);
//  			  vindex[0]-=1;
//  			  vindex[1]-=1;
//  			  vindex[2]-=1;
  			  for(i=0;i<3;i++)
  			  { 
  			    tri.loop[i].tail=vindex[i];
  				tri.loop[i].triIndex=j;
  				if(vLib.get(vindex[(i+1)%3]).hEdge==null)
  				   vLib.get(vindex[(i+1)%3]).hEdge= tri.loop[i];
  			  }
  			  
  			Entry<Integer, HalfEdge> item=null;
  			  for(i=0;i<3;i++)
  			  {
  				    adjHalfEdge[vindex[(i+1)%3]].put(new Integer(vindex[i]),tri.loop[i]);
//  				  adjHalfEdge[vindex[i]].put(new Integer(vindex[(i+1)%3]),tri.loop[i]);
  				    //iter=adjHalfEdge.get(vindex[i]).get(new Integer(vindex[(i+1)%3]) );
  				  Set<Entry<Integer, HalfEdge>>  set=adjHalfEdge[vindex[i]].entrySet();
  				iter= set.iterator();
  				
  				Integer integer= new Integer(vindex[(i+1)%3]);
  				item=null;
  				while (iter.hasNext()) {
  					Entry<Integer, HalfEdge> e=iter.next();
  					
					if (e.getKey().equals(integer)) {
						item=e;break;
					}
				}
  				  //Entry<Integer, HalfEdge> iterO=adjHalfEdge[vindex[i]].get(   );
  					if(item!=null)//iter.hasNext()
  					{
  					  tri.loop[i].twin=item.getValue();
  					item.getValue().twin= tri.loop[i];
  					}else
  					{
  					  edgeCount++;
  					} 
  			  }
  		  }
  		   
  		   for(j=0;j< numvertices;j++)
  		   {
  			 vLib.get(j).adjCount=adjHalfEdge[j].size();
  		   }
  		 Entry<Integer, HalfEdge> item;
  		   for(j=0;j< numvertices;j++)
  		   {
  			// adjHalfEdge[j].entrySet().iterator();
                 for(iter=adjHalfEdge[j].entrySet().iterator();iter.hasNext();)
  			   {
                	 item= iter.next();
//                	 if (!iter.hasNext()) {
//						break;
//					}
  			       if(item.getValue().twin==null)
  				   {
  			    	 vLib.get(item.getValue().tail).adjCount++;
  				   }
  			   }
  		   }
            
  		   int ct=0;
  		   HalfEdge[] etemp=new HalfEdge[20];
  		    for(i=0;i<triLib.size();i++)
  			{
  				for(j=0;j<3;j++)
  			  if(triLib.get(i).loop[j].tail==3223)
  			  {
  				  etemp[ct++]= triLib.get(i).loop[j];
  			  }
  			}
  		  // vLib[3223].adjCount=6;
  		   adjHalfEdge=null;
  		  //test();
  	  }
		private void vector_set(float[] res, float[] a, int i) {
			if (res.length==2) {
				res[0] = a[i];
		          res[0+1] = a[i+1];
			}
			else{
			res[0] = a[i];
	          res[0+1] = a[i+1];
	          res[0+2] = a[i+2];
			}
		}
		void vector_set(int[] res, int[] a, int i) {
			res[0] = a[i];
	          res[0+1] = a[i+1];
	          res[0+2] = a[i+2];
		}
		float[] vector01={0,0,0},vector12={0,0,0};
		public void getFaceNormal(Vertex v0,Vertex v1,Vertex v2, float[] faceNormal) {
		vector01[0]=v1.pos[0]-v0.pos[0];
		vector01[1]=v1.pos[1]-v0.pos[1];
		vector01[2]=v1.pos[2]-v0.pos[2];
		vector12[0]=v2.pos[0]-v1.pos[0];
		vector12[1]=v2.pos[1]-v1.pos[1];
		vector12[2]=v2.pos[2]-v1.pos[2];
		Vec3.cross(vector01, vector12, faceNormal);
		Vec3.normalize(faceNormal);
		}
}
