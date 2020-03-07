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
 *  SmartWeather Station
 *
 *  Author: Jeffrey Fry
 *
 *  Date: July 3, 2018
 */
 
metadata {
	definition (name: "My Smartweather Tile", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Illuminance Measurement"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Ultraviolet Index"
		capability "Sensor"

		attribute "localSunrise", "string"
		attribute "localSunset", "string"
		attribute "city", "string"
        attribute "zipCode", "string"
		attribute "timeZoneOffset", "string"
		attribute "weather", "string"
		attribute "wind", "string"
		attribute "weatherIcon", "string"
		attribute "forecastIcon", "string"
		attribute "feelsLike", "string"
		attribute "percentPrecip", "string"
		attribute "alert", "string"
		attribute "alertKeys", "string"
		attribute "sunriseDate", "string"
		attribute "sunsetDate", "string"
        attribute "moonPhase", "string"
        attribute "moonRise", "string"
        attribute "moonSet", "string"
        attribute "moonRiseDate", "string"
        attribute "moonSetDate", "string"
        attribute "moonPercentIlluminated", "string"
		attribute "lastUpdate", "string"
        attribute "actualLow", "number"
        attribute "actualLowTime", "string"
        attribute "actualHigh", "number"
        attribute "actualHighTime", "string"
        
        attribute "luxValue", "string"
        attribute "shortForecast", "string"
        attribute "forecast", "string"
        attribute "forecastHighTodayF", "number"
        attribute "forecastLowTodayF", "number"
        attribute "rainLastHour", "string"
        attribute "rainToday", "string"
        attribute "rainThisMonth", "string"
        attribute "rainThisYear", "string"
        attribute "observationTime", "string"
        attribute "forecastTime", "string"
        attribute "stationID", "string"
        attribute "rainDisplay", "string"

		attribute "TWCIcons", "string"

		command "resetIconList"
		command "refresh"
        command "setActualLow"
        command "setActualHigh"
        command "setRainThisMonth"
        command "setRainThisYear"
        command "resetRefreshCnt"
	}

	preferences {
		input "zipCode", "text", title: "Zip Code (optional)", required: false
        input "pws", "text", title: "Weather Station ID (optional)", required: false
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

		valueTile("humidity", "device.humidity", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}% humidity'
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
        
		standardTile("TWCwxIcon", "device.TWCwxIcon", decoration: "flat", width: 2, height: 2) {
			state "5", icon:"st.custom.wu1.chanceflurries", label: "", default: true
			state "chancerain", icon:"st.custom.wu1.chancerain", label: ""
			state "chancesleet", icon:"st.custom.wu1.chancesleet", label: ""
			state "chancesnow", icon:"st.custom.wu1.chancesnow", label: ""
			state "chancetstorms", icon:"st.custom.wu1.chancetstorms", label: ""
			state "31", icon:"st.custom.wu1.clear", label: ""
			state "26", icon:"st.custom.wu1.cloudy", label: ""
			state "flurries", icon:"st.custom.wu1.flurries", label: ""
			state "20", icon:"st.custom.wu1.fog", label: ""
			state "hazy", icon:"st.custom.wu1.hazy", label: ""
			state "28", icon:"st.custom.wu1.mostlycloudy", label: ""
			state "mostlysunny", icon:"st.custom.wu1.mostlysunny", label: ""
			state "29", icon:"st.custom.wu1.partlycloudy", label: ""
			state "34", icon:"st.custom.wu1.partlycloudy", label: ""
			state "partlysunny", icon:"st.custom.wu1.partlysunny", label: ""
			state "11", icon:"st.custom.wu1.rain", label: ""
			state "12", icon:"st.custom.wu1.rain", label: ""
			state "28", icon:"st.custom.wu1.rain", label: ""
			state "sleet", icon:"st.custom.wu1.sleet", label: ""
			state "snow", icon:"st.custom.wu1.snow", label: ""
			state "32", icon:"st.custom.wu1.sunny", label: ""
			state "tstorms", icon:"st.custom.wu1.tstorms", label: ""
			state "cloudy", icon:"st.custom.wu1.cloudy", label: ""
			state "30", icon:"st.custom.wu1.partlycloudy", label: ""
			state "nt_chanceflurries", icon:"st.custom.wu1.nt_chanceflurries", label: ""
			state "nt_chancerain", icon:"st.custom.wu1.nt_chancerain", label: ""
			state "nt_chancesleet", icon:"st.custom.wu1.nt_chancesleet", label: ""
			state "nt_chancesnow", icon:"st.custom.wu1.nt_chancesnow", label: ""
			state "nt_chancetstorms", icon:"st.custom.wu1.nt_chancetstorms", label: ""
			state "31N", icon:"st.custom.wu1.nt_clear", label: ""
			state "33N", icon:"st.custom.wu1.nt_clear", label: ""
			state "26N", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "14N", icon:"st.custom.wu1.nt_flurries", label: ""
			state "20N", icon:"st.custom.wu1.nt_fog", label: ""
			state "nt_fog", icon:"st.custom.wu1.nt_fog", label: ""
			state "nt_hazy", icon:"st.custom.wu1.nt_hazy", label: ""
			state "27N", icon:"st.custom.wu1.nt_mostlycloudy", label: ""
			state "nt_mostlysunny", icon:"st.custom.wu1.nt_mostlysunny", label: ""
			state "nt_partlycloudy", icon:"st.custom.wu1.nt_partlycloudy", label: ""
			state "nt_partlysunny", icon:"st.custom.wu1.nt_partlysunny", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "11N", icon:"st.custom.wu1.nt_rain", label: ""
			state "12N", icon:"st.custom.wu1.nt_rain", label: ""
			state "27N", icon:"st.custom.wu1.nt_rain", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "16N", icon:"st.custom.wu1.nt_snow", label: ""
			state "nt_sunny", icon:"st.custom.wu1.nt_sunny", label: ""
			state "4N", icon:"st.custom.wu1.nt_tstorms", label: ""
			state "nt_cloudy", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "29N", icon:"st.custom.wu1.nt_partlycloudy", label: ""
		}
        
		valueTile("feelsLike", "device.feelsLike", decoration: "flat", width: 2, height: 2) {
			state "default", label:'feels like ${currentValue}°'
		}

		valueTile("wind", "device.wind", decoration: "flat", width: 2, height: 2) {
			state "default", label:'wind ${currentValue} mph'
		}

		valueTile("actual", "device.actual", decortion: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}'
        }

		valueTile("weather", "device.weather", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}'
		}

		valueTile("city", "device.city", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}'
		}

		valueTile("percentPrecip", "device.percentPrecip", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue}% precip'
		}

