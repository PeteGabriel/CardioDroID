'use strict';

// User Model Error.
//
// The caller of any function exported by this module, should know how to
// interpret the ModelError object with the error information pertinent to this Model.
//
// The constructor function for the Error.
const ModelError = require('./errors/model_error');

// The database Users module.
//
const users_db = require('../db/users_db');
const rules_db = require('../db/rules_db');

module.exports = function(db_conn) {
  // Interface with the database or Data Access Layer (DAL).
  //
  const users_ops = users_db.operations;
  const rules_ops = rules_db.operations;

  this.create = function(rule, callback){
    if(!validRule(rule, callback))
      return;

    // VBerify that the creator exists.
    users_ops.get(db_conn, rule.creator, function(err, user){
      if(err){
        callback(
          new ModelError(2, err.error));
          return;
      }

      if(!user){
        callback(
          new ModelError(4, 'The user specified as the creator does not exist (email = '+rule.creator+')'));
        return;
      }

      rules_ops.add(db_conn, rule.id, rule.creator, rule.jsonRule, rule.isPrivate, function(err, rule_id){
        if(err){
          callback(
            new ModelError(3, err.error));
          return;
        }

        callback(undefined, rule_id);
      })
    })
  }

  function validRule(rule, callback){
    return true;
  }

  // Getting the rules for a user implies getting all of the users private and shared rules,
  // then getting the all of the rules from the group to which the user belongs, excluding his.
  //
  // callback signature: function(err, rules)
  this.getAllForUser = function(email, callback) {
    // Get all the users private Rules.
    rules_ops.getAllForUser(db_conn, email, function(err, user_rules) {
      if(err){
        callback(
          new ModelError(2, err.error));
          return;
      }

      callback(undefined, user_rules);
    })
  }
}