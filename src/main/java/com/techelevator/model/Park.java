package com.techelevator.model;

import java.time.LocalDate;

public class Park {

	private int id;
	private String name;
	private String location;
	private LocalDate establish_date;
	private int area;
	private int visitors;
	private String description;
	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getEstablish_date() {
		return establish_date;
	}

	public void setEstablish_date(LocalDate establish_date) {
		this.establish_date = establish_date;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getVisitors() {
		return visitors;
	}

	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
