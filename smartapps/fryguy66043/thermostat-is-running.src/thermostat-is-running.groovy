/**
 *  Thermostat is Running
 *
 *  Copyright 2017 Jeffrey Fry
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
    name: "Thermostat is Running",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Will send a notification to alert when the thermostat turns on/off.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png"
)

preferences() {
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat"
	}
    section("Reset Day count at what time?") {
    	input "resetTime", "time"
    }
    section("Send daily update at what time?") {
    	input "updateTime", "time"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification?"
    }
    section("Send a text message to this number (optional)") {
        input "phone", "phone", required: false
    }
    section("Send only on-demand from App?") {
    	input "onDemandOnly", "bool"
    }
}

def installed()
{
	log.debug "enter installed, state: $state"
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
    state.runningSince = cal.format("MM/dd/yyyy HH:mm")  //new Date().format("MM/dd/yyyy HH:mm")
    state.startTime = now()
	state.yearMin = 0
    state.year = 0
    state.monthMin = 0
    state.month = 0
    state.currYearDisp = cal.get(Calendar.YEAR) as Integer
   	state.currMonth = cal.get(Calendar.MONTH) as Integer
    state.prevYearDisp = state.currYearDisp - 1
    state.prevYear = 0
    state.prevYearMin = 0
    state.months = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsMin = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
	state.mon = 0
    state.monMin = 0
    state.tue = 0
    state.tueMin = 0
    state.wed = 0
    state.wedMin = 0
    state.thu = 0
    state.thuMin = 0
    state.fri = 0
    state.friMin = 0
    state.sat = 0
    state.satMin = 0
    state.sun = 0
    state.sunMin = 0
	subscribeToEvents()
}

def updated()
{
	log.debug "enter updated, state: $state"
	unsubscribe()
	subscribeToEvents()
}

def subscribeToEvents()
{
	subscribe(thermostat, "temperature", temperatureHandler)
    subscribe(thermostat, "thermostatMode", thermostatModeHandler)
    subscribe(thermostat, "thermostatOperatingState", thermostatOperatingStateHandler)
    subscribe(app, appHandler)	
    schedule(resetTime, resetHandler)
    schedule(updateTime, updateHandler)
}

def resetHandler() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	if (localCalendar.get(Calendar.YEAR) != state.currYearDisp) {
		state.prevYearDisp = state.currYearDisp
        state.prevYear = state.year
        state.prevYearMin = state.yearMin
		state.currYearDisp = localCalendar.get(Calendar.YEAR)
        state.year = 0
        state.yearMin = 0
    }
    if (localCalendar.get(Calendar.MONTH) != state.currMonth) {
        state.months[state.currMonth] = state.month
        state.monthsMin[state.currMonth] = state.monthMin
    	state.currMonth = localCalendar.get(Calendar.MONTH)
        state.monthMin = 0        
    }
    switch (day) {
    	case 1:
        	state.sun = 0
            state.sunMin = 0
            break
        case 2:
        	state.mon = 0
            state.monMin = 0
            break
        case 3:
        	state.tue = 0
            state.tueMin = 0
            break
        case 4:
        	state.wed = 0
            state.wedMin = 0
            break
        case 5:
        	state.thu = 0
            state.thuMin = 0
            break
        case 6:
        	state.fri = 0
            state.friMin = 0
            break
        case 7:
        	state.sat = 0
            state.satMin = 0
            break
    }
}

def updateHandler() {
	sendUpdate()
}

def appHandler(evt) {
	evaluate(evt)
	sendUpdate(evt)
}

private sendUpdate(evt) {
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()
    def updateType = ""

	if (evt == null) {
	    updateType = "Daily"
    }
    else {
    	updateType = evt.value
    }


	def msg = "${getDateTime()}\nUpdate Type: ${updateType}\n${location} Thermostat (Week)\nMonitoring Since: ${state.runningSince}\n" +
    	"Mon = ${state.mon} / ${getDispTime(state.monMin)}\nTue = ${state.tue} / ${getDispTime(state.tueMin)}\nWed = ${state.wed} / ${getDispTime(state.wedMin)}\n" +
    	"Thu = ${state.thu} / ${getDispTime(state.thuMin)}\nFri = ${state.fri} / ${getDispTime(state.friMin)}\nSat = ${state.sat} / ${getDispTime(state.satMin)}\n" +
        "Sun = ${state.sun} / ${getDispTime(state.sunMin)}"

	if (sendPush && (!onDemandOnly || updateType == "push")) {
        sendPush(msg)
    }
    if (phone && (!onDemandOnly || updateType == "push")) {
        sendSms(phone, msg)
    }

	msg = "${getDateTime()}\n${location} Thermostat (Year)\nYear (${state.currYearDisp}) = ${state.year} / ${getDispTime(state.yearMin)}\n" +
    	"Month (${dispMonth}) = ${state.month} / ${getDispTime(state.monthMin)}\n" +
    	"Prev Year (${state.prevYearDisp}) = ${state.prevYear} / ${getDispTime(state.prevYearMin)}\n" +
        "${getMonth(0, true)} = ${state.months[0]} / ${getDispTime(state.monthsMin[0])}\n" +
        "${getMonth(1, true)} = ${state.months[1]} / ${getDispTime(state.monthsMin[1])}\n" +
        "${getMonth(2, true)} = ${state.months[2]} / ${getDispTime(state.monthsMin[2])}\n" +
        "${getMonth(3, true)} = ${state.months[3]} / ${getDispTime(state.monthsMin[3])}\n" +
        "${getMonth(4, true)} = ${state.months[4]} / ${getDispTime(state.monthsMin[4])}\n" +
        "${getMonth(5, true)} = ${state.months[5]} / ${getDispTime(state.monthsMin[5])}\n" +
        "${getMonth(6, true)} = ${state.months[6]} / ${getDispTime(state.monthsMin[6])}\n" +
        "${getMonth(7, true)} = ${state.months[7]} / ${getDispTime(state.monthsMin[7])}\n" +
        "${getMonth(8, true)} = ${state.months[8]} / ${getDispTime(state.monthsMin[8])}\n" +
        "${getMonth(9, true)} = ${state.months[9]} / ${getDispTime(state.monthsMin[9])}\n" +
        "${getMonth(10, true)} = ${state.months[10]} / ${getDispTime(state.monthsMin[10])}\n" +
        "${getMonth(11, true)} = ${state.months[11]} / ${getDispTime(state.monthsMin[11])}"

	if (sendPush && (!onDemandOnly || updateType == "push")) {
        sendPush(msg)
    }
    if (phone && (!onDemandOnly || updateType == "push")) {
        sendSms(phone, msg)
    }
}

private getDispTime(min) {
	def hour = 0
    def minutes = 0
    def stringTime = ""
    def dispHour = "0 Hr"
    def dispMin = "0 Min"
    def dispTime = "0 Hr 0 Min"

	minutes = min
    if (min > 59) {
    	stringTime = Double.toString(min / 60)
        hour = Double.valueOf(stringTime).intValue()
        dispHour = Integer.toString(hour) + " Hr"
        minutes = min - (60 * hour)
    }
    dispMin = Integer.toString(minutes) + " Min"
    dispTime = "${dispHour} ${dispMin}"

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
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
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

private getDayTotal() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	switch (day) {
    	case 1:
        	return state.sun
        case 2:
        	return state.mon
        case 3:
        	return state.tue
        case 4:
        	return state.wed
        case 5:
        	return state.thu
        case 6:
        	return state.fri
        case 7:
        	return state.sat
        default:
        	return -1
    }
}

private getDayTotalMin() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	switch (day) {
    	case 1:
        	return state.sunMin
        case 2:
        	return state.monMin
        case 3:
        	return state.tueMin
        case 4:
        	return state.wedMin
        case 5:
        	return state.thuMin
        case 6:
        	return state.friMin
        case 7:
        	return state.satMin
        default:
        	return -1
    }
}

def temperatureHandler(evt)
{
//	evaluate(evt)
}

def thermostatModeHandler(evt)
{
//	sendSms(phone, "thermostatModeHandler")
    evaluate(evt)
}

def thermostatOperatingStateHandler(evt)
{
//	sendSms(phone, "thermostatOperatingStateHandler")
    evaluate(evt)
}

private evaluate(evt)
{
    def tm = thermostat.currentThermostatMode
    def os = thermostat.currentValue("thermostatOperatingState")
    def ct = thermostat.currentTemperature
    def csp = thermostat.currentValue("coolingSetpoint")
    def hsp = thermostat.currentValue("heatingSetpoint")
	def sp = 0
    def stopTime = now()
    def runMin = 0
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    
	if (evt.name == "thermostatOperatingState") {
        if (evt.value == "cooling") {
        	state.startTime = now()
        }
        else if (evt.value == "idle") {
        	if (stopTime > state.startTime) {
            	runMin = ((stopTime - state.startTime) / 1000 / 60) as Integer
            }
        }
        if (evt.value == "cooling") {
			state.year = state.year + 1
            state.month = state.month + 1
			switch (day) {
                case 1:
                    state.sun = state.sun + 1
                    break
                case 2:
                    state.mon = state.mon + 1
                    break
                case 3:
                    state.tue = state.tue + 1
                    break
                case 4:
                    state.wed = state.wed + 1
                    break
                case 5:
                    state.thu = state.thu + 1
                    break
                case 6:
                    state.fri = state.fri + 1
                    break
                case 7:
                    state.sat = state.sat + 1
                    break
            }
        }
        if (evt.value == "idle" && state.year > 0) {
            state.yearMin = state.yearMin + runMin
            state.monthMin = state.monthMin + runMin
            switch (day) {
                case 1:
                    state.sunMin = state.sunMin + runMin
                    break
                case 2:
                    state.monMin = state.monMin + runMin
                    break
                case 3:
                    state.tueMin = state.tueMin + runMin
                    break
                case 4:
                    state.wedMin = state.wedMin + runMin
                    break
                case 5:
                    state.thuMin = state.thuMin + runMin
                    break
                case 6:
                    state.friMin = state.friMin + runMin
                    break
                case 7:
                    state.satMin = state.satMin + runMin
                    break
            }
        }
    }
    def msg = "${getDateTime()}\n${location} Thermostat \nMode: ${tm} \nOp State: ${os} \nCool Temp: ${csp} \nHeat Temp: ${hsp} \n" +
    	"Curr. Temp: ${ct}\n${getDay()} Total: ${getDayTotal()} / ${getDispTime(getDayTotalMin())}"

	
	if (sendPush && (!onDemandOnly || evt.value == "push")) {
        sendPush(msg)
    }
    if (phone && (!onDemandOnly || evt.value == "push")) {
        sendSms(phone, msg)
    }
}
