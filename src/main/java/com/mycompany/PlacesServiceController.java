package com.mycompany;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlacesServiceController {
	
	@Autowired
	PlacesService placeService;
	
	@Autowired
	TourGuideMessageService messageService;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Value("${whispir.callback.id}")
	String whispirCallbackId;
	
	
	@RequestMapping("/places/{city}")
	@ResponseBody
	public CityInfo getCityInfo(@PathVariable("city") String city) {
		return new CityInfo();
	}
	
	@RequestMapping("/places/{lat_lng}/data")
	@ResponseBody
	public CityInfo getLocalData(@PathVariable("lat_lng") String latLng, @RequestParam(required=false, defaultValue="false", value="refresh") String refresh) {
		placeService.getPlaceData(latLng, refresh.equals("true") ? true : false);
		return placeService.getPlaceData(latLng);
	}
	
	@RequestMapping("/places/{lat_lng}/data/monuments")
	@ResponseBody
	public List<CityMonument> setupCityMonumentData(@PathVariable("lat_lng") String latLng, @RequestParam(required=false, defaultValue="false", value="refresh") String refresh) {
		return placeService.getCityMonumentData(latLng);
	}
	
	@RequestMapping(value="/places/{lat_lng}/data/monuments", method = RequestMethod.POST)
	@ResponseBody
	public void saveMonuments(@PathVariable("lat_lng") String latLng, @RequestBody List<CityMonument> monuments) {
		placeService.saveMonuments(latLng, monuments);
	}
	
	@RequestMapping("/ivr/{phone_number}")
	@ResponseBody
	public void makeCall(@PathVariable("phone_number") String phoneNumber) {
		//messageService.sendVoiceCall(phoneNumber);
	}
	
	@RequestMapping("/sms/welcome/{name}/{phone_number}")
	@ResponseBody
	public void sendWelcomeMessage(@PathVariable("name") String name, @PathVariable("phone_number") String phoneNumber) {
		messageService.sendWelcomeMessage(phoneNumber, name);
	}
	
	@RequestMapping("/sms/message/{name}/{phone_number}")
	@ResponseBody
	public void sendMessage(@PathVariable("name") String name, @PathVariable("phone_number") String phoneNumber) {
		messageService.sendGenericMessage(phoneNumber, name);
	}
	
	@RequestMapping(value="/handleCallback", method = RequestMethod.GET)
	@ResponseBody
	public void handleCallback(@RequestParam(required=false, defaultValue="false", value="auth") String auth) {
		System.out.println("call backed");
	}
	
	@RequestMapping(value="/handleCallback", method = RequestMethod.POST)
	@ResponseBody
	// @RequestBody WhispirCallbackMessage message
	public void handleCallback(@RequestBody WhispirCallbackMessage message, @RequestParam(required=false, defaultValue="false", value="auth") String auth) {
		System.out.println("call backed" + message.toString());
		rabbitTemplate.convertAndSend("WhispirMessageQueue", message);
	}
}
