/**
 *  Turn On Only If I Am Home In The Morning Before Sunrise.
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
    name: "Turn On Only If I Am Home In The Morning",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Turn something on only if I am home in the morning before sunrise.",
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
        input "turnOnTime", "time", title: "Turn on at what time?"
        input "turnOnDarkOnly", "bool", title: "Turn on only if it's dark?"
        input "colorHue", "number", required: false, title: "(Optional) Hue setting for color bulbs. (0 - 360)"
        input "colorSat", "number", required: false, title: "(Optional) Saturation setting for color bulbs. (0 - 100)"
        input "dimLevel", "number", required: false, title: "(Optional) Dim Level for dimmable bulbs/switches. (0 - 100)"
	}
    section("Turn off at sunrise?") {
    	input "offAtSunrise", "bool", title: "Turn off at sunrise?"
        input "minutesAfterSunrise", "number", title: "How many minutes after sunrise?"
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
    subscribe(location, "sunrise", sunriseHandler)
    subscribe(app, appHandler)
    schedule(turnOnTime, onHandler)
    state.hue = 0
    state.saturation = 0
    state.switchLevel = 100
    if (colorHue>= 0) {
    	state.hue = colorHue
    }
    if (colorSat >= 0) {
    	state.saturation = colorSat
    }
    if (dimLevel >= 0) {
    	state.switchLevel = dimLevel
    }
}

def updated()
{
	unsubscribe()
    unschedule()
    installed()
}

def appHandler(evt) {
	def now = new Date()
	def sunTime = getSunriseAndSunset();
    def dark = (now <= sunTime.sunrise || now >= sunTime.sunset)
	
    turnOn()

	def curState = switch1.currentState("switch")
    def curPresence = presence1.currentValue("presence")
	def presenceValue = presence1.find{it.currentPresence == "present"}
    def curLocation = location
    def message = "Something went wrong!"
    
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
}

def onHandler(evt) {
	log.debug "onHandler"
	def sunTime = getSunriseAndSunset()
    def dark = (now < sunTime.sunrise)
    def message = "${location}: Someone is home in the morning! Turning on ${switch1}."
    
	if (someoneHome() && dark) {
    	turnOn()
        if (sendPush) {
            sendPush(message)
        }
	}
}

def arrivalHandler(evt)
{
	def now = new Date()
	def sunTime = getSunriseAndSunset()
    def dark = (now < sunTime.sunrise)
    def message = "${location}: Welcome home in the morning! Turning on ${switch1}."
    
	if (firstOneHome() && dark) {
    	turnOn()
        if (sendPush) {
            sendPush(message)
        }
	}
}

def departureHandler(evt)
{
    def message = "${location}: Everyone has left! Turning off ${switch1}."

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

private someoneHome() {
	def result = false
    for (person in presence1) {
    	if (person.currentPresence == "present") {
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

def turnOn() {
	def capabilities = ""

	log.debug "colorHue: ${colorHue} / colorSat: ${colorSat} / dimLevel: ${dimLevel}"
	if (colorHue >= 0 && colorSat >= 0) {
    	log.debug "looking for color bulbs..."
    	state.hue = colorHue
        state.saturation = colorSat
        for (aSwitch in switch1) {
        	capabilities = aSwitch.capabilities
            log.debug "Switch = ${aSwitch}: ${capabilities}"
            for (cap in capabilities) {
            	log.debug "cap = ${cap.name}"
            	if (cap.name == "Color Control") {
                	log.debug "${aSwitch}: Hue = ${aSwitch.currentValue("hue")} / Sat = ${aSwitch.currentValue("saturation")} / Level = ${aSwitch.currentValue("level")}"
			        aSwitch?.setColor([hue: state.hue, saturation: state.saturation])
                    log.debug "Color Control Found: ${aSwitch}"
                }
            }
        }
    }
    if (dimLevel >= 0) {
    	log.debug "looking for dimmable lights/switches..."
    	state.switchLevel = dimLevel
        for (aSwitch in switch1) {
        	capabilities = aSwitch.capabilities
            log.debug "Switch = ${aSwitch}: ${capabilities}"
            for (cap in capabilities) {
            	log.debug "cap = ${cap.name}"
            	if (cap.name == "Switch Level") {
                    aSwitch?.setLevel(state.switchLevel)
                    log.debug "Switch Level Found: ${aSwitch}"
                }
            }
        }
    }
    switch1?.on()
}

def sunriseHandler(evt) {
    log.debug "sunriseHandler"
	def current = presence1.currentValue("presence")
	log.debug current
	def presenceValue = presence1.find{it.currentPresence == "present"}
	log.debug presenceValue

	if (offAtSunrise && minutesAfterSunrise == 0 && somethingOn()) {
    	def message = "${location}: Home at sunrise - Turning off ${switch1}."
    	switch1.off()
        if (sendPush) {
            sendPush(message)
        }
    }
    else if (minutesAfterSunrise > 0) {
    	log.debug "Running scheduleHandler in ${minutesAfterSunrise} minutes..."
    	runIn(60 * minutesAfterSunrise, scheduleHandler)
    }
}

def scheduleHandler() {
	log.debug "scheduleHandler"
    if (somethingOn()) {
    	def message = "${location}: Home ${minutesAfterSunrise} after sunrise - Turning off ${switch1}."
    	switch1.off()
        if (sendPush) {
            sendPush(message)
        }
    }
}
