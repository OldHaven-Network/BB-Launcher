package net.oldhaven.utility.enums;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import net.oldhaven.BBLauncher;
import net.oldhaven.controller.MainScreenController;

import java.io.IOException;

public enum Scene {
    Login("LoginScreen"),
    MainMenu("MainMenuScreen"),
    Settings("SettingsScreen"),
    ProcessInfo("ProcessInfoScreen");
    private String fileName;
    Scene(String fileName) {
        this.fileName = fileName;
    }

    public static void changeTo(Scene scene) {
        scene.changeTo();
    }

    public Node load() {
        String scene = "/fxml/templates/"+this.fileName+".fxml";
        try {
            return FXMLLoader.<Parent>load(getClass().getResource(scene));
        } catch(IOException e) {
            return null;
        }
    }

    public void changeTo() {
        if(!(BBLauncher.getCurrentController() instanceof MainScreenController))
            return;
        MainScreenController msc = ((MainScreenController) BBLauncher.getCurrentController());
        msc.setScene(this);
    }
}
