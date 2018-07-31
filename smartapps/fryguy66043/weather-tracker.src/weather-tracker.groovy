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
 *  Weather Tracker
 *
 *  Author: Jeffrey Fry
 */

definition(
    name: "Weather Tracker",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Daily forecast with precipitation tracking.",
	category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text@2x.png"
)

preferences {
	section("Zip code...") {
		input "zipcode", "text", title: "Zipcode?"
	}
	section("Turn On Lights When It's Raining? (Optional)") {
    	input "rainLights", "capability.switch", multiple: true, required: false, title: "Turn on these lights when it's raining, but only during the day"
//        input "fromTime", "time", required: false, title: "From time..."
//        input "toTime", "time", required: false, title: "To time"
    }
	section("Daily Morning Forecast?") {
    	input "dailyForecast", "boolean", defaultValue: false
        input "dailyForecastTime", "time", required: false
    }
    section("Daily Evening Forecast?") {
    	input "dailyEveForecast", "boolean", defaultValue: false
        input "dailyEveForecastTime", "time", required: false
    }
	section("Choose outside temperature sensor...") {
    	input "outsideTemp", "capability.temperatureMeasurement", required: true
    }
    section("Daily Precip Update Time (before midnight)") {
    	input "dailyPrecipUpdateTime", "time", required: true
    }
    section("Reset Values Time (after midnight)") {
    	input "dailyResetTime", "time", required: true
    }
	section("How Frequently Should I Check Weather Conditions?") {
    	input "checkInterval", "enum", title: "Interval Minutes", options: ["10", "15", "30", "60"]
    }
	section("Send Push Notification?") {
    	input "sendPushMsg", "boolean", defaultValue: false
    }
	section("Send a Text?") {
        input "phone", "phone", title: "Phone number?", required: false
	}
	section("Send a Second Text?") {
        input "phone2", "phone", title: "Phone number?", required: false
	}
    section("On Demand Text?") {
    	input "odPhone", "phone", title: "On Demand Update Phone Number?", required: false
    }
}


def installed() {
	log.debug "Installed: $settings"
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))

	state.raining = false
    state.turnedLightsOn = false
    
	state.currTemp = 0

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

	state.monthsPrecipInches = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    state.monthsHigh = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
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
	subscribe(app, appHandler)
    subscribe(outsideTemp, "temperature", temperatureHandler)
	if (dailyForecast && dailyForecastTime) {
    	schedule(dailyForecastTime, mornForecastSchedule)
        schedule(dailyForecastTime, sendUpdate)
    }
    if (dailyEveForecast && dailyEveForecastTime) {
    	schedule(dailyEveForecastTime, eveForecastSchedule)
    }
    schedule(dailyPrecipUpdateTime, precipSchedule)
    schedule(dailyResetTime, resetSchedule)
    switch (checkInterval) {
    	case "10":
            runEvery10Minutes(scheduleCheck)
            break
        case "15":
            runEvery15Minutes(scheduleCheck)
            break
        case "30":
            runEvery30Minutes(scheduleCheck)
            break
        case "60":
            runEvery1Hour(scheduleCheck)
            break
        default:
            runEvery30Minutes(scheduleCheck)
            break
    }
}

def appHandler(evt) {
	scheduleCheck()
    sendUpdate()
    getForecast()
}

def temperatureHandler(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def ot = outsideTemp.currentTemperature
    
	state.currTemp = ot

// Check Day Records
    if (ot > state.weekHigh[day - 1]) {
    	state.weekHigh[day - 1] = ot
    }
    if (ot < state.weekLow[day - 1]) {
    	state.weekLow[day - 1] = ot
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

def scheduleCheck(evt) {
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def daylight = getSunriseAndSunset()
	def msg = "${location}\n"
    def schedRun = false
    
    def response = getWeatherFeature("conditions", zipcode)

	def rain = Float.parseFloat(response?.current_observation?.precip_1hr_in)
    def rainToday = Float.parseFloat(response?.current_observation?.precip_today_in)

    state.weekPrecipInches[day - 1] = rainToday                


	if (evt) {
    	schedRun = true
    }


//	sendSms("9136831550", "${location}")
//	sendSms("9136679526", "${location}: size == ${rainLights?.size()}")
//    rainLights.on()
//	if (rainLights?.size() > 0) {
//		sendSms("9136831550", "${location}: Testing ${rainLights} message.")
//    }
//    else {
//    	sendSms("9136831550", "${location}: No lights!")
//   }
//	sendSms("9136831550", "${location}: state.raining == ${state.raining}")
//    state.raining = true
	def lightsOn = rainLights.find{it.currentSwitch == "on"}
    def LightsOff = rainLights.find{it.currentSwitch == "off"}
//    sendSms("9136831550", "lightsOn == ${lightsOn}  state.raining == ${state.raining}")
//	  sendSms("9136831550", "state.turnedLightsOn == ${state.turnedLightsOn}")
	if (rain > 0.0 || state.raining) {
//    		sendSms("9136831550", "state.raining == ${state.raining}")
//    		sendSms("9136831550", "${location}: rainLights.currentSwitch == ${rainLights.currentSwitch}")
//          sendSms("9136831550", "${location}: sunrise == ${daylight.sunrise} sunset == ${daylight.sunset}")
            if (lightsOff) {
                if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                    rainLights?.on()
                    state.turnedLightsOn = true
                    if (sendPushMsg) {
                        sendPush("${location}: Turned on ${rainLights} because it started raining")
                    }
                    if (phone) {
                    	sendSms(phone, "${location}: Turned on ${rainLights} because it started raining")
                    }
                }
            }
//        sendSms("9136831550", "${location}: rain == ${rain}  rainToday == ${rainToday}")
        if (rain > 0.0 || rainToday > 0.0) {
            if (rain > 0.0) {
            	state.raining = true
                msg = msg + "It's Raining!  ${rain} in last hour.\n"
            }
            else {
            	state.raining = false
            }
            if (rainToday > 0.0) {
                msg = msg + "Today's Total: ${rainToday}"
            }
            if (sendPushMsg) {
                sendPush(msg)
            }
            if (phone) {
                sendSms(phone, msg)
            }
        }
        else {
        	state.raining = false
        }
    }

	else {
    	if (state.turnedLightsOn) {
        	if (lightsOn) {
            	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                    rainLights.off()
                    state.turnedLightsOn = false
                    if (sendPushMsg) {
                        sendPush("${location}: Turned off ${rainLights} because it stopped raining")
                    }
                    if (phone) {
                    	sendSms(phone, "${location}: Turned off ${rainLights} because it stopped raining")
                    }
                }
            }
            else {
            	state.turnedLightsOn = false
            }
        }
    }

}

