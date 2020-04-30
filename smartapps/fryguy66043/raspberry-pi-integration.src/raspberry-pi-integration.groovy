definition(
    name: "Raspberry Pi Integration",
    namespace: "FryGuy66043", //Use Your Namespace here.  Whatever you want it to be across your apps.
    author: "Jeffrey Fry",    //Use Your Name Here.
    description: "Raspberry Pi Server Integration",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
	section ("Home") {
    	input "forecast", "device.mySmartweatherTile", required: false, title: "Select Forecast Provider."
        input "jeff", "capability.presenceSensor", required: false, title: "Select Jeff's Phone."
        input "cyndi", "capability.presenceSensor", required: false, title: "Select Cyndi's Phone."
        input "server", "text", required: true, title: "Enter the IP Address for the Server."
        input "port", "text", required: true, title: "Enter the Port for the Server."
    }
    section ("Garage") {
    	input "garage_display", "text", required: false, title: "Enter Display Name for Garage."
        input "garage_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "garage_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Garage."
        input "garage_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Garage."
        input "garage_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Garage."
        input "garage_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Garage."
        input "garage_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Garage."
        input "garage_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Garage."
    }
    section ("Living Room") {
    	input "lr_display", "text", required: false, title: "Enter Display Name for Living Room."
        input "lr_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "lr_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Living Room."
        input "lr_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Living Room."
        input "lr_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Living Room."
        input "lr_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Living Room."
        input "lr_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Living Room."
        input "lr_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Living Room."
    }
    section ("Kitchen") {
    	input "kitchen_display", "text", required: false, title: "Enter Display Name for Kitchen."
        input "kitchen_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "kitchen_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Kitchen."
        input "kitchen_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Kitchen."
        input "kitchen_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Kitchen."
        input "kitchen_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Kitchen."
        input "kitchen_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Kitchen."
        input "kitchen_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Kitchen."
    }
    section ("Outside") {
    	input "outside_display", "text", required: false, title: "Enter Display Name for Outside."
        input "outside_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "outside_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Outside."
        input "outside_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Outside."
        input "outside_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Outside."
        input "outside_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Outside."
        input "outside_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Outside."
        input "outside_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Outside."
    }
    section ("Basement") {
    	input "basement_display", "text", required: false, title: "Enter Display Name for Basement."
        input "basement_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "basement_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Basement."
        input "basement_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Basement."
        input "basement_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Basement."
        input "basement_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Basement."
        input "basement_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Basement."
        input "basement_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Basement."
    }
    section ("Family Room") {
    	input "fr_display", "text", required: false, title: "Enter Display Name for Family Room."
        input "fr_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "fr_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Family Room."
        input "fr_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Family Room."
        input "fr_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Family Room."
        input "fr_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Family Room."
        input "fr_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Family Room."
        input "fr_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Family Room."
    }
    section ("Master") {
    	input "master_display", "text", required: false, title: "Enter Display Name for Master."
        input "master_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "master_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Master."
        input "master_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Master."
        input "master_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Master."
        input "master_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Master."
        input "master_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Master."
        input "master_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Master."
    }
    section ("Master Bath") {
    	input "mb_display", "text", required: false, title: "Enter Display Name for Master Bath."
        input "mb_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "mb_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Master Bath."
        input "mb_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Master Bath."
        input "mb_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Master Bath."
        input "mb_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Master Bath."
        input "mb_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Master Bath."
        input "mb_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Master Bath."
    }
    section ("Bedroom1") {
    	input "br1_display", "text", required: false, title: "Enter Display Name for Bedroom1."
        input "br1_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "br1_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom1."
        input "br1_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom1."
        input "br1_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom1."
        input "br1_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom1."
        input "br1_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom1."
        input "br1_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Bedroom1."
    }
    section ("Bedroom2") {
    	input "br2_display", "text", required: false, title: "Enter Display Name for Bedroom1."
        input "br2_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "br2_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom2."
        input "br2_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom2."
        input "br2_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom2."
        input "br2_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom2."
        input "br2_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom2."
        input "br2_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Bedroom2."
    }
    section ("Bedroom3") {
    	input "br3_display", "text", required: false, title: "Enter Display Name for Bedroom3."
        input "br3_idx", "number", required: false, title: "What order do you want this to display in?"
    	input "br3_lights", "capability.switch", required: false, multiple: true, title: "Select Lights and Switches for Bedroom3."
        input "br3_doors", "capability.doorControl", required: false, multiple: true, title: "Select Door Controls for Bedroom3."
        input "br3_locks", "capability.lock", required: false, multiple: true, title: "Select Locks for Bedroom3."
        input "br3_contacts", "capability.contactSensor", required: false, multiple: true, title: "Select Contacts for Bedroom3."
        input "br3_temps", "capability.temperatureMeasurement", required: false, multiple: false, title: "Select Temperature Sensor for Bedroom3."
        input "br3_humid", "capability.relativeHumidityMeasurement", required: false, multiple: false, title: "Select Humidity Sensor for Bedroom3."
    }
	section("Send Push Notification Reminder?") {
        input "sendPush", "bool", required: false, title: "Yes or No?"
    }
    section("Send Text Message Reminder?") {
        input "phone", "phone", required: false, title: "Phone Number"
    }
}

def installed()
{
	initialize()
}

def updated()
{
	log.debug "updated()"
    
	unsubscribe()
    initialize()
    scheduledUpdates()
    runEvery5Minutes(scheduledUpdates)
}

