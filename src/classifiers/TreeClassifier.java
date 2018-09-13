/* ID3 (Examples, Target_Attribute, Attributes)
   Create a root for the tree
   If all examples are positive, return the single-node tree Root, with label = +
   If all examples are negative, return the single-node tree Root, with label = -
   If number of predictive attributes is empty, then return single-node tree Root,
       with label = most common value of target attribute in the examples
   Otherwise Begin
       A <- The attribute for the best classification (entropy)
       Decision Tree attribute for Root = A
       For each possible value v_i for A,
           Add a new branch below root, corresponding to A = v_i
	   Examples(v_i) be the subset of examples that have v_i as value of A
	   If Examples(v_i) is empty
	       below corresponding branch add a leaf node
	       with label = most common value of target attribute in examples
	   Else below this branch add a new subtree ID3 (Examples(v_i), Target_Attribute, Attributes\setdiff{A})
    End
    Return Root
*/
import java.io.*;
import ds.Tree;
import java.util.LinkedList;

public class TreeClassifier
{
    static int target_attribute = 42; // 43 columns. 42 attributes and 1 class.
    public static void main(String args[]) throws IOException
    {
	/*
	  Examples : 0,1-array of line numbers
	  Target Attribute : index
	  Attributes : array of indices
	 */
	/*
	  BIG TODO BIG TODO BIG TODO
	  PANDA LISTEN
	  Currently examples and attributes are 0-1 indicator arrays. WE CANNOT ACTUALLY DO THAT.
	  Working on using a dynamically sized DS, and store only required indices, or better still ranges of indices.
	 */
	int[] temp = {0, 67557};
	LinkedList<int[]> examples = (new LinkedList<int[]>()).add(temp);
	temp = {0, 42};
	LinkedList<int[]> attributes = (new LinkedList<int[]>()).add(temp);
	(new TreeClassifier()).ID3(examples, attributes); // calls ID3 with Examples = whole set and all attributes
    }

