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
    subscribe(wxDevice, "rainToday", changeHandler)
    subscribe(jeff, "presence", changeHandler)
    subscribe(cyndi, "presence", changeHandler)
    subscribe(dee, "currentLocation", changeHandler)
    subscribe(app, appHandler)
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
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    if (command.contains("all=lock")) {
    	allLock = true
    }
    
    locks.each {lock ->
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
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
    
    if (command.contains("all=close")) {
    	allClose = true
    }
    
    doors.each {door ->
    	log.debug door
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
    if (idx > -1) {
    	sv = command.substring(idx+1).toUpperCase()
    }
	if (command.contains("all=off")) {
    	allOff = true
    }

    lights.each {dev ->
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
    
    resp << [name: "alarm", value: "Alarm Sensor"]
    resp << [name: "val", value: alarmSensor.currentValue("alarmState")]
    resp << [name: "alert", value: "Alert State"]
    resp << [name: "val", value: alarmSensor.currentValue("alertState")]
    
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

	resp << [name: "weather", value: forecast.displayName]
    resp << [name: "val", value: forecast.currentValue("temperature")]
    resp << [name: "forecast", value: forecast.currentValue("weather")]
    resp << [name: "val", value: forecast.currentValue("forecast")]
    resp << [name: "wxDevice", value: "Rain Gauge"]
    resp << [name: "val", value: wxDevice.currentValue("rainToday")]
    resp << [name: "feelsLike", value: "Feels Like"]
    resp << [name: "val", value: forecast.currentValue("feelsLike")]
    resp << [name: "percentPrecip", value: "Chance of Precip"]
    resp << [name: "val", value: forecast.currentValue("percentPrecip")]
    resp << [name: "humidity", value: "Humidity"]
    resp << [name: "val", value: forecast.currentValue("humidity")]

    resp << [name: "dee", value: "Dee's Location"]
    resp << [name: "val", value: dee.currentValue("currentLocation")]
    
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
                log.debug "Found color bulb! ${lv.name} / ${lv.color} / ${lv.dim}"
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
    
    log.debug resp
    return resp
}
