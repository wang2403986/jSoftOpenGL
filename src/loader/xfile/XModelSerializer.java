package loader.xfile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.UnsupportedDataTypeException;
/**
 * 仅支持文本形式存储的Direct3D.X文件
 * @author wangTao
 *
 */
public class XModelSerializer {
	
	String line;
	TokenStack stack = new TokenStack();

	public static void main(String[] args) throws IOException {
		
			XModelSerializer serializer=new XModelSerializer();
			String s="C:\\Users\\Administrator\\Desktop\\exp\\a6.X";
			serializer.importFromFile(s);
		
	}
	public XModel importFromFile(String fnm) {
		XModel model = new XModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fnm));
			importXModel(br, model);
			br.close();
			stack=null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.convertToSkinnedModel();
		return model;
	}

	public void importXModel(BufferedReader br, XModel model)
			throws IOException {

		readFileHeader(br);

		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("template ")) {
				readTemplate(br, null);
			}

			else if (line.startsWith("Material ")) {
				if (model.materials == null) {
					model.materials = new ArrayList<Material>();
				}

				Material e = new Material();
				model.materials.add(e);
				readMaterial(br, e);
			} else if (line.startsWith("Frame ")) {
				if (model.frames == null) {
					model.frames = new ArrayList<XFrame>();
				}

				XFrame e = new XFrame();
				model.frames.add(e);
				readFrame(br, e,model);
			} else if (line.startsWith("AnimationSet ")) {
				if (model.animationSets==null) {
					model.animationSets=new ArrayList<AnimationSet>();
					
				}
				AnimationSet animationSet=new AnimationSet();
				model.animationSets.add(animationSet);
				readAnimationSet(br,animationSet,model);
			}

		}

	}

	private void readAnimationSet(BufferedReader br, AnimationSet animationSet, XModel model) throws IOException {
		String[] s0= line.split(" ");
		if (s0.length>2) {
			animationSet.name=s0[1];
		}
		TokenStack stack=new TokenStack();
		stack.begin();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			
			if(stack.end(line))
				break;
			if (line.startsWith("Animation ")) {
				if (animationSet.animations==null) {
					animationSet.animations=new ArrayList<Animation>();
				}
				Animation e=new Animation();
				animationSet.animations.add(e);
				readAnimation(br, e,model);
			}
		}
	}
	private void readAnimation(BufferedReader br, Animation e, XModel model) throws IOException {
		TokenStack stack=new TokenStack();
		stack.begin();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			
			stack.checkPush(line);
			if (line.startsWith("AnimationKey ")) {
				AnimationKey animationKey=new AnimationKey();
				// 读AnimationKey的类型
				line=br.readLine().trim();
				line=line.substring(0, line.length()-1);
				animationKey.keyType=Integer.parseInt(line);
				if(animationKey.keyType == 4) {
					e.matrixKey=animationKey;
				} else if (animationKey.keyType == 0) {
					e.rotateKey=animationKey;
				} else if (animationKey.keyType == 1) {
					e.scaleKey=animationKey;
				} else if (animationKey.keyType == 2) {
					e.translateKey=animationKey;
				}
				
				readAnimationKey(br,animationKey);
			} else if (line.startsWith("{ ")) {
				e.name=line.substring(1, line.length()-1).trim();
				e.node=model.getFrame(e.name);
			}
			if(stack.checkPop(line))break;
		}
	}
	private void readAnimationKey(BufferedReader br, AnimationKey animationKey) throws IOException {
		// 读AnimationKey的长度
		line=br.readLine().trim();
		line=line.substring(0, line.length()-1);
		int nKeys=Integer.parseInt(line);
		animationKey.nKeys=nKeys;
		
		int offset=0;
		int keyLength = 16;
		if(animationKey.keyType == 4) {
			keyLength = 16;
		} else if (animationKey.keyType == 0) {
			keyLength = 4;
		} else if (animationKey.keyType == 1) {
			keyLength = 3;
		} else if (animationKey.keyType == 2) {
			keyLength = 3;
		}
		animationKey.times=new int[nKeys];
		animationKey.floatKeys=new float[keyLength*nKeys];
		for (int i = 0; i < nKeys; i++) {
			String[] strings=br.readLine().trim().split(";|,");
			animationKey.times[i]=Integer.parseInt(strings[0]);
			int length = Integer.parseInt(strings[1]);
			int p=2;
			for (int j = 0; j < keyLength; j++) {
				animationKey.floatKeys[offset+j]=Float.parseFloat(strings[p]);
				p++;
			}
			offset+=keyLength;
		}
	}
	private void readMaterial(BufferedReader br, Material e) throws IOException {
		String [] strings= line.split(" ");
		if (strings.length>2) {
			e.name=strings[1];
		}
		stack.beginWith(null);
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if(stack.checkPop(line))break;
			stack.checkPush(line);
			if (line.startsWith("TextureFilename ")) {
				line=br.readLine();
				line = line.trim();
				e.textureFilename=line.substring(1, line.length()-2);
			}
			
		}
	}

	public void readTemplate(BufferedReader br, Object o) throws IOException {

		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.charAt(0) == '}') {
				break;
			}
		}
	}

	private void readFileHeader(BufferedReader br) throws IOException {
		line=  br.readLine();
		//xof 0303txt 0032
		boolean txt= line.split(" ")[1].endsWith("txt");
		if (!txt) {
			throw new  UnsupportedDataTypeException("Unsupported file format(不支持的文件格式)!");
		}
	}
	
