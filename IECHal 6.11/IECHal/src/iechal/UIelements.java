/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.StageStyle;

/**
 *
 * @author Steve
 */
public class UIelements {
    
    private static TextArea console;
    public static final String indent = "    ";
    private static String currentIndentation = "";
    
    public static void setTextArea(TextArea area) {
        console = area;
    }
    
    public static ButtonType askToSave() {
        
        ButtonType answer = ButtonType.CANCEL;
        
        Dialog dialog = new Dialog();
        dialog.setContentText("Save changes?");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
           answer = result.get();
        }
        return answer;
    
    }
    
    /****************************************************************/
    /*   Methods for GUI output                                     */
    /*   Comment out if working with System.out                     */
    /****************************************************************/
    
    public static void print(String s) {
        
        console.appendText(s);
    
    }
    
    public static void println(String s) {
        
        console.appendText(s+"\n");
    
    }
    
    public static void addLine(String s) {
    
        println(currentIndentation + s);
        
    }
    
    public static void increaseIndent() {
    
        currentIndentation = currentIndentation + indent;
        
    }
    
    public static void reduceIndent() {
        
        int n = currentIndentation.length();
        currentIndentation = currentIndentation.substring(0,n-indent.length());
        
    }
    
    public static void resetIndent() {
    
        currentIndentation = "";
    }
    
    public static void reportException(Exception e) {
        
        StringWriter s = new StringWriter();
        PrintWriter p = new PrintWriter (s);
        e.printStackTrace(p);
        println(s.toString());
    
    }
    
    
    /***************************************************************/
    /*        Methods for System.out output                        */
    /*        Comment out if working with GUI                      */
    /***************************************************************/
    
    /*
    
    public static void print(String s) {
    
        System.out.print(s);
    
    }
    
    public static void println(String s) {
    
        System.out.println(s);
    
    }
    
    public static void reportException(Exception e) {
        
        e.printStackTrace(); 
    
    }
    
    */
    
    public static void main(String[] args) {
        try {
            System.out.println(1/0);
        }
        catch (Exception e) {
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter (s);
            e.printStackTrace(p);
            System.out.println(s.toString());
        }
    }
}
