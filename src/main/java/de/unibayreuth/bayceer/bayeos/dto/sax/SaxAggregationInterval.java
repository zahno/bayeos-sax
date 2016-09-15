package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Class that represents an Aggregation Interval object.
 * 
 * @author christiane-goehring
 *
 */
public class SaxAggregationInterval {

	// the ID it has in the bayeos database table sax.aggregation_interval
	private int id;
	private String description;
	// the interval in seconds
	private int seconds;

	public SaxAggregationInterval(){
		
	}
	
	public SaxAggregationInterval(int aggr_int_id, String aggr_int_description) {
		this.setDescription(aggr_int_description);
		this.setId(aggr_int_id);
		switch (aggr_int_description) {
		case "10 minutes":
			setSeconds(600);
			break;
		case "30 minutes":
			setSeconds(3 * 600);
			break;
		case "1 hour":
			setSeconds(6 * 600);
			break;
		case "2 hours":
			setSeconds(12 * 600);
			break;
		case "4 hours":
			setSeconds(24 * 600);
			break;
		case "6 hours":
			setSeconds(36 * 600);
			break;
		case "12 hours":
			setSeconds(72 * 600);
			break;
		case "1 day":
			setSeconds(144 * 600);
			break;
		}
	}

	public SaxAggregationInterval(int seconds) {
		this.setSeconds(seconds);
		switch (seconds) {
		case 600:
			this.setDescription("10 minutes");
			break;
		case 3 * 600:
			this.setDescription("30 minutes");
			break;
		case 6 * 600:
			this.setDescription("1 hour");
			break;
		case 12 * 600:
			this.setDescription("2 hours");
			break;
		case 24 * 600:
			this.setDescription("4 hours");
			break;
		case 36 * 600:
			this.setDescription("6 hours");
			break;
		case 72 * 600:
			this.setDescription("12 hours");
			break;
		case 144 * 600:
			this.setDescription("24 hours");
			break;
		}
	}

	public SaxAggregationInterval(int id, int seconds) {
		this.setId(id);
		this.setSeconds(seconds);
		switch (seconds) {
		case 600:
			this.setDescription("10 minutes");
			break;
		case 3 * 600:
			this.setDescription("30 minutes");
			break;
		case 6 * 600:
			this.setDescription("1 hour");
			break;
		case 12 * 600:
			this.setDescription("2 hours");
			break;
		case 24 * 600:
			this.setDescription("4 hours");
			break;
		case 36 * 600:
			this.setDescription("6 hours");
			break;
		case 72 * 600:
			this.setDescription("12 hours");
			break;
		case 144 * 600:
			this.setDescription("24 hours");
			break;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	@Override
	public String toString() {
		return "AggregationInterval: description = " + description + ", seconds = " + seconds + ", id = " + id;
	}

}
