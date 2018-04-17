SET WIRELESS_DEVICE="Wi-Fi"

netsh wlan disconnect
netsh interface ipv4 set address %WIRELESS_DEVICE% dhcp
pause
