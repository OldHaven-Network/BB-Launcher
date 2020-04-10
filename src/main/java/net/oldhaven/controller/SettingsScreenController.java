package net.oldhaven.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModSection;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SettingsScreenController {

    private double offset_x;
    private double offset_y;

    @FXML private ListView<String> modview;
    @FXML private Label close_button;
    @FXML private Label main_button, settings_button, processinfo_button;
    @FXML private TextField selectedmodpath;
    @FXML private TextField selectedmodtype;

    public void initialize() {

        ArrayList<String> stringBuilder = new ArrayList<>();
        for(Mod mod : Mods.getMods())
            stringBuilder.add(mod.getName());

        modview.getItems().addAll(stringBuilder);
        modview.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            final Mod mod = Mods.getModByName(item);
            if(mod != null) {
                observable.addListener((obs, oldValue, newValue) -> {
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
        File file = fileChooser.showOpenDialog(close_button.getScene().getWindow());

        List<String> choices = new ArrayList<>();
        choices.add("MCP");
        choices.add("ModLoader");
        choices.add("Fabric");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("ModLoader", choices);
        dialog.setTitle("Choose mod type");
        dialog.setHeaderText("Please choose the correct type for your mod.");
        dialog.setContentText("If you're not sure, select ModLoader.");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            switch(result.get()) {
                case "MCP":
                    Mods.getModSectionByName("CustomMods").addMod(ModType.MCP, file.getName(), file.getAbsolutePath(), true);
                    break;
                case "ModLoader":
                    Mods.getModSectionByName("CustomMods").addMod(ModType.ModLoader, file.getName(), file.getAbsolutePath(), true);
                    break;
                case "Fabric":
                    Mods.getModSectionByName("CustomMods").addMod(ModType.Fabric, file.getName(), file.getAbsolutePath(), true);
                    break;
                default:
                    // I swear, if somebody manages to select a fourth item from a hard coded three item check box, I won't like it.
                    Mods.getModSectionByName("CustomMods").addMod(ModType.Unknown, file.getName(), file.getAbsolutePath(), true);
                    break;
            }
            Mods.saveMods();
            modview.getItems().add(file.getName());
        }

    }

    @FXML
    private void clickRemoveModButton() {

        boolean removeSuccessful = Mods.getModSectionByName("CustomMods").removeMod(modview.getSelectionModel().getSelectedItem());
        if(removeSuccessful){
            Mods.saveMods();
            modview.getItems().remove(modview.getSelectionModel().getSelectedIndex());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You have tried to remove a built-in mod.");
            alert.setContentText("Please note that built-in mods cannot be removed. However, they can be disabled if you don't feel like using them.");
            alert.showAndWait();
        }

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
