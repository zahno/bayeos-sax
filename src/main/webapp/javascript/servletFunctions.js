var resultGraphData = new Array();
var resultGraph;
var needleGraphData = new Array();
var needleGraph;

function initiateGraph(graphData, divchart, divtimeline) {

	var graph = new Rickshaw.Graph({
		element : document.querySelector(divchart),
		renderer : 'line',
		interpolation : 'linear',
		min : 'auto',
		height : 300,
		width : window.innerWidth * 0.75,
		series : graphData,

	});

	graph.onUpdate(function f() {
		// alert(preview0)
	});

	var hoverDetail0 = new Rickshaw.Graph.HoverDetail({
		graph : graph,
		xFormatter : function(x) {
			currentX = x;
			return new Date(x * 1000).toString();
		}
	});

	var annotator0 = new Rickshaw.Graph.Annotate({
		graph : graph,
		element : document.getElementById(divtimeline)
	});

	var xAxis0 = new Rickshaw.Graph.Axis.Time({
		graph : graph,
		ticksTreatment : 'glow',
		timeFixture : new Rickshaw.Fixtures.Time.Local()
	});

	xAxis0.render();

	var yAxis0 = new Rickshaw.Graph.Axis.Y({
		graph : graph,
		tickFormat : Rickshaw.Fixtures.Number.formatKMBT,
		ticksTreatment : 'glow'
	});

	yAxis0.render();

	if (divchart == "#chart0") {
		needleGraph = graph;
	} else {
		resultGraph = graph;
	}
}

function updateGraph(graph, graphData, newData, divlegend, divpreview) {

	graphData.splice(0, graphData.length);
	$.each(newData, function(i, data) {
		graphData.push(data);
	});

	$(divlegend).empty();

	var preview0 = new Rickshaw.Graph.RangeSlider({
		graph : graph,
		element : document.getElementById(divpreview),
	});
	if (divpreview == 'preview0') {
		preview0.slideCallbacks = [ set_from_until ];
	}

	var legend = document.querySelector(divlegend);

	var Hover = Rickshaw.Class.create(Rickshaw.Graph.HoverDetail, {

		render : function(args) {

			legend.innerHTML = args.formattedXValue;

			args.detail.sort(function(a, b) {
				return a.order - b.order
			}).forEach(function(d) {

				var line = document.createElement('div');
				line.className = 'line';

				var swatch = document.createElement('div');
				swatch.className = 'swatch';
				swatch.style.backgroundColor = d.series.color;

				var label = document.createElement('div');
				label.className = 'label';
				label.innerHTML = d.name + ": " + d.formattedYValue;

				line.appendChild(swatch);
				line.appendChild(label);

				legend.appendChild(line);

				var dot = document.createElement('div');
				dot.className = 'dot';
				dot.style.top = graph.y(d.value.y0 + d.value.y) + 'px';
				dot.style.borderColor = d.series.color;

				this.element.appendChild(dot);

				dot.className = 'dot active';

				this.show();

			}, this);
		}
	});

	var hover = new Hover({
		graph : graph
	});

	graph.update();
}

$(document).ready(

		function() {
			$('#pleaseWait').modal('show');

			$("body").toggleClass("wait");

			initiateGraph(needleGraphData, "#chart0", 'timeline0');
			initiateGraph(resultGraphData, "#chart1", 'timeline1');

			$.ajax({
				type : "GET",
				url : "/bayeos-sax/rest/app/findSaxSeries",

				dataType : "json",
				success : function(data) {

					$('#availableSeries').empty();
					$('#haystackSeries').empty();

					$.each(data, function(i, data) {
						if (i == 0) {
							var state = true;
						} else
							var state = false;

						var mydata = {
							name : data.description,
							id : data.id,
							type : data.type,
							from : format_date(new Date(
									data.from_seconds * 1000)),
							until : format_date(new Date(
									data.until_seconds * 1000)),
							from_seconds : data.from_seconds,
							until_seconds : data.until_seconds,
							state : state
						};
						var mydata2 = {
							name : data.description,
							id : data.id,
							type : data.type,
							from : format_date(new Date(
									data.from_seconds * 1000)),
							until : format_date(new Date(
									data.until_seconds * 1000)),
							from_seconds : data.from_seconds,
							until_seconds : data.until_seconds,
							state : true
						};

						$('#needleTable').bootstrapTable('append', mydata);

						$('#haystackTable').bootstrapTable('append', mydata2)
								.on('check.bs.table', function(e, row) {
									set_haystack_from_until();
								}).on('uncheck.bs.table', function(e, row) {
									set_haystack_from_until();
								}).on('check-all.bs.table', function(e, row) {
									set_haystack_from_until();
								}).on('uncheck-all.bs.table', function(e, row) {
									set_haystack_from_until();
								});

					});
					var values = $('#needleTable').bootstrapTable(
							'getSelections')[0];
					loadNeedleGraph(values.id, values.from_seconds,
							values.until_seconds);
					// setSaxIds(values.id);
					setAggregationIntervals(values.id);
					set_haystack_from_until();

					$('#pleaseWait').modal('hide');
				}
			});
		});

