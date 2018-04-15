package loader.ogre;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import loader.ogre.mesh.OgreMesh;
import loader.ogre.mesh.VertexBuffer;
import loader.ogre.mesh.VertexDeclaration;


public class OgreMeshSerializer_v1_4 {
	static int MSTREAM_OVERHEAD_SIZE = 2 + 4;
	private int mCurrentstreamLen;
	
	public static void main(String[] s) throws IOException {
		OgreMeshSerializer_v1_4 v1_4=new OgreMeshSerializer_v1_4();
		FileInputStream stream=new FileInputStream(System.getProperty("user.dir")+"\\models\\°¢±Ì.mesh");
		v1_4.importMesh(stream, new OgreMesh());
	}
	void importMesh(InputStream stream,OgreMesh pMesh) throws IOException
    {
		// Determine endianness (must be the first thing we do!)
		//determineEndianness(stream);

        // Check header
        readFileHeader(stream);

         int streamID;
        while(stream.available()>0)
        {
            streamID = readChunk(stream);
            switch (streamID)
            {
            case M_MESH:
                readMesh(stream, pMesh);
                break;
			}

        }
    }
	private int readChunk(InputStream stream) throws IOException {
		int i= readShort(stream);
		mCurrentstreamLen= readInt(stream);
		return i;
	}
	private void readFileHeader(InputStream stream) throws IOException {
		int i=readShort(stream);
		String string=readString(stream);
		
	}
	void readGeometry(InputStream stream,OgreMesh pMesh,
	        Object dest) throws IOException//VertexData
	    {

	        int dest_vertexStart = 0;

	         int vertexCount = 0;
	         vertexCount= readInt(stream);
	        int dest_vertexCount = vertexCount;

	        // Find optional geometry streams
	        if (stream.available()>0)
	        {
	             int streamID = readChunk(stream);
	            while(stream.available()>0 &&
	                (streamID == M_GEOMETRY_VERTEX_DECLARATION ||
	                 streamID == M_GEOMETRY_VERTEX_BUFFER ))
	            {
	                switch (streamID)
	                {
	                case M_GEOMETRY_VERTEX_DECLARATION:
	                    readGeometryVertexDeclaration(stream, pMesh, dest);
	                    break;
	                case M_GEOMETRY_VERTEX_BUFFER:
	                    readGeometryVertexBuffer(stream, pMesh, dest,vertexCount);
	                    break;
	                }
	                // Get next stream
	                if (stream.available()>0)
	                {
	                    streamID = readChunk(stream);
	                }
	            }
	            if (stream.available()>0)
	            {
	                // Backpedal back to start of non-submesh stream
	                stream.skip(-MSTREAM_OVERHEAD_SIZE);
	            }
	        }

			// Perform any necessary colour conversion for an active rendersystem
//			if (Root::getSingletonPtr() && Root::getSingleton().getRenderSystem())
//			{
//				// We don't know the source type if it's VET_COLOUR, but assume ARGB
//				// since that's the most common. Won't get used unless the mesh is
//				// ambiguous anyway, which will have been warned about in the log
//				dest->convertPackedColour(VET_COLOUR_ARGB, 
//					VertexElement::getBestColourVertexElementType());
//			}
	    }
	    private int readInt(InputStream stream) throws IOException {
	    	byte[] b=new byte[4];
	    	stream.read(b);
	    	
			int i=(b[3]&0xff)<<24;
				i+=	(b[2]&0xff)<<16;
				i+=(b[1]&0xff)<<8;
				i+=(b[0]&0xff);
			return i;
		
	}
		//---------------------------------------------------------------------
	    void readGeometryVertexDeclaration(InputStream stream,
	        OgreMesh pMesh, Object dest) throws IOException//VertexData
	    {
	        // Find optional geometry streams
	        if (stream.available()>0)
	        {
	             int streamID = readChunk(stream);
	            while(stream.available()>0 &&
	                (streamID == M_GEOMETRY_VERTEX_ELEMENT ))
	            {
	                switch (streamID)
	                {
	                case M_GEOMETRY_VERTEX_ELEMENT:
	                    readGeometryVertexElement(stream, pMesh, dest);
	                    break;
	                }
	                // Get next stream
	                if (stream.available()>0)
	                {
	                    streamID = readChunk(stream);
	                }
	            }
	            if (stream.available()>0)
	            {
	                // Backpedal back to start of non-submesh stream
	                stream.skip(-MSTREAM_OVERHEAD_SIZE);
	            }
	        }

		}
	    //---------------------------------------------------------------------
	    void readGeometryVertexElement(InputStream stream,
	        OgreMesh pMesh, Object dest) throws IOException//VertexData
	    {
			 int source, offset, index, tmp;
			short VertexElementType_vType;
			short VertexElementSemantic_vSemantic;
			VertexDeclaration declaration=new VertexDeclaration();
			// unsigned short source;  	// buffer bind source
			declaration.source=readShort(stream);
			// unsigned short type;    	// VertexElementType
			declaration.type=readShort(stream);
//			vType = static_cast<VertexElementType>(tmp);
			// unsigned short semantic; // VertexElementSemantic
			declaration.semantic=readShort(stream);
			//vSemantic = static_cast<VertexElementSemantic>(tmp);
			// unsigned short offset;	// start offset in buffer in bytes
			declaration.offset=readShort(stream);
			// unsigned short index;	// index of the semantic
			declaration.semanticIndex=readShort(stream);

			if (pMesh.declarations==null) {
				pMesh.declarations=new ArrayList<VertexDeclaration>();
			}
			pMesh.declarations.add(declaration);
			//dest->vertexDeclaration->addElement(source, offset, vType, vSemantic, index);

//			if (vType == VET_COLOUR)
//			{
//				//LogManager::getSingleton().stream()
//					<< "Warning: VET_COLOUR element type is deprecated, you should use "
//					<< "one of the more specific types to indicate the byte order. "
//					<< "Use OgreMeshUpgrade on " +" as soon as possible. ";
//			}

		}
	    private int readShort(InputStream stream ) throws IOException {
	    	byte[] b=new byte[2];
	    	stream.read(b);
	    	int r= (b[1]&0xff)<<8;
	    	r+=(b[0]&0xff);
			return r;
			
		}
		//---------------------------------------------------------------------
	    void readGeometryVertexBuffer(InputStream stream,
	        OgreMesh pMesh, Object dest,int vertexCount) throws IOException//VertexData
	    {
	    	VertexBuffer vertexBuffer=new VertexBuffer();
			 int bindIndex, vertexSize;
			// unsigned short bindIndex;	// Index to bind this buffer to
			 vertexBuffer.bindIndex=bindIndex=readShort(stream);
			// unsigned short vertexSize;	// Per-vertex size, must agree with declaration at this index
			 vertexSize=readShort(stream);

			// Check for vertex data header
			 int headerID;
			headerID = readChunk(stream);
			if (headerID != M_GEOMETRY_VERTEX_BUFFER_DATA)
			{
				throw new RuntimeException( "Can't find vertex buffer data area"+
	            	"MeshSerializerImpl::readGeometryVertexBuffer");
			}
			// Check that vertex size agrees
//			if (dest_vertexDeclaration_getVertexSize(bindIndex) != vertexSize)
//			{
//				OGRE_EXCEPT(Exception::ERR_INTERNAL_ERROR, "Buffer vertex size does not agree with vertex declaration",
//	            	"MeshSerializerImpl::readGeometryVertexBuffer");
//			}

			// Create / populate vertex buffer
			
			//int vertexCount = 0;
			vertexBuffer.byteBuffer = new byte[vertexCount * vertexSize];
	        stream.read(vertexBuffer.byteBuffer,0, vertexCount * vertexSize);
	        
	        vertexBuffer.vertexCount=vertexCount;
	        pMesh.declarations.get(bindIndex).vertexBuffer=vertexBuffer;
			

		}
	    //---------------------------------------------------------------------
		void readSubMeshNameTable(InputStream stream,Object pMesh) throws IOException
		{
			// The map for
			Map< Integer, String>  subMeshNames = new LinkedHashMap<Integer, String>();
			 int streamID, subMeshIndex;

			// Need something to store the index, and the objects name
			// This table is a method that imported meshes can retain their naming
			// so that the names established in the modelling software can be used
			// to get the sub-meshes by name. The exporter must support exporting
			// the optional stream M_SUBMESH_NAME_TABLE.

	        // Read in all the sub-streams. Each sub-stream should contain an index and Ogre::String for the name.
			if (stream.available()>0)
			{
				streamID = readChunk(stream);
				while(stream.available()>0 && (streamID == M_SUBMESH_NAME_TABLE_ELEMENT ))
				{
					// Read in the index of the submesh.
					subMeshIndex=readShort(stream);
					// Read in the String and map it to its index.
					subMeshNames.put(subMeshIndex, readString(stream));

					// If we're not end of file get the next stream ID
					if (stream.available()>0)
						streamID = readChunk(stream);
				}
				if (stream.available()>0)
				{
					// Backpedal back to start of stream
					stream.skip(-MSTREAM_OVERHEAD_SIZE);
				}
			}

			// Set all the submeshes names
			// ?

			// Loop through and save out the index and names.
//			map<unsigned short, String>::type::const_iterator it = subMeshNames.begin();
//
//			while(it != subMeshNames.end())
//			{
//				// Name this submesh to the stored name.
//				pMesh->nameSubMesh(it->second, it->first);
//				++it;
//			}



		}
	    private String readString(InputStream stream) throws IOException {
	    	byte[] bytes=new byte[2];
	    	
	    	StringBuilder buffer=new StringBuilder();
	    	while (stream.available()>0) {
	    		stream.read(bytes);
	    		if (bytes[0]==0x0a) {
	    			stream.skip(-1);
					break;
				}
	    		if (bytes[1]==0x0a) {
	    			buffer.append(new String(bytes).charAt(0));
					break;
				}
	    		buffer.append(new String(bytes,Charset.forName("GBK")));
			}
			return buffer.toString();
		}
		//---------------------------------------------------------------------
	    void readMesh(InputStream stream, OgreMesh pMesh) throws IOException
	    {
	        // Never automatically build edge lists for this version
	        // expect them in the file or not at all
	        //pMesh->mAutoBuildEdgeLists = false;

			// bool skeletallyAnimated
//			bool skeletallyAnimated;
//			readBools(stream, skeletallyAnimated, 1);
			stream.read();
	        // Find all substreams
	        if (stream.available()>0)
	        {
	             int streamID = readChunk(stream);
	            while(stream.available()>0 &&
	                (streamID == M_GEOMETRY ||
					 streamID == M_SUBMESH ||
	                 streamID == M_MESH_SKELETON_LINK ||
	                 streamID == M_MESH_BONE_ASSIGNMENT ||
					 streamID == M_MESH_LOD ||
	                 streamID == M_MESH_BOUNDS ||
					 streamID == M_SUBMESH_NAME_TABLE ||
					 streamID == M_EDGE_LISTS ||
					 streamID == M_POSES ||
					 streamID == M_ANIMATIONS ||
					 streamID == M_TABLE_EXTREMES))
	            {
	                switch(streamID)
	                {
					case M_GEOMETRY:
						//pMesh->sharedVertexData = OGRE_NEW VertexData();
						try {
							Object sharedVertexData = null;
							readGeometry(stream, pMesh, sharedVertexData);//pMesh->sharedVertexData
						}
						catch (Exception  e)
						{
							if (e.getMessage().equals( "ERR_ITEM_NOT_FOUND"))
							{
								// duff geometry data entry with 0 vertices
								//OGRE_DELETE pMesh->sharedVertexData;
								//pMesh->sharedVertexData = 0;
								// Skip this stream (pointer will have been returned to just after header)
								stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
							}
							else
							{
								//throw;
							}
						}
						break;
	                case M_SUBMESH:
	                	
	                	if (pMesh.subMeshs==null) {
	                		pMesh.subMeshs=new ArrayList<OgreMesh>();
						}
	                	OgreMesh subMesh=new OgreMesh();
	                	pMesh.subMeshs.add(subMesh);
	                    readSubMesh(stream, subMesh);
	                    break;
	                case M_MESH_SKELETON_LINK:
	                    readSkeletonLink(stream, pMesh);
	                    break;
	                case M_MESH_BONE_ASSIGNMENT:
	                    readMeshBoneAssignment(stream, pMesh);
	                    break;
	                case M_MESH_LOD:
	                	stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
						//readMeshLodInfo(stream, pMesh);
						break;
	                case M_MESH_BOUNDS:
	                    readBoundsInfo(stream, pMesh);
	                    break;
					case M_SUBMESH_NAME_TABLE:
	    	            readSubMeshNameTable(stream, pMesh);
						break;
	                case M_EDGE_LISTS:
	                	stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
	                    //readEdgeList(stream, pMesh);
	                    break;
					case M_POSES:
						stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
						//readPoses(stream, pMesh);
						break;
					case M_ANIMATIONS:
						stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
						//readAnimations(stream, pMesh);
	                    break;
	                case M_TABLE_EXTREMES:
	                	stream.skip(mCurrentstreamLen - MSTREAM_OVERHEAD_SIZE);
	                    //readExtremes(stream, pMesh);
	                    break;
	                }

	                if (stream.available()>0)
	                {
	                    streamID = readChunk(stream);
	                }

	            }
	            if (stream.available()>0)
	            {
	                // Backpedal back to start of stream
	                stream.skip(-MSTREAM_OVERHEAD_SIZE);
	            }
	        }

	    }
	    //---------------------------------------------------------------------
	    void readSubMesh(InputStream stream, OgreMesh pMesh ) throws IOException
	    {
	         int streamID;

	        //SubMesh* sm = pMesh->createSubMesh();

	        // char* materialName
	        String materialName = readString(stream);
//			if(listener)
//				listener->processMaterialName(pMesh, &materialName);
//	        sm->setMaterialName(materialName, pMesh->getGroup());

	        boolean useSharedVertices;
	        useSharedVertices= readBool(stream);

	        //sm->indexData->indexStart = 0;
	         int indexCount = 0;
	         indexCount= readInt(stream);
	        

	        int[] ibuf;
	         boolean idx32bit;
//	        bool idx32bit;
	         idx32bit=readBool(stream);
	        if (indexCount > 0)
	        {
	            if (idx32bit)
	            {
	                //int[] ibuf ;
	                 int faceVertexIndices;
	                 int[] pIdx =new int[indexCount];
		             for (int i = 0; i <indexCount; i++) {
		            	 pIdx[i]=readInt(stream);
		             }
		             pMesh.faceVertexIndices=pIdx;

	            }
	            else // 16-bit
	            {
//	                ibuf = HardwareBufferManager::getSingleton().
//	                    createIndexBuffer(
//	                        HardwareIndexBuffer::IT_16BIT,
//	                        sm->indexData->indexCount,
//	                        pMesh->mIndexBufferUsage,
//						    pMesh->mIndexBufferShadowBuffer);
	                int faceVertexIndices;
	                 int[] pIdx =new int[indexCount];
	               for (int i = 0; i <indexCount; i++) {
	            	   pIdx[i]=  readShort(stream);
				}
	               pMesh.faceVertexIndices=pIdx;
	                
	            }
	        }
	        //indexBuffer = ibuf;

	        // M_GEOMETRY stream (Optional: present only if useSharedVertices = false)
	        if (!useSharedVertices)
	        {
	            streamID = readChunk(stream);
	            if (streamID != M_GEOMETRY)
	            {
	                throw new  RuntimeException( "Missing geometry data in mesh file");
	            }
	            //sm->vertexData = OGRE_NEW VertexData();
	            Object vertexData=null;
	            readGeometry(stream, pMesh, vertexData);
	        }


	        // Find all bone assignments, submesh operation, and texture aliases (if present)
	        if (stream.available()>0)
	        {
	            streamID = readChunk(stream);
	            while(stream.available()>0 &&
	                (streamID == M_SUBMESH_BONE_ASSIGNMENT ||
	                 streamID == M_SUBMESH_OPERATION ||
	                 streamID == M_SUBMESH_TEXTURE_ALIAS))
	            {
	            	Object sm=null;
	                switch(streamID)
	                {
	                case M_SUBMESH_OPERATION:
	                    readSubMeshOperation(stream, pMesh, sm);
	                    break;
	                case M_SUBMESH_BONE_ASSIGNMENT:
	                    readSubMeshBoneAssignment(stream, pMesh, sm);
	                    break;
	                case M_SUBMESH_TEXTURE_ALIAS:
	                    readSubMeshTextureAlias(stream, pMesh, sm);
	                    break;
	                }

	                if (stream.available()>0)
	                {
	                    streamID = readChunk(stream);
	                }

	            }
	            if (stream.available()>0)
	            {
	                // Backpedal back to start of stream
	                stream.skip(-MSTREAM_OVERHEAD_SIZE);
	            }
	        }


	    }
	    private boolean readBool(InputStream stream) throws IOException {
			int b= stream.read();
			if (b==1) {
				return true;
			}
			return false;
		}
		//---------------------------------------------------------------------
	    void readSubMeshOperation(InputStream stream,
	       Object pMesh, Object sm) throws IOException//SubMesh
	    {
	        // unsigned short operationType
	         int opType;
	         opType=readShort(stream);
	        //sm->operationType = static_cast<RenderOperation::OperationType>(opType);
	    }
	    //---------------------------------------------------------------------
	    void readSubMeshTextureAlias(InputStream stream,Object pMesh, Object sub) throws IOException//SubMesh
	    {
	        String aliasName = readString(stream);
	        String textureName = readString(stream);
	        //sub->addTextureAlias(aliasName, textureName);
	    }
	    
