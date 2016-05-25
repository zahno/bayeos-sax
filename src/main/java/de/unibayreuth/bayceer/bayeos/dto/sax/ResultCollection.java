package de.unibayreuth.bayceer.bayeos.dto.sax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This data structure is used to store the x best results from the sax
 * similarity search algorithm.
 * 
 * @author christiane-goehring
 *
 */
public class ResultCollection {

	private SaxDistance[] results;
	private int worstResultIndex = 0;
	private int noOfElements = 0;

	public ResultCollection(int size) {
		results = new SaxDistance[size];
	}

	/**
	 * Finds the index in the results array where a new element with a certain
	 * distance value needs to be inserted.
	 * 
	 * 
	 * @param distance
	 * @return The index where a new entry with a certain distance value needs
	 *         to be inserted. -1, if the new entry can not be inserted into the
	 *         list (distance value too big).
	 */
	private int getIndexForNewEntry(double distance) {
		// das array ist noch nicht gefuellt: Fuege alle neuen Eintraege ein
		if (noOfElements < results.length) {
			return noOfElements;
		} else if (distance < results[worstResultIndex].getDistance()) {
			return worstResultIndex;
		} else
			return -1;
	}

	/**
	 * Inserts an element at a certain index into the results array and updates
	 * the index of the now worst result (that is the entry with the biggest
	 * distance value) in the list
	 * 
	 * @param entry
	 *            The new entry
	 * @param index
	 *            The index where the entry is supposed to be inserted in the
	 *            results array
	 */
	private void insertNewEntry(SaxDistance entry, int index) {
		results[index] = entry;
		noOfElements++;
		updateWorstResultIndex(index);
	}

	/**
	 * Inserts a new entry in the list of results if the distance of the new
	 * element is smaller than the distance of any entry currently in the
	 * results array. Thereby, the worst result will be removed from the list.
	 * 
	 * @param entry
	 *            The element that you try to insert
	 * @return true, if the entry was inserted and therefore had a smaller
	 *         distance than any of the entrys in the results list. false,
	 *         otherwise
	 */
	public boolean insert(SaxDistance entry) {
		int index = getIndexForNewEntry(entry.getDistance());
		if (index != -1) {
			insertNewEntry(entry, index);
			return true;
		}
		return false;
	}

	/**
	 * Updates the index of the currently worst result (that is the one with the
	 * highest distance)
	 * 
	 * @param newIndex
	 *            The index of the currently inserted new element
	 */
	private void updateWorstResultIndex(int newIndex) {

		for (int i = 0; i < results.length; i++) {
			if (results[i] == null) {
				return;
			}
			if (results[i].getDistance() > results[worstResultIndex].getDistance()) {
				worstResultIndex = i;
			}
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.length; i++) {
			sb.append(i + 1 + ". \t" + results[i] + "\n");
		}
		return sb.toString();
	}

	/**
	 * This method removes empty entries in case there are not as many results
	 * as there is space in the results array.
	 */
	public void trimResultsArray() {
		List<SaxDistance> list = new ArrayList<SaxDistance>();

		for (SaxDistance res : results) {
			if (res != null) {
				list.add(res);
			}
		}

		results = list.toArray(new SaxDistance[list.size()]);
		Arrays.sort(results);

	}

	public SaxDistance[] getResults() {
		return results;
	}

	public void setResults(SaxDistance[] results) {
		this.results = results;
	}

	public int getNoOfElements() {
		return noOfElements;
	}

	public void setNoOfElements(int noOfElements) {
		this.noOfElements = noOfElements;
	}

}
