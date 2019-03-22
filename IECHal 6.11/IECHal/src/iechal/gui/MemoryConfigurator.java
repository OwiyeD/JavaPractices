/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iechal.gui;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import iechal.UIelements;
import javafx.util.Callback;

/**
 *
 * @author Christoforos
 */
public class MemoryConfigurator {
    
        private static final Label lblInputs = new Label("Number of input bytes: ");
        private static final Label lblOutputs = new Label("Number of output bytes: ");
        private static final Label lblMemory = new Label("Number of memory bytes: ");
        private static final Tooltip tooltip = new Tooltip("Please enter a positive integer value.");
        private TextField txtInputs;
        private TextField txtOutputs;
        private TextField txtMemory;
        private boolean abnormalClosing = true;
        private boolean valuesHaveChanged = false;
        private boolean okToSave = false;
        private ArrayList<Integer> resultArrayList = new ArrayList();
        
        private Dialog<ArrayList<Integer>> dialog;
        //private Dialog dialog;
        
        private EventHandler keyEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //Without the following test, the tooltip disappears if instead of clicking "OK" we pressed Enter.
                if (!event.getCharacter().equals("\r")) {
                    if (tooltip.isShowing()) {
                        tooltip.hide();
                    }
                    valuesHaveChanged = true;
                }    
            }
        };
        
        public void configureDialog(int inputBytes, int outputBytes, int memoryBytes) {
            resultArrayList.add(new Integer(inputBytes));
            resultArrayList.add(new Integer(outputBytes));
            resultArrayList.add(new Integer(memoryBytes));
            txtInputs = new TextField(Integer.toString(inputBytes));
            txtInputs.setOnKeyTyped(keyEventHandler);
            txtOutputs = new TextField(Integer.toString(outputBytes));
            txtOutputs.setOnKeyTyped(keyEventHandler);
            txtMemory = new TextField(Integer.toString(memoryBytes));
            txtMemory.setOnKeyTyped(keyEventHandler);
            GridPane grid = new GridPane();
            grid.add(lblInputs, 1, 1);
            grid.add(txtInputs, 2, 1);
            grid.add(lblOutputs, 1, 2);
            grid.add(txtOutputs, 2, 2);
            grid.add(lblMemory, 1, 3);
            grid.add(txtMemory, 2, 3);
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(grid);
            
            dialogPane.getButtonTypes().add(ButtonType.OK);
            Button btnOk = (Button)dialogPane.lookupButton(ButtonType.OK);
            btnOk.addEventFilter(ActionEvent.ACTION, 
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (! (validate(txtInputs,0) &&
                               validate(txtOutputs,1) &&
                               validate(txtMemory,2))) {
                            event.consume();
                        } else {
                            okToSave = true;
                            abnormalClosing = false;
                        }
                    }
                });
                        
            dialogPane.getButtonTypes().add(ButtonType.CANCEL);
            Button btnCancel = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
            btnCancel.addEventFilter(ActionEvent.ACTION, 
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {                       
                        abnormalClosing = false;
                    }
                });

            
            dialog = new Dialog();
            dialog.setTitle("Set Memory Size");
            
            dialog.setResultConverter(
                new Callback<ButtonType, ArrayList<Integer>>() {
                    public ArrayList<Integer> call(ButtonType b ) {
                        ArrayList<Integer> result = null;
                        if (okToSave && valuesHaveChanged) {
                            result = resultArrayList;
                        }
                        return result;
                    }
                });
            
            dialog.setDialogPane(dialogPane);
            
            dialogPane.getScene().getWindow().setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        if (abnormalClosing && valuesHaveChanged) {
                            ButtonType response = UIelements.askToSave();
                            if (response == ButtonType.YES) {
                                if (! (validate(txtInputs,0) &&
                                       validate(txtOutputs,1) &&
                                       validate(txtMemory,2))) {
                                    event.consume();
                                } else {
                                    okToSave = true;
                                }    
                            } else if (response != ButtonType.NO) {
                                //Cancel or abnormal close
                                event.consume();
                            } 
                        }
                    }
                });
            dialogPane.getScene().getWindow().setOnShown(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        txtInputs.requestFocus();
                        txtInputs.positionCaret(txtInputs.getLength());
                    }
                });
        }
        
        private boolean validate(TextField txt, int index) {
            
            boolean retval = true;
            int n;
            try {
                n = Integer.parseInt(txt.getText());
                if (n <= 0) {
                    retval = false;
                } else {
                    resultArrayList.set(index, new Integer(n));
                }
            } catch (NumberFormatException e) {
                retval = false;
            }
            if (!retval) {
                Bounds bounds = txt.getBoundsInLocal();
                Point2D p = txt.localToScreen(bounds.getMinX(), bounds.getMinY()+bounds.getHeight());
                tooltip.show(txt,p.getX(),p.getY());
                txt.requestFocus();
                txt.selectAll();
            }
            return retval;
        }
        
        public Dialog getDialog() {
            return dialog;
        }
        
        
}
