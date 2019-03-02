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
		
        attribute "startupDateTime", "string"
        attribute "availDiskSpace", "string"
        attribute "cpuTempF", "string"
        attribute "update", "string"
        attribute "logSize", "string"
        attribute "alarmMin", "string"
        attribute "alarm1On", "string"
        attribute "alarm1Skip", "string"
        attribute "alarm1CheckPres", "string"
        attribute "alarm1CurrPres", "string"
        attribute "alarm1Time", "string"
        attribute "alarm1Days", "string"
        attribute "alarm1Presence", "string"
        attribute "alarm1Alarm", "string"
        
        command "getHealthStatus"
        command "getLogData"
        command "getSettings"
        command "setLogSize"
        command "setAlarmMin"
        command "alarmOnOff"
        command "alarmCheckPresence"
        command "setAlarmTime"
        command "alarmSkip"
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
        valueTile("logData", "device.logData", decoration: "flat", width: 6, height: 6) {
        	state "default", label: '${currentValue}'
        }
        valueTile("alarm1Time", "device.alarm1TimeDisp", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}', backgroundColor: "#ffffff"
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
        standardTile("alarm1", "device.alarm1", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office6", backgroundColor:"#bc2323"
        	state "off", label: '${name}', icon: "st.Office.office6", backgroundColor:"#ffffff"
            state "on", label: '${name}', icon: "st.Office.office6", backgroundColor:"#00A0DC"
            state "onPresent", label: 'ON', icon: "st.Office.office6", backgroundColor:"#00A0DC"
            state "onAway", label: 'OFF', icon: "st.Office.office6", backgroundColor:"#ffffff"
            state "alarm", label: '${name}', icon: "st.Office.office6", backgroundColor:"bc2323"
        }
		standardTile("alarm1Presence", "device.alarm1Presence", decoration: "flat", width: 2, height: 2) {
			state "home", label: '${name}', action: "changePresence", icon:"st.Home.home4", backgroundColor:"#00A0DC", nextState: "changingAway"
            state "changingAway", label: 'UPDATING', icon:"st.Home.home4", backgroundColor:"#00A0DC", nextState: "away"
			state "away", label: '${name}', action: "changePresence", icon:"st.Home.home3", backgroundColor:"#ffffff", nextState: "changingHome"
            state "changingHome", label: 'UPDATING', icon:"st.Home.home3", backgroundColor:"#ffffff", nextState: "home"
			state "noCheck", label: 'OFF', icon:"st.Lighting.light8", backgroundColor:"#ffffff"
		}
        standardTile("getLogData", "device.getLogData", decoration: "flat", width: 2, height: 2) {
        	state "default", label: 'Get Log', action: "getLogData"
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("skip", "device.skip", decoration: "flat", width: 2, height: 2) {
        	state "skip", label: 'Silence', action: "alarmSkip", backgroundColor:"#00A0DC", nextState: "changingSkip"
            state "changingSkip", label: 'Updating', backgroundColor:"#00A0DC", nextState: "noSkip"
            state "noSkip", label: 'Active', action: "alarmSkip", backgroundColor: "#ffffff", nextState: "changingNoSkip"
            state "changingNoSkip", label: 'Updating', backgroundColor:"#ffffff", nextState: "Skip"
        }
		controlTile("safetyControl", "device.safetyControl", "slider", height: 2, width: 2, inactiveLabel: false, range: "(0..10)") {
        	state "level", action: "setSafetyControl"
        }


		main "alarm1"
		details(["alarm1","alarm1Time", "alarm1Presence", "diskSpace", "cpuTemp", "substatus", "refresh", "skip", "logData"])}
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


def getHealthStatus() {
	log.debug "getHealthStatus"

	sendEvent(name: "substatus", value: "Requesting Health Status...")
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/healthcheck",
        headers: [
            "HOST" : "192.168.1.137:5000"],
        null,
        [callback: getHealthStatusHandler]
    )
    //    log.debug result.toString()
    sendHubCommand(result)
}

