/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callzone;

abstract class PhoneCard {
	  //variables used within class
	  protected long number;
	  protected int password;
	  protected double balance;
	  protected double costPerMin;
	  
	  
	  public PhoneCard (long no, int passwd, double bal){//method of creating PhoneCard object
	    assert no >= 0; //makes sure card number cannot equal 0
	    assert passwd >=0;//makes ssure card password cannot equal 0
	   number=no;
	   password=passwd;
	   balance=bal;
	  }
	  public long getNumber(){//method of returning card number
	    return number;
	  }
	  public int getPassword(){//method of returning card password
	    return password;
	  }
	  public double getBalance(){//method of returning card balance
	    return balance;
	  }
	  public void setBalance(double bal){//method of setting card balance
	    balance = bal;
	  }
	  public abstract boolean allowed (CallZone zone);//abstract method of allowing a card (defined in child classes)
	  public abstract double costPerMin (CallZone zone);//abstract method of finding the cost per minute (defined in child class)
	  public int getLimit (CallZone zone){//method of returning how many minutes a card can call based on it's balance
	    double maxMin = balance/costPerMin(zone);//equation of determining how many minutes remain per zone
	   
	    return (int)maxMin;
	  }
	  public boolean charge (int minutes, CallZone zone){//method of charging a card based on the minutes wished to use and the zone being called
	    
	    double charge = costPerMin(zone)*minutes;//returns the cost of the call
	    if(charge< balance){//if call can be made (based on balance), set the new balance after call
	      balance=balance-charge;
	      
	     return true; 
	    }
	    else 
	      return false;
	  }
	  public abstract void deductWeeklyFee();//abstact method of deducting weekly fee based on card type (defined in child class)
	  public String toString(){//method of return card number and balance
	     return "Card no:" +number+" has a balance of: "+balance; 
	  }
	}
