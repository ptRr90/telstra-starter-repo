package au.com.telstra.simcardactivator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ActivationController {

    String serviceURL = "http://localhost:8444/actuate";
    @PostMapping("/activation")
    public ResponseEntity<String> activation(@RequestBody String activation) {
        System.out.println("Post Mapping Class for Activation");
        try {
            // Parse json data
            ObjectMapper om = new ObjectMapper();
            JsonNode rootNode = om.readTree(activation);

            JsonNode extraction = rootNode.get("iccid");

            if (extraction != null) {
                // Convert to string
                String iccid = om.writeValueAsString(extraction);

                RestTemplate rt = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");

                HttpEntity<String> request = new HttpEntity<>(iccid, headers);
                System.out.println("Sending Json File.");

                ResponseEntity<JsonNode> response = rt.postForEntity(serviceURL, request, JsonNode.class);
                System.out.println("Received response: " + response);

                return ResponseEntity.badRequest().body("Json File Sent.");
            } else {
                System.out.println("Can't Find iccid in JSON File.");
                return ResponseEntity.badRequest().body("The desired part of the JSON data is not found.");

            }
        }
        catch (Exception e){
            System.out.println("Couldn't forward JSON data to microservice.");
            return ResponseEntity.badRequest().body("Failed to forward JSON data to the microservice: " + e.getMessage());
        }
    }
}
