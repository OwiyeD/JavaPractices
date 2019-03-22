/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Scanner;
import java.util.Stack;


public class stackWithMin extends Stack<NodeWithMin>{
  public void push(int value){
      int newMin = Math.min(value, min());
      super.push(new NodeWithMin(value, newMin));
  }
public int min(){
    if(this.isEmpty()){
        return Integer.MAX_VALUE;
    }
    else{
        return peek().min;
    }
    
} 
public static void main(String [] args){
    System.out.println("Enter the number to be pushed into the stack");
    Scanner scan = new Scanner(System.in);
    int val = scan.nextInt();
    stackWithMin p = new stackWithMin();
    p.push(val);
    
}
}
class NodeWithMin{
    public int value;
    public int min;
    public NodeWithMin(int v, int min){
        this.value = v;
        this.min = min;
        
    }
    
}
