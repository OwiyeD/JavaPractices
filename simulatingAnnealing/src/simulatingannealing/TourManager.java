
package simulatingannealing;

import java.util.ArrayList;

/*
Holds the Cities of our tour
*/
public class TourManager {
    //Holds our city
    private static ArrayList destinationCities = new ArrayList<City>();
    // Adds a destination city
    public static void addCity(City city){
        destinationCities.add(city);
    }
    
    //get a city
    public static City getCity(int index){
        return (City)destinationCities.get(index);
    }
    
    //Get the number of destination cities
    public static int numberOfCities(){
        return destinationCities.size();
    }
}
