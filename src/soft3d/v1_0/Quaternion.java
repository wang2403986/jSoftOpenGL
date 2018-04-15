package soft3d.v1_0;

import soft3d.v1_0.types.vec3;

public class Quaternion {
public float x,y,z,w;
/*
///setFromAxisAngle�������������趨��ת��Ԫ��
/// NOTE:����axis�����ǵ�λ����,ͨ������.normalize()�õ���λ����.
*/
///<summary>setFromAxisAngle</summary>
///<param name ="axis" type="Vector3">��ά����</param>
///<param name ="angle" type="float">�Ƕ�</param>
///<returns type="Quaternion">�����µ���Ԫ��</returns>
public Quaternion setFromAxisAngle (vec3 axis, float angle ) {

  // http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm

  // assumes axis is normalized 
  // NOTE:����axis�����ǵ�λ����,ͨ������.normalize()�õ���λ����.

  float halfAngle = angle / 2, s = (float)Math.sin( halfAngle );

  this.x = axis.x * s;
  this.y = axis.y * s;
  this.z = axis.z * s;
  this.w = (float)Math.cos( halfAngle );

//  this.onChangeCallback();  //���ûص�����.

  return this;  //�����µ���Ԫ��

}
/*
///applyQuaternion����Ӧ��һ����Ԫ���任����ǰ��ά����.
*/
///<summary>applyQuaternion</summary>
///<param name ="q" type="Quaternion">��Ԫ��</param>
///<returns type="Vector3">����������ֵ����ά����</returns>
public static vec3 applyQuaternion ( vec3 v ,Quaternion q) {

  float x = v.x;
  float y = v.y;
  float z = v.z;

  float qx = q.x;
  float qy = q.y;
  float qz = q.z;
  float qw = q.w;

  // calculate quat * vector

  float ix =  qw * x + qy * z - qz * y;
  float iy =  qw * y + qz * x - qx * z;
  float iz =  qw * z + qx * y - qy * x;
  float iw = - qx * x - qy * y - qz * z;

  // calculate result * inverse quat
  v.x = ix * qw + iw * - qx + iy * - qz - iz * - qy;
  v.y = iy * qw + iw * - qy + iz * - qx - ix * - qz;
  v.z = iz * qw + iw * - qz + ix * - qy - iy * - qx;

  return v;  //����������ֵ����ά����

}
}
