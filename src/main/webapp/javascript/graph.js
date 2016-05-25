/**
 * 
 */
var currentX = 0;
var selectedX = new Object();

function setSelectedTSTable() {
	var keys = [];
	for ( var k in selectedX) {
		keys.push(k);
	}
	keys.sort();

	if (keys.length == 0) {
		$('#selected_ts').html('');
		return;
	}
	$('#selected_ts').html(
			'<div class="col-xs-12"><b>Selected Timestamps:</b></div>');
	for ( var i in keys) {
		d = new Date(keys[i] * 1000);
		$('#selected_ts')
				.append(
						'<div class="col-md-4 col-xs-6">'
								+ d.toString()
								+ ' <input type="hidden" name="ts[]" value="'
								+ keys[i]
								+ '">'
								+ '<a class="btn btn-xs btn-default" onclick="delete selectedX['
								+ keys[i]
								+ ']; setSelectedTSTable(); return;">'
								+ '<span class="glyphicon glyphicon-remove"></span></a></div>');
	}
	$('#selected_ts').append('<div class="col-xs-12"><hr/></div>');
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

var palette = new Rickshaw.Color.Palette({
	scheme : 'colorwheel'
});
