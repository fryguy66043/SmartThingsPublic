/**
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
 *  Home Weather Station
 *    Calls fryguypi.ddns.net/wxcurrent to get current AcuRite weather information from the weewx weather service running on my Raspberry Pi.
 *
 *  Author: Jeffrey Fry
 *
 *  Date: October 26, 2018
 */
 
metadata {
	definition (name: "FryGuyPi Wx Device", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Sensor"

		attribute "lastUpdate", "string"
        attribute "observationTime", "string"

// Current Values
        attribute "insideTemperature", "string"
        attribute "actualLow", "string"
        attribute "actualHigh", "string"
		attribute "feelsLike", "string"
        attribute "dewpoint", "string"
        attribute "insideTemperature", "string"
        attribute "actualLow", "string"
        attribute "actualHigh", "string"
		attribute "feelsLike", "string"
        attribute "dewpoint", "string"

		attribute "wind", "string"
        attribute "highWind", "string"
        attribute "avgWind", "string"
        
		attribute "sunriseDate", "string"
		attribute "sunsetDate", "string"
        
        attribute "moonPhase", "string"
        
        attribute "highRainRate", "string"
        attribute "rainRate", "string"
        attribute "rainToday", "string"
        attribute "rainDisplay", "string"

        attribute "barometer", "string"
        attribute "barometerTrend", "string"

		attribute "wind", "string"
        attribute "highWind", "string"
        attribute "avgWind", "string"
        
		attribute "sunriseDate", "string"
		attribute "sunsetDate", "string"
        
        attribute "moonPhase", "string"
        
        attribute "highRainRate", "string"
        attribute "rainRate", "string"
        attribute "rainToday", "string"
        attribute "rainDisplay", "string"

        attribute "barometer", "string"
        attribute "barometerTrend", "string"

// Weekly Values
        attribute "weeklyHigh", "string"
        attribute "weeklyLow", "string"
        attribute "weeklyRainTotal", "string"
        attribute "weeklyHighRainRate", "string"
        attribute "weeklyHighWindSpeed", "string"


// Monthly Values
		attribute "monthlyHigh", "string"
        attribute "monthlyLow", "string"
        attribute "monthlyRainTotal", "string"
        attribute "monthlyHighRainRate", "string"
        attribute "monthlyHighWindSpeed", "string"


// Yearly Values
		attribute "yearlyHigh", "string"
        attribute "yearlyLow", "string"
		attribute "yearlyRainTotal", "string"
        attribute "yearlyHighRainRate", "string"
        attribute "yearlyHighWindSpeed", "string"



		command "refresh"
	}


	preferences {
		input "pollTime", "text", title: "Poll Minutes (optional)", required: false
        input "zipCode", "text", title: "Zipcode (optional)", required: false
	}


	tiles(scale: 2) {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state "default", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}

		standardTile("weatherIcon", "device.weatherIcon", decoration: "flat", width: 2, height: 2) {
			state "chanceflurries", icon:"st.custom.wu1.chanceflurries", label: ""
			state "chancerain", icon:"st.custom.wu1.chancerain", label: ""
			state "chancesleet", icon:"st.custom.wu1.chancesleet", label: ""
			state "chancesnow", icon:"st.custom.wu1.chancesnow", label: ""
			state "chancetstorms", icon:"st.custom.wu1.chancetstorms", label: ""
			state "clear", icon:"st.custom.wu1.clear", label: ""
			state "cloudy", icon:"st.custom.wu1.cloudy", label: ""
			state "flurries", icon:"st.custom.wu1.flurries", label: ""
			state "fog", icon:"st.custom.wu1.fog", label: ""
			state "hazy", icon:"st.custom.wu1.hazy", label: ""
			state "mostlycloudy", icon:"st.custom.wu1.mostlycloudy", label: ""
			state "mostlysunny", icon:"st.custom.wu1.mostlysunny", label: ""
			state "partlycloudy", icon:"st.custom.wu1.partlycloudy", label: ""
			state "partlysunny", icon:"st.custom.wu1.partlysunny", label: ""
			state "rain", icon:"st.custom.wu1.rain", label: ""
			state "sleet", icon:"st.custom.wu1.sleet", label: ""
			state "snow", icon:"st.custom.wu1.snow", label: ""
			state "sunny", icon:"st.custom.wu1.sunny", label: ""
			state "tstorms", icon:"st.custom.wu1.tstorms", label: ""
			state "cloudy", icon:"st.custom.wu1.cloudy", label: ""
			state "partlycloudy", icon:"st.custom.wu1.partlycloudy", label: ""
			state "nt_chanceflurries", icon:"st.custom.wu1.nt_chanceflurries", label: ""
			state "nt_chancerain", icon:"st.custom.wu1.nt_chancerain", label: ""
			state "nt_chancesleet", icon:"st.custom.wu1.nt_chancesleet", label: ""
			state "nt_chancesnow", icon:"st.custom.wu1.nt_chancesnow", label: ""
			state "nt_chancetstorms", icon:"st.custom.wu1.nt_chancetstorms", label: ""
			state "nt_clear", icon:"st.custom.wu1.nt_clear", label: ""
			state "nt_cloudy", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "nt_flurries", icon:"st.custom.wu1.nt_flurries", label: ""
			state "nt_fog", icon:"st.custom.wu1.nt_fog", label: ""
			state "nt_hazy", icon:"st.custom.wu1.nt_hazy", label: ""
			state "nt_mostlycloudy", icon:"st.custom.wu1.nt_mostlycloudy", label: ""
			state "nt_mostlysunny", icon:"st.custom.wu1.nt_mostlysunny", label: ""
			state "nt_partlycloudy", icon:"st.custom.wu1.nt_partlycloudy", label: ""
			state "nt_partlysunny", icon:"st.custom.wu1.nt_partlysunny", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "nt_rain", icon:"st.custom.wu1.nt_rain", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "nt_snow", icon:"st.custom.wu1.nt_snow", label: ""
			state "nt_sunny", icon:"st.custom.wu1.nt_sunny", label: ""
			state "nt_tstorms", icon:"st.custom.wu1.nt_tstorms", label: ""
			state "nt_cloudy", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "nt_partlycloudy", icon:"st.custom.wu1.nt_partlycloudy", label: ""
		}
        
		valueTile("weather", "device.weather", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}'
		}

		valueTile("percentPrecip", "device.percentPrecip", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}% precip'
		}

		valueTile("alert", "device.alert", width: 4, height: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
		valueTile("forecast", "device.forecast", width: 6, height: 5, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
		valueTile("humidity", "device.humidity", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Humidity:\n${currentValue}%'
		}

		valueTile("feelsLike", "device.feelsLike", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Feels Like:\n${currentValue}°'
		}

		valueTile("wind", "device.wind", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Wind:\n${currentValue}'
		}

		valueTile("rainDisplay", "device.rainDisplay", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Precip:\n${currentValue}'
		}

		valueTile("highLow", "device.highLow", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Today:\n${currentValue}'
		}

		standardTile("refresh", "device.weather", decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}

		valueTile("rise", "device.localSunrise", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Sunrise:\n${currentValue}'
		}

		valueTile("set", "device.localSunset", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Sunset:\n${currentValue}'
		}

        valueTile("moon", "device.moon", decoration: "flat", width: 6, height: 2) {
        	state "default", label: 'Moon:\n${currentValue}'
        }
        
        valueTile("week", "device.week", decoration: "flat", width: 6, height: 5) {
        	state "default", label: 'This Week:\n${currentValue}'
        }
        
        valueTile("month", "device.month", decoration: "flat", width: 6, height: 5) {
        	state "default", label: 'This Month:\n${currentValue}'
        }
        
        valueTile("year", "device.year", decoration: "flat", width: 6, height: 5) {
        	state "default", label: 'This Year:\n${currentValue}'
        }
        
		valueTile("lastUpdate", "device.lastUpdate", width: 6, height: 2, decoration: "flat") {
			state "default", label:'Last Updated:\n${currentValue}'
		}

        htmlTile(name: "htmlTempPage", action: "getHtmlPage", refreshInterval: 60, width: 6, height: 5, whitelist: ["fryguypi.ddns.net", "65.28.96.234", "192.168.2.3"])
        
        htmlTile(name: "htmlRainPage", action: "getRainPic", refreshInterval: 60, width: 6, height: 5, whitelist: ["fryguypi.ddns.net"])

        htmlTile(name: "htmlMonthTempPage", action: "getMonthTempPic", refreshInterval: 60, width: 6, height: 5, whitelist: ["fryguypi.ddns.net", "65.28.96.234", "192.168.2.3"])
        
        htmlTile(name: "htmlMonthRainPage", action: "getMonthRainPic", refreshInterval: 60, width: 6, height: 5, whitelist: ["fryguypi.ddns.net"])

		main(["temperature"])
		details(["temperature", "feelsLike", "humidity", "wind", "rainDisplay", "highLow", 
        	"rise", "set", "moon", "week", "month", "year", "lastUpdate", "htmlTempPage", "htmlRainPage", "htmlMonthTempPage", "htmlMonthRainPage", "refresh"])}
//		details(["temperature", "weatherIcon", "weather", "alert", "feelsLike", "humidity", "wind", "percentPrecip", "rainDisplay", "forecast", "highLow", 
//        	"rise", "set", "moon", "week", "month", "year", "lastUpdate", "htmlTempPage", "htmlRainPage", "htmlMonthTempPage", "htmlMonthRainPage", "refresh"])}
}


