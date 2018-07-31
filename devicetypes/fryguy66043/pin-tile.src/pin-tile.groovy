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
	definition (name: "PIN Tile", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "update", "string"
        attribute "pCodeString", "string"
        
        command "close"
        command "open"
		command "p0"
		command "p1"
		command "p2"
		command "p3"
		command "p4"
		command "p5"
		command "p6"
		command "p7"
		command "p8"
		command "p9"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("state", "device.contact", width: 2, height: 2) {
			state("closed", label:'SECURE', icon:"st.security.alarm.on", backgroundColor:"#00A0DC")
			state("open", label:'UNSECURE', icon:"st.security.alarm.off", backgroundColor:"#e86d13")
		}
        valueTile("pCode", "device.pCode", width: 4, height: 2) {
        	state "default", label: '${currentValue}'
        }
        standardTile("t0", "device.t0", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '0', action: "p0"
        }
        standardTile("t1", "device.t1", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '1', action: "p1"
        }
        standardTile("t2", "device.t2", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '2', action: "p2"
        }
        standardTile("t3", "device.t3", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '3', action: "p3"
        }
        standardTile("t4", "device.t4", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '4', action: "p4"
        }
        standardTile("t5", "device.t5", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '5', action: "p5"
        }
        standardTile("t6", "device.t6", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '6', action: "p6"
        }
        standardTile("t7", "device.t7", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '7', action: "p7"
        }
        standardTile("t8", "device.t8", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '8', action: "p8"
        }
        standardTile("t9", "device.t9", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '9', action: "p9"
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh", icon:"st.secondary.refresh"
		}
        
		main "state"
		details(["state", "pCode", "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "refresh"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

private p0() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}0"
	log.debug "p0: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p1() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}1"
	log.debug "p1: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p2() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}2"
	log.debug "p2: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p3() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}3"
	log.debug "p3: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p4() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}4"
	log.debug "p4: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p5() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}5"
	log.debug "p5: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p6() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}6"
	log.debug "p6: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p7() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}7"
	log.debug "p7: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p8() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}8"
	log.debug "p8: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

private p9() {
    def pc = (device.currentValue("pCodeString")) ? device.currentValue("pCodeString") : ""
    pc = "${pc}9"
	log.debug "p9: pc = ${pc}"
    sendEvent(name: "pCodeString", value: "${pc}")
	sendEvent(name: "pCode", value: "_${pc}_")
}

def open() {
	sendEvent(name: "contact", value: "open")
}

def close() {
    sendEvent(name: "contact", value: "closed")
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
    sendEvent(name: "pCode", value: "__")
    sendEvent(name: "pCodeString", value: "")
}