def scheduledUpdates() {
	log.debug "scheduledUpdates()"
    def server_addr = "${server}:${port}"
    sendHubCommand(new physicalgraph.device.HubAction("""GET /update_req HTTP/1.1\r\nHOST: ${server_addr}\r\n\r\n""", 
    				physicalgraph.device.Protocol.LAN, 
                    "" ,
                    [callback: updateReqHandler]))
}

def updateReqHandler(reply) {
	log.debug "updateReqHandler: ${reply.status} / ${reply.body}"
}

def initialize()
{
    subscribe(jeff, "presence", changeHandler)
    subscribe(cyndi, "presence", changeHandler)
    
    subscribe(forecast, "observation_json", wxObsHandler)
//    subscribe(forecast, "forecast_json", wxFcHandler)

	subscribe(garage_lights, "switch", changeHandler)
    subscribe(garage_doors, "door", changeHandler)
    subscribe(garage_locks, "lock", changeHandler)
    subscribe(garage_contacts, "contact", changeHandler)
    subscribe(garage_temps, "temperature", changeHandler)
    subscribe(garage_humid, "humidity", changeHandler)

	subscribe(lr_lights, "switch", changeHandler)
    subscribe(lr_doors, "door", changeHandler)
    subscribe(lr_locks, "lock", changeHandler)
    subscribe(lr_contacts, "contact", changeHandler)
    subscribe(lr_temps, "temperature", changeHandler)
    subscribe(lr_humid, "humidity", changeHandler)

	subscribe(kitchen_lights, "switch", changeHandler)
    subscribe(kitchen_doors, "door", changeHandler)
    subscribe(kitchen_locks, "lock", changeHandler)
    subscribe(kitchen_contacts, "contact", changeHandler)
    subscribe(kitchen_temps, "temperature", changeHandler)
    subscribe(kitchen_humid, "humidity", changeHandler)

	subscribe(outside_lights, "switch", changeHandler)
    subscribe(outside_doors, "door", changeHandler)
    subscribe(outside_locks, "lock", changeHandler)
    subscribe(outside_contacts, "contact", changeHandler)
    subscribe(outside_temps, "temperature", changeHandler)
    subscribe(outside_humid, "humidity", changeHandler)

	subscribe(basement_lights, "switch", changeHandler)
    subscribe(basement_doors, "door", changeHandler)
    subscribe(basement_locks, "lock", changeHandler)
    subscribe(basement_contacts, "contact", changeHandler)
    subscribe(basement_temps, "temperature", changeHandler)
    subscribe(basement_humid, "humidity", changeHandler)

	subscribe(fr_lights, "switch", changeHandler)
    subscribe(fr_lights, "level", changeHandler)
    subscribe(fr_doors, "door", changeHandler)
    subscribe(fr_locks, "lock", changeHandler)
    subscribe(fr_contacts, "contact", changeHandler)
    subscribe(fr_temps, "temperature", changeHandler)
    subscribe(fr_humid, "humidity", changeHandler)

	subscribe(master_lights, "switch", changeHandler)
	subscribe(master_lights, "level", changeHandler)
    subscribe(master_doors, "door", changeHandler)
    subscribe(master_locks, "lock", changeHandler)
    subscribe(master_contacts, "contact", changeHandler)
    subscribe(master_temps, "temperature", changeHandler)
    subscribe(master_humid, "humidity", changeHandler)

	subscribe(mb_lights, "switch", changeHandler)
    subscribe(mb_doors, "door", changeHandler)
    subscribe(mb_locks, "lock", changeHandler)
    subscribe(mb_contacts, "contact", changeHandler)
    subscribe(mb_temps, "temperature", changeHandler)
    subscribe(mb_humid, "humidity", changeHandler)

	subscribe(br1_lights, "switch", changeHandler)
    subscribe(br1_doors, "door", changeHandler)
    subscribe(br1_locks, "lock", changeHandler)
    subscribe(br1_contacts, "contact", changeHandler)
    subscribe(br1_temps, "temperature", changeHandler)
    subscribe(br1_humid, "humidity", changeHandler)

	subscribe(br2_lights, "switch", changeHandler)
    subscribe(br2_doors, "door", changeHandler)
    subscribe(br2_locks, "lock", changeHandler)
    subscribe(br2_contacts, "contact", changeHandler)
    subscribe(br2_temps, "temperature", changeHandler)
    subscribe(br2_humid, "humidity", changeHandler)

	subscribe(br3_lights, "switch", changeHandler)
    subscribe(br3_doors, "door", changeHandler)
    subscribe(br3_locks, "lock", changeHandler)
    subscribe(br3_contacts, "contact", changeHandler)
    subscribe(br3_temps, "temperature", changeHandler)
    subscribe(br3_humid, "humidity", changeHandler)

    subscribe(app, appHandler)
}

mappings {
  path("/get_home_away") {
  	action: [
    	GET: "getHomeAway"
    ]
  }
  path("/send_message/:command") {
  	action: [
    	GET: "sendMessage"
    ]
  }
  path("/get_switch") {
  	action: [
    	GET: "getSwitch"
    ]
  }
  path("/set_switch/:command") {
  	action: [
    	GET: "setSwitch"
    ]
  }
  path("/set_level/:command") {
  	action: [
    	GET: "setLevel"
    ]
  }
  path("/set_lock/:command") {
  	action: [
    	GET: "setLock"
    ]
  }
  path("/set_door/:command") {
  	action: [
    	GET: "setDoor"
    ]
  }
  path("/get_rooms") {
  	action: [
    	GET: "getRooms"
    ]
  }
  path("/get_wx_obs") {
  	action: [
    	GET: "getWxObs"
    ]
  }
  path("/get_wx_fc") {
  	action: [
    	GET: "getWxFc"
    ]
  }
}

