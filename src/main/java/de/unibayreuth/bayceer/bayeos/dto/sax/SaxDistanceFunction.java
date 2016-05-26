package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Represents a distance function (eg. MinDist or MaxDist)
 * @author christiane-goehring
 *
 */
public class SaxDistanceFunction {

	private String description;
	// the id from the bayeos database table sax.sax_distances
	private int id;

	public SaxDistanceFunction(int id, String description) {
		this.id = id;
		this.description = description;
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
			return this.id == ((SaxDistanceFunction) obj).id
					&& this.description.equals(((SaxDistanceFunction) obj).description);
		}
		return false;
	}

}
