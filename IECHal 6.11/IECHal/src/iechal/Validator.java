/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.Locale;
import org.w3c.dom.*;

/**
 *
 * @author Christoforos
 */
public class Validator {
    
    private static void createErrorNode(Element elem, String msg) {
        
        Document dom = elem.getOwnerDocument();
        Element err = dom.createElement("iecHalError");
        Attr attr = dom.createAttribute("message");
        attr.setValue(msg);
        err.setAttributeNode(attr);
        elem.appendChild(err);
            
    }
    
    private static void createInfoNode(Element elem, String val) {
        
        Document dom = elem.getOwnerDocument();
        Element err = dom.createElement("iecHalInfo");
        Attr attr = dom.createAttribute("value");
        attr.setValue(val);
        err.setAttributeNode(attr);
        elem.appendChild(err);
            
    }
    
    public static void validateProject(Project proj) {
        
        // Validate Project Name
        Element root = proj.getDom().getDocumentElement();        
        boolean ok;
        validateProjectName((Element)root.getElementsByTagName("contentHeader").item(0));
        
        // Validate Data Types
        DataTypeHandler dataTypeHandler = proj.getDataTypeHandler();
        InitialValueParser initialValueParser = proj.getInitialValueParser();
        initialValueParser.resetConstants();
        int n = dataTypeHandler.getNumberOfDataTypes();
        for (int i = 0; i < n; i++) {
            Element dataType = dataTypeHandler.getDataTypeElement(i);
            ok = validateDataTypeName(dataType,proj);
            if (ok) {
                // The dataType element must contain one and only one baseType Element (XML schema)
                Element baseType = XMLUtilities.findChildElement(dataType, "baseType");
                // baseType contains one and only one child element (XML schema)
                baseType = XMLUtilities.getSingleChildElement(baseType);
                ok = validateBaseType(baseType, proj);
                if (ok) {
                    Element initialValue = XMLUtilities.findChildElement(dataType, "initialValue");
                    if (initialValue != null) {
                        initialValue = XMLUtilities.getSingleChildElement(initialValue);
                        if (!initialValue.getTagName().equals("simpleValue")) {                    
                            createErrorNode(initialValue,"Unsupported type of initial value.");
                        } else {    
                            validateSimpleInitialValue(initialValue, dataType.getAttribute("name"), proj);                            
                        }    
                    }
                }
            }    
        }
        
        //Validate POU Types
        PouTypeHandler pouTypeHandler = proj.getPouTypeHandler();
        n = pouTypeHandler.getNumberOfPouTypes();
        for (int i = 0; i < n; i++) {
            Element pouType = pouTypeHandler.getPouTypeElement(i);
            validatePouTypeName(pouType, proj);
            
            Element pouInterface = XMLUtilities.findChildElement(pouType, "interface");
            if (pouInterface != null) {
                Element returnType = XMLUtilities.findChildElement(pouInterface, "returnType");
                if (returnType != null) {
                    if (!pouType.getAttribute("pouType").equals("function")) {
                        createErrorNode(pouType,"Only functions can have a return type.");
                    } else {
                        // returnType contains one and only one child element (XML schema)
                        returnType = XMLUtilities.getSingleChildElement(returnType);
                        validateFunctionReturnType(returnType, proj);
                    }
                }
                VarListHandler interfaceHandler = new VarListHandler(pouType);
                int numVars = interfaceHandler.getNumberOfVariables();
                for (int j = 0; j < numVars; j++) {
                    Element var = interfaceHandler.getVariableElement(j);
                    String varType = VarListHandler.getVariableType(var);
                    if (varType.equals("accessVars")) {
                        createErrorNode(var,"Access variables not supported.");
                    } else if (varType.equals("globalVars")) {
                        createErrorNode(var,"Global variable declarations not supported inside POUs.");
                    } else {
                        validateVariableName(var, interfaceHandler, proj);
                    }
                }
            }
            
            
            
        }
        
    }
    
    public static boolean validateProjectName(Element contentHeader) {
        
        boolean retval = true;
        String s = contentHeader.getAttribute("name").trim(); //We know that the attribute exists (XML schema)
        if (s.equals("")) {            
            createErrorNode(contentHeader,"Attribute \"name\" of element \"project\\contentHeader\" cannot be empty.");
            retval = false;
        }        
        return retval;
        
    }
    
    public static boolean validateDataTypeName(Element dataType, Project proj) {
        
        boolean retval = true;
        
        String s = dataType.getAttribute("name").trim();
        if (s.equals("")) {
            createErrorNode(dataType,"The data type name cannot be empty.");
            retval = false;
        } else if (!IECUtilities.isValidIdentifier(s)) {
            createErrorNode(dataType,"The data type name must be a valid identifier.");
            retval = false;
        } else if (CommonLexer.isKeyword(s)) {
            createErrorNode(dataType,"The data type name cannot be a reserved word.");
            retval = false;
        } else if (IECUtilities.isStandardPouName(s)){
            createErrorNode(dataType,"A data type cannot have the same name as a standard POU.");
            retval = false;
        } else if (proj.getPouTypeHandler().isUserPou(s)) {
            createErrorNode(dataType,"A data type cannot have the same name as a user defined POU.");
            retval = false;
        } else if (!proj.getDataTypeHandler().isUniqueTypeName(s)) {
            createErrorNode(dataType,"The data type name is not unique.");
            retval = false;
        }
        return retval;
        
    }    
    
