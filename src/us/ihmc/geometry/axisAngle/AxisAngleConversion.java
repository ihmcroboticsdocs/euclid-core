package us.ihmc.geometry.axisAngle;

import us.ihmc.geometry.axisAngle.interfaces.AxisAngleBasics;
import us.ihmc.geometry.matrix.Matrix3DFeatures;
import us.ihmc.geometry.matrix.RotationMatrixConversion;
import us.ihmc.geometry.matrix.interfaces.RotationMatrixReadOnly;
import us.ihmc.geometry.matrix.interfaces.RotationScaleMatrixReadOnly;
import us.ihmc.geometry.tuple3D.RotationVectorConversion;
import us.ihmc.geometry.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.geometry.tuple4D.QuaternionConversion;
import us.ihmc.geometry.tuple4D.Tuple4DTools;
import us.ihmc.geometry.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.geometry.yawPitchRoll.YawPitchRollConversion;

/**
 * This class gathers all the methods necessary to converts any type of rotation into an axis-angle.
 * <p>
 * To convert an orientation into other data structure types see:
 * <ul>
 *    <li> for quaternion: {@link QuaternionConversion},
 *    <li> for rotation matrix: {@link RotationMatrixConversion},
 *    <li> for rotation vector: {@link RotationVectorConversion},
 *    <li> for yaw-pitch-roll: {@link YawPitchRollConversion}.
 * </ul>
 * </p>
 * 
 * @author Sylvain Bertrand
 *
 */
public class AxisAngleConversion
{
   private static final double EPS = 1.0e-12;

