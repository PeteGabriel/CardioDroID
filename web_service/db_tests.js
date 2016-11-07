'use strict';

// DB access.
var users_db = require("./db/users_db");
var rules_db = require("./db/rules_db");
var groups_db = require("./db/groups_db");

var sqlite3_supplier = require("./db/sqlite3/sqlite3");

function debugCallback(msg){
  console.log(msg);
}

var db = sqlite3_supplier.getDebugDb('testDB', function(msg){
  debugCallback("QUERY MADE: "+msg);
});


var user_ops = new users_db.operations(db);
var rule_ops = new rules_db.operations(db);
var group_ops = new groups_db.operations(db);

// Entity Models.
var user = require('./entity_models/User');
var rule = require('./entity_models/Rule');
var group = require('./entity_models/Group');

function creationCb(err){
  if(err){
    console.log(err.error);

    if(err.extra)
      console.log(err.extra);

    return;
  }

  console.log('table created successfuly!');
}

// Test the isolation level of sqlite3: SERIALIZABLE??

/*db.serialize(function(){
  db.run('BEGIN');
  db.run(
    'UPDATE Rules SET rule={} WHERE id=17',
    function(err){
      if(err){
        console.log("ERROR "+err);
        return;
      }
      console.log('rule has been updated');

      setTimeout(function(){
        db.run(
          'COMMIT',
          function(err){
            if(err){
              console.log("ERROR "+err);
              return;
            }
            console.log('commited');
          })
      }, 5000);
    });
});

db.serialize(function(){
  db.run('BEGIN');
  db.all(
    'SELECT * FROM Rules '+'COMMIT',
    function(err, rules){
      if(err){
        console.log("ERROR "+err);
        return;
      }

      console.log('obtained RULES');
      console.log(rules);

      db.run('COMMIT');
    });
});*/
// Throws error: SQLITE_ERROR: cannot start a transaction within a transaction at Error (native)


db.serialize(function(){
  //group_ops.createTable(creationCb);

  //console.log('Tables created');

  //user_tests();
  rule_tests();
  //group_tests();
});

function user_tests(){
  console.log();
  console.log('USER TESTS');

  let test_users =
  [
    new user('tiago@email.com', 'Tiago', null),
    new user('joao@email.com', 'Joao', null),
    new user('pedro@email.com', 'Pedro', null)
  ];

  user_ops.dropTable(function(err){
    user_ops.createTable(function(err){

      user_ops.add(
        test_users[0].email,
        test_users[0].name,
        function(err, email){

          user_ops.add(
            test_users[1].email,
            test_users[1].name,
            function(err, email){

              user_ops.add(
                test_users[2].email,
                test_users[2].name,
                function(err, email){

                  user_ops.getAll(function(err, users){
                    console.log(users);

                    user_ops.update(users[0].email, 'Stephan', null, function(err){

                      user_ops.get(users[0].email, function(err, user){
                        console.log(user);

                        group_ops.add('testgroup', function(err, group_id){

                          user_ops.updateGroup(users[0].email, group_id, function(err){

                            user_ops.get(users[0].email, function(err, user){
                              console.log(user);

                              user_ops.getAllForGroup(group_id, function(err, users){
                                console.log(users);

                                user_ops.updateGroup(users[0].email, null, function(err){

                                 user_ops.get(users[0].email, function(err, user){
                                    console.log(user);

                                    group_ops.delete(group_id, function(err){

                                      user_ops.delete(users[0].email, function(err){

                                        user_ops.getAll(function(err, users){
                                          console.log(users);

                                          user_ops.delete(users[0].email, function(){
                                            if(err)
                                              console.log(err.error);
                                            else
                                              console.log('Successfuly removed User: '+users[0].email)
                                            user_ops.delete(users[1].email, function(){
                                                  if(err)
                                                    console.log(err.error);
                                                  else
                                                    console.log('Successfuly removed User: '+users[0].email)
                                              user_ops.getAll(function(err, users){
                                                console.log(users);
                                              })
                                            })
                                          })
                                        })
                                      })
                                    })
                                  })
                                })
                              });
                            });
                          });
                        })
                      });
                    })
                  })
                });
            });
        });
    })
  })

}

