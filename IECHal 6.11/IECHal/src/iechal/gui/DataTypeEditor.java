/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal.gui;

import iechal.IECUtilities;
import iechal.ProjectManager;
import iechal.XMLUtilities;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christoforos
 */
public class DataTypeEditor extends Editor {
    
   
    public DataTypeEditor(Element element) {
        this.element = element;
        
        
        
        if (element == null) {
            Document dom = ProjectManager.getActiveProject().getDom();
            element = dom.createElement("dataType");
            element.setAttribute("name", "");
            Element baseType = dom.createElement("baseType");
            baseType.appendChild(dom.createElement("INT"));
            element.appendChild(baseType);
            parentElement = (Element)dom.getElementsByTagName("dataTypes").item(0);
        }
    }
    
    @Override
    public void setup() {

        Label lblDataTypeName = new Label("Data type name  ");
        TextField txtDataTypeName = new TextField();
        txtDataTypeName.setText(super.getElement().getAttribute("name"));
        
        Label lblBaseType = new Label("Base Type");
        lblBaseType.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        Label lblInitialValue = new Label("Initial value  ");
        lblInitialValue.setFont(Font.font("Verdana", FontWeight.LIGHT, 11));
        Element baseType = XMLUtilities.findChildElement(super.getElement(), "baseType");
        baseType = XMLUtilities.getSingleChildElement(baseType);
        String baseTypeName = baseType.getNodeName();
        
        Element initialValue = XMLUtilities.findChildElement(super.getElement(), "initialValue");
        String initValue;
        
        //Base Type initial value
        if (initialValue != null) {
            //get the simple value element
            initialValue = XMLUtilities.getSingleChildElement(initialValue);
            initValue=initialValue.getAttribute("value");                                            
        } else {
            initValue="";
        }
        
        final ToggleGroup group = new ToggleGroup();

        RadioButton radiobutton1 = new RadioButton("Standard");
        radiobutton1.setToggleGroup(group);
        radiobutton1.setUserData("1");

        RadioButton radiobutton2 = new RadioButton("User defined");
        radiobutton2.setToggleGroup(group);
        radiobutton2.setUserData("2");
        
        TextField txtInitialValue = new TextField();
        txtInitialValue.setText(initValue);
        txtInitialValue.setVisible(true);
        
        ChoiceBox cb = new ChoiceBox();
        //ChoiceBox Listener
        cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
            }
        });
        
        cb.setVisible(false);

        ArrayList<String> list2 = new ArrayList<String>();
        //Filling the list with items for user defined types option
        int counter = ProjectManager.getActiveProject().getDataTypeHandler().getNumberOfDataTypes();
        for (int i=0;i<counter;i++){
            if(!ProjectManager.getActiveProject().getDataTypeHandler().getDataTypeElement(i).getAttribute("name").equals(super.getElement().getAttribute("name"))){
                list2.add(ProjectManager.getActiveProject().getDataTypeHandler().getDataTypeElement(i).getAttribute("name"));
            }
        }
        
        
        IECUtilities ic = new IECUtilities();
        
        
        //Choose selected radio button based on baseTypeName
        if(baseTypeName.equals("derived")){
            baseTypeName = super.getElement().getAttribute("name");
            System.out.println(baseTypeName);
            radiobutton1.setSelected(true);
            cb.setItems(FXCollections.observableArrayList(ic.getIECtypes()));
            cb.setVisible(true);
            cb.setTooltip(new Tooltip("Select an option"));
        }
        else if(!IECUtilities.isAnyElementary(baseTypeName)){
            baseTypeName = "Not supported";
            radiobutton2.setSelected(true);
            cb.setItems(FXCollections.observableArrayList(list2));
            cb.setVisible(true);
            cb.setTooltip(new Tooltip("Select an option"));
        }
        else if(IECUtilities.isAnyElementary(baseTypeName)){
            cb.setItems(FXCollections.observableArrayList(ic.getIECtypes()));
            for(int i=0;i<ic.getIECtypes().size();i++){
                if(ic.getIECtypes().get(i).equals(baseTypeName)){
                    cb.getSelectionModel().select(i);
                }
            }
            cb.setVisible(true);
            cb.setTooltip(new Tooltip("Select an option"));
        }

        //Toggle group Listener
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
          public void changed(ObservableValue<? extends Toggle> ov,
              Toggle old_toggle, Toggle new_toggle) {
            if (group.getSelectedToggle() != null) {
                if(group.getSelectedToggle().getUserData().toString().equals("1")){
                    
                    cb.setItems(FXCollections.observableArrayList(ic.getIECtypes()));
                    cb.getSelectionModel().selectFirst();
                    cb.setVisible(true);
                }
                else if(group.getSelectedToggle().getUserData().toString().equals("2")){
                    cb.setItems(FXCollections.observableArrayList(list2));
                    cb.getSelectionModel().selectFirst();
                    cb.setVisible(true);
                }
            }
          }
        });

        GridPane gridDataTypeName = new GridPane();
        gridDataTypeName.add(lblDataTypeName, 1, 0);
        gridDataTypeName.add(txtDataTypeName, 2, 0);
        
        GridPane gridBaseType = new GridPane();
        gridBaseType.add(radiobutton1, 1, 2);
        gridBaseType.add(radiobutton2, 1, 3);
        gridBaseType.add(cb, 1, 4);
        gridBaseType.setVgap(5);
        
        GridPane gridInitialValue = new GridPane();
        gridInitialValue.add(lblInitialValue, 1,1 );
        gridInitialValue.add(txtInitialValue, 2,1 );
        
        BorderPane.setMargin(gridDataTypeName, new Insets(0,0,10,0));
        BorderPane.setAlignment(lblBaseType, Pos.CENTER);
        BorderPane.setMargin(lblBaseType, new Insets(12,0,0,0));
        BorderPane.setMargin(gridBaseType, new Insets(0,5,5,5));
        BorderPane.setMargin(gridInitialValue, new Insets(10,0,10,0));
        
        
        //Containes both data type name and base type 
        BorderPane container = new BorderPane();
        //Contains the grid of base type
        BorderPane baseTypeView = new BorderPane();
        
        baseTypeView.setTop(lblBaseType);
        baseTypeView.setCenter(gridBaseType);
        baseTypeView.setStyle("-fx-border-color: black;");
        
        container.setTop(gridDataTypeName);
        container.setCenter(baseTypeView);
        container.setBottom(gridInitialValue);
        
        super.titledPane.setContent(container);
    }
    
    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void loadValues() {
        
    }

    @Override
    public void save() {
        
    }

    
    
}
