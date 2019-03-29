/**
 *  Alarm Controller (REST) SmartApp
 *  Provides REST services for a physical alarm controller/monitor to communicate with the Alarm Controller DH.
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

definition(
    name: "Alarm Controller (REST) SmartApp",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Provides REST services for a physical alarm controller/monitor to communicate with the Alarm Controller DH.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png")

preferences {
	section ("Alarm Controller") {
    	input "alarmSensor", "device.fryguyAlarmController", required: true, title: "Select Alarm Controller."
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	initialize()
}

def initialize() {
	log.debug "initialize()"
}

mappings {
  path("/initialize") {
  	action: [
    	GET: "initializeController"
    ]
  }
  path("/setdisarmed") {
    action: [
      GET: "setDisarmed"
    ]
  }
  path("/setarmed/:command") {
    action: [
      GET: "setArmed"
    ]
  }
}

def initializeController() {
	log.debug "initializeController"
    def resp = []
    resp << [name: "AlarmState", value: "${alarmSensor.currentValue("alarmState")}"]
    resp << [name: "AlertState", value: "${alarmSensor.currentValue("alertState")}"]
    resp << [name: "UnsecureList", value: "${alarmSensor.currentValue("unsecureList")}"]
    log.debug "Reply: ${resp}"
    alarmSensor.updateServer()
    return resp
}

def setDisarmed() {
	state.setTime = state.setTime ?: new Date().format("yyyy-MM-dd HH:mm:ss")
    def lastDate = Date.parse("yyyy-MM-dd HH:mm:ss", state.setTime)
    def curDate = new Date()
    def pass = true
    use (groovy.time.TimeCategory) {
    	if (curDate <= lastDate - 5.seconds) {
        	log.debug "Executed within 5 seconds.  Skipping!"
        	pass = false
        }
    }
	log.debug "setDisarmed()"
    
    def resp = []
    if (pass) {
    	if (!alarmSensor.currentValue("alertState").contains("silent")) {
        	log.debug "Dismissing Alarm"
            resp << [name: "Alert", value: "Dismissed"]
            alarmSensor.dismissAlert()
        }
    	if (!alarmSensor.currentValue("alarmState").contains("Disarmed")) {
            log.debug "Disarmed"
            resp << [name: "Alarm", value: "Disarmed"]
            alarmSensor.setDisarmed()
        }
        else {
        	log.debug "Not Changing - Already Disarmed"
            resp << [name: "Alarm", value: "Unchanged"]
        }
    }
    else {
    	log.debug "Skipping.  Executed too quickly."
        resp << [name: "Execution", value: "Too Soon!"]
    }
    return resp
}

def setArmed() {
	state.setTime = state.setTime ?: new Date().format("yyyy-MM-dd HH:mm:ss")
    def lastDate = Date.parse("yyyy-MM-dd HH:mm:ss", state.setTime)
    def curDate = new Date()
    def pass = true
    use (groovy.time.TimeCategory) {
    	if (curDate <= lastDate - 5.seconds) {
        	log.debug "Executed within 5 seconds.  Skipping!"
        	pass = false
        }
    }
	log.debug "setArmed(${params.command}) / curr: ${alarmSensor.currentValue("alarmState")}"
    
    def resp = []
    def command = params.command
    resp << [name: "Command", value: command]
    if (pass) {
        switch(command) {
            case "home":
		    	if (!alarmSensor.currentValue("alarmState").contains("Armed Home")) {
                    log.debug "Armed Home"
                    resp << [name: "Armed", value: "Home"]
                    alarmSensor.setArmedHome()
                }
                else {
                    log.debug "Not Changing - Already Armed Home"
                    resp << [name: "Alarm", value: "Unchanged"]
                }
                break
            case "away":
		    	if (!alarmSensor.currentValue("alarmState").contains("Armed Away")) {
                    log.debug "Armed Away"
                    resp << [name: "Armed", value: "Away"]
                    alarmSensor.setArmedAway()
                }
                else {
                    log.debug "Not Changing - Already Armed Away"
                    resp << [name: "Alarm", value: "Unchanged"]
                }
                break
            default:
                log.debug "Invalid Command"
                httpError(400, "Command '${command}' is not valid for Alarm Controller")
                break
        }
    }
    else {
    	log.debug "Skipping.  Executed too quickly."
        resp << [name: "Execution", value: "Too Soon!"]
    }
    return resp
}
