package net.oldhaven.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.Mods;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsScreenController implements Initializable {

    private double offset_x;
    private double offset_y;

    @FXML private ListView<String> modview;
    @FXML private Label close_button;
    @FXML private Label main_button, settings_button, processinfo_button;
    @FXML private TextField selectedmodpath;
    @FXML private TextField selectedmodtype;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ArrayList<String> stringBuilder = new ArrayList<>();
        for(Mod mod : Mods.getMods())
            stringBuilder.add(mod.getName());

        modview.getItems().addAll(stringBuilder);
        modview.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            final Mod mod = Mods.getModByName(item);
            if(mod != null) {
                observable.addListener((obs, oldValue, newValue) -> {
                    System.out.println("Check box for " + item + " changed from " + oldValue + " to " + newValue);
                    mod.setEnabled(newValue);
                    Mods.saveMods();
                });
                observable.set(mod.isEnabled());
            }
            return observable ;
        }));

        modview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> item, String oldValue, String newValue) -> {
            String selectedItem = modview.getSelectionModel().getSelectedItem();
            Mod mod = Mods.getModByName(selectedItem);
            if(mod != null) {
                selectedmodpath.setText(mod.getFile().toString());
                selectedmodtype.setText(mod.getType().name());
            }
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mod for Minecraft", "*.jar", "*.zip"));
        fileChooser.showOpenDialog(close_button.getScene().getWindow());
        /* can you make it so they also choose if it's a fabric/modloader/MCP mod? */
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
