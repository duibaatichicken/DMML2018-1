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
import ds.*;

public class TreeClassifier
{
    static String dataSource = "../data/connect-4-full.data"; // data source
    static int dataLength = 67557; // number of rows
    static int classColumn = 42; // 43 columns. 42 attributes and 1 class.

    /* MAIN METHOD
       calls ID3 on the entire dataset and all attributes
    */
    public static void main(String args[]) throws IOException
    {
	/*
	  Examples : DataSubset indicator
	  Target Attribute : Integer index
	  Attributes : DataSubset indicator
	 */
	/*
	  PANDA LISTEN
	  I am reworking this using your pretty DataSubset class
	 */

	// initialize to entire dataset and all attributes
	DataSubset examples = (new DataSubset()).addRun(new Interval(0,dataLength));
	DataSubset attributes = (new DataSubset()).addRun(new Interval(0,classColumn));
	(new TreeClassifier()).ID3(examples, attributes); // initial call to ID3 algorithm
    }

    private Tree ID3(int examples[], int attributes[]) throws IOException, IndexOutOfBoundsException //target_attribute is the same throughout
    {
	BufferedReader br = new BufferedReader (new FileReader (dataSource)); // file reader
	String line = ""; // temporary variable for reading
	int rowNumber = -1; // keeps track of which example we are at
	int exampleIndex = examples.get(0).getStart(); // will run through examples list, starts from first good one
	int attributeIndex = attributes.get(0).getStart(); // will run through attribute list, starts from first good one
	String[] row = new String[classColumn+1]; // 43 columns in each row of data

	/*----------------------------------------------------------------------*/
	/* CASE 1 : CLASS-PURE
	 * ALL WINS, or
	 * ALL LOSSES, or
	 * ALL DRAWS
	 * We will return
	 * A lonely root labelled by the pure class
	 */
	String targetTracker[] = {"",""}; // tracks if examples are all of same class
	int majorityTracker[] = {0,0}; // tracks majority class in given example, stores {win-loss, win-draw}
	while((line = br.readLine()) != null && exampleIndex < examples.size())
	{
	    rowNumber++;
	    /* THE LEGALIZER
	     * The following block of code finds if the current row is included in the subset
	     * moves the exampleIndex until either examples end
	     * or we exceed the row number
	     * hence confirming where the row number lies w.r.t intervals
	     */
	    if (rowNumber < examples.get(exampleIndex).getStart())
	    {
		continue; // we traverse from the left, so rowNumber >= examples[exampleIndex-1].getStart() is ensured
		// hence we can discard
	    }
	    else
	    {
		try
		{
		    while (rowNumber >= examples.get(exampleIndex++).getEnd()); // look in the next index and so on
		}
		catch (IndexOutofBoundsException e) // no more examples
		{
		    line = null; // use this as a flag for all breaks other than moving to other cases
		    break;
		}
		/* At this point
		 * examples[exampleIndex-1].getEnd() <= rowNumber < examples[exampleIndex].getEnd()
		 * so we can discard if rowNumber < examples[exampleIndex].getStart()
		 */
		if (rowNumber < examples.get(exampleIndex).getStart())
		    continue; //discard
	    }
	    /* END OF LEGALIZER */

	    /*If we reached here
	     *then this row is in the desired subset
	     */
	    row = line.split(",");

	    /* The following block code
	     * updates the majorityTracker
	     * according to the classes in current subset
	     * because we need it in Case 2 where we don't reread everything
	     */
	    if (row[classColumn].equals("win"))
	    {
		majorityTracker[0]++;
		majorityTracker[1]++;
	    }
	    else if (row[classColumn].equals("loss"))40
	        majorityTracker[0]--;
	    else
		majorityTracker[1]--;
	     
	    /* The following block of code
	     * compares the latest seen class with the immediate previous one
	     * if there are different classes, there are consecutive rows with different classes
	     * we detect the first such.
	     */
	    targetTracker[0] = targetTracker[1];
	    targetTracker[1] = row[classColumn];
	    if (targetTracker[0] != targetTracker[1]) // then we know this base case does not hold
		break; // so we move on to other cases
  
	    /* if the loop above did not detect a change of class
	     * we can say there is just one class in the examples
	     */
	}
	// the pure class case
        if (line == null) //so I know it didn't break due to class change
	    return (new Tree(targetTracker[1])); // lonely root with label of class
    
	/* END OF CASE 1 */

	/*----------------------------------------------------------------------*/

	/* CASE 2 : 
	 * EMPTY ATTRIBUTE LIST 
	 * We will return
	 * A lonely root with majority of the class
	 */
	if (attributes.get(0).getStart() == attributes.get(0).getEnd()) // if there are no attributes
	{
	    if (rowNumber == dataLength) // if I was done reading the data, I can return right away
		return (new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); //single node with majority
	    else // if there is data unread in Case 1, read it and update majority accordingly
	    {
		while((line = br.readLine()) != null && exampleIndex < examples.size())
		{
		    rowNumber++;
		    /* THE LEGALIZER 2.0 (it's the same, really)
		     * The following block of code finds if the current row is included in the subset
		     * moves the exampleIndex until either examples end
		     * or we exceed the row number
		     * hence confirming where the row number lies w.r.t intervals
		     */
		    if (rowNumber < examples.get(exampleIndex).getStart())
		    {
			continue; // we traverse from the left, so rowNumber >= examples[exampleIndex-1].getStart() is ensured
			// hence we can discard
		    }
		    else
		    {
			try
			{
			    while (rowNumber >= examples.get(exampleIndex++).getEnd()); // look in the next index and so on
			}
			catch (IndexOutofBoundsException e) // no more examples
			{
			    line = null; // use this as a flag for all breaks other than moving to other cases
			    break;
			}
			/* At this point (recall we increment within while loop)
			 * examples[exampleIndex-2].getEnd() <= rowNumber < examples[exampleIndex-1].getEnd()
			 * so we can discard if rowNumber < examples[exampleIndex-1].getStart()
			 */
			if (rowNumber < examples.get(exampleIndex-1).getStart())
			    continue; //discard
		    }
		    /* END OF LEGALIZER */

		    /* The following block code
		     * updates the majorityTracker
		     * according to the classes in current subset
		     * because we need it in Case 2 where we don't reread everything
		     */
		    if (row[classColumn].equals("win"))
		    {
			majorityTracker[0]++;
		        majorityTracker[1]++;
		    }
		    else if (row[classColumn].equals("loss"))
			majorityTracker[0]--;
		    else
			majorityTracker[1]--;
	        }                     	     
		return (new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); //single node with majority
	    }
	    br.close();
       }    
        /* END OF CASE 2 */

        /*----------------------------------------------------------------------*/        
	
	/* CASE 3 : NONE OF THE BASE CASES CAME UP
	 * This is where it gets real
	 * Now we use Shannon entropy to find best classifying attribute
	 * Then we branch on that attribute and recurse
	 */
	int bestAttribute = (new TreeClassifier()).bestAttribute(examples, attributes);// pick best attribute A according to Shannon entropy
	br = new BufferedReader (new FileReader (dataSource)); // PANDA LISTEN can I do this
	exampleIndex = examples.get(0).getStart(); // reset beginning of subset
	rowNumber = 0; // reset top of file
	DataSubset bExamples = new DataSubset();
	DataSubset oExamples = new DataSubset();
	DataSubset xExamples = new DataSubset(); // these 3 will hold A=v_i subsets
	// for each possible value v_i, build Examples(v_i)
	    // CASE v_i.1 empty. then add majority leaf node
	    // CASE v_i.2 recurse
	while((line = br.readLine()) != null && exampleIndex < examples.size())
        {
	    rowNumber++;
	    /* THE LEGALIZER is back yo
	     * The following block of code finds if the current row is included in the subset
	     * moves the exampleIndex until either examples end
	     * or we exceed the row number
	     * hence confirming where the row number lies w.r.t intervals
	     */
	    if (rowNumber < examples.get(exampleIndex).getStart())
	    {
		continue; // we traverse from the left, so rowNumber >= examples[exampleIndex-1].getStart() is ensured
		// hence we can discard
	    }
	    else
	    {
		try
		{
		    while (rowNumber >= examples.get(exampleIndex++).getEnd()); // look in the next index and so on
		}
		catch (IndexOutofBoundsException e) // no more examples
		{
		    line = null; // use this as a flag for all breaks other than moving to other cases
		    break;
		}
		/* At this point (recall we increment within while loop)
		 * examples[exampleIndex-2].getEnd() <= rowNumber < examples[exampleIndex-1].getEnd()
		 * so we can discard if rowNumber < examples[exampleIndex-1].getStart()
		 */
		if (rowNumber < examples.get(exampleIndex-1).getStart())
		    continue; //discard
	    }
	    /* END OF LEGALIZER */
	    
	    /*The following block of code
	     *builds Examples(v_i)
	     */
	    row = line.split(",");
	    if (row[currentAttribute].equals("b"))
		bExamples.addValue(currentAttribute);    // add current example to bExamples
	    else if (row[currentAttribute].equals("o"))
		oExamples.addValue(currentAttribute); // and so on
	    else
		xExamples.addValue(currentAttribute);
	    
	}
	// remove function required to proceed.
	
    }