$(document).ready(function() {
	$('#needleTable').click(function() {
		$("body").toggleClass("wait");

		var values = $('#needleTable').bootstrapTable('getSelections')[0];
		loadNeedleGraph(values.id, values.from_seconds, values.until_seconds);
		// setSaxIds(values.id);
		setAggregationIntervals(values.id);

	});
});

function loadResultGraph(needle_id, needle_from, needle_until, haystack_id,
		haystack_from, haystack_until) {
	$("body").toggleClass("wait");

	$.ajax({
		type : "GET",
		url : "/bayeos-sax/rest/app/GetDataToIds/" + needle_id + "/"
				+ needle_from + "/" + needle_until + "/" + haystack_id + "/"
				+ haystack_from + "/" + haystack_until,

		dataType : "json",
		success : function(data) {
			$("body").toggleClass("wait");

			var newData = [ {
				name : data[1].name,
				data : data[1].data,
				color : 'red'
			}, {
				name : data[0].name,
				data : data[0].data,
				color : 'steelblue'
			} ];

			updateGraph(resultGraph, resultGraphData, newData, "#legend1",
					'preview1')
		}
	});

}

function resultChecked(e, row) {
	var needlerow = $('#resulttable').bootstrapTable('getRowByUniqueId',
			row.queryid);

	loadResultGraph(needlerow.n_id, needlerow.n_from_seconds,
			needlerow.n_until_seconds, row.h_id, row.h_from_seconds,
			row.h_until_seconds)
}

function update() {
	$("body").toggleClass("wait");

	var from = moment($('#from').val()).toDate().getTime() / 1000;
	var until = moment($('#until').val()).toDate().getTime() / 1000;

	loadNeedleGraph($('#needleTable').bootstrapTable('getSelections')[0].id,
			from, until);
}
function needle_back() {
	$("body").toggleClass("wait");

	var from = moment($('#from').val()).toDate().getTime();
	var until = moment($('#until').val()).toDate().getTime();

	var interval = until - from;

	from = from - interval / 3;
	from = new Date(from);
	from.setMinutes(0, 0, 0);

	until = until - interval / 3;
	until = new Date(until);
	until.setMinutes(0, 0, 0);

	from = from.getTime() / 1000;
	until = until.getTime() / 1000;

	loadNeedleGraph($('#needleTable').bootstrapTable('getSelections')[0].id,
			from, until);
}

function needle_forward() {
	$("body").toggleClass("wait");

	var from = moment($('#from').val()).toDate().getTime();
	var until = moment($('#until').val()).toDate().getTime();

	var interval = until - from;

	from = from + interval / 3;
	from = new Date(from);
	from.setMinutes(0, 0, 0);

	until = until + interval / 3;
	until = new Date(until);
	until.setMinutes(0, 0, 0);

	from = from.getTime() / 1000;
	until = until.getTime() / 1000;

	loadNeedleGraph($('#needleTable').bootstrapTable('getSelections')[0].id,
			from, until);
}

