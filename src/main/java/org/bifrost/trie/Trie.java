package org.bifrost.trie;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Stack;

/**
 * A trie or a prefix tree is a map-datastructure that is very well-suited for mapping
 * long, similar strings written in a small alphabet to values of any kind.
 * 
 * It's worst-case characteristics is in many respects better than
 * hash tables and binary trees.  Average performance compared to
 * binary trees and hash tables in common scenarios, are not too
 * exciting though.
 * 
 * @url http://en.wikipedia.org/wiki/Trie
 */
public class Trie<K extends CharSequence, V> extends AbstractMap<K, V> {
    /** 
     * Our basic datastructure is a tree, consisting of Nodes and
     * Edges.  Each Edge has a label, and points to a node.
     * 
     * When searching for the existence of a key, we will recursively
     * walk down the edges, comparing an edge to the first character
     * of the key, go to the node at the end of the edge, and follow
     * edges from there with the second character etc.
     *
     * This will work when characters are partof utf16 surrogate pairs
     * too.  Though we have no guarantee that someone has not put an
     * illegal surrogate pair in the tree.
     */
    class Edge { 
	final char label;
	final Node to;

	public Edge(char label, Node to) {
	    this.label = label;
	    this.to = to;
	}
	
	public Node getTo() { return to; }
	public char getLabel() { return label; }
    }
    
    /**
     * A node has zero or more children, a parent (only used for speeding up deletion somewhat)
     * and occasionally some value (the payload)
     */ 
    class Node {
	final Node parent;
	final LinkedList<Edge> children; 
	boolean hasPayload;
	TrieEntry payload; 
	
	public Node(Node parent, TrieEntry payload) {
	    this(parent);
	    setPayload(payload);
	}

	public boolean hasParent() {
	    return parent != null;
	}

	public Node(Node parent) { 
	    this.parent = parent;
	    hasPayload = false;
	    children = new LinkedList<Edge>();
	}
	
	public void setPayload(TrieEntry payload) { 
	    hasPayload = true;
	    this.payload = payload;
	}

	public void deletePayload() { 
	    hasPayload = false;
	    this.payload = null;
	}

	public void addChild(Edge child) { 
	    children.add(child);
	}
	
	public Node getParent() { return parent; }
	public List<Edge> getChildren() { return children; }
	public TrieEntry getPayload() { return payload; }
	public boolean hasPayload() { return hasPayload; }
    }

    Node root;
    /**
     * nrElements keeps track of the number of elements we have added and not removed (or overwritten).
     * it is redundant, but will speed up common operations like size() and isEmpty() tremendously.
     */
    int nrElements = 0;

    /**
     * TrieEntry necessary for entrySet()
     */
    public class TrieEntry implements Map.Entry<K, V> {
	final K k;
	V v;
	
	public TrieEntry(K k, V v) { 
	    this.k = k;
	    this.v = v;
	}

