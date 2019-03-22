/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 *
 * @author Christoforos
 */
public class DataTypeHandler {

    private NodeList dataTypes;
        
    public void refresh(Element project) {
        dataTypes = project.getElementsByTagName("dataType");
    }
    
    public int getNumberOfDataTypes() {
        return dataTypes.getLength();
    }
    
    public Element getDataTypeElement(int i) {
        return (Element)dataTypes.item(i);
    }
    
    public boolean isUserType(String name) {
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        boolean retval = false;
        
        for (int i=0; i < dataTypes.getLength(); i++) {
            Element type = (Element)dataTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                retval = true;
                break;
            }
        }
        
        return retval;
    }
    
    public boolean isUniqueTypeName(String name) {
        
        int matches = 0;
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        
        for (int i=0; i < dataTypes.getLength(); i++) {
            Element type = (Element)dataTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                if (++matches > 1) {
                    break;
                }    
            }
        }
        return (matches == 1);
    }
    
    public String getBaseType(String name){
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        String retval = "";
        
        for (int i = 0; i < dataTypes.getLength(); i++) {
            Element type = (Element)dataTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                //According to the XML Schema, each user-defined datatype element
                //must have one and only one "baseType" child element
                //and this must have one and only one child element 
                Element baseType = XMLUtilities.findChildElement(type, "baseType");
                baseType = XMLUtilities.getSingleChildElement(baseType);
                retval = baseType.getNodeName();
                if (retval.equals("derived")) {
                    //According to the XML schema, a "derived" element will
                    //always have a "name" attribute
                    retval = getBaseType(baseType.getAttribute("name"));
                }
                break; 
            }
        }
        return retval;
    
    }
    
    
    private String getParentType(String name) {
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        String retval = "";
        
        for (int i = 0; i < dataTypes.getLength(); i++) {
            Element type = (Element)dataTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                //According to the XML Schems, each user-defined datatype element
                //must have one and only one "baseType" child element
                //and this must have one and only one child element 
                Element baseType = XMLUtilities.findChildElement(type, "baseType");
                baseType = XMLUtilities.getSingleChildElement(baseType);
                if (retval.equals("derived")) {
                    ////According to the XML schema, a "derived" element will
                    //always have a "name" attribute
                    retval = baseType.getAttribute("name");
                }
                break;
            }
        }          
        return retval;
    } 
    
    public String getSimpleInitialValue(String name) {
        
        //This method is called during compilation only
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        String retVal = "";
        
        if (IECUtilities.isAnyElementary(sname)) {
            retVal = IECUtilities.getInitialValue(sname);
        } else {
            for (int y = 0; y < dataTypes.getLength(); y++) {
                Element type = (Element)dataTypes.item(y);
                String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
                if (sname.equals(typename)) {
                    Element initialValue = XMLUtilities.findChildElement(type, "initialValue");
                    if (initialValue != null) {
                        //According to the XML schema, the "dataType" element can have
                        //at most one "initialValue" element. If such an element exists
                        //the Validator quarantees that it will have only one child element
                        //which will be "simpleValue". Since we are compiling,
                        //there are no errors and thus the "simpleValue" element contains
                        //an "iecHalInfo" element.
                        initialValue = XMLUtilities.getSingleChildElement(initialValue);
                        retVal = XMLUtilities.findChildElement(initialValue, "iecHalInfo").getAttribute("value");                                            
                    }
                    break;
                }
            }
            if (retVal.equals("")) {
                retVal = getSimpleInitialValue(getParentType(sname));    
            }
        }
        return retVal;
    }
    
    public static String extractTypeName(Element type) {
        String retval = type.getNodeName();
        if (retval.equals("derived")) {
            retval = type.getAttribute("name");
        }
        return retval;
    }
    
}
   

