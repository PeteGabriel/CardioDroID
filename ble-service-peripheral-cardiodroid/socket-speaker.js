'use strict';

/**
 * Este módulo vai actuar como script, na medida em que irá executar
 * uma função para criar um servidor à escuta no porto 3000.
 *
 * Ao criar este servidor, irá também iniciar o serviço BLE que construimos
 * com ajuda do módulo Bleno.
 */

var net = require('net');
var socket_listener = require('./socket-listener/socket-listener');
var service = require('./services/Service');

var writable = service.modifyCharacteristics;

/**
 * Socket Command Handlers
 * */
var ExhaustionStateHandler = require('./socket-command-handlers/ExhaustionStateHandler')(writable);
var UserIdStateHandler = require('./socket-command-handlers/UserIdStateHandler')(writable);

/**
 * Socket Port Definitions
 * */
var SOCKET_PORT_LISTEN = 3000; // listen for incoming commands
var SOCKET_PORT_WRITE = 3030; // Send out going commands.

/**********************************************************************************************************************
 * Socket Server Setup
 *
 * This socket communicates data to the CardioWheel.
 **********************************************************************************************************************/

var socket_server = new socket_listener(SOCKET_PORT_LISTEN, onSocketListen, null);

function onSocketListen() {
    console.log("Starting BLE Interface");
    service.startService(connect);
}

socket_server.on("EXHAUSTION_STATE", ExhaustionStateHandler);
socket_server.on("USER_ID_STATE", UserIdStateHandler);

socket_server.create();

/**********************************************************************************************************************
 * Socket Communication Setup
 *
 * Connect to the socket on which the CardioWheel is listening, to transmit information.
 **********************************************************************************************************************/
function connect() {
    var client = net.connect({
        port: SOCKET_PORT_WRITE
    });

    client.on('error', function(err) {
        console.log("ERROR OCCURRED: " + err);
    });
    return client;
}