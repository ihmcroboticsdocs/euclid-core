package us.ihmc.geometry.tuple3D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import us.ihmc.geometry.testingTools.GeometryBasicsRandomTools;
import us.ihmc.geometry.tuple3D.interfaces.Point3DBasics;

public abstract class Point3DBasicsTest<T extends Point3DBasics<T>> extends Tuple3DBasicsTest<T>
{
   // Read-only part
   @Test
   public void testDistance()
   {
      Random random = new Random(654135L);

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         Vector3D translation = GeometryBasicsRandomTools.generateRandomVector3DWithFixedLength(random, 1.0);
         double expectedDistance = GeometryBasicsRandomTools.generateRandomDouble(random, 0.0, 10.0);
         translation.scale(expectedDistance);
         T p1 = createRandomTuple(random);
         T p2 = createTuple(p1.getX() + translation.getX(), p1.getY() + translation.getY(), p1.getZ() + translation.getZ());
         double actualDistance = p1.distance(p2);
         assertEquals(expectedDistance, actualDistance, 5.0 * getEpsilon());
      }
   }

   @Test
   public void testDistanceSquared()
   {
      Random random = new Random(654135L);

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         Vector3D translation = GeometryBasicsRandomTools.generateRandomVector3DWithFixedLength(random, 1.0);
         double expectedDistanceSquared = GeometryBasicsRandomTools.generateRandomDouble(random, 0.0, 10.0);
         translation.scale(Math.sqrt(expectedDistanceSquared));
         T p1 = createRandomTuple(random);
         T p2 = createTuple(p1.getX() + translation.getX(), p1.getY() + translation.getY(), p1.getZ() + translation.getZ());
         double actualDistanceSquared = p1.distanceSquared(p2);
         assertEquals(expectedDistanceSquared, actualDistanceSquared, 10.0 * getEpsilon());
      }
   }

   // Basics part
   @Test
   @Ignore
   public void testApplyTransform()
   {
      fail("Not yet implemented");
   }
}