//		valueTile("ultravioletIndex", "device.ultravioletIndex", decoration: "flat") {
//			state "default", label:'${currentValue} UV index'
//		}
//		valueTile("rainToday", "device.rainToday", decoration: "flat") {
//			state "default", label:'Rain Today:\n${currentValue} inches'
//		}
		valueTile("rainDisplay", "device.rainDisplay", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Precip:\n${currentValue}'
		}

		valueTile("alert", "device.alert", width: 4, height: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
		valueTile("forecast", "device.forecast", width: 6, height: 5, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        
		standardTile("refresh", "device.weather", decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}

		valueTile("rise", "device.localSunrise", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Sunrise ${currentValue}'
		}

		valueTile("set", "device.localSunset", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Sunset ${currentValue}'
		}

		valueTile("light", "device.illuminance", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue} lux'
		}
        
        valueTile("moon", "device.moon", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}'
        }
        
		valueTile("lastUpdate", "device.lastUpdate", width: 6, height: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("message", "device.message", width: 6, height: 2, decoration: "flat") {
        	state "default", label: '${currentValue}'
        }
        
        standardTile("resetIcons", "device.resetIcons", width: 2, height: 2, decoration: "flat") {
        	state "default", label: 'Reset Icons', action: "resetIconList"
        }

		main(["temperature"])
		details(["temperature", "humidity", "TWCwxIcon", "feelsLike", "wind", "weather", "actual", "city", "percentPrecip", "rainDisplay", "forecast", "alert", "refresh", "rise", "set", "moon", "lastUpdate", "message", "resetIcons"])}
//		details(["temperature", "humidity", "weatherIcon", "feelsLike", "wind", "weather", "actual", "city", "percentPrecip", "rainDisplay", "forecast", "alert", "refresh", "rise", "set", "light", "moon", "lastUpdate", "message"])}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def installed() {
	twcPoll()
	runEvery10Minutes(twcPoll)
}

def uninstalled() {
	unschedule()
}

// handle commands

def resetIconList() {
	log.debug "resetIconList"
    // Only using to help collect missing TWC Icons
    sendEvent(name: "TWCIcons", value: "")
}

def setRainThisMonth(val) {
	log.debug "setRainThisMonth($val)"
    sendEvent(name: "rainThisMonth", value: val)
}

def setRainThisYear(val) {
	log.debug "setRainThisYear($val)"
    sendEvent(name: "rainThisYear", value: val)
}

def setActualLow(val) {
	log.debug "setActualLow(${val})"
    def time = new Date().format("h:mm a", location.timeZone)
    
    if (val) {
    	def high = (device.currentValue("actualHigh")) ? device.currentValue("actualHigh") : -99
        def highTime = (device.currentValue("actualHighTime")) ? device.currentValue("actualHighTime") : "N/A"
    	def disp = "Low: ${val}° ${time}\nHigh: ${high}° ${highTime}"
    	sendEvent(name: "actualLow", value: val)
        sendEvent(name: "actualLowTime", value: time)
        sendEvent(name: "actual", value: disp)
    }
}

def setActualHigh(val) {
	log.debug "setActualHigh(${val})"
    def time = new Date().format("h:mm a", location.timeZone)
    
    if (val) {
    	def low = (device.currentValue("actualLow")) ? device.currentValue("actualLow") : 99
        def lowTime = (device.currentValue("actualLowTime")) ? device.currentValue("actualLowTime") : "N/A"
    	def disp = "Low: ${low}° ${lowTime}\nHigh: ${val}° ${time}"
    	sendEvent(name: "actualHigh", value: val)
        sendEvent(name: "actualHighTime", value: time)
        sendEvent(name: "actual", value: disp)
    }
}