//	public List<Hierarchy> readHierarchy(BufferedReader br) throws IOException {
//		List<Hierarchy> roots=new ArrayList<Hierarchy>();
//		Hierarchy current=null;
//		stack.clear();
//		while ((line = br.readLine()) != null) {
//			line = line.trim();
//			if(line.length()==0)continue;
//			if (stack.isBegin(line)) {
//				Hierarchy node=new Hierarchy();
//				node.header=line;
//				if(current==null){
//					roots.add(node);
//				}
//				else{
//					current.children.add(node);
//				}
//				stack.push(node);
//				current=node;
//			}
//			else if (stack.isEnd(line)) {
//				stack.pop();
//				current=(Hierarchy) stack.peek();
//			}
//			else{
//				if(current!=null)
//					current.datas.add(line);
//			}
//			
//			
//		}
//		return roots;
//	}

	public void readFrame(BufferedReader br, XFrame frame, XModel model) throws IOException {
		String[] s0=line.split(" ");
		if (s0.length>2) {
			frame.name=s0[1];
		}
		TokenStack inFrame = new TokenStack();
		inFrame.beginWith("{");
		while ((line = br.readLine()) != null) {
			line = line.trim();
			
			inFrame.checkPush(line);
			if (line.startsWith("Frame ")) {
				XFrame child = new XFrame();

				if (frame.children == null) {
					frame.children = new ArrayList<XFrame>();
				}
				frame.children.add(child);
				readFrame(br, child,model);
			}

			else if (line.startsWith("FrameTransformMatrix ")) {
				line=br.readLine().trim();
				String[] strings=line.split(",|;");
				float[][] m={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
				if(strings.length==16){
					for (int j = 0; j < 4; j++) {
						for (int j2 = 0; j2 < 4; j2++) {
							m[j][j2]= Float.parseFloat(strings[j*4+j2].trim());
						}
					}
				}
				
				if(strings.length==4){
					for (int j = 0; j < 4; j++) {
						for (int j2 = 0; j2 < 4; j2++) {
							m[j][j2]= Float.parseFloat(strings[j2].trim());
						}
						if(j==3)
							break;
						line=br.readLine().trim();
						strings=line.split(",|;");
						
					}
				}
				frame.frameTransformMatrix=m;
			}

			else if (line.startsWith("Mesh ")) {
				XMesh mesh = new XMesh();
				frame.mesh = mesh;
				readMesh(br, mesh,model);
			}
			
			if(inFrame.checkPop(line))
				break;
		}
	}
	private void readMesh(BufferedReader br, XMesh mesh, XModel model) throws IOException {
		TokenStack stack=new TokenStack();
		stack.beginWith("{");
		
		line=br.readLine().trim().split(";")[0];
		mesh.nVertices=Integer.parseInt(line);
		mesh.vertices=new float[3*mesh.nVertices];
		int offset=0;
		for (int i = 0; i < mesh.nVertices; i++) {
			String[] strings=br.readLine().trim().split(";|,");
			mesh.vertices[offset]=Float.parseFloat(strings[0]);
			mesh.vertices[offset+1]=Float.parseFloat(strings[1]);
			mesh.vertices[offset+2]=Float.parseFloat(strings[2]);
			offset+=3;
		}
		String[] a= br.readLine().trim().split(";");
		line=a.length>0?a[0]:"0";
		int nFaces=Integer.parseInt(line);
		
		mesh.indices=new int[nFaces*3];
		int p=0;
		for (int i = 0; i < nFaces; i++) {
			String[] s=br.readLine().trim().split(";|,");
			//int k= Integer.parseInt(s[0]);
			for (int j2 = 0; j2 < 3; j2++) {
				mesh.indices[p+j2]= Integer.parseInt(s[j2+1]);
			}
			p+=3;
		}
		
		
		while((line=br.readLine())!=null){
			line=line.trim();
			
			stack.checkPush(line);
			
			if (line.startsWith("VertexDuplicationIndices ")) {
				readVertexDuplicationIndices(br,mesh);
			}
			else if (line.startsWith("MeshMaterialList ")) {
				readMeshMaterialList(br,mesh,model);
			}
			
			else if (line.startsWith("MeshNormals ")) {
				readMeshNormals(br,mesh);
			}
			else if (line.startsWith("MeshTextureCoords ")) {
				readMeshTextureCoords(br,mesh);
			}
			else if (line.startsWith("XSkinMeshHeader ")) {
				
			}
			else if (line.startsWith("SkinWeights ")) {
				SkinWeights skinWeights=new SkinWeights();
				if (mesh.skinWeightsList==null) {
					mesh.skinWeightsList=new ArrayList<SkinWeights>();
				}
				mesh.skinWeightsList.add(skinWeights);
				readSkinWeights(br,skinWeights);
			}
			else if (line.startsWith("DeclData ")) {
				readDeclData(br,mesh,model);
			}
			
			if(stack.checkPop(line))break;
		}
		
	
	}
	private void readDeclData(BufferedReader br, XMesh mesh, XModel model) throws IOException {
		line=br.readLine().trim();
		line=line.split(";")[0];
		int nElements=Integer.parseInt(line);
		VertexElement [] elements=new VertexElement[nElements];
		for (int i = 0; i < nElements; i++) {
			line=br.readLine().trim();
			String[] strings= line.split(";");
			int type=Integer.parseInt(strings[0]);
			int method=Integer.parseInt(strings[1]);
			int usage=Integer.parseInt(strings[2]);
			int usageIndex=Integer.parseInt(strings[3]);
			elements[i]=new VertexElement();
			elements[i].type=type;
			elements[i].method=method;
			elements[i].usage=usage;
			elements[i].usageIndex=usageIndex;
			
		}
		line=br.readLine().trim();
		line=line.split(";")[0];
		int vertexIndex=0;
		int nDWords=Integer.parseInt(line);
		int[] dWords=new int[nDWords];
		for (int i = 0; i < nDWords; i++) {
			line=br.readLine().trim().split(";|,")[0];
			int dWord= (int) Long.parseLong(line);
			
			dWords[i]=dWord;
			
		}
		int[][] startEnd=new int[nElements][2];
		int start=0;
		for (int i = 0; i < nElements; i++) {
			startEnd[i][0]=start;
			VertexElement element= elements[i];
			switch (element.type) {
			case VertexElement.D3DDECLTYPE_FLOAT1:
				start+=1;
				break;
			case VertexElement.D3DDECLTYPE_FLOAT2:
				start+=2;
				break;
			case VertexElement.D3DDECLTYPE_FLOAT3:
				start+=3;
				break;
			case VertexElement.D3DDECLTYPE_FLOAT4:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_D3DCOLOR:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_UBYTE4:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_SHORT2:
				start+=2;
				break;
			case VertexElement.D3DDECLTYPE_SHORT4:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_UBYTE4N:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_SHORT2N:
				start+=2;
				break;
			case VertexElement.D3DDECLTYPE_SHORT4N:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_USHORT2N:
				start+=2;
				break;
			case VertexElement.D3DDECLTYPE_USHORT4N:
				start+=4;
				break;
			case VertexElement.D3DDECLTYPE_UDEC3:
				start+=3;
				break;
			case VertexElement.D3DDECLTYPE_DEC3N:
				start+=3;
				break;
			case VertexElement.D3DDECLTYPE_FLOAT16_2:
				start+=16;
				break;
			case VertexElement.D3DDECLTYPE_FLOAT16_4:
				start+=16;
				break;
			default:
				break;
			}
			startEnd[i][1]=start-1;
		}
		int len=start;
		for (int i = 0; i < nElements; i++) {
			VertexElement element= elements[i];
			switch (element.usage) {
			case VertexElement.D3DDECLUSAGE_TEXCOORD:
				
				start=startEnd[i][0];
				int end=startEnd[i][1];
				int nVertices=mesh.nVertices;
				if (mesh.texCoords!=null) {
					break;
				}
				mesh.texCoords=new float[nVertices*2];
				float[] texCoords=mesh.texCoords;
				int k=0;
				for (int j = start; j < nDWords; j+=len) {
					
					float u=Float.intBitsToFloat(dWords[j]);
					if(u<0)u=0;if(u>1)u=1;
					texCoords[k]=u;
					
					u= Float.intBitsToFloat(dWords[j+1]);
					if(u<0)u=0;if(u>1)u=1;
					 texCoords[k+1]=u;
					k+=2;
				}
				
				
				
				break;
			case VertexElement.D3DDECLUSAGE_NORMAL:
				start=startEnd[i][0];
				end=startEnd[i][1];
				nVertices=mesh.nVertices;
				if (mesh.normals!=null) {
					break;
				}
				mesh.normals=new float[nVertices*3];
				float[] normals=mesh.normals;
				k=0;
				for (int j = start; j < nDWords; j+=len) {
					
					float u=Float.intBitsToFloat(dWords[j]);
					//if(u<0)u=0;if(u>1)u=1;
					normals[k]=u;
					
					u= Float.intBitsToFloat(dWords[j+1]);
					//if(u<0)u=0;if(u>1)u=1;
					normals[k+1]=u;
					
					u= Float.intBitsToFloat(dWords[j+2]);
					//if(u<0)u=0;if(u>1)u=1;
					normals[k+2]=u;
					k+=3;
				}
				break;
			default:
				break;
			}
		}
		dWords=null;
	}
	private void readMeshTextureCoords(BufferedReader br, XMesh mesh) throws IOException {
		line=br.readLine().trim();
		line=line.split(";")[0];
		int nTexCoords=Integer.parseInt(line);
		mesh.texCoords=new float[nTexCoords*2];
		int p=0;
		for (int i = 0; i < nTexCoords; i++) {
			line=br.readLine().trim();
			String[] strings=line.split(";");
			float u=Float.parseFloat(strings[0]);
			if(u<0)
				u=1+u;
			if(u>1)
				u=u-1;
			float v=Float.parseFloat(strings[1]);
			if(v<0)
				v=1+v;
			if(v>1)
				v=v-1;
			mesh.texCoords[p]=u;  mesh.texCoords[p+1]=v;
			//System.out.println(mesh.texCoords[p]+","+mesh.texCoords[p+1]);
			p+=2;
		}
		
	}
	private void readMeshNormals(BufferedReader br, XMesh mesh) throws IOException {
		line=br.readLine().trim();
		line=line.split(";")[0];
		int nNormals=Integer.parseInt(line);
		mesh.normals=new float[nNormals*3];
		for (int i = 0; i < nNormals; i++) {
			line=br.readLine().trim();
			String[] a=line.split(";");
			mesh.normals[i*3]=Float.parseFloat(a[0]);
			mesh.normals[i*3+1]=Float.parseFloat(a[1]);
			mesh.normals[i*3+2]=Float.parseFloat(a[2]);
		}
	}
	private void readMeshMaterialList(BufferedReader br, XMesh mesh, XModel model) throws IOException {
		line=br.readLine().trim();
		line=line.split(";")[0];
		int nMaterials=Integer.parseInt(line);
		line=br.readLine().trim();
		line=line.split(";")[0];
		int nFaces=Integer.parseInt(line);
		mesh.materialIndices=new int[nFaces];
		for (int j = 0; j < nFaces; j++) {
			line=br.readLine().trim();
			line=line.split(";|,")[0];
			int index=Integer.parseInt(line);
			mesh.materialIndices[j]=index;
		}
		TokenStack stack=new TokenStack();
		stack.beginWith("{");
		while((line=br.readLine())!=null){
			line=line.trim();
			
			stack.checkPush(line);
			
			if (line.startsWith("{")) {
				String name=line.substring(1, line.length()-1).trim();
				Material material= model.getMaterial(name);
				if (mesh.materials==null) {
					mesh.materials=new ArrayList<Material>();
				}
				mesh.materials.add(material);
				
			}else if (line.startsWith("Material ")) {
				if (mesh.materials==null) {
					mesh.materials=new ArrayList<Material>();
				}
				Material e=new Material();
				//添加到Mesh的materials列表
				mesh.materials.add(e);
//				//添加到Model的materials列表
//				if(model.materials==null)
//					model.materials=new ArrayList<Material>();
//				model.materials.add(e);
				readMaterial(br, e);
			}
			
			if(stack.checkPop(line))break;
		}
	}
	private void readVertexDuplicationIndices(BufferedReader br, XMesh mesh) throws IOException {
		line=br.readLine().trim().split(";")[0];
		int nIndices=Integer.parseInt(line);
		line=br.readLine().trim().split(";")[0];
		for (int i = 0; i < nIndices; i++) {
			line=br.readLine().trim();
		}
		
	}
	private void readSkinWeights(BufferedReader br, SkinWeights skinWeights) throws IOException {
		line=br.readLine().trim();
		line=line.substring(1, line.length()-2);
		skinWeights.nodeName=line;
		System.out.println(line);
		line=br.readLine().trim();
		line=line.split(";")[0];
		skinWeights.nWeights=Integer.parseInt(line);
		skinWeights.vertexIndices=new int[skinWeights.nWeights];
		skinWeights.weights=new float[skinWeights.nWeights];
		for (int i = 0; i < skinWeights.nWeights; i++) {
			line=br.readLine().trim();
			line=line.substring(0, line.length()-1);
			skinWeights.vertexIndices[i]=Integer.parseInt(line);
			
		}
		for (int i = 0; i < skinWeights.nWeights; i++) {
			line=br.readLine().trim();
			line=line.substring(0, line.length()-1);
			skinWeights.weights[i]=Float.parseFloat(line);
			
		}
		line=br.readLine().trim();
		String[] strings=line.split(";|,");
		float[][] m={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
		for (int j = 0; j < 4; j++) {
			for (int j2 = 0; j2 < 4; j2++) {
				m[j][j2]= Float.parseFloat( strings[j*4+j2]);
			}
		}
		skinWeights.matrixOffset=m;
	}
}
