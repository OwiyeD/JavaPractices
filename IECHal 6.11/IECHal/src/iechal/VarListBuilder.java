/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 * @author Christoforos
 */
public class VarListBuilder extends VarListHandler {
        
    private StringBuffer declarations;
    private StringBuffer constructor;
    private StringBuffer initializer;
    
    private Project project;
    
    public void setProject(Project project) {
        this.project = project;
    }    
    
    public VarListBuilder(Element container) {
        //The container can be either a pou element or a 
        //configuration element containing global variables).
        super(container);
        declarations = new StringBuffer();
        constructor = new StringBuffer();
    }
    
    public String getDeclarations() {
        return declarations.toString();
    }
    
    public String getConstructor() {
        return constructor.toString();
    }
            
    public String getInitializer() {
        return initializer.toString();
    }
    
    private String getSimpleVariableInitialValue(Element initialValueElement, String iecType) {
    
        String retVal = null;
                
        //At validation phase we must have checked that the initialValue element contains a simpleValue element,
        //which has the attribute value, which is a constant expression of the appropriate data type.
        //If all is well, at validation phase we must have added an iecHalInfo element under simpleValue,
        //whose "value" attribute is the java expression for the initial value. This is the only
        //iecHalInfo element in the subtree rooted at initialValue
        NodeList tempList = initialValueElement.getElementsByTagName("iecHalInfo");
        //We don't check if tempList contains elements. If it doesn't contain then we should have detected some
        //error in the validation phase and the compile command would not be executed.
        retVal = ((Element)tempList.item(0)).getAttribute("value");
        if (retVal.equals("")) {
            //The initial value could not be calculated at validation phase because it depends on external constants
            if (project.getConfigurationHandler().getActiveConfiguration() != null) {
                Element simpleValueElement = XMLUtilities.getSingleChildElement(initialValueElement);
                InitialValueParser initialValueParser = project.getInitialValueParser();
                initialValueParser.clear();
                String expr = simpleValueElement.getAttribute("value");    
                if (initialValueParser.parseExpr(expr)) {
                    //Everything OK
                    retVal = initialValueParser.makeJavaExp(iecType);
                } else {
                    retVal = null;
                }
            } 
        }
        return retVal;
    } 
    
