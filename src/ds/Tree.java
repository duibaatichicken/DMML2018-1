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
    private HashMap children; // named branches

    // default constructor
    public TreeNode()
    {
	this.label = "";
	this.children = new HashMap();
    }

    // constructor when label is given
    public TreeNode(String l)
    {
	this.label = l;
	this.children = new HashMap();
    }

    // getters
    public String getLabel()
    {
	return this.label;
    }
    public HashMap getChildren()
    {
	return this.children;
    }

    // setters
    public void setLabel(String l)
    {
	this.label = l;
    }
    public void setChildren(HashMap c)
    {
	this.children = c;
    }

    // check if a node is a leaf
    public boolean isLeaf()
    {
	return this.children.isEmpty();
    }

    // add a child given its label
    public void addChild(String branch, String labl)
    {
	this.children.put(branch, new TreeNode(labl));
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

    // getter
    public TreeNode getRoot()
    {
	return this.root;
    }

    //setter
    public void setRoot(TreeNode n)
    {
	this.root = n;
    }

    // add a subtree at root
    public void addSubtree(String branch, Tree t)
    {
	this.root.getChildren().put(branch, t.getRoot());
    }
}

    
