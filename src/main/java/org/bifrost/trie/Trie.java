package org.bifrost.trie;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

class Trie<K extends CharSequence, V> extends AbstractMap<K, V> {
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

    class Node {
	final LinkedList<Edge> children; 
	boolean hasPayload;
	TrieEntry payload; 
	
	public Node(TrieEntry payload) {
	    this();
	    setPayload(payload);
	}

	public Node() { 
	    hasPayload = false;
	    children = new LinkedList<Edge>();
	}
	
	public void setPayload(TrieEntry payload) { 
	    hasPayload = true;
	    this.payload = payload;
	}

	public void addChild(Edge child) { 
	    children.add(child);
	}
	
	public List<Edge> getChildren() { return children; }
	public TrieEntry getPayload() { return payload; }
	public boolean hasPayload() { return hasPayload; }
    }

    Node root;
    int nrElements = 0;

    class TrieEntry implements Map.Entry<K, V> {
	final K k;
	final V v;
	
	public TrieEntry(K k, V v) { 
	    this.k = k;
	    this.v = v;
	}

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

	@Override 
	public int hashCode() { 
	    return (getKey() == null ? 0 : getKey().hashCode()) ^ 
		   (getValue() == null ? 0 : getValue().hashCode());
	}

	@Override 
	public V setValue(V v) {
	    return Trie.this.put(k, v);
	}
    }

    public Trie() {
	root = new Node();
    }

    @Override
    public void clear() { 
	nrElements = 0;
	root = new Node();
    }

    @Override
    public V put(K k, V value) { 
	Node node = root;
	CharSequence key = (CharSequence) k;
	outer: for (int i = 0; i < key.length(); ++i ) {
	    char c = key.charAt(i);
	    for(Edge child: node.getChildren()) {
		if (child.getLabel() == c) {
		    node = child.getTo();
		    continue outer;
		}
	    } 
	    
	    for (;i < key.length(); ++i) {
		Node newNode = new Node();
		Edge e = new Edge(key.charAt(i), newNode);
		node.addChild(e);
		node = newNode;
	    }
	}
	
	TrieEntry oldPayload = null;
	if (node.hasPayload()) { 
	    oldPayload = node.getPayload();
	} else {
	    nrElements += 1;
	}
	node.setPayload(new TrieEntry (k, value));
	return oldPayload == null ? null : oldPayload.getValue();
    }

    class TrieSet extends AbstractSet<Map.Entry<K, V>> {
	@Override 
	public int size() {
	    return nrElements;
	}

	class TrieSetIterator implements Iterator<Map.Entry<K, V>> {
	    class NodeIteration {
		public final Node node;
		public final Iterator<Edge> children;
		
		public NodeIteration(Node n) {
		    node = n;
		    children = node.getChildren().iterator();
		}
	    }
		
	    private Node savedNode = null;
	    private Stack<NodeIteration> stack;
	    {
		stack = new Stack<NodeIteration>();
		stack.push(new NodeIteration(root));
		gotoNext();
	    }
		
	    private void gotoNext() { 
		while(!stack.isEmpty()) {
		    NodeIteration cur = stack.peek();
		    while(cur.children.hasNext()) {
			Node next = cur.children.next().getTo();
			stack.push(new NodeIteration(next));
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
		Node cur = savedNode = stack.peek().node;
		Map.Entry<K, V> payload = cur.getPayload();
		gotoNext();
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
	for (Edge e: n.getChildren()) {
	    Node to = e.getTo();
	    if(to.hasPayload() && to.getPayload().equals(value)) return true;
	    if (containsValue(value, to)) return true;
	}
	return false;
    }
}
