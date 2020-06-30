package net.oldhaven.controller.templates;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import net.oldhaven.utility.settings.LaunchSettings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsScreenController implements Initializable {

    private double offset_x;
    private double offset_y;

    @FXML private ListView<String> modview;
    @FXML private Tooltip fabricTooltip;
    @FXML private TextField selectedmodpath, selectedmodtype, minmem_field, maxmem_field;
    @FXML private Button addmod_button, removemod_button, resetbgbutton, launcherbg_button, launcherfolder_button;
    @FXML private ImageView background;
    @FXML private Label maxmem_label, minmem_label, mod_self, settings_self;
    @FXML public AnchorPane pain;
    @FXML public Pane clipPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings_self.setText(Lang.SETTINGS_SELF.translate());
        mod_self.setText(Lang.MODS_SELF.translate());
        addmod_button.setText(Lang.MODS_BUTTONS_ADD.translate());
        removemod_button.setText(Lang.MODS_BUTTONS_REMOVE.translate());
        resetbgbutton.setText(Lang.SETTINGS_BACKGROUND_RESET.translate());
        launcherbg_button.setText(Lang.SETTINGS_BACKGROUND_CHANGE.translate());
        launcherfolder_button.setText(Lang.SETTINGS_OPENFOLDER.translate());
        selectedmodpath.setPromptText(Lang.MODS_PATH.translate());
        selectedmodtype.setPromptText(Lang.MODS_TYPE.translate());
        maxmem_label.setText(Lang.SETTINGS_MEMORY_MAX.translate());
        minmem_label.setText(Lang.SETTINGS_MEMORY_MIN.translate());
        ArrayList<String> stringBuilder = new ArrayList<>();
        for(Mod mod : Mods.getMods()) {
            String req = !mod.canDisable() ? "* " : "";
            stringBuilder.add(req + mod.getName());
        }
        modview.getItems().addAll(stringBuilder);
        modview.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            boolean forceEnable = false;
            if(item.startsWith("* "))
                forceEnable = true;
            item = item.replace("* ", "");
            final Mod mod = Mods.getModByName(item);
            if(mod != null) {
                observable.addListener((obs, oldValue, newValue) -> {
                    mod.setEnabled(newValue);
                    if(mod.getType().equals(ModType.Fabric)) {
                        File dir = new File(Install.getMinecraftPath() + "mods/.disabled/");
                        if(!dir.exists() && !dir.mkdirs()) {
                            System.err.println("Failed to create directory of " + dir.getAbsolutePath());
                            return;
                        }
                        File file = new File(Install.getMinecraftPath() + "mods/.disabled/" +  mod.getFile().getName());
                        try {
                            if(newValue && file.exists()) {
                                FileUtils.moveFile(file, mod.getFile());
                            } else if(!newValue && mod.getFile().exists()) {
                                FileUtils.moveFile(mod.getFile(), file);
                            }
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Mods.saveMods();
                });
                if(!forceEnable)
                    observable.set(mod.isEnabled());
                else
                    observable.set(true);
            }
            return observable;
        }));

        modview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> item, String oldValue, String newValue) -> {
            String selectedItem = modview.getSelectionModel().getSelectedItem();
            Mod mod = Mods.getModByName(selectedItem);
            if(mod != null) {
                
                selectedmodpath.setText(mod.getFile().toString());
                selectedmodtype.setText(mod.getType().name());
            }
        });


        maxmem_field.setText(LaunchSettings.MEMORY_MAX.getAsString());
        minmem_field.setText(LaunchSettings.MEMORY_MIN.getAsString());
    }

    private void doMemOption(final File settingsFile, final TextField mem_field) {
        mem_field.textProperty().addListener((observable, oldValue, newValue) -> {
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

    @FXML
    private void clickAddModButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mod to add...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mod for Minecraft", "*.jar", "*.zip"));
        File file = fileChooser.showOpenDialog(modview.getScene().getWindow());

        ZipFile modZipFile = new ZipFile(file);
        if(file == null)
            return;
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
        File file = fileChooser.showOpenDialog(modview.getScene().getWindow());

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
}
