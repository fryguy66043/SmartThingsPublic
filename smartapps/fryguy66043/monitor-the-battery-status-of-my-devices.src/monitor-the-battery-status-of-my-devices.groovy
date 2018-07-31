/**
 *  Monitor the battery status of my devices
 *
 *  Copyright 2018 Jeffrey Fry
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
    name: "Monitor the Battery Status of My Devices",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Monitor the Battery Status of My Devices with Optional Temperature Readings.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Monitor These Devices With Batteries:"){
    	input "batteries", "capability.battery", required: true, multiple: true
    }
    section("Alert Me When They Reach This Level:") {
    	input "percentage", "number", required: true, title: "Percentage"
    }
	section("Get Temperature Updates From These Devices (optional):") {
    	input "temps", "capability.temperatureMeasurement", multiple: true, required: false
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
	subscribe(batteries, "battery", batteryHandler)
    subscribe(app, appHandler)
}

def batteryHandler(evt) {
    def msg = "${location} Battery % Is At/Below Threshold (${percentage}%)!\n\nBattery Status:\n"
	def cnt = 0
    def tCnt = 0

	for (device in batteries) {
    	if (cnt > 0) {
        	msg = msg + "\n"
        }
        if (device.currentBattery <= percentage) {
        	tCnt++
        }
    	msg = msg + "${device.displayName} = ${device.currentBattery}%"
        cnt++
    }

	if (tCnt > 0) {
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

    message = "${location} Battery Status:\n"

	for (device in batteries) {
    	if (cnt > 0) {
        	message = message + "\n"
        }
    	message = message + "${device.displayName} = ${device.currentBattery}%"
        cnt++
    }

	def TempMessage = ""
	cnt = 0
    for (tsensor in temps) {
    	if (cnt > 0) {
        	TempMessage = TempMessage + "\n"
        }
        else {
        	TempMessage = "Temperature Readings:\n"
        }
        TempMessage = TempMessage + "${tsensor.displayName} = ${tsensor.currentTemperature}"
        cnt++
    }

	log.debug "message == $message"
    log.debug "TempMessage = $TempMessage"
    def pushMessage = "${message} \n\n${TempMessage}"
    if (sendPush) {
        sendPush(pushMessage)
    }
    if (phone) {
    	sendSms(phone, pushMessage)
    }
}

