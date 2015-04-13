var express = require("express");
var bodyParser = require("body-parser");
var mysql = require("mysql");
var request = require('request');

var connection = mysql.createConnection({
  host : 'http://10.27.163.216',
  port: '3306',
  user : 'yikolyk',
  password : '0323',
  database: 'cms'
});

connection.connect();

    connection.query('SELECT * FROM subscriber', function(err, rows, fields){
        if(err){
          throw err;
        }
        console.log('The solution is: ', rows[0].subscriber_name);
    });
    
    connection.end();

var app = express();
var port = process.env.PORT || 5000;

app.listen(port, function() {
   console.log("Listening on " + port);
 });

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.get("/publicView", function(req, res) {

    var query = 'SELECT * from event';

    connection.query(query, function(err, rows, fields){
        if(err){
          throw err;
        }

        res.send(rows);
        
    });
    connection.end();

});

app.get("/getEvent/:id", function(req, res){
    var id = req.param('id');
    var query = 'SELECT * from event where event_id=' + id;
    
    connection.query(query, function(err, rows, fields){
        if(err){
          throw err;
        }

        res.send(rows);
    });
    connection.end();
});

app.post("/callOperator", function(req, res){
    var reporterName = req.body.reporterName;
    var coords = req.body.coords;
    var priority = req.body.priority;
    var lat = coords.lat;
    var lng = coords.lng;
    var location = req.body.location;
    var callerPhone = req.body.callerPhone;
    var description = req.body.description;
    var NRIC = req.body.NRIC;
    var type = req.body.type;
    var status = req.body.status;
    var callOperatorId = req.body.callOperatorId;

    // need to modify!!!
    var query = 'INSERT INTO table reporter VALUES(NULL,' + callerPhone + ',' + description + ',' + priority + ',' +
                location + ',' + status + ',' +  NRIC + ',' + type + ',' + lat + ',' + lng +  ')';
    connection.query(query, function(err, rows, fields){
        if(err){
          throw err;
        }
        
    });
    connection.end();

    var eventDescription = 'Attention: ' + type + ' found in ' + location + ' (exact coordinates: [' + lat + ',' + lng + ']';
    if(description != null){
        eventDescription += ('details: ' + description);
    }

    // sms
     var data = {
      'body' :eventDescription,
      'to' : '+6583067211'
    };
    
    postToSMSServer(data);

    postToFacebookServer(data);
    postToTwitterServer(data);

    res.send("OK");
});
 
app.post("/subscribe", function(req, res) {
    console.log(req.body);

    var name = req.body.name;
    var mobile = req.body.mobile;
    var location = req.body.location;
    var email = req.body.email;
    var query = 'INSERT INTO table subscriber VALUES(NULL,' + name + ',' + mobile + ',' + location + ',' + email + ')';

    console.log(name + " " + mobile + " " + location + " " + email);
    connection.connect();

    connection.query(query, function(err, rows, fields){
        if(err){
          throw err;
        }
        //console.log('The solution is: ', rows[0].solution);
    });
    
    connection.end();
    res.send("OK");
});

app.post("/PSI", function(req, res){
    var PSI = req.body.PSI;
    var haze = req.body.haze;
    var hour = PSI.hour;
    var value = PSI.value;
    var descriptor = PSI.value;
    var hasHaze = haze.hasHaze;
    var queryForPSI = 'INSERT INTO table psi VALUES(' + hour + ',' + value + ',' + descriptor + ')';
    var queryForHaze = 'INSERT INTO table haze VALUES(' + hour + ',' + hasHaze + ')';

    connection.connect();

    connection.query(queryForPSI, function(err, rows, fields){
        if(err){
            throw err;
        }
        //console.log('The solution is: ', rows[0].solution);
    });

    if(hasHaze){
      connection.query(queryForHaze, function(err, rows, fields){
        if(err){
          throw err;
        }
        //console.log('The solution is: ', rows[0].solution);
      });
    }

    connection.end();
    res.send("OK");
});

app.post("/weather", function(req, res){
    var text = req.body.text;
    var celsius = req.body.celsius;
    
    var query = 'INSERT INTO table weather VALUES(' + text + ',' + celsius + ')';
   
    connection.connect();

    connection.query(query, function(err, rows, fields){
        if(err){
            throw err;
        }
        //console.log('The solution is: ', rows[0].solution);
    });
    connection.end();
    res.send("OK");
});

app.post("/dengue", function(req, res){
    var dengueList = req.body;
    dengueList.forEach(function(dengueItem){
        var callerNumber = dengueItem.callerNumber;
        var description = dengueItem.description;
        var priority = dengueItem.priority;
        var callingTime = dengueItem.callingTime;
        var location = dengueItem.location;
        var postalCode = dengueItem.postalCode;
        var reporter = dengueItem.reporter;
        var eventType = dengueItem.eventType;
        var eventName = eventType.eventName;
        var coords = dengueItem.coords;
        var lat = coords.lat;
        var lng = coords.lng;
        var showWindow = dengueItem.showWindow;
        var options = dengueItem.options;
        var draggable = options.draggable;
        var iconUrl = dengueItem.iconUrl;
        var query = 'INSERT INTO table dengue VALUES(' + callerNumber + ',' + description + ',' + priority + ',' + callingTime + ',' +
                    location + ',' + postalCode + ',' + reporter + ',' + eventName + ',' + lat + ',' + lng + ',' + showWindow + ',' + 
                    draggable + ',' + iconUrl + ')'; 
        connection.query(queryForPSI, function(err, rows, fields){
          if(err){
              throw err;
          }
          //console.log('The solution is: ', rows[0].solution);
        });
    });
    connection.end();
    res.send("OK");
});

function postToSMSServer(data){
  var url = "http://172.27.121.20:5003/post";
  post(data, url);
}

function postToFacebookServer(data){
  var url = "http://172.27.121.20:5001/post";
  post(data, url);
}

function postToTwitterServer(data){
  var url = "http://172.27.121.20:5002/post";
  post(data, url); 
}

function post(data, url){
  data = JSON.stringify(data);
  console.log(data);
  request({
    uri : url,
    method : "POST",
    form : data
  }, function(error, response, body){
    if(error){
      console.log("failed posting data ....");
    }
    console.log(body);
  }); 
}