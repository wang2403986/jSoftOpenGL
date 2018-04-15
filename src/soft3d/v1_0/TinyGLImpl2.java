package soft3d.v1_0;

import static soft3d.v1_0.GLM.*;
import static soft3d.v1_0.FastMath.*;
import soft3d.Framebuffer;
import soft3d.v1_0.types.*;

public final class TinyGLImpl2 extends TinyGL {
	private float[] VSOutputBuffers = new float[1024*1024];
	public TinyGLImpl2(){
//__init();
uniforms= new Object[0];
	}
	@Override
	public void glDrawElements(int mode, int count, int type, int[] indices) {
		final vec4 vertex0 = new vec4(), vertex1 = new vec4(), vertex2 = new vec4();
		final VertexAttribPointer[] vertexAttribPointers = TinyGL.vertexAttribPointers;
		int sizeOfVSOutput = 3;// x,y,w,l
//__sizeOfVSOutput();
sizeOfVSOutput = 4;
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
 vec2 texCoord=new vec2();
 vec3 normal=new vec3();
 vec3 position=new vec3();
 float light=0;
attribPointer0 = vertexAttribPointers[2];if(attribPointer0.data!=null){
offset0 = _i0*attribPointer0.size+attribPointer0.offset;normal.x=attribPointer0.data[offset0+0];normal.y=attribPointer0.data[offset0+1];normal.z=attribPointer0.data[offset0+2];
}
attribPointer0 = vertexAttribPointers[1];if(attribPointer0.data!=null){
offset0 = _i0*attribPointer0.size+attribPointer0.offset;texCoord.x=attribPointer0.data[offset0+0];texCoord.y=attribPointer0.data[offset0+1];
}
attribPointer0 = vertexAttribPointers[0];if(attribPointer0.data!=null){
offset0 = _i0*attribPointer0.size+attribPointer0.offset;position.x=attribPointer0.data[offset0+0];position.y=attribPointer0.data[offset0+1];position.z=attribPointer0.data[offset0+2];
}
{gl_Position=vec4(position,1.0f);
gl_Position=mul(GLProjMatrix,gl_Position);
vec3 g_LightPos=vec3(- 600f,0f,- 1024f);
vec3 g_eyePos=vec3(400f,0f,- 800f);
g_eyePos=vec3(400f,0f,- 800f);
vec3 L=sub(g_LightPos,position);
vec3 V=sub(g_eyePos,g_LightPos);
vec3 H=add(L,V);
L=normalize(L);
float g_ambient=0.1f;
float saturate=dot(normal,L);
saturate=saturate<0f?0f:saturate;
float diffuse=saturate;
float specular=pow(saturate,7);
light=add(add(sub(mul(diffuse,2),1.4f),g_ambient),specular);
light=mul(light,100);
}

			//project(vertex0);
			copy(vertex0, gl_Position);
			int _yScale = height;
			vertex0.w=vertex0.w==0f?1:1/vertex0.w;
			vertex0.x=vertex0.x*vertex0.w*_yScale +(width >> 1); vertex0.y=-vertex0.y*vertex0.w*_yScale +(_yScale >> 1);
			VSOutputBuffers[outputId++] = vertex0.x;
			VSOutputBuffers[outputId++] = vertex0.y;
			VSOutputBuffers[outputId++] = vertex0.w;
//__vertex_shader_end();
VSOutputBuffers[outputId++]=light;
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
 float v0_light=0;
 float v1_light=0;
 float v2_light=0;
 vec2 v0_texCoord=new vec2();
 vec2 v1_texCoord=new vec2();
 vec2 v2_texCoord=new vec2();
attribPointer0 = vertexAttribPointers[1];if(attribPointer0.data!=null){
offset0 = _i0*attribPointer0.size+attribPointer0.offset;offset1 = _i1*attribPointer0.size+attribPointer0.offset;offset2 = _i2*attribPointer0.size+attribPointer0.offset;
v0_texCoord.x=attribPointer0.data[offset0 + 0];v0_texCoord.y=attribPointer0.data[offset0 + 1];
v1_texCoord.x=attribPointer0.data[offset1 + 0];v1_texCoord.y=attribPointer0.data[offset1 + 1];
v2_texCoord.x=attribPointer0.data[offset2 + 0];v2_texCoord.y=attribPointer0.data[offset2 + 1];
}
v0_light = VSOutputBuffers[v0_Offset+3];v1_light = VSOutputBuffers[v1_Offset+3];v2_light = VSOutputBuffers[v2_Offset+3];
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
 float ddx_light=0;
 float ddy_light=0;
v0_light*=v0_gl_FragDepth;v1_light*=v1_gl_FragDepth;v2_light*=v2_gl_FragDepth;
fDelta0 = v1_light - v0_light;fDelta1 = v2_light - v0_light;fDdx = (fDelta0 * (fDeltaY1)- fDelta1 * (fDeltaY0)) * (fCommonGradient);fDdy = -(fDelta0 * (fDeltaX1) - fDelta1 * (fDeltaX0)) * (fCommonGradient);ddx_light =fDdx;ddy_light =fDdy;
 vec2 ddx_texCoord=new vec2();
 vec2 ddy_texCoord=new vec2();
v0_texCoord.x*=v0_gl_FragDepth;v1_texCoord.x*=v1_gl_FragDepth;v2_texCoord.x*=v2_gl_FragDepth;v0_texCoord.y*=v0_gl_FragDepth;v1_texCoord.y*=v1_gl_FragDepth;v2_texCoord.y*=v2_gl_FragDepth;
fDelta0 = v1_texCoord.x - v0_texCoord.x;fDelta1 = v2_texCoord.x - v0_texCoord.x;fDdx = (fDelta0 * (fDeltaY1)- fDelta1 * (fDeltaY0)) * (fCommonGradient);fDdy = -(fDelta0 * (fDeltaX1) - fDelta1 * (fDeltaX0)) * (fCommonGradient);ddx_texCoord.x =fDdx;ddy_texCoord.x =fDdy;fDelta0 = v1_texCoord.y - v0_texCoord.y;fDelta1 = v2_texCoord.y - v0_texCoord.y;fDdx = (fDelta0 * (fDeltaY1)- fDelta1 * (fDeltaY0)) * (fCommonGradient);fDdy = -(fDelta0 * (fDeltaX1) - fDelta1 * (fDeltaX0)) * (fCommonGradient);ddx_texCoord.y =fDdx;ddy_texCoord.y =fDdy;
 float ddx_texCoord_mipmap=0;
 float ddy_texCoord_mipmap=0;
 float v0_texCoord_mipmap=0;
 float v1_texCoord_mipmap=0;
 float v2_texCoord_mipmap=0;

v0_texCoord_mipmap=MipmapEXT.mipmapLevel(texture0,v0_texCoord,ddx_texCoord,ddy_texCoord,v0_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);
v1_texCoord_mipmap=MipmapEXT.mipmapLevel(texture0,v1_texCoord,ddx_texCoord,ddy_texCoord,v1_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);
v2_texCoord_mipmap=MipmapEXT.mipmapLevel(texture0,v2_texCoord,ddx_texCoord,ddy_texCoord,v2_gl_FragDepth,ddx_gl_FragDepth,ddy_gl_FragDepth);
fDelta0 = v1_texCoord_mipmap - v0_texCoord_mipmap;fDelta1 = v2_texCoord_mipmap - v0_texCoord_mipmap;fDdx = (fDelta0 * fDeltaY1- fDelta1 * fDeltaY0) * fCommonGradient;fDdy = -(fDelta0 * fDeltaX1 - fDelta1 * fDeltaX0) * fCommonGradient;ddx_texCoord_mipmap =fDdx;ddy_texCoord_mipmap =fDdy;

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
 float x0_light=0;
 vec2 x0_texCoord=new vec2();
 float x0_texCoord_mipmap=0;
x0_light = v0_light + ddx_light * (fOffsetX) + ddy_light * (fOffsetY);x0_texCoord.x = v0_texCoord.x + ddx_texCoord.x * (fOffsetX) + ddy_texCoord.x * (fOffsetY);x0_texCoord.y = v0_texCoord.y + ddx_texCoord.y * (fOffsetX) + ddy_texCoord.y * (fOffsetY);x0_texCoord_mipmap = v0_texCoord_mipmap + ddx_texCoord_mipmap * (fOffsetX) + ddy_texCoord_mipmap * (fOffsetY);
				for (; gl_FragCoord0 <= _endX; gl_FragCoord0++,
//__add_ddx();
x0_light += ddx_light,x0_texCoord.x += ddx_texCoord.x,x0_texCoord.y += ddx_texCoord.y,x0_texCoord_mipmap += ddx_texCoord_mipmap,
				x0_gl_FragDepth += ddx_gl_FragDepth) {
					float invW = 1.0f / x0_gl_FragDepth;
					int pixelOffset = _ywidth + gl_FragCoord0;
					float dstFragDepth = depthBuffer[pixelOffset];
					float gl_FragDepth = x0_gl_FragDepth;
//__PSInput();
 float light=0;
 vec2 texCoord=new vec2();
 float texCoord_mipmap=0;
light = x0_light*invW;texCoord.x = x0_texCoord.x*invW;texCoord.y = x0_texCoord.y*invW;texCoord_mipmap = x0_texCoord_mipmap*invW;
					boolean depthTestPass = true;
//__depth_test();
gl_FragDepth =invW; depthTestPass = dstFragDepth > gl_FragDepth;
					if (!depthTestPass)
						continue;
					boolean discard = false;
					boolean depthWriteMask = true;
					ivec4 gl_FragColor= ivec4(255, 0, 0, 255);
//__fragment_shader();
{
	if (texture0!=null)
	gl_FragColor=MipmapEXT.texture2D(texture0,texCoord,texCoord_mipmap);
//	gl_FragColor=texture2D(texture0,texCoord);
}
					if (depthWriteMask) //
						depthBuffer[pixelOffset] = gl_FragDepth;// –¥»ÎZª∫¥Ê
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

static vec3 phong_lighting(vec3 position,int arg1) {return position;
}


}