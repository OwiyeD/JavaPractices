/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christoforos
 */
public class ConfigurationHandler {
    
    private Project project;
    private Element activeConfiguration;
    private Element activeResource;
    private Element controller;
    private Element process;
    
    private static final int defaultInputBytes = 100;
    private static final int defaultOutputBytes = 100;
    private static final int defaultMemoryBytes = 400;
    private static final String indent = UIelements.indent;
    
    private int numOfInputBytes = defaultInputBytes;
    private int numOfOutputBytes = defaultOutputBytes;
    private int numOfMemoryBytes = defaultMemoryBytes;
    
    private StringBuffer declarations;
    private StringBuffer initializations;
    private StringBuffer methods;
    
    private HashSet<String> partiallyLocatedInstances;
    private NodeList programInstances;
    
    public ConfigurationHandler(Project project) {
        this.project = project;
    }
    
    public void setActiveConfiguration(Element activeConfiguration) {
        this.activeConfiguration = activeConfiguration;
        initActiveConfiguration();
    }    
    
    public Element getActiveConfiguration() {
        
        return activeConfiguration;
        
    }
    
    public void setController(Element controller) {
        //Program instance inside the active resource
        this.controller = controller;    
    }
    
    public Element getController() {
        return controller;
    }
    
    public void setProcess(Element process) {
        //Program instance inside the active resource (optional)
        this.process = process;    
    }
    
    public int getNumOfInputBytes() {        
        return numOfInputBytes;        
    }
    
    public int getNumOfOutputBytes() {        
        return numOfOutputBytes;        
    }
    
    public int getNumOfMemoryBytes() {        
        return numOfMemoryBytes;
    }
    
    public String getDeclarations() {
        return declarations.toString();
    }
    
    public String getInitializations() {
        return initializations.toString();
    }
    
    public void init() {
        
        int nconf = getNumOfConfigurations();
        if (nconf == 1) {
            activeConfiguration = (Element)project.getDom().getElementsByTagName("configuration").item(0);
            initActiveConfiguration();
        } else {
            activeConfiguration = null;
            activeResource = null;
            controller = null;
            process = null;
        }
        
    }
        
    private void initActiveConfiguration() {
        
        NodeList resources = activeConfiguration.getElementsByTagName("resource");
        controller = null;
        process = null;
        if (resources.getLength() > 0) {
            activeResource = (Element)resources.item(0);
            if (getNumOfProgramInstances() == 1) {
                controller = (Element)activeResource.getElementsByTagName("pouInstance").item(0);
            }
        } else {
            activeResource = null;
        }
        readNumbersOfBytes();        
    }
    
    private void readNumbersOfBytes() {
        ArrayList<Element> numOfBytesElements = XMLUtilities.getExtensionElements(activeConfiguration, "iecHal.numOfBytes");
        Element elem;
        boolean inputsSet = false, outputsSet = false, memorySet = false;
        if (numOfBytesElements.size() > 0) {
            //It will be an error if the list contains more than one element. This can happen only when opening an XML file
            //created outside our environment. The error wiil have been reported by the validation routines and the only
            //way to fix it will be to edit the XML file outside our environment. However, since this error is not likely to
            //happen under normal usage conditions of our system, its occurence will not hinder the program operation. Instead, we will
            //take into account only the first element and ignore the rest.
            elem = numOfBytesElements.get(0);
            Element child = XMLUtilities.findChildElement(elem, "iechal.inputs");
            //The validation routine will have reported any errors in the structure of the XML file (if there are more than one iechal.inputs
            //elements, if they contain other elements, if the attribute "value" is missing, or if its content is not a positive integer). Once again,
            //since these situations cannot occur under normal usage conditions of our system, we will not write code to handle them. In the case of the
            //first error we will take into account only the first of the elements. In the case of the second one we will do nothing. In the case of
            //the third or fourth, we will just use the default values of the parameters until the user specifies new ones.
            if (child != null) {
                try {
                    numOfInputBytes = Integer.parseInt(child.getAttribute("value"));
                    if (numOfInputBytes > 0) {
                        inputsSet = true;
                    }
                } catch (NumberFormatException e) {                   
                    //No need to do anything. Error has already been reported when importing the project.
                }                
            }
            //Do the same for the elements iechal.outputs and iechal.memory (same comments as above).
            child = XMLUtilities.findChildElement(elem, "iechal.outputs");
            if (child != null) {
                try {
                    numOfOutputBytes = Integer.parseInt(child.getAttribute("value"));
                    if (numOfOutputBytes > 0) {
                        outputsSet = true;
                    }
                } catch (NumberFormatException e) {
                    //No need to do anything. Error has already been reported when importing the project.
                }
            }
            child = XMLUtilities.findChildElement(elem, "iechal.memory");
            if (child != null) {
                try {
                    numOfMemoryBytes = Integer.parseInt(child.getAttribute("value"));
                    if (numOfMemoryBytes > 0) {
                        memorySet = true;
                    }
                } catch (NumberFormatException e) {
                    //No need to do anything. Error has already been reported when importing the project.
                }                
            }
        }
        if (!inputsSet) {
            numOfInputBytes = defaultInputBytes;
        }
        if (!outputsSet) {
            numOfOutputBytes = defaultOutputBytes;
        }
        if (!memorySet) {
            numOfMemoryBytes = defaultMemoryBytes;
        }
    }
    
