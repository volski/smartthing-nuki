import groovy.json.JsonSlurper


metadata {
    definition (name: "Nuki_handler", namespace: "Volski", author: "Volski") {
        capability "Actuator"
        capability "Lock"      
        capability "Refresh" 
        capability "Actuator"
        capability "Health Check"
        
    	
        command "refresh"
        command "lock"
        command "unlock"
        command "lockNgo" 
    }
    
    preferences {
        input("api_token","string",title:"Api Web Token",description:"Enter your web API token",required:true ,displayDuringSetup: true)
        input("nukiId","string",title:"nukiId",description:"Enter your nukiId",required:true ,displayDuringSetup: true)
	}

    tiles {
    
    
		standardTile("door", "lock", width: 3, height: 2, canChangeIcon: true) {
        	state "locking", label: "Locking", action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#F00000"
			state "locked", label: "Locked", action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#FF0000",nextState:"unlocking"
            state "waiting", label: "Waiting", action: "waitdevice", icon:"st.locks.lock.locked", backgroundColor:"#c0c0c0"
            state "unlocked", label: "Unlocked", action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#79b821",nextState:"locking"
            state "unlocking", label: "Unlocking", action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#79b711"
            state "lockngo",label: "LockNgo - UNLOCKED",action: "wait", icon:"st.locks.lock.locked", backgroundColor:"#c0c123"
		}
        
         standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
        	state "default", action:"refresh", icon:"st.secondary.refresh"
    	}
        
        standardTile("lockNgo", "lock", inactiveLabel: false,  width: 2, height: 2) {
        	state "locked",label:"Lock'N'Go", action:"lockNgo", icon:"st.locks.lock.locked", backgroundColor:"#FF0000"
            state "unlocked",label:"Lock'N'Go", action:"lockNgo", icon:"st.locks.lock.locked", backgroundColor:"#79b821"
    	}
    }
}

def lock() {
    log.trace "---LOCK COMMAND--- ${DevicePathOff}"
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
{resp -> log.debug "resp data: ${resp}"}
	refresh()
    }

def unlock() {
    log.trace "---UNLOCK COMMAND---"
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
	{resp ->  log.debug "resp data: ${resp.text}"} 
    refresh()
	}

def lockNgo(){
	log.trace "lockNgo"
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
	{resp ->  log.debug "resp data: ${resp.text}"} 
    refresh()
    }
    
def refresh() {
	log.trace "Refreshing!!"
    def params = [
    uri: "https://api.nuki.io/smartlock/${nukiId}",
    requestContentType: "application/json",
    headers: ['Authorization': "Bearer ${api_token}"]
  			]
	httpGet(params)
		{resp ->           
        	log.debug "resp data: ${resp.data}"
        		if(1==resp.data.state.state)
                {
                 sendEvent(name: "lock", value: "locked")
                }
                else if (2==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "unlocking")
                }
                else if (3==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "unlocked")
                }
                else if (4==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "locking")
                }
                else if (6==resp.data.state.state)
                {
                sendEvent(name: "lock", value: "lockngo")
                }
                else 
                {
                sendEvent(name: "lock", value: "waiting")
                }
			}
	}

def installed() {
    log.info('called shade installed()')
    runEvery1Minute(refresh)
}

def updated() {
    log.info('called shade updated()')
    runEvery1Minute(refresh)
}
def initialize() {
	log.debug "Initialized"
	runEvery1Minute(refresh)
}