function needle_interval(interval) {
	$("body").toggleClass("wait");
	var until = new Date();
	until.setSeconds(0, 0);
	if (interval == 1) {
		var from = getToday();
	} else if (interval == 2) {
		var from = getLast24Hours();
	} else if (interval == 3) {
		var from = getLast3Days();
	} else if (interval == 4) {
		var from = getLast7Days();
	} else if (interval == 5) {
		var from = getLast30Days();
	} else if (interval == 6) {
		var from = getYesterday();
		var until = getYesterday();
		until.setHours(24, 0, 0, 0);
	} else if (interval == 7) {
		var from = getFirstDayOfThisWeek();
	} else if (interval == 8) {
		from = new Date(until.setDate(from.getDate() - 7));
	} else if (interval == 9) {
		from = new Date(until.getFullYear(), until.getMonth(), 1);
	} else if (interval == 10) {
		var from = new Date(until.getFullYear(), until.getMonth() - 1, 1);
		until = new Date(until.getFullYear(), until.getMonth(), 1);
	} else if (interval == 11) {
		from = new Date(until.getFullYear(), 0);
	} else if (interval == 12) {
		from = new Date(until.getFullYear() - 1, 0);
		until = new Date(until.getFullYear(), 0);
	}
	from = from.getTime() / 1000;
	until = until.getTime() / 1000;

	loadNeedleGraph($('#needleTable').bootstrapTable('getSelections')[0].id,
			from, until);
}

function getToday() {
	var d = new Date();
	d.setHours(0, 0, 0, 0);
	return d;
}

function getLast24Hours() {
	var d = new Date();

	var last24Hours = new Date(d.getFullYear(), d.getMonth(), d.getDate() - 1);
	last24Hours.setHours(d.getHours(), d.getMinutes(), 0, 0);
	return last24Hours;
}

function getLast3Days() {
	var now = new Date();
	var last3days = new Date(now.getFullYear(), now.getMonth(),
			now.getDate() - 3);
	last3days.setHours(now.getHours(), now.getMinutes(), 0, 0);
	return last3days;
}

function getLast7Days() {
	var now = new Date();
	var last7days = new Date(now.getFullYear(), now.getMonth(),
			now.getDate() - 7);
	last7days.setHours(now.getHours(), now.getMinutes(), 0, 0);
	return last7days;
}
function getLast30Days() {
	var now = new Date();
	var last30days = new Date(now.getFullYear(), now.getMonth() - 1, now
			.getDate() - 30);
	last30days.setHours(now.getHours(), now.getMinutes(), 0, 0);

	return last30days;
}

function getYesterday() {
	var d = new Date();
	var res = new Date(d.getFullYear(), d.getMonth(), d.getDate() - 1);
	res.setHours(0, 0, 0, 0);
	return res;
}

function getFirstDayOfThisWeek() {
	var curr = new Date(); // get current date
	var first = curr.getDate() - curr.getDay(); // First day is the day of the
	// month - the day of the week
	first = new Date(curr.setDate(first));
	first.setHours(0, 0, 0, 0);
	return first;
}

function addLeadingZeros(number, length) {
	var num = '' + number;
	while (num.length < length)
		num = '0' + num;
	return num;
}

function format_date(d) {
	var s = d.getFullYear() + '-' + addLeadingZeros(d.getMonth() + 1, 2) + '-'
			+ addLeadingZeros(d.getDate(), 2) + ' ' + d.toLocaleTimeString();
	return s;
}

function set_from_until(g, from, until) {
	if (from != null)
		$('#from').val(format_date(new Date(from * 1000)));
	if (until != null)
		$('#until').val(format_date(new Date(until * 1000)));
}

function set_haystack_from_until() {
	var haystackSelections = $('#haystackTable')
			.bootstrapTable('getSelections');
	if (haystackSelections.length == 0) {
		$('#haystack_from').val("No rows selected");
		$('#haystack_until').val("No rows selected");
		return;
	}
	var haystack_from_all = new Array();
	var haystack_until_all = new Array();

	for (var i = 0; i < haystackSelections.length; i++) {
		haystack_from_all.push(haystackSelections[i].from_seconds);
		haystack_until_all.push(haystackSelections[i].until_seconds);
	}

	var haystack_from = Math.min.apply(null, haystack_from_all);
	var haystack_until = Math.max.apply(null, haystack_until_all);

	if (from != null)
		$('#haystack_from').val(format_date(new Date(haystack_from * 1000)));
	if (until != null)
		$('#haystack_until').val(format_date(new Date(haystack_until * 1000)));
}

