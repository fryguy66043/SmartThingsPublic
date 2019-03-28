/**
 *  Monitor the temperature of my devices
 *
 *  This SmartApp allows you to monitor the temperature reported by all supported devices.  You define an alert threshold and it will alert you via push or text
 *  if the threshold is reached.  You can also optionally include the "My Battery Status Tile" to provide a visual indicator of battery levels and easily adjust
 *  alert-level setting.
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
    name: "Monitor the Temperature of My Devices",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Monitor the Temperature Reported By My Supported Devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
//	section("Battery Status Tile?") {
//    	input "batteryTile", "device.myBatteryStatusTile", required: false, title: "Use Jeff's Battery Status Tile?"
//    }
	section("Monitor These Devices That Report Temperature:"){
    	input "tempDevices", "capability.temperatureMeasurement", required: true, multiple: true
    }
    section("Alert Me When Any Reach This High Temperature:") {
    	input "highTemp", "number", required: true, title: "High Temperature"
        input "highCritical", "number", required: true, title: "Critical High Temperature"
    }
    section("Alert Me When Any Reach This Low Temperature:") {
    	input "lowTemp", "number", required: true, title: "Low Temperature"
        input "lowCritical", "number", required: true, title: "Critical Low Temperature"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification?"
    }
    section("Send a text message to this number (optional)") {
        input "phone", "phone", required: false
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "Initializing..."
	subscribe(tempDevices, "temperature", tempHandler)
//    subscribe(batteryTile, "update", batteryTileUpdateHandler)
//    subscribe(batteryTile, "batteryAlertLevel", batteryAlertLevelHandler)
    subscribe(app, appHandler)

//	state.batteryAlertLevel = percentage
	def dmap = [:]
	for (device in tempDevices) {
    	dmap << ["${device.displayName}":device.currentTemperature]
    }
    
	state.temps = dmap
}

def tempHandler(evt) {
	log.debug "tempHandler(${evt?.value})"
    def msg = "${location}: Temperature Alert!\n"
    def normalList = ""
    def lowList = ""
    def highList = ""
    def criticalList = ""
    def tCnt = 0
    def critical = false
    
    def val = evt?.value ? evt?.value as Integer : -99
    if (val >= highCritical || val <= lowCritical) {
    	critical = true
        log.debug "Critical Temperature Reached: ${evt.name} = ${evt.value}"
    }

	log.debug "state.temps = ${state.temps}"
    
	for (device in tempDevices) {
    	if (device.currentTemperature <= lowTemp) { 
            if (state.temps["${device.displayName}"] > lowTemp) {
	            tCnt ++
            }
            if (device.currentTemperature <= lowCritical) {
            	criticalList += "${device.displayName} = ${device.currentTemperature}\n"
            }
            else {
            	lowList += "${device.displayName} = ${device.currentTemperature}\n"
            }
        }
        else if (device.currentTemperature >= highTemp) {
            if (state.temps["${device.displayName}"] < highTemp) {
            	tCnt ++
            }
            if (device.currentTemperature >= highCritical) {
            	criticalList += "${device.displayName} = ${device.currentTemperature}\n"
            }
            else {
            	highList += "${device.displayName} = ${device.currentTemperature}\n"
            }
        }
        else {
	        normalList += "${device.displayName} = ${device.currentTemperature}\n"
        }
        state.temps["${device.displayName}"] = device.currentTemperature
    }
    log.debug "New state.temps = ${state.temps}"
    if (criticalList) {
    	msg += "The following have exceeded critical levels (${lowCritical}/${highCritical}):\n${criticalList}\n"
    }
    if (lowList) {
    	msg += "The following are at/below threshold of ${lowTemp}:\n${lowList}\n"
    }
    if (highList) {
		msg += "The following are at/above threshold of ${highTemp}:\n${highList}\n"
    }
    if (normalList) {
    	msg += "The following are in the normal range:\n${normalList}"
    }
    
	if (tCnt > 0 || critical) {
	    log.debug msg
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }	
    }
}

def appHandler(evt) {
	def cnt = 0
	def message = ""

    message = "${location} Temperature Status:\n"

	for (device in tempDevices) {
//    	log.debug "Log: ${device.displayName} = ${state.temps["${device.displayName}"]}"
    	if (cnt > 0) {
        	message = message + "\n"
        }
    	message = message + "${device.displayName} = ${device.currentTemperature} F"
        cnt++
    }

	log.debug "message == $message"
    if (sendPush) {
        sendPush(message)
    }
    if (phone) {
    	sendSms(phone, message)
    }
}
