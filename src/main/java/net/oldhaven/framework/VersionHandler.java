package net.oldhaven.framework;

import net.oldhaven.utility.enums.Version;

import java.io.*;

public class VersionHandler {
    public static File versionFile = new File(Install.getMainPath() + "currentversion.txt");
    public static void updateSelectedVersion(String newVersion){
        newVersion = newVersion.replaceAll("\\.", "");
        try {
            Version.selectedVersion = Version.valueOf(newVersion);
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
            String line = versionReader.readLine();
            line = line.replaceAll("\\.", "");
            Version.selectedVersion = Version.valueOf(line);
            versionReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
