/**
 *  My Tracking Sensor
 *
 *  Designed to take open() and close() commands.  
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
	definition (name: "My Tracking Sensor", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"
        attribute "reportRequest", "string"
        attribute "monthReportRequest", "string"
        attribute "lastMonthReportRequest", "string"
        attribute "trackingSince", "string"
        attribute "name", "string"
        attribute "trackingList", "string"
        attribute "currentLocation", "string"
        attribute "currentLocationArrivalTime", "string"
        attribute "lastLocation", "string"
        attribute "lastLocationArrivalTime", "string"
        attribute "lastLocationDepartureTime", "string"

        command "off"
        command "on"
        command "setLocationArrival"
        command "setLocationDeparture"
        command "setTrackingList"
        command "setLastLocation"
        command "reset"
        command "month"
        command "lastMonth"
	}

	simulator {
		
	}

	tiles(scale: 2) {
    	standardTile("state", "device.switch", width: 2, height: 2) {
        	state("on", label: 'Present', icon: "st.Lighting.light13", backgroundColor:"#00A0DC")
            state("off", label: 'Not Present',icon: "st.Lighting.light11", backgroundColor:"#ffffff")
        }
        
        multiAttributeTile(name: "trackingTile", type: "generic", width: 6, height: 4) {
        	tileAttribute("device.locationDisp", key: "PRIMARY_CONTROL") {
            	attributeState "on", label: '${currentValue}', backgroundColor: "#00A0DC", defaultState: true
                attributeState "Away", label: '${currentValue}', backgroundColor: "#ffffff"
            }
            tileAttribute("device.lastLocationDisp", key: "SECONDARY_CONTROL") {
            	attributeState "lastLocationDisp", label: '${currentValue}'
            }
        }
		valueTile("location", "device.location", width: 6, height: 2) {
			state("default", label:'${currentValue}')
		}
        valueTile("since", "device.since", width: 6, height: 2) {
        	state("default", label: 'Tracking Since:\n${currentValue}')
        }
        valueTile("tracking", "device.tracking", width: 6, height: 6) {
        	state("default", label: '${currentValue}')
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", label: 'Week', action: "refresh", icon:"st.Office.office19"
		}
        standardTile("month", "device.month", decoration: "flat", width: 2, height: 2) {
        	state "default", label: 'Month', action: "month", icon:"st.Office.office19"
        }
        standardTile("lastMonth", "device.lastMonth", decoration: "flat", width: 2, height: 2) {
        	state "default", label: 'Last Month', action: "lastMonth", icon:"st.Office.office19"
        }
        
		main "trackingTile"
		details(["trackingTile", "location", "since", "tracking", "refresh", "month", "lastMonth"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def setTrackingList(list) {
	if (!device.currentValue("since")) {
    	def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    	sendEvent(name: "since", value: date)
        sendEvent(name: "trackingSince", value: date)
    }
	sendEvent(name: "trackinglist", value: list)
    sendEvent(name: "tracking", value: list)
}

def setLastLocation(loc, time) {
	log.debug "setLastLocation(${loc}, ${time})"
    def date = (time) ? time : new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	if (loc) {
        	sendEvent(name: "lastLocation", value: device.currentValue("currentLocation"))
	    	sendEvent(name: "lastLocationDisp", value: "${device.currentValue("currentLocation")}\n${date}")
	        sendEvent(name: "lastLocationArrivalTime", value: "${date}")
    }
}

def setLocationDeparture(loc, time) {
	log.debug "setLocationDeparture(${loc}, ${time})"
    def date = (time) ? time : new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	if (loc) {
    	sendEvent(name: "switch", value: "off")
    	if (loc == device.currentValue("currentLocation")) {
	        sendEvent(name: "lastLocationArrivalTime", value: device.currentValue("currentLocationArrivalTime"))
        }
        else {
        	sendEvent(name: "lastLocationArrivalTime", value: "???")
        }
        sendEvent(name: "lastLocation", value: loc)
    	sendEvent(name: "lastLocationDisp", value: "Departed: ${loc}\n${date}")
        sendEvent(name: "lastLocationDepartureTime", value: date)
        sendEvent(name: "currentLocation", value: "Away")
        sendEvent(name: "currentLocationArrivalTime", value: "")
        sendEvent(name: "location", value: "Away")
        sendEvent(name: "locationDisp", value: "Away")
    }    
}

def setLocationArrival(loc, time) {
	log.debug "setLocationArrival(${loc}, ${time})"
    def date = (time) ? time : new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	if (loc) {
    	sendEvent(name: "switch", value: "on")
    	if (device.currentValue("currentLocation") != "Away"){
        	sendEvent(name: "lastLocation", value: device.currentValue("currentLocation"))
	    	sendEvent(name: "lastLocationDisp", value: "${device.currentValue("currentLocation")}\n${date}")
	        sendEvent(name: "lastLocationArrivalTime", value: device.currentValue("currentLocationArrivalTime"))
        }
//        sendEvent(name: "lastLocationDepartureTime", value: date)
        sendEvent(name: "currentLocation", value: loc)
        sendEvent(name: "currentLocationArrivalTime", value: date)
        sendEvent(name: "location", value: "Arrived: ${loc}\n${date}")
        sendEvent(name: "locationDisp", value: loc)
    }
}

def on() {
	log.debug "switch: on()"
	sendEvent(name: "switch", value: "on")
}

def off() {
	log.debug "switch: off()"
    sendEvent(name: "switch", value: "off")
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
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    
    sendEvent(name: "trackingSince", value: date)
	sendEvent(name: "since", value: date)
    refresh()
}

def refresh() {
	log.debug "Request report()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "reportRequest", value: timestamp)
}

private month() {
	log.debug "Request month report()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "monthReportRequest", value: timestamp)
}

private lastMonth() {
	log.debug "Request last month report()"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "lastMonthReportRequest", value: timestamp)
}

def reset() {
	log.debug "Resetting all settings..."

	sendEvent(name: "name", value: "")
    sendEvent(name: "trackingList", value: "")
    sendEvent(name: "currentLocation", value: "")
    sendEvent(name: "currentLocationArrivalTime", value: "")
    sendEvent(name: "lastLocation", value: "")
    sendEvent(name: "lastLocationArrivalTime", value: "")
    sendEvent(name: "lastLocationDepartureTime", value: "")
    sendEvent(name: "locationDisp", value: "")
    sendEvent(name: "lastLocationDisp", value: "")
    sendEvent(name: "location", value: "")
    sendEvent(name: "tracking", value: "")
	off()    
    initialize()
}
