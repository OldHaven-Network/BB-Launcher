package net.oldhaven.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.oldhaven.Main;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.LogOutput;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.oldhaven.utility.JavaProcess;
import net.oldhaven.utility.enums.Scenes;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessInfoScreenController implements Initializable {

    double offset_x;
    double offset_y;

    @FXML private ImageView background;
    @FXML private Label close_button;
    @FXML private Label main_button, settings_button, processinfo_button;
    @FXML private StyleClassedTextArea process_text;
    @FXML private AnchorPane pain;
    @FXML private Pane clipPane;
    @FXML private TextField loglines_textfield;

    private ScheduledExecutorService executor;

    public void clearLog() {
        process_text.clear();
    }

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

        AtomicInteger loglines = new AtomicInteger();
        loglines.set(10000);
        loglines_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                loglines_textfield.setText(newValue.replaceAll("[^\\d]", ""));
            }
            try{
                // Don't worry about this. It looks cancer, and it is, but pretend that this isn't here.
                if(process_text.getText().split("\n", -1).length < Integer.parseInt(loglines_textfield.getText())) {
                    loglines.set(Integer.parseInt(loglines_textfield.getText()));
                } else {
                    loglines.set(Integer.parseInt(loglines_textfield.getText()) + 10);
                }
            } catch(NumberFormatException e){
                loglines.set(10000);
            }
        });

        process_text.setAutoScrollOnDragDesired(true);

        Runnable helloRunnable = () -> Platform.runLater(() -> {
            String text;
            if (!(text = LogOutput.getLogOutput()).isEmpty()) {
                process_text.appendText(text);
                process_text.scrollYBy(process_text.getLength());
            }
            String textInArea = process_text.getText();
            String[] lines = textInArea.split("\n", -1);
            if (lines.length > loglines.get()) {
                process_text.appendText("WARNING: Log exceeded " + loglines.get() + " lines. Logging has ceased to save memory.");
                executor.shutdownNow();
            }
        });
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 250, TimeUnit.MILLISECONDS);
    }

    @FXML
    private void clickMainButton() {
        Scenes.MainMenu.changeTo();
    }

    @FXML
    private void clickSettingsButton() {
        Scenes.Settings.changeTo();
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

    @FXML
    public void restartMinecraftButton(MouseEvent event) {
        JavaProcess.restartProcess();
    }

    @FXML
    public void clearButton() {
        process_text.clear();
    }

}
