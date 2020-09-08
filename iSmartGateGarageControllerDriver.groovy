/**
 *
 *  File: iSmartGateGarageControllerDriver.groovy
 *  Platform: Hubitat
 *
 *
 *  Requirements:
 *     1) iSmartGate Garage Controller connected to same LAN as your Hubitat Hub.  Use router
 *        DHCP Reservation to prevent IP address from changing.
 *     2) Authentication Credentials for iSmartGate Garage Door Open.  This is the credentials 
 *        that are used to log on to the opener at it's index.php
 *
 *  Original Copyright 2019 Robert B. Mergner
 *      https://github.com/bmergner/bcsmart
 *
 *  iSmartGate variant by scrytch available at:
 *      https://github.com/scrytch/hubitat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2019-03-11  Bob Mergner  Original Creation
 *    2019-03-13  Bob Mergner  Cleaned up and refactored.  Added every two second polling for more reactive automation chains in rules
 *    2019-03-14  Bob Mergner  More cleanup
 *    2019-06-29  Shane Lord   Adjusted GoGoGate driver to work with the new iSmartGate and iSmartGate Pro
 *    2019-07-16  Shane Lord   Added capability to use door open/close sensor as Hubitat Contact Sensor
 *    2020-04-21  bofisher     Added code to split door number from door open-close status  
 *
 *    GoGoGate and iSmartGate are trademarks and/or copyrights of REMSOL EUROPE S.L. and its affiliates
 *    	 https://ismartgate.com
 */

def version() {"v0.1.20200421"}

import hubitat.helper.InterfaceUtils

metadata {
    definition (name: "iSmartGate Garage Controller", namespace: "bcsmart", author: "Bob Mergner") {
        capability "Initialize"
        capability "Refresh"
	capability "DoorControl"
	capability "Contact Sensor"		
	attribute "door", "string"
    }
}

preferences {
    input("ip", "text", title: "IP Address", description: "[IP Address of your iSmartGate Device]", required: true)
	input("user", "text", title: "iSmartGate Garage User", description: "[iSmartGate Username (usually admin)]", required: true)
	input("pass", "password", title: "iSmartGate Garage Password", description: "[Your iSmartGate user's Password]", required: true)
	input("door", "text", title: "Garage Door Number", description: "[Enter 1, 2 or 3]", required: true)
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
}



def logsOff(){
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def refresh() {
    //log.info "refresh() called"
	//initialize()
	GetDoorStatus()
}

def doorPollStatus() {
	 for(int i = 0;i<30;i++) {
         refresh()
		 pauseExecution(2000)
      }
}

def installed() {
    log.info "installed() called"
    updated()
}

def updated() {
    log.info "updated() called"
    //Unschedule any existing schedules
    unschedule()
    
    //Create a 30 minute timer for debug logging
    if (logEnable) runIn(1800,logsOff)
    
    currentState = "99"
	runEvery1Minute(doorPollStatus)
    refresh()
}

def LogOnAndGetCookie(){
	def allcookie
	def cookie
	
	httpPost("http://${ip}", "login=${user}&pass=${pass}&send-login=Sign+In") { resp ->
		allcookie = resp.headers['Set-Cookie']
		//log.debug "SetCookieValue: ${allcookie}"
		
		cookie = allcookie.toString().replaceAll("; path=/","").replaceAll("Set-Cookie: ","")
    }
	
	return cookie
	
}

def GetDoorStatus() {
	if (currentState == null) {
		currentState = "99"
	}
	//def doorStatus
	def cookie = LogOnAndGetCookie()
	
	def params = [uri: "http://${ip}/isg/statusDoorAll.php?status1=10",
				  headers: ["Cookie": """${cookie}""",
							"Referer": "http://${ip}/index.php",
							"Host": """${ip}""",
                            "Connection": "keep-alive"],
                  requestContentType: "application/json; charset=UTF-8"]
	
       httpGet(params) { resp ->
		 	doorStatus = resp.data.toString().substring(1)
		   	doorStatus = doorStatus.substring(0, doorStatus.length() - 1)
		   	int whichdoor = Integer.parseInt(door,16) - 1
		   	status = doorStatus.split(",")[whichdoor]
		   	status = status.split(":")[1].replaceAll("\"", "")
		   	doorStatus = status
		   
			if ( status.contains("0") && !currentState.contains("0") ) {
			   	sendEvent(name: "door", value: "closed")
				currentState = "0"
		   	}
		   	else if ( status.contains("1") && !currentState.contains("1") ) 
		   	{
			   	sendEvent(name: "door", value: "open")
				currentState = "1"
		   	}
	   	}
	
		return cookie
}

def open() {
	log.info "Door ${door} received open command from Hubitat Elevation"

	def cookie = GetDoorStatus()
	
	//now see if door is open already
	if ( doorStatus.contains("0") )
		{
			toggleDoor(cookie)
			doorStatus = "1"
			log.info "Open command sent to Door ${door}"
	   	}
	else
		{
			log.info "Door ${door} already open"
		}
}

def close() {
	log.info "Door ${door} received Close command from Hubitat Elevation"
	
	def cookie = GetDoorStatus()
	
	//now see if door is closed already
	if ( doorStatus.contains("1") )
		{
			toggleDoor(cookie)
	 		doorStatus = "0"
			log.info "Close command sent to Door ${door}"
	   	}
	else
		{
			log.info "Door ${door} already closed"
		}
}

def toggleDoor(cookie){
	def params = [uri: "http://${ip}/isg/opendoor.php?numdoor=${door}&status=0&login=${user}",
		headers: ["Cookie": """${cookie}""",
				  "Referer": "http://${ip}/index.php",
				  "Host": """${ip}""",
                  "Connection": "keep-alive"],
                  requestContentType: "application/json; charset=UTF-8"]
	
      	httpGet(params) { resp ->
            //log.debug resp.contentType
            //log.debug resp.status
			//log.debug resp.data
		}
}
	
def initialize() {
    state.version = version()
	currentState = "99"
	runEvery1Minute(doorPollStatus)
    log.info "initialize() called"
    
    if (!ip || !user || !pass || !door) {
        log.warn "iSmartGate Garage Door Controller required fields not completed.  Please complete for proper operation."
        return
    }
	
    refresh()
}

