package cf.dejf.controller;

import cf.dejf.framework.Install;
import cf.dejf.utility.UserInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.fabricmc.loader.launch.knot.KnotClient;
import xyz.ashleyz.JavaProcess;

import java.io.*;
import java.util.Scanner;

public class MainMenuScreenController {
    double offset_x;
    double offset_y;

    @FXML private Label username;
    @FXML private Button launch_button;
    @FXML private Label close_button;
    @FXML private Label main_button, settings_button, processinfo_button;

    @FXML
    private void initialize(){
        username.setText("Username: " + UserInfo.getUsername());
        username.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(username, 0.0);
        AnchorPane.setRightAnchor(username, 0.0);
        username.setAlignment(Pos.CENTER);
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
    private void press_logoutButton() {
        Install.setCurrentUser(null);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage primaryStage = (Stage) username.getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
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
    private void handleLaunch() throws IOException, InterruptedException {
        StringBuilder libraryBuilder = new StringBuilder();
        String[] libraries = new String[]{"minecraft.jar", "jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "json.jar"};
        for(String libraryAppend : libraries) {
            if (Install.getOS().equals("Windows"))
                libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(";");
            else
                libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(":");
        }

        System.setProperty("java.class.path", Install.getClassPath());
        System.setProperty("java.libs.path", Install.getNativesPath());
        new JavaProcess(System.getProperty("java.home")).exec(KnotClient.class);
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
