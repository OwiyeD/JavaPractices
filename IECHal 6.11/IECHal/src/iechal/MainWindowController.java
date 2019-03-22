package iechal;

import iechal.gui.DataTypeEditor;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;

import iechal.gui.DataTypeViewer;
import iechal.gui.Editor;
import iechal.gui.MemoryConfigurator;
import iechal.gui.POUTypeViewer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Christoforos
 */
public class MainWindowController implements Initializable  {    
   
    private Stage stage;
    
    @FXML private TreeView<Element> projectListView;
    @FXML private TextArea console;
    @FXML private Menu menuPLC;
    @FXML private MenuItem menuItemSetActiveConf;
    @FXML private MenuItem menuItemSetController;
    @FXML private MenuItem menuItemSetProcess;
    @FXML private MenuItem menuItemSetMemorySize;
    @FXML private SeparatorMenuItem menuSepPLC1;
    @FXML private SeparatorMenuItem menuSepPLC2;
    @FXML private ScrollPane scrollPane;
    @FXML private Button btnApply;
    @FXML private Button btnCancel;
    @FXML private Button btnClose;
    @FXML private Accordion accordion;
    
    private DataTypeViewer dataTypeViewer = new DataTypeViewer();
    private POUTypeViewer pouTypeViwer = new POUTypeViewer();
    private ArrayList<Editor> editorList = new ArrayList();
    
    //Model
    private IECHal model=null;
    public void setModel(IECHal model) {
        this.model = model;
    }
  
    //Initialize Stage
    public void setStage(Stage stage){
        this.stage=stage;
    }
    
    //Dhmiourgia New Project Click
    public void NewProjectClicked() {
        
        String name = ProjectManager.getNewName();
        Project proj = ProjectManager.addProject(name);
        SelectionModel sm = projectListView.getSelectionModel();
        TreeItem<Element> root = projectListView.getRoot();
        TreeItem projectItem = new TreeItem(proj.getDom().getDocumentElement());
        addDom(projectItem);
        root.getChildren().add(projectItem);
        ProjectManager.setActiveProject(proj);
        sm.select(projectItem);
        updateMenus();
    }
   
    //Dhmiourgia Open Click
   public void OpenClicked(){
       FileChooser fco = new FileChooser();
       fco.setTitle("Open File");
       //fco.setInitialDirectory(new File(System.getProperty("user.home")));
       fco.setInitialDirectory(new File("."));
       fco.getExtensionFilters().addAll(
           new FileChooser.ExtensionFilter("XML Documents","*.xml")
       );
       
       File file =  fco.showOpenDialog(stage);
       if (file != null) {
           SelectionModel sm = projectListView.getSelectionModel();
           TreeItem<Element> root = projectListView.getRoot();
           Project proj = ProjectManager.findProject(file);           
           if (proj == null) {
               proj = ProjectManager.openProject(file);
               if (proj != null) {                   
                   TreeItem projectItem = new TreeItem(proj.getDom().getDocumentElement());
                   addDom(projectItem);
                   root.getChildren().add(projectItem);
                   ProjectManager.setActiveProject(proj);
                   sm.select(projectItem);
                   updateMenus();
               }   
           } else {
               Element elem = proj.getDom().getDocumentElement();
               ObservableList<TreeItem<Element>> list = root.getChildren();
               for (int i = 0; i < list.size(); i++) {
                   TreeItem<Element> item = list.get(i);
                   if (elem.equals(item.getValue())) {
                       ProjectManager.setActiveProject(proj);
                       sm.select(item);
                       updateMenus();
                       break;
                   }
               }
           }
       }    
   }
   
    /*Dhmiourgia Save Click
    //public void SaveClick(){
    
      } */   
    
    
    
    //Dhmiourgia Save As Click   
    public void SaveAsClicked() throws TransformerConfigurationException, TransformerException{
        
        try {
            
            FileChooser fileToBeSavedAs = new FileChooser();
            fileToBeSavedAs.setInitialDirectory(new File("."));
            fileToBeSavedAs.setInitialFileName(ProjectManager.getActiveProject().getName());
            //https://stackoverflow.com/questions/15310393/using-filechooser-to-save-a-file-with-default-filename
            fileToBeSavedAs.setTitle("Save Project");
            fileToBeSavedAs.getExtensionFilters().addAll(
                new ExtensionFilter("XML Documents","*.xml")
            );
        
            File phil = fileToBeSavedAs.showSaveDialog(stage);
        
            if (phil!= null){
                Project proj = ProjectManager.findProject(phil);
                if ((proj != null) && (!proj.equals(ProjectManager.getActiveProject()))) {
                    Alert alert = new Alert(AlertType.ERROR);
                    Label label = new Label("Can't save the project on that file.\nIt contains a different project!");
                    label.setWrapText(true);
                    alert.getDialogPane().setContent(label);
                    alert.setTitle("Error!");
                    alert.setHeaderText("File already open!");
                    alert.showAndWait();
                } else {
                    ProjectManager.saveActiveProject(phil);
                }    
            }    
        }catch 
           (Exception e) { }
    }
      

