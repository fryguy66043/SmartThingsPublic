/**
 *  Alarm Controller SmartApp
 *  Monitors selected contact sensors, door controls, and locks and sets a virtual contact sensor to indicate if the perimeter is secure or not.
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

definition(
    name: "Alarm Controller SmartApp",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Set a Virtual Contact Sensor to indicate if perimeter contact sensors, door controls and locks are closed/locked.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png")

preferences {
	page(name: "commonPage")
	page(name: "armedHomePage")
    page(name: "armedAwayPage")
}

def commonPage() {
    dynamicPage(name: "commonPage", title: "Select Alarm Control Sensor and Presence Sensors", nextPage: "armedHomePage", install: false, uninstall: true) {
        section("Which Alarm Controller Sensor?") {
            input "alarmSensor", "device.fryguyAlarmController", required: true, title: "Which Alarm Controller Sensor do you want to indicate alarm status?"
        }
        section("Which Presence Sensors?") {
            input "presence1", "capability.presenceSensor", required: true, multiple: true, title: "Who determines if Home or Away?"
        }
        section("Send Push Notification?") {
            input "sendPush", "bool", required: false,
                  title: "Send Push Notification when Opened?"
        }
        section("Send a text message to this number") {
            input "phone", "phone", required: false
        }
        section("Send a second text message to this number") {
            input "phone2", "phone2", required: false
        }
    }
}

def armedHomePage() {
    dynamicPage(name: "armedHomePage", title: "When Alarm is Armed While Home", nextPage: "armedAwayPage", install: false, uninstall: true) {
        section("Do You Want To Setup a Schedule To Arm/Disarm The Alarm?") {
        	input "homeArmSchedule", "bool", required: true, multiple: false, title: "Do you want to setup a schedule?"
        	input "homeArmTime", "time", required: false, multiple: false, title: "What time do you want to arm the alarm when home?"
            input "homeDisarmTime", "time", required: false, multiple: false, title: "What time do you want to disarm the alarm when home?"
        }
        section("Which Contact Sensors?") {
            input "homeContacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
        }
        section("Which Door Controls?") {
            input "homeDoors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
        }
        section("Which Locks?") {
            input "homeLocks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
        }
        section("Alert With Lights/Switches?") {
			input "homeAlertSwitches", "capability.switch", required: false, multiple: true, title: "Which lights/switchs do you want to turn on?"
            input "homeAlertSwitchesDarkOnly", "bool", required: true, title: "Do you want to alert with lights only after dark?"
        }
        section("Delay Before Triggering Alert?") {
			input "homeAlertDelay", "number", required: true, title: "How many seconds do you want to delay before triggering alert?"
        }
    }
}

def armedAwayPage() {
    dynamicPage(name: "armedAwayPage", title: "When Alarm is Armed When Everyone is Away", install: true, uninstall: true) {
        section("Which Contact Sensors?") {
            input "awayContacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
        }
        section("Which Door Controls?") {
            input "awayDoors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
        }
        section("Which Locks?") {
            input "awayLocks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
        }
        section("Alert With Lights/Switches?") {
			input "awayAlertSwitches", "capability.switch", required: false, multiple: true, title: "Which lights/switchs do you want to turn on?"
            input "awayAlertSwitchesDarkOnly", "bool", required: true, title: "Do you want to alert with lights only after dark?"
        }
        section("Delay Before Triggering Alert?") {
			input "awayAlertDelay", "number", required: true, title: "How many seconds do you want to delay before triggering alert?"
        }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "initialize()"
    subscribe(app, appHandler)
	subscribe(presence1, "presence.present", arrivalHandler)
    subscribe(presence1, "presence.not present", departureHandler)
    subscribe(alarmSensor, "alarmState", alarmSensorHandler)
    subscribe(alarmSensor, "alertState", alarmAlertHandler)
    subscribe(homeContacts, "contact", contactHandler)
    subscribe(homeDoors, "door", doorHandler)
    subscribe(homeLocks, "lock", lockHandler)
    subscribe(awayContacts, "contact", contactHandler)
    subscribe(awayDoors, "door", doorHandler)
    subscribe(awayLocks, "lock", lockHandler)
    state.unsecure = false
    state.change = false
    state.alert = false
    state.alertMessage = ""
    state.offSwitches = ""
    if (homeArmSchedule && homeArmTime && homeDisarmTime) {
        schedule(homeArmTime, homeArmTimeHandler)
        schedule(homeDisarmTime, homeDisarmTimeHandler)
    }
    runEvery5Minutes(virtualController)
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    getStatus()
}

def homeArmTimeHandler(evt) {
	log.debug "homeArmTimeHandler"
    def date = new Date().format("MM/dd/yyyy h:mm a", location.timeZone)
    def msg = "${location} ${date}\n"
	if (anyOneHome()) {
    	log.debug "Someone is home.  state.alert = ${state.alert}"
    	if (state.alert == false) {
        	log.debug "setting alarm to Armed Home"
        	msg = msg + "Alarm set to: Armed Home"
	    	alarmSensor.setArmedHome()
        }
        else {
        	log.debug "Alarm in precess.  Unable to set to Armed Home"
        	msg = msg + "Unable to change alarm state.  Alarm in process!"
        }
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
}

def homeDisarmTimeHandler(evt) {
	log.debug "homeDisarmTimeHandler"
    def date = new Date().format("MM/dd/yyyy h:mm a", location.timeZone)
    def msg = "${location} ${date}\n"
    if (alarmSensor.currentValue("alarmState") == "Armed Home") {
        msg = msg + "Alarm set to: Disarmed"
        alarmSensor.setDisarmed()
    	if (state.alert) {
        	msg = msg + " - Alarm in process!!!"
        }
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
}

private checkHomeDevices() {
	log.debug "checkHomeDevices"
    def result = ""
    def list = ""
    
    result = homeContacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        list = list + result
    }
    result = homeDoors.findAll{it.currentValue("door") == "open"}
    if (result.size() > 0) {
        list = list + result
    }
    result = homeLocks.findAll{it.currentValue("lock") == "unlocked"}
    if (result.size() > 0) {
        list = list + result
    }
    return list
}

private checkAwayDevices() {
	log.debug "checkAwayDevices"
    def result = ""
    def list = ""
    
    result = awayContacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        list = list + result
    }
    result = awayDoors.findAll{it.currentValue("door") == "open"}
    if (result.size() > 0) {
        list = list + result
    }
    result = awayLocks.findAll{it.currentValue("lock") == "unlocked"}
    if (result.size() > 0) {
        list = list + result
    }
    return list
}

def arrivalHandler(evt) {
	log.debug "arrivalHandler: ${evt.value}"
    if (firstOneHome()) {
    	log.debug "First one home.  Disarming alarm"
        def date = new Date().format("MM/dd/yyyy h:mm a", location.timeZone)
        def msg = "${location} ${date}\nFirst one home.  Disarming alarm."
        if (alarmSensor.currentValue("alertState") == "alarm") {
        	msg = msg + "\nNOTICE: Alarm was triggered while away! Dismiss alert in Alarm Controller if addressed."
        }
	    alarmSensor.setDisarmed()
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
}

def departureHandler(evt) {
	log.debug "departureHandler: ${evt.value}"
    def date = new Date().format("MM/dd/yyyy h:mm a", location.timeZone)
    def msg = "${location} ${date}\nEveryone has departed.  Setting alarm to Armed Away."
    def result = ""
    if (everyoneIsAway()) {
	    alarmSensor.setArmedAway()
        result = checkAwayDevices()
    	if(result) {
        	def warn = "\nNOTICE:  The following devices are unsecure:\n${result}"
            msg = msg + warn
        }
    	log.debug "Everyone is away.  Setting alarm to Armed Away."
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
}

def alarmSensorHandler(evt) {
	log.debug "alarmSensorHandler: ${evt.value}"
}

def alarmAlertHandler(evt) {
	log.debug "alarmAlertHandler: ${evt.value}"
    if (alarmSensor.currentValue("alertState") == "silent") {
    	state.alert = false
        turnOffAlertLights()
    }
}

def contactHandler(evt) {
	log.debug "contactHandler: ${evt.value} / alarmState = ${alarmSensor.currentValue("alarmState")}"
    def result = ""
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	result = homeContacts.findAll{it.currentValue("contact") == "open"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        case "Armed Away":
        	result = awayContacts.findAll{it.currentValue("contact") == "open"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        default:
        	break
    }
}

def doorHandler(evt) {
	log.debug "doorHandler: ${evt.value}"
    def result = ""
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	result = homeDoors.findAll{it.currentValue("door") == "open"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        case "Armed Away":
        	result = awayDoors.findAll{it.currentValue("door") == "open"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        default:
        	break
    }
}

def lockHandler(evt) {
	log.debug "lockHandler: ${evt.value}"
    def result = ""
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	result = homeLocks.findAll{it.currentValue("lock") == "unlocked"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        case "Armed Away":
        	result = awayLocks.findAll{it.currentValue("lock") == "unlocked"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        default:
        	break
    }
}

def virtualController(evt) {
	log.debug "virtualController(): state.unsecure = ${state.unsecure}"
    log.debug "alarmSensor = ${alarmSensor.currentValue("alarmState")}"
    def unsecure = false
    def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\n ALARM!!! "
    def result = ""
    def contacts = ""
    def doors = ""
    def locks = ""
    
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	log.debug "Checking Armed Home sensors..."
        	contacts = homeContacts
            doors = homeDoors
            locks = homeLocks
        	break
        case "Armed Away":
        	log.debug "Checking Armed Away sensors..."
        	contacts = awayContacts
            doors = awayDoors
            locks = awayLocks
        	break
        default:
        	break
    }
    
    if (alarmSensor.currentValue("alarmState") != "Disarmed") {
		log.debug "Checking alarm sensors..."
        log.debug "contacts = ${contacts}"
        result = contacts.findAll{it.currentValue("contact") == "open"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
        }
        result = doors.findAll{it.currentValue("door") == "open"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
        }
        result = locks.findAll{it.currentValue("lock") == "unlocked"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
        }
    }

	log.debug "unsecure = ${unsecure}"
	if (unsecure || state.alert) {
    	log.debug "Potential Alert triggered: Setting delay timer.\n${msg}"
        
        if (state.alert) {
       		log.debug "state.alertMessage = ${state.alertMessage}"
        	msg = "${state.alertMessage}"
        	if (sendPush) {
            	sendPush(msg)
            }
            if (phone) {
            	sendSms(phone, msg)
            }
        }
        else {
            state.alertMessage = msg
            setAlertTimer()        
        }
/*        
        alarmSensor.setAlert()
        triggerLights()
        state.alert = true
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
        	log.debug "Sending SMS: ${msg}"
            sendSms(phone, msg)
        }
    }
    state.change = false
*/    
	}
}

