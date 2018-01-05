

metadata {
    // Automatically generated. Make future change here.
    definition (name: "Nuki_handler", namespace: "Volski", author: "Volski") {
        capability "Actuator"
        
        capability "Health Check"

        capability "Lock"
        
        capability "Refresh"
        
        capability "Actuator"
        //capability "Sensor"
        capability "Health Check"
        
    	
        command "refresh"
        command "lock"
        command "unlock"
        //command "lockNgo"
        

        
    }
    
    preferences {
	    input("ServerIp", "string", title:"Server IP Address", description: "Please enter server ip address", required: true, displayDuringSetup: true)
    	input("ServerPort", "number", title:"Server Port", description: "Please enter your server Port", defaultValue: 8080 , required: false, displayDuringSetup: true)
        input("token","string",title:"Token",description:"Enter your token",required:true ,displayDuringSetup: true)
        input("nukiId","string",title:"nukiId",description:"Enter your nukiId",required:true ,displayDuringSetup: true)


        
        /*
        input("DevicePathOff", "string", title:"URL Path for Lock", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
		input("DevicePathOn", "string", title:"URL Path for UnLock", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
        input("DeviceLockNgo", "string", title:"URL Path for LockNgo", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
        input("DeviceStatus", "string", title:"URL Status", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
        */
	}

    // Simulated lock
    tiles {
    
    
		standardTile("door", "lock", width: 3, height: 2, canChangeIcon: true) {
			state "locked", label: "Locked", action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#FF0000"
            state "waiting", label: "Waiting", action: "waitdevice", icon:"st.locks.lock.locked", backgroundColor:"#c0c0c0"
            state "unlocked", label: "Unlocked", action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#79b821"
		}
        
         standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
        	state "default", action:"refresh", icon:"st.secondary.refresh"
    	}
        
        standardTile("lockNgo", "lock", inactiveLabel: false,  width: 2, height: 2) {
        	state "locked",label:"Lock'N'Go", action:"lockNgo", icon:"st.locks.lock.locked", backgroundColor:"#FF0000"
            state "unlocked",label:"Lock'N'Go", action:"lockNgo", icon:"st.locks.lock.locked", backgroundColor:"#79b821"
            
    	}
        
        standardTile("switch", "switch", inactiveLabel: false,  width: 1, height: 1) {
        	state "locked", action:"on",label: "", icon:"device.switch", backgroundColor:"#FF0000"
            state "unlocked", action:"off",label: "" , icon:"device.switch", backgroundColor:"#79b821"
            
    	}
        
       /*
        multiAttributeTile(name:"toggle", type: "generic", width: 6, height: 4){
            tileAttribute ("device.lock", key: "PRIMARY_CONTROL") {
                attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#00A0DC", nextState:"unlocking"
                attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#FFFFFF", nextState:"locking"
                attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#00A0DC"
                attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#FFFFFF"
            } 
        }*/ 
    }
}
// parse events into attributes
/*
def lock() {
    log.trace "lock()"
    sendEvent(name: "lock", value: "locked")
}

def unlock() {
    log.trace "unlock()"
    sendEvent(name: "lock", value: "unlocked")
    
}
*/

def lock() {
    log.trace "---LOCK COMMAND--- ${DevicePathOff}"
    String  DevicePathOff = "/lockAction?nukiId=${nukiId}&token=${token}&action=2"
	runCmd(DevicePathOff)
    //sendEvent(name: "lock", value: "locked")
    //testitout()
    //refresh()
}

def unlock() {
    log.trace "---UNLOCK COMMAND--- ${DevicePathOn}"
    String  DevicePathOn = "/lockAction?nukiId=${nukiId}&token=${token}&action=1"
    runCmd(DevicePathOn)
    //sendEvent(name: "lock", value: "unlocked")
    //testitout()
    //refresh()
}



def refresh() {
	log.trace "Refreshing!!"
    sendEvent(name: "lock", value: "waiting")
    String  DeviceStatus = "/lockState?nukiId=${nukiId}&token=${token}"
    runCmd(DeviceStatus)
    //updateCurrentWeather()
}

def lockNgo(){
	log.trace "lockNgo"
    sendEvent(name: "_lockgo", value: "unlocked")
    runCmd(DeviceLockNgo)
    refresh()
    }
    
def runCmd(String varCommand) {
	def host = ServerIp
	def LocalDevicePort = ''
	if (ServerPort==null) { LocalDevicePort = "8080" } else { LocalDevicePort = ServerPort }
	log.info "The device id configured is: $device.deviceNetworkId"

	def path = varCommand
	//log.debug "path is: $path"
	def headers = [:] 
	headers.put("HOST", "$host:$LocalDevicePort")
	headers.put("Content-Type", "application/x-www-form-urlencoded")
	//log.debug "The Header is $headers"
	def method = "GET"
	//log.debug "The method is $method"
	try {
    	def hostHex = makeNetworkId(ServerIp, ServerPort)
		device.deviceNetworkId = hostHex
		def hubAction = new physicalgraph.device.HubAction(
			method: method,
			path: path,
			headers: headers
			)
		//log.debug hubAction
       	sendHubCommand(hubAction)
		//return hubAction
        log.info "Run: $varCommand"
	}
	catch (Exception e) {
		log.debug "Hit Exception $e on $hubAction"
	}
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex

}

def parse(String description) {
	log.debug "parse get"
	def msg = parseLanMessage(description)
  	def bodyString = msg.body
  	if (bodyString) {
    	def json = msg.json;
    	if (json) {
      	log.trace("Values received: ${json}")
      	log.trace("Lock State: ${json.stateName}")
        if(json.stateName!=null)
        	{
            	if("locked"==json.stateName)
                {
                 sendEvent(name: "switch", value: "locked")
                 sendEvent(name: "lock", value: "locked")
                }
                else
                {
                sendEvent(name: "switch", value: "unlocked")
                sendEvent(name: "lock", value: "unlocked")
                }
            }
        else
        	{
            log.debug "Empty parse"
        runCmd(DeviceStatus)
        	}
    	}
  	}
    else{
    	log.debug ("Error : ${msg}")
        runCmd(DeviceStatus)
    }
}


private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

private String makeNetworkId(ipaddr, port) { 
     String hexIp = ipaddr.tokenize('.').collect { 
     String.format('%02X', it.toInteger()) 
     }.join() 
     String hexPort = String.format('%04X', port) 
     log.debug "${hexIp}:${hexPort}" 
     return "${hexIp}:${hexPort}" 
}


def installed() {
    log.info('called shade installed()')
    //runCmd(waiting)
    runEvery1Minute(refresh)
}

def updated() {
    log.info('called shade updated()')
    //runCmd(waiting)
    runEvery1Minute(refresh)
}
def initialize() {
	log.debug "Initialized"
	//poll()
    //subscribe(location, null, lanResponseHandler, [filterEvents:false])
    // Schedule it to run every 1 minutes
	runEvery1Minute(refresh)
}