def appHandler(evt) {
	def capStr = ""
    master_lights.each {dev ->
    	capStr = "${dev.displayName}: "
        def capabilities = dev.capabilities
        for (cap in capabilities) {
        	capStr += "${cap.name} / "
        }
        log.debug capStr
    }
}

def wxHandler(evt) {
	log.debug "wxHandler()"
	wxScheduledUpdate()
    runEvery5Minutes(wxScheduledUpdate)
}

def wxScheduledUpdate(evt) {
	log.debug "wxScheduledUpdate"
    def server_addr = "${server}:${port}"
    sendHubCommand(new physicalgraph.device.HubAction("""GET /wx_obs_change HTTP/1.1\r\nHOST: ${server_addr}\r\n\r\n""", 
    				physicalgraph.device.Protocol.LAN, 
                    "" ,
                    [callback: updateWxHandler]))
}

def updateWxHandler(reply) {
	log.debug "updateWxHandler: ${reply.status} / ${reply.body}"
}

def wxObsHandler(evt) {
	log.debug "wxObsHandler()"
//    log.debug "json: ${forecast.currentValue("observation_json")}"
    wxHandler(evt)
}

def wxFcHandler(evt) {
	log.debug "wxFcHandler()"
//    log.debug "fc: ${moon:forecast.currentValue("forecast_json")}"
}

def getHomeAway() {
	log.debug "getHomeAway"
    def found = false
    def resp = []
    if (jeff) {
    	found = true
	    resp << [device: jeff.displayName, status: jeff.currentPresence]
    }
    if (cyndi) {
    	found = true
	    resp << [device: cyndi.displayName, status: cyndi.currentPresence]
    }
    if (!found) {
    	resp << [device: "Not Configured", status: "N/A"]
    }
//    log.debug "Reply: ${resp}"
    return resp
}

def getSwitch() {
	log.debug "getSwitch"
    def found = false
    def resp = []
    fr_lights.each { sw ->
    	found = true
    	resp << [switch: sw.label, status: sw.currentValue("switch")]
    }
    if (!found) {
    	resp << [switch: "Not Configured", status: "N/A"]
    }
    log.debug "Reply: ${resp}"
    return resp
}

def setSwitch() {
	log.debug "setSwitch($params.command)"
	def switches = [garage_lights, lr_lights, kitchen_lights, outside_lights, basement_lights, fr_lights, master_lights, mb_lights, br1_lights, br2_lights, br3_lights]
    def cmd = params.command
    def nidx = cmd.indexOf("=")
    def amp = cmd.indexOf("&")
    def sidx = cmd.indexOf("=", amp)
    def status = "Error in parameters"
    if (nidx > -1 && amp > -1 && sidx > -1) {
    	status = "OK"
    	def sName = cmd.substring(nidx+1, amp)
        def sState = cmd.substring(sidx+1)
        log.debug "sName: $sName / sState: $sState"
        status = "$sName was turned $sState"
        switches.each {rm ->
        	rm.each {dev ->
                if (sName == dev.displayName) {
                    if (sState == "on") {
                        log.debug "...turning on $sName"
                        dev.on()
                    }
                    else if (sState == "off") {
                        log.debug "...turning off $sName"
                        dev.off()
                    }
                    else {
                        status = "Invalid Switch Command: $sState"
                        log.debug status
                    }
                }
            }
        }
        if (phone) {
            sendSms(phone, status)
        }
    }
    return status
}

def setLevel() {
	log.debug "setLevel($params.command)"
	def switches = [garage_lights, lr_lights, kitchen_lights, outside_lights, basement_lights, fr_lights, master_lights, mb_lights, br1_lights, br2_lights, br3_lights]
    def cmd = ""
    def sName = ""
    def sLevel = ""
    def status = "Error in parameters"
    def command = params.command
    cmd = command.split('&')
    log.debug "cmd: ${cmd}"
    for (String value : cmd) {
        if (value.contains("switch")) {
            def nCmd = [] 
            nCmd = value.split('=')
            log.debug "sName = ${nCmd[1]}"
            sName = nCmd[1].trim()
        }
        else if (value.contains("level")) {
        	def nCmd = []
            nCmd = value.split('=')
            log.debug "sLevel = ${nCmd[1]}"
            sLevel = nCmd[1].trim()
        }
    }
    switches.each {rm ->
    	rm.each {dev ->
        	log.debug "${dev.displayName} = ${sName} : ${dev.displayName == sName}"
        	if (dev.displayName == sName) {
            	log.debug "Setting $sName to $sLevel"
                status = "OK - Setting $sName to $sLevel"
            	dev?.setLevel(sLevel.toInteger())
            }
        }
    }
    return status
}

