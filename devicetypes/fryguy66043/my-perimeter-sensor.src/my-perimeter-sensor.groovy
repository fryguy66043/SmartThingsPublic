/**
 *  My Perimeter Sensor
 *
 *  Designed to work with my Perimeter Monitor smart app.  Similar in functionality to My Status Sensor device, but it doesn't monitor switches, only open/close sensors, door controllers, 
 *  and SmartLocks.  It provides a single view into the state of all perimeter devices that are being monitored.
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
 
import groovy.json.JsonSlurper

metadata {
	definition (name: "My Perimeter Sensor", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "update", "string"
        attribute "report", "string"
        attribute "monitoredDeviceList", "string"
        attribute "unsecuredDeviceList", "string"
        attribute "lastUnsecuredDeviceList", "string"
        attribute "lastUnsecuredDateTime", "string"
        attribute "lastSecuredDateTime", "string"
        
        command "close"
        command "open"
        command "setMonitoredDeviceList"
        command "setUnsecuredDeviceList"
        command "report"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("state", "device.contact", width: 2, height: 2) {
			state("closed", label:'SECURE', icon:"st.security.alarm.on", backgroundColor:"#00A0DC")
			state("open", label:'UNSECURE', icon:"st.security.alarm.off", backgroundColor:"#e86d13")
		}
        valueTile("monitoredDevices", "device.monitoredDevices", decoration: "flat", width: 6, height: 3) {
        	state "default", label: 'Monitored Devices:\n${currentValue}'
        }
        valueTile("unsecuredDevices", "device.unsecuredDevices", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Unsecured Devices:\n${currentValue}'
        }
        valueTile("lastUnsecuredDevices", "device.lastUnsecuredDevices", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Last Unsecured Devices:\n${currentValue}'
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("report", "device.report", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '', action: "report", icon: "st.Office.office19"
        }
        
		main "state"
		details(["state", "refresh", "report", "unsecuredDevices", "lastUnsecuredDevices", "monitoredDevices"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def open() {
	sendEvent(name: "contact", value: "open")
}

def close() {
    sendEvent(name: "contact", value: "closed")
}

private getJsonDisplay(jsonString, showAll) {
	log.debug "getJsonDisplay(${jsonString})"
    def disp = ""
    def cnt = 0
	if (jsonString) {    
	    def jObj = new JsonSlurper().parseText(jsonString)
        if (jObj?.Contacts?.size()) {
        	disp = "Contacts: ["
            cnt = 0
        	jObj.Contacts.each { 
            	cnt = cnt + 1
                disp = disp + "${it}" 
                if (cnt < jObj.Contacts.size()) {
                	disp = disp + ", "
                }
            }
            disp = disp + "]"
        }
        else {
        	if (showAll) {
        		disp = "Contacts: None"
            }
        }
        if (jObj?.Doors?.size()) {
        	disp = disp ? disp + "\nDoors: [" : "Doors: ["
            cnt = 0
            jObj.Doors.each {
            	cnt = cnt + 1
                disp = disp + "${it}"
                if (cnt < jObj.Doors.size()) {
                	disp = disp + ", "
                }
            }
            disp = disp + "]"
        }
        else {
        	if (showAll) {
        		disp = disp + "\nDoors: None"
            }
        }
        if (jObj?.Locks?.size()) {
        	disp = disp ? disp + "\nLocks: [" : "Locks: ["
            cnt = 0
            jObj.Locks.each {
            	cnt = cnt + 1
                disp = disp + "${it}"
                if (cnt < jObj.Locks.size()) {
                	disp = disp + ", "
                }
            }
            disp = disp + "]"
        }
        else {
        	if (showAll) {
        		disp = disp + "\nLocks: None"
            }
        }
    }
    log.debug "disp = ${disp}"
    return disp
}

def setMonitoredDeviceList(deviceList) {
	if (deviceList) {
    	sendEvent(name: "monitoredDeviceList", value: deviceList)
        sendEvent(name: "monitoredDevices", value: "${getJsonDisplay(deviceList, true)}")
    }
    else {
    	sendEvent(name: "monitoredDeviceList", value: "None")
        sendEvent(name: "monitoredDevices", value: "None")
    }
}

def setUnsecuredDeviceList(deviceList) {
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def unsecDeviceList = "${device.currentValue("unsecuredDeviceList")}"
    def unsecDeviceDisplay = getJsonDisplay(deviceList, false)

    if (deviceList) {
        if (deviceList != device.currentValue("unsecuredDeviceList")) {
            sendEvent(name: "unsecuredDeviceList", value: deviceList)
            sendEvent(name: "unsecuredDevices", value: unsecDeviceDisplay)
            sendEvent(name: "lastUnsecuredDeviceList", value: unsecDeviceDisplay)
            if (!anyMatches(deviceList, unsecDeviceList)) {
	            sendEvent(name: "lastUnsecuredDateTime", value: date)
            }
            sendEvent(name: "lastUnsecuredDevices", value: "${device.currentValue("lastUnsecuredDateTime")}\n${unsecDeviceDisplay}")
        }
    }
    else if (device.currentValue("unsecuredDeviceList") != "None") {
    	log.debug "No unsecure devices..."
    	sendEvent(name: "unsecuredDeviceList", value: "None")
        sendEvent(name: "unsecuredDevices", value: "${date}\nNone")
        sendEvent(name: "lastSecuredDateTime", value: date)
    }
}

private anyMatches(list1, list2) {
	log.debug "anyMatches(${list1} / ${list2})"
	def result = false

	if (list1[0] == "{" && list2[0] == "{") {
        def newList1 = new JsonSlurper().parseText(list1)
        def newList2 = new JsonSlurper().parseText(list2)
        if (newList1?.Contacts?.size() && newList2?.Contacts?.size()) {
            newList1.Contacts.each { c1 ->
            	newList2.Contacts.each { c2 ->
                    log.debug "${c1} == ${c2}: ${c1 == c2}"
                    if (c1 == c2) {
                        result = true
                    }
                }
            }
        }
        if (!result && newList1?.Doors?.size() && newList2?.Doors?.size()) {
            newList1.Doors.each { d1 ->
            	newList2.Doors.each { d2 ->
	                log.debug "${d1} == ${d2}: ${d1 == d2}"
    	            if (d1 == d2) {
                    	result = true
                    }
                }
            }
        }
        if (!result && newList1?.Locks?.size() && newList2?.Locks?.size()) {
            newList1.Locks.each { l1 ->
            	newList2.Locks.each { l2 ->
	                log.debug "${l1} == ${l2}: ${l1 == l2}"
                    if (l1 == l2) {
                        result = true
                    }
                }
            }
        }
    }
    else {
    	log.debug "Invalid json string"
    }
    
    log.debug "Return ${result}"
    return result
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
    refresh()
}

def refresh() {
	log.debug "refresh"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
}

def report() {
	log.debug "report"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "report", value: timestamp)
}