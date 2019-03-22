/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;

/**
 *
 * @author Christoforos
 */
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
        
public class Project {
 
    private Document dom;
    private boolean bsaved = true;
    private File f = null;
    private DataTypeHandler dataTypeHandler;
    private PouTypeHandler pouTypeHandler;
    private InitialValueParser initialValueParser;
    private ConfigurationHandler configurationHandler;
    
    public String getName() {
        Element elem = (Element)dom.getDocumentElement().getElementsByTagName("contentHeader").item(0);
        return elem.getAttribute("name");
    }
    
    public void setName(String name) {
        try {
            Element elem = (Element)dom.getDocumentElement().getElementsByTagName("contentHeader").item(0);
            elem.setAttribute("name", name);
        } catch (DOMException e) {
            UIelements.reportException(e);
        }
    }
    
    public Document getDom() {
        return dom;
    }
    
    public void setDom(Document dom){
        this.dom=dom;
    }
    
    public boolean getSaved() {
        return bsaved;
    }
    
    public void setSaved(boolean b) {
        bsaved=b;
    }
    
    public File getFile() {
        return f;
    }
    
    public void setFile(File f) {
        this.f = f;
    }
    
    public DataTypeHandler getDataTypeHandler() {
        return dataTypeHandler;
    }
    
    public PouTypeHandler getPouTypeHandler() {
        return pouTypeHandler;
    }
    
    public InitialValueParser getInitialValueParser() {
        return initialValueParser;
    }
    
    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;    
    }
    
    public void init() {
        Element proj = dom.getDocumentElement();
        dataTypeHandler = new DataTypeHandler();
        dataTypeHandler.refresh(proj);
        pouTypeHandler = new PouTypeHandler();
        pouTypeHandler.refresh(proj);
        initialValueParser = new InitialValueParser();
        configurationHandler = new ConfigurationHandler(this);
        configurationHandler.init();
    }
    
    public void createDom(String name) {    

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            dom = dBuilder.newDocument();
             
            //Create root Element
            Element rootElement = dom.createElement("project");
            dom.appendChild(rootElement);
            
            //Setting attribute to project
            Attr attr = dom.createAttribute("xmlns");
            attr.setValue("http://www.plcopen.org/xml/tc6_0201");
            rootElement.setAttributeNode(attr);
            
            //Create Branch fileHeader Element
            Element fileHeader = dom.createElement("fileHeader");
            rootElement.appendChild(fileHeader);
             
            //setting attributes to fileHeader
            attr = dom.createAttribute("companyName");
            attr.setValue("");
            fileHeader.setAttributeNode(attr);
            
            //attr = dom.createAttribute("companyURL");
            //attr.setValue("");
            //fileHeader.setAttributeNode(attr);
            
            attr = dom.createAttribute("productName");
            attr.setValue("");
            fileHeader.setAttributeNode(attr);
            
            attr = dom.createAttribute("productVersion");
            attr.setValue("");
            fileHeader.setAttributeNode(attr);
            
            attr = dom.createAttribute("productRelease");
            attr.setValue("");
            fileHeader.setAttributeNode(attr);
            
            attr = dom.createAttribute("creationDateTime");
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = new Date();

            attr.setValue(dateFormat.format(date));
            fileHeader.setAttributeNode(attr);
            
            attr = dom.createAttribute("contentDescription");
            attr.setValue("");
            fileHeader.setAttributeNode(attr);
            
            //Create Branch contentHeader Element
            Element contentHeader = dom.createElement("contentHeader");
            rootElement.appendChild(contentHeader);
            
            //Setting attribute to contentHeader
            
            attr = dom.createAttribute("name");
            attr.setValue(name);
            contentHeader.setAttributeNode(attr);
            
            //Elements of contentHeader
            
            Element coordinateInfo = dom.createElement("coordinateInfo");
            contentHeader.appendChild(coordinateInfo);

            Element fbd = dom.createElement("fbd");
            coordinateInfo.appendChild(fbd);

            Element scaling =dom.createElement("scaling");
            Attr x = dom.createAttribute("x");
            x.setValue("1");
            scaling.setAttributeNode(x);
            Attr y = dom.createAttribute("y");
            y.setValue("1");
            scaling.setAttributeNode(y);
            fbd.appendChild(scaling);
            
            Element ld = dom.createElement("ld");
            coordinateInfo.appendChild(ld);

            scaling =dom.createElement("scaling");
            x = dom.createAttribute("x");
            x.setValue("1");
            scaling.setAttributeNode(x);
            y = dom.createAttribute("y");
            y.setValue("1");
            scaling.setAttributeNode(y);
            ld.appendChild(scaling);
            
            
            Element sfc = dom.createElement("sfc");
            coordinateInfo.appendChild(sfc);
            
            scaling =dom.createElement("scaling");
            x = dom.createAttribute("x");
            x.setValue("1");
            scaling.setAttributeNode(x);
            y = dom.createAttribute("y");
            y.setValue("1");
            scaling.setAttributeNode(y);
            sfc.appendChild(scaling);
            
            //Create Branch types Element
            Element types = dom.createElement("types");
            rootElement.appendChild(types);
            
            //Create Elements of types
            Element dataTypes = dom.createElement("dataTypes");
            types.appendChild(dataTypes);
            
            Element pous = dom.createElement("pous");
            types.appendChild(pous);
             
            //Create Branch instances Element
            Element instances = dom.createElement("instances");
            rootElement.appendChild(instances);
            
            //Create Element of instances
            Element configurations = dom.createElement("configurations");
            instances.appendChild(configurations);
                                     
        } catch (Exception ex) {
            UIelements.reportException(ex);
        }           
        
    }


    
}
