definition(
    name: "Contact Book Example",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Example using Contact Book",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Which Door?") {
        input "door", "capability.contactSensor", required: true,
              title: "Which Door?"
    }

    section("Send Notifications?") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false, multiple: true
        }
    }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
    subscribe(door, "contact.open", doorOpenHandler)
}

def doorOpenHandler(evt) {
    log.debug "recipients configured: $recipients"

    def message = "The ${door.displayName} is open!"
    if (location.contactBookEnabled && recipients) {
        log.debug "Contact Book enabled!"
        sendNotificationToContacts(message, recipients)
    } else {
        log.debug "Contact Book not enabled"
        if (phone) {
            sendSms(phone, message)
        }
    }
}