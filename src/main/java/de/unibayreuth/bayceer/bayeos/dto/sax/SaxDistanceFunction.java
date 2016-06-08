package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Represents a distance function (eg. MinDist or MaxDist)
 * 
 * @author christiane-goehring
 *
 */
public class SaxDistanceFunction {

	private String description;
	// the id from the bayeos database table sax.sax_distances
	private int id;
	private int type;

	public static final int UNDEFINED = 0;
	public static final int MINDIST = 1;
	public static final int MAXDIST = 2;

	public SaxDistanceFunction(int id, String description) {
		this.id = id;
		this.description = description;
		if (description.equals("MinDist"))
			setType(MINDIST);
		else if (description.equals("MaxDist"))
			setType(MAXDIST);
		else
			setType(UNDEFINED);

	}

	public SaxDistanceFunction(int type) {
		this.type = type;
		switch (type) {
		case MAXDIST:
			id = 2;
			description = "MaxDist";
			break;
		case MINDIST:
			id = 1;
			description = "MinDist";
			break;
		default:
			id = 0;
			description = "undefined";
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SaxDistanceFunction) {
			return this.type == ((SaxDistanceFunction) obj).getType();
		}
		return false;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
