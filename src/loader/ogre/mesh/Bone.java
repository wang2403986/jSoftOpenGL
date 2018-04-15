package loader.ogre.mesh;

import java.util.ArrayList;
import java.util.List;

public class Bone {
	/**
	 * handle
	 */
	public int handle;//handle 
	public String name;
	public Bone parent;
	public float[] position;
	public float[] orientation;
	public float[] scale;
	public List<Bone> children=new ArrayList<Bone>();
	
}
