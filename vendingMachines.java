/*
 * Ryan McCullough
 * CS 1050-005
 * 
 * temp variables are used to get user input
 * The scanner is a jerk and wouldnt clear the System.in buffer so had to use scanner as local variable only
 */

import java.util.Scanner;
import java.util.ArrayList;

public class vendingMachines {
	public static void main(String[] args) {
		Controller c = new Controller();
		c.flowControl();
	}
}

class Controller {
	private ArrayList<TransHist> transhistory = new ArrayList<TransHist>();
	Items[] item = new Items[16];
	String[] userprompts = {
			"Enter Selection or admin to access admin features, or q to quit", 
			"Enter password",
			"Invalid input please re-enter",
			"Invalid input",
			"Enter admin password",
			"(R)efill items or (V)iew transaction log?",
			"This machine can only hold 20 of each item, please re-enter number of items to restock",
			"Enter item number",
			"Enter quantity to restock",
			"Item not found",
			"Amount required to displense",
			"Dispensing",
	};
	Validation v = new Validation();
	
	public void flowControl() {
		String temp, temp1;
		boolean cont = true;
		Scanner stdIn = new Scanner(System.in);
		
		createitems();
		displayContents(item);
		do {
			System.out.println(userprompts[0]);
			temp1 = stdIn.nextLine();
			temp = validator(temp1);
			if (temp.equalsIgnoreCase("admin")) {
				adminConsol();
			}
			else if (temp.equalsIgnoreCase("q")) {
				cont = false;
			}
			else {
				vendItem(findItem(temp));
			}
		} while (cont);
		
	}
	
	private void vendItem(int t) {
		double amt, amtneeded;
		double change = 0.0;
		Scanner stdIn = new Scanner(System.in);
		
		System.out.println(item[t].name + " selected please insert " + item[t].price);
		amt = stdIn.nextDouble();
		amtneeded = item[t].price - amt;
		while (amtneeded > 0.0) {
			System.out.println(userprompts[10] + " " + item[t].name + ": " + amtneeded + "\nPlease enter indicated amount");
			amtneeded -= stdIn.nextDouble();
		}
		if (amt > item[t].price) {
			change = amt-item[t].price;
		}
		System.out.println(userprompts[11] + " " + item[t].name);
		System.out.println(change + " dispensed");
		item[t].qoh--;
		transhistory.add(new TransHist(item[t].name, item[t].price, change));
	}
	
	private void createitems() {
		String[][] fillmachine = {
				{"Donut Gems", "20", "100", "1.75"},
				{"BBQ Lays", "20", "101", "1.40"},
				{"Gummiworms", "20", "102", "2.00"},
				{"Skittles", "20", "103", "1.40"},
				{"Twix", "20", "200", "1.40"},
				{"Snikers", "20", "201", "1.40"},
				{"Baby Ruth", "20", "202", "1.40"},
				{"Milkyway", "20", "203", "1.40"},
				{"Coke", "20", "300", "1.75"},
				{"Dr Pepper", "20", "301", "1.75"},
				{"Mt. Dew", "20", "302", "1.75"},
				{"Diet Coke", "20", "303", "1.75"},
				{"Coke Zero", "20", "400", "1.75"},
				{"TB Mt. Dew", "20", "401", "1.75"},
				{"Diet Dr Pepper", "20", "402", "1.75"},
				{"Coke Vanilla", "20", "403", "2.00"}
		};
		
		for (int i=0; i<item.length; i++) {
			item[i] = new Items(fillmachine[i][0], Integer.parseInt(fillmachine[i][1]), Integer.parseInt(fillmachine[i][2]), Double.parseDouble(fillmachine[i][3]));
		}
	}
	
	private String validator(String temp) {
		boolean valid = false;
		valid = v.val(temp);
		Scanner stdIn = new Scanner(System.in);
		
		while (!valid) {
			System.out.println(userprompts[2]);
			temp = stdIn.nextLine();
			valid = v.val(temp);
		}
		return temp;
	}
	
	private void adminConsol() {
		boolean valid;
		Scanner stdIn = new Scanner(System.in);
		
		System.out.println(userprompts[4]);
		valid = v.val1(stdIn.nextLine());
		if (valid) {
			adminConsolflow();
		}
		else {
			System.out.println(userprompts[3]);
		}
	}
	
