/**
 *  Tracking Monitor
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
    name: "Tracking Monitor",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Uses virtual switches to trigger arrival and departure from different locations.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Home/home4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Home/home4-icn@2x.png")

preferences {
	section("Select Tracking Sensor") {
    	input "trackingSensor", "device.myTrackingSensor", required: true, title: "Select the tracking sensor."
    }
	section("Name of Person to monitor?") {
    	input "personName", "text", required: true, multiple: false, title: "Which Person do you want to monitor?"
    }
	section("Home Location...") {
    	input "homeSwitch", "capability.switch", title: "Which Switch will represent Home?"
    }
    section("Location 2...") {
    	input "loc2Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 3...") {
    	input "loc3Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 4...") {
    	input "loc4Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 5...") {
    	input "loc5Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 6...") {
    	input "loc6Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 7...") {
    	input "loc7Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 8...") {
    	input "loc8Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 9...") {
    	input "loc9Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Location 10...") {
    	input "loc10Switch", "capability.switch", required: false, title: "Which Switch will represent this location?"
    }
    section("Reset tracking at what time?") {
    	input "resetTime", "time", title: "What time do you want to reset tracking data for the day?"
    }
    section("Erase Last Entry Switch") {
    	input "eraseSwitch", "capability.switch", required: false, title: "Which Switch will erase the last tracking entry?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when location changes?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false, title: "Send a Text Notification when location changes?"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
    
    state.trackingDays = ["","","","","","",""]
	state.trackingList = []
    state.trackingTime = []
    state.trackingListLastMonth = []
    state.trackingTimeLastMonth = []
    state.trackingDayIndex = 0
    resetHandler()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {    
//	reset()

	state.erase = false
    subscribe(app, appHandler)
    subscribe(homeSwitch, "switch", switchHandler)
    subscribe(trackingSensor, "reportRequest", reportHandler)
    subscribe(eraseSwitch, "switch.on", eraseHandler)
    schedule(resetTime, resetHandler)
    if (loc2Switch) {
    	subscribe(loc2Switch, "switch", switchHandler)
    }
    if (loc3Switch) {
    	subscribe(loc3Switch, "switch", switchHandler)
    }
    if (loc4Switch) {
    	subscribe(loc4Switch, "switch", switchHandler)
    }
    if (loc5Switch) {
    	subscribe(loc5Switch, "switch", switchHandler)
    }
    if (loc6Switch) {
    	subscribe(loc6Switch, "switch", switchHandler)
    }
    if (loc7Switch) {
    	subscribe(loc7Switch, "switch", switchHandler)
    }
    if (loc8Switch) {
    	subscribe(loc8Switch, "switch", switchHandler)
    }
    if (loc9Switch) {
    	subscribe(loc9Switch, "switch", switchHandler)
    }
    if (loc10Switch) {
    	subscribe(loc10Switch, "switch", switchHandler)
    }
}

private reset() {
    state.trackingDays = ["","","","","","",""]
	state.trackingList = []
    state.trackingTime = []
    state.trackingListLastMonth = []
    state.trackingTimeLastMonth = []
    state.trackingDayIndex = 0
    resetHandler()
//	trackingSensor.reset()
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

//	reset()  // Resets all value, including in the tracking tile.
    
	reportHandler("Touch")
}

def eraseHandler(evt) {
	log.debug "eraseHandler(${evt.value})"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def result = true
    
    state.erase = true
    
    if (state.trackingList[state.trackingList.size()-1].contains("Arr:") || state.trackingList[state.trackingList.size()-1].contains("Dpt:")) {
        if (state.trackingList[state.trackingList.size()-2].contains("Arr:")) {
            setArrival("${state.trackingList[state.trackingList.size()-2]}", "${state.trackingTime[state.trackingTime.size()-2]}")
            if (state.trackingList[state.trackingList.size()-3].contains("Dpt:") || state.trackingList[state.trackingList.size()-3].contains("Arr:")) {
                setLastLocation("${state.trackingList[state.trackingList.size()-3]}", "${state.trackingTime[state.trackingTime.size()-3]}")
            }
        }
        else if (state.trackingList[state.trackingList.size()-2].contains("Dpt:")) {
            setDeparture("${state.trackingList[state.trackingList.size()-2]}", "${state.trackingTime[state.trackingTime.size()-2]}")
            if (state.trackingList[state.trackingList.size()-3].contains("Arr:") || state.trackingList[state.trackingList.size()-3].contains("Dpt:")) {
    //        	setLastLocation("${state.trackingList[state.trackingList.size()-3]}", "${state.trackingTime[state.trackingTime.size()-3]}")
            }
        }
    }
    else {
    	result = false
        log.debug "Unable to erase last entry"
    }
    if (result) {
        state.trackingList.remove(state.trackingList.size()-1)
        state.trackingTime.remove(state.trackingTime.size()-1)
        setTrackingDisplay()
    }
    state.erase = false
}

def reportHandler(evt) {
	log.debug "reportHandler(${evt.value})"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH)
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def calDate = new Date()
    def newDate = new Date()
    log.debug "calDate = ${calDate.format("MM/dd", location.timeZone)}"
    def dates = ["","","","","","",""]
    use(groovy.time.TimeCategory) {
    	log.debug "day = ${day}"
    	for (int x = 0; x < 7; x++) {
        	if (x+1 == day) {
            	log.debug "today == ${x}"
            	dates[x] = "${calDate.format("MM/dd", location.timeZone)}"
            }
            else if (x+1 < day) {
            	newDate = calDate - (day-(x+1))
            	dates[x] = newDate.format("MM/dd", location.timeZone)
            }
            else if (x+1 > day) {
            	newDate = calDate - (day + (6 - x))
            	dates[x] = newDate.format("MM/dd", location.timeZone)
            }
            log.debug "dates[${x}] == ${dates[x]}"
        }
    }
/*    
    def msg = "Tracking Data for: ${personName}\n${date}\n\n" +
    	"SUN (${dates[0]}):\n${state.trackingDays[0]}\n\n" +
        "MON (${dates[1]}):\n${state.trackingDays[1]}\n\n" +
        "TUE (${dates[2]}):\n${state.trackingDays[2]}\n\n" +
        "WED (${dates[3]}):\n${state.trackingDays[3]}\n\n" +
        "THU (${dates[4]}):\n${state.trackingDays[4]}\n\n" +
        "FRI (${dates[5]}):\n${state.trackingDays[5]}\n\n" +
        "SAT (${dates[6]}):\n${state.trackingDays[6]}"
*/        
    def msg = "Tracking Data for: ${personName}\n${date}\n\n" +
    	"SUN (${dates[0]}):\n${getList(dates[0])}\n\n" +
        "MON (${dates[1]}):\n${getList(dates[1])}\n\n" +
        "TUE (${dates[2]}):\n${getList(dates[2])}\n\n" +
        "WED (${dates[3]}):\n${getList(dates[3])}\n\n" +
        "THU (${dates[4]}):\n${getList(dates[4])}\n\n" +
        "FRI (${dates[5]}):\n${getList(dates[5])}\n\n" +
        "SAT (${dates[6]}):\n${getList(dates[6])}"
        
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
}

