/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.math.BigInteger;

/**
 *
 * @author Christoforos
 */
public class NumUtilities {

    private static final String [] INT_TYPES = {"SINT", "INT", "DINT", "LINT", "USINT", "UINT", "UDINT", "ULINT"};
    private static final String [] BITSTR_TYPES = {"BYTE", "WORD", "DWORD", "LWORD"};
    private static final BigInteger [] MIN_VALUES = {new BigInteger("-128"), new BigInteger("-32768"), 
        new BigInteger("-2147483648"), new BigInteger("-9223372036854775808"), BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};
    private static final BigInteger [] MAX_VALUES = {new BigInteger("127"), new BigInteger("32767"), new BigInteger("2147483647"), new BigInteger("9223372036854775807"),
        new BigInteger("FF",16), new BigInteger("FFFF",16), new BigInteger("FFFFFFFF",16), new BigInteger("FFFFFFFFFFFFFFFF", 16)};
    
    public static String checkIntLiteral(String source, String type) {
        //This method assumes that type contains the name of an IEC integer type 
        //in capital letters, without any leading or trailing spaces.
        
        String retval = null;
        
        BigInteger x = new BigInteger(source);
        
        for (int i = 0; i < INT_TYPES.length; i++) {
            if (type.equals(INT_TYPES[i])) {
                if ((x.compareTo(MIN_VALUES[i]) >= 0) && (x.compareTo(MAX_VALUES[i]) <= 0)) {
                    //The value is within the limits of the data type
                    retval = source;
                }
                break;
            }
        }
        
        return retval;
    }
    
    public static String makeIntLiteral(String source, String type, int base) {
        //This method assumes that type contains the name of an IEC integer type 
        //in capital letters, without any leading or trailing spaces.
        
        String retval = null;
        
        BigInteger x = new BigInteger(source, base);
        
        for (int i = 0; i < INT_TYPES.length; i++) {
            if (type.equals(INT_TYPES[i])) {
                if ((x.compareTo(MIN_VALUES[i]) >= 0) && (x.compareTo(MAX_VALUES[i]) <= 0)) {
                    //The value is within the limits of the data type
                    retval = x.toString();                                        
                }
                break;
            }
        }
        
        return retval;
    }
    
    public static String checkRealLiteral(String source, String type) {
        
        String retval = source;
        
        if (type.equals("REAL")) {
            float f = Float.parseFloat(source);
            if (Float.isInfinite(f)) {
                retval = null;
            } 
        } else {
            double d = Double.parseDouble(source);
            if (Double.isInfinite(d)) {
                retval = null;
            }
        }
        
        return retval;
    }
    
    public static String makeJavaBitStringLiteral(String source, String type) {
        
        String retval = null;
        
        for (int i = 0; i < BITSTR_TYPES.length; i++) {
            if (type.equals(BITSTR_TYPES[i])) {
                retval = checkIntLiteral(source, INT_TYPES[4+i]);
                break;
            }
        }
        return retval;
    }
    
    public static String makeJavaBitStringLiteral(String source, String type, int base) {
        
        String retval = null;
        
        for (int i = 0; i < BITSTR_TYPES.length; i++) {
            if (type.equals(BITSTR_TYPES[i])) {
                retval = makeIntLiteral(source, INT_TYPES[4+i],base);
                break;
            }
        }
        return retval;
    }
    
    public static String findSmallestBitStringType(BigInteger x) {
        
        String retval = null;
        for (int i = 4; i < MAX_VALUES.length; i++) {
            if ((x.compareTo(MAX_VALUES[i]) <= 0)) {
                //The value is within the limits of the data type
                retval = BITSTR_TYPES[i-4];
                break;
            }        
        }
        return retval;
        
    }
    
    public static String findSmallestIntegerType(String source) {
    
        String retval = findSmallestSignedIntegerType(source);
        if ((retval == null) && !source.startsWith(("-"))) {
            BigInteger x = new BigInteger(source);
            if (x.compareTo(MAX_VALUES[7]) <= 0) {
                retval = "ULINT";
            }
        }
        
        return retval;
    }
    
