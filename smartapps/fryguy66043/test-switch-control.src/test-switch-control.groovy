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
	section("Switch 1") {
    	input "switch1", "capability.switch", required: false
    }
	section("Switch 2") {
    	input "switch2", "capability.switch", required: false
    }
	section("Switch 3") {
    	input "switch3", "capability.switch", required: false
    }
    section("Lock") {
    	input "myLock", "capability.lock", required: false
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when Opened?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	state.onCnt = 0
    state.onTime = 0
    subscribe(app, appHandler)
    subscribe(switch1, "switch", switchHandler)
    subscribe(switch2, "switch", switchHandler)
    subscribe(switch3, "switch", switchHandler)
    subscribe(myLock, "lock", lockHandler)
    subscribe(myLock, "codeReport", lockCodeHandler)
    subscribe(myLock, "reportAllCodes", reportAllCodesHandler)
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"

	
//	def code = myLock.requestCode("1")
//	log.debug "code = ${code}"
	def codes = myLock.currentValue("lockCodes")
    log.debug "codes = ${codes}"
    myLock.poll()
    
/*
	def lockCommands = myLock.supportedCommands
    log.debug "Commands: ${lockCommands}"
    def caps = myLock.capabilities
    caps.commands.each { comm ->
    	log.debug "Command Name: ${comm.name}"
    }
*/
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
                	if (switch1.displayName != evt.displayName) {
                    	log.debug "${switch1} != ${evt.displayName}"
                        if (switch1.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch1} off..."
                        	switch1.off()
                        }
                    }
                	break
                case 1:
                	if (switch2.displayName != evt.displayName) {
                    	log.debug "${switch2} != ${evt.displayName}"
                        if (switch2.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch2} off..."
                        	switch2.off()
                        }
                    }
                	break
                case 2:
                	if (switch3.displayName != evt.displayName) {
                    	log.debug "${switch3} != ${evt.displayName}"
                        if (switch3.currentValue("switch") == "on") {
                        	log.debug "Turning ${switch3} off..."
                        	switch3.off()
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