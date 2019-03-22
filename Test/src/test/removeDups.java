/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashSet;
import java.util.ArrayList;

/**
 *
 * @author CET
 */
public class removeDups {
    void removeDup(LinkedListNode n){
        HashSet<Integer> set = new HashSet<Integer>();
        LinkedListNode previous = null;
        while(n != null){
            if(set.contains(n.data)){
                previous.next =n.next;
                
            }else{
                set.add(n.data);
                previous = n;
            }
            n = n.next;
        }
        
        
        
    }
    
}
/*No buffer*/
class noBuffer{
    void deleteDups(LinkedListNode head){
        LinkedListNode current = head;
        while(current != null){
            LinkedListNode runner = current;
            while(runner !=null){
            if(runner.next.data == current){
                runner.next = runner.next.next;
            }
            else{
                runner = runner.next;
            }
        }
        }
        current =current.next;
    }
}
