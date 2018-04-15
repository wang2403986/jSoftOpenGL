package loader.ogre.mesh;

import java.util.ArrayList;
import java.util.List;

public class OgreSkeleton {
	public List<Bone> bones=new ArrayList<Bone>();
	public List<int[]> boneParent;//childHandle,parentHandle;
	public List<Animation> animations=new ArrayList<Animation>();
	
	public Bone getBone(int handle){
		for (Bone i :bones) {
			if (i.handle==handle) {
				return i;
			}
		}
		return null;
		
	}
}
