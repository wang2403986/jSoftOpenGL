package loader.ogre.mesh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

//import javax.media.opengl.GL2;
//
//import com.sun.opengl.util.BufferUtil;
//import com.sun.opengl.util.texture.Texture;

public class OgreMesh {
	public List<VertexDeclaration> declarations=new ArrayList<VertexDeclaration>();
	public List<VertexBuffer> vertexBuffers=new ArrayList<VertexBuffer>();
	public int[] faceVertexIndices;
	
	//public int[] indices;
	public float[] vertices;
	public float[] tmpVertices;
	public float[] texCoords;
	
	public List<float[]>  assignments;
	public List<String> skeletonLinks=new ArrayList<String>();
	int currentIndex=0;
	
	public List<OgreMesh> subMeshs;
	
	public void createBuffer() {
		for (VertexDeclaration i:declarations) {
			i.convert(this);
		}
		
		tmpVertices=new float[vertices.length];
		resetBuffer();
	}
	
	public void resetBuffer() {
		System.arraycopy(vertices, 0, tmpVertices, 0, vertices.length);
	}
	public void updateByTime(long t) {
		
	}
//	public void draw(GL2 gl) {
//		gl.glEnable(GL2.GL_TEXTURE_2D);
//		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
//		FloatBuffer texCoord = BufferUtil.newFloatBuffer(texCoords);
//		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texCoord);
//
//		// gl.glBindTexture( GL2.GL_TEXTURE_2D,1 );
//		// FloatBuffer texturebuf = FloatBuffer.wrap(g.texCoords);
////		Texture tex = this.textures.texArray.get(g.MaterialID);
////		tex.enable();
////		tex.bind();
//
//		FloatBuffer vertexBuf = BufferUtil.newFloatBuffer(tmpVertices);
//		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
//		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertexBuf);
//
//		IntBuffer indexBuf = BufferUtil.newIntBuffer(faceVertexIndices);
//		gl.glDrawElements(GL2.GL_TRIANGLES, faceVertexIndices.length,
//				GL2.GL_UNSIGNED_INT, indexBuf);
//	}
	
}
