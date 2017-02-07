package us.ihmc.geometry.tuple3D;

import java.io.Serializable;

import us.ihmc.geometry.interfaces.GeometryObject;
import us.ihmc.geometry.transform.interfaces.Transform;
import us.ihmc.geometry.tuple3D.interfaces.Vector3DBasics;

public class Vector3D32 extends Tuple3D32 implements Serializable, Vector3DBasics, GeometryObject<Vector3D32>
{
   private static final long serialVersionUID = 1186918378545386628L;

   public Vector3D32()
   {
      super();
   }

   public Vector3D32(float x, float y, float z)
   {
      super(x, y, z);
   }

   public Vector3D32(float[] vectorArray)
   {
      super(vectorArray);
   }

   public Vector3D32(Tuple3D32 tuple)
   {
      super(tuple);
   }

   @Override
   public void set(Vector3D32 other)
   {
      super.set(other);
   }

   public void setAndNormalize(Vector3D32 other)
   {
      set(other);
      normalize();
   }

   public float angle(Vector3D32 other)
   {
      double vDot = dot(other) / (length() * other.length());
      return (float) Math.acos(Math.min(1.0, Math.max(-1.0, vDot)));
   }

   public void cross(Vector3D32 v1, Vector3D32 v2)
   {
      float x = v1.getY32() * v2.getZ32() - v1.getZ32() * v2.getY32();
      float y = v1.getZ32() * v2.getX32() - v1.getX32() * v2.getZ32();
      float z = v1.getX32() * v2.getY32() - v1.getY32() * v2.getX32();
      set(x, y, z);
   }

   public float dot(Vector3D32 other)
   {
      return getX32() * other.getX32() + getY32() * other.getY32() + getZ32() * other.getZ32();
   }

   public float length()
   {
      return (float) Math.sqrt(lengthSquared());
   }

   public float lengthSquared()
   {
      return dot(this);
   }

   public void normalize()
   {
      if (containsNaN())
         return;
      scale(1.0f / length());
   }

   @Override
   public void applyTransform(Transform transform)
   {
      transform.transform(this);
   }

   @Override
   public boolean epsilonEquals(Vector3D32 other, double epsilon)
   {
      return super.epsilonEquals(other, epsilon);
   }
}