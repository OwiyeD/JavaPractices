/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _simulatingannealong;

import java.util.ArrayList;

/**
 *
 * @author CET
 */
public class TourManager {
    private static ArrayList destinationCities= new ArrayList<City>();
    
    //add Destination Cities
    public static void addCity(City city){
       destinationCities.add(city);
    }
    
    //Get a cioty
    public static City getCity(int index){
        return (City)destinationCities.get(index);
    }
    
    public static int numberOfCities(){
        return destinationCities.size();
    }
    
    
}
