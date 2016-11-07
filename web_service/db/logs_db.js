'use strict'

const dbUtils = require('./dbUtils');
const ErrorMessage = dbUtils.createErrMsg;

const users_db = require('./users_db');

const users_ops = users_db.operations;

const TABLE_NAME = 'Logs';
const ROW_ID = 'id';
const ROW_USER = 'user';
const ROW_DATE = 'date';
const ROW_CONTEXTS = 'contexts';

// Check if the Groups Table exists and create it if it doesn't.
//
// callback signature: function(err)
function createTable(db, callback) {
    dbUtils.createTable(db, TABLE_NAME,
        ROW_ID + ' INTEGER PRIMARY KEY,'+
        ROW_USER + ' VARCHAR NOT NULL,'+
        ROW_DATE + ' VARCHAR NOT NULL,'+
        ROW_CONTEXTS + ' VARCHAR NOT NULL,'+
        'FOREIGN KEY (' + ROW_USER + ') REFERENCES ' +
            users_db.constants.TABLE_NAME +
            '(' + users_db.constants.ROW_EMAIL + ')',
        callback
    );
};

// Drop this table if it exists.
//
// callback signature: function(err)
function dropTable(db, callback) {
    dbUtils.dropTable(db, TABLE_NAME, callback);
};

// Add a new Log.
//
// 'user_email' specfiies the user which is creating the log.
// 'date' the date of creation of the log.
// 'contexts' the contexts associated to the log.
//
// callback signature: function(err, log_id)
function add(db, user_email, date, contexts, callback) {
    console.log('DEBUG: CONTEXTS');
    console.log(user_email);
    console.log(date);
    console.log(contexts);
    console.log(callback);
    db.run('INSERT INTO ' + TABLE_NAME +' VALUES(NULL,?,?,?)',
        [
            user_email,
            date,
            JSON.stringify(contexts)
        ],
        function(err) {
            if(err) {
                callback(
                    new ErrorMessage('An error occurred while trying to create the Log !', err));
                return;
            }

            callback();
        }
    );
}

// Get all the Logs.
//
// callback signature: function(err, [Groups])
function getAll(db, callback) {
    dbUtils.getAll(db, TABLE_NAME, function(err, logs){
        if(err){
            callback(err);
            return;
        }

        callback(undefined,
            convertLogFields(logs) );
    });
}

function convertLogFields(logs){
    for(var i = 0; i < logs.length; ++i){
        logs[i].contexts = JSON.parse(logs[i].contexts);
    }

    return logs;
}

// Remove a Log.
//
// 'log_id' specifies the id of the Log.
//
// callback signature: function(err)
function deleteLog(db, log_id, callback) {
    console.log('Attempting to delete a Log! (' + log_id + ')');

    db.run(
        'DELETE FROM ' + TABLE_NAME + ' WHERE ' + ROW_ID + '=$id',
        {
            $id: log_id
        },
        function(err) {
            if (err){
                callback(
                    new ErrorMessage('An error occurred while treying to remove the Group (' + log_id + ') !', err));

                return;
            }

            callback();
        }
    );
}

// Gaurantee the creation of the table before executing the desired function.
//
// 'postCreation' is the function to be executed after the table is gauranteed to exist.
// 'postCreation' signature: function().
function createTableBefore(db, postCreation) {
    createTable(db, function(err) {
        if (err) {
            console.log('ERROR: ' + err.error);
            console.log('EXTRA: ' + err.extra);
            return;
        }

        setFunctionsPostTableCreation();
        postCreation();
    });
}

// Remove the creation of the table after it has been created
// (executed by the first call to any function).
function setFunctionsPostTableCreation() {
    module.exports.operations.add = add;
    module.exports.operations.getAll = getAll;
    module.exports.operations.delete = deleteLog;
}


/*
    Export the operations.
*/
module.exports.operations = {
    createTable: createTable,
    droptable: dropTable,
    add: function(db, user_email, date, contexts, callback) {
        createTableBefore(db, function() {
            add(db, user_email, date, contexts, callback);
        })
    },
    getAll: function(db, callback) {
        createTableBefore(db, function() {
            getAll(db, callback);
        });
    },
    delete: function(db, callback) {
        createTableBefore(db, function() {
            deleteLog(db, callback);
        })
    }
}
/*
    Export the constant values which are pertinent to this table (Table name and Row names).
*/
module.exports.constants = {
    TABLE_NAME: TABLE_NAME,
    ROW_ID: ROW_ID,
    ROW_USER: ROW_USER,
    ROW_DATE: ROW_DATE,
    ROW_CONTEXTS: ROW_CONTEXTS
}