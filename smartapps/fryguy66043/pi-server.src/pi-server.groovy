/**
 *  Use Virtual Switches to execute web server calls on Raspberry Pi.
 *
 *  Copyright 2017 Jeffrey Fry
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
    name: "Pi Server",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Use Virtual Switches to execute web server calls on Raspberry Pi.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Twitter...") {
    	input "cpuTweetSwitch", "capability.switch", required: false, title: "Tweet Pi CPU Temperature"
    	input "imageTweetSwitch", "capability.switch", required: false, title: "Tweet Pi Picture"        
    }
	section("Email Pi CPU Temperature") {
    	input "cpuEmailSwitch", "capability.switch", required: false, title: "Email Pi CPU Temperature"
        input "cpuEmailSchedule", "number", required: false, title: "Automatically run on a schedule? (0 = off)"
        input "imageEmailSwitch", "capability.switch", required: false, title: "Email Pi Picture"
        input "imageEmailSchedule", "number", required: false, title: "Automatically run on a schedule? (0 = off)"
    }    
    section("Image Capture Loop") {
    	input "imageLoop", "bool", title: "Start/Stop Image Capture Service on Pi Server"
    	input "imageLoopSwitch", "capability.switch", required: false, title: "Control Time-Lapsed Impage Capture"
        input "imageLoopInterval", "enum", options: ["Off", "1", "5", "15", "30", "60"], title: "Timer Setting in Minutes"
        input "imageLoopSunriseAutoStart", "bool", title: "Auto-Start at sunrise?"
        input "imageLoopSunriseMinutesBefore", "number", required: false, title: "Number of minutes before sunrise?"
        input "imageLoopSunsetAutoStop", "bool", title: "Auto-Stop at sunset?"
        input "imageLoopSunsetMinutesAfter", "number", required: false, title: "Number of minutes after sunset?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", title: "Send Push Notification when command executed?"
    }
    section("Send a Text Message?") {
        input "phone", "phone", required: false, title: "Send a Text Message when command executed?"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(app, appHandler)
    subscribe(cpuTweetSwitch, "switch.on", cpuTweetHandler)
    subscribe(cpuEmailSwitch, "switch.on", cpuEmailHandler)
    subscribe(imageTweetSwitch, "switch.on", imageTweetHandler)
    subscribe(imageEmailSwitch, "switch.on", imageEmailHandler)
    subscribe(imageLoopSwitch, "switch", imageLoopHandler)
    if (cpuEmailSchedule || imageEmailSchedule) {
    	runEvery1Hour(emailScheduleHandler)
    }
    if (imageLoopSunriseAutoStart) {
    	subscribe(location, "sunriseTime", sunsetTimeHandler)
    }
    if (imageLoopSunsetAutoStop) {
    	subscribe(location, "sunsetTime", sunriseTimeHandler)
        scheduleTurnOff(location.currentValue("sunsetTime"))
    }
    if (imageLoop && !state.imageLoopServiceRunning) {
    	startImageService()
    }
    else if (state.imageLoopServiceRunning) {
    	stopImageService()
    }
    state.cpuEmailScheduleHours = state.cpuEmailScheduleHours ?: 0
    state.imageEmailScheduleHours = state.imageEmailScheduleHours ?: 0
    state.imageLoopServiceRunning = state.imageLoopServiceRunning ?: false
}

def appHandler(evt) {
	log.debug "appHandler"
}

def sunsetTimeHandler(evt) {
	log.debug "sunsetTimeHandler"
    
}

def sunriseTimeHandler(evt) {
	log.debug "sunriseTimeHandler"
}

def scheduleTurnOff(sunsetString) {
	log.debug "scheduleTurnOff(${sunsetString})"
    
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)

    //calculate the offset
    def timeAfterSunset = new Date(sunsetTime.time + (imageLoopSunsetMinutesAfter * 60 * 1000))

    log.debug "Scheduling for: $timeAfterSunset (sunset is $sunsetTime)"

    //schedule this to run one time
    runOnce(timeAfterSunset, autoStopHandler)
}

def scheduleTurnOn(sunriseString) {
	log.debug "scheduleTurnOn(${sunriseString})"

    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)

    //calculate the offset
    def timeBeforeSunrise = new Date(sunriseTime.time - (imageLoopSunriseMinutesBefore * 60 * 1000))

    log.debug "Scheduling for: $timeBeforeSunrise (sunrise is $sunriseTime)"

    //schedule this to run one time
    runOnce(timeBeforeSunrise, autoStartHandler)
}

def autoStartHandler(evt) {
	log.debug "autoStartHandler(${evt.value})"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Auto-Start Image Capture Service..."
    imageLoopSwitch?.on()
    log.debug msg
}

def autoStopHandler(evt) {
	log.debug "autoStopHandler(${evt.value})"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Auto-Stop Image Capture Service..."
    imageLoopSwitch?.off()
    log.debug msg
}

def startImageService() {
	log.debug "startImageService"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Start Image Capture Service..."
    sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureinitialize HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
	log.debug msg
	state.imageLoopServiceRunning = true
}

def stopImageService() {
	log.debug "stopImageService"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    msg = msg + "Requesting Pi Server Terminate Image Capture Service..."
    sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureterminate HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
	log.debug msg
    if (imageLoopSwitch.currentValue("switch") == 'on') {
    	imageLoopSwitch.off()
    }
    state.imageLoopServiceRunning = false
}

def imageLoopHandler(evt) {
	log.debug "imageLoopHandler(${evt.value}"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: "
    if (evt.value == 'on' && imageLoopInterval != "Off") {
    	msg = msg + "Requesting Pi Server Time-Lapsed Image Capture Start..."
		sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecapturestart/${imageLoopInterval} HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    }
    else {
    	msg = msg + "${location} ${date}: Requesting Pi Server Time-Lapsed Image Capture Stop..."
		sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecapturestop HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    }
	log.debug msg
}

def emailScheduleHandler(evt) {
	log.debug "emailScheduleHandler"
    if (cpuEmailSchedule) {
    	state.cpuEmailScheduleHours = state.cpuEmailScheduleHours + 1
        if (state.cpuEmailScheduleHours >= cpuEmailSchedule) {
        	cpuEmailSwitch?.on()
            state.cpuEmailScheduleHours = 0
        }
    }
    if (imageEmailSchedule) {
    	state.imageEmailScheduleHours = state.imageEmailScheduleHours + 1
        imageEmailSwitch?.on()
        state.imageEmailScheduleHours = 0
    }
}

def cpuTweetHandler(evt) {
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Requesting Pi Server CPU Temp Tweet..."
	log.debug msg
	sendHubCommand(new physicalgraph.device.HubAction("""GET /tweetcpu HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    cpuTweetSwitch.off()
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

def imageTweetHandler(evt) {
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Requesting Pi Server Image Capture Tweet..."
	log.debug msg
	sendHubCommand(new physicalgraph.device.HubAction("""GET /tweetpic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    imageTweetSwitch.off()
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

def cpuEmailHandler(evt) {
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Requesting Pi Server CPU Temp Email..."
	log.debug msg
	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailcpu HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    cpuEmailSwitch.off()
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

def imageEmailHandler(evt) {
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Requesting Pi Server Image Capture Email..."
	log.debug msg
	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailpic HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
    imageEmailSwitch.off()
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

def httpHandler(evt) {
	log.debug "httpHandler"
    
//    httpGet("http://192.168.1.128:5000/"){
//		response -> log.debug "response = ${response}"
//	}
    
//	def result = sendHubCommand(new physicalgraph.device.HubAction("""GET / HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callbackHandler]))
//	def result = sendHubCommand(new physicalgraph.device.HubAction("""GET / HTTP/1.1\r\nHOST: 192.168.1.128:5000\r\n\r\n""", physicalgraph.device.Protocol.LAN))
//	def result = sendHubCommand(new physicalgraph.device.HubAction("""GET /cakes \r\nHOST: 192.168.1.128:5000""", physicalgraph.device.Protocol.LAN))
//	def result = sendHubCommand(new physicalgraph.device.HubAction("""GET /cpu \r\nHOST: 192.168.1.128:5000""", physicalgraph.device.Protocol.LAN))
	def result = sendHubCommand(new physicalgraph.device.HubAction("""GET /email \r\nHOST: 192.168.1.128:5000""", physicalgraph.device.Protocol.LAN))
//	log.debug "result = ${result}"
}

def callbackHandler(hubResponse){
	log.debug "callbackHandler"
//    log.debug "Status: ${hubResponse.status} / Body: ${hubResponse.body}"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Pi Server Call (${hubResponse.status}: ${hubResponse.body})"
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
	log.debug msg    
    
/*
    hubResponse.each { item ->
    	log.debug "item = ${item}"
    }

    def msg = parseLanMessage(hubResponse)
    log.debug "msg = ${msg}"

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
    log.debug "header = ${headerAsString} / headerMap = ${headerMap} / body = ${body} / status = ${staus} / json = ${json} / xml = ${xml} / data = ${data}"
*/    
}

