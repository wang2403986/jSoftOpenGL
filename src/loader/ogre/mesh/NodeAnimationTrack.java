package loader.ogre.mesh;

import java.util.ArrayList;
import java.util.List;

public class NodeAnimationTrack {
	public int boneHandle;//
	public String name;
	public float length;
	
	public Bone bone;
	
	public List<NodeKeyFrame> keyFrames=new ArrayList<NodeKeyFrame>();
}
