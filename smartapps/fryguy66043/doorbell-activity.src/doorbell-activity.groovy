/**
 *  Define Actions To Take When Doorbell Activity Is Detected.
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
    name: "Doorbell Activity",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Define what to do when Doorbell detects activity.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	section("Select Doorbell(s)..."){
    	input "dbMotion", "capability.motionSensor", title: "Doorbell Motion Sensor(s)?", multiple: true, required: false
        input "dbButton", "capability.button", title: "Doorbell Button(s)?", multiple: true, required: false
    }
	section("Turn on a light..."){
		input "switch1", "capability.switch", multiple: true, title: "Turn on which light(s) when activity is detected?"
        input "minutes", "number", title: "Turn off light(s) after how many minutes?"
        input "darkOnly", "bool", title: "Turn on light(s) only after dark?"
	}
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false, title: "Send Push Notification when executed?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false, title: "Send a text when executed?"
    }
}

def installed()
{
    subscribe(app, appHandler)
    subscribe(dbMotion, "motion", motionHandler)
    subscribe(dbButton, "button", buttonHandler)
}

def updated()
{
	unsubscribe()
    unschedule()
    installed()
}

private getAppName() { return "Doorbell Activity" }

def appHandler(evt) {
	log.debug "appHandler"
//	def now = new Date()
//	def sunTime = getSunriseAndSunset();
//    def dark = (now >= sunTime.sunset)
	def dark = darkNow()
	def date = new Date().format("MM/dd/yy hh:mm a", location.timeZone)
    def msg = "${location} ${date} <${getAppName()}>: "
    
	if (dark) {
    	msg = msg + "Currently Dark. "
    }
    else {
    	msg = msg + "Currently Before Dark. "
    }
    msg = somethingOn() ? msg + "${switch1} = ON" : msg + "${switch1} = OFF"
    log.debug msg
    if (phone) {
    	sendSms(phone, msg)
    }
}

def motionHandler(evt) {
	log.debug "motionHandler"
    if (!somethingOn()) {
    	turnOnLights()
    }
}

def buttonHandler(evt) {
	log.debug "buttonHandler"
    if (!somethingOn()) {
    	turnOnLights()
    }
}

private turnOnLights() {
	log.debug "turnOnLights: darkOnly = ${darkOnly} / darkNow = ${darkNow()}"
	def date = new Date().format("MM/dd/yy hh:mm a", location.timeZone)
    def msg = "${location} ${date} <${getAppName()}>: Motion After Dark!\n"
    msg = msg + "Turning on ${switch1} for ${minutes} minutes."
    
	if ((darkOnly && darkNow()) || !darkOnly) {
        switch1?.on()
        runIn(minutes * 60, turnOffLights)
        log.debug msg
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
}

def turnOffLights() {
	log.debug "turnOffLights"
	switch1?.off()
}

private somethingOn() {
	log.debug "somethingOn"
	def result = false
    
    for (aSwitch in switch1) {
        if (aSwitch.currentSwitch == "on") {
            result = true
            break
        }
    }
    return result
}

private darkNow() {
	log.debug "darkNow"
	def now = new Date()
	def sunTime = getSunriseAndSunset();
    def dark = false
    if ((now >= sunTime.sunset) || (now <= sunTime.sunrise)) {
    	dark = true
    }    
    return dark
}