def setLock() {
	log.debug "setLock($params.command)"
	def locks = [garage_locks, lr_locks, kitchen_locks, outside_locks, basement_locks, fr_locks, master_locks, mb_locks, br1_locks, br2_locks, br3_locks]
    def cmd = params.command
    def nidx = cmd.indexOf("=")
    def amp = cmd.indexOf("&")
    def sidx = cmd.indexOf("=", amp)
    def status = "Error in parameters"
    if (nidx > -1 && amp > -1 && sidx > -1) {
    	status = "OK"
    	def sName = cmd.substring(nidx+1, amp)
        def sState = cmd.substring(sidx+1)
        log.debug "sName: $sName / sState: $sState"
        status = "$sName was turned $sState"
        locks.each {rm ->
        	rm.each {dev ->
                if (sName == dev.displayName) {
                    if (sState == "lock") {
                        log.debug "...locking $sName"
                        dev.lock()
                    }
                    else if (sState == "unlock") {
                        log.debug "...unlocking $sName"
                        dev.unlock()
                    }
                    else {
                        status = "Invalid Switch Command: $sState"
                        log.debug status
                    }
                }
            }
        }
        if (phone) {
            sendSms(phone, status)
        }
    }
    return status
}

def setDoor() {
	log.debug "setDoor($params.command)"
	def rm_doors = [garage_doors, lr_doors, kitchen_doors, outside_doors, basement_doors, fr_doors, master_doors, mb_doors, br1_doors, br2_doors, br3_doors]
    def cmd = params.command
    def nidx = cmd.indexOf("=")
    def amp = cmd.indexOf("&")
    def sidx = cmd.indexOf("=", amp)
    def status = "Error in parameters"
    if (nidx > -1 && amp > -1 && sidx > -1) {
    	status = "OK"
    	def sName = cmd.substring(nidx+1, amp)
        def sState = cmd.substring(sidx+1)
        log.debug "sName: $sName / sState: $sState"
        status = "$sName was sent the command: $sState"
        rm_doors.each {rm ->
        	rm.each {dev ->
                if (sName == dev.displayName) {
                    if (sState == "open") {
                        log.debug "...opening $sName"
                        dev.open()
                    }
                    else if (sState == "close") {
                        log.debug "...closinging $sName"
                        dev.close()
                    }
                    else {
                        status = "Invalid Switch Command: $sState"
                        log.debug status
                    }
                }
            }
        }
        if (phone) {
            sendSms(phone, status)
        }
    }
    return status
}

def switchHandler(evt) {
	log.debug("switchHandler (${evt.displayName}/${evt.value})")

	def sname = URLEncoder.encode("${evt.displayName}", "UTF-8")
	def cmd = "?switch=${sname}&state=${evt.value}"
    
    def server_addr = "${server}:${port}"
    sendHubCommand(new physicalgraph.device.HubAction("""GET /switch_change${cmd} HTTP/1.1\r\nHOST: ${server_addr}\r\n\r\n""", 
    				physicalgraph.device.Protocol.LAN, 
                    "" ,
                    [callback: updateSwitchHandler]))
}

def updateSwitchHandler(reply) {
	log.debug "updateSwitchHandler: ${reply.status} / ${reply.body}"
}

def changeHandler(evt) {
	log.debug "changeHandler(${evt.displayName} / ${evt.value})"
    log.debug "name: ${evt.name} / unit: ${evt.unit}"
    def cmd = ""
    def path = "/device_change"
	def sname = URLEncoder.encode("${evt.displayName}", "UTF-8")

	if (evt.name == "switch") {
        garage_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        garage_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        garage_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }
    
    if (evt.name == "contact") {
        garage_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }
    
	if (evt.name == "temperature") {
        garage_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        garage_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        lr_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        lr_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        lr_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        lr_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        lr_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        lr_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        kitchen_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        kitchen_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        kitchen_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        kitchen_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        kitchen_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        kitchen_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        outside_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        outside_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        outside_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        outside_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        outside_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
	}
    if (evt.name == "humidity") {
        outside_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        basement_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        basement_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        basement_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        basement_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        basement_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        basement_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        fr_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
                def capabilities = dev.capabilities
                for (cap in capabilities) {
                    if (cap.name == "Switch Level") {
                        cmd += "&level=${dev.currentValue("level")}"
                    }
                }
            }
        }
    }
    
    if (evt.name == "door") {
        fr_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        fr_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        fr_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        fr_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }


    if (evt.name == "humidity") {
        fr_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        master_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
                def capabilities = dev.capabilities
                for (cap in capabilities) {
                    if (cap.name == "Switch Level") {
                        cmd += "&level=${dev.currentValue("level")}"
                    }
                }
            }
        }
    }
    
    if (evt.name == "door") {
        master_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        master_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        master_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        master_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        master_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        mb_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        mb_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        mb_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        mb_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        mb_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        mb_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }


	if (evt.name == "switch") {
        br1_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        br1_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        br1_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        br1_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        br1_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        br1_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        br2_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        br2_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        br2_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        br2_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        br2_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        br2_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	if (evt.name == "switch") {
        br3_lights.each {dev ->
            if (dev.displayName == evt.displayName) {
                log.debug "Found $dev"
                cmd = "?device=switch&name=${sname}&value=${dev.currentSwitch}"
            }
        }
    }
    
    if (evt.name == "door") {
        br3_doors.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=door&name=${sname}&value=${dev.currentValue("door")}"
            }
        }
    }
    
    if (evt.name == "lock") {
        br3_locks.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=lock&name=${sname}&value=${dev.currentValue("lock")}"
            }
        }
    }

    if (evt.name == "contact") {
        br3_contacts.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=contact&name=${sname}&value=${dev.currentValue("contact")}"
            }
        }
    }

	if (evt.name == "temperature") {
        br3_temps.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=temp&name=${sname}&value=${dev.currentTemperature}"
            }
        }
    }

    if (evt.name == "humidity") {
        br3_humid.each {dev ->
            if (dev.displayName == evt.displayName) {
                cmd = "?device=humidity&name=${sname}&value=${dev.currentValue("humidity")}"
            }
        }
    }

	log.debug ("cmd: $cmd")
    
    def server_addr = "${server}:${port}"
    sendHubCommand(new physicalgraph.device.HubAction("""GET /device_change${cmd} HTTP/1.1\r\nHOST: ${server_addr}\r\n\r\n""", 
    				physicalgraph.device.Protocol.LAN, 
                    "" ,
                    [callback: updateSwitchHandler]))
}

