var socket = require('./connect-to-socket');

var command_prefix = "EXHAUSTION_STATE";

module.exports = function(new_state){

    // Prepare the command
    console.log("NEW STATE RECEIVED: " + new_state);

    var cmd_to_send = command_prefix + " " + new_state;

    console.log("COMMAND: " + cmd_to_send);

    // Connect and send the command.
    socket.connect(cmd_to_send);
    
    return false;
}