package net.oldhaven;

import net.oldhaven.framework.Install;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.oldhaven.utility.mod.ModSection;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;

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
        /* note:
            You need this section cause it adds all of the mods, even if they already exist.
            defaultEnabled variable is "if not exist, make it enabled/disabled",
            we're saving afterwards cause if things don't exist and they are created, we're saving that data.

            The CustomMods section is where... custom mods will go, if that wasn't obvious
            can you make it so they're added to that when they add a new mod?
            Mods.getModSectionByName("CustomMods").addMod();
         */
        Mods.addMod(ModType.Fabric,"MegaMod", mainPath + "mods/MegaMod-Mixins.jar", true);
        Mods.addMod(ModType.MCP, "Optifine", mainPath + "mods/optifine.jar", false);
        Mods.addMod(ModType.MCP,"ReiMinimap", mainPath + "mods/ReiMinimap.jar", false);
        ModSection section = Mods.addModSection("CustomMods");
        section.addMod(ModType.ModLoader, "TestMod", mainPath+"mods/TestMod.jar", false);
        if(Mods.shouldUpdate)
            Mods.saveMods();

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