def twcPoll() {
	log.debug "TWC: Executing 'poll', location: ${location.name}"
	def date = new Date().format("MMM dd, h:mm a z")
    def msgDate = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def month = new Date().format("MM", location.timeZone)
    def year = new Date().format("YYYY", location.timeZone)
    def msg = "${location} ${msgDate}: Starting poll()..."
    sendEvent(name: "message", value: msg)
    state.failCnt = state.failCnt ?: 0
    
    msg = "${location} ${msgDate}\n"
    
    state.failCnt = 0
    
	// Current conditions
	def rep = getTwcConditions(zipCode)
    if (!rep) {
    	sendEvent(name: "message", value: msg + "TCW Conditions call failed...")
        log.debug "TCW Conditions call failed..."
	    return
    }
    
    log.debug "back from TWC Conditions call..."
    def obs = rep
    if (obs.size() > 0) {
        def tempCheck = obs?.temperature
        if (tempCheck > -50 && tempCheck < 150) {
            // Last update time stamp
            def tDate = obs.validTimeLocal
            tDate = tDate.replace("T", " ")
            tDate = tDate.replace("-0500", "")
            tDate = tDate.replace("-0600", "")
            def obsDate = Date.parse("yyyy-MM-dd H:mm:ss", tDate)
            def obsTime = obsDate.format("MM/dd/yy h:mm a", location.timeZone)
            def timeStamp = "${new Date().format("MM/dd/yy h:mm a", location.timeZone)}\n(${obsTime})"
            sendEvent(name: "lastUpdate", value: timeStamp)

//            def weatherIcon = obs.iconCodeExtend as String
            def weatherIcon = obs.iconCode as String

			log.debug "wxIcon = ${weatherIcon}"
            if (obs.dayOrNight == "N") {
            	weatherIcon += "N"
            }
            
            def tempF = Math.round(tempCheck)

            send(name: "temperature", value: tempF, unit: "F")
            send(name: "feelsLike", value: Math.round(obs.temperatureFeelsLike as Double), unit: "F")
            def nowTime = new Date().format("HH:mm:ss", location.timeZone)
            def actLow = 99
            def actHigh = -99
            if (nowTime >= "00:00:00" && nowTime <= "00:10:59") {
                setActualLow(99)
                setActualHigh(-99)
            }
            else {
                actLow = device.currentValue("actualLow") ?: 99
                actHigh = device.currentValue("actualHigh") ?: -99
            }
            if (tempF < actLow) {
                setActualLow(tempF)
            }
            if (tempF > actHigh) {
                setActualHigh(tempF)
            }

            send(name: "humidity", value: obs.relativeHumidity as Integer, unit: "%")
            send(name: "weather", value: obs.wxPhraseLong)
            send(name: "TWCwxIcon", value: weatherIcon, displayed: false)
            send(name: "wind", value: Math.round(obs.windSpeed) as String, unit: "MPH") // as String because of bug in determining state change of 0 numbers
			log.debug "TWCwxIcon = ${device.currentValue("TWCwxIcon")}"

			def strIcons = device.currentValue("TWCIcons") ? device.currentValue("TWCIcons") : ""
//            if (strIcons) {
                if (!strIcons.contains(weatherIcon)) {
                    strIcons += "[${weatherIcon} : ${obs.dayOrNight}-${obs.wxPhraseLong}]"
                    sendEvent(name: "TWCIcons", value: strIcons)
                }
            	log.debug "TWCIcons = ${device.currentValue("TWCIcons")}"
//            }
            
            send(name: "zipCode", value: zipCode)

            send(name: "ultravioletIndex", value: Math.round(obs.uvIndex as Double))
            def rToday = obs.precip24Hour //Float.parseFloat(obs.precip24Hour)
            if (rToday < 0) {
                rToday = 0.0
            }
            def rLastHr = obs.precip1Hour //Float.parseFloat(obs.precip1Hour)
            if (rLastHr < 0) {
                rLastHr = 0.0
            }
            def rainMonth = device.currentValue("rainThisMonth") > "0.0" ? device.currentValue("rainThisMonth") : "0.0"
            def rainYear = device.currentValue("rainThisYear") > "0.0" ? device.currentValue("rainThisYear") : "0.0"
            send(name: "rainToday", value: rToday)
            send(name: "rainLastHour", value: rLastHr)
            def rainDisp = "Day: ${rToday}\"\nHr: ${rLastHr}\""
//            def rainDisp = "Day: ${rToday}\"\nMon: ${rainMonth}\"\nYr: ${rainYear}\""
            send(name: "rainDisplay", value: rainDisp)

            // Sunrise / Sunset
//            log.debug "obs = $obs"
            def strDate = obs.sunriseTimeLocal
            strDate = strDate.replace("T", " ")
            strDate = strDate.replace("-0500", "")
            strDate = strDate.replace("-0600", "")
            def sunriseDate = Date.parse("yyyy-MM-dd HH:mm:ss", strDate)
            def localSunrise = sunriseDate.format("h:mm a")
            send(name: "localSunrise", value: localSunrise, descriptionText: "Sunrise today is at $localSunrise")
            
            strDate = obs.sunsetTimeLocal
            strDate = strDate.replace("T", " ")
            strDate = strDate.replace("-0500", "")
            strDate = strDate.replace("-0600", "")
            def sunsetDate = Date.parse("yyyy-MM-dd HH:mm:ss", strDate)
            def localSunset = sunsetDate.format("h:mm a")
            send(name: "localSunset", value: localSunset, descriptionText: "Sunset today is at $localSunset")
            
            def a = getTwcForecast(zipCode)
			strDate = a.moonriseTimeLocal[0]
            if (!strDate) {
            	strDate = a.moonriseTimeLocal[1]
            }
            strDate = strDate.replace("T", " ")
            strDate = strDate.replace("-0500", "")
            strDate = strDate.replace("-0600", "")
            log.debug "moonriseDate = $strDate"
//            log.debug "a = $a"
            def moonriseDate = Date.parse("yyyy-MM-dd HH:mm:ss", strDate)
            def localMoonrise = moonriseDate.format("EEE h:mm a")

			strDate = a.moonsetTimeLocal[0]
            if (!strDate) {
            	strDate = a.moonsetTimeLocal[1]
            }
            strDate = strDate.replace("T", " ")
            strDate = strDate.replace("-0500", "")
            strDate = strDate.replace("-0600", "")
            log.debug "moonsetDate = $strDate"
            def moonsetDate = Date.parse("yyyy-MM-dd HH:mm:ss", strDate)
            def localMoonset = moonsetDate.format("EEE h:mm a")

			if (moonsetDate < moonriseDate) {
            	log.debug "Fixing moonrise..."
                strDate = a.moonsetTimeLocal[1]
                if (!strDate) {
                    strDate = a.moonsetTimeLocal[2]
                }
                strDate = strDate.replace("T", " ")
                strDate = strDate.replace("-0500", "")
                strDate = strDate.replace("-0600", "")
                log.debug "moonsetDate = $strDate"
                moonsetDate = Date.parse("yyyy-MM-dd HH:mm:ss", strDate)
                localMoonset = moonsetDate.format("EEE h:mm a")
            }
            
            send(name: "moonRiseDate", value: moonriseDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), descriptionText: "Moonrise Date")
            send(name: "moonSetDate", value: moonsetDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), descriptionText: "Moonset Date")
            log.debug "moonRiseDate: ${moonriseDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")} / moonSetDate: ${moonsetDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}"
            
            def localMoonPhase = a.moonPhase[0]

            send(name: "moonRise", value: localMoonrise, descriptionText: "Moonrise today is at $localMoonrise")
            send(name: "moonSet", value: localMoonset, descriptionText: "Moonset today is at $localMoonset")
            send(name: "moonPhase", value: localMoonPhase, descriptionText: "Current moon phase: $localMoonPhase")

            sendEvent(name: "moon", value: "Moon Phase: ${localMoonPhase}\nRise: ${localMoonrise}\nSet: ${localMoonset}")

