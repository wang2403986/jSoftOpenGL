package soft3d;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

import loader.xfile.XModel;
import loader.xfile.XModelSerializer;
import soft3d.util.BasicGeometry;
import soft3d.util.Extent;
import soft3d.util.Mesh;
import soft3d.Framebuffer;
import soft3d.v1_0.GLM;
import soft3d.v1_0.Matrix4;
import soft3d.v1_0.MipmapEXT;
import soft3d.v1_0.TinyGL;
import soft3d.v1_0.TrackballControls;
import soft3d.v1_0.compiler.ShaderCompiler;
import soft3d.v1_0.types.*;

import static soft3d.Matrix.loadIdentity;
import static soft3d.Framebuffer.VIEWMATRIX;
import static soft3d.v1_0.GLM.vec3;
import static soft3d.v1_0.TinyGL.*;

public class Application extends JFrame implements ActionListener{	
	private static final long serialVersionUID = 1L;
//	String folder="D:/Download/anew/models/exp/";//a1-18 a13
	String f0=System.getProperty("user.dir")+"\\models\\";
	String f7="C:\\Users\\Administrator\\Desktop\\X Exporter\\a1\\";
	String f8="C:\\Users\\Administrator\\Desktop\\X Exporter\\a2\\aaa\\";
	String f9="C:\\Users\\Administrator\\Desktop\\X Exporter\\a2\\";
//	LoopSubdivision subdivision=new LoopSubdivision();
//	PNTriangles PN=new PNTriangles();
//	PhongTessellation phongTess=new PhongTessellation();
	Mesh mesh1=new Mesh();
	public Application() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		String folder = f0;
		xfile = "a4.X";
		init();
		model=new XModelSerializer().importFromFile(folder+xfile);
//		folder=folder+xfile.substring(0, xfile.length()-2)+"/";
		model.loadTexture(folder);
//		model.updateByTime(200);model.calcExtent(extent,true);
//		BasicGeometry.CreateGrid(16, 16,1,1, meshData);
//		BasicGeometry.CreateBox(16, 16, 8, meshData);
//		BasicGeometry.CreateSphere(16,160, 160, meshData);//160
//		BasicGeometry.CreateGrid(200, 200, 8, 8, mesh1);
//		meshData.passConvert();
		
//		meshData=MeshData.create(model.frames.get(0).mesh);
//		PN.tessellate(meshData);PN.convertToMesh();
//		phongTess.tessellate(meshData);phongTess.convertToMesh();
//		meshData.creatSimpleMesh2();
//		meshData.passConvert();
//		SoftGraphics3D.computeNormals(meshData.vertices, meshData.indices, meshData.normals);
//		subdivision.loadModel(meshData);//model.frames.get(0).mesh
//		for (int i = 0; i < 4; i++)
//		subdivision.loopSubdivision();
//		subdivision.convertToMesh();
		initGL();
	}
	long frameTime=System.currentTimeMillis();
	int frameCount=0,frameRate=0;
	ArrayList<BufferedImage> bufferedImages;
	public void initGL(){
//		glBindAttribLocation(0, 0, "position");//position
		glVertexAttribPointer(0, 3, 0, true, 0, 0);//position
		glVertexAttribPointer(1, 2, 0, true, 0, 0);//texCoord
		glVertexAttribPointer(2, 3, 0, true, 0, 0);//normal
		gl = new soft3d.v1_0.TinyGLImpl2();
//		gl=ShaderCompiler.createProgram("kernels/vertexShader.txt","kernels/fragShader.txt");
		try {
			gl.texture0=new Texture("D:/用户目录/Pictures/a1.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		bufferedImages = MipmapEXT.createMipmapEXT(gl.texture0);
		framebuffer=new Framebuffer(getGraphics(),1024,680);
		gl.glBindFramebuffer(framebuffer);
		trackball=new TrackballControls(this);
		trackball.setCamera(vec3(-0.1032f, 0.97f,-5.09f),vec3(-0.103f,0.970f, 0.0f),vec3(0,1f,0));
		trackball.handleResize(TinyGL.width, TinyGL.height);
	}
	TrackballControls trackball;
	protected void render() {
		Matrix4.identity(TinyGL.GLProjMatrix);
		Matrix4.identity(TinyGL.GLViewMatrix);
		
		Matrix4.perspective(TinyGL.GLProjMatrix,45, 1f, 1f, 1000.0f);
		trackball.update();
		trackball.getViewMatrix(TinyGL.GLViewMatrix);
//		Matrix4.lookAt(GLViewMatrix, eye, target, up);
		Matrix4.copy(VIEWMATRIX, TinyGL.GLViewMatrix);//先 (arg2)
		if (!isShowing()) {
			return;
		}
		gl.glClear(0);
		long time=System.currentTimeMillis()-beginTime;
		model.updateByTime(time<<2);  model.draw(gl);
//		meshData.update();
//		glBindBufferData(0, 0, 0, meshData.verticesBuf, 0);//position
//		glBindBufferData(0, 1, 0, meshData.texCoords, 0);//texCoord
//		glBindBufferData(0, 2, 0, meshData.normalsBuf, 0);//normal
//		gl.glDrawElements(0, 0, 0, meshData.indices);
		
//		subdivision.update();subdivision.draw(graphics3d);
//		PN.update();PN.draw(graphics3d);
//		phongTess.update();phongTess.draw(graphics3d);
//		model.updateByTime(time<<2);
		if(System.currentTimeMillis()-frameTime>1000){
			frameTime=System.currentTimeMillis();
			frameRate=frameCount;
			frameCount=0;
		}
		frameCount++;
		framebuffer.drawString("动画帧率："+frameRate, 20,20);
//		int height=0;
//		if(bufferedImages!=null)
//		for (BufferedImage i: bufferedImages) {
//			framebuffer.bufferedGraphics.drawImage(i, height, 40, null);
//			height+=i.getWidth();
//		}
		gl.glFinish(0);
		 
	}
	
	public static void main(String[] s) {
		Application app=new Application();
		app.setVisible(true);
		Timer timer=new Timer(10, app);
		timer.start();
	}
	Framebuffer framebuffer;
	TinyGL gl; 
	void init(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1048, 700);
		this.setVisible(true);
		ComponentAdapter adapter = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Container contentPane=Application.this.getContentPane();
				contentPane.getLocationOnScreen();// 注意只有窗口显示后getLocationOnScreen才可以调用，否则出错
				Dimension size = contentPane.getSize(); // 可视区域的大小
				if (gl==null) return;
				gl.glViewport(size.width, size.height);
				trackball.handleResize(size.width, size.height);
			}
		};
		this.addComponentListener(adapter);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		render();
	}
	
	XModel model=null;
	Mesh meshData=new Mesh();
	final Extent extent=new Extent();
	long beginTime=System.currentTimeMillis();
	String xfile="";
}
