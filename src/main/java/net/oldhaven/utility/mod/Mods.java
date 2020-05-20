package net.oldhaven.utility.mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.JsonConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Mods {
    static final List<Mod> mods = new LinkedList<>();
    static final List<ModSection> sections = new LinkedList<>();
    static JsonConfig config = new JsonConfig(Install.getMainPath() + "mods.json");

    public static boolean shouldUpdate = false;

    public static void updateConfigLoc() {
        String fileLoc = Install.getMinecraftPath() + "mods.json";
        File file = new File(fileLoc);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = new JsonConfig(fileLoc);
        Mods.removeAllMods();
        Mods.addModSection("CustomMods");
        JsonObject json = config.getJson();
        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet) {
            String modName = entry.getKey();
            if(modName.equalsIgnoreCase("CustomMods"))
                continue;
            JsonObject modObj = entry.getValue().getAsJsonObject();
            String modType = modObj.get("Type").getAsString();
            String modPath = modObj.get("Path").getAsString();
            boolean modEnab = modObj.get("Enabled").getAsBoolean();
            addMod(ModType.valueOf(modType), modName, modPath, modEnab);
        }
    }

    public static Mod addMod(ModType type, String name, String path, boolean defaultEnabled) {
        Mod mod = new Mod(type, name, path);
        if(!config.hasProperty(name)) {
            mod.setEnabled(defaultEnabled);
            shouldUpdate = true;
        } else {
            JsonObject json = config.getJson();
            JsonObject object = ((JsonObject)json.get(name));
            boolean enabled = object.get("Enabled").getAsBoolean();
            mod.setEnabled(enabled);
        }
        synchronized (mods) {
            mods.add(mod);
        }
        return mod;
    }
    public static void removeAllMods() {
        synchronized(mods) {
            mods.clear();
        }
        synchronized(sections) {
            sections.clear();
        }
    }
    public static ModSection addModSection(String name) {
        ModSection section = new ModSection(name);
        if(config.hasProperty(name)) {
            JsonObject json = config.getJson();
            JsonObject object = ((JsonObject)json.get(name));
            for(String key : object.keySet()) {
                JsonObject modObj = ((JsonObject)object.get(key));
                String path = modObj.get("Path").getAsString();
                boolean enabled = modObj.get("Enabled").getAsBoolean();
                ModType type = ModType.fromString(modObj.get("Type").getAsString());
                section.addMod(type, key, path, enabled);
            }
        } else
            shouldUpdate = true;
        synchronized(sections) {
            sections.add(section);
        }
        return section;
    }
    public static void removeMod(Mod mod) {
        synchronized(mods) {
            mods.remove(mod);
        }
    }

    public static Mod getModByName(String name) {
        synchronized(mods) {
            for (Mod mod : mods) {
                if (mod.getName().equalsIgnoreCase(name))
                    return mod;
            }
        }
        return null;
    }
    public static ModSection getModSectionByName(String name) {
        synchronized(sections) {
            for (ModSection section : sections) {
                System.out.println(section.getName());
                if (section.getName().equalsIgnoreCase(name))
                    return section;
            }
            return addModSection(name);
        }
    }

    public static List<Mod> getMods() {
        synchronized(mods) { return mods; }
    }
    public static List<ModSection> getSections() {
        return sections;
    }

    public static void saveMods() {
        config.clear();
        synchronized(sections) {
            for (ModSection section : sections)
                config.setProperty(section.getName(), section.toJson());
        }
        synchronized(mods) {
            for (Mod mod : mods) {
                if (mod.getSection() != null)
                    /* Mod belongs to a section */
                    continue;
                JsonObject object = new JsonObject();
                object.addProperty("Path", mod.getFile().getAbsolutePath());
                object.addProperty("Enabled", mod.isEnabled());
                object.addProperty("Type", mod.getType().name());
                config.setProperty(mod.getName(), object);
            }
        }
        config.save();
        shouldUpdate = false;
    }
}
