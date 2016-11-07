const ModelErrors = require('./model_errors');
// Contructor function for an Error which may occur at the Model level.
//
// The 'error_number' is an error identifier, which is specific to each model and is
// defined by the conncrete Model.
//
// The message is also supplied by the concrete model, being a generic message.
//
// 'extra_info' MAY contain any aditional information about the error whuich occurred.
module.exports = function (error_number, extra_info) {
  this.id = error_number;
  this.message = ModelErrors[error_number];
  this.extra = extra_info;
}