    public void CloseApp(){
        System.exit(0);
    }
    
    public void CompileClicked() {
        PLCCompiler compiler = new PLCCompiler(ProjectManager.getActiveProject());
        compiler.compile();
    }
    
    public void setMemorySizeClicked() {
        
        ConfigurationHandler configurationHandler = ProjectManager.getActiveProject().getConfigurationHandler();
        int numOfInputBytes = configurationHandler.getNumOfInputBytes();
        int numOfOutputBytes = configurationHandler.getNumOfOutputBytes();
        int numOfMemoryBytes = configurationHandler.getNumOfMemoryBytes();
        
        MemoryConfigurator memoryConfigurator = new MemoryConfigurator();
        memoryConfigurator.configureDialog(numOfInputBytes, numOfOutputBytes, numOfMemoryBytes);
        
        Optional<ArrayList<Integer>> result = memoryConfigurator.getDialog().showAndWait();
        if (result.isPresent()) {
            ArrayList<Integer> resultList = result.get();
            numOfInputBytes = resultList.get(0).intValue();
            numOfOutputBytes = resultList.get(1).intValue();
            numOfMemoryBytes = resultList.get(2).intValue();
            configurationHandler.writeNumbersOfBytes(numOfInputBytes, numOfOutputBytes, numOfMemoryBytes);
        }
    }
    
