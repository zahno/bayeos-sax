package de.unibayreuth.bayceer.bayeos.sax;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import de.unibayreuth.bayceer.bayeos.dto.sax.ResultCollection;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxAggregationInterval;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxDistance;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxDistanceFunction;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxProperties;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxResult;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxSeries;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxShiftBy;
import de.unibayreuth.bayceer.bayeos.servlet.ServletInitializer;
import de.unibayreuth.bayceer.bayeos.util.MirroredTuple;

/**
 * Class for SAX Similarity Search Algorithm
 * 
 * @author christiane-goehring
 *
 */
public class Sax {

	private static Logger log = Logger.getLogger(Sax.class.getName());

	/**
	 * This method finds the most similar subsequences of one or more time
	 * series, the "haystacks", to another time series, the "needle". It returns
	 * a SaxResult object containing the x best results.
	 * 
	 * @param needle_id
	 *            The id of the time series that is beeing looked for
	 * @param needle_description
	 *            A description of that time series
	 * 
	 * @param needle_from
	 *            A timestamp from where that time series starts
	 * @param needle_until
	 *            A timestamp where that time series ends
	 * @param haystack_ids
	 *            An array containing one or more ids of the haystack time
	 *            series
	 * 
	 * @param haystack_descriptions
	 *            An array with a description of those time series in the same
	 *            order
	 * 
	 * @param haystack_from
	 *            A timestamp from where those time series start
	 * 
	 * @param haystack_until
	 *            A timestamp where those time series end
	 * @param sax_index
	 *            A unique identifier that defines the used sax method (id in
	 *            bayeos data base table "sax.sax_index")
	 * @param aggr_int_seconds
	 *            The aggregation interval in seconds
	 * @param aggr_int_string
	 *            A description of the used aggregation interval (eg. "6 hours")
	 * @param shift_by_indices
	 *            An integer describing how many indices the time window is
	 *            being shift by to receive subsequences of the haystack
	 * @param shiftby_description
	 *            A description of the used "shift by" parameter (eg. "1 day")
	 * @param missing_values
	 *            Describes the portion of max missing values in each haystack
	 *            time series subsequence(between 0 and 1)
	 * @param no_best_hits
	 *            Number of how many best hits are being returned
	 * @param sax_distances
	 *            A hashtable mapping the SAX-letter to distances that is being
	 *            used as a lookup table to calculate the distance between two
	 *            time series
	 * @param distanceTable_id
	 *            An unique identifier for the used distance-function (id in the
	 *            bayeos data base table "sax.sax_distances")
	 * @param distanceTable_name
	 *            A description of the distance function (eg. "MaxDist" or
	 *            "MinDist")
	 * @param compression_factor
	 *            The factor the data is compressed by, eg. original data in 10
	 *            min interval, aggregation interval is 60 minutes ->
	 *            compression factor is 6
	 * @return A SaxResult object containing the best x hits along with the
	 *         euclidean distance to each subsequence
	 */
	public static SaxResult similaritySearch(int needle_id, String needle_description, DateTime needle_from,
			DateTime needle_until, int[] haystack_ids, String[] haystack_descriptions, DateTime[] haystack_from,
			DateTime[] haystack_until, int sax_index, int aggr_int_seconds, String aggr_int_string,
			int shift_by_indices, String shiftby_description, double missing_values, int no_best_hits,
			HashMap<MirroredTuple<Character, Character>, Double> sax_distances, int distanceTable_id,
			String distanceTable_name, int compression_factor) {

		log.info("Starting SAX similarity search. Shift window by " + shift_by_indices + " indizes \n\tNeedle: [ID: "
				+ needle_id + ", from " + needle_from.toString() + " until " + needle_until.toString()
				+ "]\n\tHaystack: [ID: " + haystack_ids + ", from " + haystack_from.toString() + " until "
				+ haystack_until.toString() + "]");

		SaxDistanceFunction distanceFunction = new SaxDistanceFunction(distanceTable_id, distanceTable_name);

		ResultCollection results = new ResultCollection(no_best_hits);

		// Retreive the sax sequence of the needle from the bayeos data base
		String saxstring_needle = get_sax_subsequence(needle_id, needle_from, needle_until, sax_index);

		// Create the needle object
		SaxSeries needle = new SaxSeries(needle_id, needle_description, compression_factor, needle_from, needle_until,
				saxstring_needle);

		String saxstring_haystack, sub_saxstring_haystack;
		int maxindex, currentindex, noMinValidValues;
		Double distance;

		// The number of values that need to be valid at least
		noMinValidValues = (int) ((1 - missing_values) * ((double) saxstring_needle.length()));

		// loop through all haystacks
		for (int i = 0; i < haystack_ids.length; i++) {
			log.info("\tNeedle: " + needle_id + " from " + needle_from + " until " + needle_until + "\n\tHaystack: "
					+ haystack_ids[i] + " from " + haystack_from[i] + " until " + haystack_until[i]);

			if (haystack_from[i] == null || haystack_until[i] == null) {
				log.info("There seems to be no SAX String for id " + haystack_ids[i] + " with sax-index " + sax_index);
				continue;
			}

			currentindex = 0;

			// Retreive the sax sequence of the haystack with current id
			saxstring_haystack = get_sax_subsequence(haystack_ids[i], haystack_from[i], haystack_until[i], sax_index);

			// This (start-)index can't be exceeded when shifting the time
			// window
			maxindex = saxstring_haystack.length() - saxstring_needle.length();

			// Shift the time window over the subsequence
			while (currentindex <= maxindex) {

				distance = null;
				// Cut the haystack string into a substring at the currentindex
				// and currentindex + needle-length
				sub_saxstring_haystack = saxstring_haystack.substring(currentindex,
						currentindex + saxstring_needle.length());

				// calculate the euclidean distance using the two strings and
				// the lookup table (is null if there are too many missing
				// values)
				distance = getDistance(saxstring_needle, sub_saxstring_haystack, sax_distances, compression_factor,
						noMinValidValues, distanceFunction);

				if (distance != null) {
					// Create a SaxSeries object and insert (if possible) into
					// the best hits result
					SaxSeries sub_haystack = new SaxSeries(haystack_ids[i], haystack_descriptions[i],
							compression_factor,
							haystack_from[i].withPeriodAdded(Period.seconds(aggr_int_seconds), currentindex),
							haystack_from[i].withPeriodAdded(Period.seconds(aggr_int_seconds),
									currentindex + sub_saxstring_haystack.length()),
							sub_saxstring_haystack);
					results.insert(new SaxDistance(sub_haystack, distance));

				}
				currentindex += shift_by_indices;

			}

		}

		// trims empty fields from array
		results.trimResultsArray();

		log.info(results.toString());

		SaxResult saxResult = new SaxResult(needle, results.getResults(),
				new SaxProperties(new SaxAggregationInterval(aggr_int_seconds),
						new SaxShiftBy(shift_by_indices, shiftby_description), distanceFunction, sax_index,
						missing_values, no_best_hits));

		return saxResult;

	}

