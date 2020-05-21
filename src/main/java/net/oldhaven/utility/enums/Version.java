package net.oldhaven.utility.enums;

import net.oldhaven.framework.Install;
import net.oldhaven.utility.Launcher;
import net.oldhaven.utility.mod.Mods;

import java.io.File;

public enum Version {
    b173("b1.7.3", () -> {
        if (!new File(Install.getBinPath() + "fabric/").exists()) {
            Install.installFabric();
        }
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
    }), AetherMP("AetherMP", () -> {
        if (!new File(Install.getBinPath() + "fabric/").exists()) {
            Install.installFabric();
        }
        Install.installAetherMP();
    });

    private Runnable onLaunch;
    private String name;
    Version(String name, Runnable onLaunch) {
        this.name = name;
        this.onLaunch = onLaunch;
    }
    public String getName() {
        return name;
    }

    public void launch() {
        this.onLaunch.run();
    }

    public static Version selectedVersion;
}
