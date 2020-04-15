package net.oldhaven.controller;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.LogOutput;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.oldhaven.utility.JavaProcess;
import org.apache.commons.io.IOUtils;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessInfoScreenController implements Initializable {

    double offset_x;
    double offset_y;

    @FXML public ImageView background;
    @FXML public Label close_button;
    @FXML public Label main_button, settings_button, processinfo_button;
    @FXML public StyleClassedTextArea process_text;

    private ScheduledExecutorService executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File file : fileArray) {
            if(file.getAbsolutePath().contains("launcherbg")) {
                background.setImage(new Image(file.toURI().toString()));
            }
        }
        Runnable helloRunnable = () -> Platform.runLater(() -> {
            String text;
            if (!(text = LogOutput.getLogOutput()).isEmpty()) {
                process_text.appendText(text);
                process_text.scrollYToPixel(process_text.getLayoutY());
                process_text.moveTo(process_text.getText().length());
            }
            String textInArea = process_text.getText();
            String[] lines = textInArea.split("\n", -1);
            if (lines.length > 10000) {
                //lines = Arrays.copyOfRange(lines, lines.length - 10000, lines.length);
                //textInArea = String.join("\n", lines);
                process_text.appendText("WARNING: Log exceeded 10,000 lines. Logging has ceased to save memory.");
                executor.shutdownNow();
            }
        });
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 250, TimeUnit.MILLISECONDS);
    }

    private void changeScene(String sceneResource) {
        executor.shutdown();
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
    private void clickMainButton() {
        changeScene("/fxml/MainMenuScreen.fxml");
    }

    @FXML
    private void clickSettingsButton() {
        changeScene("/fxml/SettingsScreen.fxml");
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

    @FXML
    private void killMinecraftButton(){
        JavaProcess.destroyProcess();
    }

    public void restartMinecraftButton(MouseEvent event) {
        JavaProcess.restartProcess();
    }
}
