package com.mycompany;

import java.util.List;

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
	
	public List<CityMonument> getCityMonumentData(String latLng) {
		return placeService.getCityMonumentData(latLng);
	}
	
	public void saveMonuments(String latLng, List<CityMonument> monuments) {
		monuments.forEach(monument ->
			{
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "address", monument.getAddress());
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "latLng", monument.getLatLng());
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "name", monument.getName());
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "phoneNumber", monument.getPhoneNumber());
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "typeIcon", monument.getTypeIcon());
				redisTemplate.opsForHash().put(latLng + ":monument:" + monument.getName(), "url", monument.getUrl());
			}
		);
	}
	
}
