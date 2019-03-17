/**
 *  Button Controller Smart App
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
    name: "Restful Button Controller",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Testing Rest services. Defines the behavior of selected button devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Cyndi's Button") {
    	input "button1", "capability.button", required: false, title: "Select Cyndi's Button"
        input "button1_pushed", "capability.switch", multiple: true, required: false, title: "What switches do you want to toggle when pressed?"
        input "button1_double", "capability.switch", multiple: true, required: false, title: "What switches do you want to toggle when double-pressed?"
        input "button1_held", "capability.switch", multiple: true, required: false, title: "What switches do you want to toggle when held?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false, title: "Send Push Notification when a button is pressed?"
    }
    section("Send a text message to this number") {
        input "phone", "phone", required: false, title: "Send a text when a button is pressed?"
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
    subscribe(button1, "button", button1Handler)
    subscribe(button2, "button", button2Handler)
}

import groovy.time.TimeCategory

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/switches/:command") {
    action: [
      POST: "updateSwitches"
    ]
  }
}

def listSwitches() {
	log.debug "listSwitches"

	def resp = []
    resp << [name: button1_pushed.label, value: button1_pushed.currentValue("switch")]
//    resp << [name: button1_held, value: button1_held.currentValue("switch")]
    log.debug "resp = ${resp}"
	return resp
    
/*
    def resp = []
    switches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
*/
}

def updateSwitches() {
	log.debug "updateSwitches"

    // use the built-in request object to get the command parameter
    log.debug "params.command = ${params.command}"
    def command = "${params.command}"

	def resp = []

    // all switches have the command
    // execute the command on all switches
    // (note we can do this on the array - the command will be invoked on every element
    switch(command) {
        case "on":
            button1_pushed.on()
            log.debug "Turning on ${button1_pushed}"
            break
        case "off":
            button1_pushed.off()
            log.debug "Turning off ${button1_pushed}"
            break
        default:
        	log.debug "Invalid Command"
            httpError(400, "$command is not a valid command for all switches specified")
    }
    resp << [name: button1_pushed.label, value: button1_pushed.currentValue("switch")]
    def reply = "Success!"
//    render contentType: "text/html", data: reply, status: 200
	return resp
}

def button1Handler(evt) {
	log.debug "button1Handler(${evt.descriptionText})"
	def date = new Date().format("MM/dd/yy hh:mm a", location.timeZone)
    def msg = "${location} ${date}: ${evt.descriptionText}\n"
    switch (evt.value) {
    	case "pushed":
        	if (button1_pushed) {
                log.debug "button1_pushed.currentSwitch == ${button1_pushed.find{it.currentSwitch == "on"}}"
                if (button1_pushed.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button1_pushed.off()"
                    button1_pushed?.off()
                    msg = msg + "Turning off ${button1_pushed}"
                }
                else {
                    log.debug "Executing button1_pushed.on()"
                    button1_pushed?.on()
                    msg = msg + "Turning on ${button1_pushed}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        case "double":
        	if (button1_double) {
                if (button1_double.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button1_double.off()"
                    button1_double?.off()
                    msg = msg + "Turning off ${button1_double}"
                }
                else {
                    log.debug "Executing button1_double.on()"
                    button1_double?.on()
                    msg = msg + "Turning on ${button1_double}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        case "held":
        	if (button1_held) {
                if (button1_held.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button1_held.off()"
                    button1_held?.off()
                    msg = msg + "Turning off ${button1_held}"
                }
                else {
                    log.debug "Executing button1_held.on()"
                    button1_held?.on()
                    msg = msg + "Turning on ${button1_held}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        default:
        	log.debug "Unknown button value"
            msg = msg + "Unknown button event!"
    }
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}

def button2Handler(evt) {
	log.debug "button2Handler(${evt.descriptionText})"
	def date = new Date().format("MM/dd/yy hh:mm a", location.timeZone)
    def msg = "${location} ${date}: ${evt.descriptionText}\n"
    switch (evt.value) {
    	case "pushed":
        	if (button2_pushed) {
                log.debug "button2_pushed.currentSwitch == ${button2_pushed.find{it.currentSwitch == "on"}}"
                if (button2_pushed.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button2_pushed.off()"
                    button2_pushed?.off()
                    msg = msg + "Turning off ${button2_pushed}"
                }
                else {
                    log.debug "Executing button2_pushed.on()"
                    button2_pushed?.on()
                    msg = msg + "Turning on ${button2_pushed}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        case "double":
        	if (button2_double) {
                if (button2_double.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button2_double.off()"
                    button2_double?.off()
                    msg = msg + "Turning off ${button2_double}"
                }
                else {
                    log.debug "Executing button2_double.on()"
                    button2_double?.on()
                    msg = msg + "Turning on ${button2_double}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        case "held":
        	if (button2_held) {
                if (button2_held.find{it.currentSwitch == "on"}) {
                    log.debug "Executing button2_held.off()"
                    button2_held?.off()
                    msg = msg + "Turning off ${button2_held}"
                }
                else {
                    log.debug "Executing button2_held.on()"
                    button2_held?.on()
                    msg = msg + "Turning on ${button2_held}"
                }
            }
            else {
            	msg = msg + "No switches defined for this event!"
            }
            break
        default:
        	log.debug "Unknown button value"
            msg = msg + "Unknown button event!"
    }
    if (sendPush) {
    	sendPush(msg)
    }
    if (phone) {
    	sendSms(phone, msg)
    }
}