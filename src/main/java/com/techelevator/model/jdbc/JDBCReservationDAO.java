package com.techelevator.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.ReservationDAO;
import com.techelevator.model.Site;

public class JDBCReservationDAO implements ReservationDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Site> getAvailableSites(int campgroundId, LocalDate arrivalDate, LocalDate departureDate) {
		
		List<Site> availableSites = new ArrayList<Site>();
		
		String sql = "SELECT site.site_number, max_occupancy, accessible, max_rv_length, utilities, daily_fee, site.site_id " + 
				"FROM site " + 
				"JOIN campground ON campground.campground_id = site.campground_id " + 
				"WHERE site.campground_id = ? " + 
				"AND site_id NOT IN (SELECT site_id FROM reservation " + 
				"WHERE (? > from_date AND ? < to_date) " + 
				"OR (? < to_date AND ? > from_date) " + 
				"OR (? >= from_date AND ? <= to_date) " + 
				"OR (? < from_date AND ? > to_date)) " +
				"ORDER BY site_number " + 
				"LIMIT 5;";
		
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(sql, campgroundId, 
				arrivalDate, arrivalDate, 
				departureDate, departureDate, 
				arrivalDate, departureDate, 
				arrivalDate, departureDate);
		
		while(results.next()) {
			Site theSite = new Site();
			theSite.setSite_number(results.getInt("site_number"));
			theSite.setMax_occupancy(results.getInt("max_occupancy"));
			theSite.setAccessible(results.getBoolean("accessible"));
			theSite.setMax_rv_length(results.getInt("max_rv_length"));
			theSite.setUtilities(results.getBoolean("utilities"));
			theSite.setDaily_rate(results.getBigDecimal("daily_fee"));
			availableSites.add(theSite);
		}
		return availableSites;	
	}

	@Override
	public int saveReservation(int site_id, String name, LocalDate from_date, LocalDate to_date) {
		
		int res_id = 0;
		
		jdbcTemplate.update("BEGIN; " + 
				"INSERT INTO reservation (site_id, name, from_date, to_date) " + 
				"VALUES (?, ?, ?, ?); ", site_id, name, from_date, to_date);
		
		String sql = "SELECT currval('reservation_reservation_id_seq') AS res_id FROM reservation ORDER BY reservation_id DESC LIMIT 1; ";
				
		SqlRowSet result = this.jdbcTemplate.queryForRowSet(sql);
		
		if(result.next()) {
			res_id = result.getInt("res_id");
		}
		
		jdbcTemplate.update("COMMIT;");
		
		return res_id;
	}

}
