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
    	input "siren", "capability.alarm", required: false, title: "Select Alarm Siren/Strobe."
    	input "alarmSensor", "device.fryguyAlarmController", required: true, title: "Select Alarm Controller."
        input "thermostat", "capability.thermostat", required: false, title: "Select Thermostat."
        input "lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches."
        input "lightsIgnore", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches to Ignore OFFLINE Status."
        input "doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls."
        input "locks", "capability.lock", required: false, multiple: true, title: "Select Locks."
        input "contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contact Sensors."
    	input "forecast", "device.mySmartweatherTile", required: false, title: "Select Forecast Provider."
        input "wxDevice", "device.fryguypiWxDevice", required: false, title: "Select Weather Device."
        input "jeff", "capability.presenceSensor", required: false, title: "Select Jeff's Phone."
        input "cyndi", "capability.presenceSensor", required: false, title: "Select Cyndi's Phone."
        input "dee", "device.myTrackingSensor", required: false, title: "Select Tracking Sensor."
        input "disableWhenAway", "bool", required: true, title: "Disable Monitor When Everyone Is Away?"
        input "awayList", "capability.presenceSensor", required: false, multiple: true, title: "Disable When Who Is Away?"
    }
    section ("Garage") {
    	input "garage_display", "text", required: false, title: "Enter Display Name for Garage."
    	input "garage_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Garage."
        input "garage_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Garage."
        input "garage_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Garage."
        input "garage_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Garage."
        input "garage_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Garage."
    }
    section ("Living Room") {
    	input "lr_display", "text", required: false, title: "Enter Display Name for Living Room."
    	input "lr_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Living Room."
        input "lr_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Living Room."
        input "lr_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Living Room."
        input "lr_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Living Room."
        input "lr_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Living Room."
    }
    section ("Kitchen") {
    	input "kitchen_display", "text", required: false, title: "Enter Display Name for Kitchen."
    	input "kitchen_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Kitchen."
        input "kitchen_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Kitchen."
        input "kitchen_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Kitchen."
        input "kitchen_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Kitchen."
        input "kitchen_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Kitchen."
    }
    section ("Outside") {
    	input "outside_display", "text", required: false, title: "Enter Display Name for Outside."
    	input "outside_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Outside."
        input "outside_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Outside."
        input "outside_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Outside."
        input "outside_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Outside."
        input "outside_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Outside."
    }
    section ("Basement") {
    	input "basement_display", "text", required: false, title: "Enter Display Name for Basement."
    	input "basement_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Basement."
        input "basement_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Basement."
        input "basement_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Basement."
        input "basement_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Basement."
        input "basement_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Basement."
    }
    section ("Family Room") {
    	input "fr_display", "text", required: false, title: "Enter Display Name for Family Room."
    	input "fr_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Family Room."
        input "fr_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Family Room."
        input "fr_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Family Room."
        input "fr_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Family Room."
        input "fr_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Family Room."
    }
    section ("Master") {
    	input "master_display", "text", required: false, title: "Enter Display Name for Master."
    	input "master_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Master."
        input "master_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Master."
        input "master_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Master."
        input "master_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Master."
        input "master_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Master."
        input "master_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Master."
    }
    section ("Master Bath") {
    	input "mb_display", "text", required: false, title: "Enter Display Name for Master Bath."
    	input "mb_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Master Bath."
        input "mb_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Master Bath."
        input "mb_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Master Bath."
        input "mb_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Master Bath."
        input "mb_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Master Bath."
        input "mb_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Master Bath."
    }
    section ("Bedroom1") {
    	input "br1_display", "text", required: false, title: "Enter Display Name for Bedroom1."
    	input "br1_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom1."
        input "br1_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom1."
        input "br1_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom1."
        input "br1_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom1."
        input "br1_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom1."
    }
    section ("Bedroom2") {
    	input "br2_display", "text", required: false, title: "Enter Display Name for Bedroom1."
    	input "br2_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom2."
        input "br2_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom2."
        input "br2_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom2."
        input "br2_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom2."
        input "br2_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom2."
    }
    section ("Bedroom3") {
    	input "br3_display", "text", required: false, title: "Enter Display Name for Bedroom3."
    	input "br3_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom3."
        input "br3_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom3."
        input "br3_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom3."
        input "br3_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom3."
        input "br3_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom3."
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false, title: "Send a Push Notification When Things Happen?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false, title: "Send a Text Message When Things Happen?"
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
    
    state.colorBulbs = null
    
    subscribe(lights, "switch", changeHandler)
    subscribe(lights, "level", changeHandler)
    subscribe(lights, "color", changeHandler)
    subscribe(doors, "door", changeHandler)
    subscribe(locks, "lock", changeHandler)
    subscribe(contacts, "contact", changeHandler)
    subscribe(alarmSensor, "alarmState", changeHandler)
    subscribe(thermostat, "thermostat", changeHandler)
    subscribe(thermostat, "thermostatOperatingState", changeHandler)
    subscribe(thermostat, "temperature", changeHandler)
    subscribe(forecast, "temperature", changeHandler)
    subscribe(forecast, "weather", changeHandler)
    subscribe(forecast, "forecast", changeHandler)
    subscribe(forecast, "feelsLike", changeHandler)
    subscribe(forecast, "percentPrecip", changeHandler)
    subscribe(forecast, "humidity", changeHandler)
    subscribe(forecast, "rainToday", changeHandler)
    subscribe(jeff, "presence", changeHandler)
    subscribe(cyndi, "presence", changeHandler)
    subscribe(dee, "currentLocation", changeHandler)
    subscribe(app, appHandler)

	subscribe(garage_lights, "switch", changeHandler)
    subscribe(garage_doors, "door", changeHandler)
    subscribe(garage_locks, "lock", changeHandler)
    subscribe(garage_contacts, "contact", changeHandler)
    subscribe(garage_temps, "temperature", changeHandler)

	subscribe(lr_lights, "switch", changeHandler)
    subscribe(lr_doors, "door", changeHandler)
    subscribe(lr_locks, "lock", changeHandler)
    subscribe(lr_contacts, "contact", changeHandler)
    subscribe(lr_temps, "temperature", changeHandler)

	subscribe(kitchen_lights, "switch", changeHandler)
    subscribe(kitchen_doors, "door", changeHandler)
    subscribe(kitchen_locks, "lock", changeHandler)
    subscribe(kitchen_contacts, "contact", changeHandler)
    subscribe(kitchen_temps, "temperature", changeHandler)

	subscribe(outside_lights, "switch", changeHandler)
    subscribe(outside_doors, "door", changeHandler)
    subscribe(outside_locks, "lock", changeHandler)
    subscribe(outside_contacts, "contact", changeHandler)
    subscribe(outside_temps, "temperature", changeHandler)

	subscribe(basement_lights, "switch", changeHandler)
    subscribe(basement_doors, "door", changeHandler)
    subscribe(basement_locks, "lock", changeHandler)
    subscribe(basement_contacts, "contact", changeHandler)
    subscribe(basement_temps, "temperature", changeHandler)

	subscribe(fr_lights, "switch", changeHandler)
    subscribe(fr_doors, "door", changeHandler)
    subscribe(fr_locks, "lock", changeHandler)
    subscribe(fr_contacts, "contact", changeHandler)
    subscribe(fr_temps, "temperature", changeHandler)

	subscribe(master_lights, "switch", changeHandler)
    subscribe(master_doors, "door", changeHandler)
    subscribe(master_locks, "lock", changeHandler)
    subscribe(master_contacts, "contact", changeHandler)
    subscribe(master_temps, "temperature", changeHandler)
    subscribe(master_humid, "humidity", changeHandler)

	subscribe(mb_lights, "switch", changeHandler)
    subscribe(mb_doors, "door", changeHandler)
    subscribe(mb_locks, "lock", changeHandler)
    subscribe(mb_contacts, "contact", changeHandler)
    subscribe(mb_temps, "temperature", changeHandler)
    subscribe(mb_humid, "humidity", changeHandler)

	subscribe(br1_lights, "switch", changeHandler)
    subscribe(br1_doors, "door", changeHandler)
    subscribe(br1_locks, "lock", changeHandler)
    subscribe(br1_contacts, "contact", changeHandler)
    subscribe(br1_temps, "temperature", changeHandler)

	subscribe(br2_lights, "switch", changeHandler)
    subscribe(br2_doors, "door", changeHandler)
    subscribe(br2_locks, "lock", changeHandler)
    subscribe(br2_contacts, "contact", changeHandler)
    subscribe(br2_temps, "temperature", changeHandler)

	subscribe(br3_lights, "switch", changeHandler)
    subscribe(br3_doors, "door", changeHandler)
    subscribe(br3_locks, "lock", changeHandler)
    subscribe(br3_contacts, "contact", changeHandler)
    subscribe(br3_temps, "temperature", changeHandler)

	alarmSensor.tickler()
    runEvery5Minutes(changeHandler)
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
  path("/getdoor/:command") {
  	action: [
    	GET: "getDoor"
    ]
  }
  path("/setlock/:command") {
  	action: [
    	GET: "setLock"
    ]
  }
  path("/set_temp/:command") {
  	action: [
    	GET: "setTemp"
    ]
  }
  path("/set_color_bulb/:command") {
  	action: [
    	GET: "setColorBulb"
    ]
  }
  path("/getstatus") {
  	action: [
    	GET: "getStatus"
    ]
  }
}

