/**
 *  Eat My Pi
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
	definition (name: "EatMyPi", namespace: "FryGuy66043", author: "Jeffrey Fry") {
		capability "Image Capture"
        capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
		
        attribute "update", "string"

        command "refresh"
        command "getStatus"
        command "imageServiceOff"
        command "imageServiceOn"
        command "callEmailCPU"
        command "callEmailPic"
        command "callTweetPicAndCPU"
		command "setSafetyControl"

		command "getPiPage"
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
        valueTile("status", "device.status", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}' 
        }
        valueTile("substatus", "device.substatus", decoration: "flat", width: 6, height: 2) {
        	state "default", label: '${currentValue}'
        }
        valueTile("diskSpace", "device.diskSpace", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nGB', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
            	[value: 1, color: "#bc2323"],
            	[value: 10, color: "#44b621"]
        	]
        }
        valueTile("cpuTemp", "device.cpuTemp", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nCPU', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
                [value: 100, color: "#44b621"],
                [value: 200, color: "#bc2323"]
            ]
        }
        valueTile("nbrPics", "device.nbrPics", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}\nPics', defaultState: true, backgroundColors: [
            	[value: 0, color: "#ffffff"],
            	[value: 1, color: "#44b621"],
                [value: 2000, color: "#bc2323"]
            ]
        }
        standardTile("emailPic", "device.emailPic", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office19", backgroundColor:"#bc2323"
        	state "off", label: 'OFF', icon: "st.Office.office19", backgroundColor:"#ffffff"
            state "on", label: 'PIC', action: "callEmailPic", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "sending"
            state "sending", label: 'Sending', icon: "st.Office.office19", backgroundColor:"#e86d13", nextState: "sent"
            state "sent", label: 'PIC', action: "callEmailPic", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "on"
        }
        standardTile("emailCPU", "device.emailCPU", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Office.office19", backgroundColor:"#bc2323"
        	state "off", label: '${name}', icon: "st.Office.office19", backgroundColor:"#ffffff"
            state "on", label: 'CPU', action: "callEmailCPU", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "sending"
            state "sending", label: 'Sending', icon: "st.Office.office19", backgroundColor:"#e86d13", nextState: "sent"
            state "sent", label: 'CPU', action: "callEmailCPU", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "on"
        }
        standardTile("tweetPicAndCPU", "device.tweetPicAndCPU", decoration: "flat", width: 2, height: 2) {
        	state "error", label: '${name}', icon: "st.Outdoor.outdoor20", backgroundColor:"#bc2323"
        	state "off", label: '${name}', icon: "st.Outdoor.outdoor20", backgroundColor:"#ffffff"
            state "on", label: 'PIC', action: "callTweetPicAndCPU", icon: "st.Outdoor.outdoor20", backgroundColor:"#00A0DC", nextState: "sending"
            state "sending", label: 'Sending', icon: "st.Outdoor.outdoor20", backgroundColor:"#e86d13", nextState: "sent"
            state "sent", label: 'CPU', action: "callTweetPicAndCPU", icon: "st.Office.office19", backgroundColor:"#00A0DC", nextState: "on"
        }
		standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
//			state "default", label: '', action: "getPiPage", icon:"st.secondary.refresh"
			state "default", label: '', action: "getStatus", icon:"st.secondary.refresh"
		}
        standardTile("imageService", "device.imageService", decoration: "flat", width: 2, height: 2) {
        	state "on", label: 'Img Svc', action: "imageServiceOff", icon: "st.switches.switch.on", backgroundColor:"#00A0DC", nextState: "turningOff"
            state "turningOff", label: 'Turning off', icon: "st.switches.switch.off", backgroundColor:"#ffffff", nextState: "off"
            state "off", label: 'Img Svc', action: "imageServiceOn", icon: "st.switches.switch.off", backgroundColor:"#ffffff", nextState: "turningOn"
            state "turningOn", label: 'Turning on', icon: "st.switches.switch.on", backgroundColor:"#00A0DC", nextState: "on"
        }
		controlTile("safetyControl", "device.safetyControl", "slider", height: 2, width: 2, inactiveLabel: false, range: "(0..10)") {
        	state "level", action: "setSafetyControl"
        }

		standardTile("image", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: true) {
            state "default", label: "", action: "", icon: "st.camera.dropcam-centered", backgroundColor: "#FFFFFF"
        }

        carouselTile("cameraDetails", "device.image", width: 3, height: 2) { }

        standardTile("take", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
//            state "take", label: "Take", action: "getPiPage", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
            state "taking", label:'Taking', action: "", icon: "st.camera.dropcam", backgroundColor: "#00A0DC"
            state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.dropcam", backgroundColor: "#FFFFFF", nextState:"taking"
        }
//        htmlTile(name: "htmlPage", action: "getHtmlPage", refreshInterval: 10, width: 6, height: 5, whitelist: ["65.28.96.234", "192.168.1.128", "www.youtube.com"])
        htmlTile(name: "htmlPage", action: "getHtmlPage", refreshInterval: 10, width: 6, height: 5, whitelist: ["fryguypi.ddns.net", "65.28.96.234", "192.168.2.3"])

		main "state"
//		main "image"
//		details(["state", "diskSpace", "cpuTemp", "nbrPics", "emailCPU", "emailPic", "tweetPicAndCPU", "status", "substatus", "refresh", "safetyControl", "imageService", "htmlPage"])
//		details(["state", "diskSpace", "cpuTemp", "nbrPics", "emailCPU", "emailPic", "status", "substatus", "refresh", "safetyControl", "imageService",  "htmlPage", "image", "cameraDetails", "take"])
		details(["htmlPage", "diskSpace", "cpuTemp", "nbrPics", "emailCPU", "emailPic", "tweetPicAndCPU", "status", "substatus", "refresh", "safetyControl", "imageService", "cameraDetails", "take"])
//		details(["state", "diskSpace", "cpuTemp", "nbrPics", "emailCPU", "emailPic", "status", "substatus", "refresh", "safetyControl", "imageService", "cameraDetails", "take"])
	}
}

def getFullPath() {
	def PI_IP = "fryguypi.ddns.net"
//	def PI_IP = "65.28.96.234"
//	def PI_IP = "192.168.2.3"
	def PI_PORT = "80"

	return "http://${PI_IP}:${PI_PORT}"
}

mappings {
	path("/getHtmlPage") {
    	action: [GET: "getHtmlPage"]
    }
}

def getHtmlPage(page) {
	log.debug "getHtmlPage"
    def date = new Date().format("HH:mm:ss", location.timeZone)

	def piLinksHtml = """
    	<!DOCTYPE html>
        <html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
        <body>
        <h1>Embed Test</h1>
		<iframe width="300" height="300" src="192.168.1.128:80/picrecent/1" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
		</body>
        </html>
    """
//		<iframe width="729" height="410" src="https://www.youtube.com/embed/ae3ju4rEGgQ" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width">
					<meta name="viewport" content="initial-scale = 1.0, user-scalable=no">
				</head>
				<body>
					Pi Picture<br>
                    <img src="http://fryguypi.ddns.net:80/get_pic/${date}" alt="Pi Image" height="300" width="360"> 
				</body>
			</html>
		"""
	if (!page) {
    	log.debug "loading default page"
		render contentType: "text/html", data: html, status: 200
    }
    else {
    	log.debug "loading passed in page"
		render contentType: "text/html", data: page, status: 200
    }
}


def getPiPage() {
	log.debug "getPiPage"
//	sendHubCommand(new physicalgraph.device.HubAction("""GET /get_pic/refresh HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: getPiPageHandler]))

	def path = "${getFullPath()}/get_pic/refresh"

    try {
        httpGet(path) { resp ->
        	log.debug "Refreshing picture cache..."
            log.debug "response contentType: ${resp.contentType}"
            if (resp.status == 200) {
            	log.debug "Picture cache refreshed!"
            }
            else {
                log.error "Error getting Pi Status..."
            }
        }
    } catch (err) {
        log.debug "Error making getHttp get_pic/refresh request: $err"
    }
}

/*
def getPiPageHandler(hubResponse) {
	log.debug hubResponse.body
//    getHtmlPage(hubResponse.body)
}
*/


