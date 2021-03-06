var mysql = require("mysql");
var connection = mysql.createConnection({
  host : '127.0.0.1',
  port: '3306',
  user : 'yikolyk',
  password : '0323',
  database: 'cms2'
});

exports.reportHandlder = function(req, res){
	var query = "SELECT * FROM event";

	var reportTable = "<html><title>All Recent Events</title><body><table><tr><th>Event Type</th><th>Description</th><th>Location></th><th>Latitude</th><th>Longitude</th><th>Priority</th>";
	connection.query(query, function(err, rows, fields){
	    if(err){
	    	throw err;
	    }
	    rows.forEach(function(rowData){
	        var description = rowData.description;
	        var priority = rowData.priority;
	        var location = rowData.location;
	        var type = rowData.type;
	        var lat = rowData.latitude;
	        var lng = rowData.longitude;

	        var htmlRow = "<tr><td>" + type + "</td><td>" + description + "</td><td>" + location + "</td><td>" + lat + "</td><td>" + lng + "</td><td>" + priority + "</td></tr>"; 
	        reportTable += htmlRow;
	    });

	    reportTable += "</body></html>"

	    res.send(htmlRow);
	});
}