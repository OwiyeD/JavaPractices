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
public class PouTypeHandler {
   
    private NodeList pouTypes;
        
    public void refresh(Element project) {
        pouTypes = project.getElementsByTagName("pou");
    }
    
    public int getNumberOfPouTypes() {
        return pouTypes.getLength();
    }
    
    public Element getPouTypeElement(int i) {
        return (Element)pouTypes.item(i);
    }
    
    public Element getPouTypeElementByName(String name) {
        
        Element retval = null;
        
        String sName = name.trim().toUpperCase(Locale.ENGLISH);
        for (int i=0; i<pouTypes.getLength(); i++) {
            Element pou = (Element)pouTypes.item(i);
            String pouName = pou.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (pouName.equals(sName)) {
                retval = pou;
                break;
            }
        }
        
        return retval;
     
    }
    
    public boolean isUserPou(String name) {
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        boolean retval = false;
        
        for (int i=0; i < pouTypes.getLength(); i++) {
            Element type = (Element)pouTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                retval = true;
                break;
            }
        }
        
        return retval;
    }
    
    public boolean isUserFB(String name) {
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        boolean retval = false;
        
        for (int i=0; i < pouTypes.getLength(); i++) {
            Element type = (Element)pouTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                if (type.getAttribute("pouType").equals("functionBlock")) {
                    retval = true;
                    break;
                }    
            }
        }
        
        return retval;
    }
    
    public boolean isFBType(String name) {
        
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        boolean retval = IECUtilities.isStandardFunctionBlockName(sname) || isUserFB(sname);
                
        return retval;
        
    }
    
    public boolean isUniquePouTypeName(String name) {
        
        int matches = 0;
        String sname = name.trim().toUpperCase(Locale.ENGLISH);
        
        for (int i=0; i < pouTypes.getLength(); i++) {
            Element type = (Element)pouTypes.item(i);
            String typename = type.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (sname.equals(typename)) {
                if (++matches > 1) {
                    break;
                }    
            }
        }
        return (matches == 1);
    }
    
    public static String findReturnType(Element pou) {
        String retval = null;
        NodeList tempList = pou.getElementsByTagName("returnType");
        //There can be at most one returnType element in a pou
        if (tempList.getLength() != 0) {
            Element type = XMLUtilities.getSingleChildElement((Element)tempList.item(0));
            retval = DataTypeHandler.extractTypeName(type);
        }
        return retval;
    }
    
}
