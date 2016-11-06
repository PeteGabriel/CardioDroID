'use strict';

/*
    Serviço criado para ser publicitado pelo periférico sobre BLE.

    Este serviço contém 3 caracteristicas:
        Access Rights Process (ARP)
        AuthId Messenger (AIM)
        Exhaustion Measurement Rate (EMR)
*/

var bleno = require('bleno');
var net = require('net');
var PrimaryService = bleno.PrimaryService;


/*TODO doc*/


var AuthIdMessenger = require('../characteristics/auth-id-messenger');
var AccessRightsProcess = require('../characteristics/access-rights-process');
var ExhaustionMeasurementRate = require('../characteristics/exhaustion-measurement-rate');

//User Identification Characteristic
var AIM;
//Exhaustion Characteristic
var EMR;
var ARP;

/**
 * Inicia a publicidade de pacotes do serviço criado.
 *
 * O parametro 'writable' é uma função que quando invocada devolve
 * uma instância de um porto para onde podem ser realizadas escritas
 * (envio de dados). Possibilita o transporte de informação entre módulos,
 * efectuado assim os protocolos de comunicação.
 */
function startService(connect) {
    var serviceUuids = ['ec00'];
    var serviceName = 'CardioPsService';

    EMR = new ExhaustionMeasurementRate();
    ARP = new AccessRightsProcess(connect);
    AIM = new AuthIdMessenger(connect);

    // Notify the console that we've accepted a connection
    bleno.on('accept', function(clientAddress) {
        console.log("Accepted connection from address: " + clientAddress);
    });

    // Notify the console that we have disconnected from a client
    bleno.on('disconnect', function(clientAddress) {
        console.log("Disconnected from address: " + clientAddress);
    });

    bleno.on('stateChange', function(state) {
        console.log('on -> stateChange: ' + state);

        if (state === 'poweredOn') {
            bleno.startAdvertising(serviceName, serviceUuids);
        } else {
            bleno.stopAdvertising();
        }
    });

    bleno.on('advertisingStart', function(error) {
        console.log('on -> advertisingStart: ' + (error ? 'error ' + error : 'success'));

        if (!error) {
            bleno.setServices([
                new PrimaryService({
                    uuid: serviceUuids[0],
                    characteristics: [EMR, AIM, ARP]
                })
            ]);
        }
    });
}



/**
 * Exportação de um conjunto de funções que sabem
 * como modificar as caracteristicas.
 */
var modifyCharacteristics = {
    writeIntoEMR: function(value) {
      console.log("Current AIM value: " + AIM._value);
      if (AIM._value != "OK"){
        return;
      }
      console.log("Sending new state");
      EMR._value = new Buffer(value);

        if (EMR._updateValueCallback) {
            console.log("New EMR value: " + EMR._value);
            EMR._updateValueCallback(EMR._value);
        }
    },
    writeIntoAIM: function(value) {
        AIM._value = new Buffer(value);
	console.log("New AIM value: " + AIM._value);
        if (AIM._updateValueCallback) {
            AIM._updateValueCallback(AIM._value);
        }
    },
    writeIntoARP: function(value) {
        ARP._value = new Buffer(value);
        if (ARP._updateValueCallback) {
            console.log("New ARP value: " + ARP._value);
            ARP._updateValueCallback(ARP._value);
        }
    }
};


module.exports = {
    modifyCharacteristics: modifyCharacteristics,
    startService: startService
};
