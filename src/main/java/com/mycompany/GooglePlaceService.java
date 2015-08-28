package com.mycompany;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

public class GooglePlaceService {
	
	@Value("${google.api.key}")
	String apiKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final String GEOCODE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng={lat_lng}&key={api_key}";
	private static final String PANORAMIO_API_URL = "http://www.panoramio.com/map/get_panoramas.php?order=popularity&set=public&from=0&to=10&maxx={max_lng}&maxy={max_lat}&minx={min_lng}&miny={min_lat}&size=original";
	
	public List<String> getImageUrls(String latLng) {
		List<String> imageUrls = null;
		String response = restTemplate.getForObject(GEOCODE_API_URL, String.class, latLng, apiKey);
		List<String> lat = JsonPath.read(response, "$..results[(@.length-3)].geometry.viewport..lat");
		List<String> lng = JsonPath.read(response, "$..results[(@.length-3)].geometry.viewport..lng");
		
		if(lat != null && lng != null) {
			// use panormia api to get photos for this location
			response = restTemplate.getForObject(PANORAMIO_API_URL, String.class, lng.get(0), lat.get(0), lng.get(1), lat.get(1));
			imageUrls = JsonPath.read(response, "$..photos[*].photo_file_url");
			imageUrls.forEach(System.out::println);
		}
		
		return imageUrls;
		
	}
}
