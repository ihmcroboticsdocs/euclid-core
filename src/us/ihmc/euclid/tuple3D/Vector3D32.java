package us.ihmc.euclid.tuple3D;

import us.ihmc.euclid.interfaces.GeometryObject;
import us.ihmc.euclid.tools.EuclidCoreIOTools;
import us.ihmc.euclid.tools.EuclidHashCodeTools;
import us.ihmc.euclid.tuple2D.Vector2D32;
import us.ihmc.euclid.tuple3D.interfaces.Tuple3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DBasics;

/**
 * A 3D vector represents a physical quantity with a magnitude and a direction. For instance, it can
 * be used to represent a 3D velocity, force, or translation from one 3D point to another.
 * <p>
 * This version of 3D vector uses single precision fields to save the value of each component. It is
 * meant for garbage free usage and for situations where heap memory is limited. When memory is not
 * a constraint, the use of {@link Vector2D32} is preferable.
 * </p>
 *
 * @author Sylvain Bertrand
 */
public class Vector3D32 implements Vector3DBasics, GeometryObject<Vector3D32>
{
   /** The x-component. */
   private float x;
   /** The y-component. */
   private float y;
   /** The z-component. */
   private float z;

   /**
    * Creates a new vector and initializes it components to zero.
    */
   public Vector3D32()
   {
      setToZero();
   }

   /**
    * Creates a new vector and initializes it with the given components.
    *
    * @param x the x-component.
    * @param y the y-component.
    * @param z the z-component.
    */
   public Vector3D32(float x, float y, float z)
   {
      set(x, y, z);
   }

   /**
    * Creates a new vector and initializes its component {@code x}, {@code y}, {@code z} in order
    * from the given array.
    *
    * @param vectorArray the array containing this vector's components. Not modified.
    */
   public Vector3D32(float[] vectorArray)
   {
      set(vectorArray);
   }

   /**
    * Creates a new vector and initializes it to {@code other}.
    *
    * @param other the tuple to copy the components from. Not modified.
    */
   public Vector3D32(Tuple3DReadOnly other)
   {
      set(other);
   }

   /**
    * Sets this vector to {@code other}.
    *
    * @param other the other vector to copy the values from. Not modified.
    */
   @Override
   public void set(Vector3D32 other)
   {
      Vector3DBasics.super.set(other);
   }

   /**
    * Sets the x-component of this vector.
    *
    * @param x the x-component.
    */
   @Override
   public void setX(double x)
   {
      this.x = (float) x;
   }

   /**
    * Sets the y-component of this vector.
    *
    * @param y the y-component.
    */
   @Override
   public void setY(double y)
   {
      this.y = (float) y;
   }

   /**
    * Sets the z-component of this vector.
    *
    * @param z the z-component.
    */
   @Override
   public void setZ(double z)
   {
      this.z = (float) z;
   }

   /**
    * Sets the x-component of this vector.
    *
    * @param x the x-component.
    */
   public void setX(float x)
   {
      this.x = x;
   }

   /**
    * Sets the y-component of this vector.
    *
    * @param y the y-component.
    */
   public void setY(float y)
   {
      this.y = y;
   }

   /**
    * Sets the z-component of this vector.
    *
    * @param z the z-component.
    */
   public void setZ(float z)
   {
      this.z = z;
   }

   /**
    * Returns the value of the x-component of this vector.
    *
    * @return the x-component's value.
    */
   @Override
   public double getX()
   {
      return x;
   }

   /**
    * Returns the value of the y-component of this vector.
    *
    * @return the y-component's value.
    */
   @Override
   public double getY()
   {
      return y;
   }

   /**
    * Returns the value of the z-component of this vector.
    *
    * @return the z-component's value.
    */
   @Override
   public double getZ()
   {
      return z;
   }

   /**
    * Returns the value of the x-component of this vector.
    *
    * @return the x-component's value.
    */
   @Override
   public float getX32()
   {
      return x;
   }

   /**
    * Returns the value of the y-component of this vector.
    *
    * @return the y-component's value.
    */
   @Override
   public float getY32()
   {
      return y;
   }

   /**
    * Returns the value of the z-component of this vector.
    *
    * @return the z-component's value.
    */
   @Override
   public float getZ32()
   {
      return z;
   }

   /**
    * Tests if the given {@code object}'s class is the same as this, in which case the method
    * returns {@link #equals(Tuple3DReadOnly)}, it returns {@code false} otherwise.
    *
    * @param object the object to compare against this. Not modified.
    * @return {@code true} if {@code object} and this are exactly equal, {@code false} otherwise.
    */
   @Override
   public boolean equals(Object object)
   {
      try
      {
         return equals((Tuple3DReadOnly) object);
      }
      catch (ClassCastException e)
      {
         return false;
      }
   }

   /**
    * Tests on a per component basis if this vector is equal to the given {@code other} to an
    * {@code epsilon}.
    *
    * @param other the other vector to compare against this. Not modified.
    * @param epsilon the tolerance to use when comparing each component.
    * @return {@code true} if the two tuples are equal, {@code false} otherwise.
    */
   @Override
   public boolean epsilonEquals(Vector3D32 other, double epsilon)
   {
      return Vector3DBasics.super.epsilonEquals(other, epsilon);
   }

   /**
    * Tests if {@code this} and {@code other} represent the same vector 3D to an {@code epsilon}.
    * <p>
    * Two vectors are considered geometrically equal if the length of their difference is less than
    * or equal to {@code epsilon}.
    * </p>
    * <p>
    * Note that {@code this.geometricallyEquals(other, epsilon) == true} does not necessarily imply
    * {@code this.epsilonEquals(other, epsilon)} and vice versa.
    * </p>
    *
    * @param other the other vector 3D to compare against this. Not modified.
    * @param epsilon the maximum length of the difference vector can be for the two vectors to be
    *           considered equal.
    * @return {@code true} if the two vectors represent the same geometry, {@code false} otherwise.
    */
   @Override
   public boolean geometricallyEquals(Vector3D32 other, double epsilon)
   {
      return Vector3DBasics.super.geometricallyEquals(other, epsilon);
   }

   /**
    * Provides a {@code String} representation of this vector 3D as follows: (x, y, z).
    *
    * @return the {@code String} representing this vector 3D.
    */
   @Override
   public String toString()
   {
      return EuclidCoreIOTools.getTuple3DString(this);
   }

   /**
    * Calculates and returns a hash code value from the value of each component of this vector 3D.
    *
    * @return the hash code value for this vector 3D.
    */
   @Override
   public int hashCode()
   {
      long bits = 1L;
      bits = EuclidHashCodeTools.addToHashCode(bits, x);
      bits = EuclidHashCodeTools.addToHashCode(bits, y);
      bits = EuclidHashCodeTools.addToHashCode(bits, z);
      return EuclidHashCodeTools.toIntHashCode(bits);
   }
}
