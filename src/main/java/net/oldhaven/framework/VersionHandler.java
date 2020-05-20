package net.oldhaven.framework;

import java.io.*;

public class VersionHandler {
    public enum Version {
        b173("b1.7.3"), AetherMP("AetherMP");
        private String name;
        Version(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    public static Version selectedVersion;
    public static File versionFile = new File(Install.getMainPath() + "currentversion.txt");

    public static Version getSelectedVersion(){
        return selectedVersion;
    }

    public static void updateSelectedVersion(Version newVersion){
        try {
            selectedVersion = newVersion;
            PrintWriter versionWriter = new PrintWriter(versionFile, "UTF-8");
            versionWriter.println(newVersion);
            versionWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static void updateSelectedVersion(String newVersion){
        try {
            selectedVersion = Version.valueOf(newVersion);
            PrintWriter versionWriter = new PrintWriter(versionFile, "UTF-8");
            versionWriter.println(newVersion);
            versionWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void initializeVersionHandler(){
        if(!versionFile.exists() || versionFile.length() == 0){
            try {
                PrintWriter versionWriter = new PrintWriter(versionFile, "UTF-8");
                versionWriter.println("b1.7.3");
                versionWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader versionReader = new BufferedReader(new FileReader(versionFile));
            selectedVersion = Version.valueOf(versionReader.readLine());
            versionReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
