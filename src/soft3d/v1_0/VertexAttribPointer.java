package soft3d.v1_0;

public final class VertexAttribPointer {
	int index;
	/**
	 * ��position����3����x,y,z����ɣ�
	 */
	public int size = 3;//��position����3����x,y,z����ɣ�
	public int type;
	public boolean normalized = true;
	/**
	 * ���Ϊ0����ô�������Իᱻ���Ϊ�������ǽ���������һ��ġ�
	 */
	public int stride = 0;//���Ϊ0����ô�������Իᱻ���Ϊ�������ǽ���������һ��ġ�
	/**
	 * ָ����һ�����������ĵ�һ�����������е�ƫ������
	 */
	public int offset = 0;
	public String bindAttribLocation=null;
	
	public float[] data;//float���Ͷ�������
	
	public int[] intData;//int���Ͷ�������
	public Object[] ObjectData;//Object���Ͷ�������
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