def getHealthStatusHandler(sData) {
	log.debug "getHealthStatusHandler"

    def hBody = sData.body.replace("<br>", "")
    def data = hBody.split('\n')

    for (int i=0; i < data.size(); i++) {
        hBody = data[i]
        if (hBody.contains("Avail Disk Space = ")) {
	        hBody = hBody.replace("Avail Disk Space = ", "")
            hBody = hBody.replace(" GB", "")
            sendEvent(name: "diskSpace", value: hBody)
        }
        if (hBody.contains("CPU Temp = ")) {
        	hBody = hBody.replace("CPU Temp = ", "")
            hBody = hBody.replace(" F", "")
            sendEvent(name: "cpuTemp", value: hBody)
        }
    }
}

def getLogData() {
	log.debug "getLogData"
    
    sendEvent(name: "substatus", value: "Requesting Log Data...")
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/getalarmlog",
        headers: [
            "HOST" : "192.168.1.137:5000"],
        null,
        [callback: getLogDataHandler]
    )
    //    log.debug result.toString()
    sendHubCommand(result)
}


def getLogDataHandler(sData) {
	log.debug "getLogDataHandler"
    def String strSize = device.currentValue("logSize")
    def maxSize = strSize ? strSize as Integer : 25
    log.debug "Max Log Size = ${maxSize}"
    
    def hBody = sData.body.replace("<br>", "")
    def data = hBody.split('\n')
    log.debug "Log Size = ${data.size()}"
//    if (data.size() > maxSize) {
    	hBody = "Log Size: ${data.size()}\n"
//    	for (int i=data.size() - maxSize; i < data.size(); i++) {
    	for (int i=0; i < data.size(); i++) {
        	hBody += data[i] + "\n"
        }
//    }
//    else {
//    	hBody = "Log Size: ${data.size()}\n" + hBody
//    }
    sendEvent(name: "logData", value: hBody)
}


def setLogSize(val) {
	log.debug "setLogSize(${val})"
    if (val != device.currentValue("logSize")) {
        log.debug "Updating logSize from ${device.currentValue("logSize")} to ${val}..."
        state.setLogSize = false
        sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Log Size to ${val}...")
//        sendEvent(name: "substatus", value: "")
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/setalarm?nbr=1&log=${val}",
            headers: [
                "HOST" : "192.168.1.137:5000"],
            null,
            [callback: setLogSizeHandler]
        )
        //    log.debug result.toString()
        sendHubCommand(result)
    }
}

def setLogSizeHandler(sData) {
	log.debug "setLogSizeHandler"
    refresh()
}

def alarmOnOff(nbr, val) {
	log.debug "alarmOnOff(${nbr}, ${val})"
	def temp = ""

    if (val.toUpperCase() == "TRUE" || val.toUpperCase() == "FALSE") {
    	if (val.toUpperCase() == "TRUE") {
        	temp = "True"
        }
        else {
        	temp = "False"
        }
        if (nbr == 1 && val.toUpperCase() != device.currentValue("alarm1On").toUpperCase()) {
            log.debug "Updating alarm1On from ${device.currentValue("alarm1On")} to ${temp}..."
            state.alarmOnOff = false
            sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Alarm 1 ${val}...")
//            sendEvent(name: "substatus", value: "")
            def result = new physicalgraph.device.HubAction(
                method: "GET",
                path: "/setalarm?nbr=1&on=${temp}",
                headers: [
                        "HOST" : "192.168.1.137:5000"],
                        null,
                        [callback: alarmOnOffHandler]
            )
        //    log.debug result.toString()
            sendHubCommand(result)
        }
    }
}

def alarmOnOffHandler(sData) {
	log.debug "alarmOnOffHandler"
    
    refresh()
}

def setAlarmMin(val) {
	log.debug "setAlarmMin(${val})"

	if (val != device.currentValue("alarmMin")) {
    	log.debug "Updating alarmMin from ${device.currentValue("alarmMin")} to ${val}..."
        state.setAlarmMin = false
        sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Alarm Minutes...")
//        sendEvent(name: "substatus", value: "")
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/setalarm?min=${val}",
            headers: [
                    "HOST" : "192.168.1.137:5000"],
                    null,
                    [callback: setAlarmMinHandler]
        )
    //    log.debug result.toString()
        sendHubCommand(result)
    }
}

def setAlarmMinHandler(sData) {
	log.debug "setAlarmMinHandler"

	refresh()
}

