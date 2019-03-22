
package ephy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class makingScheme {
    public static void main(String []args){
        HashMap<Integer, String> ma = new HashMap<>();
        ma.put(1, "B");
        ma.put(6, "A");
        ma.put(11, "B");
        ma.put(16, "C");
        ma.put(2, "D");
        ma.put(7, "B");
        ma.put(12, "C");
        ma.put(17, "C");
        ma.put(3, "A");
        ma.put(8, "A");
        ma.put(13, "D");
        ma.put(18, "B");
        ma.put(4, "A");
        ma.put(9, "C");
        ma.put(14, "A");
        ma.put(19, "D");
        ma.put(5, "C");
        ma.put(10, "D");
        ma.put(15, "D");
        ma.put(20, "A");
        System.out.println("Enter your Name");
        Scanner scan = new Scanner(System.in);
        String student_Name = scan.nextLine();
        String answers []= new String [20];
        HashMap<Integer, String> answer = new HashMap<>();
        System.out.println("ENTER YOUR ANSWERS HERE PLEASE:");
   
        for(int i = 0; i <= answers.length-1; ++i){
            int q = i+1;
            System.out.println("Question "+q+" : ");
            answers[i] = scan.nextLine();
            if(answers[i].equalsIgnoreCase("A") || answers[i].equalsIgnoreCase("B")||
                    answers[i].equalsIgnoreCase("C")||answers[i].equalsIgnoreCase("D")){
            answer.put(i+1, answers[i]);
            }
            else {
                System.out.println("You made a mistake in your entrance rerun again the program");
                System.exit(0);                
            }
        }
        
        int marks = 0;
        ArrayList<Integer> correctAnswers = new ArrayList<>();
        ArrayList<Integer> wrongAnswers = new ArrayList<>();
        for(HashMap.Entry<Integer, String> entry: ma.entrySet()){
            for(HashMap.Entry<Integer, String> entry2: answer.entrySet()){
                if((Objects.equals(entry.getKey(), entry2.getKey())) && entry.getValue().equalsIgnoreCase(entry2.getValue())){
                    correctAnswers.add(entry2.getKey());
                    marks = marks + 1;
                }
                else if(Objects.equals(entry.getKey(), entry2.getKey())) {
                    wrongAnswers.add(entry2.getKey());
                }
            }
        }
        
        System.out.println("STUDENT ANSWER SHEET");
        System.out.println("SUDENT NAME : "+student_Name.toUpperCase());
        System.out.println("STUDENT MARKS : " +marks);
        System.out.println("Question Done Correctly: "+correctAnswers.toString());
        System.out.println("Question Missed: "+wrongAnswers.toString());
        if(marks < 15 )System.out.println("FAILED");
        else System.out.println("CONGRATULATIONS FOR PASSING DRIVING LISENCE EXAM");
         for(HashMap.Entry<Integer, String> entry: answer.entrySet()){
            int key = entry.getKey();
            String values = entry.getValue();
            System.out.println("The Question "+key+" = "+values);
        }
        
    }
}
