/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christoforos
 */
public class XMLUtilities {

    public static Element getSingleChildElement(Element e) {
        //Used with element nodes such as "type" or "returnType", which have 
        //one and only one child of type element, according to the XML schema.
        //Returns that element node.
        Element retval = null;
        NodeList list = e.getChildNodes();
        for (int j = 0; j < list.getLength(); j++) {
            if(list.item(j).getNodeType() == Node.ELEMENT_NODE){
                Element current = (Element)list.item(j);
                if (!(current.getTagName().equals("iecHalError") || current.getTagName().equals("iecHalInfo"))) {
                    retval = (Element)list.item(j);
                    break;
                }    
            }
        }
        return retval;
    }
    
    public static Element findChildElement(Element e, String tag) {
        //Normally used with element nodes which have at most one child of type element with the given tag tame, according to the XML schema.
        //Returns that element node. If the element node has more children with the given tag name, the method returns the first one of them.
        Element retval = null;
        NodeList list = e.getChildNodes();
        for (int j = 0; j < list.getLength(); j++) {
            if(list.item(j).getNodeType() == Node.ELEMENT_NODE){
                Element current = (Element)list.item(j);
                if (current.getTagName().equals(tag)) {
                    retval = current;
                    break;
                }
            }
        }        
        return retval;
    }
 
    public static ArrayList<Element> getChildrenElements(Element e) {
        
        ArrayList<Element> retval = new ArrayList();
        NodeList list = e.getChildNodes();
        for (int j = 0; j < list.getLength(); j++) {
            if(list.item(j).getNodeType() == Node.ELEMENT_NODE){
                retval.add((Element)list.item(j));                
            }
        }
        
        return retval;
    }
    
    public static ArrayList<Element> getExtensionElements(Element e, String tag) {
        
        ArrayList<Element> retval = new ArrayList();
        NodeList list = e.getElementsByTagName(tag);
        for (int j = 0; j < list.getLength(); j++) {
            Element current = (Element)list.item(j);
            Element parent = (Element)current.getParentNode();
            if (parent.getTagName().equals("data")) {
                parent = (Element)parent.getParentNode();
                if (parent.getTagName().equals("addData")) {
                    parent = (Element)parent.getParentNode();
                    if (parent == e) {
                        retval.add(current);
                    }
                }
            }
        }
        
        return retval;
    }
    
}