def appHandler(evt) {
	log.debug "appHandler"
    
    log.debug "Siren Cmds: ${siren.getSupportedCommands()}"
    def capabilities = siren.capabilities
    for (cap in capabilities) {
        log.debug "cap: ${cap}"
    }
    log.debug "Status: ${siren.currentValue("alarm")}"
    if (siren.currentValue("alarm") == "off") {
    	siren.strobe()
    }
    else {
    	siren.off()
    }
}

def changeHandler(evt) {
	log.debug "changeHandler()"
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
    def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nDisarm Alarm:\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
    return resp
}

def setColorBulb() {
	/* JDF - Needs updated with new command params!*/

	log.debug "setColorBulb(${params.command})"
    def resp = []
    def command = params.command
    def bulbFound = -1
    def cmd = ""
    def bulb = ""
    def color = ""
    def dim = ""
    def dn = ""
    def hue = ""
    def saturation = ""
    def curDate = new Date()
    
    cmd = command.split('&')
    for (String value : cmd) {
        if (value.contains("name")) {
            def nCmd = [] 
            nCmd = value.split('=')
            log.debug "name = ${nCmd[1]}"
            bulb = nCmd[1].trim()
        }

        else if (value.contains("color")) {
			def cCmd = []
            cCmd = value.split('=')
            log.debug "color = ${cCmd[1]}"
            color = cCmd[1].trim()
            color.toLowerCase()
    /* white: hue: 56, saturation: 2
       red: hue: 2, saturation: 94
       blue: hue: 67, saturation: 92
       green: hue: 28, saturation: 98
    */
            switch (color) {
            	case "white":
                	hue = 56
                    saturation = 2
                	break
                case "red":
                	hue = 2
                    saturation = 94
                	break
                case "blue":
                	hue = 67
                    saturation = 92
                	break
                case "green":
                	hue = 28
                    saturation = 98
                	break
            }
        }

        else if (value.contains("dim")) {
			def dCmd = []
            dCmd = value.split('=')
            log.debug "dim = ${dCmd[1]}"
            dim = dCmd[1].trim()
        }
    }
    

	log.debug "Searching for ${bulb}..."
    if (!state.colorBulbs) {
    	log.debug "state.colorBulbs not defined.  Initializing..."
        state.colorBulbs = []
    }
    lights.each {dev ->
        dn = "${dev}".trim()
        if (bulb == dn) {
        	log.debug "${bulb} Found!"
            bulbFound = -1

			for (int x = 0; x < state.colorBulbs.size(); x++) {
            	if (state.colorBulbs[x].name == bulb) {
                  bulbFound = x
                  break
                }
            }
            if (bulbFound == -1) {
            	state.colorBulbs << [name: bulb, hue: hue, saturation: saturation, dim: dim]
            }
            else {
              log.debug "Already exists!"
              state.colorBulbs[bulbFound].hue = hue
              state.colorBulbs[bulbFound].saturation = saturation
              state.colorBulbs[bulbFound].dim = dim
            }
            
            log.debug "colorBulbs: ${state.colorBulbs}"
            def capabilities = dev.capabilities
            for (cap in capabilities) {
            	if (cap.name == "Color Control") {
                	dev?.setColor([hue: hue, saturation: saturation])
                    dev?.setLevel(dim.toInteger())
                }
            }
        }
    }
    resp << [name: "Command", value: command]

	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetColorBulb: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }

    return resp
}

