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
public class transformChar {
    public static void main(String []args){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the string to be reveresed");
        String input = scan.nextLine();
        //String input = "abcd";
        transformChar t = new transformChar();
        
     System.out.println("the output of " +input+ " is " + t.setChar(input));
        }
  /*  static StringBuilder setChar(String str, int val1){
        StringBuilder sb = new StringBuilder();
        String alphabet = "zyxwvutsrqponmlkjihjfedcba";
        char[] ch = alphabet.toCharArray();
        
       // int val2 = getNumericValu(val1);
        for(int i = 0; i<=str.length()-1; i++){
            for(char c: ch){
                int alphaNum = alphabet.indexOf(c);
        if(val1==alphaNum){
            
            sb.setCharAt(i, c);
        }
            }
        }
        
        
        
     return sb;   
    }*/
    StringBuilder setChar(String str){
        StringBuilder sb = new StringBuilder();
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        char[] ch = alpha.toCharArray();
        char[] str1 = str.toCharArray();
        int h = 0;
        for(char k : str1){
           h = getNumericValu(k);
           for(char y : ch){
               if(h == 25-getNumericValu(y)){
                   sb.append(y);
           }
            
        }
    }
    return sb;
    }
       
   int getNumericValu(int c){
        int a = Character.getNumericValue('a');
        int z = Character.getNumericValue('z');
        int val = Character.getNumericValue(c);
        if(a<= val && val<=z){
            return val-a;
        }
        return -1;
    }
    }
    

