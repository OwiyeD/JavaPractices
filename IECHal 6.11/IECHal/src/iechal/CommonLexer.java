/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 *
 * @author Christoforos
 */
public class CommonLexer {
   
    private String buf;
    private int maxpos;
    private int pos;
    private static final Character[] WHITESPACE_ARRAY = {new Character(' ')
            ,new Character('\t')};
    //public static HashSet keyword_set;
    private static final HashSet whitespace_set = 
                    new HashSet(Arrays.asList(WHITESPACE_ARRAY));
    private static final Character[] EOL_ARRAY = {new Character('\n'), 
        new Character('\r'), new Character('\f')};
    private static final HashSet eol_set = 
                    new HashSet(Arrays.asList(EOL_ARRAY));
    private static final String [] TOKEN_STRINGS = {"(", ")", ".", ",", ";", 
                     ":=", ":", "+", "-", "**", "/", "*", "=", "<=", "<>", "<",
                     ">=", ">", "&", "#"};
    private static final Token [] TOKEN_NAMES = {Token.LPAREN, Token.RPAREN, 
         Token.POINT, Token.COMMA, Token.SEMICOLON, Token.BECOMES, Token.COLON,
         Token.PLUS, Token.MINUS, Token.DOUBLESTAR, Token.SLASH, Token.STAR,
         Token.EQUALS, Token.LE, Token.NE, Token.LT, Token.GE, Token.GT, 
         Token.AMPERSAND, Token.HASH};
    private static final String [] KEYWORD_STRINGS = {"IF", "THEN", "ELSE",
                    "ELSIF", "END_IF", "CASE", "END_CASE", "OF", "FOR", "DO",
                    "END_FOR", "EXIT", "CONTINUE", "TO", "WHILE", "END_WHILE",
                    "REPEAT", "UNTIL", "END_REPEAT", "RETURN", 
                    "SINT", "INT",
                    "DINT", "LINT", "USINT", "UINT", "UDINT", "ULINT", "REAL", 
                    "LREAL", "TIME", "BOOL", "BYTE", "WORD", "DWORD", "LWORD",
                    "FALSE", "TRUE"};
    private static final HashSet keyword_set = 
                    new HashSet(Arrays.asList(KEYWORD_STRINGS));
    private static final Token[] KEYWORD_NAMES = {Token.IF, Token.THEN,
                    Token.ELSE, Token.ELSIF, Token.END_IF, Token.CASE,
                    Token.END_CASE, Token.OF, Token.FOR, Token.DO,
                    Token.END_FOR, Token.EXIT, Token.CONTINUE, Token.TO,
                    Token.WHILE, Token.END_WHILE, Token.REPEAT, Token.UNTIL,
                    Token.END_REPEAT, Token.RETURN,
                    Token.SINT, Token.INT,
                    Token.DINT, Token.LINT, Token.USINT, Token.UINT,
                    Token.UDINT, Token.ULINT, Token.REAL, Token.LREAL,
                    Token.TIME, Token.BOOL, Token.BYTE, Token.WORD,
                    Token.DWORD, Token.LWORD, Token.FALSE, Token.TRUE
    };
    private static final int stInit = 0;
    private static final int stCmnt_p = 1;
    private static final int stCmnt_s = 2;
    private static final int stCmnt_1 = 3;
    private static final int stIdent = 4;
    private static final int stUint = 5;
    private static final int stBin = 6;
    private static final int stOct = 7;
    private static final int stHex = 8;
    private static final int stAddr = 9;
    
    public static boolean isKeyword(String s) {
        
       return keyword_set.contains(s.trim().toUpperCase(Locale.ENGLISH));
        
    }
            