private getList(date) {
	log.debug "getList(${date})"
	def list = ""
    def found = false
    
    for (int x = 0; x < state.trackingList.size(); x++) {
        if (found) {
        	if (state.trackingList[x].contains("Arr:") || state.trackingList[x].contains("Dpt:")) {
	        	list = list + "[${state.trackingTime[x]} ${state.trackingList[x]}]"
            }
            else {
            	found = false
                break
            }
        }
        else if (state.trackingList[x] == date) {
        	found = true
        }
    }
    return list
}

def resetHandler(evt) {
	log.debug "resetHandler"
    Calendar localCal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCal.get(Calendar.DAY_OF_WEEK)
    int dayOfMonth = localCal.get(Calendar.DAY_OF_MONTH)
    def date = new Date().format("MM/dd", location.timeZone)
    
	state.trackingDays[day - 1] = ""
    if (dayOfMonth == 1) {
    	state.trackingListLastMonth = state.trackingList
        state.trackingTimeLastMonth = state.trackingTime
        state.trackingList = []
        state.trackingTime = []
    }
    state.trackingList.add("${date}")
    state.trackingTime.add("${date}")
    state.trackingDayIndex = state.trackingList.size()
//    trackingSensor.setTrackingList("${getDay(day)}:\n${state.trackingDays[day - 1]}")
}