def getFullPath() {
	def PI_IP = "fryguypi.ddns.net"
	def PI_PORT = "80"

	return "http://${PI_IP}:${PI_PORT}"
}

mappings {
	path("/getHtmlPage") {
    	action: [GET: "getHtmlPage"]
    }
    path("/getRainPic") {
    	action: [GET: "getRainPic"]
    }
	path("/getMonthTempPic") {
    	action: [GET: "getMonthTempPic"]
    }
    path("/getMonthRainPic") {
    	action: [GET: "getMonthRainPic"]
    }
}

def getHtmlPage() {
	log.debug "getHtmlPage"
    def date = new Date().format("HH:mm:ss", location.timeZone)

	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
				<body>
					Last 24 Hour Temperature Readings<br>
                    <img src="${getFullPath()}/wxtemppic/${date}" alt="Pi Image" height="260" width="360"> 
				</body>
			</html>
		"""
    render contentType: "text/html", data: html, status: 200
}

def getRainPic() {
	log.debug "getRainPic"
    def date = new Date().format("HH:mm:ss", location.timeZone)

	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
				<body>
					Last 24 Hour Rain Readings<br>
                    <img src="${getFullPath()}/wxrainpic/${date}" alt="Pi Image" height="260" width="360"> 
				</body>
			</html>
		"""
    render contentType: "text/html", data: html, status: 200
}

