/**
 *  Control a SmartLock.
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
    name: "Smartlock Controller",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Control a SmartLock.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Select a SmartLock") {
    	input "myLock", "capability.lock", title: "Which SmartLock?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification when Locked/Unlocked?"
    }
    section("Send a text message?") {
        input "phone", "phone", required: false, title: "Optionally send a text message to this number."
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
    subscribe(app, appHandler)
    subscribe(myLock, "lock", lockHandler)
}

def appHandler(evt) {
	log.debug "appHandler: ${evt.value}"

	def lockCommands = myLock.supportedCommands
    log.debug "Commands: ${lockCommands}"
    def caps = myLock.capabilities
    caps.commands.each { comm ->
    	log.debug "Command Name: ${comm.name}"
    }
}

def lockHandler(evt) {
	log.debug "lockHandler"
    def dataString = "${evt.data}"
    def index = 0
    def user = ""
    def method = ""
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
	log.debug "name: ${evt.name} / displayName: ${evt.displayName} / value: ${evt.value} / data: ${evt?.data} / size: ${evt.data.size()}"

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
        index = dataString.indexOf("method")
        if (index > -1) {
        	method = dataString.substring(index+9, dataString.size())
            index = method.indexOf(",")
            method = method.substring(0, index-1)
        }
    }

	def msg = "${location} ${date}: ${evt.displayName} was ${evt.value}"
    if (user) {
    	msg = msg + " by code: ${user}."
    }
    else if (method) {
    	if (method == "manual") {
        	msg = msg + " manually."
        }
        else {
	    	msg = msg + " by ${method}."
        }
    }
    else {
    	msg = msg + "."
    }
	log.debug msg
    
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

