package ds;

class BinaryTreeNode {
	
	// Variables
	public String label;
	public BinaryTreeNode parent;
	public BinaryTreeNode left, right;
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public BinaryTreeNode(String s) {
		this.label = s;
		this.parent = null;
		this.left = null;
		this.right = null;
	}
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public BinaryTreeNode() {
		this("");
	}
	
	/************************* *************************/
	
	/**
	 * @description Getter functions
	 */
	public String getLabel() { return this.label; }
	public BinaryTreeNode getParent() { return this.parent; }
	public BinaryTreeNode getLeft() { return this.left; }
	public BinaryTreeNode getRight() { return this.right; }
	
	/************************* *************************/
	
	/**
	 * @description Setter functions
	 */
	public void setLabel(String s) { this.label = s; }
	public void setParent(BinaryTreeNode b) { this.parent = b; }
	public void setLeft(BinaryTreeNode b) { this.left = b; }
	public void setRight(BinaryTreeNode b) { this.right = b; }
}

/**
 * Class to store and perform operations on a binary tree.
 * 
 */
public class BinaryTree {
	
	public BinaryTreeNode root;
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public BinaryTree(String s) {
		this.root = new BinaryTreeNode(s);
	}
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public BinaryTree() {
		this.root = null;
	}
	
	/************************* *************************/
	
	/**
	 * @description Returns whether the binary tree is empty.
	 */
	public boolean isEmpty() {
		return (root == null);
	}
}
