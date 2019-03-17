/**
 *  Call Web Server When...
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
    name: "Call Web Server When...",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Execute a web server call at a certain time or on presence change.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
    section("Remind Me When I Get Home?") {
    	input "arrivalReminder", "bool", title: "Yes or No?", defaultValue: false
        input "arrivalMessage", "text", title: "Arrival Message:", required: false
    	input "arrivalPeople", "capability.presenceSensor", title: "When Who Arrives?", required: false, multiple: false
        input "arrivalMinutes", "number", title: "How Many Minutes After I'm Home?", required: false, defaultValue: 0
        input "arrivalEveryTime", "bool", title: "Remind Me Every Arrival?", required: false, defaultValue: false
    }
    section("Remind Me When I Leave?") {
    	input "departureReminder", "bool", title: "Yes or No?", defaultValue: false
        input "departureMessage", "text", title: "Departure Message:", required: false
        input "departurePeople", "capability.presenceSensor", title: "When Who Departs?", required: false, multiple: false
        input "departureMinutes", "number", title: "How Many Minutes After I'm Gone?", required: false, defaultValue: 0
        input "departureEveryTime", "bool", title: "Remind Me Every Departure?", required: false, defaultValue: false
    }
    section("Remind Me At A Certain Time?") {
    	input "timeReminder", "bool", title: "Yes or No?", defaultValue: false
        input "timeMessage", "text", title: "Time Message:", required: false
        input "timeTime", "time", title: "What Time?", required: false
        input "timeEveryTime", "bool", title: "Remind Me Every Day", defaultValue: false
    }
	section("Send Push Notification Reminder?") {
        input "sendPush", "bool", required: false, title: "Yes or No?"
    }
    section("Send Text Message Reminder?") {
        input "phone", "phone", required: false, title: "Phone Number"
    }
}

def installed()
{
    state.sent = false
    state.arrivalSent = false
    state.departureSent = false
    state.timeSent = false
    state.current = ""
	subscribe(arrivalPeople, "presence.present", arrivalHandler)
    subscribe(departurePeople, "presence.not present", departureHandler)
    subscribe(app, appHandler)
    if (timeReminder && timeTime) {
	    schedule(timeTime, timeHandler)
    }
}

def updated()
{
	unsubscribe()
    installed()
}

def appHandler(evt) {
	log.debug "appHandler"
    
    callServer("test=testing")
}

def callServer(param) {
	log.debug "callServer()"
    
    state.pollStatus = false
    runIn(10, pollErr)
    try {
        httpGet("http://fryguypi.ddns.net:80/setlocation?${param}") { resp ->
        	log.debug "polling round-trip..."
            
			if (resp.status == 200) {
            	pollHandler("${resp.data}")
            } else {
                log.error "Error polling FryGuyPi service.  Status: ${resp.status}"
                pollErr()
            }
        }
        log.debug "After httpGet..."
    } catch (err) {
        log.debug "Error making getHttp poll request: $err"
    }
}

def pollHandler(sData) {
	log.debug "pollHandler()"
    state.pollStatus = true
    log.debug "sData = ${sData}"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
}

def pollErr() {
	log.debug "getStatusErr (checking for errors)"
    if (!state.pollStatus) {
    	log.debug "Polling timed out..."
    }
    else {
    	log.debug "Polling success!"
    }
}



/*
def appHandler(evt) {
	def msg = "Message Delivered: ${state.sent} / Arrival: ${state.arrivalSent} / Departure: ${state.departureSent} / Time: ${state.timeSent}"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
    if (!sendPush && !phone) {
    	sendPush("Reminder Not Currently Enabled.")
    }
}
*/

def arrivalHandler(evt) {
	log.debug "arrivalHandler"

	def name = arrivalPeople[0].label as String
    name = URLEncoder.encode(name, "UTF-8")
	log.debug "arrivalPeople.label = ${name}"

	def loc = location as String
    loc = URLEncoder.encode(loc, "UTF-8")
    log.debug "loc = ${loc}"
    callServer("name=${name}&arrive=${loc}")
	if (arrivalReminder) {
    	if (arrivalEveryTime || !state.arrivalSent) {
        	state.current = "arrival"
        	runIn(60 * arrivalMinutes, msgHandler)
        }
    }
}

def departureHandler(evt) {
	log.debug "departureHandler"

	def name = departurePeople[0].label as String
    name = URLEncoder.encode(name, "UTF-8")
	log.debug "departurePeople.label = ${name}"
    
	def loc = location as String
    loc = URLEncoder.encode(loc, "UTF-8")
    log.debug "loc = ${loc}"
    callServer("name=${name}&depart=${loc}")
	if (departureReminder) {
    	if (departureEveryTime || !state.departureSent) {
        	state.current = "departure"
        	runIn(60 * departureMinutes, msgHandler)
        }
    }
}

def timeHandler(evt)
{
	if (timeReminder && (timeEveryTime || !state.timeSent)) {
    	state.current = "time"
        msgHandler(evt)
    }
}

def msgHandler(evt)
{
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def message = "${location} ${date}: "
    
	state.sent = true
    switch (state.current) {
    	case "arrival":
        	state.arrivalSent = true
            message = message + arrivalMessage
            break
        case "departure":
        	state.departureSent = true
            message = message + departureMessage
            break
        case "time":
        	state.timeSent = true
            message = message + timeMessage
            break
    }
    if (sendPush) {
        sendPush(message)
    }
    if (phone) {
        sendSms(phone, message)
    }
}