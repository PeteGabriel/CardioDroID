/* Creates an instance of a User.
    'email' (string) Identifies the User.
    'name' (string) The name of the User.
    'type' (string) The type of User ('normal' or 'admin').
    'group' (integer) OPTIONAL the id of the group to which the user belongs.
*/
module.exports = function(email, name, group){
    this.email = email;
    this.name = name;
    this.user_group = group;
}