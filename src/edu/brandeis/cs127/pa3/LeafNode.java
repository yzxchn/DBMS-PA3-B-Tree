package edu.brandeis.cs127.pa3;

/**
   LeafNodes of B+ trees
 */
public class LeafNode extends Node {

	/**
       Construct a LeafNode object and initialize it with the parameters.
       @param d the degree of the leafnode
       @param k the first key value of the node
       @param n the next node 
       @param p the previous node
	 */
	public LeafNode (int d, int k, Node n, Node p){
		super (d, n, p);
		keys [1] = k;
		lastindex = 1;
	}      


	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "L" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j < lastindex; j++) {
			if (j > 0) label += "|";
			label += String.valueOf(keys[j+1]);
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/** 
	the minimum number of keys the leafnode should have.
	 */
	public int minkeys () {
		
		///////////////////
		// ADD CODE HERE //
		///////////////////

		return (int) Math.ceil((degree - 1) / 2.0);
	}

	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
       @return true if this node can be combined with other; otherwise false.
	 */
	public boolean combinable (Node other){
		// ADD CODE HERE
		return this.getLast() + other.getLast() < degree - 1;
	}

	/**
       Combines contents of this node and its next sibling (nextsib)
       into a single node
	 */
	public void combine (){

		///////////////////
		// ADD CODE HERE //
		///////////////////
	}

	/**
       Redistributes keys and pointers in this node and its
       next sibling so that they have the same number of keys
       and pointers, or so that this node has one more key and
       one more pointer,.  
       @return int Returns key that must be inserted
       into parent node.
	 */
	public int redistribute (){  
		int key = 0;

		///////////////////
		// ADD CODE HERE //
		///////////////////

		return key;
	}

	/**
       Insert val into this node at keys [i].  (Ignores ptr) Called when this
       node is not full.
       @param val the value to insert to current node
       @param ptr not used now, use null when call this method 
       @param i the index where this value should be
	 */
	public void insertSimple (int val, Node ptr, int i){

		///////////////////
		// ADD CODE HERE //
		///////////////////
		// when val is greater than all the values in keys
		if (i == lastindex && val > keys[lastindex]){
			keys[lastindex+1] = val;	
		} else {
			int insert_index = i;
			int insert_val = val;
			// insert into array and shift the values to the right forward.
			while (insert_index <= lastindex){
				int temp = keys[insert_index];
				keys[insert_index] = insert_val;
				insert_val = temp;
				insert_index++;
			}
			keys[insert_index] = insert_val;
		}
		lastindex++;
	}


	/**
       Deletes keys [i] and ptrs [i] from this node,
       without performing any combination or redistribution afterwards.
       Does so by shifting all keys from index i+1 on
       one position to the left.  
	 */
	public void deleteSimple (int i){

		///////////////////
		// ADD CODE HERE //
		///////////////////	
	} 

	/**
       Uses findKeyIndex, and if val is found, returns the reference with match set to true, otherwise returns
       the reference with match set to false.
       @return a Reference object referring to this node. 
	 */
	public Reference search (int val){
		Reference ref = null;
		boolean found = false;

		///////////////////
		// ADD CODE HERE //
		///////////////////
		int foundIndex = findKeyIndex(val);
		if (keys[foundIndex] == val){
			found = true;
		}
		ref = new Reference(this, foundIndex, found);
		

		return ref;
	}

	/**
       Insert val into this, creating split
       and recursive insert into parent if necessary
       Note that ptr is ignored.
       @param val the value to insert
       @param ptr (not used now, use null when calling this method)
	 */
	public void insert (int val, Node ptr){

		///////////////////
		// ADD CODE HERE //
		///////////////////
		//TODO insertSimple doesn't replace the default 0 value.
		if (!full()){
			insertSimple(val, null, findKeyIndex(val));
		} else {
			//when splitting is needed
			// create a new key array
			int lhs_size = (int) Math.ceil((lastindex + 1)/2.0);
			int lhs_count = 0;
			int old_index = 1;
			int new_index = 1;
			//whether the new inserted value has been added to either of the split nodes.
			boolean added_new_val = false;
			int[] new_keys = new int[degree];
			// store the old key array
			int[] old_keys = keys;
			// use the new array as this node's key array
			keys = new_keys;
			int old_lastindex = lastindex;
			lastindex = 0;
			while (lhs_count < lhs_size){
				if (!added_new_val && val < old_keys[old_index]){
					insertSimple(val, null, new_index);
					added_new_val = true;
				} else {
					insertSimple(old_keys[old_index], null, new_index);
					old_index++;
				}
				lhs_count++;
				new_index++;
			}
			
			LeafNode newRightNode = null;
			// create a new node to the right of current node
			if (!added_new_val && val < old_keys[old_index]){
				newRightNode = new LeafNode(degree, val, this.next, this);
				added_new_val = true;
			} else {
				newRightNode = new LeafNode(degree, old_keys[old_index], this.next, this);
				old_index++;
			}
			
			// reset new_index
			new_index = 1;
			while (old_index <= old_lastindex){
				if (!added_new_val && val < old_keys[old_index]){
					newRightNode.insertSimple(val, null, new_index);
					added_new_val = true;
				} else {
					newRightNode.insertSimple(old_keys[old_index], null, new_index);
					old_index++;
				}
				new_index++;
			}
			// add the new value, or the last key value in the old key array, whichever is lesser, to the new node
			if (!added_new_val){
				newRightNode.insertSimple(val, null, new_index);
				new_index++;
			}
			// propagate up the tree
			Reference pref = getParent();
			// if leafnode has no parent, create a new root
			if (pref == null){
				Node parent = new InternalNode(degree, this, newRightNode.getKey(1), newRightNode, null, null);
				this.setParent(new Reference(parent, 0, false));
				newRightNode.setParent(new Reference(parent, 1, false));
			} else {
				parentref.getNode().insert(newRightNode.getKey(1), newRightNode);
			}
		}
	}


	/**
       Print to stdout the content of this node
	 */
	void printNode (){
		System.out.print ("[");
		for (int i = 1; i < lastindex; i++) 
			System.out.print (keys[i]+" ");
		System.out.print (keys[lastindex] + "]");
	}
}
