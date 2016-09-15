package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Represents the sax index
 * 
 * @author christiane-goehring
 *
 */
public class SaxIndex {

	// theid from the bayeos database table sax.sax_index
	private int saxIndex;
	private String description;

	public SaxIndex(int saxIndex, String description) {
		this.saxIndex = saxIndex;
		this.description = description;
	}

	public SaxIndex() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSaxIndex() {
		return saxIndex;
	}

	public void setSaxIndex(int saxIndex) {
		this.saxIndex = saxIndex;
	}

	@Override
	public String toString() {
		return "SaxIndex: description = " + description + ", index = " + saxIndex;
	}

}