def getMonthTempPic() {
	log.debug "getMonthTempPic"
    def date = new Date().format("HH:mm:ss", location.timeZone)

	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
				<body>
					Last 30 Days Temperature Readings<br>
                    <img src="${getFullPath()}/wxmonthtemppic/${date}" alt="Pi Image" height="260" width="360"> 
				</body>
			</html>
		"""
    render contentType: "text/html", data: html, status: 200
}

def getMonthRainPic() {
	log.debug "getMonthRainPic"
    def date = new Date().format("HH:mm:ss", location.timeZone)

	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
				<body>
					Last 30 Days Rain Readings<br>
                    <img src="${getFullPath()}/wxmonthrainpic/${date}" alt="Pi Image" height="260" width="360"> 
				</body>
			</html>
		"""
    render contentType: "text/html", data: html, status: 200
}



// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def installed() {
	log.debug "installed"
	poll()
	runEvery5Minutes(poll)
    WUPoll()
    runEvery15Minutes(WUPoll)
}

def updated() {
	log.debug "updated"
    def min = 10
    unschedule()
    if (pollTime) {
    	min = Integer.parseInt(pollTime)
    }
    log.debug "min = ${min}"
    if (min <= 5) {
    	log.debug "5"
    	runEvery5Minutes(poll)
    }
    else if (min <= 10) {
    	log.debug "10"
    	runEvery10Minutes(poll)
    }
    else {
    	log.debug "15"
    	runEvery15Minutes(poll)
    }
    runEvery15Minutes(WUPoll)
}

