/**
 *  Update Virtual Switch w/ REST Services.
 *
 *  Copyright 2019 Jeffrey Fry
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
    name: "Update Virtual Switch (REST) App",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Update Virtual Switch with REST Service Calls.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Virtual Switches") {
    	input "sNotUsed", "capability.switch", title: "Not Used"
    	input "sHome", "capability.switch", title: "Home"
    	input "sCurves", "capability.switch", title: "Curves"
    	input "sDoctor", "capability.switch", title: "Doctor"
    	input "sHeart", "capability.switch", title: "Heart Clinic"
    	input "sHeritageCenter", "capability.switch", title: "Heritage Center"
    	input "sJeffAndCyndi", "capability.switch", title: "Jeff & Cyndi"
    	input "sNutritionCenter", "capability.switch", title: "Nutrition Center"
    	input "sVet", "capability.switch", title: "Vet"
    	input "sWalmart", "capability.switch", title: "Walmart"
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
}

def initialize() {
}

mappings {
  path("/depart/:command") {
    action: [
      GET: "departLocation"
    ]
  }
  path("/arrive/:command") {
    action: [
      GET: "arriveLocation"
    ]
  }
}


def departLocation() {
	state.setLocationTime = state.setLocationTime ?: new Date().format("yyyy-MM-dd HH:mm:ss")
    def lastDate = Date.parse("yyyy-MM-dd HH:mm:ss", state.setLocationTime)
    def curDate = new Date()
    def pass = true
    use (groovy.time.TimeCategory) {
    	if (curDate < lastDate - 5.seconds) {
        	log.debug "Executed within 5 seconds.  Skipping!"
        	pass = false
        }
    }
	log.debug "departLocation(${params.command})"
    
    def resp = []
    def command = params.command
    resp << [name: "Depart", value: command]
    if (pass) {
        switch(command) {
            case "Dee":
                log.debug "Departing Home"
                sHome.off()
                break
            case "Curves":
            	log.debug "Departing Curves"
                sCurves.off()
                break
            case "Doctors":
            	log.debug "Departing Doctors"
                sDoctor.off()
                break
            case "Heart Clinic":
            	log.debug "Departing Heart Clinic"
                sHeartClinic.off()
                break
            case "Heritage Center":
            	log.debug "Departing Heritage Center"
                sHeritageCenter.off()
                break
            case "Jeff & Cyndi":
            	log.debug "Departing Jeff & Cyndi"
                sJeffAndCyndi.off()
                break
            case "Nutrition Center":
            	log.debug "Departing Nutrition Center"
                sNutritionCenter.off()
                break
            case "Pioneer Vet Clinic":
            	log.debug "Departing Vet"
                sVet.off()
                break
            case "Walmart":
            	log.debug "Departing Walmart"
                sWalmart.off()
                break
            default:
                log.debug "Invalid Command"
//                httpError(400, "Command '${command}' is not valid for Smart Alarm Clock")
                break
        }
    }
    else {
    	log.debug "Skipping.  Executed too quickly."
        resp << [name: "Execution", value: "Too Soon!"]
    }
    return resp
}

def arriveLocation() {
	state.setLocationTime = state.setLocationTime ?: new Date().format("yyyy-MM-dd HH:mm:ss")
    def lastDate = Date.parse("yyyy-MM-dd HH:mm:ss", state.setLocationTime)
    def curDate = new Date()
    def pass = true
    use (groovy.time.TimeCategory) {
    	if (curDate < lastDate - 5.seconds) {
        	log.debug "Executed within 5 seconds.  Skipping!"
        	pass = false
        }
    }
	log.debug "arriveLocation(${params.command})"
    
    def resp = []
    def command = params.command
    resp << [name: "Arrive", value: command]
    if (pass) {
        switch(command) {
            case "Dee":
                log.debug "Arriving Home"
                sHome.on()
                break
            case "Curves":
            	log.debug "Arriving Curves"
                sCurves.on()
                break
            case "Doctors":
            	log.debug "Arriving Doctors"
                sDoctor.on()
                break
            case "Heart Clinic":
            	log.debug "Arriving Heart Clinic"
                sHeartClinic.on()
                break
            case "Heritage Center":
            	log.debug "Arriving Heritage Center"
                sHeritageCenter.on()
                break
            case "Jeff & Cyndi":
            	log.debug "Arriving Jeff & Cyndi"
                sJeffAndCyndi.on()
                break
            case "Nutrition Center":
            	log.debug "Arriving Nutrition Center"
                sNutritionCenter.on()
                break
            case "Pioneer Vet Clinic":
            	log.debug "Arriving Vet"
                sVet.on()
                break
            case "Walmart":
            	log.debug "Arriving Walmart"
                sWalmart.on()
                break
            default:
                log.debug "Invalid Command"
//                httpError(400, "Command '${command}' is not valid for Smart Alarm Clock")
                break
        }
    }
    else {
    	log.debug "Skipping.  Executed too quickly."
        resp << [name: "Execution", value: "Too Soon!"]
    }
    return resp
}
