/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author CET
 */
public class compressLength {
    String compress(String str){
        int finalLength = countCompression(str);
        if(finalLength >= str.length())return str;
        StringBuilder sb = new StringBuilder(finalLength);
        int countConsecutive = 0;
        for(int i = 0; i < str.length(); i++){
            countConsecutive++;
            if(i+1 >= str.length() || str.charAt(i) != str.charAt(i+1)){
                sb.append(str.charAt(i));
                sb.append(countConsecutive);
                countConsecutive = 0;
            }
        }
        return sb.toString();
    }
     int countCompression(String str){
         int compressedLength = 0;
         int countConsecutive = 0;
         for(int i = 0; i < str.length(); i++){
             countConsecutive++;
             if(i+1 >= str.length() || str.charAt(i) != str.charAt(i+1)){
                 compressedLength += String.valueOf(countConsecutive).length();
                 countConsecutive = 0;
             }
         }
         return compressedLength;
     }
      public static void main(String[] args){
          compressLength cl = new compressLength();
          String str = "aaabbccdd";
          String s = Stream.of(str.split(""))
                  .sorted(Comparator.comparingInt(o -> Character.toLowerCase(o.charAt(0))))
                  .collect(Collectors.joining());
          System.out.println(cl.compress(s));
      }
    
}
