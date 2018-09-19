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
		capability "Image Capture"
        capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"

        command "refresh"
        command "imageServiceOff"
        command "imageServiceOn"
        command "callEmailCPU"
        command "callEmailPic"
		command "setSafetyControl"

		command "loadPic"
	}

	simulator {
		
	}

	tiles(scale: 2) {
        standardTile("state", "device.state", decoration: "flat", width: 2, height: 2) {
        	state "ok", label: 'OK', icon: "st.Entertainment.entertainment1", backgroundColor:"#00A0DC"
            state "noImageService", label: 'Image Svc', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "noImageLoop", label: 'Loop', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "lowDiskSpace", label: 'Low Disk', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "highCPUTemp", label: 'CPU Temp', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "unavailable", label: 'Unavail', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "error", label: '${name}', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
        }
        valueTile("status", "device.status", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}' 
        }
        valueTile("substatus", "device.substatus", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}'
        }
        valueTile("diskSpace", "device.diskSpace", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nGB', defaultState: true, backgroundColors: [
            	[value: 1, color: "#bc2323"],
            	[value: 10, color: "#44b621"]
        	]
        }
        valueTile("cpuTemp", "device.cpuTemp", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nCPU', defaultState: true, backgroundColors: [
            	[value: 130, color: "#44b621"],
                [value: 200, color: "#bc2323"]
            ]
        }
        valueTile("nbrPics", "device.nbrPics", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nPics', defaultState: true, backgroundColors: [
            	[value: 500, color: "#44b621"],
                [value: 2000, color: "#bc2323"]
            ]
        }
        standardTile("emailPic", "device.emailPic", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office19", backgroundColor:"#bc2323"
        	state "off", label: 'OFF', icon: "st.Office.office19", backgroundColor:"#ffffff"
            state "on", label: 'PIC', action: "callEmailPic", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "sending"
            state "sending", label: 'Sending', icon: "st.Office.office19", backgroundColor:"#e86d13", nextState: "sent"
            state "sent", label: 'PIC', action: "callEmailPic", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "on"
        }
        standardTile("emailCPU", "device.emailCPU", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office19", backgroundColor:"#bc2323"
        	state "off", label: '${name}', icon: "st.Office.office19", backgroundColor:"#ffffff"
            state "on", label: 'CPU', action: "callEmailCPU", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "sending"
            state "sending", label: 'Sending', icon: "st.Office.office19", backgroundColor:"#e86d13", nextState: "sent"
            state "sent", label: 'CPU', action: "callEmailCPU", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "on"
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
//			state "default", label: '', action: "loadPic", icon:"st.secondary.refresh"
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("imageService", "device.imageService", decoration: "flat", width: 2, height: 2) {
        	state "on", label: 'Img Svc', action: "imageServiceOff", icon: "st.switches.switch.on", backgroundColor:"#00A0DC", nextState: "turningOff"
            state "turningOff", label: 'Turning off', icon: "st.switches.switch.off", backgroundColor:"#ffffff", nextState: "off"
            state "off", label: 'Img Svc', action: "imageServiceOn", icon: "st.switches.switch.off", backgroundColor:"#ffffff", nextState: "turningOn"
            state "turningOn", label: 'Turning on', icon: "st.switches.switch.on", backgroundColor:"#00A0DC", nextState: "on"
        }
		controlTile("safetyControl", "device.safetyControl", "slider", height: 2, width: 2, inactiveLabel: false, range: "(0..10)") {
        	state "level", action: "setSafetyControl"
        }

		standardTile("image", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: true) {
            state "default", label: "", action: "", icon: "st.camera.dropcam-centered", backgroundColor: "#FFFFFF"
        }

        carouselTile("cameraDetails", "device.image", width: 3, height: 2) { }

        standardTile("take", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
            state "taking", label:'Taking', action: "", icon: "st.camera.dropcam", backgroundColor: "#00A0DC"
            state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
        }

		main "state"
		details(["state", "diskSpace", "cpuTemp", "nbrPics", "emailCPU", "emailPic", "status", "substatus", "refresh", "safetyControl", "imageService"])
	}
}

