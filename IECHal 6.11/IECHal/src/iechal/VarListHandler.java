/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christoforos
 */
public class VarListHandler {

    protected Element container;
    protected NodeList variables;

    public VarListHandler(Element container) {
        //The container can be either a pou element or a 
        //configuration element containing global variables).
        this.container = container;
        variables = container.getElementsByTagName("variable");
    }
    
    public int getNumberOfVariables() {
        return variables.getLength();
    }
    
    public Element getVariableElement(int i) {
        return (Element)variables.item(i);
    }    
    
    public Element getVariableElementByName(String name) {
        
        Element retval = null;
        
        String sName = name.trim().toUpperCase(Locale.ENGLISH);
        for (int i=0; i<variables.getLength(); i++) {
            Element var = (Element)variables.item(i);
            String vName = var.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (vName.equals(sName)) {
                retval = var;
                break;
            }
        }
        
        return retval;
    }
        
    public static String getVariableDataType(Element var) {
        
        NodeList tempList = var.getElementsByTagName("type");
        //Each variable must have one and only one type element
        //which has one and only one child
        Element type = XMLUtilities.getSingleChildElement((Element)tempList.item(0));
        return DataTypeHandler.extractTypeName(type);
    
    }
    
    public static String getVariableType(Element var) {
        
        return var.getParentNode().getNodeName();
        
    }
    
    public static boolean isConstant(Element var) {
    
        return ((Element)var.getParentNode()).getAttribute("constant").equals("true");
    
    }
    
    public boolean isUniqueVariableName(String name) {
        
        int matches = 0;
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        
        for (int i=0; i < variables.getLength(); i++) {
            Element type = (Element)variables.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                if (++matches > 1) {
                    break;
                }    
            }
        }
        return (matches == 1);
    }        
    
}
