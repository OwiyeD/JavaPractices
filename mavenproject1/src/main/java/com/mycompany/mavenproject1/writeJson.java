/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author CET
 */
public class writeJson {
    public static void writeJsonDemo(String filename)throws Exception{
        JSONObject sampleObj = new JSONObject();
        sampleObj.put("name", "Douglas");
        sampleObj.put("age", 30);
        
        JSONArray messages = new JSONArray();
        messages.add("Hey");
        messages.add("What's up?!");
        sampleObj.put("messages", messages);
        Files.write(Paths.get(filename), sampleObj.toJSONString().getBytes());
        
        
    }
    
}
