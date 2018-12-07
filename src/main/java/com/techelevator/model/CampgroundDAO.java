package com.techelevator.model;

import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getCampgroundList(int parkId);
	
	public List<Park> getParkList();
		
}
