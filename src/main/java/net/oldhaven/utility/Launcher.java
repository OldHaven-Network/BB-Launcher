package net.oldhaven.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.fabricmc.loader.launch.knot.KnotClient;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.oldhaven.Main;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.enums.Version;
import net.oldhaven.utility.mod.Mod;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Launcher {
    public static void launch() {
        try {
            // Back up MC to minecraft.old if that hasn't been done yet. If it has, get fresh MC from minecraft.old to prepare it for injection.
            if(!Files.exists(Paths.get(Install.getBinPath() + "minecraft.old"))) {
                new ZipFile(Install.getBinPath() + "minecraft.jar").removeFile("META-INF/");
                FileUtils.copyFile(new File(Install.getBinPath() + "minecraft.jar"), new File(Install.getBinPath() + "minecraft.old"));
            } else {
                FileUtils.forceDelete(new File(Install.getBinPath() + "minecraft.jar"));
                FileUtils.copyFile(new File(Install.getBinPath() + "minecraft.old"), new File(Install.getBinPath() + "minecraft.jar"));
            }

            // Create temp folder, catch non-existent mods, extract appropriate mod contents into it.
            List<File> filePaths = new LinkedList<>();
            File modTempPath = new File(Install.getMinecraftPath() + "mods/temp/");
            List<Mod> mods = Mods.getMods();
            //Collections.reverse(mods);
            for (Mod mod : mods) {
                if (mod.isEnabled() && mod.getType().equals(ModType.NonFabric)) {
                    if (mod.getFile().exists()) {
                        ZipFile modZip = new ZipFile(mod.getFile());
                        File dir = new File(modTempPath, mod.getFile().getName());
                        if(!dir.exists())
                            dir.mkdirs();
                        modZip.extractAll(dir.toString());
                        filePaths.add(dir);
                        System.out.println("Mod " + mod.getName() + " will be added to minecraft.jar.");
                    } else {
                        System.out.println("The mod " + mod.getName() + " does not exist!");
                    }
                }
            }

            // Delete META-INF files, inject mods into minecraft.jar and clean up our mess behind us.
            ZipFile zip = new ZipFile(Install.getBinPath() + "minecraft.jar");
            List<FileHeader> headers = zip.getFileHeaders();
            // The following three lines are not a mistake, by the way. It just works. Trust me.
            zip.removeFile(headers.get(0));
            zip.removeFile(headers.get(0));
            zip.removeFile(headers.get(0));
            ZipParameters parameters = new ZipParameters();
            parameters.setIncludeRootFolder(false);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            parameters.setDefaultFolderPath(modTempPath.getAbsolutePath());
            for(File path : filePaths) {
                parameters.setDefaultFolderPath(modTempPath.getAbsolutePath());
                File[] files = path.listFiles(File::isFile);
                File[] folders = path.listFiles(File::isDirectory);
                if (files != null && files.length > 0)
                    zip.addFiles(Arrays.asList(files));
                if (folders != null && folders.length > 0) {
                    for(File folder : folders) {
                        parameters.setDefaultFolderPath(path.getAbsolutePath());
                        deepSearchFiles(folder, zip, parameters);
                    }
                }
            }
            //FileUtils.deleteDirectory(modTempPath);
            System.out.println("All mods have been injected.");

            System.setProperty("java.class.path", Install.getClassPath(true));
            System.setProperty("java.libs.path", Install.getNativesPath());
            new JavaProcess(System.getProperty("java.home")).exec(KnotClient.class);

            Parent root = FXMLLoader.load(Version.class.getResource("/fxml/ProcessInfoScreen.fxml"));
            Stage primaryStage = (Stage) Main.getPrimaryStage().getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private static void deepSearchFiles(File path, ZipFile zip, ZipParameters parameters) {
        File[] files = path.listFiles(File::isFile);
        File[] folders = path.listFiles(File::isDirectory);
        try {
            if (files != null && files.length > 0)
                zip.addFiles(Arrays.asList(files), parameters);
            if (folders != null && folders.length > 0)
                for(File folder : folders)
                    deepSearchFiles(folder, zip, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
