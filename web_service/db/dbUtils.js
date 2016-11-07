'use strict'

// Generic function which drops the specified table (tableName) form the specified Database (db).
function dropTable(db, tableName, callback){
    console.log("Attempting to DROP Table: "+tableName);

    if(tableName == undefined && callback){
        callback(
            new createErrMsg("no table was specified!"));
        return;
    }

    if(db == undefined && callback){
        callback(
            new createErrMsg("no Database instance was provided!"));
        return;
    }

    db.run(
        'DROP TABLE '+tableName,
        function(err){
            if(err){
                callback(
                    new createErrMsg('Error Dropping the table ('+tableName+') from the DB.'));
                return;
            }

            console.log('\''+tableName+'\' Table Dropped succesfully!')
            callback();
        }
    );
}

// Generic function which checks if the specified Database (db) has the specified Table (tableName).
// callback has to have the following signature: function(error, doesExist).
function tableExists(db, tableName, callback){
    console.log("Attempting to check if the Table ("+ tableName +') exists');

    if(tableName == undefined && callback){
        callback(
            new createErrMsg("no table was specified!"));
        return;
    }

    if(db == undefined && callback){
        callback(
            new createErrMsg("no Database instance was provided!"));
        return;
    }

    // the get method return the first row of the result set, if there are any results.
    db.get('SELECT name FROM sqlite_master WHERE type=\'table\' AND name=\''+tableName+'\'',
        function(err, row){
            // Determine if the table exists or not.
            let doesExist = (row != undefined);

            callback(err, doesExist);
        }
    );
}

function createTable(db, tableName, tableDefParams, callback){
    console.log("Attempting to CREATE Table: "+tableName);

    if(tableName == undefined && callback){
        callback(
            new createErrMsg("no table was specified!"));
        return;
    }

    if(db == undefined && callback){
        callback(
            new createErrMsg("no Database instance was provided!"));
        return;
    }

    tableExists(db, tableName, function(err, exists){
            if(err){
                callback(
                    new createErrMsg('error occurred checking if the db ('+tableName+') exists'));
                return;
            }

            if(!exists){
                console.log('\''+tableName+'\' TABLE DOES NOT EXIST, CREATING IT!')

                db.run(
                    'CREATE TABLE ' + tableName +
                    ' ( ' +
                        tableDefParams +
                    ' ) ',
                    [],
                    function(err){
                        if(err){
                            callback(
                                new createErrMsg('Error creating the table: '+tableName, err));
                            return;
                        }
                        callback();
                    }
                );
            }else{
                console.log(tableName+" Table already exists!");
                callback();
            }
    });
}

// Get all of the rows for a given table, in a given database.
//
// callback signature: function(err, rows)
function getAll(db, tableName, cb){
    console.log('Attempting to get all the rows for: '+tableName);

    db.all('SELECT * FROM '+tableName, {}, function(err, rows){
        if(err){
            cb(
                new createErrMsg('An error occurred while trying to obtain all the groups', err));
            return;
        }
        cb(undefined, rows);
    });
}

// Begins a transaction on the provided connection to the database.
//
// callback signature: function(err)
function BeginTrx(db, cb){
    db.run('BEGIN', cb);
}

// Commits a transaction on the provided connection to the database.
//
// callback signature: function(err)
function CommitTrx(db, cb){
    db.run('COMMIT', cb);
}

// Aborts a transaction on the provided connection to the database.
//
// callback signature: function(err)
function AbortTrx(db, cb){
    db.run('ABORT', cb);
}

// The name of the column which contaions the last inserted row id.
const TEMP_ROW_LAST_ID = 'id';

// Get the ROWID of the last inserted element.
//
// callback signature: function(err, id)
function getLastInsertRowId(db, cb){
    db.get('SELECT last_insert_rowid() as \''+TEMP_ROW_LAST_ID+'\'', function(err, result){
        cb(err, result[TEMP_ROW_LAST_ID]);
    });
}

// Creates an error message describing the error which occured.
// 'message' (string) describes the error.
// 'extra' (object) OPTIONAL information associated to the error.
function createErrMsg(message, extra){
    this.error = message;

    if(extra != undefined)
        this.extra = extra;
}

module.exports = {
    dropTable:              dropTable,
    tableExists:            tableExists,
    createTable:            createTable,
    beginTrans:             BeginTrx,
    commitTrans:            CommitTrx,
    abortTrans:             AbortTrx,
    getLastInsertRowId:     getLastInsertRowId,
    getAll:                 getAll,
    createErrMsg:           createErrMsg
}