    private boolean generateBlockMemberInitializers(Element structValue, StringBuffer initializations, String namePrefix) {
        
        boolean retVal = true;
        String indent = UIelements.indent;
        
        ArrayList<Element> memberList = XMLUtilities.getChildrenElements(structValue);
        Iterator<Element> iter = memberList.iterator();
        while (iter.hasNext()) {
            Element valueElement = iter.next();
            String memberName = valueElement.getAttribute("member").trim().toUpperCase(Locale.ENGLISH);
            String variableName = namePrefix + "." + memberName;
            Element var = getVariableElementByName(memberName);
            String variableDataType = getVariableDataType(var).trim().toUpperCase(Locale.ENGLISH);
            PouTypeHandler pouTypeHandler = project.getPouTypeHandler();
            if (pouTypeHandler.isFBType(variableDataType)) {
                Element structValueElement = XMLUtilities.getSingleChildElement(valueElement);
                NodeList tempList = structValueElement.getElementsByTagName("simpleValue");
                if (tempList.getLength() > 0) {                    
                    String vType = getVariableType(var);
                    if (vType.equals("inputVars")) {
                        initializations.append(indent).append(indent).append(variableName).append(" = new ").append(variableDataType).append("();\n");
                    }
                    if (IECUtilities.isStandardFunctionBlockName(variableDataType)) {
                        retVal = generateStandardBlockInitializer(variableDataType,structValueElement,initializations, variableName) && retVal;
                    } else {
                        Element pouElement = pouTypeHandler.getPouTypeElementByName(variableDataType);
                        VarListBuilder interfaceBuilder = new VarListBuilder(pouElement);
                        retVal = interfaceBuilder.generateBlockMemberInitializers(structValueElement,initializations, variableName) && retVal;
                    }                    
                }
            } else {
                DataTypeHandler dataTypeHandler = project.getDataTypeHandler();
                if (dataTypeHandler.isUserType(variableDataType)) {
                    variableDataType = dataTypeHandler.getBaseType(variableDataType);
                }                
                String initialValue = getSimpleVariableInitialValue(valueElement,variableDataType);
                if (initialValue == null) {       
                    UIelements.addLine("Initial value error for variable \""+variableName+"\": "+project.getInitialValueParser().getErrorMessage());
                    initialValue = ""; //So that we don't get a null pointer exception in subsequent code using initial value.
                    retVal = false;
                }
                String address = var.getAttribute("address");
                if (getVariableType(var).equals("localVars") && (!address.equals(""))) {
                    //located variable with partial address (the only possibility in a function block
                    char areaPrefix = address.charAt(1);
                    if (variableDataType.equals("BOOL")) {
                        String byteIndex = namePrefix + "." + "__byte_addr_" + memberName;
                        String bitIndex = namePrefix + "." + "__byte_addr_" + memberName;
                        initializations.append(indent).append(indent).append("Configuration.putBit(").append(ConfigurationHandler.byteArrayName(areaPrefix))
                                .append(",").append(byteIndex).append(",").append(bitIndex).append(",").append(initialValue).append(");\n");
                    } else {
                        String byteIndex = namePrefix + "." + "__addr_" + memberName;
                        initializations.append(indent).append(indent).append("Configuration.").append(IECUtilities.getJavaType(variableDataType)).append("2bytes(")
                                .append(ConfigurationHandler.byteArrayName(areaPrefix)).append(",").append(byteIndex).append(",").append(initialValue).append(");\n");
                    }
                } else {
                    initializations.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                }    
            }
        }
                
        return retVal;
    
    }
    
    private void generateGlobalBlockMemberInitializers(Element structValue, StringBuffer initializations, String namePrefix) {
        
        String indent = UIelements.indent;
        
        ArrayList<Element> memberList = XMLUtilities.getChildrenElements(structValue);
        Iterator<Element> iter = memberList.iterator();
        while (iter.hasNext()) {
            Element valueElement = iter.next();
            String memberName = valueElement.getAttribute("member").trim().toUpperCase(Locale.ENGLISH);
            String variableName = namePrefix + "." + memberName;
            Element var = getVariableElementByName(memberName);
            String variableDataType = getVariableDataType(var).trim().toUpperCase(Locale.ENGLISH);
            PouTypeHandler pouTypeHandler = project.getPouTypeHandler();
            if (pouTypeHandler.isFBType(variableDataType)) {
                Element structValueElement = XMLUtilities.getSingleChildElement(valueElement);
                NodeList tempList = structValueElement.getElementsByTagName("simpleValue");
                if (tempList.getLength() > 0) {                
                    String vType = getVariableType(var);
                    if (vType.equals("inputVars")) {
                        initializations.append(indent).append(variableName).append(" = new ").append(variableDataType).append("();\n");
                    }
                    if (IECUtilities.isStandardFunctionBlockName(variableDataType)) {
                        generateStandardBlockInitializer(variableDataType,structValueElement,initializations, variableName);
                        //We don't check if the function returns true. Since there are no external dependencies in the case of global variables,
                        //all mistakes in the initial values will have been detected at validation phase. If there were any such mistakes, then
                        //we wouldn't be doing compilation.
                    } else {
                        Element pouElement = pouTypeHandler.getPouTypeElementByName(variableDataType);
                        VarListBuilder varListBuilder = new VarListBuilder(pouElement);
                        varListBuilder.generateGlobalBlockMemberInitializers(structValueElement,initializations, variableName);
                    }
                }
            } else {
                DataTypeHandler dataTypeHandler = project.getDataTypeHandler();
                if (dataTypeHandler.isUserType(variableDataType)) {
                    variableDataType = dataTypeHandler.getBaseType(variableDataType);
                }
                NodeList tempList = valueElement.getElementsByTagName("iecHalInfo");
                //We don't check if tempList contains elements. If it doesn't contain then we should have detected some
                //error in the validation phase and the compile command would not be executed.
                String initialValue = ((Element)tempList.item(0)).getAttribute("value");
                //We don't check if initial value is empty because this can't happen since there are no external dependencies in the case of global variables.
                initializations.append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
            }
        }        
    }
    
