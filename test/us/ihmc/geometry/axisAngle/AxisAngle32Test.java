package us.ihmc.geometry.axisAngle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import us.ihmc.geometry.matrix.RotationMatrix;
import us.ihmc.geometry.testingTools.GeometryBasicsRandomTools;
import us.ihmc.geometry.testingTools.GeometryBasicsTestTools;
import us.ihmc.geometry.tuple3D.Vector3D;
import us.ihmc.geometry.tuple4D.Quaternion;

public class AxisAngle32Test extends AxisAngleBasicsTest<AxisAngle32>
{
   public static final int NUMBER_OF_ITERATIONS = AxisAngleTest.NUMBER_OF_ITERATIONS;
   public static final double EPS = 1e-6;

   @Test
   public void testAxisAngle32()
   {
      Random random = new Random(613615L);
      AxisAngle32 axisAngle = new AxisAngle32(), expected;
      { // Test AxisAngle32()
         Assert.assertTrue(axisAngle.getX() == 1.0);
         Assert.assertTrue(axisAngle.getY() == 0.0);
         Assert.assertTrue(axisAngle.getZ() == 0.0);
         Assert.assertTrue(axisAngle.getAngle() == 0.0);
      }

      { // Test AxisAngle32(AxisAngleBasics other)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            axisAngle = expected = GeometryBasicsRandomTools.generateRandomAxisAngle32(random);

            AxisAngle32 axisAngle2 = new AxisAngle32(axisAngle);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, axisAngle2, EPS);
            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expected, EPS);
         }
      }

      { // Test AxisAngle32(float x, float y, float z, float angle)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            expected = GeometryBasicsRandomTools.generateRandomAxisAngle32(random);
            axisAngle = new AxisAngle32(expected.getX32(), expected.getY32(), expected.getZ32(), expected.getAngle32());

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expected, EPS);
         }
      }

      { // Test AxisAngle32(float[] axisAngleArray)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            expected = GeometryBasicsRandomTools.generateRandomAxisAngle32(random);
            float[] axisAngleArray;
            float[] axisAngleArrayCopy;
            axisAngleArray = axisAngleArrayCopy = new float[] {expected.getX32(), expected.getY32(), expected.getZ32(), expected.getAngle32()};

            axisAngle = new AxisAngle32(axisAngleArray);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expected, EPS);

            for (int j = 0; j < axisAngleArray.length; j++)
               Assert.assertTrue(axisAngleArray[j] == axisAngleArrayCopy[j]);
         }
      }

      { // Test AxisAngle32(VectorBasics axis, float angle)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            Vector3D vectorAxis, vectorAxisCopy;
            vectorAxis = vectorAxisCopy = GeometryBasicsRandomTools.generateRandomVector3D(random);

            float angle, angleCopy;
            angle = angleCopy = random.nextFloat();

            axisAngle = new AxisAngle32(vectorAxis, angle);

            Assert.assertEquals(axisAngle.getX(), vectorAxis.getX(), EPS);
            Assert.assertEquals(axisAngle.getY(), vectorAxis.getY(), EPS);
            Assert.assertEquals(axisAngle.getZ(), vectorAxis.getZ(), EPS);
            Assert.assertEquals(axisAngle.getAngle(), angle, EPS);

            GeometryBasicsTestTools.assertRotationVectorEquals(vectorAxis, vectorAxisCopy, EPS);
            Assert.assertTrue(angle == angleCopy);
         }
      }

      { // Test AxisAngle32(QuaternionBasics quaternion)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            Quaternion quaternion, quaternionCopy;
            quaternion = quaternionCopy = GeometryBasicsRandomTools.generateRandomQuaternion(random);

            axisAngle = new AxisAngle32(quaternion);
            AxisAngle32 expectedAxisAngle32 = new AxisAngle32();
            AxisAngleConversion.convertQuaternionToAxisAngle(quaternion, expectedAxisAngle32);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expectedAxisAngle32, EPS);
            GeometryBasicsTestTools.assertQuaternionEquals(quaternion, quaternionCopy, EPS);
         }
      }

      { // Test AxisAngle32(RotationMatrix rotationMatrix)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            RotationMatrix matrix, matrixCopy;
            matrix = matrixCopy = GeometryBasicsRandomTools.generateRandomRotationMatrix(random);

            float angle;
            float angleCopy;
            angle = angleCopy = random.nextFloat();

            axisAngle = new AxisAngle32(matrix);
            AxisAngle32 expectedAxisAngle32 = new AxisAngle32();
            AxisAngleConversion.convertMatrixToAxisAngle(matrix, expectedAxisAngle32);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expectedAxisAngle32, EPS);

            Assert.assertTrue(angle == angleCopy);

            GeometryBasicsTestTools.assertMatrix3DEquals(matrix, matrixCopy, EPS);
         }
      }

      { // Test AxisAngle32(VectorBasics rotationVector)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            Vector3D rotationVector, rotationVectorCopy;
            rotationVector = rotationVectorCopy = GeometryBasicsRandomTools.generateRandomRotationVector(random);

            axisAngle = new AxisAngle32(rotationVector);
            AxisAngle32 expectedAxisAngle32 = new AxisAngle32();
            AxisAngleConversion.convertRotationVectorToAxisAngle(rotationVector, expectedAxisAngle32);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expectedAxisAngle32, EPS);
            GeometryBasicsTestTools.assertRotationVectorEquals(rotationVector, rotationVectorCopy, EPS);
         }
      }

      { // Test AxisAngle(double yaw, double pitch, double roll)
         for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
         {
            double[] yawPitchRoll = GeometryBasicsRandomTools.generateRandomYawPitchRoll(random);

            axisAngle = new AxisAngle32(yawPitchRoll[0], yawPitchRoll[1], yawPitchRoll[2]);
            AxisAngle32 expectedAxisAngle = new AxisAngle32();
            AxisAngleConversion.convertYawPitchRollToAxisAngle(yawPitchRoll, expectedAxisAngle);

            GeometryBasicsTestTools.assertAxisAngleEquals(axisAngle, expectedAxisAngle, EPS);
         }
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      Random random = new Random(621541L);
      AxisAngle32 axisAngle = GeometryBasicsRandomTools.generateRandomAxisAngle32(random);

      int newHashCode, previousHashCode;
      newHashCode = axisAngle.hashCode();
      assertEquals(newHashCode, axisAngle.hashCode());

      previousHashCode = axisAngle.hashCode();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         axisAngle.set(random.nextInt(4), random.nextFloat());
         newHashCode = axisAngle.hashCode();
         assertNotEquals(newHashCode, previousHashCode);
         previousHashCode = newHashCode;
      }
   }

   @Override
   public AxisAngle32 createEmptyAxisAngle()
   {
      return new AxisAngle32();
   }

   @Override
   public AxisAngle32 createAxisAngle(double ux, double uy, double uz, double angle)
   {
      return new AxisAngle32((float) ux, (float) uy, (float) uz, (float) angle);
   }

   @Override
   public AxisAngle32 createRandomAxisAngle(Random random)
   {
      return GeometryBasicsRandomTools.generateRandomAxisAngle32(random);
   }

   @Override
   public double getEpsilon()
   {
      return EPS;
   }

   @Override
   public double getSmallestEpsilon()
   {
      return 1.0e-6;
   }
}
