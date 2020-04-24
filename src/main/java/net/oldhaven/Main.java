package net.oldhaven;

import javafx.scene.image.Image;
import net.lingala.zip4j.ZipFile;
import net.oldhaven.framework.Install;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.lang.LanguageUtil;
import net.oldhaven.utility.mod.ModSection;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Objects;

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
            alert.setTitle(Lang.ALERT_ERROR.translate());
            alert.setHeaderText(Lang.OS_UNKNOWN.translate());
            alert.setContentText(Lang.OS_SUPPORTED.translate());
            alert.showAndWait();
            if(!alert.isShowing()) {
                System.exit(0);
                return;
            }
        }

        String mainPath = Install.getMinecraftPath();
        /* note:
            You need this section cause it adds all of the mods, even if they already exist.
            defaultEnabled variable is "if not exist, make it enabled/disabled",
            we're saving afterwards cause if things don't exist and they are created, we're saving that data.

            The CustomMods section is where... custom mods will go, if that wasn't obvious
            can you make it so they're added to that when they add a new mod?
            Mods.getModSectionByName("CustomMods").addMod();
         */

        Mods.addMod(ModType.Fabric,"MegaMod-Mixins.jar", mainPath + "mods-inactive/MegaMod-Mixins.jar", true);
        Mods.addMod(ModType.NonFabric, "OptiFine.zip", mainPath + "mods/non-fabric/OptiFine.zip", false);
        Mods.addMod(ModType.NonFabric,"ReiMinimap.zip", mainPath + "mods/non-fabric/ReiMinimap.zip", false);
        ModSection section = Mods.addModSection("CustomMods");
        Objects.requireNonNull(Mods.getModSectionByName("CustomMods")).getMods();
        if(Mods.shouldUpdate)
            Mods.saveMods();

        this.createFolders();
        Install.installSavedServers(mainPath);
        Install.installMegaMod(mainPath + "mods/");
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
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
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