	    void readSkeletonLink(InputStream stream, OgreMesh pMesh) throws IOException
	    {
	        String skelName = readString(stream);
	        if (pMesh.skeletonLinks==null) {
	        	pMesh.skeletonLinks=new ArrayList<String>();
			}
	        pMesh.skeletonLinks.add(skelName);
	    }
	    //---------------------------------------------------------------------
	    void readTextureLayer(InputStream stream, Object pMesh,
	        Object pMat)//MaterialPtr
	    {
	        // Material definition section phased out of 1.1
	    }
	    
	    void readMeshBoneAssignment(InputStream stream, OgreMesh pMesh) throws IOException
	    {
	        //VertexBoneAssignment assign;

	         int vertexIndex;
	         vertexIndex= readInt(stream);
	         int boneIndex;
	         boneIndex= readShort(stream);
	         float weight;
	         weight= readFloat(stream);
	         
	         float[] e=new float[]{vertexIndex,boneIndex,weight};

	        if (pMesh.assignments==null) {
	        	pMesh.assignments=new ArrayList<float[]>();
			}
	        pMesh.assignments.add(e);
	    }
	    private float readFloat(InputStream stream) throws IOException {
	    	int bits= readInt(stream);
	    	float f= Float.intBitsToFloat(bits);
			return f;
		}
		//---------------------------------------------------------------------
	    void readSubMeshBoneAssignment(InputStream stream,
	        Object pMesh, Object sub) throws IOException//SubMesh
	    {
	        //VertexBoneAssignment assign;

	         int vertexIndex;
	         vertexIndex=readInt(stream);
	         int boneIndex;
	         boneIndex= readShort(stream);
	         float weight;
	         weight= readFloat(stream);

	        //sub->addBoneAssignment(assign);

	    }
	    
	    
	    void readBoundsInfo(InputStream stream, Object pMesh) throws IOException
	    {
	        float[] min = new float[3], max;
	        // float minx, miny, minz
	        min[0]=readFloat(stream);
	        min[0]=readFloat(stream);
	        min[0]=readFloat(stream);
	        // float maxx, maxy, maxz
	        readFloat(stream);
	        readFloat(stream);
	        readFloat(stream);
	        //AxisAlignedBox box(min, max);
	        //pMesh->_setBounds(box, true);
	        // float radius
	        float radius;
	        radius=readFloat(stream);
	        //pMesh->_setBoundingSphereRadius(radius);



	    }
	    
		
	public final static int M_HEADER                = 0x1000,
            // char*          version           : Version number check
        M_MESH                = 0x3000,
			// bool skeletallyAnimated   // important flag which affects h/w buffer policies
            // Optional M_GEOMETRY chunk
            M_SUBMESH             = 0x4000, 
                // char* materialName
                // bool useSharedVertices
                // unsigned int indexCount
                // bool indexes32Bit
                // unsigned int* faceVertexIndices (indexCount)
                // OR
                // unsigned short* faceVertexIndices (indexCount)
                // M_GEOMETRY chunk (Optional: present only if useSharedVertices = false)
                M_SUBMESH_OPERATION = 0x4010, // optional, trilist assumed if missing
                    // unsigned short operationType
                M_SUBMESH_BONE_ASSIGNMENT = 0x4100,
                    // Optional bone weights (repeating section)
                    // unsigned int vertexIndex;
                    // unsigned short boneIndex;
                    // float weight;
    			// Optional chunk that matches a texture name to an alias
                // a texture alias is sent to the submesh material to use this texture name
                // instead of the one in the texture unit with a matching alias name
                M_SUBMESH_TEXTURE_ALIAS = 0x4200, // Repeating section
                    // char* aliasName;
                    // char* textureName;

