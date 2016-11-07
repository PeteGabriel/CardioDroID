'use strict'
//
// Define the routes pertinent to the Rules.
//

let express = require('express');
let router = express.Router();

const rules_model = require('../models/rules_model');

const model_errors = require('../models/errors/model_errors');

//contains utility objects/methods for the project in general
const http_status = require('../utils').http_status;

module.exports = function (db_conn) {
  // Data Model.
  const rules = new rules_model(db_conn);

  // Deifne routes.

  router.get('/:user_email', getAllRules);
  router.post('/:user_email', createNewRule);

  router.get('/:user_email/:rule_id', getRule);
  router.put('/:user_email/:rule_id', updateRule);
  router.delete('/:user_email/:rule_id', deleteRule);

  return router;

  // Route handling functions.

  // Get all the Rules for the specified User.
  function getAllRules(req, res) {
    let user_email = req.params.user_email;
    console.log('GET All rules for: ' + user_email);

    rules.getAllForUser(user_email, function (err, rules) {
      if (err) {
        // Client side error.
        if (err.id == 1)
          res.status(http_status.bad_request);
        // Server side error.
        else if (err.id == 2)
          res.status(http_status.internal_error);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({ error: (err.extra ? err.extra : err.message) }));
        return;
      }

      res.status(http_status.ok)
        .set('Content-Type', 'application/json')
        // FIXME create view generator for rules.
        .end(JSON.stringify({ rules: rules }));
    })
  }

  // Create a new Rule for the specified User.
  function createNewRule(req, res) {

    let creator = req.params.user_email;

    let rule_to_create = req.body;
    rule_to_create.creator = creator;

    console.log(rule_to_create);

    delete rule_to_create.mRule;

    rules.create(rule_to_create, function (err, rule_id) {
      if (err) {
        if (err.id == 1 || err.id == 4)
          res.status(http_status.bad_request);
        else if (err.id == 2)
          res.status(http_status.internal_error);
        else if (err.id == 3)
          res.status(http_status.conflict);
        else if (err.id == 5)
          res.status(http_status.not_found);

        res.set('Content-Type', 'application/json')
          // FIXME use generic error repsonse creation.
          .end(JSON.stringify({ error: (err.extra ? err.extra : err.message) }));
        return;
      }

      let created_resource_uri = '/api/rules/' + creator + '/' + rule_id;

      res.status(http_status.created)
        .set('Location', created_resource_uri)
        .end(JSON.stringify({ id: rule_id }))
    });
  }

  // Get the Rule specified by 'rule_id'.
  function getRule(req, res) {
    res.status(500).end();
  }

  // Update the Rule specified by 'rule_id'.
  function updateRule(req, res) {
    res.status(500).end();
  }

  // Delete the Rule specified by 'rule_id'.
  function deleteRule(req, res) {
    res.status(500).end();
  }
}