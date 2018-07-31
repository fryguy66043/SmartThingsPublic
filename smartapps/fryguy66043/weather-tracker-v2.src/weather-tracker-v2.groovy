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
 *  Weather Tracker v2
 *
 *  Author: Jeffrey Fry
 */

definition(
    name: "Weather Tracker v2",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Daily forecast with precipitation tracking.",
	category: "Convenience",
	iconUrl: "http://cdn.device-icons.smartthings.com/Weather/weather10-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Weather/weather10-icn@2x.png"
//    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text.png",
//    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text@2x.png"
)

preferences {
	section("Zipcode...") {
		input "zipcode", "text", title: "Zipcode for weather data."
	}
	section("Turn On Lights When It Gets Dark During The Day? (Optional)") {
    	input "rainLights", "capability.switch", multiple: true, required: false, title: "Turn on these lights when weather conditions make it dark, but only during the day."
        input "luxLevel", "enum", options: [250, 500, 750, 1000, 1250, 1500, 1750, 2000], required: false, title: "Turn on at selected illumination level?"
    }
	section("Get a Daily Morning Forecast?") {
    	input "dailyForecast", "boolean", defaultValue: false, title: "Yes or No?"
        input "dailyForecastTime", "time", required: false, title: "What time?"
    }
    section("Get a Daily Evening Forecast?") {
    	input "dailyEveForecast", "boolean", defaultValue: false, title: "Yes or No?"
        input "dailyEveForecastTime", "time", required: false, title: "What time?"
    }
	section("Choose Outside Temperature Sensor...") {
    	input "outsideTemp", "capability.temperatureMeasurement", required: true, title: "You need a sensor that measures outside temps."
    }
    section("Daily Precip Update Time (before midnight e.g.- 11:59 pm)") {
    	input "dailyPrecipUpdateTime", "time", required: true, title: "This is the time I will accumulate precipitation totals for the day."
    }
    section("Reset Values Time (after midnight - e.g. 12:00 am)") {
    	input "dailyResetTime", "time", required: true, title: "This is the time I will reset the totals for the day/week/month/year."
    }
	section("How Frequently Should I Check Weather Conditions?") {
    	input "checkInterval", "enum", title: "Interval Minutes", options: ["10", "15", "30", "60"]
    }
	section("Send a Full Precipation Report Periodically") {
    	input "updateInterval", "enum", 
        	title: "Full Report Schedule (will send on the first day of selection). You need an On Demand Phone to receive these reports. It can be the same as the Primary or Secondary Phone.", 
        	options: ["Never", "Daily", "Weekly", "Monthly"], required: true
        input "updateTime", "time", title: "Send at What Time?"
    }
	section("Play Button Actions...") {
    	input "playSendForecast", "boolean", title: "Send the forecast?", defaultValue: false
        input "playCheckConditions", "boolean", title: "Check weather conditions?", defaultValue: false
        input "playSendWeeklyUpdate", "boolean", title: "Send weekly update?", defaultValue: false
        input "playSendYearlyUpdate", "boolean", title: "Send yearly update?", defaultValue: false
        input "playSendStationID", "boolean", title: "Send weather station ID?", defaultValue: false
        input "playSendLuxValue", "boolean", title: "Send Lux value?", defaultValue: false
    }
	section("Send Push Notification?") {
    	input "sendPushMsg", "boolean", defaultValue: false, title: "Send a SmartThings Notification?"
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
}


def installed() {
	log.debug "Installed: $settings"
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))

	state.raining = false
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
	subscribe(app, appHandler)
	subscribe(outsideTemp, "temperature", temperatureHandler)
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
	def msg = "${location}:\n\n"
    def sendMsg = true
    def bCheckConditions = playCheckConditions
    def bWeeklyUpdate = playSendWeeklyUpdate