def getWxObs() {
	log.debug "getWxObs()"
    def resp = []
    
    resp << [forecast.currentValue("observation_json")]
//    log.debug "${resp}"
    return resp
}

def getWxFc() {
	log.debug "getWxFc()"
    def resp = []
    
    resp << [forecast:forecast.currentValue("forecast_json")]
//    log.debug "${resp}"
    return resp
}
def getRooms() {
	log.debug "getRooms()"

	def rooms = []
    
	def garageLights = []
    def garageDoors = []
    def garageLocks = []
    def garageContacts = []
    def garageTemps = []
    def garageHumid = []

	def lrLights = []
    def lrDoors = []
    def lrLocks = []
    def lrContacts = []
    def lrTemps = []
    def lrHumid = []

	def kitchenLights = []
    def kitchenDoors = []
    def kitchenLocks = []
    def kitchenContacts = []
    def kitchenTemps = []
    def kitchenHumid = []

	def outsideLights = []
    def outsideDoors = []
    def outsideLocks = []
    def outsideContacts = []
    def outsideTemps = []
    def outsideHumid = []

	def basementLights = []
    def basementDoors = []
    def basementLocks = []
    def basementContacts = []
    def basementTemps = []
    def basementHumid = []

	def frLights = []
    def frDoors = []
    def frLocks = []
    def frContacts = []
    def frTemps = []
    def frHumid = []

	def masterLights = []
    def masterColorLights = []
    def masterDoors = []
    def masterLocks = []
    def masterContacts = []
    def masterTemps = []
    def masterHumid = []

	def mbLights = []
    def mbDoors = []
    def mbLocks = []
    def mbContacts = []
    def mbTemps = []
    def mbHumid = []

	def br1Lights = []
    def br1Doors = []
    def br1Locks = []
    def br1Contacts = []
    def br1Temps = []
    def br1Humid = []

	def br2Lights = []
    def br2Doors = []
    def br2Locks = []
    def br2Contacts = []
    def br2Temps = []
    def br2Humid = []

	def br3Lights = []
    def br3Doors = []
    def br3Locks = []
    def br3Contacts = []
    def br3Temps = []
    def br3Humid = []

	def home = []
    
    def t_name = ""
    def t_val = ""
    
/* Home */
	if (jeff || cyndi) {
    	if (jeff) {
        	home << [device: "presence", name: jeff.label, value: jeff.currentPresence]
        }
        if (cyndi) {
	        home << [device: "presence", name: cyndi.label, value: cyndi.currentPresence]
        }
	}

/* Garage */
	def itemCnt = 0
    def index = 0

	garage_lights.each {gl ->
    	t_name = "${gl}"
        if (gl.getStatus() != "OFFLINE") {
	        t_val = gl.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		garageLights << [device:"switch", name: t_name, value: t_val]
    }
    
    garage_doors.each {gd ->
    	t_name = "${gd}"
        if (gd.getStatus() != "OFFLINE") {
	        t_val = gd.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        garageDoors << [device:"door", name: t_name, value: t_val]
    }

    garage_locks.each {gl ->
    	t_name = "${gl}"
        if (gl.getStatus() != "OFFLINE") {
	        t_val = gl.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        garageLocks << [device:"lock", name: t_name, value: t_val]
    }

    garage_contacts.each {gc ->
    	t_name = "${gc}"
        if (gc.getStatus() != "OFFLINE") {
	        t_val = gc.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        garageContacts << [device:"contact", name: t_name, value: t_val]
    }

    garage_temps.each {gt ->
    	t_name = "${gt}"
        t_val = gt.currentValue("temperature")
        itemCnt++
        garageTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    garage_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        garageHumid << [device:"humidity", name: t_name, value: t_val]
    }

    
    if (itemCnt > 0) {
    	def g_devices = []
        t_val = "Garage"
        index = garage_idx ? garage_idx : ++index
        if (garage_display?.size() > 0) {
            t_val = garage_display
        }
        
        garageLights.sort{it.name}
        garageLights.each {gl ->
            g_devices << [device:"switch", name: gl.name, value: gl.value]
        }

        garageDoors.sort{it.name}
        garageDoors.each {gd ->
        	g_devices << [device:"door", name: gd.name, value: gd.value]
        }

        garageLocks.sort{it.name}
        garageLocks.each {gl ->
        	g_devices << [device:"lock", name: gl.name, value: gl.value]
        }

        garageContacts.sort{it.name}
        garageContacts.each {gc ->
        	g_devices << [device:"contact", name: gc.name, value: gc.value]
        }

        garageTemps.sort{it.name}
        garageTemps.each {gt ->
        	g_devices << [device:"temp", name: gt.name, value: gt.value]
        }
        
        garageHumid.sort{it.name}
        garageHumid.each {lr ->
        	g_devices << [device:"humidity", name: lr.name, value: lr.value]
        }
            
        rooms << [room: t_val, devices:g_devices, index:index]
//        log.debug "rooms: ${rooms}"
    }
//    return rooms

/* Living Room */
	itemCnt = 0
    
    lr_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		lrLights << [device:"switch", name: t_name, value: t_val]
    }
    
    lr_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        lrDoors << [device:"door", name: t_name, value: t_val]
    }

    lr_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        lrLocks << [device:"lock", name: t_name, value: t_val]
    }

    lr_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        lrContacts << [device:"contact", name: t_name, value: t_val]
    }

    lr_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        lrTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    lr_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        lrHumid << [device:"humidity", name: t_name, value: t_val]
    }

	if (itemCnt > 0) {
    	def l_devices = []
        t_val = "Living Room"
        index = lr_idx ? lr_idx : ++index
        if (lr_display?.size() > 0) {
            t_val = lr_display
        }

        lrLights.sort{it.name}
        lrLights.each {lr ->
        	l_devices << lr
        }

        lrDoors.sort{it.name}
        lrDoors.each {lr ->
        	l_devices << lr
        }

        lrLocks.sort{it.name}
        lrLocks.each {lr ->
        	l_devices << lr
        }

        lrContacts.sort{it.name}
        lrContacts.each {lr ->
        	l_devices << lr
        }

        lrTemps.sort{it.name}
        lrTemps.each {lr ->
        	l_devices << lr
        }
        
        lrHumid.sort{it.name}
        lrHumid.each {lr ->
        	l_devices << lr
        }
        rooms << [room: t_val, devices:l_devices, index:index]
    }

