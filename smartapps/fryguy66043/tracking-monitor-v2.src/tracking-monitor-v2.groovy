/**
 *  Tracking Monitor v2
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
    name: "Tracking Monitor v2",
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
        input "eraseEntry", "bool", title: "This will erase the last location entry, including the arrival and departure."
    }
    section("Add Entry Manually") {
    	input "addEntry", "enum", options: ["None", "Arrival", "Departure"], title: "Do you want to add an entry manually?"
        input "addLocation", "enum", options: ["Home", "Curves", "Doctors", "Heart Clinic", "Heritage Center", "Jeff & Cyndi", "Nutrition Center", "Vet", "Walmart"]
        input "addTime", "text", required: false
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
    
	state.erase = false
	state.displayName = ""
    state.lastEvent = ""
    state.lastEventTime = 0
    state.trackingDayIndex = 0
	state.trackingList = []
    state.trackingArrTime = []
	state.trackingDptTime = []
    state.trackingListLastMonth = []
    state.trackingArrTimeLastMonth = []
    state.trackingDptTimeLastMonth = []
    reset()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {    
//	reset()

    state.lastEvent = ""
    state.lastEventTime = 0
    if (addEntry != "None" && addTime) {
    	log.debug "Adding entry: ${addEntry} / ${addLocation} / ${addTime}"
    	state.displayName = addLocation
        state.lastEventTime = now()
        if (addEntry == "Arrival") {
            state.lastEvent = "on"
            setArrival(addLocation, addTime)
        }
        else if (addEntry == "Departure") {
            state.lastEvent = "off"
            setDeparture(addLocation, addTime)
        }
        setTrackingDisplay()
    }
    if (eraseEntry) {
    	log.debug "Erasing last entry..."
        state.trackingList.remove(state.trackingList.size()-1)
        state.trackingArrTime.remove(state.trackingArrTime.size()-1)
        state.trackingDptTime.remove(state.trackingDptTime.size()-1)
        setTrackingDisplay()
    }

    subscribe(app, appHandler)
    subscribe(homeSwitch, "switch", switchHandler)
    subscribe(trackingSensor, "reportRequest", reportHandler)
    subscribe(trackingSensor, "monthReportRequest", reportThisMonthHandler)
    subscribe(trackingSensor, "lastMonthReportRequest", reportLastMonthHandler)
    subscribe(trackingSensor, "reportRequestFull", reportFullHandler)
    subscribe(trackingSensor, "monthReportRequestFull", reportThisMonthFullHandler)
    subscribe(trackingSensor, "lastMonthReportRequestFull", reportLastMonthFullHandler)
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
	state.trackingDayIndex = 0
	state.trackingList = []
    state.trackingArrTime = []
    state.trackingDptTime = []
    state.trackingListLastMonth = []
    state.trackingArrTimeLastMonth = []
    state.trackingDptTimeLastMonth = []
    resetHandler()
	trackingSensor.reset()
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)

//	reset()  // Resets all value, including in the tracking tile.
    setTrackingDisplay()    
//	reportHandler("Touch")
	reportThisMonthHandler()
}

def eraseHandler(evt) {
	log.debug "eraseHandler(${evt.value})"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def result = true
    
    state.erase = true
    
	def arrDate = (state.trackingArrTime[state.trackingList.size()-1] != "00:00") ? Date.parse("hh:mm", state.trackingArrTime[state.trackingList.size()-1]) : Date.parse("hh:mm", "12:01")
    def dptDate = (state.trackingDptTime[state.trackingList.size()-1] != "00:00") ? Date.parse("hh:mm", state.trackingDptTime[state.trackingList.size()-1]) : Date.parse("hh:mm", "12:01")
    log.debug "arrDate = ${arrDate} / dptDate = ${dptDate} : arrDate < dptDate = ${arrDate < dptDate} : dptDate < arrDate = ${dptDate < arrDate}"
    log.debug "arrDate = dptDate == ${arrDate == dptDate}"

    if (!state.trackingList[state.trackingList.size()-1].contains("~")) { //Make sure we're not deleting anything before today.
        if (arrDate > dptDate) { //This is an arrival event being restored.
            setArrival("${state.trackingList[state.trackingList.size()-2]}", "${state.trackingArrTime[state.trackingArrTime.size()-2]}")
            state.trackingDptTime[state.trackingList.size()-2] = "00:00"
            if (!state.trackingList[state.trackingList.size()-3].contains("~")) {
                setLastLocation("${state.trackingList[state.trackingList.size()-3]}", "${state.trackingDptTime[state.trackingDptTime.size()-3]}")
            }
        }
        else if (arrDate < dptDate) { //this is a departure event being restored.
        	if (state.trackingArrTime[state.trackingArrTime.size()-1] != "00:00") {
            	state.trackingDptTime[state.trackingArrTime.size()-1] = "00:00"
                result = false
            }
            setDeparture("${state.trackingList[state.trackingList.size()-2]}", "${state.trackingDptTime[state.trackingDptTime.size()-2]}")
        }
    }
    else {
    	result = false
        log.debug "Unable to erase last entry"
    }
    if (result) {
    	log.debug "Removing entry: ${state.trackingList[state.trackingList.size()-1]}"
        state.trackingList.remove(state.trackingList.size()-1)
        state.trackingArrTime.remove(state.trackingArrTime.size()-1)
        state.trackingDptTime.remove(state.trackingDptTime.size()-1)
    }

    setTrackingDisplay()
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
    def msg = "Tracking Data for: ${personName}\n${date}\n\n" +
    	"SUN (${dates[0]}):\n${getSimpleList(dates[0])}\n" +
        "MON (${dates[1]}):\n${getSimpleList(dates[1])}\n" +
        "TUE (${dates[2]}):\n${getSimpleList(dates[2])}\n" +
        "WED (${dates[3]}):\n${getSimpleList(dates[3])}\n" +
        "THU (${dates[4]}):\n${getSimpleList(dates[4])}\n" +
        "FRI (${dates[5]}):\n${getSimpleList(dates[5])}\n" +
        "SAT (${dates[6]}):\n${getSimpleList(dates[6])}"
        
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
}

def reportFullHandler(evt) {
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
    def msg = "Tracking Data for: ${personName}\n${date}\n\n" +
    	"Sun (${dates[0]}):\n${getList(dates[0])}\n\n" +
        "Mon (${dates[1]}):\n${getList(dates[1])}\n\n" +
        "Tue (${dates[2]}):\n${getList(dates[2])}\n\n" +
        "Wed (${dates[3]}):\n${getList(dates[3])}\n\n" +
        "Thu (${dates[4]}):\n${getList(dates[4])}\n\n" +
        "Fri (${dates[5]}):\n${getList(dates[5])}\n\n" +
        "Sat (${dates[6]}):\n${getList(dates[6])}"
        
    if (phone) {
    	sendSms(phone, msg)
    }
    if (sendPush) {
    	sendPush(msg)
    }
}

def reportThisMonthHandler(evt) {
	log.debug "reportThisMonthHandler(${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def report = "${location} ${date}: Tracking This Month (page 1)\n\n"
	def dispDate = "x"
    def cnt = 0
    def pCnt = 1

    for (int x = 0; x < state.trackingList.size(); x++) {
    	if (state.trackingList[x].contains("~") && !state.trackingList[x].contains(dispDate)) {
        	dispDate = state.trackingList[x]
            dispDate = dispDate.replace("~", "")
            getDate(dispDate)
	        report = report + "${getDate(dispDate)}:\n${getSimpleList(dispDate)}\n"
	        cnt = cnt + 1
        }
        if (cnt == 7) {
        	log.debug "pCnt = ${pCnt}"
            if (phone) {
                sendSms(phone, report)
            }
            if (sendPush) {
                sendPush(report)
            }
            cnt = 0
            pCnt = pCnt + 1
            report = "${location} ${date}: Tracking This Month (page ${pCnt})\n\n"
        }
    }

	if (phone) {
    	sendSms(phone, report)
    }
    if (sendPush) {
    	sendPush(report)
    }
}

def reportThisMonthFullHandler(evt) {
	log.debug "reportThisMonthFullHandler(${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def report = "${location} ${date}: Tracking This Month (page 1)\n\n"
	def dispDate = "x"
    def cnt = 0
    def pCnt = 1

    for (int x = 0; x < state.trackingList.size(); x++) {
    	if (state.trackingList[x].contains("~") && !state.trackingList[x].contains(dispDate)) {
        	dispDate = state.trackingList[x]
            dispDate = dispDate.replace("~", "")
            getDate(dispDate)
	        report = report + "${getDate(dispDate)}:\n${getList(dispDate)}\n\n"
	        cnt = cnt + 1
        }
        if (cnt == 7) {
            if (phone) {
                sendSms(phone, report)
            }
            if (sendPush) {
                sendPush(report)
            }
            cnt = 0
            pCnt = pCnt + 1
            report = "${location} ${date}: Tracking This Month (page ${pCnt})\n\n"
        }
    }

	if (phone) {
    	sendSms(phone, report)
    }
    if (sendPush) {
    	sendPush(report)
    }
}

private getDate(dispDate) {
	log.debug "getDate(${dispDate})"
    def txtYear = new Date().format("yy", location.timeZone)
    def year = Integer.parseInt(txtYear)
    def month = new Date().format("MM", location.timeZone)
	def dispMonth = dispDate.take(2)
    def dispYear = year
    if (month < dispMonth) {
	    dispYear = year - 1
    }
    log.debug "dispMonth = ${dispMonth} / month = ${month} / year = ${dispYear}"
    
	def dispDay = ""
    
	if (dispDate) {
    	def fullDispDate = "${dispDate}/${dispYear}"
        def date = new Date().parse("MM/dd/yy", fullDispDate)
		def day = date.format("EEE")
        dispDay = "${day} (${dispDate})"
    }
    log.debug "dispDay = ${dispDay}"
    return dispDay
}

private getList(date) {
	log.debug "getList(${date})"
	def list = ""
    def arrDisp = ""
    def dptDisp = ""
    def found = false
    
    for (int x = 0; x < state.trackingList.size(); x++) {
    	
        if (found) {
        	if (!state.trackingList[x].contains("~")) {
            	arrDisp = (state.trackingArrTime[x] != "00:00") ? "Arr: ${state.trackingArrTime[x]} " : ""
                dptDisp = (state.trackingDptTime[x] != "00:00") ? " Dpt: ${state.trackingDptTime[x]}" : ""
	        	list = list + "[${arrDisp}\"${state.trackingList[x]}\"${dptDisp}]"
            }
            else {
            	found = false
                break
            }
        }
        else if (state.trackingList[x].contains(date)) {
        	if (x+1 < state.trackingList.size()) { 
            	if (!state.trackingList[x+1].contains(date)) { // To address issues with ST schedules firing more than once on occassion.
	        		found = true
                }
            }
        }
    }
    return list
}

private getSimpleList(date) {
	log.debug "getSimpleList(${date})"
	def list = ""
    def found = false
    
    for (int x = 0; x < state.trackingList.size(); x++) {
    	
        if (found) {
        	if (!state.trackingList[x].contains("~")) {
	        	list = list + "${state.trackingList[x]}\n"
            }
            else {
            	found = false
                break
            }
        }
        else if (state.trackingList[x].contains(date)) {
        	if (x+1 < state.trackingList.size()) { 
            	if (!state.trackingList[x+1].contains(date)) { // To address issues with ST schedules firing more than once on occassion.
	        		found = true
                }
            }
        }
    }
    return list
}

def reportLastMonthHandler(evt) {
	log.debug "reportLastMonthHandler(${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def report = "${location} ${date}: Tracking Last Month (page 1)\n\n"
	def dispDate = "x"
    def cnt = 0
    def pCnt = 1

    for (int x = 0; x < state.trackingListLastMonth.size(); x++) {
    	if (state.trackingListLastMonth[x].contains("~") && !state.trackingListLastMonth[x].contains(dispDate)) {
        	dispDate = state.trackingListLastMonth[x]
            dispDate = dispDate.replace("~", "")
	        report = report + "${getDate(dispDate)}:\n${getLastMonthSimpleList(dispDate)}\n"
	        cnt = cnt + 1
        }
        if (cnt == 7) {
            if (phone) {
                sendSms(phone, report)
            }
            if (sendPush) {
                sendPush(report)
            }
            cnt = 0
            pCnt = pCnt + 1
            report = "${location} ${date}: Tracking This Month (page ${pCnt})\n\n"
        }
    }

	if (phone) {
    	sendSms(phone, report)
    }
    if (sendPush) {
    	sendPush(report)
    }
}

def reportLastMonthFullHandler(evt) {
	log.debug "reportLastMonthFullHandler(${evt?.value})"
    def date = new Date().format("MM/dd/yy h:mm a", location.timeZone)
    def report = "${location} ${date}: Tracking Last Month (page 1)\n\n"
	def dispDate = "x"
    def cnt = 0
    def pCnt = 1

    for (int x = 0; x < state.trackingListLastMonth.size(); x++) {
    	if (state.trackingListLastMonth[x].contains("~") && !state.trackingListLastMonth[x].contains(dispDate)) {
        	dispDate = state.trackingListLastMonth[x]
            dispDate = dispDate.replace("~", "")
	        report = report + "${getDate(dispDate)}:\n${getLastMonthList(dispDate)}\n\n"
	        cnt = cnt + 1
        }
        if (cnt == 7) {
            if (phone) {
                sendSms(phone, report)
            }
            if (sendPush) {
                sendPush(report)
            }
            cnt = 0
            pCnt = pCnt + 1
            report = "${location} ${date}: Tracking This Month (page ${pCnt})\n\n"
        }
    }

	if (phone) {
    	sendSms(phone, report)
    }
    if (sendPush) {
    	sendPush(report)
    }
}

private getLastMonthList(date) {
	log.debug "getLastMonthList(${date})"
	def list = ""
    def arrDisp = ""
    def dptDisp = ""
    def found = false
    
    for (int x = 0; x < state.trackingListLastMonth.size(); x++) {
    	
        if (found) {
        	if (!state.trackingListLastMonth[x].contains("~")) {
            	arrDisp = (state.trackingArrTimeLastMonth[x] != "00:00") ? "Arr: ${state.trackingArrTimeLastMonth[x]} " : ""
                dptDisp = (state.trackingDptTimeLastMonth[x] != "00:00") ? " Dpt: ${state.trackingDptTimeLastMonth[x]}" : ""
	        	list = list + "[${arrDisp}\"${state.trackingListLastMonth[x]}\"${dptDisp}]"
            }
            else {
            	found = false
                break
            }
        }
        else if (state.trackingListLastMonth[x].contains(date)) {
        	if (x+1 < state.trackingListLastMonth.size()) { 
            	if (!state.trackingListLastMonth[x+1].contains(date)) { // To address issues with ST schedules firing more than once on occassion.
	        		found = true
                }
            }
        }
    }
    return list
}

private getLastMonthSimpleList(date) {
	log.debug "getLastMonthSimpleList(${date})"
	def list = ""
    def found = false
    
    for (int x = 0; x < state.trackingListLastMonth.size(); x++) {
    	
        if (found) {
        	if (!state.trackingListLastMonth[x].contains("~")) {
	        	list = list + "${state.trackingListLastMonth[x]}\n"
            }
            else {
            	found = false
                break
            }
        }
        else if (state.trackingListLastMonth[x].contains(date)) {
        	if (x+1 < state.trackingListLastMonth.size()) { 
            	if (!state.trackingListLastMonth[x+1].contains(date)) { // To address issues with ST schedules firing more than once on occassion.
	        		found = true
                }
            }
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
    
    if (dayOfMonth == 1) {
    	state.trackingListLastMonth = state.trackingList
        state.trackingArrTimeLastMonth = state.trackingArrTime
        state.trackingDptTimeLastMonth = state.trackingDptTime
        state.trackingList = []
        state.trackingArrTime = []
        state.trackingDptTime = []
    }
    state.trackingList.add("~${date}")
    state.trackingArrTime.add("~${date}")
    state.trackingDptTime.add("~${date}")
    state.trackingDayIndex = state.trackingList.size()
}

def switchHandler(evt) {
	log.debug "switchHandler: ${evt.name} / ${evt.value} / ${evt.displayName}"
    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCalendar.get(Calendar.DAY_OF_WEEK)
    def date = new Date().format("h:mm a", location.timeZone)
    def index = -1
    def delay = false
    def skip = false

	if (state.lastEventTime + (60 * 15000) > now()) { // If something has occured in the last 15 minutes, check to make sure it's not a duplicate
    	log.debug "It's been less than 15 minutes..."
        if (evt.displayName == state.displayName && evt.value == state.lastEvent) {
        	log.debug "This is a duplicate: skipping..."
        	skip = true
        }
    }
    else {
    	log.debug "Not a duplicate event. Updating state values."
        state.displayName = evt.displayName
        state.lastEvent = evt.value
        state.lastEventTime = now()
    }
    
	if (!skip) {
        if (evt.value == "on") {
            log.debug "Checking to see if any other switches need to be turned off..."
            for (int i = 0; i < 10; i++) {
                switch (i) {
                    case 0:
                        if (evt.displayName != homeSwitch?.displayName) {
                            if (homeSwitch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${homeSwitch}"
                                delay = true
                                homeSwitch.off()
                            }
                        }
                        break
                    case 1:
                        if (evt.displayName != loc2Switch?.displayName) {
                            if (loc2Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc2Switch}"
                                delay = true
                                loc2Switch.off()
                            }
                        }
                        break
                    case 2:
                        if (evt.displayName != loc3Switch?.displayName) {
                            if (loc3Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc3Switch}"
                                delay = true
                                loc3Switch.off()
                            }
                        }
                        break
                    case 3:
                        if (evt.displayName != loc4Switch?.displayName) {
                            if (loc4Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc4Switch}"
                                delay = true
                                loc4Switch.off()
                            }
                        }
                        break
                    case 4:
                        if (evt.displayName != loc5Switch?.displayName) {
                            if (loc5Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc5Switch}"
                                delay = true
                                loc5Switch.off()
                            }
                        }
                        break
                    case 5:
                        if (evt.displayName != loc6Switch?.displayName) {
                            if (loc6Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc6Switch}"
                                delay = true
                                loc6Switch.off()
                            }
                        }
                        break
                    case 6:
                        if (evt.displayName != loc7Switch?.displayName) {
                            if (loc7Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc7Switch}"
                                delay = true
                                loc7Switch.off()
                            }
                        }
                        break
                    case 7:
                        if (evt.displayName != loc8Switch?.displayName) {
                            if (loc8Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc8Switch}"
                                delay = true
                                loc8Switch.off()
                            }
                        }
                        break
                    case 8:
                        if (evt.displayName != loc9Switch?.displayName) {
                            if (loc9Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc9Switch}"
                                delay = true
                                loc9Switch.off()
                            }
                        }
                        break
                    case 9:
                        if (evt.displayName != loc10Switch?.displayName) {
                            if (loc10Switch?.currentValue("switch") == "on") {
                                log.debug "Turning off ${loc10Switch}"
                                delay = true
                                loc10Switch.off()
                            }
                        }
                        break
                    default:
                        break
                }
            }
            log.debug "switch = ${evt.displayName} / currentLocation = ${trackingSensor.currentValue("currentLocation")}"
            if (!delay && !state.erase) {
                setArrival("${evt.displayName}", "${date}")        
//                state.trackingList.add("${evt.displayName}")
//                state.trackingArrTime.add("${date}")
//                state.trackingDptTime.add("00:00")
            }        
        }
        else if (evt.value == "off") {
            if (!state.erase) {
                setDeparture("${evt.displayName}", "${date}")        
//                index = getLastArrivalLoc("${evt.displayName}")
//                if (index > -1) {
//                    log.debug "index found: ${index}"
//                    state.trackingDptTime[index] = "${date}"
//                }
//                else {
//                    log.debug "index not found"
//                    state.trackingList.add("${evt.displayName}")
//                    state.trackingDptTime.add("${date}")
//                    state.trackingArrTime.add("00:00")
//                }
            }
        }
        if (delay) {
            state.displayName = "${evt.displayName}"
            runIn(5, turnOnDelay)
        }
        if (!state.erase) {
            setTrackingDisplay()
        }
    }
}

def turnOnDelay(evt) {
	log.debug "turnOnDelay"
    def date = new Date().format("h:mm a", location.timeZone)
    setArrival("${state.displayName}", "${date}")        
    state.trackingList.add("${state.displayName}")
    state.trackingArrTime.add("${date}")
    state.trackingDptTime.add("00:00")
    state.displayName = ""
    setTrackingDisplay()
}

private getLastArrivalLoc(loc) {
	log.debug "getLastArrival(${loc})"
    def index = -1
    
    for (int x = state.trackingList.size()-1; x >= 0; x--) {
    	if (state.trackingList[x].contains("~")) {
        	break
        }
        else if (state.trackingList[x] == loc && state.trackingDptTime[x] == "00:00") {
        	log.debug "found: index = ${x}: ${state.trackingList[x]} / ${state.trackingDptTime[x]}"
        	index = x
        	break
        }
    }
    return index
}

private setArrival(disp, time) {
	log.debug "setArrival(${disp}, ${time}) / state.erase = ${state.erase}"
    if (state.erase) {
    	log.debug "Checking to see if any switches need to be turned on..."
        switch (disp) {
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
    state.trackingList.add("${disp}")
    state.trackingArrTime.add("${time}")
    state.trackingDptTime.add("00:00")
	trackingSensor.setLocationArrival(disp, time)
}

private setDeparture(disp, time) {
	log.debug "setDeparture(${disp}, ${time}) / state.erase = ${state.erase}"
    if (state.erase) {
    	log.debug "Checking to see if any switches need to be turned off..."
        switch (disp) {
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
    def index = getLastArrivalLoc("${disp}")
    if (index > -1) {
        log.debug "index found: ${index}"
        state.trackingDptTime[index] = "${time}"
    }
    else {
        log.debug "index not found"
        state.trackingList.add("${disp}")
        state.trackingDptTime.add("${time}")
        state.trackingArrTime.add("00:00")
    }
    trackingSensor.setLocationDeparture(disp, time)
}

private setLastLocation(disp, time) {
	log.debug "setLastLocation(${disp}, ${time})"
    trackingSensor.setLastLocation(disp, time)    
}

private setTrackingDisplay() {
	log.debug "setTrackingDisplay"
    Calendar localCal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
	int day = localCal.get(Calendar.DAY_OF_WEEK)
    
	def disp = "${getDay(day)}:\n"
    def arrDisp = ""
    def dptDisp = ""
    for (int x=state.trackingDayIndex; x < state.trackingList.size(); x++) {
    	arrDisp = (state.trackingArrTime[x] != "00:00") ? "Arr: ${state.trackingArrTime[x]} " : ""
        dptDisp = (state.trackingDptTime[x] != "00:00") ? " Dpt: ${state.trackingDptTime[x]}" : ""
    	disp = disp + "[${arrDisp}\"${state.trackingList[x]}\"${dptDisp}]\n"
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