/*
			log.debug "***** estimated lux value"
            def luxVal = estimateLux(sunriseDate, sunsetDate, weatherIcon)
            send(name: "illuminance", value: luxVal)
            send(name: "luxValue", value: luxVal)
*/

            // Forecast
            def f = a

			def forecastHigh = f.temperatureMax[0]
            def forecastLow = f.temperatureMin[0]
            def i = 0
            log.debug "f.daypart[0].daypartName[0] = ${f.daypart[0].daypartName[0]}"
            if (f.daypart[0].daypartName[0] == null) {
            	i = 1
            }
            def value = f.daypart[0].precipChance[i]
            def icon = f.daypart[0].iconCodeExtend[i]
            def shortForecastDisp = f.daypart[0].daypartName[i] + ": " + f.daypart[0].wxPhraseLong[i]
            def forecastDisp = f.daypart[0].daypartName[i] + ": " + f.daypart[0].narrative[i]
            forecastDisp += "  " + f.daypart[0].daypartName[i+1] + ": " + f.daypart[0].narrative[i+1]
//			log.debug "shortForecast = ${shortForecastDisp}"
//            log.debug "longForecast = ${forecastDisp}"
//            log.debug "f = ${f.daypart}"

            send(name: "percentPrecip", value: value, unit: "%")
            send(name: "forecastIcon", value: icon, displayed: false)
            send(name: "forecastHighTodayF", value: forecastHigh)
            send(name: "forecastLowTodayF", value: forecastLow)
            send(name: "shortForecast", value: shortForecastDisp)
            send(name: "forecast", value: forecastDisp)
            
            //log.debug "forecast = ${f}"
            def loc = getTwcLocation(zipCode)
            //log.debug "loc = ${loc}"
            def locLat = loc.location.latitude as String
            log.debug "latitude = ${locLat}"
            def locLong = loc.location.longitude as String
            log.debug "longitude = ${locLong}" 


            def cityValue = "${loc.location.city}, ${loc.location.adminDistrictCode}"
            if (cityValue != device.currentValue("city")) {
                send(name: "city", value: cityValue, isStateChange: true)
            }
            send(name: "zipCode", value: zipCode)


            // Alerts
            def alerts = getTwcAlerts("${locLat},${locLong}")
//            alerts = alerts?.alerts
			log.debug "Nbr Alerts = ${alerts.size()}"
