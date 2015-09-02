package com.mycompany;

import java.net.URI;

import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MyTourGuideApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(clientFactory());
	}
	
	@Bean
	public GooglePlaceService googlePlaceService() {
		return new GooglePlaceService();
	}
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() throws Exception {
		URI redisUri = new URI(System.getenv("REDISCLOUD_URL"));
		JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
		jedisFactory.setHostName(redisUri.getHost());
		jedisFactory.setPort(redisUri.getPort());
		jedisFactory.setPassword(redisUri.getUserInfo().split(":",2)[1]);
		return jedisFactory;
	}

	@Bean
	public StringRedisSerializer stringSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() throws Exception {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setKeySerializer(stringSerializer());
		redisTemplate.setHashKeySerializer(stringSerializer());
		redisTemplate.setHashValueSerializer(stringSerializer());
		redisTemplate.setValueSerializer(stringSerializer());
		return redisTemplate;
	}
	
	@Bean
	public PlacesService placeService() {
		return new PlacesService();
	}
	
	@Bean
	public WhispirService whispirService() {
		return new WhispirService();
	}
	
	@Bean 
	public HttpComponentsClientHttpRequestFactory clientFactory() {
		HttpComponentsClientHttpRequestFactory http = new HttpComponentsClientHttpRequestFactory();
		
		http.setHttpClient(HttpClients.createMinimal());
		http.setConnectTimeout(0);
		http.setConnectionRequestTimeout(0);
		http.setReadTimeout(0);
		return http;
	}
	
	@Bean
	public TourGuideMessageService tourGuideService() {
		return new TourGuideMessageService();
	}
	
    public static void main(String[] args) {
        SpringApplication.run(MyTourGuideApplication.class, args);
    }
}
