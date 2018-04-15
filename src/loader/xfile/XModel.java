package loader.xfile;

import static soft3d.v1_0.TinyGL.glBindBufferData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soft3d.util.Extent;
import soft3d.Framebuffer;
import soft3d.v1_0.TinyGL;
import soft3d.Texture;

public class XModel {
	public List<Material> materials;

	public List<XFrame> frames;
	public List<AnimationSet> animationSets;
	public AnimationSet animationSet;
	public XFrame root;
	public List<Texture> textures;

	/**
	 * 转化为蒙皮模型
	 */
	public void convertToSkinnedModel() {
		if (root==null) {
			root=new XFrame();
			root.children=frames;
		}
		convertToSkinnedMesh(root);
	}
	/**
	 * 转化为蒙皮网格
	 * @param frame
	 */
	public void convertToSkinnedMesh(XFrame frame) {
		if (frame.mesh!=null&&frame.mesh.skinWeightsList!=null) {
			for (SkinWeights skin: frame.mesh.skinWeightsList) {
				skin.node=getFrame(skin.nodeName);
			}
		}
		if(frame.children!=null)
		for (XFrame child:frame.children) {
			convertToSkinnedMesh(child);
		}
	}
	
//	public void draw(Framebuffer soft3D) {
//		Framebuffer.textureList=textures;
//		for (XFrame root : frames) {
//			drawFrame(root, soft3D);
//		}
//	}
	public void draw(TinyGL soft3D) {
//		Framebuffer.textureList=textures;
		for (XFrame root : frames) {
			drawFrame(root, soft3D);
		}
	}
	int[] CreateSubMeshFaceIndices(List<int[]> groups, int [] materialIndices,int[] indices){
	  if (indices==null||materialIndices==null) {
      return null;
    }
	  ArrayList<Integer> span = new ArrayList<Integer>();
	  ArrayList<Integer> materialId = new ArrayList<Integer>();
	  span.add(0);
	  int last=0;
	  materialId.add(last);
	  for (int i = 0; i < materialIndices.length; i++) {
      if (materialIndices[i]!= last) {
        span.add(i);
        materialId.add(materialIndices[i]);
        last = materialIndices[i];
      }
    }
	  int[] minToMax=null;
	  if (span.size()>1) {
//	    span.add(ids.length-1);
	    System.err.println("More than one item in materialIndices, CreateSubMeshFaceIndices.");
      for (int it=0;it< span.size(); it+=1) {
        int start=span.get(it);
        int end;
        if(it+1<span.size())
          end=span.get(it+1);
        else 
          end=indices.length-1;
        int[] newArr =Arrays.copyOfRange(indices, start*3, end*3) ;
        
        groups.add(newArr);
      }
      minToMax= new int[groups.size()*3];
      int iMinToMax=0;
      for (int i = 0; i < groups.size(); i++) {
        int[] newArr =groups.get(i);
        int min =0,max=0;
        if(newArr.length>0){
        	min=newArr[0];max=min;
        }
        for (int j = 0; j < newArr.length; j++) {
          if (min>newArr[j]) {
            min=newArr[j];
          } if(max<newArr[j]){
            max=newArr[j];
          }
        }
        minToMax[iMinToMax*3]=min;  minToMax[iMinToMax*3 +1]=max; minToMax[iMinToMax*3 +2]=materialId.get(i);
        iMinToMax++;
      }
    }
    return minToMax;
	}
	public void drawFrame(XFrame root,TinyGL gl) {
		XMesh mesh=null;
		if (root.mesh != null){
			
			mesh=root.mesh;
			float[] vertices = mesh.verticesBuf;
			int[] indices = mesh.indices;
			float[] texCoords = mesh.texCoords;
			
			float[] normalsBuf=root.mesh.normalsBuf;
			float[] normals = root.mesh.normals;
			if(normalsBuf!=null)normals=normalsBuf;
			
//			float[] screenBuf = root.mesh.screenBuf;//25.407299, -569.05634, 25.48917, -568.30176
			int[] materialIndices=mesh.materialIndices;
			
			if (mesh.subMeshIndices ==null) {
			  mesh.subMeshIndices = new ArrayList<>();
			  mesh.subMeshVertexRange =CreateSubMeshFaceIndices(mesh.subMeshIndices, materialIndices, indices);
			}
			glBindBufferData(0, 0, 0, vertices, 0);//position
			glBindBufferData(0, 1, 0, texCoords, 0);//texCoord
			glBindBufferData(0, 2, 0, normals, 0);//normal
			try {
				if (materialIndices!=null&&mesh.textures!=null &&materialIndices[0]<mesh.textures.size()) {
						gl.texture0 = mesh.textures.get(materialIndices[0]);
				}
				if (mesh.subMeshIndices.size()>0) { // Draw Sub Mesh
//				  gl.useLastVSOutput=true;
				  int subMeshIndex=0;
          for (int[] subMesh: mesh.subMeshIndices) {
            int endOffset =mesh.subMeshVertexRange[subMeshIndex*3+1];
            int materialIndex = mesh.subMeshVertexRange[subMeshIndex*3+2];
            glBindBufferData(0, 0, endOffset+1, vertices, 0);//position
            subMeshIndex++;
            if (materialIndex<mesh.textures.size())
            	gl.texture0 = mesh.textures.get(materialIndex);
            gl.glDrawElements(0, 0, 0, subMesh);
          }
          gl.useLastVSOutput=false;
        } else {
       // 材质
//        gl.glUniform(gl.getUniformLocation("uMatrix"), new soft3d.v1_0.compiler.types.mat4());
          gl.glDrawElements(0, 0, 0, indices);
        }
				
			} catch (Exception e) {
				e.printStackTrace();
				gl.useLastVSOutput=false;
			}
		}
		if (root.children!=null) {
			for (XFrame child: root.children) {
				drawFrame(child, gl);
			}
		}
	}
//	/**
//	 * 绘制框架 Draw Frame
//	 * @param root
//	 * @param soft3D
//	 */
//	public void drawFrame(XFrame root,Framebuffer soft3D) {
//		XMesh mesh=null;
//		if (root.mesh != null){
//			
//			mesh=root.mesh;
//			float[] vertices = mesh.verticesBuf;
//			int[] indices = mesh.indices;
//			float[] uvs = mesh.texCoords;
//			
//			float[] normalsBuf=root.mesh.normalsBuf;
//			float[] normals = root.mesh.normals;
//			if(normalsBuf!=null)normals=normalsBuf;
//			
//			float[] screenBuf = root.mesh.screenBuf;//25.407299, -569.05634, 25.48917, -568.30176
//			int[] materialIndices=mesh.materialIndices;
//			
//			if (mesh.textures!=null) {
//				Framebuffer.textureList=mesh.textures;
//			}
//			//物体直径小于1或超出屏幕坐标范围
////			if (soft3D.visible( mesh.screenBounds))
////				soft3D.drawTriangleList(vertices, indices, uvs, normals, screenBuf,materialIndices);
//		}
//		if (root.children!=null) {
//			for (XFrame child: root.children) {
//				drawFrame(child, soft3D);
//			}
//		}
//	}
	/**
	 * 设置播放动画索引
	 * @param index
	 */
	public void setAnimationIndex(int index) {
		animationSet=animationSets.get(index);
	}
	/**
	 * 设置播放动画名称
	 * @param id
	 */
	public void setAnimationIndex(String name) {
		for (AnimationSet i :animationSets) {
			if (i.name!=null&&i.name.equals(name)) {
				animationSet=i;
				break;
			}
		}
	}
	/**
	 * 按时间更新动画
	 * @param time
	 */
	public void updateByTime(long time) {
		if (animationSets == null) {
			for (XFrame root : frames) {
				root.updateMesh();
			}
			return;
		}
		if (animationSet == null) {
			animationSet = animationSets.get(0);
			
		}
		for (Animation animation : animationSet.animations) {
			animation.updateTransform((int) time);
		}
		
		for (XFrame root : frames) {
			root.updateTransform(null);
		}

		for (XFrame root : frames) {
			root.updateMesh();
		}
	}
	/**
	 * 从指定的路径加载贴图
	 * @param folder
	 */
	public void loadTexture(String folder) {
		for (XFrame root : frames) {
			loadTexture(root, folder);
		}
		textureNames=null;
		modelTextures=null;
	}
	/**
	 * 计算包围区域(返回立方体区域)
	 * @param e 返回区域
	 * @param inbuffer 是否变换后的顶点
	 */
	public void calcExtent(Extent e,boolean inbuffer) {
		e.empty=true;
		for (XFrame root : frames) {
			extent1.empty=true;
			frameExtent(root,e,inbuffer);
			
		}
	}
	final Extent extent1=new Extent();
	private void frameExtent(XFrame frame,Extent e,boolean inBuf) {
		extent1.empty=true;
		if(frame.mesh!=null&&frame.mesh.vertices!=null){
			if(inBuf)
				Extent.calcExtent(frame.mesh.verticesBuf, extent1);
			else
			Extent.calcExtent(frame.mesh.vertices, extent1);
			if(!extent1.empty)
				Extent.unionExtent(e,extent1 , e);
		}
		if(frame.children!=null){
			for (XFrame child : frame.children) {
				frameExtent(child,e,inBuf);
			}
		}
	}
	List<String> textureNames;
	List<Texture> modelTextures;
	public void loadTexture(XFrame frame,String folder) {
		if (frame.mesh!=null&&frame.mesh.materials!=null) {
			XMesh mesh=frame.mesh;
			if(textureNames==null){
				textureNames=new ArrayList<String>();
				modelTextures=new ArrayList<Texture>();
			}
			for (Material material :mesh.materials) {
				if(material.textureFilename!=null){
					if (mesh.textures==null) {
						mesh.textures=new ArrayList<Texture>();
					}
					Texture e=new Texture();
					String name=material.textureFilename;
					name=folder+name;
					int index=-1;
					if ((index=textureNames.indexOf(name))!=-1) {
						e=modelTextures.get(index);
						mesh.textures.add(e);
						modelTextures.add(e);
						textureNames.add(name);
						continue;
					} else {
						textureNames.add(name);
					}
					
					try {
						e.loadFromFile(name);
					} catch (IOException e1) {
						e=null;
						System.err.println("贴图："+name+" 读取失败");
						e1.printStackTrace();
					}
					mesh.textures.add(e);
					modelTextures.add(e);
				}
				
			}
		}
		if (frame.children==null) {
			return;
		}
		for (XFrame child:frame.children) {
			loadTexture(child,folder);
		}
	}
	public void linkMaterials(XFrame frame){
		if (frame.mesh!=null&&frame.mesh.materialNames!=null) {
			frame.mesh.materials=new ArrayList<Material>();
			
			for (String name: frame.mesh.materialNames) {
				frame.mesh.materials.add(getMaterial(name));
			}
			
		}
		
		
		if(frame.children!=null)
		for (XFrame i :frame.children) {
			linkMaterials(i);
		}
	}
	public Material getMaterial(String name) {
		if(materials!=null)
		for (Material i:materials) {
			if (i.name.equals(name)) {
				return i;
			}
		}
		return null;
		
	}
	public XFrame getFrame(String name) {
		if (root==null) {
			root=new XFrame();
			root.children=frames;
		}
		return findFrame(root, name);
	}
	public XFrame findFrame(XFrame frame,String name) {
		if (frame.name!=null&&frame.name.equals(name)) {
			return frame;
		}
		XFrame inChildren=null;
		if(frame.children!=null)
		for (XFrame child :frame.children) {
			inChildren=findFrame(child, name);
			if (inChildren!=null) {
				return inChildren;
			}
		}
		return null;
	}
}
