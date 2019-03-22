/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callzone;

/**
 *
 * @author CET
 */
public class Global10Card extends PhoneCard{//PhoneCard class for Global10Card (child class of PhoneCard)
	  //define variables used within class
	  protected final double costPerMinCan = 0.07;
	  protected final double costPerMinUsa = 0.15;
	  protected final double costPerMinEuro = 0.30;
	  protected final double costPerMinAs = 0.45;
	  protected final double costPerMinAf = 0.60;
	  protected final double costPerMinAuNzLa = 0.45;
	  protected final double weeklyFee=1.00;
	  public Global10Card (long no, int passwd){//creating Global10Card using PhoneCard method
	    super(no, passwd, 10.00);//calls variable from PhoneCard class, except the balance which is stated
	  }
	  public boolean allowed (CallZone zone){//contacts the CallZone class to see if the zone requested for card is valid for type of card
	    if(zone==CallZone.CANADA || zone==CallZone.USA || zone==CallZone.EUROPE || zone==CallZone.ASIA || zone==CallZone.AFRICA || zone==CallZone.LATINAM || zone==CallZone.ANZ){
	      return true;
	    }
	    else {
	      return false;
	    }
	  }
	  public double costPerMin (CallZone zone){//method used to calculate the cost of a call based on the zone being called
	    costPerMin = 0.0;
	    
	    //if statement to match zone read from driver and zone listed in enum class, if they match assign the cost per minute for the zone
	    if(zone.equals(CallZone.CANADA)){
	      costPerMin = costPerMinCan;
	    }
	    if (zone.equals(CallZone.USA)){
	      costPerMin = costPerMinUsa;
	    }
	    if (zone.equals(CallZone.EUROPE)){
	      costPerMin=costPerMinEuro;
	    }
	    if (zone.equals(CallZone.ASIA)){
	      costPerMin = costPerMinAs;
	    }
	    if (zone.equals(CallZone.AFRICA)){
	      costPerMin = costPerMinAf;
	    }
	    if (zone.equals(CallZone.LATINAM) || zone.equals(CallZone.ANZ)){
	      costPerMin = costPerMinAuNzLa;
	    }
	    return costPerMin;
	  }
	  public void deductWeeklyFee(){//method to deduct the weekly fee for the card
	    balance = balance-weeklyFee;
	    if(weeklyFee < balance){
	      balance = balance-weeklyFee;
	    }
	    else{
	      balance = 0.00;
	    }
	  }
	}
