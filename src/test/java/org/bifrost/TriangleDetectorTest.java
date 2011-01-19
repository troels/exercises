package org.bifrost;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.bifrost.triangledetector.TriangleDetector;
import java.util.Collection;
import java.util.Iterator;;
import java.util.List;;
import java.util.ArrayList;
import java.util.LinkedList;

class PermutationGenerator { 
    /**
     * A simple nonefficient permutation-generator. 
     * 
     * @param in A List of values to be permuted
     * 
     * @return A complete linkedList of linkedlists, each containing a unique permutation of the input list
     */
    static <T> LinkedList<LinkedList<T>> permute(List<T> in) {
	if (in.isEmpty()) { 
	    LinkedList<LinkedList<T>> ret = new LinkedList<LinkedList<T>>();
	    ret.add(new LinkedList<T>());
	    return ret;
	} 
	
	LinkedList<LinkedList<T>> perms = new LinkedList<LinkedList<T>>();
	
	LinkedList<T> temp = new LinkedList<T>(in);
	temp.remove(0);
	T elem = in.get(0);

	Collection<LinkedList<T>> newPerms = permute(temp);
	for (LinkedList<T> list: newPerms) {
	    int size = list.size();
	    for(int j = 0; j <= size; ++j) {
		LinkedList<T> l = new LinkedList(list);
		l.add(j, elem);
		perms.add(l);
	    }
	}
	return perms;
    }
}

public class TriangleDetectorTest extends TestCase 
{ 
    public TriangleDetectorTest() {
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
	testPermutations(1, 2, 4, TriangleDetector.ERROR);
    }
}