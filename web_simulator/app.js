var debug = function(msg){console.log(msg)};

/**********************************************************************************************************************
 * Web Server Setup
 **********************************************************************************************************************/
var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var net = require('net');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hbs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

/**
 * DB Setup
 * */
var configureDB = require('./storage/DriverStorage').configure;

configureDB(function(error){
    if (error){
        debug(error.message);
        return;
    }
    debug("DB created !");
});

/**
 * Containers where the values of the BLE characteristics can be stored.
 * */
var container = require('./value-container');

var arp_container = new container(null);
var aim_container = new container(null);

/**
 * routing
 * */
var routes = require('./routes/states');
var patterns = require('./routes/patterns')(aim_container, arp_container);
var protocol = require('./routes/protocol')(arp_container);

/*
* (1) Client Requests A/I
*   Server -> Respond with state (A,I, undefined)
*   Client -> Act accordingly
*       -> A/I present patterns
*       -> undefined -> maintain button to check A/I state
* (2) A/I state indicated
*   Show Client list of available patterns
*   Have the Client select one and send it to the Server
*       Server -> registers this pattern and waits for Android id
*   Client makes recursive requests to obtain result (OK / NOT_OK)*/

app.get('/', function(req, res){res.redirect('/states');});
app.use('/states', routes);
app.use('/patterns', patterns);
app.use('/protocol', protocol);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/**
 * error handlers
 * */

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stack traces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});

/**********************************************************************************************************************
 * Socket Server Setup
 *
 * This socket communicates data to the BLE Interface.
 **********************************************************************************************************************/
var socket_listener = require('./socket-listener/socket-listener');

var AccessRightsHandler = require('./socket-command-handlers/AccessRightsHandler')(arp_container);
var AuthIdHandler = require('./socket-command-handlers/AuthIdHandler')(aim_container);

var SOCKET_PORT_LISTEN = 3030;

var socket_server = new socket_listener(SOCKET_PORT_LISTEN, null, null);

socket_server.on("ACCESS_RIGHTS_PROCESS", AccessRightsHandler);
socket_server.on("USER_ID_STATE", AuthIdHandler);

socket_server.create();

/**********************************************************************************************************************
 * Module Exports
 **********************************************************************************************************************/
module.exports = app;