/* Kitchen */
	itemCnt = 0
    
    kitchen_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		kitchenLights << [device:"switch", name: t_name, value: t_val]
    }
    
    kitchen_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        kitchenDoors << [device:"door", name: t_name, value: t_val]
    }

    kitchen_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        kitchenLocks << [device:"lock", name: t_name, value: t_val]
    }

    kitchen_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        kitchenContacts << [device:"contact", name: t_name, value: t_val]
    }

    kitchen_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        kitchenTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    kitchen_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        kitchenHumid << [device:"humidity", name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
    	def k_devices = []
        t_val = "Kitchen"
        index = kitchen_idx ? kitchen_idx : ++index
        if (kitchen_display?.size() > 0) {
            t_val = kitchen_display
        }

        kitchenLights.sort{it.name}
        kitchenLights.each {lr ->
        	k_devices << lr
        }

        kitchenDoors.sort{it.name}
        kitchenDoors.each {lr ->
        	k_devices << lr
        }

        kitchenLocks.sort{it.name}
        kitchenLocks.each {lr ->
        	k_devices << lr
        }

        kitchenContacts.sort{it.name}
        kitchenContacts.each {lr ->
        	k_devices << lr
        }

        kitchenTemps.sort{it.name}
        kitchenTemps.each {lr ->
        	k_devices << lr
        }
        
        kitchenHumid.sort{it.name}
        kitchenHumid.each {lr ->
        	k_devices << lr
        }
        
        rooms << [room: t_val, devices:k_devices, index:index]
    }

/* Outside */
	itemCnt = 0
    
    outside_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		outsideLights << [device:"switch", name: t_name, value: t_val]
    }
    
    outside_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
            t_val = "OFFLINE"
        }
        itemCnt++
        outsideDoors << [device:"door", name: t_name, value: t_val]
    }

    outside_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        outsideLocks << [device:"lock", name: t_name, value: t_val]
    }

    outside_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        outsideContacts << [device:"contact", name: t_name, value: t_val]
    }

    outside_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        outsideTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    outside_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        outsideHumid << [device:"humidity", name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Outside"
        index = outside_idx ? outside_idx : ++index
        def o_devices = []
        if (outside_display?.size() > 0) {
            t_val = outside_display
        }

        outsideLights.sort{it.name}
        outsideLights.each {lr ->
        	o_devices << lr
        }

        outsideDoors.sort{it.name}
        outsideDoors.each {lr ->
        	o_devices << lr
        }

        outsideLocks.sort{it.name}
        outsideLocks.each {lr ->
        	o_devices << lr
        }

        outsideContacts.sort{it.name}
        outsideContacts.each {lr ->
        	o_devices << lr
        }

        outsideTemps.sort{it.name}
        outsideTemps.each {lr ->
        	o_devices << lr
        }
        
        outsideHumid.sort{it.name}
        outsideHumid.each {lr ->
        	o_devices << lr
        }
        
        rooms << [room: t_val, devices:o_devices, index:index]
    }