def setAlarmTime(nbr, time) {
	log.debug "setAlarmTime(${nbr}, ${time})"

	if (time != device.currentValue("alarm1Time")) {
    	log.debug "Updating alarm1Time from ${device.currentValue("alarm1Time")} to ${time}..."
        state.setAlarmTime = false
        sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Alarm Time...")
//        sendEvent(name: "substatus", value: "")
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/setalarm?nbr=1&time=${time}",
            headers: [
                    "HOST" : "192.168.1.137:5000"],
                    null,
                    [callback: setAlarmTimeHandler]
        )
    //    log.debug result.toString()
        sendHubCommand(result)
    }
}

def setAlarmTimeHandler(sData) {
	log.debug "setAlarmTimeHandler"
    
    refresh()
}

def alarmSkip() {
	log.debug "alarmSkip"
    def tSkip = device.currentValue("alarm1Skip")
    def temp = ""
    if (tSkip == "true") {
    	temp = "False"
    }
    else if (tSkip == "false") {
    	temp = "True"
    }
    if (temp) {
        state.alarmSkip = false
        sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Skip Next Alarm State...")
//        sendEvent(name: "substatus", value: "")
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/alarmskip?skip=${temp}",
            headers: [
                "HOST" : "192.168.1.137:5000"],
            null,
            [callback: alarmSkipHandler]
        )
        //    log.debug result.toString()
        sendHubCommand(result)
    }
}

def alarmSkipHandler(sData) {
	log.debug "alarmSkipHandler"
    refresh()
}

def alarmCheckPresence(nbr, val) {
	log.debug "alarmCheckPresence(${nbr}, ${val})"
    def temp = "False"
    def updt = false
    log.debug "Current Check Pres = ${device.currentValue("alarm1CheckPres")}"
    if (val == "true") {
    	if (device.currentValue("alarm1CheckPres") == "false") {
        	temp = "True"
            updt = true
        }        
    }
    else {
    	if (device.currentValue("alarm1CheckPres") == "true") {
    		temp = "False"
            updt = true
        }
    }

	if (updt) {
        log.debug "Updating alarm1CheckPres from ${device.currentValue("alarm1CheckPres")} to ${val}..."
        state.alarmCheckPres = false
        sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Check Presence to ${val}...")
//        sendEvent(name: "substatus", value: "")
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/setalarm?nbr=1&pres=${temp}",
            headers: [
                "HOST" : "192.168.1.137:5000"],
            null,
            [callback: alarmCheckPresenceHandler]
        )
        //    log.debug result.toString()
        sendHubCommand(result)
    }
}

def alarmCheckPresenceHandler(sData) {
	log.debug "alarmCheckPresenceHandler"
    refresh()
}

def setPresence(nbr, val) {
	log.debug "setPresence(${nbr}, ${val})"
    
    if (val == "true") {
    	if (device.currentValue("alarm1CurrPres") == "false") {
        	changePresence()
        }
    }
    else {
    	if (val == "false") {
        	if (device.currentValue("alarm1CurrPres") == "true") {
            	changePresence()
            }
        }
    }
    refresh()
}

def changePresence() {
	log.debug "changePresence"
    state.changePresence = false
    def newPres = device.currentValue("alarm1CurrPres") == "true" ? "False" : "True"
    sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Change Presence...")
//    sendEvent(name: "substatus", value: "")
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/setpresence?nbr=1&pres=${newPres}",
        headers: [
                "HOST" : "192.168.1.137:5000"],
                null,
                [callback: changePresenceHandler]
	)
//    log.debug result.toString()
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
    sendEvent(name: "substatus", value: "Requesting Smart Alarm Clock Settings...")
//    sendEvent(name: "substatus", value: "")
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
    runIn(10, getSettingsErrCheck)
}

