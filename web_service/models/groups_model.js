'use strict';

// User Model Error.
//
// The caller of any function exported by this module, should know how to
// interpret the ModelError object with the error information pertinent to this Model.
//
// The constructor function for the Error.
const ModelError = require('./errors/model_error');

// The database module.
//
const groups_db = require('../db/groups_db');
const users_db = require('../db/users_db');

// Interface with the database or Data Access Layer (DAL).
//
const group_ops = groups_db.operations;
const user_ops = users_db.operations;

// Constructor function which creates an object with the methods to interface with the User Data model.
module.exports = function(db){

  // Create a new Group
  //
  // 'user' is expected to be a json object containing the properties which represent the User Entity.
  //
  // callback signature: function(err, created_user_email)
  this.create = function(group, callback){
    if(!validateGroup(group, callback)){
      return;
    }

    group_ops.get(db, group.name, function(err, gotten_group){
      if(err){
        callback(
          new ModelError(2, 'An error occurred while trying to create the group: '+group.name));
        return;
      }

      // Indicate that the group already exists.
      if(gotten_group){
        callback(
          new ModelError(3, 'The group specified already exists: '+group.name));
        return;
      }

      // Create the new group.
      group_ops.add(db, group.name, function(err, name){
        if(err){
          console.log('an error occurred adding the group!');
          console.log(err);
          callback(
            new ModelError(2, 'An error occurred while trying to create the group: '+group.name));
          return;
        }
        callback(undefined, name);
      });
    });
  }

  function validateGroup(group, callback){
    if(!group.name){
      callback(
        new ModelError(1, 'The name of the group must be supplied.'));
      return false;
    }

    return true;
  }

  this.getAll = function(callback){
    group_ops.getAll(db, function(err, groups){
      if(err){
        callback(
          new ModelError(2, 'An error occurred when trying to get all the Groups.'))
      }

      callback(undefined, groups);
    });
  }

  this.addUserToGroup = function(user_email, group_name, callback){
    group_ops.get(db, group_name, function(err, group){
      if(err){
        callback(
          new ModelError(2, 'An error occurred while tryignto update the Users ( '+user_email+' ) Group ('+group_name+')'));
        return;
      }

      if(!group){
        callback(
          new ModelError(5, "The group ("+group_name+") does not exist"));
        return;
      }

      user_ops.get(db, user_email, function(err, user){
        if(err){
          callback(
            new ModelError(2, 'An error occurred while tryignto update the Users ( '+user_email+' ) Group ('+group_name+')'));
          return;
        }

        if(!user){
          callback(
            new ModelError(5, "The User ("+user_email+") does not exist"));
          return;
        }

        user_ops.updateGroup(db, user_email, group_name, function(err){
          if(err){
            console.log(err);
            callback(
              new ModelError(2, 'An error occurred while tryignto update the Users ( '+user_email+' ) Group ('+group_name+')'));
            return;
          }
          // Users Group has been updated.
          callback();
        });
      });
    })
  }
}