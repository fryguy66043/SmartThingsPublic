/**
 *  Turn On Only If I Am Home At Sunset
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
    name: "Turn On Only If I Am Home At Sunset",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Turn something on only if I am home at sunset.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	section("When I am home..."){
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
	}
	section("Turn on a light..."){
		input "switch1", "capability.switch", multiple: true
	}
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when executed?"
    }
}

def installed()
{
    subscribe(presence1, "presence.present", arrivalHandler)
    subscribe(presence1, "presence.not present", departureHandler)
    subscribe(location, "sunset", sunsetHandler)
    subscribe(app, appHandler)
}

def updated()
{
	unsubscribe()
    installed()
    state.hue = 0
    state.saturation = 0
}

def appHandler(evt) {
	def now = new Date()
	def sunTime = getSunriseAndSunset();
    def dark = (now >= sunTime.sunset)

	sunsetHandler()

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
	def now = new Date()
	def sunTime = getSunriseAndSunset()
    def dark = (now >= sunTime.sunset)
    def message = "Welcome home at night! Turning light(s) on."
    
	if (firstOneHome() && (now >= sunTime.sunset)) {
    	if (state.hue || state.saturation) {
        	switch1?.setColor([hue: state.hue, saturation: state.saturation])
        }
		switch1.on()
		log.debug "Welcome home at night! Turning light(s) on."
        if (sendPush) {
            sendPush(message)
        }
	}
}

def departureHandler(evt)
{
    def message = "Everyone has left! Turning light(s) off."

	if (everyoneIsAway() && somethingOn()) {
    	switch1.off()
        if (sendPush) {
            sendPush(message)
        }
    }
}

private somethingOn() {
	def result = false
    for (aSwitch in switch1) {
    	if (aSwitch.currentSwitch == "on") {
        	result = true
            break
        }
    }
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

def sunsetHandler(evt) {
    log.debug "Sun has set!"
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue

	if (presenceValue) {
    	if (state.hue || state.saturation) {
        	switch1?.setColor([hue: state.hue, saturation: state.saturation])
        }
		switch1.on()
		log.debug "Home at night!"
        def message = "${location}: Home at sunset - Turning light(s) on."
        if (sendPush) {
            sendPush(message)
        }
	}
    else {
    	switch1.off()
        def message = "${location}: Not home at sunset- Not turning light(s) on."
        if (sendPush) {
            sendPush(message)
        }
    }
}
