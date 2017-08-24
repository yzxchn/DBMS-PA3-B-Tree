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
	
	public int minPtrs() {
		if (parentref == null){
			return 1;
		} else {
			return (int) Math.ceil(degree / 2.0);
		}
	}
	
	public boolean underfull(){
		return lastindex + 1 < minPtrs();
	}

	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
	 */
	public boolean combinable (Node other) {
		///////////////////
		// ADD CODE HERE //
		///////////////////
		// Plus 1 because demoting the excluding key from parent node
		return this.getLast() + other.getLast() + 1 < degree;
	}


	/**
       Combines contents of this node and its next sibling (next)
       into a single node,
	 */
	public void combine () {

		///////////////////
		// ADD CODE HERE //
		///////////////////
		Reference nextParent = next.getParent();
		int midKey = nextParent.getNode().getKey(nextParent.getIndex());
		this.insert(midKey, next.getPtr(0));
		for (int i=1; i<=next.getLast(); i++){
			this.insert(next.getKey(i), next.getPtr(i));
		}
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
       one more pointer.  Returns the key that must be inserted
       into parent node.
       @return the value to be inserted to the parent node
	 */
	public int redistribute () {

		///////////////////
		// ADD CODE HERE // 
		///////////////////
		int requiredKeys = (int) Math.ceil((lastindex + next.getLast())/2.0);
		int keysToMove = requiredKeys - lastindex;
		int keyToInsert = parentref.getNode().getKey(next.getParent().getIndex());
		if (keysToMove > 0){
			for (int i=1; i<=keysToMove; i++){
				this.insertSimple(keyToInsert, next.getPtr(0), lastindex);
				//parentref.getNode().insertSimple(next.getKey(1), next, next.getParent().getIndex());
				keyToInsert = next.getKey(1);
				next.shiftleft(1);
			}
		} else {
			for (int i=-1; i>=keysToMove; i--){
				// the key to be moved up to the parent
				keyToInsert = keys[lastindex];
				// the ptr to be put the the leftmost position in the next sibling.
				Node lastPtr = ptrs[lastindex];
				deleteSimple(lastindex);
				// the key to be moved down from the parent
				int k = parentref.getNode().getKey(next.getParent().getIndex());
				InternalNode nextSibling = (InternalNode) next;
				// putting the demoted parent key and the last pointer of this node into the next sibling
				nextSibling.insertAtBeginning(k, lastPtr);
			}
		}
		
		return keyToInsert;
	}
	
	/**
	 * Insert a key  and ptr to the very beginning of the node, 
	 * and shift the remaining keys and pointer to the right.
	 * Different from insertSimple since the inserted ptr is 
	 * to the left of the inserted key.
	 * 
	 * @param key The key to be inserted
	 * @param leftPtr The pointer inserted to the left of key
	 */
	public void insertAtBeginning(int key, Node leftPtr){
		int insertKey = key;
		Node insertPtr = leftPtr;
		Node tempPtr = ptrs[0];
		// insert the pointer at the beginning
		ptrs[0] = insertPtr;
	    insertPtr = tempPtr;
	    // reset the inserted ptr's parent reference
	    ptrs[0].setParent(new Reference(this, 0, false));
	    // shift the remaining keys and ptrs to the right
	    for (int insertIndex=1; insertIndex <= lastindex+1; insertIndex++){
	    	int tempKey = keys[insertIndex];
	    	tempPtr = ptrs[insertIndex];
	    	keys[insertIndex] = insertKey;
	    	ptrs[insertIndex] = insertPtr;
	    	ptrs[insertIndex].getParent().increaseIndex();
	    	insertKey = tempKey;
	    	insertPtr = tempPtr;
	    }
	    lastindex++;
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
		if ((i == lastindex && val > keys[lastindex]) || lastindex == 0){
			keys[lastindex+1] = val;
			ptrs[lastindex+1] = ptr;
			lastindex++;
			// i+1 since this key would be inserted after the last key
			ptr.setParent(new Reference(this, lastindex, false));
		} else {
			int insert_index = i;
			int insert_val = val;
			Node insert_ptr = ptr;
			// insert into array and shift the values on the right forward by one cell.
			int temp = keys[insert_index];
			Node tempPtr = ptrs[insert_index];
			keys[insert_index] = insert_val;
			ptrs[insert_index] = insert_ptr;
			insert_ptr.setParent(new Reference(this, insert_index, false));
			insert_val = temp;
			insert_ptr = tempPtr;
			insert_index++;
			while (insert_index <= lastindex){
				temp = keys[insert_index];
				tempPtr = ptrs[insert_index];
				keys[insert_index] = insert_val;
				ptrs[insert_index] = insert_ptr;
				// increase the index value in the parent reference of the node pointed by this ptr
				insert_ptr.getParent().increaseIndex();
				insert_val = temp;
				insert_ptr = tempPtr;
				insert_index++;
			}
			keys[insert_index] = insert_val;
			ptrs[insert_index] = insert_ptr;
			insert_ptr.getParent().increaseIndex();
			lastindex++;
		}
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
		int[] newKeys = new int[degree];
		Node[] newPtrs = new Node[degree];
		newPtrs[0] = ptrs[0];
		for (int j=1, h=1; j<=lastindex;j++){
			if (j != i){
				newKeys[h] = keys[j];
				newPtrs[h] = ptrs[j];
				// update parent references of ptrs from i+1 on
				if (j > i){
					newPtrs[h].getParent().decreaseIndex();
				}
				h++;
			}
		}
		this.keys = newKeys;
		this.ptrs = newPtrs;
		lastindex--;
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
		if (!full()){
			insertSimple(val, ptr, findKeyIndex(val));
		} else {
			//create "super node"
			int[] superKeys = new int[degree+1];
			Node[] superPtrs = new Node[degree+1];
			superPtrs[0] = ptrs[0];
			int old_index = 1;
			boolean addedNewVal = false;
			//put keys and pointers in the old node and the added key and pointer in the "super node" in the
			//correct order.
			for (int i=1;i<degree+1;i++){
				if (old_index > lastindex || (!addedNewVal && val < keys[old_index])){
					superKeys[i] = val;
					superPtrs[i] = ptr;
					addedNewVal = true;
				} else {
					superKeys[i] = keys[old_index];
					superPtrs[i] = ptrs[old_index];
					old_index++;
				}
			}
			//split the super node
			int midIndex = (int) Math.ceil((degree+1)/2.0);
			int midKey = superKeys[midIndex];
			//create the new node on the left
			Node[] newPtrsL = new Node[degree];
			int[] newKeysL = new int[degree];
			newPtrsL[0] = superPtrs[0];
			//reset the index in the parent reference stored in the node pointed to
			newPtrsL[0].getParent().setIndex(0);
			for (int i=1; i<midIndex; i++){
				newKeysL[i] = superKeys[i];
				newPtrsL[i] = superPtrs[i];
				newPtrsL[i].setParent(new Reference(this, i, false));
				lastindex = i;
			}
			this.keys = newKeysL;
			this.ptrs = newPtrsL;
			
			//create the new node on the right
			InternalNode newNodeR = new InternalNode(degree, superPtrs[midIndex], superKeys[midIndex+1], superPtrs[midIndex+1], this.next, this);
			for (int i=midIndex+2; i<superKeys.length;i++){
				newNodeR.insert(superKeys[i], superPtrs[i]);
			}
			
			// add the pointer to the new right node to the parent node
			Reference pref = getParent();
		    // if leaf node has no parent, create a new root
			if (pref == null){
			    Node parent = new InternalNode(degree, this, midKey, newNodeR, null, null);
		    } else {
				Node parent = parentref.getNode();
				parent.insert(midKey, newNodeR);
			}
		}
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


