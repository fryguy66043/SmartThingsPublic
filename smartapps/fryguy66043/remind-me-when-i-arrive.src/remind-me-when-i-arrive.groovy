/**
 *  Remind Me When I Arrive
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
    name: "Remind Me When I Arrive",
    namespace: "Fryguy66043",
    author: "Jeffrey Fry",
    description: "Give me a message when I arrive home.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
	section("When I Am Home..."){
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
	}
	section("Short Message"){
		input "message", "text", required: true, title: "Short Message"
	}
	section("How Many Minutes After I'm Home?") {
    	input "minutes", "number", required: false, title: "Minutes?", defaultValue: 0
    }
	section("Remind Every Time I Arrive?") {
    	input "everytime", "bool", required: false, title: "Every Arrival?"
    }
	section("Send Push Notification?") {
        input "sendPush", "bool", required: false,
              title: "Send Push Notification When Home?"
    }
    section("Send a text message to this number (optional)") {
        input "phone", "phone", required: false
    }
}

def installed()
{
    state.sent = false
	subscribe(presence1, "presence", presenceHandler)
    subscribe(app, appHandler)
}

def updated()
{
	unsubscribe()
    installed()
}

def appHandler(evt) {
	def msg = "${message}  Delivered:  ${state.sent}"

	if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
    if (!sendPush && ! phone) {
    	sendPush("Reminder Not Currently Enabled.")
    }
}

def presenceHandler(evt)
{
	def current = presence1.currentValue("presence")
    def presenceValue = presence1.find{it.currentPresence == "present"}
	def sendMessage = true
	log.debug current
	log.debug presenceValue
    if (!everytime && state.sent) {
    	sendMessage = false
    }
	if (presenceValue && sendMessage) {
    	runIn(60*minutes, msgHandler)
	}
}

def msgHandler(evt)
{
	def date = new Date().format("MM/dd/yy h:mm:ss a", location.timeZone)
	def msg = "${location} ${date}: Reminder!\n${message}"
    
    state.sent = true
    if (sendPush) {
        sendPush(msg)
    }
    if (phone) {
        sendSms(phone, msg)
    }
}