def uninstalled() {
	log.debug "uninstalled"
	unschedule()
}

// handle commands

def poll() {
	log.debug "polling..."
    state.pollStatus = false
	state.getStatus = false

	def path = "${getFullPath()}/wxcurrent"
    log.debug "polling Path: ${path}"
    runIn(10, pollErr)
    try {
        httpGet(path) { resp ->
        	log.debug "polling round-trip..."
            
			if (resp.status == 200) {
            	pollHandler("${resp.data}")
            } else {
                log.error "Error polling FryGuyPi service.  Status: ${resp.status}"
                pollErr()
            }
        }
        log.debug "After httpGet..."
    } catch (err) {
        log.debug "Error making getHttp poll request: $err"
    }
}

def pollHandler(sData) {
	log.debug "pollHandler()"
    state.pollStatus = true
    def hData = sData.replace("<br>", "")
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
//    log.debug "hData = \n${hData}"
    def hServerMsg = hData.split('\n')
	log.debug "hServerMsg.size = ${hServerMsg.size()}"
    def msg = ""
    def currTemp = 0.0
    def feelsLikeTemp = 0.0
    def rainRate = ""
    def weekDisp = ""
    def monthDisp = ""
    def yearDisp = ""
    def highLowDisp = ""
    def updateDisp = ""
    def windDisp = ""

    for (int i = 0; i < hServerMsg.size(); i++) {
//    	log.debug "hServerMsg[i] = ${hServerMsg[i]}"
        
        if (hServerMsg[i].contains("weewx data")) {
        	log.debug "Started parsing weather data..."
        }
        else if (hServerMsg[i].contains("{CurrTime}=")) {
        	msg = hServerMsg[i].replace("{CurrTime}=","")
            sendEvent(name: "observationTime", value: msg)
            updateDisp = "${timestamp}\n(${msg})"
            sendEvent(name: "lastUpdate", value: updateDisp)
        }
        else if(hServerMsg[i].contains("{Outside Temperature}=")) {
        	msg = hServerMsg[i].replace("{Outside Temperature}=","")
            msg = msg.replace("°F", "")
            currTemp = Float.parseFloat(msg)
            feelsLikeTemp = currTemp
            sendEvent(name: "temperature", value: msg)
        }
        else if(hServerMsg[i].contains("{Wind Chill}=")) {
        	msg = hServerMsg[i].replace("{Wind Chill}=","")
            msg = msg.replace("°F", "")
            if (feelsLikeTemp == currTemp) {
	            feelsLikeTemp = Float.parseFloat(msg)
                sendEvent(name: "feelsLike", value: feelsLikeTemp)
            }
        }
        else if(hServerMsg[i].contains("{Heat Index}=")) {
        	msg = hServerMsg[i].replace("{Heat Index}=","")
            msg = msg.replace("°F", "")
            if (feelsLikeTemp == currTemp) {
	            feelsLikeTemp = Float.parseFloat(msg)
                sendEvent(name: "feelsLike", value: feelsLikeTemp)
            }
        }
        else if(hServerMsg[i].contains("{Dewpoint}=")) {
        	msg = hServerMsg[i].replace("{Dewpoint}=","")
            sendEvent(name: "dewpoint", value: msg)
        }
        else if(hServerMsg[i].contains("{Humidity}=")) {
        	msg = hServerMsg[i].replace("{Humidity}=","")
            msg = msg.replace("%","")
            sendEvent(name: "humidity", value: msg)
        }
        else if(hServerMsg[i].contains("{Barometer}=")) {
        	msg = hServerMsg[i].replace("{Barometer}=","")
            sendEvent(name: "barometer", value: msg)
        }
        else if(hServerMsg[i].contains("{Barometer Trend (3 hours)}=")) {
        	msg = hServerMsg[i].replace("{Barometer Trend (3 hours)}=","")
            sendEvent(name: "barometerTrend", value: msg)
        }
        else if(hServerMsg[i].contains("{Wind}=")) {
        	msg = hServerMsg[i].replace("{Wind}=","")
            windDisp = "Last: ${msg}\n"
            //sendEvent(name: "wind", value: msg)
        }
        else if(hServerMsg[i].contains("{High Wind}=")) {
        	msg = hServerMsg[i].replace("{High Wind}=", "")
            
            def idx1 = msg.indexOf("from")
            def idx2 = msg.indexOf("at")
            def highWind = msg[0..idx1-1] + msg[idx2..msg.size()-1]
            sendEvent(name: "highWind", value: highWind)
            
            windDisp = windDisp + "Gust: ${highWind}"
            sendEvent(name: "wind", value: windDisp)
        }
        else if(hServerMsg[i].contains("{Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Rain Rate}=","")
            def rainRateIn = msg.replace(" in/hr", "")
            sendEvent(name: "rainRate", value: rainRateIn)
            rainRate = msg
        }
        else if(hServerMsg[i].contains("{Inside Temperature}=")) {
        	msg = hServerMsg[i].replace("{Inside Temperature}=","")
            sendEvent(name: "insideTemperature", value: msg)
        }
        else if(hServerMsg[i].contains("{High Temperature}=")) {
        	msg = hServerMsg[i].replace("{High Temperature}=","")
            def idx = msg.indexOf("°F")
            def high = msg[0..idx - 1]
            sendEvent(name: "actualHigh", value: high)
            highLowDisp = "High: ${msg}\n"
        }
        else if(hServerMsg[i].contains("{Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Low Temperature}=","")
            def idx = msg.indexOf("°F")
            def low = msg[0..idx - 1]
            sendEvent(name: "actualLow", value: low)
            highLowDisp = highLowDisp + "Low: ${msg}"
            sendEvent(name: "highLow", value: highLowDisp)
        }
        else if(hServerMsg[i].contains("{Today's Rain}=")) {
        	msg = hServerMsg[i].replace("{Today's Rain}=","")
            def rainTodayIn = msg.replace (" in", "")
            sendEvent(name: "rainToday", value: rainTodayIn)
            sendEvent(name: "rainDisplay", value: "Today: ${msg}\nRate: ${rainRate}")
        }
        else if(hServerMsg[i].contains("{High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{High Rain Rate}=","")
            sendEvent(name: "highRainRate", value: msg)
        }
        else if(hServerMsg[i].contains("{Average Wind}=")) {
        	msg = hServerMsg[i].replace("{Average Wind}=","")
            sendEvent(name: "avgWind", value: msg)
        }
        else if(hServerMsg[i].contains("{Sunrise}=")) {
        	msg = hServerMsg[i].replace("{Sunrise}=","")
            sendEvent(name: "sunriseDate", value: msg)
            sendEvent(name: "localSunrise", value: msg)
        }
        else if(hServerMsg[i].contains("{Sunset}=")) {
        	msg = hServerMsg[i].replace("{Sunset}=","")
            sendEvent(name: "sunsetDate", value: msg)
            sendEvent(name: "localSunset", value: msg)
        }
        else if(hServerMsg[i].contains("{Moon Phase}=")) {
        	msg = hServerMsg[i].replace("{Moon Phase}=","")
            sendEvent(name: "moonPhase", value: msg)
            sendEvent(name: "moon", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly High Temperature}=")) {
        	msg = hServerMsg[i].replace("{Weekly High Temperature}=","")
            def idx = msg.indexOf("°F")
            def high = msg[0..idx - 1]
            sendEvent(name: "weeklyHigh", value: high)
            weekDisp = "High: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Weekly Low Temperature}=","")
            def idx = msg.indexOf("°F")
            def low = msg[0..idx - 1]
            sendEvent(name: "weeklyLow", value: low)
            weekDisp = weekDisp + "Low: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly Rain Total}=")) {
        	msg = hServerMsg[i].replace("{Weekly Rain Total}=","")
            sendEvent(name: "weeklyRainTotal", value: msg)
            weekDisp = weekDisp + "Rain: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Weekly High Rain Rate}=","")
            sendEvent(name: "weeklyHighRainRate", value: msg)
            weekDisp = weekDisp + "Rain Rate: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly High Wind Speed}=")) {
        	msg = hServerMsg[i].replace("{Weekly High Wind Speed}=","")
            def idx1 = msg.indexOf("from ")
            def idx2 = msg.indexOf("at ")
            def highWind = msg[0..idx1-1] + msg[idx2..msg.size()-1]
            weekDisp = weekDisp + "High Wind: ${highWind}"
            sendEvent(name: "weeklyHighWindSpeed", value: msg)
            sendEvent(name: "week", value: weekDisp)
        }
        else if(hServerMsg[i].contains("{Monthly High Temperature}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Temperature}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "monthlyHigh", value: newMsg)
            monthDisp = "High: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Monthly Low Temperature}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "monthlyLow", value: newMsg)
            monthDisp = monthDisp + "Low: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly Rain Total}=")) {
        	msg = hServerMsg[i].replace("{Monthly Rain Total}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "monthlyRainTotal", value: newMsg)
            monthDisp = monthDisp + "Rain: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Rain Rate}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "monthlyHighRainRate", value: newMsg)
            monthDisp = monthDisp + "Rain Rate: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly High Wind Speed}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Wind Speed}=","")
            sendEvent(name: "monthlyHighWindSpeed", value: msg)
            def idx1 = msg.indexOf("from")
            def idx2 = msg.indexOf("at")
            msg = msg[0..idx1-1] + msg[idx2..msg.size()-1]
            
            msg = msg.replace(" at ", " on ")
            idx1 = msg.indexOf(" on")
            idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]            
            
            monthDisp = monthDisp + "High Wind: ${newMsg}"
            sendEvent(name: "month", value: monthDisp)
        }
        else if(hServerMsg[i].contains("{Yearly High Temperature}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Temperature}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "yearlyHigh", value: newMsg)
            yearDisp =  "High: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Yearly Low Temperature}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "yearlyLow", value: newMsg)
            yearDisp = yearDisp + "Low: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly Rain Total}=")) {
        	msg = hServerMsg[i].replace("{Yearly Rain Total}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "yearlyRainTotal", value: newMsg)
            yearDisp = yearDisp + "Rain: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Rain Rate}=","")
            msg = msg.replace(" at ", " on ")
            def idx1 = msg.indexOf(" on")
            def idx2 = msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            sendEvent(name: "yearlyHighRainRate", value: newMsg)
            yearDisp = yearDisp + "Rain Rate: ${newMsg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly High Wind Speed}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Wind Speed}=","")
            sendEvent(name: "yearlyHighWindSpeed", value: msg)
            def idx1 = msg.indexOf("from")
            def idx2 = msg.indexOf("at")
            msg = msg[0..idx1-1] + msg[idx2..msg.size()-1]
            msg = msg.replace(" at", " on")
            idx1 = msg.indexOf(" on")
            idx2 - msg.indexOf(" ", idx1 + 5)
            def newMsg = msg[0..idx2]
            yearDisp = yearDisp + "High Wind: ${newMsg}"
            sendEvent(name: "year", value: yearDisp)
        }
        else {
        	log.debug "Error: i = ${i} / hServerMsg[i] = ${hServerMsg[i]}"
        }
    }
}

