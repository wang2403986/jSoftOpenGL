package soft3d.util;

import soft3d.Vec3;

/**
 * 基本立体几何图形
 * @author Administrator
 *
 */
public class BasicGeometry {
	/**
	 * 创建网格
	 * @param width
	 * @param height
	 * @param m
	 * @param n
	 * @param mesh
	 */
	public static void CreateGrid(float width, float height, int m, int n, Mesh mesh)
	{
//		mesh.vertices.clear();
//		mesh.indices.clear();
		//每行顶点数、每列顶点数
		int nVertsRow = m + 1;
		int nVertsCol = n + 1;
		//起始x、z坐标
		float oX = -width * 0.5f;
		float oZ = height * 0.5f;
		//每一格纹理坐标变化
		float dx = width / m;
		float dz = height /n;

		//顶点总数量：nVertsRow * nVertsCol
		mesh.rawVertices=new CustomVertex[nVertsRow * nVertsCol];
		for (int i = 0; i < mesh.rawVertices.length; i++) {
			mesh.rawVertices[i]=new CustomVertex();
		}

		//逐个添加顶点
		for(int i=0; i<nVertsCol; ++i)
		{
			float tmpZ = oZ - dz * i;
			for(int j=0; j<nVertsRow; ++j)
			{
				int index = nVertsRow * i + j;
				mesh.rawVertices[index].pos=new float[3];
				mesh.rawVertices[index].pos[0] = oX + dx * j;
				mesh.rawVertices[index].pos[1] = 0.f;
				mesh.rawVertices[index].pos[2] = tmpZ;

				mesh.rawVertices[index].normal = new float[]{0.f,1.f,0.f};
				mesh.rawVertices[index].tangent = new float[]{1.f,0.f,0.f};
				
				mesh.rawVertices[index].tex = new float[]{dx*j/width,dx*i/height};
			}
		}

		//总格子数量:m * n
		//因此总索引数量: 6 * m * n
		int nIndices = m * n * 6;
		mesh.indices=new int[nIndices];
		int tmp = 0;
		for(int i=0; i<n; ++i)
		{
			for(int j=0; j<m; ++j)
			{
				mesh.indices[tmp] = i * nVertsRow + j;
				mesh.indices[tmp+1] = i * nVertsRow + j + 1;
				mesh.indices[tmp+2] = (i + 1) * nVertsRow + j;
				mesh.indices[tmp+3] = i * nVertsRow + j + 1;
				mesh.indices[tmp+4] = (i + 1) * nVertsRow + j + 1;
				mesh.indices[tmp+5] = (i + 1) * nVertsRow + j;
				
				tmp += 6;
			}
		}
	}
	/**
	 * 创建长方体
	 * @param width 宽
	 * @param height 高
	 * @param depth 长
	 * @param mesh
	 */
	public static void CreateBox(float width, float height, float depth, Mesh mesh)
	{
//		mesh.vertices.clear();
//		mesh.indices.clear();

		//一共24个顶点(每面4个)
		mesh.rawVertices=new CustomVertex[24];
		for (int i = 0; i < mesh.rawVertices.length; i++) {
			mesh.rawVertices[i]=new CustomVertex();
		}
		//一共36个索引(每面6个)
		mesh.indices=new int[36];

		float halfW = width * 0.5f;
		float halfH = height * 0.5f;
		float halfD = depth * 0.5f;

		//眼睛面向z轴正方向
		//构建顶点
		//前面
		mesh.rawVertices[0].pos = new float[]{-halfW,-halfH,-halfD};
		mesh.rawVertices[0].normal = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[0].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[0].tex = new float[]{0.f,1.f};
		mesh.rawVertices[1].pos = new float[]{-halfW,halfH,-halfD};
		mesh.rawVertices[1].normal = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[1].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[1].tex = new float[]{0.f,0.f};
		mesh.rawVertices[2].pos = new float[]{halfW,halfH,-halfD};
		mesh.rawVertices[2].normal = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[2].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[2].tex = new float[]{1.f,0.f};
		mesh.rawVertices[3].pos = new float[]{halfW,-halfH,-halfD};
		mesh.rawVertices[3].normal = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[3].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[3].tex = new float[]{1.f,1.f};
		//左侧面
		mesh.rawVertices[4].pos = new float[]{-halfW,-halfH,halfD};
		mesh.rawVertices[4].normal = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[4].tangent = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[4].tex = new float[]{0.f,1.f};
		mesh.rawVertices[5].pos = new float[]{-halfW,halfH,halfD};
		mesh.rawVertices[5].normal = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[5].tangent = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[5].tex = new float[]{0.f,0.f};
		mesh.rawVertices[6].pos = new float[]{-halfW,halfH,-halfD};
		mesh.rawVertices[6].normal = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[6].tangent = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[6].tex = new float[]{1.f,0.f};
		mesh.rawVertices[7].pos = new float[]{-halfW,-halfH,-halfD};
		mesh.rawVertices[7].normal = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[7].tangent = new float[]{0.f,0.f,-1.f};
		mesh.rawVertices[7].tex = new float[]{1.f,1.f};
		//背面
		mesh.rawVertices[8].pos = new float[]{halfW,-halfH,halfD};
		mesh.rawVertices[8].normal = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[8].tangent = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[8].tex = new float[]{0.f,1.f};
		mesh.rawVertices[9].pos = new float[]{halfW,halfH,halfD};
		mesh.rawVertices[9].normal = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[9].tangent = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[9].tex = new float[]{0.f,0.f};
		mesh.rawVertices[10].pos = new float[]{-halfW,halfH,halfD};
		mesh.rawVertices[10].normal = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[10].tangent = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[10].tex = new float[]{1.f,0.f};
		mesh.rawVertices[11].pos = new float[]{-halfW,-halfH,halfD};
		mesh.rawVertices[11].normal = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[11].tangent = new float[]{-1.f,0.f,0.f};
		mesh.rawVertices[11].tex = new float[]{1.f,1.f};
		//右侧面
		mesh.rawVertices[12].pos = new float[]{halfW,-halfH,-halfD};
		mesh.rawVertices[12].normal = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[12].tangent = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[12].tex = new float[]{0.f,1.f};
		mesh.rawVertices[13].pos = new float[]{halfW,halfH,-halfD};
		mesh.rawVertices[13].normal = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[13].tangent = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[13].tex = new float[]{0.f,0.f};
		mesh.rawVertices[14].pos = new float[]{halfW,halfH,halfD};
		mesh.rawVertices[14].normal = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[14].tangent = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[14].tex = new float[]{1.f,0.f};
		mesh.rawVertices[15].pos = new float[]{halfW,-halfH,halfD};
		mesh.rawVertices[15].normal = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[15].tangent = new float[]{0.f,0.f,1.f};
		mesh.rawVertices[15].tex = new float[]{1.f,1.f};
		//上面
		mesh.rawVertices[16].pos = new float[]{-halfW,halfH,-halfD};
		mesh.rawVertices[16].normal = new float[]{0.f,1.f,0.f};
		mesh.rawVertices[16].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[16].tex = new float[]{0.f,1.f};
		mesh.rawVertices[17].pos = new float[]{-halfW,halfH,halfD};
		mesh.rawVertices[17].normal = new float[]{0.f,1.f,0.f};
		mesh.rawVertices[17].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[17].tex = new float[]{0.f,0.f};
		mesh.rawVertices[18].pos = new float[]{halfW,halfH,halfD};
		mesh.rawVertices[18].normal = new float[]{0.f,1.f,0.f};
		mesh.rawVertices[18].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[18].tex = new float[]{1.f,0.f};
		mesh.rawVertices[19].pos = new float[]{halfW,halfH,-halfD};
		mesh.rawVertices[19].normal = new float[]{0.f,1.f,0.f};
		mesh.rawVertices[19].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[19].tex = new float[]{1.f,1.f};
		//底面
		mesh.rawVertices[20].pos = new float[]{-halfW,-halfH,halfD};
		mesh.rawVertices[20].normal = new float[]{0.f,-1.f,0.f};
		mesh.rawVertices[20].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[20].tex = new float[]{0.f,1.f};
		mesh.rawVertices[21].pos = new float[]{-halfW,-halfH,-halfD};
		mesh.rawVertices[21].normal = new float[]{0.f,-1.f,0.f};
		mesh.rawVertices[21].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[21].tex = new float[]{0.f,0.f};
		mesh.rawVertices[22].pos = new float[]{halfW,-halfH,-halfD};
		mesh.rawVertices[22].normal = new float[]{0.f,-1.f,0.f};
		mesh.rawVertices[22].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[22].tex = new float[]{1.f,0.f};
		mesh.rawVertices[23].pos = new float[]{halfW,-halfH,halfD};
		mesh.rawVertices[23].normal = new float[]{0.f,-1.f,0.f};
		mesh.rawVertices[23].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[23].tex = new float[]{1.f,1.f};
		
		//构建索引
		mesh.indices[0] = 0;
		mesh.indices[1] = 1;
		mesh.indices[2] = 2;
		mesh.indices[3] = 0;
		mesh.indices[4] = 2;
		mesh.indices[5] = 3;
		
		mesh.indices[6] = 4;
		mesh.indices[7] = 5;
		mesh.indices[8] = 6;
		mesh.indices[9] = 4;
		mesh.indices[10] = 6;
		mesh.indices[11] = 7;
		
		mesh.indices[12] = 8;
		mesh.indices[13] = 9;
		mesh.indices[14] = 10;
		mesh.indices[15] = 8;
		mesh.indices[16] = 10;
		mesh.indices[17] = 11;
		
		mesh.indices[18] = 12;
		mesh.indices[19] = 13;
		mesh.indices[20] = 14;
		mesh.indices[21] = 12;
		mesh.indices[22] = 14;
		mesh.indices[23] = 15;
		
		mesh.indices[24] = 16;
		mesh.indices[25] = 17;
		mesh.indices[26] = 18;
		mesh.indices[27] = 16;
		mesh.indices[28] = 18;
		mesh.indices[29] = 19;
		
		mesh.indices[30] = 20;
		mesh.indices[31] = 21;
		mesh.indices[32] = 22;
		mesh.indices[33] = 20;
		mesh.indices[34] = 22;
		mesh.indices[35] = 23;
	}
	/**
	 * 创建球体
	 * @param radius
	 * @param slice
	 * @param stack
	 * @param mesh
	 */
	public static void CreateSphere(float radius, int slice, int stack, Mesh mesh)
	{
//		mesh.vertices.clear();
//		mesh.indices.clear();

		int vertsPerRow = slice + 1;
		int nRows = stack - 1;

		int nVerts = vertsPerRow * nRows + 2;
		int nIndices = (nRows-1)*slice*6 + slice * 6;

		mesh.rawVertices=new CustomVertex[nVerts];
		for (int i = 0; i < mesh.rawVertices.length; i++) {
			mesh.rawVertices[i]=new CustomVertex();
		}
		mesh.indices=new int[nIndices];

		for(int i=1; i<=nRows; ++i)
		{
			float phy = (float)Math.PI * i / stack;//XM_PI
			float tmpRadius = radius * (float)Math.sin(phy);
			for(int j=0; j<vertsPerRow; ++j)
			{
				float theta = (float)Math.PI*2 * j / slice;//XM_2PI
				int index = (i-1)*vertsPerRow+j;

				float x = tmpRadius*(float)Math.cos(theta);
				float y = radius*(float)Math.cos(phy);
				float z = tmpRadius*(float)Math.sin(theta);

				//位置坐标
				mesh.rawVertices[index].pos = new float[]{x,y,z};
				//法线
				float[] N = new float[]{x,y,z,0.f};
				Vec3.normalize1(N);
				mesh.rawVertices[index].normal=N;
				//切线
//				XMVECTOR T = XMVectorSet(-sin(theta),0.f,cos(theta),0.f);
//				XMStoreFloat3(&mesh.vertices[index].tangent,XMVector3Normalize(T));
				//纹理坐标
				mesh.rawVertices[index].tex = new float[]{j*1.f/slice,i*1.f/stack};
			}
		}

		int size = vertsPerRow * nRows;
		//添加顶部和底部两个顶点信息
		mesh.rawVertices[size].pos = new float[]{0.f,radius,0.f};
		mesh.rawVertices[size].normal = new float[]{0.f,1.f,0.f};
		mesh.rawVertices[size].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[size].tex = new float[]{0.f,0.f};

		mesh.rawVertices[size+1].pos = new float[]{0.f,-radius,0.f};
		mesh.rawVertices[size+1].normal = new float[]{0.f,-1.f,0.f};
		mesh.rawVertices[size+1].tangent = new float[]{1.f,0.f,0.f};
		mesh.rawVertices[size+1].tex = new float[]{0.f,1.f};
		
		int tmp=(0);
		int start1 = 0;
		int start2 = mesh.rawVertices.length - vertsPerRow - 2;
		int top = size;
		int bottom = size + 1;
		for(int i=0; i<slice; ++i)
		{
			mesh.indices[tmp] = top;
			mesh.indices[tmp+1] = start1+i+1;
			mesh.indices[tmp+2] = start1+i;

			tmp += 3;
		}

		for(int i=0; i<slice; ++i)
		{
			mesh.indices[tmp] = bottom;
			mesh.indices[tmp+1] = start2 + i;
			mesh.indices[tmp+2] = start2 + i + 1;

			tmp += 3;
		}

		for(int i=0; i<nRows-1; ++i)
		{
			for(int j=0; j<slice; ++j)
			{
				mesh.indices[tmp] = i * vertsPerRow + j;
				mesh.indices[tmp+1] = (i + 1) * vertsPerRow + j + 1;
				mesh.indices[tmp+2] = (i + 1) * vertsPerRow + j;
				mesh.indices[tmp+3] = i * vertsPerRow + j;
				mesh.indices[tmp+4] = i * vertsPerRow + j + 1;
				mesh.indices[tmp+5] = (i + 1) * vertsPerRow + j + 1;

				tmp += 6;
			}
		}
	}


}