def getSettingsErrCheck() {
	log.debug "getSettingsErrCheck"
	if (state.getSettings == false) {
    	sendEvent(name: "substatus", value: "Smart Alarm Clock call failed...")
//        sendEvent(name: "substatus", value: "Check server availabilty.")
    }
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
//    body = body.replace("]<>", "])")
	body += ")"
    body = body.replace("<>)", ")")
    sendEvent(name: "status", value: header)
    sendEvent(name: "substatus", value: body)
    reply = reply.replace("<>", "\n")
    def hMsg = reply.split('\n')
    def temp = ""

    //log.debug "hMsg = ${hMsg}"
    for (int i = 0; i < hMsg.size(); i++) {
        //    	log.debug "hMsg[${i}] = ${hMsg[i]}"
        if (hMsg[i].contains("Startup =")) {
        	temp = hMsg[i].replace("Startup = ", "")
            log.debug "Startup = ${temp}"
            log.debug "startupDateTime = ${device.currentValue("startupDateTime")}"
            if (temp != device.currentValue("startupDateTime")) {
            	log.debug "Server Restarted.  Need to send update for alarm settings and presence value."
            	sendEvent(name: "startupDateTime", value: temp)
            }
        }
        if (hMsg[i].contains("Log Size = ")) {
        	temp = hMsg[i].replace("Log Size = ", "")
            log.debug "Log Size = ${temp}"
            sendEvent(name: "logSize", value: temp)
        }
        if (hMsg[i].contains("Alarm Min = ")) {
        	temp = hMsg[i].replace("Alarm Min = ", "")
            sendEvent(name: "alarmMin", value: temp)
        }
        if (hMsg[i].contains("Alarm On")) {
            temp = hMsg[i].replace("Alarm On = ", "")
            if (temp == "True" ) {
                sendEvent(name: "alarm1On", value: "true")
            }
            else {
                sendEvent(name: "alarm1On", value: "false")
            }
        }
        if (hMsg[i].contains("Alarm Skip")) {
            temp = hMsg[i].replace("Alarm Skip = ", "")
            if (temp == "True" ) {
            	log.debug "Setting to skip..."
                sendEvent(name: "alarm1Skip", value: "true")
                sendEvent(name: "skip", value: "skip")
            }
            else {
            	log.debug "Setting to noSkip..."
                sendEvent(name: "alarm1Skip", value: "false")
                sendEvent(name: "skip", value: "noSkip")
            }
        }
        if (hMsg[i].contains("Alarm Check Pres")) {
            temp = hMsg[i].replace("Alarm Check Pres = ", "")
            if (temp == "True" ) {
                sendEvent(name: "alarm1CheckPres", value: "true")
            }
            else {
                sendEvent(name: "alarm1CheckPres", value: "false")
            }
        }
        if (hMsg[i].contains("Alarm Curr Pres")) {
            temp = hMsg[i].replace("Alarm Curr Pres = ", "")
            log.debug "Curr Pres = ${temp}"
            if (temp == "True" ) {
                sendEvent(name: "alarm1CurrPres", value: "true")
            }
            else {
                sendEvent(name: "alarm1CurrPres", value: "false")
            }
        }
        if (hMsg[i].contains("Alarm Time")) {
            temp = hMsg[i].replace("Alarm Time = ", "")
            log.debug "converting time: ${temp}"
            if (temp > "" && temp != "null") {
                def ap = "am"
                def hhD = (temp[0] + temp[1]).toInteger()
                def mm = temp[2] + temp[3] + temp[4]
                if (hhD > 11) {
                    ap = "pm"
                }
                if (hhD > 12) {
                    hhD = hhD - 12
                }
                def hh = String.format("%02d", hhD)
                log.debug "hh = ${hh}"
                def aTime = "${hh}${mm} ${ap}\n(${device.currentValue("alarmMin")} min)"
                log.debug "temp = ${temp} / aTime = ${aTime}"
                sendEvent(name: "alarm1Time", value: temp)
                sendEvent(name: "alarm1TimeDisp", value: aTime)
            }
            else {
            	log.debug "Alarm 1 Time from server invalid!"
            }
        }
        if (hMsg[i].contains("Alarm Days")) {
            temp = hMsg[i].replace("Alarm Days (M->Su) = ", "")
            sendEvent(name: "alarm1Days", value: temp)
        }
        if (hMsg[i].contains("Alarm =")) {
            temp = hMsg[i].replace("Alarm = ", "")
            if (temp == "True") {
                sendEvent(name: "alarm1Alarm", value: "true")
            }
            else {
                sendEvent(name: "alarm1Alarm", value: "false")
            }
        }
    }



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
            runIn(60, refresh)
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
//    runEvery10Minutes(refresh)
}

def refresh() {
	log.debug "switch: request refresh()"
    sendEvent(name: "substatus", value: "")
    getSettings()
    getHealthStatus()
    getLogData()
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
	refresh()
    runEvery10Minutes(refresh)
}
