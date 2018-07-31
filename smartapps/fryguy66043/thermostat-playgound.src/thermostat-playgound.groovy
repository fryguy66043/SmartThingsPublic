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
    name: "Thermostat Playgound",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Will send a notification to alert when the thermostat turns on/off, and provide daily updates of number of cycles and total time.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png"
)

preferences() {
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat", required: true
	}

	section("Choose temp sensor...") {
    	input "ts", "capability.temperatureMeasurement"
    }

    section("Reset Day count at what time?") {
    	input "resetTime", "time", required: false
    }
    section("Send daily update at what time?") {
    	input "updateTime", "time", required: false
    }
    section("Send realtime HVAC start/stop messages?") {
    	input "sendStartStop", "bool", required: false,
        	title: "Send Realtime Updates?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification?"
    }
    section("Send a text message to this number (optional)") {
        input "phone", "phone", required: false
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

	state.currYearDisp = cal.get(Calendar.YEAR) as Integer
    state.currYearCoolCnt = 0
	state.currYearCoolMin = 0
    state.currYearHeatCnt = 0
    state.currYearHeatMin = 0
    state.currMonthCoolMin = 0

    state.prevYearDisp = state.currYearDisp - 1
    state.prevYearCoolCnt = 0
    state.prevYearCoolMin = 0
    state.prevYearHeatCnt = 0
    state.prevYearHeatMin = 0

	state.currMonth = cal.get(Calendar.MONTH) as Integer
	state.currMonthCoolCnt = 0
    state.currMonthCoolMin = 0
    state.currMonthHeatCnt = 0
	state.currMonthHeatMin = 0

	state.monthsCoolCnt = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsCoolMin = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsHeatCnt = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsHeatMin = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

	state.weekCoolCnt = [0, 0, 0, 0, 0, 0, 0]
    state.weekCoolMin = [0, 0, 0, 0, 0, 0, 0]
    state.weekHeatCnt = [0, 0, 0, 0, 0, 0, 0]
    state.weekHeatMin = [0, 0, 0, 0, 0, 0, 0]

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
//    schedule(resetTime, resetHandler)
//    schedule(updateTime, updateHandler)
//    runEvery1Minute(osCheckHandler)
}

def resetHandler() {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	if (localCalendar.get(Calendar.YEAR) != state.currYearDisp) {
		state.prevYearDisp = state.currYearDisp
        state.prevYearCoolCnt = state.currYearCoolCnt
        state.prevYearCoolMin = state.currYearCoolMinCnt
        state.prevYearHeatCnt = state.currYearHeatCnt
        state.prevYearHeatMin = state.currYearHeatMin
        
		state.currYearDisp = localCalendar.get(Calendar.YEAR)
        state.currYearCoolCnt = 0
        state.currYearCoolMin = 0
        state.currYearHeatCnt = 0
        state.currYearHeatMin = 0
    }
    if (localCalendar.get(Calendar.MONTH) != state.currMonth) {
        state.monthsCoolCnt[state.currMonth] = state.currMonthCoolCnt
        state.monthsCoolMin[state.currMonth] = state.currMonthCoolMin
        state.monthsHeatCnt[state.currMonth] = state.currMonthHeatCnt
        state.monthsHeatMin[state.currMonth] = state.currMonthHeatMin
        
    	state.currMonth = localCalendar.get(Calendar.MONTH)
        state.currMonthCoolCnt = 0
        state.currMonthCoolMin = 0
        state.currMonthHeatCnt = 0
        state.currMonthHeatMin = 0
    }

	state.weekCoolCnt[day - 1] = 0
    state.weekCoolMin[day - 1] = 0
    state.weekHeatCnt[day - 1] = 0
    state.weekHeatMin[day - 1] = 0
}

def updateHandler() {
	sendUpdate()
}

def appHandler(evt) {
	sendSms("9136831550", "Signal Strength = ${ts.signalStrength}")
//	evaluate(evt)
//	sendUpdate(evt)
}

