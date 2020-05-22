package net.oldhaven.utility.enums;

import net.oldhaven.framework.Install;
import net.oldhaven.utility.Launcher;
import net.oldhaven.utility.mod.Mods;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum Version {
    b173("b1.7.3", "b173.json", Launcher::launch, Install::installOldHavenb173),
    AetherMP("AetherMP", "b173.json", Install::installAetherMP, null),
    a122a("Alpha 1.2.2a", "a122a.json", Launcher::launch, null);
    //c030_01c("Classic 0.30_01c", "c030_01c.json", Launcher::launch, null);

    private String name;
    private String jsonFile;
    private Runnable onLaunch;
    private Runnable onInstall;
    private Map<String/*name*/, File> fabricLibs;
    Version(String name, String jsonFile, Runnable onLaunch, Runnable onInstall) {
        this.name = name;
        this.jsonFile = jsonFile;
        this.onLaunch = onLaunch;
        this.onInstall = onInstall;
        this.fabricLibs = new LinkedHashMap<>();
    }

    public void addFabricLib(String name, File file) {
        if(!this.fabricLibs.containsKey(name))
            this.fabricLibs.put(name, file);
    }

    public List<File> getFabricLibs() {
        return new ArrayList<>(fabricLibs.values());
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public String getName() {
        return name;
    }

    public void install() {
        if(onInstall != null)
            onInstall.run();
    }

    public void launch() {
        this.onLaunch.run();
    }

    public static Version selectedVersion;
}