    private static boolean generateStandardBlockInitializer(String name, Element structElement, StringBuffer initializations, String namePrefix) {
    
        return true;
        
    }
    
    public boolean buildFunctionInterface() {
        
        boolean retVal = true;
        
        String indent = UIelements.indent;
        DataTypeHandler dataTypeHandler = project.getDataTypeHandler();                
        
        initializer.append(indent).append("public static void init() {\n");
        
        declarations.append(indent).append("public static long __pc;\n");
        initializer.append(indent).append(indent).append("__pc = -1;\n");
        
        String javaType = "";
        String initialValue = "";
        String iecType = PouTypeHandler.findReturnType(container);
        if (!(iecType==null)) {
            String functionName = container.getAttribute("name");
            initialValue = dataTypeHandler.getSimpleInitialValue(iecType);
            if (dataTypeHandler.isUserType(iecType)) {
                iecType = dataTypeHandler.getBaseType(iecType);
            }
            javaType = IECUtilities.getJavaType(iecType);
            declarations.append(indent).append("public static ").append(javaType).append(" ").append(functionName).append(";\n");
            initializer.append(indent).append(indent).append(functionName).append("=").append(initialValue).append(";\n");
        }
        
        int n = getNumberOfVariables();
        boolean enFound = false;
        boolean enoFound = false;
        
        for (int i = 0; i < n; i++) {
            Element var = getVariableElement(i);
            //get name of parent element
            String vType =  getVariableType(var);
            String variableName = var.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            String variableDataType = getVariableDataType(var);
            String defaultInitialValue = dataTypeHandler.getSimpleInitialValue(variableDataType);
            if (variableName.equals("EN") || variableName.equals("ENO")) {
                //The default initial value for the data type boolean is false
                //This should not be the case for the special variables EN and ENO
                defaultInitialValue = "true"; 
            }
            iecType = variableDataType;
            if (dataTypeHandler.isUserType(iecType)) {
                iecType = dataTypeHandler.getBaseType(iecType);
            }
            javaType = IECUtilities.getJavaType(iecType);
            Element initialValueElement = XMLUtilities.findChildElement(var, "initialValue");
            if (initialValueElement == null) {
                initialValue = defaultInitialValue;    
            } else {
                initialValue = getSimpleVariableInitialValue(initialValueElement, iecType);
                if (initialValue == null) {
                    //The initial value conatins some error that could not have been detected at validation phase e.g. value out of limits in an
                    //expression involving external constants. Note that at this phase we cannot have external dependencies (external constants
                    //with unknown values) because we are compiling within the framework of a particular configuration.
                    UIelements.addLine("Initial value error for variable \""+variableName+"\": "+project.getInitialValueParser().getErrorMessage());
                    initialValue = ""; //So that we don't get a null pointer exception in subsequent code using initial value.
                    retVal = false;
                } 
            }
            switch (vType) {
                case "inputVars":
                    declarations.append(indent).append("public static ").append(javaType).append(" ").append(variableName).append(";\n");
                    initializer.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                    if (variableName.equals("EN")) {
                        enFound = true;
                    }                   
                    break;
                case "outputVars":
                    declarations.append(indent).append("public static ").append(javaType).append(" ").append(variableName).append(";\n");
                    initializer.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                    if (variableName.equals("ENO")) {
                        enoFound = true;
                    }
                    break;
                case "inOutVars":
                    declarations.append(indent).append("public static ").append(javaType).append(" ").append(variableName).append(";\n");
                    break;
                case "localVars":
                    boolean bConstant = isConstant(var);
                    declarations.append(indent).append("private static ");
                    if (bConstant) {
                        declarations.append("final ");
                    }
                    declarations.append(javaType).append(" ").append(variableName);
                    if (bConstant) {
                        declarations.append(" = ").append(initialValue);
                        project.getInitialValueParser().addConstant(variableName, iecType, initialValue);
                    } else {
                        initializer.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                    }
                    declarations.append(";\n");                    
                    break;
                case "tempVars":
                    declarations.append(indent).append("private static ").append(javaType).append(" ").append(variableName).append(";\n");
                    initializer.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                    break;
                case "externalVars":
                    ConfigurationHandler configurationHandler = project.getConfigurationHandler();
                    if (configurationHandler.getActiveConfiguration() != null) {
                        Element global = configurationHandler.getGlobalVariable(variableName);
                        if (global == null) {
                            UIelements.addLine("External variable \""+variableName+"\": No such global variable in the active configuration.");
                            retVal = false;
                        } else if (!variableDataType.equals(getVariableDataType(global))) {
                            UIelements.addLine("External variable \""+variableName+"\": The corresponding global variable has a different data type.");
                            retVal = false;
                        } else if (isConstant(var)) {
                            if (isConstant(global)) {
                                //Consider the variable as a known external constant                                
                                project.getInitialValueParser().addConstant(variableName, iecType, initialValue);
                            }
                        } else if (isConstant(global)) {
                            UIelements.addLine("The external variable \"" + variableName + " is not declared constant but the corresponding global variable is a constant.");
                            retVal = false;
                        }
                    }                    
            }//switch
        }//for                                          
         
        if (!enFound) {
            declarations.append(indent).append("public static boolean EN;\n");
            initializer.append(indent).append(indent).append("EN=true;\n");
        }
        if (!enoFound) {
            declarations.append(indent).append("public static boolean ENO;\n");
            initializer.append(indent).append(indent).append("ENO=true;\n");
        }
        
        initializer.append(indent).append("}\n");
                             
        return retVal ;
        
    }
    
