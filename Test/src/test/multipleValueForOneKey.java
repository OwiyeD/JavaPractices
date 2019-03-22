/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CET
 */
public class multipleValueForOneKey {
    public static void main(String[] args){
        Map<String, List<String>> map = new HashMap<>();
        List<String> names= new ArrayList<>();
        names.add(("Owiye Atsewa"));
        names.add("Lotengan Esinyeni");
        names.add("Njeri Faith");
        
        List<String> address = new ArrayList<>();
        address.add("51, Butere");
        address.add("634, Turkana");
        address.add("45, Kakamega");
        
        map.put("A", names);
        map.put("B", address);
        for(Map.Entry<String, List<String>> entry: map.entrySet()){
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("The key = "+key);
            System.out.println("Values = "+values);
        }
    }
    
}
