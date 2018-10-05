/**
 *  Copyright 2015 SmartThings
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
 *  Weather Tracker v4
 *
 *  Author: Jeffrey Fry
 */

definition(
    name: "Weather Tracker v4",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Daily forecast with precipitation tracking.",
	category: "Convenience",
	iconUrl: "http://cdn.device-icons.smartthings.com/Weather/weather10-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Weather/weather10-icn@2x.png"
)

preferences {
	section("Turn On Lights When It Gets Dark During The Day? (Optional)") {
    	input "rainLights", "capability.switch", multiple: true, required: false, title: "Turn on these lights when weather conditions make it dark, but only during the day."
        input "luxLevel", "enum", options: [250, 500, 750, 1000, 1250, 1500, 1750, 2000], required: false, title: "Turn on at selected illumination level?"
    }
	section("Get a Daily Morning Forecast?") {
    	input "dailyForecast", "bool", defaultValue: false, title: "Yes or No?"
        input "dailyForecastTime", "time", required: false, title: "What time?"
    }
    section("Get a Daily Evening Forecast?") {
    	input "dailyEveForecast", "bool", defaultValue: false, title: "Yes or No?"
        input "dailyEveForecastTime", "time", required: false, title: "What time?"
    }
	section("Choose Jeff's Custom Weather Device") {
    	input "myWxDevice", "device.mySmartweatherTile", required: false
    }
	section("Choose Outside Temperature Sensor...") {
    	input "outsideTemp", "capability.temperatureMeasurement", required: true, title: "Use Jeff's Custome Weather Device."
    }
    section("Choose UV Index Sensor...") {
    	input "uvSensor", "capability.ultravioletIndex", required: true, title: "Use Jeff's Custom Weather Device."
    }
    section("Choose Outside Illumination Sensor...") {
    	input "luxSensor", "capability.illuminanceMeasurement", required: true, title: "Use Jeff's Custome Weather Device."
    }
    section("Send updates when it's raining?") {
    	input "rainUpdates", "bool", title: "Do you want to receive updates when the weather sensor detects rain?"
    }
    section("Daily Precip Update Time (before midnight e.g.- 11:59 pm)") {
    	input "dailyPrecipUpdateTime", "time", required: true, title: "This is the time I will accumulate precipitation totals for the day."
    }
    section("Reset Values Time (after midnight - e.g. 12:00 am)") {
    	input "dailyResetTime", "time", required: true, title: "This is the time I will reset the totals for the day/week/month/year."
    }
	section("Send a Full Precipation Report Periodically") {
    	input "updateInterval", "enum", 
        	title: "Weekly Report Schedule (will send on the first day of selection). You need an On Demand Phone to receive these reports. It can be the same as the Primary or Secondary Phone.", 
        	options: ["Never", "Daily", "Weekly"], required: true
        input "yearlyUpdateInterval", "enum",
        	title: "Yearly Report Schedule (will send on the first day of selection). You need an On Demand Phone to receive these reports. It can be the same as the Primary or Secondary Phone.", 
        	options: ["Never", "Daily", "Weekly", "Monthly"], required: true
        input "updateTime", "time", title: "Send at What Time?"
    }
	section("Send Push Notification?") {
    	input "sendPushMsg", "bool", defaultValue: false, title: "Send a SmartThings Notification?"
    }
	section("Send a Text?") {
        input "phone", "phone", title: "Primary phone number?", required: false
	}
	section("Send a Second Text?") {
        input "phone2", "phone", title: "Secondary phone number? (Use if you want to send updates to two phones)", required: false
	}
    section("On Demand Text?") {
    	input "odPhone", "phone", title: "Send to this number when you press the 'Play' button and for Full Updates. It will also receive Forecasts.", required: false
    }
    section("Update Precip Total") {
    	input "precipUpdateValue", "decimal", required: false, title: "Update precip total in the event of a failure in capturing the data automatically."
        input "precipUpdateDay", "enum", options: ["None", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"], required: false, title: "Which day in the past?"
    }
    section("Update Temp Value") {
    	input "lowTempUpdate", "bool", title: "Update the Low Temp?"
    	input "lowTempValue", "number", required: false, title: "Update Low Temp to what value?"
        input "highTempUpdate", "bool", title: "Update the High Temp?"
        input "highTempValue", "number", required: false, title: "Update High Temp to what value?"
        input "tempDay", "enum", options: ["None", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"], required: false, title: "Which day in the past?"
    }
}


def installed() {
	log.debug "Installed: $settings"
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))

	state.raining = false
    state.rainUpdate = now()
    state.rainTotal = 0.0
    state.turnedLightsOn = false
    
	state.currTemp = 0
    state.currLux = 0
    state.stationID = "N/A"

	state.runningSince = cal.format("MM/dd/yyyy h:mm a")
    state.startTime = now()

	state.currYearDisp = cal.get(Calendar.YEAR) as Integer
    state.currYearPrecipInches = 0.0
    state.currYearHigh = 0
    state.currYearHighDate = state.runningSince
    state.currYearLow = 99
    state.currYearLowDate = state.runningSince

    state.prevYearDisp = state.currYearDisp - 1
    state.prevYearPrecipInches = 0.0
    state.prevYearHigh = 0
    state.prevYearHighDate = "N/A"
    state.prevYearLow = 99
    state.prevYearLowDate = "N/A"

	state.currMonth = cal.get(Calendar.MONTH) as Integer
    state.currMonthPrecipInches = 0.0
    state.currMonthHigh = 0
    state.currMonthHighDate = state.runningSince
    state.currMonthLow = 99
    state.currMonthLowDate = state.runningSince

	state.monthsPrecipInches = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    state.monthsHigh = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    state.monthsLow = [99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99]

	state.weekPrecipInches = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    state.weekHigh = [0, 0, 0, 0, 0, 0, 0, 0]
    state.weekLow = [99, 99, 99, 99, 99, 99, 99, 99]

    scheduleEvents()
}

