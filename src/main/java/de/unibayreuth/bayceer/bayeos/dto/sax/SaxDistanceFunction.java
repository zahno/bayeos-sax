package de.unibayreuth.bayceer.bayeos.dto.sax;

/**
 * Represents a distance function (eg. MinDist or MaxDist)
 * @author christiane-goehring
 *
 */
public class SaxDistanceFunction {

	private String distanceFunctionName;
	// the id from the bayeos database table sax.sax_distances
	private int distances_id;

	public SaxDistanceFunction(int distances_id, String distanceFunctionName) {
		this.distances_id = distances_id;
		this.distanceFunctionName = distanceFunctionName;
	}

	public int getDistances_id() {
		return distances_id;
	}

	public void setDistances_id(int distances_id) {
		this.distances_id = distances_id;
	}

	public String getDistanceFunctionName() {
		return distanceFunctionName;
	}

	public void setDistanceFunctionName(String distanceFunctionName) {
		this.distanceFunctionName = distanceFunctionName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SaxDistanceFunction) {
			return this.distances_id == ((SaxDistanceFunction) obj).distances_id
					&& this.distanceFunctionName.equals(((SaxDistanceFunction) obj).distanceFunctionName);
		}
		return false;
	}

}
