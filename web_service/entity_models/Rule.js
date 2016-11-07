/* Create an Instance of a Rule.
    'id'(integer) identifies the Rule. Generated internally.
    'jsonRule' (object) the rule in json format.
    'creator' (string) User (email) which created the rule.
    'isPrivate' (boolean) specifies if the Rule can be shared or not.
*/
module.exports = function(id, jsonRule, creator, isPrivate){
    this.id = id;
    this.jsonRule = jsonRule;
    this.creator = creator;
    this.isPrivate = isPrivate;
}