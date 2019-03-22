/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author CET
 */
public class reverse {
    
    public static void main(String []args){
       String text = "am Douglas ThaProgrammer";
       //This algorith employs both stream and lambda in transforming the string
       //It accepts both uppercase and lowercase characters 
       int[] chars = text.chars()
               .map(ch -> Character.isUpperCase(ch) ? 25 - ch + 'A' *2:
                       Character.isLowerCase(ch)? 25 - ch + 'a'*2 : ch)
               .toArray();
       text = new String(chars, 0, chars.length);
       System.out.println(text);
       
        
    }
    
}