    public static boolean validateBaseType(Element baseType, Project proj) {
        
        boolean retval = true;
        String s = baseType.getTagName();
        if (!IECUtilities.isAnyElementary(s)) {
            if (s.equals("derived")) {
                s = baseType.getAttribute("name").trim(); //We know that the attribute exists (XML schema)
                if (s.equals("")) {
                    createErrorNode(baseType, "Base type name of user defined type cannot be empty.");
                    retval = false;
                } else {
                    DataTypeHandler dataTypeHandler = proj.getDataTypeHandler();
                    if (!dataTypeHandler.isUserType(s)) {
                        createErrorNode(baseType, "Unknown base type name: " + s + ".");
                        retval = false;
                    } else {
                        s = s.toUpperCase(Locale.ENGLISH);
                        //We know that the following attribute exists (XML Schema)
                        String dataTypeName = ((Element)baseType.getParentNode().getParentNode()).getAttribute("name");
                        dataTypeName = dataTypeName.trim().toUpperCase(Locale.ENGLISH);
                        if (s.equals(dataTypeName)) {
                            createErrorNode(baseType, "A user defined type cannot have the same name as its base type.");
                            retval = false;
                        }
                    }
                }    
            } else {
                createErrorNode(baseType, "Unsupported base type: " + s + ".");
                retval = false;
            }
        }
        return retval;
        
    }   
    
    public static boolean validateSimpleInitialValue(Element initialValue, String targetTypeName, Project proj) {
        
        InitialValueParser initialValueParser = proj.getInitialValueParser();
        initialValueParser.clear();
        String value = initialValue.getAttribute("value");
        boolean retval = initialValueParser.parseExpr(value);
        if (!retval) {
            createErrorNode(initialValue, initialValueParser.getErrorMessage());            
        } else {
            String baseTypeName = targetTypeName.trim().toUpperCase(Locale.ENGLISH);
            DataTypeHandler dataTypeHandler = proj.getDataTypeHandler();
            if (dataTypeHandler.isUserType(baseTypeName)) {
                baseTypeName = dataTypeHandler.getBaseType(baseTypeName);
            }    
            if (!baseTypeName.equals("")) {
                if (initialValueParser.hasExternalDependencies()) {
                    //This can happen when validating the initial value of a
                    //variable and not of a data type.
                    createInfoNode(initialValue,"");
                } else {    
                    String javaValue = initialValueParser.makeJavaExp(baseTypeName);
                    if (javaValue == null) {
                        createErrorNode(initialValue, "The initial value \"" + initialValueParser.getValue() + "\" is not appropriate for data type " + baseTypeName);
                        retval = false;                    
                    } else {
                        createInfoNode(initialValue, javaValue);
                    }
                }    
            }
            // No else. if baseTypeName.equals("") then some anchestor of
            //targetTypeName is not a well-defined data type. We can't check
            //suitability of initial value and we don't need to report the 
            //error because it will be reported by validateBaseType.
        }
        return retval;
        
    }
    
    public static boolean validatePouTypeName(Element pouType, Project proj) {
        
        boolean retval = true;
        
        String s = pouType.getAttribute("name").trim();
        if(s.equals("")){
            createErrorNode(pouType,"The data type name cannot be empty.");
            retval = false;
            
        } else if (!IECUtilities.isValidIdentifier(s)) {
            createErrorNode(pouType,"The data type name must be a valid identifier.");
            retval = false;
        } else if (CommonLexer.isKeyword(s)) {
            createErrorNode(pouType,"The data type name cannot be a reserved word.");
            retval = false;
        } else if (IECUtilities.isStandardPouName(s)){
            createErrorNode(pouType,"A data type cannot have the same name as a standard POU.");
            retval = false;
        } else if (proj.getPouTypeHandler().isUserPou(s)) {
            createErrorNode(pouType,"A data type cannot have the same name as a user dedined POU.");
            retval = false;
        } else if (!proj.getDataTypeHandler().isUniqueTypeName(s)) {
            createErrorNode(pouType,"The data type name is not unique.");
            retval = false;
        }
        
        return retval;
        
    }
    
    public static boolean validateFunctionReturnType(Element returnType, Project proj) {
    
        boolean retval = true;
                
        return retval;
    
    }
   
    public static boolean validateVariableName(Element var, VarListHandler interfaceHandler, Project proj) {
        
        boolean retval = true;
        
        return retval;
        
    }
}