def setSafetyControl(val) {
	log.debug "setSafetyControl"
    if (val) {
    	sendEvent(name: "safetyControl", value: val)
    }
}

/**********************************************************************
def take() {
	log.debug "take(3)"
	def host = PI_IP
    def port = PI_PORT
    def path = "/load_pic"
    def method = "GET"
    def hosthex = convertIPToHex(host)
    def porthex = Long.toHexString(Long.parseLong((port)))
    if (porthex.length() < 4) { porthex = "00" + porthex }
    
    log.debug "Port in Hex is $porthex"
    log.debug "Hosthex is : $hosthex"
    log.debug "Path is $path"
    
    device.deviceNetworkId = "$hosthex:$porthex"     
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    
    def headers = [:] //"HOST:" + getHostAddress() + ""
    headers.put("HOST", "$host:$port")
    def options = [outputMsgToS3:true]
    
//    try {
        def hubAction = new physicalgraph.device.HubAction(
            method: "GET",
            path: path,
            headers: headers
//            options: [outputMsgToS3:true]
            )  
        hubAction.options = [outputMsgToS3:true]
//    }
//    catch (Exception e) 
//    {
//        log.debug "Hit Exception on $hubAction"
//        log.debug e
//    }
    return hubAction
}
*/

private getPictureName() {
    return java.util.UUID.randomUUID().toString().replaceAll('-', '')
}

