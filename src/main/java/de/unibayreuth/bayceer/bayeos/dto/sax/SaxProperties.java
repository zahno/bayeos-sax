package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * An object holding the properties of the SAX similarity search such as
 * aggregation interval, by how much the time window is being shift, the
 * distance function (eg. MinDist or MaxDist), the SAX index, the portion of
 * maximum missing values and the number of results (best hits)
 * 
 * @author christiane-goehring
 *
 */
public class SaxProperties {

	private SaxAggregationInterval aggr_int;
	private SaxShiftBy shift_by;
	private SaxDistanceFunction dist_func;
	private SaxIndex sax_index;
	private double missing_values;
	private int no_best_hits;

	public SaxProperties() {

	}

	public SaxProperties(SaxAggregationInterval aggr_int, SaxShiftBy shift_by, SaxDistanceFunction dist_func,
			int sax_index, double missing_values, int no_best_hits) {
		this.setAggr_int(aggr_int);
		this.setShift_by(shift_by);
		this.setDist_func(dist_func);
		this.sax_index = new SaxIndex(sax_index, null);
		this.missing_values = missing_values;
		this.no_best_hits = no_best_hits;
	}

	public SaxIndex getSax_index() {
		return sax_index;
	}

	public void setSax_index(int sax_index) {
		this.sax_index = new SaxIndex(sax_index, null);
	}

	public double getMissing_values() {
		return missing_values;
	}

	public void setMissing_values(double missing_values) {
		this.missing_values = missing_values;
	}

	public int getNo_best_hits() {
		return no_best_hits;
	}

	public void setNo_best_hits(int no_best_hits) {
		this.no_best_hits = no_best_hits;
	}

	public SaxAggregationInterval getAggr_int() {
		return aggr_int;
	}

	public void setAggr_int(SaxAggregationInterval aggr_int) {
		this.aggr_int = aggr_int;
	}

	public SaxShiftBy getShift_by() {
		return shift_by;
	}

	public void setShift_by(SaxShiftBy shift_by) {
		this.shift_by = shift_by;
	}

	public SaxDistanceFunction getDist_func() {
		return dist_func;
	}

	public void setDist_func(SaxDistanceFunction dist_func) {
		this.dist_func = dist_func;
	}

	@Override
	public String toString() {
		return "Properties: [(" + sax_index + "), (" + aggr_int + "), (" + shift_by + "), (" + dist_func + "), ("
				+ missing_values + "), (" + missing_values + ")]";
	}

}
