package net.oldhaven.utility;

import com.google.gson.JsonObject;
import net.oldhaven.framework.Install;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Mod {
    public static List<Mod> mods = new LinkedList<>();
    private static JsonConfig config = new JsonConfig(Install.getMainPath() + "mods.json");

    private final File file;
    private final String name;
    private boolean enabled = false;
    public Mod(String name, String path) {
        this.name = name;
        this.file = new File(path);
        mods.add(this);
    }
    public Mod(String name, String path, boolean defaultEnable) {
        this(name, path);
        this.enabled = defaultEnable;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }
    public File getFile() {
        return file;
    }

    public static Mod getModByName(String name) {
        for(Mod mod : mods) {
            if(mod.name.equalsIgnoreCase(name))
                return mod;
        }
        return null;
    }
    public static void saveMods() {
        config.clear();
        for(Mod mod : mods) {
            JsonObject object = new JsonObject();
            object.addProperty("Path", mod.file.getAbsolutePath());
            object.addProperty("Enabled", mod.enabled);
            config.setProperty(mod.getName(), object);
        }
        config.save();
    }
    public static List<Mod> getMods() {
        return mods;
    }
}
