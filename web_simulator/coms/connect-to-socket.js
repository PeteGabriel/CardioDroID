var net = require('net');


function connect(toSend) {
    var client = net.connect({
        port: 3000
    },function(){
        console.log("connected to socket, sending data: "+toSend);
        client.end(toSend);
    });

    client.on('error', function (err) {
        console.log("ERROR occurred whilst trying to socket to the socket: " + err);
    });

    client.on('end', function(){console.log("Disconnected from Socket")})
}

function connectWithResult(toSend, callback) {
    var client = net.connect({
        port: 3000
    },function(){
        console.log("connected to socket, sending data: "+toSend);
        client.send(toSend);
    });

    client.on('data', function(data){
        callback(data.toString());
        client.end();
    });

    client.on('error', function (err) {
        console.log("ERROR occurred whilst trying to socket to the socket: " + err);
    });

    client.on('end', function(){console.log("Disconnected from Socket")})
}

module.exports = {
    connect:connect,
    connectWithResult:connectWithResult
}