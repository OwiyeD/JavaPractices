package ephy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
public class insertAsterik {
    public static void main(String[] args) {
        System.out.println("Type the sentence to be transformed");
        Scanner scan = new Scanner(System.in);
        String sentence = scan.nextLine();
        insertAsterik i = new insertAsterik();
        ArrayList<String> text = i.convertStringToList(sentence);
        //int k = i.convertStringToList(sentence);
        ArrayList<String> new_sentence;
        new_sentence = i.insertAsterisks(text);
        System.out.println("Sentence: " + new_sentence);
    }
    ArrayList<String> convertStringToList(String s) {
        String[] s1 = s.split(" ");
        ArrayList<String> text;
        text = new ArrayList<>();
        text.addAll(Arrays.asList(s1));
        return text;
    }
    ArrayList<String> insertAsterisks(ArrayList<String> text) {
        ArrayList<String> result = new ArrayList<>(100);
        int index = 0;

        for (Iterator<String> it = text.iterator(); it.hasNext();) {
            String str = it.next();
            if (str.length() == 4) {
                index = text.indexOf(str);
                result.add(index, "**** " + str);

            } else {
                index = text.indexOf(str);
                result.add(index, str);
            }
        }
        return result;
    }
}
