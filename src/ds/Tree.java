/* Tree Data Structure
 * unbounded branching
 * named branches (String)
 * laballed nodes (String)
 */

package ds;
import java.util.HashMap;

class TreeNode
{
    // attributes of a Tree Node
    private String label;
    private HashMap<String, Tree> children; // (branch name, pointer root of subtree)

    // default constructor
    public TreeNode()
    {
	this.label = "";
	this.children = new HashMap<String,Tree>();
    }

    // constructor when label is given
    public TreeNode(String l)
    {
	this.label = l;
	this.children = new HashMap<String,Tree>();
    }

    // getters
    public String getLabel()
    {
	return this.label;
    }
    public HashMap<String,Tree> getChildren()
    {
	return this.children;
    }

    // setters
    public void setLabel(String l)
    {
	this.label = l;
    }
    public void setChildren(HashMap<String,Tree> c)
    {
	this.children = c;
    }

    // check if a node is a leaf i.e. no children
    public boolean isLeaf()
    {
	return this.children.isEmpty();
    }

    // add a child given its branch name
    public void addChild(String branch, String labl)
    {
	this.addSubtree(branch, new Tree(labl));
    }

    // add a subtree given its branch name
    public void addSubtree(String branch, Tree t)
    {
	this.children.put(branch, t);
    }
    
}

/* class to implement Tree with required functionality */

public class Tree
{
    //attributes of a Tree
    private TreeNode root;

    //default constructor
    public Tree()
    {
	this.root = new TreeNode();
    }

    // constructor when root label is given
    public Tree(String l)
    {
	this.root = new TreeNode(l);
    }
    /* GETTERS BEGIN */
    // root getter
    public TreeNode getRoot()
    {
	return this.root;
    }

    // subtree getter of specified name. null if no such branch
    public Tree getSubtree(String branch)
    {
	return (this.root.getChildren()).get(branch);
    }

    // getter of label of root
    public String getRootLabel()
    {
	return this.root.getLabel();
    }
    
    /* GETTERS END */
    /*SETTERS BEGIN*/
    //root setter
    public void setRoot(TreeNode n)
    {
	this.root = n;
    }

    // setter of root label
    public void setRootLabel(String l)
    {
	this.root.setLabel(l);
    }
    /*SETTERS END*/
    // add a subtree at root
    public void addSubtree(String branch, Tree t)
    {
	this.root.addSubtree(branch, t);
    }

    // check if it is a singleton leaf
    public boolean isLeaf()
    {
	return this.root.isLeaf();
    }

    //check if it is an empty tree
    public boolean isEmpty()
    {
	return this.root.getLabel().equals(null);
    }

}

    
