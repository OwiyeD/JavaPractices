/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callzone;

public enum CallZone
	   {  CANADA, USA, EUROPE, ASIA, ANZ, LATINAM, AFRICA;
	
	      public static boolean isValidZone(String zone)
	      {  if(CANADA.toString().equals(zone) || 
	            USA.toString().equals(zone) || 
	            EUROPE.toString().equals(zone) || 
	            ASIA.toString().equals(zone) || 
	            ANZ.toString().equals(zone) || 
	            LATINAM.toString().equals(zone) || 
		    AFRICA.toString().equals(zone))
		 {  return true;
	         }
	         else
		 {  return false;
	         }
	      }
	
	      public static CallZone convertToZone(String zone)
	      {  if(CANADA.toString().equals(zone))
		 {  return CANADA;
	         }
	         else if(USA.toString().equals(zone))
		 {  return USA;
	         }
	         else if(EUROPE.toString().equals(zone))
		 {  return EUROPE;
	         }
	         else if(ASIA.toString().equals(zone))
		 {  return ASIA;
	         }
	          else if(ANZ.toString().equals(zone))
		 {  return ANZ;
	         }
	         else if(LATINAM.toString().equals(zone))
		 {  return LATINAM;
	         }
	         else
		 {  assert AFRICA.toString().equals(zone);
	            return AFRICA;
	         }
	      }
	   }

