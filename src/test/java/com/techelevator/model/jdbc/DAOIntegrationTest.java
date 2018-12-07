package com.techelevator.model.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Site;

public class DAOIntegrationTest {

	/* Using this particular implementation of DataSource so that
	 * every database interaction is part of the same database
	 * session and hence the same database transaction */
	private static SingleConnectionDataSource dataSource;
	
	/* Before any tests are run, this method initializes the datasource for testing. */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/* The following line disables autocommit for connections 
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}
	
	/* After all tests have finished running, this method will close the DataSource */
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	/* This method provides access to the DataSource for subclasses so that 
	 * they can instantiate a DAO for testing */
	protected DataSource getDataSource() {
		return dataSource;
	}
	
	@Test
	public void testReservationValidation() {
		//instantiate JDBCReservationDAO
		JDBCReservationDAO dao = new JDBCReservationDAO(getDataSource());
		
		//create fake campground and get back the autogen camp_id
		String sqlInsertCamp = "INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (1, 'Test Site', 06, 09, 25.00);";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
		jdbcTemplate.update(sqlInsertCamp);
		
		String sqlGetCampNum = "SELECT currval('campground_campground_id_seq');";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetCampNum);
		results.next();
		int campNum = results.getInt("currval");
		
		//create a single fake campsite and get back autogen id
		String sqlInsertSite = "INSERT INTO site (campground_id, site_number) "
				+ "VALUES (?, 999);";
		jdbcTemplate.update(sqlInsertSite, campNum);
		
		String sqlGetSiteNum = "SELECT currval('site_site_id_seq');";
		results = jdbcTemplate.queryForRowSet(sqlGetSiteNum);
		results.next();
		int siteNum = results.getInt("currval");
		
		//create fake reservations for the campsite
		String sqlInsertRes = "INSERT INTO reservation (site_id, name, from_date, to_date) "
				+ "VALUES (?, 'A Reservation', '2018-06-05', '2018-07-15');";
		jdbcTemplate.update(sqlInsertRes, siteNum);
		
		sqlInsertRes = "INSERT INTO reservation (site_id, name, from_date, to_date) "
				+ "VALUES (?, 'B Reservation', '2018-07-20', '2018-07-25');";
		jdbcTemplate.update(sqlInsertRes, siteNum);
		
		sqlInsertRes = "INSERT INTO reservation (site_id, name, from_date, to_date) "
				+ "VALUES (?, 'C Reservation', '2018-08-03', '2018-08-09');";
		jdbcTemplate.update(sqlInsertRes, siteNum);
		
		sqlInsertRes = "INSERT INTO reservation (site_id, name, from_date, to_date) "
				+ "VALUES (?, 'D Reservation', '2018-08-13', '2018-08-17');";
		jdbcTemplate.update(sqlInsertRes, siteNum);
		
		//pass into getAvailableSites**(int id, LocalDate from, LocalDate to)
		//check all corner cases
		
		//Check for a valid date, should return map size 1
		List<Site> top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 16), LocalDate.of(2018, 07, 18));
		assertEquals("testReservationValidation 07/16/2018 - 07/18/2018", 1, top5Camps.size());
		
		//check for valid date where end date matches existing reservation start date
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 16), LocalDate.of(2018, 7, 20));
		assertEquals("testReservationValidation 07/16/2018 - 07/20/2018", 1, top5Camps.size());
		
		//check for valid date where start date matches existing reservation end date
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 25), LocalDate.of(2018, 7, 27));
		assertEquals("testReservationValidation 07/25/2018 - 07/27/2018", 1, top5Camps.size());
		
		//check for valid date where both start and end fit in
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 15), LocalDate.of(2018, 7, 20));
		assertEquals("testReservationValidation 07/15/2018 - 07/20/2018", 1, top5Camps.size());
		
		//check for valid reservation across two months
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 26), LocalDate.of(2018, 8, 01));
		assertEquals("testReservationValidation 07/26/2018 - 08/01/2018", 1, top5Camps.size());
		
		//Check for invalid end date
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 18), LocalDate.of(2018, 07, 21));
		assertEquals("testReservationValidation 07/18/2018 - 07/21/2018", 0, top5Camps.size());
		
		//check for invalid start date
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 24), LocalDate.of(2018, 07, 26));
		assertEquals("testReservationValidation 07/24/2018 - 07/26/2018", 0, top5Camps.size());
		
		//check for identical reservation
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 20), LocalDate.of(2018, 07, 25));
		assertEquals("testReservationValidation 07/20/2018 - 07/25/2018", 0, top5Camps.size());
		
		//check for invalid start/end dates in two different reservations
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 14), LocalDate.of(2018, 07, 21));
		assertEquals("testReservationValidation 07/14/2018 - 07/21/2018", 0, top5Camps.size());
		
		//check for invalid reservation encompassing another
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 8, 02), LocalDate.of(2018, 8, 10));
		assertEquals("testReservationValidation 08/02/2018 - 08/10/2018", 0, top5Camps.size());
		
		//check for invalid reservation inside another
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 8, 04), LocalDate.of(2018, 8, 8));
		assertEquals("testReservationValidation 08/04/2018 - 08/08/2018", 0, top5Camps.size());
		
		//check for invalid reservation across two months
		top5Camps = dao.getAvailableSites(campNum, LocalDate.of(2018, 07, 30), LocalDate.of(2018, 8, 04));
		assertEquals("testReservationValidation 07/30/2018 - 08/04/2018", 0, top5Camps.size());
		
	}
}
