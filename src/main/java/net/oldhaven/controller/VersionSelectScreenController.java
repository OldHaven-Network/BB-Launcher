package net.oldhaven.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.oldhaven.Main;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.enums.Versions;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class VersionSelectScreenController implements Initializable {
    double offset_x;
    double offset_y;

    @FXML public ImageView background;
    @FXML private Label close_button, logout_button;
    @FXML public ListView listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setCurrentController(this);
        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File file : fileArray) {
            if(file.getAbsolutePath().contains("launcherbg")) {
                Image image = new Image(file.toURI().toString());
                background.setImage(image);
                //background.fitWidthProperty().bind(clipPane.widthProperty());
            }
        }

        for(Versions versions : Versions.values()) {
            listView.getItems().add(versions.getName());
            //version_picker.getItems().add(version.getName());
        }
        //AnchorPane.setLeftAnchor(username, 0.0);
        //AnchorPane.setRightAnchor(username, 0.0);
        //username.setAlignment(Pos.CENTER);
    }

    @FXML
    private void movableWindow(){
        Scene scene = background.getScene();
        Stage stage = (Stage) background.getScene().getWindow();
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
    private void closeButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML private void close(){
        System.exit(0);
    }
}
