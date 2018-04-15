package soft3d;

import static soft3d.Vec3.dot;
import static soft3d.Vec3.normalize;
import static soft3d.Vec3.vec3;
@Deprecated
public class Light {
	
	/**
	 * ������
	 */
	public static float g_ambient=0.1f;//0.1f
	/**
	 * ���淴��Ĺ���ȣ����������С
	 */
	public static int g_shininess=5;//5,7
	/**
	 * ������ϵ��
	 */
	public static float g_diffuse=1.0f;
	
	/**
	 * ƽ�й�
	 */
	public static final Vec3 g_DirLight=vec3(0f,0f,1f);
	/**
	 * ��Դλ��
	 */
	public static final Vec3 g_LightPos=vec3(-600f,0f,-1024f);
	/**
	 * �۾�λ��
	 */
	public static final Vec3 g_eyePos=vec3(400f,0f,-800f);
	
	/**
	 * �������
	 */
	static final Vec3 H =vec3(0,0,0);
	static final Vec3 L=vec3(0,0,0),NL=vec3(0,0,0),V=vec3(0,0,0);
	
	/**
	 * Blinn_Phong����ģ��
	 * @param worldPos
	 * @param N
	 * @return
	 */
//	public static final int blinn_phong_lighting(Vertex e) {//blinn_phong_lighting
//		float x=e.x,y=e.y,z=e.z;
//		//��������ⷽ��
//		L.x=g_LightPos.x - x;
//		L.y=g_LightPos.y - y;
//		L.z=g_LightPos.z - z;
//		
//		//�������߷���
//		V.x=g_eyePos.x - x;
//		V.y=g_eyePos.y - y;
//		V.z=g_eyePos.z - z;
//		
//		H.x=L.x+V.x;
//		H.y=L.y+V.y;
//		H.z=L.z+V.z;
//		
//		float saturate;
//		//�����������ǿ
//		Vec3.normalize(L);
//		saturate=dot(e.n, L);
//		saturate = saturate < 0f ? 0f : saturate ;
//		float diffuse = saturate;
//		//���㾵�淴���ǿ
//		Vec3.normalize(H);
//		saturate=dot( e.n, H);
//		saturate = saturate < 0f ? 0f : saturate ;
//		float specular=0;
//		if (g_shininess > 0)
//			specular = (float) pow(saturate, g_shininess);
//		//�����ܹ�ǿ
//		float l=(diffuse*2-1.4f+g_ambient)*100;
//		l+=specular*128;
//		e.l=l;
//		return 1;
//	}
	
	/**
	 * Phong����ģ��
	 * @return
	 */
//	public static final int phong_lighting(Vertex e) {//phong_lighting
//		Vec3 N=e.n;
//		
//		//��������ⷽ��
//		L.x=g_LightPos.x - e.x;
//		L.y=g_LightPos.y - e.y;
//		L.z=g_LightPos.z - e.z;
//		normalize( L );
//		//���㷴��ⷽ��
//		NL.x=-L.x;
//		NL.y=-L.y;
//		NL.z=-L.z;
//		reflect(NL, N);//R = reflect(-L, N)
//		Vec3 R = Light.R;
//		//�������߷���
//		V.x=g_eyePos.x - e.x;
//		V.y=g_eyePos.y - e.y;
//		V.z=g_eyePos.z - e.z;//g_eyePos.xyz - In.worldPos
//		normalize(V);
//
//		float saturate;
//		//�����������ǿ
//		saturate=dot(N,L);
//		if(saturate<0.0f)saturate=0.0f;
//		float diffuse = saturate;
//		//���㾵�淴���ǿ
//		saturate=-dot( N, R);
//		if(saturate<0.0f)saturate=0.0f;
//		float specular= 0;
//		if (g_shininess > 0)
//			specular = (float) pow(saturate, g_shininess);
//		float l = (diffuse * 2f - 1.4f) * 128;
//		l += specular * 128;
//		e.l = l;
//		return 1;
//	}
	static final Vec3 R=vec3(0f,0f,0f);
	static final void reflect(Vec3 L, Vec3 n) {
		/* L-2*(L*n)*n*/;  //2 * ( N dot L) * N - L
		float dot=dot(L,n);
		if (dot<0)
			dot=0;
		R.x=L.x-2*dot*n.x;
		R.y=L.y-2*dot*n.y;
		R.z=L.z-2*dot*n.z;
	}
	
//	public static final void blinn_phong_lighting1(Point3D e) {//blinn_phong_lighting
//		float x=e.objX,y=e.objY,z=e.objZ;
//		//��������ⷽ��
//		L.x=g_LightPos.x - x;
//		L.y=g_LightPos.y - y;
//		L.z=g_LightPos.z - z;
//		
//		//�������߷���
//		V.x=g_eyePos.x - x;
//		V.y=g_eyePos.y - y;
//		V.z=g_eyePos.z - z;
//		
//		H.x=L.x+V.x;
//		H.y=L.y+V.y;
//		H.z=L.z+V.z;
//		
//		float saturate;
//		//�����������ǿ
//		Vec3.normalize(L);
//		saturate = dot(e.normal, L);
//		saturate = saturate < 0f ? 0f : saturate ;
//		float diffuse = saturate;
//		//���㾵�淴���ǿ
//		Vec3.normalize(H);
//		saturate=dot( e.normal, H);
//		saturate = saturate < 0f ? 0f : saturate ;
//		float specular = g_shininess > 1 ? (float) pow(saturate, 12)
//				: 0;
//		//�����ܹ�ǿ
//		e.light = (diffuse * 2 - 1.4f + g_ambient) * 100;
//		e.light += specular * 128;
//		e.light = 100;
//	}
}