def setSafetyControl(val) {
	log.debug "setSafetyControl"
    if (val) {
    	sendEvent(name: "safetyControl", value: val)
    }
}


/*
def take() {
	log.debug "take()"
//	sendHubCommand(new physicalgraph.device.HubAction("""GET /load_pic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: loadPicHandler, outputMsgToS3: true]))    
	sendHubCommand(new physicalgraph.device.HubAction("""GET /load_pic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: loadPicHandler]))    
}
*/


def take() {
	log.debug "take(orig)"
//    def host = getHostAddress()
    def host = "192.168.1.128:5000"
    def port = host.split(":")[1]

    def path = "/load_pic"

    def hubAction = new physicalgraph.device.HubAction(
        method: "GET",
        path: path,
        headers: [HOST:host]
    )

    hubAction.options = [outputMsgToS3:true]

//	def hubResponse = hubAction
//    log.debug "hubResponse = ${hubResponse}"
//    return hubResponse
	return hubAction
}


/**
* Utility method to get the host addresses
*/
private getHostAddress() {
	log.debug "getHostAddress: deviceNetworkId = ${device.deviceNetworkId}"
    def parts = device.deviceNetworkId.split(":")
    def ip = convertHexToIP(parts[0])
    def port = convertHexToInt(parts[1])
    log.debug "${ip}:${port}"
    return ip + ":" + port
}




def loadPic() {
	log.debug "loadPic"
	sendHubCommand(new physicalgraph.device.HubAction("""GET /load_pic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: loadPicHandler]))
    
}

def loadPicHandler(hubResponse) {
	log.debug "loadPicHandler(${hubResponse})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body
    def hData = hubResponse.data
    log.debug "hStatus = ${hStatus}: hBody = ${hBody}: hData = ${hData}"
}

def savePic(imgName, img) {
	log.debug "loadPic"
    
    
    
    
    try {
    	storeImage(imgName, img)
    }
    catch (e) {
    	log.error "Error storing ${imgName}: ${e}"
    }
}

def callEmailPic() {
	log.debug "callEmailPic"
    state.emailPic = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "substatus", value: "${timestamp}\nRequesting Real-time Image email...")
	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailpic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callEmailPicHandler]))
    runIn(10, checkEmailPic)
}

def callEmailPicHandler(hubResponse) {
	log.debug "callEmailPicHandler"
    state.emailPic = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail Pic Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailPic", value: "sent")
    }
    else {
    	sendEvent(name: "emailPic", value: "error")
    }
}

def checkEmailPic() {
	log.debug "checkEmailPic"
    if (!state.emailPic) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "emailPic Call Timed Out..."
 		sendEvent(name: "substatus", value: "${date}\nEmail Pic call timed out.")
        sendEvent(name: "emailPic", value: "off")
    }
    else {
    	sendEvent(name: "emailPic", value: "on")
        log.debug "emailCPU call succeeded!"
    }
}

def callEmailCPU() {
	log.debug "callEmailCPU"
    state.emailCPU = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "substatus", value: "${timestamp}\nRequesting CPU Temp email...")
	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailcpu HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callEmailCPUHandler]))
    runIn(10, checkEmailCPU)
}

def callEmailCPUHandler(hubResponse) {
	log.debug "callEmailCPUHandler"
    state.emailCPU = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail CPU Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailCPU", value: "sent")
    }
    else {
    	sendEvent(name: "emailCPU", value: "error")
    }
}

def checkEmailCPU() {
	log.debug "checkEmailCPU"
    if (!state.emailCPU) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "emailCPU Call Timed Out..."
 		sendEvent(name: "substatus", value: "${date}\nEmail CPU call timed out.")
        sendEvent(name: "emailCPU", value: "off")
    }
    else {
    	sendEvent(name: "emailCPU", value: "on")
        log.debug "emailCPU call succeeded!"
    }
}

def refresh() {
	log.debug "switch: request refresh()"
    sendEvent(name: "substatus", value: "")
    getStatus() 
}

def getStatus() {
	log.debug "getStatus"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "status", value: "${timestamp}\nGetting Pi Status...")
	state.getStatus = false
	sendHubCommand(new physicalgraph.device.HubAction("""GET /getstatus HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: getStatusHandler]))
    runIn(10, checkGetStatus)
}