def updated() {
	log.debug "Updated: $settings"
	unschedule()
    scheduleEvents()
}

def scheduleEvents() {
	log.debug "Scheduling Events"
	subscribe(app, appHandler)
    subscribe(myWxDevice, "forecast", forecastHandler)
    subscribe(myWxDevice, "rainToday", rainTodayHandler)
    subscribe(myWxDevice, "rainLastHour", rainLastHourHandler)
	subscribe(outsideTemp, "temperature", temperatureHandler)
    subscribe(luxSensor, "illuminance", luxHandler)
    subscribe(myWxDevice, "luxValue", luxHandler2)
	if (dailyForecast && dailyForecastTime) {
    	schedule(dailyForecastTime, mornForecastSchedule)
    }
    if (dailyEveForecast && dailyEveForecastTime) {
    	schedule(dailyEveForecastTime, eveForecastSchedule)
    }
    schedule(dailyPrecipUpdateTime, precipSchedule)
    schedule(dailyResetTime, resetSchedule)
    if (updateInterval != "Never") {
    	schedule(updateTime, updateSchedule)
    }
    if (updateYearlyInterval != "Never") {
    	schedule(updateTime, updateYearlySchedule)
    }
    state.rainUpdate = now()
    state.rainTotal = 0.0
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def nowTime = localCalendar.format("h:mm a")
	def msg = "${location}: ${nowTime} / Day = ${day}"

//	state.currYearPrecipInches = 21.61

	log.debug "Setting rain totals: Month = ${state.currMonthPrecipInches} / Year = ${state.currYearPrecipInches}"
	myWxDevice.setRainThisMonth(Float.parseFloat("${state.currMonthPrecipInches}").round(2))
	myWxDevice.setRainThisYear(Float.parseFloat("${state.currYearPrecipInches}").round(2))

	log.debug "precipUpdateValue = ${precipUpdateValue} / precipUpdateDay = ${precipUpdateDay}"
	if (precipUpdateValue >= 0 && precipUpdateDay != "None") {
    	log.debug "Calling precipUpdate()"
    	precipUpdate()
    }
	

	log.debug "lowTempUpdate = ${lowTempUpdate} / lowTempValue = ${lowTempValue} / highTempUpdate = ${highTempUpdate} / highTempValue = ${highTempValue} / tempDay = ${tempDay}"
    if ((lowTempUpdate || highTempUpdate) && tempDay != "None") {
    	log.debug "Calling tempUpdate()"
        tempUpdate()
    }

//	scheduleCheck()
//	resetSchedule()
//    sendWeeklyUpdate("On Demand")
//    sendYearlyUpdate("On Demand")
//  updateYearlySchedule()
}

def forecastHandler(evt) {
	log.debug "forecastHandler: ${evt.value}"
//    if (odPhone) {
//    	sendSms(odPhone, "${location}: Updated Forecast:\n${evt.value}")
//    }
}

