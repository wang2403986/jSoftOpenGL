package soft3d.v1_0;

import static soft3d.v1_0.GLM.*;
import static soft3d.v1_0.FastMath.*;
import soft3d.Framebuffer;
import soft3d.Texture;
import soft3d.v1_0.types.*;

public final class TinyGLImpl extends TinyGL {
	private float[] VSOutputBuffers = new float[1024*1024];
	public TinyGLImpl(){
//__init();
	}
	@Override
	public void glDrawElements(int mode, int count, int type, int[] indices) {
		final vec4 vertex0 = new vec4(), vertex1 = new vec4(), vertex2 = new vec4();
		final VertexAttribPointer[] vertexAttribPointers = TinyGL.vertexAttribPointers;
		int sizeOfVSOutput = 3;// x,y,w,l
//__sizeOfVSOutput();
		VertexAttribPointer attribPointer0 = vertexAttribPointers[0];
		int numberOfElements = attribPointer0.data.length / attribPointer0.size, startOffset = 0;
		if (attribPointer0.endOffset!=0){
			numberOfElements = attribPointer0.endOffset ;
			startOffset = attribPointer0.startOffset;
		}
		if (numberOfElements * sizeOfVSOutput > VSOutputBuffers.length) {
			VSOutputBuffers = new float[numberOfElements * sizeOfVSOutput];
		}
		final float[] VSOutputBuffers = this.VSOutputBuffers;
		int outputId = 0;
		vec4 gl_Position= vec4(0, 0, 0,1.0f);//gl_Position_0, gl_Position_1, gl_Position_2,1.0f
		if(!useLastVSOutput)
		for (; startOffset < numberOfElements; startOffset++) {
			int _i0 = startOffset, offset0;
			attribPointer0 = vertexAttribPointers[0];
			offset0 = _i0 * attribPointer0.size + attribPointer0.offset;
			gl_Position.x = attribPointer0.data[offset0];
			gl_Position.y = attribPointer0.data[offset0 + 1];
			gl_Position.z = attribPointer0.data[offset0 + 2];
//__vertex_shader_begin();
			//project(vertex0);
			copy(vertex0, gl_Position);
			int _yScale = height;
			vertex0.w=vertex0.w==0f?1:1/vertex0.w;
			vertex0.x=vertex0.x*vertex0.w*_yScale +(width >> 1); vertex0.y=-vertex0.y*vertex0.w*_yScale +(_yScale >> 1);
			VSOutputBuffers[outputId++] = vertex0.x;
			VSOutputBuffers[outputId++] = vertex0.y;
			VSOutputBuffers[outputId++] = vertex0.w;
//__vertex_shader_end();
		}
		for (int _i = 0; _i < indices.length; _i += 3) {
			int _i0 = indices[_i], _i1 = indices[_i + 1], _i2 = indices[_i + 2];
			int offset0, offset1, offset2;
			attribPointer0 = vertexAttribPointers[0];
			offset0 = _i0 * attribPointer0.size + attribPointer0.offset;
			offset1 = _i1 * attribPointer0.size + attribPointer0.offset;
			offset2 = _i2 * attribPointer0.size + attribPointer0.offset;
			// float v0_gl_FragDepth = attribPointer0.data[offset0 + 2];
			int v0_Offset = sizeOfVSOutput * _i0, v1_Offset = sizeOfVSOutput * _i1,
					v2_Offset = sizeOfVSOutput * _i2;
			vertex0.x = VSOutputBuffers[v0_Offset];
			vertex0.y = VSOutputBuffers[v0_Offset + 1];
			vertex1.x = VSOutputBuffers[v1_Offset];
			vertex1.y = VSOutputBuffers[v1_Offset + 1];
			vertex2.x = VSOutputBuffers[v2_Offset];
			vertex2.y = VSOutputBuffers[v2_Offset + 1];
			if (isBackFace(vertex0, vertex1, vertex2))
				continue;
			float v0_gl_FragDepth = VSOutputBuffers[v0_Offset + 2],
				v1_gl_FragDepth = VSOutputBuffers[v1_Offset + 2],
				v2_gl_FragDepth = VSOutputBuffers[v2_Offset + 2];
			if ( (v0_gl_FragDepth<0||v1_gl_FragDepth<0||v2_gl_FragDepth<0 ) ||
			(v0_gl_FragDepth>FAR_CLIP_PLANE||v1_gl_FragDepth>FAR_CLIP_PLANE||v2_gl_FragDepth>FAR_CLIP_PLANE)) {
				continue;
			}
//__perspectiveCorrectInterpolations;
			// Sort vertices by the computed window coordinates.
			vec4 vA = vertex0, vB = vertex1, vC = vertex2;
			if (vB.y < vA.y) {
				vA = vB;
				vB = vertex0;
			}
			if (vC.y < vA.y) {
				vC = vB;
				vB = vA;
				vA = vertex2;
			} else if (vC.y < vB.y) {
				vC = vB;
				vB = vertex2;
			}
			final int _width = width, _height_1=height - 1;
			final int _width_1 = _width- 1;
			float y = vA.y,endY = vC.y;
			y = (y < -1) ? -1 : y;
			endY = (endY > _height_1) ? _height_1 : endY;
			y = (int)(y +1);
			endY = (int)(endY +1)-1;
			
			float _midY = vB.y; // Edges : AB-AC, BC-AC
			_midY = _midY < -1 ? -1 : _midY;
			_midY = (int)(_midY + 1);
			float dY01 = vB.y - vA.y;
			float dY02 = vC.y - vA.y;
			float dY12 = vC.y - vB.y;
			float fStepX0 = (dY01 > 0.0f) ? (vB.x - vA.x) / (dY01) : 0.0f;
			float fStepX1 = (dY02 > 0.0f) ? (vC.x - vA.x) / (dY02) : 0.0f;
			float fStepX2 = (dY12 > 0.0f) ? (vC.x - vB.x) / (dY12) : 0.0f;
			float fX0, fX1, fX2;
			fX0 = (y - vA.y) * fStepX0 + vA.x;
			fX1 = (y - vA.y) * fStepX1 + vA.x;
			fX2 = (_midY - vB.y) * fStepX2 + vB.x;
			float fDeltaX0 = vertex1.x - vertex0.x, fDeltaX1 = vertex2.x - vertex0.x;
			float fDeltaY0 = vertex1.y - vertex0.y, fDeltaY1 = vertex2.y - vertex0.y;
			float fCommonGradient = 1.0f / (fDeltaX0 * fDeltaY1 - fDeltaX1 * fDeltaY0);
			float fDelta0, fDelta1, fDdx, fDdy;
			fDelta0 = v1_gl_FragDepth - v0_gl_FragDepth;
			fDelta1 = v2_gl_FragDepth - v0_gl_FragDepth;
			fDdx = (fDelta0 * fDeltaY1 - fDelta1 * fDeltaY0) * fCommonGradient;
			fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;
			float ddx_gl_FragDepth = fDdx, ddy_gl_FragDepth = fDdy;
//__ddx_ddy();
			final float[] depthBuffer = Framebuffer.zBuffer;//TODO
			final int[] colorBuffer = Framebuffer.pixels;
			for (; y <= endY; y++, fX0 += fStepX0, fX1 += fStepX1) {
				if (y == _midY) {
					fX0 = fX2;
					fStepX0 = fStepX2;
				}
				float _fStartX, _fEndX;
				if (fX0 < fX1) {
					_fStartX = fX0;
					_fEndX = fX1;
				} else {
					_fStartX = fX1;
					_fEndX = fX0;
				}
				if (_fStartX < -1)
					_fStartX = -1;
				if (_fEndX > _width_1)
					_fEndX = _width_1;
				int gl_FragCoord0= (int)(_fStartX+1), gl_FragCoord1 = (int) y;
				int _endX = (int)(_fEndX+1)-1;
				if (gl_FragCoord0 > _endX)
					continue;
				int _ywidth = gl_FragCoord1 * _width;
				float fOffsetX = gl_FragCoord0 - vertex0.x;
				float fOffsetY = y - vertex0.y;
				float x0_gl_FragDepth = v0_gl_FragDepth + ddx_gl_FragDepth * fOffsetX + ddy_gl_FragDepth * fOffsetY;
//__x0();
				for (; gl_FragCoord0 <= _endX; gl_FragCoord0++,
//__add_ddx();
				x0_gl_FragDepth += ddx_gl_FragDepth) {
					float invW = 1.0f / x0_gl_FragDepth;
					int pixelOffset = _ywidth + gl_FragCoord0;
					float dstFragDepth = depthBuffer[pixelOffset];
					float gl_FragDepth = x0_gl_FragDepth;
//__PSInput();
					boolean depthTestPass = true;
//__depth_test();
					if (!depthTestPass)
						continue;
					boolean discard = false;
					boolean depthWriteMask = true;
					ivec4 gl_FragColor= ivec4(255, 0, 0, 255);
//__fragment_shader();
					if (depthWriteMask) //
						depthBuffer[pixelOffset] = gl_FragDepth;// Ð´ÈëZ»º´æ
					if (discard)
						continue;
					colorBuffer[pixelOffset] = (gl_FragColor.x << 24) | (gl_FragColor.y << 16) | (gl_FragColor.z << 8)
							| gl_FragColor.w;
				}
			}
			primitiveID++;
		}
	}

	private static void copy(vec4 v, vec4 v1) {
		v.x = v1.x ; v.y = v1.y ; v.z = v1.z ; v.w = v1.w ;
	}
//__functionDefs
}