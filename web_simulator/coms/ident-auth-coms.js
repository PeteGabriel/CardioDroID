var socket = require('./connect-to-socket');

var command_prefix = "USER_ID_STATE";

module.exports = function(new_state){

    console.log("NEW STATE RECEIVED: " + new_state);

    var cmd_to_send = command_prefix + " " + new_state;

    console.log("COMMAND: " + cmd_to_send);

    socket.connect(cmd_to_send);
}