/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package _simulatingannealong;

/**
 *
 * @author CET
 */
public class City {
    int x;
    int y;
    
    //Construct a randomly placed city
    public City(){
        this.x = (int)(Math.random()*200);
        this.y = (int)(Math.random()*200);
    }
    
    //Construct a city at chosen coordinate
    public City(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    //Get x coordinate
    public int getX(){
        return this.x;
    }
    //Get y coordinate
    public int getY(){
        return this.y;
    }
      //Get distance to a given city
    public double distanceTo(City city){
        int xDistance = Math.abs(getX()-city.getX());
        int yDistance = Math.abs(getY()- city.getY());
        
        double distance = Math.sqrt((xDistance*xDistance)-(yDistance*yDistance));
        return distance;
    }
    
    @Override
    public String toString(){
        return getX()+", " +getY();
    }
}
