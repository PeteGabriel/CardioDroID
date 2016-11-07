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

// Interface with the database or Data Access Layer (DAL).
//
const users_ops = users_db.operations;

// Constructor function which creates an object with the methods to interface with the User Data model.
module.exports = function(db){

  this.get = function(user_email, callback){
    users_ops.get(db, user_email, function(err, user){
      if(err){
        callback(
          // The error supplied by contains an error message (error) and the original error (extra).
          new ModelError(2, err.error));
        return;
      }

      if(!user){
        callback(
          new ModelError(5, 'The email provided does not correspond to a valid User: '+user_email));
        return;
      }

      callback(undefined, user);
    })
  }

  // Create a new User
  //
  // 'user' is expected to be a json object containing the properties which represent the User Entity.
  //
  // callback signature: function(err, created_user_email)
  this.create = function(user, callback){

    // Check if the User object is well formed.
    if(!validateUser(user, callback))
      return;

    // Check if the User already exists.
    users_ops.get(db, user.email, function(err, gotten_user){
      if(err){
        callback(
          // The error supplied by contains an error message (error) and the original error (extra).
          new ModelError(2, err.error));
        return;
      }

      if(gotten_user){
        callback(
          new ModelError(3, 'The user (email = '+user.email+') already exists.'));
        return;
      }

      // Create the User.
      users_ops.add(db, user.email, user.name, function(err, email){
        if(err){
          callback(
            new ModelError(2, 'An error occurred while trying to create the User: '+user.email));
          return;
        }

        // The creation was successful, so we call the callback giving it the
        // email of the created user.
        callback(undefined, email);
      })
    })
  }

  // Check if the users parameters are valid, passing an error onto the callback and
  // returning 'false' to the caller if they are not.
  function validateUser(user, callback){
    let errors = '';

    if(!user.email)
      errors += 'Users email was not specified; ';
    else if(!validateEmail(user.email))
      errors += 'Invalid email format; ';
    if(!user.name)
      errors += 'Users name was not specified; ';

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
}