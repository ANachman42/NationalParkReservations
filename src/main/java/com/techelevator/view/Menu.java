package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

//TODO Need to rewrite code to not have to do this
//Had to switch from using Maps to Lists and required widespread rewrites
//Threw this together due to lack of time
import com.techelevator.model.Park;

public class Menu {

	private PrintWriter out;
	private Scanner in;

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}
	
	public Object getChoiceFromOptions(Object[] options, boolean isMainMenu) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options, isMainMenu);
			choice = getChoiceFromUserInput(options, isMainMenu);
		}
		return choice;
	}
	
	public Object getChoiceFromOptions(Object[] options, String displayOption) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(displayOption);
			choice = getChoiceFromUserInput(options, displayOption);
		}
		return choice;
	}
	
	public Object getChoiceFromOptions(String options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption <= options.length && selectedOption > 0) {
				choice = options[selectedOption - 1];
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	private Object getChoiceFromUserInput(Object[] options, boolean isMainMenu) {
		Object choice = null;
		String userInput = in.nextLine();
		//must check for String comparison before converting to Integer
		if(userInput.toUpperCase().equals("Q")) {
			/*This is a total hack and I am aware of it.
			Program was originally built with Maps and I only have a few
			hours to make it work using Lists*/
			Park park = new Park();
			park.setName("Q");
			choice = park;
		}		
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption <= options.length && selectedOption > 0) {
				choice = options[selectedOption - 1];
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	private Object getChoiceFromUserInput(Object[] options, String displayOption) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			for(int i = 0; i < options.length; i++) {
				if(selectedOption == (int)options[i]) {
					choice = options[i];
				}
			}
			//otherwise check for 0
			if(selectedOption == 0) {
				choice = 0;
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	private Object getChoiceFromUserInput(String options) {

		String userInput = in.nextLine();
		
		return userInput;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	private void displayMenuOptions(Object[] options, boolean isMainMenu) {
		out.println();
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.println(optionNum+") "+options[i]);
		}
		
		if(isMainMenu) {
			out.println("Q) quit");
		}
		
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}
	
	private void displayMenuOptions(String displayOption) {
		out.println();
		out.print(displayOption);
		out.flush();
	}
	
	public LocalDate dateSelection(String option) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate choice = null;
		String userInput = "";
		while(choice == null) {
			out.println();
			out.print(option);
			out.flush();
			try {
				userInput = in.nextLine();
				choice = LocalDate.parse(userInput, formatter);
			} catch (DateTimeParseException e) {
				//eat the exception!! nom nom
			}
			if(choice == null) {
				out.println("\n*** "+userInput+" is not a valid option ***\n");
			}
		}	// close while
		return choice;
	}
	
}
