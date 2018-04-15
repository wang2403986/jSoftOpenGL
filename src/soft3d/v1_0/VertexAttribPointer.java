package soft3d.v1_0;

public final class VertexAttribPointer {
	int index;
	/**
	 * 如position是由3个（x,y,z）组成，
	 */
	public int size = 3;//如position是由3个（x,y,z）组成，
	public int type;
	public boolean normalized = true;
	/**
	 * 如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。
	 */
	public int stride = 0;//如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。
	/**
	 * 指定第一个组件在数组的第一个顶点属性中的偏移量。
	 */
	public int offset = 0;
	public String bindAttribLocation=null;
	
	public float[] data;//float类型顶点属性
	
	public int[] intData;//int类型顶点属性
	public Object[] ObjectData;//Object类型顶点属性
	public int startOffset, endOffset;
	
	public static final VertexAttribPointer[] create(int number) {
		final VertexAttribPointer[] vertexAttribPointers = new VertexAttribPointer[number];
		for (int i = 0; i < number; i++) {
			vertexAttribPointers[i] = new VertexAttribPointer();
		}
		return vertexAttribPointers;
	}
//	public static void glBindAttribLocation(int program, int index,String name) {
//		VertexAttribPointer attribPointer = TinyGL.vertexAttribPointers[index];
//		attribPointer.bindAttribLocation = name;
//	}

}
