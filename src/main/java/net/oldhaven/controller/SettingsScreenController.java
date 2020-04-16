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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModSection;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SettingsScreenController {

    private double offset_x;
    private double offset_y;

    @FXML private ListView<String> modview;
    @FXML private Label close_button;
    @FXML private Label main_button, settings_button, processinfo_button;
    @FXML private TextField selectedmodpath, selectedmodtype, minmem_field, maxmem_field, launcherbg_path;
    @FXML private ImageView background;
    @FXML public AnchorPane pain;
    @FXML public Pane clipPane;

    public void initialize() {

        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File file : fileArray) {
            if(file.getAbsolutePath().contains("launcherbg")) {
                Image image = new Image(file.toURI().toString());
                background.setImage(image);
                background.fitWidthProperty().bind(clipPane.widthProperty());
            }
        }

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
                    if(mod.getType().equals(ModType.ModLoader) || mod.getType().equals(ModType.Fabric)){
                        if(newValue){
                            try {
                                FileUtils.moveFile(mod.getFile(), new File(Install.getMinecraftPath() + "mods/" + mod.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                FileUtils.moveFile(new File(Install.getMinecraftPath() + "mods/" + mod.getName()), mod.getFile());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
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


        File settingsFile = new File(Install.getMainPath() + "settings.txt");

        try {
            BufferedReader settingsReader = new BufferedReader(new FileReader(settingsFile));
            maxmem_field.setText(settingsReader.readLine());
            minmem_field.setText(settingsReader.readLine());
            settingsReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        maxmem_field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                PrintWriter settingsWriter = new PrintWriter(settingsFile, "UTF-8");
                settingsWriter.println(maxmem_field.getText()); // Maximum allocated memory
                settingsWriter.println(minmem_field.getText()); // Minimum allocated memory
                settingsWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        minmem_field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                PrintWriter settingsWriter = new PrintWriter(settingsFile, "UTF-8");
                settingsWriter.println(maxmem_field.getText()); // Maximum allocated memory
                settingsWriter.println(minmem_field.getText()); // Minimum allocated memory
                settingsWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
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
            String notMcpModPath = Install.getMinecraftPath() + "mods-inactive/" + file.getName();
            switch(result.get()) {
                case "MCP":
                    Mods.getModSectionByName("CustomMods").addMod(ModType.MCP, file.getName(), file.getAbsolutePath(), true);
                    break;
                case "ModLoader":
                    try {
                        FileUtils.copyFile(new File(file.getAbsolutePath()), new File(notMcpModPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Mods.getModSectionByName("CustomMods").addMod(ModType.ModLoader, file.getName(), notMcpModPath, true);
                    boolean isModloaderPresent = false;
                    for(Mod mod : Mods.getMods()){
                        if(mod.getName().toLowerCase().contains("modloader")){
                            isModloaderPresent = true;
                        }
                    }
                    if(!isModloaderPresent) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Could not detect ModLoader within your mods.");
                        alert.setContentText("Please make sure to acquire and add ModLoader as an MCP mod alongside your ModLoader mod.");
                        alert.showAndWait();
                    }
                    break;
                case "Fabric":
                    try {
                        FileUtils.copyFile(new File(file.getAbsolutePath()), new File(notMcpModPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Mods.getModSectionByName("CustomMods").addMod(ModType.Fabric, file.getName(), notMcpModPath, true);
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
            ModType removedModType = Mods.getModSectionByName("CustomMods").getModByName(modview.getSelectionModel().getSelectedItem()).getType();
            String removedModName = Mods.getModSectionByName("CustomMods").getModByName(modview.getSelectionModel().getSelectedItem()).getName();
            if(removedModType.equals(ModType.ModLoader) || removedModType.equals(ModType.Fabric)){
                FileUtils.deleteQuietly(new File(Install.getMinecraftPath() + "mods-inactive/" + removedModName));
                FileUtils.deleteQuietly(new File(Install.getMinecraftPath() + "mods/" + removedModName));
            }
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
    private void clickChangeBackgroundButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mod to add...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.gif"));
        File file = fileChooser.showOpenDialog(close_button.getScene().getWindow());

        File newImage = new File(Install.getMainPath() + "launcherbg." + FilenameUtils.getExtension(file.getAbsolutePath()));
        try {
            File[] fileArray = new File(Install.getMainPath()).listFiles();
            assert fileArray != null;
            for(File fileUghh : fileArray) {
                if(fileUghh.getAbsolutePath().contains("launcherbg")) {
                    fileUghh.delete();
                }
            }
            FileUtils.copyFile(file.getAbsoluteFile(), newImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        launcherbg_path.setText(newImage.getAbsolutePath());
        background.setImage(new Image(file.toURI().toString()));

    }

    @FXML
    private void clickLauncherFolderButton() throws IOException {
        Desktop.getDesktop().open(new File(Install.getMainPath()));
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
