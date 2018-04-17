SET GROUP="groep31"
SET WIRELESS_DEVICE="Wi-Fi"
SET COMPUTER_NUMBER=3

netsh wlan set profileparameter %GROUP% connectiontype=ibss connectionmode=manual
netsh wlan connect %GROUP%
netsh interface ipv4 set address %WIRELESS_DEVICE% static 192.168.5.%COMPUTER_NUMBER% 255.255.255.0
pause
