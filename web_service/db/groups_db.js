'use strict'

const dbUtils = require('./dbUtils');
const ErrorMessage = dbUtils.createErrMsg;

const TABLE_NAME = 'Groups';
const ROW_NAME = 'name';

// Check if the Groups Table exists and create it if it doesn't.
//
// callback signature: function(err)
function createTable(db, callback) {
    dbUtils.createTable(db, TABLE_NAME,
        ROW_NAME + ' TEXT PRIMARY KEY',
        callback
    );
};

// Drop this table if it exists.
//
// callback signature: function(err)
function dropTable(db, callback) {
    dbUtils.dropTable(db, TABLE_NAME, callback);
};

// Add a new Group.
//
// 'name' specifies the name of the group to be created.
//
// callback signature: function(err, group_name)
function add(db, name, callback) {
    db.run('INSERT INTO ' + TABLE_NAME +' VALUES(?)',
        [
            name
        ],
        function(err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to create the Group (' + name + ') !', err));
                return;
            }

            callback(undefined, name);
        }
    );
}

// Get all the Groups.
//
// callback signature: function(err, [Groups])
function getAll(db, callback) {
    dbUtils.getAll(db, TABLE_NAME, callback);
}

// Get a Group.
//
// 'group_id' specifies the id of the group.
//
// callback signature: function(err, group)
function getGroup(db, group_name, callback) {
    db.get('SELECT * FROM ' + TABLE_NAME + ' WHERE ' + ROW_NAME + '=$name', {
        $name: group_name
    }, function(err, group) {
        if (err) {
            callback(
                new ErrorMessage('An error occured while trying to obtain a group (' + group_name + ')', err));
            return;
        }

        callback(undefined, group);
    });
}

// Remove a Group.
//
// 'group_id' specifies the id of the group.
//
// callback signature: function(err)
function deleteGroup(db, group_name, callback) {
    console.log('Attempting to delete a Group! (' + group_name + ')');

    db.run(
        'DELETE FROM ' + TABLE_NAME + ' WHERE ' + ROW_NAME + '=$name', 
        {
            $name: group_name
        },
        function(err) {
            if (err){
                callback(
                    new ErrorMessage('An error occurred while treying to remove the Group (' + group_name + ') !', err));

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
    module.exports.operations.get = getGroup;
    module.exports.operations.delete = deleteGroup;
}


/*
    Export the operations.
*/
module.exports.operations = {
    createTable: createTable,
    droptable: dropTable,
    add: function(db, name, callback) {
        createTableBefore(db, function() {
            add(db, name, callback);
        })
    },
    getAll: function(db, callback) {
        createTableBefore(db, function() {
            getAll(db, callback);
        });
    },
    get: function(db, group_id, callback) {
        createTableBefore(db, function() {
            getGroup(db, group_id, callback);
        })
    },
    delete: function(db, group_id, callback) {
        createTableBefore(db, function() {
            deleteGroup(db, group_id, callback);
        })
    }
}
/*
    Export the constant values which are pertinent to this table (Table name and Row names).
*/
module.exports.constants = {
    TABLE_NAME: TABLE_NAME,
    ROW_NAME: ROW_NAME
}