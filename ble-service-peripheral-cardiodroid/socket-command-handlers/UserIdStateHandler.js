var OK = "OK";
var NOT_OK = "NOT_OK";

module.exports = function(writable) {
    return function(state) {
        console.log("UserIdStateHandler - state = " + state);

        /** ARP = A (Authenticação) */
        //quando se trata de uma resposta OK/NOT_OK
        if (state === OK || state === NOT_OK) {
            console.log("User authentication state supplied, writing to AIM characteristic: " + state);
            return writable.writeIntoAIM(state);
        }

        /** ARP = I (Identificação) Neste caso nao se trata de um estado mas sim do ID*/
        console.log("User ID provided, writing to AIM characteristic: " + state);
        return writable.writeIntoAIM(state);
    }
}