function rule_tests(){
  console.log();
  console.log('RULE TESTS');

  var test_user = 'email2';
  var test_rule = {someField:''};

  rule_ops.dropTable(function(err){
    rule_ops.createTable(function(){

      rule_ops.deleteAllForUser(test_user, function(){
        rule_ops.add(test_rule, test_user, false, function(err, id){
          if(err){
            console.log("ERROR "+err.error);
            console.log(err.extra);
            return;
          }
          console.log("Rule added: id = "+id);

          rule_ops.add(test_rule, test_user, false, function(err, id){
            if(err){
              console.log("ERROR "+err.error);
              console.log(err.extra);
              return;
            }
            console.log("Rule added: id = "+id);

            rule_ops.add(test_rule, test_user, true, function(err, id){
              if(err){
                console.log("ERROR "+err.error);
                console.log(err.extra);
                return;
              }
              console.log("Rule added: id = "+id);

              rule_ops.add(test_rule, test_user, true, function(err, id){
                if(err){
                  console.log("ERROR "+err.error);
                  console.log(err.extra);
                  return;
                }
                console.log("Rule added: id = "+id);

                rule_ops.getAll(function(err, rules){
                  if(err){
                    console.log("ERROR "+err.error);
                    console.log(err.extra);
                    return;
                  }
                  console.log("ALL THE RULES");
                  console.log(rules);

                  rule_ops.getAllForUser(test_user, function(err, rules){
                    if(err){
                      console.log("ERROR "+err.error);
                      console.log(err.extra);
                      return;
                    }
                    console.log("ALL THE RULES");
                    console.log(rules);

                    rule_ops.getAllSharedForUser(test_user, function(err, rules){
                      if(err){
                        console.log("ERROR "+err.error);
                        console.log(err.extra);
                        return;
                      }

                      console.log(rules);

                      var test_id = rules[0][rules_db.constants.ROW_ID];

                      rule_ops.updatePrivacy(test_id, true, function(err){
                        if(err){
                          console.log("ERROR "+err.error);
                          console.log(err.extra);
                          return;
                        }

                        rule_ops.get(test_id, function(err, rule){
                          if(err){
                            console.log("ERROR "+err.error);
                            console.log(err.extra);
                            return;
                          }
                          console.log(rule);

                          rule_ops.updateRule(test_id, {}, function(err){
                            if(err){
                              console.log("ERROR "+err.error);
                              console.log(err.extra);
                              return;
                            }

                            rule_ops.get(test_id, function(err, rule){
                              if(err){
                                console.log("ERROR "+err.error);
                                console.log(err.extra);
                                return;
                              }
                              console.log(rule);

                              rule_ops.delete(test_id, function(err){
                                if(err){
                                  console.log("ERROR "+err.error);
                                  console.log(err.extra);
                                  return;
                                }

                                rule_ops.get(test_id, function(err, rule){
                                  // this rule was just deleted so the error is expected.
                                  console.log("ERROR "+err.error);
                                })

                              })
                            });
                          });
                        });
                      });
                    });
                  });
                });
              });
            });
          });
        });
      });
    });
  });

}

function group_tests(){
  console.log();
  console.log('GROUP TESTS');

  group_ops.dropTable(function(err){
    group_ops.createTable(function(err){
      group_ops.add('testGroup1', function(err, id){
        if(err){
          console.log("ERROR "+err.error);
          console.log(err.extra);
          return;
        }

        console.log('ID = '+id);

        group_ops.get(id, function(err, group){
          if(err){
            console.log("ERROR "+err.error);
            return;
          }

          console.log(group);

          group_ops.add('testGroup2', function(err, id){
            if(err){
              console.log("ERROR "+err.error);
              return;
            }

            console.log('ID = '+id);

            group_ops.getAll(function(err, groups){
              if(err){
                console.log("ERROR "+err.error);
                return;
              }

              console.log(groups);

              for(var i = 0; i < groups.length; ++i){
                group_ops.delete(groups[i][groups_db.constants.ROW_ID], function(err){
                  if(err){
                    console.log(err.error);
                    console.log(err.extra);
                    return;
                  }
                })
              }

              group_ops.getAll(function(err, groups){
                console.log(groups);
              })
            })
          })
        })
      })
    });
  });
}