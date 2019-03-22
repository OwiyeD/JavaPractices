/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author CET
 */
public class ceilingAlarm {

    public static void main(String[] args) {

        int threshold = 10;
        System.out.println("The List of timestamp with temperature "
                + "greater than: "+threshold +" is: " + buildRecord(threshold));

    }

    static List<String> buildRecord(int threshold) {
        List<String> result = null;
        //Set<String> result = new HashSet();
        HashMap<String, Integer> map = new HashMap<>();
        int q = threshold;
        map.put("45900", 0);
        map.put("45910", 7);
        map.put("45920", 12);
        map.put("45930", 18);
        map.put("45940", 8);
        map.put("45950", 17);

        if (map.isEmpty()) {
            System.out.println("The system can't find any record");
        } else {
            result = new ArrayList<>();
           
            for (HashMap.Entry<String, Integer> entry: map.entrySet()) {
                int value = entry.getValue();
                
                if (value > q) {
                    
                    result.add(entry.getKey());
                    
                    

                }
            }
        }

        return result;
    }
}
