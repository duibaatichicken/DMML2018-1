package ds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains a linked list to identify a subset of
 * the original data. Since the original data is read line
 * by line, this structure keeps a track of runs of elements
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

	/************************* *************************/

	/**
	 * @description Redefines emptiness to be count = 0
	 */
	public boolean isEmpty()
	{
		return this.getCount() == 0;
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
				throw(new RuntimeException("Invalid algorithm!"));
			}
		}
	}

	/************************* *************************/

	/**
	 * @description Removes specified value from the list.
	 */
	public void removeValue(int toRemove) {
		this.removeRun(new Interval(toRemove, toRemove+1));
	}

	/************************* *************************/

	/**
	 * @description Removes specified interval from the list.
	 */
	public void removeRun(Interval toRemove) {

		// Prune interval by given interval
		for(int i=0;i<this.size();++i) {
			Interval tmpToRemove = new Interval(toRemove.getStart(), toRemove.getEnd());
			Interval curr = this.get(i);
			if(tmpToRemove.getStart() < curr.getStart()) {
				tmpToRemove.setStart(curr.getStart());
			}
			if(tmpToRemove.getEnd() > curr.getEnd()) {
				tmpToRemove.setEnd(curr.getEnd());
			}
			if(tmpToRemove.isValid()) {
				if(tmpToRemove.getStart() == curr.getStart()) {
					if(tmpToRemove.getEnd() == curr.getEnd()) {
						this.remove(i);
						break;
					} else {
						curr.setStart(tmpToRemove.getEnd());
					}
				} else { // tmpToRemove.getStart() > curr.getStart();
					if(tmpToRemove.getEnd() == curr.getEnd()) {
						curr.setEnd(tmpToRemove.getStart());
					} else {
						int tmp = curr.getEnd();
						curr.setEnd(tmpToRemove.getStart());
						if(i == this.size()-1) {
							this.add(new Interval(tmpToRemove.getEnd(), tmp));
						} else {
							this.add(i+1, new Interval(tmpToRemove.getEnd(), tmp));
						}
					}
				}
			}
		}
	}

	/************************* *************************/

	/**
	 * @description Return the points of interest
	 */
	private List<PointOfInterest> getPointsOfInterest(int id) {
		List<PointOfInterest> ans = new ArrayList<PointOfInterest>();
		Iterator<Interval> iter = this.iterator();
		while(iter.hasNext()) {
			Interval currInterval = iter.next();
			ans.add(new PointOfInterest(currInterval.getStart(), true, id));
			ans.add(new PointOfInterest(currInterval.getEnd(), false, id));
		}
		return ans;
	}

	/************************* *************************/

	/**
	 * @description Finds the given value in the subsets
	 */
	/*public DataSubset findValue(int toFind){
	        boolean toLowerBound = true;
		boolean toUpperBound = false;
		while (toLowerBound){
		    // TODO : Write code for finding a value.        
		    }*/

	/************************* *************************/

	/**
	 * @description Returns the intersection of the this subset
	 * with the subset given by ds.
	 */
	public DataSubset getIntersectionWith(DataSubset ds) {
		DataSubset ans = new DataSubset();
		
		// Get a global transcript of all points of interest in order.
		List<PointOfInterest> poi1 = this.getPointsOfInterest(1);
		List<PointOfInterest> poi2 = ds.getPointsOfInterest(2);
		List<PointOfInterest> transcript = new ArrayList<PointOfInterest>();
		int p1 = 0, p2 = 0;
		int size1 = poi1.size(), size2 = poi2.size();
		while(true) {
			if(p1 < size1) {
				if(p2 < size2) {
					if(poi1.get(p1).getValue() < poi2.get(p2).getValue()) {
						transcript.add(poi1.get(p1++));
					} else if(poi1.get(p1).getValue() > poi2.get(p2).getValue()) {
						transcript.add(poi2.get(p2++));
					} else {
						transcript.add(poi1.get(p1++));
						transcript.add(poi2.get(p2++));
					}
				} else {
					transcript.add(poi1.get(p1++));
				}
			} else {
				if(p2 < size2) {
					transcript.add(poi2.get(p2++));
				} else {
					break;
				}
			}
		}
		
		// Decide what intervals to add to intersection based in transcript.
		boolean in1 = false, in2 = false;
		for(int i=0;i<transcript.size();++i) {
			PointOfInterest currPoi = transcript.get(i);
			PointOfInterest nextPoi = null;
			if(i < transcript.size()-1) {
				nextPoi = transcript.get(i+1);
			}
			if(currPoi.isStart()) {
				if(currPoi.getId() == 1) {
					in1 = true;
				} else {
					in2 = true;
				}
			} else {
				if(currPoi.getId() == 1) {
					in1 = false;
				} else {
					in2 = false;
				}
			}
			if(in1 && in2) {
				ans.add(new Interval(currPoi.getValue(), nextPoi.getValue()));
			}
		}
		return ans;

		//		while (true)
		//		{
		//		    if (!in1 && !iter1.hasNext())
		//			break;
		//		    else if (!in2 && !iter2.hasNext())
		//			break;
		//		    while (!in1 && !in2 && iter1.hasNext() && iter2.hasNext())
		//		    {
		//			curr1 = iter1.next();
		//			curr2 = iter2.next();
		//			if (curr1.intersects(curr2))
		//		        {
		//			    ans.addRun(new Interval(Math.max(curr1.getStart(), curr2.getStart()), Math.min(curr1.getEnd(), curr2.getEnd())));
		//			    if (curr1.getEnd() > curr2.getEnd())
		//				in1 = true;
		//			    else if (curr2.getEnd() > curr1.getEnd())
		//				in2 = true;
		//			}
		//			else if (curr1.getStart() >= curr2.getEnd())
		//			    in1 = true;
		//			else // curr1 preceeds curr2
		//			    in2 = true;
		//		    }
		//		    while (!in1 && in2 && iter1.hasNext())
		//		    {
		//			curr1 = iter1.next();
		//			if (curr1.intersects(curr2))
		//		        {
		//			    ans.addRun(new Interval(Math.max(curr1.getStart(), curr2.getStart()), Math.min(curr1.getEnd(), curr2.getEnd())));
		//			    if (curr1.getEnd() > curr2.getEnd())
		//			    {
		//				in1 = true;
		//				in2 = false;
		//			    }
		//			    else if (curr1.getEnd() == curr2.getEnd())
		//				in2 = false;
		//			}
		//			else if (curr1.getStart() >= curr2.getEnd())
		//			{
		//			    in1 = true;
		//			    in2 = false;
		//			}
		//		    }
		//		    while (in1 && !in2 && iter2.hasNext())
		//		    {
		//			curr2 = iter2.next();
		//			if (curr2.intersects(curr1))
		//			{
		//			    ans.addRun(new Interval(Math.max(curr2.getStart(), curr1.getStart()), Math.min(curr2.getEnd(),curr1.getEnd())));
		//			    if (curr2.getEnd() > curr1.getEnd())
		//			    {
		//				in2 = true;
		//				in1 = false;
		//			    }
		//			    else if (curr1.getEnd() == curr2.getEnd())
		//				in1 = false;
		//			}
		//			else if (curr2.getStart() >= curr1.getEnd())
		//			{
		//			    in2 = true;
		//			    in1 = false;
		//			}
		//		    }
		//		    if (in1 && in2)
		//			throw (new RuntimeException ("Bad algorithm!"));
		//		}
		//		return ans;
	}
	
	/************************* *************************/
	
	/**
	 * @description Return the first few terms in the list.
	 */
	public String printHead() {
		StringBuilder ans = new StringBuilder();
		Iterator<Interval> iter = this.iterator();
		int count = 0;
		while(iter.hasNext() && count < 7) {
			ans.append(iter.next() + " -> ");
			count++;
		}
		ans.append("...");
		return ans.toString();
	}

	/************************* *************************/

	/**
	 * @description Main function for local testing.
	 */
	public static void main(String[] args) {
		DataSubset ds1 = new DataSubset(), ds2 = new DataSubset();
		ds1.addRun(new Interval(100, 500));
		ds1.addRun(new Interval(700, 900));
		ds2.addRun(new Interval(200, 600));
		ds2.addRun(new Interval(800, 1200));
//		System.out.println(ds1.getPointsOfInterest(1));
//		System.out.println(ds2.getPointsOfInterest(2));
		DataSubset dsint = ds1.getIntersectionWith(ds2);
		System.out.println(dsint);
	}
}
