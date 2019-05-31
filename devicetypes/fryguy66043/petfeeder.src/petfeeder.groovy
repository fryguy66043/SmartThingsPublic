/**
 *  Pet Feeder
 *
 *
 *
 *  Copyright 2019 Jeffrey Fry
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
	preferences {
    	input "serverOn", "bool", title: "Is the Pet Feeder Server Running?"
    }
	definition (name: "PetFeeder", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "startupDateTime", "string"
        attribute "availDiskSpace", "string"
        attribute "cpuTempF", "string"
        attribute "update", "string"
        
        command "getHealthStatus"
        command "feed"
	}

	simulator {
		
	}

	tiles(scale: 2) {
        valueTile("status", "device.status", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}' 
        }
        valueTile("substatus", "device.substatus", decoration: "flat", width: 6, height: 8) {
        	state "default", label: '${currentValue}'
        }
        valueTile("diskSpace", "device.diskSpace", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nGB', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
            	[value: 1, color: "#bc2323"],
            	[value: 10, color: "#44b621"]
        	]
        }
        valueTile("cpuTemp", "device.cpuTemp", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}°\nCPU', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
                [value: 100, color: "#44b621"],
                [value: 200, color: "#bc2323"]
            ]
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("feed", "device.feed", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '', action: "feed", icon:"st.Food & Dining.dining13"
        }


		main "state"
		details(["substatus", "diskSpace", "cpuTemp", "feed", "refresh"])}
}

def getFullPath() {
	def PI_IP = "192.168.1.205"
	def PI_PORT = "5000"

	return "${PI_IP}:${PI_PORT}"
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

	log.debug "msg = ${msg}"
	log.debug "status = ${status}"
    log.debug "headerString = ${headersAsString}"
	log.debug "headers = ${headerMap}"
    log.debug "body = ${body}"
    log.debug "data = ${data}"
}

def feed() {
	log.debug "feed"
    
    if(!serverOn) {
    	log.debug "Server disabled in preferences"
        return
    }
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/feed",
        headers: [
            "HOST" : "192.168.1.205:5000"],
        null,
        [callback: feedHandler]
    )
    //    log.debug result.toString()
    sendHubCommand(result)
}

def feedHandler(sData) {
	log.debug "feedHandler"

	getHealthStatus()
}

def getHealthStatus() {
	log.debug "getHealthStatus"

	if(!serverOn) {
    	log.debug "Server disabled in preferences"
        return
    }
    
//	sendEvent(name: "substatus", value: "Requesting Health Status...")
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/healthcheck",
        headers: [
            "HOST" : "192.168.1.205:5000"],
        null,
        [callback: getHealthStatusHandler]
    )
    //    log.debug result.toString()
    sendHubCommand(result)
}

def getHealthStatusHandler(sData) {
	log.debug "getHealthStatusHandler"

    def hBody = sData.body.replace("<br>", "")
//    log.debug "hBody: ${hBody}"
    sendEvent(name: "substatus", value: hBody)
    
	def data = hBody.split('\n')
	def idx = 0
    
    for (int i=0; i < data.size(); i++) {
        hBody = data[i]
        if (hBody.contains("Avail Disk Space = ")) {
	        hBody = hBody.replace("Avail Disk Space = ", "")
            hBody = hBody.replace(" GB", "")
            sendEvent(name: "diskSpace", value: hBody)
        }
        if (hBody.contains("CPU Temp = ")) {
        	hBody = hBody.replace("CPU Temp = ", "")
            idx = hBody.indexOf(" F")
            hBody = hBody.substring(0, idx)
            sendEvent(name: "cpuTemp", value: hBody)
        }
    }
//    updateDisplay()
}

def updateDisplay() {
	log.debug "updateDisplay"


    def checkAlarm = false
    def checkTime = new Date()
    
    if (device.currentValue("alarm1On") == "true") {
        if (device.currentValue("alarm1CheckPres") == "true") {
            if (device.currentValue("alarm1CurrPres") == "true") {
                sendEvent(name: "alarm1", value: "onPresent")
                sendEvent(name: "alarm1Presence", value: "home")
                checkAlarm = true
            }
            else {
                sendEvent(name: "alarm1", value: "onAway")
                sendEvent(name: "alarm1Presence", value: "away")
            }
        }
        else {
            sendEvent(name: "alarm1", value: "on")
            sendEvent(name: "alarm1Presence", value: "noCheck")
            checkAlarm = true
        }
        if (device.currentValue("alarm1Alarm") == "true") {
            sendEvent(name: "alarm1", value: "alarm")
            log.debug "Alarming..."
//            runIn(60, refresh)
        }
        else if (checkAlarm) {
        	log.debug "checkAlarm = true"
        	def tDate = new Date().format("MM/dd/yyyy", location.timeZone)
            tDate += " ${device.currentValue("alarm1Time")}"
            checkTime = Date.parse("MM/dd/yyyy HH:mm", tDate)
            log.debug "checkTime = ${checkTime}"
        }
    }
    else {
        sendEvent(name: "alarm1", value: "off")
        sendEvent(name: "alarm1Presence", value: "noCheck")
    }
}

def refresh() {
	log.debug "switch: request refresh()"
    if (serverOn) {
        sendEvent(name: "substatus", value: "")
        getHealthStatus()
    }
    else {
    	log.debug "Skipping refresh.  Server not running"
    }
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
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
    if(serverOn){
//        refresh()
//        runEvery10Minutes(refresh)
		getHealthStatus()
        runEvery10Minutes(getHealthStatus)
    }
    else {
    	unschedule()
	    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	sendEvent(name:"substatus", value:"Server Off: ${date}")
    }
}