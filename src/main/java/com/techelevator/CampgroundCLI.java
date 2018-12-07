package com.techelevator;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.model.Campground;
import com.techelevator.model.CampgroundDAO;
import com.techelevator.model.Park;
import com.techelevator.model.ReservationDAO;
import com.techelevator.model.Site;
import com.techelevator.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.model.jdbc.JDBCReservationDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {

	//private static final String[] MAIN_MENU_OPTIONS;
	
	private static final String PARK_INFO_OPTION_VIEW = "View Campgrounds";
	private static final String PARK_INFO_OPTION_SEARCH = "Search for Reservation";
	private static final String PARK_INFO_OPTION_RETURN = "Return to Previous Screen";
	private static final String[] PARK_INFO_OPTIONS = new String[] { PARK_INFO_OPTION_VIEW, 
																		PARK_INFO_OPTION_SEARCH, 
																		PARK_INFO_OPTION_RETURN};
	
	private static final String CAMPGROUND_INFO_OPTION_SEARCH = "Search for Available Reservation";
	private static final String CAMPGROUND_INFO_OPTION_RETURN = "Return to Previous Screen";
	private static final String[] CAMPGROUND_INFO_OPTIONS = new String[] {CAMPGROUND_INFO_OPTION_SEARCH, 
																			CAMPGROUND_INFO_OPTION_RETURN};
	
	private static final String RESERVATION_ARRIVAL_DATE = "What is the arrival date (mm/dd/yyyy)? ";
	private static final String RESERVATION_DEPARTURE_DATE = "What is the departure date (mm/dd/yyyy)? ";
//	private static final String[] RESERVATION_OPTIONS = new String[] {RESERVATION_ARRIVAL_DATE, RESERVATION_DEPARTURE_DATE};
	
	//other options?
	
	private Menu menu;
	private boolean isMainMenu;
	private CampgroundDAO campgroundDAO;
	private ReservationDAO reservationDAO;
	private List<Park> parkList;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		CampgroundCLI application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		this.menu = new Menu(System.in, System.out);
		// create your DAOs here
		this.campgroundDAO = new JDBCCampgroundDAO(datasource);
		this.reservationDAO = new JDBCReservationDAO(datasource);
	}
	
	public void run() {
		parkList = campgroundDAO.getParkList();
		Park [] MAIN_MENU_OPTIONS = parkList.toArray(new Park[parkList.size()]);
		
		while(true) {
			this.isMainMenu = true;
			Park choice = (Park)this.menu.getChoiceFromOptions(MAIN_MENU_OPTIONS, isMainMenu);
			
			if(choice.getName().equals("Q")) {
				System.out.println("Ending Program");
				System.exit(0);
			} else {
				runParkInfoScreen(choice);						
			}
		}
		
	}	//close run()
	
	private void runParkInfoScreen(Park park) {
		boolean isDone = false;
		
		while(!isDone) {
			displayParkInfo(park);
			String campChoice = (String)this.menu.getChoiceFromOptions(PARK_INFO_OPTIONS);
			
			if(campChoice.equals(PARK_INFO_OPTION_VIEW)) {
				//display list of campgrounds
				runParkCampgroundsMenu(park);
			} else if(campChoice.equals(PARK_INFO_OPTION_SEARCH)) {
				System.out.println("Searching reservations.");
			} else if(campChoice.equals(PARK_INFO_OPTION_RETURN)) {
				isDone = true;
			}
		}	// close while

	}	// close runParkInfoScreen()
	
	private void runParkCampgroundsMenu(Park parkChoice) {
		boolean isDone = false;
		
		int parkID = parkChoice.getId();
		List<Campground> campgroundList = this.campgroundDAO.getCampgroundList(parkID);
		Campground[] campArray = campgroundList.toArray(new Campground[campgroundList.size()]);
		displayCampgrounds(parkChoice.getName(), campArray);
		
		while(!isDone) {
			String siteChoice = (String)this.menu.getChoiceFromOptions(CAMPGROUND_INFO_OPTIONS);
			
			if(siteChoice.equals(CAMPGROUND_INFO_OPTION_SEARCH)) {
				//make array of valid campgroundIds
				Integer[] campgroundIdList = new Integer[campArray.length];
				for(int i = 0; i < campArray.length; i++) {
					campgroundIdList[i] = campArray[i].getCampground_id();
				}
				
				//String for display choice
				String campgroundPick = "Which campground (enter 0 to cancel)? ";
				
				int campgroundId = (int)this.menu.getChoiceFromOptions(campgroundIdList, campgroundPick);
				if(campgroundId == 0) {		//equivalent to "0" for quit
					isDone = true;
					break;
				}
				LocalDate arrivalDate = this.menu.dateSelection(RESERVATION_ARRIVAL_DATE);
				LocalDate departureDate = this.menu.dateSelection(RESERVATION_DEPARTURE_DATE);
				
				List<Site> availableSites = 
						this.reservationDAO.getAvailableSites(campgroundId, arrivalDate, departureDate);
				runMakeReservationMenu(availableSites, arrivalDate, departureDate);				
			} else if(siteChoice.equals(CAMPGROUND_INFO_OPTION_RETURN)) {
				isDone = true;
				//is there a less hacky way to do this??
				//return
				//displayParkInfo(parkList.get(parkChoice));
			}
		}
	}	// close runParkCampgroundsMenu()
	
	private void runMakeReservationMenu(List<Site> availableSites, LocalDate arrivalDate, LocalDate departureDate) {
		boolean isDone = false;
		//is cancellation working?
		while(!isDone) {
			Integer[] siteNums = displaySites(availableSites);
			//which site should be reserved? (0 cancels)
			String siteQuery = "Which site should be reserved (enter 0 to cancel)? ";
			int siteId = (int)this.menu.getChoiceFromOptions(siteNums, siteQuery);
			
			if(Integer.parseInt(siteQuery) > 0) {
				//what name should the reservation be under?
				String nameQuery = "What name should the reservation be under? ";
				String nameReserve = (String)this.menu.getChoiceFromOptions(nameQuery);
				//saveReservation needs to be modified to return the reservation id number
				int reservationId = this.reservationDAO.saveReservation(siteId, nameReserve, arrivalDate, departureDate);
				
				System.out.println("The reservation has been made and the confirmation id is: " + reservationId);
			}
			isDone = true;
		}
	}	// close runMakeReservationMenu()
	
	//DAO method??
	private void displayParkInfo(Park park) {
		System.out.println();
		System.out.println(park.getName() + " National Park");
		System.out.printf("%-18s%-20s\n", "Location:", park.getLocation());
		System.out.printf("%-18s%-20s\n", "Established:", park.getEstablish_date().format(formatter));	
		System.out.printf("%-18s%-20s\n", "Area:", NumberFormat.getNumberInstance(Locale.getDefault()).format(park.getArea()) + " sq km");		
		System.out.printf("%-18s%-20s\n", "Annual Visitors:", NumberFormat.getNumberInstance(Locale.getDefault()).format(park.getVisitors()));	
		System.out.println();
		String formattedDescription = formatParkDescription(park.getDescription());
		System.out.println(formattedDescription);
	}	// close displayParkInfo()
	
	private void displayCampgrounds(String parkName, Campground[] campArray) {
		System.out.println();
		System.out.println(parkName + " National Park Campgrounds\n");
		//TODO length for campground name might need to be variable somehow....
		//getLongestName method?
		System.out.printf("%-5s%-35s%-10s%-10s%-10s\n", "", "Name", "Open", "Close", "Daily Fee");
		for(int i = 0; i < campArray.length; i++) {
			String openDate = dateConverter(campArray[i].getOpen_from_mm());
			String closeDate = dateConverter(campArray[i].getOpen_to_mm());
			System.out.printf("%-5s%-35s%-10s%-10s%-10s\n", "#" + campArray[i].getCampground_id(), campArray[i].getName(), 
					openDate, closeDate, "$" + campArray[i].getDaily_fee() + "0");
		}
		
	}	// close displayCampgrounds()
	
	private Integer[] displaySites(List<Site> availableSites) {
		Integer[] siteNums = new Integer[availableSites.size()];
		String yes = "Yes";
		String no = "No";
		String n_a = "N/A";
		
		System.out.println();
		System.out.println("Results Matching Your Search Criteria");
		System.out.printf("%-10s%-12s%-15s%-17s%-10s%-8s\n", "Site No.", "Max Occup.", 
				"Accessible?", "Max RV Length", "Utility", "Cost");
		for(int i = 0; i < availableSites.size(); i++) {
			//convert true/false to yes/no
			String accessible;
			String utility;
			if(availableSites.get(i).getAccessible() == true) {
				accessible = yes;
			} else {
				accessible = no;
			}
			if(availableSites.get(i).getUtilities() == true) {
				utility = yes;
			} else {
				utility = no;
			}
			//convert 0 to N/A
			String rv;
			if(availableSites.get(i).getMax_rv_length() > 0) {
				rv = Integer.toString(availableSites.get(i).getMax_rv_length());
			} else {
				rv = n_a;
			}
			System.out.printf("%-10s%-12s%-15s%-17s%-10s%-8s\n", availableSites.get(i).getSite_number(), 
					availableSites.get(i).getMax_occupancy(), accessible, 
					rv, utility, 
					availableSites.get(i).getDaily_rate());
			//add site number to array
			siteNums[i] = availableSites.get(i).getSite_number();
		}	// end for loop
		return siteNums;
	}
	
	private String formatParkDescription(String parkDescription) {
		//create a method to format a long string so it has no more than about 100 characters per line
		String formattedDescription = "";
		String subString = "";
		int curLength = 100;
		int maxLength = 100;		//make constant at top of class, don't want to hunt for it
		
		for(int i = 0; i < parkDescription.length(); i+=curLength) {
			if(i+maxLength < parkDescription.length()) {
				boolean isDone = false;
				while(!isDone) {
					if(!parkDescription.substring(i+(curLength-1), i+curLength).equals(" ")) {
						curLength--;
					} else {
						subString = parkDescription.substring(i, i+curLength);
						isDone = true;
					}
				}	//close while()
			} else {
				subString = parkDescription.substring(i, parkDescription.length());
			}
			formattedDescription += subString + "\n";
		}
		
		return formattedDescription;
	}	// close formatParkDescription
	
	//This should go in the DAO perhaps
	private String dateConverter(String dateString) {
		String convertedDate = "";
		
		switch (dateString) {
			case "01": convertedDate = "January";
				break;
			case "02": convertedDate = "February";
				break;
			case "03": convertedDate = "March";
				break;
			case "04": convertedDate = "April";
				break;
			case "05": convertedDate = "May";
				break;
			case "06": convertedDate = "June";
				break;
			case "07": convertedDate = "July";
				break;
			case "08": convertedDate = "August";
				break;
			case "09": convertedDate = "Septembr";
				break;
			case "10": convertedDate = "October";
				break;
			case "11": convertedDate = "November";
				break;
			case "12": convertedDate = "December";
				break;
		}
		
		return convertedDate;
	}	// close dateConverter
	
}	// close CampgroundCLI
