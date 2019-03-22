/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal.gui;

import javafx.scene.control.TitledPane;
import org.w3c.dom.Element;

/**
 *
 * @author Christoforos
 */
public abstract class Editor {
    
    protected TitledPane titledPane = new TitledPane();
    protected Element element;
    protected Element parentElement;
    protected boolean dataChanged = false;  
    
    
    public TitledPane getTitledPane() {
        return titledPane;
    }    

    
    public Element getElement() {
        return element;
    }    
    
    public boolean isDataChanged() {
        return dataChanged;
    }
    
    //Configure the titled pane    
    public abstract void setup();
    //Initialize the titled pane using the fields in element
    public abstract void loadValues();
    //Validate the entered values
    public abstract boolean validate();
    //Transfer the entered values to element. If parentElement is not null, add element to parentElement
    public abstract void save();
    
}
