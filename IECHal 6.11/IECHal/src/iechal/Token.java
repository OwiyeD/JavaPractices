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
public enum Token {
    EOF, IDENTIFIER,
    IF, THEN, ELSE, ELSIF, END_IF,
    CASE, OF, END_CASE,
    FOR, DO, END_FOR, EXIT, CONTINUE, TO,
    WHILE, END_WHILE, REPEAT, UNTIL, END_REPEAT, RETURN,
    SINT, INT, DINT, LINT, USINT, UINT, UDINT, ULINT,
    REAL, LREAL, TIME,
    BOOL, BYTE, WORD, DWORD, LWORD, FALSE, TRUE,
    LPAREN, RPAREN,
    POINT, COMMA, SEMICOLON, BECOMES, COLON,
    PLUS, MINUS, STAR, SLASH, DOUBLESTAR,
    LT, LE, GT, GE, EQUALS, NE, AMPERSAND, HASH,
    BIN_INT, OCTAL_INT, HEX_INT, UNSIGNED_INT, ADDR_SPEC,
    ERROR,OTHER;

    public boolean isFirstTokenInLine = false;
    public boolean continuesPreviousToken = true;
    private String image;
    private int startPos;
    private String errorMsg="";
    
    public void setImage(String im) {
        this.image = im;
    }
    
    public String getImage() {
        return this.image;
    }
    
    public int getStartPos() {
        return startPos;
    }
    
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }
    
    public String getErrorMsg() {
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
