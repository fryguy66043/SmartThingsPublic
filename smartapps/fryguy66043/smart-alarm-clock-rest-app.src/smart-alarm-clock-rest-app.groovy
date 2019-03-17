/**
 *  Smart Alarm Clock Smart App w/ REST Services.
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
    name: "Smart Alarm Clock (REST) App",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Smart Alarm Clock Smart App with REST Services.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Smart Alarm Clock") {
    	input "alarmClock", "device.smartalarmclock", title: "Select your Smart Alarm Clock."
        input "switches", "capability.switch", title: "Select a switch."
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
}

def initialize() {
}

mappings {
  path("/alarmstate") {
    action: [
      GET: "getAlarmState"
    ]
  }
  path("/setalarm/:command") {
    action: [
      POST: "setAlarm"
    ]
  }
}

def getAlarmState() {
	log.debug "getAlarmState"
    
    state.setAlarmTime = ""
    
	def resp = []
    resp << [name: "AlarmOn", value: alarmClock.currentValue("alarm1On")]
    resp << [name: "AlarmCheckPres", value: alarmClock.currentValue("alarm1CheckPres")]
    resp << [name: "AlarmCurrPres", value: alarmClock.currentValue("alarm1CurrPres")]
    resp << [name: "Alarm", value: alarmClock.currentValue("alarm1Alarm")]
    resp << [name: "AlarmSkip", value: alarmClock.currentValue("alarm1Skip")]
    
    return resp
}

def setAlarm() {
	state.setAlarmTime = state.setAlarmTime ?: new Date().format("yyyy-MM-dd HH:mm:ss")
    def lastDate = Date.parse("yyyy-MM-dd HH:mm:ss", state.setAlarmTime)
    def curDate = new Date()
    def pass = true
    use (groovy.time.TimeCategory) {
    	if (curDate < lastDate - 5.seconds) {
        	log.debug "Executed within 5 seconds.  Skipping!"
        	pass = false
        }
    }
	log.debug "setAlarm (${params.command})"
    
    def resp = []
    def command = params.command
    resp << [name: "Command", value: command]
    if (pass) {
        switch(command) {
            case "on":
                log.debug "Alarm on"
                resp << [name: "Alarm", value: "True"]
                alarmClock.setAlarmOn()
                break
            case "off":
                log.debug "Alarm off"
                resp << [name: "Alarm", value: "False"]
                alarmClock.setAlarmOff()
                break
            case "skip":
                log.debug "Skip alarm"
                resp << [name: "AlarmSkip", value: "True"]
                alarmClock.setAlarmSkip()
                break
            default:
                log.debug "Invalid Command"
                httpError(400, "Command '${command}' is not valid for Smart Alarm Clock")
                break
        }
    }
    else {
    	log.debug "Skipping.  Executed too quickly."
        resp << [name: "Execution", value: "Too Soon!"]
    }
    return resp
}