    public static String findSmallestSignedIntegerType(String source) {
        
        String retval = null;
        
        BigInteger x = new BigInteger(source);
        for (int i = 0; i < 4; i++) {
            if ((x.compareTo(MIN_VALUES[i]) >= 0) && (x.compareTo(MAX_VALUES[i]) <= 0)) {
                //The value is within the limits of the data type
                retval = INT_TYPES[i];
                break;
            }        
        }
        
        return retval;
        
    }
    
    public static String findSmallestPositiveType(String source) {
        
        String retval;
        
        if (source.equals("0") || source.equals("1")) {
            retval = "BOOL";
        } else {
            retval = findSmallestBitStringType(new BigInteger(source));        
        }
        return retval;
        
    }
    
    public static String findSmallestRealType(String source) {
    
        String retval = null;
        
        float f = Float.parseFloat(source);
        if (!Float.isInfinite(f)) {
            retval = "REAL";
        } else {    
            Double d = Double.parseDouble(source);
            if (!Double.isInfinite(d)) {
                retval = "LREAL";
            }
        }
        
        return retval;
    }
    
    public static boolean isWithinLimitsOfIntType(BigInteger x, String type) {
        
        //The calling routine must make sure that type is the name of an integer type
        //of IEC61131-3, in capital letters and without leading or trailing whitespace
        //characters. Otherwise, the method will return false.
        
        boolean retval = false;
        
        for (int i = 0; i < INT_TYPES.length; i++) {
            if (type.equals(INT_TYPES[i])) {
                if ((x.compareTo(MIN_VALUES[i]) >= 0) && (x.compareTo(MAX_VALUES[i]) <= 0)) {
                    //The value is within the limits of the data type
                    retval = true;                                        
                }
                break;
            }
        }
        
        return retval;
    }
    
    public static String changeSign(String source) {
       
        String retval;
        
        if (source.startsWith("-")) {
            retval = source.substring(1);
        } else {
            retval = "-" + source;
        }        
        return retval;
    }
    
    public static double raiseToLongIntPower(double x, BigInteger n) {
        
        double retval = 1.0;
        
        int exponentsign = n.compareTo(BigInteger.ZERO);
        
        switch (exponentsign) {
            case -1:
                retval = 1 / raiseToLongIntPower(x, n.negate());
                break;            
            case 1:
                int nbits = n.bitLength() - 1;
                for (int i = nbits; i >=0; i--) {
                    retval *= retval;
                    if (n.testBit(i)) {
                        retval *= x;
                    }
                }
                break;
        }
        
        return retval;
    }
    
    public static boolean compare(double x1, double x2, Token op) {
    
        boolean retval = false;
        
        switch(op) {
            case EQUALS:
                retval = (x1 == x2);
                break;
            case NE:
                retval = (x1 != x2);
                break;
            case LT:
                retval = (x1 < x2);
                break;
            case GT:
                retval = (x1 > x2);
                break;
            case LE:
                retval = (x1 <= x2);
                break;
            case GE:
                retval = (x1 >= x2);
                break;
        }
        
        return retval;
    }
    
    public static boolean compare(BigInteger x1, BigInteger x2, Token op) {
    
        boolean retval = false;
        int res = x1.compareTo(x2);
        
        switch(op) {
            case EQUALS:
                retval = (res == 0);
                break;
            case NE:
                retval = (res != 0);
                break;
            case LT:
                retval = (res < 0);
                break;
            case GT:
                retval = (res > 0);
                break;
            case LE:
                retval = (res <= 0);
                break;
            case GE:
                retval = (res >= 0);
                break;
        }
        
        return retval;
    }
    
    public static String logicalNot(String value, String type) {
    
        String retval = null;
        
        if (type.equals("BOOL")) {
            retval = value.equals("0")?"1":"0";
        } else {
            BigInteger x = new BigInteger(value);
            for (int i = 0; i < BITSTR_TYPES.length; i++) {
                if (type.equals(BITSTR_TYPES[i])) {
                    retval = x.xor(MAX_VALUES[4+i]).toString();
                }
            }
        }
        return retval;
    }
    