private Long converIntToLong(ipAddress) {
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }

    return result & 0xFFFFFFFF;
}

private String convertIPToHex(ipAddress) {
	return Long.toHexString(converIntToLong(ipAddress));
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
//**********************************************************************

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



def take() {
	log.debug "take(4)"
    def params = [
        uri: getFullPath(),
        path: "/load_pic"
    ]

    try {
        httpGet(params) { response ->
        	log.debug "Got a hit!"
            // we expect a content type of "image/jpeg" from the third party in this case
            if (response.status == 200 && response.headers.'Content-Type'.contains("image/jpeg")) {
                def imageBytes = response.data
                if (imageBytes) {
                	log.debug "We have a picture!"
                    def name = getImageName()
                    try {
                        storeImage(name, imageBytes)
                    } catch (e) {
                        log.error "Error storing image ${name}: ${e}"
                    }

                }
            } else {
                log.error "Image response not successful or not a jpeg response"
            }
        }
    } catch (err) {
        log.debug "Error making request: $err"
    }

}

def getImageName() {
    return java.util.UUID.randomUUID().toString().replaceAll('-','')
}




/******************************************************************
def take() {
	log.debug "take(orig)"
//    def host = getHostAddress()
    def host = "411C60EA:1388"
    //65.28.96.234
    def port = host.split(":")[1]
    device.deviceNetworkId = "$host

    def path = "/load_pic"

    def hubAction = new physicalgraph.device.HubAction(
        method: "GET",
        path: path,
        headers: [HOST:host]
    )

    hubAction.options = [outputMsgToS3:true]
	return hubAction
    
	def hubResponse = hubAction
    log.debug "hubResponse = ${hubResponse}"
    log.debug "Status = ${hubResponse.status}"
    log.debug "Headers = ${hubResponse.headers}"
    log.debug "Body = ${hubResponse.body}"
    log.debug "Data = ${hubResponse.data}"
    
//    return hubResponse


    def msg = parseLanMessage(hubResponse)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)

    def map = stringToMap(hubResponse)
    log.debug map

    if (map.tempImageKey) {
        try {
            storeTemporaryImage(map.tempImageKey, getPictureName())
            log.debug "Image Stored"
        } catch (Exception e) {
            log.error e
        }
    } else if (map.error) {
        log.error "Error: ${map.error}"
    }


}
*/
//************************************************************

def callTweetPicAndCPU() {
	log.debug "callTweetPicAndCPU"
    state.tweetPicAndCPU = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "substatus", value: "${timestamp}\nRequesting Tweet of Real-time Image and CPU Temp...")

//	sendHubCommand(new physicalgraph.device.HubAction("""GET /tweetpiccpu HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callTweetPicAndCPUHandler]))
//    runIn(10, checkTweetPicAndCPU)

	def path = "${getFullPath()}/tweetpiccpu"
    try {
        httpGet(path) { resp ->
        	log.debug "tweetPicCPU call round-trip..."
            
            resp.headers.each {
            log.debug "${it.name} : ${it.value}"
        }
        log.debug "response contentType: ${resp.contentType}"
        log.debug "response data: ${resp.data}"
			if (resp.status == 200) {
            	callTweetPicAndCPUHandler("${resp.data}")
            } else {
                log.error "Error getting Pi Status..."
            }
        }
    } catch (err) {
        log.debug "Error making getHttp getStatus request: $err"
    }
}

