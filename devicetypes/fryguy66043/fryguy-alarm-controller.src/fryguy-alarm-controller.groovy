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
        
        command "setArmedAway"
        command "setArmedHome"
        command "setDisarmed"
        command "setAlert"
        command "dismissAlert"
        command "activateAlarm"
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
        valueTile("summary", "device.summary", decoration: "flat", width: 6, height: 6) {
 	      state "default", label: '${currentValue}'
        }
       
		main "state"
        
		details(["state", "armAway", "armHome", "disarm", "dismiss", "alarm", "summary"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def setArmedAway() {
	log.debug "setArmedAway: alarmState = ${device.currentValue("alarmState")}"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

    if (device.currentValue("alarmState") != "Armed Away") {
    	log.debug "Setting to Armed Away"
        sendEvent(name: "contact", value: "armedAway")
        sendEvent(name: "alarmState", value: "Armed Away")
        sendEvent(name: "lastArmedAwayDate", value: date)
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
            updateSummary()
        }
    }
}

def setAlert() {
	log.debug "setAlert: ${device.currentValue("alarmState")}"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def alert = false
    
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
    setDisarmed()
}

