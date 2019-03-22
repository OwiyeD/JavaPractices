/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal.gui;

import iechal.IECUtilities;
import iechal.XMLUtilities;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.w3c.dom.Element;

/**
 *
 * @author Christoforos
 */
public class DataTypeViewer {
    
    private Text typeLabel = new Text("Data type:     ");
    private Text baseLabel = new Text("Base type:     ");
    private Text initLabel = new Text("Initial value: ");
    
    private Text typeName = new Text();
    private Text baseName = new Text();
    private Text initValue = new Text();
    
    private GridPane gridPane = new GridPane();
    
    public DataTypeViewer() {
        
        typeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        baseLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        initLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        typeName.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        baseName.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        initValue.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        gridPane.add(typeLabel,0,0);
        gridPane.add(baseLabel,0,1);
        gridPane.add(initLabel,0,2);
        gridPane.add(typeName,1,0);
        gridPane.add(baseName,1,1);
        gridPane.add(initValue,1,2);
        
    }
    
    public Node getContent() {
        
        return gridPane;
    
    }
    
    public void setDataType(Element dataTypeElement) {
    
        typeName.setText(dataTypeElement.getAttribute("name"));
        Element baseType = XMLUtilities.findChildElement(dataTypeElement, "baseType");
        baseType = XMLUtilities.getSingleChildElement(baseType);
        String baseTypeName = baseType.getNodeName();
        if (baseTypeName.equals("derived")) {                    
            baseTypeName = baseType.getAttribute("name");
        } else if (!IECUtilities.isAnyElementary(baseTypeName)) {
            baseTypeName = "Not supported (" + baseTypeName +")";
        }
        baseName.setText(baseTypeName);
        Element initialValue = XMLUtilities.findChildElement(dataTypeElement, "initialValue");
        if (initialValue != null) {
            //get the simple value element
            initialValue = XMLUtilities.getSingleChildElement(initialValue);
            initValue.setText(initialValue.getAttribute("value"));                                            
        } else {
            initValue.setText("Not provided.");
        }
    }
}
