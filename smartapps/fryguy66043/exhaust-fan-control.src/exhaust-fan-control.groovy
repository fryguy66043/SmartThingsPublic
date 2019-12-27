/**
 *  When Exhaust Fan is turned on, monitor Relative Humididty to determine when to turn back off.
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
    name: "Exhaust Fan Control",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "When exhaust fan is turned on, measure RH to determine when to turn back off.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
)

preferences {
	section("Exhaust Fan Control") {
    	input "fan", "capability.switch", required: true, multiple: false, title: "Select switch for Exhaust Fan."
        input "rh", "capability.relativeHumidityMeasurement", required: true, multiple: false, title: "Select RH Sensor."
        input "rhMax", "number", required: true, multiple: false, title: "Select RH% to automatically turn on Exhaust Fan."
        input "rhMin", "number", required: true, multiple: false, title: "Select RH% to automatically turn off Exhaust Fan."
        input "rhMinTime", "number", required: true, multiple: false, title: "Select Minimum Runtime (minutes) for Exhaust Fan to run."
        input "rhMaxTime", "number", required: true, multiple: false, title: "Select Maximum Runtime (minutes) for Exhause Fan to run.  (0 = forever)"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification When Executed?"
    }
    section("Send a text message to this number (optional)") {
        input "phone", "phone", required: false
    }
}

def installed()
{
	state.minTime = false
    state.rh = 0
    state.rhTime = now()
	subscribe(fan, "switch", fanHandler)
    subscribe(rh, "humidity", rhHandler)
}

def updated()
{
	unschedule()
	unsubscribe()
    installed()
}

private getAppName() { return "Exhaust Fan Control" }

def fanHandler(evt) {
    log.debug "fanHandler (${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = "${getAppName()}: ${date}\n"

    if (evt?.value == "on") {
        state.rh = rh.currentValue("humidity")
        msg += "Turning fan on.  Current RH: ${state.rh}%  Target RH: ${rhMin}%"
        state.rhTime = now()
        runIn(rhMinTime * 60, delayHandler)
        if (rhMaxTime > rhMinTime) {
        	runIn (rhMaxTime * 60, maxDelayHandler)
        }
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else if (evt?.value == "off") {
        state.minTime = false
        unschedule()
    }
}

def maxDelayHandler(evt) {
	log.debug "maxDelayHandler"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = "${getAppName()}: ${date}\n"
    
    if (state.minTime && fan.currentValue("switch") == "on") {
        fan.off()
        log.debug "Max Time of ${rhMaxTime} minutes reached.  Turning fan off..."
        msg += "Max Time of ${rhMaxTime} minutes reached.  Turning fan off..."
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
}

def delayHandler(evt)
{
    log.debug "delayHandler"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = "${getAppName()}: ${date}\n"

    if (fan.currentValue("switch") == "on") {
        if (rh.currentValue("humidity") > rhMin) {
            state.minTime = true
            log.debug "Minimum Time Reached.  Beginning RH checks..."
            msg += "Minimum Time of ${rhMinTime} minutes Reached.  Beginning RH checks..."
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
        else {
            state.minTime = false
            fan.off()
            log.debug "Minimum Time Reached.  RH value at/below minumum.  Turning fan off..."
            msg += "Minimum Time of ${rhMinTime} minutes Reached.  RH value at/below minumum of ${rhMin}%.  Turning fan off..."
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }        
    }
}

def rhHandler(evt)
{
    log.debug "rhHandler(${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = "${getAppName()}: ${date}\n"
	def currVal = evt?.value.toInteger()
    
    if (state.minTime) {
        log.debug "state.minTime"
        if (currVal <= rhMin) {
            log.debug "rhMin value reached.  Turning off fan..."
            msg += "rhMin value ${rhMin}% reached.  Turning off fan..."
            fan.off()
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
/*
		else if (currVal <= state.rh && currVal < rhMax) {
            log.debug "rh has reached it's starting value.  Turning off fan..."
            msg += "rh has reached it's starting value of ${state.rh}%.  Turning off fan..."
            fan.off()
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
*/
    }
    else if (currVal >= rhMax && fan.currentValue("switch") == "off") {
        fan.on()
        log.debug "rhMax value reached.  Turning on fan..."
        msg += "rhMax value of ${rhMax}% reached.  Turning on fan..."
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
}
