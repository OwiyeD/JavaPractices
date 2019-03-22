package _simulatingannealong;

public class _simulatingAnnealong {
//Calculate acceptance probability
 public static double acceptanceProbability(int energy, int newEnergy, double temperature) {

        //If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        
        //If the new solution is worse calculate acceptance probability
        return Math.exp((energy - newEnergy)/temperature);
    }
    public static void main(String[] args) {
        
    //Create and add our Cities
    City city = new City(60, 200);
    TourManager.addCity(city);
    City city1 = new City(80, 140);
    TourManager.addCity(city1);
    City city2 = new City(60, 180);
    TourManager.addCity(city2);
    City city3 = new City(100, 120);
    TourManager.addCity(city3);
    City city4 = new City(140, 80);
    TourManager.addCity(city4);
    City city5 = new City(100, 100);
    TourManager.addCity(city5);
    City city6 = new City(40, 80);
    TourManager.addCity(city6);
    City city7 = new City(160, 180);
    TourManager.addCity(city7);
    City city8 = new City(180, 100);
    TourManager.addCity(city8);
    City city9 = new City(40, 120);
    TourManager.addCity(city9);
    
    //Sett the initial temperature
    double temp = 10000;
    
    //Cooling rate
    double coolingRate = 0.003;
    
    //Initialize current solution
    Tour currentSolution = new Tour();
    currentSolution.generateIndividual();
    
    System.out.println("Initial Solution distance" + currentSolution.getDistance());
    
    //Set as current best
    Tour best = new Tour(currentSolution.getTour());
    
    //Loop until system has cooled
    while(temp > 1){
        //Create new neighbor tour
        Tour newSolution = new Tour(currentSolution.getTour());
        
        //get a random position in the tour
        int tourPos1 = (int)(newSolution.tourSize()*Math.random());
        int tourPos2 = (int)(newSolution.tourSize()*Math.random());
   
        //Get cities at the selected positions
        City citySwap1 = newSolution.getCity(tourPos1);
        City citySwap2 = newSolution.getCity(tourPos2);
        
        //Swap them
        newSolution.setCity(tourPos2, citySwap1);
        newSolution.setCity(tourPos1, citySwap2);
        
        //Get energy of solution
        int currentEnergy = currentSolution.getDistance();
        int newEnergy = newSolution.getDistance();
        
        //Decide if we should accept the neighbor
        if(acceptanceProbability(currentEnergy, newEnergy, temp)>Math.random()){
            currentSolution = new Tour(newSolution.getTour());
        }
        
        //Keep track of the best solution
        if(currentSolution.getDistance()< best.getDistance()){
            best = new Tour(currentSolution.getTour());
        }
        
        //Cool the system
        temp *=1-coolingRate;
   
    }
    System.out.println("Final Solution: "+ best.getDistance());
    System.out.println("Tour: "+ best);

}

}