/**
 * 
 */
function loadNeedleGraph(id, from, until) {
	if (until < from) {
		until = from + 24 * 60 * 60;
	}

	set_from_until(null, from, until);

	$.ajax({
		type : "GET",
		url : "/bayeos-sax/rest/app/GetDataToId/" + id + "/" + from + "/"
				+ until,

		dataType : "json",
		success : function(data) {
			if (data != null) {
				var newData = [ {
					name : data.name,
					data : data.data,
					color : 'steelblue'
				} ];

				updateGraph(needleGraph, needleGraphData, newData, "#legend0",
						'preview0')
			} else {
				alert("No data available in the selected interval!")
			}
			$("body").toggleClass("wait");

		}
	});
}

$(document).ready(function() {
	$('#defaultAccordion, #expertAccordion').collapse('hide');

});

$(document).ready(function() {
	$('input[name="radios"]').change(function() {

		if ($('#expertSettings').is(":checked")) {

			$('#expertAccordion').collapse('show');

		} else {

			$('#expertAccordion').collapse('hide');
		}

	});
});

$(document).ready(function() {
	$('aggr_int').change(function() {
		setDistanceFunc($('#aggr_int').val());
	});
});

function setSaxIds(messreiheId) {
	$.ajax({
		type : "GET",
		url : "/bayeos-sax/rest/app/GetSaxIDs/" + messreiheId,

		dataType : "json",
		success : function(data) {

			$('#aggr_int').empty();
			$('#shiftby').empty();

			$.each(data, function(i, data) {

				var div_data = "<option value=" + data.saxIndex + ">"
						+ data.description + "</option>";
				$(div_data).appendTo('#aggr_int');
				$(div_data).appendTo('#shiftby');
			});

		}
	});

}

function setAggregationIntervals(messreiheId) {
	$.ajax({
		type : "GET",
		url : "/bayeos-sax/rest/app/GetAggregationIntervals/" + messreiheId,

		dataType : "json",
		success : function(data) {

			$('#aggr_int').empty();
			$('#shiftby').empty();

			$.each(data, function(i, aggr_int) {

				var div_data = "<option value=" + aggr_int.id + ">"
						+ aggr_int.description + "</option>";
				$(div_data).appendTo('#aggr_int');
				$(div_data).appendTo('#shiftby');
			});

			setDistanceFunc($('#aggr_int').val());
		}
	});

}

function setDistanceFunc(aggr_int_id) {
	$.ajax({
		type : "GET",
		url : "/bayeos-sax/rest/app/GetDistanceFunctions/" + aggr_int_id,

		dataType : "json",
		success : function(data) {

			$('#dist_func').empty();

			$.each(data, function(i, data) {

				var div_data = "<option value=" + data.id + ">"
						+ data.description + "</option>";
				$(div_data).appendTo('#dist_func');
			});

		}
	});

}

var queryId = 0;

