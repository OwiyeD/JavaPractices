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
public class CardTable {
   // constructor methods
 
    public CardTable() {
        ct = new PhoneCard[TABLE_LENGTH];
        ctSize = 0;
        current = 0;
    }
 
    // specialized methods
 
    public boolean add(PhoneCard card) {
        if (ctSize == TABLE_LENGTH) return false;
        if (get(card.getNumber()) != null) return false;
        ct[ctSize] = card;
        ctSize++;
        return true;
    }
 
    public PhoneCard get(long no) { // YOU MUST FILL THIS PART
        // OF THE DEFINITION
    }
 
    public PhoneCard first() {
        if (ctSize == 0) {
            return null;
        } else {
            current = 0;
            return ct[current];
        }
    }
 
    public PhoneCard next() {
        if (current + 1 == ctSize) {
            return null;
        } else {
            current++;
            return ct[current];
        }
    }
 
 
    // instance variables/attributes/fields
 
    private PhoneCard[] ct;
    private int ctSize;
    private int current;
 
    // class/static variables/attributes/fields
 
    private static int TABLE_LENGTH = 20;
 
 
}