def rainCheckHandler() {
	log.debug "rainCheckHandler"
    def rainTotal = Float.parseFloat(myWxDevice.currentValue("rainToday"))
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def msg = "${location} ${date}: Total Rain Today: ${rainTotal.round(2)}"
    if (state.rainUpdate + (59 * 60 * 1000) < now()) {
    	log.debug "It's been an hour since rain was reported..."
    	state.raining = false
        state.rainUpdate = now()
        if (rainTotal > 0.0) {
        	log.debug "Reporting today's rain total"
	        state.rainTotal = rainTotal
            if (rainUpdates) {
                if (sendPushMsg) {
                    sendPush(msg)
                }
                if (phone) {
                    sendSms(phone, msg)
                }
                if (phone2) {
                    sendSms(phone2, msg)
                }
            }
            if (odPhone) {
                sendSms(odPhone, msg)
            }
        }
    }
    else {
    	log.debug "Additional rain has been reported..."
    }
}

def rainTodayHandler(evt) {
	log.debug "rainTodayHandler: ${evt.value}"
    def rainToday = Float.parseFloat(myWxDevice.currentValue("rainToday"))
    def rainLastHour = Float.parseFloat(myWxDevice.currentValue("rainLastHour"))
    if (rainToday > 0.0 && rainLastHour == 0.0) {
		if (state.rainTotal < rainToday) {
            rainLastHourHandler(evt)
        }
    }
}

def rainLastHourHandler(evt) {
	log.debug "rainLastHourHandler(${evt.value}) / rainLastHour = ${myWxDevice.currentValue("rainLastHour")} / rainToday = ${myWxDevice.currentValue("rainToday")} / state.rainTotal = ${state.rainTotal}"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def rain = Float.parseFloat(myWxDevice.currentValue("rainLastHour")) ?: Float.parseFloat(evt.value)
    def rainTotal = Float.parseFloat(myWxDevice.currentValue("rainToday"))
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def msg = "${location} ${date}: "
    state.rainUpdate = state.rainUpdate ?: now()
    state.rainTotal = state.rainTotal ?: 0.0
    log.debug "${location}: rain > 0.0 = ${rain > 0.0} / rainTotal > state.rainTotal = ${rainTotal > state.rainTotal} / now() > state.rainUpdate + (60 * 60 * 1000) = ${now() > state.rainUpdate + (60 * 60 * 1000)} / rainTotal >= state.rainTotal + 0.1 = ${rainTotal >= state.rainTotal + 0.1}"
    
    if (rainTotal > 0.0) {
        state.weekPrecipInches[day - 1] = rainTotal
        float rainMonth = state.currMonthPrecipInches + rainTotal
        float rainYear = state.currYearPrecipInches + rainTotal
        myWxDevice.setRainThisMonth(rainMonth.round(2))
        myWxDevice.setRainThisYear(rainYear.round(2))
    }
    
    if (rain > 0.0 && rainTotal > state.rainTotal && (now() > state.rainUpdate + (60 * 60 * 1000) || rain >= state.rainTotal + 0.1))  {
        state.raining = true
        state.rainUpdate = now()
        state.rainTotal = rainTotal
//        msg = msg + "It's Raining!\nTotal Today: ${rainTotal.round(2)}\nRate per Hour: ${rain.round(2)}"
        msg = msg + "It's Raining! Total Today: ${rainTotal.round(2)}"
		if (rainUpdates) {
            if (sendPushMsg) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
            if (phone2) {
                sendSms(phone2, msg)
            }
        }
        if (odPhone) {
            sendSms(odPhone, msg)
        }
    }
    else {
/*    
    	if (rain == 0.0 && state.raining && state.rainTotal == rainTotal) { 
        	state.raining = false            
	        state.rainUpdate = now()
            msg = msg + "Total Rain Today: ${rainTotal.round(2)}"
            
            if (rainUpdates) {
                if (sendPushMsg) {
                    sendPush(msg)
                }
                if (phone) {
                    sendSms(phone, msg)
                }
                if (phone2) {
                    sendSms(phone2, msg)
                }
            }
            
            if (odPhone) {
                sendSms(odPhone, msg)
            }
        }
*/        
    }
//    state.rainTotal = rainTotal
    log.debug "Setting timer to check for rain in 1 hour..."
    runIn(60 * 60, rainCheckHandler)

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
    badLowTemp = state.weekLow[dayVal-1]
    badHighTemp = state.weekHigh[dayVal-1]

	if (lowTempUpdate) {
    	log.debug "Updating day low temp from ${state.weekLow[dayVal-1]} to ${lowTempValue}"
    	state.weekLow[dayVal-1] = lowTempValue
    	if (state.weekLow[7] == badLowTemp) {
	    	log.debug "Updating week low temp from ${state.weekLow[7]} to ${lowTempValue}"
        	state.weekLow[7] = lowTempValue
        	resetHighLow = true
        }
        if (state.currMonthLow == badLowTemp) {
        	log.debug "Updating month low from ${state.currMonthLow} to ${lowTempValue}"
        	state.currMonthLow = lowTempValue
        }
        if (state.currYearLow == badLowTemp) {
	    	log.debug "Updating year low temp from ${state.currYearLow} to ${lowTempValue}"
        	state.currYearLow = lowTempValue
        }
    }
    if (highTempUpdate) {
    	log.debug "Updating day high temp from ${state.weekHigh[dayVal-1]} to ${highTempValue}"
    	state.weekHigh[dayVal-1] = highTempValue
        if (state.weekHigh[7] == badHighTemp) {
	    	log.debug "Updating week high temp from ${state.weekHigh[7]} to ${highTempValue}"
        	state.weekHigh[7] = highTempValue
        	resetHighLow = true
        }
        if (state.currMonthHigh == badHighTemp) {
	    	log.debug "Updating month high temp from ${state.currMonthHigh} to ${highTempValue}"
        	state.currMonthHigh = highTempValue
        }
        if (state.currYearHigh == badHighTemp) {
	    	log.debug "Updating year high temp from ${state.currYearHigh} to ${highTempValue}"
        	state.currYearHigh = highTempValue
        }
    }
    
    if (resetHighLow) {
// Check Week Records
		for (int x = 0; x < 7; x++) {
            if (state.weekLow[x] < state.weekLow[7]) {
            	log.debug "Adjust week low from ${state.weekLow[7]} to ${state.weekLow[x]}"
                state.weekLow[7] = state.weekLow[x]
            }
            if (state.weekHigh[x] > state.weekHigh[7]) {
            	log.debug "Adjust week high from ${state.weekHigh[7]} to ${state.weekHigh[x]}"
                state.weekHigh[7] = state.weekHigh[x]
            }
        }
// Check Month Records
        if (state.weekHigh[7] > state.currMonthHigh) {
            log.debug "Adjust month high from ${state.currMonthHigh} to ${state.weekHigh[7]}"
            state.currMonthHigh = state.weekHigh[7]
        }
        if (state.weekLow[7] < state.currMonthLow) {
            log.debug "Adjust month low from ${state.currMonthLow} to ${state.weekLow[7]}"
            state.currMonthLow = state.weekLow[7]
        }


// Check Year Records
        if (state.currMonthHigh > state.currYearHigh) {
            log.debug "Adjust year high from ${state.currYearHigh} to ${state.currMonthHigh}"
            state.currYearHigh = state.currMonthHigh
        }
        if (state.currMonthLow < state.currYearLow) {
            log.debug "Adjust year low from ${state.currYearLow} to ${state.currMonthLow}"
            state.currYearLow = state.currMonthLow
        }
    }
}

