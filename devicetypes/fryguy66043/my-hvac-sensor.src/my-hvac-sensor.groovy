/**
 *  My HVAC Sensor
 *
 *  Will show when HVAC is running in either heat or cool mode.  If outside temps are <= inside temp in cool mode, it will recommend opening the windows.
 *  If outside temps are >= inside temp in heat mode, it will recommend opening the windows.
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
	definition (name: "My HVAC Sensor", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Actuator"
		capability "Switch"
        capability "Thermostat"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "runningSince", "string"
		attribute "update", "string"
        attribute "myThermostatName", "string"
        attribute "mode", "enum", ["off", "auto", "cool", "heat", "emergencyHeat"]
        attribute "operatingState", "enum", ["idle", "cooling", "fanOnly", "heating"]
        attribute "setTemp", "string"
        attribute "insideTemp", "string"
        attribute "outsideTemp", "string"
        attribute "heatCycles", "number"
        attribute "coolCycles", "number"
        attribute "heatCyclesToday", "number"
        attribute "coolCyclesToday", "number"
        attribute "coolCycleStart", "string"
        attribute "coolCycleStop", "string"
        attribute "heatCycleStart", "string"
        attribute "heatCycleStop", "string"
        
        command "refresh"
        command "setMyThermostatName"
        command "setMode"
        command "setOperatingState"
        command "setInsideTemp"
        command "setOutsideTemp"
        command "setPointTemp"
        command "resetDailyCycles"
        command "resetMonthlyCycles"
        command "reset"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("hvac", "device.hvac", width: 2, height: 2) {
			state("off", label:'OFF', backgroundColor:"#CCCCCC")
			state("heatIdle", label:'Idle', icon:"st.Home.home29", backgroundColor:"#d04e00")
			state("heatHeating", label:'Heating', icon:"st.Home.home29", backgroundColor:"#bc2323")
			state("emergHeatIdle", label:'Idle', icon:"st.Home.home29", backgroundColor:"#d04e00")
			state("emergHeatHeating", label:'Heating', icon:"st.Home.home29", backgroundColor:"#bc2323")
			state("coolIdle", label:'Idle', icon:"st.Weather.weather7", backgroundColor:"#1e9cbb")
			state("coolCooling", label:'Cooling', icon:"st.Weather.weather7", backgroundColor:"#153591")
            state("err", label: 'ERROR', icon:"st.Seasonal Fall.seasonal-fall-006", backgroundColor:"#f1d801")
		}
        valueTile("setPoint", "device.setPoint", width: 2, height: 2) {
        	state("default", label: 'Set Temp: ${currentValue}°')
        }
        valueTile("hvacMode", "device.hvacMode", width: 2, height: 2) {
        	state("default", label: 'Mode: ${currentValue}')
        }
        valueTile("hvacOS", "device.hvacOS", width: 2, height: 2) {
        	state("default", label: 'Op State: ${currentValue}')
        }
        valueTile("outside", "device.outside", width: 2, height: 2) {
        	state("default", label: 'Outside Temp: ${currentValue}°')
        }
        valueTile("inside", "device.inside", width: 2, height: 2) {
        	state("default", label: 'Inside Temp: ${currentValue}°')
        }
        valueTile("cool", "device.cool", width: 6, height: 2) {
        	state("default", label: 'Cool Cycles:\n${currentValue}')
        }
        valueTile("heat", "device.heat", width: 6, height: 2) {
        	state("default", label: 'Heat Cycles:\n${currentValue}')
        }
        valueTile("lastUpdated", "device.lastUpdated", width: 6, height: 2) {
        	state("default", label: '${currentValue}')
        }
        valueTile("since", "device.since", width: 6, height: 2) {
        	state("default", label: '${currentValue}')
        }

        
		main "hvac"
		details(["hvac", "setPoint", "inside", "outside", "hvacMode", "hvacOS", "cool", "heat", "lastUpdated", "since"])
	}
}

def installed() {
	log.trace "Executing 'installed'"
    sendEvent(name: "heatCycles", value: 0)
    sendEvent(name: "coolCycles", value: 0)
    sendEvent(name: "coolCyclesToday", value: 0)
    sendEvent(name: "heatCyclesToday", value: 0)
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
    
    sendEvent(name: "mode", value: "off")
    sendEvent(name: "operatingState", value: "idle")
        
    refresh()
}

def parse(String description) {
	log.trace "parse($description)"
}

def resetDailyCycles() {
	log.debug "resetting cycles for today..."
    def cycleCnt = 0
    def cycleTodayCnt = 0
    
    sendEvent(name: "coolCyclesToday", value: 0)
    sendEvent(name: "heatCyclesToday", value: 0)

    cycleCnt = (device.currentValue("coolCycles")) ? device.currentValue("coolCycles") : 0
    sendEvent(name: "cool", value: "Today: ${cycleTodayCnt} / Month: ${cycleCnt}\nLast Cycle Start: N/A")
    cycleCnt = (device.currentValue("heatCycles")) ? device.currentValue("heatCycles") : 0
    sendEvent(name: "heat", value: "Today: ${cycleTodayCnt} / Month: ${cycleCnt}\nLast Cycle Start: N/A")                    
    refresh()
}

def resetMonthlyCycles() {
	log.debug "resetting cycles for the month..."
    
    sendEvent(name: "coolCycles", value: 0)
    sendEvent(name: "heatCycles", value: 0)
    resetDailyCycles()
}

def setMyThermostatName(tName) {
	log.debug "setMyThermostatName(${tName})"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    def sinceDate = device.currentValue("runningSince")
    
	if (tName) {
//    	if (tName != device.currentValue("myThermostatName")) {
            sendEvent(name: "myThermostatName", value: tName)
//            sendEvent(name: "runningSince", value: date)
            sendEvent(name: "since", value: "${tName}\nRunning Since:\n${sinceDate}")
//            refresh("Changed Thermostat: ${tName}")
//        }
    }
}

def setMode(val) {
	def os = device.currentValue("operatingState")
	log.debug "setMode(${val}): Current operatingState = ${os} / mode = ${device.currentValue("mode")}"
	def valid = true
    if (val != device.currentValue("hvacMode")) {
        switch (val) {
            case "off":
                log.debug "Setting tile to off"
                sendEvent(name: "hvac", value: "off")
                break
            case "auto":
                break
            case "cool":
                switch (os) {
                    case "idle":
                        log.debug "Setting tile to coolIdle"
                        sendEvent(name: "hvac", value: "coolIdle")
                        break
                    case "cooling":
                        log.debug "Setting tile to coolCooling"
                        sendEvent(name: "hvac", value: "coolCooling")
                        break
                    default:
                        log.debug "Setting tile to err"
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "heat":
                switch (os) {
                    case "idle":
                        log.debug "Setting tile to heatIdle"
                        sendEvent(name: "hvac", value: "heatIdle")
                        break
                    case "heating":
                        log.debug "Setting tile to heatHeating"
                        sendEvent(name: "hvac", value: "heatHeating")
                        break
                    default:
                        log.debug "Setting tile to err"
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "emergencyHeat":
                switch (os) {
                    case "idle":
                        log.debug "Setting tile to emergHeatIdle"
                        sendEvent(name: "hvac", value: "emergHeatIdle")
                        break
                    case "heating":
                        log.debug "Setting tile to emergHeatHeating"
                        sendEvent(name: "hvac", value: "emergHeatHeating")
                        break
                    default:
                        log.debug "Setting tile to err"
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            default:
                valid = false
                break
        }
        if (valid) {
            log.debug "Setting mode to ${val}"
            sendEvent(name: "mode", value: val)
            sendEvent(name: "hvacMode", value: val)
        }
        else {
            log.debug "Invalid mode: ${val}"
        }
        refresh("Set Mode: ${val}")
    }
}

def setOperatingState(val) {
	def mode = device.currentValue("mode")
	log.debug "setOperatingState(${val}): current mode = ${mode} / operating state = ${device.currentValue("operatingState")}"
	def valid = true
    def heatCycleCnt  = 0
    def heatCycleTodayCnt = 0
    def coolCycleCnt  = 0
    def coolCycleTodayCnt = 0
    def cycleStart = ""
    def cycleStop = ""
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

	if (!mode) {
    	sendEvent(name: "mode", value: "off")
    }
    coolCycleCnt = (device.currentValue("coolCycles")) ? device.currentValue("coolCycles") : 0
    coolCycleTodayCnt = (device.currentValue("coolCyclesToday")) ? device.currentValue("coolCyclesToday") : 0
    log.debug "setOperatingState Start: coolCycleCnt = ${coolCycleCnt} / coolCycleTodayCnt = ${coolCycleTodayCnt}"
    log.debug "setOperatingState Device Start: coolCycles = ${device.currentValue("coolCycles")} / coolCyclesToday = ${device.currentValue("coolCyclesToday")}"
    if (coolCycleCnt == 0 || coolCycleTodayCnt == 0) {
    	log.debug "Resetting cool cycle display: coolCycles = ${coolCycleCnt} / coolCyclesToday = ${coolCycleTodayCnt}"
        sendEvent(name: "coolCycles", value: coolCycleCnt)
        sendEvent(name: "coolCyclesToday", value: coolCycleTodayCnt)
        sendEvent(name: "cool", value: "Today: ${coolCycleTodayCnt} / Month: ${coolCycleCnt}\nLast Cycle Start: N/A") //${date}")
    }
    heatCycleCnt = (device.currentValue("heatCycles")) ? device.currentValue("heatCycles") : 0
    heatCycleTodayCnt = (device.currentValue("heatCyclesToday")) ? device.currentValue("heatCyclesToday") : 0
    if (heatCycleCnt == 0 || heatCycleTodayCnt == 0) {
    	log.debug "Resetting heat cycle display: heatCycles = ${heatCycleCnt} / heatCyclesToday = ${heatCycleTodayCnt}"
        sendEvent(name: "heatCycles", value: heatCycleCnt)
        sendEvent(name: "heatCyclesToday", value: heatCycleTodayCnt)
        sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: N/A")                    
    }
    
    if (val != device.currentValue("hvacOS")) {
        switch (val) {
            case "idle":
                switch (mode) {
                    case "cool":
                        sendEvent(name: "hvac", value: "coolIdle")
                        if (!device.currentValue("coolCycleStop")) {
                            sendEvent(name: "coolCycleStop", value: date)
                            cycleStart = device.currentValue("coolCycleStart")
                            sendEvent(name: "cool", value: "Today: ${coolCycleTodayCnt} / Month: ${coolCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                        }
                        break
                    case "heat":
                        sendEvent(name: "hvac", value: "heatIdle")
                        if (!device.currentValue("heatCycleStop")) {
                            sendEvent(name: "heatCycleStop", value: date)
                            cycleStart = device.currentValue("heatCycleStart")
                            sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                        }
                        break
                    case "emergencyHeat":
                        sendEvent(name: "hvac", value: "heatIdle")
                        if (!device.currentValue("heatCycleStop")) {
                            sendEvent(name: "heatCycleStop", value: date)
                            cycleStart = device.currentValue("heatCycleStart")
                            sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                        }
                        break
                    case "off":
                        sendEvent(name: "hvac", value: "off")
                        break
                    default:
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "cooling":
                switch (mode) {
                    case "cool":
    					log.debug "setOperatingState Pre-Change: coolCycleCnt = ${coolCycleCnt} / coolCycleTodayCnt = ${coolCycleTodayCnt}"
                    	coolCycleCnt = coolCycleCnt + 1
                        coolCycleTodayCnt = coolCycleTodayCnt + 1
                        sendEvent(name: "hvac", value: "coolCooling")
                        sendEvent(name: "coolCycles", value: coolCycleCnt)
                        sendEvent(name: "coolCyclesToday", value: coolCycleTodayCnt)
                        sendEvent(name: "coolCycleStart", value: date)
                        sendEvent(name: "coolCycleStop", value: "")
                        sendEvent(name: "cool", value: "Today: ${coolCycleTodayCnt} / Month: ${coolCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")
    					log.debug "setOperatingState Post-Change: coolCycleCnt = ${coolCycleCnt} / coolCycleTodayCnt = ${coolCycleTodayCnt}"
                        break
                    case "off":
                        sendEvent(name: "hvac", value: "off")
                        break
                    default:
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "heating":
                switch (mode) {
                    case "heat":
                        sendEvent(name: "hvac", value: "heatHeating")
                        heatCycleCnt = heatCycleCnt + 1
                        heatCycleTodayCnt = heatCycleTodayCnt + 1
                        sendEvent(name: "heatCycles", value: heatCycleCnt)
                        sendEvent(name: "heatCyclesToday", value: heatCycleTodayCnt)
                        sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")                    
                        break
                    case "emergencyHeat":
                        sendEvent(name: "hvac", value: "heatHeating")
                        heatCycleCnt = heatCycleCnt + 1
                        heatCycleTodayCnt = heatCycleTodayCnt + 1
                        sendEvent(name: "heatCycles", value: heatCycleCnt)
                        sendEvent(name: "heatCyclesToday", value: heatCycleTodayCnt)
                        sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")                    
                        break
                    case "off":
                        sendEvent(name: "hvac", value: "off")
                        break
                    default:
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "fanOnly":
                break
            default:
                valid = false
                break
        }
        sendEvent(name: "hvacOS", value: val)
        if (valid) {
            sendEvent(name: "operatingState", value: val)
            log.debug "Set to...  Mode: ${device.currentValue("mode")} / Operating State: ${device.currentValue("operatingState")}"
        }
        else {
            log.debug "Invalid operatingState: ${val}"
        }
        refresh("Changed OS: ${val}")
    }
    log.debug "setOperatingState End: coolCycleCnt = ${coolCycleCnt} / coolCycleTodayCnt = ${coolCycleTodayCnt}"
    log.debug "setOperatingState Device End: coolCycles = ${device.currentValue("coolCycles")} / coolCyclesToday = ${device.currentValue("coolCyclesToday")}"
}

def setInsideTemp(val) {
	if (val) {
    	if (val != device.currentValue("insideTemp")) {
            sendEvent(name: "insideTemp", value: val)
            sendEvent(name: "inside", value: val)
            refresh("Inside Temp: ${val}")
        }
    }
}

def setOutsideTemp(val) {
	if (val) {
    	if (val != device.currentValue("outsideTemp")) {
            sendEvent(name: "outsideTemp", value: val)
            sendEvent(name: "outside", value: val)
            refresh("Outside Temp: ${val}")
        }
    }
}

def setPointTemp(val) {
	if (val) {
        sendEvent(name: "setTemp", value: val)
        sendEvent(name: "setPoint", value: val)
    }
}

def refresh(updText) {
	log.debug "refresh(${updText})"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    def disp = (updText) ? "Last Update:\n${updText}\n${timestamp}" : "Last Update:\n${timestamp}"
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "lastUpdated", value: disp)
}

def reset() {
	log.debug "resetting all data..."
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
//    if (!device.currentValue("since")) {
    	def tName = (device.currentValue("myThermostatName")) ? device.currentValue("myThermostatName") : "Thermostat"
	    sendEvent(name: "since", value: "${tName}\nRunning Since:\n${timestamp}")
//    }
    sendEvent(name: "mode", value: "off")
    sendEvent(name: "operatingState", value: "idle")
    sendEvent(name: "cool", value: 0)
    sendEvent(name: "heat", value: 0)
    sendEvent(name: "coolCycles", value: 0)
    sendEvent(name: "heatCycles", value: 0)
    sendEvent(name: "coolCyclesToday", value: 0)
    sendEvent(name: "heatCyclesToday", value: 0)
    sendEvent(name: "heatCycleStart", value: "")
    sendEvent(name: "heatCycleStop", value: "")
    sendEvent(name: "coolCycleStart", value: "")
    sendEvent(name: "coolCycleStop", value: "")
}
