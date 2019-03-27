/**
 *  FryGuy Alarm Controller
 *
 *  Designed to take open() and close() commands.  
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
	preferences {
    	input "alarmService", "bool", title: "Turn calls to Alarm Service On?", default: false, required: false
    	input "alarmServiceIP", "text", title: "(Optional) Alarm Service IP", requried: false
        input "alarmServicePort", "text", title: "(Optional) Alarm Service Port", required: false
        input "alarmServiceCode", "text", title: "(Optional) Enter Code for Alarm Panel Access", required: false
    }
	definition (name: "FryGuy Alarm Controller", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "update", "string"
        attribute "alarmState", "enum", ["Armed Away", "Armed Home", "Disarmed"]
        attribute "alertState", "enum", ["alarm", "silent", "userAlarm"]
        attribute "lastAlarmDate", "string"
        attribute "lastAlarmDismissedDate", "string"
        attribute "lastArmedHomeDate", "string"
        attribute "lastArmedAwayDate", "string"
        attribute "lastDisarmedDate", "string"
        attribute "armedAwayMonitoredList", "string"
        attribute "armedHomeMonitoredList", "string"
        attribute "unsecureList", "string"
        
        command "setArmedAway"
        command "setArmedHome"
        command "setDisarmed"
        command "setAlert"
        command "dismissAlert"
        command "activateAlarm"
        command "setArmedHomeMonitoredList"
        command "setArmedAwayMonitoredList"
        command "setUnsecureList"
        command "updateSummary"
        command "resetUserAlertCnt"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("state", "device.contact", width: 2, height: 2) {
			state("armedAway", label: 'AWAY', icon: "st.security.alarm.on", backgroundColor:"#00A0DC")
			state("armedHome", label: 'HOME', icon: "st.security.alarm.on", backgroundColor:"#00A0DC")
			state("disarmed", label: 'DISARMED', icon: "st.security.alarm.off", backgroundColor:"#e86d13")
            state("armedAwayAlert", label: 'ALARM', icon: "st.security.alarm.on", backgroundColor:"#bc2323")
            state("armedHomeAlert", label: 'ALARM', icon: "st.security.alarm.on", backgroundColor: "#bc2323")
            state("disarmedAlert", label: 'DISARMED', icon: "st.security.alarm.on", backgroundColor: "#bc2323")
            state("userAlert", label: 'USER', icon: "st.security.alarm.on", backgroundColor: "#bc2323")
		}

		standardTile("armAway", "device.setAlarm", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'AWAY', action: "setArmedAway", icon:"st.security.alarm.on"
		}
		standardTile("armHome", "device.setAlarm", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'HOME', action: "setArmedHome", icon:"st.Home.home3"
		}
		standardTile("disarm", "device.setAlarm", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'DISARM', action: "setDisarmed", icon:"st.security.alarm.off"
		}
		standardTile("dismiss", "device.setAlarm", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'DISMISS', action: "dismissAlert", icon:"st.custom.sonos.muted"
		}
        standardTile("alarm", "device.alarm", decoration: "flat", width: 2, height: 2) {
 	     	state "default", label: 'ACTIVATE ALARM', action: "activateAlarm", icon:"st.alarm.beep.beep"
        }
        valueTile("unsecure", "device.unsecure", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Alarm Triggers:\n${currentValue}'
        }
        valueTile("summary", "device.summary", decoration: "flat", width: 6, height: 6) {
 	      	state "default", label: '${currentValue}'
        }
        valueTile("monitored", "device.monitored", decoration: "flat", width: 6, height: 6) {
        	state "default", label: '${currentValue}'
        }
       
		main "state"
        
		details(["state", "armAway", "armHome", "disarm", "dismiss", "alarm", "unsecure", "summary", "monitored"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def setArmedAwayMonitoredList(list) {
	log.debug "setArmedAwayMonitoredList(${list})"
    def monitored = list ?: "None"
    sendEvent(name: "armedAwayMonitoredList", value: monitored)
    updateServerList("away", monitored)
}

def setArmedHomeMonitoredList(list) {
	log.debug "setArmedHomeMonitoredList(${list})"
    def monitored = list ?: "None"
    sendEvent(name: "armedHomeMonitoredList", value: monitored)
    updateServerList("home", monitored)
}

def setUnsecureList(list) {
	log.debug "setUnsecureList(${list})"
    def monitored = list ?: "None"
    sendEvent(name: "unsecureList", value: monitored)
    sendEvent(name: "unsecure", value: monitored)
    if (state.serverRefresh) {
    	def alist = device.currentValue("armedAwayMonitoredList")
    	if (alist) {
	    	updateServerList("away", alist)
        }
    	def hlist = device.currentValue("armedHomeMonitoredList")
    	if (hlist) {
	    	updateServerList("home", hlist)
        }
    }
}

def setArmedAway() {
	log.debug "setArmedAway: alarmState = ${device.currentValue("alarmState")}"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

    if (device.currentValue("alarmState") != "Armed Away") {
    	log.debug "Setting to Armed Away"
        sendEvent(name: "contact", value: "armedAway")
        sendEvent(name: "alarmState", value: "Armed Away")
        sendEvent(name: "lastArmedAwayDate", value: date)
        def awayList = device.currentValue("armedAwayMonitoredList") ?: "None"
        sendEvent(name: "monitored", value: "Armed Away Monitoring:\n${awayList}")
        updateSummary()
    }
}

def setArmedHome() {
	log.debug "setArmedHome"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
    if (device.currentValue("alarmState") != "Armed Home") {
        sendEvent(name: "contact", value: "armedHome")
        sendEvent(name: "alarmState", value: "Armed Home")
        sendEvent(name: "lastArmedHomeDate", value: date)
        def homeList = device.currentValue("armedHomeMonitoredList") ?: "None"
        sendEvent(name: "monitored", value: "Armed Home Monitoring:\n${homeList}")
        updateSummary()
    }
}

def setDisarmed() {
	log.debug "setDisarmed"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def disarm = false

	if (device.currentValue("alarmState") != "Disarmed") {
        switch (device.currentValue("alertState")) {
            case "alarm":
                sendEvent(name: "contact", value: "disarmedAlert")
                sendEvent(name: "alarmState", value: "Disarmed")
                disarm = true
                break
            case "silent":
                sendEvent(name: "contact", value: "disarmed")
                sendEvent(name: "alarmState", value: "Disarmed")
                disarm = true
                break
            default:
                break
        }
        if (disarm) {
            sendEvent(name: "lastDisarmedDate", value: date)
            sendEvent(name: "monitored", value: "Not Actively Monitoring")
            updateSummary()
        }
    }
}

def setAlert() {
	log.debug "setAlert: ${device.currentValue("alarmState")}"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def alert = false

    if (device.currentValue("alertState") == "userAlarm") {
        log.debug "set userAlert"
        sendEvent(name: "contact", value: "userAlert")
        alert = true
    }
    else {
        switch (device.currentValue("alarmState")) {
            case "Armed Away":
                log.debug "set armedAwayAlert"
                sendEvent(name: "contact", value: "armedAwayAlert")
                sendEvent(name: "alertState", value: "alarm")
                alert = true
                break
            case "Armed Home":
                log.debug "set armedHomeAlert"
                sendEvent(name: "contact", value: "armedHomeAlert")
                sendEvent(name: "alertState", value: "alarm")
                alert = true
                break
            case "Disarmed":
                if (device.currentValue("alertState") == "userAlarm") {
                    log.debug "set userAlert"
                    sendEvent(name: "contact", value: "userAlert")
                    alert = true
                }
                break
            default:
                log.debug "unknown alarmState"
                break
        }
	}
    if (alert) {
        sendEvent(name: "lastAlarmDate", value: date)
        updateSummary()
    }
}

def dismissAlert() {
	log.debug "dismissAlert"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def alarm = false
    
	switch (device.currentValue("alarmState")) {
    	case "Armed Away":
        	log.debug "dismiss armedAwayAlert"
        	sendEvent(name: "contact", value: "armedAway")
            sendEvent(name: "alertState", value: "silent")
            alarm = true
        	break
        case "Armed Home":
        	log.debug "dismiss armedHomeAlert"
        	sendEvent(name: "contact", value: "armedHome")
            sendEvent(name: "alertState", value: "silent")
            alarm = true
        	break
        case "Disarmed":
        	log.debug "dismiss homeAlert"
            if (device.currentValue("alertState") == "userAlarm") {
            	alarm = true
            }
            sendEvent(name: "contact", value: "disarmed")
            sendEvent(name: "alertState", value: "silent")
            break
        default:
        	log.debug "unknown alarmState"
        	break
    }
    if (alarm) {
	    sendEvent(name: "lastAlarmDismissedDate", value: date)
        updateSummary()
    }
}

private activateAlarm() {
	log.debug "activateAlarm"
    if (device.currentValue("alertState") != "userAlarm") {
        state.userAlertCnt = (state.userAlertCnt) ? state.userAlertCnt : 0
        state.userAlertCnt = state.userAlertCnt + 1
        log.debug "activateAlarm: state.userAlertCnt = ${state.userAlertCnt}"
        if (state.userAlertCnt > 2) {
            log.debug "activateAlarm: state.userAlertCnt > 2.  Calling setAlert()"
            sendEvent(name: "alertState", value: "userAlarm")
            setAlert()
        }
        runIn(3, resetUserAlertCnt)
    }
    else {
    	log.debug "User Alarm already activated..."
    }
}

def resetUserAlertCnt() {
	log.debug "resetUserAlertCnt: state.userAlertCnt = ${state.userAlertCnt}"
    state.userAlertCnt = 0
}

private updateSummary() {
	log.debug "updateSummary"
	def lastAlarm = (device.currentValue("lastAlarmDate")) ? device.currentValue("lastAlarmDate") : "N/A"
    def alarmDismissed = (device.currentValue("lastAlarmDismissedDate")) ? device.currentValue("lastAlarmDismissedDate") : "N/A"
    def armedAway = (device.currentValue("lastArmedAwayDate")) ? device.currentValue("lastArmedAwayDate") : "N/A"
    def armedHome = (device.currentValue("lastArmedHomeDate")) ? device.currentValue("lastArmedHomeDate") : "N/A"
    def lastDisarmed = (device.currentValue("lastDisarmedDate")) ? device.currentValue("lastDisarmedDate") : "N/A"

	def sum = "Last Events:\nAlarm: ${lastAlarm}\n" +
    	"Dismissed: ${alarmDismissed}\n" +
    	"Armed Away: ${armedAway}\n" +
    	"Armed Home: ${armedHome}\n" +
        "Disarmed: ${lastDisarmed}"
    sendEvent(name: "summary", value: sum)
    updateServer()
}

def getFullPath() {
	def PI_URL = alarmServiceIP
	def PI_PORT = alarmServicePort

	return "http://${PI_URL}:${PI_PORT}"
}

private updateServerCode() {
	log.debug "updateServerCode()"

	if (alarmServiceCode) {
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/updatecode?code=${alarmServiceCode}",
            headers: [
                "HOST" : "${alarmServiceIP}:${alarmServicePort}"],
            null,
            [callback: updateServerCodeHandler]
        )
        //    log.debug result.toString()
        sendHubCommand(result)
	}
    else {
    	log.debug "Alarm Service Code Not Configured"
    }
}

def updateServerCodeHandler(sData) {
	log.debug "updateServerCodeHandler(status: ${sData.status} / body = ${sData.body})"
}

private updateServerList(list, values) {
	log.debug "updateServerList(${list}, ${values})"

	if (alarmService && alarmServiceIP && alarmServicePort) {
    	def listVals =  URLEncoder.encode(values, "UTF-8")
        state.serverRefresh = false
        
        def cmd = "monitored?${list}=${listVals}"
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/${cmd}",
            headers: [
                "HOST" : "${alarmServiceIP}:${alarmServicePort}"],
            null,
            [callback: updateServerListHandler]
        )
        //    log.debug result.toString()
        sendHubCommand(result)
	}
    else {
    	log.debug "Alarm Service Not Configured"
    }
}

def updateServerListHandler(sData) {
	log.debug "updateServerListHandler(status: ${sData.status} / body = ${sData.body})"
}

private updateServer() {
	log.debug "updateServer: alarm = ${device.currentValue("alarmState")} / state = ${device.currentValue("alertState")}"

	log.debug "Alarm Service: ${alarmService} / ${alarmServiceIP} / ${alarmServicePort}"
	if (alarmService && alarmServiceIP && alarmServicePort) {
        def alarm = device.currentValue("alarmState")
        def alert = device.currentValue("alertState")
        def cmd = ""

        switch (alarm) {
            case "Armed Home":
                cmd = "armedhome"
                break
            case "Armed Away":
                cmd = "armedaway"
                break
            case "Disarmed":
                cmd = "disarmed"
                break
            default:
                log.debug "Invalid Alarm State: ${device.currentValue("alarmState")}"
        }
        if (cmd) {
        	def unsec = URLEncoder.encode(device.currentValue("unsecure"), "UTF-8")
        	cmd += "?alertstate=${device.currentValue("alertState")}&unsecure=${unsec}"
            state.pollStatus = false
            def path = "${getFullPath()}/${cmd}"
            log.debug "Calling Alarm Service: ${path}"

            def result = new physicalgraph.device.HubAction(
                method: "GET",
                path: "/${cmd}",
                headers: [
                    "HOST" : "${alarmServiceIP}:${alarmServicePort}"],
                null,
                [callback: updateServerHandler]
            )
            //    log.debug result.toString()
            sendHubCommand(result)

/*
			try {
                httpGet(path) { resp ->
                    log.debug "    calling server..."

                    if (resp.status == 200) {
                        updateServerHandler("${resp.data}")
                    } else {
                        log.error "Error calling alarm service.  Status: ${resp.status}"
                        updateServerErr()
                    }
                }
                log.debug "After httpGet..."
            } catch (err) {
                log.debug "Error executing alarm service request: $err"
            }
*/            
        } 
    }
    else {
    	log.debug "Alarm Service Not Configured"
    }
}

def updateServerHandler(sData) {
	log.debug "updateServerHandler(status: ${sData.status} / body = ${sData.body})"
    state.pollStatus = true    
}

def updateServerErr() {
	log.debug "updateServerErr (checking for errors)"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    if (!state.pollStatus) {
    	log.debug "Polling timed out..."
    }
    else {
    	log.debug "Polling success!"
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
    sendEvent(name: "unsecure", value: "None")

//    setDisarmed() //Not sure I need to set this to Disarmed everytime an update is done.  Evaluating...
    
    if (alarmService) {
        state.serverRefresh = true
    }
    
    if (alarmServiceCode) {
    	updateServerCode()
    }
}

