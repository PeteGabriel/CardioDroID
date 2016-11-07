var db = require('../storage/DriverStorage').actions;
var connect = require('../coms/connect-to-socket');

var debug = function(msg){console.log(msg)};

var REGISTER = 'R';

module.exports = function( arp_container){
    return function(state){
        if(state == REGISTER){
            db.addDriver(function (err, driver) {
                if(err){
                    // ...
                }

                debug("Driver has been created: "+driver.user_id);
                connect.connect("USER_ID_STATE "+driver.user_id);
            });
        } else {
            arp_container.setValue(state);
        }
    }
}