def timeHandler(evt) {
	log.debug "timeHandler"
    state.testTime = state.testTime ?: now()
    def nowTime = now()
    log.debug "nowTime = ${nowTime} / state.testTime = ${state.testTime} / nowTime - state.testTime = ${nowTime - state.testTime}"
    log.debug "nowTime > state.testTime + (60 * 1000) = ${nowTime > state.testTime + (60 * 1000)}"
    if (nowTime > state.testTime + (60 * 1000)) {
    	state.testTime = nowTime
    }
}

def p1Handler(evt) {
	log.debug "p1handler(${evt.value})"
    if (p1?.currentValue("presence") == "present") {
    	switch1?.off()
    }
}

def hvacHandler(evt) {
	log.debug "hvacHandler(${evt.value}) / p = present: ${p?.currentValue("presence") == "present"} / p1 = present: ${p1?.currentValue("presence") == "present"}"
    
    if (p?.currentValue("presence") == "present" && p1?.currentValue("presence") != "present") {
        switch(hvac.currentValue("operatingState")) {
            case "cooling":
                switch1?.on()
                break
            case "idle":
                switch1?.off()
                break
            default:
                break
        }
    }
}

def reportAllCodesHandler(evt) {
	log.debug "reportAllCodesHandler(${evt.value})"
}

