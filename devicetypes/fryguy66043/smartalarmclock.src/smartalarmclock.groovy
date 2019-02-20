/**
 *  Smart Alarm Clock
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
	definition (name: "SmartAlarmClock", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"
        attribute "alarm1On", "string"
        attribute "alarm1CheckPres", "string"
        attribute "alarm1CurrPres", "string"
        attribute "alarm1Time", "string"
        attribute "alarm1Days", "string"
        attribute "alarm1Presence", "string"

        command "getSettings"
        command "setAlarm"
        command "setPresence"
        command "changePresence"
	}

	simulator {
		
	}

	tiles(scale: 2) {
        standardTile("state", "device.state", decoration: "flat", width: 2, height: 2) {
        	state "ok", label: 'OK', icon: "st.Entertainment.entertainment1", backgroundColor:"#00A0DC"
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
            	[value: 0, color: "#ffffff"],
            	[value: 1, color: "#bc2323"],
            	[value: 10, color: "#44b621"]
        	]
        }
        valueTile("cpuTemp", "device.cpuTemp", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nCPU', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
                [value: 100, color: "#44b621"],
                [value: 200, color: "#bc2323"]
            ]
        }
        standardTile("alarm1", "device.alarm1", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office6", backgroundColor:"#bc2323"
        	state "off", label: '${name}', icon: "st.Office.office6", backgroundColor:"#ffffff"
            state "on", label: '${name}', icon: "st.Office.office6", backgroundColor:"#00A0DC"
            state "onPresent", label: 'ON', icon: "st.Office.office6", backgroundColor:"#00A0DC"
            state "onAway", label: 'OFF', icon: "st.Office.office6", backgroundColor:"#ffffff"
        }
		standardTile("alarm1Presence", "device.alarm1Presence", decoration: "flat", width: 2, height: 2) {
			state "home", label: '${name}', action: "changePresence", icon:"st.Home.home4", backgroundColor:"#00A0DC", nextState: "changingAway"
            state "changingAway", label: 'UPDATING', icon:"st.Home.home4", backgroundColor:"#00A0DC", nextState: "away"
			state "away", label: '${name}', action: "changePresence", icon:"st.Home.home3", backgroundColor:"#ffffff", nextState: "changingHome"
            state "changingHome", label: 'UPDATING', icon:"st.Home.home3", backgroundColor:"#ffffff", nextState: "home"
			state "noCheck", label: 'OFF', icon:"st.Lighting.light8", backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
		controlTile("safetyControl", "device.safetyControl", "slider", height: 2, width: 2, inactiveLabel: false, range: "(0..10)") {
        	state "level", action: "setSafetyControl"
        }


		main "state"
		details(["alarm1","alarm1Presence", "status", "substatus", "refresh"])}
}

def getFullPath() {
//	def PI_IP = "fryguypi.ddns.net"
	def PI_IP = "192.168.1.137"
	def PI_PORT = "5000"

//	return "http://${PI_IP}:${PI_PORT}"
	return "${PI_IP}:${PI_PORT}"
}

def setSafetyControl(val) {
	log.debug "setSafetyControl"
    if (val) {
    	sendEvent(name: "safetyControl", value: val)
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

	log.debug "msg = ${msg}"
	log.debug "status = ${status}"
    log.debug "headerString = ${headersAsString}"
	log.debug "headers = ${headerMap}"
    log.debug "body = ${body}"
    log.debug "data = ${data}"
}

def changePresence() {
	log.debug "changePresence"
    state.changePresence = false
    def newPres = device.currentValue("alarm1CurrPres") == "true" ? "False" : "True"
    sendEvent(name: "status", value: "Requesting Smart Alarm Clock Change Presence...")
    sendEvent(name: "substatus", value: "")
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/setpresence?nbr=1&pres=${newPres}",
        headers: [
                "HOST" : "192.168.1.137:5000"],
                null,
                [callback: changePresenceHandler]
	)
    log.debug result.toString()
    sendHubCommand(result)
}

def changePresenceHandler(sData) {
	log.debug "changePresenceHandler"
    state.changePresence = true
	refresh()
}

def getSettings() {
	log.debug "getSettings"
    state.getSettings = false
    sendEvent(name: "status", value: "Requesting Smart Alarm Clock Settings...")
    sendEvent(name: "substatus", value: "")
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/getalarmsettings",
        headers: [
                "HOST" : "192.168.1.137:5000"],
                null,
                [callback: getSettingsHandler]
	)
//    log.debug result.toString()
    sendHubCommand(result)
}

def getSettingsHandler(sData) {
	log.debug "getSettingsHandler"
    state.getSettings = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
	def hData = sData
    def header = hData.header
//    log.debug "header = ${header}"
    def body = hData.body
//    log.debug "body = ${body}"
    body = body.replace("<br><br>", "\n")
    body = body.replace("<br>", "<>")
    def reply = body
    body = body.replace("Alarm 1<>", "Alarm 1: (")
    body = body.replace("]<>", "])")
    sendEvent(name: "status", value: header)
    sendEvent(name: "substatus", value: body)
    reply = reply.replace("<>", "\n")
    def hMsg = reply.split('\n')
    def temp = ""
    
   	//log.debug "hMsg = ${hMsg}"
    for (int i = 0; i < hMsg.size(); i++) {
//    	log.debug "hMsg[${i}] = ${hMsg[i]}"
        switch (i) {
        	case 2:
            	if (hMsg[i].contains("Alarm On")) {
                	temp = hMsg[i].replace("Alarm On = ", "")
//                    log.debug "val = ${temp}"
                    if (temp == "True" ) {
                    	sendEvent(name: "alarm1On", value: "true")
//                        if (device.currentValue("alarm1On") == "true") {
//                        	log.debug "It worked!"
//                        }
                    }
                    else {
                    	sendEvent(name: "alarm1On", value: "false")
                    }
                }
                break
            case 3:
            	if (hMsg[i].contains("Alarm Check Pres")) {
                	temp = hMsg[i].replace("Alarm Check Pres = ", "")
//                    log.debug "val = ${temp}"
                    if (temp == "True" ) {
                    	sendEvent(name: "alarm1CheckPres", value: "true")
                    }
                    else {
                    	sendEvent(name: "alarm1CheckPres", value: "false")
                    }
                }
                break
            case 4:
            	if (hMsg[i].contains("Alarm Curr Pres")) {
                	temp = hMsg[i].replace("Alarm Curr Pres = ", "")
//                    log.debug "val = ${temp}"
                    if (temp == "True" ) {
                    	sendEvent(name: "alarm1CurrPres", value: "true")
                    }
                    else {
                    	sendEvent(name: "alarm1CurrPres", value: "false")
                    }
                }
                break
            case 5:
            	if (hMsg[i].contains("Alarm Time")) {
                	temp = hMsg[i].replace("Alarm Time = ", "")
//                    log.debug "val = ${temp}"
                    sendEvent(name: "alarm1Time", value: temp)
                }
                break
            case 6:
            	if (hMsg[i].contains("Alarm Days")) {
                	temp = hMsg[i].replace("Alarm Days (M->Su) = ", "")
//                    log.debug "val = ${temp}"
                    sendEvent(name: "alarm1Days", value: temp)
                }
                break
            default:
            	break
        }
    }
    if (device.currentValue("alarm1On") == "true") {
    	if (device.currentValue("alarm1CheckPres") == "true") {
        	if (device.currentValue("alarm1CurrPres") == "true") {
            	sendEvent(name: "alarm1", value: "onPresent")
                sendEvent(name: "alarm1Presence", value: "home")
            }
            else {
            	sendEvent(name: "alarm1", value: "onAway")
                sendEvent(name: "alarm1Presence", value: "away")
            }
        }
        else {
        	sendEvent(name: "alarm1", value: "on")
            sendEvent(name: "alarm1Presence", value: "noCheck")
        }
    }
    else {
    	sendEvent(name: "alarm1", value: "off")
        sendEvent(name: "alarm1Presence", value: "noCheck")
    }
}

def refresh() {
	log.debug "switch: request refresh()"
    sendEvent(name: "substatus", value: "")
    getSettings()
//    getStatus() 
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
//	reset()
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
//	refresh()
    runEvery10Minutes(refresh)
}