def switchHandler(evt) {
	log.debug "switchHandler: ${evt.name} / ${evt.value} / ${evt.displayName}"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def date = new Date().format("h:mm a", location.timeZone)
	def trackingDisp = (state.trackingDays[day - 1]) ? (state.trackingDays[day - 1]) : "["

	if (evt.value == "on") {
    	for (int i = 0; i < 10; i++) {
        	switch (i) {
            	case 0:
                	if (evt.displayName != homeSwitch?.displayName) {
                    	if (homeSwitch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${homeSwitch}"
                        	homeSwitch.off()
                        }
                    }
                	break
                case 1:
                	if (evt.displayName != loc2Switch?.displayName) {
                    	if (loc2Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc2Switch}"
                        	loc2Switch.off()
                        }
                    }
                	break
                case 2:
                	if (evt.displayName != loc3Switch?.displayName) {
                    	if (loc3Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc3Switch}"
                        	loc3Switch.off()
                        }
                    }
                	break
                case 3:
                	if (evt.displayName != loc4Switch?.displayName) {
                    	if (loc4Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc4Switch}"
                        	loc4Switch.off()
                        }
                    }
                	break
                case 4:
                	if (evt.displayName != loc5Switch?.displayName) {
                    	if (loc5Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc5Switch}"
                        	loc5Switch.off()
                        }
                    }
                	break
                case 5:
                	if (evt.displayName != loc6Switch?.displayName) {
                    	if (loc6Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc6Switch}"
                        	loc6Switch.off()
                        }
                    }
                	break
                case 6:
                	if (evt.displayName != loc7Switch?.displayName) {
                    	if (loc7Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc7Switch}"
                        	loc7Switch.off()
                        }
                    }
                	break
                case 7:
                	if (evt.displayName != loc8Switch?.displayName) {
                    	if (loc8Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc8Switch}"
                        	loc8Switch.off()
                        }
                    }
                	break
                case 8:
                	if (evt.displayName != loc9Switch?.displayName) {
                    	if (loc9Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc9Switch}"
                        	loc9Switch.off()
                        }
                    }
                	break
                case 9:
                	if (evt.displayName != loc10Switch?.displayName) {
                    	if (loc10Switch?.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc10Switch}"
                        	loc10Switch.off()
                        }
                    }
                	break
                default:
                	break
            }
        }
    	log.debug "switch = ${evt.displayName} / currentLocation = ${trackingSensor.currentValue("currentLocation")}"
//	    trackingSensor.setLocationArrival(evt.displayName)
		if (!state.erase) {
            setArrival("${evt.displayName}", "${date}")        
            state.trackingDays[day - 1] = state.trackingDays[day - 1] + "[${date} Arr: ${evt.displayName}"
            state.trackingList.add("Arr: ${evt.displayName}")
            state.trackingTime.add("${date}")
		}        
    }
    else if (evt.value == "off") {
//    	trackingSensor.setLocationDeparture(evt.displayName)
		if (!state.erase) {
            setDeparture("${evt.displayName}", "${date}")        
            state.trackingDays[day - 1] = trackingDisp + " / ${date} Dpt: ${evt.displayName}]\n"
            state.trackingList.add("Dpt: ${evt.displayName}")
            state.trackingTime.add("${date}")
        }
    }
//    trackingSensor.setTrackingList("${getDay(day)}:\n${state.trackingDays[day - 1]}")
	if (!state.erase) {
		setTrackingDisplay()
    }
}

private setArrival(disp, time) {
	log.debug "setArrival(${disp}, ${time})"
    def loc = disp.replaceAll("Arr: ", "")
    loc = loc.replaceAll("Dpt: ", "")
    log.debug "loc = ${loc}"
    if (state.erase) {
        switch (loc) {
            case homeSwitch.displayName:
                log.debug "Turning on homeSwitch"
                if (homeSwitch.currentSwitch == "off") {
	                homeSwitch.on()
                }
                break
            case loc2Switch.displayName:
                log.debug "Turning on loc2Switch"
                if (loc2Switch.currentSwitch == "off") {
	                loc2Switch.on()
                }
                break
            case loc3Switch.displayName:
                log.debug "Turning on loc3Switch"
                if (loc3Switch.currentSwitch == "off") {
	                loc3Switch.on()
                }
                break
            case loc4Switch.displayName:
                log.debug "Turning on loc4Switch"
                if (loc4Switch.currentSwitch == "off") {
                	loc4Switch.on()
                }
                break
            case loc5Switch.displayName:
                log.debug "Turning on loc5Switch"
                if (loc5Switch.currentSwitch == "off") {
            	    loc5Switch.on()
                }
                break
            case loc6Switch.displayName:
                log.debug "Turning on loc6Switch"
                if (loc6Switch.currentSwitch == "off") {
        	        loc6Switch.on()
                }
                break
            case loc7Switch.displayName:
                log.debug "Turning on loc7Switch"
                if (loc7Switch.currentSwitch == "off") {
    	            loc7Switch.on()
                }
                break
            case loc8Switch.displayName:
                log.debug "Turning on loc8Switch"
                if (loc8Switch.currentSwitch == "off") {
	                loc8Switch.on()
                }
                break
            case loc9Switch.displayName:
                log.debug "Turning on loc9Switch"
                if (loc9Switch.currentSwitch == "off") {
    	            loc9Switch.on()
                }
                break
            case loc10Switch.displayName:
                log.debug "Turning on loc10Switch"
                if (loc10Switch.currentSwitch == "off") {
	                loc10Switch.on()
                }
                break
            default:
                log.debug "Unknown switch"
                break
        }
    }
	trackingSensor.setLocationArrival(loc, time)
}

