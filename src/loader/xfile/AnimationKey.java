package loader.xfile;

public class AnimationKey {
	/**
	 * 0:rotate(w,x,y,z), 1:scale(x,y,z), 2:translate(x,y,z), 4£ºmatrix
	 */
	public int keyType;
	public int nKeys;
	public int currentFrame;
	public int nextFrame;
	public int[] times;
	public float[] floatKeys;
}
