package org.bifrost.simplehashset;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.AbstractSet;

public class SimpleHashSet<T> extends AbstractSet<T> {
    private final HashMap<T, Object> hashmap;
    
    public SimpleHashSet() {
	hashmap = new HashMap<T, Object>(16, 0.75f);
    }
    
    public SimpleHashSet(Collection<? extends T> coll) { 
    	hashmap = new HashMap<T, Object>(coll.size(), 0.75f);
    	for (T elem: coll) {
    	    hashmap.put(elem, new Object());
    	}
    }
    
    public SimpleHashSet(int initialCapacity) {
	hashmap = new HashMap<T, Object>(initialCapacity);
    }

    public SimpleHashSet(int initialCapacity, float loadFactor) {
	hashmap = new HashMap<T, Object>(initialCapacity, loadFactor);
    }
    
    @Override
    public boolean add(T t) {
	if (contains(t)) {
	    return false;
	} else {
	    hashmap.put(t, new Object());
	    return true;
	}
    }

    @Override
    public Object clone() { 
	return new SimpleHashSet<T>(hashmap.keySet());
    }
    
    @Override
    public void clear() {
	hashmap.clear();
    }

    @Override
    public boolean contains(Object o) {
	return hashmap.containsKey(o);
    }

    @Override
    public boolean isEmpty() {
	return hashmap.isEmpty();
    }
    
    @Override
    public Iterator<T> iterator() { 
	return hashmap.keySet().iterator();
    }
    
    @Override
    public boolean remove(Object o){ 
	return hashmap.remove(o) != null;
    }

    @Override
    public int size() {
	return hashmap.size();
    }
};

