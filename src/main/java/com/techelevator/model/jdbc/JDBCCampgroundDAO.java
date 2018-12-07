package com.techelevator.model.jdbc;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Campground;
import com.techelevator.model.CampgroundDAO;
import com.techelevator.model.Park;

public class JDBCCampgroundDAO implements CampgroundDAO {

private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Campground> getCampgroundList(int parkId) {
		
		List<Campground> campgroundList = new ArrayList<Campground>();
		
		String sql = "SELECT * FROM campground WHERE park_id = ? ORDER BY campground_id;";
		
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(sql, parkId);
		
		while(results.next()) {
			Campground camp = new Campground();
			camp.setCampground_id(results.getInt("campground_id"));
			camp.setPark_id(results.getInt("park_id"));
			camp.setName(results.getString("name"));
			camp.setOpen_from_mm(results.getString("open_from_mm"));
			camp.setOpen_to_mm(results.getString("open_to_mm"));
			camp.setDaily_fee(results.getBigDecimal("daily_fee"));
			campgroundList.add(camp);
		}
		
		
		return campgroundList;
	}

	@Override
	public List<Park> getParkList() {
		
		List<Park> parkMap = new ArrayList<Park>();
		
		String sql = "SELECT * FROM park ORDER BY name;";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Park thePark = new Park();
			thePark.setId(results.getInt("park_id"));
			thePark.setName(results.getString("name"));
			thePark.setLocation(results.getString("location"));
			thePark.setEstablish_date(results.getDate("establish_date").toLocalDate());
			thePark.setArea(results.getInt("area"));
			thePark.setVisitors(results.getInt("visitors"));
			thePark.setDescription(results.getString("description"));
			parkMap.add(thePark);
		}
		
		return parkMap;
	}
	
	
}
