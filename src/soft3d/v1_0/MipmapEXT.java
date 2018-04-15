package soft3d.v1_0;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import soft3d.Texture;
import soft3d.v1_0.GLM;
import soft3d.v1_0.types.ivec4;
import soft3d.v1_0.types.vec2;
import soft3d.v1_0.types.vec3;

public final class MipmapEXT {
  static int previous_power_of_two( int x ) {
    if (x == 0) {
        return 0;
    }
     x--; //Uncomment this, if you want a strictly less than 'x' result.
    x |= (x >> 1);
    x |= (x >> 2);
    x |= (x >> 4);
    x |= (x >> 8);
    x |= (x >> 16);
    return x - (x >> 1);
}
  static int upper_power_of_two(int v)
  {
      v--;
      v |= v >> 1;
      v |= v >> 2;
      v |= v >> 4;
      v |= v >> 8;
      v |= v >> 16;
      v++;
      return (int) v;

  }
  public static final ArrayList<BufferedImage> createMipmapEXT(Texture tex){
	  ArrayList<BufferedImage> bufferedImages=new ArrayList<BufferedImage>();
	  int start = 1;
	    int end = tex.width;
	    int iii=end -1;
	    BufferedImage image = new BufferedImage(tex.width, tex.height,
	        BufferedImage.TYPE_INT_ARGB);//构造一个类型为预定义图像类型之一的 BufferedImage。
	    image.setRGB(0, 0, tex.width, tex.height, tex.intData, 0, tex.width);
	    ArrayList<Texture> mipmaps = new ArrayList<Texture>();
	    bufferedImages.add(image);
	    while(previous_power_of_two(iii)>=start){
	      int size = previous_power_of_two(iii);
	      System.err.println(size);
	      if (size*tex.height/tex.width<1) {
	        break;
	      }
	      BufferedImage newImg =getScaledImage(image, size ,size*tex.height/tex.width);
	      bufferedImages.add(newImg);
	      Texture tex0= new Texture();
	      tex0.setSize(newImg.getWidth(), newImg.getHeight());
	      tex0.intData = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
	      mipmaps.add(tex0);
	      iii=previous_power_of_two(iii);
	    }
	    
		return bufferedImages;
	    
  }
  public static final void createMipmap(Texture tex){
	  if(tex.width<=0)return;
    int start = 4;
    int end = tex.width;
    int iii=end -1;
    BufferedImage image = new BufferedImage(tex.width, tex.height,
        BufferedImage.TYPE_INT_ARGB);//构造一个类型为预定义图像类型之一的 BufferedImage。
    image.setRGB(0, 0, tex.width, tex.height, tex.intData, 0, tex.width);
    ArrayList<Texture> mipmaps = new ArrayList<Texture>();
    mipmaps.add(tex);
    while(previous_power_of_two(iii)>=start){
      int size = previous_power_of_two(iii);
      System.err.println(size);
      if (size*tex.height/tex.width<1) {
        break;
      }
      BufferedImage newImg =getScaledImage(image, size ,size*tex.height/tex.width);
      Texture tex0= new Texture();
      tex0.setSize(newImg.getWidth(), newImg.getHeight());
      tex0.intData = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
      mipmaps.add(tex0);
      iii=previous_power_of_two(iii);
    }
    tex.mipmap=new Texture[mipmaps.size()];
    mipmaps.toArray(tex.mipmap);
  }
  public static final ivec4 texture2D(Texture tex,vec3 UV) {
	  return texture2D(tex, GLM.vec2(UV.x,UV.y), UV.z);
  }
  public static final ivec4 texture2D(Texture tex,vec2 UV, float mipmap) {
	  final ivec4 color=new ivec4();
	  if(tex.mipmap==null)return color;
    int Level = (int) (mipmap);
    if (Level<0) 
      Level=0;
    int length = tex.mipmap.length;
    if (Level>= length)
      Level=length-1;
    tex.mipmap[Level].bilinearFilter(color, UV.x,UV.y);
    if (mipmap>0 && Level+1<length) {
//      ivec4 color1=GLM.texture2D(tex.mipmap[Level+1], UV);
      final ivec4 color1=new ivec4();
      tex.mipmap[Level+1].bilinearFilter(color1, UV.x,UV.y);
      float ratio =(mipmap-Level);
      int pm0=(int) (ratio*(2048));
	  int pm1=(2048-pm0);
//	  color.x =  (pm1*color.x+ pm0*color1.x)>>11;
	  color.y =  (pm1*color.y+ pm0*color1.y)>>11;
      color.z =  (pm1*color.z+ pm0*color1.z)>>11;
      color.w =  (pm1*color.w+ pm0*color1.w)>>11;

    }
    return color;
  }
//  private static final double HALF_INVLOG2 = 0.5 / Math.log(2);
  public static final float mipmapLevel(Texture texture,vec2 UV, vec2 ddx_UV, vec2 ddy_UV,
	      float w,float ddx_w,float ddy_w) {
	  if(texture==null) return 0;
	    float textureSizeX = texture.width;
	    float textureSizeY = texture.height;
	   
	    float tmp0 = (w+ddx_w)==0?w: 1/(w+ddx_w);
	    float tmp1 = (w+ddy_w)==0?w: 1/(w+ddy_w);
	   final vec2 UVxw = UV;
	   UV = GLM.mul(UV, 1.0f/w);
	    float Ux = (UVxw.x + ddx_UV.x)*tmp0  - UV.x;
	    float Vx = (UVxw.y + ddx_UV.y)*tmp0  - UV.y;
	    
	    float Uy = (UVxw.x + ddy_UV.x)*tmp1  - UV.x;
	    float Vy = (UVxw.y + ddy_UV.y)*tmp1  - UV.y;
	    
//	    float Uy = UV0.x + a * u01 + b * u02 - UV.x;
	    Ux = Ux * textureSizeX;
	    Uy = Uy * textureSizeX;
	    Vx = Vx * textureSizeY;
	    Vy = Vy * textureSizeY;
	    float d = Math.max((Ux * Ux + Vx * Vx), (Uy * Uy + Vy * Vy));
//	    double level = Math.log(d) * HALF_INVLOG2;
	    float level = 0.5f * FastMath.log2(d);
	    return (float) level*w;
	  }
  
  public static BufferedImage getScaledImage(BufferedImage originalFile, 
          int newWidth, int newHeight)  {  

      Image i = originalFile;
      Image temp = i.getScaledInstance(newWidth, newHeight, Image.SCALE_AREA_AVERAGING);

      // Create the buffered image.  
      BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null),  
              temp.getHeight(null), BufferedImage.TYPE_INT_ARGB);  

      // Copy image to buffered image.  
      Graphics2D g = bufferedImage.createGraphics();
      g.drawImage(temp, 0, 0, null);
      g.dispose();
//      RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
//    renderHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
      return (bufferedImage);  
  }
}