            M_GEOMETRY          = 0x5000, // NB this chunk is embedded within M_MESH and M_SUBMESH
                // unsigned int vertexCount
				M_GEOMETRY_VERTEX_DECLARATION = 0x5100,
					M_GEOMETRY_VERTEX_ELEMENT = 0x5110, // Repeating section
						// unsigned short source;  	// buffer bind source
						// unsigned short type;    	// VertexElementType
						// unsigned short semantic; // VertexElementSemantic
						// unsigned short offset;	// start offset in buffer in bytes
						// unsigned short index;	// index of the semantic (for colours and texture coords)
				M_GEOMETRY_VERTEX_BUFFER = 0x5200, // Repeating section
					// unsigned short bindIndex;	// Index to bind this buffer to
					// unsigned short vertexSize;	// Per-vertex size, must agree with declaration at this index
					M_GEOMETRY_VERTEX_BUFFER_DATA = 0x5210,
						// raw buffer data
            M_MESH_SKELETON_LINK = 0x6000,
                // Optional link to skeleton
                // char* skeletonName           : name of .skeleton to use
            M_MESH_BONE_ASSIGNMENT = 0x7000,
                // Optional bone weights (repeating section)
                // unsigned int vertexIndex;
                // unsigned short boneIndex;
                // float weight;
            M_MESH_LOD = 0x8000,
                // Optional LOD information
                // string strategyName;
                // unsigned short numLevels;
                // bool manual;  (true for manual alternate meshes, false for generated)
                M_MESH_LOD_USAGE = 0x8100,
                // Repeating section, ordered in increasing depth
				// NB LOD 0 (full detail from 0 depth) is omitted
				// LOD value - this is a distance, a pixel count etc, based on strategy
                // float lodValue;
                    M_MESH_LOD_MANUAL = 0x8110,
                    // Required if M_MESH_LOD section manual = true
                    // String manualMeshName;
                    M_MESH_LOD_GENERATED = 0x8120,
                    // Required if M_MESH_LOD section manual = false
					// Repeating section (1 per submesh)
                    // unsigned int indexCount;
                    // bool indexes32Bit
                    // unsigned short* faceIndexes;  (indexCount)
                    // OR
                    // unsigned int* faceIndexes;  (indexCount)
            M_MESH_BOUNDS = 0x9000,
                // float minx, miny, minz
                // float maxx, maxy, maxz
                // float radius
                    
