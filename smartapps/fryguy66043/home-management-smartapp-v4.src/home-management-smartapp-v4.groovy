/**
 *  Home Management SmartApp v4
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
    name: "Home Management SmartApp v4",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "One source to manage home automation.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png"
//    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
//    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	page(name: "arrivalPage")
    page(name: "departurePage")
    page(name: "visitorPage")
    page(name: "eveningPage")
    page(name: "nightPage")
    page(name: "morningPage")
    page(name: "notificationPage")
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
//            	input "visitorGarageDoor", "capability.garageDoorControl", required: false, title: "Garage Door"
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
                input "visitorArrivalOnSwitchesDarkOnly", "boolean", title: "Turn on only if visitor arrives after dark?"
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
    subscribe(location, "sunriseTime", sunriseTimeHandler)
    //schedule it to run today too
    scheduleTurnOn(location.currentValue("sunsetTime"))
    if (morningTimeOrSunrise == "Time") {
		schedule(morningTime, morningHandler)
    }
    else {
    	scheduleSunriseTurnOn(location.currentValue("sunriseTime"))
    }
    schedule(nightTime, nightHandler)
    //visitor handling
    subscribe(visitorExpectedSwitch, "switch", visitorExpectedSwitchHandler)
    subscribe(visitorPresentSwitch, "switch", visitorPresentSwitchHandler)
    subscribe(visitorVirtualGarageDoor, "contact", visitorVirtualGarageDoorHandler)
//    subscribe(visitorGarageDoor, "door", visitorGarageDoorHandler)
    subscribe(visitorGarageDoor, "contact", visitorGarageDoorHandler)
    subscribe(visitorVirtualDoor, "contact", visitorVirtualDoorHandler)
    subscribe(visitorDoor, "contact", visitorDoorHandler)
}

def updated()
{
	unsubscribe()
    installed()
}

def visitorExpectedSwitchHandler(evt) {	
	def msg = "visitorExpectedSwitch.currentSwitch == ${visitorExpectedSwitch.currentSwitch}\nlocation.mode == ${location.mode}\n"
    msg = msg + "departureMode == ${departureMode} / visitorExpectedMode == ${visitorExpectedMode}"
	if (visitorPhone) {
		sendSms(visitorPhone, msg)
    }
    if (visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == departureMode) {
            if (location.modes?.find{it.name == visitorExpectedMode}) {
                setLocationMode(visitorExpectedMode)
                msg = "${location}: ${visitorExpectedSwitch} activated while away.  Setting Mode to ${visitorExpectedMode}"
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
        	if (location.mode == visitorExpectedMode) {
            	setLocationMode(departureMode)
                msg = "${location}: ${visitorExpectedSwitch} deactivated while away.  Setting Mode to ${departureMode}"
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
    if (visitorPhone) {
		sendSms(visitorPhone, "visitorVirtualGarageDoorHandler: ${evt.value}")
    }
	def msg = ""
    
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

	if (true && visitorExpectedSwitch.currentSwitch == "on") {
//    if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
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
                	msg = "${location}: Possible Visitor departure.  Setting timer for ${visitorDepartureAlarmRoutineDelay} minutes"
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
//    if (visitorPhone) {
//		sendSms(visitorPhone, "visitorGarageDoorHandler: ${evt.value}")
//    }
	def msg = ""
    
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
                if (diff > 10) {
                	msg = "${location}: Possible Visitor departure.  Setting timer for ${visitorDepartureAlarmRoutineDelay} minutes"
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
            state.doorOpened = true
        	break
        case "closed":
        	state.doorClosedTime = now()
        	state.doorClosed = true
        	break
        default:
        	break
    }
	if (true && visitorExpectedSwitch.currentSwitch == "on") {
//	if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == visitorArrivalMode) {
			if (state.doorOpened && state.garageDoorOpened) {
                visitorPresentSwitch.on()
                visitorPresentPresenceSensor.present()
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
//	if (true && visitorExpectedSwitch.currentSwitch == "on") {
	if (everyoneIsAway() && visitorExpectedSwitch.currentSwitch == "on") {
        if (location.mode == visitorArrivalMode) {
			if (state.doorOpened && state.garageDoorOpened) {
                visitorPresentSwitch.on()
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
	def msg = "${location}: Visitor Arrival!"
    def daylight = getSunriseAndSunset()

    if (visitorPush) {
    	sendPush(msg)
    }
    if (visitorPhone) {
    	sendSms(visitorPhone, msg)
    }
    location.helloHome?.execute(settings.visitorArrivalAlarmRoutine)
    location.helloHome?.execute(settings.visitorArrivalRoutine)
    setLocationMode(visitorArrivalMode)
    state.visitorDeparted = false
/*	
    visitorPresentSwitch.on()
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
*/    
}

