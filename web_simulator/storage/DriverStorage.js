'use strict';
var sqlite3 = require('sqlite3').verbose();

var debug = function(msg) {
    console.log(msg);
};

var DB_NAME = './DriversDB';

var TableName = "Driver";
var HeartRatePattern = "heartRatePattern";
var UserId = "user_id";
var asCount = "count";

/* SELECT Statements */
var SelectAllDrivers = "SELECT " + HeartRatePattern + ", " + UserId + " FROM " + TableName;
var SelectAllPatterns = "SELECT " + HeartRatePattern + " FROM " + TableName;
var SelectCountEntries = "SELECT count(" + HeartRatePattern + ") as " + asCount + " FROM " + TableName;

/* INSERT Statements*/
var InsertDriver = "INSERT INTO DRIVER VALUES(?, ?, ?)";


function open() {
    return new sqlite3.Database(DB_NAME);
}

function configure(createCallback) {
    var db = open();
    db.serialize(function() {
        db.run("CREATE TABLE Driver (id NUMERIC PRIMARY KEY, user_id TEXT NOT NULL, heartRatePattern TEXT NOT NULL)", [], createCallback);
    });

    db.close();
}

/**
 * Produce Array of Patterns.
 * callback consumes patterns, as per async function, signature: function(err, patterns). */
function getPatternsFromDb(callback) {
    var patterns = [];
    open().each(SelectAllPatterns,
        function(err, row) { // for each
            if (err) {
                debug(err);
                return;
            }
            patterns.push(row[HeartRatePattern]);
        },
        function(err) { //, num_rows) {             // on complete
            if (err) {
                debug(err);
                callback(err);
                return;
            }
            callback(null, patterns);
        });
}
/**
 * Get all the drivers
 * Produce Array of Drivers [{id, pattern}, ... ]
 * callback consumes drivers, as per async function, signature: function(err, drivers). */
function getDriversFromDb(callback) {
    var drivers = [];
    open().each(SelectAllDrivers,
        function(err, row) {
            if (err) {
                debug(err);
                return;
            }
            drivers.push(new Driver(row.id, row.user_id, row[HeartRatePattern]));
        },
        function(err){//, num_rows) {
            if (err) {
                debug(err);
                callback(err);
                return;
            }
            callback(null, drivers);
        });
}

/**
 * Get the corresponding user for a pattern.
 * callback consumes the user_id of the given pattern, as per async function, signature: function(err, drivers). */
function getUserFromPattern(pattern, callback) {
    debug("pattern::getUserFromPattern called");

    getDriversFromDb(function(err, drivers) {
        if (err) {
            debug(err);
            callback(err);
            return;
        }
        // Attempt to match the pattern to a user id.
        for (var i = 0; i < drivers.length; ++i) {
            if (drivers[i].pattern === pattern) {
                debug("Match Found: " + drivers[i].user_id);
                callback(null, drivers[i].user_id);
                return;
            }
        }
        // No user was found.
        callback(null, null);
    });
}

/**
 * id (IDENTITY): Z
 * user_id: YYYZ
 * pattern: XXXX-XXXX-YYYZ */
function insertDriverIntoDb(callback) {
    open().get(SelectCountEntries, function(err, row) {
        var generatedDriver = generateRandomDriver(row[asCount]);

        debug("New Driver: " + JSON.stringify(generatedDriver));

        open().get(InsertDriver, generatedDriver.id, generatedDriver.user_id, generatedDriver.pattern,
            function(err, rows) {
                if (err){
                    callback(err);
                }
                if (rows !== 0) {
                    debug("Driver Created Successfully!");
                    callback(null, generatedDriver);
                }

            });
    });
}

module.exports = {
    configure: configure,
    actions: {
        getUserFromPattern: getUserFromPattern,
        getAllDrivers: getDriversFromDb,
        getAllPatterns: getPatternsFromDb,
        addDriver: insertDriverIntoDb
    }
};

/**
 * Function for Creating a Driver instance. */
function Driver(id, user_id, pattern) {
    this.id = id;
    this.user_id = user_id;
    this.pattern = pattern;
}

/** AUX FUNCTIONS */

function generateRandomDriver(id) {
    // Produce the synthetic user_id: a random 3 character string + id.
    var user_id = generateRandomDigits(3) + id;

    // Produce the synthetic pattern: a random 4 character string + user_id.
    var pattern = generateRandomDigits(4) + user_id;

    return new Driver(id, user_id, pattern);
}

function generateRandomDigits(numDigitsToProduce) {
    var randomDigitStr = "";
    var i;
    for (i = 0; i < numDigitsToProduce; ++i) {
        randomDigitStr += Math.round(Math.random() * 9);
    }
    return randomDigitStr;
}


// WORKING TEST -> Obtain all drivers
/*const db_name = "../bin/DriversDB";
const db = new sqlite3.Database(db_name);*/

/*db.get(SelectCountEntries, function(err, row){
    return row[asCount];
});*/

/*db.each("SELECT heartRatePattern as pattern, id FROM Driver", function(err, row) {
    if(err)
        debug(err);

    debug("user: "+row.id+" pattern: "+row.pattern);
});*/

/*db.each("SELECT heartRatePattern as pattern FROM Driver", function(err, row) {
if(err)
debug(err);

debug("pattern: "+row.pattern);
});*/