//            log.debug "alerts = ${alerts}"
			def newKeys = []
            def alertMsg = ""
			for (i = 0; i < alerts.size(); i++) {
            	if (!newKeys.contains(alerts[i].eventDescription)) {
                    newKeys.add(alerts[i].eventDescription)
                    alertMsg += "[${alerts[i].eventDescription}]\n"
                }
                else {
                	log.debug "Duplicate alert found..."
                }
            }
    		log.debug "WUSTATION: newKeys = $newKeys / alertMsg = $alertMsg"
            
            def oldKeys = device.currentState("alertKeys")?.jsonValue

            def noneString = "no current weather alerts"
            if (!newKeys && oldKeys == null) {
            	log.debug "path 1..."
                send(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)
                send(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
            }
            else if (newKeys != oldKeys) {
            	log.debug "path 2..."
                if (oldKeys == null) {
                    oldKeys = []
                }
                send(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)
				send(name: "alert", value: alertMsg, description: "Alerts", isStateChange: true)
/*
                def newAlerts = false
                def alertMsg = ""
                log.debug "alerts.size() = ${alerts.size()}"
                alerts.each {alert ->
//                    if (!oldKeys.contains(alert.eventDescription)) {
                        alertMsg += "${alert.eventDescription}\n"
                        log.debug "alrtMsg = ${alertMsg}"
//                        send(name: "alert", value: pad(alert.eventDescription), descriptionText: alertMsg, isStateChange: true)
                        newAlerts = true
//                    }
                }
                if (alertMsg) {
                    send(name: "alert", value: pad(alert.eventDescription), descriptionText: alertMsg, isStateChange: true)
                }

                if (!newAlerts && device.currentValue("alert") != noneString) {
                    send(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
                }
*/                
            }
            else {
            	log.debug "path 3..."
            }
        }
        else {
            if (!obs) {
                log.warn "No response from TWC API"
            }
            if (!obsTime) {
                log.warn "Old data returned from TWC API"
            }
        }
    }
    else {
    	sendEvent(name: "message", value: msg + "Failure loading observations...")
        log.debug "TCW Observations call failed..."
	    return
    }
    sendEvent(name: "message", value: msg)
}

