/**
 *  Group/Area Monitor
 *  Monitors selected lights, switches, contact sensors, door controls, and locks and sets a virtual status monitor switch to indicate if the area is secure 
 *  and lights/switches are off.
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
    name: "Area Monitor",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Set a Virtual Monitor Switch to indicate if area contact sensors, door controls and locks are closed/locked and lights/switches are off.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png")

preferences {
	section("Name of Area to monitor?") {
    	input "areaName", "text", required: true, multiple: false, title: "Which Area do you want to monitor?"
    }
	section("Which Lights and Switches?") {
    	input "switches", "capability.switch", required: false, multiple: true, title: "Which Switches do you want to monitor?"
    }
	section("Which Contact Sensors?") {
    	input "contacts", "capability.contactSensor", required: false, multiple: true, title: "Which Contact Sensors do you want to monitor?"
    }
	section("Which Door Controls?") {
    	input "doors", "capability.doorControl", required: false, multiple: true, title: "Which Door Controls do you want to monitor?"
    }
    section("Which Locks?") {
    	input "locks", "capability.lock", required: false, multiple: true, title: "Which Locks do you want to monitor?"
    }
	section("Which Virual Switch?") {
    	input "areaSensor", "device.myStatusSensor", required: true, title: "Which Status Sensor do you want to indicate area status?"
    }
    section("Provide Realtime Updates?") {
    	input "realtimeUpdates", "bool", title: "Send realtime updates (notification and/or text) for activity on selected devices?"
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
    unschedule()
	initialize()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(switches, "switch", switchHandler)
    subscribe(contacts, "contact", contactHandler)
    subscribe(doors, "door", doorHandler)
    subscribe(locks, "lock", lockHandler)
    subscribe(areaSensor, "update", areaSensorHandler)
    subscribe(areaSensor, "allOnOff", allOnOffHandler)
    state.unsecure = false
    state.change = false
    runEvery5Minutes(virtualController)
    
    def switchList = (switches) ? switches : "None"
    def contactList = (contacts) ? contacts : "None"
    def doorList = (doors) ? doors : "None"
    def lockList = (locks) ? locks : "None"
    def deviceList = "Switches: ${switchList}\nContacts: ${contactList}\nDoors: ${doorList}\nLocks: ${lockList}"
    log.debug "deviceList = ${deviceList}"
    areaSensor.setMonitoredDeviceList(deviceList)
    areaSensor.setUnsecuredDeviceList("")
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    getStatus()
	getMonitoredDevices()
}

def areaSensorHandler(evt) {
	log.debug "areaSensorHandler: ${evt.value}"
    def switchList = (switches) ? switches : "None"
    def contactList = (contacts) ? contacts : "None"
    def doorList = (doors) ? doors : "None"
    def lockList = (locks) ? locks : "None"
    def deviceList = "Switches: ${switchList}\nContacts: ${contactList}\nDoors: ${doorList}\nLocks: ${lockList}"
    log.debug "deviceList = ${deviceList}"
    areaSensor.setMonitoredDeviceList(deviceList)
    areaSensor.setUnsecuredDeviceList("")
    virtualController()
    getStatus()
    getMonitoredDevices()
}

def allOnOffHandler(evt) {
	log.debug "allOnOffHandler: ${evt.value}"
    log.debug "Switches: ${switches}"
    if (areaSensor.currentValue("allOnOff") == "ALL ON") {
    	log.debug "Turning all switches on..."
    	switches?.on()
    }
    else if (areaSensor.currentValue("allOnOff") == "ALL OFF") {
    	log.debug "Turning all switches off..."
    	switches?.off()
    }
}

def switchHandler(evt) {
	log.debug "switchHandler: ${evt.value}"
    state.change = true
    virtualController()    
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
    log.debug "areaSensor = ${areaSensor.currentValue("switch")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
	def msg = "${location} ${date}\n${areaName} - Area Unsecure and/or Switches On:\n"
    def deviceList = ""
	def unsecure = false
    def result = ""

	result = switches.findAll{it.currentValue("switch") == "on"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
        deviceList = "Switches: ${result}"
    }
    
    result = contacts.findAll{it.currentValue("contact") == "open"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
        deviceList = (deviceList) ? deviceList + "\nContacts: ${result}" : "Contacts: ${result}"
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
    	log.debug "unsecuredDeviceList: ${deviceList}"
    	areaSensor.setUnsecuredDeviceList(deviceList)
    }
    else {
    	areaSensor.setUnsecuredDeviceList("")
    	log.debug "No unsecured devices..."
    }

	log.debug "unsecure = ${unsecure} / state.unsecure = ${state.unsecure}"
	if (unsecure) {
    	log.debug "Area unsecure!"
        areaSensor.on()
        if (state.unsecure == false || state.change) {
        	log.debug "Send Unsecure Msg"
            state.unsecure = true
            log.debug "realtimeUpdates = ${realtimeUpdates}"
            if (realtimeUpdates) {
            	log.debug "Sending realtime update"
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
    	log.debug "Area secure!"
        areaSensor.off()
        if (state.unsecure || state.change) {
        	log.debug "Send Secure Msg"
        	state.unsecure = false
            msg = "${location} ${date}\n${areaName} - Area Secured and All Switches Off!"
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
    log.debug "areaSensor = ${areaSensor.currentValue("switch")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\n${areaName} - Area Unsecure and/or Switches On:\n"
	def unsecure = false
    def result = ""
    
	result = switches.findAll{it.currentValue("switch") == "on"}
    if (result.size() > 0) {
        unsecure = true
        msg = msg + result
    }

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
    	log.debug "Area unsecure!"
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else if (unsecure == false) {
    	log.debug "Area secure!"
        state.unsecure = false
        msg = "${location} ${date}\n${areaName} - Area Secured and All Switches OFF!"
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
	def switchList = (switches) ? switches : "N/A"
    def contactList = (contacts) ? contacts : "N/A"
    def doorList = (doors) ? doors : "N/A"
    def lockList = (locks) ? locks : "N/A"
    
	def msg = "${location}: ${date}\n${areaName} Monitored Devices:\nSwitches: ${switchList}\nContact Sensors: ${contactList}\nDoor Controllers: ${doorList}\nLocks: ${lockList}"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

