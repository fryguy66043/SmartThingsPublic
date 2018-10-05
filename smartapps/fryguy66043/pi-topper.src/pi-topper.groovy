/**
 *  EatMyPi Device Assistant.
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
    name: "Pi Topper",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "EatMyPi Device Assistant.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Pi Server") {
    	input "piServer", "device.eatmypi", title: "Select your Pi Handler."
    }
    section("Pi Power") {
    	input "piPower", "capability.switch", title: "Select your Pi Power Switch."
        input "powerSchedule", "bool", title: "Do you want to schedule power cycles?"
        input "powerOffTime", "time", required: false, title: "What time do you want to power off?"
        input "powerOnTime", "time", required: false, title: "What time do you want to power on?"
    }
    section("Image Capture Scheduling") {
        input "imageLoopSunriseAutoStart", "bool", title: "Auto-Start at sunrise?"
        input "imageLoopSunriseMinutesBefore", "number", required: false, title: "Number of minutes before sunrise?"
        input "imageLoopSunsetAutoStop", "bool", title: "Auto-Stop at sunset?"
        input "imageLoopSunsetMinutesAfter", "number", required: false, title: "Number of minutes after sunset?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", title: "Send Push Notification when command executed?"
    }
    section("Send a Text Message?") {
        input "phone", "phone", required: false, title: "Send a Text Message when command executed?"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(piServer, "isServerRunning", piServerHandler)
    subscribe(piServer, "isImageServiceRunning", piImageServiceHandler)
    subscribe(piPower, "switch", piPowerHandler)
    if (imageLoopSunriseAutoStart) {
    	subscribe(location, "sunriseTime", sunriseTimeHandler)
        scheduleTurnOn()
    }
    if (imageLoopSunsetAutoStop) {
    	subscribe(location, "sunsetTime", sunsetTimeHandler)
        scheduleTurnOff()
    }
    if (powerSchedule) {
    	schedule(powerOffTime, powerOffHandler)
        schedule(powerOnTime, powerOnHandler)
    }
}

def appHandler(evt) {
	log.debug "appHandler"
}

def powerOffHandler(evt) {
	log.debug "powerOffHandler"
    piPower.on()
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "$date: Turning Off piPower."
    if (phone) {
        sendSms(phone, msg)
    }
    if (sendPush) {
        sendPush(msg)
    }
}

def powerOnHandler(evt) {
	log.debug "powerOnHandler"
    piPower.off()
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "$date: Turning On piPower."
    if (phone) {
        sendSms(phone, msg)
    }
    if (sendPush) {
        sendPush(msg)
    }
}

def piPowerHandler(evt) {
	log.debug "piPowerHandler(${evt.value})"
    if (evt.value == "on") {
    	piServer.setPowerStatus("true")
    }
    else {
    	piServer.setPowerStatus("false")
    }
}

def piServerHandler(evt) {
	log.debug "piServerHandler(${evt.value})"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    if (evt.value == "true") {
    	msg = msg + "piServer is now running."
    	if (phone) {
        	sendSms(phone, msg)
        }
        if (sendPush) {
        	sendPush(msg)
        }
    }
    else {
    	msg = msg + "piServer is not available."
    	if (phone) {
        	sendSms(phone, msg)
        }
        if (sendPush) {
        	sendPush(msg)
        }
    }
}

def piImageServiceHandler(evt) {
	log.debug "piImageServiceHandler(${evt.value})"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    if (evt.value == "true") {
    	msg = msg + "Image Service is now running."
    	if (phone) {
        	sendSms(phone, msg)
        }
        if (sendPush) {
        	sendPush(msg)
        }
    }
    else {
    	msg = msg + "Image Service is not available."
    	if (phone) {
        	sendSms(phone, msg)
        }
        if (sendPush) {
        	sendPush(msg)
        }
    }
}

def sunsetTimeHandler(evt) {
	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
	log.debug "sunsetTimeHandler(${evt.value})"
    if (phone) {
		sendSms(phone, "($date) SunsetTime Handler: evt.value = ${evt.value} / sunsetTime = ${location.currentValue("sunsetTime")}")
    }
//    scheduleTurnOff(location.currentValue("sunsetTime"))
	runIn(60 * 60 * 2, scheduleTurnOff)
}

def sunriseTimeHandler(evt) {
	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
	log.debug "sunriseTimeHandler(${evt.value})"
    if (phone) {
		sendSms(phone, "($date) SunriseTime Handler: evt.value = ${evt.value} / sunriseTime = ${location.currentValue("sunriseTime")}")
    }
//    scheduleTurnOn(location.currentValue("sunriseTime"))
	runIn(60 * 60 * 2, scheduleTurnOn)
}

def scheduleTurnOff() {
	log.debug "scheduleTurnOff"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", location.currentValue("sunsetTime"))

    //calculate the offset
    def timeAfterSunset = new Date(sunsetTime.time + (imageLoopSunsetMinutesAfter * 60 * 1000))

	msg = msg + "Scheduling turn off for: $timeAfterSunset (sunset is $sunsetTime)"
    log.debug msg
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }

    //schedule this to run one time
    runOnce(timeAfterSunset, autoStopHandler)
}

def scheduleTurnOn(sunriseString) {
	log.debug "scheduleTurnOn"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", location.currentValue("sunriseTime"))

    //calculate the offset
    def timeBeforeSunrise = new Date(sunriseTime.time - (imageLoopSunriseMinutesBefore * 60 * 1000))

    msg = msg + "Scheduling turn on for: $timeBeforeSunrise (sunrise is $sunriseTime)"
    log.debug msg
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }

    //schedule this to run one time
    runOnce(timeBeforeSunrise, autoStartHandler)
}

def autoStartHandler(evt) {
	log.debug "autoStartHandler"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Auto-Start Image Capture Service..."
    log.debug msg
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
    piServer.imageServiceOn()
}

def autoStopHandler(evt) {
	log.debug "autoStopHandler"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Auto-Stop Image Capture Service..."
    log.debug msg
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
    piServer.imageServiceOff(true)
}

