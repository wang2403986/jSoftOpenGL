package soft3d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import soft3d.Matrix;
import soft3d.Vec3;


public class Framebuffer {
	public static int height;
	public static int width;

	public static final float[][] VIEWMATRIX = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 },
			{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
	public static int[] pixels;
	public static float[] zBuffer;
	/**
	 * 用于绘制的图片源
	 */
	BufferedImage image;
	Graphics graphics2D, bufferedGraphics;

	public Framebuffer(Graphics graphics2d, int w, int h) {
		this.graphics2D = graphics2d;
		Matrix.loadIdentity(VIEWMATRIX);
		applyNewSize(w, h);
	}
	void applyNewSize(int w, int h){
		zBuffer = new float[h * w];
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
				.getData();
		bufferedGraphics = image.createGraphics();
		width = w;
		height = h;
	}
	public void resize(int w, int h){
		if (w !=width || h != height)
			applyNewSize(w, h);
	}

	public static float GLClearDepth=Float.MAX_VALUE;//999999999f
	public static int GLClearColor=0;//999999999f
	public final void beginScene() {
		Arrays.fill(zBuffer, GLClearDepth);// -32767f
		Arrays.fill(pixels, GLClearColor);
	}

	public final void swapBuffers() {
//	      Image resizedImage =image.getScaledInstance(width*2/8, height*2/8, Image.SCALE_SMOOTH);
//	      resizedImage =resizedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
// RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
// renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.drawImage(image, 8, 30, java.awt.Color.BLACK, null);
	}

	/**
	 * 计算顶点法线
	 * 
	 * @param vertices
	 * @param indices
	 * @param normals
	 */
	public static void computeNormals(float[] vertices, int[] indices,
			float[] normals) {
		Arrays.fill(normals, 0f);
		int offs0, offs1, offs2;
		float[] vec0 = { 0, 0, 0 }, vec1 = { 0, 0, 0 }, result = { 0, 0, 0 };
		for (int i = 0; i < indices.length; i += 3) {
			offs0 = indices[i] * 3;
			offs1 = indices[i + 1] * 3;
			offs2 = indices[i + 2] * 3;
			vec0[0] = vertices[offs1] - vertices[offs0];
			vec0[1] = vertices[offs1 + 1] - vertices[offs0 + 1];
			vec0[2] = vertices[offs1 + 2] - vertices[offs0 + 2];
			vec1[0] = vertices[offs2] - vertices[offs1];
			vec1[1] = vertices[offs2 + 1] - vertices[offs1 + 1];
			vec1[2] = vertices[offs2 + 2] - vertices[offs1 + 2];
			Vec3.cross(vec0, vec1, result);
			Vec3.normalize(result);
			normals[offs0] += result[0];
			normals[offs0 + 1] += result[1];
			normals[offs0 + 2] += result[2];
			normals[offs1] += result[0];
			normals[offs1 + 1] += result[1];
			normals[offs1 + 2] += result[2];
			normals[offs2] += result[0];
			normals[offs2 + 1] += result[1];
			normals[offs2 + 2] += result[2];
		}
		Vec3.normalizeAll(normals);
	}

	public void drawString(String str, int x, int y) {
		bufferedGraphics.drawString(str, x, y);
	}
//	public static final void project(Vec3 vertex, float halfW, float halfH) {
////float halfW = width >> 1, halfH = height >> 1;
//float difZ = screenZ - focusZ;
//float r = 1, z = vertex.z;
//if (z != focusZ)
//	r = difZ / (z - focusZ);
//float ratio = 512.0f * r;
//vertex.x = vertex.x * ratio + halfW;
///** 右手平面直角坐标系变为左手直角坐标系 */
//vertex.y = -vertex.y * ratio + halfH;
//}
}