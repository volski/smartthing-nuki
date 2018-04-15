import groovy.json.JsonSlurper

metadata {
    // Automatically generated. Make future change here.
    definition (name: "Simulated Lock", namespace: "Volski", author: "Volski") {
        capability "Actuator"
        capability "Sensor"
        capability "Health Check"

        capability "Lock"
        capability "Battery"
        capability "Refresh"
        command "lockNgo"
        
    }
	preferences {
        input("api_token","string",title:"Api Web Token",description:"Enter your web API token",required:true ,displayDuringSetup: true)
        input("nukiId","string",title:"nukiId",description:"Enter your nukiId",required:true ,displayDuringSetup: true)
	}
    tiles {
        multiAttributeTile(name:"lock", type: "generic", width: 6, height: 4){
            tileAttribute ("lock", key: "PRIMARY_CONTROL") {
                attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#FF0000"
                attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#79B821"
                attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#00A0DC"
                attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#00A0DC"
                attributeState "waiting", label: "Waiting", icon:"st.locks.lock.locked", backgroundColor:"#00000F"
                attributeState "lockngo",label: "LockNgo - UNLOCKED", icon:"st.locks.lock.locked", backgroundColor:"#78C821"

            }
            tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
                attributeState "battery", label: 'battery ${currentValue}%', unit: "%"
            }
         }

        standardTile("lockNgo", "lockNgo", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
            state "default", label:'lockNgo', action:"lockNgo", icon: "st.locks.lock.locked"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
        	state "default", action:"refresh", icon:"st.secondary.refresh"
    	}
    }
}


def installed() {
    log.trace "installed()"
    initialize()
    runEvery1Minute(refresh)
}

def updated() {
    log.info('called shade updated()')
    runEvery1Minute(refresh)
}
def initialize() {
    log.trace "initialize()"
	setBatteryLevel(50)
    }


def refresh() {
	sendEvent( name: "lock" ,value: "waiting")
	log.trace "Refreshing!!"
    def params = [
    uri: "https://api.nuki.io/smartlock/${nukiId}",
    requestContentType: "application/json",
    headers: ['Authorization': "Bearer ${api_token}"]
  			]
    try{
	httpGet(params)
		{resp ->           
        	log.debug "resp data: ${resp.data}"
            log.debug "Battery is Critical: ${resp.data.state.batteryCritical}"
            	if(resp.data.state.batteryCritical==false)
           		{
            	sendEvent(name:"battery", value:100)
               	}
                else
                {
                sendEvent(name:"battery", value:0)
                }
        		if(1==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "locked")
                }
                else if (2==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "unlocking")
                runIn(5, refresh)
                }
                else if (3==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "unlocked")
                }
                else if (4==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "locking")
                runIn(5, refresh)
                }
                else if (6==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "lockngo")
                runIn(5, refresh)
                }
                else 
                {
                sendEvent(name: "lock", value: "waiting")
                runIn(2, refresh)
                }
			}
        }
        catch(e)
        {
        log.debug "Error ${e}"
        }
        
	}
/*
In http resp status code ->
204	Ok
400	Bad Parameter
401	Not authorized
*/


def lock() {
    log.trace "---LOCK COMMAND---"
	sendEvent(name: "lock", value: "locking")
    def object = new JsonSlurper().parseText('{ "action": "2"}') 
    def options = [
        uri: "https://api.nuki.io/smartlock/${nukiId}/action",
        requestContentType: "application/json",
        headers: [
                 'User-Agent': 'none',
                 'Accept': 'application/json',   
                 'Authorization': "Bearer ${api_token}"
                 ],
                  body: object
                 ]
	httpPost(options)
{resp -> log.debug "resp status: ${resp.status}"
if ( resp.status == 204 ) { 
      runIn(7, refresh)
    }}
    }

def unlock() {
    log.trace "---UNLOCK COMMAND---"
	sendEvent(name: "lock", value: "unlocking")
    def object = new JsonSlurper().parseText('{ "action": "1"}') 
    def options = [
        uri: "https://api.nuki.io/smartlock/${nukiId}/action",
        requestContentType: "application/json",
        headers: [
                 'User-Agent': 'none',
                 'Accept': 'application/json',   
                 'Authorization': "Bearer ${api_token}"
                 ],
                  body: object
                 ]
	httpPost(options)
	{resp ->  log.debug "resp status: ${resp.status}"
    if ( resp.status == 204 ) { 
      runIn(7, refresh)
    }} 
	}

def lockNgo(){
	log.trace "lockNgo"
    sendEvent(name: "lock", value: "lockngo")
    def object = new JsonSlurper().parseText('{ "action": "4"}') 
    def options = [
        uri: "https://api.nuki.io/smartlock/${nukiId}/action",
        requestContentType: "application/json",
        headers: [
                 'User-Agent': 'none',
                 'Accept': 'application/json',   
                 'Authorization': "Bearer ${api_token}"
                 ],
                  body: object
                 ]
	httpPost(options)
	{resp ->  log.debug "resp status: ${resp.status}"
    if ( resp.status == 204 ) { 
      runIn(30, refresh)
    }} 
    
    }
def setBatteryLevelLock(Number lvl) {
    log.trace "setBatteryLevel(level)"
    sendEvent(name: "battery", value: lvl)
    sendEvent( name: "lock" ,value: "locked")
}
