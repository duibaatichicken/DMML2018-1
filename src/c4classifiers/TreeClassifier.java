package c4classifiers;

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

/* BIG TODO PANDA LISTEN (details inline)
# | ISSUE | LINE NOS. (fuzzy) |
----------------------------------
2 | please review my reuse of reader objects | 230, 365, 440 |
3 | perhaps need better emptiness check for DataSubset, including case {[r,r)} | 310 |
 */

import java.io.BufferedReader;
import java.io.FileReader;
import ds.DataSubset;
import ds.Interval;
import ds.Tree;
import java.util.Iterator;

/* A classifier for connect4 dataset
 * using ID3 decision tree algorithm
 */

public class TreeClassifier
{
    private DataSubset[][] subsetsByAttribute;
    /**
     * subsetsByAttribute :
     * 42 x 3 array of DataSubsets
     * 42 attributes x 3 possible values
     * convention for values :
     * 0 - b
     * 1 - o
     * 2 - x
     * subsetsByAttribute[i][j] is the DataSubset such that attribute_i is identically j
     * EXAMPLE
     * subsetsByAttribute[17][1] is subset of data where position 17 was taken by player o
     */
    private DataSubset[] subsetsByClass;
    /**
     * subsetsByClass :
     * 3 array pf DataSubsets
     * 3 classes
     * convention for classes
     * 0 - draw
     * 1 - win
     * 2 - loss
     */
    private Tree classifierTree;

