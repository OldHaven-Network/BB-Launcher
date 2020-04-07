package net.oldhaven.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.oldhaven.utility.Mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SettingsScreenController {

    double offset_x;
    double offset_y;

    @FXML public ListView<String> modview;
    @FXML public Label close_button;
    @FXML public Label main_button, settings_button, processinfo_button;
    @FXML public TextField selectedmodpath;

    public void initialize() {

        ArrayList<String> stringBuilder = new ArrayList<String>();
        for(Mod mod : Mod.mods) {
            stringBuilder.add(mod.getName());
        }

        modview.getItems().addAll(stringBuilder);

        modview.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, oldValue, newValue) -> {
                    System.out.println("Check box for "+item+" changed from "+oldValue+" to "+newValue);
                    assert Mod.getModByName(item) != null;
                    Mod.getModByName(item).setEnabled(newValue);
                    Mod.saveMods();
                }
            );
            assert Mod.getModByName(item) != null;
            if(Mod.getModByName(item).isEnabled()){
                observable.setValue(true);
            } else {
                observable.setValue(false);
            }
            return observable ;
        }));

        modview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> item, String oldValue, String newValue) -> {
            String selectedItem = modview.getSelectionModel().getSelectedItem();
            selectedmodpath.setText(Objects.requireNonNull(Mod.getModByName(selectedItem)).getFile().toString());
        });
    }

    private void changeScene(String sceneResource) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource(sceneResource));
            Stage primaryStage = (Stage) close_button.getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickAddModButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mod to add...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minecraft.jar mods", "*.jar", "*.zip"));
        fileChooser.showOpenDialog(close_button.getScene().getWindow());
    }

    @FXML
    private void clickRemoveModButton() {

    }

    @FXML
    private void clickMainButton() {
        changeScene("/fxml/MainMenuScreen.fxml");
    }

    @FXML
    private void clickProcessInfoButton() {
        changeScene("/fxml/ProcessInfoScreen.fxml");
    }

    @FXML
    private void movableWindow(){
        Scene scene = close_button.getScene();
        Stage stage = (Stage) close_button.getScene().getWindow();
        scene.setOnMousePressed(event -> {
            offset_x = event.getSceneX();
            offset_y = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset_x);
            stage.setY(event.getScreenY() - offset_y);
        });
    }

    @FXML
    private void closeButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void mainButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            main_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            main_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void settingsButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            settings_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            settings_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void processInfoButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            processinfo_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            processinfo_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void close(MouseEvent event){
        System.exit(0);
    }

}
