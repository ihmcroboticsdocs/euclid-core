package us.ihmc.geometry.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.EjmlUnitTests;
import org.junit.Test;

import us.ihmc.geometry.axisAngle.AxisAngle;
import us.ihmc.geometry.exceptions.NotARotationScaleMatrixException;
import us.ihmc.geometry.matrix.interfaces.Matrix3DReadOnly;
import us.ihmc.geometry.matrix.interfaces.RotationMatrixReadOnly;
import us.ihmc.geometry.matrix.interfaces.RotationScaleMatrixReadOnly;
import us.ihmc.geometry.testingTools.GeometryBasicsRandomTools;
import us.ihmc.geometry.testingTools.GeometryBasicsTestTools;
import us.ihmc.geometry.tuple2D.Vector2D;
import us.ihmc.geometry.tuple2D.interfaces.Tuple2DBasics;
import us.ihmc.geometry.tuple2D.interfaces.Tuple2DReadOnly;
import us.ihmc.geometry.tuple3D.RotationVectorConversion;
import us.ihmc.geometry.tuple3D.Vector3D;
import us.ihmc.geometry.tuple3D.interfaces.Tuple3DBasics;
import us.ihmc.geometry.tuple3D.interfaces.Tuple3DReadOnly;
import us.ihmc.geometry.tuple4D.Quaternion;
import us.ihmc.geometry.tuple4D.QuaternionTools;
import us.ihmc.geometry.tuple4D.Vector4D;
import us.ihmc.geometry.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.geometry.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.geometry.tuple4D.interfaces.Vector4DBasics;
import us.ihmc.geometry.tuple4D.interfaces.Vector4DReadOnly;
import us.ihmc.geometry.yawPitchRoll.YawPitchRollConversion;

public class RotationScaleMatrixTest extends Matrix3DBasicsTest<RotationScaleMatrix>
{
   public static final int NUMBER_OF_ITERATIONS = 100;
   public static final double EPS = 1.0e-10;

