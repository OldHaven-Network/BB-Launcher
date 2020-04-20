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
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.oldhaven.Main;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModSection;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.Sys;

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
    @FXML private TextField selectedmodpath, selectedmodtype, minmem_field, maxmem_field;
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
                background.setFitWidth(clipPane.getWidth());
                background.setFitHeight(clipPane.getHeight());
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
<<<<<<< Updated upstream
                    if(mod.getType().equals(ModType.ModLoader) || mod.getType().equals(ModType.Fabric)){
                        File file = new File(Install.getMinecraftPath() + "mods/" + mod.getName());
                        try {
                            FileUtils.moveFile(newValue ? mod.getFile() : file, newValue ? file : mod.getFile());
                        } catch(IOException ignored) {}
=======
                    if(mod.getType().equals(ModType.Fabric)){
                        if(newValue && mod.getFile().exists()){
                            try {
                                FileUtils.moveFile(mod.getFile(), new File(Install.getMinecraftPath() + "mods/" + mod.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(new File(Install.getMinecraftPath() + "mods/" + mod.getName()).exists()) {
                            try {
                                FileUtils.moveFile(new File(Install.getMinecraftPath() + "mods/" + mod.getName()), mod.getFile());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
>>>>>>> Stashed changes
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

        doMemOption(settingsFile, maxmem_field);
        doMemOption(settingsFile, minmem_field);
    }

    private void doMemOption(final File settingsFile, final TextField mem_field) {
        mem_field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                PrintWriter settingsWriter = new PrintWriter(settingsFile, "UTF-8");
                settingsWriter.println(mem_field.getText()); // Maximum allocated memory
                settingsWriter.println(mem_field.getText()); // Minimum allocated memory
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

        ZipFile modZipFile = new ZipFile(file);
        String nonFabricModPath = Install.getMinecraftPath() + "mods-inactive/" + file.getName();
        try {
            if(modZipFile.getFileHeader("fabric.mod.json") != null) {
                try {
                    FileUtils.copyFile(new File(file.getAbsolutePath()), new File(nonFabricModPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Mods.getModSectionByName("CustomMods").addMod(ModType.Fabric, file.getName(), nonFabricModPath, true);
            } else {
                Mods.getModSectionByName("CustomMods").addMod(ModType.NonFabric, file.getName(), file.getAbsolutePath(), true);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        Mods.saveMods();
        modview.getItems().add(file.getName());
    }

    @FXML
    private void clickRemoveModButton() {

        ModType removedModType = Mods.getModSectionByName("CustomMods").getModByName(modview.getSelectionModel().getSelectedItem()).getType();
        String removedModName = Mods.getModSectionByName("CustomMods").getModByName(modview.getSelectionModel().getSelectedItem()).getName();

        boolean removeSuccessful = Mods.getModSectionByName("CustomMods").removeMod(modview.getSelectionModel().getSelectedItem());
        if(removeSuccessful){
            if(removedModType.equals(ModType.Fabric)){
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
        background.setImage(new Image(file.toURI().toString()));

    }

    @FXML
    private void clickResetLauncherBackground() {
        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File fileUghh : fileArray) {
            if(fileUghh.getAbsolutePath().contains("launcherbg")) {
                fileUghh.delete();
            }
        }
        background.setImage(new Image(getClass().getResource("/images/blur.png").toString()));
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
