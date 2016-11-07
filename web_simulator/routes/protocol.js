'use strict';
var express = require('express');
var router = express.Router();
var connect = require('../coms/connect-to-socket');

var states = {
    'A': "Authentication",
    'I': "Identification"
}

// The container received holds the value of the ARP characteristic (sent via socket).
function protocol_router(arp_container) {
    // Obtain the Current state of the ARP.
    router.get("/", function (req, res, next) {

        var arp_value = arp_container.getValue();

        if(arp_value == 'A' || arp_value == 'I') {
            var obj2send = {
                state: states[arp_value],
                state_short: arp_value
            };

            res.set('Content-Type', 'application/json');
            res.end(JSON.stringify(obj2send));
        }else
            // TODO - How to handle no value found
            res.status(404).end(JSON.stringify({error: "The 'Access Rights Protocol' value has not yet been defined."}));
    });
    return router;
}

module.exports = protocol_router;