function startSAX() {
	var needle_id = $('#needleTable').bootstrapTable('getSelections')[0].id;
	var needle_description = $('#needleTable').bootstrapTable('getSelections')[0].name;

	// var needle_description = escape($('#needleTable').bootstrapTable(
	// 'getSelections')[0].name);
	var needle_from = moment($('#from').val()).toDate().getTime();
	var needle_until = moment($('#until').val()).toDate().getTime();

	var haystackSelections = $('#haystackTable')
			.bootstrapTable('getSelections');
	var haystack_ids = new Array();
	// var haystack_from_all = new Array();
	// var haystack_until_all = new Array();
	var haystack_descriptions = new Array();

	for (var i = 0; i < haystackSelections.length; i++) {
		haystack_ids.push(haystackSelections[i].id);
		// haystack_from_all.push(haystackSelections[i].from_seconds);
		// haystack_until_all.push(haystackSelections[i].until_seconds);
		haystack_descriptions.push(haystackSelections[i].name);
	}

	// alert(moment($('#haystack_from').val()));
	// alert(moment($('#haystack_from').val()).toDate().getTime());
	// alert((moment($('#haystack_from').val()).toDate()).getTimezoneOffset());
	// alert(moment($('#from').val()));
	// alert((moment($('#from').val()).toDate()).getTimezoneOffset());

	var offset = (moment($('#haystack_from').val()).toDate())
			.getTimezoneOffset()
			- (moment($('#from').val()).toDate()).getTimezoneOffset();
	// alert(offset);
	//
	// alert(moment($('#haystack_from').val()).toDate().getTime()
	// - (offset * 60 * 1000));
	var haystack_from = moment($('#haystack_from').val()).toDate().getTime()
			- (offset * 60 * 1000);
	var haystack_until = moment($('#haystack_until').val()).toDate().getTime()
			- (offset * 60 * 1000);

	// check if from or until values are invalid
	if (needle_from >= needle_until || haystack_from >= haystack_until) {
		alert("Invalid timestamps for haystack or needle!")
		return
	}
	$("body").toggleClass("wait");
	$('.nav-tabs a[href="#results"]').tab('show');

	// alert(moment(haystack_from).toDate());
	// var haystack_from = moment($('#haystack_from').val()).toDate().getTime();
	// var haystack_until =
	// moment($('#haystack_until').val()).toDate().getTime();

	// var haystack_from = Math.min.apply(null, haystack_from_all) * 1000;
	// var haystack_until = Math.max.apply(null, haystack_until_all) * 1000;

	var sax_index;

	if (!document.getElementById('defaultSettings').checked) {

		var aggr_int_id = $('#aggr_int').find("option:selected").val();
		var aggr_int = $('#aggr_int').find("option:selected").text();
		var shiftby = $('#shiftby').find("option:selected").text();
		var distanceTable_id = $('#dist_func').find("option:selected").val();
		var distanceTable_name = $('#dist_func').find("option:selected").text();
		var missing_values = $('#missingValues').find("option:selected").val();
		var no_best_hits = $('#no_best_hits').find("option:selected").val();

		$.ajax({
			type : "GET",
			url : "/bayeos-sax/rest/app/GetSaxIndex/" + aggr_int_id + "/"
					+ distanceTable_id,
			dataType : "json",
			success : function(data) {
				sax_index = data;

				startSimilaritySearch(needle_id, needle_description,
						needle_from, needle_until, haystack_ids,
						haystack_descriptions, haystack_from, haystack_until,
						sax_index, aggr_int, aggr_int_id, shiftby,
						missing_values, no_best_hits, distanceTable_id,
						distanceTable_name);
			},
			error : function() {
				alert("Error whilst retrieving properties")
				$("body").toggleClass("wait");
			}
		});
	} else {

		var available_aggr_int = new Array();
		var available_aggr_int_id = new Array();
		var available_shiftby = new Array();
		var available_distanceTable_id = new Array();

		$("#aggr_int option").each(function() {
			available_aggr_int.push($(this).text());
			available_aggr_int_id.push($(this).val());
		});
		$("#shiftby option").each(function() {
			available_shiftby.push($(this).text());
		});
		$('#dist_func').each(function() {
			available_distanceTable_id.push($(this).val());
		});

		$.ajax({
			type : "GET",
			url : "/bayeos-sax/rest/app/SAXSettings",
			data : {
				needle_id : needle_id,
				needle_from : needle_from,
				needle_until : needle_until,
				haystack_ids : haystack_ids,
				haystack_from : haystack_from,
				haystack_until : haystack_until,
				available_aggr_int : available_aggr_int,
				available_aggr_int_id : available_aggr_int_id,
				available_shiftby : available_shiftby,
				available_distance_func_id : available_distanceTable_id,
			},
			dataType : "json",
			success : function(data) {

				startSimilaritySearch(needle_id, needle_description,
						needle_from, needle_until, haystack_ids,
						haystack_descriptions, haystack_from, haystack_until,
						data.sax_index.saxIndex, data.aggr_int.seconds,
						data.aggr_int.id, data.shift_by.description,
						data.missing_values, data.no_best_hits,
						data.dist_func.id, data.dist_func.description);
			}
		});

	}
	//
	// alert(sax_index + "\n" + shiftby + "\n" + missing_values + "\n"
	// + no_best_hits)

	// alert(sax_index);

}

