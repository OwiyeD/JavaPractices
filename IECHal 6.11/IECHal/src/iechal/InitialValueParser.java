/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

/**
 *
 * @author Christoforos
 */

import java.util.HashMap;
import java.io.*;
import java.math.BigInteger;
import java.util.Locale;
public class InitialValueParser {
       
    private String newType;
    private String errorMessage;
    private HashMap knownConstants = new HashMap();
    private CommonLexer lex = new CommonLexer();
    private String newValue;
    private Token lastToken;
    private boolean externalDependencies;
    private boolean isTypedNumber;
    
    private static final int st_fp_begin = 0;
    private static final int st_fp_point = 1;
    private static final int st_fp_frac = 2;
    private static final int st_fp_e = 3;
    private static final int st_fp_exp = 4;
    private static final int st_fp_unexpected = 5;
    private static final int st_fp_end = 6;
    
    private static final String[] TYPE_NAMES = {"BIT", "BYTE", "WORD", "DWORD", "LWORD"}; 
            
    public void clear() {
        errorMessage = "";
        newValue = "";
        newType = "";
        externalDependencies = false;
        isTypedNumber = false;
    }
    
    public boolean isKnownConstant(String name) {
        return knownConstants.containsKey(name);
    }
    
    public void addConstant(String name, String type, String value) {
        String [] attr = new String[2];
        attr[0] = type;
        attr[1] = value;
        knownConstants.put(name, attr);
    }
    
    public void resetConstants() {
        knownConstants.clear();
    }
    
    public boolean parseExpr(String iec) {
        
        lex.setup(iec);
        lastToken = lex.getNextToken();
        boolean retval = false;
        if (lastToken.equals(Token.EOF)) {
            errorMessage = "Empty Initial Value.";
        } else {
            retval = Expression();
            if (retval) {
                if (lastToken.equals(Token.ERROR)) {
                    errorMessage = lastToken.getErrorMsg();
                    retval = false;
                } else if (!lastToken.equals(Token.EOF)) {
                    errorMessage = "Unexpected character after initial value expression.";
                    retval = false;
                } 
            }    
        }
        return retval;
    }
    
