package com.techelevator.model;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
	
	public List<Site> getAvailableSites(int campgroundId, LocalDate arrivalDate, LocalDate departureDate);
	
	public int saveReservation(int site_id, String name, LocalDate from_date, LocalDate to_date);

}
