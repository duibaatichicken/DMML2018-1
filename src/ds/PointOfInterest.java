package ds;

public class PointOfInterest {

	private int value;
	private boolean isStart;
	private int id;
	
	/************************* *************************/
	
	/**
	 * Constructor
	 */
	public PointOfInterest(int value, boolean isStart, int id) {
		this.value = value;
		this.isStart = isStart;
		this.id = id;
	}
	
	/************************* *************************/
	
	/**
	 * @description Getter functions.
	 */
	public int getValue() { return this.value; }
	public boolean isStart() { return this.isStart; }
	public int getId() { return this.id; }
	
	/************************* *************************/
	
	/**
	 * @description Setter functions.
	 */
	public void setValue(int value) { this.value = value; }
	public void setIsStart(boolean isStart) { this.isStart = isStart; }
	public void setId(int id) { this.id = id; }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String ans = String.valueOf(value) + "-";
		ans += isStart ? "S" : "E";
		ans += "-" + id;
		return ans;
	}
}
