/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author CET
 */
public class wordTransformer {
    LinkedList<String> transform(String start, String stop, String[] words){
        HashMapList<String, String> wildCardToWordList = createWildCardToWordMap();
        HashSet<String> visited = new HashSet<>();
        return transform(visited, start, stop, wildCardToWordList);
                
    }
    
    /*Depth-First search starting from startword to stopword
    searching through every one edit away words
    */
    LinkedList<String> transform(HashSet<String> visited, String start, String stop, HashMapList<String, String> wildCardToWordList){
        
        if(start.equals(stop)){
            LinkedList<String> path = new LinkedList<>();
            path.add(start);
            return path;
        }else if(visited.contains(start)){
          return null;  
            
        }
        visited.add(start);
        ArrayList<String> words = getValidLinkedWords(start, wildCardToWordList);
        for(String word : words){
            LinkedList<String> path = transform(visited, word, stop, wildCardToWordList);
            if(path != null){
                path.addFirst(start);
                return path;
            }
        }
        return null;
    }
    /*Insert words in dictionary into mapping from wildcard form -> word*/
    HashMapList<String, String> createWildcardToWordMap(String[] words){
        
    }
            
    
}
