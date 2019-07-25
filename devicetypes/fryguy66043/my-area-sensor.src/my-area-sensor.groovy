/**
 *  My Area Sensor
 *
 *  Works with my Area Monitor smart app.  Used to monitor a group of devices in an area or other logical grouping and provide a single source to see if any are on/open/unlocked.
 *  It also provide action tiles to turn all monitored switches on or off with a single press. 
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
	preferences {
    	input "portalService", "bool", title: "Turn calls to PyPortal Service On?", default: false, required: false
    	input "portalServiceIP", "text", title: "(Optional) PyPortal Service IP", requried: false
        input "portalServicePort", "text", title: "(Optional) PyPortal Service Port", required: false
    }
	definition (name: "My Area Sensor", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
//		capability "Switch"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"
        attribute "report", "string"
        attribute "allOnOff", "enum", ["ALL OFF", "ALL ON"]
        attribute "offlineDeviceList", "string"
        attribute "monitoredDeviceList", "string"
        attribute "unsecuredDeviceList", "string"
        attribute "lastUnsecuredDeviceList", "string"
        attribute "lastUnsecuredDateTime", "string"
        attribute "lastSecuredDateTime", "string"

        command "off"
        command "on"
        command "allOff"
        command "allOn"
        command "setOfflineDeviceList"
        command "setMonitoredDeviceList"
        command "setUnsecuredDeviceList"
        command "report"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("state", "device.switch", width: 2, height: 2) {
			state("off", label:'${name}', icon:"st.Lighting.light13", backgroundColor:"#00A0DC")
			state("on", label:'${name}', icon:"st.Lighting.light11", backgroundColor:"#e86d13")
            state("offline", label:'${name}', icon:"st.Lighting.light11", backgroundColor:"#f1d801")
		}
        
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}
        standardTile("report", "device.report", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '', action: "report", icon: "st.Office.office19"
        }
		standardTile("allOff", "device.onOff", decoration: "flat", width: 2, height: 2) {
			state "default", label: "ALL OFF", action: "allOff", icon:"st.Lighting.light13"
		}
		standardTile("allOn", "device.onOff", decoration: "flat", width: 2, height: 2) {
			state "default", label: "ALL ON", action: "allOn", icon:"st.Lighting.light11"
		}
        valueTile("monitoredDevices", "device.monitoredDevices", decoration: "flat", width: 6, height: 5) {
        	state "default", label: 'Monitored Devices:\n${currentValue}'
        }
        valueTile("unsecuredDevices", "device.unsecuredDevices", decoration: "flat", width: 6, height: 3) {
        	state "default", label: 'Unsecured Devices:\n${currentValue}'
        }
        valueTile("lastUnsecuredDevices", "device.lastUnsecuredDevices", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Last Unsecured Devices:\n${currentValue}'
        }
        valueTile("offlineDevices", "device.offlineDevices", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Offline Devices:\n${currentValue}'
        }

		main "state"
		details(["state", "allOff", "allOn", "unsecuredDevices", "lastUnsecuredDevices", "monitoredDevices", "offlineDevices", "refresh", "report"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def on() {
	log.debug "switch: on()"
	sendEvent(name: "switch", value: "on")
}

def off() {
	log.debug "switch: off()"
    log.debug "offlineDeviceList: ${device.currentValue("offlineDeviceList")}"
    if (device.currentValue("offlineDeviceList") != "None") {
    	sendEvent(name: "switch", value: "offline")
    }
    else {
        sendEvent(name: "switch", value: "off")
    }
}

def allOff() {
	log.debug "switch: request allOff()"
	sendEvent(name: "allOnOff", value: "ALL OFF", isStateChange: true)
}

def allOn() {
	log.debug "switch: request allOn()"
	sendEvent(name: "allOnOff", value: "ALL ON", isStateChange: true)
}

private cleanJsonString(jsonString) {
	log.debug "cleanJsonString(${jsonString})"
    def newString = jsonString
    if (jsonString?.size() > 0) {
    	if (jsonString.contains(",]")) {
        	newString = jsonString.replace(",]", "]")
        }
    }
    log.debug "cleanJsonString = ${newString}"
    return newString
}

private getJsonDisplay(jsonString, showAll) {
	log.debug "getJsonDisplay(${jsonString})"
    def disp = ""
    def cnt = 0
    def newString = jsonString
	if (jsonString?.size() > 0) {    
    	if (jsonString.contains(",]")) {
        	log.debug "found ',]'"
            newString = jsonString.replace(",]", "]")
        }
	    def jObj = new JsonSlurper()?.parseText(newString)
        
        if (jObj?.Switches?.size()) {
        	disp = disp ? disp + "\nSwitches: [" : "Switches: ["
            cnt = 0
            jObj.Switches.each {
            	cnt = cnt + 1
                disp = disp + "${it}"
                if (cnt < jObj.Switches.size()) {
                	disp = disp + ", "
                }
            }
            disp = disp + "]"
        }
        else {
        	if (showAll) {
            	disp = "Switches: None"
            }
        }
        if (jObj?.Contacts?.size()) {
        	disp = disp ? disp + "\nContacts: [" : "Contacts: ["
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
        		disp = disp + "\nContacts: None"
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
	log.debug "setMonitoredDeviceList(${deviceList})"
    def jsonValue = cleanJsonString(deviceList)
    log.debug "json Display = ${jsonValue}"
    def jsonDisplayValue = getJsonDisplay(jsonValue, true)
	if (deviceList) {
    	sendEvent(name: "monitoredDeviceList", value: deviceList)
        sendEvent(name: "monitoredDevices", value: "${getJsonDisplay(deviceList, true)}")
    }
    else {
    	sendEvent(name: "monitoredDeviceList", value: "None")
        sendEvent(name: "monitoredDevices", value: "None")
    }
    updateServerDeviceList("Lights", jsonValue)
}

def setUnsecuredDeviceList(deviceList) {
	log.debug "setUnsecuredDeviceList(${deviceList})"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def unsecDeviceList = "${device.currentValue("unsecuredDeviceList")}"
    
    def jsonValue = deviceList
    if (deviceList.size() > 0) {
    	jsonValue = cleanJsonString(deviceList)
    }
    log.debug "json Display = ${jsonValue}"
    def jsonDisplayValue = getJsonDisplay(jsonValue, false)
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
    updateServerDeviceList("LightsUnsec", jsonValue)
}

def setOfflineDeviceList(deviceList) {
	log.debug "setOfflineDeviceList(${deviceList})"
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def offlineDeviceList = "${device.currentValue("offlineDeviceList")}"
    
    def jsonValue = deviceList
    if (deviceList.size() > 0) {
    	jsonValue = cleanJsonString(deviceList)
    }
    log.debug "json Display = ${jsonValue}"
    def jsonDisplayValue = getJsonDisplay(jsonValue, false)
    def offlineDeviceDisplay = getJsonDisplay(jsonValue, false)
    log.debug "*** jsonValue: ${jsonValue}\n*** jsonDisplayValue: ${jsonDisplayValue}"

	offlineDeviceDisplay = jsonValue.replace("{ \"Offline\": ", "")
	offlineDeviceDisplay = offlineDeviceDisplay.replace("}", "")
	offlineDeviceDisplay = offlineDeviceDisplay.replace("\"", "")    

	if (deviceList) {
        if (true) { //(deviceList != device.currentValue("offlineDeviceList")) {
            sendEvent(name: "offlineDeviceList", value: deviceList)
            sendEvent(name: "offlineDevices", value: offlineDeviceDisplay)
        }
    }
    else if (device.currentValue("offlineDeviceList") != "None") {
    	log.debug "No offline devices..."
    	sendEvent(name: "offlineDeviceList", value: "None")
        sendEvent(name: "offlineDevices", value: "${date}\nNone")
    }
//    updateServerDeviceList("LightsUnsec", jsonValue)
}

def getHost() {
	def PI_IP = "192.168.1.189"
	def PI_PORT = "5000"

	return "${PI_IP}:${PI_PORT}"
}

def updateServerDeviceList(list, values) {
	log.debug "updateServerList(${list}, ${values})"
	def date = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    date = URLEncoder.encode(date, "UTF-8")

	if (portalService && portalServiceIP && portalServicePort) {    	
    	log.debug "Calling PyPortal Service"
        def listVals =  ''
        if (values.size() > 0) {
        	listVals = URLEncoder.encode(values, "UTF-8")
        }
        else {
        	listVals = "None"
        }
        state.serverRefresh = false
        
        def cmd = "monitored/devices?list=${list}&listVals=${listVals}&asof=${date}"
        def host = getHost()
//        log.debug "cmd = ${cmd}\nhost = ${host}"
        def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "/${cmd}",
            headers: [
                "HOST" : "${host}"],
            null,
            [callback: updateServerDeviceListHandler]
        )
//        log.debug "result = ${result.toString()}"
        sendHubCommand(result)
	}
    else {
    	log.debug "PyPortal Service Not Configured"
    }
}

def updateServerDeviceListHandler(sData) {
	log.debug "updateServerDeviceListHandler(status: ${sData.status} / body = ${sData.body})"
}

private anyMatches(list1, list2) {
	log.debug "anyMatches(${list1} / ${list2})"
	def result = false

    if (list1.contains(",]")) {
    	list1 = list1.replace(",]", "]")
    }
    if (list2.contains(",]")) {
    	list2 = list2.replace(",]", "]")
    }
	if (list1[0] == "{" && list2[0] == "{") {
        def newList1 = new JsonSlurper().parseText(list1)
        def newList2 = new JsonSlurper().parseText(list2)
        if (newList1?.Switches?.size() && newList2?.Switches?.size()) {
        	newList1.Switches.each { s1 ->
            	newList2.Switches.each { s2 ->
                	log.debug "${s1} == ${s2}: ${s1 == s2}"
                    if (s1 == s2) {
                    	result = true
                    }
                }
            }
        }
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
	log.debug "switch: request refresh()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
}

def report() {
	log.debug "report"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "report", value: timestamp)
}