def checkGetStatus() {
	log.debug "checkGetStatus"
    if (!state.getStatus) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "getStatus Call Timed Out..."
 		sendEvent(name: "status", value: "${date}\nGet Status call timed out.")
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
    def msg = "${date}\nPi Server Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hHeaders = hubResponse.headers
    log.debug "hHeaders = ${hHeaders}"
    def hData = hubResponse.data
    log.debug "hData = ${hData}"
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
                        sendEvent(name: "emailCPU", value: "on")
                        sendEvent(name: "emailPic", value: "on")
                    }
                    else if (temp == "Not Running") {
                    	sVal = "unavailable"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "emailCPU", value: "off")
                    sendEvent(name: "emailPic", value: "off")
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
                    log.debug "temp = ${temp}"
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
                    log.debug "tSize = ${tSize}"
                    sendEvent(name: "diskSpace", value: tSize)
                    if (tSize < 0.1) {
                    	sVal = "lowDiskSpace"
                    }
                    else {
                    	sVal = "ok"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
            case 4:
            	if (hServerMsg[i].contains("CPU Temp") && sVal != "error") {
                	temp = hServerMsg[i].replace("CPU Temp=", "")
                    if (temp.contains("Fail")) {
                		sVal = "error"
                    }
                    else {
                    	tSize = Float.parseFloat(temp)
                        log.debug "CPU Temp = ${tSize}"
                        sendEvent(name: "cpuTemp", value: tSize)
                        if (tSize > 200) {
                        	sVal = "highCPUTemp"
                        }
                        else {
                        	sVal = "ok"
                        }
                    }
                }
                else {
                	sVal = "error"
                }
                break
            case 5:
            	if (hServerMsg[i].contains("Number Pics")) {
                	temp = hServerMsg[i].replace("Number Pics=", "")
                    sendEvent(name: "nbrPics", value: temp)
                }
                else {
                	sendEvent(name: "nbrPics", value: 0)
                }
            	break
            default:
            	break
        }
    }
    log.debug "sVal = ${sVal}"
    sendEvent(name: "state", value: sVal)
}

def imageServiceOff() {
	log.debug "imageServiceOff"
    sendEvent(name: "substatus", value: "")
    def safetyLevel = device.currentValue("safetyControl")
    log.debug "safetyControl == '10' (${safetyLevel == "10"})"
    if (device.currentValue("safetyControl") == "10") {
    	log.debug "safetyControl Level = 10"
        state.imageService = false
        sendEvent(name: "substatus", value: "Turning off Image Service...")
        sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureterminate HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: imageServiceHandler]))
        runIn(10, checkImageService)
        sendEvent(name: "safetyControl", value: 0)
    }
    else {
    	sendEvent(name: "substatus", value: "Set Level to 10 to turn off Image Service!")
        sendEvent(name: "imageService", value: "on")
    }
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
    runIn(5, getStatus)
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

    def map = stringToMap(description)

    if (map.tempImageKey) {
        try {
            storeTemporaryImage(map.tempImageKey, getPictureName())
        } catch (Exception e) {
            log.error e
        }
    } else if (map.error) {
        log.error "Error: ${map.error}"
    }

}

private getPictureName() {
    return java.util.UUID.randomUUID().toString().replaceAll('-', '')
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
    runEvery10Minutes(refresh)
}