def pollErr() {
	log.debug "getStatusErr (checking for errors)"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    if (!state.pollStatus) {
    	log.debug "Polling timed out..."
    }
    else {
    	log.debug "Polling success!"
    }
}

def WUPoll() {
	log.debug "WUPoll() - Obsoleted..."
}

def x_WUPoll() {
// WeatherUnderground is no longer a SmartThings partner...
	log.debug "Polling WeatherUnderground: ${location}..."
    
	// Current conditions
	def rep = get("conditions")
    def obs = ""
    if (rep == "") {
    	log.debug "WU 'conditions' Failure!"
    }
    else {
    	log.debug "WU 'conditions' Success!"
        parseConditions(rep)
    }        
        
    // Forecast
    def f = get("forecast")
    if (f == "") {
    	log.debug "WU 'forecast' Failure!"
    }
    else {
    	log.debug "WU 'forecast' Success!"
        parseForecast(f)
    }

    // Alerts
    def alerts = get("alerts")?.alerts
    if (alerts == "") {
    	log.debug "WU 'alerts' Failure!"
    }
    else {
    	log.debug "WU 'alerts' Success!"
        parseAlerts(alerts)
    }
}

private get(feature) {
//	log.debug "feature = ${feature}"
//    log.debug "zipCode = ${zipCode} / pws = ${pws}"
	def options = zipCode
    def results = ""
    
    log.debug "${feature} / ${options}"
    results = getWeatherFeature(feature, options)
    return results
}

