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
        input "thermostat", "capability.thermostat", required: false, title: "Select Thermostat."
        input "lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches."
        input "doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls."
        input "locks", "capability.lock", required: false, multiple: true, title: "Select Locks."
        input "contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contact Sensors."
    	input "forecast", "device.mySmartweatherTile", required: false, title: "Select Forecast Provider."
        input "wxDevice", "device.fryguypiWxDevice", required: false, title: "Select Weather Device."
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
    subscribe(lights, "switch", changeHandler)
    subscribe(doors, "door", changeHandler)
    subscribe(locks, "lock", changeHandler)
    subscribe(contacts, "contact", changeHandler)
    subscribe(alarmSensor, "alarmState", changeHandler)
    subscribe(thermostat, "thermostat", changeHandler)
    subscribe(forecast, "temperature", changeHandler)
    subscribe(forecast, "shortForecast", changeHandler)
    subscribe(wxDevice, "rainToday", changeHandler)
    alarmSensor.tickler()
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
  path("/setswitch/:command") {
  	action: [
    	GET: "setSwitch"
    ]
  }
  path("/setdoor/:command") {
  	action: [
    	GET: "setDoor"
    ]
  }
  path("/setlock/:command") {
  	action: [
    	GET: "setLock"
    ]
  }
  path("/getstatus") {
  	action: [
    	GET: "getStatus"
    ]
  }
}

def changeHandler(evt) {
	log.debug "changeHandler(${evt.value})"
    alarmSensor.tickler()
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

def setLock() {
	log.debug "setLock(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def idx = command.indexOf("=")
    log.debug "idx = ${idx}"
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    locks.each {lock ->
    	log.debug lock
        dn = "${lock}"
        if (command.contains(dn)) {
        	log.debug "${lock} Found! / ${sv}"
            switch (sv) {
            	case "LOCK":
                	lock.lock()
                	break
                case "UNLOCK":
                	lock.unlock()
                	break
            }
        }
    }

    resp << [name: "Command", value: command]
    log.debug "resp: ${resp}"
	return resp
}

def setDoor() {
	log.debug "setDoor(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def idx = command.indexOf("=")
    log.debug "idx = ${idx}"
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    doors.each {door ->
    	log.debug door
        dn = "${door}"
        if (command.contains(dn)) {
        	log.debug "${door} Found! / ${sv}"
            switch (sv) {
            	case "OPEN":
                	door.open()
                	break
                case "CLOSE":
                	door.close()
                	break
            }
        }
    }

    resp << [name: "Command", value: command]
    log.debug "resp: ${resp}"
	return resp
}

def setSwitch() {
	log.debug "setSwitch(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def idx = command.indexOf("=")
    log.debug "idx = ${idx}"
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    lights.each {dev ->
    	log.debug dev
        dn = "${dev}"
        if (command.contains(dn)) {
        	log.debug "${dev} Found! / ${sv}"
            switch (sv) {
            	case "ON":
                	dev.on()
                	break
                case "OFF":
                	dev.off()
                	break
            }
        }
    }

    resp << [name: "Command", value: command]
    log.debug "resp: ${resp}"
	return resp
}

def getStatus() {
	log.debug "getStatus()"

	def t_name = ""
    def t_val = ""
    def resp = []
    log.debug "Alarm State: ${alarmSensor.currentValue("alarmState")}"
    resp << [name: "alarm", value: "Alarm Sensor"]
    resp << [name: "val", value: alarmSensor.currentValue("alarmState")]
    log.debug "Thermostat: ${thermostat?.displayName} / ${thermostat?.currentValue("temperature")} / ${thermostat?.currentValue("thermostatMode")}"
    log.debug "heatingSetpoint: ${thermostat?.currentValue("heatingSetpoint")} / coolingSetpoint: ${thermostat?.currentValue("coolingSetpoint")}"
    resp << [name: "thermostat", value: thermostat.displayName]
    resp << [name: "val", value: thermostat.currentValue("thermostatMode")]
    resp << [name: "temp", value: "current temp"]
    resp << [name: "val", value: thermostat.currentValue("temperature")]
    resp << [name: "heat", value: "heatingSetPoint"]
    resp << [name: "val", value: thermostat.currentValue("heatingSetpoint")]
    resp << [name: "cool", value: "coolingSetPoint"]
    resp << [name: "val", value: thermostat.currentValue("coolingSetpoint")]
    log.debug "Outside Temp: ${forecast.displayName}: ${forecast.currentValue("temperature")} / Forecast: ${forecast.currentValue("shortForecast")} / Long: ${forecast.currentValue("forecast")}"
	resp << [name: "weather", value: forecast.displayName]
    resp << [name: "val", value: forecast.currentValue("temperature")]
    resp << [name: "forecast", value: forecast.currentValue("shortForecast")]
    resp << [name: "val", value: forecast.currentValue("forecast")]
    log.debug "Rain Today: ${wxDevice.currentValue("rainToday")}"
    resp << [name: "wxDevice", value: "Rain Gauge"]
    resp << [name: "val", value: wxDevice.currentValue("rainToday")]

	lights.each {dev ->
    	t_name = "${dev}"
        t_val = dev.currentValue("switch")
        log.debug "t_name: ${t_name}"
        log.debug "t_val: ${t_val}"
        resp << [name: "switch", value: t_name]
        resp << [name: "val", value: t_val]
    }
    doors.each {door ->
    	t_name = "${door}"
        t_val = door.currentValue("door")
        log.debug "t_name: ${t_name}"
        log.debug "t_val: ${t_val}"
        resp << [name: "door", value: t_name]
        resp << [name: "val", value: t_val]
    }
    locks.each {lock ->
    	t_name = "${lock}"
        t_val = lock.currentValue("lock")
        log.debug "t_name: ${t_name}"
        log.debug "t_val: ${t_val}"
        resp << [name: "lock", value: t_name]
        resp << [name: "val", value: t_val]
    }
    contacts.each {contact ->
    	t_name = "${contact}"
        t_val = contact.currentValue("contact")
        log.debug "t_name: ${t_name}"
        log.debug "t_val: ${t_val}"
        resp << [name: "contact", value: t_name]
        resp << [name: "val", value: t_val]
    }
    return resp
}
