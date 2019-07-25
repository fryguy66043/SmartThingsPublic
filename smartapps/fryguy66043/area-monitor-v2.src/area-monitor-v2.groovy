/**
 *  Group/Area Monitor v2
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
    name: "Area Monitor v2",
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
    	input "areaSensor", "device.myAreaSensor", required: true, title: "Which Status Sensor do you want to indicate area status?"
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
    subscribe(areaSensor, "report", areaSensorReport)
    subscribe(areaSensor, "allOnOff", allOnOffHandler)
    state.unsecure = false
    state.change = false
    runEvery5Minutes(virtualController)
    
    def jsonList = getMonitoredJsonString()
    areaSensor.setMonitoredDeviceList(jsonList)
    log.debug "setting unsecured to none."
    areaSensor.setUnsecuredDeviceList("")
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    getStatus()
	getMonitoredDevices()
}

private getMonitoredJsonString() {
	log.debug "getMonitoredJsonString"
    def sList = ""
	def cList = ""
    def dList = ""
    def lList = ""
    def json = ""
    
    if (switches) {
	    sList = "\"Switches\": ["
        switches.each { sList = sList + "\"${it}\"," }
        sList = sList + "]"
        json = "{ ${sList}"
    }
    if (contacts) {
	    cList = "\"Contacts\": ["
        contacts.each { cList = cList + "\"${it}\"," }
        cList = cList + "]"
        json = json ? json + ", ${cList}" : "{ ${clist}"
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
    def sList = ""
	def cList = ""
    def dList = ""
    def lList = ""
    def json = ""
    def oList = ""
    def result = ""
    
    if (switches) {
    	switches.each {dev ->
        	if (dev.getStatus() == "OFFLINE") {
            	if (!oList) {
                	oList = "\"Offline\": ["
                }
            	oList = oList + "\"${dev}\","
            }
            if (oList) {
            	oList = "{ ${oList}]}"
            }
        }
        result = switches.findAll{it.currentValue("switch") == "on"}
        if (result.size() > 0) {
            sList = "\"Switches\": ["
            switches.each { 
            	if (it.currentValue("switch") == "on") {
                	sList = sList + "\"${it}\"," 
                }
            }
            sList = sList + "]"
            json = "{ ${sList}"
        }
    }
    
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
            json = json ? json + ", ${clist}" : "{ ${cList}"
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

private getOfflineJsonString() {
	log.debug "getOfflineJsonString"
    def sList = ""
	def cList = ""
    def dList = ""
    def lList = ""
	def oList = ""
	def json = ""
    def result = ""
    
    if (switches) {
    	switches.each {dev ->
        	if (dev.getStatus() == "OFFLINE") {
            	if (!oList) {
                	oList = "\"Offline\": ["
                }
            	oList = oList + "\"${dev}\","
            }
        }
        if (oList) {
            oList = "{ ${oList}]}"
            json = oList
        }
    }
    log.debug "offline devices: ${json}"
    return json
    
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
            json = json ? json + ", ${clist}" : "{ ${cList}"
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

def areaSensorReport(evt) {
	log.debug "areaSensorReport(${evt.value})"
    areaSensor.setMonitoredDeviceList(getMonitoredJsonString())
    log.debug "setting unsecured to none."
    areaSensor.setUnsecuredDeviceList("")
    virtualController()
    getStatus()
    getMonitoredDevices()
}

def areaSensorHandler(evt) {
	log.debug "areaSensorHandler: ${evt.value}"
    areaSensor.setMonitoredDeviceList(getMonitoredJsonString())
    log.debug "setting unsecured to none."
    areaSensor.setUnsecuredDeviceList("")
    virtualController()
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

	deviceList = ""
    deviceList = getOfflineJsonString()
	areaSensor.setOfflineDeviceList(deviceList)
    
	deviceList = getUnsecuredJsonString()
    areaSensor.setUnsecuredDeviceList(deviceList)

	if (deviceList) {
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
    else {
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
	def deviceList = ""
    
	deviceList = getUnsecuredJsonString()
   
	if (deviceList) {
    	log.debug "Area unsecure!"
        state.unsecure = true
        if (sendPush) {
            sendPush(msg)
        }
        if (phone) {
            sendSms(phone, msg)
        }
    }
    else {
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