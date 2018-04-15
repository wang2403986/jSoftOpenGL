package soft3d.v1_0;

import soft3d.v1_0.GLM;
import soft3d.v1_0.types.vec2;
import soft3d.v1_0.types.vec3;
import soft3d.v1_0.types.vec4;

public final class DFdxEXT {
	public static final vec2 dFdx(vec2 UV, vec2 ddx_UV, vec2 ddy_UV,
		      float w,float ddx_w/*,float ddy_w*/) {
		    float tmp0 = (w+ddx_w)==0?w: 1/(w+ddx_w);
//		    float tmp1 = (w+ddy_w)==0?w: 1/(w+ddy_w);
		   final vec2 UVxw = UV;
		   UV = GLM.mul(UV, 1.0f/w);
		    float Ux = (UVxw.x + ddx_UV.x)*tmp0  - UV.x;
		    float Vx = (UVxw.y + ddx_UV.y)*tmp0  - UV.y;
		    
//		    float Uy = (UVxw.x + ddy_UV.x)*tmp1  - UV.x;
//		    float Vy = (UVxw.y + ddy_UV.y)*tmp1  - UV.y;
		    
//		    float Uy = UV0.x + a * u01 + b * u02 - UV.x;
		    return GLM.vec2(Ux, Vx);
		  }
	public static final vec3 dFdx(vec3 UV, vec3 ddx_UV, vec3 ddy_UV,
		      float w,float ddx_w/*,float ddy_w*/) {
		    float tmp0 = (w+ddx_w)==0?w: 1/(w+ddx_w);
//		    float tmp1 = (w+ddy_w)==0?w: 1/(w+ddy_w);
		   final vec3 UVxw = UV;
		   UV = GLM.mul(UV, 1.0f/w);
		    float Ux = (UVxw.x + ddx_UV.x)*tmp0  - UV.x;
		    float Vx = (UVxw.y + ddx_UV.y)*tmp0  - UV.y;
		    float Zx = (UVxw.z + ddx_UV.z)*tmp0  - UV.z;
		    
//		    float Uy = (UVxw.x + ddy_UV.x)*tmp1  - UV.x;
//		    float Vy = (UVxw.y + ddy_UV.y)*tmp1  - UV.y;
		    
//		    float Uy = UV0.x + a * u01 + b * u02 - UV.x;
		    
		    return GLM.vec3(Ux, Vx, Zx);
		  }
	public static final vec4 dFdx(vec4 UV, vec4 ddx_UV, vec4 ddy_UV,
		      float w,float ddx_w/*,float ddy_w*/) {
		    float tmp0 = (w+ddx_w)==0?w: 1/(w+ddx_w);
//		    float tmp1 = (w+ddy_w)==0?w: 1/(w+ddy_w);
		   final vec4 UVxw = UV;
		   UV = GLM.mul(1.0f/w,  UV );
		    float Ux = (UVxw.x + ddx_UV.x)*tmp0  - UV.x;
		    float Vx = (UVxw.y + ddx_UV.y)*tmp0  - UV.y;
		    float Zx = (UVxw.z + ddx_UV.z)*tmp0  - UV.z;
		    float Wx = (UVxw.w + ddx_UV.w)*tmp0  - UV.w;
		    
//		    float Uy = (UVxw.x + ddy_UV.x)*tmp1  - UV.x;
//		    float Vy = (UVxw.y + ddy_UV.y)*tmp1  - UV.y;
		    
//		    float Uy = UV0.x + a * u01 + b * u02 - UV.x;
		    
		    return GLM.vec4(Ux, Vx, Zx,Wx);
		  }
	public static final float dFdx(float UV, float ddx_UV, float ddy_UV,
		      float w,float ddx_w/*,float ddy_w*/) {
		    float tmp0 = (w+ddx_w)==0?w: 1/(w+ddx_w);
//		    float tmp1 = (w+ddy_w)==0?w: 1/(w+ddy_w);
		   final float UVxw = UV;
		   UV = GLM.mul(UV, 1.0f/w);
		    float Ux = (UVxw + ddx_UV)*tmp0  - UV;
//		    float Vx = (UVxw.y + ddx_UV.y)*tmp0  - UV.y;
//		    float Zx = (UVxw.z + ddx_UV.z)*tmp0  - UV.z;
		    
//		    float Uy = (UVxw.x + ddy_UV.x)*tmp1  - UV.x;
//		    float Vy = (UVxw.y + ddy_UV.y)*tmp1  - UV.y;
		    
//		    float Uy = UV0.x + a * u01 + b * u02 - UV.x;
		    
		    return Ux;
		  }
}
