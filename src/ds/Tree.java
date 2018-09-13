// simple Tree data structure, designed for String labels
package ds;
import java.utils.LinkedList;
class TreeNode
{
    // attributes of a Tree Node
    private String label;
    private LinkedList<TreeNode> children;

    // default constructor
    public TreeNode()
    {
	this.label = "";
	this.children = new LinkedList<TreeNode>();
    }

    // constructor when label is given
    public TreeNode(String l)
    {
	this.label = l;
	this.children = new LinkedList<TreeNode>();
    }

    // getters
    public String getLabel()
    {
	return this.label;
    }
    public LinkedList<TreeNode> getChildren()
    {
	return this.children;
    }

    // setters
    public void setLabel(String l)
    {
	this.label = l;
    }
    public void setChildren(LinkedList<TreeNode> c)
    {
	this.children = c;
    }

    // check if a node is a leaf
    public boolean isLeaf()
    {
	return this.children.isEmpty();
    }

    // add a child given its label
    public void addChild(String l)
    {
	this.children.add(new TreeNode(l));
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
}

    
