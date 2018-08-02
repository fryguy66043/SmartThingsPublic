/**
 *  Monitor the battery status of my devices
 *
 *  This SmartApp allows you to monitor the battery status of all battery-operated devices.  You define an alert threshold and it will alert you via push or text
 *  if the threshold is reached.  You can also optionally include the "My Battery Status Tile" to provide a visual indicator of battery levels and easily adjust
 *  alert-level setting.
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
	section("Battery Status Tile?") {
    	input "batteryTile", "device.myBatteryStatusTile", required: false, title: "Use Jeff's Battery Status Tile?"
    }
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
	log.debug "Initializing..."
	subscribe(batteries, "battery", batteryHandler)
    subscribe(batteryTile, "update", batteryTileUpdateHandler)
    subscribe(batteryTile, "batteryAlertLevel", batteryAlertLevelHandler)
    subscribe(app, appHandler)

	state.batteryAlertLevel = percentage
    if (batteryTile) {
    	batteryHandler()
    }
}

def batteryAlertLevelHandler(evt) {
	log.debug "batteryAlertLevelHandler(${evt?.value})"
    if (batteryTile.currentValue("batteryAlertLevel") != state.batteryAlertLevel) {
    	state.batteryAlertLevel = batteryTile.currentValue("batteryAlertLevel")
        batteryHandler()
    }
}

def batteryTileUpdateHandler(evt) {
	log.debug "batteryTileUpdateHandler(${evt?.value})"
    batteryHandler(evt)
}

def batteryHandler(evt) {
	log.debug "batteryHandler(${evt?.value})"
    def msg = "${location}: Battery % is At/Below Alert Threshold of ${state.batteryAlertLevel}%\n\nBattery Status:\n"
    def deviceList = ""
    def alertList = ""
	def cnt = 0
    def tCnt = 0

	for (device in batteries) {
    	if (cnt > 0) {
        	msg = msg + "\n"
            deviceList = deviceList + "\n"
        }
        if (device.currentBattery <= state.batteryAlertLevel) {
            if (tCnt > 0) {
                alertList = alertList + "\n"
            }
            alertList = alertList + "[${device.displayName}: ${device.currentBattery}%]"
        	tCnt++
        }
        deviceList = deviceList + "[${device.displayName}: ${device.currentBattery}%]"
    	msg = msg + "${device.displayName} = ${device.currentBattery}%"
        cnt++
    }
    batteryTile?.setDeviceList(deviceList)
    batteryTile?.setDeviceAlertList(alertList)
    batteryTile?.setBatteryAlertLevel("${state.batteryAlertLevel}")

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

