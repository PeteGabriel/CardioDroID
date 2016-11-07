/**
 * This module exports a function which creates an object with which the user can specify
 * commands to listen to on a socket, which is created after
 * The user can specify the commands and handlers for each command.
 * The user can also specify a function to be called when the module starts listening.
 * */

'use strict';

var net = require('net');

/**
 * Function which creates the context for creating the socket command server.
 *
 * @param port - Port on which to create the server.
 * @param onListen - callback for the 'listen' function of the server.
 * @param onEnd - callback for the 'end' function of the socket.*/
module.exports = function(port, onListen, onEnd) {

    if (!port) {
        console.log("A port must be specified!");
        return;
    }

    /**
     * Map of commands and their handlers.
     * */
    this.command_map = {};

    /**
     * Add a new command to the command map.
     *
     * @param onCommand - string representing the command.
     * @param callback - function to be called for the specified command defined by the onCommand parameter.
     * */
    this.on = function(onCommand, callback) {
        this.command_map[onCommand] = callback;
    };

    /**
     * Create the server.
     * */
    this.create = giveMapContext(this.command_map);

    /******************************************************************************************************************
     * Auxiliary Functions which depend on the context (ei. port, onListen or onEnd).
     ******************************************************************************************************************/

    function defaultOnListen() {
        console.log("Socket is listening on port: " + port);

        if (onListen)
            onListen();
    }

    function defaultOnEnd() {
        console.log("Client Disconnected from Socket");

        if (onEnd)
            onEnd();
    }

    /** Create context for the server creation function.
     *
     * @param mapper - the map of commands
     * */
    function giveMapContext(mapper) {
        /**
         * This function verifies if any commands have been defined and returns
         * without creating the server if they haven't.
         *
         * This is done because the creation of the server does not make sense without
         * the definition of at least one command.
         * */
        return function() {

            if (Object.keys(mapper).length === 0) {
                console.log("The mapper is empty! Please define a command before creating the socket!");
                return;
            }

            /**
             * Create the socket server
             * */
            var server = net.createServer(function(socket) {

                //aqui recebo dados vindos do CardioWheel
                socket.on('data', function parseDataAndCallFunction(receivedData) {
                    var cmd_parts = parseReceivedData(receivedData);

                    // Check if the command is valid.
                    if (!mapper[cmd_parts.command]) {
                        console.log("The command was not valid: " + receivedData);
                        printCommandList(mapper);
                        return;
                    }

                    // Call the corresponding function, passing the respective arguments.
                    mapper[cmd_parts.command](cmd_parts.args);
                });

                socket.on('end', defaultOnEnd);
            });

            server.listen(port, defaultOnListen);
        }
    }
}

/**********************************************************************************************************************
 * Other Auxiliary Functions
 **********************************************************************************************************************/

/**
 * Deserialize the incoming command and arguments.
 * The command should be in the following format: '[COMMAND] [ARGUMENT]'
 *
 * @param receivedData - the data received via the socket.
 * */
function parseReceivedData(receivedData) {
    var data = receivedData.toString();
    console.log("Command received: " + data);

    var data_parts = data.split(" ");

    return {
        command: data_parts[0],
        args: data_parts[1]
    }
}

/**
 * Auxiliary function for printing the commands which the module can receive on the Socket.
 *
 * @param mapper - the map of commands
 */
function printCommandList(mapper) {
    console.log("-----------------------------------------------");
    console.log("The following commands are accepted: ")
    var accepted_commands = Object.keys(mapper);

    if (accepted_commands.length === 0) {
        console.log("No commands have been defined!");
        console.log("-----------------------------------------------");
        return
    }

    for (var i = 0; i < accepted_commands.length; ++i) {
        console.log("\t" + accepted_commands[i]);
    }
    console.log("-----------------------------------------------");
}