/* Basement */
	itemCnt = 0
    
    basement_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		basementLights << [device:"switch", name: t_name, value: t_val]
    }
    
    basement_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        basementDoors << [device:"door", name: t_name, value: t_val]
    }

    basement_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        basementLocks << [device:"lock", name: t_name, value: t_val]
    }

    basement_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        basementContacts << [device:"contact", name: t_name, value: t_val]
    }

    basement_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        basementTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    basement_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        basementHumid << [device:"humidity", name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Basement"
        index = basement_idx ? basement_idx : ++index
        def b_devices = []
        if (basement_display?.size() > 0) {
            t_val = basement_display
        }

        basementLights.sort{it.name}
        basementLights.each {lr ->
        	b_devices << lr
        }

        basementDoors.sort{it.name}
        basementDoors.each {lr ->
        	b_devices << lr
        }

        basementLocks.sort{it.name}
        basementLocks.each {lr ->
        	b_devices << lr
        }

        basementContacts.sort{it.name}
        basementContacts.each {lr ->
        	b_devices << lr
        }

        basementTemps.sort{it.name}
        basementTemps.each {lr ->
        	b_devices << lr
        }
        
        basementHumid.sort{it.name}
        basementHumid.each {lr ->
        	b_devices << lr
        }
        
        rooms << [room: t_val, devices:b_devices, index:index]
    }

/* Family Room */
	itemCnt = 0
    
    fr_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
        def capabilities = lr.capabilities
        def dim = -1
        for (cap in capabilities) {
        	if (cap.name == "Switch Level") {
            	dim = lr.currentValue("level")
            }
        }
        if (dim > -1) {
			frLights << [device:"switch", name: t_name, value: t_val, level: dim]
        }
        else {
			frLights << [device:"switch", name: t_name, value: t_val]
        }
    }
    
    fr_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        frDoors << [device:"door", name: t_name, value: t_val]
    }

    fr_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        frLocks << [device:"lock", name: t_name, value: t_val]
    }

    fr_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        frContacts << [device:"contact", name: t_name, value: t_val]
    }

    fr_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        frTemps << [device:"temp", name: t_name, value: t_val]
    }
    
    fr_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        frHumid << [device:"humidity", name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Family Room"
        index = fr_idx ? fr_idx : ++index
        def l_devices = []
        if (fr_display?.size() > 0) {
            t_val = fr_display
        }

        frLights.sort{it.name}
        frLights.each {lr ->
        	l_devices << lr
        }

        frDoors.sort{it.name}
        frDoors.each {lr ->
        	l_devices << lr
        }

        frLocks.sort{it.name}
        frLocks.each {lr ->
        	l_devices << lr
        }

        frContacts.sort{it.name}
        frContacts.each {lr ->
        	l_devices << lr
        }

        frTemps.sort{it.name}
        frTemps.each {lr ->
        	l_devices << lr
        }
        
        frHumid.sort{it.name}
        frHumid.each {lr ->
        	l_devices << lr
        }
        
        rooms << [room: t_val, devices:l_devices, index:index]
    }

/* Master */
	itemCnt = 0
    
    master_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
        def capabilities = lr.capabilities
        def dim = -1
        for (cap in capabilities) {
        	if (cap.name == "Switch Level") {
            	dim = lr.currentValue("level")
            }
        }
        if (dim > -1) {
			masterLights << [device:"switch", name: t_name, value: t_val, level: dim]
            log.debug masterLights
        }
        else {
			masterLights << [device:"switch", name: t_name, value: t_val]
        }
	}

	master_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        masterDoors << [device:"door", name: t_name, value: t_val]
    }

    master_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        masterLocks << [device:"lock", name: t_name, value: t_val]
    }

    master_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        masterContacts << [device:"contact", name: t_name, value: t_val]
    }

    master_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        masterTemps << [device:"temp", name: t_name, value: t_val]
    }

    master_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        masterHumid << [device:"humidity", name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Master"
        index = master_idx ? master_idx : ++index
        def m_devices = []
        if (master_display?.size() > 0) {
            t_val = master_display
        }

        masterLights.sort{it.name}
        masterLights.each {lr ->
        	m_devices << lr
        }

        masterDoors.sort{it.name}
        masterDoors.each {lr ->
        	m_devices << lr
        }

        masterLocks.sort{it.name}
        masterLocks.each {lr ->
        	m_devices << lr
        }

        masterContacts.sort{it.name}
        masterContacts.each {lr ->
        	m_devices << lr
        }

        masterTemps.sort{it.name}
        masterTemps.each {lr ->
        	m_devices << lr
        }

        masterHumid.sort{it.name}
        masterHumid.each {lr ->
        	m_devices << lr
        }
        rooms << [room: t_val, devices:m_devices, index:index]
    }

/* Master Bath */
	itemCnt = 0
    
    mb_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		mbLights << [device:"switch", name: t_name, value: t_val]
    }
    
    mb_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        mbDoors << [device:"door", name: t_name, value: t_val]
    }

    mb_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        mbLocks << [device:"lock", name: t_name, value: t_val]
    }

    mb_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        mbContacts << [device:"contact", name: t_name, value: t_val]
    }

    mb_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        mbTemps << [device:"temp", name: t_name, value: t_val]
    }

    mb_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        mbHumid << [device:"humidity", name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Master Bath"
        index = mb_idx ? mb_idx : ++index
        def m_devices = []
        if (mb_display?.size() > 0) {
            t_val = mb_display
        }

        mbLights.sort{it.name}
        mbLights.each {lr ->
        	m_devices << lr
        }

        mbDoors.sort{it.name}
        mbDoors.each {lr ->
        	m_devices << lr
        }

        mbLocks.sort{it.name}
        mbLocks.each {lr ->
        	m_devices << lr
        }

        mbContacts.sort{it.name}
        mbContacts.each {lr ->
        	m_devices << lr
        }

        mbTemps.sort{it.name}
        mbTemps.each {lr ->
        	m_devices << lr
        }

        mbHumid.each {lr ->
        	m_devices << lr
        }
        rooms << [room: t_val, devices:m_devices, index:index]
    }


