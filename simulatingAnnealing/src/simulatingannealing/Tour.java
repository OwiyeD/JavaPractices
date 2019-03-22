
package simulatingannealing;

import java.util.ArrayList;
import java.util.Collections;

/*
Tour.java
stores all candidates tour through all cities
*/
public class Tour {
   
    //Holds our tour of cities
    private ArrayList tour = new ArrayList<City>();
    //Cache
    private int distance = 0;
    
    //Construct a blank Tour
    public Tour(){
        for(int i = 0; i < TourManager.numberOfCities(); i++){
            tour.add(null);
        }
    }
    
    //Constructs a tour from another tour
    public Tour(ArrayList tour){
        this.tour = (ArrayList)tour.clone();
    }
    
    //returns tour information
    public ArrayList getTour(){
        return tour;
    }
    
    //Creates a random individual
    public void generateIndividual(){
        
        //Loop through all our cities destination and add them to our tour
        for(int cityIndex = 0; cityIndex<TourManager.numberOfCities(); cityIndex++){
            setCity(cityIndex, TourManager.getCity(cityIndex));
        }
        
        //Random reorder the tour
        Collections.shuffle(tour);
    }
    
    //Gets a city from the tour
    public City getCity(int tourPosition){
        return (City)tour.get(tourPosition);
    }
    
    //Sets a city in a certain position within the tour
    public void setCity(int tourPosition, City city){
        tour.set(tourPosition, city);
        
        //If tours has been altered we need to reset the fitness and distance
        distance = 0;
    }
    
    //Gets the total distance of the tour
    public int getDistance(){
        if(distance == 0){
            int tourDistance = 0;
            //Loop through our tour's cities
            for(int cityIndex=0; cityIndex < tourSize(); cityIndex++){
                
                //Get city we are travelling from
                City fromCity = getCity(cityIndex);
                
                //City we're travelling to
                City destinationCity;
                //check if we are at our last city, if we are set tour
                //tour's final destination city to our starting city
                if(cityIndex+1 < tourSize()){
                    destinationCity = getCity(cityIndex);
                }
                else{
                    destinationCity =getCity(0);
                }
                //Get the distance between the two cities
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        return distance;
    }
    
    //Get number of cities in our tour
    public int tourSize(){
        return tour.size();
    }
    
    @Override
    public String toString(){
        String geneString = "|";
        for(int i = 0; i < tourSize(); i++){
            geneString += getCity(i)+"|";
        }
        return geneString;
    }
    
}
