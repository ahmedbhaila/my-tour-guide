package com.mycompany;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
class ResponseMessage implements Serializable {
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getAcknowledged() {
		return acknowledged;
	}
	public void setAcknowledged(String acknowledged) {
		this.acknowledged = acknowledged;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	String channel;
	String acknowledged;
	@Override
	public String toString() {
		return "ResponseMessage [channel=" + channel + ", acknowledged="
				+ acknowledged + ", content=" + content + "]";
	}
	String content;
}
@JsonIgnoreProperties(ignoreUnknown=true)
class Source implements Serializable {
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getVoice() {
		return voice;
	}
	public void setVoice(String voice) {
		this.voice = voice;
	}
	String mobile;
	@Override
	public String toString() {
		return "Source [name=" + name + ", mobile=" + mobile + ", voice="
				+ voice + "]";
	}
	String voice;
}

@JsonIgnoreProperties(ignoreUnknown=true)
class CustomParameters implements Serializable {
	String senderFullName;
	String mm;
	String yy;
	public String getSenderFullName() {
		return senderFullName;
	}

	public void setSenderFullName(String senderFullName) {
		this.senderFullName = senderFullName;
	}

	public String getMm() {
		return mm;
	}

	public void setMm(String mm) {
		this.mm = mm;
	}

	public String getYy() {
		return yy;
	}

	public void setYy(String yy) {
		this.yy = yy;
	}

	public String getDd() {
		return dd;
	}

	public void setDd(String dd) {
		this.dd = dd;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRecipientVoice() {
		return recipientVoice;
	}

	public void setRecipientVoice(String recipientVoice) {
		this.recipientVoice = recipientVoice;
	}

	public String getRecipientFirstName() {
		return recipientFirstName;
	}

	public void setRecipientFirstName(String recipientFirstName) {
		this.recipientFirstName = recipientFirstName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderFirstName() {
		return senderFirstName;
	}

	public void setSenderFirstName(String senderFirstName) {
		this.senderFirstName = senderFirstName;
	}

	public String getSenderLastName() {
		return senderLastName;
	}

	public void setSenderLastName(String senderLastName) {
		this.senderLastName = senderLastName;
	}

	String dd;
	String date;
	
	@JsonProperty("recipient_voice")
	String recipientVoice;
	
	@JsonProperty("recipient_first_name")
	String recipientFirstName;
	
	@JsonProperty("sender_email")
	String senderEmail;
	
	@JsonProperty("sender_first_name")
	String senderFirstName;;
	
	@Override
	public String toString() {
		return "CustomParameters [senderFullName=" + senderFullName + ", mm="
				+ mm + ", yy=" + yy + ", dd=" + dd + ", date=" + date
				+ ", recipientVoice=" + recipientVoice
				+ ", recipientFirstName=" + recipientFirstName
				+ ", senderEmail=" + senderEmail + ", senderFirstName="
				+ senderFirstName + ", senderLastName=" + senderLastName + "]";
	}

	@JsonProperty("sender_last_name")
	String senderLastName;
	
	
}
@JsonIgnoreProperties(ignoreUnknown=true)
public class WhispirCallbackMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String messageId;
	String messageLocation;
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageLocation() {
		return messageLocation;
	}

	public void setMessageLocation(String messageLocation) {
		this.messageLocation = messageLocation;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public CustomParameters getCustomParameters() {
		return customParameters;
	}

	public void setCustomParameters(CustomParameters customParameters) {
		this.customParameters = customParameters;
	}

	@JsonProperty("from")
	Source source;
	
	ResponseMessage responseMessage;
	
	CustomParameters customParameters;

	@Override
	public String toString() {
		return "WhispirCallbackMessage [messageId=" + messageId
				+ ", messageLocation=" + messageLocation + ", source=" + source
				+ ", responseMessage=" + responseMessage
				+ ", customParameters=" + customParameters + "]";
	}
	
	

}
