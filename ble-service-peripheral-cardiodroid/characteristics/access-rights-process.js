'use strict';

var bleno = require("bleno");
var util = require('util');


function ARP(writable) {
    bleno.Characteristic.call(this, {
        uuid: 'ec02',
        properties: ['read', 'write', 'notify']
    });
    this._value = new Buffer(0);
    this._updateValueCallback = null;
    this._channel = writable;
}

util.inherits(ARP, bleno.Characteristic); //obligatory

ARP.prototype.onReadRequest = function(offset, callback) {
    console.log('ARP Read Request: value = ' + this._value.toString('utf-8'));
    if (offset) {
        callback(this.RESULT_ATTR_NOT_LONG, null);
    } else {
        //TODO
        var data = this._value;
        callback(this.RESULT_SUCCESS, data);
    }
};

ARP.prototype.onWriteRequest = function(data, offset, withoutResponse, callback) {
    console.log('ARP Write Request: value = ' + this._value.toString('utf-8'));
    this._value = data;
    var client = this._channel();
    client.write("ACCESS_RIGHTS_PROCESS " + this._value);
    client.end();

    callback(this.RESULT_SUCCESS);
};

ARP.prototype.onSubscribe = function(maxValueSize, updateValueCallback) {
    console.log('onSubscribe');
    this._updateValueCallback = updateValueCallback;
};

ARP.prototype.onUnsubscribe = function() {
    console.log('onUnsubscribe');
    this._updateValueCallback = null;
};

module.exports = ARP;
