package net.oldhaven.utility.enums;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import net.oldhaven.Main;

import java.io.IOException;

public enum Scenes {
    Login("LoginScreen"),
    MainMenu("MainMenuScreen"),
    Settings("SettingsScreen"),
    VersionSelect("VersionSelectScreen"),
    ProcessInfo("ProcessInfoScreen");
    private String fileName;
    Scenes(String fileName) {
        this.fileName = fileName;
    }

    public void changeTo() {
        String scene = "/fxml/"+this.fileName+".fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource(scene));
            Stage primaryStage = (Stage) Main.getPrimaryStage().getScene().getWindow();
            javafx.scene.Scene toScene = new javafx.scene.Scene(root);
            primaryStage.setScene(toScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
