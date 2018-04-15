package loader.ogre.mesh;

public class VertexDeclaration {
	public int source;
	public int type;
	public int semantic;
	public int offset;
	public int semanticIndex;
	public VertexBuffer vertexBuffer;

	public void convert(OgreMesh mesh) {
		switch (semantic) {
		case VertexElement.VES_BINORMAL:

			break;
		case VertexElement.VES_BLEND_INDICES:

			convertBlendIndices(mesh);
			break;
		case VertexElement.VES_BLEND_WEIGHTS:
			convertBlendWeights(mesh);
			break;
		
		case VertexElement.VES_DIFFUSE:
			
			break;
		case VertexElement.VES_NORMAL:
			convertNormal(mesh);
			break;
		case VertexElement.VES_POSITION:
			convertPosition(mesh);
			break;
		case VertexElement.VES_SPECULAR:

			break;
		
		case VertexElement.VES_TEXTURE_COORDINATES:
			convertTextureCoordinates(mesh);
			break;
		
		case VertexElement.VET_COLOUR_ABGR:

			break;
		case VertexElement.VET_COLOUR_ARGB:

			break;
		
		default:
			break;
		}
	}
	public void convertPosition(OgreMesh mesh) {
		switch (type) {
		case VertexElement.VET_FLOAT3:
			vertexBuffer.reset();
			float[] fs=new float[vertexBuffer.byteBuffer.length/(3*4)];
			int i=0;
			while (vertexBuffer.available()>=12) {
				fs[i]=vertexBuffer.readFloat();
				i++;
			}
			mesh.vertices=fs;
			break;

		default:
			break;
		}
	}
	public void convertBlendWeights(OgreMesh mesh) {
		switch (type) {
		case VertexElement.VET_FLOAT3:
			vertexBuffer.reset();
			float[] fs=new float[vertexBuffer.byteBuffer.length/(3*4)];
			int i=0;
			while (vertexBuffer.available()>=12) {
				fs[i]=vertexBuffer.readFloat();
				i++;
			}
			break;

		default:
			break;
		}
	}
public void convertTextureCoordinates(OgreMesh mesh) {
		switch (type) {
		case VertexElement.VET_FLOAT2:
			vertexBuffer.reset();
			float[] fs=new float[vertexBuffer.byteBuffer.length/(2*4)];
			int i=0;
			while (vertexBuffer.available()>=8) {
				fs[i]=vertexBuffer.readFloat();
				i++;
			}
			mesh.texCoords=fs;
			break;

		default:
			break;
		}
	}
public void convertBlendIndices(OgreMesh mesh) {
		switch (type) {
		case VertexElement.VET_FLOAT1:
			vertexBuffer.reset();
			float[] fs=new float[vertexBuffer.byteBuffer.length/(1*4)];
			int i=0;
			while (vertexBuffer.available()>=4) {
				fs[i]=vertexBuffer.readFloat();
				i++;
			}
			break;
		case VertexElement.VET_SHORT1:
			
			break;

		default:
			break;
		}
	}
public void convertNormal(OgreMesh mesh) {
	switch (type) {
	case VertexElement.VET_FLOAT3:
		
		break;
	

	default:
		break;
	}
}
}
