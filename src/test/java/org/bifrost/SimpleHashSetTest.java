package org.bifrost;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.bifrost.triangledetector.TriangleDetector;
import java.util.Collection;
import java.util.Iterator;;
import java.util.ArrayList;
import java.util.LinkedList;

public class SimpleHashSetTest extends TestCase 
{ 
    public TriangleDetectorTest()
    {
        super("TriangleDetectorTest");
    }

    public static Test suite() {
	return new TestSuite(TriangleDetectorTest.class);
    }

    /**
     * Test TriangleDetector.detectTriangle, by sending all permutations of the arguments a, b and c
     *
     * @param a, b, c Arguments permuted and passed on to TriangleDetector.detectTriangle
     *
     * @param result The expected return value from TriangleDetector.detectTriangle
     *
     */
    public void testPermutations(int a, int b, int c, int result) {
	    LinkedList<Integer> lst = new LinkedList<Integer>();
	    lst.add(a); lst.add(b); lst.add(c);
	    for (Collection<Integer> coll: PermutationGenerator.permute(lst)) {
		Iterator<Integer> iter = coll.iterator();
		int a_ = iter.next(); int b_ = iter.next(); int c_ = iter.next();
		assertEquals(
			     String.format("TriangleDetector.detectTriangle(%d, %d, %d) should return %d",
					   a_, b_, c_, result),
			     
			     result, TriangleDetector.detectTriangle(a_, b_, c_));
	    }
    }

    public void testTriangleDetector() {
	testPermutations(-1, 2, 3, TriangleDetector.ERROR);
	testPermutations(4, 0, 3, TriangleDetector.ERROR);
	testPermutations(2, 2, 3, TriangleDetector.ISOSCELES);
	testPermutations(2, 3, 4, TriangleDetector.SCALENE);
	testPermutations(4, 4, 4, TriangleDetector.EQUILATERAL);
    }
}