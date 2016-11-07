'use strict'

const dbUtils = require('./dbUtils');
const ErrorMessage = dbUtils.createErrMsg;

const users_db = require('./users_db');

const users_ops = users_db.operations;

const TABLE_NAME = "Rules";

const ROW_ID = 'id';
const ROW_RULE = 'jsonRule';
const ROW_CREATOR = 'creator';
const ROW_IS_PRIVATE = 'isPrivate';

// Check if the Rules Table exists and create it if it doesn't.
//
// callback signature: function(err)
function createTable(db, callback) {
    // TODO create users table beofre creating rules table ???
    users_ops.createTable(db, function (err) {
        if (err) {
            callback(
                new ErrorMessage('Unable to create Users Table and Rules Table', err));
            return;
        }

        dbUtils.createTable(db, TABLE_NAME,
            ROW_ID + ' INTEGER NOT NULL, ' +
            ROW_CREATOR + ' TEXT NOT NULL,' +
            ROW_RULE + ' TEXT NOT NULL, ' +
            ROW_IS_PRIVATE + ' INTEGER NOT NULL,' +
            'PRIMARY KEY (' + ROW_CREATOR + ', ' + ROW_ID + '),' +
            'FOREIGN KEY (' + ROW_CREATOR + ') REFERENCES ' +
            users_db.constants.TABLE_NAME +
            '(' + users_db.constants.ROW_EMAIL + ')',
            callback
        );
    })
};

// Drop this table if it exists.
//
// callback signature: function(err)
function dropTable(db, callback) {
    dbUtils.dropTable(db, TABLE_NAME, callback);
}

// Add a new Rule.
//
// 'isPrivate' is a BOOLEAN value indicating whether the rule can be seen
// by the members of the group the user belongs too.
//
// callback signature: function(err, rule_id)
function add(db, id, owner_email, rule, isPrivate, callback) {
    console.log("Attempting to create a Rule for: " + owner_email);

    db.run(
        'INSERT INTO ' + TABLE_NAME + ' VALUES(?,?,?,?)', [
            id,
            owner_email,
            JSON.stringify(rule),
            isPrivate ? 1 : 0
        ],
        function (err) {
            if (err) {
                console.log(err);
                callback(
                    new ErrorMessage('An error occurred trying to insert the Rule for ' + owner_email + ' !', err));
                return;
            }

            callback(undefined, id);
        }
    );
}

// Get all the Rules.
//
// callback signature: function(err, [Rule])
function getAll(db, callback) {
    console.log('Attempting to get all the Rules!');

    dbUtils.getAll(db, TABLE_NAME, function (err, rules) {
        if (err) {
            callback(err);
            return;
        }

        // Make necessary conversions to the Rules.
        for (let i = 0; i < rules.length; i++)
            rules[i] = convertRuleFields(rules[i]);

        callback(undefined, rules);
    });
}

// Get a Rule.
//
// callback signature: function(err, rule)
function getRule(db, id, callback) {
    console.log('Attempting to get a Rule (id=' + id + ')');

    db.get(
        'SELECT * FROM ' + TABLE_NAME + ' WHERE ' + ROW_ID + '=$id', {
            $id: id
        },
        function (err, rule) {
            if (err) {
                callback(
                    new ErrorMessage('An error occured while trying to get the Rule (id=' + id + ') !', err));
                return;
            }

            if (!rule) {
                callback(
                    new ErrorMessage('The specified Rule (' + id + ') does not exist'));
                return;
            }

            callback(err,
                convertRuleFields(rule));
        })
}