msg = msg + "playCheckConditions = ${playCheckConditions}\n" +
	"playSendWeeklyUpdate = ${playSendWeeklyUpdate}\n" +
    "bWeeklyUpdate = ${bWeeklyUpdate}\n" +
	"playSendYearlyUpdate = ${playSendYearlyUpdate}\n" +
    "playSendForecast = ${playSendForecast}\n" +
    "playSendStationID = ${playSendStationID}\n" +
    "playSendLuxValue = ${playSendLuxValue}\n\n"
   
    if (bCheckConditions) {
	    scheduleCheck()
    }
    if (bWeeklyUpdate) {
	    sendWeeklyUpdate("On Demand")
    }
    if (playSendYearlyUpdate) {
	    sendYearlyUpdate("On Demand")
    }
    if (playSendForecast) {
//	    getForecast()
    }
    if (playSendStationID) {
    	msg = msg + "Station ID: ${state.stationID}\n"
        sendMsg = true
    }
    if (playSendLuxValue) {
    	msg = msg + "Lux Value: ${state.currLux}"
        sendMsg = true
    }
    if (sendMsg) {
    	if (odPhone) {
        	sendSms(odPhone, msg)
        }
    }    
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

def scheduleCheck(evt) {
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def daylight = getSunriseAndSunset()
	def msg = "${location}\n"
    def schedRun = false
    
    def response = getWeatherFeature("conditions", zipcode)

	def rain = Float.parseFloat(response?.current_observation?.precip_1hr_in)
    def rainToday = Float.parseFloat(response?.current_observation?.precip_today_in)
    def stationID = response?.current_observation?.station_id
    def wxIcon = response?.current_observation?.icon
	def luxValue = getLux(wxIcon)    

	state.stationID = stationID
    state.currLux = luxValue
    state.weekPrecipInches[day - 1] = rainToday                


	if (evt) {
    	schedRun = true
    }

	if (rainLights) {
        def lightsOn = rainLights.find{it.currentSwitch == "on"}
        def lightsOff = rainLights.find{it.currentSwitch == "off"}
        if (lightsOff) {
            if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
            	if (luxValue <= Long.parseLong(luxLevel)) {
                    rainLights?.on()
                    state.turnedLightsOn = true
                    if (sendPushMsg) {
                        sendPush("${location}: Turned on ${rainLights} because it got dark outside (Lux Level: ${luxValue})")
                    }
                    if (phone) {
                        sendSms(phone, "${location}: Turned on ${rainLights} because it got dark outside (Lux Level: ${luxValue})")
                    }
                }
            }
        }
    	if (state.turnedLightsOn) {
        	if (lightsOn) {
            	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
                	if (luxValue >= Long.parseLong(luxLevel)) {	
                        rainLights?.off()
                        state.turnedLightsOn = false
                        if (sendPushMsg) {
                            sendPush("${location}: Turned off ${rainLights} because it is no longer dark outside (Lux Level: ${luxValue})")
                        }
                        if (phone) {
                            sendSms(phone, "${location}: Turned off ${rainLights} because it is no longer dark outside (Lux Level: ${luxValue})")
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

	if (rain > 0.0 || state.raining) {
        if (rain > 0.0 || rainToday > 0.0) {
            if (rain > 0.0) {
            	state.raining = true
                msg = msg + "It's Raining!\n"
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
}

def precipSchedule(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

	def response = getWeatherFeature("conditions", zipcode)
    def rain = Float.parseFloat(response?.current_observation?.precip_today_in)
	
    if (rain > 0.0) {
    // Accumulate rain totals
    // Set Day Total
        state.weekPrecipInches[day - 1] = rain
        
    // Set Week Total
    	state.weekPrecipInches[7] = (state.weekPrecipInches[7] + rain).round(2)

    // Set Month Total
        state.currMonthPrecipInches = (state.currMonthPrecipInches + rain).round(2)

    // Set Year Total
            state.currYearPrecipInches = (state.currYearPrecipInches + rain).round(2)
    }
}

def resetSchedule(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
	
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

def updateSchedule(evt) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int dayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    def sendUpdate = false
    def updateType = updateInterval

//	sendSms("9136831550", "${location}: updateSchedule(); updateInterval == ${updateInterval}")

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
//        	sendSms("9136831550", "${location}: Unable to determine update interval")
        	break
    }
    if (sendUpdate) {
        sendFullUpdate(updateType)
    }
}

private sendFullUpdate(updateType) {
	sendWeeklyUpdate(updateType)
    sendYearlyUpdate(updateType)
}

private sendWeeklyUpdate(updateType) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()

	if (odPhone)
    {
        def response = getWeatherFeature("conditions", zipcode)
        def rain = Float.parseFloat(response?.current_observation?.precip_today_in)
        state.weekPrecipInches[day - 1] = rain

        def msg = "${location} Weather Tracker (${zipcode})\n\n${updateType} Update\n${getDateTime()}\nMonitoring Since: ${state.runningSince}\n\n" +
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
	}
}

private sendYearlyUpdate(updateType) {
	Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
	def dispMonth = getMonth(state.currMonth, false)
    def dispDay = getDay()
    def msg = ""

	if (odPhone)
    {
        msg = "${location} Weather Tracker (${zipcode})\n\n${updateType} Update\n${getDateTime()}\n\n" +
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

private getLux(wxIcon) {
	def daylight = getSunriseAndSunset()
	def lux = 0
    def now = new Date().time
    
//    sendSms("9136831550", "getLux(${wxIcon})")

	if (timeOfDayIsBetween(daylight.sunrise, daylight.sunset, new Date(), location.timeZone)) {
		//day
        switch(wxIcon) {
        case "tstorms":
            lux = 200
            break
        case ["cloudy", "fog", "rain", "sleet", "snow", "flurries",
            "chanceflurries", "chancerain", "chancesleet",
            "chancesnow", "chancetstorms"]:
            lux = 1000
            break
        case "mostlycloudy":
            lux = 2500
            break
        case ["partlysunny", "partlycloudy", "hazy"]:
            lux = 7500
            break
        default:
            //sunny, clear
            lux = 10000
        }
        //adjust for dusk/dawn
        def nowTime = new Date().time
        def sunriseTime = daylight.sunrise.time
        def sunsetTime = daylight.sunset.time
        def afterSunrise = nowTime - sunriseTime
        def beforeSunset = sunsetTime - nowTime
        def oneHour = 1000 * 60 * 60

        if(afterSunrise < oneHour) {
            //dawn
            lux = (long)(lux * (afterSunrise/oneHour))
        } else if (beforeSunset < oneHour) {
            //dusk
            lux = (long)(lux * (beforeSunset/oneHour))
        }        
    }
    else {
        //night - always set to 10 for now
        //could do calculations for dusk/dawn too
        lux = 10
    }

    return lux
}

/************ Copied from Weather Tile *****************
private estimateLux(wxIcon) {
sendSms("9136831550", "estimatedLux(${wxIcon})")
	def weatherIcon = wxIcon
	def daylight = getSunriseAndSunset()
	def lux = 0
    def now = new Date().time
//    if (now > sunriseDate.time && now < sunsetDate.time) {
    if (now > daylight.sunrise && now < daylight.sunset) {
        //day
        switch(weatherIcon) {
        case "tstorms":
            lux = 200
            break
        case ["cloudy", "fog", "rain", "sleet", "snow", "flurries",
            "chanceflurries", "chancerain", "chancesleet",
            "chancesnow", "chancetstorms"]:
            lux = 1000
            break
        case "mostlycloudy":
            lux = 2500
            break
        case ["partlysunny", "partlycloudy", "hazy"]:
            lux = 7500
            break
        default:
            //sunny, clear
            lux = 10000
        }

        //adjust for dusk/dawn
//        def afterSunrise = now - sunriseDate.time
//        def beforeSunset = sunsetDate.time - now
        def afterSunrise = now - daylight.sunrise
        def beforeSunset = daylight.sunset - now
        def oneHour = 1000 * 60 * 60

        if(afterSunrise < oneHour) {
            //dawn
            lux = (long)(lux * (afterSunrise/oneHour))
        } else if (beforeSunset < oneHour) {
            //dusk
            lux = (long)(lux * (beforeSunset/oneHour))
        }
    }
    else {
        //night - always set to 10 for now
        //could do calculations for dusk/dawn too
        lux = 10
    }

    return lux
}
*//////////////////////////////////////////////////////////////////
