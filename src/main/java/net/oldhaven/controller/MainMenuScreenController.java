package net.oldhaven.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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
import net.oldhaven.utility.enums.Scenes;
import net.oldhaven.utility.enums.Versions;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;
import org.lwjgl.Sys;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainMenuScreenController implements Initializable {
    double offset_x;
    double offset_y;

    @FXML public ImageView background;
    @FXML private Label username, downloading_label, selectedversion_label;
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

        this.skin.setImage(new Image("https://minotar.net/body/"+UserInfo.getUsername()+"/100.png"));
        username.setText(UserInfo.getUsername());
        username.setMaxWidth(Double.MAX_VALUE);
        //AnchorPane.setLeftAnchor(username, 0.0);
        //AnchorPane.setRightAnchor(username, 0.0);
        //username.setAlignment(Pos.CENTER);

        selectedversion_label.setText(Versions.selectedVersion.getName());
        selectedversion_label.setMaxWidth(Double.MAX_VALUE);
        Mods.updateConfigLoc();
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
    private void launchButton_onClick() {
        File temp = new File(Install.getMainPath() + "temp/");
        if(temp.exists()) {
            try {
                FileUtils.forceDelete(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        temp.mkdirs();

        Install.installMinecraft(Versions.selectedVersion);
        Versions.selectedVersion.launch();
    }

    @FXML
    private void selectVersionButton_onClick() throws IOException {

        Alert popupWindow = new Alert(Alert.AlertType.NONE);
        popupWindow.setTitle("Version Manager");
        popupWindow.setHeaderText(null);
        popupWindow.initStyle(StageStyle.UTILITY);

        TreeView<String> versionTree1 = new TreeView<>();
        TreeView<String> versionTree2 = new TreeView<>();
        TreeItem<String> root1 = new TreeItem<>("Minecraft");
        TreeItem<String> root2 = new TreeItem<>("Minecraft");
        TreeItem<String> betaFolder = new TreeItem<>("Beta");
        TreeItem<String> alphaFolder = new TreeItem<>("Alpha");
        TreeItem<String> moddedFolder = new TreeItem<>("Modded");
        root1.getChildren().addAll(alphaFolder, betaFolder, moddedFolder);

        /*
        This doesn't work!
        You probably shouldn't bother uncommenting this and sticking these variables back where they belonged.
        - Dejf

        ImageView alphaImage = new ImageView(new Image(getClass().getResourceAsStream("/images/alpha.png")));
        ImageView betaImage = new ImageView(new Image(getClass().getResourceAsStream("/images/beta.png")));
        ImageView unknownImage = new ImageView(new Image(getClass().getResourceAsStream("/images/unknown.png")));
         */

        for(Versions versions : Versions.values()) {
            if(versions.getName().toLowerCase().contains("alpha")) {
                alphaFolder.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                        getClass().getResourceAsStream("/images/alpha.png")))));
                if(versions.isInstalled()){
                    root2.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                            getClass().getResourceAsStream("/images/alpha.png")))));
                }
            } else if (versions.getName().toLowerCase().contains("beta")) {
                betaFolder.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                        getClass().getResourceAsStream("/images/beta.png")))));
                if(versions.isInstalled()){
                    root2.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                            getClass().getResourceAsStream("/images/beta.png")))));
                }
            } else {
                moddedFolder.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                        getClass().getResourceAsStream("/images/unknown.png")))));
                if(versions.isInstalled()){
                    root2.getChildren().add(new TreeItem<>(versions.getName(), new ImageView(new Image(
                            getClass().getResourceAsStream("/images/unknown.png")))));
                }
            }
        }

        versionTree1.setRoot(root1);
        versionTree2.setRoot(root2);
        root1.setExpanded(true);
        root2.setExpanded(true);

        Button button1 = new Button("Install selected version");
        Button button2 = new Button("Uninstall selected version");

        Label label1 = new Label("Available Versions");
        Label label2 = new Label("Installed Versions");

        GridPane gridPane = new GridPane();
        //gridPane.setMinSize(200, 400);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setMaxHeight(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 1, 0);
        gridPane.add(versionTree1, 0, 1);
        gridPane.add(versionTree2, 1, 1);
        gridPane.add(button1, 0, 2);
        gridPane.add(button2, 1, 2);

        popupWindow.getDialogPane().setContent(gridPane);

        Window window = popupWindow.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        popupWindow.show();

        button1.setOnAction(event -> {
            String version = versionTree1.getSelectionModel().getSelectedItem().getValue();
            if(version.toLowerCase().contains("alpha")) {
                root2.getChildren().add(new TreeItem<>(version, new ImageView(new Image(
                            getClass().getResourceAsStream("/images/alpha.png")))));
            } else if (version.toLowerCase().contains("beta")) {
                root2.getChildren().add(new TreeItem<>(version, new ImageView(new Image(
                            getClass().getResourceAsStream("/images/beta.png")))));
            } else {
                root2.getChildren().add(new TreeItem<>(version, new ImageView(new Image(
                            getClass().getResourceAsStream("/images/unknown.png")))));
            }
            VersionHandler.updateSelectedVersion(version);
            Mods.updateConfigLoc();
            selectedversion_label.setText(versionTree2.getSelectionModel().getSelectedItem().getValue());
            selectedversion_label.setMaxWidth(Double.MAX_VALUE);
        });

        button2.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uninstall");
            alert.setHeaderText("You are about to uninstall " + Versions.selectedVersion.getName() + ".");
            alert.setContentText("All game files for this version will be lost.\nTHIS ACTION CANNOT BE UNDONE!" +
                    "\nWould you like to proceed with uninstalling this version?");

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yes, no);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yes) {
                root2.getChildren().remove(versionTree2.getSelectionModel().getSelectedItem());
                Versions.selectedVersion.uninstall();
            }
        });

        versionTree2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            VersionHandler.updateSelectedVersion(newValue.getValue());
            Mods.updateConfigLoc();
            selectedversion_label.setText(versionTree2.getSelectionModel().getSelectedItem().getValue());
            selectedversion_label.setMaxWidth(Double.MAX_VALUE);
        });

        /*
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/VersionSelectScreen.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Version Manager");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon2.png")));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        scene.setOnMousePressed(event -> {
            offset_x = event.getSceneX();
            offset_y = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset_x);
            stage.setY(event.getScreenY() - offset_y);
        });
        stage.show();
         */


    }

    @FXML private void close(MouseEvent event){
        System.exit(0);
    }

    @FXML
    private void clickSettingsButton() {
        Scenes.Settings.changeTo();
    }

    @FXML
    private void clickProcessInfoButton() {
        Scenes.ProcessInfo.changeTo();
    }
}