	/*
	 * Because of erasure this method is shaky, as we don't have any guarantees what kind of 
	 * Map.Entry o is.  It may throw exceptions or it may not think itself equals to us (dropping symmetry)
	 */
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof Map.Entry)) return false;
	    Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
	    return entry.getKey().equals(getKey()) && entry.getValue().equals(getValue());
	}

	@Override
	public K getKey() { return k; }
	
	@Override 
	public V getValue() { return v; }
	
	/*
	 * This is from java documentation and is used by Trie.hashCode to calculate the total hashCode of the trie.
	 */
	@Override 
	public int hashCode() { 
	    return (getKey() == null ? 0 : getKey().hashCode()) ^ 
		   (getValue() == null ? 0 : getValue().hashCode());
	}
	
	
	/**
	 * We have a gurantee that the mapping still exists in the map, so this is basically just a payload exchange.
	 * if the mapping does not exist we are undefined (but will generally pull out allright anyway).
	 */
	@Override 
	public V setValue(V v) {
	    V ret =  Trie.this.put(k, v);
	    this.v = v;
	    return ret;
	}
    }

    public Trie() {
	nrElements = 0;
	root = new Node(null);
    }

    @Override
    public void clear() { 
	nrElements = 0;
	root = new Node(null);
    }

    @Override
    public V put(K k, V value) { 
	Node node = root;
	CharSequence key = (CharSequence) k;
	outer: for (int i = 0; i < key.length(); ++i ) {
	    // Traverse until we have knocked of as much prefix as exists in the trie from k.
	    char c = key.charAt(i);
	    for(Edge child: node.getChildren()) {
		if (child.getLabel() == c) {
		    node = child.getTo();
		    continue outer;
		}
	    } 
	    
	    // Key is novel, all prefix of k we could eat has been eaten.
	    for (;i < key.length(); ++i) {
		Node newNode = new Node(node);
		Edge e = new Edge(key.charAt(i), newNode);
		node.addChild(e);
		node = newNode;
	    }
	}
	
	//Finally add payload
	TrieEntry oldPayload = null;
	if (node.hasPayload()) { 
	    oldPayload = node.getPayload();
	} else {
	    nrElements++;
	}
	node.setPayload(new TrieEntry (k, value));
	return oldPayload == null ? null : oldPayload.getValue();
    }

    /**
     * TrieSet for entrySet()
     */
    class TrieSet extends AbstractSet<Map.Entry<K, V>> {
	@Override 
	public int size() {
	    return nrElements;
	}

	/**
	 * TrieSetIterator is a bit tricky as we would have to use the callstack for returning
	 * to the one calling next(). 
	 * We therefore remember the position we were on in the tree by a stack and a couple of iterators.
	 *
	 * remove is also tricky as the active iterators may give undefined behaviour if we modify a collection 
	 * by any other means than the very same iterator. We therefore don't implement remove currently.
	 */
	class TrieSetIterator implements Iterator<Map.Entry<K, V>> {
	    class NodeIteration {
		public final Node node;
		public final Iterator<Edge> children;
		
		public NodeIteration(Node n) {
		    node = n;
		    children = node.getChildren().iterator();
		}
	    }
		
	    private Stack<NodeIteration> stack;
	    {
		stack = new Stack<NodeIteration>();
		stack.push(new NodeIteration(root));
		/**
		 * Edge case: when root has payload, don't go searching anymore.
		 */
		if (!root.hasPayload()) {
		    gotoNextPayload();
		}
	    }
	    
	    /** 
	     * depth first preorder traversal after payloads.
	     */
	    private void gotoNextPayload() { 
		while(!stack.isEmpty()) {
		    NodeIteration cur = stack.peek();
		    while(cur.children.hasNext()) {
			Node next = cur.children.next().getTo();
			stack.push((cur = new NodeIteration(next)));
			if (next.hasPayload()) return;
		    }
		    stack.pop();
		}
	    }
	    
	    @Override
	    public boolean hasNext() {
		return !stack.isEmpty();
	    }

	    @Override 
	    public Map.Entry<K, V> next() {
		Node cur = stack.peek().node;
		Map.Entry<K, V> payload = cur.getPayload();
		gotoNextPayload();
		return payload;
	    }

	    @Override 
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	}
		    
	public Iterator<Map.Entry<K, V>> iterator() { 
	    return new TrieSetIterator();
	}
    }
	
    @Override 
    public Set<Map.Entry<K, V>> entrySet() {
	return new TrieSet();
    }


    private void getRidOfNode(Node n) {
	assert n.hasPayload();
	n.deletePayload();
	pruneUpwards(n);
	nrElements--;
    }
	
    @Override 
    public V remove(Object key) { 
	CharSequence cs = (CharSequence) key;
	Node n = findNode(cs);
	if (n == null || !n.hasPayload()) return null;
	TrieEntry te = n.getPayload();
	
	getRidOfNode(n);
	return te.getValue();
    }

    private void pruneUpwards(Node n) { 
	while (n.getChildren().isEmpty() && !n.hasPayload() && n.hasParent()) {
	    Node parent = n.getParent();
	    int i = 0;
	    Collection<Edge> children = parent.getChildren();
	    for(Edge e: children) {
		if (e.getTo() == n) break;
		++i;
	    }
	    assert i < children.size();
	    children.remove(i);
	    n = parent;
	}
    }
    
    @Override
    public boolean containsKey(Object key) { 
	CharSequence cs = (CharSequence) key;
	Node followedTo = findNode(cs);
	return followedTo != null && followedTo.hasPayload();
    }
    
    @Override
    public boolean containsValue(Object value) { 
	return containsValue(value, root);
    }

    @Override
    public V get(Object k) {
	CharSequence cs = (CharSequence)k;
	
	Node n = findNode(cs);

	return n != null && n.hasPayload() ? n.getPayload().getValue() : null;
    }

    private Node findNode(CharSequence s) { 
	Node node = root;
	outer: for (int i = 0; i < s.length(); ++i) {
	    char c = s.charAt(i);
	    for (Edge child: node.getChildren()) {
		if (child.getLabel() == c) {
		    node = child.getTo();
		    continue outer;
		}
	    } 
	    return null;
	}
	return node;
    }

    private boolean containsValue(Object value, Node n) {
	if (n.hasPayload() && n.getPayload().getValue().equals(value)) return true; 
	for (Edge e: n.getChildren()) {
	    if (containsValue(value, e.getTo())) return true;
	}
	return false;
    }
}