   @Test
   public void testDefinition() throws Exception
   {
      // Tests that the rotation-scale matrix M can be expressed as M = R * S, with R a rotation matrix, S a diagonal matrix.
      Random random = new Random(324234L);

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random, 1.0, 10.0);
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix(rotationMatrix, scale);

         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         rotationMatrix.get(rotationDenseMatrix);
         DenseMatrix64F scaleDenseMatrix = new DenseMatrix64F(3, 3);
         for (int index = 0; index < 3; index++)
            scaleDenseMatrix.set(index, index, scale.get(index));
         DenseMatrix64F rotationScaleDenseMatrix = new DenseMatrix64F(3, 3);
         CommonOps.mult(rotationDenseMatrix, scaleDenseMatrix, rotationScaleDenseMatrix);
         Matrix3D expectedRotationScaleMatrix = new Matrix3D();
         expectedRotationScaleMatrix.set(rotationScaleDenseMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(expectedRotationScaleMatrix, rotationScaleMatrix, EPS);
      }
   }

   @Test
   public void testConstructors() throws Exception
   {
      Random random = new Random(36456L);

      { // Test RotationScaleMatrix()
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               if (row == column)
                  assertTrue(rotationScaleMatrix.getElement(row, column) == 1.0);
               else
                  assertTrue(rotationScaleMatrix.getElement(row, column) == 0.0);
            }
         }

         assertTrue(rotationScaleMatrix.getScaleX() == 1.0);
         assertTrue(rotationScaleMatrix.getScaleY() == 1.0);
         assertTrue(rotationScaleMatrix.getScaleZ() == 1.0);
         assertEquals(new RotationMatrix(), rotationScaleMatrix.getRotationMatrix());
      }

      { // Test RotationScaleMatrix(RotationScaleMatrix rotationScaleMatrix)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(Matrix3DReadOnly rotationScaleMatrix)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix((Matrix3DReadOnly<?>) matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(DenseMatrix64F rotationScaleMatrix)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, matrixExpected.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixActual = new RotationScaleMatrix(denseMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(double[] rotationScaleMatrixArray)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         double[] matrixArray = new double[9];

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               matrixArray[3 * row + column] = matrixExpected.getElement(row, column);
            }
         }

         RotationScaleMatrix matrixActual = new RotationScaleMatrix(matrixArray);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(AxisAngleReadOnly axisAngle, double scale)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(axisAngle, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(AxisAngleReadOnly axisAngle, double scalex, double scaley, double scalez)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(axisAngle, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(AxisAngleReadOnly axisAngle, TupleReadOnly scales)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(axisAngle, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(DenseMatrix64F matrix, double scale)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationDenseMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(DenseMatrix64F matrix, double scalex, double scaley, double scalez)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationDenseMatrix, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(DenseMatrix64F matrix, TupleReadOnly scales)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationDenseMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(QuaternionReadOnly quaternion, double scale)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(quaternion, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(QuaternionReadOnly quaternion, double scalex, double scaley, double scalez)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(quaternion, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(QuaternionReadOnly quaternion, TupleReadOnly scales)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(quaternion, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(RotationMatrixReadOnly rotationMatrix, double scale)
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(RotationMatrixReadOnly rotationMatrix, double scalex, double scaley, double scalez)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationMatrix, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test RotationScaleMatrix(RotationMatrixReadOnly rotationMatrix, TupleReadOnly scales)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix(rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }
   }

   @Test
   public void testCheckIfMatrixProper() throws Exception
   {
      RotationScaleMatrix matrix = new RotationScaleMatrix();

      matrix.checkIfRotationMatrixProper(); // Should not throw any exception
      matrix.checkIfRotationScaleMatrixProper(); // Should not throw any exception
      matrix.checkIfScalesProper(); // Should not throw any exception

      RotationMatrix rotationMatrix = (RotationMatrix) matrix.getRotationMatrix();
      rotationMatrix.setUnsafe(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      matrix.checkIfScalesProper(); // Should not throw any exception

      try
      {
         matrix.checkIfRotationMatrixProper();
         fail("Should have thrown a NotARotationScaleMatrixException");
      }
      catch (NotARotationScaleMatrixException e)
      {
         // good
      }
      catch (Exception e)
      {
         fail("Should have thrown a NotARotationScaleMatrixException");
      }

      try
      {
         matrix.checkIfRotationScaleMatrixProper();
         fail("Should have thrown a NotARotationScaleMatrixException");
      }
      catch (NotARotationScaleMatrixException e)
      {
         // good
      }
      catch (Exception e)
      {
         fail("Should have thrown a NotARotationScaleMatrixException");
      }

      rotationMatrix.setIdentity();
      Vector3D scale = (Vector3D) matrix.getScale();
      scale.setX(-1.0);

      matrix.checkIfRotationMatrixProper(); // Should not throw any exception

      try
      {
         matrix.checkIfScalesProper();
         fail("Should have thrown a NotARotationScaleMatrixException");
      }
      catch (NotARotationScaleMatrixException e)
      {
         // good
      }
      catch (Exception e)
      {
         fail("Should have thrown a NotARotationScaleMatrixException");
      }

      try
      {
         matrix.checkIfRotationScaleMatrixProper();
         fail("Should have thrown a NotARotationScaleMatrixException");
      }
      catch (NotARotationScaleMatrixException e)
      {
         // good
      }
      catch (Exception e)
      {
         fail("Should have thrown a NotARotationScaleMatrixException");
      }

   }

   @Test
   public void testNormalizeRotationMatrix() throws Exception
   {
      Random random = new Random(39456L);
      RotationScaleMatrix matrixExpected = new RotationScaleMatrix();
      RotationScaleMatrix matrixActual = new RotationScaleMatrix();

      { // Check that identity does not get modified
         matrixActual.setIdentity();
         matrixExpected.setIdentity();

         matrixActual.normalizeRotationMatrix();
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      // Test that normalizing a proper rotation matrix does not change it.
      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         matrixExpected.set(GeometryBasicsRandomTools.generateRandomRotationMatrix(random));
         matrixActual.set(matrixExpected);

         matrixActual.normalizeRotationMatrix();
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      Vector3D vector1 = new Vector3D();
      Vector3D vector2 = new Vector3D();

      // Test that it actually makes a random matrix ortho-normal
      double corruptionFactor = 0.1;
      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationMatrix randomRotation = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         double m00 = randomRotation.getM00() + corruptionFactor * random.nextDouble();
         double m01 = randomRotation.getM01() + corruptionFactor * random.nextDouble();
         double m02 = randomRotation.getM02() + corruptionFactor * random.nextDouble();
         double m10 = randomRotation.getM10() + corruptionFactor * random.nextDouble();
         double m11 = randomRotation.getM11() + corruptionFactor * random.nextDouble();
         double m12 = randomRotation.getM12() + corruptionFactor * random.nextDouble();
         double m20 = randomRotation.getM20() + corruptionFactor * random.nextDouble();
         double m21 = randomRotation.getM21() + corruptionFactor * random.nextDouble();
         double m22 = randomRotation.getM22() + corruptionFactor * random.nextDouble();
         ((RotationMatrix) matrixActual.getRotationMatrix()).setUnsafe(m00, m01, m02, m10, m11, m12, m20, m21, m22);
         matrixActual.normalizeRotationMatrix();

         // Test that each row & column vectors are unit-length
         for (int j = 0; j < 3; j++)
         {
            matrixActual.getRow(j, vector1);
            assertEquals(1.0, vector1.length(), EPS);

            matrixActual.getColumn(j, vector1);
            assertEquals(1.0, vector1.length(), EPS);
         }

         // Test that each pair of rows and each pair of columns are orthogonal
         for (int j = 0; j < 3; j++)
         {
            matrixActual.getRow(j, vector1);
            matrixActual.getRow((j + 1) % 3, vector2);
            assertEquals(0.0, vector1.dot(vector2), EPS);

            matrixActual.getColumn(j, vector1);
            matrixActual.getColumn((j + 1) % 3, vector2);
            assertEquals(0.0, vector1.dot(vector2), EPS);
         }
      }
   }

   @Test
   public void testResetScale() throws Exception
   {
      RotationScaleMatrix matrix = new RotationScaleMatrix();
      Vector3D scale = new Vector3D(5.20, 1261.0, 1.1152);
      matrix.setScale(scale);
      GeometryBasicsTestTools.assertTuple3DEquals(scale, matrix.getScale(), EPS);

      matrix.resetScale();
      Vector3D expectedScale = new Vector3D(1.0, 1.0, 1.0);
      GeometryBasicsTestTools.assertTuple3DEquals(expectedScale, matrix.getScale(), EPS);
   }

   @Test
   public void testSetToZero() throws Exception
   {
      Random random = new Random(546L);
      RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      matrix.setToZero();
      assertTrue(matrix.getScaleX() == 1.0);
      assertTrue(matrix.getScaleY() == 1.0);
      assertTrue(matrix.getScaleZ() == 1.0);
      assertEquals(new RotationMatrix(), matrix.getRotationMatrix());
   }

   @Override
   @Test
   public void testSetToNaN() throws Exception
   {
      Random random = new Random(546L);
      RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      matrix.setToNaN();
      GeometryBasicsTestTools.assertMatrix3DContainsOnlyNaN(matrix);
      GeometryBasicsTestTools.assertMatrix3DContainsOnlyNaN(matrix.getRotationMatrix());
      GeometryBasicsTestTools.assertTupleContainsOnlyNaN(matrix.getScale());
   }

   @Override
   @Test
   public void testSetIdentity() throws Exception
   {
      Random random = new Random(546L);
      RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      matrix.setIdentity();
      assertTrue(matrix.getScaleX() == 1.0);
      assertTrue(matrix.getScaleY() == 1.0);
      assertTrue(matrix.getScaleZ() == 1.0);
      assertEquals(new RotationMatrix(), matrix.getRotationMatrix());
   }

   @Test
   public void testSetRotationToZero() throws Exception
   {
      Random random = new Random(546L);
      RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      Vector3D scale = new Vector3D(matrix.getScale());

      matrix.setRotationToZero();
      GeometryBasicsTestTools.assertTuple3DEquals(scale, matrix.getScale(), EPS);
      assertEquals(new RotationMatrix(), matrix.getRotationMatrix());
   }

   @Override
   @Test
   public void testContainsNaN() throws Exception
   {
      super.testContainsNaN();

      /*
       * Also test for NaN in the scale part.
       */

      RotationScaleMatrix matrix = new RotationScaleMatrix();
      matrix.setScale(Double.NaN, 1.0, 1.0);
      assertTrue(matrix.containsNaN());
      matrix.setScale(1.0, Double.NaN, 1.0);
      assertTrue(matrix.containsNaN());
      matrix.setScale(1.0, 1.0, Double.NaN);
      assertTrue(matrix.containsNaN());
   }

   @Test
   public void testSet() throws Exception
   {
      Random random = new Random(5464L);

      { // Test set(RotationScaleMatrix other)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);

         matrixActual.setToNaN();
         matrixActual.set((RotationScaleMatrixReadOnly<?>) matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);

         matrixActual.setToNaN();
         matrixActual.set((RotationScaleMatrixReadOnly<?>) matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);

         matrixActual.setToNaN();
         matrixActual.set((Matrix3DReadOnly<?>) matrixExpected);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(RotationMatrixReadOnly rotationMatrix)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         matrixExpected.resetScale();
         RotationScaleMatrix matrixActual = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         matrixActual.set(matrixExpected.getRotationMatrix());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(double[] rotationScaleMatrixArray)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         double[] matrixArray = new double[9];

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               matrixArray[3 * row + column] = matrixExpected.getElement(row, column);
            }
         }

         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(matrixArray);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(DenseMatrix64F rotationScaleMatrix)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, matrixExpected.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(denseMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(DenseMatrix64F rotationScaleMatrix, int startRow, int startColumn)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         int startRow = random.nextInt(10);
         int startColumn = random.nextInt(10);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3 + startRow, 3 + startColumn);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row + startRow, column + startColumn, matrixExpected.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(startRow, startColumn, denseMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
         RotationScaleMatrix matrixExpected = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         double m00 = matrixExpected.getM00();
         double m01 = matrixExpected.getM01();
         double m02 = matrixExpected.getM02();
         double m10 = matrixExpected.getM10();
         double m11 = matrixExpected.getM11();
         double m12 = matrixExpected.getM12();
         double m20 = matrixExpected.getM20();
         double m21 = matrixExpected.getM21();
         double m22 = matrixExpected.getM22();

         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected.getRotationMatrix(), matrixActual.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(matrixExpected.getScale(), matrixActual.getScale(), EPS);

         // Test with negative determinant
         m00 = matrixExpected.getM00();
         m01 = matrixExpected.getM01();
         m02 = matrixExpected.getM02();
         m10 = matrixExpected.getM10();
         m11 = matrixExpected.getM11();
         m12 = matrixExpected.getM12();
         m20 = -matrixExpected.getM20();
         m21 = -matrixExpected.getM21();
         m22 = -matrixExpected.getM22();

         try
         {
            matrixActual.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
            fail("Should have thrown an exception");
         }
         catch (NotARotationScaleMatrixException e)
         {
            // good
         }

         // Test a scale == 0.0
         m00 = matrixExpected.getM00();
         m01 = matrixExpected.getM01();
         m02 = matrixExpected.getM02();
         m10 = matrixExpected.getM10();
         m11 = matrixExpected.getM11();
         m12 = matrixExpected.getM12();
         m20 = 0.0;
         m21 = 0.0;
         m22 = 0.0;

         try
         {
            matrixActual.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
            fail("Should have thrown an exception");
         }
         catch (NotARotationScaleMatrixException e)
         {
            // good
         }

         // Test with random values
         m00 = random.nextDouble();
         m01 = random.nextDouble();
         m02 = random.nextDouble();
         m10 = random.nextDouble();
         m11 = random.nextDouble();
         m12 = random.nextDouble();
         m20 = random.nextDouble();
         m21 = random.nextDouble();
         m22 = random.nextDouble();

         try
         {
            matrixActual.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
            fail("Should have thrown an exception");
         }
         catch (NotARotationScaleMatrixException e)
         {
            // good
         }
      }

      { // Test set(AxisAngleReadOnly axisAngle, double scale)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(axisAngle, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(AxisAngleReadOnly axisAngle, double scalex, double scaley, double scalez)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(axisAngle, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(AxisAngleReadOnly axisAngle, TupleReadOnly scales)
         AxisAngle axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(axisAngle);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(axisAngle, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(DenseMatrix64F matrix, double scale)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationDenseMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(DenseMatrix64F matrix, double scalex, double scaley, double scalez)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationDenseMatrix, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(DenseMatrix64F matrix, TupleReadOnly scales)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F rotationDenseMatrix = new DenseMatrix64F(3, 3);
         DenseMatrix64F expectedDenseMatrix = new DenseMatrix64F(3, 3);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         rotationMatrix.get(rotationDenseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               expectedDenseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(expectedDenseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationDenseMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(QuaternionReadOnly quaternion, double scale)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(quaternion, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(QuaternionReadOnly quaternion, double scalex, double scaley, double scalez)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(quaternion, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(QuaternionReadOnly quaternion, TupleReadOnly scales)
         Quaternion quaternion = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = new RotationMatrix(quaternion);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(quaternion, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(Matrix3DReadOnly rotationMatrix, double scale)
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set((Matrix3DReadOnly<?>) rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(Matrix3DReadOnly rotationMatrix, double scalex, double scaley, double scalez)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set((Matrix3DReadOnly<?>) rotationMatrix, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(Matrix3DReadOnly rotationMatrix, TupleReadOnly scales)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set((Matrix3DReadOnly<?>) rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(RotationMatrixReadOnly rotationMatrix, double scale)
         double scale = GeometryBasicsRandomTools.generateRandomDouble(random, 0.1, 2.0);
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(RotationMatrixReadOnly rotationMatrix, double scalex, double scaley, double scalez)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationMatrix, scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }

      { // Test set(RotationMatrixReadOnly rotationMatrix, TupleReadOnly scales)
         Vector3D scale = GeometryBasicsRandomTools.generateRandomRotationVector(random, 2.0);
         scale.absolute();
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrix.set(row, column, scale.get(column) * rotationMatrix.getElement(row, column));
            }
         }

         RotationScaleMatrix matrixExpected = new RotationScaleMatrix(denseMatrix);
         RotationScaleMatrix matrixActual = new RotationScaleMatrix();
         matrixActual.set(rotationMatrix, scale);
         GeometryBasicsTestTools.assertMatrix3DEquals(matrixExpected, matrixActual, EPS);
      }
   }

   @Test
   public void testSetRotation() throws Exception
   {
      Random random = new Random(4356L);

      { // Test setRotation(DenseMatrix64F rotationMatrix)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);
         rotationMatrix.get(denseMatrix);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(denseMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(double[] rotationMatrixArray)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         double[] matrixArray = new double[9];
         rotationMatrix.get(matrixArray);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(matrixArray);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(AxisAngleReadOnly axisAngle)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         AxisAngle axisAngle = new AxisAngle();
         axisAngle.set(rotationMatrix);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(axisAngle);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(QuaternionReadOnly quaternion)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         Quaternion quaternion = new Quaternion();
         quaternion.set(rotationMatrix);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(quaternion);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(Matrix3DReadOnly rotationMatrix)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation((Matrix3DReadOnly<?>) rotationMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(RotationMatrixReadOnly rotationMatrix)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(rotationMatrix);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }

      { // Test setRotation(VectorReadOnly rotationVector)
         RotationMatrix rotationMatrix = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         Vector3D rotationVector = new Vector3D();
         rotationMatrix.get(rotationVector);
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
         rotationScaleMatrix.setScale(scale);
         rotationScaleMatrix.setRotation(rotationVector);
         GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, rotationScaleMatrix.getScale(), EPS);
      }
   }

   @Test
   public void testSetScale() throws Exception
   {
      Random random = new Random(5675L);

      { // Test setScale(double scale)
         RotationScaleMatrix matrix = new RotationScaleMatrix();
         double scale = random.nextDouble();
         matrix.setScale(scale);
         assertEquals(scale, matrix.getScaleX(), EPS);
         assertEquals(scale, matrix.getScaleY(), EPS);
         assertEquals(scale, matrix.getScaleZ(), EPS);
      }

      { // Test setScale(double x, double y, double z)
         RotationScaleMatrix matrix = new RotationScaleMatrix();
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         matrix.setScale(scale.getX(), scale.getY(), scale.getZ());
         GeometryBasicsTestTools.assertTuple3DEquals(scale, matrix.getScale(), EPS);

         try
         {
            matrix.setScale(0.0, 1.0, 1.0);
            fail("Should have thrown an exception");
         }
         catch (RuntimeException e)
         {
            // Good
         }

         try
         {
            matrix.setScale(1.0, 0.0, 1.0);
            fail("Should have thrown an exception");
         }
         catch (RuntimeException e)
         {
            // Good
         }

         try
         {
            matrix.setScale(1.0, 1.0, 0.0);
            fail("Should have thrown an exception");
         }
         catch (RuntimeException e)
         {
            // Good
         }
      }

      { // Test setScale(double x, double y, double z)
         RotationScaleMatrix matrix = new RotationScaleMatrix();
         Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
         scale.absolute();
         matrix.setScale(scale);
         GeometryBasicsTestTools.assertTuple3DEquals(scale, matrix.getScale(), EPS);
      }
   }

   @Test
   public void testSetToPitchMatrix() throws Exception
   {
      Random random = new Random(435L);
      RotationMatrix rotationMatrix = new RotationMatrix();
      RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      double pitch = 2.0 * Math.PI * random.nextDouble();
      rotationMatrix.setToPitchMatrix(pitch);
      rotationScaleMatrix.setToPitchMatrix(pitch);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);
   }

   @Test
   public void testSetToRollMatrix() throws Exception
   {
      Random random = new Random(435L);
      RotationMatrix rotationMatrix = new RotationMatrix();
      RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      double roll = 2.0 * Math.PI * random.nextDouble();
      rotationMatrix.setToRollMatrix(roll);
      rotationScaleMatrix.setToRollMatrix(roll);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);
   }

   @Test
   public void testSetToYawMatrix() throws Exception
   {
      Random random = new Random(435L);
      RotationMatrix rotationMatrix = new RotationMatrix();
      RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      double yaw = 2.0 * Math.PI * random.nextDouble();
      rotationMatrix.setToYawMatrix(yaw);
      rotationScaleMatrix.setToYawMatrix(yaw);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);
   }

   @Test
   public void testSetYawPitchRoll() throws Exception
   {
      Random random = new Random(435L);
      RotationMatrix rotationMatrix = new RotationMatrix();
      RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

      double yaw = 2.0 * Math.PI * random.nextDouble();
      double pitch = 2.0 * Math.PI * random.nextDouble();
      double roll = 2.0 * Math.PI * random.nextDouble();
      rotationMatrix.setYawPitchRoll(yaw, pitch, roll);
      rotationScaleMatrix.setYawPitchRoll(yaw, pitch, roll);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      rotationScaleMatrix.setYawPitchRoll(new double[] {yaw, pitch, roll});
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      rotationScaleMatrix.setEuler(new Vector3D(roll, pitch, yaw));
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      rotationScaleMatrix.setEuler(roll, pitch, yaw);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix, EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(new Vector3D(1.0, 1.0, 1.0), rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      Vector3D expectedScales = new Vector3D(rotationScaleMatrix.getScale());
      rotationScaleMatrix.setRotationYawPitchRoll(yaw, pitch, roll);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(expectedScales, rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix.setRotationToZero();
      rotationScaleMatrix.setRotationYawPitchRoll(new double[] {yaw, pitch, roll});
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(expectedScales, rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix.setRotationToZero();
      rotationScaleMatrix.setRotationEuler(new Vector3D(roll, pitch, yaw));
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(expectedScales, rotationScaleMatrix.getScale(), EPS);

      rotationScaleMatrix.setRotationToZero();
      rotationScaleMatrix.setRotationEuler(roll, pitch, yaw);
      GeometryBasicsTestTools.assertMatrix3DEquals(rotationMatrix, rotationScaleMatrix.getRotationMatrix(), EPS);
      GeometryBasicsTestTools.assertTuple3DEquals(expectedScales, rotationScaleMatrix.getScale(), EPS);
   }

   @Test
   public void testPreMultiply() throws Exception
   {
      Random random = new Random(65561L);
      RotationScaleMatrix m1 = new RotationScaleMatrix();
      RotationMatrix m2 = new RotationMatrix();
      RotationScaleMatrix expected = new RotationScaleMatrix();
      RotationScaleMatrix actual = new RotationScaleMatrix();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         m2 = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         m1 = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         expected.set(m1);
         ((RotationMatrix) expected.getRotationMatrix()).preMultiply(m2);
         actual.set(m1);
         actual.preMultiply(m2);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testPreMultiplyTransposeOther() throws Exception
   {
      Random random = new Random(65561L);
      RotationScaleMatrix m1 = new RotationScaleMatrix();
      RotationMatrix m2 = new RotationMatrix();
      RotationScaleMatrix expected = new RotationScaleMatrix();
      RotationScaleMatrix actual = new RotationScaleMatrix();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         m2 = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         m1 = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);

         expected.set(m1);
         ((RotationMatrix) expected.getRotationMatrix()).preMultiplyTransposeOther(m2);
         actual.set(m1);
         actual.preMultiplyTransposeOther(m2);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformTuple() throws Exception
   {
      Random random = new Random(435L);
      Vector3D actual = new Vector3D();
      Vector3D expected = new Vector3D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector3D original = GeometryBasicsRandomTools.generateRandomVector3D(random);

         matrix.transform(original, expected);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertTuple3DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertTuple3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformTuple2D() throws Exception
   {
      Random random = new Random(435L);
      Vector2D actual = new Vector2D();
      Vector2D expected = new Vector2D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = new RotationScaleMatrix();
         matrix.setToYawMatrix(2.0 * Math.PI * random.nextDouble());
         matrix.setScale(10.0 * random.nextDouble(), 10.0 * random.nextDouble(), 1.0);
         Vector2D original = GeometryBasicsRandomTools.generateRandomVector2D(random);

         matrix.transform(original, expected, true);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);

         actual.setToNaN();
         Matrix3DTools.transform(matrix, original, expected, true);
         actual.set(original);
         matrix.transform(actual, true);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual, true);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformMatrix() throws Exception
   {
      Random random = new Random(435L);
      Matrix3D actual = new Matrix3D();
      Matrix3D expected = new Matrix3D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Matrix3D original = GeometryBasicsRandomTools.generateRandomMatrix3D(random);

         matrix.transform(original, expected);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformQuaternion() throws Exception
   {
      Random random = new Random(435L);
      Quaternion actual = new Quaternion();
      Quaternion expected = new Quaternion();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Quaternion original = GeometryBasicsRandomTools.generateRandomQuaternion(random);

         matrix.transform(original, expected);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertQuaternionEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertQuaternionEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformVector4D() throws Exception
   {
      Random random = new Random(435L);
      Vector4D actual = new Vector4D();
      Vector4D expected = new Vector4D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector4D original = GeometryBasicsRandomTools.generateRandomVector4D(random);

         matrix.transform(original, expected);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testTransformRotationMatrix() throws Exception
   {
      Random random = new Random(435L);
      RotationMatrix actual = new RotationMatrix();
      RotationMatrix expected = new RotationMatrix();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         RotationMatrix original = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);

         matrix.transform(original, expected);
         actual.set(original);
         matrix.transform(actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.transform(original, actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testInverseTransformTuple() throws Exception
   {
      Random random = new Random(435L);
      Vector3D actual = new Vector3D();
      Vector3D expected = new Vector3D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector3D original = GeometryBasicsRandomTools.generateRandomVector3D(random);

         matrix.inverseTransform(original, expected);
         actual.set(original);
         matrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertTuple3DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertTuple3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testInverseTransformTuple2D() throws Exception
   {
      Random random = new Random(435L);
      Vector2D actual = new Vector2D();
      Vector2D expected = new Vector2D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = new RotationScaleMatrix();
         matrix.setToYawMatrix(2.0 * Math.PI * random.nextDouble());
         matrix.setScale(10.0 * random.nextDouble(), 10.0 * random.nextDouble(), 1.0);
         Vector2D original = GeometryBasicsRandomTools.generateRandomVector2D(random);

         matrix.inverseTransform(original, expected, true);
         actual.set(original);
         matrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertTuple2DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testInverseTransformVector4D() throws Exception
   {
      Random random = new Random(435L);
      Vector4D actual = new Vector4D();
      Vector4D expected = new Vector4D();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector4D original = GeometryBasicsRandomTools.generateRandomVector4D(random);

         matrix.inverseTransform(original, expected);
         actual.set(original);
         matrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);

         actual.setToNaN();
         matrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }
   }

   @Override
   @Test
   public void testInverseTransform() throws Exception
   {
      super.testInverseTransform();

      Random random = new Random(6787L);
      RotationScaleMatrix rotationScaleMatrix = createEmptyMatrix();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test transform(TupleBasics tupleToTransform)
         Tuple3DReadOnly tuple = GeometryBasicsRandomTools.generateRandomVector3D(random);
         Tuple3DBasics<?> actualTuple = new Vector3D(tuple);
         Tuple3DBasics<?> expectedTuple = GeometryBasicsRandomTools.generateRandomVector3D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, tuple, expectedTuple);
         rotationScaleMatrix.inverseTransform(actualTuple);

         GeometryBasicsTestTools.assertTuple3DEquals(expectedTuple, actualTuple, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test transform(TupleBasics tupleOriginal, TupleBasics tupleTransformed)
         Tuple3DReadOnly tuple = GeometryBasicsRandomTools.generateRandomVector3D(random);
         Tuple3DBasics<?> actualTuple = new Vector3D(tuple);
         Tuple3DBasics<?> expectedTuple = GeometryBasicsRandomTools.generateRandomVector3D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, tuple, expectedTuple);
         rotationScaleMatrix.inverseTransform(tuple, actualTuple);

         GeometryBasicsTestTools.assertTuple3DEquals(expectedTuple, actualTuple, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test transform(Tuple2DBasics tupleToTransform)
         Tuple2DReadOnly<?> tuple = GeometryBasicsRandomTools.generateRandomVector2D(random);
         Tuple2DBasics<?> actualTuple = new Vector2D(tuple);
         Tuple2DBasics<?> expectedTuple = GeometryBasicsRandomTools.generateRandomVector2D(random);
         double theta = GeometryBasicsRandomTools.generateRandomDouble(random, Math.PI);
         rotationScaleMatrix.setToYawMatrix(theta);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, tuple, expectedTuple, false);
         rotationScaleMatrix.inverseTransform(actualTuple);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
         actualTuple.set(tuple);
         rotationScaleMatrix.inverseTransform(actualTuple, true);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
         actualTuple.set(tuple);
         rotationScaleMatrix.inverseTransform(actualTuple, false);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test transform(Tuple2DBasics tupleOriginal, Tuple2DBasics tupleTransformed)
         Tuple2DReadOnly<?> tuple = GeometryBasicsRandomTools.generateRandomVector2D(random);
         Tuple2DBasics<?> actualTuple = new Vector2D(tuple);
         Tuple2DBasics<?> expectedTuple = GeometryBasicsRandomTools.generateRandomVector2D(random);
         double theta = GeometryBasicsRandomTools.generateRandomDouble(random, Math.PI);
         rotationScaleMatrix.setToYawMatrix(theta);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, tuple, expectedTuple, false);
         rotationScaleMatrix.inverseTransform(tuple, actualTuple);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
         rotationScaleMatrix.inverseTransform(tuple, actualTuple, true);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
         rotationScaleMatrix.inverseTransform(tuple, actualTuple, false);
         GeometryBasicsTestTools.assertTuple2DEquals(expectedTuple, actualTuple, EPS);
      }

      // Test exceptions
      try
      {
         rotationScaleMatrix = createRandomMatrix(random);
         rotationScaleMatrix.inverseTransform(new Vector2D());
         fail("Should have thrown an exception");
      }
      catch (RuntimeException e)
      {
         // good
      }

      try
      {
         rotationScaleMatrix = createRandomMatrix(random);
         rotationScaleMatrix.inverseTransform(new Vector2D(), new Vector2D());
         fail("Should have thrown an exception");
      }
      catch (RuntimeException e)
      {
         // good
      }
      try
      {
         rotationScaleMatrix = createRandomMatrix(random);
         rotationScaleMatrix.inverseTransform(new Vector2D(), true);
         fail("Should have thrown an exception");
      }
      catch (RuntimeException e)
      {
         // good
      }

      try
      {
         rotationScaleMatrix = createRandomMatrix(random);
         rotationScaleMatrix.inverseTransform(new Vector2D(), new Vector2D(), true);
         fail("Should have thrown an exception");
      }
      catch (RuntimeException e)
      {
         // good
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(QuaternionBasics<?> quaternionToTransform)
         QuaternionReadOnly<?> original = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         QuaternionBasics<?> actual = new Quaternion(original);
         QuaternionBasics<?> expected = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         rotationScaleMatrix = createRandomMatrix(random);

         QuaternionTools.multiplyTransposeMatrix(rotationScaleMatrix.getRotationMatrix(), original, expected);
         rotationScaleMatrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(QuaternionReadOnly<?> quaternionOriginal, QuaternionBasics<?> quaternionTransformed)
         QuaternionReadOnly<?> original = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         QuaternionBasics<?> actual = new Quaternion(original);
         QuaternionBasics<?> expected = GeometryBasicsRandomTools.generateRandomQuaternion(random);
         rotationScaleMatrix = createRandomMatrix(random);

         QuaternionTools.multiplyTransposeMatrix(rotationScaleMatrix.getRotationMatrix(), original, expected);
         rotationScaleMatrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(Vector4DBasics<?> vectorToTransform)
         Vector4DReadOnly<?> original = GeometryBasicsRandomTools.generateRandomVector4D(random);
         Vector4DBasics<?> actual = new Vector4D(original);
         Vector4DBasics<?> expected = GeometryBasicsRandomTools.generateRandomVector4D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, original, expected);
         rotationScaleMatrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(Vector4DReadOnly<?> vectorOriginal, Vector4DBasics<?> vectorTransformed)
         Vector4DReadOnly<?> original = GeometryBasicsRandomTools.generateRandomVector4D(random);
         Vector4DBasics<?> actual = new Vector4D(original);
         Vector4DBasics<?> expected = GeometryBasicsRandomTools.generateRandomVector4D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, original, expected);
         rotationScaleMatrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertTuple4DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(Matrix3D matrixToTransform)
         Matrix3DReadOnly<?> original = GeometryBasicsRandomTools.generateRandomMatrix3D(random);
         Matrix3D actual = new Matrix3D(original);
         Matrix3D expected = GeometryBasicsRandomTools.generateRandomMatrix3D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, original, expected);
         rotationScaleMatrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(Matrix3DReadOnly<?> matrixOriginal, Matrix3D matrixTransformed)
         Matrix3DReadOnly<?> original = GeometryBasicsRandomTools.generateRandomMatrix3D(random);
         Matrix3D actual = new Matrix3D(original);
         Matrix3D expected = GeometryBasicsRandomTools.generateRandomMatrix3D(random);
         rotationScaleMatrix = createRandomMatrix(random);

         Matrix3DTools.inverseTransform(rotationScaleMatrix, original, expected);
         rotationScaleMatrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(RotationMatrix matrixToTransform)
         RotationMatrixReadOnly<?> original = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         RotationMatrix actual = new RotationMatrix(original);
         RotationMatrix expected = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         rotationScaleMatrix = createRandomMatrix(random);

         RotationMatrixTools.multiplyTransposeLeft(rotationScaleMatrix.getRotationMatrix(), original, expected);
         rotationScaleMatrix.inverseTransform(actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test inverseTransform(RotationMatrixReadOnly<?> matrixOriginal, RotationMatrix matrixTransformed)
         RotationMatrixReadOnly<?> original = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         RotationMatrix actual = new RotationMatrix(original);
         RotationMatrix expected = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
         rotationScaleMatrix = createRandomMatrix(random);

         RotationMatrixTools.multiplyTransposeLeft(rotationScaleMatrix.getRotationMatrix(), original, expected);
         rotationScaleMatrix.inverseTransform(original, actual);
         GeometryBasicsTestTools.assertMatrix3DEquals(expected, actual, EPS);
      }
   }

   @Test
   public void testGetRotationEuler() throws Exception
   {
      Random random = new Random(3456L);
      RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      RotationMatrix rotationMatrix = new RotationMatrix(matrix.getRotationMatrix());
      Vector3D eulerActual = new Vector3D();
      matrix.getRotationEuler(eulerActual);
      Vector3D eulerExpected = new Vector3D();
      rotationMatrix.getEuler(eulerExpected);
      GeometryBasicsTestTools.assertTuple3DEquals(eulerExpected, eulerActual, EPS);
   }

   @Test
   public void testGetMaxScale() throws Exception
   {
      Random random = new Random(3456L);

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         RotationScaleMatrix matrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector3D scale = new Vector3D();
         matrix.getScale(scale);
         double expectedMaxScale = Math.max(scale.getX(), Math.max(scale.getY(), scale.getZ()));
         assertTrue(expectedMaxScale == matrix.getMaxScale());
      }
   }

   @Test
   public void testGetRotation() throws Exception
   {
      Random random = new Random(564651L);

      { // Test getRotation(RotationMatrix rotationMatrixToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         RotationMatrix matrixActual = new RotationMatrix();
         rotationScaleMatrix.getRotation(matrixActual);
         assertEquals(rotationScaleMatrix.getRotationMatrix(), matrixActual);
      }

      { // Test getRotation(DenseMatrix64F rotationMatrixToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         DenseMatrix64F denseMatrixActual = new DenseMatrix64F(3, 3);
         DenseMatrix64F denseMatrixExpected = new DenseMatrix64F(3, 3);
         rotationScaleMatrix.getRotation(denseMatrixActual);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               denseMatrixExpected.set(row, column, rotationScaleMatrix.getRotationMatrix().getElement(row, column));
            }
         }

         EjmlUnitTests.assertEquals(denseMatrixExpected, denseMatrixActual, EPS);
      }

      { // Test getRotation(QuaternionBasics quaternionToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Quaternion qActual = new Quaternion();
         rotationScaleMatrix.getRotation(qActual);
         Quaternion qExpected = new Quaternion(rotationScaleMatrix.getRotationMatrix());
         GeometryBasicsTestTools.assertQuaternionEquals(qExpected, qActual, EPS);
      }

      { // Test getRotation(AxisAngleBasics axisAngleToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         AxisAngle axisAngleActual = new AxisAngle();
         rotationScaleMatrix.getRotation(axisAngleActual);
         AxisAngle axisAngleExpected = new AxisAngle(rotationScaleMatrix.getRotationMatrix());
         GeometryBasicsTestTools.assertAxisAngleEquals(axisAngleExpected, axisAngleActual, EPS);
      }

      { // Test getRotation(VectorBasics rotationVectorToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         Vector3D rotationVectorActual = new Vector3D();
         rotationScaleMatrix.getRotation(rotationVectorActual);
         Vector3D rotationVectorExpected = new Vector3D();
         RotationVectorConversion.convertMatrixToRotationVector(rotationScaleMatrix.getRotationMatrix(), rotationVectorExpected);
         GeometryBasicsTestTools.assertTuple3DEquals(rotationVectorExpected, rotationVectorActual, EPS);
      }
   }

   @Test
   public void testGetRotationYawPitchRoll() throws Exception
   {
      Random random = new Random(564651L);

      { // Test getRotationYawPitchRoll(double[] yawPitchRollToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         double[] yawPitchRollActual = new double[3];
         rotationScaleMatrix.getRotationYawPitchRoll(yawPitchRollActual);
         double[] yawPitchRollExpected = new double[3];
         YawPitchRollConversion.convertMatrixToYawPitchRoll(rotationScaleMatrix.getRotationMatrix(), yawPitchRollExpected);
         GeometryBasicsTestTools.assertYawPitchRollEquals(yawPitchRollExpected, yawPitchRollActual, EPS);
      }

      { // Test getRotationYaw(), getRotationPitch(), and getRotationRoll()
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         double[] yawPitchRollActual = {rotationScaleMatrix.getRotationYaw(), rotationScaleMatrix.getRotationPitch(), rotationScaleMatrix.getRotationRoll()};
         double[] yawPitchRollExpected = new double[3];
         YawPitchRollConversion.convertMatrixToYawPitchRoll(rotationScaleMatrix.getRotationMatrix(), yawPitchRollExpected);
         GeometryBasicsTestTools.assertYawPitchRollEquals(yawPitchRollExpected, yawPitchRollActual, EPS);
      }
   }

   @Test
   public void testGetScale() throws Exception
   {
      Random random = new Random(234534L);
      RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      Vector3D scaleExpected = GeometryBasicsRandomTools.generateRandomVector3D(random);
      scaleExpected.absolute();
      rotationScaleMatrix.setScale(scaleExpected);
      Vector3D scaleActual = new Vector3D();
      rotationScaleMatrix.getScale(scaleActual);
      GeometryBasicsTestTools.assertTuple3DEquals(scaleExpected, scaleActual, EPS);

      scaleActual.set(rotationScaleMatrix.getScaleX(), rotationScaleMatrix.getScaleY(), rotationScaleMatrix.getScaleZ());
      GeometryBasicsTestTools.assertTuple3DEquals(scaleExpected, scaleActual, EPS);

      GeometryBasicsTestTools.assertTuple3DEquals(scaleExpected, rotationScaleMatrix.getScale(), EPS);
   }

   @Test
   public void testGetRotationMatrix() throws Exception
   {
      Random random = new Random(23524L);
      RotationMatrix rotationMatrixExpected = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);
      Vector3D scale = GeometryBasicsRandomTools.generateRandomVector3D(random);
      scale.absolute();

      RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix(rotationMatrixExpected, scale);

      assertEquals(rotationMatrixExpected, rotationScaleMatrix.getRotationMatrix());
   }

   @Test
   public void testGet() throws Exception
   {
      Random random = new Random(8768L);

      { // Test get(double[] rotationScaleMatrixArrayToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         double[] matrixArray = new double[9];
         rotationScaleMatrix.get(matrixArray);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               assertTrue(matrixArray[3 * row + column] == rotationScaleMatrix.getElement(row, column));
            }
         }
      }

      { // Test get(double[] rotationScaleMatrixArrayToPack, int startIndex)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         int startIndex = random.nextInt(10);
         double[] matrixArray = new double[9 + startIndex];
         rotationScaleMatrix.get(startIndex, matrixArray);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               assertTrue(matrixArray[3 * row + column + startIndex] == rotationScaleMatrix.getElement(row, column));
            }
         }
      }

      { // Test get(DenseMatrix64F rotationScaleMatrixToPack)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3, 3);
         rotationScaleMatrix.get(denseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               assertTrue(denseMatrix.get(row, column) == rotationScaleMatrix.getElement(row, column));
            }
         }
      }

      { // Test get(DenseMatrix64F rotationScaleMatrixToPack, int startRow, int startColumn)
         RotationScaleMatrix rotationScaleMatrix = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
         int startRow = random.nextInt(10);
         int startColumn = random.nextInt(10);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(3 + startRow, 3 + startColumn);
         rotationScaleMatrix.get(startRow, startColumn, denseMatrix);

         for (int row = 0; row < 3; row++)
         {
            for (int column = 0; column < 3; column++)
            {
               assertTrue(denseMatrix.get(row + startRow, column + startColumn) == rotationScaleMatrix.getElement(row, column));
            }
         }
      }
   }

   @Test
   public void testEquals() throws Exception
   {
      Random random = new Random(2354L);
      RotationScaleMatrix m1 = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      RotationScaleMatrix m2 = new RotationScaleMatrix();

      assertFalse(m1.equals(m2));
      assertFalse(m1.equals(null));
      assertFalse(m1.equals(new double[4]));
      m2.set(m1);
      assertTrue(m1.equals(m2));
      assertTrue(m1.equals((Object) m2));

      double smallestEpsilon = 1.0e-16;
      double[] coeffs = new double[9];

      for (int row = 0; row < 3; row++)
      {
         for (int column = 0; column < 3; column++)
         {
            m2.set(m1);
            assertTrue(m1.equals(m2));
            m1.get(coeffs);
            coeffs[3 * row + column] += smallestEpsilon;
            m2.set(coeffs);
            assertFalse(m1.equals(m2));

            m2.set(m1);
            assertTrue(m1.equals(m2));
            m1.get(coeffs);
            coeffs[3 * row + column] -= smallestEpsilon;
            m2.set(coeffs);
            assertFalse(m1.equals(m2));
         }
      }
   }

   @Override
   @Test
   public void testEpsilonEquals() throws Exception
   {
      /*
       * This test needs to be custom as the method calls epsilonEquals on the rotationMatrix and
       * the scale separately. This means that two rotation-scale matrices can have different
       * rotation and scale parts but still being epsilon-equal when looking at the resulting
       * matrix.
       */
      Random random = new Random(2354L);
      RotationScaleMatrix m1 = GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
      RotationScaleMatrix m2 = new RotationScaleMatrix();
      double epsilon = 1.0e-3;
      double[] coeffs = new double[9];

      assertFalse(m1.epsilonEquals(m2, epsilon));
      m2.set(m1);
      assertTrue(m1.epsilonEquals(m2, epsilon));

      for (int row = 0; row < 3; row++)
      {
         for (int column = 0; column < 3; column++)
         {
            m2.set(m1);
            assertTrue(m1.epsilonEquals(m2, epsilon));
            m1.getRotation(coeffs);
            coeffs[3 * row + column] += 0.999 * epsilon;
            ((RotationMatrix) m2.getRotationMatrix()).setUnsafe(coeffs[0], coeffs[1], coeffs[2], coeffs[3], coeffs[4], coeffs[5], coeffs[6], coeffs[7],
                                                                coeffs[8]);
            assertTrue(m1.epsilonEquals(m2, epsilon));

            m2.set(m1);
            assertTrue(m1.epsilonEquals(m2, epsilon));
            m1.getRotation(coeffs);
            coeffs[3 * row + column] += 1.001 * epsilon;
            ((RotationMatrix) m2.getRotationMatrix()).setUnsafe(coeffs[0], coeffs[1], coeffs[2], coeffs[3], coeffs[4], coeffs[5], coeffs[6], coeffs[7],
                                                                coeffs[8]);
            assertFalse(m1.epsilonEquals(m2, epsilon));

            m2.set(m1);
            assertTrue(m1.epsilonEquals(m2, epsilon));
            m1.getRotation(coeffs);
            coeffs[3 * row + column] -= 0.999 * epsilon;
            ((RotationMatrix) m2.getRotationMatrix()).setUnsafe(coeffs[0], coeffs[1], coeffs[2], coeffs[3], coeffs[4], coeffs[5], coeffs[6], coeffs[7],
                                                                coeffs[8]);
            assertTrue(m1.epsilonEquals(m2, epsilon));

            m2.set(m1);
            assertTrue(m1.epsilonEquals(m2, epsilon));
            m1.getRotation(coeffs);
            coeffs[3 * row + column] -= 1.001 * epsilon;
            ((RotationMatrix) m2.getRotationMatrix()).setUnsafe(coeffs[0], coeffs[1], coeffs[2], coeffs[3], coeffs[4], coeffs[5], coeffs[6], coeffs[7],
                                                                coeffs[8]);
            assertFalse(m1.epsilonEquals(m2, epsilon));
         }
      }
   }

   @Override
   public RotationScaleMatrix createEmptyMatrix()
   {
      return new RotationScaleMatrix();
   }

   @Override
   public RotationScaleMatrix createMatrix(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
   {
      RotationMatrix rotationMatrix = new RotationMatrix();
      rotationMatrix.setUnsafe(m00, m01, m02, m10, m11, m12, m20, m21, m22);
      RotationScaleMatrix rotationScaleMatrix = new RotationScaleMatrix();
      rotationScaleMatrix.setRotation(rotationMatrix);
      return rotationScaleMatrix;
   }

   @Override
   public RotationScaleMatrix createRandomMatrix(Random random)
   {
      return GeometryBasicsRandomTools.generateRandomRotationScaleMatrix(random, 10.0);
   }
}