			// Added By DrEvil
			// optional chunk that contains a table of submesh indexes and the names of
			// the sub-meshes.
			M_SUBMESH_NAME_TABLE = 0xA000,
				// Subchunks of the name table. Each chunk contains an index & string
				M_SUBMESH_NAME_TABLE_ELEMENT = 0xA100,
	                // short index
                    // char* name
			
			// Optional chunk which stores precomputed edge data					 
			M_EDGE_LISTS = 0xB000,
				// Each LOD has a separate edge list
				M_EDGE_LIST_LOD = 0xB100,
					// unsigned short lodIndex
					// bool isManual			// If manual, no edge data here, loaded from manual mesh
                        // bool isClosed
                        // unsigned long numTriangles
                        // unsigned long numEdgeGroups
						// Triangle* triangleList
                            // unsigned long indexSet
                            // unsigned long vertexSet
                            // unsigned long vertIndex[3]
                            // unsigned long sharedVertIndex[3] 
                            // float normal[4] 

                        M_EDGE_GROUP = 0xB110,
                            // unsigned long vertexSet
                            // unsigned long triStart
                            // unsigned long triCount
                            // unsigned long numEdges
						    // Edge* edgeList
                                // unsigned long  triIndex[2]
                                // unsigned long  vertIndex[2]
                                // unsigned long  sharedVertIndex[2]
                                // bool degenerate

