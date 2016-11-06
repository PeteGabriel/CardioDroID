'use strict';

var bleno = require("bleno");
var util = require('util');


function AIM(writable) {
    bleno.Characteristic.call(this, {
        uuid: 'ec03',
        properties: ['read', 'write', 'notify']
    });
    this._value = new Buffer(0);
    this._updateValueCallback = null;
    this._channel = writable;
}

util.inherits(AIM, bleno.Characteristic); //obligatory

AIM.prototype.onReadRequest = function(offset, callback) {
    console.log('AIM Read Request: value = ' + this._value.toString('utf-8'));

    if (offset) {
        callback(this.RESULT_ATTR_NOT_LONG, null);
    } else {
        var data = this._value;
        callback(this.RESULT_SUCCESS, data);
    }
};

AIM.prototype.onWriteRequest = function(data, offset, withoutResponse, callback) {
    console.log('AIM Write Request: value = ' + this._value.toString('utf-8'));
    this._value = data;

    var client = this._channel();
    client.write("USER_ID_STATE " + this._value);
    client.end();

    callback(this.RESULT_SUCCESS);
};

AIM.prototype.onSubscribe = function(maxValueSize, updateValueCallback) {
    console.log('onSubscribe');
    this._updateValueCallback = updateValueCallback;
};

AIM.prototype.onUnsubscribe = function() {
    console.log('onUnsubscribe');
    this._updateValueCallback = null;
};

module.exports = AIM;