def precipSchedule(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	def response = getWeatherFeature("conditions", zipcode)
    def rain = Float.parseFloat(response?.current_observation?.precip_today_in)
	
    if (rain > 0.0) {
    	// Accumulate rain totals
    // Check Day Records
        state.weekPrecipInches[day - 1] = rain
        
    // Week Records
    	state.weekPrecipInches[7] = (state.weekPrecipInches[7] + rain).round(2)

    // Check Month Records
        state.currMonthPrecipInches = (state.currMonthPrecipInches + rain).round(2)

    // Check Year Records
            state.currYearPrecipInches = (state.currYearPrecipInches + rain).round(2)
    }
}

def resetSchedule(evt) {
	if (localCalendar.get(Calendar.YEAR) != state.currYearDisp) {
		state.prevYearDisp = state.currYearDisp
        state.prevYearPrecipInches = state.currYearPrecipInches
        state.prevYearHigh = state.currYearHigh
        state.prevYearHighDate = state.currYearHighDate
        state.prevYearLow = state.currYearLow
        state.prevYearLowDate = state.currYearLowDate
        
		state.currYearDisp = localCalendar.get(Calendar.YEAR)
        state.currYearPrecipInches = 0.0
        state.currYearHigh = 0
        state.currYearHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
        state.currYearLow = 99
        state.currYearLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
    if (localCalendar.get(Calendar.MONTH) != state.currMonth) {
        state.monthsPrecipInches[state.currMonth] = state.currMonthPrecipInches
        state.monthsHigh[state.currMonth] = state.currMonthHigh
        state.monthsLow[state.currMonth] = state.currMonthLow
        
    	state.currMonth = localCalendar.get(Calendar.MONTH)
        state.currMonthPrecipInches = 0.0
        state.currMonthHigh = 0
        state.currMonthHighDate = localCalendar.format("MM/dd/yyyy h:mm a")
        state.currMonthLow = 99
        state.currMonthLowDate = localCalendar.format("MM/dd/yyyy h:mm a")
    }
	if (day == 1) {
    	state.weekPrecipInches[7] = 0.0
    }
    
	state.weekPrecipInches[day - 1] = 0.0
    state.weekHigh[day - 1] = 0
    state.weekLow[day - 1] = 99
}

def mornForecastSchedule(evt) {
	def result = false
    for(int i = 0; i < 3; i++) {
		result = getForecast()
        if (result) {
        	break
        }
    }
    if (!result) {
    	sendSms("9136831550", "${location}: Forecast retry!")
    	runIn(15, forecastRetry)
    }
}

def forecastRetry(evt) {
	if (!getForecast()) {
        if (sendPushMsg) {
            sendPush("${location}: Unable to retrieve forecast.")
        }
        if (phone) {
            sendSms(phone, "${location}: Unable to retrieve forecast.")
        }
        if (odPhone) {
            sendSms(odPhone, "${location}: Unable to retrieve forecast.")
        }
    }
}

def eveForecastSchedule(evt) {
	def result = false
    for(int i = 0; i < 3; i++) {
		result = getForecast()
        if (result) {
        	break
        }
    }
    if (!result) {
        if (sendPushMsg) {
            sendPush("${location}: Unable to retrieve forecast.")
        }
        if (phone) {
            sendSms(phone, "${location}: Unable to retrieve forecast.")
        }
        if (odPhone) {
            sendSms(odPhone, "${location}: Unable to retrieve forecast.")
        }
    }
}

private getForecast() {
	def result = false
	def msg = "${location} Forecast (${zipcode}):\n"
    def text = ""
	def response = getWeatherFeature("forecast", zipcode)
	def forecast = response?.forecast?.txt_forecast?.forecastday[0]
	if (forecast) {
    	text = forecast?.title + ": "
		text = text + forecast?.fcttext
        forecast = response?.forecast?.txt_forecast?.forecastday[1]
        text = text + "\n" + forecast?.title + ":"
        text = text + forecast?.fcttext
        forecast = response?.forecast?.txt_forecast?.forecastday[2]
        text = text + "\n" + forecast?.title + ":"
        text = text + forecast?.fcttext
		if (!text) {
        	return result
//        	text = "Unable to retrieve today's forecast."
        }
    
        def forecastDetail = response?.forecast?.simpleforecast?.forecastday?.first()
        def forecastHigh = forecastDetail?.high?.fahrenheit
        def forecastLow = forecastDetail?.low?.fahrenheit
        def forecastPOP = forecastDetail?.pop
        def forecastPrecip = forecastDetail?.qpf_allday?.in
        def forecastSnow = forecastDetail?.snow_allday?.in
        text = text + " (${forecastLow} / ${forecastHigh} F; Precip: ${forecastPOP}%; Rain: ${forecastPrecip} in; Snow: ${forecastSnow} in)"
	} else {
		log.warn "Did not get a forecast: $json"
        return result
//        text = "Unable to retrieve today's forecast."
	}
    msg = msg + text
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
    result = true
    return result
}

def sendUpdate(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()
    def updateType = ""

	if (evt == nul) {
	    updateType = "Daily"
    }
    else {
    	updateType = evt.value
    }

	if (phone || odPhone)
    {
        def response = getWeatherFeature("conditions", zipcode)
        def rain = Float.parseFloat(response?.current_observation?.precip_today_in)
        state.weekPrecipInches[day - 1] = rain

        def msg = "${location} Weather Tracker (${zipcode})\n\n${getDateTime()}\nUpdate Type: ${updateType}\nMonitoring Since: ${state.runningSince}\n\n" +
            "Week Total Precip = ${state.weekPrecipInches[7]} in.\n" +
            "Week Low / High Temp = ${state.weekLow[7]} / ${state.weekHigh[7]} deg.\n\n" +
            "Mon Precip = ${state.weekPrecipInches[1]} in.\n" +
            "Mon Low / High = ${state.weekLow[1]} / ${state.weekHigh[1]} deg.\n\n" +
            "Tue Precip = ${state.weekPrecipInches[2]} in.\n" +
            "Tue Low / High = ${state.weekLow[2]} / ${state.weekHigh[2]} deg.\n\n" +
            "Wed Precip = ${state.weekPrecipInches[3]} in.\n" +
            "Wed Low / High = ${state.weekLow[3]} / ${state.weekHigh[3]} deg.\n\n" +
            "Thu Precip = ${state.weekPrecipInches[4]} in.\n" +
            "Thu Low / High = ${state.weekLow[4]} / ${state.weekHigh[4]} deg.\n\n" +
            "Fri Precip = ${state.weekPrecipInches[5]} in.\n" +
            "Fri Low / High = ${state.weekLow[5]} / ${state.weekHigh[5]} deg.\n\n" +
            "Sat Precip = ${state.weekPrecipInches[6]} in.\n" +
            "Sat Low / High = ${state.weekLow[6]} / ${state.weekHigh[6]} deg.\n\n" +
            "Sun Precip = ${state.weekPrecipInches[0]} in.\n" +
            "Sun Low / High = ${state.weekLow[0]} / ${state.weekHigh[0]} deg.\n"

        if (odPhone) {
            sendSms(odPhone, msg)
        }
        else {
            if (phone) {
                sendSms(phone, msg)
            }
        }

        msg = "${location} Weather Tracker (${zipcode})\n\n${getDateTime()}\n\n" +
            "Curr Year (${state.currYearDisp}):\n" +
            "Total Precip = ${state.currYearPrecipInches} in.\n" +
            "Record Low = ${state.currYearLow} deg. on ${state.currYearLowDate}\n" +
            "Record High = ${state.currYearHigh} deg. on ${state.currYearHighDate}\n\n" +

            "Curr Month (${dispMonth}):\n" +
            "Total Precip = ${state.currMonthPrecipInches} in.\n" +
            "Record Low = ${state.currMonthLow} deg. on ${state.currMonthLowDate}\n" +
            "Record High = ${state.currMonthHigh} deg. on ${state.currMonthHighDate}\n\n" +

            "Prev Year (${state.prevYearDisp}):\n" +
            "Total Precip = ${state.prevYearPrecipInches} in.\n" +
            "Record Low = ${state.prevYearLow} deg. on ${state.prevYearLowDate}\n" +
            "Record High = ${state.prevYearHigh} deg. on ${state.prevYearHighDate}\n\n" +

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
            "${getMonth(11, true)} Low / High = ${state.monthsLow[11]} / ${state.monthsHigh[11]}\n"

        if (odPhone) {
            sendSms(odPhone, msg)
        }
        else {
            if (phone) {
                sendSms(phone, msg)
            }
        }
    }
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
