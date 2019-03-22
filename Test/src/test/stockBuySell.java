/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;

/**
 *
 * @author CET
 */
public class stockBuySell {
    void stockBuySell(int[] price, int n){
        
        //prices must be given atleast for 2 days
        if(n ==1) return;
        
            int count = 0;
            ArrayList<Interval> sol = new ArrayList<Interval>();
            
            //Traverse through given array
            int i = 0;
            while(i<n-1)
            {
                //Find Local Minima. Note that the limit has to be (n-2) as we are
                //comparing present element to the next element.
                while((i < n-1) && (price[i + 1] <= price[i]))i++;
                
                
                //If we reached the end, break as no further solution possible
                if(i == n-1) break;
                
                Interval e = new Interval();
                e.buy = i++;
                //Store the index of minima
                
                //Find Local Maxima. Note that the limitis (n-1) as we are
                //comparing to the previous element
                while ((i < n) && (price[i] >= price[i - 1]))i++;
                
                //store the index of maxima
                e.sell = i-1;
                sol.add(e);
                
                //Increment number of buy/sell
                count++;
            }
            
            // print solution
            if(count == 0){
                System.out.println("There is no day when buying the stock"
                        + "will make profit");
            }
            else
            {
                for (int j = 0; j < count; j++){
                    System.out.println("Buy on day: " + sol.get(j).buy
                            + " Sell on day : " + sol.get(j).sell);
                }
            }
            return;
            
        
    }
    public static void main(String[] args){
        stockBuySell stock = new stockBuySell();
        
        //Stock prices on consecutive days
        int price[] ={30, 20, 10, 15, 17, 25, 20, 23};
        int n = price.length;
        
        // functional call
        stock.stockBuySell(price, n);
    }
    
}
class Interval
{
    int buy, sell;
}

