/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author CET
 */
public class splitString {

    public static void main(String[] args) {
         splitString split = new splitString();
         System.out.println("What is the split " + split.splitString("cdfr jk", 2));
       

    }
    /*First of all I want to address some issues we are not told by the question like 
    the limit of our N so this program does not put a limit of N.
    This program deals with string that has 1 or no character separately as well as 
    those that are divisible by N and not divisible. Also it should be noted that
    when N is greater than the string length the system returns the string as it is.
    */

    String splitString(String str, int n) {
        List<String> parts = new ArrayList<>();
        /*StringBuilder sb = new StringBuilder();
        sb.append(sb.substring(n, n));*/        
        if (str.length() <= 1) {
            parts.add(str);
        }

        else if (isDivisibleByN(str, n)) {
            // parts= new String();
            int len = str.length();
            for (int i = 0; i < len; i += n) {
                //parts.add(str.substring(i, Math.min(i + n, len)));
                parts.add(str.substring(i, Math.min(len, i+n)));
            }
        }
        
        else if(!isDivisibleByN(str, n)){
            int len = str.length();
            for(int i = 0; i<len; i += n){
           //parts.add(str.substring(i, Math.min(i+n, len)));
           parts.add(str.substring(i, Math.min(len, i+n)));
            }
        }
        return parts.toString();
    }

    //This method checks if our string is divisible by N note that
    //it does not takes in string with counts less than 2 as this is taken
    //care by splitString method and also n has to be a positive integer
    boolean isDivisibleByN(String str, int n) {

        char[] s_array = str.toCharArray();
        int countChars = 0;
        
        //This loop runs through the string counting the number of characters in the string
        for (int i = 1; i <= s_array.length; i++) {
            countChars++;
        }

        int divideBy = n;
        //If the number of characters is divisible by N then it returns true else false
        if(countChars % divideBy == 0){
            return true;
        }
        return false;
    }
}
