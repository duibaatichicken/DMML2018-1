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
4 | Need getIntersectionWith to be defined
*/

import java.io.*;
import ds.DataSubset;
import ds.Interval;
import ds.Tree;
import java.util.Iterator;

import util.StaticConstants;

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

    /************************** ***************************/
    
    /**
     * Constructor
     */
    public TreeClassifier()
    {
	
	this.subsetsByAttribute = new DataSubset[StaticConstants.CLASS_COLUMN][3];
	this.subsetsByClass = new DataSubset[3];

	// initializes each element of subsetsByAttribute to an empty DataSubset
	for (int i = 0 ; i < StaticConstants.CLASS_COLUMN ; i++)
	{
	    for (int j = 0 ; j < 3 ; j++)
	    {
		this.subsetsByAttribute[i][j] = new DataSubset();
	    }
	}

	// initializes each element of subsetsByClass to an empty DataSubset
	for (int i = 0; i < 3; i++)
	{
	    this.subsetsByClass[i] = new DataSubset();
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
    private void computeSubsets(int dataset) throws IOException
    {
	BufferedReader br = new BufferedReader (new FileReader (StaticConstants.TRAINING_SOURCE_DAZZLE[dataset]));
	String currLine = "";
	int lineCount = 0;
	/*INTERFERING testline System.out.print(br.readLine());*/
	while((currLine = br.readLine()) != null) // PANDA HALP
	{
	    /*testline System.out.println(currLine);*/
	    /*testline if (lineCount >= 67557) break; */
	    String[] row = currLine.split(",");
	    /*testline*/ if (lineCount % 100 == 0) System.out.print(lineCount+" "); if (lineCount % 5000 == 0) System.out.print("\n");
	    /*testlines begin
	    if (lineCount % 100 == 0)
		System.out.print (lineCount+" ");
	    if (lineCount % 10000 == 0)
		System.out.print("\n");
	    end testlines*/
		if (row[StaticConstants.CLASS_COLUMN].equals("draw"))
		subsetsByClass[0].addValue(lineCount);
	    else if (row[StaticConstants.CLASS_COLUMN].equals("win"))
		subsetsByClass[1].addValue(lineCount);
	    else if (row[StaticConstants.CLASS_COLUMN].equals("loss"))
		subsetsByClass[2].addValue(lineCount);
	    for (int i = 0 ; i < StaticConstants.CLASS_COLUMN ; i++)
	    {
		/*testline System.out.println(row[i]);*/
		if (row[i].equals("b"))
		    subsetsByAttribute[i][0].addValue(lineCount);
		else if (row[i].equals("o"))
		    subsetsByAttribute[i][1].addValue(lineCount);
		else if (row[i].equals("x"))
		    subsetsByAttribute[i][2].addValue(lineCount);
		else
		    throw(new RuntimeException("Invalid data!"));
	    }
	    lineCount++;
	}
	br.close();
    }

    /************************* *************************/   
    
    /**
     * @description Builds ID3 decision tree based on 
     * subsets constructed from data
     * takes as input the rows and columns currently under consideration
     */
    private Tree makeTree(DataSubset examples, DataSubset attributes)
    {
	Tree decisionTree = new Tree(); // decision tree for this level of recursion

	/* BASE CASE 1
	 * all nodes are of same class C
	 * return leaf node labelled C
	 */
	DataSubset[] subsetsByClassNow = new DataSubset[3];
	for (int cl = 0; cl < 3; cl++)
	{
	    subsetsByClassNow[cl] = examples.getIntersectionWith(subsetsByClass[cl]);
	}
	if (subsetsByClassNow[1].isEmpty())
	{
	    if (subsetsByClassNow[2].isEmpty())
	    {
		decisionTree.setRootLabel("draw");
		return decisionTree;
	    }
	    else if (subsetsByClassNow[0].isEmpty())
	    {
		decisionTree.setRootLabel("loss");
		return decisionTree;
	    }
	}
	else if (subsetsByClassNow[0].isEmpty() && subsetsByClassNow[2].isEmpty())
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
	String majority = subsetsByClassNow[0].size() > subsetsByClassNow[1].size() && subsetsByClassNow[0].size() > subsetsByClassNow[2].size() ? "draw" : (subsetsByClassNow[1].size() > subsetsByClassNow[2].size() ? "win" : "loss"); 
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
	int bestAttribute = -1;
	DataSubset subExamples;
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
		subExamples = subsetsByAttribute[currAttribute][attrVal];
		currEntropy += subExamples.size() * entropyOf(subExamples) / examples.size();
	    }
	    
	    // compare with Shannon entropy corresponding to other attributes, and maintain minimum
	    if (currEntropy < minEntropy)
	    {
		minEntropy = currEntropy;
		bestAttribute = currAttribute;
	    }
	}

	if (bestAttribute < 0)
	    throw (new RuntimeException ("Bad Algorithm!"));

	// label root node by best attribute
	decisionTree.setRootLabel(Integer.toString(bestAttribute));

	// update attribute subset to remove A, since we will now branch out
	attributes.removeValue(bestAttribute);


	// add subtrees labelled by b,o,x
	String[] temp = {"draw","win","loss"};
	for (int valA = 0 ; valA < 3; valA++)
	{
	    if (subsetsByAttribute[bestAttribute][valA].isEmpty()) // trivial case
		decisionTree.addSubtree(temp[valA], new Tree(majority));
	    else // recursive case
	    {
		decisionTree.addSubtree(temp[valA], makeTree(examples.getIntersectionWith(subsetsByAttribute[bestAttribute][valA]), attributes)); // new attributes does not have A, new examples all have A = valA
	    }
	}
	return decisionTree;
    }

    /************************* *************************/   
    
    /**
     * @throws IOException
     * @description obtains the classifier tree by
     * calling computeSubsets method
     * calling makeTree for the first time
     */
    public void trainTreeClassifier(int dataset) throws IOException
    {
	System.out.println("Training Dataset "+Integer.toString(dataset));
	System.out.print("\tComputing Attribute and Class Subsets...");
	computeSubsets(dataset);
	System.out.println("done");
	System.out.print("\tConstructing Arguments for ID3...");
	DataSubset exampleSubset = new DataSubset();
	exampleSubset.addRun(new Interval(0, StaticConstants.DATA_LENGTH));
	DataSubset attributeSubset = new DataSubset();
	exampleSubset.addRun(new Interval(0, StaticConstants.CLASS_COLUMN));
	System.out.println("done");
	System.out.print("\tConstructing Decision Tree...");
	classifierTree = makeTree(exampleSubset, attributeSubset);
	System.out.println("done");
    }

    /************************* *************************/   
    
    /**
     * @description classifies a single row of data based on
     * the tree that is passed. assumed to be called after trainTreeClassifier()
     */
    private String classify(String[] row, Tree subTreeHere)
    {
	if (row.length != StaticConstants.CLASS_COLUMN + 1)
	    throw (new RuntimeException("Invalid data!"));
	if (subTreeHere.isLeaf())
	    return subTreeHere.getRootLabel();
	int attributeHere = Integer.parseInt(subTreeHere.getRootLabel());
	String valueHere = row[attributeHere];
	Tree subTreeNext = subTreeHere.getSubtree(valueHere);
	return classify(row, subTreeNext);
    }
    
    /************************* *************************/   
    
    /**
     * @throws IOException
     * @description tests the test data by repeatedly calling classify
     * using classifierTree
     */
    public float testTreeClassifier(int dataset) throws IOException
    {
	System.out.println("Testing Dataset "+Integer.toString(dataset));
	int total = 0;
	int correct = 0;
	float accuracy = 0;
	BufferedReader br = new BufferedReader (new FileReader (StaticConstants.TESTING_SOURCE_DAZZLE[dataset]));
	String currLine = "";
	String actualClass = "";
	String[] row = new String[StaticConstants.CLASS_COLUMN + 1];
	while ((currLine = br.readLine()) != null)
	{
	    if (currLine.endsWith("win"))
		actualClass = "win";
	    else if (currLine.endsWith("draw"))
		actualClass = "draw";
	    else if (currLine.endsWith("loss"))
		actualClass = "loss";
	    else
		throw (new RuntimeException("Invalid data!"));
		       
	    if (total % 1000 == 0)
		System.out.println("\tClassifying rows "+Integer.toString(total)+"-"+Integer.toString(total+999)+" of data");
	    row = currLine.split(",");
	    correct = classify(row, classifierTree).equals(actualClass) ? correct+1 : correct;
	    total++;
	}
	br.close();
	accuracy = (float)correct / (float)total;
        System.out.println("\nAccuracy "+Integer.toString(correct)+"/"+Integer.toString(total)+" = "+Float.toString(accuracy));
        return accuracy;
    }
    
    /************************* *************************/   
    
    /**
     * @throws IOException
     * @description 10 fold cross validation of classifier by calling
     * trainTreeClassifier and testTreeClassifier
     * for values of dataset in 1...10
     */

    public static float tenfoldValidation() throws IOException
    {
	System.out.println("10-FOLD VALIDATION OF ID3 DECISION TREE ON UCI CONNECT4 DATASET");
	float avgAccuracy = 0;
	TreeClassifier tc;
	for (int dataset = 0; dataset < 10; dataset++)
	{
	    System.out.println("---------------------------\nValidating Fold "+Integer.toString(dataset));
	    tc = new TreeClassifier();
	    tc.trainTreeClassifier(dataset);
	    avgAccuracy += tc.testTreeClassifier(dataset) * StaticConstants.TESTING_DATA_LENGTH[dataset];
	}
	avgAccuracy /= StaticConstants.DATA_LENGTH;
	System.out.println("---------------------------\nCROSS VALIDATION COMPLETE\nAverage Accuracy = "+Float.toString(avgAccuracy));
	return avgAccuracy;
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
    private double entropyOf(DataSubset subset)
    {
	int winCount = subset.getIntersectionWith(subsetsByClass[1]).size();
	int drawCount = subset.getIntersectionWith(subsetsByClass[0]).size();
	int lossCount = subset.getIntersectionWith(subsetsByClass[2]).size();
	int totalCount = winCount + drawCount + lossCount;
	double ans = 0;
	if(totalCount != 0)
	{
	    double pWin = (double)winCount / (double)totalCount;
	    double pDraw = (double)drawCount / (double)totalCount;
	    double pLoss = (double)lossCount / (double)totalCount;
	    ans -= pWin * Math.log(pWin);
	    ans -= pDraw * Math.log(pDraw);
	    ans -= pLoss * Math.log(pLoss);
	}
	return ans;
    }
    /*----------------------------------- attempting sleeker version using new DS
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
	}   */ 
    /************************* *************************/
    
    /**
     * @throws IOException
     * @description Main method for local testing
     */
    public static void main(String args[]) throws IOException
    {
	float crossAvg = tenfoldValidation();
    }

}


