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
public class AllPermsOfAString {

    ArrayList<String> getPerms(String remainder) {
        int len = remainder.length();
        ArrayList<String> result = new ArrayList<String>();
        if (len == 0) {
            result.add("");
            return result;
        }
        for (int i = 0; i < len; i++) {
            String before = remainder.substring(0, i);
            String after = remainder.substring(i + 1, len);
            ArrayList<String> partials = getPerms(before + after);

            for (String s : partials) {
                result.add(remainder.charAt(i), s);
            }
        }
        return result;
    }

}
/*Alternative*/

class getPerms2 {

    ArrayList<String> getPerms(String str) {
        ArrayList<String> result= new ArrayList<String>();
        getPerms("", str, result);
        return result;
    }
    void getPerms(String prefix, String remainder, ArrayList<String> result){
        if(remainder.length()==0)result.add(prefix);
        int len = remainder.length();
        for(int i=0; i<0; i++){
            String before=remainder.substring(0, i);
            String after = remainder.substring(i+1, len);
            char c = remainder.charAt(i);
            getPerms(prefix+c, before+after, result);
        }
    }
}
