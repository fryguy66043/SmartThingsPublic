/**
 *  Pet Feeder App.
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
    name: "Pet Feeder App",
    namespace: "FryGuy66043",
    author: "Jeffrey Fry",
    description: "Pet Feeder App.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Pet Feeder") {
    	input "feeder", "device.petfeeder", title: "Select your Pet Feeder."
        input "sched1", "time", required: false, title: "1st feed time?"
        input "sched2", "time", required: false, title: "2nd feed time?"
        input "sched3", "time", required: false, title: "3rd feed time?"
        input "sched4", "time", required: false, title: "4th feed time?"
        input "sched5", "time", required: false, title: "5th feed time?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", title: "Send Push Notification when alarm is activated?"
    }
    section("Send a Text Message?") {
        input "phone", "phone", required: false, title: "Send a Text Message when alarm is activated?"
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
    def aDate = ""
    def aTime = ""
    def cmd = ""
    def cnt = 0
    if (sched1) {
    	aDate = new Date(timeToday(sched1).time)
    	aTime = aDate.format("HH:mm", location.timeZone)
    	cmd = "?one=${aTime}"
        cnt++
    }
    if (sched2) {
    	aDate = new Date(timeToday(sched2).time)
    	aTime = aDate.format("HH:mm", location.timeZone)
    	if (cnt == 1) {
            cmd += "&two=${aTime}"
        }
        else {
        	cmd = "?one=${aTime}"
        }
        cnt++
    }
    if (sched3) {
    	aDate = new Date(timeToday(sched3).time)
    	aTime = aDate.format("HH:mm", location.timeZone)
    	if (cnt > 0) {
        	switch (cnt) {
            	case 1:
                	cmd += "&two=${aTime}"
                    break
                case 2:
                	cmd += "&three=${aTime}"
                    break
            }
        }
        else {
        	cmd = "?one=${aTime}"
        }
        cnt++
    }
    if (sched4) {
    	aDate = new Date(timeToday(sched4).time)
    	aTime = aDate.format("HH:mm", location.timeZone)
    	if (cnt > 0) {
        	switch (cnt) {
            	case 1:
                	cmd += "&two=${aTime}"
                    break
                case 2:
                	cmd += "&three=${aTime}"
                    break
                case 3:
                	cmd += "&four=${aTime}"
                    break
            }
        }
        else {
        	cmd = "?one=${aTime}"
        }
        cnt++
    } 
    if (sched5) {
    	aDate = new Date(timeToday(sched5).time)
    	aTime = aDate.format("HH:mm", location.timeZone)
    	if (cnt > 0) {
        	switch (cnt) {
            	case 1:
                	cmd += "&two=${aTime}"
                    break
                case 2:
                	cmd += "&three=${aTime}"
                    break
                case 3:
                	cmd += "&four=${aTime}"
                    break
                case 4:
                	cmd += "&five=${aTime}"
            }
        }
        else {
        	cmd = "?one=${aTime}"
        }
        cnt++
    }
    log.debug "cnt = ${cnt} / cmd = ${cmd}"
    feeder.setSchedule(cmd)
}

def appHandler(evt) {
	log.debug "appHandler"
    feeder.refresh()
}