// Get all the SHARED and PRIVATE Rules for a User.
//
// 'user_email' specifies the User whos Rules we must get.
//
// callback signature: function(err, [Rule])
function getAllForUser(db, user_email, callback) {
    console.log('Attempting to get all the Rules for a User (' + user_email + ')');

    getForUser(db, user_email, false, function (err, user_rules) {
        if (err) {
            callback(
                new ErrorMessage('Error occurred while trying to obtain the Users Rules', err));
            return;
        }

        users_ops.get(db, user_email, function (err, user) {
            if (err) {
                callback(
                    new ErrorMessage('Error occurred while trying to obtain the User information', err));
                return;
            }

            // check to see if the user belongs to a group.
            if (user && user.user_group != null) {
                // If the user belongs to a group,
                // get the public rules from the other users in the group.
                db.all(
                    'SELECT * FROM ' + TABLE_NAME +
                    ' WHERE ' + ROW_CREATOR + ' IN (SELECT ' + users_db.constants.ROW_EMAIL +
                    ' FROM ' + users_db.constants.TABLE_NAME +
                    ' WHERE ' + users_db.constants.ROW_EMAIL + '!=$email ' +
                    'AND ' + users_db.constants.ROW_GROUP + '=$group) ' +
                    'AND ' + ROW_IS_PRIVATE + '=0',
                    {
                        $email: user_email,
                        $group: user.user_group
                    },
                    function (err, group_rules) {
                        if (err) {
                            callback(
                                new ErrorMessage('Error occurred while trying to obtain the Rules for the Group', err));
                            return;
                        }

                        // Convert and Add the group rules to the array of the Users rules.
                        group_rules.forEach(function (group_rule) {
                            user_rules.push(convertRuleFields(group_rule));
                        });

                        callback(undefined, user_rules)
                    }
                );
            }
            else
                callback(undefined, user_rules);
        })
    })
}

// Get all the SHARED Rules for a User.
//
// 'user_email' specifies the User whos non private Rules we must get.
//
// callback signature: function(err, [Rules])
function getAllSharedForUser(db, user_email, callback) {
    console.log('Attempting to get all the SHARED Rules for a User (' + user_email + ')!');

    getForUser(db, user_email, true, callback);
}

// Auxiliary function: Get all the private or shared Rules for a User.
//
// 'user_email' specifies the User whos Rules we must get.
//
// The 'getShared' parameter indicates whether all Rules should be
// obtained (getShared = false) or just the shared Rules (getShared = true).
//
// callback signature: function(err, [Rule])
function getForUser(db, user_email, getShared, callback) {
    let query = 'SELECT * FROM ' + TABLE_NAME + ' WHERE ' + ROW_CREATOR + '=$creator';

    if (getShared)
        query += ' AND ' + ROW_IS_PRIVATE + '=0';

    db.all(
        query, {
            $creator: user_email
        },
        function (err, rules) {
            if (err) {
                callback(
                    new ErrorMessage('An eror occurred while trying to get all of the Rules!', err));
                return;
            }

            rules = rules.filter(function (item) {
                return item.$creator === user_email;
            });

            for (let i = 0; i < rules.length; i++)
                rules[i] = convertRuleFields(rules[i]);

            callback(err, rules);
        }
    );
}

// Update the rule of a Rule
//
// callback signature: function(err)
function updateJsonRule(db, id, rule, callback) {
    console.log('Attempting to update the jsonRule of a Rule (' + id + ')!');

    db.run(
        'UPDATE ' + TABLE_NAME + ' SET ' + ROW_RULE + '=$rule WHERE ' + ROW_ID + '=$id', {
            $rule: JSON.stringify(rule),
            $id: id
        },
        function (err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to update the Rule (id=' + id + ')!', err));
                return;
            }
            callback();
        })
}

// Update the rule of a Rule
//
// callback signature: function(err)
function updatePrivacy(db, id, isPrivate, callback) {
    console.log('Attempting to update the privacy of a Rule (' + id + ')');

    db.run(
        'UPDATE ' + TABLE_NAME + ' SET ' + ROW_IS_PRIVATE + '=$isPrivate WHERE ' + ROW_ID + '=$id', {
            $isPrivate: isPrivate ? 1 : 0,
            $id: id
        },
        function (err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to update the Rule (id=' + id + ')!', err));
                return;
            }
            callback();
        })
}

// Update the rule of a Rule
//
// callback signature: function(err)
function update(db, id, rule, isPrivate, callback) {
    console.log('Attempting to update a Rule (' + id + ')');

    db.run(
        'UPDATE ' + TABLE_NAME + ' SET ' + ROW_RULE + '=$rule,' + ROW_IS_PRIVATE + '=$isPrivate WHERE ' + ROW_ID + '=$id', {
            $rule: JSON.stringify(rule),
            $isPrivate: isPrivate ? 1 : 0,
            $id: id
        },
        function (err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to update the Rule (id=' + id + ')!', err));
                return;
            }
            callback();
        })
}

