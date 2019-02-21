/**
 *  Smart Alarm Clock Smart App.
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
    name: "Smart Alarm Clock App",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Smart Alarm Clock Smart App.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Smart Alarm Clock") {
    	input "alarmClock", "device.smartalarmclock", title: "Select your Smart Alarm Clock."
    }
    section("Presence Settings") {
    	input "alarm1CheckPres", "bool", title: "Only Trigger Alarm 1 if Someone is Home?"
        input "alarm1Pres", "capability.presenceSensor", title: "Who?", multiple: true
    }
    section("Set Alarm Time") {
    	input "alarm1Time", "time", title: "Alarm time."
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
	state.alarm1Time = ""

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	initialize()
    if (state.alarm1Time != alarm1Time) {
    	def aDate = new Date(timeToday(alarm1Time).time)
        def aTime = aDate.format("HH:mm", location.timeZone)
        log.debug "aTime = ${aTime}"
    	alarmClock.setAlarmTime(1, aTime)
        state.alarm1Time = alarm1Time
    }
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(alarmClock, "update", alarmClockUpdateHandler)
    subscribe(alarm1Pres, "presence", alarm1PresenceHandler)
}

def appHandler(evt) {
	log.debug "appHandler"
    alarmClock.getSettings()
}

def alarm1PresenceHandler(evt) {
	log.debug "alarm1PresenceHandler"
    def presenceValue = alarm1Pres.find{it.currentPresence == "present"}
    log.debug "presence = ${presenceValue}"
	if (presenceValue) {
    	alarmClock.setPresence(1, "true")
    }
    else {
    	alarmClock.setPresence(1, "false")
    }
}

def alarmClockUpdateHandler(evt) {
	log.debug "alarmClockUpdateHandler"
}

