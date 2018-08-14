/**
 *  Perimeter Monitor v2
 *  Monitors selected contact sensors, door controls, and locks and sets a virtual contact sensor to indicate if the perimeter is secure or not.
 *  Works in conjuction with 'My Contact Sensor' device handler.
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
    name: "Perimeter Monitor v2",
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
	section("Choose 'My Contct Sensor'.") {
    	input "perimeterSensor", "device.myPerimeterSensor", required: true, title: "Choose 'My Contact Sensor' to update display in tile."
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
    unschedule()
	initialize()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(contacts, "contact", contactHandler)
    subscribe(doors, "door", doorHandler)
    subscribe(locks, "lock", lockHandler)
    subscribe(perimeterSensor, "update", perimeterSensorHandler)
    subscribe(perimeterSensor, "report", perimeterSensorReport)
    state.unsecure = false
    state.change = false
    runEvery5Minutes(virtualController)
	
    def jsonList = getMonitoredJsonString()
    perimeterSensor.setMonitoredDeviceList(jsonList)
    perimeterSensor.setUnsecuredDeviceList("")
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    getStatus()
    getMonitoredDevices()
}

private getMonitoredJsonString() {
	log.debug "getMonitoredJsonString"
	def cList = ""
    def dList = ""
    def lList = ""
    def json = ""
    
    if (contacts) {
	    cList = "\"Contacts\": ["
        contacts.each { cList = cList + "\"${it}\"," }
        cList = cList + "]"
        json = "{ ${cList}"
    }
    if (doors) {
    	dList = "\"Doors\": ["
        doors.each { dList = dList + "\"${it}\"," }
        dList = dList + "]"
        json = json ? json + ", ${dList}" : "{ ${dList}"
    }
    if (locks) {
    	lList = "\"Locks\": ["
        locks.each {lList = lList + "\"${it}\"," }
        lList = lList + "]"
        json = json ? json + ", ${lList}" : "{ ${lList}"
    }
    json = json ? "${json} }" : ""
    log.debug "Monitored json = ${json}"
	return json
}

private getUnsecuredJsonString() {
	log.debug "getUnsecuredJsonString"
	def cList = ""
    def dList = ""
    def lList = ""
    def json = ""
    def result = ""
    
    if (contacts) {
        result = contacts.findAll{it.currentValue("contact") == "open"}
        if (result.size() > 0) {
            cList = "\"Contacts\": ["
            contacts.each { 
            	if (it.currentValue("contact") == "open") {
                	cList = cList + "\"${it}\"," 
                }
            }
            cList = cList + "]"
            json = "{ ${cList}"
        }
    }
    
    if (doors) {
        result = doors.findAll{it.currentValue("door") == "open"}
        if (result.size() > 0) {
            dList = "\"Doors\": ["
            doors.each { 
            	if (it.currentValue("door") == "open") {
	                dList = dList + "\"${it}\"," 
                }
            }
            dList = dList + "]"
            json = json ? json + ", ${dList}" : "{ ${dList}"
        }
    }
    
    if (locks) {
        result = locks.findAll{it.currentValue("lock") == "unlocked"}
        if (result.size() > 0) {
            lList = "\"Locks\": ["
            locks.each {
            	if (it.currentValue("lock") == "unlocked") {
                	lList = lList + "\"${it}\"," 
                }
            }
            lList = lList + "]"
            json = json ? json + ", ${lList}" : "{ ${lList}"
        }
    }
    json = json ? "${json} }" : ""
    log.debug "Monitored json = ${json}"
	return json
}

def perimeterSensorReport(evt) {
	log.debug "perimeterSensorReport(${evt.value})"
    perimeterSensor.setMonitoredDeviceList(getMonitoredJsonString())
    perimeterSensor.setUnsecuredDeviceList("")
    virtualController()
    getStatus()
    getMonitoredDevices()
}

def perimeterSensorHandler(evt) {
	log.debug "perimeterSensorHandler: ${evt.value}"
    perimeterSensor.setMonitoredDeviceList(getMonitoredJsonString())
    perimeterSensor.setUnsecuredDeviceList("")
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
    log.debug "perimeterSensor = ${perimeterSensor.currentValue("contact")}"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def msg = "${location} ${date}\nPerimeter Unsecure: "
    def deviceList = ""

	deviceList = getUnsecuredJsonString()
    perimeterSensor.setUnsecuredDeviceList(deviceList)

	if (deviceList) {
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
    else {
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
	def deviceList = ""
    
	deviceList = getUnsecuredJsonString()

	if (deviceList) {
    	log.debug "Perimeter unsecure!"
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else {
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
