package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * This object represents the result from a sax similarity search. It contains
 * the needle, an array of the x best haystack-subsequences with the
 * corresponding distance value and the thereby used properties.
 * 
 * @author christiane-goehring
 *
 */
public class SaxResult {

	private SaxSeries needle;
	private SaxDistance[] haystack_subsequences;
	private SaxProperties properties;

	public SaxResult(){
		
	}
	
	public SaxResult(SaxSeries needle, SaxDistance[] haystack_subsequences, SaxProperties properties) {
		super();
		this.needle = needle;
		this.haystack_subsequences = haystack_subsequences;
		this.properties = properties;
	}

	public SaxSeries getNeedle() {
		return needle;
	}

	public void setNeedle(SaxSeries needle) {
		this.needle = needle;
	}

	public SaxDistance[] getHaystack_subsequences() {
		return haystack_subsequences;
	}

	public void setHaystack_subsequences(SaxDistance[] haystack_subsequences) {
		this.haystack_subsequences = haystack_subsequences;
	}

	public SaxProperties getProperties() {
		return properties;
	}

	public void setProperties(SaxProperties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SAX RESULTS. Sax-Index " + properties.getSax_index() + ", shift by " + properties.getShift_by()
				+ " indizes");
		sb.append("\n\tNeedle: [" + needle.toString() + "]");
		sb.append(" \n\tResults: \n");
		for (int i = 0; i < +haystack_subsequences.length; i++) {
			sb.append("\t\t" + haystack_subsequences[i].toString() + "\n");
		}
		return sb.toString();
	}
}
