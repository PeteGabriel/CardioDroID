var statesAccepted = ["LOW", "MEDIUM", "HIGH"];

module.exports = function(writable){
    return function(state) {
        console.log("socket-speaker::ExhaustionStateHandler - state = "+state);

        if (statesAccepted.indexOf(state) > -1) {
            console.log("Exhaustion state supplied, attempting to write to EMR characteristic: " + state);
            var value = new Buffer(state);
            return writable.writeIntoEMR(value);
        }
        console.log("The state supplied is not specified, not being written!");
    };
};