    public String getDataType() {
        return newType;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getValue() {
        return newValue;
    }
    
    public boolean hasExternalDependencies() {
        return externalDependencies;    
    } 
    
    public String makeJavaExp(String targetType) {
    
        String retval = null;
        
        String sourceType = newType;
        if (sourceType.startsWith("p")) {
            if (IECUtilities.isAnyBit(targetType)) {
                sourceType = sourceType.substring(1);
            } else {
                sourceType = NumUtilities.findSmallestIntegerType(newValue);
            }
        }    
        if (sourceType.equals(targetType) || IECUtilities.isImplicitlyConverted(sourceType, targetType)) {
            String result = newValue;
            switch (targetType) {
                case "REAL": 
                    result = result + "f";
                    break;
                case "LINT":
                case "TIME":
                    result = result + "L";
                    break;
                case "ULINT":
                case "LWORD":
                    if (sourceType.equals(targetType)) {
                        result = "(long)0x"+(new BigInteger(result)).toString(16)+"L";
                    } else {
                        //Not really necessary but doesn't hurt
                        result = result + "L";
                    }
                    break;
                case "UDINT":
                case "DWORD":
                    if (sourceType.equals(targetType)) {
                        result = "(int)0x" + (new BigInteger(result)).toString(16);
                    }
                    break;
                case "UINT":
                case "WORD":
                    if (sourceType.equals(targetType)) {
                        result = "(short)0x" + (new BigInteger(result)).toString(16);
                    }
                    break;
                case "USINT":
                case "BYTE":
                    if (sourceType.equals(targetType)) {
                        result = "(byte)0x" + (new BigInteger(result)).toString(16);
                    }
                    break;
                case "BOOL":
                    result = result.equals("0")?"false":"true";
                    break;
            }
            retval = result;
        }
        
        return retval;
    
    }
    
    private boolean Expression() {
        //Expression : Xor_Expr ('OR'  Xor_Expr)*;
        
        boolean retval = Xor_Expr();
        if (retval) {
            if (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("OR")) {
                if(newType.startsWith("p")) {
                    newType = newType.substring(1);
                }
                if (IECUtilities.isAnyBit(newType)) {
                    String runningType = newType;
                    //We use biginteger in order to handle all different bit-string sizes
                    //with a single data type
                    BigInteger result = new BigInteger("0");
                    if (!externalDependencies) {
                        result = new BigInteger(newValue);
                    }
                    do {
                        lastToken = lex.getNextToken();            
                        retval = Xor_Expr();
                        if (retval) {                 
                            if(newType.startsWith("p")) {
                                newType = newType.substring(1);
                            }
                            if (IECUtilities.isAnyBit(newType)) {
                                if (!runningType.equals(newType)) {
                                    if (IECUtilities.isImplicitlyConverted(runningType, newType)) {
                                        runningType = newType;
                                    }
                                    //No else: Since newType and runningType are both any_bit types,
                                    //newType is implicitly converted to runningType
                                    //(for any two any_bit types, the smaller is implicitly
                                    //converted to the larger)
                                }
                                if (!externalDependencies) {
                                    result = result.or(new BigInteger(newValue));
                                }    
                            } else {
                                errorMessage = "Not appropriate data type for \"OR\" operator: " + newType + ".";
                                retval = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    } while (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("OR"));
                    if (retval) {
                        newType = runningType;
                        if (!externalDependencies) {
                            newValue = result.toString();
                        }    
                    }
                } else {
                    errorMessage = "Not appropriate data type for \"OR\" operator: " + newType;
                    retval = false;
                }    
            }     
        }
        //No need to set isTypedNumber. If the expression was a single XOR_Expr, isTypedNumber
        //will keep the value set by the method XOR_Expr. If the expression consists of more
        //than one XOR_Expr then these will be of boolean or bitstring type and therefore
        //after the calls to XOR_Expr isTypedNumber will have the value false. 
        return retval;
    }
    
    private boolean Xor_Expr() {
        //Xor_Expr : And_Expr ('XOR' And_Expr)*;
        
        boolean retval = And_Expr();
        if (retval) {
            if (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("XOR")) {
                if(newType.startsWith("p")) {
                    newType = newType.substring(1);
                }
                if (IECUtilities.isAnyBit(newType)) {
                    String runningType = newType;
                    //We use biginteger in order to handle all different bit-string sizes
                    //with a single data type
                    BigInteger result = new BigInteger("0");
                    if (!externalDependencies) {
                        result = new BigInteger(newValue);
                    }
                    do {
                        lastToken = lex.getNextToken();            
                        retval = And_Expr();
                        if (retval) {                 
                            if(newType.startsWith("p")) {
                                newType = newType.substring(1);
                            }
                            if (IECUtilities.isAnyBit(newType)) {
                                if (!runningType.equals(newType)) {
                                    if (IECUtilities.isImplicitlyConverted(runningType, newType)) {
                                        runningType = newType;
                                    }
                                    //No else: See comment in Expression()
                                }
                                if (!externalDependencies) {
                                    result = result.xor(new BigInteger(newValue));
                                }
                            } else {
                                errorMessage = "Not appropriate data type for \"XOR\" operator: " + newType + ".";
                                retval = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    } while (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("XOR"));
                    if (retval) {
                        newType = runningType;
                        if (!externalDependencies) {
                            newValue = result.toString();
                        }    
                    }
                } else {
                    errorMessage = "Not appropriate data type for \"XOR\" operator: " + newType + ".";
                    retval = false;
                }
            } 
        }
        //No need to update isTypedNumber, for similar reasons as in method Expression
        //(See comment there)
        return retval;
    }
    
    private boolean And_Expr() {
        //And_Expr : Compare_Expr (('&'|'AND') Compare_Expr)*;
        boolean retval = Compare_Expr();
        if (retval) {
            if (lastToken.equals(Token.AMPERSAND) || 
                (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("AND")) ) {
                if(newType.startsWith("p")) {
                    newType = newType.substring(1);
                }
                if (IECUtilities.isAnyBit(newType)) {
                    String runningType = newType;
                    //We use biginteger in order to handle all different bit-string sizes
                    //with a single data type
                    BigInteger result = new BigInteger("0");
                    if (!externalDependencies) {
                        result = new BigInteger(newValue);
                    }
                    do {
                        String opstring = lastToken.getImage();
                        lastToken = lex.getNextToken();
                        retval = Compare_Expr();
                        if (retval) {                 
                            if(newType.startsWith("p")) {
                                newType = newType.substring(1);
                            }
                            if (IECUtilities.isAnyBit(newType)) {
                                if (!runningType.equals(newType)) {
                                    if (IECUtilities.isImplicitlyConverted(runningType, newType)) {
                                        runningType = newType;
                                    }
                                    //No else: see comment in Expression()
                                }
                                if (!externalDependencies) {
                                    result = result.and(new BigInteger(newValue));
                                }    
                            } else {
                                errorMessage = "Not appropriate data type for \"" + opstring + "\" operator: " + newType +".";
                                retval = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    } while (lastToken.equals(Token.AMPERSAND) || 
                             (lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("AND")) );
                    if (retval) {
                        newType = runningType;
                        if (!externalDependencies) {
                            newValue = result.toString();
                        }    
                    }
                } else {
                    errorMessage = "Not appropriate data type for \"" + lastToken.getImage() +"\" operator: " + newType + ".";
                    retval = false;
                }
            } 
        }
        //No need to update isTypedNumber, for similar reasons as in method Expression
        //(See comment there)
        return retval;
    }
    
    private boolean Compare_Expr() {
        //Compare_Expr : Add_Expr (('=' | '<>'|'<'|'>'|'<='|'>=') Add_Expr)?;
        
        boolean retval = Add_Expr();
        if (retval) {
            if (lastToken.equals(Token.EQUALS) || lastToken.equals(Token.NE) || lastToken.equals(Token.LT) ||
                lastToken.equals(Token.GT) || lastToken.equals(Token.LE) || lastToken.equals(Token.GE)) {                
                Token op = lastToken;
                String firstType = newType;
                String firstValue = newValue;
                lastToken = lex.getNextToken();
                retval = Add_Expr();
                if (retval) {
                    if (firstType.startsWith("p")) {
                        if (IECUtilities.isAnyBit(newType)) {
                            firstType = firstType.substring(1);
                        } else {
                            firstType = NumUtilities.findSmallestIntegerType(firstValue);                                
                        }
                    }
                    if (newType.startsWith("p")) {
                        if (IECUtilities.isAnyBit(firstType)) {
                            newType = newType.substring(1);
                        } else {
                            newType = NumUtilities.findSmallestIntegerType(newValue);
                        }
                    }
                    if (! (firstType.equals(newType) || IECUtilities.isImplicitlyConverted(firstType, newType) || IECUtilities.isImplicitlyConverted(newType, firstType) || 
                            (firstType.equals("ULINT") && IECUtilities.isAnyInt(newType)) || (newType.equals("ULINT") && IECUtilities.isAnyInt(firstType))) ){
                        errorMessage = "Incompatible data types \"" + newType + "\" and \"" + firstType + "\".";
                        retval = false;
                    }
                    if (retval) {
                        newType = "BOOL";
                        isTypedNumber = false;
                        if (!externalDependencies) {
                            if (IECUtilities.isAnyReal(firstType) || IECUtilities.isAnyReal(newType)) {
                                //We know that the values can be accurately represented as double
                                //because we have checked that the data types are compatible
                                newValue = Boolean.toString(NumUtilities.compare(Double.parseDouble(firstValue), Double.parseDouble(newValue), op));
                            } else {
                                newValue = Boolean.toString(NumUtilities.compare(new BigInteger(firstValue), new BigInteger(newValue), op));
                            }
                        }
                    }                    
                }
            }
        }             
        return retval;
    }
    
    private boolean Add_Expr(){
        // Add_Expr : Term (('+'|'-') Term)*;
        
        boolean retval = Term();
        if (retval) {
            if (lastToken.equals(Token.PLUS) || lastToken.equals(Token.MINUS)) {
                if (newType.startsWith("p")) {
                    newType = NumUtilities.findSmallestIntegerType(newValue);
                }
                if (IECUtilities.isAnyMagnitude(newType)) {
                    String runningType = newType;
                    boolean resultIsTypedNumber = isTypedNumber;
                    boolean isInteger;
                    BigInteger intVal = BigInteger.ZERO;
                    //we use BigInteger in order to be able to handle signed
                    //and unsigned types of any size with the same code.
                    double x = 0.0;
                    if (IECUtilities.isAnyReal(runningType)) {
                        isInteger = false;
                        if (!externalDependencies) {
                            x = Double.parseDouble(newValue);
                        }    
                    } else {                        
                        isInteger = true;
                        if (!externalDependencies) {
                            intVal = new BigInteger(newValue);
                        }    
                    }
                    do {
                        String opstring = lastToken.getImage();
                        lastToken = lex.getNextToken();            
                        retval = Term();
                        if (retval) {   
                            if (newType.startsWith("p")) {
                                newType = NumUtilities.findSmallestIntegerType(newValue);
                            }                                                        
                            if (!IECUtilities.isAnyMagnitude(newType)) {
                                errorMessage = "Not appropriate data type for \"" + opstring +"\" operator.";
                                retval = false;
                                break;
                            }
                            resultIsTypedNumber |= isTypedNumber;
                            if (!runningType.equals(newType)) {
                                if (IECUtilities.isImplicitlyConverted(runningType, newType)) {
                                    runningType = newType;
                                } else if (!IECUtilities.isImplicitlyConverted(newType, runningType)) {
                                    errorMessage = "Incompatible data types \"" + newType + "\" and \"" + runningType + "\".";
                                    retval = false;
                                    break;
                                }
                            }                            
                            if (opstring.equals("-") && (!externalDependencies)) {
                                newValue = NumUtilities.changeSign(newValue);
                            }
                            if (isInteger && IECUtilities.isAnyReal(newType)) {
                                isInteger = false;
                                if (!externalDependencies) {
                                    //We know that the conversion can be accurately done
                                    //because we have checked that runningType is compatible
                                    //with newType
                                    x = intVal.doubleValue();
                                }
                            }
                            if (!externalDependencies) {
                                if (isInteger) {
                                    intVal = intVal.add(new BigInteger(newValue));
                                    if (IECUtilities.isAnyDuration(runningType)) {
                                        if (!NumUtilities.isWithinLimitsOfIntType(intVal, "LINT")) {
                                            errorMessage = "Result of " + opstring + " operation is too big for data type " + runningType;
                                            retval = false;
                                            break;
                                        }
                                    } else if (!NumUtilities.isWithinLimitsOfIntType(intVal, runningType)) {
                                        if (resultIsTypedNumber) {
                                            errorMessage = "Result of " + opstring + " operation is too big for data type " + runningType;
                                            retval = false;
                                            break;
                                        } else {
                                            runningType = NumUtilities.findSmallestIntegerType(intVal.toString());
                                            if (runningType == null) {
                                                errorMessage = "Result of " + opstring + " exceeds the range of all integer data types.";
                                                retval = false;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    x = x + Double.parseDouble(newValue);
                                    if (!Double.isFinite(x)) {
                                        errorMessage = "Result of " + opstring + " operation cannot be represented as a finite floating point number.";
                                        retval = false;
                                        break;
                                    } else if (runningType.equals("REAL")) {
                                        float f = (float)x;
                                        if (!Float.isFinite(f)) {
                                            if (resultIsTypedNumber) {
                                                errorMessage = "Result of expression too big for data type REAL. Use LREAL instead";
                                                retval = false;
                                                break;
                                            } else {
                                                runningType = "LREAL";
                                            }
                                        }
                                    }
                                }   
                            }
                        } else {
                            break;
                        }
                    } while (lastToken.equals(Token.PLUS) || lastToken.equals(Token.MINUS));
                    if (retval) {
                        isTypedNumber = resultIsTypedNumber;
                        newType = runningType;
                        if (!externalDependencies) {
                            if (isInteger) {
                                newValue = intVal.toString();
                            } else if (newType.equals("REAL")) {
                                newValue = Float.toString((float)x);
                            } else {
                                newValue = Double.toString(x);
                            }
                        }
                    }
                } else {
                    errorMessage = "Not appropriate data type for \""+lastToken.getImage()+"\" operator.";
                    retval = false;
                }
            }
        }    
        return retval;
    }
    
    private boolean Term(){
        //Term : Power_Expr ('*' | '/' | 'MOD' Power_Expr)*;
        
        boolean retval = Power_Expr ();
        if (retval){            
            if (lastToken.equals(Token.STAR) || lastToken.equals(Token.SLASH)||(lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("MOD"))){
                if (newType.startsWith("p")) {
                    newType = NumUtilities.findSmallestIntegerType(newValue);
                }
                if ((!IECUtilities.isAnyNum(newType)) || (lastToken.getImage().equals("MOD") && (!IECUtilities.isAnyInt(newType)))) {
                    errorMessage = "Not appropriate data type for \"" + lastToken.getImage() +"\" operator: "+ newType +".";
                    retval = false;
                } else {                                                    
                    String runningType = newType;
                    boolean resultIsTypedNumber = isTypedNumber;
                    boolean isInteger;
                    BigInteger intVal = BigInteger.ONE;
                    double x = 0.0;
                    if (IECUtilities.isAnyInt(runningType)) {
                        isInteger = true;
                        if (!externalDependencies) {
                            intVal = new BigInteger(newValue);
                        }    
                    } else {
                        isInteger = false;
                        if (!externalDependencies) {
                            x = Double.parseDouble(newValue);
                        }
                    }
                    do {
                        String opstring = lastToken.getImage();
                        lastToken = lex.getNextToken(); 
                        retval=Power_Expr();
                        if (retval) {
                            if (newType.startsWith("p")) {
                                newType = NumUtilities.findSmallestIntegerType(newValue);
                            }
                            if (!IECUtilities.isAnyNum(newType)) {
                                errorMessage = "Not appropriate data type for \"" + opstring +"\" operator: "+ newType +".";
                                retval = false;
                                break;
                            }
                            if (!newType.equals(runningType)) {
                                if (IECUtilities.isImplicitlyConverted(runningType, newType)) {
                                    runningType = newType;
                                } else if (! IECUtilities.isImplicitlyConverted(newType, runningType)) {
                                    errorMessage = "Incompatible data types \"" + newType + "\" and \"" + runningType + "\".";
                                    retval = false;
                                    break;
                                }
                            }
                            resultIsTypedNumber |= isTypedNumber;
                            if (opstring.equals("MOD")) {
                                if (!(isInteger && IECUtilities.isAnyInt(newType))) {
                                    errorMessage = "\"MOD\" operator accepts only integer operands.";
                                    retval = false;
                                    break;
                                } 
                                if (!externalDependencies) {
                                    BigInteger temp = new BigInteger(newValue);
                                    try {
                                        intVal = intVal.remainder(temp);
                                    }
                                    catch (ArithmeticException e) {
                                        //Table 29 (p.88) of the standard   
                                        intVal = BigInteger.ZERO;
                                    }
                                }
                            } else {
                                if (isInteger) {
                                    if (IECUtilities.isAnyInt(newType)) {
                                        if (!externalDependencies) {
                                            BigInteger temp = new BigInteger(newValue);
                                            if (opstring.equals("*")) {
                                                intVal = intVal.multiply(temp);                                                                                                
                                            } else {
                                                try {
                                                    intVal = intVal.divide(temp);
                                                }
                                                catch (ArithmeticException e) {
                                                    errorMessage = "Division by zero.";
                                                    retval = false;
                                                    break;
                                                }
                                            }
                                            if (!NumUtilities.isWithinLimitsOfIntType(intVal, runningType)) {
                                                if (resultIsTypedNumber) {
                                                    errorMessage = "Result of " + opstring + " operation is too big for data type " + runningType;
                                                    retval = false;
                                                    break;
                                                } else {
                                                    runningType = NumUtilities.findSmallestIntegerType(intVal.toString());
                                                    if (runningType == null) {
                                                        errorMessage = "Result of " + opstring + " exceeds the range of all integer data types.";
                                                        retval = false;
                                                        break;
                                                    }
                                                }
                                            }    
                                        }       
                                    } else {
                                        isInteger = false;
                                        if (!externalDependencies) {
                                            //We know that the conversion can be accurately done
                                            //because we have checked that runningType is compatible
                                            //with newType
                                            x = intVal.doubleValue();
                                        }    
                                    }
                                }
                                if ((!isInteger) && (!externalDependencies)) {
                                    if (opstring.equals("*")) {
                                        x = x * Double.parseDouble(newValue);
                                    } else {
                                        x = x / Double.parseDouble(newValue);
                                        //Floating point division by zero produces Infinity or NaN, no Exception.
                                    }
                                    if (!Double.isFinite(x)) {
                                        errorMessage = "Result of " + opstring + " operation cannot be represented as a finite floating point number";
                                        retval = false;
                                        break;
                                    } else if (runningType.equals("REAL")) {
                                        float f = (float)x;
                                        if (!Float.isFinite(f)) {
                                            if (resultIsTypedNumber) {
                                                errorMessage = "Result of expression too big for data type REAL. Use LREAL instead";
                                                retval = false;
                                                break;
                                            } else {
                                                runningType = "LREAL";
                                            }
                                        }
                                    }
                                }
                            }     
                        } else {
                            break;
                        } 
                    } while (lastToken.equals(Token.STAR)|| lastToken.equals(Token.SLASH)||(lastToken.equals(Token.IDENTIFIER) && lastToken.getImage().equals("MOD")));                        
                    if (retval) {
                        isTypedNumber = resultIsTypedNumber;
                        newType = runningType;
                        if (!externalDependencies) {
                            if (isInteger) {
                                newValue = intVal.toString();
                            } else if (newType.equals("REAL")) {
                                newValue = Float.toString((float)x);
                            } else {
                                newValue = Double.toString(x);
                            }
                        }    
                    }
                }     
             }
        }
        return retval;
    }
    
    private boolean Power_Expr (){
    //Power_Expr : Unary_Expr ('**' Unary_Expr)*;
    
        boolean retval = Unary_Expr();
        double x = 0;
        //Math.pow and then cast to float if have to
        //Only problem if base is LINT or ULINT, which cannot be implicitly
        //converted to double
        if(retval){
            if (lastToken.equals(Token.DOUBLESTAR)){
                if (newType.startsWith("p")) {
                    newType = NumUtilities.findSmallestIntegerType(newValue);
                }
                if ((!IECUtilities.isAnyNum(newType)) || isLint(newType)) {
                    errorMessage = "Not appropriate data type for operator \"**\": The first argument cannot be a " + newType + ".";
                    retval = false;
                } else {
                    if (!externalDependencies) {
                        x = Double.parseDouble(newValue);
                    }    
                }
                if (retval) {
                    String runningType = newType.equals("REAL")?"REAL":"LREAL";
                    boolean resultIsTypedNumber = isTypedNumber;
                    //The result will be the same type as the base
                    //(Standard, p. )
                    do {
                        lastToken = lex.getNextToken();
                        retval = Unary_Expr();
                        if (retval) {
                            if (newType.startsWith("p")) {
                                newType = NumUtilities.findSmallestIntegerType(newValue);
                            }
                            if (!IECUtilities.isAnyNum(newType)) {
                                errorMessage = "Not appropriate data type for operator \"**\": The second argument cannot be a " + newType + ".";
                                retval = false;
                                break;
                            }                            
                            if (!externalDependencies) {
                                if (isLint(newType)) {
                                    //A LINT or ULINT may not be accurately represented as a double
                                    //so we can't use Math.pow
                                    x = NumUtilities.raiseToLongIntPower(x, new BigInteger(newValue));
                                } else {
                                    //Both the base and the exponent can be accurately represented as double
                                    //so we can use Math.pow
                                    x = Math.pow(x, Double.parseDouble(newValue));
                                }
                                if (!Double.isFinite(x)) {
                                    errorMessage = "Result of power expression cannot be represented as a finite floating point number";
                                    retval = false;
                                    break;
                                } else if (runningType.equals("REAL")) {
                                    float f = (float)x;
                                    if (!Float.isFinite(f)) {
                                        if (resultIsTypedNumber) {
                                            errorMessage = "Result of expression too big for data type REAL. Use LREAL instead";
                                            retval = false;
                                            break;
                                        } else {
                                            runningType = "LREAL";
                                        }
                                    }
                                }    
                            }    
                        } else {
                            break;
                        }
                    } while (lastToken.equals(Token.DOUBLESTAR));
                    if (retval) {
                        isTypedNumber = resultIsTypedNumber;
                        newType = runningType;
                        if (!externalDependencies) {
                            if (newType.equals("REAL")) {
                                newValue = Float.toString((float)x);
                            } else {
                                newValue = Double.toString(x);
                            }
                        }
                    }
                }
            }
        }
        
        return retval;
    }
        
    private boolean Unary_Expr () {
    //Unary_Expr  : '-' | '+' | 'NOT' ? Primary_Expr;
        
        boolean retval;
        boolean hasSign = false;
        boolean isNegative = false;
        boolean hasNot = false;
        if (lastToken.equals(Token.MINUS)) {
            hasSign = true;
            isNegative = true;
            lastToken = lex.getNextToken();
        } else if (lastToken.equals(Token.PLUS)) {
            hasSign = true;
            lastToken = lex.getNextToken();
        } else if (lastToken.equals(Token.IDENTIFIER)
                 && lastToken.getImage().equals("NOT")) {
            hasNot = true;
            lastToken = lex.getNextToken();
        }
        retval = Primary_Expr();
        if (retval) {
            if (hasSign) {
                if (newType.startsWith("p") || IECUtilities.isAnyNum(newType)) {
                    if (!externalDependencies) {
                        if (isNegative) {
                            newValue = NumUtilities.changeSign(newValue);
                            if (IECUtilities.isAnyInt(newType)) {
                                newType = NumUtilities.findSmallestIntegerType(newValue);
                                if (newType == null) {
                                    errorMessage = "Integer constant too big.";
                                    retval = false;
                                }
                            }
                        }
                        if (newType.startsWith("p")) {
                            newType = NumUtilities.findSmallestIntegerType(newValue);
                            if (newType == null) {
                                errorMessage = "Integer constant too big.";
                                retval = false;
                            }
                        }
                    }
                } else {
                    errorMessage = "Wrong data type: Number expected after sign.";
                    retval = false;
                }
            } else if (hasNot) {
                if (newType.equals("pBOOL")) {
                    newType = "BOOL";
                    if (!externalDependencies) {
                        newValue = newValue.equals("0")?"1":"0";
                    }    
                } else {
                    if (newType.startsWith("p")) {
                        newType = newType.substring(1);
                    }
                    if (IECUtilities.isAnyBit(newType)) {
                        if (!externalDependencies) {
                            newValue = NumUtilities.logicalNot(newValue, newType);
                        }    
                    } else {    
                        errorMessage = "Wrong data type: Boolean or bit string expected.";
                        retval = false;
                    }
                }    
            }
        }
        //No change to isTypedNumber: leave it as it is.
        
        return retval;        
    }
    
    private boolean Primary_Expr() {
        //Primary_Expr     : '(' Expression ')' | Identifier Multibit_Part_Access ? | Constant ;
        
        boolean retval = true;
        
        if (lastToken.equals(Token.LPAREN)) {
            lastToken = lex.getNextToken();
            retval = Expression();
            if (retval && lastToken.equals(Token.RPAREN)) {
                lastToken=lex.getNextToken();                
            } else {
                errorMessage = "Unexpected character: \")\" expected.";
                retval = false;
            }                
        } else if (lastToken.equals(Token.IDENTIFIER)) {
            String name = lastToken.getImage();
            String [] attr = (String [])knownConstants.get(name);
            if (attr == null) {
                errorMessage = "Unknown identifier: \"" + name + "\"";
                retval = false;
            } else {
                newType = attr[0];
                isTypedNumber = true;
                if (attr[1]==null) {
                    externalDependencies = true;
                } else {
                    newValue = attr[1];
                }
                lastToken = lex.getNextToken();
                if (lastToken.equals(Token.POINT) && lastToken.continuesPreviousToken && IECUtilities.isAnyBit(newType) && (!newType.equals("BOOL"))) {
                    boolean hasLetter = false;
                    String addressSpec = "";
                    lastToken = lex.getNextToken();
                    if (lastToken.equals(Token.ADDR_SPEC) && lastToken.continuesPreviousToken) {
                        addressSpec = lastToken.getImage();
                        if (addressSpec.matches("[XBWDL]")) {
                            hasLetter = true;
                            lastToken = lex.getNextToken();
                        } else {
                            errorMessage = "Illegal address specifier: " + addressSpec;
                            retval = false;
                        }    
                    }
                    if (retval) {
                        if (lastToken.equals(Token.UNSIGNED_INT) && lastToken.continuesPreviousToken) {
                            BigInteger count = new BigInteger(lastToken.getImage());
                            //I use BigInteger in order to be sure that there will be no problem
                            //if some programmer puts a really big number (an unlikely but
                            //conceivable situation).
                            if (!hasLetter) {
                                addressSpec = "X";
                            }
                            int sizeIndex = "XBWDL".indexOf(addressSpec);
                            //We know that size is not -1 because we have checked that
                            //addressSpec is one of the letters X,B,W,D,L
                            int maxVal = NumUtilities.getMaxIndex(newType, sizeIndex);
                            if (maxVal == -1) {
                                //The logic of the method NumUtilities.getMaxIndex guarantees
                                //that this cannot happen if the address specifier is "X" or if the 
                                //address specifier is missing.
                                errorMessage = "Address specifier " + addressSpec + " cannot be used with data type " + newType + ".";
                                retval = false;
                            } else if (count.compareTo(new BigInteger((new Integer(maxVal)).toString())) > 0) {
                                String typeName = TYPE_NAMES[sizeIndex].substring(0, 1) + TYPE_NAMES[sizeIndex].substring(1).toLowerCase(Locale.ENGLISH);
                                errorMessage = typeName + " number " + count.toString() + " too high for data type " + newType + "(max " + maxVal + ").";
                                retval = false;
                            } else { 
                                newType = TYPE_NAMES[sizeIndex];
                                isTypedNumber = true;
                                if ((!externalDependencies) && (sizeIndex < 4)) {                                
                                    //If sizeIndex == 4 (LWORD) then no change of newValue (which must also be of type LWORD)
                                    int icount = count.intValue(); //we know that count < 64, so it fits in an int
                                    newValue = NumUtilities.partialAccess(newValue, sizeIndex, icount);
                                }
                            }
                            lastToken = lex.getNextToken();
                        } else {
                            errorMessage = "Unexpected character: " + (hasLetter?"Number":"Address specifier or number") + " expected.";
                            retval = false;
                        }
                    }
                }
            }            
        } else {
            retval = Constant();
        }
        return retval;    
    }
    
    private boolean Constant() {
       //'TIME' '#' ('+'|'-')? Interval | Literal
       
        boolean retval;
        
        if (lastToken.equals(Token.IDENTIFIER)
            && lastToken.getImage().equals("TIME")) {
            lastToken = lex.getNextToken();
            if (!(lastToken.equals(Token.HASH) && lastToken.continuesPreviousToken)) {
                errorMessage = "Unexpected character: \"#\" expected.";
                retval = false;
            } else {
                lastToken = lex.getNextToken();
                if (!lastToken.continuesPreviousToken) {
                    errorMessage = "Unexpected character: Sign or time interval expected.";
                    retval = false;
                } else {
                    boolean hasSign = false;
                    boolean isNegative = false;
                    if (lastToken.equals(Token.PLUS)){
                        lastToken = lex.getNextToken();
                        hasSign = true;
                    } else if (lastToken.equals(Token.MINUS)) {
                        lastToken = lex.getNextToken();
                        hasSign = true;
                        isNegative = true;
                    }
                    if (hasSign && !lastToken.continuesPreviousToken) {
                        errorMessage = "Unexpected character: Time interval expected.";
                        retval = false;
                    } else {
                        retval = Interval();
                        if (retval) {
                            isTypedNumber = true;
                            if (isNegative) {
                                //No need to check external dependencies. This is always a constant.
                                newValue = "-" + newValue;
                            }
                        }
                    }
                }
            }
        } else {
            retval = Literal();
        }
        return retval;
    }
    
    private boolean Interval() {
        //Unsigned_Int ('.' Unsigned_Int Identifier) | 
        //              Identifier ('.' Unsigned_Int Identifier)?)

        boolean retval = true ;
        BigInteger firstNum = null;
        BigInteger lastNum = null;
        String lastUnit = "";
        String middle = "";
        
        if (lastToken.equals(Token.UNSIGNED_INT)) {
            firstNum = new BigInteger(lastToken.getImage());
            lastToken = lex.getNextToken();
            if(lastToken.equals(Token.POINT) && lastToken.continuesPreviousToken){
                lastToken = lex.getNextToken();
                if (lastToken.equals(Token.UNSIGNED_INT) && lastToken.continuesPreviousToken) {
                    lastNum = new BigInteger(lastToken.getImage());
                    lastToken=lex.getNextToken();
                    if (lastToken.equals(Token.IDENTIFIER) 
                            && lastToken.continuesPreviousToken 
                            && TimeUtilities.isTimeUnit(lastToken.getImage())) {
                        lastUnit = lastToken.getImage();
                        lastToken = lex.getNextToken();
                    } else {
                        errorMessage = "Unexpected character: Time unit expected.";
                        retval = false;
                    }
                } else {
                    errorMessage = "Unexpected character: Number expected.";
                    retval = false;
                }
            } else if (lastToken.equals(Token.IDENTIFIER) && lastToken.continuesPreviousToken) {
                middle = lastToken.getImage();
                lastToken = lex.getNextToken();
                if (lastToken.equals(Token.POINT) && lastToken.continuesPreviousToken) {
                    lastToken = lex.getNextToken();
                    if (lastToken.equals(Token.UNSIGNED_INT) && lastToken.continuesPreviousToken) {
                        lastNum = new BigInteger(lastToken.getImage());
                        lastToken = lex.getNextToken();                        
                        if (lastToken.equals(Token.IDENTIFIER) 
                                && lastToken.continuesPreviousToken
                                && TimeUtilities.isTimeUnit(lastToken.getImage())) {
                            lastUnit = lastToken.getImage();
                            lastToken = lex.getNextToken();
                        } else {
                            errorMessage = "Unexpected character: Time unit expected.";
                            retval = false;
                        }
                    } else {
                        errorMessage = "Unexpected character: Number expected.";
                        retval = false;
                    }
                }    
            } else {
                errorMessage = "Unexpected character: Time unit or decimal point expected.";
                retval = false;
            }
        } else {
            errorMessage = "Unexpected character: Number expected.";
            retval = false;
        }
        
        if (retval) {
            String temps = TimeUtilities.analyze(firstNum, lastNum, middle, lastUnit);
            if (temps.startsWith("*")) {
                errorMessage = temps.substring(1);
                retval = false;
            } else {
                newValue = temps;
                newType = "TIME";
            }
        }
        
        return retval;
    }
     
    private boolean Bool_Literal () {
      //Bool_Literal  : '0'|'1'|'FALSE' | 'TRUE';
        
        boolean retval = true;
         
        if (lastToken.equals(Token.UNSIGNED_INT)
            && lastToken.getImage().equals("0")) {
            newValue = "0";
            newType = "BOOL";
            lastToken = lex.getNextToken();
        }
        else if (lastToken.equals(Token.UNSIGNED_INT) 
                 && lastToken.getImage().equals("1")) {
            newValue = "1";
            newType = "BOOL";
            lastToken = lex.getNextToken();
        }    
        else if (lastToken.equals(Token.FALSE)){
            newValue = "0";
            newType = "BOOL";
            lastToken = lex.getNextToken();
        }
        else if (lastToken.equals(Token.TRUE)){
            newValue = "1";
            newType = "BOOL";
            lastToken = lex.getNextToken();
        } else {
            errorMessage = "Unexpected character: Bool literal expected.";
            retval = false;
        }        
        return retval;
    }

    private boolean Literal() {
        // 'BOOL' '#' Bool_Literal | 
        // ('SINT'|'INT'|'DINT'|'LINT'|'USINT'|'UINT'|'UDINT'|'ULINT') '#' Int_Literal |
        // ('REAL' | 'LREAL') '#' Real_Literal |
        // ('BYTE' | 'WORD' | 'DWORD' | 'LWORD') '#' (Unsigned_Int | Based_Int) |
        // 'FALSE' | 'TRUE' | Binary_Int | Octal_Int | Hex_Int |
        // ('+' | '-')? Unsigned_Int('.'Unsigned_Int ('E' ('+'|'-')?Unsigned_Int)?)?
   
        boolean retval=true;
        
        if (lastToken.equals(Token.BOOL)) {
            lastToken = lex.getNextToken();
            if (!(lastToken.equals(Token.HASH) && lastToken.continuesPreviousToken)){
                errorMessage = "Unexpected character: \"#\" expected.";
                retval = false;            
            } else {
                lastToken=lex.getNextToken();
                if (!lastToken.continuesPreviousToken) {
                    errorMessage = "Unexpected character: Bool literal expected.";
                    retval = false;
                } else {
                    retval= Bool_Literal();
                    if (retval) {
                        isTypedNumber = true;
                    }
                }
            }            
        } else if (isIntType(lastToken)) {
            String type = lastToken.getImage();
            lastToken = lex.getNextToken();
            if (!(lastToken.equals(Token.HASH) && lastToken.continuesPreviousToken)){
                errorMessage = "Unexpected character: \"#\" expected.";
                retval = false;
            } else {
                lastToken=lex.getNextToken();
                if (!lastToken.continuesPreviousToken) {
                    errorMessage = "Unexpected character: Integer literal or based number expected.";
                    retval = false;
                } else {
                    retval = Int_Literal(type);
                    if (retval) {
                        isTypedNumber = true;
                    }
                }    
            }
        } else if (isRealType(lastToken)) {
            String type = lastToken.getImage();
            lastToken = lex.getNextToken();
            if (!(lastToken.equals(Token.HASH) && lastToken.continuesPreviousToken)){
                errorMessage = "Unexpected character: \"#\" expected.";
                retval = false;
            } else {
                lastToken=lex.getNextToken();
                if (!lastToken.continuesPreviousToken) {
                    errorMessage = "Unexpected character: Real literal expected.";
                    retval = false;
                } else {
                    retval= Real_Literal(type);
                    if (retval) {
                        isTypedNumber = true;
                    }
                }
            }
        } else if (isBitStringType(lastToken)) {
            String type = lastToken.getImage();
            lastToken = lex.getNextToken();
            if (!(lastToken.equals(Token.HASH) && lastToken.continuesPreviousToken)){
                errorMessage = "Unexpected character: \"#\" expected.";
                retval = false;
            } else {
                lastToken=lex.getNextToken();
                if (!lastToken.continuesPreviousToken) {
                    errorMessage = "Unexpected character: Based number or positive integer expected.";
                    retval = false;
                } else if (lastToken.equals(Token.UNSIGNED_INT)) {
                    String value = NumUtilities.makeJavaBitStringLiteral(lastToken.getImage(), type);
                    if (value != null) {
                        newType = type;
                        newValue = value;
                    } else {
                        errorMessage = "Not suitable value for data type " + type + ".";
                        retval = false;
                    }
                    lastToken = lex.getNextToken();
                } else {
                    retval = Based_Int_BitStr(type);
                }
                if (retval) {
                    isTypedNumber = true;
                }
            }
        } else if (lastToken.equals(Token.FALSE)) {
            newValue = "0";
            newType = "BOOL";
            isTypedNumber = false;            
            lastToken = lex.getNextToken();
        } else if (lastToken.equals(Token.TRUE)) {
            newValue = "1";
            newType = "BOOL";
            isTypedNumber = false;
            lastToken = lex.getNextToken();
        } else if (lastToken.equals(Token.BIN_INT)) {
            retval = doBitStringLiteral(2);
            if (retval) {
                isTypedNumber = false;
            }
        } else if (lastToken.equals(Token.OCTAL_INT)) {
            retval = doBitStringLiteral(8);
            if (retval) {
                isTypedNumber = false;
            }
        } else if (lastToken.equals(Token.HEX_INT)) {
            retval = doBitStringLiteral(16);
            if (retval) {
                isTypedNumber = false;
            }
        } else { 
            retval = Real_Literal(null);
            if (retval) {
                isTypedNumber = false;
            }
        }
        return retval;    
    }
    
    private boolean Based_Int(String type) {
      //Binary_Int | Octal_Int | Hex_Int
        
        boolean retval = true;
        int base = 0;
        switch (lastToken) {
            case BIN_INT:
                base = 2;
                break;
            case OCTAL_INT:
                base = 8;
                break;
            case HEX_INT:
                base = 16;
                break;
        }
        if (base > 0) {
            String value = NumUtilities.makeIntLiteral(lastToken.getImage(), type, base);
            if (value != null) {
                newType = type;
                newValue = value;
            } else {
                errorMessage = "Not suitable value for data type " + type + ".";
                retval = false;
            }
            lastToken= lex.getNextToken();            
        } else {
            errorMessage = "Unexpected character: Integer literal or based number expected.";
            retval = false;
        }
        
        return retval;
    }
    
    private boolean Based_Int_BitStr(String type) {
      //Binary_Int | Octal_Int | Hex_Int
        
        boolean retval = true;
        int base = 0;
        switch (lastToken) {
            case BIN_INT:
                base = 2;
                break;
            case OCTAL_INT:
                base = 8;
                break;
            case HEX_INT:
                base = 16;
                break;
        }
        if (base > 0) {            
            String value = NumUtilities.makeJavaBitStringLiteral(lastToken.getImage(), type, base);
            if (value != null) {
                newType = type;
                newValue = value;
            } else {
                errorMessage = "Not suitable value for data type " + type + ".";
                retval = false;
            }            
            lastToken= lex.getNextToken();
        } else {
            errorMessage = "Unexpected character: Based number or positive integer expected.";
            retval = false;
        }
        
        return retval;
    }
    
    private boolean Real_Literal(String type) {
      //Real_Literal: ('+' | '-')?  Unsigned_Int '.' Unsigned_Int ('E' ('+|'-')?Unsigned_Int)?
    
        boolean retval = true;
        boolean hasSign = false;
        boolean isInteger = false;
        int state = st_fp_begin;
        StringBuffer value = new StringBuffer("");
        String err = null;
        
        while (state != st_fp_end) {
            switch (state) {
                case st_fp_begin:
                    if (lastToken.equals(Token.PLUS)) {
                        lastToken = lex.getNextToken();
                        hasSign = true;
                    } else if (lastToken.equals(Token.MINUS)) {
                        lastToken = lex.getNextToken();
                        hasSign = true;
                        value.append("-");
                    }
                    if (lastToken.equals(Token.UNSIGNED_INT)) {
                        if (hasSign && !lastToken.continuesPreviousToken) {
                            state = st_fp_unexpected;
                            err = "Number";
                        } else {
                            value.append(lastToken.getImage());
                            lastToken = lex.getNextToken();
                            state = st_fp_point;
                        }                        
                    } else {
                        state = st_fp_unexpected;
                        err = "Number";
                    }
                    break;
                case st_fp_point:
                    if (lastToken.equals(Token.POINT) && lastToken.continuesPreviousToken) {
                        value.append(".");
                        lastToken = lex.getNextToken();
                        state = st_fp_frac;                         
                    } else if (type == null) {
                        isInteger = true;
                        state = st_fp_end;
                    } else {
                        state = st_fp_unexpected;
                        err = "Decimal point";
                    }
                    break;
                case st_fp_frac:
                    if (lastToken.equals(Token.UNSIGNED_INT) && lastToken.continuesPreviousToken) {
                        value.append(lastToken.getImage());
                        lastToken = lex.getNextToken();
                        state = st_fp_e;                        
                    } else {
                        state = st_fp_unexpected;
                        err = "Number";
                    }
                    break;
                case st_fp_e: 
                    if (lastToken.equals(Token.IDENTIFIER) 
                            && lastToken.getImage().equals("E") 
                            && lastToken.continuesPreviousToken) {
                        value.append("E");
                        lastToken = lex.getNextToken();
                        state = st_fp_exp;                        
                    } else {
                        state = st_fp_end;
                    }
                    break;
                case st_fp_exp:
                    if ((lastToken.equals(Token.PLUS) || lastToken.equals(Token.MINUS)) && lastToken.continuesPreviousToken) {
                        value.append(lastToken.getImage());
                        lastToken = lex.getNextToken();                                        
                    }
                    if (lastToken.equals(Token.UNSIGNED_INT) && lastToken.continuesPreviousToken) {
                        value.append(lastToken.getImage());
                        lastToken = lex.getNextToken();    
                        state = st_fp_end;
                    } else {
                        state = st_fp_unexpected;
                        err = "Number";
                    }
                    break;
                case st_fp_unexpected:
                    errorMessage = "Unexpected character: " + err + " expected.";
                    retval = false;
                    state = st_fp_end;
            }
        }    
        
        String svalue = value.toString();
        if (retval) {
            if (type != null) {
                svalue = NumUtilities.checkRealLiteral(svalue, type);
                if (svalue != null) {
                    newType = type;
                    newValue = svalue;
                } else {
                    errorMessage = "Not suitable value for data type " + type + ".";
                    retval = false;
                }    
            } else if (!isInteger) {
                String stype = NumUtilities.findSmallestRealType(svalue);
                if (stype != null) {
                    newType = stype;
                    newValue = svalue;
                } else {
                    errorMessage = "Floating point constant too big.";
                    retval = false;
                }
            } else if (!svalue.startsWith("-")) {
                String stype = NumUtilities.findSmallestPositiveType(svalue);
                if (stype != null) {
                    newType = "p" + stype;
                    newValue = svalue;
                } else {
                    errorMessage = "Integer constant too big.";
                    retval = false;
                }
            } else {
                String stype = NumUtilities.findSmallestSignedIntegerType(svalue);
                if (stype != null) {
                    newType = stype;
                    newValue = svalue;
                } else {
                    errorMessage = "Integer constant too big.";
                    retval = false;
                }
            }
        }
                    
        return retval;
    }
    
            
    private boolean Int_Literal (String type) {     
        //Int_Literal :  (('+'|'-')? Unsigned_Int ) | Based_Int;
            
        boolean retval = true;
        boolean hasSign = false;
        String value = "";
        if (lastToken.equals(Token.PLUS)){
            hasSign = true;
            lastToken = lex.getNextToken();
        } else if (lastToken.equals(Token.MINUS)) {
            hasSign = true;
            value = "-";
            lastToken = lex.getNextToken();
        }
        if (lastToken.equals(Token.UNSIGNED_INT)) {
            if (hasSign && (!lastToken.continuesPreviousToken)) {
                errorMessage = "Unexpected character: Number expected.";
                retval = false;
            } else {    
                value = value + lastToken.getImage();
                value = NumUtilities.checkIntLiteral(value, type);
                if (value != null) {
                    newType = type;
                    newValue = value;
                } else {
                    errorMessage = "Not suitable value for data type " + type + ".";
                    retval = false;
                }
                lastToken = lex.getNextToken();
            }                
        } else if (! hasSign) {   
            retval = Based_Int(type);
        } else {
            errorMessage = "Unexpected character: Integer literal or based number expected.";;
            retval = false;
        }
        
        return retval;
        
    }
            
    private boolean doBitStringLiteral(int base) {
    
        boolean retval = true;
        
        BigInteger x = new BigInteger(lastToken.getImage(),base);
        String type = NumUtilities.findSmallestBitStringType(x);
        if (type != null) {
            newValue = x.toString();
            newType = type;
        } else {
            errorMessage = "Number too big.";
            retval = false;
        }
        lastToken = lex.getNextToken();
        
        return retval;
        
    }        
    
    private boolean isLint(String type) {
        return type.endsWith("LINT");
    }
    
    private boolean isIntType(Token t) {
    
        boolean retval;
        
        switch (t) {
            case SINT:
            case INT:
            case DINT:
            case LINT:
            case USINT:
            case UINT:
            case UDINT:
            case ULINT:    
                retval = true;
                break;
            default:
                retval = false;
        }
        return retval;
    }
    
    private boolean isRealType(Token t) {
        //REAL or LREAL
        
        boolean retval;
        
        switch (t) {
            case REAL:
            case LREAL:
                retval = true;
                break;
            default:
                 retval = false;
            
        }
        return retval;
    }
    
    private boolean isBitStringType(Token t) {
        // BYTE, WORD, DWORD, LWORD
        
        boolean retval;
        
        switch (t) {
            case BYTE:
            case WORD:
            case DWORD:
            case LWORD:
                retval = true;
                break;
            default:
                 retval = false;
        }
        return retval;
    }
    
    
    
    public static void main(String[] args) {
        
        System.out.println("abx".matches("[abxTY]"));
        /*
        boolean res;
        InitialValueParser parser = new InitialValueParser();
        // The name of the file to open.
    String fileName ="newfile";

    // This will reference one line at a time
    String line = null;

    try {
        // FileReader reads text files in the default encoding.
        FileReader fileReader = 
            new FileReader(fileName);

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = 
            new BufferedReader(fileReader);

        while((line = bufferedReader.readLine()) != null) {
            parser.clear();
            res=parser.parseExpr(line);
            System.out.println(line);
            if(res){
                System.out.println("ok");
            } else {
                System.out.println(parser.getErrorMessage());
            }    
        }   

        // Always close files.
        bufferedReader.close();         
    }
    catch(FileNotFoundException ex) {
        System.out.println(
            "Unable to open file '" + 
            fileName + "'");                
    }
    catch(IOException ex) {
        System.out.println(
            "Error reading file '" 
            + fileName + "'");                  
        // Or we could just do this: 
        // ex.printStackTrace();
      }
      */
   }
}