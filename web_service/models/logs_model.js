'use strict';

// User Model Error.
//
// The caller of any function exported by this module, should know how to
// interpret the ModelError object with the error information pertinent to this Model.
//
// The constructor function for the Error.
const ModelError = require('./errors/model_error');

const users_db = require('../db/users_db');
const logs_db = require('../db/logs_db');

// Interface with the database or Data Access Layer (DAL).
//
const users_ops = users_db.operations;
const logs_ops = logs_db.operations;

/*
{
  user,
  date,
  contexts
}
*/


module.exports = function(db_conn) {

  this.create = function(log, callback){
    if(!validLog(log, callback))
      return;

    // Verify that the creator exists.
    users_ops.get(db_conn, log.user, function(err, user){
      if(err){
        callback(
          new ModelError(2, err.error));
          return;
      }

      if(!user){
        callback(
          new ModelError(4, 'The user specified as the creator does not exist (email = '+log.user+')'));
        return;
      }
      logs_ops.add(db_conn, log.user, log.date, log.contexts, function(err){
        if(err){
          callback(
            new ModelError(3, err.error));
          return;
        }

        callback();
      });
    })
  }

  function validLog(log, callback){
    let errors = '';

    if(!log.user)
      errors += 'Users email was not specified; ';
    else if(!validateEmail(log.user))
      errors += 'Invalid email format; ';
    if(!log.date)
      errors += 'Users name was not specified; ';
    if(!log.contexts)
      errors += 'No OCntexts where supplied; ';

    if(errors.length != 0){
      callback(
        new ModelError(1, errors));
      return false;
    }

    return true;
  }

  // regular expression which describes the syntax of a valid email adress (RFC 2822).
  // RFC 2822 -> https://tools.ietf.org/html/rfc2822#section-3.4.1
  const emailRegex = /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/;

  // Tests the validity of an email address.
  function validateEmail(email){
    return emailRegex.test(email);
  }

  // Getting the rules for a user implies getting all of the users private and shared rules,
  // then getting the all of the rules from the group to which the user belongs, excluding his.
  //
  // callback signature: function(err, rules)
  this.getAll = function(callback) {
    // Get all the users private Rules.
    logs_ops.getAll(db_conn, function(err, logs) {
      if(err){
        callback(
          new ModelError(2, err.error));
          return;
      }

      callback(undefined, {logs: logs});
    })
  }
}