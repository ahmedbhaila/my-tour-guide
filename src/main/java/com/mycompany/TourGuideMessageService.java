package com.mycompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

public class TourGuideMessageService {
	
	private static final String NAME = "{name}";
	
	@Value("${whispir.message.template.id}")
	String messageTemplateId;
	
	@Value("${welcome.message.body}")
	String messageBody;
	
	@Autowired
	WhispirService whispirService;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public void sendWelcomeMessage(String phone, String name) {
		whispirService.sendSMS(phone, messageTemplateId, "TourGuideCallback test", messageBody.replace(NAME, name));
	}
	
	public void sendGenericMessage(String phone, String message) {
		String alert = (String) redisTemplate.opsForHash().get("subscriber:" + phone, "alerts");
		if(alert.equals("true")) {
			whispirService.sendSMS(phone, messageTemplateId, null, message);
		}
	}
}
