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
1 | Interval.getStart(), Interval.getEnd() inaccessible from here | 50, 60, in and around every LEGALIZER... |
2 | please review my reuse of reader objects | 230, 365, 440 |
3 | perhaps need better emptiness check for DataSubset, including case {[r,r)} | 310 |
 */

import java.io.*;
import ds.DataSubset;
import ds.Interval;
import ds.Tree;

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

		// initialize to entire dataset and all attributes
		DataSubset examples = new DataSubset();
		examples.addRun(new Interval(0, dataLength));
		DataSubset attributes = new DataSubset();
		attributes.addRun(new Interval(0, classColumn));
		(new TreeClassifier()).ID3(examples, attributes); // initial call to ID3 algorithm
	}

	private Tree ID3(DataSubset examples, DataSubset attributes) throws IOException, IndexOutOfBoundsException //target_attribute is the same throughout
	{
		BufferedReader br = new BufferedReader (new FileReader (dataSource)); // file reader
		String line = ""; // temporary variable for reading
		int rowNumber = -1; // keeps track of which example we are at
		int exampleIndex = examples.get(0).getStart(); // will run through examples list, starts from first good one
		int attributeIndex = attributes.get(0).getStart(); // will run through attribute list, starts from first good one
		String currClass = ""; // stores current class

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
				catch (IndexOutOfBoundsException e) // no more examples
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

			/* The following block code
			 * updates the majorityTracker
			 * according to the classes in current subset
			 * because we need it in Case 2 where we don't reread everything
			 */
			if (line.endsWith("win"))
			{
			        currClass = "win";
				majorityTracker[0]++;
				majorityTracker[1]++;
			}
			else if (line.endsWith("loss"))
			{
			        currClass = "loss";
			        majorityTracker[0]--;
			}
			else
			{
			        currClass = "draw";
				majorityTracker[1]--;
			}
			
			/* The following block of code
			 * compares the latest seen class with the immediate previous one
			 * if there are different classes, there are consecutive rows with different classes
			 * we detect the first such.
			 */
			targetTracker[0] = targetTracker[1];
			targetTracker[1] = currClass;
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

		/* The following while loop
		 * finishes reading the data and if it wasn't done already
		 * and computes the overall majority
		 * the majority computation might be unnecessary in some subcases of case 2
		 * but might be requried later
		 */
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
				catch (IndexOutOfBoundsException e) // no more examples
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
		        if (line.endsWith("win"))
			{
			        currClass = "win";
				majorityTracker[0]++;
				majorityTracker[1]++;
			}
			else if (line.endsWith("loss"))
			{
			        currClass = "loss";
			        majorityTracker[0]--;
			}
			else
			{
			        currClass = "draw";
				majorityTracker[1]--;
			}
		}                     	     
		if (attributes.isEmpty()) // if there are no attributes
			return (new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); //single node with majority

		br.close();

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
		String[] row = new String[classColumn+1];
		
		/* The following while loop
		 * reads the data and creates Example[A=v_i] subsets according to best Attribute A
		 */
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
				catch (IndexOutOfBoundsException e) // no more examples
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
			if (row[bestAttribute].equals("b"))
				bExamples.addValue(bestAttribute);    // add current example to bExamples
			else if (row[bestAttribute].equals("o"))
				oExamples.addValue(bestAttribute); // and so on
			else
				xExamples.addValue(bestAttribute);

		}
		/* now we shall build the root node
		 * followed by, recursively, its children
		 */

		// figure out name of attribute from column number
		/* The Connect4 board is numbered as
		 * 6 ------
		 * 5 ------
		 * 4 ------
		 * 3 ------
		 * 2 ------
		 * 1 ------
		 *   abcdef
		 */
		/* Attributes appear in dataset as
		 * 1. a1
		 * 2. a2
		 * ...
		 * 6. a6
		 * 7. b1
		 * ...
		 * 12. b6
		 * ...
		 * ...
		 * 42. g6
		 */
		/* So where attribute = 6q + r, r < 6, '
		 * a'+q is the letter and 
		 * r is the number (but we write 0 as 6) */

		Tree thisLevel = new Tree(Character.toString((char)(97 + (bestAttribute / 6))) + Integer.toString(bestAttribute % 6 == 0 ? 6 : bestAttribute % 6)); // node with attribute label is the root of subtree at this recursion depth
		attributes.removeValue(bestAttribute); // all attributes other than the one we already branched on

		/* PANDA TODO
		 * how does emptiness work in DS?
		 * currently have accounted for both possibilities here itself
		 * perhaps you should define something like trueEmpty in DS
		 */

		/* PRAISE THE RECURSION
		 * The following block of code
		 * checks if each Examples(v_i) is empty or not
		 * if empty, creates single majority node for the corresponding child
		 * if not empty, recurses to create non-trivial subtree
		 */

		TreeClassifier tc = new TreeClassifier(); // your friendly neighbourhood object, only for function calls, jisko bachpan mein bina samjhe banate theyy
		// branch A="b", board configurationss where position A is left blank
		if (bExamples.isEmpty() || bExamples.get(0).getStart() == bExamples.get(0).getEnd())
			thisLevel.addSubtree("b", new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); // single majority node as child
		else
			thisLevel.addSubtree("b", tc.ID3(bExamples, attributes));

		// branch A="o", board configurationss where position A is taken by player o	
		if (oExamples.isEmpty() || oExamples.get(0).getStart() == oExamples.get(0).getEnd())
			thisLevel.addSubtree("o", new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); // single majority node as child
		else
			thisLevel.addSubtree("o", tc.ID3(oExamples, attributes));

		// branch A="x", board configurationss where position A is taken by player x
		if (xExamples.isEmpty() || xExamples.get(0).getStart() == xExamples.get(0).getEnd())
			thisLevel.addSubtree("x", new Tree(majorityTracker[0] > 0 && majorityTracker[1] > 0? "win" : (majorityTracker[0] < majorityTracker[1] ? "loss" : "draw"))); // single majority node as child
		else
			thisLevel.addSubtree("x", tc.ID3(xExamples, attributes));

		return thisLevel;
	}

	private int bestAttribute(DataSubset examples, DataSubset attributes) throws IOException, IndexOutOfBoundsException
	{
		String[] row = new String[classColumn+1]; // 1 row of data
		BufferedReader br;
		String line = "";
		int attributeIndex = attributes.get(0).getStart(); // runs over attributes, starts at first good one
		int exampleIndex = examples.get(0).getStart(); //similarly for examples
		int rowNumber = 0;
		DataSubset bExamples = new DataSubset();
		DataSubset oExamples = new DataSubset();
		DataSubset xExamples = new DataSubset(); // these 3 will hold A=v_i subsets
		int minAttribute = -1; // best Attribute
		double currentMinEntropy = 0, tempEntropy = 1;
		for (int currentAttribute = 0; currentAttribute < classColumn ; currentAttribute++)
		{
			br = new BufferedReader (new FileReader (dataSource));
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
				catch (IndexOutOfBoundsException e) // no more attributes to check
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
					catch (IndexOutOfBoundsException e) // no more examples
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
			/* PANDA LISTEN : pliss to review my usage of multiple readers. can we do better? */

			/* The following block of code
			 * Computes Shannon entropies of {b,o,x}Examples
			 * Maintains minimum such
			 * over all Attributes in given attribute subset
			 */
			TreeClassifier tc = new TreeClassifier(); // friendly neighbourhood object
			tempEntropy = tc.entropyOf(bExamples) + tc.entropyOf(xExamples) + tc.entropyOf(oExamples);
			if (tempEntropy < currentMinEntropy)
			{
			    currentMinEntropy = tempEntropy;
			    minAttribute = currentAttribute;
			}
		}			      
		return minAttribute; // best attribute to branch on
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
	private double entropyOf(DataSubset subset) {
		int winCount = 0;
		int drawCount = 0;
		int lossCount = 0;
		int totalCount = 0;
		double ans = 0;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(dataSource));
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