	/**
	 * Calculates the euclidean distance between two time series using a lookup
	 * table
	 * 
	 * @param saxstring_needle
	 * @param sub_saxstring_haystack
	 * @param sax_distances
	 * @param noMinValidValues
	 * @param distancefuntion
	 * @return
	 */
	private static Double getDistance(String saxstring_needle, String sub_saxstring_haystack,
			HashMap<MirroredTuple<Character, Character>, Double> sax_distances, int compression_factor,
			int noMinValidValues, SaxDistanceFunction distancefuntion) {
		Double squaredsum = null;
		int noValidValues = saxstring_needle.length();

		for (int letterindex = 0; letterindex < sub_saxstring_haystack.length(); letterindex++) {
			Double mindist = sax_distances.get(new MirroredTuple<Character, Character>(
					saxstring_needle.charAt(letterindex), sub_saxstring_haystack.charAt(letterindex)));

			if (mindist != null) {
				if (squaredsum == null)
					squaredsum = 0.0;
				squaredsum += mindist * mindist;
			} else {
				noValidValues--;
				if (noValidValues <= noMinValidValues) {
					squaredsum = null;
					break;
				}
			}
		}

		return (squaredsum != null) ? Math.sqrt(squaredsum / noValidValues) : null;

		// if (distancefuntion.getType() == SaxDistanceFunction.MINDIST) {
		// return (squaredsum != null) ? Math.sqrt(squaredsum) *
		// Math.sqrt(compression_factor) : null;
		// } else if (distancefuntion.getType() == SaxDistanceFunction.MAXDIST)
		// {
		// return (squaredsum != null) ? Math.sqrt(squaredsum) : null;
		// } else
		// return null;

	}

	/**
	 * Returns the sax subsequence in a certain time interval [from, until] by
	 * connecting to the database
	 * 
	 * @param id
	 *            Identifier of the time series
	 * @param from
	 *            Timestamp
	 * @param until
	 *            Timestamp
	 * @param sax_index
	 *            SAX Index
	 * @return
	 */
	private static String get_sax_subsequence(int id, DateTime from, DateTime until, int sax_index) {
		String res = null;

		try (Connection c = ServletInitializer.getConnection();
				CallableStatement callSt = c.prepareCall("{?= call sax.get_sax_subsequence(?,?,?)}");) {
			callSt.setInt(1, id);
			callSt.setTimestamp(2, new java.sql.Timestamp(from.getMillis()));
			callSt.setTimestamp(3, new java.sql.Timestamp(until.getMillis()));
			callSt.setInt(4, sax_index);

			callSt.registerOutParameter(1, Types.VARCHAR);
			callSt.execute();
			res = callSt.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

}
