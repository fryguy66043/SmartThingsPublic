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
        input "alarmLogSize", "number", title: "Max log file size?"
    }
    section("Alarm 1 Settings") {
    	input "alarm1OnOff", "bool", title: "Turn Alarm 1 on?"
        input "alarm1CheckPres", "bool", title: "Only Trigger Alarm 1 if Someone is Home?"
        input "alarm1Pres", "capability.presenceSensor", title: "Who?", multiple: true
    	input "alarm1Time", "time", title: "Set Alarm 1 time."
        input "alarm1Mon", "bool", title: "Monday"
        input "alarm1Tue", "bool", title: "Tuesday"
        input "alarm1Wed", "bool", title: "Wednesday"
        input "alarm1Thu", "bool", title: "Thursday"
        input "alarm1Fri", "bool", title: "Friday"
        input "alarm1Sat", "bool", title: "Saturday"
        input "alarm1Sun", "bool", title: "Sunday"
        
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", title: "Send Push Notification when alarm is activated?"
    }
    section("Send a Text Message?") {
        input "phone", "phone", required: false, title: "Send a Text Message when alarm is activated?"
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
    syncSettings()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(alarmClock, "startupDateTime", alarmClockStartupHandler)
    subscribe(alarmClock, "update", alarmClockUpdateHandler)
    subscribe(alarm1Pres, "presence", alarm1PresenceHandler)
    subscribe(alarmClock, "alarm1Alarm", alarm1AlarmHandler)
}

def syncSettings() {
	log.debug "syncSettings"

    if (alarmLogSize as String != alarmClock.currentValue("logSize")) {
    	log.debug "Update Log Size"
        alarmClock.setLogSize(alarmLogSize as String)
    }
    if (Boolean.toString(alarm1OnOff).toUpperCase() != alarmClock.currentValue("alarm1On").toUpperCase()) {
    	log.debug "Updating Alarm 1 on/off"
	    alarmClock.alarmOnOff(1, Boolean.toString(alarm1OnOff))
    }
    log.debug "Alarm Min: ${alarmMin} / ${alarmClock.currentValue("alarmMin")}"
    if (alarmMin as String != alarmClock.currentValue("alarmMin")) {
    	log.debug "Updating Alarm Minutes"
	    alarmClock.setAlarmMin(alarmMin)
    }
    log.debug "Check Pres: ${Boolean.toString(alarm1CheckPres).toUpperCase()} / ${alarmClock.currentValue("alarm1CheckPres").toUpperCase()}"
    if (Boolean.toString(alarm1CheckPres).toUpperCase() != alarmClock.currentValue("alarm1CheckPres").toUpperCase()) {
    	log.debug "Updating Check Presence"
    	alarmClock.alarmCheckPresence(1, alarm1CheckPres as String)
    }
    alarm1PresenceHandler()
    alarm1SetTime()
    alarm1SetDays()
    alarmClock.refresh()
}

def alarm1SetTime() {
	log.debug "alarm1SetTime"
    def aDate = new Date(timeToday(alarm1Time).time)
    def aTime = aDate.format("HH:mm", location.timeZone)
    log.debug "Check Time: ${aTime} / ${alarmClock.currentValue("alarm1Time")}"
    if (aTime != alarmClock.currentValue("alarm1Time")) {
        alarmClock.setAlarmTime(1, aTime)
    }
    state.alarm1Time = alarm1Time
//    schedule(alarm1Time, alarm1AlarmCheck)
}

def alarm1SetDays() {
	log.debug "alarm1SetDays"
    def days = "["
    
    if (alarm1Mon) {
    	days += "1"
    }
    else {
    	days += "0"
    }
    if (alarm1Tue) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }
    if (alarm1Wed) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }
    if (alarm1Thu) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }
    if (alarm1Fri) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }
    if (alarm1Sat) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }
    if (alarm1Sun) {
    	days += ", 1"
    }
    else {
    	days += ", 0"
    }

	days += "]"
    log.debug "Days = ${days}"
    alarmClock.setAlarmDays(1, days)
}

def appHandler(evt) {
	log.debug "appHandler"
    alarmClock.refresh()
}

def alarmClockStartupHandler(evt) {
	log.debug "alarmClockStartupHandler"
	syncSettings()
}

def alarm1AlarmCheck(evt) {
	log.debug "alarm1AlarmCheck"
    alarmClock.refresh()
//    runIn(60 * alarmMin, alarm1AlarmCheck2)
}

def alarm1AlarmCheck2() {
	log.debug "alarm1AlarmCheck2"
//    if (alarmClock.currentValue("alarm1Alarm") == "false") {
    	alarmClock.refresh()
//    }
}

def alarm1AlarmHandler(evt) {
	log.debug "alarm1AlarmHandler: alarmClock.alarm1Alarm = ${alarmClock.currentValue("alarm1Alarm")}"
    if (alarmClock.currentValue("alarm1Alarm") == "true") {
    	log.debug "Alarm 1 is Alarming..."
        def date = new Date().format("MM/dd/yy hh:mm a", location.timeZone)
        def msg = "${location} ${date}: Smart Alarm Clock: Alarm Activated"
        if (sendPush) {
        	sendPush(msg)
        }
        if (phone) {
        	sendSms(phone, msg)
        }
    }
    else {
    	log.debug "Alarm 1 has stopped Alarming..."
    }
}

def alarm1PresenceHandler(evt) {
	log.debug "alarm1PresenceHandler"
    def presenceValue = alarm1Pres.find{it.currentPresence == "present"}
    log.debug "presence = ${presenceValue} / ${alarmClock.currentValue("alarm1CurrPres")}"
	if (presenceValue) {
    	if (alarmClock.currentValue("alarm1CurrPres") != "true") {
	    	alarmClock.setPresence(1, "true")
        }
    }
    else {
    	if (alarmClock.currentValue("alarm1CurrPres") != "false") {
    		alarmClock.setPresence(1, "false")
        }
    }
}

def alarmClockUpdateHandler(evt) {
	log.debug "alarmClockUpdateHandler"
}

