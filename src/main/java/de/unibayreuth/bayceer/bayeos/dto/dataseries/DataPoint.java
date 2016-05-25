package de.unibayreuth.bayceer.bayeos.dto.dataseries;

/**
 * A data point from a time series (value x and timestamp y)
 * @author christiane-goehring
 *
 */
public class DataPoint implements Comparable<DataPoint>  {

	// timestamp
	private long x;
	// value
	private double y;

	public DataPoint(long x, double y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public int compareTo(DataPoint o) {
		return ((Long) x).compareTo((Long) o.x);
	}

}
