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
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"

		attribute "runningSince", "string"
		attribute "update", "string"
        attribute "filterChanged", "string"
        attribute "filterChangeRequired", "enum", ["true", "false"]
        attribute "filterChangeSchedule", "enum", ["Never", "Days", "Run Time"]
        attribute "filterChangeInterval", "number"
        attribute "filterChangeCurrentValue", "string"
        attribute "myThermostatName", "string"
        attribute "mode", "enum", ["off", "auto", "cool", "heat", "emergencyHeat"]
        attribute "operatingState", "enum", ["idle", "cooling", "fanOnly", "heating"]
        attribute "setTemp", "string"
        attribute "insideTemp", "string"
        attribute "outsideTemp", "string"
        attribute "emergHeatCyclesTotal", "number"
        attribute "heatCyclesTotal", "number"
        attribute "coolCyclesTotal", "number"
        attribute "coolMinutesTotal", "number"
        attribute "emergHeatMinutesTotal", "number"
        attribute "heatMinutesTotal", "number"
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
        command "changeFilterRequired"
        command "filterChanged"
        command "setFilterChangeSchedule"
        command "setFilterChangeInterval"
        command "setFilterChangeCurrentValue"
        command "reset"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		standardTile("hvac", "device.hvac", width: 2, height: 2) {
			state("off", label:'Off', icon:"thermostat.heating-cooling-off", backgroundColor:"#CCCCCC")
            state("offFilter", label: 'Off', icon:"thermostat.heating-cooling-off", backgroundColor:"#f1d801")
            state("heatIdle", label:'Idle', icon:"st.Home.home29", backgroundColor:"#d04e00")
            state("heatIdleFilter", label:'Idle', icon:"st.Home.home29", backgroundColor:"#f1d801")
            state("heatHeating", label:'Heating', icon:"st.Home.home29", backgroundColor:"#bc2323")
            state("heatHeatingFilter", label:'Heating', icon:"st.Home.home29", backgroundColor:"#f1d801")
            state("emergHeatIdle", label:'Idle', icon:"st.Home.home29", backgroundColor:"#d04e00")
            state("emergHeatIdleFilter", label:'Idle', icon:"st.Home.home29", backgroundColor:"#f1d801")
            state("emergHeatHeating", label:'Heating', icon:"st.Home.home29", backgroundColor:"#bc2323")
            state("emergHeatHeatingFilter", label:'Heating', icon:"st.Home.home29", backgroundColor:"#f1d801")
            state("coolIdle", label:'Idle', icon:"st.Weather.weather7", backgroundColor:"#1e9cbb")
            state("coolIdleFilter", label:'Idle', icon:"st.Weather.weather7", backgroundColor:"#f1d801")
            state("coolCooling", label:'Cooling', icon:"st.Weather.weather7", backgroundColor:"#153591")
            state("coolCoolingFilter", label:'Cooling', icon:"st.Weather.weather7", backgroundColor:"#f1d801")
//			state("heatIdle", label:'', icon:"st.thermostat.heat", backgroundColor:"#d04e00")
//			state("heatHeating", label:'', icon:"st.thermostat.heating", backgroundColor:"#bc2323")
//			state("emergHeatIdle", label:'', icon:"st.thermostat.emergency-heat", backgroundColor:"#d04e00")
//			state("emergHeatHeating", label:'', icon:"st.thermostat.heating", backgroundColor:"#bc2323")
//			state("coolIdle", label:'', icon:"st.thermostat.cool", backgroundColor:"#1e9cbb")
//			state("coolCooling", label:'', icon:"st.thermostat.cooling", backgroundColor:"#153591")
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
        valueTile("filter", "device.filter", width: 6, height: 2) {
        	state("default", label: '${currentValue}')
        }
        valueTile("filterChange", "device.filterChange", width: 6, height: 2) {
        	state("default", label: '${currentValue}', action: "filterChanged")
        }
        valueTile("totals", "device.totals", width: 6, height: 2) {
        	state("default", label: 'Lifetime Totals:\n${currentValue}')
        }
        
		main "hvac"
		details(["hvac", "setPoint", "inside", "outside", "hvacMode", "hvacOS", "cool", "heat", "lastUpdated", "since", "filter", "filterChange", "totals"])
	}
}

