package com.mycompany;

import java.net.URI;

import org.apache.http.impl.client.HttpClients;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
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
	
	private static final String QNAME = "WhispirMessageQueue";

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
		jedisFactory.setPassword(redisUri.getUserInfo().split(":", 2)[1]);
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

	@Bean
	public Queue queue() {
		return new Queue(QNAME, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange("registration-exchange");
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QNAME);
	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory("owl.rmq.cloudamqp.com");
		factory.setUsername("rfpcegnu");
		factory.setPassword("BCPkpO4dQJ77pc2Xg0jMHbIwTHYzF65-");
		factory.setVirtualHost("rfpcegnu");
		return factory;
	}

	@Bean
	public SimpleMessageListenerContainer container(
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(QNAME);
		container.setMessageListener(listenerAdapter);
		container.setAcknowledgeMode(AcknowledgeMode.NONE);
		// container.setMessageConverter(new Jackson2JsonMessageConverter());
		return container;
	}

	@Bean
	public WhispirMessageReceiver receiver() {
		return new WhispirMessageReceiver();
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(WhispirMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "handleWhispirMessage");
	}

	public static void main(String[] args) {
		SpringApplication.run(MyTourGuideApplication.class, args);
	}
}
