package soft3d.v1_0;

import soft3d.v1_0.types.vec3;

public class Quaternion {
public float x,y,z,w;
/*
///setFromAxisAngle方法绕任意轴设定旋转四元数
/// NOTE:参数axis必须是单位向量,通过调用.normalize()得到单位向量.
*/
///<summary>setFromAxisAngle</summary>
///<param name ="axis" type="Vector3">三维向量</param>
///<param name ="angle" type="float">角度</param>
///<returns type="Quaternion">返回新的四元数</returns>
public Quaternion setFromAxisAngle (vec3 axis, float angle ) {

  // http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm

  // assumes axis is normalized 
  // NOTE:参数axis必须是单位向量,通过调用.normalize()得到单位向量.

  float halfAngle = angle / 2, s = (float)Math.sin( halfAngle );

  this.x = axis.x * s;
  this.y = axis.y * s;
  this.z = axis.z * s;
  this.w = (float)Math.cos( halfAngle );

//  this.onChangeCallback();  //调用回调函数.

  return this;  //返回新的四元数

}
/*
///applyQuaternion方法应用一个四元数变换到当前三维向量.
*/
///<summary>applyQuaternion</summary>
///<param name ="q" type="Quaternion">四元数</param>
///<returns type="Vector3">返回新坐标值的三维向量</returns>
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

  return v;  //返回新坐标值的三维向量

}
}
