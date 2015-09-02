package com.mycompany;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WhispirService {
	
	@Value("${whispir.api.key}")
	String apiKey;
	
	@Value("${whispir.auth.user}")
	String authUser;
	
	@Value("${whispir.message.template.name}")
	String messageTemplateName;
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final String WHISPIR_API_URL = "https://api.whispir.com/messages?apikey={api_key}";
	
	
	
	
	public void sendVoiceCall(String phoneNumber) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/vnd.whispir.message-v1+json");
		headers.set("Authorization", "Basic " + authUser);
		
		JSONObject request = new JSONObject();
		request.put("to", phoneNumber);
		request.put("messageTemplateName", messageTemplateName);

		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
		
	}
	
	public void sendSMS(String phoneNumber, String templateId, String body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/vnd.whispir.message-v1+json");
		headers.set("Authorization", "Basic " + authUser);
		
		JSONObject request = new JSONObject();
		request.put("to", phoneNumber);
		request.put("messageTemplateId", templateId);
		request.put("body", body);

		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
	}
}