function startSimilaritySearch(needle_id, needle_description, needle_from,
		needle_until, haystack_ids, haystack_descriptions, haystack_from,
		haystack_until, sax_index, aggr_int, aggr_int_id, shiftby,
		missing_values, no_best_hits, distanceTable_id, distanceTable_name) {
	$
			.ajax({
				type : "GET",
				url : "/bayeos-sax/rest/app/SimilaritySearch",
				data : {
					needle_id : needle_id,
					needle_description : needle_description,
					needle_from : needle_from,
					needle_until : needle_until,
					haystack_ids : haystack_ids,
					haystack_descriptions : haystack_descriptions,
					haystack_from : haystack_from,
					haystack_until : haystack_until,
					sax_index : sax_index,
					aggr_int : aggr_int,
					shiftby : shiftby,
					missing_values : missing_values,
					no_best_hits : no_best_hits,
					distanceTable_id : distanceTable_id,
					distanceTable_name : distanceTable_name,
					aggr_int_id : aggr_int_id
				},
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				success : function(data) {
					// alert(this.url);
					$("body").toggleClass("wait");

					var aggr_int = data.properties.aggr_int.description;
					var shiftby = data.properties.shift_by.description;
					var missingvalues = data.properties.missing_values;
					var needle_id = data.needle.id;
					var needle_description = data.needle.description;
					var needle_from = format_date(new Date(
							data.needle.from.millis));
					var needle_until = format_date(new Date(
							data.needle.until.millis));
					var needle_from_seconds = data.needle.from.millis / 1000;
					var needle_until_seconds = data.needle.until.millis / 1000;
					var distance_func = data.properties.dist_func.description;
					// alert(aggr_int + " " + shiftby + " " + missingvalues + "
					// "
					// + needle_id + " " + needle_description + " "
					// + needle_from + " " + needle_until + " "
					// + needle_from_seconds + " " + needle_until_seconds)
					queryId++;

					var resultData = {

						queryid : queryId,

						aggr_int : aggr_int,
						shiftby : shiftby,
						n_name : needle_description,
						n_id : needle_id,
						n_from : needle_from,
						n_until : needle_until,
						n_from_seconds : needle_from_seconds,
						n_until_seconds : needle_until_seconds,
						distance_func : distance_func,
						missing_values : (missingvalues * 100 + "%"),
						nested : []
					}
					// alert("haystack_subsequences " +
					// data.haystack_subsequences)
					$
							.each(
									data.haystack_subsequences,
									function(i, hay_sub) {

										if (i == 0) {
											var state = true;
										} else
											var state = false;

										resultData.nested
												.push({
													state : state,
													distance_formatted : (hay_sub.distance)
															.toFixed(4),
													distance : hay_sub.distance,
													h_id : hay_sub.haystack_subsequence.id,
													h_name : hay_sub.haystack_subsequence.description,
													h_from : format_date(new Date(
															hay_sub.haystack_subsequence.from.millis)),
													h_until : format_date(new Date(
															hay_sub.haystack_subsequence.until.millis)),
													h_from_seconds : (hay_sub.haystack_subsequence.from.millis / 1000),
													h_until_seconds : (hay_sub.haystack_subsequence.until.millis / 1000),
													queryid : queryId
												})
									});
					$('#resulttable').bootstrapTable('prepend', resultData);
					$('#resulttable').bootstrapTable('collapseAllRows', false);
					$('#resulttable').bootstrapTable('expandRow', 0);

				},
				error : function() {
					alert("error")
					$("body").toggleClass("wait");
				}

			});

}

$.fn.disable = function() {
	return this.each(function() {
		if (typeof this.disabled != "undefined")
			this.disabled = true;
	});
}

$.fn.enable = function() {
	return this.each(function() {
		if (typeof this.disabled != "undefined")
			this.disabled = false;
	});
}
