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
import DMML1DS.BTree;
import java.util.Arrays;

public class Connect4Classifier
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
	int examples[] = new int[67557];
	Arrays.fill(examples, 1); // full dataset
	int attributes[] = new int[42];
	Arrays.fill(attributes,1); // all attributes
	(new Connect4Classifier()).ID3(examples, attributes); // calls ID3 with Examples = whole set and all attributes
    }

    private BTree ID3(int examples[], int attributes[]) throws IOException //target_attribute is the same throughout
    {
	BufferedReader br = new BufferedReader (new FileReader ("connect-4.data")); // file reader
	String line = ""; // temporary variable for reading
	int row_number = 0; // keeps track of which example we are at
	
	String[] row = new String[43]; // 43 columns in each row of data

	// CASE 1 : ALL WINS OR ALL LOSSES
	String target_tracker[] = {"",""}; // tracks if examples are all of same class
	int majority_tracker = 0; // tracks majority class in given example
	while((line = br.readLine()) != null)
	{
	    if (examples[++row_number] == 0) // discards rows not in current example subset
		continue;
	    row = line.split(",");
	    majority_tracker = row[42].equals("win") ? majority_tracker+1 : majority_tracker-1; // majority > 0 => more wins
	    // if there are different classes, there are consecutive rows with different classes. we detect the first such.
	    target_tracker[0] = target_tracker[1]; // keeps track of current class and last seen class
	    target_tracker[1] = row[42];
	    if (target_tracker[0] == target_tracker[1]) // a change of class breaks this case
		break;
	}
	if (line == null) //so I know it didn't break due to class change
	    return (new BTree(target_tracker[1])); // lonely root with label of class

	// CASE 2 : EMPTY ATTRIBUTE LIST
	//check if no attributes
	int temp = 0;
	for (int i = 0; i < 42; i++)
	{
	    temp+=attributes[i];
	    if (temp > 0)
	    {
		temp = -1;
		break;
	    }
	}
	if (temp != -1) // if there are no attributes
	{
	    if (line == null) // if I was done reading the data. should I check row number == 67557 instead?
		return (new BTree(majority_tracker > 0 ? "win" : "loss")); //single node with majority
	    else // I should read the rest of the data and calculate majority
		while((line = br.readLine()) != null)
		{
		    if (examples[++row_number] == 0) //discards complement
			continue;
		    row = line.split(","); // this line isn't really required for this part. just for consistency and in case we need row later.
		    majority_tracker = row[42].equals("win") ? majority_tracker+1 : majority_tracker-1; // majority > 0 => more wins
	        }
	    return (new BTree(majority_tracker > 0 ? "win" : "loss")); //single node with majority
	}

	// CASE 3 : NONE OF THE BASE CASES CAME UP
	// pick best attribute A according to Shannon entropy
	// for each possible value v_i, build Examples(v_i)
	    // CASE v_i.1 empty. then add majority leaf node
	    // CASE v_i.2 recurse

	return (new BTree()); //dummy return statement, will remove later
    }

    private double shannonEntropy(int[] examples)
    {
	// implement, or use external package
	return 0; // dummy return statement, will modify later
    }
}
