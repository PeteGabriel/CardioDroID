systemctl disable bluetoothd
rfkill unblock bluetooth
killall -9 bluetoothd
hciconfig hci0 up