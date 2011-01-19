package org.bifrost;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bifrost.trie.Trie;

public class TrieTest extends TestCase 
{ 
    public TrieTest() {
	super("TrieTest");
    }

    public static Test suite() { 
	return new TestSuite(TrieTest.class);
    }

    public void testAdd() { 
	Trie<String, String> trie = new Trie<String, String>();
	assertEquals(null, trie.put("Hello there", "I am"));
	assertEquals(null, trie.put("Hi me", "its here"));
	assertEquals(null, trie.put("Hello its me", "Fine by me"));
	assertEquals("Fine by me", trie.get("Hello its me"));
	assertEquals("Fine by me", trie.put("Hello its me", "Not so fine"));
	assertEquals("Not so fine", trie.get("Hello its me"));
    }

    public void testRemove() { 
	Trie<String, Integer> trie = new Trie<String, Integer>();
	trie.put("String1", 1); trie.put("String23", 23); 
	trie.put("StrING45", 45); trie.put("String15", 15);

	assertEquals(new Integer(15), trie.get("String15"));
	assert(!trie.isEmpty());
	assertEquals(new Integer(15), trie.remove("String15"));
	assertEquals(new Integer(23), trie.remove("String23"));
	assertEquals(null, trie.get("String15"));
	assert(!trie.isEmpty());
	assertEquals(2, trie.size());

	assertEquals(new Integer(1), trie.remove("String1"));
	assertEquals(new Integer(45), trie.remove("StrING45"));
	assertEquals(0, trie.size());
	assert(trie.isEmpty());
	
	assertEquals(null, trie.put("String1", 23));
    }

    public void testEntrySet() { 
	Random rng = new Random();
	ArrayList<Integer> al = new ArrayList<Integer>(100);
	Trie<String, Integer> trie = new Trie<String, Integer>();

	for (int i = 0; i < 100; ++i) {
	    Integer ni = rng.nextInt(100000000);
	    al.add(ni);
	    trie.put(ni.toString(), ni);
	}
	
	Set<Map.Entry<String, Integer>> set = trie.entrySet();
	Iterator<Map.Entry<String, Integer>> iter = set.iterator();
	assert(iter.hasNext());
	assertEquals(set.size(), 100);

	trie.put("", 100);
	while(iter.hasNext()) {
	    al.remove(new Integer(iter.next().getValue()));
	}
	assert(al.isEmpty());
	
	assertEquals(101, trie.size());
	assertEquals(new Integer(100), trie.entrySet().iterator().next().getValue());
    }
    
    /**
     * test containsKey and containsValue
     */
    public void testContains() {
	Random rng = new Random();
	ArrayList<Integer> al = new ArrayList<Integer>(100);
	Trie<String, Integer> trie = new Trie<String, Integer>();

	for (int i = 0; i < 100; ++i) {
	    Integer ni = rng.nextInt(100000000);
	    al.add(ni);
	    trie.put(ni.toString(), ni);
	}
	
	for(Integer i: al) { 
	    assert(trie.containsKey(i.toString()));
	    assert(trie.containsValue(i));
	}

	for (Integer i = 0 ; i < 1000; ++i ) {
	    assert (al.contains(i) || (!trie.containsKey(i.toString()) && !trie.containsValue(i)) );
	}
    }
}