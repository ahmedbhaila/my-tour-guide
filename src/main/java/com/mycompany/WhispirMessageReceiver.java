package com.mycompany;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

public class WhispirMessageReceiver {
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	WhispirService whispirService;
	
	@Value("${whispir.message.template.confirm.alert}")
	String alertTemplate;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	
	public void handleWhispirMessage(WhispirCallbackMessage message) {
		String content = message.getResponseMessage().getContent();
		
		if(content.equalsIgnoreCase("sub")) {
			// user wants to register to receive alerts
			
			// save this preference in datastore
			redisTemplate.opsForHash().put("subscriber:" + message.getSource().getVoice(), "alerts", "true");
			
			// send another text message to this user indicating their choice
			whispirService.sendSMS(message.getSource().getVoice(), alertTemplate, null, "You have chosen to receive alerts from your location, if you ever want to stop receiving alert just type STOP and send to this number");
			
		}
		
		else if(content.equalsIgnoreCase("stop")) {
			// user wants to stop receiving alerts
			whispirService.sendSMS(message.getSource().getVoice(), alertTemplate, null, "We have received your request to not send alerts anymore, if you would like to receive alerts again please type SUB and send to this number");
			
			// save this preference in datastore
			redisTemplate.opsForHash().put("subscriber:" + message.getSource().getVoice(), "alerts", "false");
		}
		// and so son
		
	}
}
