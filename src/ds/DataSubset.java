package ds;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.management.RuntimeErrorException;

/**
 * Contains a linked list to identify a subset of
 * the original data. Since the original data is read line
 * by line, this structure keep a track of runs of elements
 * in the subset. Each run is represented by 2 integers,
 * start and end. Start is inclusive, end is exclusive.
 * 
 * [r_1, s_1) -> [r_2, s_2) -> ... -> [r_n, s_n)
 * 
 * The list follows the following rules.
 * 1) s_i >= r_i
 * 2) r_{i+1} > s_i
 *
 */

class Interval {
	
	private int start;
	private int end;
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public Interval(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	/************************* *************************/
	
	/**
	 * @description Getter functions.
	 */
	public int getStart() { return this.start; }
	public int getEnd() { return this.end; }
	
	/************************* *************************/
	
	/**
	 * @description Setter functions.
	 */
	public void setStart(int start) { this.start = start; }
	public void setEnd(int end) { this.end = end; }
	
	/************************* *************************/
	
	/**
	 * @description Returns the number of elements in the run.
	 */
	public int getCount() {
		return (end - start);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String ans = "[" + start + ", " + end + ")";
		return ans;
	}
	
	/************************* *************************/
	
	/**
	 * @description Returns whether this is contained in
	 * another interval
	 */
	public boolean containedIn(Interval a) {
		return (start >= a.start && end <= a.end);
	}
	
	/************************* *************************/
	
	/**
	 * @description Returns whether this intersects another
	 * interval.
	 */
	public boolean intersects(Interval a) {
		return !(end < a.getStart() || a.getEnd() < start);
	}
}

/**
 * Class to store and operate on subsets of a given data.
 * Subsets are stored in the form of line numbers.
 *
 */
public class DataSubset extends LinkedList<Interval> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public DataSubset() {
		super();
	}
	
	/************************* *************************/
	
	/**
	 * @description Returns the count of elements in the subset.
	 */
	public int getCount() {
		int ans = 0;
		for(int i=0;i<this.size();++i) {
			ans += this.get(i).getCount();
		}
		return ans;
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString() {
		StringBuilder ans = new StringBuilder();
		Iterator<Interval> iter = this.iterator();
		while(iter.hasNext()) {
			ans.append(iter.next() + " -> ");
		}
		ans.append("NULL");
		return ans.toString();
	}
	
	/************************* *************************/
	
	/**
	 * @description Adds specified value to the list.
	 */
	public void addValue(int toAdd) {
		this.addRun(new Interval(toAdd, toAdd+1));
	}
	
	/************************* *************************/
	
	/**
	 * @description Adds specified interval to the list.
	 */
	public void addRun(Interval toAdd) {

		// Check if interval to add is contained in some interval.
		boolean dropInterval = false;
		Iterator<Interval> iter = this.iterator();
		while(iter.hasNext()) {
			if(toAdd.containedIn(iter.next())) {
				dropInterval = true;
				break;
			}
		}
		if(!dropInterval) {
			
			// Drop intervals contained in toAdd.
			Iterator<Interval> iter1 = this.iterator();
			while(iter1.hasNext()) {
				if(iter1.next().containedIn(toAdd)) {
					iter1.remove();
				}
			}
			if(this.size() == 0) {
				this.add(toAdd);
			} 
			
			// Find out number of intersecting intervals.
			int intersections = 0;
			Iterator<Interval> iter2 = this.iterator();
			while(iter2.hasNext()) {
				if(iter2.next().intersects(toAdd)) {
					intersections++;
				}
			}
			
			// Merge intervals
			if(intersections == 0) {
				boolean toAppend = true;
				for(int i=0;i<this.size();++i) {
					if(toAdd.getEnd() < this.get(i).getStart()) {
						this.add(i, toAdd);
						toAppend = false;
						break;
					}
				}
				if(toAppend) {
					this.addLast(toAdd);
				}
				
			} else if(intersections == 1) {
				for(int i=0;i<this.size();++i) {
					if(this.get(i).intersects(toAdd)) {
						Interval curr = this.get(i);
						curr.setStart(Math.min(curr.getStart(), toAdd.getStart()));
						curr.setEnd(Math.max(curr.getEnd(), toAdd.getEnd()));
						break;
					}
				}
			} else if(intersections == 2) {
				// The loop starts from 1 since if the interval cannot be to the far left.
				for(int i=1;i<this.size();++i) { 
					if(this.get(i-1).intersects(toAdd) && this.get(i).intersects(toAdd)) {
						this.get(i-1).setEnd(this.get(i).getEnd());
						this.remove(i);
					}
				}
			} else {
				throw(new RuntimeException("Invalid dataset!"));
			}
		}
	}
	
	/************************* *************************/
	
	/**
	 * @description Main function for local testing.
	 */
	public static void main(String[] args) {
		DataSubset ds = new DataSubset();
		Random prng = new Random();
		for(int i=0;i<10;++i) {
			int a = prng.nextInt(30);
			int b = prng.nextInt(30);
			Interval k = new Interval(Math.min(a, b), Math.max(a, b));
			System.out.println("+" + k);
			ds.addRun(k);
			System.out.println(ds);
			System.out.println();
		}
	}
}