def visitorDeparture(evt) {
	log.debug "visitorDeparture"
    log.debug "state.visitorDeparted == ${state.visitorDeparted}"
	def msg = "${location}: Visitor Departed!"
    
	if (state.visitorDeparted) {
    	if (visitorPush) {
        	sendPush(msg)
        }
        if (visitorPhone) {
        	sendSms(visitorPhone, msg)
        }
        location.helloHome?.execute(settings.visitorDepartureAlarmRoutine)
        location.helloHome?.execute(settings.visitorDepartureRoutine)
        setLocationMode(visitorDepartureMode)
        visitorPresentSwitch.off()
        if (state.visitorSwitchesTurnedOn) {
        	visitorArrivalOnSwitches?.off()
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
/*
	import static java.util.Calendar.*

    def localTimeZone = TimeZone.getTimeZone("America/Chicago")
    def utcTimeZone = TimeZone.getTimeZone("UTC")

	def cal = Calendar.instance
    cal.set(year: 2011, month: OCTOBER, date: 20, hourOfDay: 12, minute: 30)

    def date = cal.time
    def dateFormat = 'yyyy/MM/dd HH:mm'
*/

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
    	sendSms("9136831550", message)
    }
}

def arrivalHandler(evt)
{
	def message = "Something went wrong!"
	def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"
    if (firstOneHome()) {
        log.debug "Someone Has Arrived: ${settings.arrivalAlarmRoutine}"
        location.helloHome?.execute(settings.arrivalAlarmRoutine)
        location.helloHome?.execute(settings.arrivalRoutine)
        state.schedule = "arrival"
        state.presence = "home"
        state.tryCount = 1
//        turnSwitchesOn()
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
        message = "${location}: First One Home! ${alarmMsg} / Mode: ${modeMsg}"
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

def departureHandler(evt)
{
	def message = "Something went wrong!"
	def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"

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
        message = "${location}: Performing Goodbye Home! ${alarmMsg} / Mode: ${modeMsg}"
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

/*
def turnSwitchesOn() {
	def checkTime = state.checkTime

 	sendSms("9136679526", "turnSwitchesOn: ${state.switches} / ${state.schedule} / ${state.presence} / ${state.tryCount}")
    
    state.switches.on()
    sendSms("9136679526", "Calling checkOnSwitches")
    runIn(30, checkOnSwitches)
//	checkOnSwitches()
}

def checkOnSwitches() {
	def checkTime = 0
	sendSms("9136679526", "In checkOnSwitches / state.switchSize.size() = ${state.switchSize.size()} / Try = ${state.tryCount}")    

	if (state.tryCount < 4) {
//        def switchList = state.switches.currentSwitch
        def onSwitches = state.switchSize.findAll { switchVal ->
            switchVal == "on" ? true : false
        }
        sendSms("9136831550", "onSwitches.size() == ${onSwitches.size()} / state.switchSize.size() == ${state.switchSize.size()}")
        if (onSwitches.size() != state.switchSize.size()) {
        	sendSms("9136831550", "Turning on...")
            state.switches?.on()
            state.tryCount = state.tryCount + 1
            runIn(30, finalCheckOnSwitches)
        }
    }
    else {
    	sendSms("9136831550", "Failed: ${state.schedule} / ${state.presence}")
    }
	
/*
	switch(state.schedule) {
    	case "arrival":
        	if (arrivalOnSwitches) {
            	def switchList = arrivalOnSwitches.currentSwitch
                def onSwitches = switchList.findAll { switchVal ->
                	switchVal == "on" ? true : false
                }
                sendSms("9136831550", "onSwitches.size() == ${onSwitches.size()} / arrivalOnSwitches.size() == ${arrivalOnSwitches.size()}")
                if (onSwitches.size() != arrivalOnSwitches.size()) {
                    if (state.tryCount < 3) {
                    	
                        state.tryCount = state.tryCount + 1
                        checkTime = state.tryCount * state.checkTime
                        sendSms("9136831550", "Arrival Switch On Failure:  Retry Count = ${state.tryCount}")
                        runIn(checkTime, turnOnSwitches)
                    }
                    else {
                        sendSms("9136831550", "Arrival Switch On Failure!")
                    }
                }
                else {
                    sendSms("9136831550", "It worked!")
                }
            }
        	break
        case "departure":
        	break
        case "night":
        	sendSms("9136679526", "Night Test")
            if (state.presence == "home") {
            	if (nightHomeOnSwitches) {
                    def switchList = nightHomeOnSwitches.currentSwitch
                    def onSwitches = switchList.findAll { switchVal ->
                        switchVal == "on" ? true : false
                    }
                    sendSms("9136679526", "onSwitches.size() == ${onSwitches.size()} / switchList.size() == ${switchList.size()}")
                    if (onSwitches.size() != switchList.size()) {
                        if (state.tryCount < 3) {
                        	nightHomeOnSwitches.on()
                            state.tryCount = state.tryCount + 1
                            checkTime = state.checkTime
                            sendSms("9136679526", "Night Home Switch On Failure:  checkTime = ${checkTime} Retry Count = ${state.tryCount}")
                            runIn(checkTime, checkOnSwitches)
                        }
                        else {
                            sendSms("9136679526", "Night Home Switch On Failure!")
                        }
                    }
                    else {
                        sendSms("9136679526", "It worked!")
                    }
                }
                else {	
                	sendSms("9136679526", "Nothing to turn on!")
                }
            }
            else {
            	if (nightAwayOnSwitches) {
                	if (nightAwayOnSwitches != "on") {
                        if (state.tryCount < 3) {
                            state.tryCount = state.tryCount + 1
                            checkTime = state.tryCount * state.checkTime
                            sendSms("9136679526", "Night Away Switch On Failure:  checkTime = ${checkTime} Retry Count = ${state.tryCount}")
                            runIn(checkTime, turnOnSwitches)
                        }
                        else {
                            sendSms("9136679526", "Night Away Switch On Failure!")
                        }
                    }
                    else {
                    	sendSms("9136679526", "It worked!")
                    }
                }
            }
        	break
        case "evening":
        	if (eveningOnSwitches != "on") {
            	if (state.tryCount < 3) {
                	state.tryCount = state.tryCount + 1
                    checkTime = state.tryCount * state.checkTime
                	sendSms("9136831550", "Evening Switch On Failure:  Retry Count = ${state.tryCount}")
                    runIn(checkTime, checkOnSwitches)
                }
                else {
                	sendSms("9136831550", "Evening Switch On Failure!")
                }
            }
        	break
        case "morning":
        	break
        default:
        	break
    }
//
}
*/

/*
def finalCheckOnSwitches() {
	def checkTime = 0
	sendSms("9136679526", "In finalCheckOnSwitches / state.switchSize.size() = ${state.switchSize.size()} / Try = ${state.tryCount}")    

	if (state.tryCount < 4) {
//        def switchList = state.switches.currentSwitch
        def onSwitches = state.switchSize.findAll { switchVal ->
            switchVal == "on" ? true : false
        }
        sendSms("9136831550", "onSwitches.size() == ${onSwitches.size()} / state.switchSize.size() == ${state.switchSize.size()}")
        if (onSwitches.size() != state.switchSize.size()) {
        	sendSms("9136831550", "Turning on...")
            state.switches?.on()
            state.tryCount = state.tryCount + 1
//            runIn(30, checkOnSwitches)
        }
    }
    else {
    	sendSms("9136831550", "Failed: ${state.schedule} / ${state.presence}")
    }
	
/*
	switch(state.schedule) {
    	case "arrival":
        	if (arrivalOnSwitches) {
            	def switchList = arrivalOnSwitches.currentSwitch
                def onSwitches = switchList.findAll { switchVal ->
                	switchVal == "on" ? true : false
                }
                sendSms("9136831550", "onSwitches.size() == ${onSwitches.size()} / arrivalOnSwitches.size() == ${arrivalOnSwitches.size()}")
                if (onSwitches.size() != arrivalOnSwitches.size()) {
                    if (state.tryCount < 3) {
                    	
                        state.tryCount = state.tryCount + 1
                        checkTime = state.tryCount * state.checkTime
                        sendSms("9136831550", "Arrival Switch On Failure:  Retry Count = ${state.tryCount}")
                        runIn(checkTime, turnOnSwitches)
                    }
                    else {
                        sendSms("9136831550", "Arrival Switch On Failure!")
                    }
                }
                else {
                    sendSms("9136831550", "It worked!")
                }
            }
        	break
        case "departure":
        	break
        case "night":
        	sendSms("9136679526", "Night Test")
            if (state.presence == "home") {
            	if (nightHomeOnSwitches) {
                    def switchList = nightHomeOnSwitches.currentSwitch
                    def onSwitches = switchList.findAll { switchVal ->
                        switchVal == "on" ? true : false
                    }
                    sendSms("9136679526", "onSwitches.size() == ${onSwitches.size()} / switchList.size() == ${switchList.size()}")
                    if (onSwitches.size() != switchList.size()) {
                        if (state.tryCount < 3) {
                        	nightHomeOnSwitches.on()
                            state.tryCount = state.tryCount + 1
                            checkTime = state.checkTime
                            sendSms("9136679526", "Night Home Switch On Failure:  checkTime = ${checkTime} Retry Count = ${state.tryCount}")
                            runIn(checkTime, checkOnSwitches)
                        }
                        else {
                            sendSms("9136679526", "Night Home Switch On Failure!")
                        }
                    }
                    else {
                        sendSms("9136679526", "It worked!")
                    }
                }
                else {	
                	sendSms("9136679526", "Nothing to turn on!")
                }
            }
            else {
            	if (nightAwayOnSwitches) {
                	if (nightAwayOnSwitches != "on") {
                        if (state.tryCount < 3) {
                            state.tryCount = state.tryCount + 1
                            checkTime = state.tryCount * state.checkTime
                            sendSms("9136679526", "Night Away Switch On Failure:  checkTime = ${checkTime} Retry Count = ${state.tryCount}")
                            runIn(checkTime, turnOnSwitches)
                        }
                        else {
                            sendSms("9136679526", "Night Away Switch On Failure!")
                        }
                    }
                    else {
                    	sendSms("9136679526", "It worked!")
                    }
                }
            }
        	break
        case "evening":
        	if (eveningOnSwitches != "on") {
            	if (state.tryCount < 3) {
                	state.tryCount = state.tryCount + 1
                    checkTime = state.tryCount * state.checkTime
                	sendSms("9136831550", "Evening Switch On Failure:  Retry Count = ${state.tryCount}")
                    runIn(checkTime, checkOnSwitches)
                }
                else {
                	sendSms("9136831550", "Evening Switch On Failure!")
                }
            }
        	break
        case "morning":
        	break
        default:
        	break
    }

}
*/

def runEveningSchedule(evt) {
	log.debug "Running Evening Schedule."
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
    def message = "Something went wrong!"
    def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"

	if (presenceValue) { 
    	log.debug "Someone is home in the evening..."
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
        message = "${location}: Someone is home in the evening: ${alarmMsg} / Mode: ${modeMsg}"
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
        message = "${location}: Everyone is away in the evening: ${alarmMsg} / Mode: ${modeMsg}"
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

def morningHandler(evt) {
	log.debug "Running Morning Schedule."
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
    def message = "Something went wrong!"
    def alarmMsg = "Alarm not changed"
    def modeMsg = "Not changed"

	if (presenceValue) { 
    	log.debug "Someone is home in the morning..."
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
        message = "${location}: Someone is home in the morning: ${alarmMsg} / Mode: ${modeMsg}"
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
        message = "${location}: Everyone is away in the morning: ${alarmMsg} / Mode: ${modeMsg}"
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

	if (presenceValue) { 
    	log.debug "Someone is home at night..."
        location.helloHome?.execute(settings.nightHomeAlarmRoutine)
        location.helloHome?.execute(settings.nightHomeRoutine)
/*		
        if (nightHomeOnSwitches) {
            state.schedule = "night"
            state.presence = "home"
            state.tryCount = 0
            state.switches = nightHomeOnSwitches
            state.switchSize = nightHomeOnSwitches.currentSwitch
            turnSwitchesOn()
        }
*/        
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
        message = "${location}: Someone is home at night: ${alarmMsg} / Mode: ${modeMsg}"
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
        message = "${location}: Everyone is away at night: ${alarmMsg} / Mode: ${modeMsg}"
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