    public void writeNumbersOfBytes(int inputBytes, int outputBytes, int memoryBytes) {
        //See comments in getNumbersOfBytes about possible problems in the structure of an externally created XML file
        ArrayList<Element> numOfBytesElements = XMLUtilities.getExtensionElements(activeConfiguration, "iechal.numOfBytes");
        Element elem = null, newElement;
        Attr attr;
        Document dom = activeConfiguration.getOwnerDocument();
        if (numOfBytesElements.size() == 0) {
            //Element iechal.numOfBytes not found. Add it, except if all three variables have the default values.
            if (!((inputBytes == defaultInputBytes) && (outputBytes == defaultOutputBytes) && (memoryBytes == defaultMemoryBytes))) {                
                newElement = dom.createElement("addData");
                activeConfiguration.appendChild(newElement);
                elem = newElement;
                newElement = dom.createElement("data");
                attr = dom.createAttribute("name");
                attr.setValue("params");
                newElement.setAttributeNode(attr);
                attr = dom.createAttribute("handleUnknown");
                attr.setValue("implementation");
                newElement.setAttributeNode(attr);
                elem.appendChild(newElement);
                elem = newElement;
                newElement = dom.createElement("iechal.numOfBytes");
                elem.appendChild(newElement);
                elem = newElement;
            }
        } else {
            elem = numOfBytesElements.get(0);
        }   
        if (elem != null) {
            newElement = XMLUtilities.findChildElement(elem, "iechal.inputs");
            if (newElement != null) {
                newElement.setAttribute("value",Integer.toString(inputBytes));
            } else if (!(inputBytes == defaultInputBytes)) {                                
                newElement = dom.createElement("iechal.inputs");
                attr = dom.createAttribute("value");
                attr.setValue(Integer.toString(inputBytes));
                elem.appendChild(newElement);
            }
            newElement = XMLUtilities.findChildElement(elem, "iechal.outputs");
            if (newElement != null) {
                newElement.setAttribute("value",Integer.toString(outputBytes));
            } else if (!(outputBytes == defaultOutputBytes)) {                                
                newElement = dom.createElement("iechal.outputs");
                attr = dom.createAttribute("value");
                attr.setValue(Integer.toString(outputBytes));
                elem.appendChild(newElement);
            }
            newElement = XMLUtilities.findChildElement(elem, "iechal.memory");
            if (newElement != null) {
                newElement.setAttribute("value",Integer.toString(memoryBytes));
            } else if (!(memoryBytes == defaultMemoryBytes)) {                                
                newElement = dom.createElement("iechal.memory");
                attr = dom.createAttribute("value");
                attr.setValue(Integer.toString(memoryBytes));
                elem.appendChild(newElement);
            }
        }    
        numOfInputBytes = inputBytes;
        numOfOutputBytes = outputBytes;
        numOfMemoryBytes = memoryBytes;
    }
    
    /************************************************************************/
    /* The next method is used for managing the list of configurations. */
    /* Unlike DataTypeHandler and PouTypeHandler, the ConfigurationHandler  */
    /* does not keep the configurations in a NodeList. Since the list of    */
    /* configurations is not likely to be processed often, keeping it in a  */
    /* NodeList would be a waste of resources.                              */
    /************************************************************************/
    
    public int getNumOfConfigurations() {
        
        return project.getDom().getElementsByTagName("configuration").getLength();
    
    }
    
