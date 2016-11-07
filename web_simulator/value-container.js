// This module does not support writing to the socket, is is intended to act
// as a storage for the the value sent by the client (of this socket).

module.exports = function(initialValue){

    this.value = initialValue;

    this.setValue = function(newVal){
        this.value = newVal;
        console.log("ValueContainer - value hs been set: "+this.value);
    };
    this.getValue = function(){
        return this.value;
    };
}