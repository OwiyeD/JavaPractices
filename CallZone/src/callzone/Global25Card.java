/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callzone;

public class Global25Card extends PhoneCard{//PhoneCard class for Global25Card (child class of PhoneCard)
	   //define variables used within class
	  protected final double costPerMinCan = 0.05;
	   protected final double costPerMinUsa = 0.10;
	   protected final double costPerMinEuro = 0.20;
	   protected final double costPerMinAsAf = 0.40;
	   protected final double costPerMinAuNzLa = 0.30;
	   protected final double weeklyFee=1.00;
	  public Global25Card (long no, int passwd){//creating Global25Card using PhoneCard method
	    super(no, passwd, 25.00);//calls variable from PhoneCard class, except the balance which is stated
	  }
	  public boolean allowed (CallZone zone){//contacts the CallZone class to see if the zone requested for card is valid for type of card
	    if(zone.equals(CallZone.CANADA) || zone.equals(CallZone.USA) || zone.equals(CallZone.EUROPE) || zone.equals(CallZone.ASIA) || zone.equals(CallZone.AFRICA) || zone.equals(CallZone.LATINAM) || zone.equals(CallZone.ANZ)){
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
	   if (zone.equals(CallZone.ASIA) || zone.equals(CallZone.AFRICA)){
	     costPerMin = costPerMinAsAf;
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
