/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iechal;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Christoforos
 */
public class PLCCompiler {
     
    private Project project;
    private boolean generateCode = true;    
    private String indent = UIelements.indent;
    
    public PLCCompiler(Project project) {
        this.project = project;
    }
             
    public void compile() {
        
        boolean ok = true;
        
        UIelements.println("Compilation of current project started.");
        ConfigurationHandler configurationHandler = project.getConfigurationHandler();
        if (configurationHandler.getNumOfConfigurations() == 0) {            
            UIelements.print("--> No configurations found in the current project.");            
            generateCode = false;
        } else if (configurationHandler.getActiveConfiguration() == null) {
            UIelements.print("--> More than one configurations found. One of them must be set active.");
            generateCode = false;
        } else if (configurationHandler.getController() == null) {
            UIelements.print("--> More than one program instances found. One of them must be selected as controller.");
            generateCode = false;
        }        
        if (! generateCode) {
            UIelements.println(" No code will be generated.");
        }
         
        ok = compileAllPous();
        if (generateCode) {
            ok = compileConfiguration() && ok;
            UIelements.print("Compilation completed");
        } else {
            UIelements.print("Syntax check completed");
        }    
        if (!ok) {
            UIelements.print(" with errors");
        }
        UIelements.println(".");
    }
    
    private boolean compileAllPous() {
        
        boolean ok = true;
        PouTypeHandler pouTypeHandler = project.getPouTypeHandler();
        if (generateCode) {
            UIelements.print("Compiling ");
        } else {
            UIelements.print("Checking syntax of ");
        }
        UIelements.println(" POU types.");
        int n = pouTypeHandler.getNumberOfPouTypes();
        if (n == 0) {
            UIelements.println("No POUs found!");
        } else {
            if (generateCode) {
                //if temp directory does not exist, create it
                //and if it exists, empty ir.
            }
            for (int i = 0; i < n; i++) {
                Element pouType = pouTypeHandler.getPouTypeElement(i);
                ok = compilePou(pouType) && ok;
                
            }            
        }
        
        return ok;
    
    }
    
    private boolean compilePou(Element pou) {
        
        boolean ok = true;
        
        StringBuffer javaCode = new StringBuffer("public class ");
                
        String type = pou.getAttribute("pouType").trim();
        if (type.equals("functionBlock")) {
            type = "function block";
        }
        String name = pou.getAttribute("name").trim().toUpperCase(Locale.ENGLISH);
        UIelements.addLine(type + " " + name);
        if (pou.getElementsByTagName("iecHalError").getLength()>0) {
            UIelements.addLine(" --> Errors found. No compilation.");
            ok = false;
        } else {
            StringBuffer runner = new StringBuffer();
            StringBuffer debugRunner = new StringBuffer();
            project.getInitialValueParser().resetConstants();
            VarListBuilder varListBuilder = new VarListBuilder(pou);
            varListBuilder.setProject(project);            
            if (type.equals("function")) {
                ok = varListBuilder.buildFunctionInterface() && ok;
                runner.append(indent).append("public static void run() {\n");
                debugRunner.append(indent).append("public static void debugRun(boolean __step) {\n");
            } else {
                ok = varListBuilder.buildBlockInterface() && ok;
            }
            
            if (ok) {
                javaCode.append(name).append(" {\n");
          //      javaCode.append(varListBuilder.getOpeningCode());
                
                javaCode.append("}\n");
            }
        }
        
        return ok;
    }
    
    private boolean compileConfiguration() {
    
        boolean ok = true;
        
        ConfigurationHandler configurationHandler = project.getConfigurationHandler();
        configurationHandler.buildConfigurationCode();
                
        
        
        if (ok) {
            //compileConfiguration is always called with generateCode true
            StringBuffer javaCode = new StringBuffer("public class Configuration {\n");
            javaCode.append(configurationHandler.getDeclarations());
            javaCode.append("\n");
            javaCode.append(indent).append("private static void init() {\n");
            javaCode.append(configurationHandler.getInitializations());
            javaCode.append(indent).append("}\n\n");
            
        }
        return ok;
        
    }
    
    private void generateFiles() {
        
    }

    
    
    

    public static void main(String[] args) {
    //σημειο εκκίνησης κάθε εφαρμογής, 
    //η μέθοδος main ξεκινάει την εκτέλεση της Java εφαρμογής,
    //το String[] args είναι μέθοδος
         
        try{
            
            File fXmlFile = new File("TestTypes.xml");
            //δημιουργεί ένα αντικείμενο και το εκχωρεί στο fXmlFile
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //νέα παρουσία documentBuilderFactory-στατική μέθοδος δημιουργίας νέας εμφάνισης για χρήση,
            //κλάσεων
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //δημιουργεί ένα αντικείμενο
            Document doc = dBuilder.parse(fXmlFile);
            //αναλύει το xml 
            doc.getDocumentElement().normalize();
            //βάζει όλα τα text nodes σε όλο το βάθος κάτω από αυτόν τον κόμβο
            //για παράδειγμα : ένα denormalized node (<foo> hello
            //                                        wor         
            //                                        ld</foo>) θα γίνει
            //Element foo --> Text node:""
            //                Text node:"Hello"
            //                Text node:"wor"
            //                Text node:"ld"
            //ενώ ένα normalized node:
            //Element foo --> Text node:"Hello world"
            Project project = new Project();
            project.setDom(doc);
            project.setFile(fXmlFile);
            project.init();
            PLCCompiler compiler = new PLCCompiler(project);
            compiler.compile();
            /*
            System.out.println("Root element: " +doc.getDocumentElement().getNodeName() );
            //εμφανίζει τα "" συν το document element και το node name
            */
            /*
            NodeList nList = doc.getElementsByTagName("types");
            //επιστρέφει ένα nodelist
            //εαν θέλω το πρώτο Node -->doc.getElementsByTagName("types").item(0);
            //εαν θέλω και τιμή --> doc.getElementsByTagName("types").item(0).getTextContent();
            System.out.println("------------------------------");
            //εμφανίζει -------------------------
            Element eTypes = (Element)nList.item(0);
            
            nList = eTypes.getElementsByTagName("dataTypes");
            Element eDataTypes = (Element)nList.item(0);
            nList = eDataTypes.getElementsByTagName("dataType");
            
            for (int i=0; i<nList.getLength(); i++){
                Node nNode = nList.item(i);
                System.out.println("\nCurrent element: " + nNode.getNodeName() );
                
                
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    
                    Element eElement =(Element) nNode;
                    String sname = eElement.getAttribute("name");
                    
                    System.out.println("Data Type Name: " + sname );
                    System.out.println("Base Type: " + compiler.dataTypeHandler.getBaseType(sname));
                    //System.out.println("Initial Value: " + eElement.getElementsByTagName("initialValue").item(0) );
                    System.out.println("Initial Value: " + compiler.dataTypeHandler.getInitialValue(sname) );
                }
            } */
        }catch (IOException | ParserConfigurationException | DOMException | SAXException e){
        }
    }    
}