   /**
    * Converts the rotation part of the given rotation-scale matrix into an axis-angle.
    * <p>
    * After calling this method, the orientation represented by the axis-angle is the same 
    * as the given rotation part of the rotation-scale matrix.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *   <li> if the rotation matrix contains at least one {@link Double#NaN}, the axis-angle is 
    *    set to {@link Double#NaN}.
    * </ul>
    * </p>
    * 
    * @param rotationScaleMatrix a 3-by-3 matrix representing an orientation and a scale.
    *    Only the orientation part is used during the conversion. Not modified.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertMatrixToAxisAngle(RotationScaleMatrixReadOnly<?> rotationScaleMatrix, AxisAngleBasics<?> axisAngleToPack)
   {
      convertMatrixToAxisAngle(rotationScaleMatrix.getRotationMatrix(), axisAngleToPack);
   }

   /**
    * Converts the given rotation matrix into an axis-angle.
    * <p>
    * After calling this method, the orientation represented by the axis-angle is the same
    * as the given rotation matrix.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *   <li> if the rotation matrix contains at least one {@link Double#NaN}, the axis-angle is 
    *    set to {@link Double#NaN}.
    * </ul>
    * </p>
    * 
    * @param rotationMatrix a 3-by-3 matrix representing an orientation. Not modified.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertMatrixToAxisAngle(RotationMatrixReadOnly<?> rotationMatrix, AxisAngleBasics<?> axisAngleToPack)
   {
      double m00 = rotationMatrix.getM00();
      double m01 = rotationMatrix.getM01();
      double m02 = rotationMatrix.getM02();
      double m10 = rotationMatrix.getM10();
      double m11 = rotationMatrix.getM11();
      double m12 = rotationMatrix.getM12();
      double m20 = rotationMatrix.getM20();
      double m21 = rotationMatrix.getM21();
      double m22 = rotationMatrix.getM22();
      convertMatrixToAxisAngleImpl(m00, m01, m02, m10, m11, m12, m20, m21, m22, axisAngleToPack);
   }

   /**
    * Converts the given rotation matrix into an axis-angle.
    * <p>
    * <b> This method is for internal use. Use {@link #convertMatrixToAxisAngle(RotationMatrixReadOnly, AxisAngleBasics)}
    * or {@link #convertMatrixToAxisAngle(RotationScaleMatrixReadOnly, AxisAngleBasics)} instead. </b>
    * </p>
    * <p>
    * After calling this method, the orientation represented by the axis-angle is the same
    * as the given rotation matrix.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *   <li> if the rotation matrix contains at least one {@link Double#NaN}, the axis-angle is 
    *    set to {@link Double#NaN}.
    * </ul>
    * </p>
    * 
    * @param m00 the new 1st row 1st column coefficient for the matrix to use for the conversion.
    * @param m01 the new 1st row 2nd column coefficient for the matrix to use for the conversion.
    * @param m02 the new 1st row 3rd column coefficient for the matrix to use for the conversion.
    * @param m10 the new 2nd row 1st column coefficient for the matrix to use for the conversion.
    * @param m11 the new 2nd row 2nd column coefficient for the matrix to use for the conversion.
    * @param m12 the new 2nd row 3rd column coefficient for the matrix to use for the conversion.
    * @param m20 the new 3rd row 1st column coefficient for the matrix to use for the conversion.
    * @param m21 the new 3rd row 2nd column coefficient for the matrix to use for the conversion.
    * @param m22 the new 3rd row 3rd column coefficient for the matrix to use for the conversion.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   static void convertMatrixToAxisAngleImpl(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22,
         AxisAngleBasics<?> axisAngleToPack)
   {
      if (Matrix3DFeatures.containsNaN(m00, m01, m02, m10, m11, m12, m20, m21, m22))
      {
         axisAngleToPack.setToNaN();
         return;
      }

      double angle, x, y, z; // variables for result

      x = m21 - m12;
      y = m02 - m20;
      z = m10 - m01;

      double s = Math.sqrt(x * x + y * y + z * z);

      if (s > EPS)
      {
         double sin = 0.5 * s;
         double cos = 0.5 * (m00 + m11 + m22 - 1.0);
         angle = Math.atan2(sin, cos);
         x /= s;
         y /= s;
         z /= s;
      }
      else if (Matrix3DFeatures.isZeroRotation(m00, m01, m02, m10, m11, m12, m20, m21, m22))
      {
         axisAngleToPack.setToZero();
         return;
      }
      else
      {
         // otherwise this singularity is angle = 180
         angle = Math.PI;
         double xx = 0.50 * (m00 + 1.0);
         double yy = 0.50 * (m11 + 1.0);
         double zz = 0.50 * (m22 + 1.0);
         double xy = 0.25 * (m01 + m10);
         double xz = 0.25 * (m02 + m20);
         double yz = 0.25 * (m12 + m21);

         if (xx > yy && xx > zz)
         { // m00 is the largest diagonal term
            x = Math.sqrt(xx);
            y = xy / x;
            z = xz / x;
         }
         else if (yy > zz)
         { // m11 is the largest diagonal term
            y = Math.sqrt(yy);
            x = xy / y;
            z = yz / y;
         }
         else
         { // m22 is the largest diagonal term so base result on this
            z = Math.sqrt(zz);
            x = xz / z;
            y = yz / z;
         }
      }
      axisAngleToPack.set(x, y, z, angle);
   }

   /**
    * Converts the given quaternion into an axis-angle.
    * <p>
    * After calling this method, the quaternion and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *   <li> if the quaternion contains at least one {@link Double#NaN}, the axis-angle is 
    *    set to {@link Double#NaN}.
    *   <li> if the norm of the vector part of the quaternion is less than {@value #EPS}, the axis-angle
    *    is set to zero via {@link AxisAngleBasics#setToZero()}.
    * </ul>
    * </p>
    * 
    * @param quaternion the unit quaternion to use for the conversion. Not modified.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertQuaternionToAxisAngle(QuaternionReadOnly quaternion, AxisAngleBasics<?> axisAngleToPack)
   {
      convertQuaternionToAxisAngle(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getS(), axisAngleToPack);
   }

   /**
    * Converts the given quaternion into an axis-angle.
    * <p>
    * After calling this method, the quaternion and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *   <li> if the quaternion contains at least one {@link Double#NaN}, the axis-angle is 
    *    set to {@link Double#NaN}.
    *   <li> if the norm of the vector part of the quaternion is less than {@value #EPS}, the axis-angle
    *    is set to zero via {@link AxisAngleBasics#setToZero()}.
    * </ul>
    * </p>
    * 
    * @param qx the vector part x-component of the unit quaternion to use for the conversion.
    * @param qy the vector part y-component of the unit quaternion to use for the conversion.
    * @param qz the vector part z-component of the unit quaternion to use for the conversion.
    * @param qs the scalar part of the unit quaternion to use for the conversion.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertQuaternionToAxisAngle(double qx, double qy, double qz, double qs, AxisAngleBasics<?> axisAngleToPack)
   {
      if (Tuple4DTools.containsNaN(qx, qy, qz, qs))
      {
         axisAngleToPack.setToNaN();
         return;
      }

      double uNorm = Math.sqrt(qx * qx + qy * qy + qz * qz);

      if (uNorm > EPS)
      {
         axisAngleToPack.setAngle(2.0 * Math.atan2(uNorm, qs));
         uNorm = 1.0 / uNorm;
         axisAngleToPack.setX(qx * uNorm);
         axisAngleToPack.setY(qy * uNorm);
         axisAngleToPack.setZ(qz * uNorm);
      }
      else
      {
         axisAngleToPack.setToZero();
      }
   }

   /**
    * Converts the rotation vector into an axis-angle.
    * <p>
    * After calling this method, the rotation vector and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *    <li> if the rotation vector contains at least a {@link Double#NaN}, the axis-angle is set
    *     to {@link Double#NaN}.
    * </ul>
    * </p>
    * <p>
    * WARNING: a rotation vector is different from a yaw-pitch-roll or Euler angles representation.
    * A rotation vector is equivalent to the axis of an axis-angle that is multiplied by the angle
    * of the same axis-angle.
    * </p>
    * 
    * @param rotationVector the rotation vector to use in the conversion. Not modified.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertRotationVectorToAxisAngle(Vector3DReadOnly rotationVector, AxisAngleBasics<?> axisAngleToPack)
   {
      convertRotationVectorToAxisAngle(rotationVector.getX(), rotationVector.getY(), rotationVector.getZ(), axisAngleToPack);
   }

   /**
    * Converts the rotation vector into an axis-angle.
    * <p>
    * After calling this method, the rotation vector and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *    <li> if the rotation vector contains at least a {@link Double#NaN}, the axis-angle is set
    *     to {@link Double#NaN}.
    * </ul>
    * </p>
    * <p>
    * WARNING: a rotation vector is different from a yaw-pitch-roll or Euler angles representation.
    * A rotation vector is equivalent to the axis of an axis-angle that is multiplied by the angle
    * of the same axis-angle.
    * </p>
    * 
    * @param rx the x-component of the rotation vector to use in the conversion.
    * @param ry the y-component of the rotation vector to use in the conversion.
    * @param rz the z-component of the rotation vector to use in the conversion.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertRotationVectorToAxisAngle(double rx, double ry, double rz, AxisAngleBasics<?> axisAngleToPack)
   {
      if (Double.isNaN(rx) || Double.isNaN(ry) || Double.isNaN(rz))
      {
         axisAngleToPack.setToNaN();
         return;
      }

      double norm = Math.sqrt(rx * rx + ry * ry + rz * rz);

      if (norm > EPS)
      {
         axisAngleToPack.setAngle(norm);
         norm = 1.0 / norm;
         axisAngleToPack.setX(rx * norm);
         axisAngleToPack.setY(ry * norm);
         axisAngleToPack.setZ(rz * norm);
      }
      else
      {
         axisAngleToPack.setToZero();
      }
   }

   /**
    * Converts the given yaw-pitch-roll angles into an axis-angles.
    * <p>
    * After calling this method, the yaw-pitch-roll and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *    <li> if either of the yaw, pitch, or roll angle is {@link Double#NaN}, the axis-angle is set
    *     to {@link Double#NaN}.
    * </ul>
    * </p>
    * <p>
    * Note: the yaw-pitch-roll representation, also called Euler angles, corresponds
    * to the representation of an orientation by decomposing it by three successive rotations around
    * the three axes: Z (yaw), Y (pitch), and X (roll).
    * The equivalent rotation matrix of such representation is:
    * <br> R = R<sub>Z</sub>(yaw) * R<sub>Y</sub>(pitch) * R<sub>X</sub>(roll) </br>
    * </p>
    * 
    * @param yawPitchRoll the yaw-pitch-roll angles to use in the conversion. Not modified.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertYawPitchRollToAxisAngle(double[] yawPitchRoll, AxisAngleBasics<?> axisAngleToPack)
   {
      convertYawPitchRollToAxisAngle(yawPitchRoll[0], yawPitchRoll[1], yawPitchRoll[2], axisAngleToPack);
   }
   
   /**
    * Converts the given yaw-pitch-roll angles into an axis-angles.
    * <p>
    * After calling this method, the yaw-pitch-roll and the axis-angle represent the same orientation.
    * </p>
    * <p>
    * Edge case:
    * <ul>
    *    <li> if either of the yaw, pitch, or roll angle is {@link Double#NaN}, the axis-angle is set
    *     to {@link Double#NaN}.
    * </ul>
    * </p>
    * <p>
    * Note: the yaw-pitch-roll representation, also called Euler angles, corresponds
    * to the representation of an orientation by decomposing it by three successive rotations around
    * the three axes: Z (yaw), Y (pitch), and X (roll).
    * The equivalent rotation matrix of such representation is:
    * <br> R = R<sub>Z</sub>(yaw) * R<sub>Y</sub>(pitch) * R<sub>X</sub>(roll) </br>
    * </p>
    * 
    * @param yaw the yaw angle to use in the conversion.
    * @param pitch the pitch angle to use in the conversion.
    * @param roll the roll angle to use in the conversion.
    * @param axisAngleToPack the axis-angle in which the result is stored. Modified.
    */
   public static void convertYawPitchRollToAxisAngle(double yaw, double pitch, double roll, AxisAngleBasics<?> axisAngleToPack)
   {
      double halfYaw = yaw / 2.0;
      double cYaw = Math.cos(halfYaw);
      double sYaw = Math.sin(halfYaw);

      double halfPitch = pitch / 2.0;
      double cPitch = Math.cos(halfPitch);
      double sPitch = Math.sin(halfPitch);

      double halfRoll = roll / 2.0;
      double cRoll = Math.cos(halfRoll);
      double sRoll = Math.sin(halfRoll);

      double qs = cYaw * cPitch * cRoll + sYaw * sPitch * sRoll;
      double qx = cYaw * cPitch * sRoll - sYaw * sPitch * cRoll;
      double qy = sYaw * cPitch * sRoll + cYaw * sPitch * cRoll;
      double qz = sYaw * cPitch * cRoll - cYaw * sPitch * sRoll;

      convertQuaternionToAxisAngle(qx, qy, qz, qs, axisAngleToPack);
   }
}
