/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _simulatingannealong;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author CET
 */
public class Tour {

    private ArrayList tour = new ArrayList<City>();
    private int distance = 0;

//Create a blank tour
    public Tour() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }

    }

//Create Tout from another tour
    public Tour(ArrayList tour) {
        this.tour = (ArrayList) tour.clone();
    }

//Return tour information
    public ArrayList getTour() {
        return tour;
    }

//Create a random individual
    public void generateIndividual() {
        //Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++) {
            setCity(cityIndex, TourManager.getCity(cityIndex));
        }

        //Random reorder the tour
        Collections.shuffle(tour);
    }

//Get a City from the tour
    public City getCity(int tourPosition) {
        return (City) tour.get(tourPosition);
    }

//Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
        //If the tours has been altered we need to reset the fitness and distance
        distance = 0;
    }

//Gets the total distance of the tour
    public int getDistance() {
        if (distance == 0) {
            int tourDistance = 0;
            //Loop through our tour cities
            for (int cityIndex = 0; cityIndex < tourSize(); cityIndex++) {
                //Get City we are travelling from
                City fromCity = getCity(cityIndex);

                //City we are travelling to
                City destinationCity;

                //Check that we are not in our last city, if so set our destination city to our starting city
                if (cityIndex + 1 < tourSize()) {
                    destinationCity = getCity(cityIndex + 1);
                } else {
                    destinationCity = getCity(0);
                }

                //Get the distance between the two cities
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        return distance;

    }

//Get number of cities in our tour
    public int tourSize() {
        return tour.size();
    }

    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getCity(i) + "|";
        }
        return geneString;
    }
}
