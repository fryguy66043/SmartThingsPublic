/**
 *  Eat My Pi
 *
 *
 *
 *  Copyright 2018 Jeffrey Fry
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
 */
 
metadata {
	definition (name: "EatMyPi", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"

        command "refresh"
        command "imageServiceOff"
        command "imageServiceOn"
	}

	simulator {
		
	}

	tiles(scale: 2) {
        standardTile("state", "device.state", decoration: "flat", width: 2, height: 2) {
        	state "ok", label: 'OK', icon: "st.Entertainment.entertainment1", backgroundColor:"#00A0DC"
            state "noImageService", label: 'Image Svc', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "noImageLoop", label: 'Loop', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "lowDiskSpace", label: 'Low Disk', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "unavailable", label: 'Unavail', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "error", label: '${name}', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
        }
        valueTile("status", "device.status", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}' 
        }
        valueTile("substatus", "device.substatus", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}'
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("imageService", "device.imageService", decoration: "flat", width: 2, height: 2) {
        	state "on", label: 'Img Svc', action: "imageServiceOff", icon: "st.switches.switch.on", backgroundColor:"#00A0DC"
            state "off", label: 'Img Svc', action: "imageServiceOn", icon: "st.switches.switch.off", backgroundColor:"#ffffff"
        }

		main "state"
		details(["state", "status", "substatus", "refresh", "imageService"])
	}
}

def refresh() {
	log.debug "switch: request refresh()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "status", value: "${timestamp}\nGetting Pi Status...")
    
    getStatus() 
}

def getStatus() {
	log.debug "getStatus"
	state.getStatus = false
	sendHubCommand(new physicalgraph.device.HubAction("""GET /getstatus HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: getStatusHandler]))
    runIn(10, checkGetStatus)
}

def checkGetStatus() {
	log.debug "checkGetStatus"
    if (!state.getStatus) {
    	log.debug "getStatus Call Timed Out..."
 		sendEvent(name: "status", value: "Get Status call timed out.")
        sendEvent(name: "state", value: "unavailable")
        sendEvent(name: "substatus", value: "")
    }
    else {
    	log.debug "getStatus Call succeeded!"
    }
}

def getStatusHandler(hubResponse){
	log.debug "callbackHandler"
    state.getStatus = true
    
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Pi Server Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "status", value: msg)
	log.debug msg
    
    def sVal = ""
    def temp = ""
    def tSize = 0.0
    for (int i = 0; i < hServerMsg.size(); i++) {
    	log.debug "hServerMsg[i] = ${hServerMsg[i]}"
    	switch (i) {
        	case 0:
            	if (hServerMsg[i].contains("Server") && sVal != "error") {
                	temp = hServerMsg[i].replace("Server=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                    }
                    else if (temp == "Not Running") {
                    	sVal = "unavailable"
                    }
                    else {
                    	sVal = "error"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
            case 1:
            	if (hServerMsg[i].contains("Image Service") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Service=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                        sendEvent(name: "imageService", value: "on")
                        log.debug "imageService = on"
                    }
                    else if(temp == "Not Running" || temp == "Stopped") {
                    	sVal = "noImageService"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "imageService", value: "off")
                    log.debug "imageService = off"
                }
            	break
			case 2:
            	if (hServerMsg[i].contains("Image Capture Loop") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Capture Loop=", "")
                    if (temp == "Not Ready") { 
                    	if (sVal == "ok") { // This is only important if the Image Service is running
                    		sVal = "noImageLoop"
                        }
                    }
                    else if (temp.contains("Minute")) {
                    	sVal = "ok"
                    }
                    else {
                    	sVal = "error"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
			case 3:
            	if (hServerMsg[i].contains("Avail Disk Space") && sVal != "error") {
                	temp = hServerMsg[i].replace(" GB Avail Disk Space", "")
                    tSize = Float.parseFloat(temp)
                    if (tSize < 0.1) {
                    	sVal = "lowDiskSpace"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
            default:
            	sVal = "error"
            	break
        }
    }
    log.debug "sVal = ${sVal}"
    sendEvent(name: "state", value: sVal)
}

def imageServiceOff() {
	log.debug "imageServiceOff"
	state.imageService = false
    sendEvent(name: "substatus", value: "Turning off Image Service...")
	sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureterminate HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: imageServiceHandler]))
    runIn(10, checkImageService)
}

def imageServiceOn() {
	log.debug "imageServiceOn"
	state.imageService = false
    sendEvent(name: "substatus", value: "Turning on Image Service...")
	sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureinitialize HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: imageServiceHandler]))
    runIn(10, checkImageService)
}

def imageServiceHandler(hubResponse) {
	log.debug "imageServiceHandler"
    state.imageService = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Pi Server Call (${hubResponse.status}: ${hubResponse.body})"
    sendEvent(name: "substatus", value: msg)
    runIn(5, refresh)
}

def checkImageService() {
	log.debug "checkImageService"
    if (!state.imageService) {
    	log.debug "Image Capture call timed out..."
        sendEvent(name: "status", value: "Image Capture call timed out...")
        sendEvent(name: "state", value: "unavailable")
        sendEvent(name: "imageService", value: "off")
        log.debug "imageService = off"
    }
    else {
    	log.debug "Image Capture call succeeded!"
    }
}

def parse(String description) {
	log.trace "parse($description)"
    
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
	reset()
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	refresh()
}

