package com.mycompany;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

public class GooglePlaceService {
	
	@Value("${google.api.key}")
	String apiKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	private static final String GEOCODE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng={lat_lng}&key={api_key}";
	private static final String PANORAMIO_API_URL = "http://www.panoramio.com/map/get_panoramas.php?order=popularity&set=public&from=0&to=10&maxx={max_lng}&maxy={max_lat}&minx={min_lng}&miny={min_lat}&size=original";
	private static final String GEONAME_API_URL = "http://api.geonames.org/citiesJSON?north={lat_north}&south={lat_south}&east={lng_east}&west={lng_west}&lang=en&username=ahmedbhaila";
	private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles={title}";
	private static final String GOOGLE_PLACE_RADAR_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location={lat_lng}&radius={radius}&types={types}&key={api_key}";
	private static final String GOOGLE_PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid={place_id}&key={key}";
	
	private static final String[] categoriesOfInterest = new String[] {
		"airport", "amusement_park", "aquarium", "art_gallery","casino", "zoo", "shopping_mall", "museum"
	};
	public void setupCityData(String latLng) {
		List<String> imageUrls = null;
		String response = restTemplate.getForObject(GEOCODE_API_URL, String.class, latLng, apiKey);
		List<String> lat = JsonPath.read(response, "$..results[(@.length-3)].geometry.viewport..lat");
		List<String> lng = JsonPath.read(response, "$..results[(@.length-3)].geometry.viewport..lng");
		
		if(lat != null && lng != null) {
			
			// use geonames to get city names
			response = restTemplate.getForObject(GEONAME_API_URL, String.class, lat.get(0), lat.get(1), lng.get(0), lng.get(1));
			String cityName = (String)((JSONArray)JsonPath.read(response, "$..geonames[0].name")).get(0);
			
			redisTemplate.opsForHash().put(latLng, "city", cityName);
			
			// get intro text from wikipedia
			response = restTemplate.getForObject(WIKIPEDIA_API_URL, String.class, cityName);
			String aboutCity = (String)((JSONArray)JsonPath.read(response, "$..extract")).get(0);
			redisTemplate.opsForHash().put(latLng, "about", aboutCity);
			
			
			// use panormia api to get photos for this location
			response = restTemplate.getForObject(PANORAMIO_API_URL, String.class, lng.get(0), lat.get(0), lng.get(1), lat.get(1));
			imageUrls = JsonPath.read(response, "$..photos[*].photo_file_url");
			imageUrls.forEach(System.out::println);
			
			imageUrls.forEach(url -> redisTemplate.opsForList().leftPush(latLng + ":images", url));
		}
	}
	
	private String getJsonStringValue(String json, String pattern) {
		JSONArray array = JsonPath.read(json, pattern);
		String value = "";
		if(array != null && array.size() != 0) {
			value = ((String)((JSONArray)JsonPath.read(json, pattern)).get(0));
		}
		return value;
	}
	
	private String getJsonDoubleValue(String json, String pattern) {
		return ((Double)((JSONArray)JsonPath.read(json, pattern)).get(0)).toString();
	}
	
	private CityMonument generateCityMonumentData(String json) {
		CityMonument monument = new CityMonument();
		monument.setAddress(getJsonStringValue(json, "$..formatted_address"));
		monument.setName(getJsonStringValue(json, "$..name"));
		monument.setPhoneNumber(getJsonStringValue(json, "$..formatted_phone_number"));
		monument.setUrl(getJsonStringValue(json, "$..url"));
		monument.setLatLng(getJsonDoubleValue(json, "$..lat") + "," + getJsonDoubleValue(json, "$..lng"));
		monument.setTypeIcon(getJsonStringValue(json, "$..icon"));
		return monument;
	}
	
	public List<CityMonument> getCityMonumentData(String latLng) {
		//String types = Arrays.asList(categoriesOfInterest).stream().collect(Collectors.joining("|"));
		String types = "points_of_interest";
		// do a lookup on which monuments are around this lat,lng
		String response = restTemplate.getForObject(GOOGLE_PLACE_RADAR_SEARCH_URL, String.class, latLng, "50", types, apiKey);
		List<String> places = JsonPath.read(response, "$..place_id");
		List<CityMonument> monuments = new LinkedList<CityMonument>();
		
		// get details from these monuments
		places.forEach(	
				place -> {
					String responses = null;
					responses = restTemplate.getForObject(GOOGLE_PLACE_DETAILS_URL, String.class, place, apiKey);
					monuments.add(generateCityMonumentData(responses));
				}
		);
		
		
		
		
		System.out.println("Response is " + response);
		return monuments;
		// hard-coding for now
		// Chicago : millennium park, Willis Tower,  Michigan_Avenue_(Chicago)
		// Karachi: Mazar-e-Quaid, Clifton Beach Karachi, Karachi University
	}
}
