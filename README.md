# Smartthing-nuki
nuki smart lock controller for Smartthings
steps to install the device handler:
1. Copy the code and login into the smartthings console https://graph.api.smartthings.com (EU Site https://graph-eu01-euwest1.api.smartthings.com)
2. At the top of the console click on "My Device Handlers"
3. Click on "Create New Device Handler"
4. Next on the top select "From Code"
5. Paste the code into the page and click on "Create" at the bottom of the page
6. After you get the "Device type published successfully" - click on "Publish" and select "For me"
7. Click on "My Devices" at the top
8. Click on "New Device"
9. In the "Name" fill -> Nuki (or anything else this will be the name of the Device)
10. In the "Device Network Id" fill -> Nuki (or anything else - must be unique).
11. In the "Type" select "Simulated Lock" (this is the device we have created in the steps before)
12. In the "Version" select "Self-Published"
13. In "Location" select your location
14. In "Hub" select the hub you want to control the nuki
15. Click on "Create"
16. DONT close the console open a new page and go to https://web.nuki.io
17. Login to your account click on the setting at the top and select API
18. Click on "Generate API token" 
19. In the "API token name" -> select a name for the token for example NukiSmartThings (can be anything else).
20. ALL the rights should be enabled otherwise it will not work
21. Click on "Generate" and copy the code - Dont close the nuki webpage
22. Go the smartthings console under "My Devices" Click on the Device you have create before
23. Next to "Preferences" click on "edit" in the "API token" paste the token.
24. Go back to nuki web and go to https://web.nuki.io/en/#/admin/smartlocks/
25. Click on the lock you want to control - now the tricky part after click on it - click on the browser web address the url
will look like this https://web.nuki.io/en/#/admin/smartlocks/XXXXXX
where the XXXXXX - is the lock ID copy the number and return to the smartthings console.
26. In the nukiId paste the number.
29. Click on "Save"
30. Open the smartthings app and you are ready to control the Nuki Lock
