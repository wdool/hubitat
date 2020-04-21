# hubitat
Drivers I've made or modified for my Hubitat Elevation (https://hubitat.com/)

----
- iSmartGate
  Modified for iSmartGate controller from GoGoGate driver by 
  https://github.com/bmergner/bcsmart
  
  Instructions:
    1. Install as new driver in "drivers code" section of Hubitat
    2. Add a new Virtual Driver, selecting "iSmartGate Garage Controller" as the driver - name it to designate the door it will control
    3. Insert the IP address of the iSmartGate (static IP needed), username, password and the garage door this driver will control
      
    Note: Individual drivers are needed for each door connected to the iSmartGate - choose door 1, 2 or 3 when configuring driver.
  
  iSmartGate available via https://ismartgate.com/

----  
- Nue Zigbee Single, Double and Triple Gang Wall Switch Drivers
  Modified to allow Endpoint to be set by preference setting from original driver by 
  https://github.com/GeorgeCastanza/Nue-Zigbee-drivers-for-Hubitat
  
  Zigbee switches available from https://3asmarthome.com/products in Australia