// Delete a Rule.
//
// callback signature: fucntion(err)
function deleteRule(db, id, callback) {
    console.log('Attempting to delete a Rule (id=' + id + ')');

    db.run(
        'DELETE FROM ' + TABLE_NAME + ' WHERE ' + ROW_ID + '=$id', {
            $id: id
        },
        function (err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to delete the Rule (id=' + id + ') !', err));
                return;
            }
            callback();
        }
    );
}

// Delete all of the Rules for a given User
//
// callback signature: function(err)
function deleteAllForUser(db, user_email, callback) {
    console.log('Attempting to delete the Rules for a User (email=' + user_email + ')');

    db.run('DELETE FROM ' + TABLE_NAME + ' WHERE ' + ROW_CREATOR + '=$email', {
        $email: user_email
    },
        function (err) {
            if (err) {
                callback(
                    new ErrorMessage('An error occurred while trying to delete the Rules for a User (email=' + user_email + ') !', err));
                return;
            }

            callback();
        })
}

// Do the necessary conversions for the database Rule object.
function convertRuleFields(rule) {
    rule = convertIsPrivate2boolean(rule);
    rule[ROW_RULE] = JSON.parse(rule[ROW_RULE]);
    return rule;
}

// Convert the isPrivate field of a rule form the INTEGER value stored in
// the sql database, to a BOOLEAN value.
//
// 'rule' is the Rule object which is stored in the sql database.
function convertIsPrivate2boolean(rule) {
    rule[ROW_IS_PRIVATE] = rule[ROW_IS_PRIVATE] == 0 ? false : true;
    return rule;
}

// Gaurantee the creation of the table before executing the desired function.
//
// 'postCreation' is the function to be executed after the table is gauranteed to exist.
// 'postCreation' signature: function().
function createTableBefore(db, postCreation) {
    createTable(db, function (err) {
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
    module.exports.operations.get = getRule;
    module.exports.operations.getAllForUser = getAllForUser;
    module.exports.operations.getAllSharedForUser = getAllSharedForUser;
    module.exports.operations.updateJsonRule = updateJsonRule;
    module.exports.operations.updatePrivacy = updatePrivacy;
    module.exports.operations.update = update;
    module.exports.operations.delete = deleteRule;
    module.exports.operations.deleteAllForUser = deleteAllForUser;
}

/*
    Export the operations.
*/
module.exports.operations = {
    add: function (db, id, owner_email, rule, isPrivate, callback) {
        createTableBefore(db, function () {
            add(db, id, owner_email, rule, isPrivate, callback);
        })
    },
    getAll: function (db, callback) {
        createTableBefore(db, function () {
            getAll(db, callback);
        })
    },
    get: function (db, id, callback) {
        createTableBefore(db, function () {
            getRule(db, id, callback);
        })
    },
    getAllForUser: function (db, user_email, callback) {
        createTableBefore(db, function () {
            getAllForUser(db, user_email, callback);
        })
    },
    getAllSharedForUser: function (db, user_email, callback) {
        createTableBefore(db, function () {
            getAllSharedForUser(db, user_email, callback);
        })
    },
    updateJsonRule: function (db, id, rule, callback) {
        createTableBefore(db, function () {
            updateJsonRule(db, id, rule, callback);
        })
    },
    updatePrivacy: function (db, id, isPrivate, callback) {
        createTableBefore(db, function () {
            updatePrivacy(db, id, isPrivate, callback);
        })
    },
    update: function (db, id, rule, isPrivate, callback) {
        createTableBefore(db, function () {
            update(db, id, rule, isPrivate, callback);
        })
    },
    delete: function (db, id, callback) {
        createTableBefore(db, function () {
            deleteRule(db, id, callback);
        })
    },
    deleteAllForUser: function (db, user_email, callback) {
        createTableBefore(db, function () {
            deleteAllForUser(db, user_email, callback);
        })
    }
}

/*
    Export the constant values which are pertinent to this table (Table name and Row names).
*/
module.exports.constants = {
    TABLE_NAME: TABLE_NAME,
    ROW_ID: ROW_ID,
    ROW_RULE: ROW_RULE,
    ROW_CREATOR: ROW_CREATOR,
    ROW_IS_PRIVATE: ROW_IS_PRIVATE
}