    private int bestAttribute(DataSubset examples, DataSubset attributes) throws IOException, IndexOutOfBoundsException
    {
	String[] row = new String[classColumn+1]; // I row of data
	BufferedReader br;
	String line = "";
	int attributeIndex = attributes.get(0).getStart(); // runs over attributes, starts at first good one
        int exampleIndex = examplesget(0).getStart(); //similarly for examples
	int rowNumber = 0;
	DataSubset bExamples = new DataSubset();
	DataSubset oExamples = new DataSubset();
	DataSubset xExamples = new DataSubset(); // these 3 will hold A=v_i subsets
	int minAttribute = -1; // best Attribute
	double shannon = 1, tempshannon = 1;
	for (int currentAttribute = 0; currentAttribute < classColumn ; currentAttribute++)
	{
	    br = new BufferedReader (new FileReader (dataSource);
	    exampleIndex = examples.get(0).getStart(); //reset top of file
	    bExamples.clear();
	    oExamples.clear();
	    xExamples.clear(); // clean out the lists from previous iteration

	    /* THE LEGALIZER, but for ATTRIBUTES, because EQUALITE
	     * The following block of code finds if the current attribute is included in the attribute set
	     * moves the attributeIndex until either attributes end
	     * or we exceed the total number of attributes
	     * hence confirming where the attribute number lies w.r.t intervals
	     */
	    if (currentAttribute < attributes.get(attributeIndex).getStart())
	    {
		continue; // we traverse from the left, so currentAttribute >= attributes.get(attributeIndex).getEnd() is ensured
		// hence we can discard
	    }
	    else
	    {
		try
		{
		    while (currentAttribute >= attributes.get(attributeIndex++).getEnd()); // look in the next index and so on
		}
		catch (IndexOutofBoundsException e) // no more attributes to check
		{
		    line = null; // use this as a flag
		    break;
		}
		/* At this point (recall we increment within while loop)
		 * attrubutes[attributeIndex-2].getEnd() <= currentAttribute < attributes[attributeIndex-1].getEnd()
		 * so we can discard if currentAttribute < attributes[attributeIndex-1].getStart()
		 */
		if (currentAttribute < attributes.get(attributeIndex-1).getStart())
		    continue; //discard
	    }
	   /* END OF LEGALIZER */
	   while((line = br.readLine()) != null && exampleIndex < examples.size())
	   {
	       rowNumber++;
	       /* THE LEGALIZER 3.0 (this time for examples again)
		* The following block of code finds if the current row is included in the subset
		* moves the exampleIndex until either examples end
		* or we exceed the row number
		* hence confirming where the row number lies w.r.t intervals
		*/
	       if (rowNumber < examples.get(exampleIndex).getStart())
	       {
		   continue; // we traverse from the left, so rowNumber >= examples[exampleIndex-1].getStart() is ensured
		   // hence we can discard
	       }
	       else
	       {
		   try
		   {
		       while (rowNumber >= examples.get(exampleIndex++).getEnd()); // look in the next index and so on
		   }
		   catch (IndexOutofBoundsException e) // no more examples
		   {
		       line = null; // use this as a flag for all breaks other than moving to other cases
		       break;
		   }
		   /* At this point (recall we increment within while loop)
		    * examples[exampleIndex-2].getEnd() <= rowNumber < examples[exampleIndex-1].getEnd()
		    * so we can discard if rowNumber < examples[exampleIndex-1].getStart()
		    */
		   if (rowNumber < examples.get(exampleIndex-1).getStart())
		       continue; //discard
	       }
	       /* END OF LEGALIZER */
			     
	       /*The following block of code
		*builds Examples(v_i)
		*/
	       row = line.split(",");
	       if (row[currentAttribute].equals("b"))
		   bExamples.addValue(currentAttribute);    // add current example to bExamples
	       else if (row[currentAttribute].equals("o"))
		   oExamples.addValue(currentAttribute); // and so on
	       else
		   xExamples.addValue(currentAttribute);

	   }
	br.close();
	/* BIG TODO PANDA LISTEN : pliss to review my usage of multiple readers. can we do better? */

	/* The following block of code
	 * Wants to compute Shannon entropies of {b,o,x}Examples
	 * but cannot
	 * because we have not implemented Shannon yet
	 * The following block of code
	 * is tired
	 * yawn
	 * that's called a transferred epithet
	 * #EnglishTA
	 */
	  // tempShannon = Shannon(b) + Shannon(o) + Shannon (x)
	  // if tempShannon < Shannon, Shannon = tempShannon; minAttribute = currentAttribute; 
        }			      
	return minAttribute;
    }
    private double shannonEntropy(DataSubset posExamples) // Examples by position
    {
	// implement, or use external package
	return 0; // dummy return statement, will modify later
    }
}
    

