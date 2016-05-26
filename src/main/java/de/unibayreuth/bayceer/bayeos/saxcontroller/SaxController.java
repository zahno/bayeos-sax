package de.unibayreuth.bayceer.bayeos.saxcontroller;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import de.unibayreuth.bayceer.bayeos.dto.dataseries.DataPoint;
import de.unibayreuth.bayceer.bayeos.dto.dataseries.DataSeries;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxAggregationInterval;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxDistanceFunction;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxProperties;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxResult;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxSeries;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxShiftBy;
import de.unibayreuth.bayceer.bayeos.sax.Sax;
import de.unibayreuth.bayceer.bayeos.servlet.ServletInitializer;
import de.unibayreuth.bayceer.bayeos.util.MirroredTuple;
import de.unibayreuth.bayceer.bayeos.util.Tuple;

/**
 * Controller for the servlet.
 * 
 * @author christiane-goehring
 *
 */
@Path("/app")
public class SaxController {

	static Logger log = Logger.getLogger(SaxController.class.getName());
	@Context
	private HttpServletRequest httpRequest;

	/**
	 * Finds all time series that are available in SAX representation
	 * 
	 * @return List of SaxSeries
	 */
	@GET
	@Path("/findSaxSeries")
	@Produces("application/json")
	public List<SaxSeries> findAvailableSaxSeries() {
		log.debug("SaxController.findAvailableSaxSeries()");

		// Call Model ?
		try (Connection con = ServletInitializer.getConnection();
				ResultSet resultSet_messreihenId = con.createStatement()
						.executeQuery("SELECT DISTINCT messung_id FROM sax.sax_measured_data");

		) {

			log.info("SELECT DISTINCT messung_id FROM sax.sax_measured_data");

			if (!resultSet_messreihenId.next()) {
				log.info("No entries in table sax.sax_measured_data.");
			}

			List<SaxSeries> ret = new ArrayList<SaxSeries>(10);

			do {
				try (ResultSet resultSet_messreihenBezeichnung = con.createStatement().executeQuery(
						"SELECT bezeichnung FROM messungen WHERE id = " + resultSet_messreihenId.getInt(1));
						ResultSet resultSet_from_until = con.createStatement().executeQuery(
								("select min(von), max(bis) from sax.sax_measured_data_values WHERE sax_measured_data_values.reihe_id = "
										+ resultSet_messreihenId.getInt(1)))) {
					log.info("SELECT bezeichnung FROM messungen WHERE id = " + resultSet_messreihenId.getInt(1));
					resultSet_messreihenBezeichnung.next();
					resultSet_from_until.next();

					ret.add(new SaxSeries(resultSet_messreihenId.getInt(1),
							resultSet_messreihenBezeichnung.getString(1),
							(resultSet_from_until.getTimestamp(1).getTime() / 1000),
							(resultSet_from_until.getTimestamp(2).getTime() / 1000)));

				}

			} while (resultSet_messreihenId.next());

			return ret;
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns time series data to display in a graph in an appropriate
	 * resolution
	 * 
	 * @param id
	 *            ID of the time series
	 * @param from
	 *            Timestamp
	 * @param until
	 *            Timestamp
	 * @return DataSeries
	 */
	@GET
	@Path("/GetDataToId/{id}/{from}/{until}")
	@Produces("application/json")
	public DataSeries getData(@NotNull @PathParam("id") final Integer id,
			@NotNull @PathParam("from") final Integer from, @NotNull @PathParam("until") final Integer until) {

		String bestTable = getAppropriateTable(id, from, until);

		try (Connection con = ServletInitializer.getConnection();
				PreparedStatement st = con.prepareStatement("select von, wert from " + bestTable + " WHERE id = " + id
						+ " AND von >= to_timestamp(" + from + ") AND von <= to_timestamp(" + until + ")");
				ResultSet result = st.executeQuery();
				ResultSet resultSet_messreihenBezeichnung = con.createStatement()
						.executeQuery("SELECT bezeichnung FROM messungen WHERE id = " + id)) {
			log.debug(st.toString());

			// get description
			log.info("SELECT bezeichnung FROM messungen WHERE id = " + id);
			resultSet_messreihenBezeichnung.next();

			List<DataPoint> data = new ArrayList<>();

			while (result.next()) {
				data.add(new DataPoint(result.getTimestamp(1).getTime() / 1000, result.getDouble(2)));
			}

			return new DataSeries(resultSet_messreihenBezeichnung.getString(1), data);
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Determines the name of the data base table to receive data points in an
	 * appropriate resolution
	 * 
	 * @param id
	 *            ID of the time series
	 * @param from
	 *            Timestamp
	 * @param until
	 *            Timestamp
	 * @return Name of the best bayeos data base table
	 */
	private String getAppropriateTable(Integer id, Integer from, Integer until) {
		String[] tables = new String[5];
		int[] entrycount = new int[5];
		tables[0] = "massendaten";
		tables[1] = "aggr_avg_30min";
		tables[2] = "aggr_avg_hour";
		tables[3] = "aggr_avg_day";
		tables[4] = "aggr_avg_year";

		int i;

		for (i = 0; i < tables.length; i++) {

			try (Connection con = ServletInitializer.getConnection();
					PreparedStatement st = con.prepareStatement("SELECT sax.count_estimate('select * from " + tables[i]
							+ " WHERE id = " + id + " AND von >= to_timestamp(" + from + ") AND von <= to_timestamp("
							+ until + ")')");
					ResultSet result = st.executeQuery();) {

				result.next();

				int rowCount = result.getInt(1);

				// approximated count comes back < 100: get actual count
				if (rowCount < 100) {
					try (PreparedStatement st2 = con
							.prepareStatement("select count(*) from " + tables[i] + " WHERE id = " + id
									+ " AND von >= to_timestamp(" + from + ") AND von <= to_timestamp(" + until + ")");
							ResultSet result2 = st2.executeQuery();) {
						result2.next();
						rowCount = result2.getInt(1);
						if (rowCount == 0) {
							entrycount[i] = rowCount;
							continue;
						}
					}
				}
				entrycount[i] = rowCount;

				if (rowCount <= 10000) {
					return tables[i];
				}

			} catch (SQLException e) {
				log.error(e.getMessage());
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		for (int j = tables.length - 1; j >= 0; j--) {
			if (entrycount[j] != 0) {
				return tables[j];
			}
		}

		return null;
	}

	/**
	 * Returns a list of dataseries for two time series with unique IDs in some
	 * interval. This method is used to receive data for the rickshaw graph on
	 * the results page.
	 * 
	 * @param needle_id
	 *            Needle ID
	 * @param needle_from
	 *            Timestamp from
	 * @param needle_until
	 *            Timestamp until
	 * @param haystack_id
	 *            Haystack ID
	 * @param haystack_from
	 *            Timestamp from
	 * @param haystack_until
	 *            Timestamp until
	 * @return List containing two DataSeries
	 */
	@GET
	@Path("/GetDataToIds/{needle_id}/{needle_from}/{needle_until}/{haystack_id}/{haystack_from}/{haystack_until}")
	@Produces("application/json")
	public List<DataSeries> getDataToIds(@NotNull @PathParam("needle_id") final Integer needle_id,
			@NotNull @PathParam("needle_from") final Integer needle_from,
			@NotNull @PathParam("needle_until") final Integer needle_until,
			@NotNull @PathParam("haystack_id") final Integer haystack_id,
			@NotNull @PathParam("haystack_from") final Integer haystack_from,
			@NotNull @PathParam("haystack_until") final Integer haystack_until) {

		String bestTable = getAppropriateTable(needle_id, needle_from, needle_until);

		try (Connection con = ServletInitializer.getConnection();
				PreparedStatement needle_val_st = con.prepareStatement(
						"select von, wert from " + bestTable + " WHERE id = " + needle_id + " AND von >= to_timestamp("
								+ needle_from + ") AND von <= to_timestamp(" + needle_until + ") ");
				ResultSet needle_values = needle_val_st.executeQuery();
				ResultSet needle_description = con.createStatement()
						.executeQuery("SELECT bezeichnung FROM messungen WHERE id = " + needle_id);

				PreparedStatement haystack__val_st = con.prepareStatement("select von, wert from " + bestTable
						+ " WHERE id = " + haystack_id + " AND von >= to_timestamp(" + haystack_from
						+ ") AND von <= to_timestamp(" + haystack_until + ") ");
				ResultSet haystack_values = haystack__val_st.executeQuery();
				ResultSet haystack_description = con.createStatement()
						.executeQuery("SELECT bezeichnung FROM messungen WHERE id = " + haystack_id)) {

			// get description
			needle_description.next();
			haystack_description.next();

			List<DataSeries> res = new ArrayList<>();

			List<DataPoint> needle_data = new ArrayList<>();
			List<DataPoint> haystack_data = new ArrayList<>();

			while (needle_values.next()) {
				needle_data.add(
						new DataPoint(haystack_from + ((needle_values.getTimestamp(1).getTime() / 1000) - needle_from),
								needle_values.getDouble(2)));
			}
			while (haystack_values.next()) {
				haystack_data.add(
						new DataPoint(haystack_values.getTimestamp(1).getTime() / 1000, haystack_values.getDouble(2)));
			}

			res.add(new DataSeries(needle_description.getString(1), needle_data));
			res.add(new DataSeries(haystack_description.getString(1), haystack_data));

			return res;
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Obtains the available Distance Measurement Functions to a certain
	 * aggregation interval id
	 * 
	 * @param aggr_int_id
	 *            Aggregation Interval ID from bayeos database table
	 *            sax.aggregation_interval
	 * @return A List of available Distance Functions
	 */
	@GET
	@Path("/GetDistanceFunctions/{id}")
	@Produces("application/json")
	public List<SaxDistanceFunction> getDistanceFunctions(@NotNull @PathParam("id") final Integer aggr_int_id) {

		try (Connection con = ServletInitializer.getConnection();
				PreparedStatement st = con.prepareStatement(
						"select distinct id_sax_limits from sax.sax_index where id_aggr_int = " + aggr_int_id);
				ResultSet result = st.executeQuery()) {

			log.debug(st.toString());

			List<SaxDistanceFunction> distanceFunctions = new ArrayList<>();

			while (result.next()) {
				try (PreparedStatement st2 = con.prepareStatement(
						"select id, name FROM sax.sax_distances WHERE id_sax_limits = " + result.getInt(1));
						ResultSet result2 = st2.executeQuery()) {
					result2.next();

					SaxDistanceFunction id = new SaxDistanceFunction(result2.getInt(1), result2.getString(2));
					distanceFunctions.add(id);
				}
			}

			return distanceFunctions;

		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Obtains the available aggregation intervals to a certain time series ID
	 * 
	 * @param timeSeriesID
	 *            ID of a time series
	 * @return A List of available Aggregation Intervals
	 */
	@GET
	@Path("/GetAggregationIntervals/{id}")
	@Produces("application/json")
	public List<SaxAggregationInterval> getAggregationIntervals(@NotNull @PathParam("id") final Integer timeSeriesID) {

		try (Connection con = ServletInitializer.getConnection();
				PreparedStatement st = con.prepareStatement(
						"select distinct id_aggr_int from sax.sax_measured_data INNER JOIN sax.sax_index ON (sax.sax_measured_data.saxtable_id = sax.sax_index.id AND sax_measured_data.messung_id = "
								+ timeSeriesID + ")");
				ResultSet result = st.executeQuery()) {

			log.debug(st.toString());

			List<SaxAggregationInterval> aggr_intervals = new ArrayList<>();

			while (result.next()) {

				try (PreparedStatement st2 = con.prepareStatement(
						"select description from sax.aggregation_interval where id = " + result.getInt(1));
						ResultSet result2 = st2.executeQuery()) {
					result2.next();
					SaxAggregationInterval id = new SaxAggregationInterval(result.getInt(1), result2.getString(1));
					aggr_intervals.add(id);
				}
			}

			return aggr_intervals;

		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Obtains the unique sax index that belongs to a aggregation-interval-ID
	 * and distance-function-ID combination
	 * 
	 * @param aggr_int_id
	 *            ID of the aggregation interval
	 * @param distance_func_id
	 *            ID of the distance function
	 * @return The unique sax index that belongs to that tuple
	 */
	@GET
	@Path("/GetSaxIndex/{aggr_int_id}/{distance_func_id}")
	@Produces("application/json")
	public int getSaxIndex(@NotNull @PathParam("aggr_int_id") final Integer aggr_int_id,
			@NotNull @PathParam("distance_func_id") final Integer distance_func_id) {

		try (Connection con = ServletInitializer.getConnection();
				PreparedStatement st = con
						.prepareStatement("select id_sax_limits from sax.sax_distances where id = " + distance_func_id);
				ResultSet result = st.executeQuery()) {

			log.debug(st.toString());
			result.next();

			try (PreparedStatement st2 = con.prepareStatement("select id from sax.sax_index where id_sax_limits = "
					+ result.getInt(1) + " and id_aggr_int = " + aggr_int_id); ResultSet result2 = st2.executeQuery()) {
				result2.next();
				log.info("sax index is " + result2.getInt(1));
				return result2.getInt(1);
			}

		} catch (SQLException e) {
			log.error(e.getMessage());
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Reads request parameters needed for the SAX similarity search algorithm
	 * and then executes the search.
	 * 
	 * @return The results of that search in the form of a SaxResult object.
	 */
	@GET
	@Path("/SAX")
	@Produces("application/json")
	public SaxResult startSax() {

		String needle_description, shiftby_string, aggr_int_string, distanceTable_name;
		String[] haystack_descriptions;
		DateTime needle_from, needle_until, haystack_min_from, haystack_max_until;
		int needle_id, sax_index, no_best_hits, distanceTable_id, aggr_int_id, shift_by;
		int[] haystack_ids;
		double missing_values;

		needle_id = Integer.parseInt(httpRequest.getParameter("needle_id"));

		needle_description = httpRequest.getParameter("needle_description");
		byte[] bytes = needle_description.getBytes(StandardCharsets.ISO_8859_1);
		needle_description = new String(bytes, StandardCharsets.UTF_8);

		needle_from = new DateTime(Long.parseLong(httpRequest.getParameter("needle_from")));
		needle_until = new DateTime(Long.parseLong(httpRequest.getParameter("needle_until")));

		haystack_ids = new int[httpRequest.getParameterValues("haystack_ids[]").length];
		for (int i = 0; i < haystack_ids.length; i++) {
			haystack_ids[i] = Integer.parseInt(httpRequest.getParameterValues("haystack_ids[]")[i]);
		}
		haystack_descriptions = new String[httpRequest.getParameterValues("haystack_descriptions[]").length];
		for (int i = 0; i < haystack_ids.length; i++) {
			haystack_descriptions[i] = httpRequest.getParameterValues("haystack_descriptions[]")[i];
			byte[] bytes2 = haystack_descriptions[i].getBytes(StandardCharsets.ISO_8859_1);
			haystack_descriptions[i] = new String(bytes2, StandardCharsets.UTF_8);

		}

		haystack_min_from = new DateTime(Long.parseLong(httpRequest.getParameter("haystack_from")));
		haystack_max_until = new DateTime(Long.parseLong(httpRequest.getParameter("haystack_until")));

		// settings
		sax_index = Integer.parseInt(httpRequest.getParameter("sax_index"));
		shiftby_string = httpRequest.getParameter("shiftby");
		aggr_int_string = httpRequest.getParameter("aggr_int");
		missing_values = Double.parseDouble(httpRequest.getParameter("missing_values"));
		no_best_hits = Integer.parseInt(httpRequest.getParameter("no_best_hits"));
		distanceTable_id = Integer.parseInt(httpRequest.getParameter("distanceTable_id"));
		distanceTable_name = httpRequest.getParameter("distanceTable_name");
		aggr_int_id = Integer.parseInt(httpRequest.getParameter("aggr_int_id"));

		// get the aggregation interval depending on aggregation interval
		int aggr_int_seconds = get_agg_int_in_seconds(aggr_int_id);

		// get from and until of available sax series
		Tuple<DateTime[], DateTime[]> haystacks_from_until = getHaystacks_FromUntil(haystack_ids, sax_index,
				haystack_min_from, haystack_max_until);
		DateTime[] haystack_from = haystacks_from_until.x, haystack_until = haystacks_from_until.y;

		// Retrieve distance hashtable on first execution and set as session
		// object, otherwise retrieve that session object
		Object sessionObj_saxdistances = httpRequest.getSession().getAttribute("distanceTable_id" + distanceTable_id);
		@SuppressWarnings("unchecked")
		HashMap<MirroredTuple<Character, Character>, Double> sax_distances = (sessionObj_saxdistances instanceof HashMap<?, ?>
				? (HashMap<MirroredTuple<Character, Character>, Double>) sessionObj_saxdistances : null);

		if (sax_distances == null) {
			sax_distances = getDistanceTableToSaxIndex(distanceTable_id);
			httpRequest.getSession().setAttribute("distanceTable_id" + distanceTable_id, sax_distances);
		}

		shift_by = get_no_shiftby(aggr_int_id, shiftby_string);

		SaxResult sax = Sax.sax(needle_id, needle_description, needle_from, needle_until, haystack_ids,
				haystack_descriptions, haystack_from, haystack_until, sax_index, aggr_int_seconds, aggr_int_string,
				shift_by, shiftby_string, missing_values, no_best_hits, sax_distances, distanceTable_id,
				distanceTable_name);

		log.info(sax.toString());

		return sax;
	}

	/**
	 * Returns the actually available from- and until-timestamps in a given
	 * interval.
	 * 
	 * @param haystack_ids
	 *            Array of the time series IDs
	 * @param sax_index
	 *            The used SAX index
	 * @param haystack_min_from
	 *            Timestamp
	 * @param haystack_max_until
	 *            Timestamp
	 * @return A tuple that contains the available from-timestamps (ge.
	 *         haystack_min_from) and until-timestamp (le.haystack_max_until)
	 */
	private static Tuple<DateTime[], DateTime[]> getHaystacks_FromUntil(int[] haystack_ids, int sax_index,
			DateTime haystack_min_from, DateTime haystack_max_until) {
		DateTime[] haystack_from = new DateTime[haystack_ids.length],
				haystack_until = new DateTime[haystack_ids.length];
		DateTime server_from, server_until;

		for (int i = 0; i < haystack_ids.length; i++) {
			try (Connection con = ServletInitializer.getConnection();

					PreparedStatement st = con
							.prepareStatement("select von, bis from sax.sax_measured_data_values WHERE reihe_id = "
									+ haystack_ids[i] + " AND saxtable_id = " + sax_index);
					ResultSet result = st.executeQuery();) {
				while (result.next()) {
					server_from = new DateTime(new Date(result.getTimestamp(1).getTime()));
					server_until = new DateTime(new Date(result.getTimestamp(2).getTime()));
					// outside available interval
					if (server_until.isBefore(haystack_min_from) || server_from.isAfter(haystack_max_until)) {
						haystack_from[i] = null;
						haystack_until[i] = null;
						continue;
					}
					haystack_from[i] = haystack_min_from.isBefore(server_from.getMillis()) ? server_from
							: haystack_min_from;
					haystack_until[i] = haystack_max_until.isAfter(server_until.getMillis()) ? server_until
							: haystack_max_until;
				}
			} catch (SQLException e) {
				log.error(e.getMessage());
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		return new Tuple<DateTime[], DateTime[]>(haystack_from, haystack_until);
	}

	/**
	 * Obtains a hashmap mapping all SAX-letter-combinations to a distance value
	 * 
	 * @param distance_func_id
	 *            The used distance function ID
	 * @return A hashmap containing all SAX-letter-combinations and their
	 *         distance value.
	 */
	private static HashMap<MirroredTuple<Character, Character>, Double> getDistanceTableToSaxIndex(
			int distance_func_id) {
		HashMap<MirroredTuple<Character, Character>, Double> saxdistance = new HashMap<MirroredTuple<Character, Character>, Double>();

		try (Connection c = ServletInitializer.getConnection();
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM sax.sax_distances_values WHERE id_sax_distances = " + distance_func_id)) {

			while (rs.next()) {
				double distance = rs.getDouble("distance");
				char b1 = rs.getString("letter_1").charAt(0);
				char b2 = rs.getString("letter_2").charAt(0);

				if (!saxdistance.containsKey(new MirroredTuple<Character, Character>(b1, b2)))
					saxdistance.put(new MirroredTuple<Character, Character>(b1, b2), distance);
			}

		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return saxdistance;
	}

	/**
	 * Obtains the seconds of a certain aggregation interval
	 * 
	 * @param aggr_int_id
	 *            The ID of the aggregation interval
	 * @return The corresponding number of seconds
	 */
	public static int get_agg_int_in_seconds(int aggr_int_id) {
		int res = 0;

		try (Connection c = ServletInitializer.getConnection();
				Statement stmt = c.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT EXTRACT(EPOCH FROM aggr_int) FROM sax.aggregation_interval WHERE id = "
								+ aggr_int_id);) {
			while (rs.next()) {
				res = rs.getInt(1);
			}

		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Obtains the number of indices to shift the time window by
	 * 
	 * @param aggr_int_id
	 *            The used aggregation interval ID
	 * @param shiftby
	 *            Given in Postgresql Interval Format (eg. "1 day", "6 hours"..)
	 * @return The number of indices that the time window needs to be shift by
	 */
	public static int get_no_shiftby(int aggr_int_id, String shiftby) {
		//
		int res = 0;
		try (Connection c = ServletInitializer.getConnection();
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM sax.get_no_shiftby(" + aggr_int_id + ",'" + shiftby + "'::interval)");) {
			while (rs.next()) {
				res = rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		if (res == 0)
			res = 1;

		return res;
	}

	/**
	 * Obtains approriate settings depending on the length of the needle and
	 * haystacks
	 * 
	 * @return
	 */
	@GET
	@Path("/SAXSettings")
	@Produces("application/json")
	public SaxProperties findBestSAXSettings() {

		long needle_from_seconds = Long.parseLong(httpRequest.getParameter("needle_from")) / 1000;
		long needle_until_seconds = Long.parseLong(httpRequest.getParameter("needle_until")) / 1000;

		int[] haystack_ids = new int[httpRequest.getParameterValues("haystack_ids[]").length];
		for (int i = 0; i < haystack_ids.length; i++) {
			haystack_ids[i] = Integer.parseInt(httpRequest.getParameterValues("haystack_ids[]")[i]);
		}

		long haystack_min_from_seconds = Long.parseLong(httpRequest.getParameter("haystack_from")) / 1000;
		long haystack_max_until_seconds = Long.parseLong(httpRequest.getParameter("haystack_until")) / 1000;
		// DateTime haystack_max_until = new
		// DateTime(Long.parseLong(httpRequest.getParameter("haystack_until")));

		// settings
		String[] available_aggr_int = new String[httpRequest.getParameterValues("available_aggr_int[]").length];
		for (int i = 0; i < available_aggr_int.length; i++) {
			available_aggr_int[i] = httpRequest.getParameterValues("available_aggr_int[]")[i];
			byte[] bytes2 = available_aggr_int[i].getBytes(StandardCharsets.ISO_8859_1);
			available_aggr_int[i] = new String(bytes2, StandardCharsets.UTF_8);
		}

		int[] available_aggr_int_id = new int[httpRequest.getParameterValues("available_aggr_int_id[]").length];
		for (int i = 0; i < available_aggr_int_id.length; i++) {
			available_aggr_int_id[i] = Integer.parseInt(httpRequest.getParameterValues("available_aggr_int_id[]")[i]);
		}

		HashMap<Integer, List<SaxDistanceFunction>> available_distance_func_id = new HashMap<>();
		for (int i = 0; i < available_aggr_int_id.length; i++) {
			available_distance_func_id.put(available_aggr_int_id[i], getDistanceFunctions(available_aggr_int_id[i]));
		}

		// key is tuple of available_aggr_int_id and sax_distance_function_id
		HashMap<Tuple<Integer, Integer>, Integer> available_saxindex = new HashMap<>();

		Set<Entry<Integer, List<SaxDistanceFunction>>> entrySet = available_distance_func_id.entrySet();
		for (Entry<Integer, List<SaxDistanceFunction>> entry : entrySet) {

			for (SaxDistanceFunction func : (List<SaxDistanceFunction>) entry.getValue()) {
				available_saxindex.put((new Tuple<Integer, Integer>((int) entry.getKey(), func.getDistances_id())),
						getSaxIndex((int) entry.getKey(), func.getDistances_id()));
			}
		}

		List<SaxShiftBy> available_shiftby = new ArrayList<SaxShiftBy>(
				httpRequest.getParameterValues("available_shiftby[]").length);
		for (int i = 0; i < httpRequest.getParameterValues("available_shiftby[]").length; i++) {
			String s = new String(
					(httpRequest.getParameterValues("available_shiftby[]")[i]).getBytes(StandardCharsets.ISO_8859_1),
					StandardCharsets.UTF_8);
			available_shiftby.add(new SaxShiftBy(s));
		}
		Collections.sort(available_shiftby);

		int[] available_aggr_int_seconds = new int[available_aggr_int_id.length];
		for (int i = 0; i < available_aggr_int_id.length; i++) {
			available_aggr_int_seconds[i] = get_agg_int_in_seconds(available_aggr_int_id[i]);
		}

		// period in seconds of needle
		long needlePeriod_seconds = needle_until_seconds - needle_from_seconds;

		// calculate upper bound of total haystack period
		long haystackPeriod_seconds = haystack_max_until_seconds - haystack_min_from_seconds;

		int aggr_int_seconds = 0;
		int aggr_int_id = 0;
		int sax_index = 0;
		SaxShiftBy shiftby = null;
		int distanceTable_id = 0;
		String distanceTable_name = null;

		long maxNoOfOperations = 0;
		for (int j = 0; j < haystack_ids.length; j++) {
			maxNoOfOperations += needlePeriod_seconds * (haystackPeriod_seconds - needlePeriod_seconds + 1);
		}

		// <= 7 days: 10 min
		if (needlePeriod_seconds <= 60 * 60 * 24 * 7) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 600) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}

		// <= 15 days: 30 min
		if (needlePeriod_seconds <= 60 * 60 * 24 * 15 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 1800) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		// <= 30 days: 1 hour
		if (needlePeriod_seconds <= 60 * 60 * 24 * 31 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 3600) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		// <= 60 days: 2 hours
		if (needlePeriod_seconds <= 60 * 60 * 24 * 60 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 7200) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		// <= 120 days: 4 hours
		if (needlePeriod_seconds <= 60 * 60 * 24 * 120 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 14400) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		// <= 180 days: 6 hours
		if (needlePeriod_seconds <= 60 * 60 * 24 * 180 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 21600) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}

		// <= 360 days: 12 hours
		if (needlePeriod_seconds <= 60 * 60 * 24 * 360 && aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 43200) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		if (aggr_int_seconds == 0) {
			for (int i = 0; i < available_aggr_int_seconds.length; i++) {
				if (available_aggr_int_seconds[i] == 86400) {
					aggr_int_seconds = available_aggr_int_seconds[i];
					aggr_int_id = available_aggr_int_id[i];

					break;
				}
			}
		}
		maxNoOfOperations = maxNoOfOperations / aggr_int_seconds;

		log.info("No Of Operations:" + maxNoOfOperations);

		for (int j = 0; j < available_shiftby.size(); j++) {
			log.info("Shiftby " + available_shiftby.get(j).getDescription() + " :"
					+ maxNoOfOperations * aggr_int_seconds / available_shiftby.get(j).getSeconds());
			if (maxNoOfOperations * aggr_int_seconds / available_shiftby.get(j).getSeconds() < 1500000000
					|| j == available_shiftby.size() - 1) {
				shiftby = available_shiftby.get(j);
				break;

			}
		}

		List<SaxDistanceFunction> available_distancefunc = available_distance_func_id.get(aggr_int_id);
		if (available_distancefunc.contains(new SaxDistanceFunction(2, "MaxDist"))) {
			distanceTable_id = 2;
			distanceTable_name = "MaxDist";
		} else {
			distanceTable_id = 1;
			distanceTable_name = "MinDist";
		}

		sax_index = available_saxindex.get(new Tuple<Integer, Integer>(aggr_int_id, distanceTable_id));

		SaxProperties prop = new SaxProperties(new SaxAggregationInterval(aggr_int_id, aggr_int_seconds), shiftby,
				new SaxDistanceFunction(distanceTable_id, distanceTable_name), sax_index, 0.2, 30);

		return prop;
	}

}
