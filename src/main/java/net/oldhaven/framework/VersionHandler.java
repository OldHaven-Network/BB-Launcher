package net.oldhaven.framework;

import net.oldhaven.utility.enums.Versions;
import net.oldhaven.utility.mod.Mods;

import java.io.*;

public class VersionHandler {
    public static File versionFile = new File(Install.getMainPath() + "currentversion.txt");
    public static void updateSelectedVersion(String newVersion) {
        newVersion = newVersion.replaceAll("\\.", "");
        newVersion = newVersion.replaceAll("Beta ", "b");
        newVersion = newVersion.replaceAll("Alpha ", "a");
        newVersion = newVersion.replaceAll("Classic ", "c");
        try {
            Versions.selectedVersion = Versions.valueOf(newVersion);
            PrintWriter versionWriter = new PrintWriter(versionFile, "UTF-8");
            versionWriter.println(newVersion);
            versionWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Mods.updateConfigLoc();
        Versions.selectedVersion.install();
    }

    public static void initializeVersionHandler(){
        if(!versionFile.exists() || versionFile.length() == 0){
            try {
                if(!versionFile.exists()) {
                    if(!new File(Install.getMainPath()).exists());
                        new File(Install.getMainPath()).mkdirs();
                    versionFile.createNewFile();
                }
                PrintWriter versionWriter = new PrintWriter(versionFile, "UTF-8");
                versionWriter.println("b173");
                updateSelectedVersion("Beta 1.7.3");
                versionWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader versionReader = new BufferedReader(new FileReader(versionFile));
            String line = versionReader.readLine();
            line = line.replaceAll("\\.", "");
            Versions.selectedVersion = Versions.valueOf(line);
            versionReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