def callTweetPicAndCPUHandler(sData) {
	log.debug "callTweetPicAndCPUHandler"
    state.tweetPicAndCPU = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nTweet Pic/CPU Call (${sData})"
    def hBody = sData.replace("<>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "tweetPicAndCPU", value: "sent")
    }
    else {
    	sendEvent(name: "tweetPicAndCPU", value: "error")
    }
}

/*
def callTweetPicAndCPUHandler(hubResponse) {
	log.debug "callTweetPicAndCPUHandler"
    state.tweetPicAndCPU = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nTweet Pic/CPU Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "tweetPicAndCPU", value: "sent")
    }
    else {
    	sendEvent(name: "tweetPicAndCPU", value: "error")
    }
}

def checkTweetPicAndCPU() {
	log.debug "checkTweetPicAndCPU"
    if (!state.tweetPicAndCPU) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "tweetPicAndCPU Call Timed Out..."
 		sendEvent(name: "substatus", value: "${date}\nTweet Pic/CPU call timed out.")
        sendEvent(name: "tweetPicAndCPU", value: "off")
    }
    else {
    	sendEvent(name: "tweetPicAndCPU", value: "on")
        log.debug "tweetPicAndCPU call succeeded!"
    }
}
*/

def callEmailPic() {
	log.debug "callEmailPic"
    state.emailPic = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "substatus", value: "${timestamp}\nRequesting Real-time Image email...")

//	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailpic HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callEmailPicHandler]))
//    runIn(10, checkEmailPic)

	def path = "${getFullPath()}/emailpic"
    try {
        httpGet(path) { resp ->
        	log.debug "emailpic round-trip..."
            
            resp.headers.each {
            log.debug "${it.name} : ${it.value}"
        }
        log.debug "response contentType: ${resp.contentType}"
        log.debug "response data: ${resp.data}"
			if (resp.status == 200) {
            	callEmailPicHandler("${resp.data}")
            } else {
                log.error "Error getting Pi Status..."
            }
        }
    } catch (err) {
        log.debug "Error making getHttp getStatus request: $err"
    }
}

def callEmailPicHandler(sData) {
	log.debug "callEmailPicHandler"
    state.emailPic = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail Pic Call (${sData})"
    def hBody = sData.replace("<br>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailPic", value: "sent")
    }
    else {
    	sendEvent(name: "emailPic", value: "error")
    }
}

/*
def callEmailPicHandler(hubResponse) {
	log.debug "callEmailPicHandler"
    state.emailPic = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail Pic Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailPic", value: "sent")
    }
    else {
    	sendEvent(name: "emailPic", value: "error")
    }
}

def checkEmailPic() {
	log.debug "checkEmailPic"
    if (!state.emailPic) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "emailPic Call Timed Out..."
 		sendEvent(name: "substatus", value: "${date}\nEmail Pic call timed out.")
        sendEvent(name: "emailPic", value: "off")
    }
    else {
    	sendEvent(name: "emailPic", value: "on")
        log.debug "emailCPU call succeeded!"
    }
}
*/

def callEmailCPU() {
	log.debug "callEmailCPU"
    state.emailCPU = false
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "substatus", value: "${timestamp}\nRequesting CPU Temp email...")

