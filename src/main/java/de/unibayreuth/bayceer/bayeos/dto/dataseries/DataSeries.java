package de.unibayreuth.bayceer.bayeos.dto.dataseries;

import java.util.Collections;
import java.util.List;

/**
 * Represents a time series with a name and a list of data points
 * 
 * @author christiane-goehring
 *
 */
public class DataSeries {

	String name;
	List<DataPoint> data;
	
	public DataSeries() {
	}

	public DataSeries(String name, List<DataPoint> data) {
		this.name = name;
		this.data = data;
		Collections.sort(this.data);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataPoint> getData() {
		return data;
	}

	public void setDatapoints(List<DataPoint> data) {
		this.data = data;
		Collections.sort(this.data);
	}

	@Override
	public String toString() {
		return "DataSeries : Name = " + name + ", data = " + data;
	}

}
