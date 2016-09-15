package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Represents a result object from the sax similarity search algorithm. Contains
 * the corresponding haystack_subsequence and the distance value.
 * 
 * @author christiane-goehring
 *
 */

public class SaxDistance implements Comparable<SaxDistance> {

	private SaxSeries haystack_subsequence;
	private Double distance;

	public SaxDistance(SaxSeries haystack, double mindist) {
		this.haystack_subsequence = haystack;
		this.distance = mindist;
	}
	
	public SaxDistance(){
		
	}

	public SaxSeries getHaystack_subsequence() {
		return haystack_subsequence;
	}

	public void setHaystack_subsequence(SaxSeries haystack_subsequence) {
		this.haystack_subsequence = haystack_subsequence;
	}

	@Override
	public int compareTo(SaxDistance o) {
		return this.distance.compareTo(o.distance);
	}

	@Override
	public String toString() {
		return "Distance " + distance + " to haystack: [" + haystack_subsequence + "]";
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
