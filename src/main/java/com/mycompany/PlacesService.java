package com.mycompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class PlacesService {
	
	@Autowired
	GooglePlaceService placeService;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public void getPlaceData(String latLng, boolean refresh) {
		Long size = redisTemplate.opsForList().size(latLng + ":images");
		if(refresh || size == 0) {
			placeService.setupCityData(latLng);
		}
	}
	
	public CityInfo getPlaceData(String latLng) {
		
		CityInfo cityInfo = new CityInfo();
		cityInfo.setImages(redisTemplate.opsForList().range(latLng + ":images", 0, -1));
		cityInfo.setCityName((String)redisTemplate.opsForHash().get(latLng, "city"));
		cityInfo.setAboutCity((String)redisTemplate.opsForHash().get(latLng, "about"));
		return cityInfo;
	}
	
	public void setupCityMonumentData(String latLng) {
		placeService.setupCityMonumentData(latLng);
	}
	
}
