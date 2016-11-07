const dbUtils = require("../dbUtils");
const sqlite3 = require("sqlite3");


// Supplies a connection to the specified database ('dbName').
//
// Toggles the use of FOREIGN KEYs on.
module.exports.getDb = function(dbName){
  var db = new sqlite3.Database(dbName);

  db.run('PRAGMA foreign_keys = ON;');

  return db;
}

// Supplies a connection to the specified database ('dbName') for debug pruposes.
//
// Toggles the use of FOREIGN KEYs on.
//
// In debug mode, a full stack trace is supplied when an error is thrown.
//
// All queries made will be given to the 'debugCb' before they are made.
module.exports.getDebugDb = function(dbName, debugCb){
  var verboseSqlite3 = sqlite3.verbose();
  var db = new verboseSqlite3.Database(dbName);

  db.run('PRAGMA foreign_keys = ON;');

  db.on('trace', function(query){
    debugCb(query);
  });

  return db;
}