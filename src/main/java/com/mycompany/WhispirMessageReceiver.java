package com.mycompany;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class WhispirMessageReceiver {
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	WhispirService whispirService;
	
	@Value("${whispir.message.template.confirm.alert}")
	String alertTemplate;
	
	public void handleWhispirMessage(WhispirCallbackMessage message) {
		String content = message.getResponseMessage().getContent();
		
		System.out.println("Hello, I received content " + content);
		
		if(content.equals("1")) {
			// user wants to register to receive alerts
			
			// save this preference in datastore
			
			// send another text message to this user indicating their choice
			whispirService.sendSMS(message.getSource().getVoice(), alertTemplate, "TourGuideCallback test", "You have chosen to receive alerts from your location");
			
		}
		
		else if(content.equalsIgnoreCase("stop")) {
			// user wants to stop receiving alerts
		}
		// and so son
		
	}
}
