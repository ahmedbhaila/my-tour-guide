package com.mycompany;

public class CityMonument {
	protected String address;
	protected String phoneNumber;
	protected String latLng;
	protected String name;
	protected String url;
	protected String typeIcon;
	protected boolean tagged;
	public boolean isTagged() {
		return tagged;
	}
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
	public String getTypeIcon() {
		return typeIcon;
	}
	public void setTypeIcon(String typeIcon) {
		this.typeIcon = typeIcon;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getLatLng() {
		return latLng;
	}
	public void setLatLng(String latLng) {
		this.latLng = latLng;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
