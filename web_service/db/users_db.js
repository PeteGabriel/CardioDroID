'use strict'

const dbUtils = require('./dbUtils');
const ErrorMessage = dbUtils.createErrMsg;

const groups_db = require('./groups_db');

const group_ops = groups_db.operations;

const TABLE_NAME = "Users";

const ROW_EMAIL = 'email';
const ROW_NAME = 'name';
const ROW_GROUP = 'user_group';
const ROW_TYPE = 'type';

// Check if the Users Table exists and create it if it doesn't.
//
// callback: function(error).
function createTable(db, callback) {
    group_ops.createTable(db, function(err){
        if(err){
            callback(
                new ErrorMessage('Unable to create Groups Table and User Table', err));
            return;
        }
        // Create the Users Table.
        dbUtils.createTable(db, TABLE_NAME,
            ROW_EMAIL + ' TEXT NOT NULL PRIMARY KEY,' + // Only one user can be registered per email address.
            ROW_NAME + ' TEXT NOT NULL,' +
            ROW_GROUP + ' INTEGER,' +
            'FOREIGN KEY (' + ROW_GROUP + ') REFERENCES ' +
                groups_db.constants.TABLE_NAME +
                '(' + groups_db.constants.ROW_NAME + ')', // Can be NULL (user doesnt belong to any group.)
            callback
        );
    })
};

// Drop this table if it exists.
//
// callback: function(error).
function dropTable(db, callback) {
    dbUtils.dropTable(db, TABLE_NAME, callback);
}

// Add a new User.
//
// The users 'email' and 'name' are mandatory.
// The user does not belong to a group upon creation, he must be
// added to a group post creation.
//
// callback signature: function(error, email)
function add(db, email, name, callback) {
    console.log("Attempting to add the User: " + email + ', ' + name);

    db.run(
        'INSERT INTO ' + TABLE_NAME + ' VALUES(?,?,NULL)', [
            email,
            name,
        ],
        function(err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred trying to insert the User (' + email + ')', err));
                return;
            }

            callback(undefined, email);
        }
    );
};

// Get all the Users.
//
// callback signature: function(err, [User])
function getAll(db, callback) {
    console.log('Attempting to get all the Users!');

    db.all(
        'SELECT * FROM ' + TABLE_NAME, [],
        function(err, users) {
            if (err) {
                callback(
                    new ErrorMessage('An eror occurred while trying to get all of the Users', err));
                return;
            }

            callback(err, users);
        }
    );
}

// Get all of the Users for a given Group.
//
// 'group_id' (integer) the id of the group.
//
// callback signature: function(error, [User])
function getAllForGroup(db, group_id, callback) {
    console.log('Attempting to get the Users of Group ' + group_id + '!');

    db.all(
        'SELECT * FROM ' + TABLE_NAME + ' WHERE ' + ROW_GROUP + '=$group', {
            $group: group_id
        },
        function(err, users) {
            if (err) {
                callback(
                    new ErrorMessage('An eror occurred while trying to get all of the Users for a group (id = ' + group_id + ')', err));
                return;
            }
            callback(err, users);
        }
    );
}

// Get a User.
//
// callback signature: function(err, User)
function get(db, email, callback) {
    console.log('Attempting to get a User! (' + email + ')');

    db.get(
        'SELECT * FROM ' + TABLE_NAME + ' WHERE ' + ROW_EMAIL + '=$email', {
            $email: email
        },
        function(err, user) {
            if (err) {
                callback(
                    new ErrorMessage('An error occured while trying to get the User (' + email + ')', err));
                return;
            }
            callback(err, user);
        }
    );
}

// Update the Users Group.
//
// The User is specified by 'email'.
//
// If the group is udefined, the User will be removed from the group to
// which he/she belongs.
//
// callback signature: fucntion(err).
function updateGroupForUser(db, email, group, callback) {
    console.log('Attempting to UPDATE the Users (' + email + ') Group (' + group + ')');

    db.run(
        'UPDATE ' + TABLE_NAME + ' SET ' + ROW_GROUP + '=$group WHERE ' + ROW_EMAIL + '=$email', {
            $email: email,
            $group: group // Value can be null or undefined.
        },
        function(err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to update the user (' + email + ')', err));
                return;
            }

            callback();
        })
}

// Update the User.
//
// Updates the User specified by 'email'.
//
// callback signature: fucntion(err).
function update(db, email, name, group, callback) {
    console.log('Attempting to UPDATE the User (' + email + ')!');

    db.run(
        'UPDATE ' + TABLE_NAME + ' SET ' + ROW_NAME + '=$name,' + ROW_GROUP + '=$group WHERE ' + ROW_EMAIL + '=$email', {
            $email: email,
            $name: name,
            $group: group // Value can be null or undefined.
        },
        function(err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to update the user (' + email + ')', err));
                return;
            }
            callback();
        })
}

// Remove a User.
//
// callback signature: function(err)
function deleteUser(db, email, callback) {
    console.log('Attempting to delete a User (' + email + ')');

    db.run(
        'DELETE FROM ' + TABLE_NAME + ' WHERE ' + ROW_EMAIL + '=$email', {
            $email: email
        },
        function(err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to delete the User (' + email + ')', err));
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
    module.exports.operations.getAllForgroup = getAllForGroup;
    module.exports.operations.get = get;
    module.exports.operations.updateGroup = updateGroupForUser;
    module.exports.operations.update = update;
    module.exports.operations.delete = deleteUser;
}

/*
    Export the operations.
*/
module.exports.operations = {
    createTable: createTable,
    dropTable: dropTable,
    add: function(db, email, name, type, callback) {
        createTableBefore(db, function() {
            add(email, name, type, callback);
        });
    },
    getAll: function(db, callback) {
        createTableBefore(db, function() {
            getAll(db, callback);
        });
    },
    getAllForgroup: function(db, group_id, callback) {
        createTableBefore(db, function() {
            getAllForGroup(db, group_id, callback);
        });
    },
    get: function(db, email, callback) {
        createTableBefore(db, function() {
            get(db, email, callback);
        });
    },
    updateGroup: function(db, email, group, callback) {
        createTableBefore(db, function() {
            updateGroupForUser(db, email, group, callback);
        });
    },
    update: function(db, email, name, group, callback) {
        createTableBefore(db, function() {
            update(db, email, name, group, callback);
        });
    },
    delete: function(db, email, callback) {
        createTableBefore(db, function() {
            deleteUser(db, email, callback);
        })
    }
}

/*
    Export the constant values which are pertinent to this table.
*/
module.exports.constants = {
    TABLE_NAME: TABLE_NAME,
    ROW_EMAIL: ROW_EMAIL,
    ROW_NAME: ROW_NAME,
    ROW_GROUP: ROW_GROUP
};