def temperatureHandler(evt) {
	log.debug "temperatureHandler: ${evt.value}"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def ot = outsideTemp.currentTemperature
    
	state.currTemp = ot

	def wxLow = (myWxDevice.currentValue("actualLow")) ? myWxDevice.currentValue("actualLow") : 99
    def wxHigh = (myWxDevice.currentValue("actualHigh")) ? myWxDevice.currentValue("actualHigh") : -99
	if (ot > wxHigh) {
    	log.debug "Setting actual high"
    	myWxDevice.setActualHigh(ot)
    }
    if (ot < wxLow) {
    	log.debug "Setting actual low"
    	myWxDevice.setActualLow(ot)
    }
    
// Check Day Records
    if (ot > state.weekHigh[day - 1]) {
    	state.weekHigh[day - 1] = ot
    }
    if (ot < state.weekLow[day - 1]) {
    	state.weekLow[day - 1] = ot
    }
    
// Check Week Records
	if (ot < state.weekLow[7]) {
    	state.weekLow[7] = ot
    }
    if (ot > state.weekHigh[7]) {
    	state.weekHigh[7] = ot
    }

// Check Month Records
    if (ot > state.currMonthHigh) {
    	state.currMonthHigh = ot
        state.currMonthHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
    if (ot < state.currMonthLow) {
    	state.currMonthLow = ot
        state.currMonthLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }

// Check Year Records
    if (ot > state.currYearHigh) {
    	state.currYearHigh = ot
        state.currYearHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
    if (ot < state.currYearLow) {
    	state.currYearLow = ot
        state.currYearLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
}

def luxHandler2(evt) {
	log.debug "luxHandler2: ${evt.value}"
}

def luxHandler(evt) {
	log.debug "luxHandler: ${evt.value}"
    def daylight = getSunriseAndSunset()
//	def luxValue = Integer.parseInt(evt.value)
	def luxValue = luxSensor.currentValue("illuminance")
    def today = new Date().format("h:mm a", location.timeZone)

//	if (odPhone) {
//    	sendSms(odPhone, "luxHandler: luxValue < luxLevel = ${luxValue < Integer.parseInt(luxLevel)}")
//    }

//    if (odPhone) {
//    	sendSms(odPhone, "${location}: ${today} Lux = ${evt.value} / UV = ${uvSensor.currentValue("ultravioletIndex")}")
//    }
    if (rainLights) {
        def lightsOn = rainLights.find{it.currentSwitch == "on"}
        def lightsOff = rainLights.find{it.currentSwitch == "off"}
        if (lightsOff) {
            if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
            	if (luxValue <= Integer.parseInt(luxLevel)) {
                    rainLights?.on()
                    state.turnedLightsOn = true
                    if (sendPushMsg) {
//                        sendPush("${location}: Turned on ${rainLights} because it got dark outside (Lux Level: ${luxValue})")
                    }
                    if (odPhone) {
                        sendSms(odPhone, "${location}: Turned on ${rainLights} because it got dark outside (Lux Level: ${luxValue})")
                    }
                }
            }
        }
		if (state.turnedLightsOn) {
        	if (lightsOn) {
            	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	if (luxValue > luxLevel) {	
                        rainLights?.off()
                        state.turnedLightsOn = false
                        if (sendPushMsg) {
//                            sendPush("${location}: Turned off ${rainLights} because it is no longer dark outside (Lux Level: ${luxValue})")
                        }
                        if (odPhone) {
                            sendSms(odPhone, "${location}: Turned off ${rainLights} because it is no longer dark outside (Lux Level: ${luxValue})")
                        }
                    }
                }
                else {
                	state.turnedLightsOn = false
                }
            }
            else {
            	state.turnedLightsOn = false
            }
        }
    }
}

def precipUpdate() {
	log.debug "precipUpdate: precip = ${precipUpdateValue} / dayVal = ${precipUpdateDay}"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    int dayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR)
    int currMonth = localCalendar.get(Calendar.MONTH)
    int dayVal = 0
    int daysBack = 0
    log.debug "dayOfWeek = ${dayOfWeek} / dayOfMonth = ${dayOfMonth} / dayOfYear = ${dayOfYear} / month = ${currMonth}"
	def rain = precipUpdateValue

    if (precipUpdateValue > 0.0 && precipUpdateDay != "None") {
    	log.debug "Updating precip totals..."
        switch (precipUpdateDay) {
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

// Accumulate rain totals
// Set Day Total
		log.debug "Day: Changing ${precipUpdateDay} from ${state.weekPrecipInches[dayVal-1]} to ${rain}"
        state.weekPrecipInches[dayVal - 1] = rain

// Set Week Total
    //    	state.weekPrecipInches[7] = (state.weekPrecipInches[7] + rain).round(2)
    	log.debug "Week: Changing ${state.weekPrecipInches[7]} to ${state.weekPrecipInches[7]+rain}"
        state.weekPrecipInches[7] = state.weekPrecipInches[0] + state.weekPrecipInches[1] + state.weekPrecipInches[2] + state.weekPrecipInches[3] +
                                     state.weekPrecipInches[4] + state.weekPrecipInches[5] + state.weekPrecipInches[6]

// Set Month Total
		if (dayOfMonth - daysBack >= 1) { 
        	log.debug "Month: Changing ${state.currMonthPrecipInches} to ${state.currMonthPrecipInches + rain}"
	        state.currMonthPrecipInches = state.currMonthPrecipInches + rain
        }
        else {
        	log.debug "Previous Month: Changing ${state.monthsPrecipInches[currMonth-1]} to ${state.monthsPrecipInches[currMonth-1] + rain}"
        	state.monthsPrecipInches[currMonth-1] = state.monthsPrecipInches[currMonth-1] + rain
        }

// Set Year Total
		if (dayOfYear - daysBack >= 1) {
        	log.debug "Year: Changing ${state.currYearPrecipInches} to ${state.currYearPrecipInches + rain}"
	        state.currYearPrecipInches = state.currYearPrecipInches + rain
        }
        else {
        	log.debug "Previous Year: Changing ${state.prevYearPrecipInches} to ${state.prevYearPrecipInches + rain}"
        	state.prevYearPrecipInches = state.prevYearPrecipInches + rain
        }
    }
    else {
    	log.debug "Unable to update precip totals..."
    }
}

def precipSchedule(evt) {
	log.debug "precipSchedule"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
	def rain = 0.0
    
	rain = Float.parseFloat(myWxDevice.currentValue("rainToday"))

    if (rain > 0.0) {
    // Accumulate rain totals
    // Set Day Total
        state.weekPrecipInches[day - 1] = rain
        
    // Set Week Total
//    	state.weekPrecipInches[7] = (state.weekPrecipInches[7] + rain).round(2)
        state.weekPrecipInches[7] = (state.weekPrecipInches[0] + state.weekPrecipInches[1] + state.weekPrecipInches[2] + state.weekPrecipInches[3] +
        	state.weekPrecipInches[4] + state.weekPrecipInches[5] + state.weekPrecipInches[6]).round(2)

    // Set Month Total
        state.currMonthPrecipInches = (state.currMonthPrecipInches + rain).round(2)
        myWxDevice?.setRainThisMonth(Float.parseFloat("${state.currMonthPrecipInches}").round(2))

    // Set Year Total
            state.currYearPrecipInches = (state.currYearPrecipInches + rain).round(2)
            myWxDevice?.setRainThisYear(Float.parseFloat("${state.currYearPrecipInches}").round(2))
    }
}

def resetSchedule(evt) {
	log.debug "resetSchedule"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	state.rainTotal = 0.0
    
    if (localCalendar.get(Calendar.YEAR) != state.currYearDisp) {
    	log.debug "Resetting current year values..."
		state.prevYearDisp = state.currYearDisp
        state.prevYearPrecipInches = state.currYearPrecipInches
        state.prevYearHigh = state.currYearHigh
        state.prevYearHighDate = state.currYearHighDate
        state.prevYearLow = state.currYearLow
        state.prevYearLowDate = state.currYearLowDate
        
		state.currYearDisp = localCalendar.get(Calendar.YEAR)
        state.currYearPrecipInches = 0.0
        state.currYearHigh = -99
        state.currYearHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
        state.currYearLow = 99
        state.currYearLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
    if (localCalendar.get(Calendar.MONTH) != state.currMonth) {
    	log.debug "Resetting current month values..."
        state.monthsPrecipInches[state.currMonth] = state.currMonthPrecipInches
        state.monthsHigh[state.currMonth] = state.currMonthHigh
        state.monthsLow[state.currMonth] = state.currMonthLow
        
    	state.currMonth = localCalendar.get(Calendar.MONTH)
        state.currMonthPrecipInches = 0.0
        state.currMonthHigh = -99
        state.currMonthHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
        state.currMonthLow = 99
        state.currMonthLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }

	log.debug "Resetting today's values and recalculating weekly totals..."
	state.weekPrecipInches[day - 1] = 0.0
    state.weekHigh[day - 1] = -99
    state.weekLow[day - 1] = 99
    myWxDevice.setActualLow(99)
    myWxDevice.setActualHigh(-99)
    
    myWxDevice.setRainThisMonth(Float.parseFloat("${state.currMonthPrecipInches}").round(2))
    myWxDevice.setRainThisYear(Float.parseFloat("${state.currYearPrecipInches}").round(2))

   state.weekPrecipInches[7] = state.weekPrecipInches[0] + state.weekPrecipInches[1] + state.weekPrecipInches[2] + state.weekPrecipInches[3] +
                                 state.weekPrecipInches[4] + state.weekPrecipInches[5] + state.weekPrecipInches[6]

	state.weekLow[7] = 99
    state.weekHigh[7] = -99
    for (int x = 0; x < 7; x++) {
        if (state.weekLow[x] < state.weekLow[7]) {
            log.debug "Adjust week low from ${state.weekLow[7]} to ${state.weekLow[x]}"
            state.weekLow[7] = state.weekLow[x]
        }
        if (state.weekHigh[x] > state.weekHigh[7]) {
            log.debug "Adjust week high from ${state.weekHigh[7]} to ${state.weekHigh[x]}"
            state.weekHigh[7] = state.weekHigh[x]
        }
    }
}

def mornForecastSchedule(evt) {
	log.debug "mornForecastSchedule"
	getForecast()
}

def eveForecastSchedule(evt) {
	log.debug "eveForecastSchedule"
	getForecast()
}

private getForecast() {
	log.debug "getForecast"
	def msg = "${location} Forecast (${myWxDevice.currentValue("zipCode")})\n${myWxDevice.currentValue("forecast")}"

    if (sendPushMsg) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
    if (phone2) {
        sendSms(phone2, msg)
    }
    if (odPhone) {
        sendSms(odPhone, msg)
    }
}

def updateSchedule(evt) {
	log.debug "updateSchedule"
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
    def updateType = yearlyUpdateInterval

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

private sendFullUpdate(updateType) {
	log.debug "sendFullUpdate"
	sendWeeklyUpdate(updateType)
    sendYearlyUpdate(updateType)
}

private sendWeeklyUpdate(updateType) {
	log.debug "sendWeeklyUpdate"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()

	if (odPhone)
    {
        def response = getWeatherFeature("conditions", zipcode)
        def rain = Float.parseFloat(response?.current_observation?.precip_today_in)
        def cityDisp = (myWxDevice.currentValue("zipCode")) ? myWxDevice.currentValue("zipCode") : myWxDevice.currentValue("city")
        state.weekPrecipInches[day - 1] = rain
        
		log.debug "Day = ${day} / getDay = ${dispDay}"
        
        def msg = "${location} Weather Tracker (${cityDisp})\n\n${updateType} Update\n${getDay()}: ${getDateTime()}\nMonitoring Since: ${state.runningSince}\n\n" +
            "Week Total Precip = ${state.weekPrecipInches[7]} in.\n" +
            "Week Low / High Temp = ${state.weekLow[7]} / ${state.weekHigh[7]} F\n\n" +
            "${getDay(1)} Precip = ${state.weekPrecipInches[0]} in.\n" +
            "${getDay(1)} Low / High = ${state.weekLow[0]} / ${state.weekHigh[0]} F\n\n" +
            "${getDay(2)} Precip = ${state.weekPrecipInches[1]} in.\n" +
            "${getDay(2)} Low / High = ${state.weekLow[1]} / ${state.weekHigh[1]} F\n\n" +
            "${getDay(3)} Precip = ${state.weekPrecipInches[2]} in.\n" +
            "${getDay(3)} Low / High = ${state.weekLow[2]} / ${state.weekHigh[2]} F\n\n" +
            "${getDay(4)} Precip = ${state.weekPrecipInches[3]} in.\n" +
            "${getDay(4)} Low / High = ${state.weekLow[3]} / ${state.weekHigh[3]} F\n\n" +
            "${getDay(5)} Precip = ${state.weekPrecipInches[4]} in.\n" +
            "${getDay(5)} Low / High = ${state.weekLow[4]} / ${state.weekHigh[4]} F\n\n" +
            "${getDay(6)} Precip = ${state.weekPrecipInches[5]} in.\n" +
            "${getDay(6)} Low / High = ${state.weekLow[5]} / ${state.weekHigh[5]} F\n\n" +
            "${getDay(7)} Precip = ${state.weekPrecipInches[6]} in.\n" +
            "${getDay(7)} Low / High = ${state.weekLow[6]} / ${state.weekHigh[6]} F"

        if (odPhone) {
            sendSms(odPhone, msg)
        }
	}
}

private sendYearlyUpdate(updateType) {
	log.debug "sendYearlyUpdate: ${updateType}"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
	def dispMonth = getMonth(state.currMonth, false)
//    def dispDay = getDay()
    def msg = ""

	if (odPhone)
    {
	    def cityDisp = (myWxDevice.currentValue("zipCode")) ? myWxDevice.currentValue("zipCode") : myWxDevice.currentValue("city")
        msg = "${location} Weather Tracker (${cityDisp})\n\n${updateType} Update\n${getDateTime()}\nMonitoring Since: ${state.runningSince}\n\n" +
            "Curr Year (${state.currYearDisp}):\n" +
            "Total Precip = ${state.currYearPrecipInches} in.\n" +
            "Record Low = ${state.currYearLow} F on ${state.currYearLowDate}\n" +
            "Record High = ${state.currYearHigh} F on ${state.currYearHighDate}\n\n" +

            "Curr Month (${dispMonth}):\n" +
            "Total Precip = ${state.currMonthPrecipInches} in.\n" +
            "Record Low = ${state.currMonthLow} F on ${state.currMonthLowDate}\n" +
            "Record High = ${state.currMonthHigh} F on ${state.currMonthHighDate}\n\n" +

            "Prev Year (${state.prevYearDisp}):\n" +
            "Total Precip = ${state.prevYearPrecipInches} in.\n" +
            "Record Low = ${state.prevYearLow} F on ${state.prevYearLowDate}\n" +
            "Record High = ${state.prevYearHigh} F on ${state.prevYearHighDate}\n\n" +

            "${getMonth(0, true)} Precip = ${state.monthsPrecipInches[0]} in.\n" +
            "${getMonth(0, true)} Low / High = ${state.monthsLow[0]} / ${state.monthsHigh[0]}\n\n" +

            "${getMonth(1, true)} Precip = ${state.monthsPrecipInches[1]} in.\n" +
            "${getMonth(1, true)} Low / High = ${state.monthsLow[1]} / ${state.monthsHigh[1]}\n\n" +

            "${getMonth(2, true)} Precip = ${state.monthsPrecipInches[2]} in.\n" +
            "${getMonth(2, true)} Low / High = ${state.monthsLow[2]} / ${state.monthsHigh[2]}\n\n" +

            "${getMonth(3, true)} Precip = ${state.monthsPrecipInches[3]} in.\n" +
            "${getMonth(3, true)} Low / High = ${state.monthsLow[3]} / ${state.monthsHigh[3]}\n\n" +

            "${getMonth(4, true)} Precip = ${state.monthsPrecipInches[4]} in.\n" +
            "${getMonth(4, true)} Low / High = ${state.monthsLow[4]} / ${state.monthsHigh[4]}\n\n" +

            "${getMonth(5, true)} Precip = ${state.monthsPrecipInches[5]} in.\n" +
            "${getMonth(5, true)} Low / High = ${state.monthsLow[5]} / ${state.monthsHigh[5]}\n\n" +

            "${getMonth(6, true)} Precip = ${state.monthsPrecipInches[6]} in.\n" +
            "${getMonth(6, true)} Low / High = ${state.monthsLow[6]} / ${state.monthsHigh[6]}\n\n" +

            "${getMonth(7, true)} Precip = ${state.monthsPrecipInches[7]} in.\n" +
            "${getMonth(7, true)} Low / High = ${state.monthsLow[7]} / ${state.monthsHigh[7]}\n\n" +

            "${getMonth(8, true)} Precip = ${state.monthsPrecipInches[8]} in.\n" +
            "${getMonth(8, true)} Low / High = ${state.monthsLow[8]} / ${state.monthsHigh[8]}\n\n" +

            "${getMonth(9, true)} Precip = ${state.monthsPrecipInches[9]} in.\n" +
            "${getMonth(9, true)} Low / High = ${state.monthsLow[9]} / ${state.monthsHigh[9]}\n\n" +

            "${getMonth(10, true)} Precip = ${state.monthsPrecipInches[10]} in.\n" +
            "${getMonth(10, true)} Low / High = ${state.monthsLow[10]} / ${state.monthsHigh[10]}\n\n" +

            "${getMonth(11, true)} Precip = ${state.monthsPrecipInches[11]} in.\n" +
            "${getMonth(11, true)} Low / High = ${state.monthsLow[11]} / ${state.monthsHigh[11]}"

        if (odPhone) {
            sendSms(odPhone, msg)
        }
    }
}

private getDateTime() {
	log.debug "getDateTime"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")) //Calendar.getInstance(TimeZone.getDefault())
    return localCalendar.format("MM/dd/yyyy h:mm a")
}

private getMonth(mon, flagCurrent) {
//	log.debug "getMonth(${mon}, ${flagCurrent})"
    def retVal = ""
    
    if (flagCurrent) {
        retVal = (mon < state.currMonth) ? "<" : ">"
        if (mon == state.currMonth) {
            retVal = "*"
        }
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

private getDay(dayVal) {
//	log.debug "getDay(${dayVal})"
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def result = ""
    def checkDay = day
    if (dayVal >= 0) {
    	checkDay = dayVal
        result = (dayVal < day) ? "<" : ">"
        if (dayVal == day) {
            result = "*"
        }
    }

	switch (checkDay) {
    	case 1:
        	result = result + "Sun"
        	break
        case 2:
        	result = result + "Mon"
        	break
        case 3:
        	result = result + "Tue"
        	break
        case 4:
        	result = result + "Wed"
        	break
        case 5:
        	result = result + "Thu"
        	break
        case 6:
        	result = result + "Fri"
        	break
        case 7:
        	result = result + "Sat"
        	break
        default:
        	result = "ERR"
            break
    }
    return result
}