    public Token getNextToken() {
        boolean looping = true;
        Token result = Token.OTHER;
        int state = stInit;
        int depth = 0;
        boolean continuesPreviousToken = true;
        boolean isFirstTokenInLine = false;
        StringBuffer image = new StringBuffer("");
        int startPos = pos;
        boolean consecutiveUnderscores = false;
        boolean beginsWithUnderscore = false;
        boolean unexpectedCharacter = false;
        String errorMsg = "";
        
        while (looping) {
            switch (state) {
                case stInit: 
                    if (pos == maxpos) {
                        result = Token.EOF;
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if (whitespace_set.contains(ch)) {
                            pos = pos + 1;
                            continuesPreviousToken = false;
                        } else if (eol_set.contains(ch)) {
                            pos = pos + 1;
                            isFirstTokenInLine = true;
                            continuesPreviousToken = false;
                        } else if (ch == '_' || (ch >= 'A' && ch <= 'Z')) {
                            pos = pos+1;
                            image.append(ch);
                            state = stIdent;
                        } else if (buf.startsWith("(*",pos)) {
                            state = stCmnt_p;
                            pos=pos+2;
                            depth = 1;
                            continuesPreviousToken = false;
                        } else if (buf.startsWith("/*",pos)) {
                            state = stCmnt_s;
                            pos = pos + 2;
                            depth = 1;
                            continuesPreviousToken = false;
                        } else if (buf.startsWith("//",pos)) {
                            state = stCmnt_1;
                            pos = pos + 2;
                            continuesPreviousToken = false;
                        } else if (buf.startsWith("2#")) {
                            state = stBin;
                            pos = pos + 2;
                        } else if (buf.startsWith("8#")) {
                            state = stOct;
                            pos = pos + 2;
                        } else if (buf.startsWith("16#")) {
                            state = stHex;
                            pos = pos + 3;
                        } else if (ch >= '0' && ch <= '9') {
                            pos = pos + 1;
                            image.append(ch);
                            state = stUint;
                        } else if (ch == '%') {
                            pos = pos + 1;
                            state = stAddr;
                        } else {    
                            int n = TOKEN_STRINGS.length;
                            for (int i=0; i<n; i++) {
                                String s = TOKEN_STRINGS[i];
                                if (buf.startsWith(s,pos)) {
                                    result = TOKEN_NAMES[i];
                                    image.append(s);
                                    pos = pos + s.length();
                                    looping = false;
                                    break;
                                }
                            }
                            if (looping) {
                                //Nothing found yet. 
                                //Return the current character
                                result = Token.OTHER;
                                image.append(ch);
                                pos = pos + 1;
                                looping = false;
                            }
                        }
                    }
                    break;
                case stCmnt_p:
                    if (pos == maxpos) {
                        result = Token.ERROR;
                        errorMsg="No matching \"*)\".";
                        looping = false;
                    } else if (buf.startsWith("(*",pos)) {
                        depth = depth + 1;
                        pos = pos+2;
                    } else if (buf.startsWith("*)",pos)) {
                        depth = depth - 1;
                        pos = pos+2;
                        if (depth == 0) {
                            state = stInit;
                        } 
                    } else {
                        pos = pos + 1;
                    }
                    break;
                case stCmnt_s:
                    if (pos == maxpos) {
                        result = Token.ERROR;
                        errorMsg = "No matching \"*/\".";
                    } else if (buf.startsWith("/*",pos)) {
                        depth = depth + 1;
                        pos = pos+2;
                    } else if (buf.startsWith("*/",pos)) {
                        depth = depth - 1;
                        pos = pos+2;
                        if (depth == 0) {
                            state = stInit;
                        } 
                    } else {
                        pos = pos + 1;
                    }
                    break;
                case stCmnt_1:
                    if (pos == maxpos) {
                        result = Token.EOF;
                        looping = false;
                    } else if (eol_set.contains(buf.charAt(pos))) {
                        pos = pos + 1;
                        isFirstTokenInLine = true;
                        continuesPreviousToken = false;
                        state = stInit;
                    } else {
                        pos = pos+1;
                    }
                    break;
                case stIdent:
                    if (pos == maxpos) {
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if (ch == '_') {
                            if (image.charAt(image.length()-1) == '_') {
                               consecutiveUnderscores = true;
                            }
                            image.append(ch);
                            pos = pos + 1;
                        } else if ((ch >= 'A' && ch <= 'Z') ||
                                      (ch >= '0' && ch <= '9')) {
                            image.append(ch);
                            pos = pos+1;
                        } else {
                            looping = false;
                        }
                    }
                    if (looping == false) {
                        if (consecutiveUnderscores) {
                            if (errorMsg.length() == 0) {
                                //The error may occur several times.
                                //Report it only once per idfentifier.
                                errorMsg = "Consecutive \"_\" characters in identifier.";
                            }
                        }
                        if (image.charAt(image.length()-1) == '_') {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Identifier ends with \"_\"";
                            } else {
                                errorMsg = errorMsg + "\nIdentifier ends with \"_\".";
                            }
                        }
                        if (errorMsg.length() > 0 )  {
                            result = Token.ERROR;
                        } else {
                            result = Token.IDENTIFIER;
                            //check if keyword
                            int n = KEYWORD_STRINGS.length;
                            String s = new String(image);
                            for (int i=0; i<n; i++) {
                                if (KEYWORD_STRINGS[i].equals(s)) {
                                    result = KEYWORD_NAMES[i];
                                    break;
                                }
                            }                            
                        }
                    }
                    break;
                case stUint:
                    if (pos == maxpos) {
                        result = Token.UNSIGNED_INT;
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if (ch >= '0' && ch <= '9') {
                            image.append(ch);
                            pos = pos+1;
                        } else if (ch == '_') {
                            if (buf.charAt(pos) == '_') {
                                consecutiveUnderscores = true;
                            }
                            //ignore '_' between digits
                            pos=pos+1;
                        } else if (ch == 'E') {
                            looping = false;
                        } else if (ch >= 'A' && ch <= 'Z') {
                            // error: no letters allowed in numeric constants
                            unexpectedCharacter = true;
                            pos = pos + 1;
                        } else {
                            // any other character starts a new token
                            looping = false;
                        }
                    }    
                    if (looping == false) {
                        if (consecutiveUnderscores) {
                            //Errors may occur several times.
                            //Report them only once per number.
                            errorMsg = "Consecutive \"_\" characters in number.";
                        }
                        if (unexpectedCharacter) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Only decimal digits or underscores allowed.";   
                            } else {
                                errorMsg = errorMsg + "\nOnly decimal digits or underscores allowed.";
                            }
                        }
                        if (buf.charAt(pos-1) == '_') {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Number ends with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nNumber ends with \"_\".";
                            }    
                        }
                        if (errorMsg.length() > 0) {
                            result = Token.ERROR;
                        } else {
                            result = Token.UNSIGNED_INT;
                        }
                    }
                    break;
                case stBin:
                    if (pos == maxpos) {
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if ((ch == '0') || (ch == '1')) {
                            image.append(ch);
                            pos = pos+1;
                        } else if (ch == '_') {
                            if (image.length() == 0) {
                                beginsWithUnderscore = true;
                            } else if (buf.charAt(pos-1) == '_'){
                                consecutiveUnderscores = true;
                            }    
                            //ignore '_' between digits
                            pos = pos+1;
                        } else if ((ch >= 'A' && ch <= 'Z') ||
                                   (ch >= '2'  && ch <= '9')){
                            //no letters or non binbary digits allowed
                            unexpectedCharacter = true;
                            pos = pos + 1;
                        } else {
                            // any other character starts a new token
                            looping = false;
                        }
                    }
                    if (looping == false) {
                        if (image.length() == 0) {
                            errorMsg = "No binary digits found for binary literal.";
                        }
                        if (beginsWithUnderscore) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Binary literals can't begin with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nBinary litarals can't begin with \"_\".";
                            }
                        }
                        if (consecutiveUnderscores) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Consecutive \"_\" characters in binary literal.";   
                            } else {
                                errorMsg = errorMsg + "\nConsecutive \"_\" characters in binary literal.";
                            }
                        }
                        if (unexpectedCharacter) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Only binary digits or underscores allowed in binary literals.";   
                            } else {
                                errorMsg = errorMsg + "\nOnly binary digits or underscores allowed in binary literals.";
                            }
                        }
                        if (buf.charAt(pos-1) == '_') {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Binary literal ends with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nBinary literal ends with \"_\".";
                            }
                        } 
                        if (errorMsg.length() > 0) {
                            result = Token.ERROR;
                        } else {
                            result = Token.BIN_INT;
                        }
                    }
                    break;
                case stOct:
                    if (pos == maxpos) {
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if ((ch >= '0') && (ch <= '7')) {
                            image.append(ch);
                            pos = pos+1;
                        } else if (ch == '_') {
                            if (image.length() == 0) {
                                beginsWithUnderscore = true;
                            } else if (buf.charAt(pos-1) == '_'){
                                consecutiveUnderscores = true;
                            }    
                            //ignore '_' between digits
                            pos = pos+1;                            
                        } else if ((ch >= 'A' && ch <= 'Z') ||
                                   (ch == '8') || (ch == '9')) {
                            // error: no letters or non-octal digits allowed
                            unexpectedCharacter = true;
                            pos = pos + 1;
                        } else {
                            // any other character starts a new token
                            looping = false;
                        }
                    }
                    if (looping == false) {
                        if (image.length() == 0) {
                            errorMsg = "No octal digits found for octal literal.";
                        }
                        if (beginsWithUnderscore) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Octal literals can't begin with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nOctal litarals can't begin with \"_\".";
                            }
                        }
                        if (consecutiveUnderscores) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Consecutive \"_\" characters in octal literal.";   
                            } else {
                                errorMsg = errorMsg + "\nConsecutive \"_\" characters in octal literal.";
                            }
                        }
                        if (unexpectedCharacter) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Only octal digits or underscores allowed in octal literals.";   
                            } else {
                                errorMsg = errorMsg + "\nOnly octal digits or underscores allowed in octal literals.";
                            }
                        }
                        if (buf.charAt(pos-1) == '_') {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Octal literal ends with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nOctal literal ends with \"_\".";
                            }
                        } 
                        if (errorMsg.length() > 0) {
                            result = Token.ERROR;
                        } else {
                            result = Token.OCTAL_INT;
                        }
                    }
                    break;
                case stHex:
                    if (pos == maxpos) {
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if (((ch >= '0') && (ch <= '9')) ||  
                            ((ch >= 'A') && (ch <= 'F'))) {
                            image.append(ch);
                            pos = pos+1;
                        } else if (ch == '_') {
                            if (image.length() == 0) {
                                beginsWithUnderscore = true;
                            } else if (buf.charAt(pos-1) == '_'){
                                consecutiveUnderscores = true;
                            }    
                            //ignore '_' between digits
                            pos = pos+1;
                        } else if (ch >= 'G' && ch <= 'Z') {
                            unexpectedCharacter = true;
                            pos = pos + 1;
                        } else {
                            // any other character starts a new token
                            looping = false;
                        }
                    }
                    if (looping == false) {
                        if (image.length() == 0) {
                            errorMsg = "No hexadecimal digits found for hexadecimal literal.";
                        }
                        if (beginsWithUnderscore) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Hexadecimal literals can't begin with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nHexadecimal litarals can't begin with \"_\".";
                            }
                        }
                        if (consecutiveUnderscores) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Consecutive \"_\" characters in hexadecimal literal.";   
                            } else {
                                errorMsg = errorMsg + "\nConsecutive \"_\" characters in hexadecimal literal.";
                            }
                        }
                        if (unexpectedCharacter) {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Only hexadecimal digits or underscores allowed in hexadecimal literals.";   
                            } else {
                                errorMsg = errorMsg + "\nOnly hexadecimal digits or underscores allowed in hexadecimal literals.";
                            }
                        }
                        if (buf.charAt(pos-1) == '_') {
                            if (errorMsg.length() == 0) {
                                errorMsg = "Hexadecimal literal ends with \"_\".";   
                            } else {
                                errorMsg = errorMsg + "\nHexadecimal literal ends with \"_\".";
                            }
                        } 
                        if (errorMsg.length() > 0) {
                            result = Token.ERROR;
                        } else {
                            result = Token.HEX_INT;
                        }
                    }
                    break;
                case stAddr:
                    //only letters allowed
                    //Whether these letters make sense or not
                    //depends on the situation. So it is better to check it
                    //at a later stage.
                    if (pos == maxpos) {
                        looping = false;
                    } else {
                        char ch = buf.charAt(pos);
                        if (ch >= 'A' && ch <='Z') {
                            image.append(ch);
                            pos = pos + 1;
                        } else {
                            looping = false;
                        }
                    }
                    if (looping == false) {
                        if (image.length() == 0) {
                            errorMsg = "No address specifier found after \"%\".";
                            result = Token.ERROR;
                        } else {
                            result = Token.ADDR_SPEC;   
                        }
                    }
                    break;
            }
        }    
        result.continuesPreviousToken = continuesPreviousToken;
        result.isFirstTokenInLine = isFirstTokenInLine;
        result.setImage(new String(image));
        result.setStartPos(startPos);
        result.setErrorMsg(errorMsg);
        return result;
    }
    /**
     * @param args the command line arguments
     */
    
    public void setup(String s) {
        buf = s.toUpperCase(Locale.ENGLISH);
        maxpos = buf.length();
        pos = 0;
    }
    
    public static void main(String[] args) {
        String s = "(* A p-comment *) \n" +
                   "/* A s-comment */ \n\n" +
                   "// A single line comment \n" +
                   "for I=3.13e+15#USINT %X0.0.0";
        CommonLexer test = new CommonLexer();
        test.setup(s);
        //CommonLexer test = new CommonLexer();
        //test.buf = "(* A p-comment *) \n" +
        //           "/* A s-comment */ \n\n" +
        //           "// A single line comment \n" +
        //           "for I=3.13e+15#USINT %X0.0.0";
        //test.buf = test.buf.toUpperCase(Locale.ENGLISH);
        //test.maxpos = test.buf.length();
        //test.pos = 0;
        Token t;
        do {
            t = test.getNextToken();
            if (t.equals(Token.ERROR)) {
                System.out.println("Error at position "+t.getStartPos());
                System.out.println(t.getErrorMsg());
            } else {
                System.out.print(t);
                if (t.getImage().length() > 0) {
                    System.out.println(":" + t.getImage());
                } else {
                    System.out.println("");
                }
                if (t.continuesPreviousToken) {
                    System.out.println("==>Continues previous token.");
                }
                if (t.isFirstTokenInLine) {
                    System.out.println("==> Is first token in line.");
                }
            }
        } while (!t.equals(Token.EOF));
    }
    
}
