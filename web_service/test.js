
const db = require('./db/sqlite3/sqlite3')
  .getDebugDb('cardiodroid_api_db', function(msg){
    console.log('QUERY: '+msg);
  });

const users_model = require('./models/users_model');

const users = new users_model(db);

users.create({email:'test3@email.com', name:'test3'}, function(err, email){
  if(err){
    console.log(err);
    return;
  }

  console.log('SUCCESSFUL CREATION: '+email);
});

users.create({email:'test3@email.com', name:'test3'}, function(err, email){
  if(err){
    console.log(err);
    return;
  }

  console.log('SUCCESSFUL CREATION: '+email);
});
