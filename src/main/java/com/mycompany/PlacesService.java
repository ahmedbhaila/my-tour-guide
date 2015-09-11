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
		List<CityMonument> monuments = placeService.getCityMonumentData(latLng);
		monuments.stream().forEach(m -> {
				String name = (String) redisTemplate.opsForHash().get(m.getName(), "name");
				if(name != null && !name.equals("")) {
					m.setTagged(true);
				}
				else {
					m.setTagged(false);
				}
			}
		);
		return monuments;
	}
	
	public void saveMonuments(String latLng, List<CityMonument> monuments) {
		monuments.forEach(monument ->
			{
				redisTemplate.opsForHash().put(monument.getName(), "address", monument.getAddress());
				redisTemplate.opsForHash().put(monument.getName(), "latLng", monument.getLatLng());
				redisTemplate.opsForHash().put(monument.getName(), "name", monument.getName());
				redisTemplate.opsForHash().put(monument.getName(), "phoneNumber", monument.getPhoneNumber());
				redisTemplate.opsForHash().put(monument.getName(), "typeIcon", monument.getTypeIcon());
				redisTemplate.opsForHash().put(monument.getName(), "url", monument.getUrl());
			}
		);
	}
	
	public List<String> findTaggedPlaces(String profileName, String latLng, String timeInMillis, String timeZone) {
		List<String> places = placeService.getPlaces(latLng);
		
		// save this place data to user profile
		places.forEach(p -> redisTemplate.opsForList().leftPush(profileName + ":places", p));
		places.forEach(p -> {
			redisTemplate.opsForHash().put(profileName + ":" + p, "datetime", timeInMillis);
			redisTemplate.opsForHash().put(profileName + ":" + p, "timezone", timeZone);
		});
		
		
		return places;
	}
	
	public List<String> getVisitedPlaces(String profileName) {
		return redisTemplate.opsForList().range(profileName + ":places", 0, -1);
	}
	
}
