package edu.brandeis.cs127.pa3;

/**
    Internal Nodes of B+-Trees.
    @author cs127b
 */
public class InternalNode extends Node{

	/**
       Construct an InternalNode object and initialize it with the parameters.
       @param d degree
       @param p0 the pointer at the left of the key
       @param k1 the key value
       @param p1 the pointer at the right of the key
       @param n the next node
       @param p the previous node
	 */
	public InternalNode (int d, Node p0, int k1, Node p1, Node n, Node p){

		super (d, n, p);
		ptrs [0] = p0;
		keys [1] = k1;
		ptrs [1] = p1;
		lastindex = 1;

		if (p0 != null) p0.setParent (new Reference (this, 0, false));
		if (p1 != null) p1.setParent (new Reference (this, 1, false));
	}

	/**
       The minimal number of keys this node should have.
       @return the minimal number of keys a leaf node should have.
	 */
	public int minkeys () {

		///////////////////
		// ADD CODE HERE //
		///////////////////
		//TODO re check this
		if (parentref == null){
			//this is a root node
			return 1;
		} else {
			return (int) Math.ceil((degree - 1) / 2.0);
		}
	}

	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
	 */
	public boolean combinable (Node other) {

		boolean combinable = true;

		///////////////////
		// ADD CODE HERE //
		///////////////////
		

		return combinable;
	}


	/**
       Combines contents of this node and its next sibling (next)
       into a single node,
	 */
	public void combine () {

		///////////////////
		// ADD CODE HERE //
		///////////////////
	}

	/**
       Redistributes keys and pointers in this node and its
       next sibling so that they have the same number of keys
       and pointers, or so that this node has one more key and
       one more pointer.  Returns the key that must be inserted
       into parent node.
       @return the value to be inserted to the parent node
	 */
	public int redistribute () {
		int key = 0;

		///////////////////
		// ADD CODE HERE // 
		///////////////////

		return key;

	}

	/**
       Inserts (val, ptr) pair into this node
       at keys [i] and ptrs [i].  Called when this
       node is not full.  Differs from {@link LeafNode} routine in
       that updates parent references of all ptrs from index i+1 on.
       @param val the value to insert
       @param ptr the pointer to insert 
       @param i the position to insert the value and pointer
	 */
	public void insertSimple (int val, Node ptr, int i) {

		////////////////////
		// ADD CODE HERE  //
		////////////////////
	}

	/**
       Deletes keys [i] and ptrs [i] from this node,
       without performing any combination or redistribution afterwards.
       Does so by shifting all keys and pointers from index i+1 on
       one position to the left.  Differs from {@link LeafNode} routine in
       that updates parent references of all ptrs from index i+1 on.
       @param i the index of the key to delete
	 */
	public void deleteSimple (int i) {

		///////////////////
		// ADD CODE HERE //
		///////////////////
	}


	/**
       Uses findPtrInex and calles itself recursively until find the value or pind the position 
       where the value should be.
       @return the referenene pointing to a leaf node.
	 */
	public Reference search (int val) {
		Reference ref = null;

		///////////////////
		// ADD CODE HERE //
		///////////////////
		Node referenced_node = ptrs[findPtrIndex(val)];
		ref = referenced_node.search(val);

		return ref;
	}

	/**
       Insert (val, ptr) into this node. Uses insertSimple, redistribute etc.
       Insert into parent recursively if necessary
       @param val the value to insert
       @param ptr the pointer to insert 
	 */
	public void insert (int val, Node ptr) {

		///////////////////
		// ADD CODE HERE //
		///////////////////

	}

	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "I" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j <= lastindex; j++) {
			if (j > 0) label += "|";
			label += "<p" + ptrs[j].myname + ">";
			if (j != lastindex) label += "|" + String.valueOf(keys[j+1]);
			// Write out any link now
			BTree.writeOut(myname + ":p" + ptrs[j].myname + " -> " + ptrs[j].myname + "\n");
			// Tell your child to output itself
			ptrs[j].outputForGraphviz();
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/**
       Print out the content of this node
	 */
	void printNode () {

		int j;
		System.out.print("[");
		for (j = 0; j <= lastindex; j++) {

			if (j == 0)
				System.out.print (" * ");
			else
				System.out.print(keys[j] + " * ");

			if (j == lastindex)
				System.out.print ("]");
		}
	}
}