    public static int getMaxIndex(String dataType, int size) {
        
        int retval = -1;
        int divider = 8;
        for (int i = 1; i < size; i++) {
            divider = 2 * divider;
        }
        
        for (int i = 0; i < BITSTR_TYPES.length; i++) {
            if (dataType.equals(BITSTR_TYPES[i])) {
                int bitSize = MAX_VALUES[4+i].bitCount();
                if (size == 0) {
                    retval = bitSize - 1;
                } else {
                    retval = bitSize / divider - 1;
                }
                break;
            }
        }
        
        return retval;
    }
    
    public static String partialAccess(String value, int sizeIndex, int count) {
    
        BigInteger result = new BigInteger(value);
        
        if (sizeIndex == 0) {//bit
            result = result.and(BigInteger.ONE.shiftLeft(count)).shiftRight(count);
        } else {
            int offset = 8;
            for (int i = 1; i < sizeIndex; i++) {
                offset = 2 * offset;
            }
            offset = offset * count;
            BigInteger mask = MAX_VALUES[3+sizeIndex];
            result = result.and(mask.shiftLeft(offset)).shiftRight(offset);
        }
        return result.toString();
    
    }
    
    public static int byteSize(String javaType) {
        
        int retval = 1;
        
        switch(javaType) {
            case "short": 
                retval = 2;
                break;
            case "int":
            case "float":
                retval = 4;
                break;
            case "long":
            case "double":
                retval = 8;
                break;
        }
        return retval;
    
    }
    
    public static void main(String [] args) {
        
        for (int i = 0; i < INT_TYPES.length; i++) {
            System.out.println(INT_TYPES[i] + ": From " + MIN_VALUES[i] + " to " + MAX_VALUES[i] + ".");
            //System.out.println(INT_TYPES[i] + ": From " + MIN_VALUES[i].toString(16) + " to " + MAX_VALUES[i].toString(16) + ".");
        }
        BigInteger x = new BigInteger("6571",8);
        System.out.println("Hi" + x.toString());
        x = new BigInteger((new Long(Long.MAX_VALUE)).toString());
        System.out.println(x.toString(16));
        System.out.println(x.bitLength());
        System.out.println(raiseToLongIntPower(-2, new BigInteger("-5")));
        System.out.println(1.0/32);
        System.out.println(Double.parseDouble("12")/0);
        //System.out.println(-+-4);
        
        //x = new BigInteger("-12");
        //System.out.println(x.toString());
        
        //x=new BigInteger("001",2);
        //System.out.println(x.not().toString(2));
        
        //System.out.println(Float.parseFloat("1.23e100000"));
        //System.out.println(Byte.parseByte("147"));
        /*x = new BigInteger("147");
        System.out.println(x.toString(16));
        System.out.println(x.xor(MAX_VALUES[4]).toString());
        byte b = (byte)147;
        System.out.println(~b); */
        //double d = Float.MAX_VALUE;
        //float f = (float)Double.MAX_VALUE;
        //float f = (float)d;
        //System.out.println(f == Float.MAX_VALUE);
        //Double d = Double.parseDouble("-125");
        //System.out.println(d);
        //d = Double.POSITIVE_INFINITY;
        //System.out.println(1/d);
        //System.out.println((new BigInteger("FFF0",16)).or(new BigInteger("1")).toString());
        //System.out.println(1F);
        //long l = (long)0xFFFFL;
        //System.out.println(l);
        /*for (int i =0; i<4;i++) {
            System.out.println(MAX_VALUES[4+i].bitCount());
        }*/
        /*
        for (int i = 0; i<5; i++) {
            System.out.println(getMaxIndex("LWORD",i));
        }
        */
        for (int i = 0; i < 2; i++) {
            System.out.println(i + " " + partialAccess((new BigInteger("23aa71BdF3",16)).toString(),3,i));
        }    
    }
}
