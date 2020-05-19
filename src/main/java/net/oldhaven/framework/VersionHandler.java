package net.oldhaven.framework;

import net.oldhaven.framework.Install;

import java.io.*;

public class VersionHandler {
    public static String selectedVersion;
    public static File versionFile = new File(Install.getMainPath() + "currentversion.txt");

    public static String getSelectedVersion(){
        return selectedVersion;
    }

    public static void updateSelectedVersion(String newVersion){
        try {
            selectedVersion = newVersion;
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
            selectedVersion = versionReader.readLine();
            versionReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
