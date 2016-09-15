package de.unibayreuth.bayceer.bayeos.controller;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import de.unibayreuth.bayceer.bayeos.dto.dataseries.DataSeries;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxAggregationInterval;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxDistanceFunction;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxResult;
import de.unibayreuth.bayceer.bayeos.dto.sax.SaxSeries;

public class ObjektTest {

	private Client client = null;
	// private WebTarget target = null;

	@Before
	public void setup() {

		client = ClientBuilder.newClient();

	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAvailableSaxSeries() {
		WebTarget target = client.target("http://132.180.112.172/bayeos-sax/rest/app/findSaxSeries");
		List<SaxSeries> series = target.request().get(List.class);
		assertNotNull(series);
	}

	@Test
	public void testGetDataToId() {
		WebTarget target = client
				.target("http://132.180.112.172/bayeos-sax/rest/app/GetDataToId/200108/1104534000/1105034000");
		DataSeries series = target.request().get(DataSeries.class);
		assertNotNull(series);
	}

	@Test
	public void testGetDataToIds() {
		WebTarget target = client.target(
				"http://132.180.112.172/bayeos-sax/rest/app/GetDataToIds/200109/1104534000/1105034000/200110/1104534000/1105034000");
		List<DataSeries> series = target.request().get(List.class);
		assertNotNull(series);
	}

	@Test
	public void testGetDistanceFunctions() {
		WebTarget target = client.target("http://132.180.112.172/bayeos-sax/rest/app/GetDistanceFunctions/1");
		List<SaxDistanceFunction> series = target.request().get(List.class);
		assertNotNull(series.size() == 2);
	}

	@Test
	public void testGetAggregationIntervals() {
		WebTarget target = client.target("http://132.180.112.172/bayeos-sax/rest/app/GetAggregationIntervals/200111");
		List<SaxAggregationInterval> series = target.request().get(List.class);
		assertNotNull(series);
	}

	@Test
	public void testGetSaxIndex() {
		WebTarget target = client.target("http://132.180.112.172/bayeos-sax/rest/app/GetSaxIndex/1/1");
		List<SaxAggregationInterval> series = target.request().get(List.class);
		assertNotNull(series);
	}

	@Test
	public void testSimilaritySearch() {

		WebTarget target = client.target(
				"http://132.180.112.172/bayeos-sax/rest/app/SimilaritySearch?needle_id=200108&needle_description=ÖBG+HMP45&needle_from=946681200000&needle_until=1464588000000&haystack_ids%5B%5D=200108&haystack_ids%5B%5D=200109&haystack_ids%5B%5D=200110&haystack_ids%5B%5D=200111&haystack_ids%5B%5D=200112&haystack_ids%5B%5D=200113&haystack_ids%5B%5D=200114&haystack_ids%5B%5D=200115&haystack_descriptions%5B%5D=ÖBG+HMP45&haystack_descriptions%5B%5D=ÖBG+Psychrometer+trocken&haystack_descriptions%5B%5D=ÖBG+SHT21-Test+(DIY-Shield%2C+Ost)&haystack_descriptions%5B%5D=ÖBG+SHT21-Test+(LC-Wetterstation%2C+West)&haystack_descriptions%5B%5D=Waldstein+HMP45+2m&haystack_descriptions%5B%5D=Waldstein+HMP45+21m&haystack_descriptions%5B%5D=Pflanzgarten+HMP45&haystack_descriptions%5B%5D=Voitsumra+HMP45&haystack_from=946681200000&haystack_until=1464588000000&sax_index=16&aggr_int=86400&shiftby=10+minutes&missing_values=0.2&no_best_hits=30&distanceTable_id=2&distanceTable_name=MaxDist&aggr_int_id=8");

		long meanduration = 0;
		long start = 0, duration = 0;
		int loops = 1;
		String url;

		Response r = target.request().get();
		duration = (System.nanoTime() - start) / 1000000;
		System.out.println(duration);

		long from = 1104534000000L;
		long until_1h = 1104537600000L;
		long until_6h = 1104555600000L;
		long until_1d = 1104620400000L;
		long until_week = 1105138800000L;
		long until_month = 1107212400000L;
		long until_3months = 1112306400000L;
		long until_6months = 1120168800000L;
		long until_1year = 1136070000000L;

		long[] timespans = new long[8];
		timespans[0] = until_1h;
		timespans[1] = until_6h;
		timespans[2] = until_1d;
		timespans[3] = until_week;
		timespans[4] = until_month;
		timespans[5] = until_3months;
		timespans[6] = until_6months;
		timespans[7] = until_1year;

		String[] tsdescr = new String[timespans.length];
		tsdescr[0] = "1 hour";
		tsdescr[1] = "6 hours";
		tsdescr[2] = "1 day";
		tsdescr[3] = "1 week";
		tsdescr[4] = "1 month";
		tsdescr[5] = "3 months";
		tsdescr[6] = "6 months";
		tsdescr[7] = "1 year";

		int[] saxindizes = new int[8];
		saxindizes[0] = 9;
		saxindizes[1] = 10;
		saxindizes[2] = 11;
		saxindizes[3] = 12;
		saxindizes[4] = 13;
		saxindizes[5] = 14;
		saxindizes[6] = 15;
		saxindizes[7] = 16;

		int[] aggrintid = new int[8];
		aggrintid[0] = 1;
		aggrintid[1] = 2;
		aggrintid[2] = 3;
		aggrintid[3] = 4;
		aggrintid[4] = 5;
		aggrintid[5] = 6;
		aggrintid[6] = 7;
		aggrintid[7] = 8;

		String[] aggrintdescr = new String[8];
		aggrintdescr[0] = "10+minutes";
		aggrintdescr[1] = "30+minutes";
		aggrintdescr[2] = "1+hour";
		aggrintdescr[3] = "2+hours";
		aggrintdescr[4] = "4+hours";
		aggrintdescr[5] = "6+hours";
		aggrintdescr[6] = "12+hours";
		aggrintdescr[7] = "1+day";

		String[] shiftbydescr = new String[8];
		shiftbydescr[0] = "10+minutes";
		shiftbydescr[1] = "30+minutes";
		shiftbydescr[2] = "1+hour";
		shiftbydescr[3] = "2+hours";
		shiftbydescr[4] = "4+hours";
		shiftbydescr[5] = "6+hours";
		shiftbydescr[6] = "12+hours";
		shiftbydescr[7] = "1+day";

		for (int ts = 5; ts < timespans.length; ts++) {
			long[][] results = new long[aggrintid.length][aggrintid.length];

			System.out.println("Durations needed for time span of " + tsdescr[ts] + ":");
			for (int aggri = 0; aggri < aggrintid.length; aggri++) {
				System.out.print(aggrintdescr[aggri] + ": ");
				for (int shifti = aggri; shifti < shiftbydescr.length; shifti++) {
					meanduration = 0;
					for (int i = 0; i < loops; i++) {
						url = "http://132.180.112.172/bayeos-sax/rest/app/SimilaritySearch?needle_id=200108&needle_description=%C3%96BG+HMP45&"
								+ "needle_from=" + from + "&needle_until=" + timespans[ts] + "&"
								+ "haystack_ids%5B%5D=200108&haystack_ids%5B%5D=200109&haystack_ids%5B%5D=200110&haystack_ids%5B%5D=200111&haystack_ids%5B%5D=200112&haystack_ids%5B%5D=200113&haystack_ids%5B%5D=200114&haystack_ids%5B%5D=200115&haystack_descriptions%5B%5D=%C3%96BG+HMP45&haystack_descriptions%5B%5D=%C3%96BG+Psychrometer+trocken&haystack_descriptions%5B%5D=%C3%96BG+SHT21-Test+(DIY-Shield%2C+Ost)&haystack_descriptions%5B%5D=%C3%96BG+SHT21-Test+(LC-Wetterstation%2C+West)&haystack_descriptions%5B%5D=Waldstein+HMP45+2m&haystack_descriptions%5B%5D=Waldstein+HMP45+21m&haystack_descriptions%5B%5D=Pflanzgarten+HMP45&haystack_descriptions%5B%5D=Voitsumra+HMP45&haystack_from=946681200000&haystack_until=1464588000000&"
								+ "sax_index=" + saxindizes[aggri] + "&aggr_int=" + aggrintdescr[aggri] + "&shiftby="
								+ shiftbydescr[shifti] + "&"
								+ "missing_values=0.2&no_best_hits=5&distanceTable_id=2&distanceTable_name=MaxDist"
								+ "&aggr_int_id=" + aggrintid[aggri];
						System.out.println(url);
						target = client.target(url);
						start = System.nanoTime();

						r = target.request().get();
						duration = (System.nanoTime() - start) / 1000000;
						assertNotNull(r);
						meanduration += duration;
						System.out.print(duration + ", ");
					}
					System.out.println();
					results[aggri][shifti] = meanduration / loops;
				}
			}
			print2darray(results, aggrintdescr, shiftbydescr, tsdescr[ts]);
		}


	}

	private void print2darray(long[][] results, String[] aggrintdescr, String[] shiftbydescr, String filename) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < shiftbydescr.length; i++) {
			sb.append(";" + shiftbydescr[i]);
		}
		sb.append("\n");

		for (int r = 0; r < results.length; r++) {
			sb.append(aggrintdescr[r] + ";");
			for (int c = 0; c < results.length - 1; c++) {
				if (results[r][c] != 0) {
					sb.append(results[r][c] + ";");
				} else
					sb.append(";");
			}
			sb.append(results[r][results.length - 1] + "\n");
		}
		String res = sb.toString().replace("+", " ");

		System.out.println(res);

		PrintWriter writer;
		try {
			writer = new PrintWriter("new_" + filename.replace(" ", "_") + ".txt", "UTF-8");
			writer.println(res);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
