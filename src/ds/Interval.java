package ds;

public class Interval {
	
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
	 * @description Tests whether an interval is valid.
	 */
	public boolean isValid() {
		return (this.end >= this.start);
	}
	
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