def setTemp() {
    log.debug "setTemp(${params.command})"
    def resp = []
    def cmd = []
    def mode = ""
    def currMode = ""
    def temp = ""
    def currCoolingSetpoint = ""
    def currHeatingSetpoint = ""
    def command = params.command
    def curDate = new Date()
    resp << [name: "Command", value: command]
    cmd = command.split('&')
    for (String value : cmd) {
        if (value.contains("temp")) {
            def tCmd = [] 
            tCmd = value.split('=')
            log.debug "temp = ${tCmd[1]}"
            temp = tCmd[1].trim()
        }

        else if (value.contains("mode")) {
			def mCmd = []
            mCmd = value.split('=')
            log.debug "mode = ${mCmd[1]}"
            mode = mCmd[1].trim()
        }
    }

	currCoolingSetpoint = thermostat.currentValue("coolingSetpoint")
	currHeatingSetpoint = thermostat.currentValue("heatingSetpoint")
    currMode = thermostat.currentValue("thermostatMode")
    log.debug "CurrMode: ${currMode}"
	if (mode == "cool") {
    	if (currMode != "cool") {
        	log.debug "Changing mode to ${mode}"
        	thermostat.setThermostatMode(mode)
        }
        else if (currCoolingSetpoint.toInteger() != temp.toInteger()) {
            log.debug "CurrTemp: ${currCoolingSetpoint} / Changing temp to ${temp}"
            thermostat.setCoolingSetpoint(temp.toInteger())
        }
    }
    else if (mode == "heat") {
    	if (currMode != "heat") {
        	log.debug "Changing mode to ${mode}"
        	thermostat.setThermostatMode(mode)
        }
        else if (currHeatingSetpoint.toInteger() != temp.toInteger()) {
	    	thermostat.setHeatingSetpoint(temp.toInteger())
        	log.debug "Changing temp to ${temp}"
        }
    }
    else if (mode == "off") {
    	if (currMode != "off") {
        	log.debug "Changing mode to ${mode}"
        	thermostat.setThermostatMode(mode)
        }
    }
	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetTemp: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
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
	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetArmed: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
    return resp
}

