/**

 *  SmartPINs

 *

 *  Copyright 2016 Jake Tebbett

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

 * VERSION CONTROL - Plex Plus Parent

 * ###############

 *

 *  v0.1 - Quickly pulled together PIN eentry mechanism using virtual switches

 *  v0.2 - Added dual PINs and triggers

 *  v0.3 - Fixed major error, added routines and custom seconds before reset

 *  v0.4 - Added advanced option to require "Enter" button to be pressed to confirm PIN for additional security (optional)

 *  v0.5 - Bug fix from last change.

 *  v0.6 - Timer reset on clear and successful PIN

 *

 */







definition(

    name: "SmartPINs",

    namespace: "jebbett",

    author: "Jake Tebbett",

    description: "PIN entry for SmartTiles",

    category: "My Apps",

    iconUrl: "https://raw.githubusercontent.com/jebbett/SmartPINs/master/Resources/SmartPINs.png",

    iconX2Url: "https://raw.githubusercontent.com/jebbett/SmartPINs/master/Resources/SmartPINs.png",

    iconX3Url: "https://raw.githubusercontent.com/jebbett/SmartPINs/master/Resources/SmartPINs.png")





preferences {

    page name: "mainPage", install: true, uninstall: true

}



def installed() {

    initialize()

}







def updated() {

    log.debug "Updated with settings: ${settings}"

    unsubscribe()

    initialize()

}



def initialize() {

    subscribe(b0, "switch.on", triggerB0)

    subscribe(b1, "switch.on", triggerB1)

    subscribe(b2, "switch.on", triggerB2)

    subscribe(b3, "switch.on", triggerB3)

    subscribe(b4, "switch.on", triggerB4)

    subscribe(b5, "switch.on", triggerB5)

    subscribe(b6, "switch.on", triggerB6)

    subscribe(b7, "switch.on", triggerB7)

    subscribe(b8, "switch.on", triggerB8)

    subscribe(b9, "switch.on", triggerB9)

    subscribe(b_Clear, "switch.on", triggerBC)

    subscribe(b_Enter, "switch.on", triggerBE)

    state.pinValue = "P"

    log.debug "Installed with settings: ${settings}"

}





def mainPage() {

        

    dynamicPage(name: "mainPage", uninstall: true, install: true) {

	section() {

		paragraph "Using virtual momentary switches, this app will allow you to enter up to 2 PINs to trigger a switch or routine"

	}

    

    //Get defined routines

	def actions = location.helloHome?.getPhrases()*.label

		if (actions) {

			actions.sort()

		}

    

    

    

	section(title: "Buttons", hidden: true, hideable: true) {

    		paragraph "All buttons below are optional, you only need to select as many as you need, Clear automatically takes place after $pSecs seconds and Enter is only required if Enter required setting is set"

            input "b0", "capability.switch", title:"0", multiple: false, required: false

            input "b1", "capability.switch", title:"1", multiple: false, required: false

            input "b2", "capability.switch", title:"2", multiple: false, required: false

            input "b3", "capability.switch", title:"3", multiple: false, required: false

            input "b4", "capability.switch", title:"4", multiple: false, required: false

            input "b5", "capability.switch", title:"5", multiple: false, required: false

            input "b6", "capability.switch", title:"6", multiple: false, required: false

            input "b7", "capability.switch", title:"7", multiple: false, required: false

            input "b8", "capability.switch", title:"8", multiple: false, required: false

            input "b9", "capability.switch", title:"9", multiple: false, required: false

            input "b_Clear", "capability.switch", title:"Cancel Button", multiple: false, required: false   

            input "b_Enter", "capability.switch", title:"Enter Button", multiple: false, required: false

        }



	section(title: "Triggers", hidden: true, hideable: true) {

            input "triggerSW", "capability.switch", title:"Switch - Correct PIN 1", multiple: false, required: false

            input "triggerSW2", "capability.switch", title:"Switch - Correct PIN 2", multiple: false, required: false

            if (actions) {

				input "routine1", "enum", title: "Routine - Correct PIN 1", required:false, options: actions

				input "routine2", "enum", title: "Routine - Correct PIN 2", required:false, options: actions

			}

        }



	section(title: "PINs", hidden: true, hideable: true) {

            input(name: "pinNum", type: "text", title: "PIN 1", required:false)

            input(name: "pinNum2", type: "text", title: "PIN 2", required:false)

        }

        

    section(title: "Additional Settings", hidden: true, hideable: true) {

        	input(name: "pSecs", type: "number", defaultValue: "5", title: "Number of seconds before PIN entry resets", required:false)

            input "reqEnter", "bool", title: "Require Enter button to confirm PIN", required: false, defaultValue: false

        }

        

        section(title: "Debug Logging", hidden: true, hideable: true) {

       		paragraph "If you experiencing issues please enable logging to help troubleshoot"

            input "debugLogging", "bool", title: "Debug Logging...", required: false, defaultValue: false, refreshAfterSelection: true

            	

            if (debugLogging) { 

            	state.debugLogging = true 

                logWriter("Debug Logging is ${state.debugLogging.toString().toUpperCase()}")

            }

            else { 

            	state.debugLogging = false     

            }

    	}



    }

}





def triggerB0(evt) {

    updatePIN(0)

}

def triggerB1(evt) {

    updatePIN(1)

}

def triggerB2(evt) {

    updatePIN(2)

}

def triggerB3(evt) {

    updatePIN(3)

}

def triggerB4(evt) {

    updatePIN(4)

}

def triggerB5(evt) {

    updatePIN(5)

}

def triggerB6(evt) {

    updatePIN(6)

}

def triggerB7(evt) {

    updatePIN(7)

}

def triggerB8(evt) {

    updatePIN(8)

}

def triggerB9(evt) {

    updatePIN(9)

}

def triggerBC(evt) {

    resetPIN()

}

def triggerBE(evt) {

    checkPIN()

}





def updatePIN (numVal) {

   

    // Just in case PIN does not have P prefix

    if (state.pinValue == null) {

				resetPIN()

    }

       	else

    {

       	state.pinValue = state.pinValue + numVal

    }

    

	// Reset PIN entry after X seconds

	runIn(pSecs, resetPIN, [overwrite: true])

    

	//Check PIN if Enter not required

	if (reqEnter == false) {

    	checkPIN()

    }



}



def checkPIN () {



	def myPin = "P${pinNum}"

	def myPin2 = "P${pinNum2}"

	def yourPin = state.pinValue

    

	logWriter ("Entered PIN is $yourPin")

	logWriter ("PIN1 is $myPin")

    logWriter ("PIN2 is $myPin2")



	//Check if match against either specified correct PIN

	if (myPin == yourPin){

    

    	settings."triggerSW"?.on()

		ExecRoutine(routine1)

        logWriter ("UNLOCKED with PIN 1")

		resetPIN()

	}

	else if (myPin2 == yourPin){

    	settings."triggerSW2"?.on()

        ExecRoutine(routine2)

		logWriter ("UNLOCKED with PIN 2")

		resetPIN()

	}

    else

    {

    	logWriter ("No matching PIN")

    }

}





def resetPIN () {

	state.pinValue = "P"

    logWriter ("PIN Reset")

    unschedule("resetPIN")

    

}





private def logWriter(value) {

	if (state.debugLogging) {

        log.debug "${value}"

    }	

}



def ExecRoutine(routine) {

	if(!routine) return

	location.helloHome?.execute(routine)

}