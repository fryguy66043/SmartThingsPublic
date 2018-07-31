/**
 *  Perimeter Monitor
 *  Monitors selected contact sensors, door controls, and locks and sets a virtual contact sensor to indicate if the perimeter is secure or not.
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
    name: "Perimeter Monitor",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Set a Virtual Contact Sensor to indicate if perimeter contact sensors, door controls and locks are closed/locked.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png")

preferences {
	section("Which Contact Sensors?") {
    	input "contacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
    }
	section("Which Door Controls?") {
    	input "doors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
    }
    section("Which Locks?") {
    	input "locks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
    }
	section("Which Virtual Contact Sensor?") {
    	input "perimeterSensor", "device.myContactSensor", required: true, title: "Which Contact Sensor do you want to indicate perimeter status?"
    }
    section("Send Realtime Updates?") {
    	input "realtimeUpdates", "bool", title: "Send realtime updates as activity occurs on selected devices?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when Opened?"
    }
    section("Send a text message to this number") {
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
    subscribe(app, appHandler)
    subscribe(contacts, "contact", contactHandler)
    subscribe(doors, "door", doorHandler)
    subscribe(locks, "lock", lockHandler)
    subscribe(perimeterSensor, "update", perimeterSensorHandler)
    state.unsecure = false
    state.change = false
    runEvery1Minute(virtualController)
	
    def contactList = (contacts) ? contacts : "None"
    def doorList = (doors) ? doors : "None"
    def lockList = (locks) ? locks : "None"
    def deviceList = "Contacts: ${contactList}\nDoors: ${doorList}\nLocks: ${lockList}"
    perimeterSensor.setMonitoredDeviceList(deviceList)
    perimeterSensor.setUnsecuredDeviceList("")
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    getStatus()
    getMonitoredDevices()
}

def perimeterSensorHandler(evt) {
	log.debug "perimeterSensorHandler: ${evt.value}"
    def contactList = (contacts) ? contacts : "None"
    def doorList = (doors) ? doors : "None"
    def lockList = (locks) ? locks : "None"
    def deviceList = "Contacts: ${contactList}\nDoors: ${doorList}\nLocks: ${lockList}"
    perimeterSensor.setMonitoredDeviceList(deviceList)
    perimeterSensor.setUnsecuredDeviceList("")
    virtualController()
//    getStatus()
//    getMonitoredDevices()
}

def contactHandler(evt) {
	log.debug "contactHandler: ${evt.value}"
    state.change = true
    virtualController()
}

def doorHandler(evt) {
	log.debug "doorHandler: ${evt.value}"
    state.change = true
    virtualController()
}

def lockHandler(evt) {
	log.debug "lockHandler: ${evt.value}"
    state.change = true
    virtualController()
}

def virtualController(evt) {
	log.debug "virtualController(): state.unsecure = ${state.unsecure}"
    log.debug "perimeterSensor = ${perimeterSensor.currentValue("contact")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nPerimeter Unsecure: "
    def deviceList = ""
	def unsecure = false
    def result = ""
    
    result = contacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
        deviceList = "Contacts: ${result}"
    }
    result = doors.findAll{it.currentValue("door") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
        deviceList = (deviceList) ? deviceList + "\nDoors: ${result}" : "Doors: ${result}"
    }
    result = locks.findAll{it.currentValue("lock") == "unlocked"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
        deviceList = (deviceList) ? deviceList + "\nLocks: ${result}" : "Locks: ${result}"
    }

	if (deviceList) {
    	perimeterSensor.setUnsecuredDeviceList(deviceList)
    }
    else {
    	perimeterSensor.setUnsecuredDeviceList("")
    }

	log.debug "unsecure = ${unsecure} / state.unsecure = ${state.unsecure}"
	if (unsecure) {
    	log.debug "Perimeter unsecure!"
        perimeterSensor.open()
        if (state.unsecure == false || state.change) {
        	log.debug "Send Unsecure Msg"
            state.unsecure = true
            if (realtimeUpdates) {
                if (sendPush) {
                    sendPush(msg)
                }
                if (phone) {
                    sendSms(phone, msg)
                }
            }
        }
    }
    else if (unsecure == false) {
    	log.debug "Perimter secure!"
        perimeterSensor.close()
        if (state.unsecure || state.change) {
        	log.debug "Send Secure Msg"
        	state.unsecure = false
            msg = "${location} ${date}\nPerimeter secured!"
            if (realtimeUpdates) {
                if (sendPush) {
                    sendPush(msg)
                }
                if (phone) {
                    sendSms(phone, msg)
                }
            }
        }
    }
    state.change = false
}

private getStatus() {
	log.debug "getStatus(): state.unsecure = ${state.unsecure}"
    log.debug "perimeterSensor = ${perimeterSensor.currentValue("contact")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nPerimeter Unsecure: "
	def unsecure = false
    def result = ""
    
    result = contacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }
    result = doors.findAll{it.currentValue("door") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }
    result = locks.findAll{it.currentValue("lock") == "unlocked"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }

	log.debug "unsecure = ${unsecure} / state.unsecure = ${state.unsecure}"
	if (unsecure) {
    	log.debug "Perimeter unsecure!"
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else if (unsecure == false) {
    	log.debug "Perimter secure!"
        state.unsecure = false
        msg = "${location} ${date}\nPerimeter secured!"
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    state.change = false
}

private getMonitoredDevices() {
	log.debug "getMonitoredDevices"
    def date = new Date().format("MM/dd/yyyy h:mm a", location.timeZone)
    def contactList = (contacts) ? contacts : "N/A"
    def doorList = (doors) ? doors : "N/A"
    def lockList = (locks) ? locks : "N/A"
    
	def msg = "${location}: ${date}\nMonitored Perimeter Devices:\nContact Sensors: ${contactList}\nDoor Controllers: ${doorList}\nLocks: ${lockList}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

