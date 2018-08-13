/**
 *  Heating/Cooling Monitor
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
    name: "Heating/Cooling Monitor",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Will send a notification to alert when the thermostat turns on/off, and provide daily updates of number of cycles and total time.",
    category: "Green Living",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home1-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home1-icn@2x.png"
)

preferences() {
	section("Choose HVAC Monitor sensor...") {
    	input "hvacSensor", "device.myHVACSensor", required: false, title: "Optionally choose Jeff's HVAC Monitor sensor."
    }
	section("Choose outside temperature sensor...") {
    	input "outsideTemp", "capability.temperatureMeasurement", required: true
    }
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat", required: true
	}
    section("Reset Day count at what time?") {
    	input "resetTime", "time", required: true
    }
    section("Send periodic update?") {
    	input "updateInterval", "enum", options: ["Never", "Daily", "Weekly"], title: "How often do you want the weekly statistics?"
    	input "updateYearlyInterval", "enum", options: ["Never", "Daily", "Weekly", "Monthly"], title: "How often do you want the annual statistics?"
    	input "updateTime", "time", required: false, title: "What time do you want to send the selected update(s)?"
    }
    section("Send realtime HVAC start/stop messages?") {
    	input "sendStartStop", "bool", required: false,
        	title: "Send Realtime Updates?"
    }
    section("Provide Filter Change Reminders?") {
    	input "reminderType", "enum", options: ["Never", "Days", "Run Time"], title: "When do you want to be reminded?"
        input "reminderInterval", "number", required: false, default: 0, title: "If by Days, enter the number of days.  If by Run Time, enter the number of hours."
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification?"
    }
    section("Send a Text Message to this number (optional)") {
        input "phone", "phone", required: false
    }
    section("Open Window reminder?") {
    	input "windowReminder", "bool", title: "Do you want a reminder to open the window if the outside temps are favorable?"
    }
    section("Send reminder Push Notifications?") {
    	input "sendPushReminder", "bool", title: "Send Push Notifications for reminders (i.e., Open Windows)?"
    }
    section("Send reminder Text Messages?") {
    	input "phoneReminder", "phone", required: false, title: "Send Text Message for reminders (i.e., Open Windows)?"
    }
    section("Update Temp Value") {
    	input "lowTempUpdate", "bool", title: "Update the Low Temp?"
    	input "lowTempValue", "number", required: false, title: "Update Low Temp to what value?"
        input "highTempUpdate", "bool", title: "Update the High Temp?"
        input "highTempValue", "number", required: false, title: "Update High Temp to what value?"
        input "tempDay", "enum", options: ["None", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"], required: false, title: "Which day in the past?"
    }
}

def installed()
{
	log.debug "enter installed, state: $state"
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))

	state.currTemp = 0

	state.runningSince = cal.format("MM/dd/yyyy HH:mm")  //new Date().format("MM/dd/yyyy HH:mm")
    state.startTime = now()
	state.currMode = ""
    state.currOS = ""
	state.filterInterval = 0
    state.reminderSent = false
    state.filterChangedDate = ""

	state.currYearDisp = cal.get(Calendar.YEAR) as Integer
    state.currYearCoolCnt = 0
	state.currYearCoolMin = 0
    state.currYearHeatCnt = 0
    state.currYearHeatMin = 0
    state.currYearMaxOut = 0
    state.currYearMinOut = 99
    state.currYearMaxIn = 0
    state.currYearMinIn = 99

    state.prevYearDisp = state.currYearDisp - 1
    state.prevYearCoolCnt = 0
    state.prevYearCoolMin = 0
    state.prevYearHeatCnt = 0
    state.prevYearHeatMin = 0
    state.prevYearMaxOut = 0
    state.prevYearMinOut = 99
    state.prevYearMaxIn = 0
    state.prevYearMinIn = 99

	state.currMonth = cal.get(Calendar.MONTH) as Integer
	state.currMonthCoolCnt = 0
    state.currMonthCoolMin = 0
    state.currMonthHeatCnt = 0
	state.currMonthHeatMin = 0
    state.currMonthMaxOut = 0
    state.currMonthMinOut = 99
    state.currMonthMaxIn = 0
    state.currMonthMinIn = 99

	state.monthsCoolCnt = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsCoolMin = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsHeatCnt = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsHeatMin = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsMaxOut = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsMinOut = [99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99]
    state.monthsMaxIn = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsMinIn = [99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99]

	state.weekCoolCnt = [0, 0, 0, 0, 0, 0, 0]
    state.weekCoolMin = [0, 0, 0, 0, 0, 0, 0]
    state.weekHeatCnt = [0, 0, 0, 0, 0, 0, 0]
    state.weekHeatMin = [0, 0, 0, 0, 0, 0, 0]
    state.weekMaxOut = [0, 0, 0, 0, 0, 0, 0]
    state.weekMinOut = [99, 99, 99, 99, 99, 99, 99]
    state.weekMaxIn = [0, 0, 0, 0, 0, 0, 0]
    state.weekMinIn = [99, 99, 99, 99, 99, 99, 99]

	subscribeToEvents()
}

def updated()
{
	log.debug "enter updated, state: $state"
    unschedule()
	unsubscribe()
	subscribeToEvents()
}

private getAppName() { return "Heating/Cooling Monitor" }

def subscribeToEvents()
{
	hvacSensor.setFilterChangeSchedule(reminderType)
    if (reminderInterval) {
	    hvacSensor.setFilterChangeInterval(reminderInterval)
        hvacSensor.setFilterChangeCurrentValue(state.filterInterval)
    }

	log.debug "subscribeToEvents()"
	subscribe(thermostat, "temperature", temperatureHandler)
    subscribe(thermostat, "thermostatMode", thermostatModeHandler)
    subscribe(thermostat, "thermostatOperatingState", thermostatOperatingStateHandler)
    subscribe(outsideTemp, "temperature", temperatureHandler)
    subscribe(hvacSensor, "filterChanged", filterChangedHandler)
    subscribe(app, appHandler)	
    schedule(resetTime, resetHandler)
    if (updateInterval != "Never") {
    	schedule(updateTime, updateSchedule)
    }
    if (updateYearlyInterval != "Never") {
    	schedule(updateTime, updateYearlySchedule)
    }
//    runEvery1Minute(refreshHandler)

	state.windowReminder = false    
    state.windowReminderDateTime = ""
}

def appHandler(evt) {
	log.debug "Checking hvacSensor..."
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)

//	log.debug "${date}: thermostat.currentThermostat = ${thermostat.currentThermostat} / thermostat.currentValue("thermostatOperatingState") = ${thermostat.currentValue("thermostatOperatingState")}"
    
//	hvacSensor.resetDailyCycles()

	log.debug "lowTempUpdate = ${lowTempUpdate} / lowTempValue = ${lowTempValue} / highTempUpdate = ${highTempUpdate} / highTempValue = ${highTempValue} / tempDay = ${tempDay}"
    if ((lowTempUpdate || highTempUpdate) && tempDay != "None") {
    	log.debug "Calling tempUpdate()"
        tempUpdate()
    }
//	evaluate(evt)
//	log.debug "Reset time: ${resetTime}"

	sendWeeklyUpdate(evt)
    sendYearlyUpdate(evt)
}

def filterChangedHandler(evt) {
	log.debug "filterChangedHandler(${evt.value})"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    
    state.filterChangedDate = date
    state.filterInterval = 0
    state.reminderSent = false
    hvacSensor.setFilterChangeCurrentValue(state.filterInterval)
}

def refreshHandler(evt) {
	def curThermo = thermostat.currentThermostat
    def curOS = thermostat.currentValue("thermostatOperatingState")
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	log.debug "refreshHandler ${date}: thermostat.currentThermostat = ${curThermo} / currentOperatingState = ${curOS}"
    thermostat.refresh()
    if (curThermo != thermostat.currentThermostat) {
    	log.debug "${date}: Refresh changed thermostat from ${curThermo} to ${thermostat.currentThermostat}\ncurrent operatingState went from ${curOS} to ${thermostat.currentValue("thermostatOperatingState")}"
    }
}

private sendFilterReminder() {
	log.debug "sendFilterReminder"
    hvacSensor.changeFilterRequired(true)
    state.reminderSent = true    
}

def resetHandler() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)

	if (reminderType == "Days") {
    	state.filterInterval = state.filterInterval + 1
        hvacSensor.setFilterChangeCurrentValue(state.reminderInterval)
        if (state.reminderInterval >= reminderInterval && !state.reminderSent) {
        	sendFilterRemider()
        }
    }
    
    state.windowReminder = false
    state.windowReminderDateTime = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    if (dayOfMonth == 1) {
    	hvacSensor.resetMonthlyCycles()
    }
    else {
	    hvacSensor.resetDailyCycles()
    }

	if (localCalendar.get(Calendar.YEAR) != state.currYearDisp) {
		state.prevYearDisp = state.currYearDisp
        state.prevYearCoolCnt = state.currYearCoolCnt
        state.prevYearCoolMin = state.currYearCoolMinCnt
        state.prevYearHeatCnt = state.currYearHeatCnt
        state.prevYearHeatMin = state.currYearHeatMin
        state.prevYearMaxOut = state.currYearMaxOut
        state.prevYearMinOut = state.currYearMinOut
        state.prevYearMaxIn = state.currYearMaxIn
        state.prevYearMinIn = state.currYearMinIn
        
		state.currYearDisp = localCalendar.get(Calendar.YEAR)
        state.currYearCoolCnt = 0
        state.currYearCoolMin = 0
        state.currYearHeatCnt = 0
        state.currYearHeatMin = 0
        state.currYearMaxOut = -99
        state.currYearMinOut = 99
        state.currYearMaxIn = -99
        state.currYearMinIn = 99
    }
    if (localCalendar.get(Calendar.MONTH) != state.currMonth) {
        state.monthsCoolCnt[state.currMonth] = state.currMonthCoolCnt
        state.monthsCoolMin[state.currMonth] = state.currMonthCoolMin
        state.monthsHeatCnt[state.currMonth] = state.currMonthHeatCnt
        state.monthsHeatMin[state.currMonth] = state.currMonthHeatMin
        state.monthsMaxOut[state.currMonth] = state.currMonthMaxOut
        state.monthsMinOut[state.currMonth] = state.currMonthMinOut
        state.monthsMaxIn[state.currMonth] = state.currMonthMaxIn
        state.monthsMinIn[state.currMonth] = state.currMonthMinIn
        
    	state.currMonth = localCalendar.get(Calendar.MONTH)
        state.currMonthCoolCnt = 0
        state.currMonthCoolMin = 0
        state.currMonthHeatCnt = 0
        state.currMonthHeatMin = 0
        state.currMonthMaxOut = -99
        state.currMonthMinOut = 99
        state.currMonthMaxIn = -99
        state.currMonthMinIn = 99
    }

	state.weekCoolCnt[day - 1] = 0
    state.weekCoolMin[day - 1] = 0
    state.weekHeatCnt[day - 1] = 0
    state.weekHeatMin[day - 1] = 0
    state.weekMaxOut[day - 1] = -99
    state.weekMinOut[day - 1] = 99
    state.weekMaxIn[day - 1] = -99
    state.weekMinIn[day - 1] = 99
}

def updateSchedule(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    def sendUpdate = false
    def updateType = updateInterval

	switch (updateType) {
    	case "Daily":
	        	sendUpdate = true
        	break
        case "Weekly":
        	if (dayOfWeek == 1) {
        		sendUpdate = true
            }
        	break
        case "Monthly":
        	if (dayOfMonth == 1) {
	        	sendUpdate = true
            }
        	break
        default:
			log.debug "${location}: updateSchedule() - Unable to determine update interval."
        	break
    }
    if (sendUpdate) {
        sendWeeklyUpdate(updateType)
    }
    else {
    	log.debug "updateWeeklySchedule: Schedule does not qualify.  Skipping..."
    }
}

def updateYearlySchedule(evt) {
	log.debug "updateYearlySchedule"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    def sendUpdate = false
    def updateType = updateYearlyInterval

	log.debug "updateYearlySchedule: updateType = ${updateType} / dayOfWeek = ${dayOfWeek} / dayOfMonth = ${dayOfMonth}"
	switch (updateType) {
    	case "Daily":
	        	sendUpdate = true
        	break
        case "Weekly":
        	if (dayOfWeek == 1) {
        		sendUpdate = true
            }
        	break
        case "Monthly":
        	if (dayOfMonth == 1) {
	        	sendUpdate = true
            }
        	break
        default:
			log.debug "${location}: updateYearlySchedule() - Unable to determine update interval."
        	break
    }
    if (sendUpdate) {
        sendYearlyUpdate(updateType)
    }
    else {
    	log.debug "updateYearlySchedule: Schedule does not qualify.  Skipping..."
    }
}

def tempUpdate() {
	log.debug "tempUpdate"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    int dayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR)
    int currMonth = localCalendar.get(Calendar.MONTH)
    int dayVal = 0
    int daysBack = 0
    def badLowTemp = 0
    def badHighTemp = 0
    def ot = outsideTemp.currentTemperature
    def resetHighLow = false

    switch (tempDay) {
        case "Sun":
        dayVal = 1
        break
        case "Mon":
        dayVal = 2
        break
        case "Tue":
        dayVal = 3
        break
        case "Wed":
        dayVal = 4
        break
        case "Thu":
        dayVal = 5
        break
        case "Fri":
        dayVal = 6
        break
        case "Sat":
        dayVal = 7
        break
        default:
            break
    }
    daysBack = dayOfWeek - dayVal
    badLowTemp = state.weekMinOut[dayVal-1]
    badHighTemp = state.weekMaxOut[dayVal-1]

	if (lowTempUpdate) {
    	log.debug "Updating day low temp from ${state.weekMinOut[dayVal-1]} to ${lowTempValue}"
    	state.weekMinOut[dayVal-1] = lowTempValue
        resetHighLow = true
        if (state.currMonthMinOut == badLowTemp) {
        	log.debug "Updating month low from ${state.currMonthMinOut} to ${lowTempValue}"
        	state.currMonthMinOut = lowTempValue
        }
        if (state.currYearMinOut == badLowTemp) {
	    	log.debug "Updating year low temp from ${state.currYearMinOut} to ${lowTempValue}"
        	state.currYearMinOut = lowTempValue
        }
    }
    if (highTempUpdate) {
    	log.debug "Updating day high temp from ${state.weekMaxOut[dayVal-1]} to ${highTempValue}"
    	state.weekMaxOut[dayVal-1] = highTempValue
        resetHighLow = true
        if (state.currMonthMaxOut == badHighTemp) {
	    	log.debug "Updating month high temp from ${state.currMonthMaxOut} to ${highTempValue}"
        	state.currMonthMaxOut = highTempValue
        }
        if (state.currYearMaxOut == badHighTemp) {
	    	log.debug "Updating year high temp from ${state.currYearMaxOut} to ${highTempValue}"
        	state.currYearMaxOut = highTempValue
        }
    }
    
    if (resetHighLow) {
// Check Month Records
		for (int x = 0; x < 6; x++) {
            if (state.weekMinOut[x] < state.currMonthMinOut) {
            	log.debug "Adjust month low from ${state.currMonthMinOut} to ${state.weekMinOut[x]}"
                state.currMonthMinOut = state.weekMinOut[x]
            }
            if (state.weekMaxOut[x] > state.currMonthMaxOut) {
            	log.debug "Adjust month high from ${state.currMonthMaxOut} to ${state.weekMaxOut[x]}"
                state.currMonthMaxOut = state.weekMaxOut[x]
            }
        }
// Check Year Records
        if (state.currMonthMaxOut > state.currYearMaxOut) {
            log.debug "Adjust year high from ${state.currYearMaxOut} to ${state.currMonthMaxOut}"
            state.currYearMaxOut = state.currMonthMaxOut
        }
        if (state.currMonthMinOut < state.currYearMinOut) {
            log.debug "Adjust year low from ${state.currYearMinOut} to ${state.currMonthMinOut}"
            state.currYearMinOut = state.currMonthMinOut
        }
    }
}

private tempTest() {
	log.debug "tempTest"
    
    def daylight = getSunriseAndSunset()
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
	def tt = thermostat.currentTemperature
    def ot = outsideTemp.currentTemperature
    def hsp = thermostat.currentValue("heatingSetpoint")
    def csp = thermostat.currentValue("coolingSetpoint")
    def rainDisp = (outsideTemp.currentValue("rainLastHour")) ? outsideTemp.currentValue("rainLastHour") : "0.0"
    def rain = Float.parseFloat(rainDisp)
    def tm = thermostat.currentValue("thermostatMode")
    def windowMsg = "${location} ${date}: It's a beautiful ${ot}F outside!  Do you want to open the windows?"

	log.debug "temperatureHandler: windowReminder = ${windowReminder} / rain = ${rain} / state.windowReminder = ${state.windowReminder} / state.windowReminderDateTime = ${state.windowReminderDateTime}"

    if (windowReminder && state.windowReminder && !state.windowReminderDateTime) {
    	state.windowReminder = false
    }

    if (windowReminder) {
    	log.debug "rain = ${rain} / tm = ${tm} / ot = ${ot} / tt = ${tt} / csp = ${csp} / hsp = ${hsp}"
        if (tm == "heat") {
            if (ot > hsp && !state.windowReminder) {
                if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	state.windowReminder = true                    
                    state.windowReminderDateTime = date
                    if (phoneReminder) {
                    	sendSms(phone, windowMsg)
                    }
                    if (sendPushReminder) {
                    	sendPush(windowMsg)
                    }
                }
            }
        }
        else if (tm == "cool") {
        	log.debug "checking cool temps..."
        	if (ot < csp && !state.windowReminder) {
            	log.debug "pass temp and state.windowReminder..."
            	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	log.debug "pass time of day..."
                	state.windowReminder = true
                    state.windowReminderDateTime = date
                    if (phoneReminder) {
                    	log.debug "send phone reminder..."
                    	sendSms(phone, windowMsg)
                    }
                    if (sendPushReminder) {
                    	log.debug "send push reminder..."
                    	sendPush(windowMsg)
                    }
                }
            }
        }
    }    
}

def temperatureHandler(evt)
{
	log.debug "temperatureHandler(${evt.value})"
    def daylight = getSunriseAndSunset()
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
	def tt = thermostat.currentTemperature
    def ot = outsideTemp.currentTemperature
    def hsp = thermostat.currentValue("heatingSetpoint")
    def csp = thermostat.currentValue("coolingSetpoint")
    def rainDisp = (outsideTemp.currentValue("rainLastHour")) ? outsideTemp.currentValue("rainLastHour") : "0.0"
    def rain = Float.parseFloat(rainDisp)
    def tm = thermostat.currentValue("thermostatMode")
    def weather = (ot < 66) ? "chilly" : "beautiful"
    def windowMsg = "${location} ${date}: It's a ${weather} ${ot}F outside!  Do you want to open the windows?"

	log.debug "temperatureHandler: windowReminder = ${windowReminder} / rain = ${rain} / state.windowReminder = ${state.windowReminder} / state.windowReminderDateTime = ${state.windowReminderDateTime}"

    if (windowReminder && state.windowReminder && !state.windowReminderDateTime) {
    	state.windowReminder = false
    }

	if (tm == "heat") {
        if (state.windowReminder && ot < hsp){
            state.windowReminder = false
            state.windowReminderDateTime = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
        }
    }
    else if (tm == "cool") {
    	if (state.windowReminder && ot > csp) {
            state.windowReminder = false
            state.windowReminderDateTime = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
        }
    }

    if (windowReminder) {
    	log.debug "rain = ${rain} / tm = ${tm} / ot = ${ot} / tt = ${tt} / csp = ${csp} / hsp = ${hsp}"
        if (tm == "heat") {
            if (ot > hsp && !state.windowReminder) {
            	log.debug "Heat: passed temp test and no reminder has been issued today..."
                if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	log.debug "Daytime:  send reminder message..."
                	state.windowReminder = true 
                    state.windowReminderDateTime = date
                    if (phoneReminder) {
                    	sendSms(phone, windowMsg)
                    }
                    if (sendPushReminder) {
                    	sendPush(windowMsg)
                    }
                }
            }
        }
        else if (tm == "cool") {
        	if (ot > 64 && ot < csp && !state.windowReminder) {
            	log.debug "Cool: passed temp test and no reminder has been issued today..."
            	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	log.debug "Daytime:  send reminder message..."
                	state.windowReminder = true
                    state.windowReminderDateTime = date
                    if (phoneReminder) {
                    	sendSms(phone, windowMsg)
                    }
                    if (sendPushReminder) {
                    	sendPush(windowMsg)
                    }
                }
            }
        }
    }
    
	state.currTemp = thermostat.currentTemperature
    if (hvacSensor) {
        hvacSensor.setInsideTemp(tt)
        hvacSensor.setOutsideTemp(ot)
        log.debug "thermostat.name = ${thermostat.name} / displayName = ${thermostat?.displayName}"
        hvacSensor.setMyThermostatName(thermostat.displayName)
    }

// Check Day Records
	if (tt > state.weekMaxIn[day - 1]) {
    	state.weekMaxIn[day - 1] = tt
    }
    if (tt < state.weekMinIn[day - 1]) {
    	state.weekMinIn[day - 1] = tt
    }
    if (ot > state.weekMaxOut[day - 1]) {
    	state.weekMaxOut[day - 1] = ot
    }
    if (ot < state.weekMinOut[day - 1]) {
    	state.weekMinOut[day - 1] = ot
    }

// Check Month Records
    if (tt > state.currMonthMaxIn) {
    	state.currMonthMaxIn = tt
    }
    if (tt < state.currMonthMinIn) {
    	state.currMonthMinIn = tt
    }
    if (ot > state.currMonthMaxOut) {
    	state.currMonthMaxOut = ot
    }
    if (ot < state.currMonthMinOut) {
    	state.currMonthMinOut = ot
    }

// Check Year Records
    if (tt > state.currYearMaxIn) {
    	state.currYearMaxIn = tt
    }
    if (tt < state.currYearMinIn) {
    	state.currYearMinIn = tt
    }
    if (ot > state.currYearMaxOut) {
    	state.currYearMaxOut = ot
    }
    if (ot < state.currYearMinOut) {
    	state.currYearMinOut = ot
    }
    
	evaluate(evt)
}

def osCheckHandler()
{
    def os = thermostat.currentValue("thermostatOperatingState")
    if (state.currOS == "") {
        state.currOS = os
    }
    
//    sendSms(phone, "currOS = ${state.currOS}")
    if (os != state.currOS) {
//        sendSms (phone, "${location}: ${os} != ${state.currOS}")
        evaluate()
    }
}

def thermostatModeHandler(evt)
{
//	sendSms(phone, "thermostatModeHandler")
    evaluate(evt)
}

def thermostatOperatingStateHandler(evt)
{
	log.debug "thermostatOperatingStateHandler(${evt.value})"
    evaluate(evt)
}

private sendWeeklyUpdate(evt) {
	log.debug "sendWeeklyUpdate"
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()
    def updateType = ""
    def msg = ""

	if (evt == null) {
	    updateType = "Daily"
    }
    else {
    	updateType = evt.value
    }


	msg = "${getAppName()}\n\n${getDateTime()}\nWeekly View - Update Type: ${updateType}\n${location} Thermostat\nMonitoring Since: ${state.runningSince}\n\n" +
        "Mon In H/L = ${state.weekMaxIn[1]} / ${state.weekMinIn[1]}\n" +
        "Mon Out H/L = ${state.weekMaxOut[1]} / ${state.weekMinOut[1]}\n" +
		"Mon C = ${state.weekCoolCnt[1]} / ${getDispTime(state.weekCoolMin[1])}\n" +
    	"Mon H = ${state.weekHeatCnt[1]} / ${getDispTime(state.weekHeatMin[1])}\n\n" +
        "Tue In H/L = ${state.weekMaxIn[2]} / ${state.weekMinIn[2]}\n" +
        "Tue Out H/L = ${state.weekMaxOut[2]} / ${state.weekMinOut[2]}\n" +
        "Tue C = ${state.weekCoolCnt[2]} / ${getDispTime(state.weekCoolMin[2])}\n" +
        "Tue H = ${state.weekHeatCnt[2]} / ${getDispTime(state.weekHeatMin[2])}\n\n" +
        "Wed In H/L = ${state.weekMaxIn[3]} / ${state.weekMinIn[3]}\n" +
        "Wed Out H/L = ${state.weekMaxOut[3]} / ${state.weekMinOut[3]}\n" +
        "Wed C = ${state.weekCoolCnt[3]} / ${getDispTime(state.weekCoolMin[3])}\n" +
        "Wed H = ${state.weekHeatCnt[3]} / ${getDispTime(state.weekHeatMin[3])}\n\n" +
        "Thu In H/L = ${state.weekMaxIn[4]} / ${state.weekMinIn[4]}\n" +
        "Thu Out H/L = ${state.weekMaxOut[4]} / ${state.weekMinOut[4]}\n" +
    	"Thu C = ${state.weekCoolCnt[4]} / ${getDispTime(state.weekCoolMin[4])}\n" +
    	"Thu H = ${state.weekHeatCnt[4]} / ${getDispTime(state.weekHeatMin[4])}\n\n" +
        "Fri In H/L = ${state.weekMaxIn[5]} / ${state.weekMinIn[5]}\n" +
        "Fri Out H/L = ${state.weekMaxOut[5]} / ${state.weekMinOut[5]}\n" +
        "Fri C = ${state.weekCoolCnt[5]} / ${getDispTime(state.weekCoolMin[5])}\n" +
        "Fri H = ${state.weekHeatCnt[5]} / ${getDispTime(state.weekHeatMin[5])}\n\n" +
        "Sat In H/L = ${state.weekMaxIn[6]} / ${state.weekMinIn[6]}\n" +
        "Sat Out H/L = ${state.weekMaxOut[6]} / ${state.weekMinOut[6]}\n" +
        "Sat C = ${state.weekCoolCnt[6]} / ${getDispTime(state.weekCoolMin[6])}\n" +
        "Sat H = ${state.weekHeatCnt[6]} / ${getDispTime(state.weekHeatMin[6])}\n\n" +
        "Sun In H/L = ${state.weekMaxIn[0]} / ${state.weekMinIn[0]}\n" +
        "Sun Out H/L = ${state.weekMaxOut[0]} / ${state.weekMinOut[0]}\n" +
        "Sun C = ${state.weekCoolCnt[0]} / ${getDispTime(state.weekCoolMin[0])}\n" +
        "Sun H = ${state.weekHeatCnt[0]} / ${getDispTime(state.weekHeatMin[0])}\n"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
}

private sendYearlyUpdate(evt) {
	log.debug "sendYearlyUpdate"
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()
    def updateType = ""
    def msg = ""

	if (evt == null) {
	    updateType = "Daily"
    }
    else {
    	updateType = evt.value
    }

	msg = "${getAppName()}\n\n${getDateTime()}\nYearly View - Update Type: ${updateType}\n${location} Thermostat\n\n" +
    	"Curr Year (${state.currYearDisp}):\n" +
        "C = ${state.currYearCoolCnt} / ${getDispTime(state.currYearCoolMin)}\n" +
		"H = ${state.currYearHeatCnt} / ${getDispTime(state.currYearHeatMin)}\n" +
        "In H/L = ${state.currYearMaxIn} / ${state.currYearMinIn}\n" +
        "Out H/L = ${state.currYearMaxOut} / ${state.currYearMinOut}\n\n" +
		
        "Curr Month (${dispMonth}):\n" +
        "C = ${state.currMonthCoolCnt} / ${getDispTime(state.currMonthCoolMin)}\n" +
		"H = ${state.currMonthHeatCnt} / ${getDispTime(state.currMonthHeatMin)}\n" +
        "In H/L = ${state.currMonthMaxIn} / ${state.currMonthMinIn}\n" +
        "Out H/L = ${state.currMonthMaxOut} / ${state.currMonthMinOut}\n\n" +
    	
        "Prev Year (${state.prevYearDisp}):\n" +
        "C = ${state.prevYearCoolCnt} / ${getDispTime(state.prevYearCoolMin)}\n" +
    	"H = ${state.prevYearHeatCnt} / ${getDispTime(state.prevYearHeatMin)}\n" +
        "In H/L = ${state.prevYearMaxIn} / ${state.prevYearMinIn}\n" +
        "Out H/L = ${state.prevYearMaxOut} / ${state.prevYearMinOut}\n\n" +
        
        "${getMonth(0, true)} C = ${state.monthsCoolCnt[0]} / ${getDispTime(state.monthsCoolMin[0])}\n" +
        "${getMonth(0, true)} H = ${state.monthsHeatCnt[0]} / ${getDispTime(state.monthsHeatMin[0])}\n" +
        "${getMonth(0, true)} In H/L = ${state.monthsMaxIn[0]} / ${state.monthsMinIn[0]}\n" +
        "${getMonth(0, true)} Out H/L = ${state.monthsMaxOut[0]} / ${state.monthsMinOut[0]}\n\n" +

		"${getMonth(1, true)} C = ${state.monthsCoolCnt[1]} / ${getDispTime(state.monthsCoolMin[1])}\n" +
        "${getMonth(1, true)} H = ${state.monthsHeatCnt[1]} / ${getDispTime(state.monthsHeatMin[1])}\n" +
        "${getMonth(1, true)} In H/L = ${state.monthsMaxIn[1]} / ${state.monthsMinIn[1]}\n" +
        "${getMonth(1, true)} Out H/L = ${state.monthsMaxOut[1]} / ${state.monthsMinOut[1]}\n\n" +
        
        "${getMonth(2, true)} C = ${state.monthsCoolCnt[2]} / ${getDispTime(state.monthsCoolMin[2])}\n" +
        "${getMonth(2, true)} H = ${state.monthsHeatCnt[2]} / ${getDispTime(state.monthsHeatMin[2])}\n" +
        "${getMonth(2, true)} In H/L = ${state.monthsMaxIn[2]} / ${state.monthsMinIn[2]}\n" +
        "${getMonth(2, true)} Out H/L = ${state.monthsMaxOut[2]} / ${state.monthsMinOut[2]}\n\n" +
        
        "${getMonth(3, true)} C = ${state.monthsCoolCnt[3]} / ${getDispTime(state.monthsCoolMin[3])}\n" +
        "${getMonth(3, true)} H = ${state.monthsHeatCnt[3]} / ${getDispTime(state.monthsHeatMin[3])}\n" +
        "${getMonth(3, true)} In H/L = ${state.monthsMaxIn[3]} / ${state.monthsMinIn[3]}\n" +
        "${getMonth(3, true)} Out H/L = ${state.monthsMaxOut[3]} / ${state.monthsMinOut[3]}\n\n" +
        
        "${getMonth(4, true)} C = ${state.monthsCoolCnt[4]} / ${getDispTime(state.monthsCoolMin[4])}\n" +
        "${getMonth(4, true)} H = ${state.monthsHeatCnt[4]} / ${getDispTime(state.monthsHeatMin[4])}\n" +
        "${getMonth(4, true)} In H/L = ${state.monthsMaxIn[4]} / ${state.monthsMinIn[4]}\n" +
        "${getMonth(4, true)} Out H/L = ${state.monthsMaxOut[4]} / ${state.monthsMinOut[4]}\n\n" +
        
        "${getMonth(5, true)} C = ${state.monthsCoolCnt[5]} / ${getDispTime(state.monthsCoolMin[5])}\n" +
        "${getMonth(5, true)} H = ${state.monthsHeatCnt[5]} / ${getDispTime(state.monthsHeatMin[5])}\n" +
        "${getMonth(5, true)} In H/L = ${state.monthsMaxIn[5]} / ${state.monthsMinIn[5]}\n" +
        "${getMonth(5, true)} Out H/L = ${state.monthsMaxOut[5]} / ${state.monthsMinOut[5]}\n\n" +
        
        "${getMonth(6, true)} C = ${state.monthsCoolCnt[6]} / ${getDispTime(state.monthsCoolMin[6])}\n" +
        "${getMonth(6, true)} H = ${state.monthsHeatCnt[6]} / ${getDispTime(state.monthsHeatMin[6])}\n" +
        "${getMonth(6, true)} In H/L = ${state.monthsMaxIn[6]} / ${state.monthsMinIn[6]}\n" +
        "${getMonth(6, true)} Out H/L = ${state.monthsMaxOut[6]} / ${state.monthsMinOut[6]}\n\n" +
        
        "${getMonth(7, true)} C = ${state.monthsCoolCnt[7]} / ${getDispTime(state.monthsCoolMin[7])}\n" +
        "${getMonth(7, true)} H = ${state.monthsHeatCnt[7]} / ${getDispTime(state.monthsHeatMin[7])}\n" +
        "${getMonth(7, true)} In H/L = ${state.monthsMaxIn[7]} / ${state.monthsMinIn[7]}\n" +
        "${getMonth(7, true)} Out H/L = ${state.monthsMaxOut[7]} / ${state.monthsMinOut[7]}\n\n" +
        
        "${getMonth(8, true)} C = ${state.monthsCoolCnt[8]} / ${getDispTime(state.monthsCoolMin[8])}\n" +
        "${getMonth(8, true)} H = ${state.monthsHeatCnt[8]} / ${getDispTime(state.monthsHeatMin[8])}\n" +
        "${getMonth(8, true)} In H/L = ${state.monthsMaxIn[8]} / ${state.monthsMinIn[8]}\n" +
        "${getMonth(8, true)} Out H/L = ${state.monthsMaxOut[8]} / ${state.monthsMinOut[8]}\n\n" +
        
        "${getMonth(9, true)} C = ${state.monthsCoolCnt[9]} / ${getDispTime(state.monthsCoolMin[9])}\n" +
        "${getMonth(9, true)} H = ${state.monthsHeatCnt[9]} / ${getDispTime(state.monthsHeatMin[9])}\n" +
        "${getMonth(9, true)} In H/L = ${state.monthsMaxIn[9]} / ${state.monthsMinIn[9]}\n" +
        "${getMonth(9, true)} Out H/L = ${state.monthsMaxOut[9]} / ${state.monthsMinOut[9]}\n\n" +
        
        "${getMonth(10, true)} C = ${state.monthsCoolCnt[10]} / ${getDispTime(state.monthsCoolMin[10])}\n" +
        "${getMonth(10, true)} H = ${state.monthsHeatCnt[10]} / ${getDispTime(state.monthsHeatMin[10])}\n" +
        "${getMonth(10, true)} In H/L = ${state.monthsMaxIn[10]} / ${state.monthsMinIn[10]}\n" +
        "${getMonth(10, true)} Out H/L = ${state.monthsMaxOut[10]} / ${state.monthsMinOut[10]}\n\n" +
        
        "${getMonth(11, true)} C = ${state.monthsCoolCnt[11]} / ${getDispTime(state.monthsCoolMin[11])}\n" +
        "${getMonth(11, true)} H = ${state.monthsHeatCnt[11]} / ${getDispTime(state.monthsHeatMin[11])}\n" +
        "${getMonth(11, true)} In H/L = ${state.monthsMaxIn[11]} / ${state.monthsMinIn[11]}\n" +
        "${getMonth(11, true)} Out H/L = ${state.monthsMaxOut[11]} / ${state.monthsMinOut[11]}\n"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
}

private evaluate(evt)
{
	log.debug "evaluate(): evt.value = ${evt?.value}"
    def ot = outsideTemp.currentTemperature
    def tm = thermostat.currentThermostatMode
    def os = thermostat.currentValue("thermostatOperatingState")
    def ct = thermostat.currentTemperature
    def csp = thermostat.currentValue("coolingSetpoint")
    def hsp = thermostat.currentValue("heatingSetpoint")
    def fm = thermostat.currentValue("thermostatFanMode")
	def sp = 0
    def stopTime = now()
    def runMin = 0
    def timerCall = false
    def opState = false
    def push = false
    
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	if (hvacSensor) {
    	log.debug "Found hvacSensor..."
    	if (hvacSensor.currentValue("mode") != tm) {
        	log.debug "Calling hvacSensor.setMode(${tm})"
        	hvacSensor.setMode(tm)
        }
        if (hvacSensor.currentValue("operatingState") != os) {
        	log.debug "Calling hvacSensor.setOperatingState(${os})"
        	hvacSensor.setOperatingState(os)
        }
        if (tm == "cool") {
        	if (csp != hvacSensor.currentValue("setTemp")) {
	        	hvacSensor.setPointTemp(csp)
            }
        }
        else if (tm == "heat") {
        	if (hsp != hvacSensor.currentValue("setTemp")) {
	        	hvacSensor.setPointTemp(hsp)
            }
        }
    }

    if (evt == null) {
        timerCall = true
    }
    else {
        if (evt.name == "thermostatOperatingState") {
            opState = true
        }
        if (evt.value == "push") {
            push = true
        }
    }
    
	if (opState || timerCall) {
 //       if (evt.value != "cooling" && evt.value != "heating" && evt.value != "idle") {
//            sendSms(phone, "os = ${os}")
 //       }
        
        if (os == "cooling" || os == "heating") {
        	state.startTime = now()
            state.currMode = tm
            state.currOS = os
        }
        else if (os == "idle") {
        	if (stopTime > state.startTime) {
            	runMin = ((stopTime - state.startTime) / 1000 / 60) as Integer
            }
        }
        if (os == "cooling") {
			state.currYearCoolCnt = state.currYearCoolCnt + 1
            state.currMonthCoolCnt = state.currMonthCoolCnt + 1
			state.weekCoolCnt[day - 1] = state.weekCoolCnt[day - 1] + 1
        }
        
        if (os == "heating") {
        	state.currYeatHeatCnt = state.currYearHeatCnt + 1
            state.currMonthHeatCnt = state.currMonthHeatCnt + 1
            state.weekHeatCnt[day - 1] = state.weekHeatCnt[day - 1] + 1
        }
        
        if (os == "idle") {
        	if (state.currMode == "cool" && state.currYearCoolCnt > 0) {
            	state.currYearCoolMin = state.currYearCoolMin + runMin
                state.currMonthCoolMin = state.currMonthCoolMin + runMin
                state.weekCoolMin[day - 1] = state.weekCoolMin[day - 1] + runMin
            }
            else if (state.currMode == "heat" && state.currYearHeatCnt > 0) {
            	state.currYearHeatMin = state.currYearHeatMin + runMin
                state.currMonthHeatMin = state.currMonthHeatMin + runMin
                state.weekHeatMin[day - 1] = state.weekHeatMin[day - 1] + runMin
            }
            state.currMode = tm
            state.currOS = os
            if (reminderType == "Run Time") {
            	state.filterInterval = state.filterInterval + runMin
                hvacSensor.setFilterChangeCurrentValue(state.filterInterval/60)
                if (state.filterInterval / 60 >= reminderInterval) {
                	sendFilterReminder()
                }
            }
        }
    }

	def msg = "${getAppName()}\n\n${getDateTime()}\n${location} Thermostat\nMode: ${tm} \nOp State: ${os}\nFan Mode: ${fm}\n" +
    	"Cool Temp: ${csp}\nHeat Temp: ${hsp}\nInside Temp: ${ct}\nOutside Temp: ${ot}\n\n" + 
        "${getDay()} Total:\n" +
        "C = ${getDayCoolTotalCnt()} / ${getDispTime(getDayCoolTotalMin())}\n" +
        "H = ${getDayHeatTotalCnt()} / ${getDispTime(getDayHeatTotalMin())}\n"

	
	if (sendPush && (sendStartStop || push)) {
        sendPush(msg)
    }
    if (phone && (sendStartStop || push)) {
        sendSms(phone, msg)
    }
}

private getDispTime(min) {
	def days = 0
	def hour = 0
    def minutes = 0
    def stringTime = ""
    def dispDay = ""
    def dispHour = "0 Hr"
    def dispMin = "0 Min"
    def dispTime = "0 Hr 0 Min"

	minutes = min
    if (min > 59) {
    	stringTime = Double.toString(min / 60)
        hour = Double.valueOf(stringTime).intValue()
        minutes = min - (60 * hour)
    }
    dispMin = Integer.toString(minutes) + " Min"
    if (hour > 23) {
    	stringTime = Double.toString(hour / 24)
        days = Double.valueOf(stringTime).intValue()
        dispDay = Integer.toString(days) + " Days "
        hour = hour - (24 * days)
    }
    dispHour = Integer.toString(hour) + " Hr"
    dispTime = "${dispDay}${dispHour} ${dispMin}"

    return dispTime
}

private getDateTime() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
    return localCalendar.format("MM/dd/yyyy h:mm a")
}

private getMonth(mon, flagCurrent) {
    def retVal = ""
    
	if (flagCurrent && mon == state.currMonth) {
    	retVal = "*"
    }

	switch (mon) {
    	case 0:
        	retVal = retVal + "Jan"
            break
        case 1:
        	retVal = retVal + "Feb"
            break
        case 2:
        	retVal = retVal + "Mar"
            break
        case 3:
        	retVal = retVal + "Apr"
            break
        case 4:
        	retVal = retVal + "May"
            break
        case 5:
        	retVal = retVal + "Jun"
            break
        case 6:
        	retVal = retVal + "Jul"
            break
        case 7:
        	retVal = retVal + "Aug"
            break
        case 8:
        	retVal = retVal + "Sep"
            break
        case 9:
        	retVal = retVal + "Oct"
            break
        case 10:
        	retVal = retVal + "Nov"
            break
        case 11:
        	retVal = retVal + "Dec"
            break
        default:
        	retVal = "ERR"
    }
    return retVal
}

private getDay() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	switch (day) {
    	case 1:
        	return "Sun"
        case 2:
        	return "Mon"
        case 3:
        	return "Tue"
        case 4:
        	return "Wed"
        case 5:
        	return "Thu"
        case 6:
        	return "Fri"
        case 7:
        	return "Sat"
        default:
        	return "ERR"
    }
}

private getDayCoolTotalCnt() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	return state.weekCoolCnt[day - 1]
}

private getDayCoolTotalMin() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	return state.weekCoolMin[day - 1]
}

private getDayHeatTotalCnt() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	return state.weekHeatCnt[day - 1]
}

private getDayHeatTotalMin() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	return state.weekHeatMin[day - 1]
}

