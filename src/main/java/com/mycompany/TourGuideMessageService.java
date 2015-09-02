package com.mycompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class TourGuideMessageService {
	
	private static final String NAME = "{name}";
	
	@Value("${whispir.message.template.id}")
	String messageTemplateId;
	
	@Value("${welcome.message.body}")
	String messageBody;
	
	@Autowired
	WhispirService whispirService;
	
	public void sendWelcomeMessage(String phone, String name) {
		whispirService.sendSMS(phone, messageTemplateId, messageBody.replaceAll(NAME, name));
	}
}
