package net.oldhaven.framework;

import net.oldhaven.BBLauncher;
import net.oldhaven.utility.enums.Versions;
import net.oldhaven.utility.mod.Mods;
import net.oldhaven.utility.settings.LaunchSettings;

import java.io.*;

public class VersionHandler {
    public static void updateSelectedVersion(String newVersion) {
        newVersion = newVersion.replaceAll("\\.", "");
        newVersion = newVersion.replaceAll("Beta ", "b");
        newVersion = newVersion.replaceAll("Alpha ", "a");
        newVersion = newVersion.replaceAll("Classic ", "c");
        BBLauncher.settings.setOption("Selected Version", newVersion);
        BBLauncher.settings.saveSettings();
        Mods.updateConfigLoc();
        Versions.selectedVersion.install();
    }

    public static void initializeVersionHandler(){
        Versions.selectedVersion = Versions.valueOf(LaunchSettings.SELECTED_VERSION.getAsString());
        Mods.updateConfigLoc();
        Versions.selectedVersion.install();
    }
}
