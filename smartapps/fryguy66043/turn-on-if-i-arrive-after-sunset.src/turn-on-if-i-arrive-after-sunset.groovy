/**
 *  Turn On If I Arrive After Sunset
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
    name: "Turn On If I Arrive After Sunset",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Turn something on if I arrive home after sunset.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	section("When I arrive home after sunset..."){
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
	}
	section("Turn on these lights..."){
		input "switch1", "capability.switch", multiple: true
	}
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when executed?"
    }
    section("Optionally, Send a Text Message?") {
    	input "phone", "phone", required: false,
        	title: "Send a Text Message when executed?"
	}
}

def installed()
{
    subscribe(presence1, "presence.present", arrivalHandler)
    subscribe(app, appHandler)
}

def updated()
{
	unsubscribe()
    installed()
    state.hue = 0
    state.saturation = 0
}

private getAppName() { return "Arrive After Sunset" }

def appHandler(evt) {
	def now = new Date()
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
	def sunTime = getSunriseAndSunset();
    def dark = (now >= sunTime.sunset)
    def msg = "${location} ${date} <${getAppName()}>: "
	def presenceValue = presence1.find{it.currentPresence == "present"}

	log.debug "${app.name}"

	if (presenceValue) {
    	if (dark) {
        	msg = msg + "Home after dark."
        }
        else {
        	msg = msg + "Home before dark."
        }
    }
    else {
    	if (dark) {
        	msg = msg + "Away after dark."
        }
        else {
        	msg = msg + "Away before dark."
        }
    }

	log.debug msg
    
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }

// Save hue and saturation if turing on a color bulb
//	state.hue = (switch1.currentValue("hue")) ? switch1.currentValue("hue") : 0
//    state.saturation = (switch1.currentValue("saturation")) ? switch1.currentValue("saturation") : 0

/*
    log.debug "state.hue = ${state.hue} / state.saturation = ${state.saturation}"
    
	log.debug "nowTime: $now"
	log.debug "riseTime: $sunTime.sunrise"
	log.debug "setTime: $sunTime.sunset"
	log.debug "presenceHandler $evt.name: $evt.value"

	def curState = switch1.currentState("switch")
    def curPresence = presence1.currentValue("presence")
	def presenceValue = presence1.find{it.currentPresence == "present"}
    def curLocation = location
    def message = "Something went wrong!"
    log.debug "appHandler called: $evt"
    log.debug "curLocation == $curLocation"
    log.debug "curState == $curState.value"
    log.debug "curPresence == $curPresence"
    log.debug "presenceValue == $presenceValue"
    
  	if (presenceValue) {
	  	message = "${curLocation}:  True Presence: ${curPresence}  Sunset = ${dark}"
        log.debug "True Presence"
    }
    else if (!presenceValue) {
    	message = "${curLocation}:  False Presence: ${curPresence}  Sunset = ${dark}"
        log.debug "False Presence"
    }
    else {
    	message = "${curLocation}:  Unknown Presence: ${curPresence}  Sunset = ${dark}"
        log.debug "Unknown Presence"
    }

	log.debug "message == $message"
//    if (sendPush) {
//        sendPush(message)
//    }
*/
}

def arrivalHandler(evt)
{
	log.debug "arrivalHandler(${evt.value})"
	def now = new Date()
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
	def sunTime = getSunriseAndSunset()
    def dark = (now >= sunTime.sunset)
    def message = "${location} ${date} <${getAppName()}>: "
    
	if (firstOneHome() && dark) {
    	if (state.hue || state.saturation) {
        	switch1?.setColor([hue: state.hue, saturation: state.saturation])
        }
        if (alreadyOnCheck()) {
        	message = message + "Welcome home at night!  Lights are already on: ${switch1}"
        }
        else {
        	message = message + "Welcome home at night! Turning on light(s): ${switch1}"
			switch1.on()
        }
		log.debug message
        if (sendPush) {
            sendPush(message)
        }
        if (phone) {
        	sendSms(phone, message)
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

private alreadyOnCheck() {
	log.debug "alreadyOnCheck"
    def result = false
    def cnt = 0
	for (light in switch1) {
    	if (light.currentSwitch == "on") {
            cnt = cnt + 1
        }
    }
    if (cnt == switch1.size()) {
    	result = true
    }
    return result
}
