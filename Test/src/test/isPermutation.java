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
public class isPermutation {
    public static String sort(String s){
        char [] content = s.toCharArray();
        java.util.Arrays.sort(content);
        return new String(content);
    }
    public static boolean isPermutation(String k, String t){
        if(k.length() != t.length()){
            return false;
        }
        return sort(k).equals(sort(t));
    }
    public static void main(String [] args){
        String s1 = "Douglas";
        String s2 = "glasDo";
        isPermutation(s1, s2);
    }
   
}


