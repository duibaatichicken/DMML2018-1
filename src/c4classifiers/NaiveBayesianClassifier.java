package c4classifiers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import util.StaticConstants;

/**
 * This class contains a classifier for connect4 data
 * using a naive Bayesian approach.
 *
 */
public class NaiveBayesianClassifier {

	/**
	 * The maps contains all probabilities. The counts map contains the
	 * conditional probabilities, given that the result is 'win', 'draw',
	 * or 'loss'. Each of these has a 42 X 3 array of probabilities. For
	 * each array, the [i][j]th element is the probability that the ith
	 * attribute has value j.
	 * The convention for second component is as follows:
	 * 0 - 'b' - 'win'
	 * 1 - 'o' - 'draw'
	 * 2 - 'x' - 'loss'
	 * Examples are as follows:
	 * 
	 * 1) countGiven.get("win")[4][0] represents the probability that the 5th
	 * attribute is a 'b' given the result is a "win"
	 * 
	 * 2) countGiven.get("draw")[36][2] represents the probability that the 37th
	 * attribute is an 'x' given the result is a "loss"
	 *
	 */
	private Map<String, int[][]> countsGiven;
	private Map<String, int[]> resultCountMap;
	private int totalCount;

	/************************* *************************/

	/**
	 * Constructor
	 * @throws IOException 
	 */
	public NaiveBayesianClassifier(int dataset) throws IOException {

		this.countsGiven = new HashMap<String, int[][]>();
		countsGiven.put("win", new int[StaticConstants.CLASS_COLUMN][3]);
		countsGiven.put("draw", new int[StaticConstants.CLASS_COLUMN][3]);
		countsGiven.put("loss", new int[StaticConstants.CLASS_COLUMN][3]);

		// Create instances of probability arrays.
		this.totalCount = 0;

		// Initialise all probabilities to zero.
		for(int i=0;i<StaticConstants.CLASS_COLUMN;++i) {
			for(int j=0;j<3;++j) {
				this.countsGiven.get("win")[i][j] = 0;
				this.countsGiven.get("draw")[i][j] = 0;
				this.countsGiven.get("loss")[i][j] = 0;
			}
		}

		// Create instance of result probability array.
		this.resultCountMap = new HashMap<String, int[]>();
		resultCountMap.put("win", new int[1]);
		resultCountMap.get("win")[0] = 0;
		resultCountMap.put("draw", new int[1]);
		resultCountMap.get("draw")[0] = 0;
		resultCountMap.put("loss", new int[1]);
		resultCountMap.get("loss")[0] = 0;

		// Populate probability arrays by reading data source.
		buildCountsArrays(dataset);
		
		// Run the cross-validator.
		String result = crossValidator(dataset);
		System.out.println(result);
	}

	/************************* *************************/

	/**
	 * @throws IOException 
	 * @description Compiles a list of probabilities based on
	 * reading the data source file.
	 */
	private void buildCountsArrays(int dataset) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(StaticConstants.TRAINING_DATA_SOURCE[dataset]));
			String currLine = "";
			while((currLine = br.readLine()) != null) {
				String[] tmpArray = currLine.split(",");
				String result = tmpArray[StaticConstants.CLASS_COLUMN]; // "win", "draw", "loss"
				for(int i=0;i<StaticConstants.CLASS_COLUMN;++i) {
					if(tmpArray[i].equals("b")) {
						countsGiven.get(result)[i][0]++;
					} else if(tmpArray[i].equals("o")) {
						countsGiven.get(result)[i][1]++;
					} else if(tmpArray[i].equals("x")) {
						countsGiven.get(result)[i][2]++;
					} else {
						throw(new RuntimeException("Invalid data!"));
					}
				}
				resultCountMap.get(result)[0]++;
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		totalCount = resultCountMap.get("win")[0] + resultCountMap.get("draw")[0] + resultCountMap.get("loss")[0];
		//		System.out.println(totalCount);
	}

	/************************* *************************/

	/**
	 * @description Returns the specified probability. For explanation
	 * on convention of values, see comment before the definition of
	 * the probability array.
	 */
	public double getProbability(int attribute, int value, String given) {
		if(attribute < 0 || attribute > 41) {
			throw(new RuntimeException("Attribute index out of bounds!"));
		} else {
			if(value < 0 || value > 2) {
				throw(new RuntimeException("Value index out of bounds!"));
			} else {
				return ((double)countsGiven.get(given)[attribute][value] / (double)resultCountMap.get(given)[0]);
			}
		}
	}

	/************************* *************************/

	/**
	 * @description Classify a line of input based on the Bayesian
	 * probabilities built earlier. The line is assumed to be formatted
	 * the same as training data, except for the last, result field.
	 */
	public String classify(String line) {
		String[] tmpArray = line.split(",");
		double[] prob = new double[3];
		prob[0] = 1; prob[1] = 1; prob[2] = 1;
		for(int i=0;i<StaticConstants.CLASS_COLUMN;++i) {
			if(tmpArray[i].equals("b")) {
				prob[0] *= getProbability(i, 0, "win");
				prob[1] *= getProbability(i, 0, "draw");
				prob[2] *= getProbability(i, 0, "loss");
			} else if(tmpArray[i].equals("o")) {
				prob[0] *= getProbability(i, 1, "win");
				prob[1] *= getProbability(i, 1, "draw");
				prob[2] *= getProbability(i, 1, "loss");
			} else if(tmpArray[i].equals("x")) {
				prob[0] *= getProbability(i, 2, "win");
				prob[1] *= getProbability(i, 2, "draw");
				prob[2] *= getProbability(i, 2, "loss");
			} else {
				throw(new RuntimeException("Invalid data line!"));
			}
		}
		prob[0] *= ((double)resultCountMap.get("win")[0] / (double) totalCount);
		prob[1] *= ((double)resultCountMap.get("draw")[0] / (double) totalCount);
		prob[2] *= ((double)resultCountMap.get("loss")[0] / (double) totalCount);

		String ans = "";
		if(prob[0] > prob[1]) {
			if(prob[2] > prob[0]) {
				ans = "loss";
			} else {
				ans = "win";
			}
		} else { // prob[0] <= prob[1]
			if(prob[2] > prob[1]) {
				ans = "loss";
			} else {
				ans = "draw";
			}
		}
		return ans;
	}

	/************************* *************************/

	/**
	 * @description Validates the model built from training
	 * data on the corresponding testing data.
	 */
	public String crossValidator(int dataset) {
		String ans = "";
		int correctCount = 0, totalCount = 0;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(StaticConstants.TESTING_DATA_SOURCE[dataset]));
			String currLine = "";
			while((currLine = br.readLine()) != null) {
				String predictedResult = classify(currLine);
				String actualResult = "";
				if(currLine.endsWith("win")) {
					actualResult = "win";
				} else if(currLine.endsWith("draw")) {
					actualResult = "draw";
				} else if(currLine.endsWith("loss")) {
					actualResult = "loss";
				} else {
					throw(new RuntimeException("Invalid data!"));
				}
				if(actualResult.equals(predictedResult)) {
					correctCount++;
				}
				totalCount++;
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ans = String.valueOf(correctCount) + " / " + String.valueOf(totalCount);
		return ans;
	}

	/************************* *************************/

	/**
	 * @throws IOException 
	 * @description Main function for local testing.
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		for(int i=0;i<10;++i) {
			NaiveBayesianClassifier nbc = new NaiveBayesianClassifier(i);
		}
	}
}