private setAlertTimer() {
	log.debug "setAlertTimer"
    def timer = (alarmSensor.currentValue("alarmState") == "Armed Away") ? awayAlertDelay : homeAlertDelay
    log.debug "Alert Timer = ${timer} seconds"
    runIn(timer, checkAlert)
}

def checkAlert() {
	log.debug "checkAlert"
    if (alarmSensor.currentValue("alarmState") != "Disarmed") {
        alarmSensor.setAlert()
        triggerLights()
        state.alert = true
        state.unsecure = true
        if (sendPush) {
            sendPush(state.alertMessage)
        }
        if (phone) {
        	log.debug "Sending SMS: ${state.alertMessage}"
            sendSms(phone, state.alertMessage)
        }
    }
    else {
    	log.debug "Alarm Disarmed during delay."
    }
    state.change = false
}

private getStatus() {
	log.debug "getStatus(): state.unsecure = ${state.unsecure}"
    log.debug "perimeterSensor = ${perimeterSensor.currentValue("contact")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nPerimeter Unsecure: "
	def unsecure = false
    def result = ""
    def contacts = ""
    def doors = ""
    def locks = ""
    
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	contacts = homeContacts
            doors = homeDoors
            locks = homeLocks
        	break
        case "Armed Away":
        	contacts = awayContacts
            doors = awayDoors
            locks = awayLocks
        	break
        default:
        	break
    }
    
    result = contacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }
    result = doors.findAll{it.currentValue("door") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }
    result = locks.findAll{it.currentValue("lock") == "unlocked"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }

	log.debug "unsecure = ${unsecure} / state.unsecure = ${state.unsecure}"
	if (unsecure) {
    	log.debug "Perimeter unsecure!"
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else if (unsecure == false) {
    	log.debug "Perimter secure!"
        state.unsecure = false
        msg = "${location} ${date}\nPerimeter secured!"
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    state.change = false
}

private anyOneHome() {
	log.debug "anyOneHome"
    def result = false
    
    def home = "${presence1.findAll{it.currentValue("presence") == "present"}}"
	if (home) {
    	result = true
    }
	log.debug "anyOneHome = ${home} / result = ${result}"
    return result
}

private firstOneHome() {
	def cnt = 0
    def result = false
    for (person in presence1) {
    	if (person.currentPresence == "present") {
        	cnt++
        }
    }
    if (cnt == 1) {
    	result = true
    }
    return result
}

private everyoneIsAway() {
	def result = true
    for (person in presence1) {
    	if (person.currentPresence == "present") {
        	result = false
            break
        }
    }
    return result
}

private triggerLights() {
	log.debug "triggerLights"
    def daylight = getSunriseAndSunset()
    def turnOn = false
    
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	log.debug "checking homeAlertSwitches...Dark Only = ${homeAlertSwitchesDarkOnly}"
            if (Boolean.parseBoolean(homeAlertSwitchesDarkOnly)) {
            	log.debug "Dark Only = true / checking to see if it's dark"
            	if (timeOfDayIsBetween(daylight.sunset, daylight.sunrise, new Date(), location.timeZone)) {
                	turnOn = true
                }
            }
            else {
            	log.debug "setting turnOn = true"
            	turnOn = true
            }
            log.debug "turn on lights? ${turnOn}"
            if (turnOn) {
            	log.debug "turning on homeAlertSwitches: ${homeAlertSwitches}"
                state.offSwitches = "${homeAlertSwitches?.findAll{it.currentValue("switch") == "off"}}"
                log.debug "state.offSwitches = ${state.offSwitches}"
                homeAlertSwitches?.on()
            }
            else {
            	state.offSwitches = ""
            }
        	break
        case "Armed Away":
        	log.debug "checking awayAlertSwitches...Dark Only = ${awayAlertSwitchesDarkOnly}"
            if (Boolean.parseBoolean(awayAlertSwitchesDarkOnly)) {
            	log.debug "Dark Only = true / checking to see if it's dark"
            	if (timeOfDayIsBetween(daylight.sunset, daylight.sunrise, new Date(), location.timeZone)) {
                	turnOn = true
                }
            }
            else {
            	log.debug "setting turnOn = true"
            	turnOn = true
            }
            log.debug "turn on lights? ${turnOn}"
            if (turnOn) {
            	log.debug "turning on awayAlertSwitches: ${awayAlertSwitches}"
                state.offSwitches = "${awayAlertSwitches?.findAll{it.currentValue("switch") == "off"}}"
                log.debug "state.offSwitches = ${state.offSwitches}"
                awayAlertSwitches?.on()
            }
            else {
            	state.offSwitches = ""
            }
        	break
        default:
        	break
    }
}

private turnOffAlertLights() {
	log.debug "turnOffAlertLights"
    log.debug "state.offSwitches = ${state.offSwitches}"
    log.debug "awayAlertSwitches.size() = ${awayAlertSwitches.size()}"
    for (int i=0; i < awayAlertSwitches.size(); i++) {
    	log.debug "awayAlertSwitches[${i}] = ${awayAlertSwitches[i]}"
    	if (state.offSwitches.contains("${awayAlertSwitches[i]}")) {
        	log.debug "Turning off ${awayAlertSwitches[i]}"
        	awayAlertSwitches[i].off()
        }
    }
//    state.offSwitches?.off()
}