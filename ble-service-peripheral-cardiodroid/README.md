# Peripheral simulated with Intel Edison

This repo contains the Node.js program that advertises the BLE service. 

## BLE Service 

|  Name | Requirements  | Properties | Permissions |
|:---------------------------------:|:----------------:|:-------------------:|:------------------------------------------------------------------------------------------------------:|
| Exhaustion Measurement Rate | M | Read / Notify | AuthId Messenger |
| AuthId Messenger | M | Read / Write | Access Rights Process |
| Access Rights Process | M | Read / Write | none |


### Exhaustion Measurement Rate (EMR):

This characteristic is used to send the exhaution state. It can contain the following values:

* LOW – The lowest level of exhaustion;
* MEDIUM – The intermediate level of exhaustion;
* HIGH – The highest level of exhaustion.

This characteristic support _READ_ and _NOTIFY_ operations. 

### AuthId Messenger (AIM):
 
This characteristic sends the information about the identification process of the user against the peripheral. This is used to assure the device that the person that is using the Android app is the same one driving.

### Access Rights Process (ARP):

This characteristic provides to the app the ability of letting the CardioWheel system know that is in the process of identifying a user.