def setLock() {
	log.debug "setLock(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def allLock = false
    def idx = command.indexOf("=")
    def curDate = new Date()
    log.debug "idx = ${idx}"
    
	def dl = garage_locks
    def roomIdx = command.indexOf("_")
	def room = ""

	if (roomIdx > -1) {
    	room = command.substring(0, roomIdx)
        log.debug "room: [${room}]"
        def newcommand = command.substring(roomIdx+1)
        log.debug "newcommand: [${newcommand}]"
	}

	if (room == garage_display) {
    	dl = garage_locks
    }
    else if (room == lr_display) {
    	dl = lr_locks
    }
    else if (room == kitchen_display) {
    	dl = kitchen_locks
    }
    else if (room == outside_display) {
    	dl = outside_locks
    }
    else if (room == basement_display) {
    	dl = basement_locks
    }
    else if (room == fr_display) {
    	dl = fr_locks
    }
    else if (room == master_display) {
    	dl = master_locks
    }
    else if (room == mb_display) {
    	dl = mb_locks
    }
    else if (room == br1_display) {
    	dl = br1_locks
    }
    else if (room == br2_display) {
    	dl = br2_locks
    }
    else if (room == br3_display) {
    	dl = br3_locks
    }
        
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    if (command.contains("all=lock")) {
    	allLock = true
    }
    
    dl.each {lock ->
    	log.debug lock
        dn = "${lock}"
        if (command.contains(dn) || allLock) {
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
	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetLock: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
	return resp
}

/* JDF - Can this be removed?
def getDoor() {
	log.debug "getDoor(${params.command})"
    def resp = []
    def command = params.command

	doors.each {door ->
    	if (command.contains("${door}")) {
        	resp << [name: "${door}", value: door.currentValue("door")]
        }
    }
    if (resp.size() == 0) {
    	resp << [name: "${door}", value: "UNK"]
    }
    log.debug "Door Status Resp: ${resp}"
    return resp
}
*/

def setDoor() {
	log.debug "setDoor(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def allClose = false
    def idx = command.indexOf("=")
    def curDate = new Date()
    log.debug "idx = ${idx}"

	def dl = garage_doors
    def roomIdx = command.indexOf("_")
	def room = ""

	if (roomIdx > -1) {
    	room = command.substring(0, roomIdx)
        log.debug "room: [${room}]"
        def newcommand = command.substring(roomIdx+1)
        log.debug "newcommand: [${newcommand}]"
	}

	if (room == garage_display) {
    	dl = garage_doors
    }
    else if (room == lr_display) {
    	dl = lr_doors
    }
    else if (room == kitchen_display) {
    	dl = kitchen_doors
    }
    else if (room == outside_display) {
    	dl = outside_doors
    }
    else if (room == basement_display) {
    	dl = basement_doors
    }
    else if (room == fr_display) {
    	dl = fr_doors
    }
    else if (room == master_display) {
    	dl = master_doors
    }
    else if (room == mb_display) {
    	dl = mb_doors
    }
    else if (room == br1_display) {
    	dl = br1_doors
    }
    else if (room == br2_display) {
    	dl = br2_doors
    }
    else if (room == br3_display) {
    	dl = br3_doors
    }
        
	if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    if (command.contains("all=close")) {
    	allClose = true
    }
    
    dl.each {door ->
    	log.debug dl
        dn = "${door}"
        if (command.contains(dn) || allClose) {
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
	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetDoor: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
	return resp
}

def setSwitch() {
	log.debug "setSwitch(${params.command})"
    
    def resp = []
    def command = params.command
    def dn = ""
    def sv = ""
    def allOff = false
    def idx = command.indexOf("=")
    def curDate = new Date()
    log.debug "idx = ${idx}"
    
	def dl = garage_lights
    def roomIdx = command.indexOf("_")
	def room = ""

	if (roomIdx > -1) {
    	room = command.substring(0, roomIdx)
        log.debug "room: [${room}]"
        def newcommand = command.substring(roomIdx+1)
        log.debug "newcommand: [${newcommand}]"
	}

	if (room == garage_display) {
    	dl = garage_lights
    }
    else if (room == lr_display) {
    	dl = lr_lights
    }
    else if (room == kitchen_display) {
    	dl = kitchen_lights
    }
    else if (room == outside_display) {
    	dl = outside_lights
    }
    else if (room == basement_display) {
    	dl = basement_lights
    }
    else if (room == fr_display) {
    	dl = fr_lights
    }
    else if (room == master_display) {
    	dl = master_lights
    }
    else if (room == mb_display) {
    	dl = mb_lights
    }
    else if (room == br1_display) {
    	dl = br1_lights
    }
    else if (room == br2_display) {
    	dl = br2_lights
    }
    else if (room == br3_display) {
    	dl = br3_lights
    }
        

	if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
	if (command.contains("all=off")) {
    	allOff = true
    }


    dl.each {dev ->
    	log.debug dev
        dn = "${dev}"
        if (command.contains(dn) || allOff) {
        	log.debug "${dev} Found! / ${sv}"
            switch (sv) {
            	case "ON":
                	dev.on()
                    for (int x = 0; x < state?.colorBulbs?.size(); x++) {
                    	if (state.colorBulbs[x].name == dn) {
                        	dev?.setColor([hue: state.colorBulbs[x].hue, saturation: state.colorBulbs[x].saturation])
                            dev?.setLevel(state.colorBulbs[x].dim)
                        }
                    }
                	break
                case "OFF":
                	dev.off()
                	break
            }
        }
    }

    resp << [name: "Command", value: command]
    log.debug "resp: ${resp}"
	def msg = "HA Notification: ${curDate.format("MM/dd/yy h:mm:ss a", location.timeZone)}\n\nSetSwitch: ${command}\n${resp}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
	return resp
}

def getStatus() {
	log.debug "getStatus()"

	def t_name = ""
    def t_val = ""
    def t_hue = ""
    def t_saturation = ""
    def t_dim = ""
    def cVal = ""
    def dVal = ""

	def respLights = []
    def lightsVals = []
    def respDoors = []
    def respLocks = []
    def respContacts = []
    def resp = []

	def garageLights = []
    def garageDoors = []
    def garageLocks = []
    def garageContacts = []
    def garageTemps = []

	def lrLights = []
    def lrDoors = []
    def lrLocks = []
    def lrContacts = []
    def lrTemps = []

	def kitchenLights = []
    def kitchenDoors = []
    def kitchenLocks = []
    def kitchenContacts = []
    def kitchenTemps = []

	def outsideLights = []
    def outsideDoors = []
    def outsideLocks = []
    def outsideContacts = []
    def outsideTemps = []

	def basementLights = []
    def basementDoors = []
    def basementLocks = []
    def basementContacts = []
    def basementTemps = []

	def frLights = []
    def frDoors = []
    def frLocks = []
    def frContacts = []
    def frTemps = []

	def masterLights = []
    def masterColorLights = []
    def masterDoors = []
    def masterLocks = []
    def masterContacts = []
    def masterTemps = []
    def masterHumid = []

	def mbLights = []
    def mbDoors = []
    def mbLocks = []
    def mbContacts = []
    def mbTemps = []
    def mbHumid = []

	def br1Lights = []
    def br1Doors = []
    def br1Locks = []
    def br1Contacts = []
    def br1Temps = []

	def br2Lights = []
    def br2Doors = []
    def br2Locks = []
    def br2Contacts = []
    def br2Temps = []

	def br3Lights = []
    def br3Doors = []
    def br3Locks = []
    def br3Contacts = []
    def br3Temps = []

	def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",location.currentValue("sunriseTime"))
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",location.currentValue("sunsetTime"))
	def sunrise = sunriseTime.format("EEE h:mm a", location.timeZone)
    def sunset = sunsetTime.format("EEE h:mm a", location.timeZone)
    def sunTime = ""
	log.debug "Sunset: ${sunset} / Sunrise: ${sunrise}"

	if (sunriseTime < sunsetTime) {
    	sunTime = "Next Sunrise: ${sunrise}  /  Sunset: ${sunset}"
    }
    else {
    	sunTime = "Next Sunset: ${sunset}  /  Sunrise: ${sunrise}"
    }
    
	resp << [name: "sunrise", value: "Sunrise"]
    resp << [name: "val", value: sunrise]
    resp << [name: "sunset", value: "Sunset"]
    resp << [name: "val", value: sunset]
	
    resp << [name: "suntime", value: "Next Sunrise and Sunset"]
    resp << [name: "val", value: sunTime]

    resp << [name: "alarm", value: "Alarm Sensor"]
    resp << [name: "val", value: alarmSensor.currentValue("alarmState")]
    resp << [name: "alert", value: "Alert State"]
    resp << [name: "val", value: alarmSensor.currentValue("alertState")]
 
 	if (thermostat) {
	    resp << [name: "thermostat", value: thermostat.displayName]
    	resp << [name: "val", value: thermostat.currentValue("thermostatMode")]
        resp << [name: "temp", value: "current temp"]
        resp << [name: "val", value: thermostat.currentValue("temperature")]
        resp << [name: "heat", value: "heatingSetPoint"]
        resp << [name: "val", value: thermostat.currentValue("heatingSetpoint")]
        resp << [name: "cool", value: "coolingSetPoint"]
        resp << [name: "val", value: thermostat.currentValue("coolingSetpoint")]
        resp << [name: "thermostatOperatingState", value: "operating state"]
        resp << [name: "val", value: thermostat.currentValue("thermostatOperatingState")]
        resp << [name: "insideHumidity", value: "Inside Humidity"]
        resp << [name: "val", value: thermostat.currentValue("humidity")]
	}
    
	resp << [name: "weather", value: forecast.displayName]
    resp << [name: "val", value: forecast.currentValue("temperature")]
    resp << [name: "forecast", value: forecast.currentValue("weather")]
    resp << [name: "val", value: forecast.currentValue("forecast")]
    resp << [name: "wxDevice", value: "Rain Gauge"]
    resp << [name: "val", value: forecast.currentValue("rainToday")]
    resp << [name: "feelsLike", value: "Feels Like"]
    resp << [name: "val", value: forecast.currentValue("feelsLike")]
    resp << [name: "percentPrecip", value: "Chance of Precip"]
    resp << [name: "val", value: forecast.currentValue("percentPrecip")]
    resp << [name: "humidity", value: "Humidity"]
    resp << [name: "val", value: forecast.currentValue("humidity")]
/*
    resp << [name: "dee", value: "Dee's Location"]
    resp << [name: "val", value: dee.currentValue("currentLocation")]
*/    
    resp << [name: "jeff", value: "Jeff's Location"]
    resp << [name: "val", value: jeff.currentPresence]
    
    resp << [name: "cyndi", value: "Cyndi's Location"]
    resp << [name: "val", value: cyndi.currentPresence]

	resp << [name: "disable", value: "Disable When Away"]
    resp << [name: "val", value: disableWhenAway]
    
    def strAwayList = ""
    awayList.each { al ->
    	if (strAwayList) {
        	strAwayList = strAwayList + ", "
        }
    	strAwayList = strAwayList + "${al}"
    }
    resp << [name: "awayList", value: "Disable When Away List"]
    resp << [name: "val", value: strAwayList]

    if (!state.colorBulbs) {
    	log.debug "state.colorBulbs not defined.  Initializing..."
        state.colorBulbs = []
    }

/* Garage */
	def itemCnt = 0

	garage_lights.each {gl ->
    	t_name = "${gl}"
        if (gl.getStatus() != "OFFLINE") {
	        t_val = gl.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		garageLights << [name: t_name, value: t_val]
    }
    
    garage_doors.each {gd ->
    	t_name = "${gd}"
        if (gd.getStatus() != "OFFLINE") {
	        t_val = gd.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        garageDoors << [name: t_name, value: t_val]
    }

    garage_locks.each {gl ->
    	t_name = "${gl}"
        if (gl.getStatus() != "OFFLINE") {
	        t_val = gl.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        garageLocks << [name: t_name, value: t_val]
    }

    garage_contacts.each {gc ->
    	t_name = "${gc}"
        if (gc.getStatus() != "OFFLINE") {
	        t_val = gc.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        garageContacts << [name: t_name, value: t_val]
    }

    garage_temps.each {gt ->
    	t_name = "${gt}"
        t_val = gt.currentValue("temperature")
        itemCnt++
        garageTemps << [name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Garage"
        if (garage_display?.size() > 0) {
            t_val = garage_display
        }
        resp << [name: "garage_display", value: t_val]
        resp << [name: "val", value: t_val]

        garageLights.sort{it.name}
        garageLights.each {gl ->
            resp << [name: "garage_switch", value: gl.name]
            resp << [name: "val", value: gl.value]
        }

        garageDoors.sort{it.name}
        garageDoors.each {gd ->
            resp << [name: "garage_door", value: gd.name]
            resp << [name: "val", value: gd.value]
        }

        garageLocks.sort{it.name}
        garageLocks.each {gl ->
            resp << [name: "garage_lock", value: gl.name]
            resp << [name: "val", value: gl.value]
        }

        garageContacts.sort{it.name}
        garageContacts.each {gc ->
            resp << [name: "garage_contact", value: gc.name]
            resp << [name: "val", value: gc.value]
        }

        garageTemps.sort{it.name}
        garageTemps.each {gt ->
            resp << [name: "garage_temp", value: gt.name]
            resp << [name: "val", value: gt.value]
        }
    }

/* Living Room */
	itemCnt = 0
    
    lr_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		lrLights << [name: t_name, value: t_val]
    }
    
    lr_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        lrDoors << [name: t_name, value: t_val]
    }

    lr_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        lrLocks << [name: t_name, value: t_val]
    }

    lr_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        lrContacts << [name: t_name, value: t_val]
    }

    lr_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        lrTemps << [name: t_name, value: t_val]
    }

	if (itemCnt > 0) {
        t_val = "Living Room"
        if (lr_display?.size() > 0) {
            t_val = lr_display
        }
        resp << [name: "lr_display", value: t_val]
        resp << [name: "val", value: t_val]

        lrLights.sort{it.name}
        lrLights.each {lr ->
            resp << [name: "lr_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        lrDoors.sort{it.name}
        lrDoors.each {lr ->
            resp << [name: "lr_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        lrLocks.sort{it.name}
        lrLocks.each {lr ->
            resp << [name: "lr_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        lrContacts.sort{it.name}
        lrContacts.each {lr ->
            resp << [name: "lr_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        lrTemps.sort{it.name}
        lrTemps.each {lr ->
            resp << [name: "lr_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Kitchen */
	itemCnt = 0
    
    kitchen_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		kitchenLights << [name: t_name, value: t_val]
    }
    
    kitchen_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        kitchenDoors << [name: t_name, value: t_val]
    }

    kitchen_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        kitchenLocks << [name: t_name, value: t_val]
    }

    kitchen_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        kitchenContacts << [name: t_name, value: t_val]
    }

    kitchen_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        lrTemps << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Kitchen"
        if (kitchen_display?.size() > 0) {
            t_val = kitchen_display
        }
        resp << [name: "kitchen_display", value: t_val]
        resp << [name: "val", value: t_val]

        kitchenLights.sort{it.name}
        kitchenLights.each {lr ->
            resp << [name: "kitchen_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        kitchenDoors.sort{it.name}
        kitchenDoors.each {lr ->
            resp << [name: "kitchen_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        kitchenLocks.sort{it.name}
        kitchenLocks.each {lr ->
            resp << [name: "kitchen_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        kitchenContacts.sort{it.name}
        kitchenContacts.each {lr ->
            resp << [name: "kitchen_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        kitchenTemps.sort{it.name}
        kitchenTemps.each {lr ->
            resp << [name: "kitchen_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Outside */
	itemCnt = 0
    
    outside_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		outsideLights << [name: t_name, value: t_val]
    }
    
    outside_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
            t_val = "OFFLINE"
        }
        itemCnt++
        outsideDoors << [name: t_name, value: t_val]
    }

    outside_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        outsideLocks << [name: t_name, value: t_val]
    }

    outside_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        outsideContacts << [name: t_name, value: t_val]
    }

    outside_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        outsideTemps << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Outside"
        if (outside_display?.size() > 0) {
            t_val = outside_display
        }
        resp << [name: "outside_display", value: t_val]
        resp << [name: "val", value: t_val]

        outsideLights.sort{it.name}
        outsideLights.each {lr ->
            resp << [name: "outside_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        outsideDoors.sort{it.name}
        outsideDoors.each {lr ->
            resp << [name: "outside_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        outsideLocks.sort{it.name}
        outsideLocks.each {lr ->
            resp << [name: "outside_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        outsideContacts.sort{it.name}
        outsideContacts.each {lr ->
            resp << [name: "outside_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        outsideTemps.sort{it.name}
        outsideTemps.each {lr ->
            resp << [name: "outside_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Basement */
	itemCnt = 0
    
    basement_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		basementLights << [name: t_name, value: t_val]
    }
    
    basement_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        basementDoors << [name: t_name, value: t_val]
    }

    basement_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        basementLocks << [name: t_name, value: t_val]
    }

    basement_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        basementContacts << [name: t_name, value: t_val]
    }

    basement_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        basementTemps << [name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Basement"
        if (basement_display?.size() > 0) {
            t_val = basement_display
        }
        resp << [name: "basement_display", value: t_val]
        resp << [name: "val", value: t_val]

        basementLights.sort{it.name}
        basementLights.each {lr ->
            resp << [name: "basement_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        basementDoors.sort{it.name}
        basementDoors.each {lr ->
            resp << [name: "basement_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        basementLocks.sort{it.name}
        basementLocks.each {lr ->
            resp << [name: "basement_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        basementContacts.sort{it.name}
        basementContacts.each {lr ->
            resp << [name: "basement_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        basementTemps.sort{it.name}
        basementTemps.each {lr ->
            resp << [name: "basement_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Family Room */
	itemCnt = 0
    
    fr_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		frLights << [name: t_name, value: t_val]
    }
    
    fr_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        frDoors << [name: t_name, value: t_val]
    }

    fr_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        frLocks << [name: t_name, value: t_val]
    }

    fr_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        frContacts << [name: t_name, value: t_val]
    }

    fr_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        frTemps << [name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Family Room"
        if (fr_display?.size() > 0) {
            t_val = fr_display
        }
        resp << [name: "fr_display", value: t_val]
        resp << [name: "val", value: t_val]

        frLights.sort{it.name}
        frLights.each {lr ->
            resp << [name: "fr_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        frDoors.sort{it.name}
        frDoors.each {lr ->
            resp << [name: "fr_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        frLocks.sort{it.name}
        frLocks.each {lr ->
            resp << [name: "fr_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        frContacts.sort{it.name}
        frContacts.each {lr ->
            resp << [name: "fr_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        frTemps.sort{it.name}
        frTemps.each {lr ->
            resp << [name: "fr_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Master */
	itemCnt = 0
    
    master_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		masterLights << [name: t_name, value: t_val]

		def bulbFound = -1
        def colorBulb = false
        
        t_hue = lr?.currentValue("hue")
        if (t_hue) {
        	colorBulb = true

			bulbFound = -1
            for (int x = 0; x < state?.colorBulbs?.size(); x++) {
                if (state.colorBulbs[x].name == t_name) {
                    bulbFound = x
                    break
                }
            }
            if (bulbFound == -1) {
            	t_hue = lr?.currentValue("hue")
                t_saturation = lr?.currentValue("saturation")
                t_dim = lr?.currentValue("level")
            	state.colorBulbs << [name: t_name, hue: t_hue, saturation: t_saturation, dim: t_dim]
            }
            else {
                t_hue = state.colorBulbs[bulbFound].hue
                t_saturation = state.colorBulbs[bulbFound].saturation
                t_dim = state.colorBulbs[bulbFound].dim
                if (!t_dim) {
                    t_dim = 37
                }
            }
        /* white: hue: 56, saturation: 2
           red: hue: 2, saturation: 94
           blue: hue: 67, saturation: 92
           green: hue: 28, saturation: 98
        */
            if (t_hue == 2 && t_saturation == 94) {
                cVal = "red"
            }
            else if (t_hue == 67 && t_saturation == 92) {
                cVal = "blue"
            }
            else if (t_hue == 28 && t_saturation == 98) {
                cVal = "green"
            }
            else {
                cVal = "white"
            }
            dVal = t_dim
            if (!t_dim) {
                dVal = 37
            }
            masterColorLights << [name: t_name, color: cVal, dim: dVal]
        }
    }



    master_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        masterDoors << [name: t_name, value: t_val]
    }

    master_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        masterLocks << [name: t_name, value: t_val]
    }

    master_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        masterContacts << [name: t_name, value: t_val]
    }

    master_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        masterTemps << [name: t_name, value: t_val]
    }

    master_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        masterHumid << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Master"
        if (master_display?.size() > 0) {
            t_val = master_display
        }
        resp << [name: "master_display", value: t_val]
        resp << [name: "val", value: t_val]

        masterLights.sort{it.name}
        masterLights.each {lr ->
            resp << [name: "master_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
            masterColorLights.each { lv ->
                if (lv.name == lr.name) {
//                    log.debug "Found color bulb! ${lv.name} / ${lv.color} / ${lv.dim}"
                    resp << [name: "switch_c", value: lv.name]
                    resp << [name: lv.color, value: lv.dim]
                }
            }
        }

        masterDoors.sort{it.name}
        masterDoors.each {lr ->
            resp << [name: "master_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        masterLocks.sort{it.name}
        masterLocks.each {lr ->
            resp << [name: "master_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        masterContacts.sort{it.name}
        masterContacts.each {lr ->
            resp << [name: "master_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        masterTemps.sort{it.name}
        masterTemps.each {lr ->
            resp << [name: "master_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        masterHumid.sort{it.name}
        masterHumid.each {lr ->
            resp << [name: "master_humid", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Master Bath */
	itemCnt = 0
    
    mb_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		mbLights << [name: t_name, value: t_val]
    }
    
    mb_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        mbDoors << [name: t_name, value: t_val]
    }

    mb_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        mbLocks << [name: t_name, value: t_val]
    }
    mbLocks.sort{it.name}
    mbLocks.each {lr ->
    	resp << [name: "mb_lock", value: lr.name]
        resp << [name: "val", value: lr.value]
    }

    mb_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        mbContacts << [name: t_name, value: t_val]
    }

    mb_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        mbTemps << [name: t_name, value: t_val]
    }

    mb_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        mbHumid << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Master Bath"
        if (mb_display?.size() > 0) {
            t_val = mb_display
        }
        resp << [name: "mb_display", value: t_val]
        resp << [name: "val", value: t_val]

        mbLights.sort{it.name}
        mbLights.each {lr ->
            resp << [name: "mb_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        mbDoors.sort{it.name}
        mbDoors.each {lr ->
            resp << [name: "mb_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        mbLocks.sort{it.name}
        mbLocks.each {lr ->
            resp << [name: "mb_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        mbContacts.sort{it.name}
        mbContacts.each {lr ->
            resp << [name: "mb_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        mbTemps.sort{it.name}
        mbTemps.sort{it.name}
        mbTemps.each {lr ->
            resp << [name: "mb_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        mbHumid.each {lr ->
            resp << [name: "mb_humid", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }


/* Bedroom 1 */
	itemCnt = 0
    
    br1_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br1Lights << [name: t_name, value: t_val]
    }
    
    br1_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br1Doors << [name: t_name, value: t_val]
    }

    br1_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br1Locks << [name: t_name, value: t_val]
    }

    br1_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br1Contacts << [name: t_name, value: t_val]
    }

    br1_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br1Temps << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Bedroom 1"
        if (br1_display?.size() > 0) {
            t_val = br1_display
        }
        resp << [name: "br1_display", value: t_val]
        resp << [name: "val", value: t_val]

        br1Lights.sort{it.name}
        br1Lights.each {lr ->
            resp << [name: "br1_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br1Doors.sort{it.name}
        br1Doors.each {lr ->
            resp << [name: "br1_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br1Locks.sort{it.name}
        br1Locks.each {lr ->
            resp << [name: "br1_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br1Contacts.sort{it.name}
        br1Contacts.each {lr ->
            resp << [name: "br1_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br1Temps.sort{it.name}
        br1Temps.each {lr ->
            resp << [name: "br1_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Bedroom 2 */
	itemCnt = 0
    
    br2_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br2Lights << [name: t_name, value: t_val]
    }
    
    br2_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br2Doors << [name: t_name, value: t_val]
    }

    br2_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br2Locks << [name: t_name, value: t_val]
    }

    br2_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br2Contacts << [name: t_name, value: t_val]
    }

    br2_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br2Temps << [name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Bedroom 2"
        if (br2_display?.size() > 0) {
            t_val = br2_display
        }
        resp << [name: "br2_display", value: t_val]
        resp << [name: "val", value: t_val]

        br2Lights.sort{it.name}
        br2Lights.each {lr ->
            resp << [name: "br2_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br2Doors.sort{it.name}
        br2Doors.each {lr ->
            resp << [name: "br2_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br2Locks.sort{it.name}
        br2Locks.each {lr ->
            resp << [name: "br2_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br2Contacts.sort{it.name}
        br2Contacts.each {lr ->
            resp << [name: "br2_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br2Temps.sort{it.name}
        br2Temps.each {lr ->
            resp << [name: "br2_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

/* Bedroom 3 */
	itemCnt = 0
    
    br3_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br3Lights << [name: t_name, value: t_val]
    }
    
    br3_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br3Doors << [name: t_name, value: t_val]
    }

    br3_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br3Locks << [name: t_name, value: t_val]
    }

    br3_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br3Contacts << [name: t_name, value: t_val]
    }

    br3_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br3Temps << [name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Bedroom 3"
        if (br3_display?.size() > 0) {
            t_val = br3_display
        }
        resp << [name: "br3_display", value: t_val]
        resp << [name: "val", value: t_val]

        br3Lights.sort{it.name}
        br3Lights.each {lr ->
            resp << [name: "br3_switch", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br3Doors.sort{it.name}
        br3Doors.each {lr ->
            resp << [name: "br3_door", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br3Locks.sort{it.name}
        br3Locks.each {lr ->
            resp << [name: "br3_lock", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br3Contacts.sort{it.name}
        br3Contacts.each {lr ->
            resp << [name: "br3_contact", value: lr.name]
            resp << [name: "val", value: lr.value]
        }

        br3Temps.sort{it.name}
        br3Temps.each {lr ->
            resp << [name: "br3_temp", value: lr.name]
            resp << [name: "val", value: lr.value]
        }
    }

	def ignore = false
	lights.each {dev ->
    	ignore = false
    	t_name = "${dev}"
        if (dev.getStatus() != "OFFLINE") {
	        t_val = dev.currentValue("switch")
        }
        else {
        	if (lightsIgnore) {
            	lightsIgnore.each { li ->
                	log.debug "li: ${li} / dev: ${dev}"
                	if ("${li}" == "${dev}") {
                    	ignore = true
                        log.debug "Ignoring ${li}"
                    }
                }
            }
            if (ignore) {
            	t_val = dev.currentValue("switch")
            }
            else {
                t_val = "OFFLINE"
                dev.ping()
                if (dev.getStatus() != "OFFLINE") {
                    t_val = dev.currentValue("switch")
                }
            }
        }
		respLights << [name: t_name, value: t_val]

		def bulbFound = -1
        def colorBulb = false
        
        t_hue = dev?.currentValue("hue")
        if (t_hue) {
        	colorBulb = true

			bulbFound = -1
            for (int x = 0; x < state?.colorBulbs?.size(); x++) {
                if (state.colorBulbs[x].name == t_name) {
                    bulbFound = x
                    break
                }
            }
            if (bulbFound == -1) {
            	t_hue = dev?.currentValue("hue")
                t_saturation = dev?.currentValue("saturation")
                t_dim = dev?.currentValue("level")
            	state.colorBulbs << [name: t_name, hue: t_hue, saturation: t_saturation, dim: t_dim]
            }
            else {
                t_hue = state.colorBulbs[bulbFound].hue
                t_saturation = state.colorBulbs[bulbFound].saturation
                t_dim = state.colorBulbs[bulbFound].dim
                if (!t_dim) {
                    t_dim = 37
                }
            }
        /* white: hue: 56, saturation: 2
           red: hue: 2, saturation: 94
           blue: hue: 67, saturation: 92
           green: hue: 28, saturation: 98
        */
            if (t_hue == 2 && t_saturation == 94) {
                cVal = "red"
            }
            else if (t_hue == 67 && t_saturation == 92) {
                cVal = "blue"
            }
            else if (t_hue == 28 && t_saturation == 98) {
                cVal = "green"
            }
            else {
                cVal = "white"
            }
            dVal = t_dim
            if (!t_dim) {
                dVal = 37
            }
            lightsVals << [name: t_name, color: cVal, dim: dVal]
        }
    }
	respLights.sort{it.name}
    respLights.each {rl ->
    	resp << [name: "switch", value: rl.name]
        resp << [name: "val", value: rl.value]
        lightsVals.each { lv ->
            if (lv.name == rl.name) {
//                log.debug "Found color bulb! ${lv.name} / ${lv.color} / ${lv.dim}"
                resp << [name: "switch_c", value: rl.name]
                resp << [name: lv.color, value: lv.dim]
            }
        }
    }


	doors.each {door ->
    	t_name = "${door}"
        if (door.getStatus() != "OFFLINE") {
	        t_val = door.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
		respDoors << [name: t_name, value: t_val]
    }    
    respDoors.sort{it.name}
    respDoors.each {rd ->
    	resp << [name: "door", value: rd.name]
        resp << [name: "val", value: rd.value]
    }
    
    locks.each {lock ->
    	t_name = "${lock}"
        if (lock.getStatus() != "OFFLINE") {
	        t_val = lock.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
		respLocks << [name: t_name, value: t_val]
    }
    respLocks.sort{it.name}
    respLocks.each {rl ->
    	resp << [name: "lock", value: rl.name]
        resp << [name: "val", value: rl.value]
    }
    
    contacts.each {contact ->
    	t_name = "${contact}"
        if (contact.getStatus() != "OFFLINE") {
	        t_val = contact.currentValue("contact")
        }
        else {
        	t_val = "OFFLINE"
        }
		respContacts << [name: t_name, value: t_val]
    }
    respContacts.sort{it.name}
    respContacts.each { rc ->
    	resp << [name: "contact", value: rc.name]
        resp << [name: "val", value: rc.value]
    }
    
//    log.debug resp
    return resp
}
