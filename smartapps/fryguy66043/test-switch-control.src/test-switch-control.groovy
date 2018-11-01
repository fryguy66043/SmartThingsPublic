/**
 *  My First SmartApp To Control a Switch With A Contact Sensor and a Timer
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
    name: "Test Switch Control",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Control a Switch with a schedule.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Enable if present") {
    	input "p", "capability.presenceSensor", required: true
    }
	section("Disable if present") {
    	input "p1", "capability.presenceSensor", required: false, multiple: true
    }
	section("HVAC Sensor") {
    	input "hvac", "device.myHVACSensor", required: false
    }
    section("Button 1") {
    	input "button1", "capability.button", required: false
    }
	section("Switch 1") {
    	input "switch1", "capability.switch", multiple: true, required: false
    }
	section("Switch 2") {
    	input "switch2", "capability.switch", multiple: true, required: false
    }
	section("Switch 3") {
    	input "switch3", "capability.switch", multiple: true, required: false
    }
    section("Lock") {
    	input "myLock", "capability.lock", multiple: true, required: false
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when Opened?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false
    }
}

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

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
	state.onCnt = 0
    state.onTime = 0
    state.testTime = now()
    subscribe(app, appHandler)
    subscribe(button1, "button", buttonHandler)
    subscribe(hvac, "operatingState", hvacHandler)
    subscribe(switch1, "switch", switchHandler)
    subscribe(switch2, "switch", switchHandler)
    subscribe(switch3, "switch", httpHandler)
    subscribe(myLock, "lock", lockHandler)
    subscribe(myLock, "codeReport", lockCodeHandler)
    subscribe(myLock, "reportAllCodes", reportAllCodesHandler)
    subscribe(p1, "presence", p1Handler)
//    runEvery1Minute(timeHandler)
}

import groovy.time.TimeCategory

def buttonHandler(evt) {
	log.debug "buttonHandler(${evt.value})"
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value} / evt.data = ${evt?.data}"
	def testVal = (evt.value) ?: "Nope!"
	log.debug "testVal = ${testVal}"


    def now = new Date()
    def then = new Date()
	def sunTime = getSunriseAndSunset()
    def dark = false
    def offset = 35.7 as Integer
    use (TimeCategory) {
    	then = then + offset.minutes
    }
    log.debug "offset = ${offset}"
    log.debug "now = ${now.format("MM/dd/yy hh:mm:ss a", location.timeZone)} / then = ${then.format("MM/dd/yy hh:mm:ss a", location.timeZone)}"	


//	httpHandler()


/*
	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def endDate = new Date().parse("MM/dd/yy hh:mm:ss a", date)
    def startDate = new Date().parse("MM/dd/yy hh:mm:ss a", "08/10/18 3:30:15 PM")
    Double timeDiff = ((endDate.getTime() - startDate.getTime()) / 1000 / 60)
    log.debug "startDate = ${startDate} / date = ${endDate} / timeDiff = ${timeDiff.round(2)}"

	
	def s1List = "["
    switch1.each { s1List = s1List + "\"${it}\"," }
    s1List = s1List + "]"
    
    def s2List = "["
    switch2.each { s2List = s2List + "\"${it}\"," }
    s2List = s2List + "]"
    
    def s3List = "["
    switch3.each { s3List = s3List + "\"${it}\"," }
    s3List = s3List + "]"
    
    def lList = "["
    myLock.each { lList = lList + "\"${it}\"," }
    lList = lList + "]"
    
	def deviceList = "{\"Switch1\": ${s1List}, \"Switch2\": ${s2List}, \"Switch3\": ${s3List}, \"Lock\": ${lList}}"
//	def deviceList = '{"Switch1": ["Bedroom TV"], "Switch2": ["Switch A", "Switch B"], "Locks": ["Basement Lock"]}'
    log.debug deviceList
    def output = new JsonSlurper().parseText(deviceList)
    log.debug output
	output.Switch2.each {
    	log.debug "${it} == Switch B: ${it == "Switch B"}"
    }
*/
//	def code = myLock.requestCode("1")
//	log.debug "code = ${code}"
//	def codes = "${myLock.currentValue("lockCodes")}"
//    def codeId = "2"
//    log.debug "codes = ${codes}"
//    def users = new JsonSlurper().parseText(codes)
//    log.debug "users = ${users} / users.size() = ${users?.size()}"
//    if (users) {
//        log.debug "passed users..."
//        users.each {k, v -> 
//            log.debug "k = ${k} / v = ${v}"
//            if (k == "${codeId}") {
//                log.debug "Found user: ${v}"
//            }
//        }
//    }

//    myLock.poll()
    
/*
	def lockCommands = myLock.supportedCommands
    log.debug "Commands: ${lockCommands}"
    def caps = myLock.capabilities
    caps.commands.each { comm ->
    	log.debug "Command Name: ${comm.name}"
    }
*/
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
	log.debug "callbackHandler: ${hubResponse}"
    
    hubResponse.each { item ->
    	log.debug "item = ${item}"
    }

/*
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