//	sendHubCommand(new physicalgraph.device.HubAction("""GET /emailcpu HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: callEmailCPUHandler]))
//    runIn(10, checkEmailCPU)

	def path = "${getFullPath()}/emailcpu"
    try {
        httpGet(path) { resp ->
        	log.debug "emailcpu round-trip..."
            
            resp.headers.each {
            log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.contentType}"
            log.debug "response data: ${resp.data}"
			if (resp.status == 200) {
            	callEmailCPUHandler("${resp.data}")
            } else {
                log.error "Error sending cpu email.  Status: ${resp.status} / Data: ${resp.data}"
            }
        }
    } catch (err) {
        log.debug "Error making getHttp getStatus request: $err"
    }
}

def callEmailCPUHandler(sData) {
	log.debug "callEmailCPUHandler"
    state.emailCPU = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail CPU Call (${sData})"
    def hBody = sData
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailCPU", value: "sent")
    }
    else {
    	sendEvent(name: "emailCPU", value: "error")
    }
}

/*
def callEmailCPUHandler(hubResponse) {
	log.debug "callEmailCPUHandler"
    state.emailCPU = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nEmail CPU Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "substatus", value: msg)
	log.debug msg    
    if (hBody.contains("Success")) {
    	sendEvent(name: "emailCPU", value: "sent")
    }
    else {
    	sendEvent(name: "emailCPU", value: "error")
    }
}

def checkEmailCPU() {
	log.debug "checkEmailCPU"
    if (!state.emailCPU) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "emailCPU Call Timed Out..."
 		sendEvent(name: "substatus", value: "${date}\nEmail CPU call timed out.")
        sendEvent(name: "emailCPU", value: "off")
    }
    else {
    	sendEvent(name: "emailCPU", value: "on")
        log.debug "emailCPU call succeeded!"
    }
}
*/

def refresh() {
	log.debug "switch: request refresh()"
    sendEvent(name: "substatus", value: "")
    getStatus() 
    if (device.currentValue("imageService") == "on") {
        getPiPage()
        take()
    }
}