    private Tree ID3(int examples[], int attributes[]) throws IOException, IndexOutOfBoundsException //target_attribute is the same throughout
    {
	BufferedReader br = new BufferedReader (new FileReader ("../data/connect-4-full.data")); // file reader
	String line = ""; // temporary variable for reading
	int row_number = 0; // keeps track of which example we are at
	int exampleIndex = examples[0][0]; // will run through examples list, starts from first good one
	int attributeIndex = attributes[0][0]; // will run through attribute list, starts from first good one
	String[] row = new String[43]; // 43 columns in each row of data
	int[] range = {0,0};
	// CASE 1 : ALL WINS OR ALL LOSSES OR ALL DRAWS
	String target_tracker[] = {"",""}; // tracks if examples are all of same class
	int majority_tracker[] = range; // tracks majority class in given example, stores {win-loss, win-draw}
	while((line = br.readLine()) != null)
	{
	    // to see only elements that are in current subset
	    if (row_number >= examples[exampleIndex][1]) // outside current range
		exampleIndex++;
	    try
	    {
		if (row_number++ < examples[exampleIndex][0]) // also outside next range
		    continue; // discard
	    }
	    catch (IndexOutOfBoundsException e) // no more examples left
	    {
		line = null; // using line itself as a flag for not seeing a class change
		break;
	    }
	    row = line.split(",");
	    // updating majority
	    if (row[42].equals("win"))
	    {
		majority_tracker[0]++;
		majority_tracker[1]++;
	    }
	    else if (row[42].equals("loss")
	        majority_tracker[0]--;
	    else
		majority_tracker[1]--;
	     
	    // if there are different classes, there are consecutive rows with different classes. we detect the first such.
	    target_tracker[0] = target_tracker[1]; // keeps track of current class and last seen class
	    target_tracker[1] = row[42];
	    if (target_tracker[0] == target_tracker[1]) // a change of class breaks this case
		break;
	}	     
	if (line == null) //so I know it didn't break due to class change
	    return (new Tree(target_tracker[1])); // lonely root with label of class

	// CASE 2 : EMPTY ATTRIBUTE LIST
	if (attributes.equals({[0,0]})) // if there are no attributes
	{
	    if (line == null) // if I was done reading the data. should I check row number == 67557 instead?
		return (new Tree(majority_tracker[0] > 0 && majority_tracker[1] > 0? "win" : (majority_tracker[0] < majority_tracker[1] ? "loss" : "draw"))); //single node with majority
	    else // I should read the rest of the data and calculate majority
	    {
		while((line = br.readLine()) != null)
		{
		    // to see only elements that are in current subset
		    if (row_number >= examples[exampleIndex][1]) // outside current range
			exampleIndex++;
		    try
		    {
			if (row_number++ < examples[exampleIndex][0]) // also outside next range
			    continue; // discard
		    }
		    catch (IndexOutOfBoundsException e) // no more examples left
		    {
			break;
		    }

		    // majority update
		    if (row[42].equals("win"))
		    {
			majority_tracker[0]++;
			majority_tracker[1]++;
		    }
		    else if (row[42].equals("loss")
			majority_tracker[0]--;
		    else
			majority_tracker[1]--;
	       }
	       return (new Tree(majority_tracker[0] > 0 && majority_tracker[1] > 0? "win" : (majority_tracker[0] < majority_tracker[1] ? "loss" : "draw"))); //single node with majority
	    }
	}
	    
	br.close();

	// CASE 3 : NONE OF THE BASE CASES CAME UP
	int bestAttribute = (new TreeClassifier()).bestAttribute(examples, attributes);// pick best attribute A according to Shannon entropy
	BufferedReader in = new BufferedReader (new FileReader ("../data/connect-4-full.data"));
	// for each possible value v_i, build Examples(v_i)
	    // CASE v_i.1 empty. then add majority leaf node
	    // CASE v_i.2 recurse

	return (new Tree()); //dummy return statement, will remove later
    }

    private int bestAttribute(LinkedList<int[]> examples, LinkedList<int[]> attributes) throws IOException, IndexOutOfBoundsException
    {
	String[] row = new String[43]; // I row of data
	BufferedReader br;
	String line = "";
	int attributeIndex = attributes[0][0]; // runs over attributes, starts at first good one
        int exampleIndex = examples[0][0]; //similarly for examples
	int rowNumber = 0;
	LinkedList<int[]> bExamples = new LinkedList<int[]>();
	LinkedList<int[]> oExamples = new LinkedList<int[]>();
	LinkedList<int[]> xExamples = new LinkedList<int[]>(); // these will hold A=v_i subsets
	int minAttribute = -1; // best Attribute
	double shannon = 1, tempshannon = 1;
	for (int currentAttribute = 0; currentAttribute < 42 ; currentAttribute++)
	{
	    br = new BufferedReader (new FileReader ("../data/connect-4-full.data");
	    exampleIndex = examples[0][0]; //reset top of file
	    bExamples.clear();
	    oExamples.clear();
	    xExamples.clear(); // clean out the lists from previous iteration
	    // to see only the indicated subset of attributes
	    if(currentAttribute >= attributes[attributeIndex][1])// beyond current range
		attributeIndex++;
	    try
	    {
		if(currentAttribute < attributes[attributeIndex][0]) // too low for next range
		    continue;
	    }
	    catch (IndexOutOfBoundsException e)
	    {
		line = null; // again this is a flag for attributes running out. don't know if needed.
		break;
	    }
	    while ((line = br.readLine()) != null)
	    {
		// to see only elements that are in current subset of data
		if (rowNumber >= examples[exampleIndex][1]) // outside current range
		    exampleIndex++;
		try
		{
		    if (rowNumber++ < examples[exampleIndex][0]) // also outside next range
		    continue; // discard
		}
		catch (IndexOutOfBoundsException e) // no more examples left
		{
		    line = null; // using line itself as a flag for not seeing a class change
		    break;
		}
		row = line.split(",");
		if (row[currentAttribute].equals("b"))
		    // add current example to bExamples
		else if (row[currentAttribute].equals("o"))
		    // add current example to oExamples
		else
		    // add current example to bExamples

	    }
	    br.close();
	// compute Shannon entropies
	  // tempShannon = Shannon(b) + Shannon(o) + Shannon (x)
	  // if tempShannon < Shannon, Shannon = tempShannon; minAttribute = currentAttribute; 
	}
	return minAttributes;
    }
    private double shannonEntropy(LinkedList examples)
    {
	// implement, or use external package
	return 0; // dummy return statement, will modify later
    }
}

