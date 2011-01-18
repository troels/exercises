package org.bifrost.bag;

import java.util.Collections;
import java.util.List;

class Utils {
    List<Integer> genRange(int min, int max, int step) {
	List<Integer> lst = new ArrayList<Integer>((max - min) / step)
	assert min <= max;
	assert step > 0;
	
	for(int i = min; i < max; i += step) {
	    lst.add(i);
	}
	return lst;
    }

    List<Integer> genShuffledrange(int min, int max) {
	List<Integer> lst = genRange(min, max, 1);
	Collections.shuffle(lst);
	return lst;
    }
};

class Bag<T> extends AbstractCollection { 
    private Object[] container;
    private int[] containerSequence;

    Bag() {
	container = 
	
}