package com.mycompany;

import java.util.ArrayList;
import java.util.List;

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

	@Value("${whispir.callback.id}")
	String callbackId;
	
	@Value("${whispir.message.template.id}")
	String messageTemplateId;
	
	@Value("${whispir.callback.id}")
	String whispirCallbackId;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	public void handleWhispirMessage(WhispirCallbackMessage message) {
		String content = message.getResponseMessage().getContent();
		String profileName = message.getSource().getMobile();

		if (content.equalsIgnoreCase("sub")) {
			// user wants to register to receive alerts

			// save this preference in datastore
			redisTemplate.opsForHash().put(
					"subscriber:" + message.getSource().getVoice(), "alerts",
					"true");

			// send another text message to this user indicating their choice
			whispirService
					.sendSMS(
							message.getSource().getVoice(),
							alertTemplate,
							callbackId,
							"You have chosen to receive alerts from your location, if you ever want to stop receiving alert just type STOP and send to this number");

		}

		else if (content.equalsIgnoreCase("stp")) {
			// user wants to stop receiving alerts
			whispirService
					.sendSMS(
							message.getSource().getVoice(),
							alertTemplate,
							callbackId,
							"We have received your request to not send alerts anymore, if you would like to receive alerts again please type SUB and send to this number");

			// save this preference in datastore
			redisTemplate.opsForHash().put(
					"subscriber:" + message.getSource().getVoice(), "alerts",
					"false");
		}
		// the rest are text based numerical based on which actions will be
		// taken
		// 1. Hear about this point of interest
		// 2. Hear about the city
		// 3. Text me more information on this point of interest
		// 4. Text me more information for this city

		else {
			List<String> places = redisTemplate.opsForList().range(
					profileName + ":places_current", 0, -1);
			List<CityMonument> monuments = getInformation(places);

			if (content.equals("1")) {
				whispirService.sendVoiceCall(profileName, monuments);
			} else if (content.equals("2")) {
				
			} else if (content.equals("3")) {
				whispirService.sendSMS(profileName, messageTemplateId, whispirCallbackId, monuments);
			} else if (content.equals("4")) {

			}
		}
	}

	private List<CityMonument> getInformation(List<String> places) {
		List<CityMonument> monuments = new ArrayList<CityMonument>();
		places.forEach(p -> {
			CityMonument monument = new CityMonument();
			monument.setAddress((String) redisTemplate.opsForHash().get(p,
					"address"));
			monument.setLatLng((String) redisTemplate.opsForHash().get(p,
					"latLng"));
			monument.setName((String) redisTemplate.opsForHash().get(p, "name"));
			monument.setPhoneNumber((String) redisTemplate.opsForHash().get(p,
					"phoneNumber"));
			monument.setTypeIcon((String) redisTemplate.opsForHash().get(p,
					"typeIcon"));
			monument.setUrl((String) redisTemplate.opsForHash().get(p, "url"));
			monuments.add(monument);

		});
		return monuments;
	}
}
