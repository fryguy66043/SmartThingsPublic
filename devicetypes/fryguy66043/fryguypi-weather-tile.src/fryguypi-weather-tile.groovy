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
	definition (name: "FryGuyPi Weather Tile", namespace: "FryGuy66043", author: "Jeffrey Fry") {
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

		valueTile("humidity", "device.humidity", decoration: "flat", width: 4, height: 2) {
			state "default", label:'Humidity:\n${currentValue}%'
		}

		valueTile("feelsLike", "device.feelsLike", decoration: "flat", width: 4, height: 2) {
			state "default", label:'Feels Like:\n${currentValue}°'
		}

		valueTile("wind", "device.wind", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Wind:\n${currentValue}'
		}

		valueTile("rainDisplay", "device.rainDisplay", decoration: "flat", width: 6, height: 2) {
			state "default", label:'Precip:\n${currentValue}'
		}

		standardTile("refresh", "device.weather", decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}

		valueTile("rise", "device.localSunrise", decoration: "flat", width: 4, height: 2) {
			state "default", label:'Sunrise:\n${currentValue}'
		}

		valueTile("set", "device.localSunset", decoration: "flat", width: 4, height: 2) {
			state "default", label:'Sunset:\n${currentValue}'
		}

        valueTile("moon", "device.moon", decoration: "flat", width: 4, height: 2) {
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
        
		main(["temperature"])
		details(["temperature", "feelsLike", "humidity", "wind", "rainDisplay", "rise", "set", "moon", "week", "month", "year", "lastUpdate", "refresh"])}
}


def getFullPath() {
	def PI_IP = "fryguypi.ddns.net"
	def PI_PORT = "80"

	return "http://${PI_IP}:${PI_PORT}"
}



// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def installed() {
	log.debug "installed"
	poll()
	runEvery5Minutes(poll)
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
}

def uninstalled() {
	log.debug "uninstalled"
	unschedule()
}

// handle commands

def poll() {
	log.debug "polling..."
    state.pollStatus = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "status", value: "${timestamp}\nGetting Pi Status...")
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

    for (int i = 0; i < hServerMsg.size(); i++) {
    	log.debug "hServerMsg[i] = ${hServerMsg[i]}"
        
        if (hServerMsg[i].contains("weewx data")) {
        	log.debug "Started parsing weather data..."
        }
        else if (hServerMsg[i].contains("{CurrTime}=")) {
        	msg = hServerMsg[i].replace("{CurrTime}=","")
            sendEvent(name: "observationTime", value: msg)
            sendEvent(name: "lastUpdate", value: msg)
        }
        else if(hServerMsg[i].contains("{Outside Temperature}=")) {
        	msg = hServerMsg[i].replace("{Outside Temperature}=","")
            msg = msg.replace("°F", "")
            currTemp = Float.parseFloat(msg)
            sendEvent(name: "temperature", value: msg)
        }
        else if(hServerMsg[i].contains("{Wind Chill}=")) {
        	msg = hServerMsg[i].replace("{Wind Chill}=","")
            msg = msg.replace("°F", "")
            if (feelsLikeTemp != currTemp) {
	            feelsLikeTemp = Float.parseFloat(msg)
                sendEvent(name: "feelsLike", value: feelsLikeTemp)
            }
        }
        else if(hServerMsg[i].contains("{Heat Index}=")) {
        	msg = hServerMsg[i].replace("{Heat Index}=","")
            msg = msg.replace("°F", "")
            if (feelsLikeTemp != currTemp) {
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
            sendEvent(name: "wind", value: msg)
        }
        else if(hServerMsg[i].contains("{Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Rain Rate}=","")
            sendEvent(name: "rainRate", value: msg)
            rainRate = msg
        }
        else if(hServerMsg[i].contains("{Inside Temperature}=")) {
        	msg = hServerMsg[i].replace("{Inside Temperature}=","")
            sendEvent(name: "insideTemperature", value: msg)
        }
        else if(hServerMsg[i].contains("{High Temperature}=")) {
        	msg = hServerMsg[i].replace("{High Temperature}=","")
            sendEvent(name: "actualHigh", value: msg)
        }
        else if(hServerMsg[i].contains("{Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Low Temperature}=","")
            sendEvent(name: "actualLow", value: msg)
        }
        else if(hServerMsg[i].contains("{Today's Rain}=")) {
        	msg = hServerMsg[i].replace("{Today's Rain}=","")
            sendEvent(name: "rainToday", value: msg)
            sendEvent(name: "rainDisplay", value: "Today: ${msg}\nRate: ${rainRate}")
        }
        else if(hServerMsg[i].contains("{High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{High Rain Rate}=","")
            sendEvent(name: "highRainRate", value: msg)
        }
        else if(hServerMsg[i].contains("{High Wind}=")) {
        	msg = hServerMsg[i].replace("{High Wind}=","")
            sendEvent(name: "highWind", value: msg)
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
            sendEvent(name: "weeklyHigh", value: msg)
            weekDisp = "High: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Weekly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Weekly Low Temperature}=","")
            sendEvent(name: "weeklyLow", value: msg)
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
            sendEvent(name: "weeklyHighWindSpeed", value: msg)
            weekDisp = weekDisp + "High Wind: ${msg}"
            sendEvent(name: "week", value: weekDisp)
        }
        else if(hServerMsg[i].contains("{Monthly High Temperature}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Temperature}=","")
            sendEvent(name: "monthlyHigh", value: msg)
            monthDisp = "High: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Monthly Low Temperature}=","")
            sendEvent(name: "monthlyLow", value: msg)
            monthDisp = monthDisp + "Low: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly Rain Total}=")) {
        	msg = hServerMsg[i].replace("{Monthly Rain Total}=","")
            sendEvent(name: "monthlyRainTotal", value: msg)
            monthDisp = monthDisp + "Rain: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Rain Rate}=","")
            sendEvent(name: "monthlyHighRainRate", value: msg)
            monthDisp = monthDisp + "Rain Rate: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Monthly High Wind Speed}=")) {
        	msg = hServerMsg[i].replace("{Monthly High Wind Speed}=","")
            sendEvent(name: "monthlyHighWindSpeed", value: msg)
            monthDisp = monthDisp + "High Wind: ${msg}"
            sendEvent(name: "month", value: monthDisp)
        }
        else if(hServerMsg[i].contains("{Yearly High Temperature}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Temperature}=","")
            sendEvent(name: "yearlyHigh", value: msg)
            yearDisp =  "High: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly Low Temperature}=")) {
        	msg = hServerMsg[i].replace("{Yearly Low Temperature}=","")
            sendEvent(name: "yearlyLow", value: msg)
            yearDisp = yearDisp + "Low: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly Rain Total}=")) {
        	msg = hServerMsg[i].replace("{Yearly Rain Total}=","")
            sendEvent(name: "yearlyRainTotal", value: msg)
            yearDisp = yearDisp + "Rain: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly High Rain Rate}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Rain Rate}=","")
            sendEvent(name: "yearlyHighRainRate", value: msg)
            yearDisp = yearDisp + "Rain Rate: ${msg}\n"
            //sendEvent(name: "", value: msg)
        }
        else if(hServerMsg[i].contains("{Yearly High Wind Speed}=")) {
        	msg = hServerMsg[i].replace("{Yearly High Wind Speed}=","")
            sendEvent(name: "yearlyHighWindSpeed", value: msg)
            yearDisp = yearDisp + "High Wind: ${msg}"
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

def refresh() {
	log.debug "refresh()"
	poll()
}


