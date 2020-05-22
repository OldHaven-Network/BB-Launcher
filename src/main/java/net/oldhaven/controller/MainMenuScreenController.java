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
import net.oldhaven.utility.enums.Version;
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

        for(Version version : Version.values()) {
            version_picker.getItems().add(version.getName());
        }
        version_picker.getSelectionModel().select(Version.selectedVersion.getName());
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
    private void logoutButton_onClick() throws IOException {

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
    private void closeButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void logoutButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            logout_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            logout_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void mainButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            main_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            main_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void settingsButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            settings_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            settings_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void processInfoButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            processinfo_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            processinfo_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void launchButton_onClick() throws IOException {
        File temp = new File(Install.getMainPath() + "temp/");
        if(temp.exists())
            FileUtils.forceDelete(temp);
        temp.mkdirs();

        Install.installMinecraft(Version.selectedVersion);
        Version.selectedVersion.launch();
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
