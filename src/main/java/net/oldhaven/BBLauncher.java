package net.oldhaven;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import net.oldhaven.framework.Install;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.oldhaven.framework.VersionHandler;
import net.oldhaven.utility.enums.Scene;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.mod.Mods;
import net.oldhaven.utility.settings.Settings;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BBLauncher extends Application {

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

    public static Settings settings;

    @Override
    public void start(Stage primaryStage) {
        if(Install.getOS() == Install.OS.Unknown){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Lang.ALERT_TYPES_ERROR.translate());
            alert.setHeaderText(Lang.OS_UNKNOWN.translate());
            alert.setContentText(Lang.OS_SUPPORTED.translate());
            alert.showAndWait();
            if(!alert.isShowing()) {
                System.exit(0);
                return;
            }
        }

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

        File optionsFile = new File(Install.getMainPath() + "settings.ini");
        File oldSettingsFile = new File(Install.getMainPath() + "settings.txt");
        if(oldSettingsFile.exists())
            oldSettingsFile.delete();
        settings = new Settings();
        settings.optionsFile = optionsFile;
        settings.readSettings();

        VersionHandler.initializeVersionHandler();
        Mods.updateConfigLoc();
        System.out.println("Hello there, General Kenobi");
        this.createFolders();

        Install.checkLauncherUpdate();
        this.loadFXML(primaryStage);
    }

    private static void createRunnableSwap() {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    switch (timeUnit) {
                        case HOURS:
                            Thread.sleep((long)(period / 3.6e+6));
                            break;
                        case MINUTES:
                            Thread.sleep(period / 60000);
                        case SECONDS:
                            Thread.sleep(period / 1000);
                            break;
                        case MILLISECONDS:
                            Thread.sleep(period);
                            break;
                        case MICROSECONDS:
                        case NANOSECONDS:
                        default:
                            throw new InterruptedException("Invalid TimeUnit or too long of a unit");
                    }
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                runnable.run();
            }
        });
    }

    private static Map<Scene, Thread> scenedThreads = new HashMap<>();
    public static void createRunnableWithScene(final Scene scene, final Runnable runnable, long period, TimeUnit timeUnit) {
        scenedThreads.put(scene, thread);
        thread.start();
    }
    public static void cancelRunnablesWithScene(Scene scene) {
        if(!scenedThreads.containsKey(scene))
            return;
        System.out.println("Shutting down tasks from " + scene.name());
        Thread thread = scenedThreads.get(scene);
        if(thread.isAlive() || !thread.isInterrupted())
            scenedThreads.get(scene).interrupt();
    }

    private void loadFXML(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        javafx.scene.Scene scene = new javafx.scene.Scene(root);
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

        BBLauncher.primaryStage = primaryStage;
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
            e.printStackTrace();
        }
    }
}
