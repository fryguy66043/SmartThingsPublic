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
        input "alarmMin", "number", title: "Max alarm time before turning off?"
    }
    section("Alarm 1 Presence Settings") {
    	input "alarm1CheckPres", "bool", title: "Only Trigger Alarm 1 if Someone is Home?"
        input "alarm1Pres", "capability.presenceSensor", title: "Who?", multiple: true
    }
    section("Alarm 1 Settings") {
    	input "alarm1OnOff", "bool", title: "Turn Alarm 1 on?"
    	input "alarm1Time", "time", title: "Set Alarm 1 time."
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
    if (Boolean.toString(alarm1OnOff).toUpperCase() != alarmClock.currentValue("alarm1On").toUpperCase()) {
    	log.debug "Updating Alarm 1 on/off"
	    alarmClock.alarmOnOff(1, Boolean.toString(alarm1OnOff))
    }
    if (alarmMin != alarmClock.currentValue("alarmMin")) {
	    alarmClock.setAlarmMin(alarmMin)
    }
    alarm1PresenceHandler()
    alarm1SetTime()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(alarmClock, "startupDateTime", alarmClockStartupHandler)
    subscribe(alarmClock, "update", alarmClockUpdateHandler)
    subscribe(alarm1Pres, "presence", alarm1PresenceHandler)
    subscribe(alarmClock, "alarm1Alarm", alarm1AlarmHandler)
}

def alarm1SetTime() {
	log.debug "alarm1SetTime"
    def aDate = new Date(timeToday(alarm1Time).time)
    def aTime = aDate.format("HH:mm", location.timeZone)
    log.debug "aTime = ${aTime} / aDate = ${aDate}"
    alarmClock.setAlarmTime(1, aTime)
    state.alarm1Time = alarm1Time
    schedule(alarm1Time, alarm1AlarmCheck)
}

def appHandler(evt) {
	log.debug "appHandler"
    alarmClock.getSettings()
}

def alarmClockStartupHandler(evt) {
	log.debug "alarmClockStartupHandler"
    alarm1SetTime()
    alarm1PresenceHandler()
}

def alarm1AlarmCheck(evt) {
	log.debug "alarm1AlarmCheck"
    alarmClock.refresh()
    runIn(60, alarm1AlarmCheck2)
}

def alarm1AlarmCheck2() {
	log.debug "alarm1AlarmCheck2"
    if (alarmClock.currentValue("alarm1Alarm") == "false") {
    	alarmClock.refresh()
    }
}

def alarm1AlarmHandler(evt) {
	log.debug "alarm1AlarmHandler: alarmClock.alarm1Alarm = ${alarmClock.currentValue("alarm1Alarm")}"
    if (alarmClock.currentValue("alarm1Alarm") == "true") {
    	log.debug "Alarm 1 is Alarming..."
    }
    else {
    	log.debug "Alarm 1 has stopped Alarming..."
    }
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

