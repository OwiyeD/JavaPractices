/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Christoforos
 */
public class XMLErrorLogger implements ErrorHandler {
    
    private boolean ok = true;
    
    public boolean everythingOk() {
        return ok;
    }
    
    @Override
    public void warning(SAXParseException saxpe) throws SAXException {
        UIelements.println("XML Parser warning at line " + saxpe.getLineNumber() + ", column " + saxpe.getColumnNumber() + ": "+ saxpe.getMessage());        
    }

    @Override
    public void error(SAXParseException saxpe) throws SAXException {
        UIelements.println("XML Parser error at line " + saxpe.getLineNumber() + ", column " + saxpe.getColumnNumber() + ": "+ saxpe.getMessage());
        ok = false;
    }

    @Override
    public void fatalError(SAXParseException saxpe) throws SAXException {
        UIelements.println("XML Parser fatal error at line " + saxpe.getLineNumber() + ", column " + saxpe.getColumnNumber() + ": "+ saxpe.getMessage());
        ok = false;
    }
    
}
