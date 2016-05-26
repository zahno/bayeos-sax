<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>BayEOS Similarity Search</title>


<meta name="layout" content="main">


<link rel="stylesheet"
	href="${ctx}/javascript/jquery-ui/css/smoothness/jquery-ui.min.css">
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="${ctx}/js/rickshaw.min.css">
<link rel="stylesheet" href="${ctx}/css/main.css">
<link rel="stylesheet"
	href="${ctx}/javascript/jquery-ui/css/smoothness/jquery-ui.min.css">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.css">



<script src="${ctx}/javascript/jquery/jquery.min.js"></script>
<script src="${ctx}/javascript/jquery-ui/jquery-ui.min.js"></script>
<script src="${ctx}/js/jquery.plugin.js"></script>
<script src="${ctx}/js/jquery.datetimeentry.js"></script>

</head>
<body>

	<!-- Modal Start here-->
	<div class="modal fade bs-example-modal-sm" id="pleaseWait"
		tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<span class="glyphicon glyphicon-time"> </span> Please Wait
					</h4>
				</div>
				<div class="modal-body">
					<div class="progress">
						<div
							class="progress-bar progress-bar-info
                    progress-bar-striped active"
							style="width: 100%"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal ends Here -->


	<div id="wrap">
		<nav class="navbar navbar-default">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-ex1-collapse">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>

					<a href="./Query" class="navbar-brand"><strong>BayEOS</strong>
						Similarity Search</a>
				</div>

			</div>
		</nav>


		<div class="container">
			<ul class="nav nav-tabs">
				<li class="active"><a data-toggle="tab" href="#query"><span
						class="glyphicon glyphicon-question-sign"></span> Query</a></li>
				<li><a data-toggle="tab" href="#results"><span
						class="glyphicon glyphicon-list"></span> Results</a></li>
			</ul>

			<div class="tab-content">
				<div id="query" class="tab-pane fade in active">
					<h3>Define a search query</h3>
					<div class="block">
						<div class="block-header">Needle</div>
						<div class="group">
							<h4>Determine a time series to search for:</h4>
							<table id="needleTable" data-locale="en-US" data-toggle="table"
								data-click-to-select="true" data-sort-name="id"
								data-select-item-name="myRadioName"
								data-query-params="queryParams"
								data-page-list="[5, 10, 20, 50, 100, 200]"
								data-pagination="true" data-search="true">
								<thead>
									<tr>
										<th data-field="state" data-radio="true"></th>
										<th data-field="name" data-sortable="true">Name</th>
										<th data-field="id" data-sortable="true">ID</th>
										<th data-field="from" data-sortable="true">From</th>
										<th data-field="until" data-sortable="true">Until</th>
										<th data-field="from_seconds" data-visible=false></th>
										<th data-field="until_seconds" data-visible=false></th>

									</tr>
								</thead>
							</table>

						</div>
						<br>

						<div class="row">
							<div class="col-md-9">
								<div id="y_axis0"></div>
								<div id="chart0"></div>
								<div id="timeline0"></div>
								<div id="preview0"></div>
							</div>
						</div>
						<div class="row">
							<br>
							<div class="col-md-8">
								<div id="legend0"></div>

							</div>
							<div class="col-md-4">
								<div class="btn-group"></div>
								<a class="btn btn-default" onclick="needle_back();"><span
									class="glyphicon glyphicon-arrow-left"></span> <span
									class="hidden-xs">back</span></a> <a class="btn btn-default"
									onclick="needle_forward();"><span
									class="glyphicon glyphicon-arrow-right"></span> <span
									class="hidden-xs">forward</span></a>

								<div class="btn-group">
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="glyphicon glyphicon-resize-horizontal"></span>
										Interval <span class="caret"></span>
									</button>

									<ul class="dropdown-menu" role="menu" id="interval">
										<li><a onclick="needle_interval(1);">today</a></li>
										<li><a onclick="needle_interval(2);">last 24 hours</a></li>
										<li><a onclick="needle_interval(3);">last 3 days</a></li>
										<li><a onclick="needle_interval(4);">last 7 days</a></li>
										<li><a onclick="needle_interval(5);">last 30 days</a></li>
										<li><a onclick="needle_interval(6);">yesterday</a></li>
										<li><a onclick="needle_interval(7);">this week</a></li>
										<li><a onclick="needle_interval(8);">last week</a></li>
										<li><a onclick="needle_interval(9);">this month</a></li>
										<li><a onclick="needle_interval(10);">last month</a></li>
										<li><a onclick="needle_interval(11);">this year</a></li>
										<li><a onclick="needle_interval(12);">last year</a></li>
									</ul>
								</div>
							</div>
							<br>
						</div>
						<br>

						<div class="row"></div>
						<br>

						<div class="row" id="selected_ts"></div>
						<div class="row">
							<div class="col-sm-3">
								<div class="form-group ">
									<label class="control-label" for="from"> From
										(inclusive &ge;):</label> <span class="datetimeEntry_wrap"><input
										class="form-control hasDatetimeEntry" id="from" name="from"
										value=""><span class="datetimeEntry_control"
										style="display: inline-block; width: 20px; height: 20px;"></span></span>
									<script type="text/javascript">
										$('#from')
												.datetimeEntry(
														{
															datetimeFormat : 'Y-O-D H:M',
															spinnerImage : 'js/spinnerOrange.png'
														});
									</script>
								</div>
							</div>

							<div class="col-sm-3">
								<div class="form-group ">
									<label class="control-label" for="until"> Until
										(inclusive &le;):</label> <span class="datetimeEntry_wrap"><input
										class="form-control hasDatetimeEntry" id="until" name="until"
										value=""><span class="datetimeEntry_control"
										style="display: inline-block; width: 20px; height: 20px;"></span></span>
									<script type="text/javascript">
										$('#until')
												.datetimeEntry(
														{
															datetimeFormat : 'Y-O-D H:M',
															spinnerImage : 'js/spinnerOrange.png'
														});
									</script>
								</div>
							</div>
							<div class="col-sm-6">
								<br>
								<button class="btn btn-primary" onclick="update();">
									<span class="glyphicon glyphicon-refresh"></span> <span
										class="hidden-xs">Refresh Graph</span>
								</button>
								<p class="help-block">Refresh may improve the graph's
									resolution.</p>

							</div>
						</div>

					</div>
					<br>
					<div class="block">

						<div class="block-header">Haystack</div>
						<h4>Choose one or more time series to search in:</h4>
						<table id="haystackTable" data-locale="en-US" data-toggle="table"
							data-click-to-select="true" data-sort-name="id"
							data-query-params="queryParams"
							data-page-list="[5, 10, 20, 50, 100, 200]" data-pagination="true"
							data-search="true">
							<thead>
								<tr>
									<th data-field="state" data-checkbox="true"></th>
									<th data-field="name" data-sortable="true">Name</th>
									<th data-field="id" data-sortable="true">ID</th>
									<th data-field="from" data-sortable="true">From</th>
									<th data-field="until" data-sortable="true">Until</th>
									<th data-field="from_seconds" data-visible=false></th>
									<th data-field="until_seconds" data-visible=false></th>
								</tr>
							</thead>

						</table>
						<div class="row">
							<div class="col-sm-3">
								<div class="form-group ">
									<label class="control-label" for="haystack_from"> From
										(inclusive &ge;):</label> <span class="datetimeEntry_wrap"><input
										class="form-control hasDatetimeEntry" id="haystack_from"
										name="haystack_from" value=""><span
										class="datetimeEntry_control"
										style="display: inline-block; width: 20px; height: 20px;"></span></span>
									<script type="text/javascript">
										$('#haystack_from')
												.datetimeEntry(
														{
															datetimeFormat : 'Y-O-D H:M',
															spinnerImage : 'js/spinnerOrange.png'
														});
									</script>
								</div>
							</div>

							<div class="col-sm-3">
								<div class="form-group ">
									<label class="control-label" for="haystack_until">
										Until (inclusive &le;):</label> <span class="datetimeEntry_wrap"><input
										class="form-control hasDatetimeEntry" id="haystack_until"
										name="haystack_until" value=""><span
										class="datetimeEntry_control"
										style="display: inline-block; width: 20px; height: 20px;"></span></span>
									<script type="text/javascript">
										$('#haystack_until')
												.datetimeEntry(
														{
															datetimeFormat : 'Y-O-D H:M',
															spinnerImage : 'js/spinnerOrange.png'
														});
									</script>
								</div>
							</div>
						</div>
					</div>
					<br>
					<div class="block">

						<div class="block-header">Settings</div>
						<div class="row">
							<div class="radio col-xs-12">
								<label> <input type="radio" name="radios"
									class="track-order-change" id="defaultSettings" value=""
									checked="checked"> <b>Use auto settings</b>
								</label>
							</div>

							<div class="radio col-xs-12">
								<label> <input type="radio" name="radios"
									class="track-order-change" id="expertSettings"><b>Select
										settings </b>
								</label>
							</div>
							<div class="col-sm-1"></div>
							<div class="col-sm-11 panel-collapse collapse in"
								id="expertAccordion">
								<div>
									Aggregation interval: <select
										title="Choose aggregation interval..." id="aggr_int">
									</select>
								</div>
								<br>
								<div>
									Shift time window by <select title="Choose time window..."
										id="shiftby">
									</select>
								</div>
								<br>
								<div>
									Use distance function: <select
										title="Choose distance function..." id="dist_func">
									</select>
								</div>
								<br>
								<div>
									Only evaluate if less than <select data-width="fit"
										id="missingValues">
										<option value="0">0%</option>
										<option value="0.1">10%</option>
										<option value="0.2" selected="selected">20%</option>
										<option value="0.3">30%</option>
										<option value="0.4">40%</option>
										<option value="0.5">50%</option>
										<option value="0.6">60%</option>
										<option value="0.7">70%</option>
										<option value="0.8">80%</option>
										<option value="0.9">90%</option>
										<option value="1">100%</option>
									</select> of the values are missing
								</div>
								<br>
								<div>
									Number of best hits: <select data-width="fit" id="no_best_hits">
										<option>5</option>
										<option>10</option>
										<option selected="selected">15</option>
										<option>20</option>
										<option>30</option>
										<option>50</option>
										<option>75</option>
										<option>100</option>

									</select>
								</div>
							</div>

						</div>
					</div>
					<br>
					<div class="col-md-5"></div>
					<div class="col-md-2">
						<button class="btn btn-primary" onclick="startSAX();">
							<span class="glyphicon glyphicon-ok"></span> <span>Go!</span>
						</button>
					</div>


				</div>


				<div id="results" class="tab-pane fade">
					<h3>Results</h3>
					<div class="block">

						<div class="block-header">Chart</div>
						<div class="group">

							<div class="row" id="resultChart">
								<div class="col-md-12">
									<div id="y_axis1"></div>
									<div id="chart1"></div>
									<div id="timeline1"></div>
									<div id="preview1"></div>
								</div>
							</div>
							<br>
							<div class="row">
								<div class="col-md-12">

									<div id="legend1"></div>
								</div>
							</div>
							<br> <br>
						</div>
					</div>
					<br>
					<div class="block">
						<div class="block-header">Results table</div>

						<table id="resulttable">
						</table>


						<script>
							var $table = $('#resulttable');
							$(function() {

								$table
										.bootstrapTable({
											columns : [ {
												field : 'queryid',
												title : 'Query No.',
												sortable : true
											}, {
												field : 'n_name',
												title : 'Name',
												sortable : true
											}, {
												field : 'n_id',
												title : 'ID',
												sortable : true
											}, {
												field : 'n_from',
												title : 'From',
												sortable : true
											}, {
												field : 'n_until',
												title : 'Until',
												sortable : true
											}, {
												field : 'n_from_seconds',
												visible : false
											}, {
												field : 'n_until_seconds',
												visible : false
											}, {
												field : 'aggr_int',
												title : 'Aggregation Interval',
												sortable : true
											}, {
												field : 'shiftby',
												title : 'Shift By',
												sortable : true
											}, {
												field : 'distance_func',
												title : 'Distance Method'
											}, {
												field : 'missing_values',
												title : 'Max. Missing Values',
												sortable : true
											}, {
												field : "remove",
												formatter : "actionFormatter",
												events : "actionEvents"
											} ],
											uniqueId : 'queryid',
											sortName : 'queryid',
											sortOrder : 'desc',
											detailView : true,
											locale : 'en-US',

											onExpandRow : function(index, row,
													$detail) {
												$detail
														.html('<table></table>')
														.find('table')
														.bootstrapTable(
																{
																	columns : [
																			{
																				field : 'queryid',
																				visible : false
																			},
																			{
																				field : 'state',
																				radio : true
																			},

																			{
																				field : 'distance_formatted',
																				title : 'Distance',
																				sortable : true
																			},
																			{
																				field : 'distance',
																				visible : false

																			},
																			{
																				field : 'h_name',
																				title : 'Name',
																				sortable : true
																			},
																			{
																				field : 'h_id',
																				title : 'ID',
																				sortable : true
																			},
																			{
																				field : 'h_from',
																				title : 'From',
																				sortable : true
																			},
																			{
																				field : 'h_until',
																				title : 'Until',
																				sortable : true
																			},
																			{
																				field : 'h_from_seconds',
																				visible : false
																			},
																			{
																				field : 'h_until_seconds',
																				visible : false
																			} ],
																	data : row.nested,
																	sortName : 'distance',
																	locale : 'en-US',
																	clickToSelect : true,

																})
														.on(
																'check.bs.table',
																function(e, row) {
																	resultChecked(
																			e,
																			row);
																})
														.bootstrapTable(
																'check', 0);

											}
										});
							});

							function actionFormatter(value, row, index) {
								return [
										'<a class="remove ml10" href="javascript:void(0)" title="Remove">',
										'<i class="glyphicon glyphicon-remove"></i>',
										'</a>' ].join('');
							}

							window.actionEvents = {
								'click .remove' : function(e, value, row, index) {
									$('#deleteDialogModal').data('row', row)
											.modal('show');
								}
							};
						</script>

					</div>
				</div>

			</div>

		</div>
	</div>




	<!-- start: Delete Coupon Modal -->
	<div class="modal fade" id="deleteDialogModal" tabindex="-1"
		role="dialog" aria-labelledby="deleteDialogModalLabel"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h3 class="modal-title" id="deleteDialogModalLabel">Warning!</h3>

				</div>
				<div class="modal-body">
					<h4>Are you sure you want to delete this result?</h4>

				</div>
				<!--/modal-body-collapse -->
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" id="btnDelteYes">Yes</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
				</div>
				<!--/modal-footer-collapse -->
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	<script type="text/javascript">
		$('#btnDelteYes').click(function() {
			var row = $('#deleteDialogModal').data('row');
			$('#resulttable').bootstrapTable('remove', {
				field : 'queryid',
				values : [ row.queryid ]
			});
			$('#deleteDialogModal').modal('hide');
		});
	</script>



	<div id="footer">

		<p class="text-muted credit">
			© 2014-2016 <a href="http://www.uni-bayreuth.de/">University of
				Bayreuth,</a> <a href="http://www.bayceer.uni-bayreuth.de/">BayCEER</a>
			Release 1.0.20 - Fabulous icons from <a href="http://glyphicons.com">Glyphicons
				Free</a>, licensed under <a
				href="http://creativecommons.org/licenses/by/3.0/">CC BY 3.0</a>
		</p>
	</div>



	<!-- Latest compiled and minified JavaScript -->
	<script
		src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.10.1/bootstrap-table.min.js"></script>

	<script type="text/javascript"
		src="http://tarruda.github.com/bootstrap-datetimepicker/assets/js/bootstrap-datetimepicker.min.js">
		
	</script>

	<script src="${ctx}/js/d3.min.js"></script>
	<script src="${ctx}/js/rickshaw.min.js"></script>
	<script src="${ctx}/javascript/moment.js"></script>

	<script src="${ctx}/javascript/servletFunctions.js"></script>
	<script type="text/javascript"
		src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js">
		<script src="js/bundle-bundle_bootstrap_defer.js"
	type="text/javascript">
	</script>
</body>
</html>

