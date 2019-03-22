package ephy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class redFiles {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("TYPE THE FILE NAME(MAKE SURE YOU SAVED IT IN URI src/ephy/)\n "
                + "IF NOT SO BE SURE"
                + "THE PROGRAM WON'T DO ANYTHING");
        String name_Of_The_File = scan.nextLine();
        File file_Name = new File(name_Of_The_File);
        Scanner source_File = new Scanner(new File("src/ephy/" + file_Name));
        ArrayList<String> result = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        while (source_File.hasNext()) {
            String line = source_File.next();
            result.add(line);
            sb = new StringBuffer();
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
        }
        System.out.println("\n THE LIST IS: " + result);
        Collections.reverse(result);
        System.out.println("\n REVERSED LIST: " + result);
        ArrayList<String> plurals = new ArrayList<>();
        ArrayList<String> non_plurals = new ArrayList<>();        
        for (String str : result) {
            if (Character.toUpperCase(str.charAt(str.length() - 1)) == 'S') {
                plurals.add(str);
            }
            else non_plurals.add(str);
        }
        if (plurals.isEmpty()) {
            System.out.println("\n No plural Found");
        } else {
            System.out.println("\n PLURALS: " + plurals);
        }
        if(non_plurals.isEmpty())System.out.println("\n All the words are plural or the file is empty");
        else System.out.println("\n NON_PLURAL LIST: "+non_plurals);     
    }
}
