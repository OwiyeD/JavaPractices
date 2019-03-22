/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author CET
 */
public class sortingStr {
    void usingForLoop(char[] charArray){
        for(int i = 0; i<=charArray.length; i++){
            for(int j = i+1; j<= charArray.length; j++){
                if(Character.toLowerCase(charArray[j])<Character.toLowerCase(charArray[i]))
                {
                    swapChars(i,j,charArray);
                }                    
            }
        }
    }
    private static void swapChars(int i, int j, char[] charArray){
        char temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;
    }
    
    
    //Let us now sort using sort function
    //the problem with this it does not sort upper and lowcases
    void usingSortFunc(char[] charArray){
        Arrays.sort(charArray);
    }
    
    void usingComparator(String str){
        Character[] charArray = new Character[str.length()];
        for(int i = 0; i<=charArray.length; i++){
            charArray[i] = str.charAt(i);
        }
        Arrays.sort(charArray, Comparator.comparing(Character::toLowerCase));
        
        
        StringBuilder sb = new StringBuilder(charArray.length);
        for(Character c: charArray){
            sb.append(c.charValue());
        }
    }
    
    //Now I sort using stream
    void sortingUsingStream(String str){
        String s = Stream.of(str.split(""))
                .sorted(Comparator.comparingInt(o -> Character.toLowerCase(o.charAt(0))))
                .collect(Collectors.joining());
        
        
    }
}
