/**
 *  My Contact Sensor
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
 
metadata {
	definition (name: "My Contact Sensor", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "update", "string"
        attribute "monitoredDeviceList", "string"
        attribute "unsecuredDeviceList", "string"
        attribute "lastUnsecuredDeviceList", "string"
        attribute "lastUnsecuredDateTime", "string"
        attribute "lastSecuredDateTime", "string"
        
        command "close"
        command "open"
        command "setMonitoredDeviceList"
        command "setUnsecuredDeviceList"
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
        
		main "state"
		details(["state", "refresh", "unsecuredDevices", "lastUnsecuredDevices", "monitoredDevices"])
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

def setMonitoredDeviceList(deviceList) {
	if (deviceList) {
    	sendEvent(name: "monitoredDeviceList", value: deviceList)
        sendEvent(name: "monitoredDevices", value: deviceList)
    }
    else {
    	sendEvent(name: "monitoredDeviceList", value: "None")
        sendEvent(name: "monitoredDevices", value: "None")
    }
}

def setUnsecuredDeviceList(deviceList) {
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def unsecDeviceList = "${device.currentValue("unsecuredDeviceList")}"

    if (deviceList) {
        if (deviceList != device.currentValue("unsecuredDeviceList")) {
            sendEvent(name: "unsecuredDeviceList", value: deviceList)
            sendEvent(name: "unsecuredDevices", value: deviceList)
            sendEvent(name: "lastUnsecuredDeviceList", value: deviceList)
            if (!anyMatches(deviceList, unsecDeviceList)) {
	            sendEvent(name: "lastUnsecuredDateTime", value: date)
            }
            sendEvent(name: "lastUnsecuredDevices", value: "${device.currentValue("lastUnsecuredDateTime")}\n${deviceList}")
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
    def newList1 = "${list1}"
    def newList2 = "${list2}"
    def listSize = 0

	newList1 = "${newList1.replace("\n", "")}"
    newList1 = "${newList1.replace("Contacts: [", "")}"
    newList1 = "${newList1.replace("Doors: [", "")}"
    newList1 = "${newList1.replace("Locks: [", "")}"
    newList1 = "${newList1.replace("]", ",")}"	
    newList1 = "${newList1.replace("[", "")}"
    listSize = newList1.size()
    log.debug "newList1.size() = ${listSize}"
    if (newList1.substring(listSize-1, listSize) == ",") {
    	log.debug "Changing newList1 from '${newList1}' to '${newList1.substring(0, listSize-1)}'"
    	newList1 = newList1.substring(0, listSize-1)
    }
    log.debug "newList1 pre-split: ${newList1}"
	def list1Array = newList1.split(",")
    log.debug "list1Array after split: ${list1Array}"

	newList2 = "${newList2.replace("\n", "")}"
    newList2 = "${newList2.replace("Contacts: [", "")}"
    newList2 = "${newList2.replace("Doors: [", "")}"
    newList2 = "${newList2.replace("Locks: [", "")}"
    newList2 = "${newList2.replace("[", "")}"
    newList2 = "${newList2.replace("]", ",")}"	
    listSize = newList2.size()
    if (newList2.substring(listSize-1, listSize) == ",") {
    	newList2 = newList2.substring(0, listSize-1)
    }
    log.debug "newList2 pre-split: ${newList2}"
	def list2Array = newList2.split(",")
    log.debug "list2Array after split: ${list2Array}"

	for (int x=0; x<list1Array.size(); x++) {
    	for (int i=0; i<list2Array.size(); i++) {
        	log.debug "comparing '${list1Array[x]}' to '${list2Array[i]}'"
        	if (list1Array[x] == list2Array[i]) {
            	log.debug "Found: '${list1Array[x]}' = '${list2Array[i]}'"
            	result = true
                break
            }
        }
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
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
}