private setDeparture(disp, time) {
	log.debug "setDeparture(${disp}, ${time})"
    def loc = disp.replaceAll("Dpt: ", "")
    loc = loc.replaceAll("Arr: ", "")
    log.debug "loc = ${loc}"
    if (state.erase) {
        switch (loc) {
            case homeSwitch.displayName:
                log.debug "Turning off homeSwitch"
                if (homeSwitch.currentSwitch == "on") {
	                homeSwitch.off()
                }
                break
            case loc2Switch.displayName:
                log.debug "Turning off loc2Switch"
                if (loc2Switch.currentSwitch == "on") {
    	            loc2Switch.off()
                }
                break
            case loc3Switch.displayName:
                log.debug "Turning off loc3Switch"
                if (loc3Switch.currentSwitch == "on") {
	                loc3Switch.off()
                }
                break
            case loc4Switch.displayName:
                log.debug "Turning off loc4Switch"
                if (loc4Switch.currentSwitch == "on") {
                	loc4Switch.off()
                }
                break
            case loc5Switch.displayName:
                log.debug "Turning off loc5Switch"
                if (loc5Switch.currentSwitch == "on") {
            	    loc5Switch.off()
                }
                break
            case loc6Switch.displayName:
                log.debug "Turning off loc6Switch"
                if (loc6Switch.currentSwitch == "on") {
        	        loc6Switch.off()
                }
                break
            case loc7Switch.displayName:
                log.debug "Turning off loc7Switch"
                if (loc7Switch.currentSwitch == "on") {
    	            loc7Switch.off()
                }
                break
            case loc8Switch.displayName:
                log.debug "Turning off loc8Switch"
                if (loc8Switch.currentSwitch == "on") {
	                loc8Switch.off()
                }
                break
            case loc9Switch.displayName:
                log.debug "Turning off loc9Switch"
                if (loc9Switch.currentSwitch == "on") {
                	loc9Switch.off()
                }
                break
            case loc10Switch.displayName:
                log.debug "Turning off loc10Switch"
                if (loc10Switch.currentSwitch == "on") {
	                loc10Switch.off()
                }
                break
            default:
                log.debug "Unknown switch"
                break
        }
    }
    trackingSensor.setLocationDeparture(loc, time)
}

private setLastLocation(disp, time) {
	log.debug "setLastLocation(${disp}, ${time})"
    def loc = disp.replaceAll("Dpt: ", "")
    loc = loc.replaceAll("Arr: ", "")
    log.debug "loc = ${loc}"
    trackingSensor.setLastLocation("Dpt: ${loc}", time)    
}

private setTrackingDisplay() {
	log.debug "setTrackingDisplay"
    Calendar localCal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCal.get(Calendar.DAY_OF_WEEK)
    
	def disp = "${getDay(day)}:\n"
    for (int x=state.trackingDayIndex; x < state.trackingList.size(); x++) {
    	disp = disp + "[${state.trackingTime[x]} ${state.trackingList[x]}]\n"
    }
    trackingSensor.setTrackingList(disp)
}

private getDay(day) {
	def dayDisp = "ERR"
    
	if (!day) {
    	return "ERR"
    }
	switch(day) {
    	case 1:
        	dayDisp = "SUN"
        	break
        case 2:
        	dayDisp = "MON"
        	break
        case 3:
        	dayDisp = "TUE"
        	break
        case 4:
        	dayDisp = "WED"
        	break
        case 5:
        	dayDisp = "THU"
        	break
        case 6:
        	dayDisp = "FRI"
        	break
        case 7:
        	dayDisp = "SAT"
        	break
        default:
        	break
    }
}