def poll() {
	log.debug "WUSTATION: Executing 'poll', location: ${location.name}"
	def date = new Date().format("MMM dd, h:mm a z")
    def msgDate = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def month = new Date().format("MM", location.timeZone)
    def year = new Date().format("YYYY", location.timeZone)
    def msg = "${location} ${msgDate}: Starting poll()..."
    sendEvent(name: "message", value: msg)
    state.failCnt = state.failCnt ?: 0
    
    msg = "${location} ${msgDate}: "
    
    state.failCnt = 0
    
	// Current conditions
	def rep = get("conditions")
    log.debug "back from WU call..."
//    log.debug "rep = ${rep} state.failCnt = ${state.failCnt}"
    def obs = ""
    if (rep == "") {
    	log.debug "WU Failure!"
        msg = msg + "WU Call Failed!"
    }
    else {
    	log.debug "WU Success!"
    	obs = rep?.current_observation
        if (!obs) {
        	log.debug "! obs failure! rep: ${rep}"
		}
//    }
//        log.debug "obs = ${obs}"
        def obsTime = obs?.observation_time
        def lastObsTime = device.currentValue("observationTime") ?: obsTime
        def obsDateTime = obsTime.replace("Last Updated on ", "")
        def obsDate = Date.parse("MMM dd, h:mm a z", obsDateTime)
        def obsMonth = obsDate.format("MM")
        def curDate = Date.parse("MMM dd, h:mm a z", date)
        log.debug "obsmonth = ${obsMonth} current month = ${month}"

        def lastObsDateTime = lastObsTime?.contains("Last Updated") ? lastObsTime.replace("Last Updated on ", "") : lastObsTime
        def lastObsDate = Date?.parse("MMM dd, h:mm a z", lastObsDateTime) ?: obsDate
        log.debug "obsTime = ${obsTime} / lastObsTime = ${lastObsTime}"
        log.debug "obsDateTime = ${obsDateTime} / lastObsDateTime = ${lastObsDateTime}"
    //    log.debug "obsDate = ${obsDate.format("MM/dd/yy h:mm a", location.timeZone)} / lastObsDate = ${lastObsDate.format("MM/dd/yy h:mm a", location.timeZone)}"
        log.debug "obsDate = ${obsDate} / lastObsDate = ${lastObsDate} / curDate = ${curDate}"
    //    log.debug "curDate = ${curDate}"
        if (obsDate < lastObsDate || obsDate < curDate-1 || obsDate > curDate) {
    //    if (obsDate < lastObsDate || obsDate < curDate-1 || obsDate > curDate) {
            state.failCnt = state.failCnt ? state.failCnt + 1 : 1
            obsTime = ""
            log.debug "Observation Time failure... state.failCnt = ${state.failCnt}"
            msg = msg + "WU returned old data!  Returned data from ${obsDateTime}. Last update from ${lastObsDateTime}"
            runIn(60, refresh)
        }
        else {
            log.debug "state.failCnt = ${state.failCnt}"
            state.failCnt = 0
            log.debug "Observation Time passed..."
            sendEvent(name: "observationTime", value: obsTime)
            msg = msg + "WU call successful! Returned data from ${obsDateTime}. Last update from ${lastObsDateTime}"
        }
        def tempCheck = obs?.temp_f
        if (obs && tempCheck > -50 && tempCheck < 150 && obsTime) {
            // Last update time stamp
            def timeStamp = "${new Date().format("MM/yy/dd h:mm a", location.timeZone)}\n(${obs.station_id})\n${obsTime}"
            sendEvent(name: "lastUpdate", value: timeStamp)
            sendEvent(name: "stationID", value: obs.station_id)

            def weatherIcon = obs.icon_url.split("/")[-1].split("\\.")[0]
            def tempF = Math.round(obs.temp_f)

            if(getTemperatureScale() == "C") {
                send(name: "temperature", value: Math.round(obs.temp_c), unit: "C")
                send(name: "feelsLike", value: Math.round(obs.feelslike_c as Double), unit: "C")
            } else {
                send(name: "temperature", value: tempF, unit: "F")
                send(name: "feelsLike", value: Math.round(obs.feelslike_f as Double), unit: "F")
                def nowTime = new Date().format("HH:mm:ss", location.timeZone)
                log.debug "nowTime = ${nowTime}"
                def actLow = 99
                def actHigh = -99
                if (nowTime >= "00:00:00" && nowTime <= "00:10:59") {
                    setActualLow(99)
                    setActualHigh(-99)
                }
                else {
                    actLow = device.currentValue("actualLow") ?: 99
                    actHigh = device.currentValue("actualHigh") ?: -99
                }
                if (tempF < actLow) {
                    setActualLow(tempF)
                }
                if (tempF > actHigh) {
                    setActualHigh(tempF)
                }
            }

            send(name: "humidity", value: obs.relative_humidity[0..-2] as Integer, unit: "%")
            send(name: "weather", value: obs.weather)
            send(name: "weatherIcon", value: weatherIcon, displayed: false)
            send(name: "wind", value: Math.round(obs.wind_mph) as String, unit: "MPH") // as String because of bug in determining state change of 0 numbers

            if (obs.local_tz_offset != device.currentValue("timeZoneOffset")) {
                send(name: "timeZoneOffset", value: obs.local_tz_offset, isStateChange: true)
            }

            def cityValue = "${obs.display_location.city}, ${obs.display_location.state}"
            if (cityValue != device.currentValue("city")) {
                send(name: "city", value: cityValue, isStateChange: true)
            }
            send(name: "zipCode", value: zipCode)

            send(name: "ultravioletIndex", value: Math.round(obs.UV as Double))
            def rToday = Float.parseFloat(obs.precip_today_in)
            if (rToday < 0) {
                rToday = 0.0
            }
            def rLastHr = Float.parseFloat(obs.precip_1hr_in)
            if (rLastHr < 0) {
                rLastHr = 0.0
            }
            def rainMonth = device.currentValue("rainThisMonth") > "0.0" ? device.currentValue("rainThisMonth") : "0.0"
            def rainYear = device.currentValue("rainThisYear") > "0.0" ? device.currentValue("rainThisYear") : "0.0"
            send(name: "rainToday", value: rToday)
            send(name: "rainLastHour", value: rLastHr)
//            def rainDisp = "Day: ${rToday}\"\nHr: ${rLastHr}\""
            def rainDisp = "Day: ${rToday}\"\nMon: ${rainMonth}\"\nYr: ${rainYear}\""
            send(name: "rainDisplay", value: rainDisp)

            // Sunrise / Sunset
            def a = get("astronomy")?.moon_phase
            def today = localDate("GMT${obs.local_tz_offset}")
            def ltf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
            ltf.setTimeZone(TimeZone.getTimeZone("GMT${obs.local_tz_offset}"))
            def utf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            utf.setTimeZone(TimeZone.getTimeZone("GMT"))

			log.debug "a = $a / today = $today"
            def sunriseDate = ltf.parse("${today} ${a.sunrise.hour}:${a.sunrise.minute}")
            def sunsetDate = ltf.parse("${today} ${a.sunset.hour}:${a.sunset.minute}")
            def tf = new java.text.SimpleDateFormat("h:mm a")
            tf.setTimeZone(TimeZone.getTimeZone("GMT${obs.local_tz_offset}"))
            def localSunrise = "${tf.format(sunriseDate)}"
            def localSunset = "${tf.format(sunsetDate)}"
            send(name: "localSunrise", value: localSunrise, descriptionText: "Sunrise today is at $localSunrise")
            send(name: "localSunset", value: localSunset, descriptionText: "Sunset today is at $localSunset")
            
            if (!a.moonrise.hour || !a.moonset.hour) {
            	log.debug "moon failure!"
            }
            else {
                def moonriseDate = ltf.parse("${today} ${a.moonrise.hour}:${a.moonrise.minute}")
                def moonsetDate = ltf.parse("${today} ${a.moonset.hour}:${a.moonset.minute}")

                def localMoonrise = "${tf.format(moonriseDate)}"
                def localMoonset = "${tf.format(moonsetDate)}"

                def localMoonPhase = a.phaseofMoon
                def localMoonIllumination = a.percentIlluminated

                send(name: "moonRise", value: localMoonrise, descriptionText: "Moonrise today is at $localMoonrise")
                send(name: "moonSet", value: localMoonset, descriptionText: "Moonset today is at $localMoonset")
                send(name: "moonPercentIlluminated", value: localMoonIllumination, descriptionText: "Percent moon illumination: $localMoonIllumination")
                send(name: "moonPhase", value: localMoonPhase, descriptionText: "Current moon phase: $localMoonPhase")

                sendEvent(name: "moon", value: "Moon Phase: ${localMoonPhase} ${localMoonIllumination}% Illuminated\nRise: ${localMoonrise} Set: ${localMoonset}")
            }

			log.debug "***** estimated lux value"
            def luxVal = estimateLux(sunriseDate, sunsetDate, weatherIcon)
            send(name: "illuminance", value: luxVal)
            send(name: "luxValue", value: luxVal)

            // Forecast
            def f = get("forecast")
    //        log.debug "f = ${f}"
            def fTime = f?.forecast?.txt_forecast?.date ?: ""
            def fDate = "${new Date().format("MM/dd/yy")} ${fTime}"
            def fDateTime = Date?.parse("MM/dd/yy h:mm a z", fDate)
            def lastfTime = device.currentValue("forecastTime") ?: fTime
            def lastfDate = "${new Date().format("MM/dd/yy")} ${lastfTime}"
            def lastfDateTime = Date?.parse("MM/dd/yy h:mm a z", lastfDate)
            log.debug "fTime = ${fTime}: fDate = ${fDate} / lastfDate = ${lastfDate}"
            if (fDateTime < lastfDateTime) {
                log.debug "Forecast time failure..."
                fTime = ""
                msg = "${location} ${msgDate}: WU Forecast returned old data!  Returned data for ${fDateTime}.  Last update for ${lastfDateTime}"
            }
            else {
                log.debug "Forecast time passed..."
            }

            def f1 = f?.forecast?.simpleforecast?.forecastday
    //        log.debug "f1 = ${f1}"
            log.debug "f1?.size() = ${f1?.size()} / fTime?.size() = ${fTime?.size()}"
            if (f1?.size() > 0 && fTime?.size() > 0) {
                def icon = f1[0].icon_url.split("/")[-1].split("\\.")[0]
                def value = f1[0].pop as String // as String because of bug in determining state change of 0 numbers
                def forecastHigh = f1[0].high.fahrenheit
                def forecastLow = f1[0].low.fahrenheit
                log.debug "forecastHigh = ${forecastHigh} / forecastLow = ${forecastLow}"
                send(name: "percentPrecip", value: value, unit: "%")
                send(name: "forecastIcon", value: icon, displayed: false)
                send(name: "forecastHighTodayF", value: forecastHigh)
                send(name: "forecastLowTodayF", value: forecastLow)
            }
            else {
                send(name: "precentPrecip", value: "Unknown")
                send(name: "forecastIcon", value: "???")
                log.warn "Forecast not found"
            }
            def f2 = f?.forecast?.txt_forecast?.forecastday
            if (f2 && fTime) {
                def shortForecastDisp = "${f2[0].title}: ${f2[0].fcttext}"
                def forecastDisp = "${f2[0].title}: ${f2[0].fcttext} ${f2[1].title}: ${f2[1].fcttext} ${f2[2].title}: ${f2[2].fcttext}"
                send(name: "shortForecast", value: shortForecastDisp)
                send(name: "forecast", value: forecastDisp)
            }
            else {
                log.warn "Forecast not found"
            }

            // Alerts
            def alerts = get("alerts")?.alerts
            def newKeys = alerts?.collect{it.type + it.date_epoch} ?: []
    //		log.debug "WUSTATION: newKeys = $newKeys"
    //		log.trace device.currentState("alertKeys")
            def oldKeys = device.currentState("alertKeys")?.jsonValue
    //		log.debug "WUSTATION: oldKeys = $oldKeys"

            def noneString = "no current weather alerts"
            if (!newKeys && oldKeys == null) {
                send(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)
                send(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
            }
            else if (newKeys != oldKeys) {
                if (oldKeys == null) {
                    oldKeys = []
                }
                send(name: "alertKeys", value: newKeys.encodeAsJSON(), displayed: false)

                def newAlerts = false
                alerts.each {alert ->
                    if (!oldKeys.contains(alert.type + alert.date_epoch)) {
                        def alertMsg = "${alert.description} from ${alert.date} until ${alert.expires}"
                        send(name: "alert", value: pad(alert.description), descriptionText: alertMsg, isStateChange: true)
                        newAlerts = true
                    }
                }

                if (!newAlerts && device.currentValue("alert") != noneString) {
                    send(name: "alert", value: noneString, descriptionText: "${device.displayName} has no current weather alerts", isStateChange: true)
                }
            }
        }
        else {
            if (!obs) {
                log.warn "No response from Weather Underground API"
            }
            if (!obsTime) {
                log.warn "Old data returned from Weather Underground API"
            }
        }
    }
    sendEvent(name: "message", value: msg)
}

def refresh() {
//	setActualLow(99)
//    setActualHigh(-99)

	runEvery10Minutes(twcPoll)

	state.refreshCnt = state.refreshCnt ? state.refreshCnt + 1 : 1
    if (state.refreshCnt < 3) {
		twcPoll()
    }
    else {
    	log.debug "Need to reset data to start over..."
    }
    runIn(5, resetRefreshCnt)
}

def resetRefreshCnt() {
	log.debug "resetRefreshCnt: state.refreshCnt = ${state.refreshCnt}"
    state.refreshCnt = 0
}

def configure() {
	twcPoll()
}

private pad(String s, size = 25) {
	if (!s) {
    return null
    }
	def n = (size - s.size()) / 2
	if (n > 0) {
		def sb = ""
		n.times {sb += " "}
		sb += s
		n.times {sb += " "}
		return sb
	}
	else {
		return s
	}
}

private get(feature) {
//	log.debug "feature = ${feature}"
//    log.debug "zipCode = ${zipCode} / pws = ${pws}"
	def options = zipCode
    def results = ""
	if (feature == "conditions") { 
    	if (pws && state.failCnt < 4) {
        	def pwsString = pws
        	if (!pws.contains("pws:")) {
            	log.debug "pws missing 'pws:' prefix - Adding..."
            	pwsString = "pws:${pws}"
            }
            log.debug "pwsString = ${pwsString}"
        	options = pwsString
        }
        else {
        	if (state.failCnt > 3) {
                log.debug "failCnt > 3: ${state.failCnt}"
                options = ""
            }
        }
    }
    log.debug "${feature} / ${options}"
    if (state.failCnt < 1000) {
    	log.debug "state.failCnt = ${state.failCnt}"
	    results = getWeatherFeature(feature, options)
    }
    else {
    	log.debug "Failed too many times.  Stopped calling"
        results = ""
    }
//    log.debug "${feature}: ${results}"
    return results
}

private localDate(timeZone) {
	def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
	df.setTimeZone(TimeZone.getTimeZone(timeZone))
	df.format(new Date())
}

private send(map) {
//	log.debug "WUSTATION: event: $map"
	sendEvent(map)
}

private estimateLux(sunriseDate, sunsetDate, weatherIcon) {
	log.debug "***** estimateLux(${sunriseDate}, ${sunsetDate}, ${weathericon})"
	def oneHour = 1000 * 60 * 60
    def halfHour = oneHour / 2
	def firstLightDisplay = ""
    def lastLightDisplay = ""
	use(groovy.time.TimeCategory) {
    	def fLD = new Date()
        def lLD = new Date()
    	fLD = sunriseDate - 30.minutes
        firstLightDisplay = fLD.format("h:mm a", location.timeZone)
        lLD = sunsetDate + 30.minutes
        lastLightDisplay = lLD.format("h:mm a", location.timeZone)
    }    
    def firstLightTime = sunriseDate.time - halfHour
    def lastLightTime = sunsetDate.time + halfHour
    def nowDisplay = new Date().format("h:mm a", location.timeZone)
//    log.debug "FL = ${firstLightDisplay} / Now = ${nowDisplay} / LL = ${lastLightDisplay}"
    
    def lux = 0
	def now = new Date().time
	if (now > firstLightTime && now < lastLightTime) {
		//day
		switch(weatherIcon) {
			case 'tstorms':
//				lux = 200
				lux = 500
				break
			case ['cloudy', 'fog', 'rain', 'sleet', 'snow', 'flurries',
				'chanceflurries', 'chancerain', 'chancesleet',
				'chancesnow', 'chancetstorms']:
//				lux = 1000
				lux = 2500
				break
			case 'mostlycloudy':
//				lux = 2500
				lux = 5000
				break
			case ['partlysunny', 'partlycloudy', 'hazy']:
				lux = 7500
				break
			default:
				//sunny, clear
				lux = 10000
		}

		//adjust for dusk/dawn
		def afterSunrise = now - firstLightTime
		def beforeSunset = lastLightTime - now
/*		
        log.debug "now = ${now}"
        log.debug "afterSunrise == ${afterSunrise}"
        log.debug "beforeSunset == ${beforeSunset}"
        log.debug "afterSunrise < 1.5HR = ${afterSunrise < (oneHour + halfHour)}"
        log.debug "beforeSunset < 1.5HR = ${beforeSunset < (oneHour + halfHour)}"
        log.debug "1.5HR == ${oneHour + halfHour}"
        log.debug "afterSunrise/(oneHour + halfHour) == ${afterSunrise/(oneHour + halfHour)}"
*/
		if(afterSunrise < (oneHour + halfHour)) {
			//dawn
			lux = (long)(lux * (afterSunrise/(oneHour + halfHour)))
//            log.debug "afterSunrise Lux == ${lux}"
		} else if (beforeSunset < (oneHour + halfHour)) {
			//dusk
			lux = (long)(lux * (beforeSunset/(oneHour + halfHour)))
//            log.debug "beforeSunset Lux == ${lux}"
		}
	}
	else {
		//night - always set to 10 for now
		//could do calculations for dusk/dawn too
		lux = 10
	}

//	log.debug "lux = ${lux}"
	lux
}