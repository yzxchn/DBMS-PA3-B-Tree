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
	
	public boolean underfull(){
		return lastindex < minkeys();
	}


	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
       @return true if this node can be combined with other; otherwise false.
	 */
	public boolean combinable (Node other){
		// ADD CODE HERE
		return this.getLast() + other.getLast() < degree;
	}

	/**
       Combines contents of this node and its next sibling (nextsib)
       into a single node
	 */
	public void combine (){

		///////////////////
		// ADD CODE HERE //
		///////////////////
		for (int i=1, j=lastindex+1; i<=next.getLast(); i++, j++){
			keys[j] = next.getKey(i);
			lastindex++;
		}
		Reference nextParent = next.getParent();
		// delete the ptr to the sibling node being merged into this node
		nextParent.getNode().delete(nextParent.getIndex());
		setNext(next.getNext());
		if (next != null){
			next.setPrev(this);
		}
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
		int requiredKeys = (int) Math.ceil((lastindex + next.getLast())/2.0);
		int keysToMove = requiredKeys - lastindex;
		if (keysToMove > 0){
			for (int i=1; i<=keysToMove; i++){
				int k = next.getKey(1);
				next.delete(1);
				this.insert(k, null);
			}
		} else {
			for (int i=-1; i>=keysToMove; i--){
				int k = this.getKey(lastindex);
				this.delete(lastindex);
				next.insert(k, null);
			}
		}
		
		return next.getKey(1);
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
		// construct a new key array, without inserting the key to be deleted
		int[] newKeys = new int[degree];
		for (int j=1, h=1; j<=lastindex;j++){
			if (j != i){
				newKeys[h] = keys[j];
				h++;
			}
		}
		this.keys = newKeys;
		lastindex--;
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
		if (!full()){
			insertSimple(val, null, findKeyIndex(val));
		} else {
			//when splitting is needed
			// create a new key array
			int lhs_size = (int) Math.ceil((lastindex + 1)/2.0);
			int old_index = 1;
			boolean addedNewVal = false;
			// create an array as the keys for the super node
			int[] superNodeKeys = new int[degree];
			for (int i=0;i<superNodeKeys.length;i++){
				if (old_index > lastindex || (!addedNewVal && val < keys[old_index])){
					superNodeKeys[i] = val;
					addedNewVal = true;
				} else {
					superNodeKeys[i] = keys[old_index];
					old_index++;
				}
			}
			//create a new node to the right of this leaf node
			LeafNode newRightNode = new LeafNode(degree, 0, this.next, this);
			// split the array of super node keys into two
			int[] newKeysOnLeft = new int[degree];
			int[] newKeysOnRight = new int[degree];
			for (int i=0, li=1, ri=1;i<superNodeKeys.length;i++){
				if (i < lhs_size){
					newKeysOnLeft[li] = superNodeKeys[i];
					lastindex = li;
					li++;
				} else {
					newKeysOnRight[ri] = superNodeKeys[i];
					newRightNode.lastindex = ri;
					ri++;
				}
			}
			this.keys = newKeysOnLeft;
			newRightNode.keys = newKeysOnRight;
			// add the pointer to the new right node to the parent node
			Reference pref = getParent();
			// if leaf node has no parent, create a new root
			if (pref == null){
				Node parent = new InternalNode(degree, this, newRightNode.getKey(1), newRightNode, null, null);
			} else {
				Node parent = parentref.getNode();
				parent.insert(newRightNode.getKey(1), newRightNode);
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
