/**
 *  Turn On Only If I Am Away At Sunset
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
    name: "Turn On Only If I Am Away At Sunset",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Turn something on only if I am away at sunset.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	section("When I am away..."){
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
	subscribe(presence1, "presence", presenceHandler)
    subscribe(location, "sunset", sunsetHandler)
    subscribe(app, appHandler)
}

def updated()
{
	unsubscribe()
    installed()
}

def appHandler(evt) {
	def now = new Date()
	def sunTime = getSunriseAndSunset();
    
	log.debug "nowTime: $now"
	log.debug "riseTime: $sunTime.sunrise"
	log.debug "setTime: $sunTime.sunset"
	log.debug "presenceHandler $evt.name: $evt.value"

    def dark = (now >= sunTime.sunset)
    log.debug dark

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
    if (sendPush) {
        sendPush(message)
    }
}

def presenceHandler(evt)
{
	def now = new Date()
	def sunTime = getSunriseAndSunset();
    
	log.debug "nowTime: $now"
	log.debug "riseTime: $sunTime.sunrise"
	log.debug "setTime: $sunTime.sunset"
	log.debug "presenceHandler $evt.name: $evt.value"

	def current = presence1.currentValue("presence")
	log.debug current
    
    def dark = (now >= sunTime.sunset)
    log.debug dark

	def message = "${location} Presence Change:  ${current}   Sunset = ${dark}"

	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue
	if (!presenceValue && (now >= sunTime.sunset)) {
		switch1.on()
		log.debug "Not home at night!"
        if (sendPush) {
            sendPush(message)
        }
	}
//    else {
//    	switch1.off()
//    }
}

def sunsetHandler(evt) {
    log.debug "Sun has set!"
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue

	if (!presenceValue) {
		switch1.on()
		log.debug "Not home at sunset!"
        def message = "${location}: Not home at sunset - Turning light(s) on."
        if (sendPush) {
            sendPush(message)
        }
	}
    else {
    	switch1.off()
        def message = "${location}: Home at sunset - Not turning light(s) on."
        if (sendPush) {
            sendPush(message)
        }
    }
}
