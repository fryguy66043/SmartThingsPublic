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

import groovy.json.JsonSlurper

preferences {
    section("Select a SmartLock") {
    	input "myLock", "capability.lock", title: "Which SmartLock?"
    }
    section("Enter User Names For Each Code") {
    	paragraph "Only needed if the lock doesn't provide the code name and you want a name to display.  If the lock returns a code name, it will be used instead of the names below."
    	input "code1", "text", required: false, title: "Code 1 User."
    	input "code2", "text", required: false, title: "Code 2 User."
    	input "code3", "text", required: false, title: "Code 3 User."
    	input "code4", "text", required: false, title: "Code 4 User."
    	input "code5", "text", required: false, title: "Code 5 User."
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
/*
	def lockCommands = myLock.supportedCommands
    log.debug "Commands: ${lockCommands}"
    def caps = myLock.capabilities
    caps.commands.each { comm ->
    	log.debug "Command Name: ${comm.name}"
    }
*/    
}

def lockHandler(evt) {
	log.debug "lockHandler"
    def dataString = "${evt?.data}"
    def index = 0
    def user = ""
    def codeId = ""
    def method = "command"
    def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
    
	log.debug "name: ${evt.name} / displayName: ${evt.displayName} / value: ${evt.value} / data: ${evt?.data} / size: ${evt?.data?.size()}"
	def codes = "${myLock.currentValue("lockCodes")}"
    
    sendSms("9136679526", "${location}: evt.data = ${dataString} / lockCodes = ${codes}")


    def data = new JsonSlurper().parseText(evt.data)
    log.debug "data.codeName = '${data?.codeName}' / data.method = '${data?.method}' / data.lockName = '${data?.lockName}' / data.codeId = '${data?.codeId}'"

    if (data.codeName) {
        user = data.codeName
        log.debug "user = ${user}"
    }
    if (data.codeId) {
    	codeId = "${data.codeId}"
        log.debug "codeId = ${codeId}"
    }
    if (data.method) {
        method = data.method
        log.debug "method = ${method}"
    }

	if (evt.value == "unlocked") {
    	if (!user && codeId) {
        	log.debug "!user && codeId = ${codeId}"
            def users = new JsonSlurper().parseText(codes)
            if (users) {
                log.debug "passed users..."
                users.each {k, v -> 
                    log.debug "k = ${k} / v = ${v}"
                    if (k == "${codeId}") {
                        log.debug "Found user: ${v}"
                        user = v
                    }
                }
            }
            if (!user) {
                switch ("${codeId}") {
                    case "1":
                        user = code1
                        log.debug "code1"
                        break
                    case "2":
                        user = code2
                        log.debug "code2"
                        break
                    case "3":
                        user = code3
                        log.debug "code3"
                        break
                    case "4":
                        user = code4
                        log.debug "code4"
                        break
                    case "5":
                        log.debug "code5"
                        user = code5
                        break
                    default:
                        user = "${codeId}"
                        break
                }
            }
        }
        log.debug "user = ${user}"
    }

	def msg = "${location} ${date}: ${evt.displayName} was ${evt.value}"
    log.debug "user = ${user} / method = ${method} / codeId = ${codeId}"
    if (user) {
    	msg = msg + " by user code: ${user}."
    }
    else if (method) {
    	if (method == "manual") {
        	msg = msg + " manually."
        }
        else if (method == "command") {
        	msg = msg + " by command."
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

