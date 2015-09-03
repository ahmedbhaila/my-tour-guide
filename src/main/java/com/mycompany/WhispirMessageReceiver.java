package com.mycompany;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class WhispirMessageReceiver {
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	public void handleWhispirMessage(WhispirCallbackMessage message) {
		String content = message.getResponseMessage().getContent();
		
		if(content.equals("1")) {
			// user wants to register to receive alerts
		}
		
		else if(content.equalsIgnoreCase("stop")) {
			// user wants to stop receiving alerts
		}
		// and so son
		
	}
}
