/**
 *  When Appliance Use Is Detected, Turn Another Device Off
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
    name: "When One Thing Comes On, Turn Something Else Off",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "When an appliance is turned on, turn something else off.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
)

preferences {
	section("When Something Is Turned On...") {
    	input "appliance", "capability.powerMeter", required: true
    }
	section("How Many Watts Does It Draw When Off? (Default = 10)") {
    	input "offWatts", "number", required: true, title: "Off Watts", defaultValue: 10
        input "powerLossNotify", "bool", title: "Do you want to be notified if this device stops reporting? (i.e. power loss)"
    }
	section("How Many Watts Does It Draw When On? (Default =30)") {
    	input "watts", "number", required: true, title: "On Watts", defaultValue: 30
    }
	section("Turn Off What?"){
    	input "switch1", "capability.switch", required: true, multiple: true
    }
	section("Wait How Long To Turn Off?") {
    	input "minutes", "number", required: false, title: "Minutes?", defaultValue: 0
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
	state.running = false
    state.powerCheckTime = now()
    state.powerCheckAlert = false
	subscribe(appliance, "power", powerHandler)
    subscribe(app, appHandler)
    if (powerLossNotify) {
    	runEvery10Minutes(powerCheckHandler)
    }
}

def updated()
{
	unschedule()
	unsubscribe()
    installed()
}

private getAppName() { return "When One Thing Comes On..." }

def appHandler(evt) {
	def msg = "Running = ${state.running}  Watts = ${appliance.currentPower}"
    if (phone) {
        sendSms(phone, msg)
    }
    state.running=false
}

def powerCheckHandler(evt) {
	log.debug "powerCheckHandler: appliance.currentPower = ${appliance.currentPower} / appliance.currentSwitch = ${appliance.currentSwitch}"
    def date = new Date().format("MM/yy/dd h:mm:ss a", location.timeZone)
    def msg = ""
    
    if (appliance.currentSwitch == "off") {
    	appliance.on()
    }
    if (appliance.currentPower > 0 || appliance.currentSwitch == "on") {
        if (state.powerCheckAlert) {
        	log.debug "Power has been restored!"
            msg = "${location} ${date}: ${getAppName()}\n${appliance} has started reporting again.  Power has been restored."
            if (phone) {
                sendSms(phone, msg)
            }
            if (sendPush) {
            	sendPush(msg)
            }
        }
    	log.debug "We have power.  Everything's good!"
        state.powerCheckTime = now()
        state.powerCheckAlert = false    	
    }
    else {
    	log.debug "Looks like power might be out..."
        state.powerCheckTime = state.powerCheckTime ?: now()
        if (!state.powerCheckAlert && state.powerCheckTime < now() + (60 * 5)) {
            if (phone) {
            	log.debug "Sending possible loss of power message"
                state.powerCheckAlert = true
                msg = "${location} ${date}: ${getAppName()}\n${appliance} has stopped reporting.  Possible power outage."
                if (phone) {
	                sendSms(phone, msg)
                }
                if (sendPush) {
                	sendPush(msg)
                }
            }
        }
    }
}

def powerHandler(evt) {
	log.debug "powerHandler (${evt.value})"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def msg = ""
    
	if (state.powerCheckAlert) {
        log.debug "Power has been restored!"
        msg = "${location} ${date}: ${getAppName()}\n${appliance} has started reporting again.  Power has been restored."
    	if (phone) {
	    	sendSms(phone, msg)
        }
        if (sendPush) {
        	sendPush(msg)
        }
    }
	state.powerCheckTime = now()
    state.powerCheckAlert = false
	if (!state.running && appliance.currentPower >= watts) {
    	state.running = true
    	runIn(60 * minutes, delayHandler)
    }
    else if (state.running && appliance.currentPower <= offWatts) {
    	state.running = false
    }
}

def delayHandler(evt)
{
	def msg = "delayHandler: Watts = ${appliance.currentPower} / ${watts}"
    
	if (appliance.currentPower >= watts) {
//    	sendSms(phone, "Checking if anything is on")
		if (anythingOn()) {
			msg = "As Requested, Turning Off ${switch1} Due to Power Usage of ${appliance}"
            switch1.off()
            state.running = false
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
/*
		else {
        	msg = "Confirmed ${switch1} Already Turned Off"
            if (sendPush) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
*/
	}
	else { 
	    state.running = false
    }
}

private anythingOn() {
	def result = false
    def msg = "Running anythingOn()"
    for (eSwitch in switch1) {
		if (eSwitch.currentSwitch == "on") {
        	result = true
            break
        }
    }
	return result
}
