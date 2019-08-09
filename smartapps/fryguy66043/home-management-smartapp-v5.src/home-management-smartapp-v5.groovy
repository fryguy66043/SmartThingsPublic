/**
 *  Home Management SmartApp.  Added alarm support in v5.
 *
 *  Copyright 2017 Jeffrey Fry
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
    name: "Home Management SmartApp v5",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "One source to manage home automation.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png"
)

preferences {
	page(name: "alarmPage")
	page(name: "arrivalPage")
    page(name: "departurePage")
    page(name: "visitorPage")
    page(name: "eveningPage")
    page(name: "nightPage")
    page(name: "morningPage")
    page(name: "notificationPage")
}

def alarmPage() {
    dynamicPage(name: "alarmPage", title: "Define your Alarm setting preferences", nextPage: "arrivalPage", install: false, uninstall: true) {
    	section("Activate Alarm Monitoring?") {
        	input "alarmActive", "bool", title: "Do you want to activate Alarm Monitoring?"
        }
    	section("Select the Alarm Controller Device") {
        	input "alarmSensor", "device.fryguyAlarmController", title: "Select the Alarm Controller.", required: false
        }
        section("Select Disarm Switch") {
        	input "disarmSwitch", "capability.switch", multiple: true, title: "Turn this switch on and off 3 times in 5 seconds to disarm the alarm."
        }
        section("Select Devices to Monitor and Alert Actions When Home") {
        	input "homeMotions", "capability.motionSensor", required: false, multiple: true, title: "Which Motion Sensors do you want to monitor?"
            input "homeContacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
            input "homeDoors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
            input "homeLocks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
        	input "homeIndicatorLight", "capability.colorControl", required: false, multiple: true, title: "Which Lights do you want to turn on to indicate the alarm is armed?"
            input "homeIndicatorLightColor", "enum", options: ["OFF", "RED", "GREEN", "BLUE", "WHITE"], title: "Which color do you want to set your indicator light?"
        	input "homeAlarm", "capability.alarm", required: false, multiple: true, title: "Which Alarm Sensors do you want to activate during an alert?"
            input "homeAlarmLightsSirens", "enum", options: ["Off", "Strobe", "Siren", "Both", "Beep"], title: "Do you want Alarm Lights, Siren, Both, or Beep/Strobe?"
			input "homeAlertSwitches", "capability.switch", required: false, multiple: true, title: "Which lights/switchs do you want to turn on during an alert?"
            input "homeAlertLightColor", "enum", options: ["OFF", "RED", "GREEN", "BLUE", "WHITE"], title: "What color do you want to set your color-capable lights during an alert?"
            input "homeAlertSwitchesDarkOnly", "bool", required: true, title: "Do you want to alert with lights only after dark?"
        }
        section("Select Devices to Monitor and Alert Actions When Away") {
        	input "awayMotions", "capability.motionSensor", required: false, multiple: true, title: "Which Motion Sensors do you want to monitor?"
            input "awayContacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
            input "awayDoors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
            input "awayLocks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
        	input "awayIndicatorLight", "capability.colorControl", required: false, multiple: true, title: "Which Lights do you want to turn indicate the alarm is armed?"
            input "awayIndicatorLightColor", "enum", options: ["OFF", "RED", "GREEN", "BLUE", "WHITE"], title: "Which color do you want to set your indicator light?"
        	input "awayAlarm", "capability.alarm", required: false, multiple: true, title: "Which Alarm Sensors do you want to activate during an alert?"
            input "awayAlarmLightsSirens", "enum", options: ["Off", "Strobe", "Siren", "Both", "Beep"], title: "Do you want Alarm Lights, Siren, Both, or Beep/Strobe?"
			input "awayAlertSwitches", "capability.switch", required: false, multiple: true, title: "Which lights/switchs do you want to turn on?"
            input "awayAlertLightColor", "enum", options: ["OFF", "RED", "GREEN", "BLUE", "WHITE"], title: "What color do you want to set your color-capable lights during an alert?"
            input "awayAlertSwitchesDarkOnly", "bool", required: true, title: "Do you want to alert with lights only after dark?"
			input "awayAlertDelay", "number", required: true, title: "How many seconds do you want to delay before triggering alert? (This can provide time for Presence Sensors to be found.)"
        }
		section("Send An Alarm Notification?") {
            input "alarmPush", "bool", title: "Send a Push Notification?"
            input "alarmPhone", "phone", title: "Send a Text?", required: false
            input "alarmPhone2", "phone", title: "Send a Second Text?", required: false
        }
    }
}

def arrivalPage() {
    dynamicPage(name: "arrivalPage", title: "When Someone Comes Home", nextPage: "departurePage", install: false, uninstall: true) {
        section("When someone arrives"){
            input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
        }

// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
            section("Set Mode"){
            	input "arrivalMode", "mode", required: false
            }
            section("Select Alarm Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "arrivalAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
            }
            section("Select Arrival Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "arrivalRoutine", "enum", title: "Select an Arrival Routine to execute", options: actions, required: false
            }
            section("Turn on these lights or switches") {
            	input "arrivalOnSwitches", "capability.switch", multiple: true, required: false
            }
            section("Turn off these lights or switches") {
            	input "arrivalOffSwitches", "capability.switch", multiple: true, required: false
            }
            section("Send A Notification?") {
            	input "arrivalPush", "bool", title: "Send a Push Notification?"
                input "arrivalPhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def departurePage() {
    dynamicPage(name: "departurePage", title: "When Everyone Departs", nextPage: "visitorPage", install: false, uninstall: false) {
// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
            section("Set Mode"){
            	input "departureMode", "mode", required: false
            }
            section("Select Alarm Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "departureAlarmRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
            }
            section("Select Departure Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "departureRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
            }
            section("Turn on these lights and switches") {
            	input "departureOnSwitches", "capability.switch", multiple: true, required: false
            }
            section("Turn off these lights and switches") {
            	input "departureOffSwitches", "capability.switch", multiple: true, required: false
            }
            section("Send A Notification?") {
            	input "departurePush", "bool", title: "Send a Push Notification?"
                input "departurePhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def visitorPage() {
	dynamicPage(name: "visitorPage", title: "When Expecting Visitors When You're Away", nextPage: "eveningPage", install: false, uninstall: false) {
// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
			section("Visitor Expected Switch") {
            	input "visitorExpectedSwitch", "capability.switch", required: true, title: "Which switch will indicate that a visitor is expected?"
            }
            section("Visitor Present Switch") {
            	input "visitorPresentSwitch", "capability.switch", required: true, title: "Which switch will indicate that a visitor is present?"
            }
            section("Visitor Present Presence Sensor (Optional)") {
            	input "visitorPresentPresenceSensor", "capability.presenceSensor", required: false, title: "Virtual Presence Sensor to indicate that a visitor is present."
            }
			section("Set Visitor Expected Mode"){
            	input "visitorExpectedMode", "mode", required: false, options: actions, title: "What Away Mode do you want when visitors are expected?"
            }            
            section("Select Visitor Entry Doors/Sequence") {
				input "visitorVirtualGarageDoor", "capability.contactSensor", required: false, title: "Virtual Switch to test Garage Door logic."
				input "visitorGarageDoor", "capability.contactSensor", required: false, title: "Garage Door"
                input "visitorGarageDoorSeq", "number", required: false, title: "Garage Door Entry Sequence"
                input "visitorVirtualDoor", "capability.contactSensor", required: false, title: "Virtual Contact Sensor to test Door logic."
                input "visitorDoor", "capability.contactSensor", required: false, title: "Door"
                input "visitorDoorSeq", "number", required: false, title: "Door Entry Sequence"
            }
            section("Select Visitor Arrival Alarm Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "visitorArrivalAlarmRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
            }            
            section("Select Visitor Arrival Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "visitorArrivalRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
            }
			section("Set Visitor Arrival Mode"){
            	input "visitorArrivalMode", "mode", required: false, options: actions, title: "What Away Mode do you want when visitors have arrived?"
            }            
            section("Turn on these lights and switches when visitor arrives") {
            	input "visitorArrivalOnSwitches", "capability.switch", multiple: true, required: false
                input "visitorArrivalOnSwitchesDarkOnly", "bool", title: "Turn on only if visitor arrives after dark?"
            }
            section("Select Visitor Departure Alarm Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "visitorDepartureAlarmRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
                input "visitorDepartureAlarmRoutineDelay", "number", title: "How many minutes to delay beforing executing Alarm Routine?", required: false
            }
            section("Select Visitor Departure Routine (optional)") {
                log.trace actions
// use the actions as the options for an enum input
                input "visitorDepartureRoutine", "enum", title: "Select a Routine to execute", options: actions, required: false
            }
			section("Set Visitor Departed Mode"){
            	input "visitorDepartureMode", "mode", required: false, options: actions, title: "What Away Mode do you want when visitors depart?"
            }            
            section("Send A Notification?") {
            	input "visitorPush", "bool", title: "Send a Push Notification?"
                input "visitorPhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def eveningPage() {
    dynamicPage(name: "eveningPage", title: "In The Evening", nextPage: "nightPage", install: false, uninstall: false) {
		section("How Many Minutes Before Sunset?"){
        	input "minutesBeforeSunset", "number", required: true, default: 0
        }
// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
            section("Execute These If Someone Is Home") {
                log.trace actions
// use the actions as the options for an enum input
                input "eveningHomeMode", "mode", title: "Select Mode", required: false
                input "eveningHomeSetAlarm", "bool", title: "Set Alarm Controller to Armed Home?"
                input "eveningHomeAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "eveningHomeRoutine", "enum", title: "Select an Evening Home Routine to execute", options: actions, required: false
                input "eveningHomeOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "eveningHomeOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "eveningHomePush", "bool", title: "Send a Push Notification?"
                input "eveningHomePhone", "phone", title: "Send a Text?", required: false
            }
            section("Execute These If Everyone Is Away") {
                log.trace actions
// use the actions as the options for an enum input
                input "eveningAwayMode", "mode", title: "Select Mode", required: false
                input "eveningAwayAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "eveningAwayRoutine", "enum", title: "Select an Evening Away Routine to execute", options: actions, required: false
                input "eveningAwayOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "eveningAwayOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "eveningAwayPush", "bool", title: "Send a Push Notification?"
                input "eveningAwayPhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def nightPage() {
    dynamicPage(name: "nightPage", title: "At Night Time", nextPage: "morningPage", install: false, uninstall: false) {
		section("Select the time to execute"){
        	input "nightTime", "time", required: true, default: 0
        }
// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
            section("Execute These If Someone Is Home") {
                log.trace actions
// use the actions as the options for an enum input
                input "nightHomeMode", "mode", title: "Select Mode", required: false
                input "nightHomeSetAlarm", "bool", title: "Set Alarm Controller to Armed Home?"
                input "nightHomeAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "nightHomeRoutine", "enum", title: "Select an Night Home Routine to execute", options: actions, required: false
                input "nightHomeOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "nightHomeOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "nightHomePush", "bool", title: "Send a Push Notification?"
                input "nightHomePhone", "phone", title: "Send a Text?", required: false
            }
            section("Execute These If Everyone Is Away") {
                log.trace actions
// use the actions as the options for an enum input
                input "nightAwayMode", "mode", title: "Select Mode", required: false
                input "nightAwayAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "nightAwayRoutine", "enum", title: "Select an Night Away Routine to execute", options: actions, required: false
                input "nightAwayOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "nightAwayOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "nightAwayPush", "bool", title: "Send a Push Notification?"
                input "nightAwayPhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def morningPage() {
    dynamicPage(name: "morningPage", title: "At Morning Time", nextPage: "notificationPage", install: false, uninstall: false) {
		section("Execute by Time or at Sunrise?") {
        	input "morningTimeOrSunrise", "enum", options: ["Time", "Sunrise"]
        }
		section("Select the time to execute") {
        	input "morningTime", "time", required: false, default: 0
        }
        section("Minutes before sunrise") {
        	input "minutesBeforeSunrise", "number", required: false, default: 0
        }
// get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
// sort them alphabetically
            actions.sort()
            section("Execute These If Someone Is Home") {
                log.trace actions
// use the actions as the options for an enum input
                input "morningHomeMode", "mode", title: "Select Mode", required: false
                input "morningHomeSetAlarm", "bool", title: "Disarm Alarm Controller?"
                input "morningHomeAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "morningHomeRoutine", "enum", title: "Select a Morning Home Routine to execute", options: actions, required: false
                input "morningHomeOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "morningHomeOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "morningHomePush", "bool", title: "Send a Push Notification?"
                input "morningHomePhone", "phone", title: "Send a Text?", required: false
            }
            section("Execute These If Everyone Is Away") {
                log.trace actions
// use the actions as the options for an enum input
                input "morningAwayMode", "mode", title: "Select Mode", required: false
                input "morningAwayAlarmRoutine", "enum", title: "Select an Alarm Routine to execute", options: actions, required: false
                input "morningAwayRoutine", "enum", title: "Select a Morning Away Routine to execute", options: actions, required: false
                input "morningAwayOnSwitches", "capability.switch", title: "Turn on these switches", multiple: true, required: false
                input "morningAwayOffSwitches", "capability.switch", title: "Turn off these switches", multiple: true, required: false
                input "morningAwayPush", "bool", title: "Send a Push Notification?"
                input "morningAwayPhone", "phone", title: "Send a Text?", required: false
            }
        }
    }
}

def notificationPage() {
    dynamicPage(name: "notificationPage", title: "Send Notifications - Separate From Individual Settings", install: true, uninstall: false) {
		section("Send a Push Notification (optional)"){
        	input "sendPush", "bool", required: false, default: false
        }
        section("Send a Text Message (optional)") {
        	input "phone", "phone", required: false
        }
    }
}

def installed()
{
	state.schedule = "x"
    state.presence = "x"
    state.tryCount = 0
    state.checkTime = 10
    state.switches = ""
    state.switchSize = ""
    state.visitorSwitchesTurnedOn = false
    state.visitorDeparted = false
    state.garageDoorOpenTime = now()
    state.garageDoorOpened = false
    state.garageDoorOpen = false
    state.garageDoorClosed = true
    state.garageDoorClosedTime = now()
    state.doorOpen = false
    state.doorOpened = false
    state.doorOpenTime = now()
    state.doorClosed = true
    state.doorClosedTime = now()
	subscribe(presence1, "presence.present", arrivalHandler)
    subscribe(presence1, "presence.not present", departureHandler)
    subscribe(app, appHandler)
	subscribe(location, "sunsetTime", sunsetTimeHandler)
//schedule it to run today too
    scheduleTurnOn(location.currentValue("sunsetTime"))
    if (morningTimeOrSunrise == "Time") {
		schedule(morningTime, morningHandler)
    }
    else {
	    subscribe(location, "sunriseTime", sunriseTimeHandler)
    	scheduleSunriseTurnOn(location.currentValue("sunriseTime"))
    }
    schedule(nightTime, nightHandler)
//visitor handling
    subscribe(visitorExpectedSwitch, "switch", visitorExpectedSwitchHandler)
    subscribe(visitorPresentSwitch, "switch", visitorPresentSwitchHandler)
    subscribe(visitorVirtualGarageDoor, "contact", visitorVirtualGarageDoorHandler)
    subscribe(visitorGarageDoor, "contact", visitorGarageDoorHandler)
    subscribe(visitorVirtualDoor, "contact", visitorVirtualDoorHandler)
    subscribe(visitorDoor, "contact", visitorDoorHandler)
// Alarm Settings    
	if (alarmActive) {
    	log.debug "Activating Alarm Settings..."
        subscribe(alarmSensor, "alarmState", alarmSensorHandler)
        subscribe(alarmSensor, "alertState", alarmAlertHandler)
        subscribe(disarmSwitch, "switch", disarmSwitchHandler)
        subscribe(homeMotions, "motion", motionHandler)
        subscribe(homeContacts, "contact", contactHandler)
        subscribe(homeDoors, "door", doorHandler)
        subscribe(homeLocks, "lock", lockHandler)
        subscribe(awayMotions, "motion", motionHandler)
        subscribe(awayContacts, "contact", contactHandler)
        subscribe(awayDoors, "door", doorHandler)
        subscribe(awayLocks, "lock", lockHandler)
        state.alarmState = "Disarmed"
        state.unsecure = false
        state.change = false
        state.alert = false
        state.userAlarm = false
        state.alertMessage = ""
        state.offSwitches = ""
        state.offAlertSwitches = ""
        state.onTime = 0
        state.onCnt = 0
        runEvery5Minutes(virtualController)
    }
	setMonitoredDevices()
}

def updated()
{
	unsubscribe()
    unschedule()
    installed()
}

private setMonitoredDevices() {
	log.debug "setMonitoredDevices"
    def deviceList = ""
    
// Unsecure List
	alarmSensor.setUnsecureList("")
    
// Armed Home List
	if (homeMotions) {
    	deviceList = deviceList + "Motion Sensors: ${homeMotions}\n"
    }
    if (homeContacts) {
    	deviceList = deviceList + "Contact Sensors: ${homeContacts}\n"
    }
    if (homeDoors) {
    	deviceList = deviceList + "Door Controllers: ${homeDoors}\n"
    }
    if (homeLocks) {
    	deviceList = deviceList + "Smart Locks: ${homeLocks}"
    }
    log.debug "armedHomeMonitoredList = ${deviceList}"
    alarmSensor.setArmedHomeMonitoredList(deviceList)
    
// Armed Away List  
	deviceList = ""
	if (awayMotions) {
    	deviceList = deviceList + "Motion Sensors: ${awayMotions}\n"
    }
    if (awayContacts) {
    	deviceList = deviceList + "Contact Sensors: ${awayContacts}\n"
    }
    if (awayDoors) {
    	deviceList = deviceList + "Door Controllers: ${awayDoors}\n"
    }
    if (awayLocks) {
    	deviceList = deviceList + "Smart Locks: ${awayLocks}"
    }
    log.debug "armedAwayMonitoredList = ${deviceList}"
    alarmSensor.setArmedAwayMonitoredList(deviceList)
}

def visitorExpectedSwitchHandler(evt) {	
	def msg = "visitorExpectedSwitch.currentSwitch == ${visitorExpectedSwitch.currentSwitch}\nlocation.mode == ${location.mode}\n"
    msg = msg + "departureMode == ${departureMode} / visitorExpectedMode == ${visitorExpectedMode}"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
//	if (visitorPhone) {
//		sendSms(visitorPhone, msg)
//    }
    if (visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == departureMode) {
            if (location.modes?.find{it.name == visitorExpectedMode}) {
                setLocationMode(visitorExpectedMode)
                msg = "${location} ${date}: ${visitorExpectedSwitch} activated while away.  Setting Mode to ${visitorExpectedMode}"
                if (visitorPush) {
                    sendPush(msg)
                }
                if (visitorPhone) {
                    sendSms(visitorPhone, msg)
                }
            }
        }
    }
    else {
    	if (visitorExpectedSwitch.currentSwitch == "off") {
        	visitorPresentSwitch?.off()
            visitorPresentPresenceSensor?.departed()
        	if (location.mode == visitorExpectedMode) {
            	setLocationMode(departureMode)
                msg = "${location} ${date}: ${visitorExpectedSwitch} deactivated while away.  Setting Mode to ${departureMode}"
                if (visitorPush) {
                    sendPush(msg)
                }
                if (visitorPhone) {
                    sendSms(visitorPhone, msg)
                }
            }
        }
    }
}

def visitorPresentSwitchHandler(evt) {
	log.debug "visitorPresentSwitchHandler: ${evt.value}"	
}

def visitorVirtualGarageDoorHandler(evt) {
	log.debug "visitorVirtualGarageDoorHandler: ${evt.value}"
//    if (visitorPhone) {
//		sendSms(visitorPhone, "visitorVirtualGarageDoorHandler: ${evt.value}")
//    }
	def msg = ""
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
	switch (evt.value) {
        case "open":
			state.garageDoorOpenTime = now()
			state.garageDoorOpen = true
            state.garageDoorOpened = true
            state.garageDoorClosed = false
        	break
        case "closed":
        	state.garageDoorClosedTime = now()
        	state.garageDoorClosed = true
            state.garageDoorOpen = false
        	break
        default:
        	break
    }

//	if (true && visitorExpectedSwitch.currentSwitch == "on") {
    if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
		log.debug "state.visitorDeparted == ${state.visitorDeparted}"
		if (state.visitorDeparted) {
        	state.visitorDeparted = false
        }
// Check for arrival of visitor
        log.debug "state.garageDoorOpen == ${state.garageDoorOpen}"
        log.debug "state.doorOpen == ${state.doorOpen}"
        if (location.mode == visitorExpectedMode) {
            if (state.garageDoorOpen && !state.doorOpen) {
            	visitorArrival()
            }
        }
// Check for departure of visitor
        else if (location.mode == visitorArrivalMode) {
        	if (state.garageDoorClosed && state.doorClosed) {
                def diff = (state.garageDoorClosedTime - state.doorClosedTime) / 1000
                log.debug "diff == ${diff} / state.garageDoorClosedTime > state.doorClosedTime == ${state.garageDoorClosedTime > state.doorClosedTime}"
                if (diff >= 10) {
                	msg = "${location} ${date}: Possible Visitor departure.  Setting timer for ${visitorDepartureAlarmRoutineDelay} minutes"
                	if (visitorPush) {
                    	sendPush(msg)
                    }
                    if (visitorPhone) {
                		sendSms(visitorPhone, msg)
                    }
                	state.visitorDeparted = true
                    log.debug "Setting time for ${visitorDepartureAlarmRoutineDelay} minutes to test for departure."
                	runIn(60 * visitorDepartureAlarmRoutineDelay, visitorDeparture)
                }
            }
        }
    }
}

def visitorGarageDoorHandler(evt) {
	log.debug "visitorGarageDoorHandler: ${evt.value}"
	def msg = ""
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
	switch (evt.value) {
        case "open":
			state.garageDoorOpenTime = now()
			state.garageDoorOpen = true
            state.garageDoorOpened = true
            state.garageDoorClosed = false
        	break
        case "closed":
        	state.garageDoorClosedTime = now()
        	state.garageDoorClosed = true
            state.garageDoorOpen = false
        	break
        default:
        	break
    }

    if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
		log.debug "state.visitorDeparted == ${state.visitorDeparted}"
		if (state.visitorDeparted) {
        	state.visitorDeparted = false
        }
// Check for arrival of visitor
        log.debug "state.garageDoorOpen == ${state.garageDoorOpen}"
        log.debug "state.doorOpen == ${state.doorOpen}"
        if (location.mode == visitorExpectedMode) {
            if (state.garageDoorOpen && !state.doorOpen) {
            	visitorArrival()
            }
        }
// Check for departure of visitor
        else if (location.mode == visitorArrivalMode) {
        	if (state.garageDoorClosed && state.doorClosed) {
                def diff = (state.garageDoorClosedTime - state.doorClosedTime) / 1000
                log.debug "diff == ${diff} / state.garageDoorClosedTime > state.doorClosedTime == ${state.garageDoorClosedTime > state.doorClosedTime}"
                if (diff > 10) {
                	msg = "${location} ${date}: Possible Visitor departure.  Setting timer for ${visitorDepartureAlarmRoutineDelay} minutes"
                	if (visitorPush) {
                    	sendPush(msg)
                    }
                    if (visitorPhone) {
                		sendSms(visitorPhone, msg)
                    }
                	state.visitorDeparted = true
                    log.debug "Setting time for ${visitorDepartureAlarmRoutineDelay} minutes to test for departure."
                	runIn(60 * visitorDepartureAlarmRoutineDelay, visitorDeparture)
                }
            }
        }
    }
}

def visitorVirtualDoorHandler(evt) {
	log.debug "visitorVirtualDoorHandler: ${evt.value}"
    def daylight = getSunriseAndSunset()
    
    if (state.visitorDeparted) {
    	state.visitorDeparted = false
    }
	state.doorOpen = false
    state.doorClosed = false
    switch(evt.value) {
    	case "open":
        	state.doorOpenTime = now()
        	state.doorOpen = true
            state.doorOpened =
            
            true
        	break
        case "closed":
        	state.doorClosedTime = now()
        	state.doorClosed = true
        	break
        default:
        	break
    }
//	if (true && visitorExpectedSwitch.currentSwitch == "on") {
	if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == visitorArrivalMode) {
			if (state.doorOpened && state.garageDoorOpened) {
            	log.debug "Turn on visitor presence sensors"
                visitorPresentSwitch.on()
                visitorPresentPresenceSensor.arrived()
                if (visitorArrivalOnSwitches) {
                    if (visitorArrivalOnSwitchesDarkOnly) {
                        if (timeOfDayIsBetween(daylight.sunset, daylight.sunrise, new Date(), location.timeZone)) {
                            visitorArrivalOnSwitches?.on()
                            state.visitorSwitchesTurnedOn = true
                        }  
                    }
                    else {
                        visitorArrivalOnSwitches?.on()
                        state.visitorSwitchesTurnedOn = true
                    }
                }
                else {
                    state.visitorSwitchesTurnedOn = false
                }
			}	
        }
    }
}

def visitorDoorHandler(evt) {
	log.debug "visitorDoorHandler: ${evt.value}"
    def daylight = getSunriseAndSunset()
	if (state.visitorDeparted) {
    	state.visitorDeparted = false
    }
	state.doorOpen = false
    state.doorClosed = false
    switch(evt.value) {
    	case "open":
        	state.doorOpenTime = now()
        	state.doorOpen = true
            state.doorOpened = true
        	break
        case "closed":
        	state.doorClosedTime = now()
        	state.doorClosed = true
        	break
        default:
        	break
    }
	if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == visitorArrivalMode) {
			if (state.doorOpened && state.garageDoorOpened) {
                visitorPresentSwitch.on()
                visitorPresentPresenceSensor.arrived()
                log.debug "visitorArrivalOnSwitches == ${visitorArrivalOnSwitches}"
                log.debug "visitorArrivalOnSwitchesDarkOnly == ${visitorArrivalOnSwitchesDarkOnly}"
                if (visitorArrivalOnSwitches) {
                    if (visitorArrivalOnSwitchesDarkOnly) {
                    	log.debug "timeOfDayIsBetween sunset and sunrise == ${timeOfDayIsBetween(daylight.sunset, daylight.sunrise, new Date(), location.timeZone)}"
                        if (timeOfDayIsBetween(daylight.sunset, daylight.sunrise, new Date(), location.timeZone)) {
                            visitorArrivalOnSwitches?.on()
                            state.visitorSwitchesTurnedOn = true
                        }  
                    }
                    else {
                        visitorArrivalOnSwitches?.on()
                        state.visitorSwitchesTurnedOn = true
                    }
                }
                else {
                    state.visitorSwitchesTurnedOn = false
                }
			}	
        }
    }
}

private visitorArrival() {
	log.debug "visitorArrival"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def msg = "${location} ${date}: Visitor Arrival!"
    def daylight = getSunriseAndSunset()

    if (alarmActive) {
        setDisarmed()
    }
    location.helloHome?.execute(settings.visitorArrivalAlarmRoutine)
    location.helloHome?.execute(settings.visitorArrivalRoutine)
    setLocationMode(visitorArrivalMode)
    state.visitorDeparted = false
    if (visitorPush) {
    	sendPush(msg)
    }
    if (visitorPhone) {
    	sendSms(visitorPhone, msg)
    }
}

def visitorDeparture(evt) {
	log.debug "visitorDeparture"
    log.debug "state.visitorDeparted == ${state.visitorDeparted}"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def msg = "${location} ${date}: Visitor Departed!"
    
	if (state.visitorDeparted) {
        if (alarmActive) {
            setArmedAway()
        }
        location.helloHome?.execute(settings.visitorDepartureAlarmRoutine)
        location.helloHome?.execute(settings.visitorDepartureRoutine)
        setLocationMode(visitorDepartureMode)
        visitorPresentSwitch.off()
        visitorPresentPresenceSensor.departed()
        if (state.visitorSwitchesTurnedOn) {
        	visitorArrivalOnSwitches?.off()
        }
    	if (visitorPush) {
        	sendPush(msg)
        }
        if (visitorPhone) {
        	sendSms(visitorPhone, msg)
        }
    }
}

def sunsetTimeHandler(evt) {
//when I find out the sunset time, schedule the lights to turn on with an offset
    scheduleTurnOn(evt.value)
}

def sunriseTimeHandler(evt) {
	scheduleSunriseTurnOn(evt.value)
}

def scheduleTurnOn(sunsetString) {
//get the Date value for the string
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)

//calculate the offset
    def timeBeforeSunset = new Date(sunsetTime.time - (minutesBeforeSunset * 60 * 1000))

    log.debug "Scheduling for: $timeBeforeSunset (sunset is $sunsetTime)"

//schedule this to run one time
    runOnce(timeBeforeSunset, runEveningSchedule)
}

def scheduleSunriseTurnOn(sunriseString) {
	def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)
    def timeBeforeSunrise = new Date(sunriseTime.time - (minutesBeforeSunrise * 60 * 1000))
    
    runOnce(timeBeforeSunrise, morningHandler)
}

def appHandler(evt) {
	def localTimeZone = TimeZone.getTimeZone("America/Chicago")
    def dateFormat = "MM/dd/yy hh:mm a"

	def now = new Date()
	def sunTime = getSunriseAndSunset(zipCode: "66043")
    
	log.debug "nowTime: $now"
	log.debug "riseTime: $sunTime.sunrise"
	log.debug "setTime: $sunTime.sunset"
	log.debug "presenceHandler $evt.name: $evt.value"

    def dark = (now >= sunTime.sunset)
    log.debug dark

    def curPresence = presence1.currentValue("presence")
	def presenceValue = presence1.find{it.currentPresence == "present"}
    def curLocation = location
    def message = "Something went wrong!"
    log.debug "appHandler called: $evt"
    log.debug "curLocation == $curLocation"
    log.debug "curPresence == $curPresence"
    log.debug "presenceValue == $presenceValue"

  	if (presenceValue) {
	  	message = "${curLocation}:  True Presence: ${curPresence}  Dark = ${dark}\n" +
        	"Sunrise = ${sunTime.sunrise.format(dateFormat, localTimeZone)}\nSunset = ${sunTime.sunset.format(dateFormat, localTimeZone)}\n" +
            "state.schedule = ${state.schedule} Retry Count = ${state.tryCount} Presence = ${state.presence}"
        log.debug "True Presence"
    }
    else if (!presenceValue) {
    	message = "${curLocation}:  False Presence: ${curPresence}  Dark = ${dark}\n" +
        	"Sunrise = ${sunTime.sunrise.format(dateFormat, localTimeZone)}\nSunset = ${sunTime.sunset.format(dateFormat, localTimeZone)}"
        log.debug "False Presence"
    }
    else {
    	message = "${curLocation}:  Unknown Presence: ${curPresence}  Sunset = ${dark}"
        log.debug "Unknown Presence"
    }

	log.debug "message == $message"
    if (sendPush) {
        sendPush(message)
    }
    if (phone) {
        sendSms(phone, message)
    }
    if (!phone && !sendPush) {
//JDF - Send to Jeff's Phone    
    	sendSms("9136831550", message)
    }
    getStatus()
}

def arrivalHandler(evt)
{
	def message = "Something went wrong!"
	def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    def curAlarm = state.alarmState
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
    arrivalAlarmHandler(evt)
    if (curAlarm != state.alarmState) {
    	alarmMsg = "Alarm ${state.alarmState}"
    }
    
    if (firstOneHome()) {
        log.debug "Someone Has Arrived: ${settings.arrivalAlarmRoutine}"
        location.helloHome?.execute(settings.arrivalAlarmRoutine)
        location.helloHome?.execute(settings.arrivalRoutine)
        state.schedule = "arrival"
        state.presence = "home"
        state.tryCount = 1
        arrivalOnSwitches?.on()
        arrivalOffSwitches?.off()
        if (location.mode != arrivalMode) {
        	if (location.modes?.find{it.name == arrivalMode}) {
            	setLocationMode(arrivalMode)
                modeMsg = arrivalMode
            }
        }
        if (settings.arrivalAlarmRoutine) {
        	alarmMsg = settings.arrivalAlarmRoutine
        }
        message = "${location} ${date}\n${whoIsHome()}: First One Home! ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || arrivalPush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }
        if (arrivalPhone) {
            sendSms(arrivalPhone, message)
        }	
    }
}

private arrivalAlarmHandler(evt) {
	log.debug "arrivalHandler: ${evt.value}"
    if (firstOneHome()) {
    	log.debug "First one home.  Disarming alarm"
        def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
        def msg = "${location} ${date}\nFirst one home.  Disarming alarm."
        if (alarmSensor.currentValue("alertState") == "alarm") {
        	msg = msg + "\nNOTICE: Alarm was triggered while away! Dismiss alert in Alarm Controller if addressed."
        }
        setDisarmed()
        if (sendPush || alarmSendPush) {
        	sendPush(msg)
        }
    }
}


def departureHandler(evt)
{
	def message = "Something went wrong!"
	def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def curAlarm = state.alarmState
    
    departureAlarmHandler(evt)
    if (curAlarm != state.alarmState) {
    	alarmMsg = "Alarm ${state.alarmState}"
    }

	if (everyoneIsAway()) {
    	log.debug "Everyone Is Away: ${settings.departureAlarmRoutime}"
        if (visitorExpectedSwitch.currentSwitch == "on") {
            if (location.mode != visitorExpectedMode) {
                if (location.modes?.find{it.name == visitorExpectedMode}) {
                    setLocationMode(visitorExpectedMode)
                    modeMsg = visitorExpectedMode
                }
            }
        }
        else {
            if (location.mode != departureMode) {
                if (location.modes?.find{it.name == departureMode}) {
                    setLocationMode(departureMode)
                    modeMsg = departureMode
                }
            }
        }
        	
        location.helloHome?.execute(settings.departureAlarmRoutine)
        location.helloHome?.execute(settings.departureRoutine)
        departureOnSwitches?.on()
        departureOffSwitches?.off()
        if (settings.departureAlarmRoutine) {
        	alarmMsg = settings.departureAlarmRoutine
        }
        message = "${location} ${date}: Performing Goodbye Home! ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || departurePush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }
        if (departurePhone) {
        	sendSms(departurePhone, message)
        }
    }
}

private departureAlarmHandler(evt) {
	log.debug "departureHandler: ${evt.value}"
    def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nEveryone has departed.  Setting alarm to Armed Away."
    def result = ""
    if (everyoneIsAway()) {
		setArmedAway()
        result = checkAwayDevices()
    	if(result) {
        	def warn = "\nNOTICE:  The following devices are unsecure:\n${result}"
            msg = msg + warn
        }
    	log.debug "Everyone is away.  Setting alarm to Armed Away."
        if (sendPush || alarmSendPush) {
        	sendPush(msg)
        }
    }
}

def disarmSwitchHandler(evt) {
	log.debug "disarmSwitchHandler: ${evt.value}"
        
    if (evt.value == "on") {
        if (state.onTime == 0) {
            state.onTime = now()
            state.onCnt = 1
        }
        else {
            if (now() - state.onTime < 5000) {
                state.onCnt = state.onCnt + 1
                log.debug "state.onCnt = ${state.onCnt}"
                if (state.onCnt == 3) {
                	log.debug "Button Switch Triggered!"
                    if (alarmSensor.currentValue("alarmState") != "Disarmed") {
                        setDisarmed()
                        if (sendPush || alarmSendPush) {
                        	sendPush("Manual Disarm Triggered!")
                        }
                        if (alarmPhone) {
                           sendSms(alarmPhone, "Manual Disarm Triggered!")
                        }
                        if (alarmPhone2) {
                            sendSms(alarmPhone2, "Manual Disarm Triggered!")
                        }
                    }
                }
            }
            else {
                state.onTime = 0
                state.onCnt = 0
            }
        }
    }
}

def alarmSensorHandler(evt) {
	log.debug "alarmSensorHandler: ${evt.value} / state.alarmState = ${state.alarmState}"
    def result = ""
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}:"
    
    if (state.alarmState != evt.value) {
    	log.debug "Alarm State changed from controller.  Synching SmartApp..."
    	switch (evt.value) {
        	case "Armed Home":
            	setArmedHome()
                result = checkHomeDevices()
                if(result) {
                    def warn = "\nNOTICE: Alarm set to ${evt.value}, but the following devices are unsecure:\n${result}"
                    msg = msg + warn
                    log.debug "Alarm set to Armed Home, but devices are still unsecure!"
                    if (sendPush || alarmSendPush) {
                        sendPush(msg)
                    }
                    if (alarmPhone) {
                        sendSms(alarmPhone, msg)
                    }
                    if (alarmPhone2) {
                        sendSms(alarmPhone2, msg)
                    }
        		}	
            	break
            case "Armed Away":
            	setArmedAway()
                result = checkAwayDevices()
                if(result) {
                    def warn = "\nNOTICE: Alarm set to ${evt.value}, but the following devices are unsecure:\n${result}"
                    msg = msg + warn
                    log.debug "Alarm set to Armed Away, but devices are still unsecure!"
                    if (sendPush || alarmSendPush) {
                        sendPush(msg)
                    }
                    if (alarmPhone) {
                        sendSms(alarmPhone, msg)
                    }
        		}	
            	break
            case "Disarmed":
            	setDisarmed()
            	break
            default:
            	break
        }
    }
}

def alarmAlertHandler(evt) {
	log.debug "alarmAlertHandler: ${evt.value}"
    def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = ""
    
    def alarmAlertState = alarmSensor.currentValue("alertState")
    if (alarmAlertState == "silent") {
    	log.debug "${location} ${date}: Alarm Dismissed"
        msg = "${location} ${date}: Alarm Dismissed!"
    	state.alert = false
        turnOffAlertLights()
        state.userAlarm = false
        homeAlarm?.off()
        awayAlarm?.off()
        alarmSensor.setUnsecureList("")
        if (sendPush || alarmSendPush) {
        	sendPush(msg)
        }
        if (alarmPhone) {
        	sendSms(alarmPhone, msg)
        }
        if (alarmPhone2) {
        	sendSms(alarmPhone2, msg)
        }
    }
    else if (alarmAlertState == "userAlarm") {
    	log.debug "${location} ${date}: Alarm Activated By User"
        msg = "${location} ${date}: Alarm Activated By User!"
        state.alert = true
        state.userAlarm = true
        state.alertMessage = msg
        alarmSensor.setUnsecureList("User Activated!")
        virtualController()
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

def motionHandler(evt) {
	log.debug "motionHandler: ${evt.value}"
    def result = ""
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	result = homeMotions.findAll{it.currentValue("motion") == "active"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        case "Armed Away":
        	result = awayMotions.findAll{it.currentValue("motion") == "active"}
            if (result) {
                state.change = true
                virtualController()            	
            }
        	break
        default:
        	break
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
        	log.debug "Armed Away. Checking contacts..."
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
    def unsecureList = ""
    def newResult = ""
    def result = ""
    def motions = ""
    def contacts = ""
    def doors = ""
    def locks = ""
    
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	log.debug "Checking Armed Home sensors..."
            motions = homeMotions
        	contacts = homeContacts
            doors = homeDoors
            locks = homeLocks
        	break
        case "Armed Away":
        	log.debug "Checking Armed Away sensors..."
            motions = awayMotions
        	contacts = awayContacts
            doors = awayDoors
            locks = awayLocks
        	break
        default:
        	break
    }
    
    if (alarmSensor.currentValue("alarmState") != "Disarmed") {
		log.debug "Checking alarm sensors..."
        result = motions.findAll{it.currentValue("motion") == "active"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
            newResult = newResult + result
            unsecureList = unsecureList + result
        }
        result = contacts.findAll{it.currentValue("contact") == "open"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
            newResult = newResult + result
            unsecureList = unsecureList + result
        }
        result = doors.findAll{it.currentValue("door") == "open"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
            newResult = newResult + result
            unsecureList = unsecureList + result
        }
        result = locks.findAll{it.currentValue("lock") == "unlocked"}
        if (result.size() > 0) {
            unsecure = true
            msg = msg + result
            newResult = newResult + result
            unsecureList = unsecureList + result
        }
    }
    
    if (state.userAlarm) {
    	unsecureList = "User Activated!"
    }
    log.debug "Setting unsecureList = '${unsecureList}'"
    alarmSensor.setUnsecureList(unsecureList)

	log.debug "unsecure = ${unsecure} / state.alert = ${state.alert}"
	if (unsecure || state.alert) {        
        if (state.alert && !state.userAlarm) {
        	if (state.change && newResult) {
            	state.alertMessage = state.alertMessage + "\n${newResult}"
            }
       		log.debug "state.alertMessage = ${state.alertMessage}"
        	msg = "${state.alertMessage}"
	        if (sendPush || alarmSendPush) {
            	sendPush(msg)
            }
            if (alarmPhone) {
            	sendSms(alarmPhone, msg)
            }
            if (alarmPhone2) {
            	sendSms(alarmPhone2, msg)
            }
        }
        else {
	    	log.debug "Potential Alert triggered: Setting delay timer.\n${msg}"
     		if (!state.userAlarm) {
	            state.alertMessage = msg
            }
            setAlertTimer()        
        }
	}
}

private setDisarmed() {
	log.debug "setDisarmed"
    log.debug "alarmSensor.alarmState = ${alarmSensor.currentValue("alarmState")} / state.alarmState = ${state.alarmState}"
    def curState = state.alarmState
    state.alarmState = "Disarmed"
    switch (curState) {
    	case "Armed Home":
            if (state.alert) {
            	log.debug "Alarm triggered!  Leaving on lights and setting alarm to strobe."
            	homeAlarm?.strobe()
            }
            else {
            	//sendSms(phone, "Turning off lights and alarm...")
	        	log.debug "Turning off lights and alarm"
	            homeAlarm?.off()
            	homeInidcatorLight?.off()
	    		turnOffAlertLights()
            }
            break
        case "Armed Away":
            if (state.alert) {
            	log.debug "Alarm triggered!  Leaving on lights and setting alarm to strobe."
            	awayAlarm?.strobe()
            }
            else {
	        	log.debug "Turning off lights and alarm"
                awayAlarm?.off()
                awayIndicatorLight?.off()
                turnOffAlertLights()
            }
        	break
        default:
        	break
    }
//    sendSms(alarmPhone2, "Calling alarmSensor.setDisarmed()")
    alarmSensor.setDisarmed()
}

private setArmedHome() {
	log.debug "setArmedHome"
    state.alarmState = "Armed Home"
    alarmSensor.setArmedHome()
   
    if (homeIndicatorLight) {
        def hueVal = 97
        def satVal = 99
    	switch (homeIndicatorLightColor) {
        	case "RED":
            	hueVal = 97
                satVal = 99
            	break
            case "GREEN":
            	hueVal = 31
                satVal = 90
            	break
            case "BLUE":
            	hueVal = 66
                satVal = 95 
            	break
            case "WHITE":
            	hueVal = 11
                satVal = 6
            	break
            default:
            	break
        }
        homeIndicatorLight?.on()
        homeIndicatorLight?.setColor([hue: hueVal, saturation: satVal])
    }
}

private setArmedAway() {
	log.debug "setArmedAway"
    state.alarmState = "Armed Away"
    alarmSensor.setArmedAway()
   
    if (awayIndicatorLight) {
        def hueVal = 97
        def satVal = 99
    	switch (awayIndicatorLightColor) {
        	case "RED":
            	hueVal = 97
                satVal = 99
            	break
            case "GREEN":
            	hueVal = 31
                satVal = 90
            	break
            case "BLUE":
            	hueVal = 66
                satVal = 95 
            	break
            case "WHITE":
            	hueVal = 11
                satVal = 6
            	break
            default:
            	break
        }
        awayIndicatorLight?.on()
        awayIndicatorLight?.setColor([hue: hueVal, saturation: satVal])
    }
}

private setAlertTimer() {
	log.debug "setAlertTimer"
    def timer = (alarmSensor.currentValue("alarmState") == "Armed Away") ? awayAlertDelay : homeAlertDelay
    log.debug "Alert Timer = ${timer} seconds"
    if (alarmSensor.currentValue("alarmState") == "Armed Away") {
	    awayAlarm.strobe()
    }
    else {
	    homeAlarm.strobe()
    }
    runIn(timer, checkAlert)
}

def checkAlert() {
	def alarmAlarmState = alarmSensor.currentValue("alarmState")
    def alarmAlertState = alarmSensor.currentValue("alertState")
	log.debug "checkAlert: alarmState = ${alarmAlarmState} / alertState = ${alarmAlertState}"
    if (alarmAlarmState != "Disarmed" || alarmAlertState == "userAlarm") {
    	log.debug "Trigger Alert!"
        alarmSensor.setAlert()
        triggerLights()
        state.alert = true
        state.unsecure = true
        if (sendPush || alarmSendPush) {
            sendPush(state.alertMessage)
        }
        if (alarmPhone) {
        	log.debug "Sending SMS: ${state.alertMessage}"
            sendSms(alarmPhone, state.alertMessage)
        }
        if (alarmPhone2) {
        	log.debug "Sending SMS: ${state.alertMessage}"
            sendSms(alarmPhone2, state.alertMessage)
        }
    }
    else {
    	log.debug "Alarm Disarmed during delay."
    }
    state.change = false
}

def alarmStrobeOn() {
	log.debug "alarmStrobeOn"
    homeAlarm.strobe()
}

private triggerLights() {
	log.debug "triggerLights"
    def daylight = getSunriseAndSunset()
    def turnOn = false
    def result = ""
    def alarmAlarmState = alarmSensor.currentValue("alarmState")
    
    if (state.userAlarm) {
    	alarmAlarmState = "Armed Home"
    }
    switch (alarmAlarmState) {
    	case "Armed Home":
        	log.debug "checking homeAlarm settings: ${homeAlarm} ${homeAlarmLightsSirens}"
            if (homeAlarm && homeAlarmLightsSirens != "OFF") {
                switch (homeAlarmLightsSirens) {
                	case "Both":
                    	homeAlarm.both()
                    	break
                    case "Siren":
                    	homeAlarm.siren()
                    	break
                    case "Strobe":
                    	homeAlarm.strobe()
                    	break
                    case "Beep":
                    	homeAlarm.beep()
                        runIn(5, alarmStrobeOn)
                        break
                    default:
                    	homeAlarm.off()
                    	break
                }
            }
        	log.debug "checking homeAlertSwitches...Dark Only = ${homeAlertSwitchesDarkOnly}"
            if (homeAlertSwitchesDarkOnly) {
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
            log.debug "turnOn = ${turnOn} / homeIndicatorLight = ${homeIndicatorLight} / homeAlertLightColor = ${homeAlertLightColor}"
            if (turnOn || homeIndicatorLight || homeAlertLightColor != "OFF") {
            	if (turnOn) {
                    log.debug "turning on homeAlertSwitches: ${homeAlertSwitches}"
                    state.offSwitches = "${homeAlertSwitches?.findAll{it.currentValue("switch") == "off"}}"
                    log.debug "state.offSwitches = ${state.offSwitches}"
                    homeAlertSwitches?.on()
			    }
                log.debug "homeAlertLightColor = ${homeAlertLightColor}"
                if (homeAlertLightColor != "OFF") {
                	log.debug "homeAlertLight found...  state.offSwitches = ${state.offSwitches}"
                    def caps = ""
                    state.offAlertSwitches = ""
                    homeAlertSwitches.each {it ->
                        caps = it.capabilities
                        caps.each {cap ->
                            if (cap.name == "Color Control" || cap.name == "Color Temperature") {
                                log.debug "Color Bulb = ${it.name}"
                                if (!state.offAlertSwitches.contains(it.name)) {
                                	if (state.offAlertSwitches.size() > 0) {
                                    	state.offAlertSwitches = state.offAlertSwitches + ", "
                                    }
                                	state.offAlertSwitches = state.offAlertSwitches + it.name
                                }
                                switch (homeAlertLightColor) {
                                	case "RED":
                                		it.setColor([hue: 97, saturation: 99])
                                    	break
                                    case "BLUE":
                                		it.setColor([hue: 66, saturation: 95])
                                    	break
                                    case "GREEN":
                                		it.setColor([hue: 31, saturation: 90])
                                    	break
                                    case "WHITE":
                                		it.setColor([hue: 11, saturation: 6])
                                    	break
                                    default:
                                		it.setColor([hue: 97, saturation: 99])
                                    	break
                                }
                            }
                        }
                    }
                }
            }
            else {
            	state.offSwitches = ""
            }
            log.debug "state.offAlertSwitches = ${state.offAlertSwitches}"
        	break
        case "Armed Away":
        	log.debug "checking awayAlertSwitches...Dark Only = ${awayAlertSwitchesDarkOnly}"
        	log.debug "checking awayAlarm settings: ${awayAlarm} ${awayAlarmLightsSirens}"
            if (awayAlarm && awayAlarmLightsSirens != "OFF") {
                switch (awayAlarmLightsSirens) {
                	case "Both":
                    	awayAlarm.both()
                    	break
                    case "Siren":
                    	awayAlarm.siren()
                    	break
                    case "Strobe":
                    	awayAlarm.strobe()
                    	break
                    case "Beep":
                    	awayAlarm.beep()
                        runIn(5, alarmStrobeOn)
                        break
                    default:
                    	awayAlarm.off()
                    	break
                }
            }
            if (awayAlertSwitchesDarkOnly) {
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
            if (turnOn || awayIndicatorLight || awayAlertLightColor != "OFF") {
            	if (turnOn) {
                    log.debug "turning on awayAlertSwitches: ${awayAlertSwitches}"
                    state.offSwitches = "${awayAlertSwitches?.findAll{it.currentValue("switch") == "off"}}"
                    log.debug "state.offSwitches = ${state.offSwitches}"
                    awayAlertSwitches?.on()
                }
                if (awayAlertLightColor != "OFF") {
                    def caps = "" 
                    state.offAlertSwitches = ""
                    awayAlertSwitches.each {it ->
                        caps = it.capabilities
                        caps.each {cap ->
                            if (cap.name == "Color Control" || cap.name == "Color Temperature") {
                                log.debug "Color Bulb = ${it.name}"
                                if (!state.offAlertSwitches.contains(it.name)) {
                                	if (state.offAlertSwitches.size() > 0) {
                                    	state.offAlertSwitches = state.offAlertSwitches + ", "
                                    }
                                	state.offAlertSwitches = state.offAlertSwitches + it.name
                                }
                                log.debug "awayAlertLightColor = ${awayAlertLightColor}"
                                switch (awayAlertLightColor) {
                                	case "RED":
                                		it.setColor([hue: 97, saturation: 99])
                                    	break
                                    case "BLUE":
                                		it.setColor([hue: 66, saturation: 95])
                                    	break
                                    case "GREEN":
                                		it.setColor([hue: 31, saturation: 90])
                                    	break
                                    case "WHITE":
                                		it.setColor([hue: 11, saturation: 6])
                                    	break
                                    default:
                                		it.setColor([hue: 97, saturation: 99])
                                    	break
                                }
                            }
                        }
                    }
                }                
            }
            else {
            	state.offSwitches = ""
            }
            log.debug "state.offAlertSwitches = ${state.offAlertSwitches}"
        	break
        default:
        	break
    }
}

private turnOffAlertLights() {
    def alarmAlarmState = alarmSensor.currentValue("alarmState")
    log.debug "turnOffAlertLights: alarmState = ${alarmAlarmState} / alertState = ${alarmSensor.currentValue("alertState")}"
    
    if (state.userAlarm) {
    	alarmAlarmState = "Armed Home"
    }
    switch (alarmAlarmState) {
    	case "Armed Home":
            log.debug "state.offSwitches = ${state.offSwitches}"
            log.debug "homeAlertSwitches.size() = ${homeAlertSwitches.size()}"    
            if (state.offSwitches.size() > 2) {
                for (int i = 0; i < homeAlertSwitches.size(); i++) {
                    log.debug "homeAlertSwitches[${i}] = ${homeAlertSwitches[i]}"
                    if (state.offSwitches.contains("${homeAlertSwitches[i]}")) {
                        log.debug "Turning off ${homeAlertSwitches[i]}"
                        homeAlertSwitches[i].off()
                    }
                }
            }
            if (state.offAlertSwitches.size() > 0) {
            	for (int i = 0; i < homeAlertSwitches.size(); i++) {
                	if (state.offAlertSwitches.contains("${homeAlertSwitches[i]}")) {
                    	log.debug "Turning off alert switch ${homeAlertSwitches[i]}"
                        homeAlertSwitches[i].off()
                    }
                }
            }
        	log.debug "homeIndicatorLight = ${homeIndicatorLight?.currentValue("switch")}"
            if (state.alarmState == "Disarmed") {
            	homeIndicatorLight?.off()
            }
            else {
                if (homeIndicatorLight?.find{it.currentValue("switch") == "on"}) {
                    def hueVal = 97
                    def satVal = 99
                    log.debug "homeIndicatorLightColor = ${homeIndicatorLightColor}"
                    switch (homeIndicatorLightColor) {
                        case "RED":
                            hueVal = 97
                            satVal = 99
                            break
                        case "GREEN":
                            hueVal = 31
                            satVal = 90
                            break
                        case "BLUE":
                            hueVal = 66
                            satVal = 95 
                            break
                        case "WHITE":
                            hueVal = 11
                            satVal = 6
                            break
                        default:
                            break
                    }
                    homeIndicatorLight?.setColor([hue: hueVal, saturation: satVal])
                    log.debug "Resetting homeIndicatorLight color to ${homeIndicatorLightColor}"
                }
            }
        	break
        case "Armed Away":
            log.debug "state.offSwitches = ${state.offSwitches}"
            log.debug "awayAlertSwitches.size() = ${awayAlertSwitches.size()}"    
            if (state.offSwitches.size() > 2) {
                for (int i=0; i < awayAlertSwitches.size(); i++) {
                    log.debug "awayAlertSwitches[${i}] = ${awayAlertSwitches[i]}"
                    if (state.offSwitches.contains("${awayAlertSwitches[i]}")) {
                        log.debug "Turning off ${awayAlertSwitches[i]}"
                        awayAlertSwitches[i].off()
                    }
                }
            }
            if (state.offAlertSwitches.size() > 0) {
            	for (int i = 0; i < awayAlertSwitches.size(); i++) {
                	if (state.offAlertSwitches.contains("${awayAlertSwitches[i]}")) {
                    	log.debug "Turning off alert switch ${awayAlertSwitches[i]}"
                        awayAlertSwitches[i].off()
                    }
                }
            }
        	log.debug "awayIndicatorLight = ${awayIndicatorLight?.currentValue("switch")}"
            if (state.alarmSate == "Disarmed") {
            	awayIndicatorLight?.off()
            }
            else {
                if (awayIndicatorLight?.find{it.currentValue("switch") == "on"}) {
                    def hueVal = 97
                    def satVal = 99
                    switch (awayIndicatorLightColor) {
                        case "RED":
                            hueVal = 97
                            satVal = 99
                            break
                        case "GREEN":
                            hueVal = 31
                            satVal = 90
                            break
                        case "BLUE":
                            hueVal = 66
                            satVal = 95 
                            break
                        case "WHITE":
                            hueVal = 11
                            satVal = 6
                            break
                        default:
                            break
                    }
                    awayIndicatorLight?.setColor([hue: hueVal, saturation: satVal])
                    log.debug "Resetting awayIndicatorLight color to ${awayIndicatorLightColor}"
                }
            }
        	break
        default:
        	log.debug "Turning off Indicator Light"
        	homeIndicatorLight?.off()
            awayIndicatorLight?.off()
        	break
    }
}

private getStatus() {
	log.debug "getStatus(): state.unsecure = ${state.unsecure}"
    log.debug "perimeterSensor = ${perimeterSensor.currentValue("contact")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nPerimeter Unsecure: "
	def unsecure = false
    def result = ""
    def motions = ""
    def contacts = ""
    def doors = ""
    def locks = ""
    
    switch (alarmSensor.currentValue("alarmState")) {
    	case "Armed Home":
        	motions = homeMotions
        	contacts = homeContacts
            doors = homeDoors
            locks = homeLocks
        	break
        case "Armed Away":
        	motions = awayMotions
        	contacts = awayContacts
            doors = awayDoors
            locks = awayLocks
        	break
        default:
        	break
    }
    
    result = motionss.findAll{it.currentValue("motion") == "active"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
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
        if (sendPush || alarmSendPush) {
            sendPush(msg)
        }
        if (alarmPhone) {
            sendSms(alarmPhone, msg)
        }
        if (alarmPhone2) {
            sendSms(alarmPhone2, msg)
        }
    }
    else if (unsecure == false) {
    	log.debug "Perimter secure!"
        state.unsecure = false
        msg = "${location} ${date}\nPerimeter secured!"
        if (sendPush || alarmSendPush) {
            sendPush(msg)
        }
        if (alarmPhone) {
            sendSms(alarmPhone, msg)
        }
        if (alarmPhone2) {
            sendSms(alarmPhone2, msg)
        }
    }
    state.change = false
}

private whoIsHome() {
	log.debug "whoIsHome"
    def result = ""
    
    result = "${presence1.findAll{it.currentValue("presence") == "present"}}"
	log.debug "whoIsHome = ${result}"
    return result
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

def runEveningSchedule(evt) {
	log.debug "Running Evening Schedule."
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
    def message = "Something went wrong!"
    def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

	if (presenceValue) { 
    	log.debug "Someone is home in the evening..."
        if (alarmActive && eveningHomeSetAlarm) { 
        	def newAlarmMsg = "Setting Alarm Controller to: Armed Home"
            setArmedHome()
            alarmMsg = "Alarm Armed Home"
        }
        location.helloHome?.execute(settings.eveningHomeAlarmRoutine)
        location.helloHome?.execute(settings.eveningHomeRoutine)
        eveningHomeOnSwitches?.on()
        eveningHomeOffSwitches?.off()
        if (location.mode != eveningHomeMode) {
        	if (location.modes?.find{it.name == eveningHomeMode}) {
            	setLocationMode(eveningHomeMode)
                modeMsg = eveningHomeMode
            }
        }
        if (settings.eveningHomeAlarmRoutine) {
        	alarmMsg = settings.eveningHomeAlarmRoutine
        }
        message = "${location} ${date}: Someone is home in the evening: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || eveningHomePush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (eveningHomePhone) {
        	sendSms(eveningHomePhone, message)
        }
    }
    else {
    	log.debug "Everyone is away in the evening..."
        location.helloHome?.execute(settings.eveningAwayAlarmRoutine)
        location.helloHome?.execute(settings.eveningAwayRoutine)
        eveningAwayOnSwitches?.on()
        eveningAwayOffSwitches?.off()
        if (location.mode != eveningAwayMode) {
        	if (location.modes?.find{it.name == eveningAwayMode}) {
            	setLocationMode(eveningAwayMode)
                modeMsg = eveningAwayMode
            }
        }
        if (settings.eveningAwayRoutine) {
        	alarmMsg = settings.eveningAwayRoutine
        }
        message = "${location} ${date}: Everyone is away in the evening: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || eveningAwayPush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (eveningAwayPhone) {
        	sendSms(eveningAwayPhone, message)
        }
    }
}

def checkDisarm() {
	log.debug "checkDisarm"
    if (alarmSensor.currentValue("alarmState") != "Disarmed") {
    	sendSms(alarmPhone2, "Second disarm attempt...")
        setDisarmed()
    }
}

def morningHandler(evt) {
	log.debug "Running Morning Schedule."
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
    def message = "Something went wrong!"
    def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

	if (presenceValue) { 
    	log.debug "Someone is home in the morning..."
        if (alarmActive && morningHomeSetAlarm) {
        	sendSms(phone, "Disarming Alarm...")
        	def newAlarmMsg = "Setting Alarm Controller to: Disarmed"
            setDisarmed()
            alarmMsg = "Alarm Disarmed"
            runIn(60, checkDisarm)
//            *jdf*
        }
        else {
        	sendSms(phone, "Not Disarming Alarm...")
        }
        location.helloHome?.execute(settings.morningHomeAlarmRoutine)
        location.helloHome?.execute(settings.morningHomeRoutine)
        morningHomeOnSwitches?.on()
        morningHomeOffSwitches?.off()
        if (location.mode != morningHomeMode) {
        	if (location.modes?.find{it.name == morningHomeMode}) {
            	setLocationMode(morningHomeMode)
                modeMsg = morningHomeMode
            }
        }
        if (settings.morningHomeAlarmRoutine) {
        	alarmMsg = settings.morningHomeAlarmRoutine
        }
        message = "${location} ${date}: Someone is home in the morning: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || morningHomePush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (morningHomePhone) {
        	sendSms(morningHomePhone, message)
        }
    }
    else {
    	log.debug "Everyone is away in the morning..."
//        *jdf*
        sendSms(phone, "Everyone Away. Not Disarming Alarm...")
        location.helloHome?.execute(settings.morningAwayAlarmRoutine)
        location.helloHome?.execute(settings.morningAwayRoutine)
        morningAwayOnSwitches?.on()
        morningAwayOffSwitches?.of()
        if (location.mode != morningAwayMode) {
        	if (location.modes?.find{it.name == morningAwayMode}) {
            	setLocationMode(morningAwayMode)
                modeMsg = morningAwayMode
            }
        }
        if (settings.morningAwayAlarmRoutine) {
        	alarmMsg = settings.morningAwayAlarmRoutine
        }
        message = "${location} ${date}: Everyone is away in the morning: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || morningAwayPush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (morningAwayPhone) {
        	sendSms(morningAwayPhone, message)
        }
    }
}

def nightHandler(evt) {
	log.debug "Running Night Time Schedule."
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
    def message = "Something went wrong!"
    def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

	if (presenceValue) { 
    	log.debug "Someone is home at night..."
        if (alarmActive && nightHomeSetAlarm) {
        	def newAlarmMsg = "Setting Alarm Controller to: Armed Home"
            setArmedHome()
            alarmMsg = "Alarm Armed Home"
        }
        location.helloHome?.execute(settings.nightHomeAlarmRoutine)
        location.helloHome?.execute(settings.nightHomeRoutine)
        nightHomeOnSwitches?.on()
        nightHomeOffSwitches?.off()
        if (location.mode != nightHomeMode) {
        	if (location.modes?.find{it.name == nightHomeMode}) {
            	setLocationMode(nightHomeMode)
                modeMsg = nightHomeMode
            }
        }
        if (settings.nightHomeAlarmRoutine) {
        	alarmMsg = settings.nightHomeAlarmRoutine
        }
        message = "${location} ${date}: Someone is home at night: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || nightHomePush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (nightHomePhone) {
        	sendSms(nightHomePhone, message)
        }
    }
    else {
    	log.debug "Everyone is away at night..."
        location.helloHome?.execute(settings.nightAwayAlarmRoutine)
        location.helloHome?.execute(settings.nightAwayRoutine)
        nightAwayOnSwitches?.on()
        nightAwayOffSwitches?.off()
        if (location.mode != nightAwayMode) {
        	if (location.modes?.find{it.name == nightAwayMode}) {
            	setLocationMode(nightAwayMode)
                modeMsg = nightAwayMode
            }
        }
        if (settings.nightAwayAlarmRoutine) {
        	alarmMsg = settings.nightAwayAlarmRoutine
        }
        message = "${location} ${date}: Everyone is away at night: ${alarmMsg} / Mode: ${modeMsg}"
        if (sendPush || nightAwayPush) {
            sendPush(message)
        }
        if (phone) {
            sendSms(phone, message)
        }        
        if (nightAwayPhone) {
        	sendSms(nightAwayPhone, message)
        }
    }
}