def lockCodeHandler(evt) {
	log.debug "lockCodeHandler"
    log.debug "${evt.value} / ${evt.data}"
}

def lockHandler(evt) {
	log.debug "lockHandler"
    def dataString = "${evt.data}"
    def index = 0
    def user = ""
	log.debug "${evt.value} / ${evt?.data} / ${evt.data.size()}"

	index = dataString.indexOf("codeName")
	if (index > -1) {
    	user = dataString.substring(index+11, dataString.size())
        log.debug "User = ${user}"
        index = user.indexOf(",")
        user = user.substring(0, index-1)
        log.debug "Final User = ${user}"
    }
    else {
    	log.debug "User not found"
    }
    
//    evt.data.each { info ->
//    	log.debug "info = ${info}"
//    }
    
//    for (int x=0; x<evt.data.size(); x++) {
//    }
}

def switchHandler(evt) {
	log.debug "switchHandler: evt.name = ${evt.name} / evt.displayName = ${evt.displayName} / evt.value = ${evt.value}"
    if (evt.value == "on") {
    	for (int i = 0; i < 3; i++) {
        	switch (i) {
            	case 0:
                	if (switch1?.displayName != evt?.displayName) {
                    	log.debug "${switch1} != ${evt.displayName}"
                        if (switch1.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch1} off..."
                        	switch1.off()
                        }
                    }
                	break
                case 1:
                	if (switch2?.displayName != evt?.displayName) {
                    	log.debug "${switch2} != ${evt.displayName}"
                        if (switch2?.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch2} off..."
                        	switch2?.off()
                        }
                    }
                	break
                case 2:
                	if (switch3?.displayName != evt?.displayName) {
                    	log.debug "${switch3} != ${evt.displayName}"
                        if (switch3?.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch3} off..."
                        	switch3?.off()
                        }
                    }
                    break
                default:
                	log.debug "Unexpected value..."
                	break
            }
        }
    }
//    log.debug "switch1.displayName = ${switch1}"
//    log.debug "switch2.displayName = ${switch2.displayName}"
//    log.debug "switch3.displayName = ${switch3.displayName}"
}