    // Private methods
    private void initTreeView() {
        TreeItem root = new TreeItem<>(null);
        //Create the tree and hide the Root
        projectListView.setRoot(root);
        projectListView.setShowRoot(false);
        projectListView.setCellFactory(new Callback<TreeView<Element>, TreeCell<Element>>() {
            @Override
            public TreeCell<Element> call(TreeView<Element> p) {
                return new TreeCell<Element>() {
                    @Override 
                    protected void updateItem(Element item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            String name = item.getTagName();
                            String text;
                            switch(name) {
                                case "project":
                                    text = ((Element)item.getElementsByTagName("contentHeader").item(0)).getAttribute("name");                                    
                                    if (!text.equals("")) {
                                        name = text;
                                    }
                                    break;
                                case "pou":
                                    text = item.getAttribute("name");
                                    if (!text.equals("")) {
                                        name = text;
                                    }
                                    URL resource = this.getClass().getClassLoader().getResource( "iechal/gui/images/"+item.getAttribute("pouType")+".png" );
                                    if(resource != null){
                                        setGraphic(new ImageView(new Image(resource.toString())));
                                    }
                                    break;
                                case "dataType":
                                    text = item.getAttribute("name");
                                    if (!text.equals("")) {
                                        name = text;
                                    }
                                    break;
                            }
                            setText(name);
                            NodeList list = item.getElementsByTagName("iecHalError");
                            if (list.getLength()>0) {
                                setTextFill(Color.RED);
                                setTooltip(new Tooltip(((Element)list.item(0)).getAttribute("message")));
                            } else {
                                setTextFill(Color.BLACK);
                                setTooltip(null);
                            }
                        }
                    }    
                };
            }            
        });
        projectListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Element>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Element>> observable, TreeItem<Element> oldValue,
                TreeItem<Element> newValue) {
                Element selectedElement = newValue.getValue();
                Document selectedDoc = selectedElement.getOwnerDocument();
                Document activeDoc = ProjectManager.getActiveProject().getDom();
                if (!selectedDoc.equals(activeDoc)) {
                    ProjectManager.setActiveProject(ProjectManager.findProject(selectedDoc));
                    updateMenus();
                }
                if (selectedElement.getTagName().equals("dataType")) {
                    dataTypeViewer.setDataType(selectedElement);
                    scrollPane.setContent(dataTypeViewer.getContent());
                }
                else if(selectedElement.getTagName().equals("pou")){
                    pouTypeViwer.setPOUType(selectedElement);
                    scrollPane.setContent(pouTypeViwer.getContent());
                }
            }
        });
        projectListView.setOnMouseClicked(
            new EventHandler<MouseEvent>() {    
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        //Double click
                        Element elem = projectListView.getSelectionModel().getSelectedItem().getValue();
                        String elemType = elem.getTagName();
                        switch (elemType) {
                            case "dataType":
                                openDataTypeEditor(elem);
                        }
                    }
                }
            });
    }
    
    private String iconLoader(String name){
        URL resource = resource = this.getClass().getClassLoader().getResource( "iechal/gui/images/"+name+".png" );
        String url="";
        if(resource !=null){
            url = resource.toString();
        }
        return url;
    }
    
    private void openDataTypeEditor(Element elem) {
        int index = findEditorIndexForElement(elem);
        if (index == -1) {
            //An editor for this data type is not open
            Editor editor = new DataTypeEditor(elem);
            editor.setup();
            editor.loadValues();
            TitledPane newPane = editor.getTitledPane();
            newPane.setText("Data Type Editor: "+elem.getAttribute("name"));
            accordion.getPanes().add(newPane);
            editorList.add(editor);
            accordion.setExpandedPane(newPane);
            if (accordion.getPanes().size() == 1) {
                btnApply.setVisible(true);
                btnCancel.setVisible(true);
                btnClose.setVisible(true);
            }
        } else {
            //An editor for this data type is already open -> expand the corresponding pane
            accordion.setExpandedPane(accordion.getPanes().get(index));
        }
    }
    
    private void updateMenus() {
        ConfigurationHandler handler = ProjectManager.getActiveProject().getConfigurationHandler();
        boolean activeConfigurationDefined = handler.getActiveConfiguration() != null;
        menuItemSetActiveConf.setVisible(handler.getNumOfConfigurations() > 1);
        menuItemSetController.setVisible( activeConfigurationDefined && (handler.getNumOfProgramInstances() > 1));
        menuItemSetProcess.setVisible( activeConfigurationDefined && (handler.getNumOfProgramInstances() > 1));
        menuSepPLC1.setVisible(menuItemSetActiveConf.isVisible() || menuItemSetController.isVisible() || menuItemSetProcess.isVisible());
        menuItemSetMemorySize.setVisible(activeConfigurationDefined);
        menuSepPLC2.setVisible(menuItemSetMemorySize.isVisible());
        menuPLC.setVisible(true);
    }
    
    private void addDom(TreeItem<Element> item) {
        
        NodeList list = item.getValue().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node currentNode = list.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE){
                String tag = currentNode.getNodeName();
                if (!(tag.equals("iecHalError") || tag.equals("iecHalInfo"))) {
                    TreeItem<Element> newItem = new TreeItem(currentNode);                
                    if (!(tag.equals("dataType"))) {
                        addDom(newItem);
                    }
                    item.getChildren().add(newItem);
                }    
            }
        }
    }    
    
    private int findEditorIndexForElement(Element elem) {
        
        int result = -1;
        
        int n = editorList.size();
        for (int i = 0; i < n; i++) {
            if (elem.equals(editorList.get(i).getElement())) {
                result = i;
                break;
            }
        }
        return result;
    
    }
    
    private int findEditorIndexForPane(TitledPane pane) {
        
        int result = -1;
        
        int n = editorList.size();
        for (int i = 0; i < n; i++) {
            if (pane.equals(editorList.get(i).getTitledPane())) {
                result = i;
                break;
            }
        }
        return result;
        
    }
    
    private void initButtons() {
              
        btnApply.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                int ind = findEditorIndexForPane(accordion.getExpandedPane());
                Editor editor = editorList.get(ind);
                if (editor.validate()) {
                    editor.save();
                }
            }
        });
        
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                int ind = findEditorIndexForPane(accordion.getExpandedPane());
                Editor editor = editorList.get(ind);
                editor.loadValues();
            }
        });
        
        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                //If there is no expanded pane we get a null pointer exception
                int ind = findEditorIndexForPane(accordion.getExpandedPane());
                Editor editor = editorList.get(ind);
                boolean close = true;
                if (editor.isDataChanged()) {
                    ButtonType response = UIelements.askToSave();
                    if (response == ButtonType.YES) {
                        if (editor.validate()) {
                           editor.save();
                        } else {
                            close = false;
                        }
                    } else if (response != ButtonType.NO) {
                        //cancel
                        close = true;
                    }
                }
                if (close) {
                    List paneList = accordion.getPanes();
                    paneList.remove(ind);
                    editorList.remove(ind);
                    if (paneList.size() == 0) {
                        btnApply.setVisible(false);
                        btnCancel.setVisible(false);
                        btnClose.setVisible(false);
                    }
                }
            }
        });
       
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    
    public void initialize(URL url, ResourceBundle rb) {
        initTreeView();
        initButtons();
        UIelements.setTextArea(console);
    }
    
}