'use strict'
/*
  Define the routes pertinent to the Groups.
*/

let express = require('express');
let router = express.Router();

const groups_model = require('../models/groups_model');

module.exports = function(db_conn){
  // Data Model.
  const groups = new groups_model(db_conn)

  // Define routes.
  router.post('/', createNewgroup)
  router.get('/', getAllGroups);
  router.post('/:group_name/users/:user_email', addUserToGroup)

  /*
    Route handling functions.
  */

  // Create a new Group.
  function createNewgroup(req, res) {
    console.log('Creating Group: ');
    console.log(req.body);

    groups.create(req.body, function(err, group_name){
      if(err){
        if(err.id == 1)
          res.status(400);
        else if(err.id == 2)
          res.status(500);
        else if(err.id == 3)
          res.status(409);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));
        return;
      }

      res.status(204)   // successful creation, resource not identifiable by URI.
        .end();
    })
  }

  // Get all of the Groups.
  function getAllGroups(req, res) {
    groups.getAll(function(err, groups){
      if(err){
        if(err.id == 2)
          res.status(500);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));
          return;
      }

      res.set('Content-Type', 'application/json')
        .status(200)
        .end(JSON.stringify({groups: groups}))
    })
  }

  // Add a User ('user_email') to the specified group ('group_name').
  function addUserToGroup(req, res) {

    console.log('Adding User to Group');
    console.log(req.params.user_email);
    console.log(req.params.group_name);

    groups.addUserToGroup(req.params.user_email, req.params.group_name, function(err){
      if(err){
        if(err.id == 1)
          res.status(400);
        else if(err.id == 2)
          res.status(500);
        else if(err.id == 3)
          res.status(409);
        else if(err.id == 4)
          res.status(400);
        else if(err.id == 5)
          res.status(404);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));
        return;
      }

      res.status(204)
        .end();
    })
  }

  return router;
}