package soft3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import loader.xfile.TGALoader;
import soft3d.v1_0.MipmapEXT;
import soft3d.v1_0.types.ivec4;

public final class Texture {
	public float widthSub1;
	public float heightSub1;
	public int width;
	public int height;
	public int[] intData;
	public Texture[] mipmap;

	public Texture() {

	}

	public final void nearestFilter(final ivec4 v,float x,float y) {
		int w=width, h=height;
		int iX=(int) (x*w +0.5f);
		if(iX>=w) iX=w-1;
		int iY=(int) (y*h +0.5f);
		if(iY>=h) iY=h-1;
		int index = iY*w+iX;
		final int[] data = intData;
		if( index<0) return ;
		int rgba=data[index];
		v.x=rgba >>> 24;
		v.y=(rgba >> 16) &0xff;
		v.z=(rgba >> 8) &0xff;
		v.w=rgba & 0xff;
	}
	public final void bilinearFilter(final ivec4 color, float x, float y) {
		int w=width, h=height;
		float fx=(w)*x ;
		float fy=(h)*y;
//		if (x<0||y<0||x>1||y>1) {
			//System.err.println("x::"+x+","+y);
//		}
		if (fx<0) fx=0;
		int x1= (int)(fx );
		int x2=x1 + 1;
		if(x1>=w){
			x1=w-1;
		}
		if(x2>=w){
			x2=w-1;
		}
		
		if (fy<0) fy=0;
		int y1=(int)(fy);
		int y2=y1 +1;
		if(y1>=h){
			y1=h-1;
		}
		if(y2>=h){
			y2=h-1;
		}
		
		final int[] data=intData;
		int y1w=y1 * w;
		int y2w=y2 * w;
		int pos = y1w+ x1;// length = data.length-1;
		int x1y1=data[pos ];//pos< 0 ? 0:(pos> length?length:pos)
		pos = y1w+ x2;
		int x2y1=data[pos ];
		pos = y2w+ x1;
		int x1y2=data[pos ];
		pos = y2w+ x2;
		int x2y2=data[pos ];
		
		int x1y1r=(x1y1 >> 16) & 0xff;
		int x1y1g=(x1y1 >> 8) & 0xff;
		int x1y1b=x1y1  & 0xff;
		int x1y1a=x1y1 >>> 24;
		
		int x2y1r=(x2y1 >> 16) & 0xff;
		int x2y1g=(x2y1 >> 8) & 0xff;
		int x2y1b=x2y1  & 0xff;
		int x2y1a=x2y1 >>> 24;
		
		int x1y2r=(x1y2 >> 16) & 0xff;
		int x1y2g=(x1y2 >> 8) & 0xff;
		int x1y2b=x1y2  & 0xff;
		int x1y2a=x1y2 >>> 24;
		
		int x2y2r=(x2y2 >> 16) & 0xff;
		int x2y2g=(x2y2 >> 8) & 0xff;
		int x2y2b=x2y2  & 0xff;
		int x2y2a=x2y2 >>> 24;
		
//		float u=fx-x1;
//        float v=fy-y1;
//        float pm3=u*v;
//        float pm2=u*(1-v);
//        float pm1=v*(1-u);
//        float pm0=(1-u)*(1-v);
//
//        color.x=(int) ((pm0*x1y1a+pm1*x1y2a+pm2*x2y1a+pm3*x2y2a+0.5f));
//        color.y=(int) ((pm0*x1y1r+pm1*x1y2r+pm2*x2y1r+pm3*x2y2r+0.5f));
//        color.z=(int) ((pm0*x1y1g+pm1*x1y2g+pm2*x2y1g+pm3*x2y2g+0.5f));
//        color.w= (int) ((pm0*x1y1b+pm1*x1y2b+pm2*x2y1b+pm3*x2y2b+0.5f));
		
//		int u=(int) (fx* 1024)-(x1<<10);
//		int v=(int) (fy* 1024)-(y1<<10);
		int u=(int) ((fx-x1-0)* 1024);
        int v=(int) ((fy-y1-0)* 1024);
		int pm3=u*v;
		int pm2=u*(1024-v);
		int pm1=v*(1024-u);
		int pm0=(1024-u)*(1024-v);

		color.x= (pm0*x1y1a+pm1*x1y2a+pm2*x2y1a+pm3*x2y2a)>>20;
        color.y= (pm0*x1y1r+pm1*x1y2r+pm2*x2y1r+pm3*x2y2r)>>20;
        color.z= (pm0*x1y1g+pm1*x1y2g+pm2*x2y1g+pm3*x2y2g)>>20;
        color.w= (pm0*x1y1b+pm1*x1y2b+pm2*x2y1b+pm3*x2y2b)>>20;
	}
	public Texture(String fileName) throws IOException {
		this();
		loadFromFile(fileName);
	}

	public void loadFromFile(String fileName) throws IOException {
		BufferedImage bufferedImage=null;
		Pattern tga = Pattern.compile("^[\\d\\D]*\\.[Tt][Gg][aA]$");
		if (tga.matcher(fileName).matches()) {
			bufferedImage=TGALoader.loadBufferedImage(fileName);
		} else{
			bufferedImage = ImageIO.read(new File(fileName));
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		widthSub1 = width - 1;
		heightSub1 = height - 1;
		intData = new int[height * width];
		bufferedImage.getRGB(0, 0, width, height, intData, 0, width);
		bufferedImage = null;
		}
		MipmapEXT.createMipmap(this);
	}
	public void setSize(int w, int h) {
		width = w;
		height = h;
		widthSub1 = width - 1;
		heightSub1 = height - 1;
	}

}