def getStatus() {
	log.debug "getStatus"
	def timestamp = new Date().format("MM/dd/yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "update", value: timestamp)
    sendEvent(name: "status", value: "${timestamp}\nGetting Pi Status...")
	state.getStatus = false
    log.debug "getStatus path: ${getFullPath()}"

//	sendHubCommand(new physicalgraph.device.HubAction("""GET /getstatus HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: getStatusHandler]))
//    runIn(10, checkGetStatus)

	def path = "${getFullPath()}/getstatus"
    log.debug "getStatus Path: ${path}"
    try {
        httpGet(path) { resp ->
        	log.debug "getStatus round-trip..."
            
            resp.headers.each {
//            log.debug "${it.name} : ${it.value}"
        }
//        log.debug "response contentType: ${resp.contentType}"
//        log.debug "response data: ${resp.data}"
			if (resp.status == 200) {
            	statusHandler("${resp.data}")
            } else {
                log.error "Error getting Pi Status.  Status: ${resp.status}"
                getStatusErr()
            }
        }
    } catch (err) {
        log.debug "Error making getHttp getStatus request: $err"
    }
}

def getStatusErr() {
	log.debug "getStatusErr"
    sendEvent(name: "status", value: "${date}\nGet Status call timed out.")
    sendEvent(name: "state", value: "unavailable")
    sendEvent(name: "substatus", value: "")
    sendEvent(name: "diskSpace", value: 0)
    sendEvent(name: "cpuTemp", value: 0)
    sendEvent(name: "nbrPics", value: 0)
    sendEvent(name: "emailCPU", value: "off")
    sendEvent(name: "emailPic", value: "off")
    sendEvent(name: "tweetPicAndCPU", value: "off")
    sendEvent(name: "imageService", value: "off")
}

/*
def checkGetStatus() {
	log.debug "checkGetStatus"
    if (!state.getStatus) {
    	def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    	log.debug "getStatus Call Timed Out..."
 		sendEvent(name: "status", value: "${date}\nGet Status call timed out.")
        sendEvent(name: "state", value: "unavailable")
        sendEvent(name: "substatus", value: "")
        sendEvent(name: "diskSpace", value: 0)
        sendEvent(name: "cpuTemp", value: 0)
        sendEvent(name: "nbrPics", value: 0)
        sendEvent(name: "emailCPU", value: "off")
        sendEvent(name: "emailPic", value: "off")
        sendEvent(name: "tweetPicAndCPU", value: "off")
        sendEvent(name: "imageService", value: "off")
    }
    else {
    	log.debug "getStatus Call succeeded!"
    }
}
*/

def statusHandler(sData) {
	log.debug "statusHandler(${sData})"
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nPi Server Call (${sData})"
    def hData = sData.replace("<>", "\n")
    log.debug "hData = ${hData}"
    def hServerMsg = hData.split('\n')
//    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<>", "\n")
    sendEvent(name: "status", value: msg)
//	log.debug msg

    def sVal = ""
    def temp = ""
    def tSize = 0.0
    for (int i = 0; i < hServerMsg.size(); i++) {
    	log.debug "hServerMsg[i] = ${hServerMsg[i]}"
    	switch (i) {
        	case 0:
            	if (hServerMsg[i].contains("Server") && sVal != "error") {
                	temp = hServerMsg[i].replace("Server=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                        sendEvent(name: "emailCPU", value: "on")
                        sendEvent(name: "emailPic", value: "on")
                        sendEvent(name: "tweetPicAndCPU", value: "on")
                    }
                    else if (temp == "Not Running") {
                    	sVal = "unavailable"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                        sendEvent(name: "tweetPicAndCPU", value: "off")
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                        sendEvent(name: "tweetPicAndCPU", value: "off")
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "emailCPU", value: "off")
                    sendEvent(name: "emailPic", value: "off")
                    sendEvent(name: "tweetPicAndCPU", value: "off")
                }
            	break
            case 1:
            	if (hServerMsg[i].contains("Image Service") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Service=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                        sendEvent(name: "imageService", value: "on")
                        log.debug "imageService = on"
                    }
                    else if(temp == "Not Running" || temp == "Stopped") {
                    	sVal = "noImageService"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "imageService", value: "off")
                    log.debug "imageService = off"
                }
            	break
			case 2:
            	if (hServerMsg[i].contains("Image Capture Loop") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Capture Loop=", "")
                    log.debug "temp = ${temp}"
                    if (temp == "Not Ready") { 
                    	if (sVal == "ok") { // This is only important if the Image Service is running
                    		sVal = "noImageLoop"
                        }
                    }
                    else if (temp.contains("Minute")) {
                    	sVal = "ok"
                    }
                    else {
                    	sVal = "error"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
			case 3:
            	if (hServerMsg[i].contains("Avail Disk Space") && sVal != "error") {
                	temp = hServerMsg[i].replace(" GB Avail Disk Space", "")
                    tSize = Float.parseFloat(temp)
                    log.debug "tSize = ${tSize}"
                    sendEvent(name: "diskSpace", value: tSize)
                    if (tSize < 0.1) {
                    	sVal = "lowDiskSpace"
                    }
                    else {
                    	sVal = "ok"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
            case 4:
            	if (hServerMsg[i].contains("CPU Temp") && sVal != "error") {
                	temp = hServerMsg[i].replace("CPU Temp=", "")
                    if (temp.contains("Fail")) {
                		sVal = "error"
                    }
                    else {
                    	tSize = Float.parseFloat(temp)
                        log.debug "CPU Temp = ${tSize}"
                        sendEvent(name: "cpuTemp", value: tSize)
                        if (tSize > 200) {
                        	sVal = "highCPUTemp"
                        }
                        else {
                        	sVal = "ok"
                        }
                    }
                }
                else {
                	sVal = "error"
                }
                break
            case 5:
            	if (hServerMsg[i].contains("Number Pics")) {
                	temp = hServerMsg[i].replace("Number Pics=", "")
                    sendEvent(name: "nbrPics", value: temp)
                }
                else {
                	sendEvent(name: "nbrPics", value: 0)
                }
            	break
            default:
            	break
        }
    }
    log.debug "sVal = ${sVal}"
    sendEvent(name: "state", value: sVal)
}

/*
def getStatusHandler(hubResponse){
	log.debug "callbackHandler($hubResponse)"
    state.getStatus = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${date}\nPi Server Call (${hubResponse.status}: ${hubResponse.body})"
    def hStatus = hubResponse.status
    def hBody = hubResponse.body.replace("<br>", "\n")
    def hData = hubResponse.data
    log.debug "hData = ${hData}"
    def hHeaders = hubResponse.headers
    log.debug "hHeaders = ${hHeaders}"
    def hServerMsg = hBody.split('\n')
    log.debug "hServerMsg = ${hServerMsg}"
  	def hMsg = msg.replace("<br>", "\n")
    sendEvent(name: "status", value: msg)
	log.debug msg
    
    def sVal = ""
    def temp = ""
    def tSize = 0.0
    for (int i = 0; i < hServerMsg.size(); i++) {
    	log.debug "hServerMsg[i] = ${hServerMsg[i]}"
    	switch (i) {
        	case 0:
            	if (hServerMsg[i].contains("Server") && sVal != "error") {
                	temp = hServerMsg[i].replace("Server=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                        sendEvent(name: "emailCPU", value: "on")
                        sendEvent(name: "emailPic", value: "on")
                        sendEvent(name: "tweetPicAndCPU", value: "on")
                    }
                    else if (temp == "Not Running") {
                    	sVal = "unavailable"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                        sendEvent(name: "tweetPicAndCPU", value: "off")
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "emailCPU", value: "off")
                        sendEvent(name: "emailPic", value: "off")
                        sendEvent(name: "tweetPicAndCPU", value: "off")
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "emailCPU", value: "off")
                    sendEvent(name: "emailPic", value: "off")
                    sendEvent(name: "tweetPicAndCPU", value: "off")
                }
            	break
            case 1:
            	if (hServerMsg[i].contains("Image Service") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Service=", "")
                    if (temp == "Running") {
                    	sVal = "ok"
                        sendEvent(name: "imageService", value: "on")
                        log.debug "imageService = on"
                    }
                    else if(temp == "Not Running" || temp == "Stopped") {
                    	sVal = "noImageService"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                    else {
                    	sVal = "error"
                        sendEvent(name: "imageService", value: "off")
                        log.debug "imageService = off"
                    }
                }
                else {
                	sVal = "error"
                    sendEvent(name: "imageService", value: "off")
                    log.debug "imageService = off"
                }
            	break
			case 2:
            	if (hServerMsg[i].contains("Image Capture Loop") && sVal != "error") {
                	temp = hServerMsg[i].replace("Image Capture Loop=", "")
                    log.debug "temp = ${temp}"
                    if (temp == "Not Ready") { 
                    	if (sVal == "ok") { // This is only important if the Image Service is running
                    		sVal = "noImageLoop"
                        }
                    }
                    else if (temp.contains("Minute")) {
                    	sVal = "ok"
                    }
                    else {
                    	sVal = "error"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
			case 3:
            	if (hServerMsg[i].contains("Avail Disk Space") && sVal != "error") {
                	temp = hServerMsg[i].replace(" GB Avail Disk Space", "")
                    tSize = Float.parseFloat(temp)
                    log.debug "tSize = ${tSize}"
                    sendEvent(name: "diskSpace", value: tSize)
                    if (tSize < 0.1) {
                    	sVal = "lowDiskSpace"
                    }
                    else {
                    	sVal = "ok"
                    }
                }
                else {
                	sVal = "error"
                }
            	break
            case 4:
            	if (hServerMsg[i].contains("CPU Temp") && sVal != "error") {
                	temp = hServerMsg[i].replace("CPU Temp=", "")
                    if (temp.contains("Fail")) {
                		sVal = "error"
                    }
                    else {
                    	tSize = Float.parseFloat(temp)
                        log.debug "CPU Temp = ${tSize}"
                        sendEvent(name: "cpuTemp", value: tSize)
                        if (tSize > 200) {
                        	sVal = "highCPUTemp"
                        }
                        else {
                        	sVal = "ok"
                        }
                    }
                }
                else {
                	sVal = "error"
                }
                break
            case 5:
            	if (hServerMsg[i].contains("Number Pics")) {
                	temp = hServerMsg[i].replace("Number Pics=", "")
                    sendEvent(name: "nbrPics", value: temp)
                }
                else {
                	sendEvent(name: "nbrPics", value: 0)
                }
            	break
            default:
            	break
        }
    }
    log.debug "sVal = ${sVal}"
    sendEvent(name: "state", value: sVal)
}
*/

def imageServiceOff() {
	log.debug "imageServiceOff"
    sendEvent(name: "substatus", value: "")
    def safetyLevel = device.currentValue("safetyControl")
    log.debug "safetyControl == '10' (${safetyLevel == "10"})"
    if (device.currentValue("safetyControl") == "10") {
    	log.debug "safetyControl Level = 10"
        state.imageService = false
        sendEvent(name: "substatus", value: "Turning off Image Service...")
        
//        sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureterminate HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: imageServiceHandler]))
//        runIn(10, checkImageService)

		sendEvent(name: "safetyControl", value: 0)
        
        def path = "${getFullPath()}/imagecaptureterminate"
        
        try {
            httpGet(path) { resp ->
                log.debug "imagecaptureterminate round-trip..."

                resp.headers.each {
                log.debug "${it.name} : ${it.value}"
                }
                log.debug "response contentType: ${resp.contentType}"
                log.debug "response data: ${resp.data}"
                if (resp.status == 200) {
                    imageServiceHandler("${resp.data}")
                } else {
                    log.error "Error calling imagecaptureterminate.  Status: ${resp.status}"
                    sendEvent(name: "status", value: "Image Capture call timed out...")
                    sendEvent(name: "state", value: "unavailable")
                    sendEvent(name: "imageService", value: "off")
                }
            }
        } catch (err) {
            log.debug "Error making getHttp getStatus request: $err"
        }
    }
    else {
    	sendEvent(name: "substatus", value: "Set Level to 10 to turn off Image Service!")
        sendEvent(name: "imageService", value: "on")
    }
}

def imageServiceOn() {
	log.debug "imageServiceOn"
	state.imageService = false
    sendEvent(name: "substatus", value: "Turning on Image Service...")
    
//	sendHubCommand(new physicalgraph.device.HubAction("""GET /imagecaptureinitialize HTTP/1.1\r\nHOST: ${getFullPath()}\r\n\r\n""", physicalgraph.device.Protocol.LAN, "" ,[callback: imageServiceHandler]))
//    runIn(10, checkImageService)

    def path = "${getFullPath()}/imagecaptureinitialize"


    try {
        httpGet(path) { resp ->
        	log.debug "imagecaptureinitialize round-trip"
            
            resp.headers.each {
            log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.contentType}"
            log.debug "response data: ${resp.data}"
			if (resp.status == 200) {
            	imageServiceHandler("${resp.data}")
            } else {
                log.error "Error calling imagecaptureinitialize."
            }
        }
    } catch (err) {
        log.debug "Error making getHttp getStatus request: $err"
    }
}

def imageServiceHandler(sData) {
	log.debug "imageServiceHandler"
    state.imageService = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Pi Server Call (${sData})"
    sendEvent(name: "substatus", value: msg)
    runIn(5, getStatus)
}

/*
def imageServiceHandler(hubResponse) {
	log.debug "imageServiceHandler"
    state.imageService = true
    def date = new Date().format("MM/dd/yy hh:mm:ss a", location.timeZone)
    def msg = "${location} ${date}: Pi Server Call (${hubResponse.status}: ${hubResponse.body})"
    sendEvent(name: "substatus", value: msg)
    runIn(5, getStatus)
}

def checkImageService() {
	log.debug "checkImageService"
    if (!state.imageService) {
    	log.debug "Image Capture call timed out..."
        sendEvent(name: "status", value: "Image Capture call timed out...")
        sendEvent(name: "state", value: "unavailable")
        sendEvent(name: "imageService", value: "off")
        log.debug "imageService = off"
    }
    else {
    	log.debug "Image Capture call succeeded!"
    }
}
*/

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
	refresh()
    runEvery10Minutes(refresh)
}

