/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 *
 * @author Christoforos
 */
public class TimeUtilities {
    
    private static final String [] UNIT_NAMES = {"D","H","M","S","MS","US","NS"};
    private static final HashSet UNIT_SET = 
                    new HashSet(Arrays.asList(UNIT_NAMES));
    private static final BigInteger [] MULTIPLIERS = {new BigInteger("86400000000000"), new BigInteger("3600000000000"),
        new BigInteger("60000000000"), new BigInteger("1000000000"), new BigInteger("1000000"), new BigInteger("1000"), 
        BigInteger.ONE};
    private static final int stAfterNumber = 0;
    private static final int stAfterUnit = 1;
    private static final int stNeedNumber = 2;
    private static final int stDoLast = 3;
    private static final int stEnd = 4;
    
    public static String analyze(BigInteger firstNum, BigInteger lastNum, String middle, String lastUnit) {
        
        //Before calling this method firstNum must have been assigned a value.
        //Otherwise we will get a null pointer exception.
        //Futhermore, if middle.equals(""), lastUnit must have a value.
        //We don't check for
        //that since it will never happen under normal circumstances.
        String retval = "";
        String temps = middle;
        
        BigInteger value = firstNum;
        BigInteger total = BigInteger.ZERO;
        int state = stAfterNumber;
        int lastMatch = -1;
        while (state != stEnd) {
            switch(state) {
                case stAfterNumber:
                    if (temps.length() == 0) {
                        state = stDoLast;
                    } else {
                        int i = UNIT_NAMES.length - 1;                        
                        for (; i > 0; i--) {
                            //We scan the array downwards in order to check for "ms"
                            //before we check for "m".
                            if (temps.startsWith(UNIT_NAMES[i])) {
                                value = value.multiply(MULTIPLIERS[i]);
                                total = total.add(value);
                                temps = temps.substring(UNIT_NAMES[i].length());
                                break;
                            }
                        }
                        if (i < 0) {
                            retval = "*Unexpected character: Time unit expected.";
                            state = stEnd;
                        } else if (i <= lastMatch) {    
                            retval = "*Unexpected time unit (" + UNIT_NAMES[i] +"): Time units should appear in descending order.";
                            state = stEnd;
                        } else {
                            lastMatch = i;
                            state = stAfterUnit;
                        }
                    }
                    break;
                case stAfterUnit:
                    if (temps.length() == 0) {
                        if (lastUnit.length() != 0) {
                            //lastNum will also be not null, by the logic of the calling routine
                            retval = "*Unexpected character \".\": Number expected";
                        }   
                        state = stEnd;
                    } else {
                        if (temps.startsWith("_")) {
                            temps = temps.substring(1);
                        }    
                        state = stNeedNumber;
                    }
                    break;
                case stNeedNumber:
                    if (temps.length() == 0) {
                        retval = "*Unexpected character: Number needed.";
                        state = stEnd;
                    } else {
                        int i = 0;
                        char ch = temps.charAt(0);
                        while ((ch >= '0' && ch <= '9') && (i < temps.length())) {
                            i = i + 1;
                            ch = temps.charAt(i);
                        }
                        if (i == 0) {
                            retval = "*Unexpected character. Number expected.";
                            state = stEnd;
                        } else {
                            value = new BigInteger(temps.substring(0,i));
                            temps = temps.substring(i);
                            state = stAfterNumber;
                         }
                    }
                    break;
                case stDoLast:
                    if (lastUnit.length() > 0) {
                        int i = UNIT_NAMES.length - 1;                        
                        for (; i > 0; i--) {
                            //We scan the array downwards in order to check for "ms"
                            //before we check for "m".
                            if (lastUnit.equals(UNIT_NAMES[i])) {                                
                                break;
                            }
                        }
                        if (i < 0) {
                            retval = "*Unexpected character: Time unit expected.";
                        } else if (i <= lastMatch) {    
                            retval = "*Unexpected time unit (" + UNIT_NAMES[i] +"): Time units should appear in descending order.";
                        } else {
                            value = lastNum.multiply(MULTIPLIERS[i]);
                            String sval = value.toString();
                            int digitsOfValue = sval.length();
                            int digitsToCut = lastNum.toString().length();
                            //These decimal digits represent a value less than ns
                            //and hence they are ignored.
                            if (digitsToCut < digitsOfValue) {
                                sval = sval.substring(0,digitsOfValue-digitsToCut);
                                total = total.add(new BigInteger(sval));
                            } //No else. If the "if" condition is false then all digits of lastNum must be ignored
                        }
                    } //No else. If the "if" condition is false then lastNum will be null
                      //by the logic of the calling routine.
                    state = stEnd;
                    break;
            }
        }    
        if (retval.equals("")) {
            if (NumUtilities.isWithinLimitsOfIntType(total, "LINT")) {
                retval = total.toString();
            } else {
                retval = "*Too big time interval.";
            }
        }
        
        return retval;
    
    }
    
    public static boolean isTimeUnit(String s) {
                
        return UNIT_SET.contains(s.toUpperCase(Locale.ENGLISH).trim());        
        
    }
    
    public static void main(String [] args) {
        System.out.println("123".substring(0,0).equals(""));
        System.out.println((new BigInteger("FFFFFFFFFFFFFFFF",16)).divide(MULTIPLIERS[0]).divide(new BigInteger("365")));
        System.out.println((new BigInteger((new Long(Long.MAX_VALUE)).toString())).divide(MULTIPLIERS[0]).divide(new BigInteger("365")));
    }
}
