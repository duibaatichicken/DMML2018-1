package DMML1DS;

class BNode // define nodes with <=2 children
{
    String label;
    BNode left, right;

    // ctors
    public BNode(String l)
    {
	left = right = null;
	label = l;
    }
    public BNode()
    {
	left = right = null;
	label = "";
    }

    // get methods
    public BNode getLeft()
    {
	return left;
    }
    public BNode getRight()
    {
        return right;
    }
    public String getLabel()
    {
	return label;
    }
    
    // set methods
    public void setLeft(BNode node)
    {
	left = node;
    }
    public void setRight(BNode node)
    {
	right = node;
    }
    public void setLabel(String l)
    {
	label = l;
    }
}

public class BTree
{
    private BNode root;

    public BTree()
    {
	root = null;
    }

    public BTree(String l)
    {
	root = new BNode(l);
    }
    public boolean isEmpty()
    {
	return root == null;
    }
}