    public boolean buildBlockInterface(){
        
        boolean retVal = true;
        String indent = UIelements.indent;
        
        DataTypeHandler dataTypeHandler = project.getDataTypeHandler();                
        
        StringBuffer functionBlockInitialization = new StringBuffer();
                
        constructor.append(indent).append("public ").append(container.getAttribute("name")).append("() {\n");
        initializer.append(indent).append("public void init() {\n");
        
        declarations.append(indent).append("public long __pc;\n");
        initializer.append(indent).append(indent).append("__pc = -1;\n");
        
        int n = getNumberOfVariables();
        boolean enFound = false;
        boolean enoFound = false;

        for (int i = 0; i < n; i++) {
            //add each variable in opening line and/or declarations as appropriate
            Element var = (Element)variables.item(i);
            //get parent element
            String vType =  getVariableType(var);
            String variableName = var.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            String variableDataType = getVariableDataType(var);
            boolean isFBType = project.getPouTypeHandler().isFBType(variableDataType); 
            boolean initialValueFound = false;
            String defaultInitialValue = "";
            String initialValue = "";
            String iecType = "";
            String javaType = "";
            if (!isFBType) {    
                defaultInitialValue = dataTypeHandler.getSimpleInitialValue(variableDataType);
                if (variableName.equals("EN") || variableName.equals("ENO")) {
                    //The default initial value for the data type boolean is false
                    //This should not be the case for the special variables EN and ENO
                    defaultInitialValue = "true";
                }               
                iecType = variableDataType;
                if (dataTypeHandler.isUserType(iecType)) {
                   iecType = dataTypeHandler.getBaseType(iecType);
                }
                javaType = IECUtilities.getJavaType(iecType);
                Element initialValueElement = XMLUtilities.findChildElement(var, "initialValue");
                if (initialValueElement == null) {
                    initialValue = defaultInitialValue;    
                } else {
                    initialValue = getSimpleVariableInitialValue(initialValueElement, iecType);
                    if (initialValue != null) {
                        initialValueFound = true;
                    } else {    
                        UIelements.addLine("Initial value error for variable \""+variableName+"\": "+project.getInitialValueParser().getErrorMessage());
                        initialValue = ""; //So that we don't get a null pointer exception in subsequent code using initial value.
                        retVal = false;
                    }
                }     
            } else {                
                javaType = variableDataType.trim().toUpperCase(Locale.ENGLISH);
                functionBlockInitialization = new StringBuffer();
                Element initialValueElement = XMLUtilities.findChildElement(var, "initialValue");
                if (initialValueElement != null) {
                    //At validation phase we have checked that initialValue contains a single structValue element, which may contain
                    //0 or more value elements.
                    Element structValueElement = XMLUtilities.getSingleChildElement(initialValueElement);
                    if (IECUtilities.isStandardFunctionBlockName(variableDataType)) {
                        retVal = generateStandardBlockInitializer(variableDataType,structValueElement,functionBlockInitialization, variableName) && retVal;
                    } else {
                        Element pouElement = project.getPouTypeHandler().getPouTypeElementByName(variableDataType);
                        VarListBuilder varListBuilder = new VarListBuilder(pouElement);
                        retVal = varListBuilder.generateBlockMemberInitializers(structValueElement,functionBlockInitialization,variableName) && retVal;
                    }    
                }
            }
            switch (vType) {
                case "inputVars":
                    declarations.append(indent).append("public ").append(javaType).append(" ").append(variableName).append(";\n");
                    if (!isFBType) {
                        constructor.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                        if (variableName.equals("EN")) {
                            enFound = true;
                        }
                    } else {
                        if (functionBlockInitialization.length() > 0) {
                            constructor.append(indent).append(indent).append(variableName).append(" = new ").append(javaType).append("();\n");
                            constructor.append(functionBlockInitialization);
                        }
                    }
                    break;
                case "outputVars":
                    declarations.append(indent).append("public ").append(javaType).append(" ").append(variableName).append(";\n");
                    constructor.append(indent).append(indent).append(variableName).append(" = ");
                    if (!isFBType) {
                        constructor.append(initialValue).append(";\n");
                        if (variableName.equals("ENO")) {
                            enoFound = true;
                        }
                    } else {
                        constructor.append("new ").append(javaType).append("();\n");
                        constructor.append(functionBlockInitialization);
                    }
                    break;
                case "inOutVars":
                    declarations.append(indent).append("public ").append(javaType).append(" ").append(variableName).append(";\n");
                    break;
                case "localVars":
                    String address = var.getAttribute("address").trim().toUpperCase(Locale.ENGLISH);
                    if (address.length() > 0) {
                        //Located variable
                        char areaPrefix = address.charAt(1);
                        if (address.charAt(2) == '*') {
                            //Partial address specification.
                            if (javaType.equals("boolean")) {
                                declarations.append(indent).append("public int __byte_addr_").append(variableName).append(";\n");
                                declarations.append(indent).append("public int __bit_addr_").append(variableName).append(";\n");
                            } else {
                                declarations.append(indent).append("public int __addr_").append(variableName).append(";\n");                            
                            }                            
                        } else {
                            //Full address specification. Only possible in program POUs.                   
                            int byteOffset = 0;
                            int [] addressOffset = null;
                            if (javaType.equals("boolean")) {
                                addressOffset = ConfigurationHandler.bitAddressOffset(address);
                                byteOffset = addressOffset[0];
                            } else {
                                byteOffset = Integer.parseInt(address.substring(3));          
                            }                            
                            ConfigurationHandler configurationHandler = project.getConfigurationHandler();                            
                            if (configurationHandler.getActiveConfiguration() != null) {
                                //Check if valid address
                                if (!configurationHandler.addressExists(areaPrefix,byteOffset + NumUtilities.byteSize(javaType)-1)) {
                                    UIelements.addLine("Illegal address \"" + address + "\" for variable \""+variableName+"\": Memory index out of bounds.");
                                    initialValue = ""; //So that we don't get a null pointer exception in subsequent code using initial value.
                                    retVal = false;
                                }
                            }
                            if (initialValueFound) {
                                //put initialization statement in the constructor                                
                                if (javaType.equals("boolean")) {
                                    constructor.append(indent).append(indent).append("Configuration.putBit(").append(ConfigurationHandler.byteArrayName(areaPrefix))
                                            .append(",").append(byteOffset).append(",").append(addressOffset[1]).append(",").append(initialValue).append(");\n");
                                } else {
                                    constructor.append(indent).append(indent).append("Configuration.").append(javaType).append("2bytes(")
                                            .append(ConfigurationHandler.byteArrayName(areaPrefix)).append(",").append(byteOffset).append(",")
                                            .append(initialValue).append(");\n");;
                                }
                            }
                        }
                    } else {               
                        declarations.append(indent).append("public ").append(javaType).append(" ").append(variableName).append(";\n");
                        constructor.append(indent).append(indent).append(variableName).append(" = ");
                        if (!isFBType) {
                            constructor.append(initialValue).append(";\n");
                        } else {
                            constructor.append("new ").append(javaType).append("();\n");
                            constructor.append(functionBlockInitialization);
                        }
                    }                            
                    if (isConstant(var)) {
                        //Can't be of FB type, but it may be a located variable with a full address (in case of programs only).
                        //Add to the known constants, in case it will be needed for the calculation of the initial value of another local constant
                        //which depends on external constants (if it doesn't depend on local constants then the calculation will have been done
                        //at validation phase.
                        project.getInitialValueParser().addConstant(variableName, iecType, initialValue);
                    }
                    break;
                case "tempVars":
                    declarations.append(indent).append(indent).append(variableDataType).append(" ").append(variableName).append(";\n");
                    //Temporary variables cannot be of FB type
                    initializer.append(indent).append(indent).append(variableName).append(" = ").append(initialValue).append(";\n");
                    break;
                case "externalVars":
                    ConfigurationHandler configurationHandler = project.getConfigurationHandler();
                    if (configurationHandler.getActiveConfiguration() != null) {
                        Element global = configurationHandler.getGlobalVariable(variableName);
                        if (global == null) {
                            UIelements.addLine("External variable \""+variableName+"\": No such global variable in the active configuration.");
                            retVal = false;
                        } else if (!variableDataType.equals(getVariableDataType(global))) {
                            UIelements.addLine("External variable \""+variableName+"\": The corresponding global variable has a different data type.");
                            retVal = false;
                        } else if (isConstant(var)) {
                            //If it is constant then it can't be of FB type
                            if (isConstant(global)) {
                                //Consider the variable as a known external constant
                                project.getInitialValueParser().addConstant(variableName, iecType, initialValue);
                            }
                        } else if (isConstant(global)) {
                            UIelements.addLine("The external variable \"" + variableName + " is not declared constant but the corresponding global variable is a constant.");
                            retVal = false;
                        }
                    }
            }//switch
        }//for
        
        if (!enFound) {
            declarations.append(indent).append("public boolean EN;\n");
            initializer.append(indent).append(indent).append("EN=true;\n");
        }
        if (!enoFound) {
            declarations.append(indent).append("public boolean ENO;\n");
            initializer.append(indent).append(indent).append("ENO=true;\n");
        }
        
        constructor.append(indent).append("}\n");
        initializer.append(indent).append("}\n");
                
        return retVal;
    }    
    
