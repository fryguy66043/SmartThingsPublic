/**
 *  My Battery Status Tile
 *
 *  Works with the SmartApp "Monitor the Battery Status of My Devices".  Provides a visual indicator of the status of your selected devices and allows you to change the alert-level
 *  settings in the tile.
 *
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
	definition (name: "My Battery Status Tile", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"
        attribute "deviceStatusList", "string"
        attribute "deviceAlertList", "string"
        attribute "batteryAlertLevel", "number"

        command "ok"
        command "alert"
        command "setDeviceList"
        command "setDeviceAlertList"
        command "setBatteryAlertLevel"
        command "reset"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("state", "device.switch", width: 2, height: 2) {
			state("ok", label:'${name}', icon:"st.Lighting.light11", backgroundColor:"#00A0DC")
			state("alert", label:'${name}', icon:"st.Lighting.light8", backgroundColor:"#e86d13")
            state("off", label: '${name}', icon:"st.Lighting.light8", backgroundColor:"##ffffff")
		}        

        valueTile("alertLevel", "device.alertLevel", decoration: "flat", width: 2, height: 2) {
        	state "default", label: 'Alert Level: ${currentValue}%' 
        }

		controlTile("alertControl", "device.alertControl", "slider", height: 2, width: 2, inactiveLabel: false, range: "(0..100)") {
        	state "level", action: "setBatteryAlertLevel"
        }
        valueTile("monitoredDevices", "device.monitoredDevices", decoration: "flat", width: 6, height: 5) {
        	state "default", label: 'Monitored Devices:\n${currentValue}'
        }
        valueTile("alertedDevices", "device.alertedDevices", decoration: "flat", width: 6, height: 3) {
        	state "default", label: 'Low Battery Devices:\n${currentValue}'
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}

		main "state"
		details(["state", "alertLevel", "alertControl", "monitoredDevices", "alertedDevices", "refresh"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

private off() {
	log.debug "switch: off()"
    sendEvent(name: "switch", value: "off")
}

private ok() {
	log.debug "switch: ok()"
	sendEvent(name: "switch", value: "ok")
}

private alert() {
	log.debug "switch: alert()"
    sendEvent(name: "switch", value: "alert")
}

def setBatteryAlertLevel(val) {
	log.debug "setBatteryAlertLevel(${val})"
    if (val) {
    	sendEvent(name: "batteryAlertLevel", value: val)
        sendEvent(name: "alertLevel", value: val)
        sendEvent(name: "alertControl", value: val)
    }
    else {
    	sendEvent(name: "batteryAlertLevel", value: 10)
        sendEvent(name: "alertLevel", value: 10)
        sendEvent(name: "alertControl", value: 10)
    }
}

def setDeviceList(deviceList) {
	log.debug "setDeviceList(${deviceList})"
	if (deviceList) {
    	ok()
    	sendEvent(name: "monitoredDevices", value: deviceList)
        sendEvent(name: "deviceStatusList", value: deviceList)
    }
    else {
    	sendEvent(name: "monitoredDevices", value: "None")
        sendEvent(name: "deviceStatusList", value: "None")
        off()
    }
}

def setDeviceAlertList(deviceList) {
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	 def alertDeviceList = "${device.currentValue("deviceAlertList")}"
    
    if (deviceList) {
    	alert()
        if (deviceList != alertDeviceList) {
            sendEvent(name: "deviceAlertList", value: deviceList)
            sendEvent(name: "alertedDevices", value: "${date}\n${deviceList}")
        }
    }
    else if (device.currentValue("deviceAlertList") != "None") {
    	log.debug "No unsecure devices..."
        ok()
    	sendEvent(name: "deviceAlertList", value: "None")
        sendEvent(name: "alertedDevices", value: "${date}\nNone")
    }
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
	reset()
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

def reset() {
	log.debug "Resetting all values..."

	setDeviceList()
    setDeviceAlertList()
    setBatteryAlertLevel()
    off()
    refresh()
}

def refresh() {
	log.debug "switch: request refresh()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
}