	private void adminConsolflow() {
		String temp;
		boolean valid = false;
		Scanner stdIn2 = new Scanner(System.in);
				
		System.out.println(userprompts[5]);
		temp = stdIn2.nextLine();
		valid = v.adminval(temp.charAt(0));
		while (!valid) {
			System.out.println(userprompts[2]);
			temp = stdIn2.nextLine();
			valid = v.adminval(temp.charAt(0));
		}
		
		if (temp.charAt(0) == 'R' || temp.charAt(0) == 'r') {
			refillItems();
		}
		else {
			displayContents();
		}
	}
	
	private void refillItems() {
		String temp; 
		int temp1; 
		int refilqty=0;
		Scanner stdIn = new Scanner(System.in);
		
		System.out.println(userprompts[7]);
		temp = stdIn.nextLine();
		temp1 = findItem(temp) +1;
		if (temp1 > 0) {
			System.out.println(userprompts[8]);
			temp = stdIn.nextLine();
			refilqty = v.refillqty(temp, userprompts);
		}
		else {
			System.out.println(userprompts[9]);
			refillItems();
		}
		System.out.println(item[temp1-1].qoh);
		while ((item[(temp1-1)].qoh + refilqty) > 20) {
			System.out.println(userprompts[6]);
			refilqty = v.refillqty(temp, userprompts);
		}
		item[(temp1-1)].qoh += refilqty;
	}
	
	private int findItem(String temp) {
		boolean indexFound = false;
		int i=0;
		
		while (!indexFound && i<item.length) {
			if (item[i].number ==Integer.parseInt(temp)) {
				indexFound = true;
			}
			i++;
		}
		if (!indexFound) {
			i = -1;
		}
		return i-1;
	}
	
	private void displayContents(Items[] item) {
		System.out.printf("%-15s%-15s%-16s", "Item Name", " ", "Item Number");
		System.out.print("\n");
		for (int i=0; i<item.length;i++) {
			System.out.printf("\n%-15s%-15s%-16s", item[i].name,  "", item[i].number);
			System.out.println("\n-----------------------------------");
		}
	}
	
	private void displayContents() {
		double sum=0.0; 
		double sumchange=0.0;
		
		System.out.printf("%-15s%-15s%-16s%-15s%-10s", "Item Name", " ", "Item Amount", " ", "Change given");
		for (int i=0;i <transhistory.size();i++) {
			System.out.printf("\n%-15s%-15s%-16s%-15s%-10s", transhistory.get(i).itemName, " ", transhistory.get(i).price, " ", transhistory.get(i).change);
			sum += transhistory.get(i).price;
			sumchange += transhistory.get(i).change;
		}
		System.out.println("\nTotal Sales: " + sum);
		System.out.println("Total change given: " + sumchange);
	}
}

class Items {
	public String name;
	public int qoh, number;
	public double price;
	
	Items(String n, int qoh, int number, double price) {
		name = n;
		this.qoh = qoh;
		this.number = number;
		this.price = price;
	}
}

class TransHist {
	public String itemName;
	public double price, change;
	
	TransHist(String n, double price, double change) {
		itemName = n;
		this.price = price;
		this.change = change;
	}
}

class Validation {
	public boolean val(String temp) {
		int temp1;
		try {
			temp1 = Integer.parseInt(temp);
			if (temp1 == 100 || temp1 == 101 ||temp1 == 102 ||temp1 == 103 ||temp1 == 200 ||temp1 == 201 ||temp1 == 202 ||temp1 == 203 ||temp1 == 300 ||temp1 == 301 ||temp1 == 302 ||temp1 == 303 ||temp1 == 400 ||temp1 == 401 ||temp1 == 402 ||temp1 == 403) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			if (temp.equalsIgnoreCase("admin") || temp.equalsIgnoreCase("q")) {
				return true;
			}
			return false;
		}
	}
	
	public boolean val1(String temp) {
		if (temp.equals("admin")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean adminval(char temp) {
		if (temp == 'R' || temp == 'V' || temp == 'r' || temp == 'v') {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int refillqty(String temp, String[] userprompts) {
		boolean valid = false;
		Scanner stdIn = new Scanner(System.in);
		
		while (!valid) {
			System.out.println(Integer.parseInt(temp));
			try {
				Integer.parseInt(temp);
				if (Integer.parseInt(temp) < 20) {
					valid = true;
					break;
				}
				else {
					System.out.println(userprompts[6]);
					temp = stdIn.nextLine();
				}
			}
			catch (Exception e) {
				System.out.println(userprompts[2]);
				temp = stdIn.nextLine();
			}
		}
		return Integer.parseInt(temp);
	}
}