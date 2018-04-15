package loader.xfile;

import java.util.List;

import soft3d.Matrix;


public class XFrame {
	public String name;
	public XFrame parent;
	public List<XFrame> children;
	
	public float[][] frameTransformMatrix={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	public float[][] nextKeyframeTransform={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	public float interpolation = 0.0f;
	public XMesh mesh;
	
	public void updateTransform(XFrame parent) {
		if (parent != null && parent.frameTransformMatrix != null) {
			float[][] parentTransform = parent.frameTransformMatrix;
			Matrix.mul(frameTransformMatrix, parentTransform,
					frameTransformMatrix);
//			float[][] mat1= parent.nextKeyframeTransform;
//			Matrix.mul(nextKeyframeTransform, mat1,
//					nextKeyframeTransform);
		}
		if(children==null)return;
		for (XFrame child:children) {
			child.updateTransform(this);
		}
		
	}
	public void updateMesh() {
		if (mesh!=null) {
			mesh.update(this);
		}
		if(children==null)return;
		for (XFrame child:children) {
			child.updateMesh();
		}
	}
}