			// Optional poses section, referred to by pose keyframes
			M_POSES = 0xC000,
				M_POSE = 0xC100,
					// char* name (may be blank)
					// unsigned short target	// 0 for shared geometry, 
												// 1+ for submesh index + 1
					// bool includesNormals [1.8+]
					M_POSE_VERTEX = 0xC111,
						// unsigned long vertexIndex
						// float xoffset, yoffset, zoffset
						// float xnormal, ynormal, znormal (optional, 1.8+)
			// Optional vertex animation chunk
			M_ANIMATIONS = 0xD000, 
				M_ANIMATION = 0xD100,
				// char* name
				// float length
				M_ANIMATION_BASEINFO = 0xD105,
				// [Optional] base keyframe information (pose animation only)
				// char* baseAnimationName (blank for self)
				// float baseKeyFrameTime
		
				M_ANIMATION_TRACK = 0xD110,
					// unsigned short type			// 1 == morph, 2 == pose
					// unsigned short target		// 0 for shared geometry, 
													// 1+ for submesh index + 1
					M_ANIMATION_MORPH_KEYFRAME = 0xD111,
						// float time
						// bool includesNormals [1.8+]
						// float x,y,z			// repeat by number of vertices in original geometry
					M_ANIMATION_POSE_KEYFRAME = 0xD112,
						// float time
						M_ANIMATION_POSE_REF = 0xD113, // repeat for number of referenced poses
							// unsigned short poseIndex 
							// float influence