def parseConditions(rep) {
	log.debug "parseConditions"
    def obs = rep?.current_observation
    if (!obs) {
        log.debug "! obs failure! rep: ${rep}"
        return
    }
    def tempCheck = obs?.temp_f
    if (obs && tempCheck > -50 && tempCheck < 150) {
        def weatherIcon = obs.icon_url.split("/")[-1].split("\\.")[0]
        sendEvent(name: "weather", value: obs.weather)
        sendEvent(name: "weatherIcon", value: weatherIcon, displayed: false)
    }
}

def parseForecast(f) {
	log.debug "parseForecast"
    def f1 = f?.forecast?.simpleforecast?.forecastday
    log.debug "f1?.size() = ${f1?.size()}"
    if (f1?.size()) {
        def icon = f1[0].icon_url.split("/")[-1].split("\\.")[0]
        def value = f1[0].pop as String // as String because of bug in determining state change of 0 numbers
        def forecastHigh = f1[0].high.fahrenheit
        def forecastLow = f1[0].low.fahrenheit
        log.debug "forecastHigh = ${forecastHigh} / forecastLow = ${forecastLow}"
        sendEvent(name: "percentPrecip", value: value, unit: "%")
        sendEvent(name: "forecastIcon", value: icon, displayed: false)
        sendEvent(name: "forecastHighTodayF", value: forecastHigh)
        sendEvent(name: "forecastLowTodayF", value: forecastLow)
    }
    else {
        sendEvent(name: "precentPrecip", value: "Unknown")
        sendEvent(name: "forecastIcon", value: "???")
        log.warn "Forecast not found"
    }
    def f2 = f?.forecast?.txt_forecast?.forecastday
    if (f2) {
        def shortForecastDisp = "${f2[0].title}: ${f2[0].fcttext}"
        def forecastDisp = "${f2[0].title}: ${f2[0].fcttext} ${f2[1].title}: ${f2[1].fcttext} ${f2[2].title}: ${f2[2].fcttext}"
        sendEvent(name: "shortForecast", value: shortForecastDisp)
        sendEvent(name: "forecast", value: forecastDisp)
    }
    else {
        log.warn "Forecast not found"
    }
}

def parseAlerts(alerts) {
	log.debug "parseAlerts"
    def newKeys = alerts?.collect{it.type + it.date_epoch} ?: []
    def oldKeys = device.currentState("alertKeys")?.jsonValue
    def noneString = "no current weather alerts"
    if (!newKeys && oldKeys == null) {
        sendEvent(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)
        sendEvent(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
    }
    else if (newKeys != oldKeys) {
        if (oldKeys == null) {
            oldKeys = []
        }
        sendEvent(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)

        def newAlerts = false
        alerts.each {alert ->
            if (!oldKeys.contains(alert.type + alert.date_epoch)) {
                def alertMsg = "${alert.description} from ${alert.date} until ${alert.expires}"
                sendEvent(name: "alert", value: pad(alert.description), descriptionText: alertMsg, isStateChange: true)
                newAlerts = true
            }
        }

        if (!newAlerts && device.currentValue("alert") != noneString) {
            sendEvent(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
        }
    }
}

def refresh() {
	log.debug "refresh()"
	poll()
    WUPoll()
    updated()
}