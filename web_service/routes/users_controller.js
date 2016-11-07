'use strict'
/*
  Define the routes pertinent to a User.
*/

const express = require('express');
const router = express.Router();

const users_model = require('../models/users_model');

const model_errors = require('../models/errors/model_errors');

module.exports = function(db){
  // Data Model.
  const users = new users_model(db);

  // Define routes.
  router.post('/', createNewUser);
  router.get('/:user_email/group', getUsersGroup);

  /*
    Route handling functions.
  */

  // Create a new User.
  function createNewUser(req, res) {
    console.log('Creating User: ');
    console.log(req.body);

    users.create(req.body, function userCreated(err, email) {
      if(err){
        // Client side error: the request body mas malformed.
        if(err.id == 1)
          res.status(400);
          // Server side error.
        else if(err.id == 2)
          res.status(500);
        // The User we are trying to create already exists.
        else if(err.id == 3)
          res.status(409);    // Conflict

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));

        return;
      }

      // User created successfully -> respond with 204 (No Content, resource is not identifiable by a URI).
      res.status(204).end();
    });
  }

  function getUsersGroup(req, res){
    let email = req.params.user_email;
    console.log('GET group for user: '+email);

    users.get(email, function(err, user){
      if(err){
        if(err.id == 2)
          res.status(500);
        else if(err.id == 5)
          res.status(404);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));
        return;
      }

      res.status(200)
        .end(JSON.stringify({name:(user.user_group? user.user_group : null)}));
    })
  }

  return router;
}