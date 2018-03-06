
preferences {    
	section("Internal Access"){
		input "internal_ip", "text", title: "IP for RM Plugin or RM Bridge or HA(Required for all)", required: true
		input "internal_port", "text", title: "Port(Required for all)", required: true
		input "internal_on_path", "text", title: "On Path(Required for all)", required: true
		input "internal_off_path", "text", title: "Off Path(Optional)", required: false
	}
}


metadata {
	definition (name: "Simulated Switch", namespace: "smartthings", author: "volski") {
			capability "Switch"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.Home.home30", backgroundColor: "#ffffff", nextState: "on"
				state "on", label: 'On', action: "switch.off", icon: "st.Home.home30", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("offButton", "device.button", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force Off', action: "switch.off", icon: "st.Home.home30", backgroundColor: "#ffffff"
		}
		standardTile("onButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force On', action: "switch.on", icon: "st.Home.home30", backgroundColor: "#79b821"
		}
		main "button"
			details (["button","onButton","offButton"])
	}
}

def parse(String description) {
	log.debug(description)
}

def on() {
	if (internal_on_path){
    	//def userpass = "Basic " + id_pw.encodeAsBase64().toString()
		def result = new physicalgraph.device.HubAction(
				method: "POST",		/* If you want to use the RM Bridge, change the method from "POST" to "Get" */
				path: "${internal_on_path}",
				headers: [HOST: "${internal_ip}:${internal_port}"]
				)
			sendHubCommand(result)
			sendEvent(name: "switch", value: "on") 
			log.debug "Executing ON" 
			log.debug result
            /*
             def params = [
    			uri: "http://192.168.10.15:9876/send?deviceMac=b4430d104b84&codeId=182"
  			]
	httpGet(params)
		{resp ->           
        	log.debug "resp data: ${resp.data}"
            log.debug params
    	}*/
    	}
    }


def off() {
	if (internal_off_path){
    	//def userpass = "Basic " + id_pw.encodeAsBase64().toString()
		def result = new physicalgraph.device.HubAction(
				method: "POST",		/* If you want to use the RM Bridge, change the method from "POST" to "Get" */
				path: "${internal_off_path}",
				headers: [HOST: "${internal_ip}:${internal_port}"]
				)

			sendHubCommand(result)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF" 
			log.debug result
	}
}