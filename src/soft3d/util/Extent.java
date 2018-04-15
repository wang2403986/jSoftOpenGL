package soft3d.util;

import java.awt.Rectangle;

/**
 * Extent of the three-dimensional space
 * @author Administrator
 *
 */
public class Extent {
	/**
	 * 8个顶点
	 */
	float[] points=new float[8*3];
	float[] screenBuf=new float[8*2];
	float[] pointsBuf=new float[8*3];
	/**
	 * 指向物体的引用
	 */
	public Object object;
	/**
	 * 12条边
	 */
	int[][] edges=new int[12][2];
	public float x,y,z;
	/**
	 * 包围球半径
	 */
	public float Radius;
	/**
	 * 中心点
	 */
	public float [] centre={0,0,0};
	/**
	 * 沿轴线的旋转
	 */
	public float [] rotation={0,0,0};
	public float [] centreBuf={0,0,0};
	public float width;
	public float height;
	public float depth;
	/**
	 * 空的区域
	 */
	public boolean empty=true;
	
	/**
	 * 小于一个像素宽度
	 * @return
	 */
	public boolean minimal() {
		float minX=screenBuf[0],minY=screenBuf[1];
		float maxX=minX,maxY=minY;
		for (int i = 0; i < 16; i+=2) {
			float x=(screenBuf[i]);
			float y=(screenBuf[i+1]);
			if (x<minX) {
				minX=x;
			}
			else if(x>maxX){
				maxX=x;
			}
			if (y<minY) {
				minY=y;
			}
			else if(y>maxY){
				maxY=y;
			}
		}
		if (maxX-minX<1||maxY-minY<1) {
			return true;
		}
		return false;
		
	}
	public void screenBounds(Rectangle rect) {
		
	}
	float[][] bufs1={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	public static void unionExtent(Extent e0,Extent e1,Extent union){
		if(e0.empty&&e1.empty)
			return;
		if (e0.empty||e1.empty){
			if (e0.empty)
				e0=e1;
			union.x=e0.x;
			union.y=e0.y;
			union.z=e0.z;
			union.width=e0.width;
			union.height=e0.height;
			union.depth=e0.depth;
			union.centre[0]=e0.centre[0];
			union.centre[1]=e0.centre[1];
			union.centre[2]=e0.centre[2];
			union.empty=e0.empty;
		}
		else {
			float minX=e0.x,minY=e0.y,minZ=e0.z;
			
			float maxX=minX+e0.width,maxY=minY+e0.height,maxZ=minZ+e0.depth;
			if (e1.x<minX) {
				minX=e1.x;
			}
			if (e1.x+e1.width>maxX) {
				maxX=e1.x+e1.width;
			}
			
			if (e1.y<minY) {
				minY=e1.y;
			}
			if (e1.y+e1.height>maxY) {
				maxY=e1.y+e1.height;
			}
			
			if (e1.z<minZ) {
				minZ=e1.z;
			}
			if (e1.z+e1.depth>maxZ) {
				maxZ=e1.z+e1.depth;
			}
			union.empty=false;
			union.x=minX;
			union.y=minY;
			union.z=minZ;
			union.width=maxX-minX;
			union.height=maxY-minY;
			union.depth=maxZ-minZ;
			union.centre[0]=minX+union.width/2;
			union.centre[1]=minY+union.height/2;
			union.centre[2]=minZ+union.depth/2;
		}
	}
	public static void calcExtent(float[] vertices,Extent e) {
		e.empty=true;
		if(vertices==null)
			return;
		float minX=vertices[0],minY=vertices[1],minZ=vertices[2];
		e.empty=false;
		float maxX=minX,maxY=minY,maxZ=minZ;
		for (int i = 0; i < vertices.length; i+=3) {
			float x=vertices[i];
			float y=vertices[i+1];
			float z=vertices[i+2];
			if (x<minX)
				minX=x;
			else if(x>maxX)
				maxX=x;
			
			if (y<minY)
				minY=y;
			else if(y>maxY)
				maxY=y;
			
			if (z<minZ)
				minZ=z;
			else if(z>maxZ)
				maxZ=z;
		}
		e.x=minX;
		e.y=minY;
		e.z=minZ;
		e.width=maxX-minX;
		e.height=maxY-minY;
		e.depth=maxZ-minZ;
		e.centre[0]=minX+e.width/2;
		e.centre[1]=minY+e.height/2;
		e.centre[2]=minZ+e.depth/2;
	}
}