def temperatureHandler(evt)
{
	state.currTemp = thermostat.currentTemperature
//	evaluate(evt)
}

def osCheckHandler()
{
    def os = thermostat.currentValue("thermostatOperatingState")
    if (state.currOS == "") {
        state.currOS = os
    }
    
//    sendSms(phone, "currOS = ${state.currOS}")
    if (os != state.currOS) {
        sendSms (phone, "${os} != ${state.currOS}")
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
//	sendSms(phone, "thermostatOperatingStateHandler")
    evaluate(evt)
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


	def msg = "Heating/Cooling Monitor\n\n${getDateTime()}\nUpdate Type: ${updateType}\n${location} Thermostat\nMonitoring Since: ${state.runningSince}\n\n" +
    	"Mon C = ${state.weekCoolCnt[1]} / ${getDispTime(state.weekCoolMin[1])}\n" +
    	"Mon H = ${state.weekHeatCnt[1]} / ${getDispTime(state.weekHeatMin[1])}\n\n" +
        "Tue C = ${state.weekCoolCnt[2]} / ${getDispTime(state.weekCoolMin[2])}\n" +
        "Tue H = ${state.weekHeatCnt[2]} / ${getDispTime(state.weekHeatMin[2])}\n\n" +
        "Wed C = ${state.weekCoolCnt[3]} / ${getDispTime(state.weekCoolMin[3])}\n" +
        "Wed H = ${state.weekHeatCnt[3]} / ${getDispTime(state.weekHeatMin[3])}\n\n" +
    	"Thu C = ${state.weekCoolCnt[4]} / ${getDispTime(state.weekCoolMin[4])}\n" +
    	"Thu H = ${state.weekHeatCnt[4]} / ${getDispTime(state.weekHeatMin[4])}\n\n" +
        "Fri C = ${state.weekCoolCnt[5]} / ${getDispTime(state.weekCoolMin[5])}\n" +
        "Fri H = ${state.weekHeatCnt[5]} / ${getDispTime(state.weekHeatMin[5])}\n\n" +
        "Sat C = ${state.weekCoolCnt[6]} / ${getDispTime(state.weekCoolMin[6])}\n" +
        "Sat H = ${state.weekHeatCnt[6]} / ${getDispTime(state.weekHeatMin[6])}\n\n" +
        "Sun C = ${state.weekCoolCnt[0]} / ${getDispTime(state.weekCoolMin[0])}\n" +
        "Sun H = ${state.weekHeatCnt[0]} / ${getDispTime(state.weekHeatMin[0])}\n"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }

	msg = "Heating/Cooling Monitor\n\n${getDateTime()}\n${location} Thermostat\n\n" +
    	"Curr Year (${state.currYearDisp}):\n" +
        "C = ${state.currYearCoolCnt} / ${getDispTime(state.currYearCoolMin)}\n" +
		"H = ${state.currYearHeatCnt} / ${getDispTime(state.currYearHeatMin)}\n\n" +
		"Curr Month (${dispMonth}):\n" +
        "C = ${state.currMonthCoolCnt} / ${getDispTime(state.currMonthCoolMin)}\n" +
		"H = ${state.currMonthHeatCnt} / ${getDispTime(state.currMonthHeatMin)}\n\n" +
    	"Prev Year (${state.prevYearDisp}):\n" +
        "C = ${state.prevYearCoolCnt} / ${getDispTime(state.prevYearCoolMin)}\n" +
    	"H = ${state.prevYearHeatCnt} / ${getDispTime(state.prevYearHeatMin)}\n\n" +
        "${getMonth(0, true)} C = ${state.monthsCoolCnt[0]} / ${getDispTime(state.monthsCoolMin[0])}\n" +
        "${getMonth(0, true)} H = ${state.monthsHeatCnt[0]} / ${getDispTime(state.monthsHeatMin[0])}\n\n" +
        "${getMonth(1, true)} C = ${state.monthsCoolCnt[1]} / ${getDispTime(state.monthsCoolMin[1])}\n" +
        "${getMonth(1, true)} H = ${state.monthsHeatCnt[1]} / ${getDispTime(state.monthsHeatMin[1])}\n\n" +
        "${getMonth(2, true)} C = ${state.monthsCoolCnt[2]} / ${getDispTime(state.monthsCoolMin[2])}\n" +
        "${getMonth(2, true)} H = ${state.monthsHeatCnt[2]} / ${getDispTime(state.monthsHeatMin[2])}\n\n" +
        "${getMonth(3, true)} C = ${state.monthsCoolCnt[3]} / ${getDispTime(state.monthsCoolMin[3])}\n" +
        "${getMonth(3, true)} H = ${state.monthsHeatCnt[3]} / ${getDispTime(state.monthsHeatMin[3])}\n\n" +
        "${getMonth(4, true)} C = ${state.monthsCoolCnt[4]} / ${getDispTime(state.monthsCoolMin[4])}\n" +
        "${getMonth(4, true)} H = ${state.monthsHeatCnt[4]} / ${getDispTime(state.monthsHeatMin[4])}\n\n" +
        "${getMonth(5, true)} C = ${state.monthsCoolCnt[5]} / ${getDispTime(state.monthsCoolMin[5])}\n" +
        "${getMonth(5, true)} H = ${state.monthsHeatCnt[5]} / ${getDispTime(state.monthsHeatMin[5])}\n\n" +
        "${getMonth(6, true)} C = ${state.monthsCoolCnt[6]} / ${getDispTime(state.monthsCoolMin[6])}\n" +
        "${getMonth(6, true)} H = ${state.monthsHeatCnt[6]} / ${getDispTime(state.monthsHeatMin[6])}\n\n" +
        "${getMonth(7, true)} C = ${state.monthsCoolCnt[7]} / ${getDispTime(state.monthsCoolMin[7])}\n" +
        "${getMonth(7, true)} H = ${state.monthsHeatCnt[7]} / ${getDispTime(state.monthsHeatMin[7])}\n\n" +
        "${getMonth(8, true)} C = ${state.monthsCoolCnt[8]} / ${getDispTime(state.monthsCoolMin[8])}\n" +
        "${getMonth(8, true)} H = ${state.monthsHeatCnt[8]} / ${getDispTime(state.monthsHeatMin[8])}\n\n" +
        "${getMonth(9, true)} C = ${state.monthsCoolCnt[9]} / ${getDispTime(state.monthsCoolMin[9])}\n" +
        "${getMonth(9, true)} H = ${state.monthsHeatCnt[9]} / ${getDispTime(state.monthsHeatMin[9])}\n\n" +
        "${getMonth(10, true)} C = ${state.monthsCoolCnt[10]} / ${getDispTime(state.monthsCoolMin[10])}\n" +
        "${getMonth(10, true)} H = ${state.monthsHeatCnt[10]} / ${getDispTime(state.monthsHeatMin[10])}\n\n" +
        "${getMonth(11, true)} C = ${state.monthsCoolCnt[11]} / ${getDispTime(state.monthsCoolMin[11])}\n" +
        "${getMonth(11, true)} H = ${state.monthsHeatCnt[11]} / ${getDispTime(state.monthsHeatMin[11])}\n"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
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
    def timerCall = false
    def opState = false
    def push = false
    
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    
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
            sendSms(phone, "os = ${os}")
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
        }
    }

	def msg = "Heating/Cooling Monitor\n\n${getDateTime()}\n${location} Thermostat\nMode: ${tm} \nOp State: ${os}\nCool Temp: ${csp}\n" +
    	"Heat Temp: ${hsp}\nCurr. Temp: ${ct}\n\n" + 
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