def installed() {
	log.trace "Executing 'installed'"
    sendEvent(name: "heatCyclesTotal", value: 0)
    sendEvent(name: "coolCyclesTotal", value: 0)
    sendEvent(name: "heatMinutesTotal", value: 0)
    sendEvent(name: "coolMinutesTotal", value: 0)
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

def setFilterChangeSchedule(sched) {
	log.debug "setFilterChangeSchedule(${sched})"
    if (sched != "Never") {
    	sendEvent(name: "filterChangeSchedule", value: sched)
    }
    else {
    	sendEvent(name: "filterChangeSchedule", value: interval)
        sendEvent(name: "filter", value: "Filter Change Schedule Not Set")
    }
}

def setFilterChangeInterval(interval) {
	log.debug "setFilterChangeInterval(${interval})"
    if (interval) {
    	sendEvent(name: "filterChangeInterval", value: interval)
    }
}

def setFilterChangeCurrentValue(val) {
	log.debug "setFilterChangeCurrentValue(${val})"
    if (val >= 0 && device.currentValue("filterChangeSchedule") != "Never") {
    	def due = device.currentValue("filterChangeInterval")
        def interval = (device.currentValue("filterChangeSchedule") == "Days") ? "Days" : "Hours"
        Double remainder = due - val
        log.debug "Filter Change: due = ${due} / value = ${val} / interval = ${interval} / remainder = ${remainder}"
    	sendEvent(name: "filterChangeCurrentValue", value: val)
        sendEvent(name: "filter", value: "Filter Change Every ${due} ${interval}\nDue in ${remainder.round(1)} ${interval}")
    }
}

def changeFilterRequired(val) {
	log.debug "changeFilterRequired(${val})"
    def curHvac = device.currentValue("hvac")
    
    if (val == true) {
    	sendEvent(name: "filterChangeRequired", value: "true")
        switch (curHvac) {
            case "off":
                sendEvent(name: "hvac", value: "offFilter")
                break
            case "heatIdle":
                sendEvent(name: "hvac", value: "heatIdleFilter")
                break
            case "heatHeating":
                sendEvent(name: "hvac", value: "heatHeatingFilter")
                break
            case "emergHeatIdle":
                sendEvent(name: "hvac", value: "emergHeatIdleFilter")
                break
            case "emergHeatHeating":
                sendEvent(name: "hvac", value: "emergHeatHeatingFilter")
                break
            case "coolIdle":
                sendEvent(name: "hvac", value: "coolIdleFilter")
                break
            case "coolCooling":
                sendEvent(name: "hvac", value: "coolCoolingFilter")
                break
            default:
                break
        }
    }
    else {
    	sendEvent(name: "filterChangeRequired", value: "false")
    }
}


/*
	log.debug "activateAlarm"
    if (device.currentValue("alertState") != "userAlarm") {
        state.userAlertCnt = (state.userAlertCnt) ? state.userAlertCnt : 0
        state.userAlertCnt = state.userAlertCnt + 1
        log.debug "activateAlarm: state.userAlertCnt = ${state.userAlertCnt}"
        if (state.userAlertCnt > 2) {
            log.debug "activateAlarm: state.userAlertCnt > 2.  Calling setAlert()"
            sendEvent(name: "alertState", value: "userAlarm")
            setAlert()
        }
        runIn(3, resetUserAlertCnt)
    }
    else {
    	log.debug "User Alarm already activated..."
    }
*/

def filterChanged() {
	log.debug "filterChanged"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def curHvac = device.currentValue("hvac")
   	def filterChangedDate = device.currentValue("filterChanged")
 
    state.resetFilterCnt = state.resetFilterCnt ? state.resetFilterCnt + 1 : 1

	if (state.resetFilterCnt >= 3) {
        sendEvent(name: "filterChanged", value: date)
        sendEvent(name: "filterChange", value: "Filter Changed: ${date}")
        changeFilterRequired(false)
        switch (curHvac) {
            case "offFilter":
                sendEvent(name: "hvac", value: "off")
                break
            case "heatIdleFilter":
                sendEvent(name: "hvac", value: "heatIdle")
                break
            case "heatHeatingFilter":
                sendEvent(name: "hvac", value: "heatHeating")
                break
            case "emergHeatIdleFilter":
                sendEvent(name: "hvac", value: "emergHeatIdle")
                break
            case "emergHeatHeatingFilter":
                sendEvent(name: "hvac", value: "emergHeatHeating")
                break
            case "coolIdleFilter":
                sendEvent(name: "hvac", value: "coolIdle")
                break
            case "coolCoolingFilter":
                sendEvent(name: "hvac", value: "coolCooling")
                break
            default:
                break
        }
    }
    else {
        sendEvent(name: "filterChange", value: "<PRESS ${3-state.resetFilterCnt}x WHEN REPLACED>\nFilter Changed: ${filterChangedDate}")
    }
    runIn(5, resetFilterCount)
}

def resetFilterCount() {
	log.debug "resetFilterCount"
   	def filterChangedDate = device.currentValue("filterChanged")
    state.resetFilterCnt = 0
    sendEvent(name: "filterChange", value: "<PRESS ${3-state.resetFilterCnt}x WHEN REPLACED>\nFilter Changed: ${filterChangedDate}")
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
    def filter = device.currentValue("filterChangeRequired") ?: "false"
    if (val != device.currentValue("hvacMode")) {
        switch (val) {
            case "off":
                log.debug "Setting tile to off"
                if (filter == "true") {
	                sendEvent(name: "hvac", value: "offFilter")
                }
                else {
	                sendEvent(name: "hvac", value: "off")
                }
                break
            case "auto":
                break
            case "cool":
                switch (os) {
                    case "idle":
                        log.debug "Setting tile to coolIdle"
                        if (filter == "true") {
	                        sendEvent(name: "hvac", value: "coolIdleFilter")
                        }
                        else {
	                        sendEvent(name: "hvac", value: "coolIdle")
                        }
                        break
                    case "cooling":
                        log.debug "Setting tile to coolCooling"
                        if (filter == "true") {
	                        sendEvent(name: "hvac", value: "coolCoolingFilter")
                        }
                        else {
	                        sendEvent(name: "hvac", value: "coolCooling")
                        }
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
                        if (filter == "true") {
                        	sendEvent(name: "hvac", value: "heatIdleFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "heatIdle")
                        }
                        break
                    case "heating":
                        log.debug "Setting tile to heatHeating"
                        if (filter == "true") {
	                        sendEvent(name: "hvac", value: "heatHeatingFilter")
                        }
                        else {
	                        sendEvent(name: "hvac", value: "heatHeating")
                        }
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
                        if (filter == "true") {
                        	sendEvent(name: "hvac", value: "emergHeatIdleFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "emergHeatIdle")
                        }
                        break
                    case "heating":
                        log.debug "Setting tile to emergHeatHeating"
                        if (filter == "true") {
                        	sendEvent(name: "hvac", value: "emergHeatHeatingFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "emergHeatHeating")
                        }
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
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "coolIdleFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "coolIdle")
                        }
                        if (!device.currentValue("coolCycleStop")) {
                            sendEvent(name: "coolCycleStop", value: date)
                            cycleStart = device.currentValue("coolCycleStart")
                            sendEvent(name: "cool", value: "Today: ${coolCycleTodayCnt} / Month: ${coolCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                            
                            def startCoolDate = new Date().parse("MM/dd/yy hh:mm:ss a", cycleStart)
                            def endCoolDate = new Date().parse("MM/dd/yy hh:mm:ss a", date)
                            Double coolCycleMinutes = (endCoolDate.getTime() - startCoolDate.getTime()) / 1000 / 60
                            Double totalCoolCycleMinutes = device.currentValue("coolMinutesTotal") ? device.currentValue("coolMinutesTotal") + coolCycleMinutes : coolCycleMinutes
                            Double totalCoolCycleHours = totalCoolCycleMinutes > 0 ? totalCoolCycleMinutes / 60 : 0
                            Double totalCoolCycleDays = totalCoolCycleHours > 23 ? totalCoolCycleHours / 24 : 0
                            def totalCoolCycleCnt = device.currentValue("coolCyclesTotal") ? device.currentValue("coolCyclesTotal") + 1 : 1
                            sendEvent(name: "coolMinutesTotal", value: totalCoolCycleMinutes)
                            sendEvent(name: "coolCyclesTotal", value: totalCoolCycleCnt)
                            
                            Double totalHeatCycleMinutes = device.currentValue("heatMinutesTotal") ?: 0
                            Double totalHeatCycleHours = totalHeatCycleMinutes > 0 ? totalHeatCycleMinutes / 60 : 0
                            Double totalHeatCycleDays = totalHeatCycleHours > 23 ? totalHeatCycleHours / 24 : 0
                            def totalHeatCycleCnt = device.currentValue("heatCyclesTotal") ?: 0
                            
                            Double totalEmergHeatCycleMinutes = device.currentValue("emergHeatMinutesTotal") ?: 0
                            Double totalEmergHeatCycleHours = totalEmergHeatCycleMinutes > 0 ? totalEmergHeatCycleMinutes / 60 : 0
                            Double totalEmergHeatCycleDays = totalEmergHeatCycleHours > 23 ? totalEmergHeatCycleHours / 24 : 0
                            def totalEmergHeatCycleCnt = device.currentValue("emergHeatCyclesTotal") ?: 0
                            
                            def totalsDisp = "Cool: ${totalCoolCycleCnt} cycles / ${totalCoolCycleHours.round(2)} Hours\n" +
                            				 "Heat: ${totalHeatCycleCnt} cycles / ${totalHeatCycleHours.round(2)} Hours\n" +
                                             "Emerg Heat: ${totalEmergHeatCycleCnt} cycles / ${totalEmergHeatCycleHours.round(2)} Hours"
                            sendEvent(name: "totals", value: totalsDisp)
                        }
                        break
                    case "heat":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "heatIdleFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "heatIdle")
                        }
                        if (!device.currentValue("heatCycleStop")) {
                            sendEvent(name: "heatCycleStop", value: date)
                            cycleStart = device.currentValue("heatCycleStart")
                            sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                            
                            def startHeatDate = new Date().parse("MM/dd/yy hh:mm:ss a", cycleStart)
                            def endHeatDate = new Date().parse("MM/dd/yy hh:mm:ss a", date)
                            Double heatCycleMinutes = (endHeatDate.getTime() - startHeatDate.getTime()) / 1000 / 60
                            Double totalHeatCycleMinutes = device.currentValue("heatMinutesTotal") ? device.currentValue("heatMinutesTotal") + heatCycleMinutes : heatCycleMinutes
                            Double totalHeatCycleHours = totalHeatCycleMinutes > 0 ? totalHeatCycleMinutes / 60 : 0
                            Double totalHeatCycleDays = totalHeatCycleHours > 0 ? totalHeatCycleHours / 24 : 0
                            def totalHeatCycleCnt = device.currentValue("heatCyclesTotal") ? device.currentValue("heatCyclesTotal") + 1 : 1
                            sendEvent(name: "heatMinutesTotal", value: totalHeatCycleMinutes)
                            sendEvent(name: "heatCyclesTotal", value: totalHeatCycleCnt)
                            
                            Double totalCoolCycleMinutes = device.currentValue("coolMinutesTotal") ?: 0
                            Double totalCoolCycleHours = totalCoolCycleMinutes > 0 ? totalCoolCycleMinutes / 60 : 0
                            Double totalCoolCycleDays = totalCoolCycleHours > 23 ? totalCoolCycleHours / 24 : 0
                            def totalCoolCycleCnt = device.currentValue("coolCyclesTotal") ?: 0
                            
                            Double totalEmergHeatCycleMinutes = device.currentValue("emergHeatMinutesTotal") ?: 0
                            Double totalEmergHeatCycleHours = totalEmergHeatCycleMinutes > 0 ? totalEmergHeatCycleMinutes / 60 : 0
                            Double totalEmergHeatCycleDays = totalEmergHeatCycleHours > 23 ? totalEmergHeatCycleHours / 24 : 0
                            def totalEmergHeatCycleCnt = device.currentValue("emergHeatCyclesTotal") ?: 0
                            
                            def totalsDisp = "Cool: ${totalCoolCycleCnt} cycles / ${totalCoolCycleHours.round(2)} Hours\n" +
                            				 "Heat: ${totalHeatCycleCnt} cycles / ${totalHeatCycleHours.round(2)} Hours\n" +
                                             "Emerg Heat: ${totalEmergHeatCycleCnt} cycles / ${totalEmergHeatCycleHours.round(2)} Hours"
                            sendEvent(name: "totals", value: totalsDisp)
                        }
                        break
                    case "emergencyHeat":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "heatIdleFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "heatIdle")
                        }
                        if (!device.currentValue("heatCycleStop")) {
                            sendEvent(name: "heatCycleStop", value: date)
                            cycleStart = device.currentValue("heatCycleStart")
                            sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${cycleStart}\nLast Cycle Stop: ${date}")
                            
                            def startHeatDate = new Date().parse("MM/dd/yy hh:mm:ss a", cycleStart)
                            def endHeatDate = new Date().parse("MM/dd/yy hh:mm:ss a", date)
                            Double emergHeatCycleMinutes = (endHeatDate.getTime() - startHeatDate.getTime()) / 1000 / 60
                            Double totalEmergHeatCycleMinutes = device.currentValue("emergHeatMinutesTotal") ? device.currentValue("emergHeatMinutesTotal") + emergHeatCycleMinutes : emergHeatCycleMinutes
                            Double totalEmergHeatCycleHours = totalEmergHeatCycleMinutes > 0 ? totalEmergHeatCycleMinutes / 60 : 0
                            Double totalEmergHeatCycleDays = totalEmergHeatCycleHours > 0 ? totalEmergHeatCycleHours / 24 : 0
                            def totalEmergHeatCycleCnt = device.currentValue("emergHeatCyclesTotal") ? device.currentValue("emergHeatCyclesTotal") + 1 : 1
                            sendEvent(name: "emergHeatMinutesTotal", value: totalEmergHeatCycleMinutes)
                            sendEvent(name: "emergHeatCyclesTotal", value: totalEmergHeatCycleCnt)
                            
                            Double totalHeatCycleMinutes = device.currentValue("heatMinutesTotal") ?: 0
                            Double totalHeatCycleHours = totalHeatCycleMinutes > 0 ? totalHeatCycleMinutes / 60 : 0
                            Double totalHeatCycleDays = totalHeatCycleHours > 23 ? totalHeatCycleHours / 24 : 0
                            def totalHeatCycleCnt = device.currentValue("heatCyclesTotal") ?: 0
                            
                            Double totalCoolCycleMinutes = device.currentValue("coolMinutesTotal") ?: 0
                            Double totalCoolCycleHours = totalCoolCycleMinutes > 0 ? totalCoolCycleMinutes / 60 : 0
                            Double totalCoolCycleDays = totalCoolCycleHours > 23 ? totalCoolCycleHours / 24 : 0
                            def totalCoolCycleCnt = device.currentValue("coolCyclesTotal") ?: 0
                            
                            def totalsDisp = "Cool: ${totalCoolCycleCnt} cycles / ${totalCoolCycleHours.round(2)} Hours\n" +
                            				 "Heat: ${totalHeatCycleCnt} cycles / ${totalHeatCycleHours.round(2)} Hours\n" +
                                             "Emerg Heat: ${totalEmergHeatCycleCnt} cycles / ${totalEmergHeatCycleHours.round(2)} Hours"
                            sendEvent(name: "totals", value: totalsDisp)
                        }
                        break
                    case "off":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "offFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "off")
                        }
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
                        if (filter == "true") {
                        	sendEvent(name: "hvac", value: "coolCoolingFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "coolCooling")
                        }
                        sendEvent(name: "coolCycles", value: coolCycleCnt)
                        sendEvent(name: "coolCyclesToday", value: coolCycleTodayCnt)
                        sendEvent(name: "coolCycleStart", value: date)
                        sendEvent(name: "coolCycleStop", value: "")
                        sendEvent(name: "cool", value: "Today: ${coolCycleTodayCnt} / Month: ${coolCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")
    					log.debug "setOperatingState Post-Change: coolCycleCnt = ${coolCycleCnt} / coolCycleTodayCnt = ${coolCycleTodayCnt}"
                        break
                    case "off":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "offFilter")
                        }
                        else
                        {
                        	sendEvent(name: "hvac", value: "off")
                        }
                        break
                    default:
                        sendEvent(name: "hvac", value: "err")
                        break
                }
                break
            case "heating":
                switch (mode) {
                    case "heat":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "heatHeatingFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "heatHeating")
                        }
                        heatCycleCnt = heatCycleCnt + 1
                        heatCycleTodayCnt = heatCycleTodayCnt + 1
                        sendEvent(name: "heatCycles", value: heatCycleCnt)
                        sendEvent(name: "heatCyclesToday", value: heatCycleTodayCnt)
                        sendEvent(name: "heatCycleStart", value: date)
                        sendEvent(name: "heatCycleStop", value: "")
                        sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")                    
                        break
                    case "emergencyHeat":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "heatHeatingFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "heatHeating")
                        }
                        heatCycleCnt = heatCycleCnt + 1
                        heatCycleTodayCnt = heatCycleTodayCnt + 1
                        sendEvent(name: "heatCycles", value: heatCycleCnt)
                        sendEvent(name: "heatCyclesToday", value: heatCycleTodayCnt)
                        sendEvent(name: "heatCycleStart", value: date)
                        sendEvent(name: "heatCycleStop", value: "")
                        sendEvent(name: "heat", value: "Today: ${heatCycleTodayCnt} / Month: ${heatCycleCnt}\nLast Cycle Start: ${date}\nLast Cycle Stop: Pending")                    
                        break
                    case "off":
                    	if (filter == "true") {
                        	sendEvent(name: "hvac", value: "offFilter")
                        }
                        else {
                        	sendEvent(name: "hvac", value: "off")
                        }
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
