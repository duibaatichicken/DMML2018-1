
package c4classifiers;

import util.StaticConstants;
import com.joptimizer.optimizers.*;

/**
 * This class provides a classifier for the Connect-4
 * data using Support Vector Machines.
 *
 */
public class SVMClassifier {

	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public SVMClassifier() {
		
	}
	
	/************************* *************************/
	
	/**
	 * @description Accepts a line formatted like the Connect-4
	 * data and converts it to a array of integers. The convention
	 * followed is as follows
	 *  0 - b (blank position on the board)
	 * +1 - x (counters played by Player 1)
	 * -1 - o (counters played by Player 2)
	 */
	private int[] convertToPoint(String line) {
		int[] ans = new int[42];
		String[] tmpArray = line.split(",");
		for(int i=0;i<StaticConstants.CLASS_COLUMN;++i) {
			if(tmpArray[i].equals("b")) {
				ans[i] = 0;
			} else if(tmpArray[i].equals("o")) {
				ans[i] = -1;
			} else if(tmpArray[i].equals("x")) {
				ans[i] = 1;
			} else {
				throw(new RuntimeException("Invalid data!"));
			}
		}
		return ans;
	}
        /************************* *************************/
	
	/**
	 * @description optimizer
	 */
	private void getOptimizedPoint()
        {
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		opt.optimize(); 
	}
    
	/************************* *************************/
	
	/**
	 * @description Main function for local testing.
	 */
	public static void main(String[] args) {
		
	}
        
}
