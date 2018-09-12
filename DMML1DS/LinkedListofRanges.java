class RNode
{
    private int[] contents = new int[2]; //stores a range, bottom incl. top excl.
    private RNode next;
    private RNode prev;

    public RNode()
    {
	contents = [0,67557];
	next = prev = null;
    }

    public RNode(int[] r)
    {
	contents = r;
	next = prev = null;
