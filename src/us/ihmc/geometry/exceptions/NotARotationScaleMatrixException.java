package us.ihmc.geometry.exceptions;

import us.ihmc.geometry.GeometryBasicsIOTools;
import us.ihmc.geometry.matrix.interfaces.Matrix3DReadOnly;

/**
 * {@code RuntimeException} dedicated to operations expecting a rotation-scale matrix.
 * 
 * @author Sylvain
 *
 */
public class NotARotationScaleMatrixException extends RuntimeException
{
   private static final long serialVersionUID = -969795129004644572L;

   /**
    * Constructs an {@code NotARotationScaleMatrixException} with no detail message.
    */
   public NotARotationScaleMatrixException()
   {
      super();
   }

   /**
    * Constructs an {@code NotARotationScaleMatrixException} with the specified detail message.
    *
    * @param message the detail message.
    */
   public NotARotationScaleMatrixException(String message)
   {
      super(message);
   }

   /**
    * Constructs an {@code NotARotationScaleMatrixException} with a default detail message
    * outputting the given matrix coefficients.
    *
    * @param matrix the matrix to be displayed in the detail message. Not modified.
    */
   public NotARotationScaleMatrixException(Matrix3DReadOnly<?> matrix)
   {
      super("The matrix is not a rotation-scale matrix: \n" + matrix);
   }

   /**
    * Constructs an {@code NotARotationScaleMatrixException} with a default detail message
    * outputting the given matrix coefficients.
    *
    * @param m00 the 1st row 1st column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m01 the 1st row 2nd column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m02 the 1st row 3rd column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m10 the 2nd row 1st column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m11 the 2nd row 2nd column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m12 the 2nd row 3rd column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m20 the 3rd row 1st column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m21 the 3rd row 2nd column coefficient of the matrix to be displayed in the detail
    *           message.
    * @param m22 the 3rd row 3rd column coefficient of the matrix to be displayed in the detail
    *           message.
    */
   public NotARotationScaleMatrixException(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
   {
      super("The matrix is not a rotation-scale matrix: \n" + GeometryBasicsIOTools.getMatrixString(m00, m01, m02, m10, m11, m12, m20, m21, m22));
   }
}
