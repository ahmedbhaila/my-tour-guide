package com.mycompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlacesServiceController {
	
	@Autowired
	PlacesService placeService;
	
	@Autowired
	WhispirService whispirService;
	
	
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
	public void setupCityMonumentData(@PathVariable("lat_lng") String latLng, @RequestParam(required=false, defaultValue="false", value="refresh") String refresh) {
		placeService.setupCityMonumentData(latLng);
	}
	
	@RequestMapping("/ivr/{phone_number}")
	@ResponseBody
	public void makeCall(@PathVariable("phone_number") String phoneNumber) {
		whispirService.sendVoiceCall(phoneNumber);
	}
}
