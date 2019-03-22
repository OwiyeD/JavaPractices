/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author CET
 */
public class buyStock {

    /*List<Integer> buyAndSell(int[] prices) {
        int buyD = 0;
        int sellD = 0;
        List<Integer> pr = new ArrayList<>();

        //Atleast the price should be for 2 days.
        if (prices.length == 1) {
            return pr;
        } else {
            if (!isGoodToBuy(prices)) {
                for (int i = 0; i <= prices.length; i++) {
                    buyD = i;
                    pr.add(prices[i]);
                }
                for (int i = buyD; i <= prices.length - buyD; i++) {
                    if (!isGoodToSell(prices)) {
                        pr.add(prices[i]);
                    }
                }

            }
        }
        return pr;

    }*/
    void buyAndSell(List<Integer> price, int n) {
        List<days> soln = new ArrayList<>();
        days e = new days();
        int i = 0;
        int buyDay = 0;
        int sellDay = 0;
        List<Integer> p = price;
        Integer min = Integer.MAX_VALUE;
        Integer max = Integer.MIN_VALUE;
        /*
        NOTE THAT OUR SEARCH FOR THE LOWEST BUYING DAY HAS TO STOP AT n-1
        SINCE THE LIST HAS TO HAVE ATLEAST TWO DAYS- ONE FOR BUYING AND THE OTHER FOR
        SELLING. ALSO WE ARE KEEPING THE BUY DATE AS 'buy' ELEMENT IN THE LIST
        */
        while (i < n - 1) {
            i++;
        }
        for (Integer j : p) {
            if (min > j) {
                min = j;
            }
            buyDay = 1 + p.indexOf(min);
            e.buy = buyDay;
        }
        
        
        
        //System.out.println(buyDay);
        //for(Integer m: p){
        /*
        NOTE THAT OUR LIST NOW BEGINS JUST AT THE DAY OF BUYING MOVING FORWARD
        HENCE WE ARE STARTING AT buyDay TO THE LAST ELEMENT OF THE LIST
        IN DOING SO WE ARE CONCERNED ABOUT FINDING A LOCAL MAXIMA AMONG THE 
        REMAINING SUBLIST. SELL DATE IS STORED AS A 'sell' ELEMENT IN THE FINAL
        LIST
        */
            for(int k = buyDay-1; k <= n-buyDay; k++){
            
                    if (max < p.get(k)) {
                        max = p.get(k);
                    }
                    sellDay = 1 + p.indexOf(max);
                    e.sell = sellDay;
            }
                
            
       // }

        soln.add(e);

        /*THIS IS OUR OUTPUT WHERE WE GET OUR OUTPUT THAT IS IN SYNC WITH
        THE CONSTRUCTIVE CLASS CALLED days.
        */
        for (days d : soln) {
            System.out.println("Buy on day: " + d.buy 
                    +" //Buy at: " + min
                    + "\nSell on day : " + d.sell
            + " //Sell at: "+max);
        }

    }

    /*void dayToBuyAndSell(int[] prices, int n) {
        ArrayList<Interval> soln = new ArrayList<>();
        int i = 0;
        int count = 0;
        while (i <= n - 1) {
            while (i <= n - 1 && (prices[i + 1] <= prices[i])) {
                i++;
            }
            if (i == n - 1) {
                break;
            }
            Interval e = new Interval();
            e.buy = i++;

            while (i < n && (prices[i] >= prices[i - 1])) {
                i++;
            }

            e.sell = i - 1;
            soln.add(e);
             count++;
        }
            
    }*/
    public static void main(String[] args) {
        Integer[] p = {30, 20, 10, 15, 17, 25, 20, 23};
        Arrays.asList(p);
        buyStock stock = new buyStock();
        int n = p.length;

        stock.buyAndSell(Arrays.asList(p), n);

    }
}

class days {

    int buy, sell;
}