/* Bedroom 1 */
	itemCnt = 0
    
    br1_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br1Lights << [device:"switch", name: t_name, value: t_val]
    }
    
    br1_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br1Doors << [device:"door", name: t_name, value: t_val]
    }

    br1_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br1Locks << [device:"lock", name: t_name, value: t_val]
    }

    br1_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br1Contacts << [device:"contact", name: t_name, value: t_val]
    }

    br1_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br1Temps << [device:"temp", name: t_name, value: t_val]
    }
    
    br1_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        br1Humid << [device:"humidity", name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Bedroom 1"
        index = br1_idx ? br1_idx : ++index
        def b_devices = []
        if (br1_display?.size() > 0) {
            t_val = br1_display
        }

        br1Lights.sort{it.name}
        br1Lights.each {lr ->
        	b_devices << lr
        }

        br1Doors.sort{it.name}
        br1Doors.each {lr ->
        	b_devices << lr
        }

        br1Locks.sort{it.name}
        br1Locks.each {lr ->
        	b_devices << lr
        }

        br1Contacts.sort{it.name}
        br1Contacts.each {lr ->
        	b_devices << lr
        }

        br1Temps.sort{it.name}
        br1Temps.each {lr ->
        	b_devices << lr
        }
        
        br1Humid.sort{it.name}
        br1Humid.each {lr ->
        	b_devices << lr
        }
        
        rooms << [room: t_val, devices:b_devices, index:index]
    }

/* Bedroom 2 */
	itemCnt = 0
    
    br2_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br2Lights << [device:"switch", name: t_name, value: t_val]
    }
    
    br2_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br2Doors << [device:"door", name: t_name, value: t_val]
    }

    br2_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br2Locks << [device:"lock", name: t_name, value: t_val]
    }

    br2_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br2Contacts << [device:"contact", name: t_name, value: t_val]
    }

    br2_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br2Temps << [device:"temp", name: t_name, value: t_val]
    }
    
    br2_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        br2Humid << [device:"humidity", name: t_name, value: t_val]
    }
    
    if (itemCnt > 0) {
        t_val = "Bedroom 2"
        index = br2_idx ? br2_idx : ++index
        def b_devices = []
        if (br2_display?.size() > 0) {
            t_val = br2_display
        }

        br2Lights.sort{it.name}
        br2Lights.each {lr ->
        	b_devices << lr
        }

        br2Doors.sort{it.name}
        br2Doors.each {lr ->
        	b_devices << lr
        }

        br2Locks.sort{it.name}
        br2Locks.each {lr ->
        	b_devices << lr
        }

        br2Contacts.sort{it.name}
        br2Contacts.each {lr ->
        	b_devices << lr
        }

        br2Temps.sort{it.name}
        br2Temps.each {lr ->
        	b_devices << lr
        }
        
        br2Humid.sort{it.name}
        br2Humid.each {lr ->
        	b_devices << lr
        }
        
        rooms << [room: t_val, devices:b_devices, index:index]
    }

/* Bedroom 3 */
	itemCnt = 0
    
    br3_lights.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("switch")
        }
        else {
	        t_val = "OFFLINE"
        }
        itemCnt++
		br3Lights << [device:"switch", name: t_name, value: t_val]
    }
    
    br3_doors.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("door")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br3Doors << [device:"door", name: t_name, value: t_val]
    }

    br3_locks.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("lock")
        }
        else {
        	t_val = "OFFLINE"
        }
        itemCnt++
        br3Locks << [device:"lock", name: t_name, value: t_val]
    }

    br3_contacts.each {lr ->
    	t_name = "${lr}"
        if (lr.getStatus() != "OFFLINE") {
	        t_val = lr.currentValue("contact")
        }
        else {
          t_val = "OFFLINE"
        }
        itemCnt++
        br3Contacts << [device:"contact", name: t_name, value: t_val]
    }

    br3_temps.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("temperature")
        itemCnt++
        br3Temps << [device:"temp", name: t_name, value: t_val]
    }
    
    br3_humid.each {lr ->
    	t_name = "${lr}"
        t_val = lr.currentValue("humidity")
        itemCnt++
        br3Humid << [device:"humidity", name: t_name, value: t_val]
    }

    if (itemCnt > 0) {
        t_val = "Bedroom 3"
        index = br3_idx ? br3_idx : ++index
        log.debug "br3_idx: ${br3_idx} / index: ${index} br3_idx > 0: ${br3_idx > 0}"
        def b_devices = []
        if (br3_display?.size() > 0) {
            t_val = br3_display
        }

        br3Lights.sort{it.name}
        br3Lights.each {lr ->
        	b_devices << lr
        }

        br3Doors.sort{it.name}
        br3Doors.each {lr ->
        	b_devices << lr
        }

        br3Locks.sort{it.name}
        br3Locks.each {lr ->
        	b_devices << lr
        }

        br3Contacts.sort{it.name}
        br3Contacts.each {lr ->
        	b_devices << lr
        }

        br3Temps.sort{it.name}
        br3Temps.each {lr ->
        	b_devices << lr
        }
        
        br3Humid.sort{it.name}
        br3Humid.each {lr ->
        	b_devices << lr
        }
        
        rooms << [room: t_val, devices:b_devices, index:index]
    }

	rooms.sort{it.index}
    log.debug rooms
    return rooms
}