    public boolean buildGlobalVariables() {
        
        boolean retval = true;
        String indent = UIelements.indent;
        
        DataTypeHandler dataTypeHandler = project.getDataTypeHandler();
        int n = getNumberOfVariables();
        
        for (int i = 0; i < n; i++) {
            Element var = getVariableElement(i);
            String variableName = var.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            String variableDataType = getVariableDataType(var);
            boolean isFBType = project.getPouTypeHandler().isFBType(variableDataType); 
            boolean initialValueFound = false;
            String defaultInitialValue = "";
            String initialValue = "";
            String iecType = "";
            String javaType = "";
            Element initialValueElement = XMLUtilities.findChildElement(var, "initialValue");
            if (!isFBType) {    
                defaultInitialValue = dataTypeHandler.getSimpleInitialValue(variableDataType);                               
                iecType = variableDataType;
                if (dataTypeHandler.isUserType(iecType)) {
                   iecType = dataTypeHandler.getBaseType(iecType);
                }
                javaType = IECUtilities.getJavaType(iecType);                
                if (initialValueElement == null) {
                    initialValue = defaultInitialValue;    
                } else {
                    NodeList tempList = initialValueElement.getElementsByTagName("iecHalInfo");
                    //We don't check if tempList contains elements. If it doesn't contain then we should have detected some
                    //error in the validation phase and the compile command would not be executed.
                    initialValue = ((Element)tempList.item(0)).getAttribute("value");
                    //We don't check if initialValue is empty. This can't happen in the case of global variables
                    //because they have no external dependencies.
                    initialValueFound = true;                    
                }
                String address = var.getAttribute("address").trim().toUpperCase(Locale.ENGLISH);
                if (address.length() > 0) {
                    //Located variable with full address specification
                    char areaPrefix = address.charAt(1);
                    int byteOffset = 0;
                    int [] addressOffset = null;
                    if (javaType.equals("boolean")) {
                        addressOffset = ConfigurationHandler.bitAddressOffset(address);
                        byteOffset = addressOffset[0];
                    } else {
                        byteOffset = Integer.parseInt(address.substring(3));
                    }                                            
                    //Check if valid address
                    if (!project.getConfigurationHandler().addressExists(areaPrefix,byteOffset + NumUtilities.byteSize(javaType)-1)) {
                        UIelements.addLine("Illegal address \"" + address + "\" for global variable \""+variableName+"\": Memory index out of bounds.");
                        retval = false;                                
                    } else if (initialValueFound) {
                        //put initialization statement in the initializer                    
                        if (javaType.equals("boolean")) {
                            initializer.append(indent).append(indent).append("Configuration.putBit(").append(ConfigurationHandler.byteArrayName(areaPrefix))
                                       .append(",").append(byteOffset).append(",").append(addressOffset[1]).append(",").append(initialValue).append(");\n");
                        } else {
                            initializer.append(indent).append(indent).append("Configuration.").append(javaType).append("2bytes(")
                                       .append(ConfigurationHandler.byteArrayName(areaPrefix)).append(",").append(initialValue).append(");\n");;
                        }
                    }    
                } else {
                    declarations.append(indent).append("public static ").append(javaType).append(" ").append(variableName).append(" = ").append(initialValue).append(";\n");;
                }
            } else {
                //FB type
                javaType = variableDataType.trim().toUpperCase(Locale.ENGLISH);
                StringBuffer functionBlockInitialization = new StringBuffer();
                if (initialValueElement != null) {
                    //At validation phase we have checked that initialValue contains a single structValue element, which may contain
                    //0 or more value elements.
                    Element structValueElement = XMLUtilities.getSingleChildElement(initialValueElement);
                    if (IECUtilities.isStandardFunctionBlockName(variableDataType)) {
                        generateStandardBlockInitializer(variableDataType,structValueElement,functionBlockInitialization, variableName);
                        //We don't check the return value because in the case of global variables all errors in the initial value are detected
                        //at validation phase.
                    } else {
                        Element pouElement = project.getPouTypeHandler().getPouTypeElementByName(variableDataType);
                        VarListBuilder varListBuilder = new VarListBuilder(pouElement);
                        varListBuilder.generateGlobalBlockMemberInitializers(structValueElement,functionBlockInitialization,variableName);
                    }
                    initializer.append(functionBlockInitialization);
                }
                declarations.append(indent).append("public static ").append(javaType).append(" ").append(variableName).append(" = new ").append(javaType).append("();\n");
            }
            
        }
        
        return retval;
    }
}