			// Optional submesh extreme vertex list chink
			M_TABLE_EXTREMES = 0xE000,
			// unsigned short submesh_index;
			// float extremes [n_extremes][3];

	/* Version 1.2 of the .mesh format (deprecated)
	enum MeshChunkID {
        M_HEADER                = 0x1000,
            // char*          version           : Version number check
        M_MESH                = 0x3000,
			// bool skeletallyAnimated   // important flag which affects h/w buffer policies
            // Optional M_GEOMETRY chunk
            M_SUBMESH             = 0x4000, 
                // char* materialName
                // bool useSharedVertices
                // unsigned int indexCount
                // bool indexes32Bit
                // unsigned int* faceVertexIndices (indexCount)
                // OR
                // unsigned short* faceVertexIndices (indexCount)
                // M_GEOMETRY chunk (Optional: present only if useSharedVertices = false)
                M_SUBMESH_OPERATION = 0x4010, // optional, trilist assumed if missing
                    // unsigned short operationType
                M_SUBMESH_BONE_ASSIGNMENT = 0x4100,
                    // Optional bone weights (repeating section)
                    // unsigned int vertexIndex;
                    // unsigned short boneIndex;
                    // float weight;
            M_GEOMETRY          = 0x5000, // NB this chunk is embedded within M_MESH and M_SUBMESH
			*/
                // unsigned int vertexCount
                // float* pVertices (x, y, z order x numVertices)
                M_GEOMETRY_NORMALS = 0x5100,    //(Optional)
                    // float* pNormals (x, y, z order x numVertices)
                M_GEOMETRY_COLOURS = 0x5200,    //(Optional)
                    // unsigned long* pColours (RGBA 8888 format x numVertices)
                M_GEOMETRY_TEXCOORDS = 0x5300    //(Optional, REPEATABLE, each one adds an extra set)
                    // unsigned short dimensions    (1 for 1D, 2 for 2D, 3 for 3D)
                    // float* pTexCoords  (u [v] [w] order, dimensions x numVertices)
			/*
            M_MESH_SKELETON_LINK = 0x6000,
                // Optional link to skeleton
                // char* skeletonName           : name of .skeleton to use
            M_MESH_BONE_ASSIGNMENT = 0x7000,
                // Optional bone weights (repeating section)
                // unsigned int vertexIndex;
                // unsigned short boneIndex;
                // float weight;
            M_MESH_LOD = 0x8000,
                // Optional LOD information
                // unsigned short numLevels;
                // bool manual;  (true for manual alternate meshes, false for generated)
                M_MESH_LOD_USAGE = 0x8100,
                // Repeating section, ordered in increasing depth
				// NB LOD 0 (full detail from 0 depth) is omitted
                // float fromSquaredDepth;
                    M_MESH_LOD_MANUAL = 0x8110,
                    // Required if M_MESH_LOD section manual = true
                    // String manualMeshName;
                    M_MESH_LOD_GENERATED = 0x8120,
                    // Required if M_MESH_LOD section manual = false
					// Repeating section (1 per submesh)
                    // unsigned int indexCount;
                    // bool indexes32Bit
                    // unsigned short* faceIndexes;  (indexCount)
                    // OR
                    // unsigned int* faceIndexes;  (indexCount)
            M_MESH_BOUNDS = 0x9000
                // float minx, miny, minz
                // float maxx, maxy, maxz
                // float radius

			// Added By DrEvil
			// optional chunk that contains a table of submesh indexes and the names of
			// the sub-meshes.
			M_SUBMESH_NAME_TABLE,
				// Subchunks of the name table. Each chunk contains an index & string
				M_SUBMESH_NAME_TABLE_ELEMENT,
	                // short index
                    // char* name

	*/
                ;
}
