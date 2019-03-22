/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callzone;

/**************************************************************************
	
	ITEC 1630 Assignment 1
	
	Family name:Morris
	
	Given name:Christopher
	
	Student number:209142027
	
	Section (M, N, or O):M
	
	
	Marking Template: (-10 marks if this template is not included)
	
	Style (variable naming, indentation, & Layout)  _____/5
	
	Comments                                        _____/5
	
	Proper use of inheritance & polymorphism        _____/15
	  (by marker inspection of printout)
	
	Code Compiles?                                  _____(yes/no)
	
	Successful execution of test cases              _____/75
	
	Total                                           _____/100
	
	According to this template the maximum mark you can get for code that does 
	not compile is 25/100.  If you do not provide a printout of your code 
	including this template, the maximum mark is 75/100.
	
	
	****************************************************************************/
	
	import java.util.Scanner;
	
	public class SuperPhoneCardInc
	{  public static void main(String[] args)
	   {  CardTable ct = new CardTable();
	      Scanner in = new Scanner(System.in);
	      String line = null;
	      boolean done = false;
	      if(!in.hasNextLine())
	      {  done = true;
	      }
	      else
	      {  line = in.nextLine();
	      }
	      if(!done && line.length() >= 4 && line.substring(0,4).equals("quit"))
	      {  done = true;
	      }
	      while(!done)
	      {  System.out.println("Input: " + line);
	         Scanner inl = new Scanner(line);
	         String command = "";
	         if(inl.hasNext())
	  {  command = inl.next();
	         }
	         if(command.equals("add"))
	         {  boolean invalidArgs = false;
	     long no = 0;
	            int passwd = 0;
	            String cardType = null;
	     if(inl.hasNextLong())
	     {  no = inl.nextLong();
	            }
	            else
	     {  invalidArgs = true;
	     }
	     if(!invalidArgs && inl.hasNextInt())
	     {  passwd = inl.nextInt();
	            }
	            else
	     {  invalidArgs = true;
	     }
	     if(!invalidArgs && inl.hasNext())
	     {  cardType = inl.next();
	            }
	            else
	     {  invalidArgs = true;
	     }
	     if(!invalidArgs && (no <= 0 || passwd <= 0))
	     {  invalidArgs = true;
	     }
	            PhoneCard card = null;
	            if(!invalidArgs && cardType.equals("SuperNA10"))
	            {  card = new SuperNA10Card(no,passwd);
	     }
	            else if(!invalidArgs && cardType.equals("Global10"))
	            {  card = new Global10Card(no,passwd);
	     }
	            else if(!invalidArgs && cardType.equals("Global25"))
	            {  card = new Global25Card(no,passwd);
	     }
	     else
	            {  invalidArgs = true;
	            }
	            if(invalidArgs)
	            {  System.out.println("Error: invalid arguments for add command");
	     }
	     else if(ct.get(no) != null)
	            {  System.out.println("Error: card no " + no + " already exists");
	     }
	     else if(!ct.add(card))
	            {  System.out.println("Error: card table full");
	     }
	     else
	            {  System.out.println("Result: added card " + no);
	     }
	         }
	         else if(command.equals("getBalance"))
	         {  boolean invalidArgs = false;
	            long no = 0;
	            int passwd = 0;
	            if(inl.hasNextLong())
	            {
	             no = inl.nextLong();
	            }
	            else
	            {
	             invalidArgs = true;
	            }
	            if(!invalidArgs && inl.hasNextInt())
	            {
	             passwd = inl.nextInt();
	            }
	            else
	            {
	             invalidArgs = true;
	            }
	            if(!invalidArgs && (no <= 0 || passwd <= 0))
	            {
	             invalidArgs = true;
	            }
	            if(invalidArgs)
	            {
	             System.out.println("Error: invalid arguments for getBalance command");
	            }
	            else
	            {
	               PhoneCard card = ct.get(no);
	               if(card == null)
	               { 
	                System.out.println("Error: card no " + no + " does not exist");
	               }
	               else if(card.getPassword() != passwd)
	               { 
	                System.out.println("Error: password " + passwd + " incorrect");
	               }
	        else
	               {  System.out.printf("Result: card %d balance is %.2f%n",
	                                 no, card.getBalance());
	               }
	     }
	         }
	         else if(command.equals("getLimit")) { 
	           boolean invalidArgs = false;
	           long no = 0;
	           int passwd = 0;
	           String zone = "";
	           if(inl.hasNextLong()){ //if statement to see if the file has a card number 
	             no = inl.nextLong();//places the files card number in variable "no"
	           }
	           else{
	             invalidArgs = true;//if no card number, command becomes invalid
	           }
	           if(!invalidArgs && inl.hasNextInt()){ //if statement to see if command is valid and the file has the card password
	             passwd = inl.nextInt();//places file password in variable "passwd"
	           }
	           else {//if no password is give, command becomes invalid 
	             invalidArgs = true;
	           }
	           if(!invalidArgs && (no <= 0 || passwd <= 0)){//if statement to see if command is valid and the file has the zone to which the card is referencing
	             invalidArgs = true;
	           }
	           
	           if(!invalidArgs && inl.hasNext() ){//if statement to see if command is valid and the file has the zone to which the card is referencing 
	             zone = inl.next();//places zone from file in variable "zone"
	             if (!CallZone.isValidZone(zone)){//if statement to see if zone is a valid callzone
	               invalidArgs = true;//if zone is invalid set command as invalid
	             }
	           }
	           else { //if none of these arguments have been fulfilled, make command invalid 
	             invalidArgs = true;
	           }
	           
	           if(invalidArgs) {//if statement is invalid print error message
	             System.out.println("Error: invalid arguments for getBalance command");
	           }
	           else{//if statement is valid return card number from cardtable list
	             PhoneCard card = ct.get(no);
	             if(card == null){//if no card exist (does not match number read from file) print error message 
	               System.out.println("Error: card no " + no + " does not exist");
	             }
	             else if(card.getPassword() != passwd){//if card password from file does not match card password from cardtable print error message 
	               System.out.println("Error: password " + passwd + " incorrect");
	             }
	             else if(!card.allowed(CallZone.convertToZone(zone))) {//if card is not allowed to make a call to the specified zone print error message 
	               System.out.println("Error: Card number " + no + " not allowed for " + zone);
	             }
	             else{ //if every requirement is valid, print message stating the limit of the card, with it's number and zone it has called
	               System.out.printf("Result: card %d limit for zone %s is %d%n", no, zone, card.getLimit(CallZone.convertToZone(zone)));
	             }
	           }          
	         }
	         else if(command.equals("charge")) {
	           //variable used within "charge" method
	           boolean invalidArgs = false;
	           long no = 0;
	           int passwd = 0;
	           String zone = "";
	           int minutes = 0;
	           if(inl.hasNextLong()) { //if statement to see if the file has card number
	             no = inl.nextLong();//places the files card number in variable "no"
	           }
	           else {//if no card number, command becomes invalid
	             invalidArgs = true;
	           }
	           if(!invalidArgs && inl.hasNextInt()){//if statement to see if command is valid and the file has the card password 
	             passwd = inl.nextInt();//places file password in variable "passwd"
	           }
	           else{ //if no password is give, command becomes invalid
	             invalidArgs = true;
	           }
	           if(!invalidArgs && (no <= 0 || passwd <= 0)) {//if statement to see if command is valid and the file has the zone to which the card is referencing
	             invalidArgs = true;
	           }
	           
	           if(!invalidArgs && inl.hasNext() ){ //if no password is give, command becomes invalid
	             zone = inl.next();//places zone from file in variable "zone"
	             if (!CallZone.isValidZone(zone)){//if statement to see if zone is a valid callzone
	               invalidArgs = true;//if zone is invalid set command as invalid
	             }
	           }
	           else{//if none of these arguments have been fulfilled, make command invalid  
	             invalidArgs = true;
	           }
	           
	           if (!invalidArgs && inl.hasNextInt()){//if statement to see if command is valid and the file has the amount of minutes used
	             minutes = inl.nextInt();
	           }else {//if no minutes is given by file, make command invalid
	             invalidArgs = true;
	           }          
	           
	           if(invalidArgs) {//if command is invalid print error message for "charge" command
	             System.out.println("Error: invalid arguments for getBalance command");
	           }
	           else{//if commanf is valid return card no using get(no) command
	             PhoneCard card = ct.get(no);
	             double initialBalance = 0;
	             if (card != null){//if card exists, place the cards balance in variable "initalBalance"
	               initialBalance = card.getBalance();
	             }
	             
	             if(card == null){//if no card exists (does not match number read from file) print error message 
	               System.out.println("Error: card no " + no + " does not exist");
	             }
	             else if(card.getPassword() != passwd){//if card password from file does not match card password from cardtable print error message 
	               System.out.println("Error: password " + passwd + " incorrect");
	             }
	             else if(!card.allowed(CallZone.convertToZone(zone))) {//if card is not allowed to make a call to the specified zone print error message 
	               System.out.println("Error: Card number " + no + " not allowed for " + zone);
	             }
	             else if(!card.charge(minutes, CallZone.convertToZone(zone))){//if card is unabe to charge the minutes used (based on CallZone) print error message 
	               System.out.println("Error: card  "+no+" limit for zone "+zone+" is "+minutes+" minutes\n");
	             }
	             
	             else { //if every requirment is valid retrieve the new balance of the card selected and print how much was charged to card and the new balance              
	               double currentBalance = card.getBalance();
	               System.out.println("Result: card "+no+" charged " +(initialBalance-currentBalance)+ ", new balance is " +currentBalance);
	             }
	           }
	         }
	  else if(command.equals("deductWeeklyFee"))
	         {  PhoneCard card = ct.first();
	            while(card != null)
	     {  card.deductWeeklyFee();
	               System.out.printf("Result: card %d charged weekly fee%n",
	            card.getNumber());
	               card = ct.next();
	            }
	            System.out.println("Result: weekly fees deducted");
	         }
	  else if(command.equals("printAll"))
	         {  PhoneCard card = ct.first();
	            while(card != null)
	            {  System.out.printf("Result: %s%n", card);
	               card = ct.next();
	            }
	            System.out.println("Result: all cards printed");
	         }
	         else
	         {  System.out.println("Error: command invalid");
	         }
	         if(!in.hasNextLine())
	         {  done = true;
	         }
	         else
	         {  line = in.nextLine();
	         }
	         if(!done && line.length() >= 4 && line.substring(0,4).equals("quit"))
	         {  done = true;
	         }
	      }
	   }
	}

