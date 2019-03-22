/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal.gui;

import iechal.IECUtilities;
import iechal.VarListHandler;
import iechal.XMLUtilities;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.w3c.dom.Element;

/**
 *
 * @author Christoforos
 */
public class POUTypeViewer {
    
    private Element element;
    private VarListHandler varListHandler;
    //Grid pane attributes
    private Text nameLabel = new Text("POU name:     ");
    private Text typeLabel = new Text("POU type:     ");
    private Text returnTypeLabel = new Text("Return type: ");
    private Text nameText = new Text();
    private Text typeText = new Text();
    private Text returnTypeText = new Text(" ");
    
    //Grid Variables attributes
    private Text name = new Text("Name: ");
    private Text type = new Text("Type: ");
    private Text typeData = new Text("Type data: ");
    private Text isConstant = new Text("Is constant: ");
    private Text nametxt = new Text();
    private Text typetxt = new Text();
    private Text typeDatatxt = new Text();
    private Text isConstanttxt = new Text();
    
    
    private  VBox vb = new VBox();
    private GridPane gridPane = new GridPane();
    private ChoiceBox choiceBox = new ChoiceBox();
    private GridPane gridVariables = new GridPane();
    
    public POUTypeViewer(){

        initializeGridPane();
        initializeGridVariables();
        
        choiceBox.setVisible(false);

        vb.setSpacing(10);
        vb.getChildren().add(gridPane);
        vb.getChildren().add(choiceBox);
        vb.getChildren().add(gridVariables);
        
        variablesInformation();
    }
    
    public Node getContent() {
        
        return vb;
    
    }
    
    public void setPOUType(Element pouTypeElement) {
        element = pouTypeElement;
        nameText.setText(pouTypeElement.getAttribute("name"));
        String pouType = pouTypeElement.getAttribute("pouType");
        typeText.setText(pouType);
        
        gridVariables.setVisible(false);
        
        if(pouType.equals("function")){
            fillChoiceBox(pouTypeElement);
            
            Element returnType = XMLUtilities.findChildElement(pouTypeElement, "interface");
            returnType = XMLUtilities.findChildElement(returnType, "returnType");
            returnTypeLabel.setText("Return type: ");
            
            if(returnType!= null){
                returnTypeText.setText(XMLUtilities.getSingleChildElement(returnType).getNodeName());
            }
            else
                 returnTypeText.setText("Not defined");
        }
        else{
            returnTypeLabel.setText("");
            returnTypeText.setText("");
            choiceBox.setVisible(false);
        }
    }
    
    public void fillChoiceBox(Element pouTypeElement){
         varListHandler = new VarListHandler(pouTypeElement);
        if(varListHandler.getNumberOfVariables()>0){
            ArrayList<String> listOfVariables = new ArrayList<String>();
            for(int i  = 0;i<varListHandler.getNumberOfVariables();i++){
                listOfVariables.add(varListHandler.getVariableElement(i).getAttribute("name"));
            }
            choiceBox.setItems(FXCollections.observableArrayList(listOfVariables));
            choiceBox.setVisible(true);
            
        }
        else
            choiceBox.setVisible(false);    
    }
    
    public void variablesInformation(){
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(choiceBox.getSelectionModel().getSelectedItem()!= null){
                    int index = choiceBox.getSelectionModel().getSelectedIndex();
                    nametxt.setText(varListHandler.getVariableElement(index).getAttribute("name"));
                    typetxt.setText(VarListHandler.getVariableType(varListHandler.getVariableElement(index)));
                    typeDatatxt.setText(VarListHandler.getVariableDataType(varListHandler.getVariableElement(index)));
                    isConstanttxt.setText(VarListHandler.isConstant(varListHandler.getVariableElement(index))+"");
                    gridVariables.setVisible(true);
                }
            }
        });
    }
    
    public void initializeGridPane(){
        
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        typeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        returnTypeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        
        nameText.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        typeText.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        returnTypeText.setFont(Font.font("Verdana", FontWeight.LIGHT, 13));
        
        gridPane.add(nameLabel,0,0);
        gridPane.add(typeLabel,0,1);
        gridPane.add(returnTypeLabel,0,2);
        gridPane.add(nameText,1,0);
        gridPane.add(typeText,1,1);
        gridPane.add(returnTypeText,1,2);
    }
    
    public void initializeGridVariables(){
        gridVariables.add(name,0,0);
        gridVariables.add(type,0,1);
        gridVariables.add(typeData,0,2);
        gridVariables.add(isConstant,0,3);
        gridVariables.add(nametxt,1,0);
        gridVariables.add(typetxt,1,1);
        gridVariables.add(typeDatatxt,1,2);
        gridVariables.add(isConstanttxt,1,3);
        
    }
}
