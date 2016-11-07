'use strict'
//
// Define the routes pertinent to the Rules.
//

let express = require('express');
let router = express.Router();

const logs_model = require('../models/logs_model');

const model_errors = require('../models/errors/model_errors');

//contains utility objects/methods for the project in general
const http_status = require('../utils').http_status;

module.exports = function(db_conn){
  // Data Model.
  const logs = new logs_model(db_conn);

  // Deifne routes.

  router.get('/', getAllLogs);
  router.post('/', createNewLog);

  return router;

  // Route handling functions.

  // Get all of the Logs.
  function getAllLogs(req, res){
    logs.getAll(function(err, logs){
      if(err){
        // Client side error: the request body mas malformed.
        if(err.id == 1)
          res.status(400);
          // Server side error.
        else if(err.id == 2)
          res.status(500);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));

        return;
      }

      res.set('Content-Type', 'application/json')
        .status(200)
        .end(JSON.stringify(logs));
    })
  }

  // Create a new Log.
  function createNewLog(req, res){
    logs.create(req.body, function(err){
      console.log("inserted log");
      if(err){
        // Client side error: the request body mas malformed.
        if(err.id == 1){
          res.status(400);
        }else if(err.id == 2){ // Server side error.
          res.status(500);
        }else{
          res.status(400);
        }

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({error: (err.extra?err.extra:err.message)}));

        return;
      }

      res.status(204)
        .end();
    })
  }
}