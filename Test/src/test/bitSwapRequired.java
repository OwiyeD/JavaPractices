/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Scanner;

/**
 *
 * @author CET
 */
public class bitSwapRequired {
    public static void main (String [] args){
        bitSwapRequired e = new bitSwapRequired();
        Scanner scan = new Scanner (System.in);
        System.out.println("Enter the two numbers");
        int a = scan.nextInt();
        int b = scan.nextInt();
        System.out.println("you've entered " +a+ " and " +b);
        System.out.println("The number of bits to be swapped are " +e.bitSwapRequired(a, b));
    }
    int bitSwapRequired (int a, int b){
        int count = 0;
        for (int c = a^b; c != 0; c = c & (c - 1)){
            count++;
        }
        return count;
    }
}
