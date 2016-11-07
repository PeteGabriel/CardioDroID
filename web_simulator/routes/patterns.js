'use strict';
var express = require('express');
var router = express.Router();
var sendState = require('../coms/ident-auth-coms');

var db = require("../storage/DriverStorage").actions;
var getPatternsFromDb = db.getAllPatterns;
var getDriversFromDb = db.getAllDrivers;
var getUserFromPattern = db.getUserFromPattern;

var debug = function(msg) {
    console.log(msg);
};

var TIMEOUT_INTERVAL = 1000;

var MAX_AUTH_ATTEMPTS = 3;
var authenticate_attempts = 0;
var authentication_result = false; // For internal authentication only

var OK = "OK";
var NOT_OK = "NOT_OK";
var AUTHENTICATE = "A";
var IDENTIFY = "I";

function pattern_router(aim_container, arp_container) {
    /** GET all the patterns which have been stored */
    router.get('/', function(req, res) {
        getPatternsFromDb(function(err, patternArr) {
            if (err) {
                debug(err);
                res.status(500).end("Error occurred accessing the database.");
                // Internal Server error.
            }

            var patterns = {
                patterns: patternArr
            };
            res.set('Content-Type', 'application/json');
            res.end(JSON.stringify(patterns));
        });
    });

    router.get('/auth-status', function(req, res) {
        var aim_value = aim_container.getValue();
        debug(aim_value);
        if (aim_value === OK || aim_value === NOT_OK) {
            res.end(JSON.stringify({
                state: aim_value
            }));
        } else {
            res.status(404).end(JSON.stringify({
                error: "The aim value has not been set yet."
            }));
        }
    });

    /** POST a pattern for Authentication/Identification */
    router.post('/', function(req, res) {
        debug("POST REQUEST MADE FOR PATTERN: " + req.body.pattern);
        debug(aim_container);
        var arp_value = arp_container.getValue();
        debug("ARP VALUE: " + arp_value);

        getUserFromPattern(req.body.pattern, function(err, user) {

            debug("UserFromPattern CALLBACK called ");
            debug("USER = " + user);

            if (err) {
                debug(err);
                res.status(500).end("Error occurred accessing the database.");
                return;
            }

            if (!user) {
                debug("Pattern did not correspond to any user in the database.");
                res.status(404).end(JSON.stringify({
                    error: "The pattern supplied is new, and does not have a user associated."
                })); // Not Found
            }

            // Process of Identification
            else if (arp_value === IDENTIFY) {
                // Send the users id to the BLE server
                // to set the characteristic and await authentication.
                debug("Identification - user id sent to characteristic");
                sendState(user);
                res.status(204).end(); // No Content
            }

            // Process of Authentication
            else if (arp_value === AUTHENTICATE) {
                debug("AUTHENTICATION MODE - user: " + user);
                /** This function attempts to authenticate the user,
                 * by checking if the user, which corresponds to the
                 * pattern provided, matches the user provided via the AIM characteristic
                 * */
                var authenticateUser;
                authenticateUser = function() {
                    debug("pattern::authenticateUser attempt = " + authenticate_attempts);
                    // Maximum attempts to try and obtain the user
                    // id from the AIM characteristic reached.
                    // Indicate that the authentication process was not successful.
                    if (++authenticate_attempts === MAX_AUTH_ATTEMPTS) {
                        debug("MAX AUTHENTICATION ATTEMPTS REACHED");
                        sendState(NOT_OK);
                        authenticate_attempts = 0;
                        return;
                    }

                    var ext_user_id = aim_container.getValue();

                    // If no user has been supplied to the BLE on the AIM characteristic
                    // then we must set a call to this function in a certain amount of time.
                    if (!ext_user_id) {
                        setTimeout(authenticateUser, TIMEOUT_INTERVAL);
                    }
                    // If a user id was supplied by the AIM characteristic,
                    // then we can make an attempt at matching it.
                    else if (ext_user_id === user) {
                        debug("The users match: " + user.id + " ==" + ext_user_id);
                        sendState(OK);
                        res.status(204).end()
                    }
                    // The user id supplied does not match the
                    // one corresponding to the pattern supplied.
                    else {
                        debug("The users do not match: " + user + " !=" + ext_user_id);
                        sendState(NOT_OK);
                        res.status(404).end(JSON.stringify( // Not Found
                            {
                                error: "The pattern supplied does not match the user id provided (External)."
                            }
                        ));
                    }
                };
                authenticateUser();
            }
            // ARP has not been set!
            else{
                res.status(500).end(JSON.stringify({
                    error: "The ARP value has not been specified."
                }));
              }
        });
    });

    return router;
}

module.exports = pattern_router;
