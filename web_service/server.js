'use strict'
//
// # CardioDroID Web Service
//
// Web service that receives information from devices running the CardioDroID
// android application. It shows information about the usage of the application
// but also intends to provide a capacity to share data among users that
// bellong within a certain group.
//
let http = require('http');
let path = require('path');

let async = require('async');
let express = require('express');
let body_parser = require('body-parser');

let fs = require('fs');

//our view engine is handlebars
const handlebars = require('express-handlebars').create({
    defaultLayout: 'main'
});

const app = express();

// Supply the 'index.html' file for a root request.
app.get('/', function(req, res){
    fs.readFile('./public/html/index.html', function(err, data){
        if(err){
            // FIXME respond with error to client.
            console.log(err);
            return;
        }

        // TODO set status and content-type.
        res.end(data);
    });
});

app.use(express.static(path.resolve(__dirname, 'public')));

// Set the parser for 'application/json' content.
app.use(body_parser.json());

app.engine('handlebars', handlebars.engine);
app.set('view engine', 'handlebars');
app.set('views', path.join(__dirname, 'views/layouts'));

// Sqlite Database connection.
//
// FIXME use the normal database connection instead of the Debug connection.
const db = require('./db/sqlite3/sqlite3').getDebugDb('cardiodroid_api_db',
    function(msg){
        console.log('QUERY: '+msg);
    });

// Controllers
const users_controller = require('./routes/users_controller');
const groups_controller = require('./routes/groups_controller');
const rules_controller = require('./routes/rules_controller');
const logs_controller = require('./routes/logs_controller');

//init routing
const users_router = users_controller(db);
const groups_router = groups_controller(db);
const rules_router = rules_controller(db);
const logs_router = logs_controller(db);

/**
 * Logging function for debug purposes
 */
app.use(function(req, res, next) {
    console.log('%s %s — %s', (new Date()).toString(), req.method, req.url);
    return next();
});

app.use('/api/users', users_router);
app.use('/api/groups', groups_router);
app.use('/api/rules', rules_router);
app.use('/api/logs', logs_router);

const server = http.createServer(app);

server.listen(process.env.PORT || 3000, process.env.IP || "0.0.0.0", function(){
  let addr = server.address();
  console.log("Server application listening at", addr.address + ":" + addr.port);
});
