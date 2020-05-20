package net.oldhaven.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.minecraft.client.Minecraft;
import net.oldhaven.Main;
import net.oldhaven.framework.Install;
import net.oldhaven.framework.VersionHandler;
import net.oldhaven.utility.UserInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.fabricmc.loader.launch.knot.KnotClient;
import net.oldhaven.utility.JavaProcess;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainMenuScreenController implements Initializable {
    double offset_x;
    double offset_y;

    @FXML public ImageView background;
    @FXML private Label username, downloading_label;
    @FXML public Button launch_button;
    @FXML private Label close_button, logout_button;
    @FXML private Label main_button, settings_button, processinfo_button;
    @FXML public AnchorPane pain;
    @FXML public Pane clipPane;
    @FXML public ImageView skin;
    @FXML public ComboBox<String> version_picker;
    @FXML public ProgressBar progress_bar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setCurrentController(this);
        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File file : fileArray) {
            if(file.getAbsolutePath().contains("launcherbg")) {
                Image image = new Image(file.toURI().toString());
                background.setImage(image);
                background.fitWidthProperty().bind(clipPane.widthProperty());
            }
        }

        version_picker.getItems().addAll("b1.7.3", "AetherMP");
        version_picker.getSelectionModel().select(VersionHandler.getSelectedVersion().getName());
        version_picker.valueProperty().addListener((obs, oldValue, newValue) -> {
            newValue = newValue.replaceAll("\\.", "");
            VersionHandler.updateSelectedVersion(newValue);
            Mods.updateConfigLoc();
        });

        this.skin.setImage(new Image("https://minotar.net/body/"+UserInfo.getUsername()+"/100.png"));
        username.setText(UserInfo.getUsername());
        username.setMaxWidth(Double.MAX_VALUE);
        //AnchorPane.setLeftAnchor(username, 0.0);
        //AnchorPane.setRightAnchor(username, 0.0);
        //username.setAlignment(Pos.CENTER);
    }

    @FXML
    private void movableWindow(){
        Scene scene = launch_button.getScene();
        Stage stage = (Stage) launch_button.getScene().getWindow();
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
    private void press_logoutButton() throws IOException {

        // Show the user a dialog box to help him decide.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("If you also want to erase your account details, click \"Logout and forget\".");

        ButtonType logoutAndForget = new ButtonType("Logout and forget", ButtonBar.ButtonData.LEFT);
        ButtonType logout = new ButtonType("Logout", ButtonBar.ButtonData.OTHER);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(logoutAndForget, logout, cancel);

        // Get the button pressed.
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == logoutAndForget){
            // Read JSON to JsonObject, remove user's account, pretty print JSON back to players.json, clear current user, switch to login scene.
            try {
                JsonObject jsonObject = (JsonObject) JsonParser.parseReader(new FileReader(Install.getMainPath() + "players.json"));
                jsonObject.remove(UserInfo.getUsername());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(Paths.get(Install.getMainPath() + "players.json"));
                gson.toJson(jsonObject, writer);
                writer.close();
                Install.setCurrentUser(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage primaryStage = (Stage) username.getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } else if (result.get() == logout) {
            // Only switch to login scene.
            Install.setCurrentUser(null);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage primaryStage = (Stage) username.getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        }
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
    private void logoutButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            logout_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            logout_button.setTextFill(Color.web("#FFFFFF", 1));
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
    private void handleLaunch(MouseEvent event) throws IOException {

        File temp = new File(Install.getMainPath() + "temp/");
        if(temp.exists())
            FileUtils.forceDelete(new File(Install.getMainPath() + "temp/"));
        temp.mkdirs();

        // Download Minecraft if it hasn't been done so before.
        downloading_label.setVisible(true);
        if(VersionHandler.getSelectedVersion() != VersionHandler.Version.AetherMP) {
            if (!new File(Install.getBinPath() + "fabric/").exists()) {
                Install.installFabric();
                Install.installDefaultMods();
            }
        }
        if(!new File(Install.getBinPath() + "minecraft.jar").exists()) {
            downloading_label.setText("Installing Minecraft...");
            Install.installMinecraft();
        }
        if(!new File(Install.getMinecraftPath() + "planes/").exists() &&
          VersionHandler.getSelectedVersion() == VersionHandler.Version.AetherMP) {
            Install.installAetherMP();
        }
        this.launchMinecraft();
        downloading_label.setVisible(false);
    }

    public void launchMinecraft() {
        launch_button.setDisable(true);
        try {
            // Back up MC to minecraft.old if that hasn't been done yet. If it has, get fresh MC from minecraft.old to prepare it for injection.
            if(!Files.exists(Paths.get(Install.getBinPath() + "minecraft.old"))) {
                new ZipFile(Install.getBinPath() + "minecraft.jar").removeFile("META-INF/");
                FileUtils.copyFile(new File(Install.getBinPath() + "minecraft.jar"), new File(Install.getBinPath() + "minecraft.old"));
            } else {
                FileUtils.copyFile(new File(Install.getBinPath() + "minecraft.old"), new File(Install.getBinPath() + "minecraft.jar"));
            }

            // Create temp folder, catch non-existent mods, extract appropriate mod contents into it.
            File modTempPath = new File(Install.getMinecraftPath() + "mods/temp/");
            for (Mod mod : Mods.getMods()) {
                if (mod.isEnabled() && mod.getType().equals(ModType.NonFabric)) {
                    if (mod.getFile().exists()) {
                        ZipFile modZip = new ZipFile(mod.getFile());
                        modZip.extractAll(modTempPath.toString());
                        System.out.println(modTempPath.toString());
                        System.out.println("Mod " + mod.getName() + " will be added to minecraft.jar.");
                    } else {
                        System.out.println("Tried to prepare enabled mod " + mod.getName() + " for adding to minecraft.jar, but it does not exist!");
                    }
                }
            }

            // Delete META-INF files, inject mods into minecraft.jar and clean up our mess behind us.
            ZipFile minecraftJarZip = new ZipFile(Install.getBinPath() + "minecraft.jar");
            List<FileHeader> headers = minecraftJarZip.getFileHeaders();
            // The following three lines are not a mistake, by the way. It just works. Trust me.
            minecraftJarZip.removeFile(headers.get(0));
            minecraftJarZip.removeFile(headers.get(0));
            minecraftJarZip.removeFile(headers.get(0));
            File[] files = modTempPath.listFiles(File::isFile);
            File[] folders = modTempPath.listFiles(File::isDirectory);
            if (files != null)
                minecraftJarZip.addFiles(Arrays.asList(files));
            if(folders != null) {
                for(File folder : folders)
                    minecraftJarZip.addFolder(folder);
            }
            FileUtils.deleteDirectory(modTempPath);
            System.out.println("All mods have been injected.");

            if(!VersionHandler.getSelectedVersion().getName().equals("AetherMP")) {
                System.setProperty("java.class.path", Install.getClassPath(true));
                System.setProperty("java.libs.path", Install.getNativesPath());
                new JavaProcess(System.getProperty("java.home")).exec(KnotClient.class);
            } else {
                StringBuilder libraryBuilder = new StringBuilder();
                String[] libraries = new String[]{"minecraft.jar", "jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "json.jar"};
                for(String libraryAppend : libraries) {
                    if (Install.getOS().equals("Windows"))
                        libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(";");
                    else
                        libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(":");
                }
                String libraryFinal = libraryBuilder.toString();
                System.setProperty("java.class.path", libraryFinal);
                System.setProperty("java.libs.path", Install.getNativesPath());
                new JavaProcess(System.getProperty("java.home")).exec(Minecraft.class);
            }
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/ProcessInfoScreen.fxml"));
                Stage primaryStage = (Stage) close_button.getScene().getWindow();
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        launch_button.setDisable(false);
    }

    @FXML private void close(MouseEvent event){
        System.exit(0);
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
    private void clickSettingsButton() {
        changeScene("/fxml/SettingsScreen.fxml");
    }

    @FXML
    private void clickProcessInfoButton() {
        changeScene("/fxml/ProcessInfoScreen.fxml");
    }
}
