package loader.ogre.mesh;

public class VertexElement {
	/**
	 *  VertexElementSemantic
	 */
	public final static short VES_POSITION = 1,
			            VES_BLEND_WEIGHTS = 2,
			           VES_BLEND_INDICES = 3,
			           VES_NORMAL = 4,
			           VES_DIFFUSE = 5,
			           VES_SPECULAR = 6,
			           VES_TEXTURE_COORDINATES = 7,
			           VES_BINORMAL = 8,
			           VES_TANGENT = 9,
			           VES_COUNT = 9;
	/**
	 * VertexElementType
	 * 
	 */
	public final static short VET_FLOAT1 = 0,
			           VET_FLOAT2 = 1,
			           VET_FLOAT3 = 2,
			           VET_FLOAT4 = 3,
			           VET_COLOUR = 4,
			           VET_SHORT1 = 5,
			           VET_SHORT2 = 6,
			           VET_SHORT3 = 7,
			           VET_SHORT4 = 8,
			           VET_UBYTE4 = 9,
			           VET_COLOUR_ARGB = 10,
			           VET_COLOUR_ABGR = 11;
}
