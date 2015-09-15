package com.mycompany;

import java.util.List;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import scala.annotation.meta.getter;

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
		JSONObject voiceRequest = new JSONObject();
		
		voiceRequest.put("header", "This is the introduction, read out prior to any key press");
		voiceRequest.put("type", "ConfCall:,ConfAccountNo:,ConfPinNo:,ConfModPinNo:,Pin:");
		
		voiceRequest.put("body", "Chicago  is the third most populous city in the United States. With over 2.7 million residents, it is the most populous city " + 
		"in the state of Illinois and the Midwest. The Chicago metropolitan area, often referred to as Chicagoland, is home to nearly 10 million people and " + 
				"is the third-largest in the U.S.[4] Chicago is the seat of Cook County");

		request.put("voice", voiceRequest);
		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
		
	}
	
	public void sendSMS(String phoneNumber, String templateId, String callbackId, String body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/vnd.whispir.message-v1+json");
		headers.set("Authorization", "Basic " + authUser);
		
		JSONObject request = new JSONObject();
		request.put("to", phoneNumber);
		request.put("messageTemplateId", templateId);
		request.put("body", body);
		if(callbackId != null) {
			request.put("callbackId", callbackId);
		}

		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
	}
	
	public void handleMessage(WhispirCallbackMessage message) {
		String content = message.getResponseMessage().getContent();
		if(content.equals("1")) {
			// user wants to subscribe to alerts
		}
	}
	
	public void sendVoiceCall(String phoneNumber, List<CityMonument> monuments) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/vnd.whispir.message-v1+json");
		headers.set("Authorization", "Basic " + authUser);
		
		JSONObject request = new JSONObject();
		request.put("to", phoneNumber);
		request.put("messageTemplateName", messageTemplateName);
		JSONObject voiceRequest = new JSONObject();
		
		if(monuments.size() == 1) {
			voiceRequest.put("header", "We found a place that might interest you");
		}
		else {
			voiceRequest.put("header", "We found places that might interest you");	
		}
		voiceRequest.put("type", "ConfCall:,ConfAccountNo:,ConfPinNo:,ConfModPinNo:,Pin:");
		String body = "";
		for (CityMonument cityMonument : monuments) {
			body += cityMonument.getName()  + " is located at " + cityMonument.getAddress();
		}
		
		voiceRequest.put("body", body);

		request.put("voice", voiceRequest);
		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
		
	}
	public void sendSMS(String phoneNumber, String templateId, String callbackId, List<CityMonument> monuments) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/vnd.whispir.message-v1+json");
		headers.set("Authorization", "Basic " + authUser);
		
		JSONObject request = new JSONObject();
		request.put("to", phoneNumber);
		request.put("messageTemplateId", templateId);
		
		String body = "";
		for (CityMonument cityMonument : monuments) {
			body += cityMonument.getName()  + " is located at " + cityMonument.getAddress();
		}
		
		request.put("body", body);
		if(callbackId != null) {
			request.put("callbackId", callbackId);
		}

		HttpEntity<String> entity = new HttpEntity<String>(request.toJSONString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(WHISPIR_API_URL, HttpMethod.POST, entity, String.class, apiKey);
		System.out.println("Whispir response is " + response.getBody());
	}
	
}
