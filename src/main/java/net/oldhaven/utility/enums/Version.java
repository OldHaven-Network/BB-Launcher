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
    b173("b1.7.3", "b173.json", () -> {
        String mainPath = Install.getMinecraftPath();
        Mods.addModSection("CustomMods");
        if (Mods.shouldUpdate)
            Mods.saveMods();

        Install.installSavedServers(mainPath);
        Install.installMegaMod(mainPath + "mods/");

        Install.installSavedServers(mainPath);
        Install.installMegaMod(mainPath + "mods/");
        Install.installDefaultMods();
        Launcher.launch();
    }), AetherMP("AetherMP", "b173.json", Install::installAetherMP),
    a122a("Alpha 1.2.2a", "a122a.json", Launcher::launch),
    c030_01c("Classic 0.30_01c", "c030_01c.json", Launcher::launch);

    private Runnable onLaunch;
    private String name;
    private String jsonFile;
    private Map<String/*name*/, File> fabricLibs;
    Version(String name, String jsonFile, Runnable onLaunch) {
        this.name = name;
        this.onLaunch = onLaunch;
        this.jsonFile = jsonFile;
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

    public void launch() {
        this.onLaunch.run();
    }

    public static Version selectedVersion;
}
