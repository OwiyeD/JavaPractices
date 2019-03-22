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
public class Test {

    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("enter the number");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        Test test = new Test();
        System.out.println("The test is: " + test.sqrt(num));

    }

    int sqrt(int num) {
        for (int guess = 1; guess * guess <= num; guess++) {
            if (guess * guess == num) {
                return guess;
            }
        }
        return -1;

    }
}
