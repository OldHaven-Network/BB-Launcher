package net.oldhaven;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import net.oldhaven.framework.Install;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.oldhaven.framework.VersionHandler;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;

public class Main extends Application {

    private double offset_x;
    private double offset_y;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static Initializable currentController;
    private static Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }

    public static void setCurrentController(Initializable initializable) {
        currentController = initializable;
    }
    public static Initializable getCurrentController() {
        return currentController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        if(Install.isOSUnknown()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Lang.ALERT_ERROR.translate());
            alert.setHeaderText(Lang.OS_UNKNOWN.translate());
            alert.setContentText(Lang.OS_SUPPORTED.translate());
            alert.showAndWait();
            if(!alert.isShowing()) {
                System.exit(0);
                return;
            }
        }

        VersionHandler.initializeVersionHandler();
        File olderMinecraftFolder = new File(Install.getMainPath() + "minecraft");
        File oldMinecraftFolder = new File(Install.getMainPath() + "versions/b173");
        File newMinecraftFolder = new File(Install.getMainPath() + "versions/Beta 1.7.3");

        if(oldMinecraftFolder.exists()) {
            try {
                FileUtils.deleteDirectory(newMinecraftFolder);
                FileUtils.moveDirectory(oldMinecraftFolder, newMinecraftFolder);
                FileUtils.deleteDirectory(oldMinecraftFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(olderMinecraftFolder.exists()) {
            try {
                FileUtils.deleteDirectory(newMinecraftFolder);
                FileUtils.moveDirectory(olderMinecraftFolder, newMinecraftFolder);
                FileUtils.deleteDirectory(olderMinecraftFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Hello there, General Kenobi");
        this.createFolders();
        Mods.updateConfigLoc();
        // Moved Minecraft installation to after login so unauthorized users cannot download Mojang files without logging in.
        File settingsFile = new File(Install.getMainPath() + "settings.txt");
        if(!settingsFile.exists()){
            try {
                PrintWriter settingsWriter = new PrintWriter(settingsFile, "UTF-8");
                settingsWriter.println("1024"); // Maximum allocated memory
                settingsWriter.println("256"); // Minimum allocated memory
                settingsWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Install.checkLauncherUpdate();
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
        primaryStage.setTitle("Beyond Beta Launcher");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon2.png")));
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
