package com.mycompany;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class PlacesService {
	
	@Autowired
	GooglePlaceService placeService;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public void getPlaceData(String latLng) {
		Long size = redisTemplate.opsForList().size(latLng + ":images");
		if(size == 0) {
			List<String> imageUrls = placeService.getImageUrls(latLng);
			imageUrls.forEach(url -> redisTemplate.opsForList().leftPush(latLng + ":images", url));
		}
	}
	
	public List<String> getPlaceImages(String latLng) {
		return redisTemplate.opsForList().range(latLng + ":images", 0, -1);
	}
}
