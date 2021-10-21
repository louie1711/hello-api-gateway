package example.microservice.HelloWebServerAPIGateway;

import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
 
@Controller
public class PublicFacingController {
	
	@Autowired
	private DiscoveryClient discoveryClient;	
	protected Logger logger = Logger.getLogger(PublicFacingController.class.getName());
	public static final String HELLO_REST_NAME = "HELLOWORLD-REST"; 	

	@GetMapping("/hellowebpage")
	public String greeting(Model model) {			
		
		// the user e.g. chrome/firefox etc isnt aware of this url (and couldnt request this anyway as its not publically DNSed )					   
	    ServiceInstance service = discoveryClient.getInstances(HELLO_REST_NAME)
	    															.stream()
	    															.findFirst()
	    															.orElseThrow(); 
	    Map<String, String> map;	    
		try { 	
			
			URL url = new URL(service.getUri().toString() + "/helloworldrest"); // htpp://someip:someport/serviceurl					
			map = new ObjectMapper().readValue(url, Map.class); 						 
			map.forEach( (k,v) -> logger.info("JSON is :  k = "+k+" , v = "+v) ) ; // print out json returned from service
			
		}catch(Exception e) {
			throw new RuntimeException(" there was a json exception reading the url ");
		}
		
		model.addAttribute("json_from_service", map); // stick what we got in view to show us 
		return "hello";	        	
		
	}

}