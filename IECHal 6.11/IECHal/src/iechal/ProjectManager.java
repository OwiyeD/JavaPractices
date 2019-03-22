/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Christoforos
 */
public class ProjectManager {
    
    private static ArrayList projectList = new ArrayList();
    private static Project activeProject;    
    private static Transformer transformer = null;
        
    public static void init() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer(new StreamSource (new File("save_template.xsl")));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    }
    
    public static Project getActiveProject() {
        return activeProject;
    }

    public static void setActiveProject(Project p){
        activeProject = p;
    }
    
    public static Project addProject(String name) {
        
        Project proj = new Project();
        proj.createDom(name);
        proj.init();
        projectList.add(proj);
        return proj;
    
    }

    public static String getNewName() {
        
        int num = 1;
        String name = "Project"+num;
        int i = 1;
        while (i <= projectList.size()) {
            if (((Project)projectList.get(i-1)).getName().equals(name)) {
                i = 1;
                num = num + 1;
                name = "Project"+num;
            } else {
                i = i + 1;
            }
        }
        
        return name;
    }
    
    public static Project openProject(File f) {
        
        Project project = null;
        
        try{            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(true);
            dbFactory.setNamespaceAware(true);
            dbFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                     "http://www.w3.org/2001/XMLSchema");
            //File schemaFile = new File("tc6_xml_v201.xsd");
            //dbFactory.setAttribute( "http://java.sun.com/xml/jaxp/properties/schemaSource", "tc6_xml_v201.xsd");
            dbFactory.setAttribute( "http://java.sun.com/xml/jaxp/properties/schemaSource", "file:tc6_xml_v201.xsd");
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            XMLErrorLogger eh = new XMLErrorLogger();
            dBuilder.setErrorHandler(eh);
            Document doc = dBuilder.parse(f);
            if (eh.everythingOk()) {
               doc.getDocumentElement().normalize();
               project = new Project();
               project.setDom(doc);
               project.init();
               project.setFile(f);
               projectList.add(project);
               Validator.validateProject(project);
            } else {
                UIelements.println("Could not open XML file. Make sure that it conforms to the PLCOpen TC6 Schema definition");
                UIelements.println("and that the root element contains the namespace declaration xmlns=\"http://www.plcopen.org/xml/tc6_0201\".");
            }
        }catch (IOException | ParserConfigurationException | DOMException | SAXException e){
            UIelements.reportException(e);
        }
        return project;
    }
    
    public static Project findProject(File f) {
        
        Project proj = null;
        
        for (int i = 0; i < projectList.size(); i++) {
            Project current = (Project)projectList.get(i);
            if (f.equals(current.getFile())) {
                proj = current;
                break;
            }
        }
        return proj;
    }
    
    public static Project findProject(Document dom) {
        
        Project proj = null;
        
        for (int i = 0; i < projectList.size(); i++) {
            Project current = (Project)projectList.get(i);
            if (dom.equals(current.getDom())) {
                proj = current;
                break;
            }
        }
        return proj;
    }
    
    public static void saveActiveProject(File file) throws TransformerException {
        DOMSource domSource = new DOMSource(activeProject.getDom());
        StreamResult output = new StreamResult(file);
        transformer.transform(domSource,output);
        activeProject.setFile(file);
    }
    
    
}
