package com.techelevator.model;

import java.math.BigDecimal;

public class Site {
	
	private int site_id;
	private int campground_id;
	private int site_number;
	private int max_occupancy;
	private Boolean accessible;
	private int max_rv_length;
	private Boolean utilities;
	private BigDecimal daily_rate;
	
	public BigDecimal getDaily_rate() {
		return daily_rate;
	}
	public void setDaily_rate(BigDecimal daily_rate) {
		this.daily_rate = daily_rate;
	}
	public int getSite_id() {
		return site_id;
	}
	public void setSite_id(int site_id) {
		this.site_id = site_id;
	}
	public int getCampground_id() {
		return campground_id;
	}
	public void setCampground_id(int campground_id) {
		this.campground_id = campground_id;
	}
	public int getSite_number() {
		return site_number;
	}
	public void setSite_number(int site_number) {
		this.site_number = site_number;
	}
	public int getMax_occupancy() {
		return max_occupancy;
	}
	public void setMax_occupancy(int max_occupancy) {
		this.max_occupancy = max_occupancy;
	}
	public Boolean getAccessible() {
		return accessible;
	}
	public void setAccessible(Boolean accessible) {
		this.accessible = accessible;
	}
	public int getMax_rv_length() {
		return max_rv_length;
	}
	public void setMax_rv_length(int max_rv_length) {
		this.max_rv_length = max_rv_length;
	}
	public Boolean getUtilities() {
		return utilities;
	}
	public void setUtilities(Boolean utilities) {
		this.utilities = utilities;
	}

}
