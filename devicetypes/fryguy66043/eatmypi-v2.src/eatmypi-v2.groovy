/**
 *  Eat My Pi v2
 *
 *
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
 
metadata {
	definition (name: "EatMyPi v2", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Image Capture"
        capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"

        command "refresh"
	}

preferences {
	input(piIP, "string", title: "IP Address for Pi Server", description: "Please enter the IP Address for the Pi Webserver.", defaultValue: "192.168.1.128", required: true, displayDuringSetup: true)
    input(piPort, "string", title: "Port used by Pi Server", description: "Please enter the Port used by the Pi Webserver.", defaultValue: "5000", required: true, displayDuringSetup: true)
	input(piPath, "string", title: "Path to Pi page to load picture", description: "Please enter webserver path to page that loads a picture.", defaultValue: "/load_pic", required: true, displayDuringSetup: true)    
}

	simulator {
		
	}

	tiles(scale: 2) {
        standardTile("state", "device.state", decoration: "flat", width: 2, height: 2) {
        	state "ok", label: 'OK', icon: "st.Entertainment.entertainment1", backgroundColor:"#00A0DC"
            state "noImageService", label: 'Image Svc', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "noImageLoop", label: 'Loop', icon: "st.Entertainment.entertainment1", backgroundColor:"#e86d13"
            state "lowDiskSpace", label: 'Low Disk', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "highCPUTemp", label: 'CPU Temp', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "unavailable", label: 'Unavail', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
            state "error", label: '${name}', icon: "st.Entertainment.entertainment1", backgroundColor:"#bc2323"
        }
		standardTile("image", "device.image", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: true) {
            state "default", label: "", action: "", icon: "st.camera.dropcam-centered", backgroundColor: "#FFFFFF"
        }
        carouselTile("cameraDetails", "device.image", width: 6, height: 5) { }
        standardTile("take", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
            state "taking", label:'Taking', action: "", icon: "st.camera.dropcam", backgroundColor: "#00A0DC"
            state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
        }

		main "state"
		details(["state", "image", "cameraDetails", "take"])
	}
}

def take() {
	log.debug "take(3)"
	def host = "192.168.1.128"
    def port = "5000"
    def path = "/load_pic"
    
    log.debug "$host:$port$path"
    
    def method = "GET"
    def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }
    
    log.debug "Port in Hex is $porthex"
    log.debug "Hosthex is : $hosthex"
    log.debug "Path is $path"
    
    device.deviceNetworkId = "$hosthex:$porthex"     
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    
    def headers = [:]
    headers.put("HOST", "$host:$port")
    
    def hubAction = new physicalgraph.device.HubAction(
        method: "GET",
        path: path,
        headers: headers
    )  
    hubAction.options = [outputMsgToS3:true]
    return hubAction
}

private getPictureName() {
    return java.util.UUID.randomUUID().toString().replaceAll('-', '')
}

private Long convertIntToLong(ipAddress) {
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }

    return result & 0xFFFFFFFF;
}

private String convertIPToHex(ipAddress) {
	return Long.toHexString(convertIntToLong(ipAddress));
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
log.debug("Convert hex to ip: $hex") //	a0 00 01 6
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

def parse(String description) {
	log.trace "parse($description)"
    
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)

	log.debug "msg = ${msg}"
	log.debug "status = ${status}"
    log.debug "headerString = ${headersAsString}"
	log.debug "headers = ${headerMap}"
    log.debug "body = ${body}"
    log.debug "data = ${data}"

    def map = stringToMap(description)
    log.debug "tempImageKey = ${map.tempImageKey}"

    if (map.tempImageKey) {
    	log.debug "tempImageKey = ${map.tempImageKey}"
        try {
        	log.debug "storing image..."
            storeTemporaryImage(map.tempImageKey, getPictureName())
        } catch (Exception e) {
            log.error e
        }
    } else if (map.error) {
        log.error "Error: ${map.error}"
    }
    else {
    	log.error "Didn't work!"
    }

}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
	reset()
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}