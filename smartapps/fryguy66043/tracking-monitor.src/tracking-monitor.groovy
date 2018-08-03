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
    state.trackingDays = ["","","","","","",""]

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(homeSwitch, "switch", switchHandler)
    subscribe(trackingSensor, "reportRequest", reportHandler)
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

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    
//** Uncomment this line to erase tracking information for today.  Used for testing.
//    state.trackingDays[day-1] = ""
//*********************************************************************************    
//** Uncomment this linee to erase all tracking information.  Used for testing.
//	    state.trackingDays = ["","","","","","",""]
//*********************************************************************************    

	reportHandler("Touch")
}

def reportHandler(evt) {
	log.debug "reportHandler(${evt.value})"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
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
    def msg = "Tracking Data for: ${personName}\n${date}\n\n" +
    	"SUN (${dates[0]}):\n${state.trackingDays[0]}\n\n" +
        "MON (${dates[1]}):\n${state.trackingDays[1]}\n\n" +
        "TUE (${dates[2]}):\n${state.trackingDays[2]}\n\n" +
        "WED (${dates[3]}):\n${state.trackingDays[3]}\n\n" +
        "THU (${dates[4]}):\n${state.trackingDays[4]}\n\n" +
        "FRI (${dates[5]}):\n${state.trackingDays[5]}\n\n" +
        "SAT (${dates[6]}):\n${state.trackingDays[6]}"
        
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
}

def resetHandler(evt) {
	log.debug "resetHandler"
    Calendar localCal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCal.get(Calendar.DAY_OF_WEEK)
    
	state.trackingDays[day - 1] = ""
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
                	if (evt.displayName != homeSwitch.displayName) {
                    	if (homeSwitch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${homeSwitch}"
                        	homeSwitch.off()
                        }
                    }
                	break
                case 1:
                	if (evt.displayName != loc2Switch.displayName) {
                    	if (loc2Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc2Switch}"
                        	loc2Switch.off()
                        }
                    }
                	break
                case 2:
                	if (evt.displayName != loc3Switch.displayName) {
                    	if (loc3Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc3Switch}"
                        	loc3Switch.off()
                        }
                    }
                	break
                case 3:
                	if (evt.displayName != loc4Switch.displayName) {
                    	if (loc4Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc4Switch}"
                        	loc4Switch.off()
                        }
                    }
                	break
                case 4:
                	if (evt.displayName != loc5Switch.displayName) {
                    	if (loc5Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc5Switch}"
                        	loc5Switch.off()
                        }
                    }
                	break
                case 5:
                	if (evt.displayName != loc6Switch.displayName) {
                    	if (loc6Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc6Switch}"
                        	loc6Switch.off()
                        }
                    }
                	break
                case 6:
                	if (evt.displayName != loc7Switch.displayName) {
                    	if (loc7Switch.currentValue("switch") == "on") {
                        	log.debug "Turning off ${loc7Switch}"
                        	loc7Switch.off()
                        }
                    }
                	break
                case 7:
                	if (evt.displayName != loc8Switch.displayName) {
                    	if (loc8Switch.currentValue("switch") == "on") {
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
	    trackingSensor.setLocationArrival(evt.displayName)
	    state.trackingDays[day - 1] = state.trackingDays[day - 1] + "[${date} Arr: ${evt.displayName}"
        
    }
    else if (evt.value == "off") {
    	trackingSensor.setLocationDeparture(evt.displayName)
	    state.trackingDays[day - 1] = trackingDisp + " / ${date} Dpt: ${evt.displayName}]\n"
    }
    trackingSensor.setTrackingList("${getDay(day)}:\n${state.trackingDays[day - 1]}")
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
