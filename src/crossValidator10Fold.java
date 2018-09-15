import c4classifiers.TreeClassifier;
import ds.Tree;
import java.io.*;
import java.util.HashMap;

/*
 * Simple class to run 10-fold cross-validation
 * via training and testing methods
 * on data subsets externally chopped up
 *
 * METHODS
 * main
 * float validate(String classifier, int testSection)
 * Tree trainTree(String trainingData) : trains TreeClassifier on given dataset
 * trainBayes COMING SOON
 * trainSVM COMING SOON
 * float testTree(String testingData, Tree decisionTree) : tests a decision tree on the given dataset
 * testBayes COMING SOON
 * testSVM COMING SOON
 * boolean evalOnTree(String[] row, Tree decisionTree) : runs a row of data on a decision tree and gives boolean correctness
 */

public class crossValidator10Fold
{
    /* MAIN METHOD
     *
     */
    public static void main(String args[]) throws IOException, ArrayIndexOutOfBoundsException
    {
	crossValidator10Fold v = new crossValidator10Fold();
	System.out.println(v.crossValidate10Fold("tree"));
    }
    
    /*----------------------------------------------------------*/
    
    /* METHOD TO VALIDATE GIVEN TEST-TRAINING PAIR
     * INPUTS:
     * * String classifier : type of classifier
     * * int testSection : the part out of 10 parts of dataset that is reserved for testing
     * OUTPUT
     * * float : accuracy as returned by test<Classifier> methods
     */
    public float crossValidate10Fold(String classifier) throws IOException
    {
	float averageAccuracy = 0;
	for (int i = 1; i <= 10; i++)
	{
	    averageAccuracy = (averageAccuracy*(i-1) + this.crossValidate(classifier, i))/i;
	}
	return averageAccuracy;
    }
    private float crossValidate(String classifier, int testSection) throws IOException
    {
	if (testSection < 1 || testSection > 10)
	    throw (new IOException("this is 10-fold validation"));


	// find correct data files
	String trainingData = "data/connect4-cv_training_data-10-" + (testSection == 10 ? "" : "0") + Integer.toString(testSection) + ".data";
	String testData = "data/connect4-cv_test_data-10-" + (testSection == 10 ? "" : "0") + Integer.toString(testSection) + ".data";

	// validate the correct classifier type
	if (classifier == "tree")
	{
	    Tree decisionTree = this.trainTree(trainingData);
	    return this.testTree(testData, decisionTree);
	}
	else if (classifier == "bayes")
	    /*todo*/;
	else if (classifier == "svm")
	    /*todo*/;
	else
	    throw (new IOException("no classifier of that name"));
	return 0; // dummy return REMOVE LATER
    }

    /*----------------------------------------------------------*/

    /* METHOD TO TRAIN DECISION TREE
     * INPUT
     * * String trainingData : some 9/10 of data as provided by validate method
     * OUTPUT
     * * Tree : the decision tree built from trainingData
     */
    private Tree trainTree(String trainingData) throws IOException
    {
	return TreeClassifier.classifyData(trainingData, (67557-6755), 42);
    }

    /*----------------------------------------------------------*/

    /* METHOD TO TEST DECISION TREE
     * INPUTS
     * * String testData : some 1/10 of data as provided by validate method
     * * Tree decisionTree : tree made by trainTree on corresponding 9/10 training data
     * OUTPUT
     * * float treeAccuracy : fraction correct answers given by decisionTree on testData
     */
    private float testTree(String testData, Tree decisionTree) throws IOException
    {
	BufferedReader br = new BufferedReader (new FileReader (testData));
	String line = "";
	int rowsTillNow = 1; // tally of number of rows read till now from testData
	int currentResult = 0; // performance on current row i.e. "success rate" on only this iteration
	float accuracy = 1;
	/* The following while loop
	 * checks each row of testData on decisionTree
	 * keeps a running tally of accuracy
	 */ 
	while ((line = br.readLine()) != null)
	{
	    // encodes current result as 0 or 1
	    currentResult = this.evalOnTree(line.split(","), decisionTree) ? 1 : 0;

	    /* ACCURACY UPDATE
	     * for the first iteration, just take current result
	     * at all but first iteration
	     * accuracy is a fraction goodRowsTillLastTime/rowsTillLastTime
	     * retrieve goodRowsTillLastTime by multiplication
	     * add currentResult to get goodRowsTillNow
	     * divide by rowsTillNow to get new accuracy
	     */
	    accuracy = rowsTillNow > 1 ? ((accuracy * (rowsTillNow - 1)) + currentResult)/rowsTillNow : currentResult;
	    rowsTillNow++; // update. this will be truly rows till now after next line is read
	}
	return accuracy;
    }
    
    /*----------------------------------------------------------*/

    /* METHOD TO EVALUATE DATA ROW ON DECISION TREE
     * INPUTS
     * * String[] row : single row of data as attribute array
     * * Tree decisionTree : tree made by trainTree on some 9/10 training data
     * OUTPUT
     * * boolean : whether decisionTree was correct on row or not
     */
    private boolean evalOnTree(String[] row, Tree decisionTree) throws IOException
    {
	/* CASE 1
	 * We are already at a node which is labelled by a class
	 * Then sufficient to check if that's the true class
	 */
	if (decisionTree.getRootLabel().equals("win") || decisionTree.getRootLabel().equals("loss") || decisionTree.getRootLabel().equals("draw"))
	    return row[42].equals(decisionTree.getRoot());
	/* CASE 2
	 * If we are doing this correctly, decisionTree is non-empty
	 * The root is labelled by an Attribute A
	 * If we are doing this correctly, the root is not a leaf
	 * Suppose row[A] = v
	 * recursively call evalOnTree(row[], subtree[A=v])
	 */
	else
	{
	    if (decisionTree.isLeaf() || decisionTree.isEmpty())
		throw (new IOException("the decision tree should not be empty"));
	    // find the root label A, look up value of A in row, recurse on corresponding subtree 
	    return this.evalOnTree(row, decisionTree.getSubtree(row[Integer.parseInt(decisionTree.getRootLabel())]));
	}	
    }
}
