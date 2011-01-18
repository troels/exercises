package org.bifrost;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.bifrost.triangledetector.TriangleDetector;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;

class PermutationGenerator { 
    static <T> LinkedList<LinkedList<T>> permute(LinkedList<T> in) {
	if (in.isEmpty()) { 
	    LinkedList<LinkedList<T>> ret = new LinkedList<LinkedList<T>>();
	    ret.add(new LinkedList<T>());
	    return ret;
	} 
	
	LinkedList<LinkedList<T>> perms = new LinkedList<LinkedList<T>>();
	for(int i = 0; i < in.size(); ++i ){
	    T elem = in.get(i);
	    LinkedList<T> temp = new LinkedList<T>(in);
	    temp.remove(i);
	    Collection<LinkedList<T>> newPerms = permute(temp);

	    for (LinkedList<T> list: newPerms) {
		int size = newPerms.size();
		for(int j = 0; j <= size; ++j) {
		    LinkedList<T> l = new LinkedList(list);
		    l.add(j, elem);
		    perms.add(l);
		}
	    }
	}
	return perms;
    }
}

public class TriangleDetectorTest extends TestCase 
{ 
    public TriangleDetectorTest()
    {
        super("TriangleDetectorTest");
    }

    public static Test suite() {
	return new TestSuite(TriangleDetectorTest.class);
    }

    public void testDetector() {
	LinkedList<Integer> lst = new LinkedList<Integer>();
	lst.add(1); lst.add(2); lst.add(3); lst.add(4); lst.add(5);
	System.out.println(PermutationGenerator.permute(lst));
    }
}