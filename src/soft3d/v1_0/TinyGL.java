package soft3d.v1_0;

import java.util.HashMap;
import soft3d.Framebuffer;
import soft3d.Texture;
import soft3d.v1_0.types.mat4;
import soft3d.v1_0.types.vec4;

public abstract class TinyGL {
	
	public int primitiveID=0;
	public static int GLDepthFunc = 0;
	Framebuffer framebuffer;
	public boolean useLastVSOutput;
	public static final float FAR_CLIP_PLANE = 100000;

	public Object[] uniforms = null;
	public HashMap<String, Integer> uniformsMap = new HashMap<>();
	public HashMap<String, Integer> attribLocationsMap = new HashMap<>();

	public final static mat4 GLViewMatrix=new mat4();
	public final static mat4 GLProjMatrix=new mat4();

	public static int width = 1024, height = 680;
	
	public Texture texture0, texture1, texture2, texture3, texture4;

	public static final VertexAttribPointer[] vertexAttribPointers = VertexAttribPointer.create(10);
	
	public abstract void glDrawElements(int mode, int count, int type, int[] indices);

	public boolean isBackFace(vec4 a, vec4 b, vec4 c) {
		float cax = c.x - a.x;
		float cay = c.y - a.y;
		float bcx = b.x - c.x;
		float bcy = b.y - c.y;
//		return false;
		return cax * bcy < cay * bcx;
//		return cax * bcy > cay * bcx;
	}

	public final void glUniforms(int location, Object[] value​) {
		uniforms[location] = value​;
	}
	public final void glUniform(int location, Object value​) {
		uniforms[location] = value​;
	}
	public final int getUniformLocation(String name​) {
		Integer i = uniformsMap.get(name​);
		return i==null? -1 : i.intValue();
	}
	public final int getAttribLocation(String name​) {
		Integer i = attribLocationsMap.get(name​);
		return i==null? -1 : i.intValue();
	}
	public final void glUniform1fv(int location, float value​) {
		uniforms[location] = value​;
	}
	public final void glViewport(int w, int h) {
		width = w; height = h;
		framebuffer.resize(w, h);
	}
	public final void glBindFramebuffer(Framebuffer fb) {
		framebuffer = fb;
	}
	public final void glClear(int flag) {
		framebuffer.beginScene();
	}
	public final void glFinish(int flag) {
		framebuffer.swapBuffers();
	}
	public static void glBindBufferData(int target, int buffer, int size, float[] data, int usage) {
		VertexAttribPointer attribPointer = TinyGL.vertexAttribPointers[buffer];
		attribPointer.data = data;
		attribPointer.startOffset = 0;
		attribPointer.endOffset = attribPointer.startOffset + size;
	}
	public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
			int offset) {
		VertexAttribPointer attribPointer = TinyGL.vertexAttribPointers[index];
		attribPointer.index = index;
		attribPointer.size = size;
		attribPointer.type = type;
		attribPointer.normalized = normalized;
		attribPointer.stride = stride;
		attribPointer.offset = offset;
	}
//	public static void project(vec4 point) {
//	float halfW = width >> 1, halfH = height >> 1;
////	float difZ = soft3d.SoftGraphics3D.screenZ - soft3d.SoftGraphics3D.focusZ;
////	float w = 1.0f / (point.z - soft3d.SoftGraphics3D.focusZ);
////	float r = difZ * w;
////	float ratio = soft3d.SoftGraphics3D.scaling * r;
////	point.x = point.x * ratio + halfW;
////	/** 右手平面直角坐标系变为左手直角坐标系 */
////	point.y = -point.y * ratio + halfH;
////	point.w = w;
//}
}