package loader.ogre.mesh;

public class VertexBuffer {
	int currentIndex=0;
	public int bindIndex;
	public int vertexCount;
	public byte[] byteBuffer;
	
public void convertVertexBuffer() {
		
	}
	void reset(){
		currentIndex=0;
	}
	int available(){
		return byteBuffer.length-currentIndex;
	}
	 int readInt() {
    	byte[] b=byteBuffer;
    	
    	
		int i=(b[3+currentIndex]&0xff)<<24;
			i+=	(b[2+currentIndex]&0xff)<<16;
			i+=(b[1+currentIndex]&0xff)<<8;
			i+=(b[currentIndex]&0xff);
			
			currentIndex+=4;
		return i;
	
}
	 void skip(int i){
		 currentIndex+=i;
	 }
  int readShort( ) {
    	byte[] b=byteBuffer;
    	int r= (b[1+currentIndex]&0xff)<<8;
    	r+=(b[currentIndex]&0xff);
    	
    	currentIndex+=2;
		return r;
		
	}
  
  float readFloat(){
	 int bits= readInt();
	 
	return Float.intBitsToFloat(bits);
	  
  }
  double readDouble(){
	  byte[] b=byteBuffer;
	  long i=(b[7+currentIndex]&0xff)<<24;
		i+=	(b[6+currentIndex]&0xff)<<16;
		i+=(b[5+currentIndex]&0xff)<<8;
		i+=(b[4+currentIndex]&0xff);
	   i+=(b[3+currentIndex]&0xff)<<24;
		i+=	(b[2+currentIndex]&0xff)<<16;
		i+=(b[1+currentIndex]&0xff)<<8;
		i+=(b[currentIndex]&0xff);
	  currentIndex+=8;
	 
	return  Double.longBitsToDouble(i);
	  
  }
}
