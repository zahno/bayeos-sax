package de.unibayreuth.bayceer.bayeos.dto.sax;

import org.joda.time.DateTime;

/**
 * A time series represented as a SAX sequence. Contains the id and description
 * (identical to the values from the bayeos database table "messungen", columns
 * "id" and "bezeichnung") as well as from and until timestamps and the
 * corresponding SAX-string.
 * 
 * @author christiane-goehring
 *
 */
public class SaxSeries {

	// from the bayeos database table "messungen", columns "id" and "bezeichnung
	private int id;
	private String description;
	private DateTime from, until;
	private String sax;
	private int compression_factor;
	// timestamp 
	private long from_seconds, until_seconds;

	public long getFrom_seconds() {
		return from_seconds;
	}

	public void setFrom_seconds(long from_seconds) {
		this.from_seconds = from_seconds;
	}

	public long getUntil_seconds() {
		return until_seconds;
	}

	public void setUntil_seconds(long until_seconds) {
		this.until_seconds = until_seconds;
	}

	public SaxSeries(int id, String description, int compression_factor, DateTime from, DateTime until, String sax) {
		this.id = id;
		this.from = from;
		this.until = until;
		this.sax = sax;
		this.setDescription(description);
		this.compression_factor = compression_factor;
	}

	public SaxSeries(int id, String description, long from, long until) {
		this.id = id;
		this.description = description;
		this.setFrom_seconds(from);
		this.setUntil_seconds(until);

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DateTime getFrom() {
		return from;
	}

	public void setFrom(DateTime from) {
		this.from = from;
	}

	public DateTime getUntil() {
		return until;
	}

	public void setUntil(DateTime until) {
		this.until = until;
	}

	public String getSax() {
		return sax;
	}

	public void setSax(String sax) {
		this.sax = sax;
	}

	@Override
	public String toString() {
		return "ID " + id + " from " + from + " until " + until;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCompression_factor() {
		return compression_factor;
	}

	public void setCompression_factor(int compression_factor) {
		this.compression_factor = compression_factor;
	}

}
