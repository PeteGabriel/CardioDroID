'use strict';

var bleno = require("bleno");
var util = require('util');

function EMR() {
    bleno.Characteristic.call(this, {
        uuid: 'ec01',
        properties: ['read', 'notify']
    });

    this._value = new Buffer(0);
    this._updateValueCallback = null;
}

util.inherits(EMR, bleno.Characteristic); //obligatory

EMR.prototype.onReadRequest = function(offset, callback) {
    console.log('EMR Read Request: value = ' + this._value.toString('utf-8'));

    if (offset) {
        callback(this.RESULT_ATTR_NOT_LONG, null);
    } else {
        //TODO
        var data = this._value;
        callback(this.RESULT_SUCCESS, data);
    }
};

EMR.prototype.onSubscribe = function(maxValueSize, updateValueCallback) {
    console.log('onSubscribe');
    this._updateValueCallback = updateValueCallback;
};

EMR.prototype.onUnsubscribe = function() {
    console.log('onUnsubscribe');
    this._updateValueCallback = null;
};

module.exports = EMR;