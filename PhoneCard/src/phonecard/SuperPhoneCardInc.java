/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonecard;

/**
 *
 * @author CET
 */
import java.util.Scanner;
 
public class SuperPhoneCardInc {
    public static void main(String[] args) {
        CardTable ct = new CardTable();
        Scanner in = new Scanner(System.in);
        String line = null;
        boolean done = false;
        if (! in .hasNextLine()) {
            done = true;
        } else {
            line = in .nextLine();
        }
        if (!done && line.length() >= 4 && line.substring(0, 4).equals("quit")) {
            done = true;
        }
        while (!done) {
            System.out.println("Input: " + line);
            Scanner inl = new Scanner(line);
            String command = "";
            if (inl.hasNext()) {
                command = inl.next();
            }
            if (command.equals("add")) {
                boolean invalidArgs = false;
                long no = 0;
                int passwd = 0;
                String cardType = null;
                if (inl.hasNextLong()) {
                    no = inl.nextLong();
                } else {
                    invalidArgs = true;
                }
                if (!invalidArgs && inl.hasNextInt()) {
                    passwd = inl.nextInt();
                } else {
                    invalidArgs = true;
                }
                if (!invalidArgs && inl.hasNext()) {
                    cardType = inl.next();
                } else {
                    invalidArgs = true;
                }
                if (!invalidArgs && (no <= 0 || passwd <= 0)) {
                    invalidArgs = true;
                }
                PhoneCard card = null;
                if (!invalidArgs && cardType.equals("SuperNA10")) {
                    card = new SuperNA10Card(no, passwd);
                } else if (!invalidArgs && cardType.equals("Global10")) {
                    card = new Global10Card(no, passwd);
                } else if (!invalidArgs && cardType.equals("Global25")) {
                    card = new Global25Card(no, passwd);
                } else {
                    invalidArgs = true;
                }
                if (invalidArgs) {
                    System.out.println("Error: invalid arguments for add command");
                } else if (ct.get(no) != null) {
                    System.out.println("Error: card no " + no + " already exists");
                } else if (!ct.add(card)) {
                    System.out.println("Error: card table full");
                } else {
                    System.out.println("Result: added card " + no);
                }
            } else if (command.equals("getBalance")) {
                boolean invalidArgs = false;
                long no = 0;
                int passwd = 0;
                if (inl.hasNextLong()) {
                    no = inl.nextLong();
                } else {
                    invalidArgs = true;
                }
                if (!invalidArgs && inl.hasNextInt()) {
                    passwd = inl.nextInt();
                } else {
                    invalidArgs = true;
                }
                if (!invalidArgs && (no <= 0 || passwd <= 0)) {
                    invalidArgs = true;
                }
                if (invalidArgs) {
                    System.out.println("Error: invalid arguments for getBalance command");
                } else {
                    PhoneCard card = ct.get(no);
                    if (card == null) {
                        System.out.println("Error: card no " + no + " does not exist");
                    } else if (card.getPassword() != passwd) {
                        System.out.println("Error: password " + passwd + " incorrect");
                    } else {
                        System.out.printf("Result: card %d balance is %.2f%n",
                            no, card.getBalance());
                    }
                }
            } else if (command.equals("getLimit")) { // YOU MUST FILL THIS PART
                // OF THE DEFINITION
            } else if (command.equals("charge")) { // YOU MUST FILL THIS PART
                // OF THE DEFINITION
            } else if (command.equals("deductWeeklyFee")) {
                PhoneCard card = ct.first();
                while (card != null) {
                    card.deductWeeklyFee();
                    System.out.printf("Result: card %d charged weekly fee%n",
                        card.getNumber());
                    card = ct.next();
                }
                System.out.println("Result: weekly fees deducted");
            } else if (command.equals("printAll")) {
                PhoneCard card = ct.first();
                while (card != null) {
                    System.out.printf("Result: %s%n", card);
                    card = ct.next();
                }
                System.out.println("Result: all cards printed");
            } else {
                System.out.println("Error: command invalid");
            }
            if (! in .hasNextLine()) {
                done = true;
            } else {
                line = in .nextLine();
            }
            if (!done && line.length() >= 4 && line.substring(0, 4).equals("quit")) {
                done = true;
            }
        }
    }
}
