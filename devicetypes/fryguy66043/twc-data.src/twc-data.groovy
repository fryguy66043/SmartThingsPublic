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
 *  Date: September 10, 2020
 */

metadata {
	definition (name: "TWC Data", namespace: "FryGuy66043", author: "Jeffrey Fry", mnmn: "SmartThingsCommunity", vid: "9e190c52-ca31-39b9-905d-603f7de98f29") {
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
        capability "Refresh"

		capability "panelfaith63133.wxForecast"	
        capability "panelfaith63133.wxCurrent"
		capability "panelfaith63133.wxAlerts"
	}

	preferences {
		input "zipCode", "text", title: "Zip Code (optional)", required: false
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

		main(["temperature"])
		details(["temperature", "humidity"])
    }
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

def twcPoll() {
	log.debug "TWC: Executing 'poll', location: ${location.name}"
	// Current conditions
	def rep = getTwcConditions(zipCode)
    if (!rep) {
    	sendEvent(name: "message", value: msg + "TCW Conditions call failed...")
        log.error "TCW Conditions call failed..."
	    return
    }
    
    send(name: "wxCurrent", value: rep.encodeAsJSON())
    
    log.debug "back from TWC Conditions call..."
    def obs = rep
    if (obs.size() > 0) {
        def tempCheck = obs?.temperature
        if (tempCheck > -50 && tempCheck < 150) {
            def tempF = Math.round(tempCheck)
            send(name: "temperature", value: tempF, unit: "F")
            send(name: "humidity", value: obs.relativeHumidity as Integer, unit: "%")

            def a = getTwcForecast(zipCode)
            if (a.size() > 0) {
            	def fcast = ""
                def dow = []
                def daypart = ""
                def moon = ""
                def dcnt = 0
                def ditem = 0
                def dayOrNight = []
                def narrative = []
                def items = []
            	for (dcnt = 0; dcnt < 5; dcnt++) {
                	dow <<  '"' + a.dayOfWeek[dcnt] + '"'
                }
                
                dcnt = 0
                for (ditem = 0; ditem < 10; ditem++) {
                    dayOrNight << '"' + a.daypart[dcnt].dayOrNight[ditem] + '"'
                }                    
                daypart = '{"dayOrNight":' + "${dayOrNight}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].daypartName[ditem] + '"'
                }                    
                daypart += ',"daypartName":' + "${items}"
                
                for (ditem = 0; ditem < 10; ditem++) {
                    narrative << '"' + a.daypart[dcnt].narrative[ditem] + '"'
                }                    
                daypart += ',"narrative":' + "${narrative}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].precipChance[ditem] + '"'
                }                    
                daypart += ',"precipChance":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].precipType[ditem] + '"'
                }                    
                daypart += ',"precipType":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].qpf[ditem] + '"'
                }                    
                daypart += ',"qpf":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].qpfSnow[ditem] + '"'
                }                    
                daypart += ',"qpfSnow":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].relativeHumidity[ditem] + '"'
                }                    
                daypart += ',"relativeHumidity":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].snowRange[ditem] + '"'
                }                    
                daypart += ',"snowRange":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].temperature[ditem] + '"'
                }                    
                daypart += ',"temperature":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].temperatureHeatIndex[ditem] + '"'
                }                    
                daypart += ',"temperatureHeatIndex":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].temperatureWindChill[ditem] + '"'
                }                    
                daypart += ',"temperatureWindChill":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].windDirectionCardinal[ditem] + '"'
                }                    
                daypart += ',"windDirectionCardinal":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].windSpeed[ditem] + '"'
                }                    
                daypart += ',"windSpeed":' + "${items}"
                
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                    items << '"' + a.daypart[dcnt].wxPhraseLong[ditem] + '"'
                }                    
                daypart += ',"wxPhraseLong":' + "${items}"
                
				daypart += "}"
                
                items = []
                moon = '"moonPhase":'
                def itemStr = ""
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.moonPhase[ditem] + '"'
                }
                moon += "${items}," + '"moonPhaseDay":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.moonPhaseDay[ditem] + '"'
                }
                moon += "${items}," + '"moonriseTimeLocal":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.moonriseTimeLocal[ditem] + '"'
                }
                moon += "${items}," + '"moonsetTimeLocal":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.moonsetTimeLocal[ditem] + '"'
                }
                moon += "${items}," + '"sunriseTimeLocal":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.sunriseTimeLocal[ditem] + '"'
                }
                moon += "${items}," + '"sunsetTimeLocal":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.sunsetTimeLocal[ditem] + '"'
                }
                moon += "${items}," + '"qpf":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.qpf[ditem] + '"'
                }
                moon += "${items}," + '"qpfSnow":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.qpfSnow[ditem] + '"'
                }
                moon += "${items}," + '"temperatureMax":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.temperatureMax[ditem] + '"'
                }
                moon += "${items}," + '"temperatureMin":'
                items = []
                for (ditem = 0; ditem < 10; ditem++) {
                	items << '"' + a.temperatureMin[ditem] + '"'
                }
                moon += "${items}"                
                
                fcast = '{"dayOfWeek":' + "${dow}," + '"daypart":' + "${daypart},${moon}}"
                log.debug "fcast: " + fcast
            	sendEvent(name: "wxForecast", value: fcast)
            }
            
            // Forecast
            def f = a

            def loc = getTwcLocation(zipCode)
            def locLat = loc.location.latitude as String
            def locLong = loc.location.longitude as String

            // Alerts
            def alerts = getTwcAlerts("${locLat},${locLong}")
			
            def alertVals = '{"alerts":' + "${alerts}}"
            log.debug "wxAlerts: " + alertVals
            sendEvent(name: "wxAlerts", value: alertVals)
        }
    }
}

def refresh() {
	twcPoll()
}

def configure() {
	twcPoll()
}

private send(map) {
//	log.debug "WUSTATION: event: $map"
	sendEvent(map)
}

