package net.oldhaven;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.oldhaven.framework.Install;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.oldhaven.utility.JsonConfig;
import net.oldhaven.utility.Mod;

import java.awt.*;
import java.io.*;

public class Main extends Application {

    private double offset_x;
    private double offset_y;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        if(Install.isOSUnknown()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            System.out.println("ERROR: Unknown operating system, quitting...");
            alert.setTitle("Error");
            alert.setHeaderText("You are running an unknown operating system.");
            alert.setContentText("Supported operating systems include Windows, Mac OS X and Linux. If your system is on this list, you may have an outdated version.");
            alert.showAndWait();
            if(!alert.isShowing()) {
                System.exit(0);
                return;
            }
        }

        String mainPath = Install.getMinecraftPath();

        new Mod("MegaMod", mainPath+"mods/MegaMod-Mixins.jar", true);
        new Mod("Optifine", mainPath+"mods/optifine.jar");
        Mod.saveMods();

        Install.installSavedServers(mainPath);
        Install.installMegaMod(mainPath + "mods/");

        this.createFolders();
        this.loadFXML(primaryStage);
    }

    private void loadFXML(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setOnMousePressed(event -> {
            offset_x = event.getSceneX();
            offset_y = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - offset_x);
            primaryStage.setY(event.getScreenY() - offset_y);
        });

        Main.primaryStage = primaryStage;
    }

    private void createFolders() {
        try {
            File mainDir = new File(Install.getMainPath());
            boolean failedCreate = false;
            if (!mainDir.exists())
                failedCreate = !mainDir.mkdir();
            File binDir = new File(Install.getBinPath());
            if (!binDir.exists())
                failedCreate = !binDir.mkdir();
            File nativesDir = new File(Install.getNativesPath());
            if (!nativesDir.exists())
                failedCreate = !nativesDir.mkdir();
            File logsDir = new File(Install.getLogsPath());
            if (!logsDir.exists())
                failedCreate = !logsDir.mkdir();
            if(failedCreate)
                System.err.println("Failed to create a folder");
        } catch (NullPointerException e) {
            System.exit(0);
        }
    }
}
