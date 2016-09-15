package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * An object containing information on how far the time window is being shift by
 * whilst executing the sax similarity search
 * 
 * @author christiane-goehring
 *
 */
public class SaxShiftBy implements Comparable<SaxShiftBy> {

	// eg "6 hours"
	private String description;
	// number of indices that corresponds to; time interval in seconds (eg.
	// 14.400 seconds for 6 hours)
	private int indices;
	private int seconds;

	public SaxShiftBy(){
		
	}
	
	public SaxShiftBy(int shiftby_indices, String description) {
		this.indices = shiftby_indices;
		this.description = description;
		switch (description) {
		case "10 minutes":
			this.seconds = (600);
		case "30 minutes":
			this.seconds = (3 * 600);
		case "1 hour":
			this.seconds = (6 * 600);
		case "2 hours":
			this.seconds = (12 * 600);
		case "4 hours":
			this.seconds = (24 * 600);
		case "6 hours":
			this.seconds = (36 * 600);
		case "12 hours":
			this.seconds = (72 * 600);
		case "1 day":
			this.seconds = (144 * 600);
		}
	}

	public SaxShiftBy(String description) {
		if (description == null)
			return;
		this.indices = 0;

		this.description = description;
		switch (description) {
		case "10 minutes":
			this.seconds = (600);
			break;
		case "30 minutes":
			this.seconds = (3 * 600);
			break;
		case "1 hour":
			this.seconds = (6 * 600);
			break;
		case "2 hours":
			this.seconds = (12 * 600);
			break;
		case "4 hours":
			this.seconds = (24 * 600);
			break;
		case "6 hours":
			this.seconds = (36 * 600);
			break;
		case "12 hours":
			this.seconds = (72 * 600);
			break;
		case "1 day":
			this.seconds = (144 * 600);
			break;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIndices() {
		return indices;
	}

	public void setIndices(int indices) {
		this.indices = indices;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	@Override
	public int compareTo(SaxShiftBy o) {
		return this.seconds - o.getSeconds();
	}

	@Override
	public String toString() {
		if (indices == 0) {
			return "Shift By: description = " + description + ", seconds = " + seconds;
		}
		return "Shift By: description = " + description + ", seconds = " + seconds + ", equals indices = " + indices;

	}
}
