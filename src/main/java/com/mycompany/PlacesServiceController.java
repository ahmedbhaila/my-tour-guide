package com.mycompany;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlacesServiceController {
	
	@Autowired
	PlacesService placeService;
	
	
	@RequestMapping("/places/{city}")
	@ResponseBody
	public CityInfo getCityInfo(@PathVariable("city") String city) {
		return new CityInfo();
	}
	
	@RequestMapping("/places/{lat_lng}/data")
	@ResponseBody
	public List<String> getLocalData(@PathVariable("lat_lng") String latLng) {
		placeService.getPlaceData(latLng);
		return placeService.getPlaceImages(latLng);
	}
	
}