    public int getNumOfProgramInstances() {
        
        int retval = 0;
        
        if (activeResource != null) {
            retval = activeResource.getElementsByTagName("pouInstance").getLength();
        }
        return retval;
        
    }    
    
    public boolean buildConfigurationCode() {
        
        boolean ok = true;
        
        VarListBuilder varListBuilder = new VarListBuilder(activeConfiguration);
        varListBuilder.setProject(project);
        
        clearBuffers();
        
        buildByteArrayDeclarations();
        ok = varListBuilder.buildGlobalVariables();
        declarations.append(varListBuilder.getDeclarations());
        ok = buildInstanceInitializations() && ok;
        
        return ok;
    } 
    
    private void clearBuffers() {
        
        declarations = new StringBuffer();
        initializations = new StringBuffer();
        methods = new StringBuffer();
    
    }
    
    private void buildByteArrayDeclarations() {
        
        declarations.append(indent).append("public static byte[] __inputBytes = new byte["+numOfInputBytes+"];\n");
        declarations.append(indent).append("public static byte[] __outputBytes = new byte["+numOfOutputBytes+"];\n");
        declarations.append(indent).append("public static byte[] __memoryBytes = new byte["+numOfMemoryBytes+"];\n");
        
    }
    
    private Element getProgramInstanceByName(String name) {
    
        Element retval = null;
        
        String sName = name.trim().toUpperCase(Locale.ENGLISH);
        for (int i=0; i<programInstances.getLength(); i++) {
            Element programInstanceElement = (Element)programInstances.item(i);
            String instanceName = programInstanceElement.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
            if (instanceName.equals(sName)) {
                retval = programInstanceElement;
                break;
            }
        }
        
        return retval;

    }
    
    private boolean buildInstanceInitializations() {
        
        boolean ok = true;
        
        programInstances = activeResource.getElementsByTagName("pouInstance");
        populatePartiallyLocatedInstances();
        
        NodeList configVars = activeConfiguration.getElementsByTagName("configVariable");
        int n = configVars.getLength();
        
        for (int i = 0; i < n; i++) {
            Element var = (Element)configVars.item(i);            
            //At validation phase we have established that all child nodes and attributes of the configVariable element have proper values.
            //The only thing that we have not checked is whether the address (if it exists) is valid for the active configuration.
            String instancePath = var.getAttribute("instancePathAndName");
            Element initialValueElement = XMLUtilities.findChildElement(var, "initialValue");
            
            int firstPoint = instancePath.indexOf("."); //position of first point.
            int lastPoint = instancePath.lastIndexOf("."); //position of last point
            String javaPath = instancePath.substring(firstPoint + 1,lastPoint); //Strip-off resource name and variable name.
            String variableName = instancePath.substring(lastPoint+1); //Extract variable name
            String variableDataType = VarListHandler.getVariableDataType(var);
            if (!project.getPouTypeHandler().isFBType(variableDataType)) {
                DataTypeHandler dataTypeHandler = project.getDataTypeHandler();
                if (dataTypeHandler.isUserType(variableDataType)) {
                    variableDataType = dataTypeHandler.getBaseType(variableDataType);
                }
                variableDataType = IECUtilities.getJavaType(variableDataType);
                String address = var.getAttribute("address");
                if (!address.isEmpty()) {
                    char areaPrefix = address.charAt(1);
                    int byteOffset = 0;
                    if (variableDataType.equals("boolean")) {
                        int [] addressOffset = bitAddressOffset(address);
                        byteOffset = addressOffset[0];
                        initializations.append(indent).append(indent).append(javaPath).append(".__byte_addr_").append(variableName)
                                       .append(" = ").append(byteOffset);
                        initializations.append(indent).append(indent).append(javaPath).append(".__bit_addr_").append(variableName)
                                       .append(" = ").append(addressOffset[1]);
                    } else {
                        byteOffset = Integer.parseInt(address.substring(3));
                        initializations.append(indent).append(indent).append(javaPath).append(".__addr_").append(variableName)
                                       .append(" = ").append(byteOffset);
                    }
                    if (!addressExists(areaPrefix,byteOffset + NumUtilities.byteSize(variableDataType)-1)) {
                        UIelements.addLine("Illegal address assignment \"" + address + "\" for variable instance \"" + instancePath + "\": Memory index out of bounds.");
                        ok = false;
                    }
                    partiallyLocatedInstances.remove(instancePath);
                }
                
                if (initialValueElement != null) {
                
                }    
            }
        }
        if (partiallyLocatedInstances.size()>0) {
            //report error
            UIelements.addLine("No address assignment for the following instances of partially located variables:");
            Iterator<String> iter = partiallyLocatedInstances.iterator();
            while (iter.hasNext()) {
                String path = iter.next();
                UIelements.addLine("-->" + path);
            }    
            ok = false;
        }
        
        return ok;
    
    }
    
