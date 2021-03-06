package us.ihmc.euclid.tuple2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DReadOnly;
import us.ihmc.euclid.tuple3D.Vector3D;

public class Vector2DTest extends Vector2DBasicsTest<Vector2D>
{
   @Test
   public void testVector2D()
   {
      Random random = new Random(621541L);
      Vector2D vector = new Vector2D();

      { // Test Vector2D()
         Assert.assertTrue(0 == vector.getX());
         Assert.assertTrue(0 == vector.getY());
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test Vector2D(double x, double y, double z)
         double newX = random.nextDouble();
         double newY = random.nextDouble();

         vector = new Vector2D(newX, newY);

         Assert.assertTrue(newX == vector.getX());
         Assert.assertTrue(newY == vector.getY());
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test Vector2D(double[] vectorArray)
         double[] randomVector2DArray = {random.nextDouble(), random.nextDouble()};
         double[] copyRandomVector2DArray = new double[2];
         copyRandomVector2DArray[0] = randomVector2DArray[0];
         copyRandomVector2DArray[1] = randomVector2DArray[1];

         Vector2D vectorArray = new Vector2D(randomVector2DArray);

         Assert.assertTrue(randomVector2DArray[0] == vectorArray.getX());
         Assert.assertTrue(randomVector2DArray[1] == vectorArray.getY());

         Assert.assertTrue(copyRandomVector2DArray[0] == randomVector2DArray[0]);
         Assert.assertTrue(copyRandomVector2DArray[1] == randomVector2DArray[1]);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test Vector2D(Tuple2DReadOnly tuple)
         Vector2D vector2 = EuclidCoreRandomTools.nextVector2D(random);
         vector = new Vector2D(vector2);

         Assert.assertTrue(vector.getX() == vector2.getX());
         Assert.assertTrue(vector.getY() == vector2.getY());
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test Vector2D(Tuple3DReadOnly tuple)
         Vector3D vector2 = EuclidCoreRandomTools.nextVector3D(random);
         vector = new Vector2D(vector2);

         Assert.assertTrue(vector.getX() == vector2.getX());
         Assert.assertTrue(vector.getY() == vector2.getY());
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      Random random = new Random(621541L);
      Vector2D tuple1 = createRandomTuple(random);

      int newHashCode, previousHashCode;
      newHashCode = tuple1.hashCode();
      assertEquals(newHashCode, tuple1.hashCode());

      previousHashCode = tuple1.hashCode();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         tuple1.setElement(i % 2, random.nextDouble());
         newHashCode = tuple1.hashCode();
         assertNotEquals(newHashCode, previousHashCode);
         previousHashCode = newHashCode;
      }
   }

   @Override
   @Test
   public void testGeometricallyEquals() throws Exception
   {
      super.testGeometricallyEquals();

      Vector2D vectorA;
      Vector2D vectorB;
      Random random = new Random(621541L);

      for (int i = 0; i < 100; ++i)
      {
         vectorA = EuclidCoreRandomTools.nextVector2D(random);
         vectorB = EuclidCoreRandomTools.nextVector2D(random);

         if (((Vector2DReadOnly) vectorA).geometricallyEquals(vectorB, getEpsilon()))
         {
            assertTrue(vectorA.geometricallyEquals(vectorB, getEpsilon()));
         }
         else
         {
            assertFalse(vectorA.geometricallyEquals(vectorB, getEpsilon()));
         }
      }
   }

   @Override
   public double getEpsilon()
   {
      return 1.0e-15;
   }

   @Override
   public Vector2D createEmptyTuple()
   {
      return new Vector2D();
   }

   @Override
   public Vector2D createTuple(double x, double y)
   {
      return new Vector2D(x, y);
   }

   @Override
   public Vector2D createRandomTuple(Random random)
   {
      return EuclidCoreRandomTools.nextVector2D(random);
   }
}