    private static int splitNumber = i;
    if (splitNumber < 1 
    String s = i == 10 ? 
    private static String TRAINING_DATA_SOURCE = "src/data/connect-4-cv_training_data-10-01.data";
    private static String TESTING_DATA_SOURCE = "src/data/connect-4-cv_test_data-10-01.data"; 
    private static int ATTRIBUTE_SIZE = 42;
    private static int TRAINING_DATA_LENGTH = 6755;
    
    /************************** ***************************/
    
    /**
     * Constructor
     */
    public TreeClassifier()
    {
	
	this.subsetsByAttribute = new subsetsByAttribute[ATTRIBUTE_SIZE][3];
	this.subsetsByClass = new subsetsByClass[3];

	// initializes each element of subsetsByAttribute to an empty DataSubset
	for (int i = 0 ; i < ATTRIBUTE_SIZE ; i++)
	{
	    for (int j = 0 ; j < 3 ; j++)
	    {
		this.subsetsByAttribute[i][j] = new DataSubset();
	    }
	}
	
	this.classifierTree = new Tree();
    }
    
    /************************* *************************/   
    
    /**
     * @throws IOException
     * @description Computes two arrays
     * subsetsByAttribute indexed by Attributes(42) x Positions(3)
     * Such that the [i][j]th element is the DataSubset [A_i = p_j]
     * subsetsByClass indexed by Outcomes(3)
     * Such that the [i]th element is the DataSubset [Class = c_i]
     */
    private void computeSubsets() throws IOException
    {
	BufferedReader br = new BufferedReader (new FileReader (TRAINING_DATA_SOURCE));
	String currLine = "";
	int lineCount = 0;
	String[] row = new String[ATTRIBUTE_SIZE];
	while((currLine = br.readLine()) != null)
	{
	    row = currLine.split(",");
	    
	    if (row[ATTRIBUTE_SIZE].equals("draw"))
		subsetsByClass[0].addValue(lineCount);
	    else if (row[ATTRIBUTE_SIZE].equals("win"))
		subsetsByClass[1].addValue(lineCount);
	    else if (row[ATTRIBUTE_SIZE].equals("loss"))
		subsetsByClass[2].addValue(lineCount);
	    for (int i = 1 ; i < ATTRIBUTE_SIZE ; i++)
	    {
		if (row[i].equals("b"))
		    subsetsByAttribute[i][0].addValue(lineCount++);
		else if (row[i].equals("o"))
		    subsetsByAttribute[i][1].addValue(lineCount++);
		else if (row[i].equals("W"))
		    subsetsByAttribute[i][2].addValue(lineCount++);
		else
		    throw(new RunTimeException("Invalid data!"));
	    }
	}
    }

    /************************* *************************/   
    
    /**
     * @description Builds ID3 decision tree based on 
     * subsets constructed from data
     * takes as input the rows and columns currently under consideration
     */
    public Tree makeTree(DataSubset[][] examples, DataSubset[] attributes)
    {
	Tree decisionTree = new Tree(); // decision tree for this level of recursion

	/* BASE CASE 1
	 * all nodes are of same class C
	 * return leaf node labelled C
	 */
	DataSubset[] classArrayNow = new DataSubset[3];
	for (int cl = 0; cl < 3; cl++)
	{
	    classArrayNow[i] = examples.getIntersectionWith(classArray[i]);
	}
	if (classArrayNow[1].isEmpty())
	{
	    if (classArrayNow[2].isEmpty())
	    {
		decisionTree.setRootLabel("draw");
		return decisionTree;
	    }
	    else if (classArrayNow[0].isEmpty())
	    {
		decisionTree.setRootLabel("loss");
		return decisionTree;
	    }
	}
	else if (classArrayNow[0].isEmpty() && classArrayNow[2].isEmpty())
        {
	    decisionTree.setRootLabel("win");
	    return decisionTree;
	}

	/*------------------- -------------------*/

	/* BASE CASE 2
	 * No attributes left to decide by
	 * Return leaf node labelled by majority class
	 */
	// compute majority outside, since it is also required in latter case
	String majority = classArrayNow[0].size() > classArrayNow[1].size() && classArrayNow[0].size() > classArrayNow[2].size ? "draw" : (classArrayNow[1].size() > classArrayNow[2].size() ? "win" : "loss"); 
	if (attributes.isEmpty())
	{
	    decisionTree.setRootLabel(majority);
	    return decisionTree;
	}

	/*------------------- -------------------*/

	/* NONTRIVIAL CASE
	 * Among all attributes, pick A such that
	 * entropy[A = b] + entropy[A = o] + entropy[A = x] is minimum
	 * label root node with A. Construct and return the following tree :
	 *
	 * For each value v of A, add a branch labelled v 
	 * For a v branch, either
	 ** TRIVIAL SUBCASE v.1
	 * * (insufficient examples case)
	 * * [A = v] is empty i.e. subsetsByAttribute[A][v].isEmpty()
	 * * Return leaf node labelled by majority class.
	 ** RECURSIVE SUBCASE v.2
	 * * Recursively compute decision tree on [A = v] using remaining attributes
	 * * Add this tree as a subtree on the branch labelled v
	 ** 
	 */

	Iterator<Interval> attributeIter = attributes.iterator();
	int currAttribute = 0;
	Interval currInterval;
	double currEntropy, minEntropy = 1;
	int bestAttribute;
	// while loop to go over attributes and pick best according to entropy
	while (attributeIter.hasNext()) 
	{
	    /* Legalizer
	     * Makes sure the attributes we consider
	     * Are those still not branched on
	     */
	    currInterval = attributeIter.next();
	    while (currAttribute++ < currInterval.getStart())
	    {
		continue; // discard 
	    }
	    if (--currAttribute >= currInterval.getEnd())
		continue; // look in next interval
	    /*------- End of Legalizer --------*/

	    // compute Shannon entropy associated with deciding by A
	    currEntropy = 0;
	    for (int attrVal = 0 ; attrVal < 3; attrVal++)
	    {
		currEntropy += entropyOf(attributeArray[currAttribute][attrVal]);
	    }
	    
	    // compare with Shannon entropy corresponding to other attributes, and maintain minimum
	    if (currEntropy < minEntropy)
	    {
		minEntropy = currEntropy;
		bestAttribute = currAttribute;
	    }
	}

	// label root node by best attribute
	decisionTree.setRootLabel(Integer.toString(bestAttribute));

	// update attribute subset to remove A, since we will now branch out
	attributes.removeValue(bestAttribute);


	// add subtrees labelled by b,o,x
	String[] temp = {"draw","win","loss"};
	for (int valA = 0 ; valA < 3; valA++)
	{
	    if (attributeArray[bestAttribute][valA].isEmpty()) // trivial case
		decisionTree.addSubtree(temp[i], new Tree(majority));
	    else // recursive case
	    {
		decisionTree.addSubtree(temp[i].Integer.toString(valA), makeTree(examples.getIntersectionWith(attributeArray[bestAttribute][valA]), attributes)); // new attributes does not have A, new examples all have A = valA
	    }
	}
	return decisionTree;
    }

    /************************* *************************/   
    
    /**
     * @description obtains the classifier tree by
     * calling computeSubsets method
     * calling makeTree for the first time
     */
    private void trainTreeClassifier()
    {
	computeSubsets();
	classifierTree = makeTree((new DataSubset().addRun(new Interval(0,TRAINING_DATA_LENGTH))), (new DataSubset().addRun(new Interval(0, ATTRIBUTE_SIZE))));
    }

    /************************* *************************/   
    
    /**
     * @description classifies a single row of data based on
     * the tree that is passed. assumed to be called after trainTreeClassifier()
     */
    private String classify(String[] row, Tree subTreeHere)
    {
	if (row.length != ATTRIBUTE_SIZE + 1)
	    throw (new RunTimeException e ("Invalid data!"));
	if (subTreeHere.isLeaf())
	    return Integer.parseInt(subTreeHere.RootLabel());
	int attributeHere = Integer.parseInt(subTreeHere.getRootLabel());
	String valueHere = row[attributeHere];
	Tree subTreeNext = subTreeHere.getChildren().get(valueHere);
	return classify(row, subTreeNext);
    }
    
    /************************* *************************/   
    
    /**
     * @throws IOException
     * @description tests the test data by repeatedly calling classify
     * using classifierTree
     */
    private void testTreeClassifier() throws IOException
    {
	int total = 0;
	int correct = 0;
	BufferedReader br = new BufferedReader (new FileReader (TESTING_DATA_SOURCE));
	String currLine = "";
	String[] row = new String[ATTRIBUTE_SIZE+1]
	while ((currLine = br.readLine()) != null)
	{
	    row = currLine.split(",");
	    correct = classify(row, classifierTree).equals(row[ATTRIBUTE_SIZE]) ? correct+1 : correct;
	    total++;
	}
	System.out.println()
    }
    
    /************************* *************************/
    
    /**
     * @description Calculates the Shannon entropy of a given
     * subset of the original data
     * 
     * entropy = - ( P(win)log P(win) + P(draw)log P(draw) + P(loss)log P(loss) )
     * The probabilities are calculated simply by a ratio of counts.
     * 
     */
    private double entropyOf(DataSubset subset) { // PANDA(?) TODO optimize using subsetsByAttribute
	int winCount = 0;
	int drawCount = 0;
	int lossCount = 0;
	int totalCount = 0;
	double ans = 0;
	
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(this.dataSource));
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if(br != null) {
	    String currLine = "";
	    try {
		while((currLine = br.readLine()) != null) {
		    if(currLine.endsWith("win")) {
			winCount++;
		    } else if(currLine.endsWith("draw")) {
			drawCount++;
		    } else if(currLine.endsWith("loss")) {
			lossCount++;
		    } else { // Data line ends with something other than {win, draw, loss}
			throw(new RuntimeException("Invalid data line!"));
		    }
		    totalCount++;
		}
		br.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    if(totalCount != 0) {
		double pWin = (double)winCount / (double)totalCount;
		double pDraw = (double)drawCount / (double)totalCount;
		double pLoss = (double)lossCount / (double)totalCount;
		ans -= pWin * Math.log(pWin);
		ans -= pDraw * Math.log(pDraw);
		ans -= pLoss * Math.log(pLoss);
	    }
	}
	return ans;
    }    
}