    private void populatePartiallyLocatedInstances() {
    
        partiallyLocatedInstances = new HashSet();
        ArrayList<String> names = new ArrayList();
        names.add(activeResource.getAttribute("name"));
        int n = programInstances.getLength();
        for (int i = 0; i < n; i++) {
            Element programInstance = (Element)programInstances.item(i);
            PouTypeHandler pouTypeHandler = project.getPouTypeHandler();
            Element programType = pouTypeHandler.getPouTypeElementByName(programInstance.getAttribute("typeName"));
            //At validation phase we have established that this is the name of a program type.
            names.add(programInstance.getAttribute("name"));
            addPartiallyLocatedInstancesForBlock(programType,names);
            names.remove(1);
        }
    }
    
    private void addPartiallyLocatedInstancesForBlock(Element block, ArrayList<String> names) {
        
        if (block.getAttribute("pouType").equals("functionBlock")) {
            PouTypeHandler pouTypeHandler = project.getPouTypeHandler();
            VarListHandler varListHandler = new VarListHandler(block);
            int n = varListHandler.getNumberOfVariables();
            for (int i = 0; i < n; i++) {
                Element var = varListHandler.getVariableElement(i);
                String variableDataType = VarListHandler.getVariableDataType(var);
                if (pouTypeHandler.isFBType(variableDataType)) {
                    names.add(var.getAttribute("name").trim().toUpperCase(Locale.ENGLISH));
                    addPartiallyLocatedInstancesForBlock(pouTypeHandler.getPouTypeElementByName(variableDataType), names);
                    names.remove(names.size()-1);
                } else if (VarListHandler.getVariableType(var).equals("localVars")) {
                    String address = var.getAttribute("address").trim().toUpperCase(Locale.ENGLISH);
                    if (!address.isEmpty() && (address.charAt(2) == '%')) {
                        StringBuffer tempstr = new StringBuffer(names.get(0));
                        for (int j = 1; j < names.size(); j++) {
                            tempstr.append(".").append(names.get(i));
                        }
                        partiallyLocatedInstances.add(tempstr.toString());
                    }
                }
            }
        }    
    }
    
    
    public Element getGlobalVariable(String name) {
        
        //The method can only be called when activeConfiguration is not null
        Element retval = null;
        
        NodeList templist = activeConfiguration.getElementsByTagName("variable");
        int n = templist.getLength();
        for (int i = 0; i < n; i++) {
            Element var = (Element)templist.item(i);
            if (var.getAttribute("name").equals(name)) {
                retval = var;
                break;
            }
        }        
        return retval;    
    }    
    
    public boolean addressExists(char area, int byteOffset) {
        
        boolean retval = true;
        switch (area) {
            case 'I':
                retval = (byteOffset <= numOfInputBytes);
                break;
            case 'O':
                retval = (byteOffset <= numOfOutputBytes);
                break;
            case 'M':
                retval = (byteOffset <= numOfMemoryBytes);
                break;
        }
        return retval;        
    }
    
    public static String byteArrayName(char areaCode) {
        
        String retval = "";
        
        switch(areaCode) {
            case 'I':
                retval = "__inputBytes";
                break;
            case 'O':
                retval = "__outputBytes";
                break;
            case 'M':
                retval = "__memoryBytes";
                break;
        }        
        return retval;
    }
    
    public static int[] bitAddressOffset(String address) {
    
        int [] result = new int[2];
        
        String addressOffset = address.substring(2);
        if (addressOffset.charAt(0) == 'X') {
            addressOffset = addressOffset.substring(1);
        } 
        int pointPos = addressOffset.indexOf(".");
        if (pointPos == -1) {
            int bitNumber = Integer.parseInt(addressOffset);
            result[0] = bitNumber/8;
            result[1] = bitNumber % 8;
        } else {
            result[0] = Integer.parseInt(addressOffset.substring(0,pointPos));
            result[1] = Integer.parseInt(addressOffset.substring(pointPos+1));
        }
        return result;
    }        
}
